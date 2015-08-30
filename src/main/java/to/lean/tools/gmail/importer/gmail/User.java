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

import com.google.auto.value.AutoValue;
import com.google.common.base.Charsets;

import java.util.Base64;

/**
 * Encapsulates information about the user in a type safe way.
 */
@AutoValue
abstract class User {

  static User create(String emailAddress) {
    return new AutoValue_User(emailAddress);
  }

  abstract String getEmailAddress();

  String getEmailAddressAsKey() {
    return Base64.getEncoder()
        .encodeToString(getEmailAddress().getBytes(Charsets.UTF_8));
  }
}
