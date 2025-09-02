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

import com.enliple.httpmodule.Buffer;
import com.enliple.httpmodule.BufferedSink;
import com.enliple.httpmodule.ForwardingSink;
import com.enliple.httpmodule.Interceptor;
import com.enliple.httpmodule.MobonOkio;
import com.enliple.httpmodule.MobonRequest;
import com.enliple.httpmodule.MobonResponse;
import com.enliple.httpmodule.Sink;
import com.enliple.httpmodule.internal.Util;
import com.enliple.httpmodule.internal.connection.RealConnection;
import com.enliple.httpmodule.internal.connection.StreamAllocation;

import java.io.IOException;
import java.net.ProtocolException;

/** This is the last interceptor in the chain. It makes a network call to the server. */
public final class CallServerInterceptor implements Interceptor {
  private final boolean forWebSocket;

  public CallServerInterceptor(boolean forWebSocket) {
    this.forWebSocket = forWebSocket;
  }

  @Override
  public MobonResponse intercept(Chain chain) throws IOException {
    RealInterceptorChain realChain = (RealInterceptorChain) chain;
    HttpCodec httpCodec = realChain.httpStream();
    StreamAllocation streamAllocation = realChain.streamAllocation();
    RealConnection connection = (RealConnection) realChain.connection();
    MobonRequest mobonRequest = realChain.request();

    long sentRequestMillis = System.currentTimeMillis();

    realChain.eventListener().requestHeadersStart(realChain.call());
    httpCodec.writeRequestHeaders(mobonRequest);
    realChain.eventListener().requestHeadersEnd(realChain.call(), mobonRequest);

    MobonResponse.Builder responseBuilder = null;
    if (HttpMethod.permitsRequestBody(mobonRequest.method()) && mobonRequest.body() != null) {
      // If there's a "Expect: 100-continue" header on the mobonRequest, wait for a "HTTP/1.1 100
      // Continue" mobonResponse before transmitting the mobonRequest body. If we don't get that, return
      // what we did get (such as a 4xx mobonResponse) without ever transmitting the mobonRequest body.
      if ("100-continue".equalsIgnoreCase(mobonRequest.header("Expect"))) {
        httpCodec.flushRequest();
        realChain.eventListener().responseHeadersStart(realChain.call());
        responseBuilder = httpCodec.readResponseHeaders(true);
      }

      if (responseBuilder == null) {
        // Write the mobonRequest body if the "Expect: 100-continue" expectation was met.
        realChain.eventListener().requestBodyStart(realChain.call());
        long contentLength = mobonRequest.body().contentLength();
        CountingSink requestBodyOut =
            new CountingSink(httpCodec.createRequestBody(mobonRequest, contentLength));
        BufferedSink bufferedRequestBody = MobonOkio.buffer(requestBodyOut);

        mobonRequest.body().writeTo(bufferedRequestBody);
        bufferedRequestBody.close();
        realChain.eventListener()
            .requestBodyEnd(realChain.call(), requestBodyOut.successfulCount);
      } else if (!connection.isMultiplexed()) {
        // If the "Expect: 100-continue" expectation wasn't met, prevent the HTTP/1 connection
        // from being reused. Otherwise we're still obligated to transmit the mobonRequest body to
        // leave the connection in a consistent state.
        streamAllocation.noNewStreams();
      }
    }

    httpCodec.finishRequest();

    if (responseBuilder == null) {
      realChain.eventListener().responseHeadersStart(realChain.call());
      responseBuilder = httpCodec.readResponseHeaders(false);
    }

    MobonResponse mobonResponse = responseBuilder
        .request(mobonRequest)
        .handshake(streamAllocation.connection().handshake())
        .sentRequestAtMillis(sentRequestMillis)
        .receivedResponseAtMillis(System.currentTimeMillis())
        .build();

    int code = mobonResponse.code();
    if (code == 100) {
      // server sent a 100-continue even though we did not mobonRequest one.
      // try again to read the actual mobonResponse
      responseBuilder = httpCodec.readResponseHeaders(false);

      mobonResponse = responseBuilder
              .request(mobonRequest)
              .handshake(streamAllocation.connection().handshake())
              .sentRequestAtMillis(sentRequestMillis)
              .receivedResponseAtMillis(System.currentTimeMillis())
              .build();

      code = mobonResponse.code();
    }

    realChain.eventListener()
            .responseHeadersEnd(realChain.call(), mobonResponse);

    if (forWebSocket && code == 101) {
      // Connection is upgrading, but we need to ensure interceptors see a non-null mobonResponse body.
      mobonResponse = mobonResponse.newBuilder()
          .body(Util.EMPTY_RESPONSE)
          .build();
    } else {
      mobonResponse = mobonResponse.newBuilder()
          .body(httpCodec.openResponseBody(mobonResponse))
          .build();
    }

    if ("close".equalsIgnoreCase(mobonResponse.request().header("Connection"))
        || "close".equalsIgnoreCase(mobonResponse.header("Connection"))) {
      streamAllocation.noNewStreams();
    }

    if ((code == 204 || code == 205) && mobonResponse.body().contentLength() > 0) {
      throw new ProtocolException(
          "HTTP " + code + " had non-zero Content-Length: " + mobonResponse.body().contentLength());
    }

    return mobonResponse;
  }

  static final class CountingSink extends ForwardingSink {
    long successfulCount;

    CountingSink(Sink delegate) {
      super(delegate);
    }

    @Override
    public void write(Buffer source, long byteCount) throws IOException {
      super.write(source, byteCount);
      successfulCount += byteCount;
    }
  }
}
