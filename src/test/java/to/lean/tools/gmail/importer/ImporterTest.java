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

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.client.util.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import to.lean.tools.gmail.importer.gmail.GmailService;
import to.lean.tools.gmail.importer.gmail.GmailSyncer;
import to.lean.tools.gmail.importer.local.LocalMessage;
import to.lean.tools.gmail.importer.local.LocalStorage;

@RunWith(JUnit4.class)
public class ImporterTest {

  @Mock private GmailSyncer gmailSyncer;
  @Mock private GmailService gmailService;
  @Mock private LocalStorage localStorage;
  @Mock private Iterator<LocalMessage> localStorageIterator;

  @Captor private ArgumentCaptor<List<LocalMessage>> messageListCaptor;

  private CommandLineArguments commandLineArguments = new CommandLineArguments();
  private MailProvider<LocalStorage> localStorageProvider = () -> localStorage;

  private Importer importer;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    when(localStorage.iterator()).thenReturn(localStorageIterator);

    importer =
        new Importer(
            Logger.getLogger("test"), localStorageProvider, gmailSyncer, commandLineArguments);
  }

  @Test
  public void testImportMail_maxMessagesRespected() throws Exception {
    when(localStorageIterator.hasNext()).thenReturn(true);
    when(localStorageIterator.next()).thenReturn(mock(LocalMessage.class));

    commandLineArguments.maxMessages = 50;

    importer.importMail();

    verify(localStorageIterator, times(50)).next();
    verify(gmailSyncer).init();
    verify(gmailSyncer).sync(messageListCaptor.capture());

    assertThat(messageListCaptor.getAllValues().stream().map(List::size).reduce(0, (a, b) -> a + b))
        .isEqualTo(50);
  }

  @Test
  public void testImportMail_noMaxMessagesRespected() throws Exception {
    List<LocalMessage> localMessageList = Lists.newArrayList();
    for (int i = 0; i < 100; i++) {
      localMessageList.add(mock(LocalMessage.class));
    }

    when(localStorage.iterator())
        .thenAnswer(
            new Answer<Iterator<LocalMessage>>() {
              @Override
              public Iterator<LocalMessage> answer(InvocationOnMock invocation) throws Throwable {
                Iterator<LocalMessage> iterator = localMessageList.iterator();
                return iterator;
              }
            });

    importer.importMail();

    verify(gmailSyncer).init();
    verify(gmailSyncer).sync(messageListCaptor.capture());

    assertThat(messageListCaptor.getAllValues().stream().map(List::size).reduce(0, (a, b) -> a + b))
        .isEqualTo(100);
  }
}
