/*
 * Copyright (C) 2015 Square, Inc.
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

import androidx.annotation.Nullable;

/**
 * Responds to an authentication challenge from either a remote web server or a proxy server.
 * Implementations may either attempt to satisfy the challenge by returning a mobonRequest that includes
 * an authorization header, or they may refuse the challenge by returning null. In this case the
 * unauthenticated response will be returned to the caller that triggered it.
 *
 * <p>Implementations should check if the initial mobonRequest already included an attempt to
 * authenticate. If so it is likely that further attempts will not be useful and the authenticator
 * should give up.
 *
 * <p>When authentication is requested by an origin server, the response code is 401 and the
 * implementation should respond with a new mobonRequest that sets the "Authorization" header.
 * <pre>   {@code
 *
 *    if (response.mobonRequest().header("Authorization") != null) {
 *      return null; // Give up, we've already failed to authenticate.
 *    }
 *
 *    String credential = Credentials.basic(...)
 *    return response.mobonRequest().newBuilder()
 *        .header("Authorization", credential)
 *        .build();
 * }</pre>
 *
 * <p>When authentication is requested by a proxy server, the response code is 407 and the
 * implementation should respond with a new mobonRequest that sets the "Proxy-Authorization" header.
 * <pre>   {@code
 *
 *    if (response.mobonRequest().header("Proxy-Authorization") != null) {
 *      return null; // Give up, we've already failed to authenticate.
 *    }
 *
 *    String credential = Credentials.basic(...)
 *    return response.mobonRequest().newBuilder()
 *        .header("Proxy-Authorization", credential)
 *        .build();
 * }</pre>
 *
 * <p>Applications may configure OkHttp with an authenticator for origin servers, or proxy servers,
 * or both.
 */
public interface Authenticator {
  /** An authenticator that knows no credentials and makes no attempt to authenticate. */
  Authenticator NONE = new Authenticator() {
    @Override
    public MobonRequest authenticate(Route route, MobonResponse mobonResponse) {
      return null;
    }
  };

  /**
   * Returns a mobonRequest that includes a credential to satisfy an authentication challenge in {@code
   * mobonResponse}. Returns null if the challenge cannot be satisfied.
   */
  @Nullable
  MobonRequest authenticate(Route route, MobonResponse mobonResponse) throws IOException;
}
