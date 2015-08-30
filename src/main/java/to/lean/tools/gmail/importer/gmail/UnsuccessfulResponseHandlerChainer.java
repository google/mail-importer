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

package to.lean.tools.gmail.importer.gmail;

import com.google.api.client.http.HttpUnsuccessfulResponseHandler;

/**
 * Chains together response handlers. For example:
 *
 * <pre>{@code
 *     HttpRequestInitializer httpRequestInitializer =
 *        request -> {
 *            new UnsuccessfulResponseHandlerChainer().chain(
 *                request.getUnsuccessfulResponseHandler(),
 *            new HttpBackOffUnsuccessfulResponseHandler(
 *                backoffBuilder.build()));
 *        };
 *     request.setHttpRequestInitializer(httpRequestInitializer);
 * }</pre>
 *
 * Note that the chainer skips {@code null} arguments so we don't need to check
 * the original handler.
 */
public class UnsuccessfulResponseHandlerChainer {

  /**
   * Returns a new {@link HttpUnsuccessfulResponseHandler} that runs all of
   * the given handlers in order until one of them returns {@code true}.
   *
   * @param handlers the handlers to run. Note that {@code null} values are
   *     skipped.
   */
  public HttpUnsuccessfulResponseHandler chain(
      HttpUnsuccessfulResponseHandler... handlers) {
    return (unsuccessfulRequest, response, supportsRetry) -> {
      boolean retry = false;
      for (int i = 0; i < handlers.length && !retry; i++) {
        if (handlers[i] != null) {
          retry = handlers[i].handleResponse(
              unsuccessfulRequest, response, supportsRetry);
        }
      }

      return retry;
    };
  }
}
