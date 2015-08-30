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

import com.google.common.base.Verify;
import com.google.common.collect.ImmutableList;
import to.lean.tools.gmail.importer.local.JavaxMailMessage;
import to.lean.tools.gmail.importer.local.LocalMessage;

import javax.annotation.concurrent.NotThreadSafe;
import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Thunderbird-specific local message that correctly decodes the
 * {@code X-Mozilla-Status} headers.
 */
@NotThreadSafe
class ThunderbirdLocalMessage implements LocalMessage {

  private final JavaxMailMessage message;
  private final Function<String, String> relativize;
  private final XMozillaStatusParser statusParser;
  private XMozillaStatus status;

  @Inject
  ThunderbirdLocalMessage(
      JavaxMailMessage message,
      Function<String, String> relativize,
      XMozillaStatusParser statusParser) {
    this.message = message;
    this.relativize = relativize;
    this.statusParser = statusParser;
  }

  @Override
  public String getMessageId() {
    String[] messageId = message.getHeader("Message-ID");
    Verify.verify(messageId.length == 1,
        "Expected 1 message id, got: %s", Arrays.toString(messageId));
    return messageId[0];
  }

  @Override
  public String getFromHeader() {
    String[] fromHeader = message.getHeader("From");
    Verify.verify(fromHeader.length == 1,
        "Expected 1 From header, got: %s", Arrays.toString(fromHeader));
    return fromHeader[0];
  }

  @Override
  public List<String> getFolders() {
    return
        ImmutableList.of(relativize.apply(message.getFolder().getFullName()));
  }

  @Override
  public byte[] getRawContent() {
    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    try {
      message.writeTo(byteStream);
      return byteStream.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean isUnread() {
    if (status == null) {
      status = statusParser.parse(message);
    }
    return !status.isRead();
  }

  @Override
  public boolean isStarred() {
    if (status == null) {
      status = statusParser.parse(message);
    }
    return status.isMarked();
  }
}
