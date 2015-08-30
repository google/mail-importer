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

package to.lean.tools.gmail.importer.local.thunderbird;

import com.google.common.base.Charsets;
import com.google.common.base.VerifyException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import to.lean.tools.gmail.importer.local.JavaxMailMessage;

import javax.mail.Folder;
import javax.mail.Message;
import java.io.OutputStream;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ThunderbirdLocalMessageTest {

  @Mock private Message message;
  @Mock private Folder folder;
  private ThunderbirdLocalMessage localMailMessage;

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);

    localMailMessage =
        new ThunderbirdLocalMessage(
            new JavaxMailMessage(message),
            s -> "*" + s + "*",
            new XMozillaStatusParser());

    when(message.getFolder()).thenReturn(folder);
    when(folder.getFullName()).thenReturn("folder");

    doAnswer(invocation -> {
      OutputStream outputStream = (OutputStream) invocation.getArguments()[0];
      outputStream.write("BODY".getBytes(Charsets.UTF_8));
      return null;
    })
        .when(message).writeTo(any(OutputStream.class));
  }

  @Test
  public void testGetMessageId_normal() throws Exception {
    when(message.getHeader("Message-ID"))
        .thenReturn(new String[]{"<XYZ@pdq>"});

    assertThat(localMailMessage.getMessageId()).named("messageId")
        .isEqualTo("<XYZ@pdq>");
  }

  @Test
  public void testGetMessageId_missing() throws Exception {
    when(message.getHeader("Message-ID"))
        .thenReturn(new String[0]);

    try {
      localMailMessage.getMessageId();
      assertWithMessage("Should have thrown an exception").fail();
    } catch (VerifyException expected) {
      // OK!
    }
  }

  @Test
  public void testGetMessageId_multiple() throws Exception {
    when(message.getHeader("Message-ID"))
        .thenReturn(new String[]{"<XYZ@pdq>", "<ABC@123>"});

    try {
      localMailMessage.getMessageId();
      assertWithMessage("Should have thrown an exception").fail();
    } catch (VerifyException expected) {
      // OK!
    }
  }

  @Test
  public void testGetFromHeader_normal() throws Exception {
    when(message.getHeader("From"))
        .thenReturn(new String[]{"<XYZ@pdq>"});

    assertThat(localMailMessage.getFromHeader()).named("from header")
        .isEqualTo("<XYZ@pdq>");
  }

  @Test
  public void testGetFromHeader_missing() throws Exception {
    when(message.getHeader("From"))
        .thenReturn(new String[0]);

    try {
      localMailMessage.getFromHeader();
      assertWithMessage("Should have thrown an exception").fail();
    } catch (VerifyException expected) {
      // OK!
    }
  }

  @Test
  public void testGetFromHeader_multiple() throws Exception {
    when(message.getHeader("From"))
        .thenReturn(new String[]{"<XYZ@pdq>", "<ABC@123>"});

    try {
      localMailMessage.getFromHeader();
      assertWithMessage("Should have thrown an exception").fail();
    } catch (VerifyException expected) {
      // OK!
    }
  }

  @Test
  public void testGetFolders() throws Exception {
    assertThat(localMailMessage.getFolders()).named("folders")
        .containsExactly("*folder*");
  }

  @Test
  public void testGetRawContent() throws Exception {
    assertThat(localMailMessage.getRawContent()).named("raw content")
        .isEqualTo("BODY".getBytes(Charsets.UTF_8));
  }

  @Test
  public void testIsUnread_true() throws Exception {
    when(message.getHeader("X-Mozilla-Status"))
        .thenReturn(new String[] { "00000000" });
    assertThat(localMailMessage.isUnread()).named("is unread")
        .isEqualTo(true);
  }

  @Test
  public void testIsUnread_false() throws Exception {
    when(message.getHeader("X-Mozilla-Status"))
        .thenReturn(new String[] { "00000001" });
    assertThat(localMailMessage.isUnread()).named("is unread")
        .isEqualTo(false);
  }

  @Test
  public void testIsStarred_true() throws Exception {
    when(message.getHeader("X-Mozilla-Status"))
        .thenReturn(new String[] { "00000004" });
    assertThat(localMailMessage.isStarred()).named("is starred")
        .isEqualTo(true);
  }

  @Test
  public void testIsStarred_false() throws Exception {
    when(message.getHeader("X-Mozilla-Status"))
        .thenReturn(new String[] { "00000000" });
    assertThat(localMailMessage.isStarred()).named("is starred")
        .isEqualTo(false);
  }

}
