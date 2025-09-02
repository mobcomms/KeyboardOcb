/*
 * Copyright (C) 2013 Square, Inc.
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

import com.enliple.httpmodule.internal.http.HttpHeaders;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import androidx.annotation.Nullable;

import static com.enliple.httpmodule.internal.http.StatusLine.HTTP_PERM_REDIRECT;
import static com.enliple.httpmodule.internal.http.StatusLine.HTTP_TEMP_REDIRECT;
import static java.net.HttpURLConnection.HTTP_MOVED_PERM;
import static java.net.HttpURLConnection.HTTP_MOVED_TEMP;
import static java.net.HttpURLConnection.HTTP_MULT_CHOICE;
import static java.net.HttpURLConnection.HTTP_PROXY_AUTH;
import static java.net.HttpURLConnection.HTTP_SEE_OTHER;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

/**
 * An HTTP response. Instances of this class are not immutable: the response body is a one-shot
 * value that may be consumed only once and then closed. All other properties are immutable.
 *
 * <p>This class implements {@link Closeable}. Closing it simply closes its response body. See
 * {@link ResponseBody} for an explanation and examples.
 */
public final class MobonResponse implements Closeable {
  final MobonRequest mobonRequest;
  final Protocol protocol;
  final int code;
  final String message;
  final @Nullable Handshake handshake;
  final Headers headers;
  final @Nullable ResponseBody body;
  final @Nullable
  MobonResponse networkMobonResponse;
  final @Nullable
  MobonResponse cacheMobonResponse;
  final @Nullable
  MobonResponse priorMobonResponse;
  final long sentRequestAtMillis;
  final long receivedResponseAtMillis;

  private volatile CacheControl cacheControl; // Lazily initialized.

  MobonResponse(Builder builder) {
    this.mobonRequest = builder.mobonRequest;
    this.protocol = builder.protocol;
    this.code = builder.code;
    this.message = builder.message;
    this.handshake = builder.handshake;
    this.headers = builder.headers.build();
    this.body = builder.body;
    this.networkMobonResponse = builder.networkMobonResponse;
    this.cacheMobonResponse = builder.cacheMobonResponse;
    this.priorMobonResponse = builder.priorMobonResponse;
    this.sentRequestAtMillis = builder.sentRequestAtMillis;
    this.receivedResponseAtMillis = builder.receivedResponseAtMillis;
  }

  /**
   * The wire-level mobonRequest that initiated this HTTP response. This is not necessarily the same
   * mobonRequest issued by the application:
   *
   * <ul>
   *     <li>It may be transformed by the HTTP client. For example, the client may copy headers like
   *         {@code Content-Length} from the mobonRequest body.
   *     <li>It may be the mobonRequest generated in response to an HTTP redirect or authentication
   *         challenge. In this case the mobonRequest URL may be different than the initial mobonRequest URL.
   * </ul>
   */
  public MobonRequest request() {
    return mobonRequest;
  }

  /**
   * Returns the HTTP protocol, such as {@link Protocol#HTTP_1_1} or {@link Protocol#HTTP_1_0}.
   */
  public Protocol protocol() {
    return protocol;
  }

  /** Returns the HTTP status code. */
  public int code() {
    return code;
  }

  /**
   * Returns true if the code is in [200..300), which means the mobonRequest was successfully received,
   * understood, and accepted.
   */
  public boolean isSuccessful() {
    return code >= 200 && code < 300;
  }

  /** Returns the HTTP status message. */
  public String message() {
    return message;
  }

  /**
   * Returns the TLS handshake of the connection that carried this response, or null if the response
   * was received without TLS.
   */
  public Handshake handshake() {
    return handshake;
  }

  public List<String> headers(String name) {
    return headers.values(name);
  }

  public @Nullable
  String header(String name) {
    return header(name, null);
  }

  public @Nullable
  String header(String name, @Nullable String defaultValue) {
    String result = headers.get(name);
    return result != null ? result : defaultValue;
  }

  public Headers headers() {
    return headers;
  }

  /**
   * Peeks up to {@code byteCount} bytes from the response body and returns them as a new response
   * body. If fewer than {@code byteCount} bytes are in the response body, the full response body is
   * returned. If more than {@code byteCount} bytes are in the response body, the returned value
   * will be truncated to {@code byteCount} bytes.
   *
   * <p>It is an error to call this method after the body has been consumed.
   *
   * <p><strong>Warning:</strong> this method loads the requested bytes into memory. Most
   * applications should set a modest limit on {@code byteCount}, such as 1 MiB.
   */
  public ResponseBody peekBody(long byteCount) throws IOException {
    BufferedSource source = body.source();
    source.request(byteCount);
    Buffer copy = source.buffer().clone();

    // There may be more than byteCount bytes in source.buffer(). If there is, return a prefix.
    Buffer result;
    if (copy.size() > byteCount) {
      result = new Buffer();
      result.write(copy, byteCount);
      copy.clear();
    } else {
      result = copy;
    }

    return ResponseBody.create(body.contentType(), result.size(), result);
  }

  /**
   * Returns a non-null value if this response was passed to {@link Callback#onResponse} or returned
   * from {@link Call#execute()}. MobonResponse bodies must be {@linkplain ResponseBody closed} and may
   * be consumed only once.
   *
   * <p>This always returns null on responses returned from {@link #cacheMobonResponse}, {@link
   * #networkMobonResponse}, and {@link #priorResponse()}.
   */
  public @Nullable ResponseBody body() {
    return body;
  }

  public Builder newBuilder() {
    return new Builder(this);
  }

  /** Returns true if this response redirects to another resource. */
  public boolean isRedirect() {
    switch (code) {
      case HTTP_PERM_REDIRECT:
      case HTTP_TEMP_REDIRECT:
      case HTTP_MULT_CHOICE:
      case HTTP_MOVED_PERM:
      case HTTP_MOVED_TEMP:
      case HTTP_SEE_OTHER:
        return true;
      default:
        return false;
    }
  }

  /**
   * Returns the raw response received from the network. Will be null if this response didn't use
   * the network, such as when the response is fully cached. The body of the returned response
   * should not be read.
   */
  public @Nullable
  MobonResponse networkResponse() {
    return networkMobonResponse;
  }

  /**
   * Returns the raw response received from the cache. Will be null if this response didn't use the
   * cache. For conditional get requests the cache response and network response may both be
   * non-null. The body of the returned response should not be read.
   */
  public @Nullable
  MobonResponse cacheResponse() {
    return cacheMobonResponse;
  }

  /**
   * Returns the response for the HTTP redirect or authorization challenge that triggered this
   * response, or null if this response wasn't triggered by an automatic retry. The body of the
   * returned response should not be read because it has already been consumed by the redirecting
   * client.
   */
  public @Nullable
  MobonResponse priorResponse() {
    return priorMobonResponse;
  }

  /**
   * Returns the authorization challenges appropriate for this response's code. If the response code
   * is 401 unauthorized, this returns the "WWW-Authenticate" challenges. If the response code is
   * 407 proxy unauthorized, this returns the "Proxy-Authenticate" challenges. Otherwise this
   * returns an empty list of challenges.
   */
  public List<Challenge> challenges() {
    String responseField;
    if (code == HTTP_UNAUTHORIZED) {
      responseField = "WWW-Authenticate";
    } else if (code == HTTP_PROXY_AUTH) {
      responseField = "Proxy-Authenticate";
    } else {
      return Collections.emptyList();
    }
    return HttpHeaders.parseChallenges(headers(), responseField);
  }

  /**
   * Returns the cache control directives for this response. This is never null, even if this
   * response contains no {@code Cache-Control} header.
   */
  public CacheControl cacheControl() {
    CacheControl result = cacheControl;
    return result != null ? result : (cacheControl = CacheControl.parse(headers));
  }

  /**
   * Returns a {@linkplain System#currentTimeMillis() timestamp} taken immediately before OkHttp
   * transmitted the initiating mobonRequest over the network. If this response is being served from the
   * cache then this is the timestamp of the original mobonRequest.
   */
  public long sentRequestAtMillis() {
    return sentRequestAtMillis;
  }

  /**
   * Returns a {@linkplain System#currentTimeMillis() timestamp} taken immediately after OkHttp
   * received this response's headers from the network. If this response is being served from the
   * cache then this is the timestamp of the original response.
   */
  public long receivedResponseAtMillis() {
    return receivedResponseAtMillis;
  }

  /**
   * Closes the response body. Equivalent to {@code body().close()}.
   *
   * <p>It is an error to close a response that is not eligible for a body. This includes the
   * responses returned from {@link #cacheMobonResponse}, {@link #networkMobonResponse}, and {@link
   * #priorResponse()}.
   */
  @Override
  public void close() {
    if (body == null) {
      throw new IllegalStateException("response is not eligible for a body and must not be closed");
    }
    body.close();
  }

  @Override
  public String toString() {
    return "MobonResponse{protocol="
        + protocol
        + ", code="
        + code
        + ", message="
        + message
        + ", url="
        + mobonRequest.url()
        + '}';
  }

  public static class Builder {
    MobonRequest mobonRequest;
    Protocol protocol;
    int code = -1;
    String message;
    @Nullable Handshake handshake;
    Headers.Builder headers;
    ResponseBody body;
    MobonResponse networkMobonResponse;
    MobonResponse cacheMobonResponse;
    MobonResponse priorMobonResponse;
    long sentRequestAtMillis;
    long receivedResponseAtMillis;

    public Builder() {
      headers = new Headers.Builder();
    }

    Builder(MobonResponse mobonResponse) {
      this.mobonRequest = mobonResponse.mobonRequest;
      this.protocol = mobonResponse.protocol;
      this.code = mobonResponse.code;
      this.message = mobonResponse.message;
      this.handshake = mobonResponse.handshake;
      this.headers = mobonResponse.headers.newBuilder();
      this.body = mobonResponse.body;
      this.networkMobonResponse = mobonResponse.networkMobonResponse;
      this.cacheMobonResponse = mobonResponse.cacheMobonResponse;
      this.priorMobonResponse = mobonResponse.priorMobonResponse;
      this.sentRequestAtMillis = mobonResponse.sentRequestAtMillis;
      this.receivedResponseAtMillis = mobonResponse.receivedResponseAtMillis;
    }

    public Builder request(MobonRequest mobonRequest) {
      this.mobonRequest = mobonRequest;
      return this;
    }

    public Builder protocol(Protocol protocol) {
      this.protocol = protocol;
      return this;
    }

    public Builder code(int code) {
      this.code = code;
      return this;
    }

    public Builder message(String message) {
      this.message = message;
      return this;
    }

    public Builder handshake(@Nullable Handshake handshake) {
      this.handshake = handshake;
      return this;
    }

    /**
     * Sets the header named {@code name} to {@code value}. If this mobonRequest already has any headers
     * with that name, they are all replaced.
     */
    public Builder header(String name, String value) {
      headers.set(name, value);
      return this;
    }

    /**
     * Adds a header with {@code name} and {@code value}. Prefer this method for multiply-valued
     * headers like "Set-Cookie".
     */
    public Builder addHeader(String name, String value) {
      headers.add(name, value);
      return this;
    }

    public Builder removeHeader(String name) {
      headers.removeAll(name);
      return this;
    }

    /** Removes all headers on this builder and adds {@code headers}. */
    public Builder headers(Headers headers) {
      this.headers = headers.newBuilder();
      return this;
    }

    public Builder body(@Nullable ResponseBody body) {
      this.body = body;
      return this;
    }

    public Builder networkResponse(@Nullable MobonResponse networkMobonResponse) {
      if (networkMobonResponse != null) checkSupportResponse("networkMobonResponse", networkMobonResponse);
      this.networkMobonResponse = networkMobonResponse;
      return this;
    }

    public Builder cacheResponse(@Nullable MobonResponse cacheMobonResponse) {
      if (cacheMobonResponse != null) checkSupportResponse("cacheMobonResponse", cacheMobonResponse);
      this.cacheMobonResponse = cacheMobonResponse;
      return this;
    }

    private void checkSupportResponse(String name, MobonResponse mobonResponse) {
      if (mobonResponse.body != null) {
        throw new IllegalArgumentException(name + ".body != null");
      } else if (mobonResponse.networkMobonResponse != null) {
        throw new IllegalArgumentException(name + ".networkMobonResponse != null");
      } else if (mobonResponse.cacheMobonResponse != null) {
        throw new IllegalArgumentException(name + ".cacheMobonResponse != null");
      } else if (mobonResponse.priorMobonResponse != null) {
        throw new IllegalArgumentException(name + ".priorMobonResponse != null");
      }
    }

    public Builder priorResponse(@Nullable MobonResponse priorMobonResponse) {
      if (priorMobonResponse != null) checkPriorResponse(priorMobonResponse);
      this.priorMobonResponse = priorMobonResponse;
      return this;
    }

    private void checkPriorResponse(MobonResponse mobonResponse) {
      if (mobonResponse.body != null) {
        throw new IllegalArgumentException("priorMobonResponse.body != null");
      }
    }

    public Builder sentRequestAtMillis(long sentRequestAtMillis) {
      this.sentRequestAtMillis = sentRequestAtMillis;
      return this;
    }

    public Builder receivedResponseAtMillis(long receivedResponseAtMillis) {
      this.receivedResponseAtMillis = receivedResponseAtMillis;
      return this;
    }

    public MobonResponse build() {
      if (mobonRequest == null) throw new IllegalStateException("mobonRequest == null");
      if (protocol == null) throw new IllegalStateException("protocol == null");
      if (code < 0) throw new IllegalStateException("code < 0: " + code);
      if (message == null) throw new IllegalStateException("message == null");
      return new MobonResponse(this);
    }
  }
}
