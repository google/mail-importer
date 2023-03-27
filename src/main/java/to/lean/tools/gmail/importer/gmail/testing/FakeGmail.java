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

package to.lean.tools.gmail.importer.gmail.testing;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.gmail.Gmail;

/** Created by flan on 9/5/15. */
public class FakeGmail extends Gmail {
  /**
   * Constructor.
   *
   * <p>Use {@link Builder} if you need to specify any of the optional parameters.
   *
   * @param transport HTTP transport, which should normally be:
   *     <ul>
   *       <li>Google App Engine: {@code
   *           com.google.api.client.extensions.appengine.http.UrlFetchTransport}
   *       <li>Android: {@code newCompatibleTransport} from {@code
   *           com.google.api.client.extensions.android.http.AndroidHttp}
   *       <li>Java: {@link GoogleNetHttpTransport#newTrustedTransport()}
   *     </ul>
   *
   * @param jsonFactory JSON factory, which may be:
   *     <ul>
   *       <li>Jackson: {@code com.google.api.client.json.jackson2.JacksonFactory}
   *       <li>Google GSON: {@code com.google.api.client.json.gson.GsonFactory}
   *       <li>Android Honeycomb or higher: {@code
   *           com.google.api.client.extensions.android.json.AndroidJsonFactory}
   *     </ul>
   *
   * @param httpRequestInitializer HTTP request initializer or {@code null} for none
   * @since 1.7
   */
  public FakeGmail(
      HttpTransport transport,
      JsonFactory jsonFactory,
      HttpRequestInitializer httpRequestInitializer) {
    super(transport, jsonFactory, httpRequestInitializer);
  }
}
