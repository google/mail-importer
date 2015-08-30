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

import com.google.common.collect.Iterators;
import to.lean.tools.gmail.importer.local.JavaxMailFolder;
import to.lean.tools.gmail.importer.local.JavaxMailMessage;
import to.lean.tools.gmail.importer.local.JavaxMailStorage;

import java.util.Iterator;
import java.util.logging.Logger;

/**
* Created by flan on 12/21/14.
*/
class ThunderbirdMailStorage extends JavaxMailStorage {

  private final XMozillaStatusParser statusParser;

  public ThunderbirdMailStorage(
      Logger logger, JavaxMailFolder store, XMozillaStatusParser statusParser) {
    super(logger, store);
    this.statusParser = statusParser;
  }

  @Override
  protected Iterator<JavaxMailFolder> filterFolders(
      Iterator<JavaxMailFolder> iterator) {
    return Iterators.filter(iterator,
        folder -> !folder.getName().contains("@"));
  }

  @Override
  public ThunderbirdLocalMessage createLocalMessage(
      JavaxMailMessage message) {
    return new ThunderbirdLocalMessage(message, this::relativize, statusParser);
  }

  private String relativize(String folder) {
    /*
     * For Thunderbird, we need to remove the ".sbd" from the parent
     * folder names.
     */
    String rootFullName = root.getFullName();
    return (folder.startsWith(rootFullName)
                ? folder.substring(rootFullName.length() + 1)
                : folder)
        .replace(".sbd", "");
  }
}
