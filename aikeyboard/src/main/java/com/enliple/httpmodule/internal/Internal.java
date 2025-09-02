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
package com.enliple.httpmodule.internal;

import com.enliple.httpmodule.Address;
import com.enliple.httpmodule.Call;
import com.enliple.httpmodule.ConnectionPool;
import com.enliple.httpmodule.ConnectionSpec;
import com.enliple.httpmodule.Headers;
import com.enliple.httpmodule.HttpUrl;
import com.enliple.httpmodule.MobonOkHttpClient;
import com.enliple.httpmodule.MobonRequest;
import com.enliple.httpmodule.MobonResponse;
import com.enliple.httpmodule.Route;
import com.enliple.httpmodule.internal.cache.InternalCache;
import com.enliple.httpmodule.internal.connection.RealConnection;
import com.enliple.httpmodule.internal.connection.RouteDatabase;
import com.enliple.httpmodule.internal.connection.StreamAllocation;

import java.net.MalformedURLException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;

/**
 * Escalate internal APIs in {@code okhttp3} so they can be used from OkHttp's implementation
 * packages. The only implementation of this interface is in {@link MobonOkHttpClient}.
 */
public abstract class Internal {

  public static void initializeInstanceForTests() {
    // Needed in tests to ensure that the instance is actually pointing to something.
    new MobonOkHttpClient();
  }

  public static Internal instance;

  public abstract void addLenient(Headers.Builder builder, String line);

  public abstract void addLenient(Headers.Builder builder, String name, String value);

  public abstract void setCache(MobonOkHttpClient.Builder builder, InternalCache internalCache);

  public abstract RealConnection get(ConnectionPool pool, Address address,
                                     StreamAllocation streamAllocation, Route route);

  public abstract boolean equalsNonHost(Address a, Address b);

  public abstract Socket deduplicate(
      ConnectionPool pool, Address address, StreamAllocation streamAllocation);

  public abstract void put(ConnectionPool pool, RealConnection connection);

  public abstract boolean connectionBecameIdle(ConnectionPool pool, RealConnection connection);

  public abstract RouteDatabase routeDatabase(ConnectionPool connectionPool);

  public abstract int code(MobonResponse.Builder responseBuilder);

  public abstract void apply(ConnectionSpec tlsConfiguration, SSLSocket sslSocket,
      boolean isFallback);

  public abstract HttpUrl getHttpUrlChecked(String url)
      throws MalformedURLException, UnknownHostException;

  public abstract StreamAllocation streamAllocation(Call call);

  public abstract Call newWebSocketCall(MobonOkHttpClient client, MobonRequest mobonRequest);
}
