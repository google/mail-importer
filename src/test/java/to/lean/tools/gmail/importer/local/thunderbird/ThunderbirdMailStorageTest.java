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

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import java.util.logging.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import to.lean.tools.gmail.importer.local.JavaxMailFolder;
import to.lean.tools.gmail.importer.local.JavaxMailMessage;

@RunWith(JUnit4.class)
public class ThunderbirdMailStorageTest {

  @Mock private JavaxMailFolder rootFolder;
  private ThunderbirdMailStorage mailStorage;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    mailStorage =
        new ThunderbirdMailStorage(
            Logger.getAnonymousLogger(), rootFolder, new XMozillaStatusParser());
  }

  @Test
  public void testFilterFolders() throws Exception {
    ImmutableList<JavaxMailFolder> folders =
        ImmutableList.of(makeFolder("folder1"), makeFolder("@folder2"), makeFolder("!folder3"));
    ImmutableList<JavaxMailFolder> expectedFolders =
        ImmutableList.of(folders.get(0), folders.get(2));

    ImmutableList<JavaxMailFolder> filteredFolders =
        ImmutableList.copyOf(mailStorage.filterFolders(folders.iterator()));
    assertThat(filteredFolders).containsExactlyElementsIn(expectedFolders);
  }

  @Test
  public void testCreateLocalMessage() throws Exception {
    JavaxMailMessage message = mock(JavaxMailMessage.class);
    JavaxMailFolder mailMessageFolder = makeFolder("/abc/root/xyz/pdq.sbd");

    when(rootFolder.getFullName()).thenReturn("/abc/root");
    when(message.getFolder()).thenReturn(mailMessageFolder);

    ThunderbirdLocalMessage localMessage = mailStorage.createLocalMessage(message);

    assertThat(localMessage.getFolders()).containsExactlyElementsIn(ImmutableList.of("xyz/pdq"));
  }

  private JavaxMailFolder makeFolder(String name) {
    JavaxMailFolder folder = mock(JavaxMailFolder.class);
    when(folder.getFullName()).thenReturn(name);
    when(folder.getName()).thenReturn(basename(name));

    return folder;
  }

  private String basename(String name) {
    int lastSlash = name.lastIndexOf('/');
    if (lastSlash >= 0) {
      return name.substring(lastSlash + 1);
    }
    return name;
  }
}
