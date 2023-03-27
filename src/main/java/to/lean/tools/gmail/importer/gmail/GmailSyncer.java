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

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.services.gmail.model.Message;
import com.google.common.collect.Multimap;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import to.lean.tools.gmail.importer.local.LocalMessage;

/**
 * Main sync logic. After construction, instances must be initialized by calling the {@link #init()}
 * method. Messages are sync'd in batches by calling the {@link #sync(List)} method.
 */
public class GmailSyncer {
  private final Mailbox mailbox;
  private boolean initialized = false;

  @Inject
  GmailSyncer(Mailbox mailbox) {
    this.mailbox = mailbox;
  }

  /**
   * Connects to Gmail and loads the base information required for the sync.
   *
   * @throws IOException if something goes wrong with the connection
   */
  public void init() throws IOException {
    mailbox.connect();
    this.initialized = true;
  }

  /**
   * Synchronizes the given list of messages with Gmail. When this operation completes, all of the
   * messages will appear in Gmail with the same labels (folers) that they have in the local store.
   * The message state, including read/unread, will also be synchronized, but might not match
   * exactly if the message is already in Gmail.
   *
   * <p>Note that some errors can prevent some messages from being uploaded. In this case, the
   * failure policy dictates what happens.
   *
   * @param messages the list of messages to synchronize. These messages may or may not already
   *     exist in Gmail.
   * @throws IOException if something goes wrong with the connection
   */
  public void sync(List<LocalMessage> messages) throws IOException {
    Preconditions.checkState(initialized, "GmailSyncer.init() must be called first");
    Multimap<LocalMessage, Message> map = mailbox.mapMessageIds(messages);
    messages.stream()
        .filter(message -> !map.containsKey(message))
        .forEach(
            message -> {
              try {
                Message gmailMessage = mailbox.uploadMessage(message);
                map.put(message, gmailMessage);
              } catch (GoogleJsonResponseException e) {
                // Message couldn't be uploaded, but we know why
              }
            });
    mailbox.fetchExistingLabels(map.values());
    mailbox.syncLocalLabelsToGmail(map);
  }
}
