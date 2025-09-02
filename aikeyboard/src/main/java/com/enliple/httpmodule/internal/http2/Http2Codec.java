/*
 * Copyright (C) 2012 The Android Open Source Project
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
package com.enliple.httpmodule.internal.http2;

import com.enliple.httpmodule.Buffer;
import com.enliple.httpmodule.ByteString;
import com.enliple.httpmodule.ForwardingSource;
import com.enliple.httpmodule.Headers;
import com.enliple.httpmodule.Interceptor;
import com.enliple.httpmodule.MobonOkHttpClient;
import com.enliple.httpmodule.MobonOkio;
import com.enliple.httpmodule.MobonRequest;
import com.enliple.httpmodule.MobonResponse;
import com.enliple.httpmodule.Protocol;
import com.enliple.httpmodule.ResponseBody;
import com.enliple.httpmodule.Sink;
import com.enliple.httpmodule.Source;
import com.enliple.httpmodule.internal.Internal;
import com.enliple.httpmodule.internal.Util;
import com.enliple.httpmodule.internal.connection.StreamAllocation;
import com.enliple.httpmodule.internal.http.HttpCodec;
import com.enliple.httpmodule.internal.http.HttpHeaders;
import com.enliple.httpmodule.internal.http.RealResponseBody;
import com.enliple.httpmodule.internal.http.RequestLine;
import com.enliple.httpmodule.internal.http.StatusLine;

import java.io.IOException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.enliple.httpmodule.internal.http.StatusLine.HTTP_CONTINUE;
import static com.enliple.httpmodule.internal.http2.Header.RESPONSE_STATUS;
import static com.enliple.httpmodule.internal.http2.Header.TARGET_AUTHORITY;
import static com.enliple.httpmodule.internal.http2.Header.TARGET_METHOD;
import static com.enliple.httpmodule.internal.http2.Header.TARGET_PATH;
import static com.enliple.httpmodule.internal.http2.Header.TARGET_SCHEME;

/** Encode requests and responses using HTTP/2 frames. */
public final class Http2Codec implements HttpCodec {
  private static final ByteString CONNECTION = ByteString.encodeUtf8("connection");
  private static final ByteString HOST = ByteString.encodeUtf8("host");
  private static final ByteString KEEP_ALIVE = ByteString.encodeUtf8("keep-alive");
  private static final ByteString PROXY_CONNECTION = ByteString.encodeUtf8("proxy-connection");
  private static final ByteString TRANSFER_ENCODING = ByteString.encodeUtf8("transfer-encoding");
  private static final ByteString TE = ByteString.encodeUtf8("te");
  private static final ByteString ENCODING = ByteString.encodeUtf8("encoding");
  private static final ByteString UPGRADE = ByteString.encodeUtf8("upgrade");

  /** See http://tools.ietf.org/html/draft-ietf-httpbis-http2-09#section-8.1.3. */
  private static final List<ByteString> HTTP_2_SKIPPED_REQUEST_HEADERS = Util.immutableList(
      CONNECTION,
      HOST,
      KEEP_ALIVE,
      PROXY_CONNECTION,
      TE,
      TRANSFER_ENCODING,
      ENCODING,
      UPGRADE,
      TARGET_METHOD,
      TARGET_PATH,
      TARGET_SCHEME,
      TARGET_AUTHORITY);
  private static final List<ByteString> HTTP_2_SKIPPED_RESPONSE_HEADERS = Util.immutableList(
      CONNECTION,
      HOST,
      KEEP_ALIVE,
      PROXY_CONNECTION,
      TE,
      TRANSFER_ENCODING,
      ENCODING,
      UPGRADE);

  private final Interceptor.Chain chain;
  final StreamAllocation streamAllocation;
  private final Http2Connection connection;
  private Http2Stream stream;
  private final Protocol protocol;

  public Http2Codec(MobonOkHttpClient client, Interceptor.Chain chain, StreamAllocation streamAllocation,
                    Http2Connection connection) {
    this.chain = chain;
    this.streamAllocation = streamAllocation;
    this.connection = connection;
    this.protocol = client.protocols().contains(Protocol.H2_PRIOR_KNOWLEDGE)
        ? Protocol.H2_PRIOR_KNOWLEDGE
        : Protocol.HTTP_2;
  }

  @Override
  public Sink createRequestBody(MobonRequest mobonRequest, long contentLength) {
    return stream.getSink();
  }

  @Override
  public void writeRequestHeaders(MobonRequest mobonRequest) throws IOException {
    if (stream != null) return;

    boolean hasRequestBody = mobonRequest.body() != null;
    List<Header> requestHeaders = http2HeadersList(mobonRequest);
    stream = connection.newStream(requestHeaders, hasRequestBody);
    stream.readTimeout().timeout(chain.readTimeoutMillis(), TimeUnit.MILLISECONDS);
    stream.writeTimeout().timeout(chain.writeTimeoutMillis(), TimeUnit.MILLISECONDS);
  }

  @Override
  public void flushRequest() throws IOException {
    connection.flush();
  }

  @Override
  public void finishRequest() throws IOException {
    stream.getSink().close();
  }

  @Override
  public MobonResponse.Builder readResponseHeaders(boolean expectContinue) throws IOException {
    List<Header> headers = stream.takeResponseHeaders();
    MobonResponse.Builder responseBuilder = readHttp2HeadersList(headers, protocol);
    if (expectContinue && Internal.instance.code(responseBuilder) == HTTP_CONTINUE) {
      return null;
    }
    return responseBuilder;
  }

  public static List<Header> http2HeadersList(MobonRequest mobonRequest) {
    Headers headers = mobonRequest.headers();
    List<Header> result = new ArrayList<>(headers.size() + 4);
    result.add(new Header(TARGET_METHOD, mobonRequest.method()));
    result.add(new Header(TARGET_PATH, RequestLine.requestPath(mobonRequest.url())));
    String host = mobonRequest.header("Host");
    if (host != null) {
      result.add(new Header(TARGET_AUTHORITY, host)); // Optional.
    }
    result.add(new Header(TARGET_SCHEME, mobonRequest.url().scheme()));

    for (int i = 0, size = headers.size(); i < size; i++) {
      // header names must be lowercase.
      ByteString name = ByteString.encodeUtf8(headers.name(i).toLowerCase(Locale.US));
      if (!HTTP_2_SKIPPED_REQUEST_HEADERS.contains(name)) {
        result.add(new Header(name, headers.value(i)));
      }
    }
    return result;
  }

  /** Returns headers for a name value block containing an HTTP/2 response. */
  public static MobonResponse.Builder readHttp2HeadersList(List<Header> headerBlock,
                                                           Protocol protocol) throws IOException {
    StatusLine statusLine = null;
    Headers.Builder headersBuilder = new Headers.Builder();
    for (int i = 0, size = headerBlock.size(); i < size; i++) {
      Header header = headerBlock.get(i);

      // If there were multiple header blocks they will be delimited by nulls. Discard existing
      // header blocks if the existing header block is a '100 Continue' intermediate response.
      if (header == null) {
        if (statusLine != null && statusLine.code == HTTP_CONTINUE) {
          statusLine = null;
          headersBuilder = new Headers.Builder();
        }
        continue;
      }

      ByteString name = header.name;
      String value = header.value.utf8();
      if (name.equals(RESPONSE_STATUS)) {
        statusLine = StatusLine.parse("HTTP/1.1 " + value);
      } else if (!HTTP_2_SKIPPED_RESPONSE_HEADERS.contains(name)) {
        Internal.instance.addLenient(headersBuilder, name.utf8(), value);
      }
    }
    if (statusLine == null) throw new ProtocolException("Expected ':status' header not present");

    return new MobonResponse.Builder()
        .protocol(protocol)
        .code(statusLine.code)
        .message(statusLine.message)
        .headers(headersBuilder.build());
  }

  @Override
  public ResponseBody openResponseBody(MobonResponse mobonResponse) throws IOException {
    streamAllocation.eventListener.responseBodyStart(streamAllocation.call);
    String contentType = mobonResponse.header("Content-Type");
    long contentLength = HttpHeaders.contentLength(mobonResponse);
    Source source = new StreamFinishingSource(stream.getSource());
    return new RealResponseBody(contentType, contentLength, MobonOkio.buffer(source));
  }

  @Override
  public void cancel() {
    if (stream != null) stream.closeLater(ErrorCode.CANCEL);
  }

  class StreamFinishingSource extends ForwardingSource {
    boolean completed = false;
    long bytesRead = 0;

    StreamFinishingSource(Source delegate) {
      super(delegate);
    }

    @Override
    public long read(Buffer sink, long byteCount) throws IOException {
      try {
        long read = delegate().read(sink, byteCount);
        if (read > 0) {
          bytesRead += read;
        }
        return read;
      } catch (IOException e) {
        endOfInput(e);
        throw e;
      }
    }

    @Override
    public void close() throws IOException {
      super.close();
      endOfInput(null);
    }

    private void endOfInput(IOException e) {
      if (completed) return;
      completed = true;
      streamAllocation.streamFinished(false, Http2Codec.this, bytesRead, e);
    }
  }
}
