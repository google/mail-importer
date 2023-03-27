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
import static com.google.common.truth.Truth.assertWithMessage;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.base.VerifyException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import to.lean.tools.gmail.importer.local.JavaxMailMessage;

@RunWith(JUnit4.class)
public class XMozillaStatusTest {

  private XMozillaStatusParser xMozillaStatusParser = new XMozillaStatusParser();

  @Test
  public void testNoStatusHeader() throws Exception {
    XMozillaStatus status = statusForHeader();
    assertThat(status).isNotNull();
  }

  @Test
  public void testMultipleStatusHeadersThrowsException() throws Exception {
    try {
      statusForHeader("00000000", "00000000");
      assertWithMessage("Should have thrown an exception").fail();
    } catch (VerifyException e) {
      // OK!
    }
  }

  @Test
  public void testIsRead() throws Exception {
    XMozillaStatus status = statusForHeader("00000001");
    assertThat(status.isRead()).named("isRead").isTrue();
  }

  @Test
  public void testIsMarked() throws Exception {
    XMozillaStatus status = statusForHeader("00000004");
    assertThat(status.isMarked()).named("isMarked").isTrue();
  }

  @Test
  public void testIsMarkedAndRead() throws Exception {
    XMozillaStatus status = statusForHeader("00000005");
    assertThat(status.isRead()).named("isRead").isTrue();
    assertThat(status.isRead()).named("isMarked").isTrue();
  }

  private XMozillaStatus statusForHeader(String... headers) {
    JavaxMailMessage message = mock(JavaxMailMessage.class);
    when(message.getHeader("X-Mozilla-Status")).thenReturn(headers);
    return xMozillaStatusParser.parse(message);
  }
}
