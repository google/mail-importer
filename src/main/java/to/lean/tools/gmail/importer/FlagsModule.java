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

package to.lean.tools.gmail.importer;

import com.google.inject.AbstractModule;

/**
 * Makes the command line arguments available through Guice.
 */
public class FlagsModule extends AbstractModule {
  private final CommandLineArguments commandLineArguments;

  public FlagsModule(CommandLineArguments commandLineArguments) {
    this.commandLineArguments = commandLineArguments;
  }

  @Override
  protected void configure() {
    bind(CommandLineArguments.class)
        .toInstance(commandLineArguments);
  }
}
