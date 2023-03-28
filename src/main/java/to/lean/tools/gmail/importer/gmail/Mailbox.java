/*
 * Copyright 2015 The Mail Importer Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package to.lean.tools.gmail.importer.gmail;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.HttpHeaders;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.ModifyMessageRequest;
import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import to.lean.tools.gmail.importer.local.LocalMessage;

/**
 * Exports an API for a user's mailbox. This is a wrapper around the user's Gmail mailbox as exposed
 * by the Gmail API. This is not a perfect interface as it assumes that the mailbox is static except
 * for the actions that it issues.
 */
class Mailbox {
  static final int TOO_MANY_CONCURRENT_REQUESTS_FOR_USER = 429;

  private final GmailService gmailService;
  private final User user;

  private Map<String, Label> labelsById;
  private Map<String, Label> labelsByName;

  @Inject
  Mailbox(GmailService gmailService, User user) {
    this.gmailService = gmailService;
    this.user = user;
  }

  void connect() throws IOException {
    loadLabels();
  }

  /** Loads the user's labels and remembers them. */
  void loadLabels() throws IOException {
    ListLabelsResponse labelResponse =
        gmailService
            .getServiceWithRetries()
            .users()
            .labels()
            .list(user.getEmailAddress())
            .execute();

    Verify.verify(!labelResponse.isEmpty(), "could not get labels %s");

    List<Label> labels = labelResponse.getLabels();
    labelsByName = labels.stream().collect(toMap(Label::getName, label -> label));
    labelsById = labels.stream().collect(toMap(Label::getId, label -> label));
    System.err.format("Got labels: %s", labelsByName);
  }

  Multimap<LocalMessage, Message> mapMessageIds(Iterable<LocalMessage> localMessages) {
    Multimap<LocalMessage, Message> results = MultimapBuilder.hashKeys().linkedListValues().build();

    Gmail gmail = gmailService.getServiceWithRetries();
    BatchRequest batch = gmail.batch();

    try {
      for (LocalMessage localMessage : localMessages) {
        gmail
            .users()
            .messages()
            .list(user.getEmailAddress())
            .setQ("rfc822msgid:" + localMessage.getMessageId())
            .setFields("messages(id)")
            .queue(
                batch,
                new JsonBatchCallback<ListMessagesResponse>() {
                  @Override
                  public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders)
                      throws IOException {
                    System.err.println("Could not get message: " + localMessage.getMessageId());
                    System.err.println("  because: " + e);
                  }

                  @Override
                  public void onSuccess(ListMessagesResponse response, HttpHeaders responseHeaders)
                      throws IOException {
                    if (!response.isEmpty()) {
                      results.putAll(localMessage, response.getMessages());
                      System.err.println("For " + localMessage.getMessageId() + " got:");
                      response.getMessages().stream()
                          .forEach(
                              message -> System.err.println("  message id: " + message.getId()));
                    }
                  }
                });
      }
      if (batch.size() > 0) {
        batch.execute();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return results;
  }

  void fetchExistingLabels(Iterable<Message> messages) {
    Gmail gmail = gmailService.getServiceWithRetries();
    BatchRequest batch = gmail.batch();

    try {
      for (Message message : messages) {
        gmail
            .users()
            .messages()
            .get(user.getEmailAddress(), message.getId())
            .setFields("id,labelIds")
            .queue(
                batch,
                new JsonBatchCallback<Message>() {
                  @Override
                  public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders)
                      throws IOException {
                    System.err.format(
                        "For message: %s, got error: %s\n", message.getId(), e.toPrettyString());
                  }

                  @Override
                  public void onSuccess(Message responseMessage, HttpHeaders responseHeaders)
                      throws IOException {
                    Preconditions.checkState(
                        message.getId().equals(responseMessage.getId()),
                        "Message ids must be equal");
                    List<String> gmailMessageIds =
                        responseMessage.getLabelIds() == null
                            ? ImmutableList.of()
                            : responseMessage.getLabelIds();
                    System.out.format(
                        "For message %s, got labels: %s\n",
                        responseMessage.getId(),
                        gmailMessageIds.stream()
                            .map(id -> labelsById.getOrDefault(id, new Label().setName(id)))
                            .map(Label::getName)
                            .collect(Collectors.joining(", ")));
                    message.setLabelIds(gmailMessageIds);
                  }
                });
      }
      if (batch.size() > 0) {
        batch.execute();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  Message uploadMessage(LocalMessage localMessage) throws GoogleJsonResponseException {
    Gmail gmail = gmailService.getServiceWithRetries();
    try {
      Gmail.Users.Messages.GmailImport r =
          gmail
              .users()
              .messages()
              .gmailImport(
                  user.getEmailAddress(),
                  new Message(),
                  new AbstractInputStreamContent("message/rfc822") {
                    byte[] bytes = localMessage.getRawContent();

                    @Override
                    public InputStream getInputStream() throws IOException {
                      return new ByteArrayInputStream(bytes);
                    }

                    @Override
                    public long getLength() throws IOException {
                      return bytes.length;
                    }

                    @Override
                    public boolean retrySupported() {
                      return true;
                    }
                  });
      r.getMediaHttpUploader()
          .setProgressListener(
              uploader -> {
                System.out.format(
                    "[%s] Progress: %2.0f        \r",
                    uploader.getUploadState().toString(), uploader.getProgress() * 100);
                System.out.flush();
              });
      System.out.println();
      Message result = r.execute();
      System.out.println(result.toPrettyString());
      return result;
    } catch (GoogleJsonResponseException e) {
      if (e.getDetails().getMessage().equalsIgnoreCase("Invalid From header")) {
        throw e;
      }
      throw new RuntimeException(e);
    } catch (IOException e) {
      System.err.format("Failed to upload message: \n");
      try {
        ByteStreams.copy(new ByteArrayInputStream(localMessage.getRawContent()), System.err);
      } catch (IOException e1) {
        System.err.format("Holy shit Batman! Error within an error! (%s)", e.getMessage());
      }
      throw new RuntimeException(e);
    }
  }

  void syncLocalLabelsToGmail(Multimap<LocalMessage, Message> map) {
    class Batches {
      BatchRequest thisBatch;
      BatchRequest nextBatch;
    }
    Gmail gmail = gmailService.getServiceWithRetries();
    Batches batches = new Batches();
    batches.thisBatch = gmail.batch();
    batches.nextBatch = gmail.batch();

    try {
      for (Map.Entry<LocalMessage, Message> entry : map.entries()) {
        LocalMessage localMessage = entry.getKey();
        Message message = entry.getValue();

        Set<String> labelNamesToAdd =
            localMessage.getFolders().stream().map(this::normalizeLabelName).collect(toSet());
        Set<String> labelNamesToRemove = Sets.newHashSet("SPAM", "TRASH");
        labelNamesToRemove.removeAll(labelNamesToAdd);

        if (localMessage.isStarred()) {
          labelNamesToAdd.add("STARRED");
          labelNamesToRemove.remove("STARRED");
        }
        if (localMessage.isUnread()) {
          labelNamesToAdd.add("UNREAD");
          labelNamesToRemove.remove("UNREAD");
        } else {
          labelNamesToRemove.add("UNREAD");
          labelNamesToAdd.remove("UNREAD");
        }

        if (!labelNamesToAdd.contains("INBOX")) {
          labelNamesToRemove.add("INBOX");
          labelNamesToAdd.remove("INBOX");
        }

        List<String> labelIdsToAdd =
            labelNamesToAdd.stream()
                .map(labelName -> labelsByName.get(labelName).getId())
                .collect(toList());
        List<String> labelIdsToRemove =
            labelNamesToRemove.stream()
                .map(labelName -> labelsByName.get(labelName).getId())
                .collect(toList());

        Gmail.Users.Messages.Modify request =
            gmail
                .users()
                .messages()
                .modify(
                    user.getEmailAddress(),
                    message.getId(),
                    new ModifyMessageRequest()
                        .setAddLabelIds(labelIdsToAdd)
                        .setRemoveLabelIds(labelIdsToRemove));

        JsonBatchCallback<Message> callback =
            new JsonBatchCallback<Message>() {
              @Override
              public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders)
                  throws IOException {
                System.err.format(
                    "For message: %s, got error: %s\n", message.getId(), e.toPrettyString());
                if (e.getCode() == TOO_MANY_CONCURRENT_REQUESTS_FOR_USER) {
                  request.queue(batches.nextBatch, this);
                }
              }

              @Override
              public void onSuccess(Message message, HttpHeaders responseHeaders)
                  throws IOException {
                System.err.println(message.toPrettyString());
              }
            };
        request.queue(batches.thisBatch, callback);
      }

      while (batches.thisBatch.size() > 0) {
        batches.thisBatch.execute();
        batches.thisBatch = batches.nextBatch;
        batches.nextBatch = gmail.batch();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  String normalizeLabelName(String localLabel) {
    if (localLabel.equalsIgnoreCase("INBOX")) {
      return "INBOX";
    }
    if (localLabel.equalsIgnoreCase("DRAFTS")) {
      return "Import/Drafts";
    }
    if (localLabel.equalsIgnoreCase("TRASH")) {
      return "TRASH";
    }
    if (localLabel.equalsIgnoreCase("SPAM")) {
      return "SPAM";
    }
    return localLabel;
  }

  private void syncLabels(
      Gmail gmailApi,
      BiMap<String, String> labelIdToNameMap,
      Multimap<LocalMessage, Message> localMessageToGmailMessages)
      throws IOException {
    BatchRequest relabelBatch = gmailApi.batch();
    for (Map.Entry<LocalMessage, Message> entry : localMessageToGmailMessages.entries()) {
      LocalMessage localMessage = entry.getKey();
      Message gmailMessage = entry.getValue();

      Set<String> gmailLabels =
          gmailMessage.getLabelIds() == null
              ? ImmutableSet.of()
              : gmailMessage.getLabelIds().stream()
                  .map(labelIdToNameMap::get)
                  .collect(Collectors.toSet());

      List<String> missingLabelIds =
          localMessage.getFolders().stream()
              .map(folder -> folder.equalsIgnoreCase("Inbox") ? "INBOX" : folder)
              .filter(folder -> !gmailLabels.contains(folder))
              .map(folder -> labelIdToNameMap.inverse().get(folder))
              .collect(Collectors.toList());

      if (localMessage.isUnread() && !gmailLabels.contains("UNREAD")) {
        missingLabelIds.add("UNREAD");
      }
      if (localMessage.isStarred() && !gmailLabels.contains("STARRED")) {
        missingLabelIds.add("STARRED");
      }

      for (String folder : localMessage.getFolders()) {
        if (!gmailLabels.contains(folder)) {
          System.out.format(
              "Trying to add labels %s to %s\n",
              missingLabelIds.stream().map(labelIdToNameMap::get).collect(Collectors.joining(", ")),
              gmailMessage.getId());
          gmailApi
              .users()
              .messages()
              .modify(
                  user.getEmailAddress(),
                  gmailMessage.getId(),
                  new ModifyMessageRequest().setAddLabelIds(missingLabelIds))
              .queue(
                  relabelBatch,
                  new JsonBatchCallback<Message>() {
                    @Override
                    public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders)
                        throws IOException {
                      System.err.format(
                          "For label ids %s, got error: %s\n", missingLabelIds, e.toPrettyString());
                    }

                    @Override
                    public void onSuccess(Message message, HttpHeaders responseHeaders)
                        throws IOException {
                      System.out.format(
                          "Successfully added labels %s to %s\n",
                          missingLabelIds.stream()
                              .map(labelIdToNameMap::get)
                              .collect(Collectors.joining(", ")),
                          message.getId());
                    }
                  });
        }
      }
      if (relabelBatch.size() > 0) {
        relabelBatch.execute();
      }
    }
  }

  private void createMissingLabels(
      Gmail gmailApi, final BiMap<String, String> labelIdToNameMap, Set<LocalMessage> localMessages)
      throws IOException {

    Set<String> missingLabels =
        localMessages.stream()
            .flatMap(localMessage -> localMessage.getFolders().stream())
            .filter(folder -> !"INBOX".equalsIgnoreCase(folder))
            .filter(folder -> !labelIdToNameMap.containsValue(folder))
            .collect(Collectors.toSet());

    if (!missingLabels.isEmpty()) {
      BatchRequest batchRequest = gmailApi.batch();
      for (String label : missingLabels) {
        System.err.format("Adding label %s\n", label);
        gmailApi
            .users()
            .labels()
            .create(
                user.getEmailAddress(),
                new Label()
                    .setName(label)
                    .setLabelListVisibility("labelHide")
                    .setMessageListVisibility("show"))
            .queue(
                batchRequest,
                new JsonBatchCallback<Label>() {
                  @Override
                  public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders)
                      throws IOException {
                    System.err.format("For label %s, got error: %s\n", label, e.toPrettyString());
                  }

                  @Override
                  public void onSuccess(Label label, HttpHeaders responseHeaders)
                      throws IOException {
                    labelIdToNameMap.put(label.getId(), label.getName());
                  }
                });
      }
      batchRequest.execute();
    }
  }
}
