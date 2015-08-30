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

import com.google.common.base.Verify;
import to.lean.tools.gmail.importer.local.JavaxMailMessage;

import java.util.Arrays;

/**
 * Encapsulates the X-Mozilla-Status header. The flag values are interpreted
 * as described at
 * <a href="http://www.eyrich-net.org/mozilla/X-Mozilla-Status.html?en">
 *   http://www.eyrich-net.org/mozilla/X-Mozilla-Status.html
 * </a>.
 */
@SuppressWarnings("unused") // we don't use all of the X-Mozilla-Status flags
public class XMozillaStatusParser {
  /**
   * Extracts the X-Mozilla-Status for the given {@code message}. If the
   * message does not have the appropriate header field, then we treat the
   * message as if it has none of the bits set. If there is more than one
   * header, this method throws a
   * {@link com.google.common.base.VerifyException}.
   *
   * @param message the message to query
   * @throws com.google.common.base.VerifyException if there is more than one
   *     status header
   */
  public XMozillaStatus parse(JavaxMailMessage message) {
    int status = 0;

    String[] statusHeader = message.getHeader("X-Mozilla-Status");
    if (statusHeader != null && statusHeader.length > 0) {
      Verify.verify(statusHeader.length == 1,
          "Status header length should be 1. (%s)",
          Arrays.toString(statusHeader));
      status = Integer.parseInt(statusHeader[0], 16);
    }

    return new XMozillaStatus(status);
  }
}
