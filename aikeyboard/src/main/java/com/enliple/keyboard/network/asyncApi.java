package com.enliple.keyboard.network;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.enliple.httpmodule.Call;
import com.enliple.httpmodule.Callback;
import com.enliple.httpmodule.MobonResponse;
import com.enliple.keyboard.common.KeyboardLogPrint;
import com.enliple.keyboard.ui.common.LogPrint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

public class asyncApi {

    CustomAsyncTask.CallbackObjectResponse callback;

    private String mUrl;
    public Map<String, String> mParam;
    private Context mContext;
    private Call mCall;

    /**
     * public asyncApi(Context context, String _url, Map<String, String> params, String requestMethod, CustomAsyncTask.CallbackObjectResponse _response) {
     * mUrl = _url;
     * mParam = params;
     * callback = _response;
     * mContext = context;
     * RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
     * String result = requestHttpURLConnection.request(mContext, mUrl, mParam, requestMethod);
     * requestHttpURLConnection.
     * <p>
     * if (result == null || result.isEmpty()) {
     * callback.onError("");
     * return;
     * }
     * <p>
     * try {
     * String str = result;
     * KeyboardLogPrint.w("str :: " + str);
     * if (str.startsWith("{"))
     * callback.onResponse(new JSONObject(str));
     * else if (str.startsWith("[")) {
     * callback.onResponse(new JSONArray(str).getJSONObject(0));
     * } else
     * callback.onResponse(new JSONObject(str));
     * } catch (JSONException e) {
     * // TODO Auto-generated catch block
     * System.out.println("error => " + e.getMessage());
     * callback.onError("error::" + e.getMessage());
     * }
     * }
     **/

    public asyncApi(Context context, String _url, Map<String, String> params, String requestMethod, boolean getAllArr, CustomAsyncTask.CallbackObjectResponse _response) {
        mUrl = _url;
        mParam = params;
        mContext = context;
        LogPrint.d("url :: " + _url);
        if (TextUtils.equals(requestMethod.toLowerCase(), "get")) {
            mCall = MobonHttpService.get(mContext, _url, params);
            mCall.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, MobonResponse mobonResponse) throws IOException {
                    if ( mobonResponse != null ) {
                        if ( mobonResponse.isSuccessful() ) {
                        } else {
                        }
                    } else {
                    }

                    if ( mobonResponse != null && mobonResponse.isSuccessful() ) {
                        int code = mobonResponse.code();
                        if ( code == 200 ) {
                            if (mobonResponse.body() != null) {
                                String str = mobonResponse.body().string();
                                KeyboardLogPrint.d("url :: " + _url + "str :: " + str);
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            if (str.startsWith("{"))
                                                _response.onResponse(new JSONObject(str));
                                            else if (str.startsWith("[")) {
                                                _response.onResponse(new JSONArray(str));
                                            } else
                                                _response.onResponse(new JSONObject(str));
                                        } catch (JSONException e) {
                                            // TODO Auto-generated catch block
                                            System.out.println("error => " + e.getMessage());
                                            _response.onError("error::" + e.getMessage());
                                        }
                                    }
                                });
                            } else {
                                LogPrint.d("onConnectSDKUrlAPI 3 ERROR2 error => " + mobonResponse.message() + " , api :: " + call.request().url);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    LogPrint.d("onConnectSDKUrlAPI 4 ERROR3 error => " + e.toString() + " , api :: " + call.request().url);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            _response.onError("error::" + e.getMessage());
                        }
                    });

                }
            });
        } else if (TextUtils.equals(requestMethod.toLowerCase(), "post_param_map")) {
            mCall = MobonHttpService.post_map(mContext, _url, params, "json");
            mCall.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, MobonResponse mobonResponse) throws IOException {
                    if ( mobonResponse != null ) {
                        if ( mobonResponse.isSuccessful() ) {
                        } else {
                        }
                    } else {

                    }

                    if ( mobonResponse != null && mobonResponse.isSuccessful() ) {
                        int code = mobonResponse.code();
                        if ( code == 200 ) {
                            if (mobonResponse.body() != null) {
                                String str = mobonResponse.body().string();
                                KeyboardLogPrint.d("url :: " + _url + "str :: " + str);
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            if (str.startsWith("{"))
                                                _response.onResponse(new JSONObject(str));
                                            else if (str.startsWith("[")) {
                                                _response.onResponse(new JSONArray(str));
                                            } else
                                                _response.onResponse(new JSONObject(str));
                                        } catch (JSONException e) {
                                            // TODO Auto-generated catch block
                                            _response.onError("error::" + e.getMessage());
                                        }
                                    }
                                });
                            }
                        } else {

                        }
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    LogPrint.d("onConnectSDKUrlAPI 5 ERROR3 error => " + e.toString() + " , api :: " + call.request().url);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            _response.onError("error::" + e.getMessage());
                        }
                    });
                }
            });
        } else {
            mCall = MobonHttpService.post(mContext, _url, params, "json");
            mCall.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, MobonResponse mobonResponse) throws IOException {
                    if ( mobonResponse != null ) {
                        if ( mobonResponse.isSuccessful() ) {
                        } else {
                        }
                    } else {

                    }

                    if ( mobonResponse != null && mobonResponse.isSuccessful() ) {
                        int code = mobonResponse.code();
                        if ( code == 200 ) {
                            if (mobonResponse.body() != null) {
                                String str = mobonResponse.body().string();
                                KeyboardLogPrint.d("url :: " + _url + "str :: " + str);
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            if (str.startsWith("{"))
                                                _response.onResponse(new JSONObject(str));
                                            else if (str.startsWith("[")) {
                                                _response.onResponse(new JSONArray(str));
                                            } else
                                                _response.onResponse(new JSONObject(str));
                                        } catch (JSONException e) {
                                            // TODO Auto-generated catch block
                                            _response.onError("error::" + e.getMessage());
                                        }
                                    }
                                });
                            }
                        } else {

                        }
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    LogPrint.d("onConnectSDKUrlAPI 5 ERROR3 error => " + e.toString() + " , api :: " + call.request().url);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            _response.onError("error::" + e.getMessage());
                        }
                    });
                }
            });
        }
    }

    public asyncApi(Context context, String _url, Map<String, String> params, String requestMethod, CustomAsyncTask.CallbackObjectResponse _response) {
        mUrl = _url;
        mParam = params;
        mContext = context;
        LogPrint.d("url1 :: " + _url);
        if (TextUtils.equals(requestMethod.toLowerCase(), "get")) {
            mCall = MobonHttpService.get(mContext, _url, params);
            mCall.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, MobonResponse mobonResponse) throws IOException {
                    if ( mobonResponse != null ) {
                        if ( mobonResponse.isSuccessful() ) {

                        } else {

                        }
                    } else {

                    }
                    if ( mobonResponse != null && mobonResponse.isSuccessful() ) {
                        int code = mobonResponse.code();
                        if ( code == 200 ) {
                            if (mobonResponse.body() != null) {
                                String str = mobonResponse.body().string();
                                KeyboardLogPrint.d("apilist url :: " + _url + "str :: " + str);
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            if (str.startsWith("{"))
                                                _response.onResponse(new JSONObject(str));
                                            else if (str.startsWith("[")) {
                                                if ("https://ocbapi.cashkeyboard.co.kr/API/OCB/get_shopping.php".equals(_url) )
                                                    _response.onResponse(new JSONArray(str));
                                                else
                                                    _response.onResponse(new JSONArray(str).getJSONObject(0));
                                            } else
                                                _response.onResponse(new JSONObject(str));
                                        } catch (JSONException e) {
                                            // TODO Auto-generated catch block
                                            System.out.println("error => " + e.getMessage());
                                            _response.onError("error::" + e.getMessage());
                                        }
                                    }
                                });
                            } else {
                                LogPrint.d("onConnectSDKUrlAPI 6 ERROR2 error => " + mobonResponse.message() + " , api :: " + call.request().url);
                            }
                        } else {
                            LogPrint.d("onConnectSDKUrlAPI not 200 code :: " + code + " , api :: " + call.request().url);
                        }
                    } else {
                        LogPrint.d("onConnectSDKUrlAPI mobonResponse null or not success api :: " + call.request().url);
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    LogPrint.d("onConnectSDKUrlAPI 1 ERROR3 error => " + e.toString() + " , api :: " + call.request().url);

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            _response.onError("error::" + e.getMessage());
                        }
                    });
                }
            });
        } else if (TextUtils.equals(requestMethod.toLowerCase(), "post_param_map")) {
            mCall = MobonHttpService.post_map(mContext, _url, params, "json");
            mCall.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, MobonResponse mobonResponse) throws IOException {
                    if ( mobonResponse != null ) {
                        if ( mobonResponse.isSuccessful() ) {

                        } else {

                        }
                    } else {

                    }

                    if ( mobonResponse != null && mobonResponse.isSuccessful() ) {
                        int code = mobonResponse.code();
                        if ( code == 200 ) {
                            if (mobonResponse.body() != null) {
                                String str = mobonResponse.body().string();
                                KeyboardLogPrint.d("apilist url :: " + _url + " , str :: " + str);
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            if (str.startsWith("{"))
                                                _response.onResponse(new JSONObject(str));
                                            else if (str.startsWith("[")) {
                                                _response.onResponse(new JSONArray(str).getJSONObject(0));
                                            } else
                                                _response.onResponse(new JSONObject(str));
                                        } catch (JSONException e) {
                                            // TODO Auto-generated catch block
                                            _response.onError("error::" + e.getMessage());
                                        }
                                    }
                                });
                            }
                        } else {

                        }
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    LogPrint.d("onConnectSDKUrlAPI 2 ERROR3 error => " + e.toString() + " , api :: " + call.request().url);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            _response.onError("error::" + e.getMessage());
                        }
                    });
                }
            });
        } else if (TextUtils.equals(requestMethod.toLowerCase(), "post_chat")) {
            mCall = MobonHttpService.post_chat(mContext, _url, params, "json");
            mCall.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, MobonResponse mobonResponse) throws IOException {
                    if ( mobonResponse != null ) {
                        if ( mobonResponse.isSuccessful() ) {

                        } else {

                        }
                    } else {

                    }

                    if ( mobonResponse != null && mobonResponse.isSuccessful() ) {
                        int code = mobonResponse.code();
                        if ( code == 200 ) {
                            if (mobonResponse.body() != null) {
                                String str = mobonResponse.body().string();
                                KeyboardLogPrint.d("apilist url :: " + _url + "str :: " + str);
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            if (str.startsWith("{"))
                                                _response.onResponse(new JSONObject(str));
                                            else if (str.startsWith("[")) {
                                                _response.onResponse(new JSONArray(str).getJSONObject(0));
                                            } else
                                                _response.onResponse(new JSONObject(str));
                                        } catch (JSONException e) {
                                            // TODO Auto-generated catch block
                                            _response.onError("error::" + e.getMessage());
                                        }
                                    }
                                });
                            }
                        } else {

                        }
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    LogPrint.d("onConnectSDKUrlAPI 2 ERROR3 error => " + e.toString() + " , api :: " + call.request().url);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            _response.onError("error::" + e.getMessage());
                        }
                    });
                }
            });
        } else {
            mCall = MobonHttpService.post(mContext, _url, params, "json");
            mCall.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, MobonResponse mobonResponse) throws IOException {
                    if ( mobonResponse != null ) {
                        if ( mobonResponse.isSuccessful() ) {

                        } else {

                        }
                    } else {

                    }

                    if ( mobonResponse != null && mobonResponse.isSuccessful() ) {
                        int code = mobonResponse.code();
                        if ( code == 200 ) {
                            if (mobonResponse.body() != null) {
                                String str = mobonResponse.body().string();
                                KeyboardLogPrint.d("apilist url :: " + _url + "str :: " + str);
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            if (str.startsWith("{"))
                                                _response.onResponse(new JSONObject(str));
                                            else if (str.startsWith("[")) {
                                                _response.onResponse(new JSONArray(str).getJSONObject(0));
                                            } else
                                                _response.onResponse(new JSONObject(str));
                                        } catch (JSONException e) {
                                            // TODO Auto-generated catch block
                                            _response.onError("error::" + e.getMessage());
                                        }
                                    }
                                });
                            }
                        } else {

                        }
                    }

                    /**
                    if (mobonResponse != null && mobonResponse.isSuccessful() && mobonResponse.body() != null) {
                        String str = mobonResponse.body().string();

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Log.w("TAG", "str :: " + str);
                                    if (str.startsWith("{"))
                                        _response.onResponse(new JSONObject(str));
                                    else if (str.startsWith("[")) {
                                        _response.onResponse(new JSONArray(str).getJSONObject(0));
                                    } else
                                        _response.onResponse(new JSONObject(str));
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    System.out.println("error => " + e.getMessage());
                                    _response.onError("error::" + e.getMessage());
                                }
                            }
                        });

                    } else {
                        LogPrint.d("onConnectSDKUrlAPI ERROR2 error => " + mobonResponse.message());
                    }**/
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    LogPrint.d("onConnectSDKUrlAPI 2 ERROR3 error => " + e.toString() + " , api :: " + call.request().url);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            _response.onError("error::" + e.getMessage());
                        }
                    });
                }
            });
        }
    }

    public void cancel() {
        if (mCall != null) {
            mCall.cancel();
        }
    }
}
