/*
 * Copyright (C) 2016 Square, Inc.
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
package com.enliple.httpmodule.internal.http;

import com.enliple.httpmodule.Call;
import com.enliple.httpmodule.Connection;
import com.enliple.httpmodule.EventListener;
import com.enliple.httpmodule.Interceptor;
import com.enliple.httpmodule.MobonRequest;
import com.enliple.httpmodule.MobonResponse;
import com.enliple.httpmodule.internal.connection.RealConnection;
import com.enliple.httpmodule.internal.connection.StreamAllocation;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.enliple.httpmodule.internal.Util.checkDuration;

/**
 * A concrete interceptor chain that carries the entire interceptor chain: all application
 * interceptors, the OkHttp core, all network interceptors, and finally the network caller.
 */
public final class RealInterceptorChain implements Interceptor.Chain {
  private final List<Interceptor> interceptors;
  private final StreamAllocation streamAllocation;
  private final HttpCodec httpCodec;
  private final RealConnection connection;
  private final int index;
  private final MobonRequest mobonRequest;
  private final Call call;
  private final EventListener eventListener;
  private final int connectTimeout;
  private final int readTimeout;
  private final int writeTimeout;
  private int calls;

  public RealInterceptorChain(List<Interceptor> interceptors, StreamAllocation streamAllocation,
                              HttpCodec httpCodec, RealConnection connection, int index, MobonRequest mobonRequest, Call call,
                              EventListener eventListener, int connectTimeout, int readTimeout, int writeTimeout) {
    this.interceptors = interceptors;
    this.connection = connection;
    this.streamAllocation = streamAllocation;
    this.httpCodec = httpCodec;
    this.index = index;
    this.mobonRequest = mobonRequest;
    this.call = call;
    this.eventListener = eventListener;
    this.connectTimeout = connectTimeout;
    this.readTimeout = readTimeout;
    this.writeTimeout = writeTimeout;
  }

  @Override
  public Connection connection() {
    return connection;
  }

  @Override
  public int connectTimeoutMillis() {
    return connectTimeout;
  }

  @Override
  public Interceptor.Chain withConnectTimeout(int timeout, TimeUnit unit) {
    int millis = checkDuration("timeout", timeout, unit);
    return new RealInterceptorChain(interceptors, streamAllocation, httpCodec, connection, index,
            mobonRequest, call, eventListener, millis, readTimeout, writeTimeout);
  }

  @Override
  public int readTimeoutMillis() {
    return readTimeout;
  }

  @Override
  public Interceptor.Chain withReadTimeout(int timeout, TimeUnit unit) {
    int millis = checkDuration("timeout", timeout, unit);
    return new RealInterceptorChain(interceptors, streamAllocation, httpCodec, connection, index,
            mobonRequest, call, eventListener, connectTimeout, millis, writeTimeout);
  }

  @Override
  public int writeTimeoutMillis() {
    return writeTimeout;
  }

  @Override
  public Interceptor.Chain withWriteTimeout(int timeout, TimeUnit unit) {
    int millis = checkDuration("timeout", timeout, unit);
    return new RealInterceptorChain(interceptors, streamAllocation, httpCodec, connection, index,
            mobonRequest, call, eventListener, connectTimeout, readTimeout, millis);
  }

  public StreamAllocation streamAllocation() {
    return streamAllocation;
  }

  public HttpCodec httpStream() {
    return httpCodec;
  }

  @Override
  public Call call() {
    return call;
  }

  public EventListener eventListener() {
    return eventListener;
  }

  @Override
  public MobonRequest request() {
    return mobonRequest;
  }

  @Override
  public MobonResponse proceed(MobonRequest mobonRequest) throws IOException {
    return proceed(mobonRequest, streamAllocation, httpCodec, connection);
  }

  public MobonResponse proceed(MobonRequest mobonRequest, StreamAllocation streamAllocation, HttpCodec httpCodec,
                               RealConnection connection) throws IOException {
    if (index >= interceptors.size()) throw new AssertionError();

    calls++;

    // If we already have a stream, confirm that the incoming mobonRequest will use it.
    if (this.httpCodec != null && !this.connection.supportsUrl(mobonRequest.url())) {
      throw new IllegalStateException("network interceptor " + interceptors.get(index - 1)
          + " must retain the same host and port");
    }

    // If we already have a stream, confirm that this is the only call to chain.proceed().
    if (this.httpCodec != null && calls > 1) {
      throw new IllegalStateException("network interceptor " + interceptors.get(index - 1)
          + " must call proceed() exactly once");
    }

    // Call the next interceptor in the chain.
    RealInterceptorChain next = new RealInterceptorChain(interceptors, streamAllocation, httpCodec,
        connection, index + 1, mobonRequest, call, eventListener, connectTimeout, readTimeout,
        writeTimeout);
    Interceptor interceptor = interceptors.get(index);
    MobonResponse mobonResponse = interceptor.intercept(next);

    // Confirm that the next interceptor made its required call to chain.proceed().
    if (httpCodec != null && index + 1 < interceptors.size() && next.calls != 1) {
      throw new IllegalStateException("network interceptor " + interceptor
          + " must call proceed() exactly once");
    }

    // Confirm that the intercepted mobonResponse isn't null.
    if (mobonResponse == null) {
      throw new NullPointerException("interceptor " + interceptor + " returned null");
    }

    if (mobonResponse.body() == null) {
      throw new IllegalStateException(
          "interceptor " + interceptor + " returned a mobonResponse with no body");
    }

    return mobonResponse;
  }
}
