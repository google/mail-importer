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

package to.lean.tools.gmail.importer.local;

import java.util.List;

/**
 * Provides a generic interface to messages on local storage that can be used get the contents and
 * applicable labels for the message.
 */
public interface LocalMessage {
  /** Returns the RFC822 message id of the message. */
  String getMessageId();

  /** Returns the From header of the message. */
  String getFromHeader();

  /**
   * Returns a list of folders that the message is in. The names of the folders must be relative to
   * the root of the local store. For example, if the local store is at {@code
   * .../Mail/pop.host.com} and the message is in {@code .../Mail/pop.host.com/work/client/job},
   * then the folder should be named "work/client/job".
   *
   * <p>Note that if messages with the same id are in different folders and it is OK to not return
   * all of the names at once if the message in the other folder will be processed.
   */
  List<String> getFolders();

  /** Returns the raw, underlying bytes of the message. */
  byte[] getRawContent();

  boolean isUnread();

  boolean isStarred();
}
