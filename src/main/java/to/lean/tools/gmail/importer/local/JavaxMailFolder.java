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

import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.event.ConnectionListener;
import javax.mail.event.FolderListener;
import javax.mail.event.MessageChangedListener;
import javax.mail.event.MessageCountListener;
import javax.mail.search.SearchTerm;
import java.io.Closeable;

/**
 * Friendly wrapper for {@code javax.mail.Folder} that brings it into the
 * modern age by implementing {@link AutoCloseable} and it doesn't throw any
 * checked exceptions, translating all {@code MessagingException}s into
 * {@code RuntimeMessagingException}s.
 */
public class JavaxMailFolder extends Folder
    implements Closeable, AutoCloseable {
  Folder delegate;

  public JavaxMailFolder(Folder delegate) {
    super(delegate.getStore());
    this.delegate = delegate;
  }

  @Override
  public String getName() {
    return delegate.getName();
  }

  @Override
  public String getFullName() {
    return delegate.getFullName();
  }

  @Override
  public URLName getURLName() throws RuntimeMessagingException {
    try {
      return delegate.getURLName();
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public Store getStore() {
    return delegate.getStore();
  }

  @Override
  public JavaxMailFolder getParent() throws RuntimeMessagingException {
    try {
      return new JavaxMailFolder(delegate.getParent());
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public boolean exists() throws RuntimeMessagingException {
    try {
      return delegate.exists();
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public JavaxMailFolder[] list(String pattern)
      throws RuntimeMessagingException {
    try {
      return decorateFolderArray(delegate.list(pattern));
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public JavaxMailFolder[] listSubscribed(String pattern)
      throws RuntimeMessagingException {
    try {
      return decorateFolderArray(delegate.listSubscribed(pattern));
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public JavaxMailFolder[] list() throws RuntimeMessagingException {
    try {
      return decorateFolderArray(delegate.list());
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public JavaxMailFolder[] listSubscribed() throws RuntimeMessagingException {
    try {
      return decorateFolderArray(delegate.listSubscribed());
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public char getSeparator() throws RuntimeMessagingException {
    try {
      return delegate.getSeparator();
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public int getType() throws RuntimeMessagingException {
    try {
      return delegate.getType();
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public boolean create(int type) throws RuntimeMessagingException {
    try {
      return delegate.create(type);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public boolean isSubscribed() {
    return delegate.isSubscribed();
  }

  @Override
  public void setSubscribed(boolean subscribe)
      throws RuntimeMessagingException {
    try {
      delegate.setSubscribed(subscribe);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public boolean hasNewMessages() throws RuntimeMessagingException {
    try {
      return delegate.hasNewMessages();
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public JavaxMailFolder getFolder(String name)
      throws RuntimeMessagingException {
    try {
      return new JavaxMailFolder(delegate.getFolder(name));
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public boolean delete(boolean recurse) throws RuntimeMessagingException {
    try {
      return delegate.delete(recurse);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public boolean renameTo(Folder f) throws RuntimeMessagingException {
    try {
      return delegate.renameTo(f);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public void open(int mode) throws RuntimeMessagingException {
    try {
      delegate.open(mode);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public void close(boolean expunge) throws RuntimeMessagingException {
    try {
      delegate.close(expunge);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public void close() throws RuntimeMessagingException {
    try {
      delegate.close(false /* expunge */);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public boolean isOpen() {
    return delegate.isOpen();
  }

  @Override
  public int getMode() {
    return delegate.getMode();
  }

  @Override
  public Flags getPermanentFlags() {
    return delegate.getPermanentFlags();
  }

  @Override
  public int getMessageCount() throws RuntimeMessagingException {
    try {
      return delegate.getMessageCount();
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public int getNewMessageCount() throws RuntimeMessagingException {
    try {
      return delegate.getNewMessageCount();
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public int getUnreadMessageCount() throws RuntimeMessagingException {
    try {
      return delegate.getUnreadMessageCount();
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public int getDeletedMessageCount() throws RuntimeMessagingException {
    try {
      return delegate.getDeletedMessageCount();
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public JavaxMailMessage getMessage(int msgnum)
      throws RuntimeMessagingException {
    try {
      return new JavaxMailMessage(delegate.getMessage(msgnum));
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public JavaxMailMessage[] getMessages(int start, int end)
      throws RuntimeMessagingException {
    try {
      return decorateMessageArray(delegate.getMessages(start, end));
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public JavaxMailMessage[] getMessages(int[] msgnums)
      throws RuntimeMessagingException {
    try {
      return decorateMessageArray(delegate.getMessages(msgnums));
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public JavaxMailMessage[] getMessages() throws RuntimeMessagingException {
    try {
      return decorateMessageArray(delegate.getMessages());
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public void appendMessages(Message[] msgs) throws RuntimeMessagingException {
    try {
      delegate.appendMessages(msgs);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public void fetch(Message[] msgs, FetchProfile fp)
      throws RuntimeMessagingException {
    try {
      delegate.fetch(msgs, fp);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public void setFlags(
      Message[] msgs,
      Flags flag,
      boolean value) throws RuntimeMessagingException {
    try {
      delegate.setFlags(msgs, flag, value);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public void setFlags(int start, int end, Flags flag, boolean value) throws
      RuntimeMessagingException {
    try {
      delegate.setFlags(start, end, flag, value);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public void setFlags(int[] msgnums, Flags flag, boolean value) throws
      RuntimeMessagingException {
    try {
      delegate.setFlags(msgnums, flag, value);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public void copyMessages(Message[] msgs, Folder folder) throws
      RuntimeMessagingException {
    try {
      delegate.copyMessages(msgs, folder);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public JavaxMailMessage[] expunge() throws RuntimeMessagingException {
    try {
      return decorateMessageArray(delegate.expunge());
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public JavaxMailMessage[] search(SearchTerm term)
      throws RuntimeMessagingException {
    try {
      return decorateMessageArray(delegate.search(term));
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public JavaxMailMessage[] search(SearchTerm term, Message[] msgs)
      throws RuntimeMessagingException {
    try {
      return decorateMessageArray(delegate.search(term, msgs));
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public void addConnectionListener(ConnectionListener l) {
    delegate.addConnectionListener(l);
  }

  @Override
  public void removeConnectionListener(ConnectionListener l) {
    delegate.removeConnectionListener(l);
  }

  @Override
  public void addFolderListener(FolderListener l) {
    delegate.addFolderListener(l);
  }

  @Override
  public void removeFolderListener(FolderListener l) {
    delegate.removeFolderListener(l);
  }

  @Override
  public void addMessageCountListener(MessageCountListener l) {
    delegate.addMessageCountListener(l);
  }

  @Override
  public void removeMessageCountListener(MessageCountListener l) {
    delegate.removeMessageCountListener(l);
  }

  @Override
  public void addMessageChangedListener(MessageChangedListener l) {
    delegate.addMessageChangedListener(l);
  }

  @Override
  public void removeMessageChangedListener(MessageChangedListener l) {
    delegate.removeMessageChangedListener(l);
  }

  @Override
  public String toString() {
    return delegate.toString();
  }

  private JavaxMailFolder[] decorateFolderArray(Folder[] rawList) {
    JavaxMailFolder[] result = new JavaxMailFolder[rawList.length];
    for (int i = 0; i < result.length; i++) {
      result[i] = new JavaxMailFolder(rawList[i]);
    }
    return result;
  }

  private JavaxMailMessage[] decorateMessageArray(Message[] rawList) {
    JavaxMailMessage[] result = new JavaxMailMessage[rawList.length];
    for (int i = 0; i < result.length; i++) {
      result[i] = new JavaxMailMessage(rawList[i]);
    }
    return result;
  }
}
