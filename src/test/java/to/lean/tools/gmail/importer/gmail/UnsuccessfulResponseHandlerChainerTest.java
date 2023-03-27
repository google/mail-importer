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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpUnsuccessfulResponseHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.MockitoAnnotations;

@RunWith(JUnit4.class)
public class UnsuccessfulResponseHandlerChainerTest {

  private static final boolean SUPPORTS_RETRY = true;

  private HttpRequest httpRequest = null;
  private HttpResponse httpResponse = null;
  private UnsuccessfulResponseHandlerChainer chainer;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    chainer = new UnsuccessfulResponseHandlerChainer();
  }

  @Test
  public void testChainOfZero() throws Exception {
    HttpUnsuccessfulResponseHandler handler = chainer.chain();

    handler.handleResponse(httpRequest, httpResponse, SUPPORTS_RETRY);
  }

  @Test
  public void testChainOfOne() throws Exception {
    HttpUnsuccessfulResponseHandler handler = mock(HttpUnsuccessfulResponseHandler.class);

    chainer.chain(handler).handleResponse(httpRequest, httpResponse, SUPPORTS_RETRY);

    verify(handler)
        .handleResponse(any(HttpRequest.class), any(HttpResponse.class), any(Boolean.TYPE));
  }

  @Test
  public void testChainOfTwo() throws Exception {
    HttpUnsuccessfulResponseHandler handler1 = mock(HttpUnsuccessfulResponseHandler.class);
    HttpUnsuccessfulResponseHandler handler2 = mock(HttpUnsuccessfulResponseHandler.class);

    chainer.chain(handler1, handler2).handleResponse(httpRequest, httpResponse, SUPPORTS_RETRY);

    verify(handler1)
        .handleResponse(any(HttpRequest.class), any(HttpResponse.class), any(Boolean.TYPE));

    verify(handler2)
        .handleResponse(any(HttpRequest.class), any(HttpResponse.class), any(Boolean.TYPE));
  }

  @Test
  public void testChainOnlyCallsUntilTrue() throws Exception {
    HttpUnsuccessfulResponseHandler handler1 = mock(HttpUnsuccessfulResponseHandler.class);
    HttpUnsuccessfulResponseHandler handler2 = mock(HttpUnsuccessfulResponseHandler.class);

    when(handler1.handleResponse(any(HttpRequest.class), any(HttpResponse.class), anyBoolean()))
        .thenReturn(true);

    chainer.chain(handler1, handler2).handleResponse(httpRequest, httpResponse, SUPPORTS_RETRY);

    verify(handler1)
        .handleResponse(any(HttpRequest.class), any(HttpResponse.class), any(Boolean.TYPE));

    verify(handler2, never())
        .handleResponse(any(HttpRequest.class), any(HttpResponse.class), any(Boolean.TYPE));
  }
}
