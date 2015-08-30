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
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleOAuthConstants;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Profile;
import com.google.common.base.Charsets;
import com.google.common.base.Verify;
import com.google.common.collect.ImmutableList;
import com.google.common.io.CharSource;
import com.google.common.io.Resources;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Encapsulates the authorization and authentication flow.
 */
class Authorizer implements Provider<Credential> {
  private HttpTransport httpTransport;
  private JsonFactory jsonFactory;
  private User user;

  @Inject
  public Authorizer(
      User user,
      HttpTransport httpTransport,
      JsonFactory jsonFactory) {
    this.httpTransport = httpTransport;
    this.jsonFactory = jsonFactory;
    this.user = user;
  }

  public Credential get() {
    try {
      GoogleClientSecrets clientSecrets = loadGoogleClientSecrets(jsonFactory);

      DataStore<StoredCredential> dataStore = getStoredCredentialDataStore();

      // Allow user to authorize via url.
      GoogleAuthorizationCodeFlow flow =
          new GoogleAuthorizationCodeFlow.Builder(
              httpTransport,
              jsonFactory,
              clientSecrets,
              ImmutableList.of(
                  GmailScopes.GMAIL_MODIFY,
                  GmailScopes.GMAIL_READONLY))
              .setCredentialDataStore(dataStore)
              .setAccessType("offline")
              .setApprovalPrompt("auto")
              .build();

      // First, see if we have a stored credential for the user.
      Credential credential = flow.loadCredential(user.getEmailAddress());

      // If we don't, prompt them to get one.
      if (credential == null) {
        String url = flow.newAuthorizationUrl()
            .setRedirectUri(GoogleOAuthConstants.OOB_REDIRECT_URI)
            .build();
        System.out.println("Please open the following URL in your browser then "
            + "type the authorization code:\n" + url);

        // Read code entered by user.
        System.out.print("Code: ");
        System.out.flush();
        BufferedReader br = new BufferedReader(
            new InputStreamReader(System.in));
        String code = br.readLine();

        // Generate Credential using retrieved code.
        GoogleTokenResponse response = flow.newTokenRequest(code)
            .setRedirectUri(GoogleOAuthConstants.OOB_REDIRECT_URI)
            .execute();

        credential =
            flow.createAndStoreCredential(response, user.getEmailAddress());
      }

      Gmail gmail = new Gmail.Builder(httpTransport, jsonFactory, credential)
          .setApplicationName(GmailServiceModule.APP_NAME)
          .build();

      Profile profile = gmail.users()
          .getProfile(user.getEmailAddress())
          .execute();

      System.out.println(profile.toPrettyString());
      return credential;
    } catch (IOException exception) {
      throw new RuntimeException(exception);
    }
  }

  private GoogleClientSecrets loadGoogleClientSecrets(JsonFactory jsonFactory)
      throws IOException {
    URL url = Resources.getResource("client_secret.json");
    CharSource inputSupplier =
        Resources.asCharSource(url, Charsets.UTF_8);
    return GoogleClientSecrets.load(jsonFactory, inputSupplier.openStream());
  }

  private DataStore<StoredCredential> getStoredCredentialDataStore()
      throws IOException {
    File userHomeDir = getUserHomeDir();
    File mailimporter = new File(userHomeDir, ".mailimporter");
    FileDataStoreFactory dataStoreFactory =
        new FileDataStoreFactory(mailimporter);
    return dataStoreFactory.getDataStore("credentials");
  }

  private File getUserHomeDir() {
    File userHome = new File(System.getProperty("user.home"));
    Verify.verify(userHome.exists() && userHome.canRead(),
        "Can not find user's home: %s", userHome);
    return userHome;
  }
}
