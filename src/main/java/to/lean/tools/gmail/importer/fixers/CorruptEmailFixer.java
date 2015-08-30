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

package to.lean.tools.gmail.importer.fixers;

import com.google.common.base.Preconditions;
import to.lean.tools.gmail.importer.local.JavaxMailMessage;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

/**
 * Tries to fix broken e-mail addresses.
 *
 * <p>Note: This is not actually used.
 */
public class CorruptEmailFixer {
  private boolean hasValidFrom(JavaxMailMessage message) {
    String[] fromHeaders = message.getHeader("From");
    Preconditions.checkState(fromHeaders.length == 1,
        "Expected exactly 1 From header, got: " + Arrays.toString(fromHeaders));
    String rawAddress = fromHeaders[0].replaceAll("\\s+"," ");
    try {
      InternetAddress[] address =
          InternetAddress.parseHeader(rawAddress, true);
      Preconditions.checkState(address.length == 1,
          "Expected exactly 1 From address, got: " + Arrays.toString(address));
      System.err.format("Valid? %s == %s\n", address[0].toString(), rawAddress);
      return address[0].toString().equals(rawAddress);
    } catch (AddressException e) {
      System.err.format("Not valid from because: %s", e.getMessage());
      return false;
    }
  }

  private JavaxMailMessage tryToCorrectMessageFrom(JavaxMailMessage message) {
    System.err.format("Fixing message...\n");
    Address[] address = message.getFrom();
    Preconditions.checkState(address.length == 1,
        "Expected exactly 1 From address, got: "  + Arrays.toString(address));

    try {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      message.writeTo(outputStream);
      ByteArrayInputStream inputStream =
          new ByteArrayInputStream(outputStream.toByteArray());

      Session session = Session.getInstance(new Properties());
      MimeMessage newMessage = new MimeMessage(session, inputStream);
      newMessage.setFrom(address[0]);
      return new JavaxMailMessage(newMessage);
    } catch (MessagingException | IOException e) {
      throw new RuntimeException(e);
    }
  }
}
