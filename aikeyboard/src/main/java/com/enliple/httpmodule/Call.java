/*
 * Copyright (C) 2014 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.enliple.httpmodule;

import android.content.Context;

import java.io.IOException;

/**
 * A call is a mobonRequest that has been prepared for execution. A call can be canceled. As this object
 * represents a single mobonRequest/response pair (stream), it cannot be executed twice.
 */
public interface Call extends Cloneable {
  /** Returns the original mobonRequest that initiated this call. */
  MobonRequest request();

  /**
   * Invokes the mobonRequest immediately, and blocks until the response can be processed or is in
   * error.
   *
   * <p>To avoid leaking resources callers should close the {@link MobonResponse} which in turn will
   * close the underlying {@link ResponseBody}.
   *
   * <pre>{@code
   *
   *   // ensure the response (and underlying response body) is closed
   *   try (MobonResponse response = client.newCall(mobonRequest).execute()) {
   *     ...
   *   }
   *
   * }</pre>
   *
   * <p>The caller may read the response body with the response's {@link MobonResponse#body} method. To
   * avoid leaking resources callers must {@linkplain ResponseBody close the response body} or the
   * MobonResponse.
   *
   * <p>Note that transport-layer success (receiving a HTTP response code, headers and body) does
   * not necessarily indicate application-layer success: {@code response} may still indicate an
   * unhappy HTTP response code like 404 or 500.
   *
   * @throws IOException if the mobonRequest could not be executed due to cancellation, a connectivity
   * problem or timeout. Because networks can fail during an exchange, it is possible that the
   * remote server accepted the mobonRequest before the failure.
   * @throws IllegalStateException when the call has already been executed.
   */
  MobonResponse execute() throws IOException;

  /**
   * Schedules the mobonRequest to be executed at some point in the future.
   *
   * <p>The {@link MobonOkHttpClient#dispatcher dispatcher} defines when the mobonRequest will run: usually
   * immediately unless there are several other requests currently being executed.
   *
   * <p>This client will later call back {@code responseCallback} with either an HTTP response or a
   * failure exception.
   *
   * @throws IllegalStateException when the call has already been executed.
   */
  void enqueue(Callback responseCallback);

  /** Cancels the mobonRequest, if possible. Requests that are already complete cannot be canceled. */
  void cancel();

  /**
   * Returns true if this call has been either {@linkplain #execute() executed} or {@linkplain
   * #enqueue(Callback) enqueued}. It is an error to execute a call more than once.
   */
  boolean isExecuted();

  boolean isCanceled();

  /**
   * Create a new, identical call to this one which can be enqueued or executed even if this call
   * has already been.
   */
  Call clone();

  interface Factory {
    Call newCall(Context context, MobonRequest mobonRequest);
    Call newCall(MobonRequest mobonRequest);
  }
}
