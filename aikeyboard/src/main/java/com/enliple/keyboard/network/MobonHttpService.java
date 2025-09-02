package com.enliple.keyboard.network;

import android.content.Context;
import android.text.TextUtils;

import com.enliple.httpmodule.Call;
import com.enliple.httpmodule.FormBody;
import com.enliple.httpmodule.HttpUrl;
import com.enliple.httpmodule.Interceptor;
import com.enliple.httpmodule.MediaType;
import com.enliple.httpmodule.MobonOkHttpClient;
import com.enliple.httpmodule.MobonRequest;
import com.enliple.httpmodule.MobonResponse;
import com.enliple.httpmodule.RequestBody;
import com.enliple.keyboard.activity.SharedPreference;
import com.enliple.keyboard.ui.common.Key;
import com.enliple.keyboard.ui.common.LogPrint;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public final class MobonHttpService {
//	public static ListAPI api() {
//
//		return (ListAPI) retrofit(ListAPI.class);
//	}
    public static final String TEMP_VALUE = "";
    public final static long TIME_OUT_CHAT = 30;
    public final static long TIME_OUT = 5;
    public final static TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    public static Call get(Context context, String url, Map<String, String> params) {

        long timeout = TIME_OUT;
        if ( url.contains(Url.OCB_GET_GPT_ANSWER) )
            timeout = TIME_OUT_CHAT;

        LogPrint.d("timeout ::  " + timeout);
        MobonOkHttpClient.Builder b = new MobonOkHttpClient.Builder();
        b.connectTimeout(timeout, TIME_UNIT);
        b.readTimeout(timeout, TIME_UNIT);
        b.writeTimeout(timeout, TIME_UNIT);
        b.retryOnConnectionFailure(true);
        b.interceptors().add(new Interceptor() {
            @Override
            public MobonResponse intercept(Chain chain) throws IOException {
                MobonRequest request = chain.request();
                MobonResponse response = null;
                boolean responseOK = false;
                int tryCount = 0;

                while (!responseOK && tryCount < 3) {
                    try {
                        LogPrint.d("responseOK false and retryCount :: " + tryCount + ", request url :: " + request.url);
                        response = chain.proceed(request);
                        if ( response != null )
                            LogPrint.d("responseOK false response status :: " + response.message() + ", request url :: " + request.url);
                        responseOK = response.isSuccessful();
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally{
                        tryCount++;
                    }
                }

                if(response == null) {
                    throw new IOException("response null");
                }

                // otherwise just pass the original response on
                return response;
            }
        });

        MobonOkHttpClient client = b.build();
        HttpUrl.Builder httpBuider = HttpUrl.parse(url).newBuilder();

        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                httpBuider.addQueryParameter(param.getKey(), param.getValue());
            }
        }


//        MobonRequest mobonRequest = new MobonRequest.Builder().header("User-Agent", "Android")
//                .header("Accept", "application/vnd.mobon.v1.full+json").url(httpBuider.build()).build();

        MobonRequest mobonRequest = null;
        LogPrint.d("url :: " + url);
        if ( url.startsWith(Url.FORMISSION_DOMAIN) ) {
            if ( url.startsWith(Url.FORMISSION_DOMAIN + "/v1/common/getAccessToken")) {
                LogPrint.d("getAccessToken");
                mobonRequest = new MobonRequest.Builder().addHeader("User-Agent", "Android")
                        .addHeader("Content-Type", "application/json")
                        .addHeader("x-refresh-token", CustomAsyncTask.FORMISSION_REFRESH_TOKEN)
                        .addHeader("Connection","close")
                        .addHeader("Accept", "application/json").url(httpBuider.build()).build();
//                .addHeader("Accept", "application/vnd.mobon.v1.full+json").url(httpBuider.build()).build();
            } else {
                LogPrint.d("formission not getAccessToken");
                LogPrint.d("token is :: " + SharedPreference.getString(context, Key.KEY_FORMISSION_TOKEN));
                mobonRequest = new MobonRequest.Builder().addHeader("User-Agent", "Android")
                        .addHeader("Content-Type", "application/json")
                        .addHeader("x-access-token", SharedPreference.getString(context, Key.KEY_FORMISSION_TOKEN))
                        .addHeader("Connection","close")
                        .addHeader("Accept", "application/json").url(httpBuider.build()).build();
//                .addHeader("Accept", "application/vnd.mobon.v1.full+json").url(httpBuider.build()).build();
            }
        } else {
            LogPrint.d("not formission");
            mobonRequest = new MobonRequest.Builder().addHeader("User-Agent", "Android")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("token", SharedPreference.getString(context, Key.KEY_TOKEN))
                    .addHeader("Connection","close")
                    .addHeader("Accept", "application/json").url(httpBuider.build()).build();
//                .addHeader("Accept", "application/vnd.mobon.v1.full+json").url(httpBuider.build()).build();
        }

        return client.newCall(mobonRequest);
    }

    public static Call get(String url, Map<String, String> params) {

        MobonOkHttpClient.Builder b = new MobonOkHttpClient.Builder();
        b.connectTimeout(TIME_OUT, TIME_UNIT);
        b.readTimeout(TIME_OUT, TIME_UNIT);
        b.writeTimeout(TIME_OUT, TIME_UNIT);
        b.retryOnConnectionFailure(true);
        b.interceptors().add(new Interceptor() {
            @Override
            public MobonResponse intercept(Chain chain) throws IOException {
                MobonRequest request = chain.request();
                MobonResponse response = null;
                boolean responseOK = false;
                int tryCount = 0;

                while (!responseOK && tryCount < 3) {
                    try {
                        response = chain.proceed(request);
                        responseOK = response.isSuccessful();
                    }catch (Exception e){

                    }finally{
                        tryCount++;
                    }
                }

                if(response == null) {
                    throw new IOException("response null");
                }

                // otherwise just pass the original response on
                return response;
            }
        });

        MobonOkHttpClient client = b.build();
        HttpUrl.Builder httpBuider = HttpUrl.parse(url).newBuilder();


        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                httpBuider.addQueryParameter(param.getKey(), param.getValue());
            }
        }


//        MobonRequest mobonRequest = new MobonRequest.Builder().header("User-Agent", "Android")
//                .header("Accept", "application/vnd.mobon.v1.full+json").url(httpBuider.build()).build();

        MobonRequest mobonRequest = new MobonRequest.Builder().addHeader("User-Agent", "Android")
                .addHeader("Content-Type", "application/json")
                .addHeader("Connection","close")
                .addHeader("Accept", "application/json").url(httpBuider.build()).build();
//                .addHeader("Accept", "application/vnd.mobon.v1.full+json").url(httpBuider.build()).build();

        return client.newCall(mobonRequest);
    }

    public static Call post_chat(Context context, String url, Map<String, String> params, String content_type) {
        MobonOkHttpClient.Builder b = new MobonOkHttpClient.Builder();
        b.connectTimeout(TIME_OUT_CHAT, TIME_UNIT);
        b.readTimeout(TIME_OUT_CHAT, TIME_UNIT);
        b.writeTimeout(TIME_OUT_CHAT, TIME_UNIT);
        b.retryOnConnectionFailure(true);
        b.interceptors().add(new Interceptor() {
            @Override
            public MobonResponse intercept(Chain chain) throws IOException {
                MobonRequest request = chain.request();
                MobonResponse response = null;
                boolean responseOK = false;
                int tryCount = 0;

                while (!responseOK && tryCount < 3) {
                    try {
                        response = chain.proceed(request);
                        if ( response != null )
                            LogPrint.d("responseOK false response status :: " + response.message() + " , response code :: " + response.code() + " , request url :: " + request.url);
                        responseOK = response.isSuccessful();
                    }catch (Exception e){

                    }finally{
                        tryCount++;
                    }
                }

                if(response == null) {
                    throw new IOException("response null");
                }

                // otherwise just pass the original response on
                return response;
            }
        });

        MobonOkHttpClient client = b.build();

        String paramJson = "";
        String question = "";
        try {
            if (params != null) {
                for (Map.Entry<String, String> param : params.entrySet()) {
                    if ( param.getKey().equals("question") )
                        question = param.getValue();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String model = SharedPreference.getString(context, Key.CHAT_GPT_MODEL);
            int max_token = SharedPreference.getInt(context, Key.CHAT_GPT_MAX_TOKEN);
            if (max_token < 0 )
                max_token = 600;
            if (TextUtils.isEmpty(model) )
                model = "gpt-3.5-turbo";
            JSONObject obj = new JSONObject();
            obj.put("model", model);
            obj.put("max_tokens", max_token);
            JSONArray array = new JSONArray();
            JSONObject inObj = new JSONObject();
            inObj.put("role", "user");
            inObj.put("content", question);
            array.put(inObj);
            obj.put("messages", array);
            paramJson = obj.toString();
            LogPrint.d("obj.string :: " + obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), paramJson);
        LogPrint.d("paramJson :: " + paramJson);
        MobonRequest mobonRequest = null;
        mobonRequest = new MobonRequest.Builder().addHeader("User-Agent", "Android")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + TEMP_VALUE)
                .addHeader("Connection","close")
                .addHeader("Accept", "application/json").url(url).post(body).build();
        return client.newCall(mobonRequest);
    }

    public static Call post_map(Context context, String url, Map<String, String> params, String content_type) {
        MobonOkHttpClient.Builder b = new MobonOkHttpClient.Builder();
        b.connectTimeout(TIME_OUT, TIME_UNIT);
        b.readTimeout(TIME_OUT, TIME_UNIT);
        b.writeTimeout(TIME_OUT, TIME_UNIT);
        b.retryOnConnectionFailure(true);
        b.interceptors().add(new Interceptor() {
            @Override
            public MobonResponse intercept(Chain chain) throws IOException {
                MobonRequest request = chain.request();
                MobonResponse response = null;
                boolean responseOK = false;
                int tryCount = 0;

                while (!responseOK && tryCount < 3) {
                    try {
                        response = chain.proceed(request);
                        responseOK = response.isSuccessful();
                    }catch (Exception e){

                    }finally{
                        tryCount++;
                    }
                }

                if(response == null) {
                    throw new IOException("response null");
                }

                // otherwise just pass the original response on
                return response;
            }
        });



        MobonOkHttpClient client = b.build();

        String paramJson = "";
        try {
            JSONObject object = new JSONObject();
            if (params != null) {
                for (Map.Entry<String, String> param : params.entrySet()) {
                    object.put(param.getKey(), param.getValue());
                }
                paramJson = object.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), paramJson);
        LogPrint.d("paramJson :: " + paramJson);
        MobonRequest mobonRequest = null;
        mobonRequest = new MobonRequest.Builder().addHeader("User-Agent", "Android")
                .addHeader("Content-Type", "application/json")
                .addHeader("x-access-token", SharedPreference.getString(context, Key.KEY_FORMISSION_TOKEN))
                .addHeader("Connection","close")
                .addHeader("Accept", "application/json").url(url).post(body).build();
        return client.newCall(mobonRequest);
    }

    public static Call post(Context context, String url, Map<String, String> params, String content_type) {
        MobonOkHttpClient.Builder b = new MobonOkHttpClient.Builder();
        b.connectTimeout(TIME_OUT, TIME_UNIT);
        b.readTimeout(TIME_OUT, TIME_UNIT);
        b.writeTimeout(TIME_OUT, TIME_UNIT);
        b.retryOnConnectionFailure(true);
        b.interceptors().add(new Interceptor() {
            @Override
            public MobonResponse intercept(Chain chain) throws IOException {
                MobonRequest request = chain.request();
                MobonResponse response = null;
                boolean responseOK = false;
                int tryCount = 0;

                while (!responseOK && tryCount < 3) {
                    try {
                        response = chain.proceed(request);
                        responseOK = response.isSuccessful();
                    }catch (Exception e){

                    }finally{
                        tryCount++;
                    }
                }

                if(response == null) {
                    throw new IOException("response null");
                }

                // otherwise just pass the original response on
                return response;
            }
        });



        MobonOkHttpClient client = b.build();


        FormBody.Builder formBuilder = new FormBody.Builder();

        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                formBuilder.add(param.getKey(), param.getValue());
            }
        }
        FormBody formBody = formBuilder.build();
        MobonRequest mobonRequest = null;
        if (content_type.equals("json")) {
            if ( url.startsWith(Url.FORMISSION_DOMAIN) ) {
                mobonRequest = new MobonRequest.Builder().addHeader("User-Agent", "Android")
                        .addHeader("Content-Type", "application/json")
                        .addHeader("x-access-token", SharedPreference.getString(context, Key.KEY_FORMISSION_TOKEN))
                        .addHeader("Connection","close")
                        .addHeader("Accept", "application/json").url(url).post(formBody).build();
            } else {
                mobonRequest = new MobonRequest.Builder().addHeader("User-Agent", "Android")
                        .addHeader("Content-Type", "application/json")
                        .addHeader("token", SharedPreference.getString(context, Key.KEY_TOKEN))
                        .addHeader("Connection","close")
                        .addHeader("Accept", "application/json").url(url).post(formBody).build();
            }
        } else
            mobonRequest = new MobonRequest.Builder().addHeader("User-Agent", "Android")
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("token", SharedPreference.getString(context, Key.KEY_TOKEN))
                    .addHeader("Connection","close")
                    .addHeader("Accept", "application/json").url(url).post(formBody).build();
        return client.newCall(mobonRequest);
    }

    public static Call post(String url, Map<String, String> params, String content_type) {
        MobonOkHttpClient.Builder b = new MobonOkHttpClient.Builder();
        b.connectTimeout(TIME_OUT, TIME_UNIT);
        b.readTimeout(TIME_OUT, TIME_UNIT);
        b.writeTimeout(TIME_OUT, TIME_UNIT);
        b.retryOnConnectionFailure(true);
        b.interceptors().add(new Interceptor() {
            @Override
            public MobonResponse intercept(Chain chain) throws IOException {
                MobonRequest request = chain.request();
                MobonResponse response = null;
                boolean responseOK = false;
                int tryCount = 0;

                while (!responseOK && tryCount < 3) {
                    try {
                        response = chain.proceed(request);
                        responseOK = response.isSuccessful();
                    }catch (Exception e){

                    }finally{
                        tryCount++;
                    }
                }

                if(response == null) {
                    throw new IOException("response null");
                }

                // otherwise just pass the original response on
                return response;
            }
        });


        MobonOkHttpClient client = b.build();


        FormBody.Builder formBuilder = new FormBody.Builder();

        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                formBuilder.add(param.getKey(), param.getValue());
            }
        }
        FormBody formBody = formBuilder.build();
        MobonRequest mobonRequest = null;
        if (content_type.equals("json"))
            mobonRequest = new MobonRequest.Builder().addHeader("User-Agent", "Android")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Connection","close")
                    .addHeader("Accept", "application/json").url(url).post(formBody).build();
        else
            mobonRequest = new MobonRequest.Builder().addHeader("User-Agent", "Android")
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("Connection","close")
                    .addHeader("Accept", "application/json").url(url).post(formBody).build();

        return client.newCall(mobonRequest);
    }
}
