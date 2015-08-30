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

import javax.mail.MessagingException;
import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Wraps a {@link javax.mail.MessagingException}. All methods are delegated to
 * the original exception except {@link #fillInStackTrace()} which is called in
 * the constructor before the original exception is available. As all
 * stack-getting methods are delegated, this doesn't make much difference in
 * practice.
 */
public class RuntimeMessagingException extends RuntimeException {
  private final MessagingException exception;

  public Exception getNextException() {
    return exception.getNextException();
  }

  public boolean setNextException(Exception ex) {
    return exception.setNextException(ex);
  }

  @Override
  public Throwable getCause() {
    return exception.getCause();
  }

  @Override
  public String toString() {
    return exception.toString();
  }

  @Override
  public String getMessage() {
    return exception.getMessage();
  }

  @Override
  public String getLocalizedMessage() {
    return exception.getLocalizedMessage();
  }

  @Override
  public Throwable initCause(Throwable cause) {
    return exception.initCause(cause);
  }

  @Override
  public void printStackTrace() {
    exception.printStackTrace();
  }

  @Override
  public void printStackTrace(PrintStream s) {
    exception.printStackTrace(s);
  }

  @Override
  public void printStackTrace(PrintWriter s) {
    exception.printStackTrace(s);
  }

  @Override
  public StackTraceElement[] getStackTrace() {
    return exception.getStackTrace();
  }

  @Override
  public void setStackTrace(StackTraceElement[] stackTrace) {
    exception.setStackTrace(stackTrace);
  }

  public RuntimeMessagingException(MessagingException exception) {
    super(exception);
    this.exception = exception;
  }
}
