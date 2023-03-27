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

/**
 * Encapsulates the X-Mozilla-Status header. The flag values are interpreted as described at <a
 * href="http://www.eyrich-net.org/mozilla/X-Mozilla-Status.html?en">
 * http://www.eyrich-net.org/mozilla/X-Mozilla-Status.html </a>.
 */
@SuppressWarnings("unused") // we don't use all of the X-Mozilla-Status flags
public class XMozillaStatus {
  /* From: http://www.eyrich-net.org/mozilla/X-Mozilla-Status.html?en */

  /* X-Mozilla-Status */

  /** Message has been read. */
  private static final int MSG_FLAG_READ = 0x0001;
  /** A reply has been successfully sent. */
  private static final int MSG_FLAG_REPLIED = 0x0002;
  /** The user has flagged this message. */
  private static final int MSG_FLAG_MARKED = 0x0004;
  /**
   * Already gone (when folder not compacted). Since actually removing a message from a folder is a
   * semi-expensive operation, we tend to delay it; messages with this bit set will be removed the
   * next time folder compaction is done. Once this bit is set, it never gets un-set.
   */
  private static final int MSG_FLAG_EXPUNGED = 0x0008;
  /**
   * Whether subject has “Re:” on the front. The folder summary uniquifies all of the strings in it,
   * and to help this, any string which begins with “Re:” has that stripped first. This bit is then
   * set, so that when presenting the message, we know to put it back (since the “Re:” is not itself
   * stored in the file).
   */
  private static final int MSG_FLAG_HAS_RE = 0x0010;
  /** Whether the children of this sub-thread are folded in the display. */
  private static final int MSG_FLAG_ELIDED = 0x0020;
  /** DB has offline news or imap article. */
  private static final int MSG_FLAG_OFFLINE = 0x0080;
  /** If set, this thread is watched. */
  private static final int MSG_FLAG_WATCHED = 0x0100;
  /**
   * If set, then this message's sender has been authenticated when sending this msg. This means the
   * POP3 server gave a positive answer to the XSENDER command. Since this command is no standard
   * and only known by few servers, this flag is unmeaning in most cases.
   */
  private static final int MSG_FLAG_SENDER_AUTHED = 0x0200;
  /**
   * If set, then this message's body contains not the whole message, and a link is available in the
   * message to download the rest of it from the POP server. This can be only a few lines of the
   * message (in case of size restriction for the download of messages) or nothing at all (in case
   * of “Fetch headers only”)
   */
  private static final int MSG_FLAG_PARTIAL = 0x0400;
  /**
   * If set, this message is queued for delivery. This only ever gets set on messages in the queue
   * folder, but is used to protect against the case of other messages having made their way in
   * there somehow – if some other program put a message in the queue, it won't be delivered later!
   */
  private static final int MSG_FLAG_QUEUED = 0x0800;
  /** This message has been forwarded. */
  private static final int MSG_FLAG_FORWARDED = 0x1000;
  /** These are used to remember the message priority in internal status flags. */
  private static final int MSG_FLAG_PRIORITIES = 0xE000;

  /* X-Mozilla-Status2 */

  /** This message is new since the last time the folder was closed. */
  private static final int MSG_FLAG_NEW = 0x00010000;
  /** If set, this thread is ignored. */
  private static final int MSG_FLAG_IGNORED = 0x00040000;
  /**
   * If set, this message is marked as deleted on the server. This only applies to messages on IMAP
   * servers.
   */
  private static final int MSG_FLAG_IMAP_DELETED = 0x00200000;
  /**
   * This message required to send a MDN (Message Disposition Notification) to the sender of the
   * message. For information about MDN see Wikipedia:Return receipt.
   */
  private static final int MSG_FLAG_MDN_REPORT_NEEDED = 0x00400000;
  /**
   * An MDN report message has been sent for this message. No more MDN report should be sent to the
   * sender.
   */
  private static final int MSG_FLAG_MDN_REPORT_SENT = 0x00800000;
  /** If set, this message is a template. */
  private static final int MSG_FLAG_TEMPLATE = 0x01000000;
  /**
   * These are used to store the message label.
   *
   * <pre>
   *   label	value
   *       1    0x02000000
   *       2    0x04000000
   *       3    0x06000000
   *       4    0x08000000
   *       5    0x0A000000
   *       6    0x0C000000
   *       7    0x0E000000
   * </pre>
   */
  private static final int MSG_FLAG_LABELS = 0x0E000000;
  /** If set, this message has files attached to it. */
  private static final int MSG_FLAG_ATTACHMENT = 0x10000000;

  private final int status;

  /**
   * Extracts the X-Mozilla-Status for the given {@code message}. If the message does not have the
   * appropriate header field, then we treat the message as if it has none of the bits set. If there
   * is more than one header, this method throws a {@link com.google.common.base.VerifyException}.
   *
   * @param status the message to query
   * @throws com.google.common.base.VerifyException if there is more than one status header
   */
  XMozillaStatus(int status) {
    this.status = status;
  }

  public boolean isRead() {
    return (status & MSG_FLAG_READ) != 0;
  }

  public boolean isMarked() {
    return (status & MSG_FLAG_MARKED) != 0;
  }
}
