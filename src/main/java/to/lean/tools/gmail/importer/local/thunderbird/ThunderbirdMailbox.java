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

import com.google.inject.Inject;
import to.lean.tools.gmail.importer.CommandLineArguments;
import to.lean.tools.gmail.importer.MailProvider;
import to.lean.tools.gmail.importer.local.JavaxMailFolder;
import to.lean.tools.gmail.importer.local.JavaxMailStorage;
import to.lean.tools.gmail.importer.local.LocalStorage;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Reads a Thunderbird mailbox.
 */
class ThunderbirdMailbox implements MailProvider<LocalStorage> {

  private final Logger logger;
  private final CommandLineArguments commandLineArguments;
  private final XMozillaStatusParser statusParser;

  @Inject
  ThunderbirdMailbox(Logger logger,
      CommandLineArguments commandLineArguments,
      XMozillaStatusParser statusParser) {
    this.logger = logger;
    this.commandLineArguments = commandLineArguments;
    this.statusParser = statusParser;
  }

  public JavaxMailStorage get() throws MessagingException {
    Properties properties = new Properties();
    properties.setProperty("mail.store.protocol", "mstor");
    properties.setProperty("mstor.mbox.metadataStrategy", "none");
    properties.setProperty("mstor.mbox.cacheBuffers", "disabled");
    properties.setProperty("mstor.mbox.bufferStrategy", "mapped");
    properties.setProperty("mstor.metadata", "disabled");
    properties.setProperty("mstor.mozillaCompatibility", "enabled");

    Session session = Session.getDefaultInstance(properties);

    // /Users/flan/Desktop/Copy of Marie's Mail/Mail/Mail/mail.lean.to
    File mailbox = new File(commandLineArguments.mailboxFileName);
    if (!mailbox.exists()) {
      throw new MessagingException("No such mailbox:" + mailbox);
    }

    Store store = session.getStore(
        new URLName("mstor:" + mailbox.getAbsolutePath()));
    store.connect();

    return new ThunderbirdMailStorage(
        logger,
        new JavaxMailFolder(store.getDefaultFolder()),
        statusParser);
  }

}
