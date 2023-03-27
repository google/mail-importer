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

import org.kohsuke.args4j.Option;

/** Encapsulation of all command line arguments. */
public class CommandLineArguments {

  @Option(
      name = "--mailbox",
      metaVar = "DIRECTORY",
      required = true,
      usage = "Specifies the root of the mailbox to open.")
  public String mailboxFileName;

  @Option(
      name = "--user",
      metaVar = "USER",
      usage =
          "Specifies the Gmail user name for whom to import mail. The "
              + "default value of 'me' means that the account will be taken from "
              + "the credentials provided. If any other value is specified, that "
              + "value must match the credentials. This mode allows administrators "
              + "to import mail into other accounts using their administrator "
              + "credentials.")
  public String user = "me";

  @Option(
      name = "--max_messages",
      usage =
          "The maximim number of messages to import to Gmail in this "
              + "run. This can be useful for testing an import.")
  public Integer maxMessages;

  @Option(
      name = "--client_secret_resource_path",
      metaVar = "SECRET_RESOURCE_PATH",
      hidden = true,
      usage =
          "Path to the developer secret that developers can retrieve "
              + "from the developer console. Pre-built binaries should already "
              + "have this included.")
  public String clientSecretResourcePath = "/resources/client_secret.json";
}
