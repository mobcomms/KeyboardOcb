/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.enliple.httpmodule.internal.http;

import com.enliple.httpmodule.Cookie;
import com.enliple.httpmodule.CookieJar;
import com.enliple.httpmodule.GzipSource;
import com.enliple.httpmodule.Headers;
import com.enliple.httpmodule.Interceptor;
import com.enliple.httpmodule.MediaType;
import com.enliple.httpmodule.MobonOkio;
import com.enliple.httpmodule.MobonRequest;
import com.enliple.httpmodule.MobonResponse;
import com.enliple.httpmodule.RequestBody;
import com.enliple.httpmodule.internal.Version;

import java.io.IOException;
import java.util.List;

import static com.enliple.httpmodule.internal.Util.hostHeader;

/**
 * Bridges from application code to network code. First it builds a network request from a user
 * request. Then it proceeds to call the network. Finally it builds a user response from the network
 * response.
 */
public final class BridgeInterceptor implements Interceptor {
  private final CookieJar cookieJar;

  public BridgeInterceptor(CookieJar cookieJar) {
    this.cookieJar = cookieJar;
  }

  @Override
  public MobonResponse intercept(Chain chain) throws IOException {
    MobonRequest userMobonRequest = chain.request();
    MobonRequest.Builder requestBuilder = userMobonRequest.newBuilder();

    RequestBody body = userMobonRequest.body();
    if (body != null) {
      MediaType contentType = body.contentType();
      if (contentType != null) {
        requestBuilder.header("Content-Type", contentType.toString());
      }

      long contentLength = body.contentLength();
      if (contentLength != -1) {
        requestBuilder.header("Content-Length", Long.toString(contentLength));
        requestBuilder.removeHeader("Transfer-Encoding");
      } else {
        requestBuilder.header("Transfer-Encoding", "chunked");
        requestBuilder.removeHeader("Content-Length");
      }
    }

    if (userMobonRequest.header("Host") == null) {
      requestBuilder.header("Host", hostHeader(userMobonRequest.url(), false));
    }

    if (userMobonRequest.header("Connection") == null) {
      requestBuilder.header("Connection", "Keep-Alive");
    }

    // If we add an "Accept-Encoding: gzip" header field we're responsible for also decompressing
    // the transfer stream.
    boolean transparentGzip = false;
    if (userMobonRequest.header("Accept-Encoding") == null && userMobonRequest.header("Range") == null) {
      transparentGzip = true;
      requestBuilder.header("Accept-Encoding", "gzip");
    }

    List<Cookie> cookies = cookieJar.loadForRequest(userMobonRequest.url());
    if (!cookies.isEmpty()) {
      requestBuilder.header("Cookie", cookieHeader(cookies));
    }

    if (userMobonRequest.header("User-Agent") == null) {
      requestBuilder.header("User-Agent", Version.userAgent());
    }

    MobonResponse networkMobonResponse = chain.proceed(requestBuilder.build());

    HttpHeaders.receiveHeaders(cookieJar, userMobonRequest.url(), networkMobonResponse.headers());

    MobonResponse.Builder responseBuilder = networkMobonResponse.newBuilder()
        .request(userMobonRequest);

    if (transparentGzip
        && "gzip".equalsIgnoreCase(networkMobonResponse.header("Content-Encoding"))
        && HttpHeaders.hasBody(networkMobonResponse)) {
      GzipSource responseBody = new GzipSource(networkMobonResponse.body().source());
      Headers strippedHeaders = networkMobonResponse.headers().newBuilder()
          .removeAll("Content-Encoding")
          .removeAll("Content-Length")
          .build();
      responseBuilder.headers(strippedHeaders);
      String contentType = networkMobonResponse.header("Content-Type");
      responseBuilder.body(new RealResponseBody(contentType, -1L, MobonOkio.buffer(responseBody)));
    }

    return responseBuilder.build();
  }

  /** Returns a 'Cookie' HTTP request header with all cookies, like {@code a=b; c=d}. */
  private String cookieHeader(List<Cookie> cookies) {
    StringBuilder cookieHeader = new StringBuilder();
    for (int i = 0, size = cookies.size(); i < size; i++) {
      if (i > 0) {
        cookieHeader.append("; ");
      }
      Cookie cookie = cookies.get(i);
      cookieHeader.append(cookie.name()).append('=').append(cookie.value());
    }
    return cookieHeader.toString();
  }
}
