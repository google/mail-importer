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

import com.google.common.annotations.VisibleForTesting;

import javax.annotation.concurrent.NotThreadSafe;
import javax.mail.Folder;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

/**
 * Scans local mailboxes for messages to sync.
 */
@NotThreadSafe
public abstract class JavaxMailStorage implements LocalStorage {
  protected final Logger logger;
  protected final JavaxMailFolder root;

  public JavaxMailStorage(Logger logger, JavaxMailFolder root) {
    this.logger = logger;
    this.root = root;
  }

  @Override
  public Iterator<LocalMessage> iterator() {
    return new FolderIterator(root);
  }

  /** Must return a filtered view of {@code iterator}. */
  protected Iterator<JavaxMailFolder> filterFolders(
      Iterator<JavaxMailFolder> iterator) {
    return iterator;
  }

  public abstract LocalMessage createLocalMessage(JavaxMailMessage message);

  @VisibleForTesting
  class FolderIterator implements Iterator<LocalMessage> {
    private final JavaxMailFolder folder;
    private final Iterator<LocalMessage> messageIterator;
    private final Iterator<LocalMessage> subfolderIterator;

    /*
     * We do a prefix traversal of the folder. This means that we first get
     * messages from this folder until they are exhausted, then we call this
     * recursively for each subfolder.
     */
    public FolderIterator(JavaxMailFolder folder) {
      this.folder = folder;
      logger.fine(() -> "Opening folder:" + folder.getName());
      if (!folder.isOpen()) {
        folder.open(Folder.READ_ONLY);
      }
      this.messageIterator = (folder.getType() & Folder.HOLDS_MESSAGES) > 0
          ? new MessageIterator()
          : Collections.emptyIterator();
      this.subfolderIterator = (folder.getType() & Folder.HOLDS_FOLDERS) > 0
          ? new SubfolderIterator()
          : Collections.emptyIterator();
    }

    @Override
    public boolean hasNext() {
      return messageIterator.hasNext() || subfolderIterator.hasNext();
    }

    @Override
    public LocalMessage next() {
      if (messageIterator.hasNext()) {
        return messageIterator.next();
      }

      if (subfolderIterator.hasNext()) {
        return subfolderIterator.next();
      }

      throw new NoSuchElementException("No more messages in folder.");
    }

    private class MessageIterator implements Iterator<LocalMessage> {
      private final int messageCount;
      private int nextMessage;

      private MessageIterator() {
        messageCount = folder.getMessageCount();
        nextMessage = 1;
      }

      @Override
      public boolean hasNext() {
        return nextMessage <= messageCount;
      }

      @Override
      public LocalMessage next() {
        int currentMessage = nextMessage;
        nextMessage++;
        JavaxMailMessage message = folder.getMessage(currentMessage);

        return createLocalMessage(message);
      }
    }

    private class SubfolderIterator implements Iterator<LocalMessage> {
      private final Iterator<JavaxMailFolder> folderIterator;
      private Iterator<LocalMessage> currentMessageIterator =
          Collections.emptyIterator();

      private SubfolderIterator() {
        folderIterator = filterFolders(Arrays.asList(folder.list()).iterator());
      }

      @Override
      public boolean hasNext() {
        while (!currentMessageIterator.hasNext()
            && folderIterator.hasNext()) {
          currentMessageIterator = new FolderIterator(folderIterator.next());
        }
        return currentMessageIterator.hasNext();
      }

      @Override
      public LocalMessage next() {
        while (!currentMessageIterator.hasNext()
            && folderIterator.hasNext()) {
          currentMessageIterator = new FolderIterator(folderIterator.next());
        }
        if (currentMessageIterator.hasNext()) {
          return currentMessageIterator.next();
        }

        throw new NoSuchElementException("Out of subfolder messages");
      }
    }
  }
}
