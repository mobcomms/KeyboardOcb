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
import android.text.TextUtils;

import com.enliple.httpmodule.internal.NamedRunnable;
import com.enliple.httpmodule.internal.cache.CacheInterceptor;
import com.enliple.httpmodule.internal.connection.ConnectInterceptor;
import com.enliple.httpmodule.internal.connection.StreamAllocation;
import com.enliple.httpmodule.internal.http.BridgeInterceptor;
import com.enliple.httpmodule.internal.http.CallServerInterceptor;
import com.enliple.httpmodule.internal.http.RealInterceptorChain;
import com.enliple.httpmodule.internal.http.RetryAndFollowUpInterceptor;
import com.enliple.httpmodule.internal.platform.Platform;

import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static com.enliple.httpmodule.internal.platform.Platform.INFO;

final class RealCall implements Call {
    final MobonOkHttpClient client;
    final RetryAndFollowUpInterceptor retryAndFollowUpInterceptor;

    /**
     * There is a cycle between the {@link Call} and {@link EventListener} that makes this awkward.
     * This will be set after we create the call instance then create the event listener instance.
     */
    private EventListener eventListener;

    /**
     * The application's original mobonRequest unadulterated by redirects or auth headers.
     */
    final MobonRequest originalMobonRequest;
    final boolean forWebSocket;

    // Guarded by this.
    private boolean executed;
    private static Context context;
    private static boolean isInit;

    private RealCall(MobonOkHttpClient client, MobonRequest originalMobonRequest, boolean forWebSocket) {
        this.client = client;
        this.originalMobonRequest = originalMobonRequest;
        this.forWebSocket = forWebSocket;
        this.retryAndFollowUpInterceptor = new RetryAndFollowUpInterceptor(client, forWebSocket);
    }

    static RealCall newRealCall(Context _context, MobonOkHttpClient client, MobonRequest originalMobonRequest, boolean forWebSocket) {
        // Safely publish the Call instance to the EventListener.
        context = _context.getApplicationContext();
        originalMobonRequest.url(originalMobonRequest.url.toString());
        RealCall call = new RealCall(client, originalMobonRequest, forWebSocket);
        call.eventListener = client.eventListenerFactory().create(call);
        return call;
    }

    static RealCall newRealCall(MobonOkHttpClient client, MobonRequest originalMobonRequest, boolean forWebSocket) {
        // Safely publish the Call instance to the EventListener.
        RealCall call = new RealCall(client, originalMobonRequest, forWebSocket);
        call.eventListener = client.eventListenerFactory().create(call);
        return call;
    }

    @Override
    public MobonRequest request() {
        return originalMobonRequest;
    }

    @Override
    public MobonResponse execute() throws IOException {
        synchronized (this) {
            if (executed) throw new IllegalStateException("Already Executed");
            executed = true;
        }
        captureCallStackTrace();
        eventListener.callStart(this);
        try {
            client.dispatcher().executed(this);
            MobonResponse result = getResponseWithInterceptorChain();
            if (result == null) throw new IOException("Canceled");
            return result;
        } catch (IOException e) {
            eventListener.callFailed(this, e);
            throw e;
        } finally {
            client.dispatcher().finished(this);
        }
    }

    private void captureCallStackTrace() {
        Object callStackTrace = Platform.get().getStackTraceForCloseable("response.body().close()");
        retryAndFollowUpInterceptor.setCallStackTrace(callStackTrace);
    }

    @Override
    public void enqueue(Callback responseCallback) {
        synchronized (this) {
            if (executed) throw new IllegalStateException("Already Executed");
            executed = true;
        }
        captureCallStackTrace();
        eventListener.callStart(this);
        client.dispatcher().enqueue(new AsyncCall(responseCallback));
    }

    @Override
    public void cancel() {
        retryAndFollowUpInterceptor.cancel();
    }

    @Override
    public synchronized boolean isExecuted() {
        return executed;
    }

    @Override
    public boolean isCanceled() {
        return retryAndFollowUpInterceptor.isCanceled();
    }

    @SuppressWarnings("CloneDoesntCallSuperClone")
    // We are a final type & this saves clearing state.
    @Override
    public RealCall clone() {
        return RealCall.newRealCall(client, originalMobonRequest, forWebSocket);
    }

    StreamAllocation streamAllocation() {
        return retryAndFollowUpInterceptor.streamAllocation();
    }

    final class AsyncCall extends NamedRunnable {
        private final Callback responseCallback;

        AsyncCall(Callback responseCallback) {
            super("OkHttp %s", redactedUrl());
            this.responseCallback = responseCallback;
        }

        String host() {
            return originalMobonRequest.url().host();
        }

        MobonRequest request() {
            return originalMobonRequest;
        }

        RealCall get() {
            return RealCall.this;
        }

        @Override
        protected void execute() {
            boolean signalledCallback = false;
            try {
                MobonResponse mobonResponse = getResponseWithInterceptorChain();
                if (retryAndFollowUpInterceptor.isCanceled()) {
                    signalledCallback = true;
                    responseCallback.onFailure(RealCall.this, new IOException("Canceled"));
                } else {
                    signalledCallback = true;
                    responseCallback.onResponse(RealCall.this, mobonResponse);
                }
            } catch (IOException e) {
                if (signalledCallback) {
                    // Do not signal the callback twice!
                    Platform.get().log(INFO, "Callback failure for " + toLoggableString(), e);
                } else {
                    eventListener.callFailed(RealCall.this, e);
                    responseCallback.onFailure(RealCall.this, e);
                }
            } finally {
                client.dispatcher().finished(this);
            }
        }
    }

    /**
     * Returns a string that describes this call. Doesn't include a full URL as that might contain
     * sensitive information.
     */
    String toLoggableString() {
        return (isCanceled() ? "canceled " : "")
                + (forWebSocket ? "web socket" : "call")
                + " to " + redactedUrl();
    }

    String redactedUrl() {
        return originalMobonRequest.url().redact();
    }

    MobonResponse getResponseWithInterceptorChain() throws IOException {
        // Build a full stack of interceptors.
        List<Interceptor> interceptors = new ArrayList<>();
        interceptors.addAll(client.interceptors());
        interceptors.add(retryAndFollowUpInterceptor);
        interceptors.add(new BridgeInterceptor(client.cookieJar()));
        interceptors.add(new CacheInterceptor(client.internalCache()));
        interceptors.add(new ConnectInterceptor(client));
        if (!forWebSocket) {
            interceptors.addAll(client.networkInterceptors());
        }
        interceptors.add(new CallServerInterceptor(forWebSocket));

        Interceptor.Chain chain = new RealInterceptorChain(interceptors, null, null, null, 0,
                originalMobonRequest, this, eventListener, client.connectTimeoutMillis(),
                client.readTimeoutMillis(), client.writeTimeoutMillis());

        return chain.proceed(originalMobonRequest);
    }

    static private Call get(String url, Map<String, String> params) {

        MobonOkHttpClient.Builder b = new MobonOkHttpClient.Builder();
        b.connectTimeout(60, TimeUnit.SECONDS);
        b.readTimeout(60, TimeUnit.SECONDS);
        b.writeTimeout(60, TimeUnit.SECONDS);
        MobonOkHttpClient client = b.build();

        HttpUrl.Builder httpBuider = HttpUrl.parse(url).newBuilder();

        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                httpBuider.addQueryParameter(param.getKey(), param.getValue());
            }
        }

        MobonRequest mobonRequest = new MobonRequest.Builder().addHeader("User-Agent", "Android")
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json").url(httpBuider.build()).build();

        return client.newCall(mobonRequest);
    }





}
