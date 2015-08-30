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

package to.lean.tools.gmail.importer;

import com.google.api.client.util.Lists;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import to.lean.tools.gmail.importer.gmail.GmailServiceModule;
import to.lean.tools.gmail.importer.gmail.GmailSyncer;
import to.lean.tools.gmail.importer.local.LocalMessage;
import to.lean.tools.gmail.importer.local.LocalStorage;
import to.lean.tools.gmail.importer.local.thunderbird.ThunderbirdModule;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Copies messages from {@link to.lean.tools.gmail.importer.local.LocalStorage}
 * to a {@link to.lean.tools.gmail.importer.gmail.GmailSyncer} in batches. This
 * processes is single-threaded. When errors occur, an
 * {@link to.lean.tools.gmail.importer.errorstrategy.ErrorStrategy} is used to
 * handle the error.
 */
public class Importer {
  private static final int BATCH_SIZE = 100;

  private final Logger logger;
  private final MailProvider<LocalStorage> storageProvider;
  private final GmailSyncer gmailSyncer;
  private final CommandLineArguments commandLineArguments;

  /**
   * Main entry point for running {@code Importer} as a stand-alone application.
   * Commandline options can be found in
   * {@link to.lean.tools.gmail.importer.CommandLineArguments}.
   *
   * @param args the command line arguments
   * @throws MessagingException if there is a problem reading from the local
   *     store
   * @throws IOException if there is a problem with the Gmail connection
   */
  public static void main(String[] args)
      throws MessagingException, IOException {
    CommandLineArguments commandLineArguments =
        new CommandLineArguments();
    CmdLineParser commandLine =
        new CmdLineParser(commandLineArguments);
    try {
      commandLine.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      commandLine.printUsage(System.err);
      System.exit(1);
    }

    Injector injector = Guice.createInjector(
        new FlagsModule(commandLineArguments),
        new ThunderbirdModule(),
        new GmailServiceModule());

    Importer importer = injector.getInstance(Importer.class);
    importer.importMail();
  }

  @Inject
  Importer(
      Logger logger,
      MailProvider<LocalStorage> storageProvider,
      GmailSyncer gmailSyncer,
      CommandLineArguments commandLineArguments) {
    this.logger = logger;
    this.storageProvider = storageProvider;
    this.gmailSyncer = gmailSyncer;
    this.commandLineArguments = commandLineArguments;
  }

  public void importMail() throws MessagingException, IOException {
    LocalStorage storage = storageProvider.get();
    gmailSyncer.init();

    int messagesImported = 0;
    Iterator<LocalMessage> iterator = storage.iterator();
    while (iterator.hasNext() && keepImporting(messagesImported)) {
      List<LocalMessage> batch = Lists.newArrayListWithCapacity(BATCH_SIZE);
      for (int i = 0;
           i < BATCH_SIZE
               && iterator.hasNext()
               && keepImporting(messagesImported);
           i++) {
        LocalMessage message = iterator.next();
        logger.fine(() -> "Id: " + message.getMessageId());
        logger.fine(() -> "Folders: " + message.getFolders());
        batch.add(message);
        messagesImported++;
      }
      gmailSyncer.sync(batch);
    }
  }

  private boolean keepImporting(int messagesImported) {
    return commandLineArguments.maxMessages == null
        || messagesImported < commandLineArguments.maxMessages;
  }
}
