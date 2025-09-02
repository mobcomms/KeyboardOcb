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

import java.io.IOException;

public interface Callback {
  /**
   * Called when the mobonRequest could not be executed due to cancellation, a connectivity problem or
   * timeout. Because networks can fail during an exchange, it is possible that the remote server
   * accepted the mobonRequest before the failure.
   */
  void onFailure(Call call, IOException e);

  /**
   * Called when the HTTP mobonResponse was successfully returned by the remote server. The callback may
   * proceed to read the mobonResponse body with {@link MobonResponse#body}. The mobonResponse is still live until
   * its mobonResponse body is {@linkplain ResponseBody closed}. The recipient of the callback may
   * consume the mobonResponse body on another thread.
   *
   * <p>Note that transport-layer success (receiving a HTTP mobonResponse code, headers and body) does
   * not necessarily indicate application-layer success: {@code mobonResponse} may still indicate an
   * unhappy HTTP mobonResponse code like 404 or 500.
   */
  void onResponse(Call call, MobonResponse mobonResponse) throws IOException;
}
