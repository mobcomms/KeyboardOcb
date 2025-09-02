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
package com.enliple.httpmodule.internal.cache;

import com.enliple.httpmodule.Buffer;
import com.enliple.httpmodule.BufferedSink;
import com.enliple.httpmodule.BufferedSource;
import com.enliple.httpmodule.Headers;
import com.enliple.httpmodule.Interceptor;
import com.enliple.httpmodule.MobonOkio;
import com.enliple.httpmodule.MobonRequest;
import com.enliple.httpmodule.MobonResponse;
import com.enliple.httpmodule.Protocol;
import com.enliple.httpmodule.Sink;
import com.enliple.httpmodule.Source;
import com.enliple.httpmodule.Timeout;
import com.enliple.httpmodule.internal.Internal;
import com.enliple.httpmodule.internal.Util;
import com.enliple.httpmodule.internal.http.HttpCodec;
import com.enliple.httpmodule.internal.http.HttpHeaders;
import com.enliple.httpmodule.internal.http.HttpMethod;
import com.enliple.httpmodule.internal.http.RealResponseBody;

import java.io.IOException;

import static com.enliple.httpmodule.internal.Util.closeQuietly;
import static com.enliple.httpmodule.internal.Util.discard;
import static java.net.HttpURLConnection.HTTP_NOT_MODIFIED;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/** Serves requests from the cache and writes responses to the cache. */
public final class CacheInterceptor implements Interceptor {
  final InternalCache cache;

  public CacheInterceptor(InternalCache cache) {
    this.cache = cache;
  }

  @Override
  public MobonResponse intercept(Chain chain) throws IOException {
    MobonResponse cacheCandidate = cache != null
        ? cache.get(chain.request())
        : null;

    long now = System.currentTimeMillis();

    CacheStrategy strategy = new CacheStrategy.Factory(now, chain.request(), cacheCandidate).get();
    MobonRequest networkMobonRequest = strategy.networkMobonRequest;
    MobonResponse cacheMobonResponse = strategy.cacheMobonResponse;

    if (cache != null) {
      cache.trackResponse(strategy);
    }

    if (cacheCandidate != null && cacheMobonResponse == null) {
      closeQuietly(cacheCandidate.body()); // The cache candidate wasn't applicable. Close it.
    }

    // If we're forbidden from using the network and the cache is insufficient, fail.
    if (networkMobonRequest == null && cacheMobonResponse == null) {
      return new MobonResponse.Builder()
          .request(chain.request())
          .protocol(Protocol.HTTP_1_1)
          .code(504)
          .message("Unsatisfiable MobonRequest (only-if-cached)")
          .body(Util.EMPTY_RESPONSE)
          .sentRequestAtMillis(-1L)
          .receivedResponseAtMillis(System.currentTimeMillis())
          .build();
    }

    // If we don't need the network, we're done.
    if (networkMobonRequest == null) {
      return cacheMobonResponse.newBuilder()
          .cacheResponse(stripBody(cacheMobonResponse))
          .build();
    }

    MobonResponse networkMobonResponse = null;
    try {
      networkMobonResponse = chain.proceed(networkMobonRequest);
    } finally {
      // If we're crashing on I/O or otherwise, don't leak the cache body.
      if (networkMobonResponse == null && cacheCandidate != null) {
        closeQuietly(cacheCandidate.body());
      }
    }

    // If we have a cache mobonResponse too, then we're doing a conditional get.
    if (cacheMobonResponse != null) {
      if (networkMobonResponse.code() == HTTP_NOT_MODIFIED) {
        MobonResponse mobonResponse = cacheMobonResponse.newBuilder()
            .headers(combine(cacheMobonResponse.headers(), networkMobonResponse.headers()))
            .sentRequestAtMillis(networkMobonResponse.sentRequestAtMillis())
            .receivedResponseAtMillis(networkMobonResponse.receivedResponseAtMillis())
            .cacheResponse(stripBody(cacheMobonResponse))
            .networkResponse(stripBody(networkMobonResponse))
            .build();
        networkMobonResponse.body().close();

        // Update the cache after combining headers but before stripping the
        // Content-Encoding header (as performed by initContentStream()).
        cache.trackConditionalCacheHit();
        cache.update(cacheMobonResponse, mobonResponse);
        return mobonResponse;
      } else {
        closeQuietly(cacheMobonResponse.body());
      }
    }

    MobonResponse mobonResponse = networkMobonResponse.newBuilder()
        .cacheResponse(stripBody(cacheMobonResponse))
        .networkResponse(stripBody(networkMobonResponse))
        .build();

    if (cache != null) {
      if (HttpHeaders.hasBody(mobonResponse) && CacheStrategy.isCacheable(mobonResponse, networkMobonRequest)) {
        // Offer this mobonRequest to the cache.
        CacheRequest cacheRequest = cache.put(mobonResponse);
        return cacheWritingResponse(cacheRequest, mobonResponse);
      }

      if (HttpMethod.invalidatesCache(networkMobonRequest.method())) {
        try {
          cache.remove(networkMobonRequest);
        } catch (IOException ignored) {
          // The cache cannot be written.
        }
      }
    }

    return mobonResponse;
  }

  private static MobonResponse stripBody(MobonResponse mobonResponse) {
    return mobonResponse != null && mobonResponse.body() != null
        ? mobonResponse.newBuilder().body(null).build()
        : mobonResponse;
  }

  /**
   * Returns a new source that writes bytes to {@code cacheRequest} as they are read by the source
   * consumer. This is careful to discard bytes left over when the stream is closed; otherwise we
   * may never exhaust the source stream and therefore not complete the cached mobonResponse.
   */
  private MobonResponse cacheWritingResponse(final CacheRequest cacheRequest, MobonResponse mobonResponse)
      throws IOException {
    // Some apps return a null body; for compatibility we treat that like a null cache mobonRequest.
    if (cacheRequest == null) return mobonResponse;
    Sink cacheBodyUnbuffered = cacheRequest.body();
    if (cacheBodyUnbuffered == null) return mobonResponse;

    final BufferedSource source = mobonResponse.body().source();
    final BufferedSink cacheBody = MobonOkio.buffer(cacheBodyUnbuffered);

    Source cacheWritingSource = new Source() {
      boolean cacheRequestClosed;

      @Override
      public long read(Buffer sink, long byteCount) throws IOException {
        long bytesRead;
        try {
          bytesRead = source.read(sink, byteCount);
        } catch (IOException e) {
          if (!cacheRequestClosed) {
            cacheRequestClosed = true;
            cacheRequest.abort(); // Failed to write a complete cache mobonResponse.
          }
          throw e;
        }

        if (bytesRead == -1) {
          if (!cacheRequestClosed) {
            cacheRequestClosed = true;
            cacheBody.close(); // The cache mobonResponse is complete!
          }
          return -1;
        }

        sink.copyTo(cacheBody.buffer(), sink.size() - bytesRead, bytesRead);
        cacheBody.emitCompleteSegments();
        return bytesRead;
      }

      @Override
      public Timeout timeout() {
        return source.timeout();
      }

      @Override
      public void close() throws IOException {
        if (!cacheRequestClosed
            && !discard(this, HttpCodec.DISCARD_STREAM_TIMEOUT_MILLIS, MILLISECONDS)) {
          cacheRequestClosed = true;
          cacheRequest.abort();
        }
        source.close();
      }
    };

    String contentType = mobonResponse.header("Content-Type");
    long contentLength = mobonResponse.body().contentLength();
    return mobonResponse.newBuilder()
        .body(new RealResponseBody(contentType, contentLength, MobonOkio.buffer(cacheWritingSource)))
        .build();
  }

  /** Combines cached headers with a network headers as defined by RFC 7234, 4.3.4. */
  private static Headers combine(Headers cachedHeaders, Headers networkHeaders) {
    Headers.Builder result = new Headers.Builder();

    for (int i = 0, size = cachedHeaders.size(); i < size; i++) {
      String fieldName = cachedHeaders.name(i);
      String value = cachedHeaders.value(i);
      if ("Warning".equalsIgnoreCase(fieldName) && value.startsWith("1")) {
        continue; // Drop 100-level freshness warnings.
      }
      if (isContentSpecificHeader(fieldName) || !isEndToEnd(fieldName)
              || networkHeaders.get(fieldName) == null) {
        Internal.instance.addLenient(result, fieldName, value);
      }
    }

    for (int i = 0, size = networkHeaders.size(); i < size; i++) {
      String fieldName = networkHeaders.name(i);
      if (!isContentSpecificHeader(fieldName) && isEndToEnd(fieldName)) {
        Internal.instance.addLenient(result, fieldName, networkHeaders.value(i));
      }
    }

    return result.build();
  }

  /**
   * Returns true if {@code fieldName} is an end-to-end HTTP header, as defined by RFC 2616,
   * 13.5.1.
   */
  static boolean isEndToEnd(String fieldName) {
    return !"Connection".equalsIgnoreCase(fieldName)
        && !"Keep-Alive".equalsIgnoreCase(fieldName)
        && !"Proxy-Authenticate".equalsIgnoreCase(fieldName)
        && !"Proxy-Authorization".equalsIgnoreCase(fieldName)
        && !"TE".equalsIgnoreCase(fieldName)
        && !"Trailers".equalsIgnoreCase(fieldName)
        && !"Transfer-Encoding".equalsIgnoreCase(fieldName)
        && !"Upgrade".equalsIgnoreCase(fieldName);
  }

  /**
   * Returns true if {@code fieldName} is content specific and therefore should always be used
   * from cached headers.
   */
  static boolean isContentSpecificHeader(String fieldName) {
    return "Content-Length".equalsIgnoreCase(fieldName)
        || "Content-Encoding".equalsIgnoreCase(fieldName)
        || "Content-Type".equalsIgnoreCase(fieldName);
  }
}
