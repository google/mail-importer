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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Enumeration;
import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.search.SearchTerm;

/** Modernized wrapper for Message that throws {@code RuntimeMessagingException}. */
public class JavaxMailMessage extends Message {
  private final Message delegate;

  public JavaxMailMessage(Message delegate) {
    this.delegate = delegate;
  }

  @Override
  public Address[] getFrom() throws RuntimeMessagingException {
    try {
      return delegate.getFrom();
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public void setFrom() throws RuntimeMessagingException {
    try {
      delegate.setFrom();
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public void setFrom(Address address) throws RuntimeMessagingException {
    try {
      delegate.setFrom(address);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public void addFrom(Address[] addresses) throws RuntimeMessagingException {
    try {
      delegate.addFrom(addresses);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public Address[] getRecipients(RecipientType type) throws RuntimeMessagingException {
    try {
      return delegate.getRecipients(type);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public Address[] getAllRecipients() throws RuntimeMessagingException {
    try {
      return delegate.getAllRecipients();
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public void setRecipients(RecipientType type, Address[] addresses)
      throws RuntimeMessagingException {
    try {
      delegate.setRecipients(type, addresses);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public void setRecipient(RecipientType type, Address address) throws RuntimeMessagingException {
    try {
      delegate.setRecipient(type, address);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public void addRecipients(RecipientType type, Address[] addresses)
      throws RuntimeMessagingException {
    try {
      delegate.addRecipients(type, addresses);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public void addRecipient(RecipientType type, Address address) throws RuntimeMessagingException {
    try {
      delegate.addRecipient(type, address);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public Address[] getReplyTo() throws RuntimeMessagingException {
    try {
      return delegate.getReplyTo();
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public void setReplyTo(Address[] addresses) throws RuntimeMessagingException {
    try {
      delegate.setReplyTo(addresses);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public String getSubject() throws RuntimeMessagingException {
    try {
      return delegate.getSubject();
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public void setSubject(String subject) throws RuntimeMessagingException {
    try {
      delegate.setSubject(subject);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public Date getSentDate() throws RuntimeMessagingException {
    try {
      return delegate.getSentDate();
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public void setSentDate(Date date) throws RuntimeMessagingException {
    try {
      delegate.setSentDate(date);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public Date getReceivedDate() throws RuntimeMessagingException {
    try {
      return delegate.getReceivedDate();
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public Flags getFlags() throws RuntimeMessagingException {
    try {
      return delegate.getFlags();
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public boolean isSet(Flags.Flag flag) throws RuntimeMessagingException {
    try {
      return delegate.isSet(flag);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public void setFlags(Flags flag, boolean set) throws RuntimeMessagingException {
    try {
      delegate.setFlags(flag, set);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public void setFlag(Flags.Flag flag, boolean set) throws RuntimeMessagingException {
    try {
      delegate.setFlag(flag, set);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public int getMessageNumber() {
    return delegate.getMessageNumber();
  }

  @Override
  public Folder getFolder() {
    return delegate.getFolder();
  }

  @Override
  public boolean isExpunged() {
    return delegate.isExpunged();
  }

  @Override
  public JavaxMailMessage reply(boolean replyToAll) throws RuntimeMessagingException {
    try {
      return new JavaxMailMessage(delegate.reply(replyToAll));
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public void saveChanges() throws RuntimeMessagingException {
    try {
      delegate.saveChanges();
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public boolean match(SearchTerm term) throws RuntimeMessagingException {
    try {
      return delegate.match(term);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public int getSize() throws RuntimeMessagingException {
    try {
      return delegate.getSize();
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public int getLineCount() throws RuntimeMessagingException {
    try {
      return delegate.getLineCount();
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public String getContentType() throws RuntimeMessagingException {
    try {
      return delegate.getContentType();
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public boolean isMimeType(String mimeType) throws RuntimeMessagingException {
    try {
      return delegate.isMimeType(mimeType);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public String getDisposition() throws RuntimeMessagingException {
    try {
      return delegate.getDisposition();
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public void setDisposition(String disposition) throws RuntimeMessagingException {
    try {
      delegate.setDisposition(disposition);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public String getDescription() throws RuntimeMessagingException {
    try {
      return delegate.getDescription();
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public void setDescription(String description) throws RuntimeMessagingException {
    try {
      delegate.setDescription(description);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public String getFileName() throws RuntimeMessagingException {
    try {
      return delegate.getFileName();
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public void setFileName(String filename) throws RuntimeMessagingException {
    try {
      delegate.setFileName(filename);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public InputStream getInputStream() throws IOException, RuntimeMessagingException {
    try {
      return delegate.getInputStream();
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public DataHandler getDataHandler() throws RuntimeMessagingException {
    try {
      return delegate.getDataHandler();
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public Object getContent() throws IOException, RuntimeMessagingException {
    try {
      return delegate.getContent();
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public void setDataHandler(DataHandler dh) throws RuntimeMessagingException {
    try {
      delegate.setDataHandler(dh);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public void setContent(Object obj, String type) throws RuntimeMessagingException {
    try {
      delegate.setContent(obj, type);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public void setText(String text) throws RuntimeMessagingException {
    try {
      delegate.setText(text);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public void setContent(Multipart mp) throws RuntimeMessagingException {
    try {
      delegate.setContent(mp);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public void writeTo(OutputStream os) throws IOException, RuntimeMessagingException {
    try {
      delegate.writeTo(os);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public String[] getHeader(String header_name) throws RuntimeMessagingException {
    try {
      return delegate.getHeader(header_name);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public void setHeader(String header_name, String header_value) throws RuntimeMessagingException {
    try {
      delegate.setHeader(header_name, header_value);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public void addHeader(String header_name, String header_value) throws RuntimeMessagingException {
    try {
      delegate.addHeader(header_name, header_value);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public void removeHeader(String header_name) throws RuntimeMessagingException {
    try {
      delegate.removeHeader(header_name);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public Enumeration getAllHeaders() throws RuntimeMessagingException {
    try {
      return delegate.getAllHeaders();
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public Enumeration getMatchingHeaders(String[] header_names) throws RuntimeMessagingException {
    try {
      return delegate.getMatchingHeaders(header_names);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }

  @Override
  public Enumeration getNonMatchingHeaders(String[] header_names) throws RuntimeMessagingException {
    try {
      return delegate.getNonMatchingHeaders(header_names);
    } catch (MessagingException e) {
      throw new RuntimeMessagingException(e);
    }
  }
}
