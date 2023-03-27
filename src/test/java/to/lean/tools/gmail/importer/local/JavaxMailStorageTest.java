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

package to.lean.tools.gmail.importer.local;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.logging.Logger;
import javax.mail.Folder;
import javax.mail.MessagingException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.GreaterThan;
import org.mockito.internal.matchers.LessOrEqual;

@RunWith(JUnit4.class)
public class JavaxMailStorageTest {

  private JavaxMailStorage javaxMailStorage;

  @Mock private JavaxMailFolder javaxMailFolder;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    javaxMailStorage = newJavaxMailStorage();
  }

  @Test
  public void testIterator_simple() throws Exception {
    javaxMailFolder = makeMockFolderWithMessages(5);

    javaxMailStorage = newJavaxMailStorage();

    assertThat(javaxMailStorage).hasSize(5);
  }

  @Test
  public void testIterator_withSubFolders() throws Exception {
    javaxMailFolder =
        makeMockFolderWithMessages(
            5,
            makeMockFolderWithMessages(10),
            makeMockFolderWithMessages(
                15,
                makeMockFolderWithMessages(
                    6, makeMockFolderWithMessages(9), makeMockFolderWithMessages(3)),
                makeMockFolderWithMessages(
                    0,
                    makeMockFolderWithMessages(
                        0, makeMockFolderWithMessages(0, makeMockFolderWithMessages(5))))),
            makeMockFolderWithMessages(7));

    javaxMailStorage = newJavaxMailStorage();

    assertThat(javaxMailStorage).hasSize(60);
  }

  @Test
  public void testIterator_pathologicalNoMessages() throws Exception {
    javaxMailFolder =
        makeMockFolderWithMessages(
            0,
            makeMockFolderWithMessages(0),
            makeMockFolderWithMessages(
                0,
                makeMockFolderWithMessages(
                    0, makeMockFolderWithMessages(0), makeMockFolderWithMessages(0)),
                makeMockFolderWithMessages(
                    0,
                    makeMockFolderWithMessages(
                        0, makeMockFolderWithMessages(0, makeMockFolderWithMessages(0))))),
            makeMockFolderWithMessages(0));

    javaxMailStorage = newJavaxMailStorage();

    assertThat(javaxMailStorage).hasSize(0);
  }

  @Test
  public void testIterator_getWithoutLooking() throws Exception {
    javaxMailFolder =
        makeMockFolderWithMessages(
            0,
            makeMockFolderWithMessages(0),
            makeMockFolderWithMessages(
                0,
                makeMockFolderWithMessages(
                    0, makeMockFolderWithMessages(0), makeMockFolderWithMessages(1))));

    javaxMailStorage = newJavaxMailStorage();

    assertThat(javaxMailStorage.iterator().next()).isNotNull();
  }

  private JavaxMailFolder makeMockFolderWithMessages(int numMessages, JavaxMailFolder... folders) {
    JavaxMailFolder javaxMailFolder = mock(JavaxMailFolder.class);

    when(javaxMailFolder.getType()).thenReturn(Folder.HOLDS_FOLDERS | Folder.HOLDS_MESSAGES);

    when(javaxMailFolder.getMessageCount()).thenReturn(numMessages);
    when(javaxMailFolder.getMessage(Matchers.intThat(new LessOrEqual<>(numMessages))))
        .thenReturn(mock(JavaxMailMessage.class));
    when(javaxMailFolder.getMessage(Matchers.intThat(new GreaterThan<>(numMessages))))
        .thenThrow(new RuntimeMessagingException(new MessagingException("crap")));

    when(javaxMailFolder.list()).thenReturn(folders);
    return javaxMailFolder;
  }

  private JavaxMailStorage newJavaxMailStorage() {
    return new JavaxMailStorage(Logger.getLogger("test"), javaxMailFolder) {
      @Override
      public LocalMessage createLocalMessage(JavaxMailMessage message) {
        return mock(LocalMessage.class);
      }
    };
  }
}
