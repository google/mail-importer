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

package to.lean.tools.gmail.importer.errorstrategy;

import to.lean.tools.gmail.importer.local.LocalMessage;

/**
 * Encapsulates interchangeable behavior for dealing with failures while uploading a message to the
 * Gmail API.
 */
public interface ErrorStrategy {

  /** How the error should affect the import. */
  public enum Result {
    /** Retry the action that caused the original failure. */
    RETRY,
    /** Skip the action that caused the original failure. */
    SKIP,
    /** Abort the import. */
    STOP
  }

  /**
   * Handles an error that has occurred while uploading a message to the Gmail API.
   *
   * <p>If the call to this method returns {@code true}, then the message will be re-uploaded.
   * Implementations that return {@code true} must be very careful not to continually retry the same
   * message.
   *
   * <p>Implementations should not throw exceptions from this method; any thrown exception will
   * cause the import to abort. The preferred way to abort the import is to return {@link
   * Result#STOP}.
   *
   * @param localMessage the message that failed to upload
   * @param gmailApiException the exception that caused the failure. Note that this will be the
   *     error returned by the Gmail API. It could be that the real cause is buried somewhere
   *     inside.
   * @return whether or not the message should be retried
   */
  Result handleError(LocalMessage localMessage, Exception gmailApiException);
}
