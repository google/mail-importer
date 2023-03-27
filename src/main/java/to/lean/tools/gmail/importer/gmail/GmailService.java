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

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpBackOffUnsuccessfulResponseHandler;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.BackOff;
import com.google.api.services.gmail.Gmail;
import javax.inject.Inject;
import javax.inject.Provider;

/** Constructs a {@link Gmail} instance that is ready for use. */
public class GmailService {

  private final Credential credential;
  private final Provider<BackOff> backOffProvider;
  private final HttpTransport httpTransport;
  private final JsonFactory jsonFactory;

  @Inject
  GmailService(
      User user,
      Credential credential,
      Provider<BackOff> backOffProvider,
      HttpTransport httpTransport,
      JsonFactory jsonFactory) {
    this.credential = credential;
    this.backOffProvider = backOffProvider;
    this.httpTransport = httpTransport;
    this.jsonFactory = jsonFactory;
  }

  Gmail getServiceWithRetries() {
    HttpRequestInitializer httpRequestInitializer =
        request -> {
          credential.initialize(request);
          new UnsuccessfulResponseHandlerChainer()
              .chain(
                  request.getUnsuccessfulResponseHandler(),
                  new HttpBackOffUnsuccessfulResponseHandler(backOffProvider.get()));
        };

    return new Gmail.Builder(httpTransport, jsonFactory, httpRequestInitializer)
        .setApplicationName(GmailServiceModule.APP_NAME)
        .build();
  }
}
