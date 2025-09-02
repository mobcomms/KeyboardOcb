package com.enliple.keyboard.mobonAD.manager;

import android.content.Context;

/**
import com.enliple.httpmodule.Call;
import com.enliple.httpmodule.FormBody;
import com.enliple.httpmodule.HttpUrl;
import com.enliple.httpmodule.MobonOkHttpClient;
import com.enliple.httpmodule.MobonRequest;
**/
import com.enliple.httpmodule.Call;
import com.enliple.httpmodule.FormBody;
import com.enliple.httpmodule.HttpUrl;
import com.enliple.httpmodule.MobonOkHttpClient;
import com.enliple.httpmodule.MobonRequest;

import java.util.Map;
import java.util.concurrent.TimeUnit;


public final class MobonHttpService {
//	public static ListAPI api() {
//
//		return (ListAPI) retrofit(ListAPI.class);
//	}

    public static Call get(Context context, String url, Map<String, String> params) {

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

//        MobonRequest mobonRequest = new MobonRequest.Builder().header("User-Agent", "Android")
//                .header("Accept", "application/vnd.mobon.v1.full+json").url(httpBuider.build()).build();

        MobonRequest mobonRequest = new MobonRequest.Builder().addHeader("User-Agent", "Android")
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/vnd.mobon.v1.full+json").url(httpBuider.build()).build();

        return client.newCall(context, mobonRequest);
    }

    public static Call post(Context context, String url, Map<String, String> params, String content_type) {
        MobonOkHttpClient.Builder b = new MobonOkHttpClient.Builder();
        b.connectTimeout(60, TimeUnit.SECONDS);
        b.readTimeout(60, TimeUnit.SECONDS);
        b.writeTimeout(60, TimeUnit.SECONDS);

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
                    .addHeader("Accept", "application/json").url(url).post(formBody).build();
        else
            mobonRequest = new MobonRequest.Builder().addHeader("User-Agent", "Android")
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("Accept", "application/json").url(url).post(formBody).build();

        return client.newCall(context, mobonRequest);
    }


}
