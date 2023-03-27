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

import static com.google.common.truth.Truth.assertWithMessage;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.MultimapBuilder;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import to.lean.tools.gmail.importer.local.LocalMessage;

public class GmailSyncerTest {

  @Mock private Mailbox mailbox;
  private GmailSyncer gmailSyncer;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    gmailSyncer = new GmailSyncer(mailbox);
  }

  @Test
  public void testInitRequiredBeforeSync() throws Exception {
    try {
      gmailSyncer.sync(Collections.emptyList());
      assertWithMessage("sync() should fail if init() is not called first.");
    } catch (Exception ignored) {
    }
  }

  @Test
  public void testSyncEmptyList() throws Exception {
    setUpEmptyMailbox();

    gmailSyncer.init();
    gmailSyncer.sync(Collections.emptyList());

    verify(mailbox, never()).uploadMessage(any());
  }

  @Test
  public void testSyncWithMessages() throws Exception {
    setUpEmptyMailbox();

    ImmutableList<LocalMessage> localMessages =
        ImmutableList.of(
            new FakeLocalMessage("Subject 1", "Hello"),
            new FakeLocalMessage("Subject 2", "Good bye"));

    gmailSyncer.init();
    gmailSyncer.sync(localMessages);
  }

  private void setUpEmptyMailbox() {
    when(mailbox.mapMessageIds(anyListOf(LocalMessage.class)))
        .thenAnswer(
            invocation -> {
              return MultimapBuilder.hashKeys().linkedListValues().build();
            });
  }

  private static class FakeLocalMessageBuilder {
    String messageId;
    String fromHeader;
    List<String> folders;
    byte[] rawContent;
    boolean isUnread;
    boolean isStarred;
  }

  private static class FakeLocalMessage implements LocalMessage {
    public FakeLocalMessage(String from, String body) {}

    @Override
    public String getMessageId() {
      return null;
    }

    @Override
    public String getFromHeader() {
      return null;
    }

    @Override
    public List<String> getFolders() {
      return null;
    }

    @Override
    public byte[] getRawContent() {
      return new byte[0];
    }

    @Override
    public boolean isUnread() {
      return false;
    }

    @Override
    public boolean isStarred() {
      return false;
    }
  }
}
