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

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

@RunWith(JUnit4.class)
public class CommandLineArgumentsTest {

  /*
   * Note that it's basically impossible to test the main() method because it
   * calls System.exit(). Thus we test the argument parsing code "raw".
   */

  @Test
  public void testArgumentParsing_noRequiredArguments() throws Exception {
    String[] args = {};
    try {
      parse(args);
      assertWithMessage("Should have thrown an exception").fail();
    } catch (CmdLineException e) {
      assertThat(e.getMessage()).contains("--mailbox");
    }
  }

  @Test
  public void testArgumentParsing_bogusMailbox() throws Exception {
    String[] args = {"--mailbox=/no/such/directory"};

    CommandLineArguments commandLineArguments = parse(args);

    assertThat(commandLineArguments.mailboxFileName)
        .named("mailbox")
        .isEqualTo("/no/such/directory");
    assertThat(commandLineArguments.user).named("user").isEqualTo("me");
  }

  @Test
  public void testArgumentParsing_bogusMailboxAndUser() throws Exception {
    String[] args = {"--mailbox=/no/such/directory", "--user=foobar"};

    CommandLineArguments commandLineArguments = parse(args);
    assertThat(commandLineArguments.mailboxFileName)
        .named("mailbox")
        .isEqualTo("/no/such/directory");
    assertThat(commandLineArguments.user).named("user").isEqualTo("foobar");
    assertThat(commandLineArguments.maxMessages).isNull();
  }

  private CommandLineArguments parse(String[] args) throws CmdLineException {
    CommandLineArguments commandLineArguments = new CommandLineArguments();
    CmdLineParser commandLine = new CmdLineParser(commandLineArguments);
    commandLine.parseArgument(args);
    return commandLineArguments;
  }
}
