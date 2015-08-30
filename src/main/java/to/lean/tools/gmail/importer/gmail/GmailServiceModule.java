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
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.BackOff;
import com.google.api.client.util.ExponentialBackOff;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import to.lean.tools.gmail.importer.CommandLineArguments;

import javax.inject.Singleton;

/**
 * Module that provides the Gmail service.
 */
public class GmailServiceModule extends AbstractModule {
  static final String APP_NAME = "Mail Importer for Gmail";

  @Override
  protected void configure() {
    requireBinding(CommandLineArguments.class);

    bind(GmailService.class).in(Singleton.class);
    bind(Credential.class)
        .toProvider(Authorizer.class)
        .in(Singleton.class);

    bind(ExponentialBackOff.Builder.class)
        .toInstance(new ExponentialBackOff.Builder()
            .setInitialIntervalMillis(1000)
            .setMultiplier(2)
            .setRandomizationFactor(0.5)
            .setMaxIntervalMillis(60000)
            .setMaxElapsedTimeMillis(300000));
  }

  @Provides @Singleton
  User provideUser(CommandLineArguments commandLineArguments) {
    return User.create(commandLineArguments.user);
  }

  @Provides @Singleton
  HttpTransport provideHttpTransport() {
    return new NetHttpTransport();
  }

  @Provides @Singleton
  JsonFactory provideJsonFactory() {
    return new JacksonFactory();
  }

  @Provides
  BackOff provideExponentialBackoff(
      ExponentialBackOff.Builder builder) {
    return builder.build();
  }
}
