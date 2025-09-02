package com.enliple.keyboard.mobonAD;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.enliple.httpmodule.Call;
import com.enliple.httpmodule.Callback;
import com.enliple.httpmodule.MobonResponse;
import com.enliple.keyboard.mobonAD.manager.IntegrationHelper;
import com.enliple.keyboard.network.MobonHttpService;
import com.enliple.keyboard.ui.common.LogPrint;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * import com.enliple.httpmodule.Call;
 * import com.enliple.httpmodule.Callback;
 * import com.enliple.httpmodule.MobonResponse;
 * import com.enliple.mobonAD.manager.AESCipher;
 * import com.enliple.mobonAD.manager.IntegrationHelper;
 * import com.enliple.mobonAD.manager.SPManager;
 * import com.enliple.network.MobonHttpService;
 * import com.enliple.ui.common.LogPrint;
 **/

public class MobonSimpleSDK {
    private static MobonSimpleSDK mInstance = null;
    private Context mApplicationContext;
    private Context mContext;

    private static String mViewMobonKey;
    private static String mAdGubun;
    private static String mUnitId;
    private int mImageLimit = -1;
    private static Application mApplication;

    public MobonSimpleSDK(Context context, String _mediaCode) {
        mApplicationContext = context.getApplicationContext();
        mContext = context;
        setMetaCode(mApplicationContext, _mediaCode);
    }

    public static MobonSimpleSDK get(Context context) {

        if (context == null || mInstance == null)
            return null;

        String _mediaCode = SPManager.getString(context, MobonKey.META_DATA_MEDIA_CODE);

        if (TextUtils.isEmpty(_mediaCode))
            _mediaCode = MobonUtils.getMetaData(context, MobonKey.META_DATA_MEDIA_CODE);

        if (TextUtils.isEmpty(_mediaCode)) {
            return null;
        }

        synchronized (context) {
            if (mInstance == null) {
                new MobonSimpleSDK(context, _mediaCode);
            }
        }

        return mInstance;
    }

    public MobonSimpleSDK setApplication(Application _application) {

        if (_application == null)
            throw new IllegalArgumentException("not Apllication Context");

        mApplication = _application;
        return this;
    }


    public static void init(Application _application) {

        if (_application == null)
            throw new IllegalArgumentException("not Apllication initialize");

        mApplication = _application;
    }

    public Application getApplication() {
        return mApplication;
    }

    public Context getContext() {
        return mApplicationContext;
    }


    private void onCreate() {
        try {
            LogPrint.d("mobon oncreate()");
            if (mApplicationContext == null || TextUtils.isEmpty(SPManager.getString(mApplicationContext, MobonKey.META_DATA_MEDIA_CODE)))
                return;

            if (mInstance == null)
                mInstance = this;
            else {
                onConnectGetAUID_API();
                return;
            }

            IntegrationHelper.validateIntegration(mApplicationContext);

            // 앱 최초 설치시 체크
            if (!SPManager.getBoolean(mApplicationContext, MobonKey.FIRST_APP_INSTALL)) {
                LogPrint.d("앱 최초 설치시 초기화 작업");
                SPManager.setBoolean(mApplicationContext, MobonKey.INTERSTITIAL_CANCELABLE, true);
                SPManager.setBoolean(mApplicationContext, MobonKey.ENDING_POPUP_CANCELABLE, true);
                SPManager.setBoolean(mApplicationContext, MobonKey.FIRST_APP_INSTALL, true);
                SPManager.setBoolean(mApplicationContext, MobonKey.BACON_BANNER_CHECKABLE, true);
                SPManager.setBoolean(mApplicationContext, MobonKey.BACON_ENDING_CHECKABLE, true);
                SPManager.setBoolean(mApplicationContext, MobonKey.BACON_INTERSTITIAL_CHECKABLE, true);
            }

                MobonUtils.getAdId(mApplicationContext, MobonSimpleSDK.this);           
                onConnectSDKUrlAPI();

        } catch (Exception e) {
            LogPrint.e("onCreate() Exception! " + e.getLocalizedMessage());
        }
    }

    /**
     * ending popup의 노출 여부
     *
     * @param _b
     */
    @Deprecated
    public void setEndingPopupVisibility(boolean _b) {
        //mContext = contextWeakReference.get();
        if (mApplicationContext == null) return;
        boolean isVisible = SPManager.getBoolean(mApplicationContext, MobonKey.BANNER_ON_OFF_CLOSE);
        SPManager.setBoolean(mApplicationContext, MobonKey.BANNER_ON_OFF_CLOSE, _b);
    }

    @Deprecated
    public void setEndingType(MobonKey.ENDING_TYPE _type) {
        SPManager.setString(mApplicationContext, MobonKey.ENDING_POPUP_TYPE, _type.toString());
    }

    @Deprecated
    private void setEndingBgColor(String _color) {
        // mContext = contextWeakReference.get();
        if (mApplicationContext == null) return;
        SPManager.setString(mApplicationContext, MobonKey.ENDING_BG_COLOR, _color);
    }

    @Deprecated
    private void setEndingTextColor(String _color) {
        // mContext = contextWeakReference.get();
        if (mApplicationContext == null) return;
        SPManager.setString(mApplicationContext, MobonKey.ENDING_TEXT_COLOR, _color);
    }

    @Deprecated
    public void setInterstitialType(MobonKey.INTERSTITIAL_TYPE _type) {
        SPManager.setString(mApplicationContext, MobonKey.INTERSTITIAL_POPUP_TYPE, _type.toString());
    }

    public MobonSimpleSDK setImageSizeLimit(int _limitKb) {
        mImageLimit = _limitKb;
        return this;
    }


    public void onConnectSDKUrlAPI() {
        // mContext = contextWeakReference.get();

        if (mApplicationContext == null) return;
        final String today = DateManagers.getDate();
        String saveDate = SPManager.getString(mApplicationContext, MobonKey.URL_LIST_DOWNLOAD_SAVE_DATE);

        if ("".equals(saveDate) || !today.equals(saveDate)) {

            onConnectGetAUID_API();
            LogPrint.d("onConnectSDKUrlAPI 호출");
            if (!MobonUtils.isConnectNetwork(mApplicationContext))
                return;

            final Map<String, String> params = new HashMap<String, String>();
            params.put("id", SPManager.getString(mApplicationContext, MobonKey.META_DATA_MEDIA_CODE));
            LogPrint.d("API_SDK_INFO :: " + MobonUrl.DOMAIN_PROTOCOL + MobonUrl.API_SDK_INFO + "mbot.json");
            MobonHttpService.get(MobonUrl.DOMAIN_PROTOCOL + MobonUrl.API_SDK_INFO + "mbot.json", null).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, MobonResponse mobonResponse) throws IOException {
                    if (mobonResponse != null && mobonResponse.isSuccessful() && mobonResponse.body() != null) {
                        try {
                            String body = mobonResponse.body().string();
                            LogPrint.d("API_SDK_INFO result :: " + body);
                            if (TextUtils.isEmpty(body))
                                return;

                            JSONObject obj = new JSONObject(body);
                            if (obj.getInt("resultCode") != 200)
                                return;

                            MobonSdkUrlInfoJson type = new MobonSdkUrlInfoJson(obj.getString("result"));

                            //SPManager.setString(mContext.getApplicationContext(), MobonKey.VIEW_TYPE_RF, type.rfUrl);
                            SPManager.setString(mApplicationContext, MobonKey.MOBON_MEDIA_ENDING_S_VALUE, type.ending_s);
                            SPManager.setString(mApplicationContext, MobonKey.MOBON_MEDIA_INTRO_S_VALUE, type.intro_s);
                            SPManager.setString(mApplicationContext, MobonKey.MOBON_MEDIA_BANNER_S_VALUE, type.banner_s);
                            SPManager.setString(mApplicationContext, MobonKey.MOBON_MEDIA_JSONDATA_S_VALUE, type.json1_s);
                            SPManager.setString(mApplicationContext, MobonKey.MOBON_MEDIA_JSONDATA_S2_VALUE, type.json2_s);
                            SPManager.setString(mApplicationContext, MobonKey.MOBON_MEDIA_JSONDATA_S3_VALUE, type.json3_s);
                            SPManager.setString(mApplicationContext, MobonKey.MOBON_MEDIA_JSONDATA_S4_VALUE, type.json4_s); //bacon
                            SPManager.setString(mApplicationContext, MobonKey.MOBON_MEDIA_MCOVER_S_VALUE, type.mcover_s);
                            SPManager.setInteger(mApplicationContext, MobonKey.MOBON_MEDIA_MCOVER_FQ_VALUE, type.mcover_fq);
                            SPManager.setInteger(mApplicationContext, MobonKey.MOBON_MEDIA_AUID_FQ_VALUE, type.auid_fq);
                            SPManager.setString(mApplicationContext, MobonKey.MOBON_MEDIA_CROSS_BROWSER_VALUE, type.cross_browser);
                            SPManager.setString(mApplicationContext, MobonKey.URL_LIST_DOWNLOAD_SAVE_DATE, today);
                            if (!TextUtils.isEmpty(type.domain)) {
                                Uri uri = Uri.parse(type.domain);
                                String key = uri.getQueryParameter("key");
                                if (!TextUtils.isEmpty(key))
                                    SPManager.setString(mApplicationContext, MobonKey.MOBON_API_KEY, key);

                                String refresh = uri.getQueryParameter("refresh");
                                if (!TextUtils.isEmpty(refresh))
                                    SPManager.setBoolean(mApplicationContext, MobonKey.MOBON_AUID_REFRESH, refresh.equals("Y") ? true : false);

                                String crossYn = uri.getQueryParameter("crossYn");
                                if (!TextUtils.isEmpty(crossYn))
                                    SPManager.setString(mApplicationContext, MobonKey.MOBON_MEDIA_CROSS_BROWSER_VALUE, crossYn);

                                String baconPeriod = uri.getQueryParameter("baconPeriod");
                                if (!TextUtils.isEmpty(baconPeriod) && MobonUtils.isNumber(baconPeriod))
                                    SPManager.setInteger(mApplicationContext, MobonKey.MOBON_MEDIA_BACON_PERIOD_VALUE, Integer.parseInt(baconPeriod));
                                else
                                    SPManager.setInteger(mApplicationContext, MobonKey.MOBON_MEDIA_BACON_PERIOD_VALUE, 60);
                            }


                        } catch (Exception e) {
                            LogPrint.d("onConnectSDKUrlAPI ERROR1 error => " + e.toString());
                        }


                    } else {
                        LogPrint.d("onConnectSDKUrlAPI ERROR2 error => " + mobonResponse.message());
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    LogPrint.d("onConnectSDKUrlAPI ERROR3 error => " + e.toString());
                }

            });


        } else {
            LogPrint.d("금일 url 업데이트는 완료됨.");
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onConnectGetAUID_API();
            }
        }, 100);


    }

    private void onConnectMobonAdData(final Context pContext, final int _adCount, final String _UnitId, iMobonSDKAdCallback pMobonAdCallback) {

        if (TextUtils.isEmpty(_UnitId)) {
            if (pMobonAdCallback != null)
                pMobonAdCallback.onLoadedMobonAdData(false, null, "Empty UnitId");
            return;
        }

        if (!MobonUtils.isNumber(_UnitId)) {
            if (pMobonAdCallback != null)
                pMobonAdCallback.onLoadedMobonAdData(false, null, "Wrong UnitId");
            return;
        }

        mUnitId = _UnitId;

        boolean isPlural = _adCount > 1 ? true : false;
        String count = isPlural ? String.valueOf(_adCount) : "5";

        final Map<String, String> params = MobonUtils.getDefaultParams(mApplicationContext);
        params.put("s", _UnitId);
        params.put("cntsr", count);
        params.put("cntad", count);
        LogPrint.d("ad call unitId :: " + _UnitId);

        getMobonAdList(MobonUrl.DOMAIN_PROTOCOL + MobonUrl.API_MOBILE_BANNER, _UnitId, params, isPlural, pMobonAdCallback);
    }


    public void getMobonAdData(final Context pContext, final int _adCount, final String _unitId, iMobonSDKAdCallback pMobonAdCallback) {
        onConnectMobonAdData(pContext, _adCount, _unitId, pMobonAdCallback);
    }

    public void setOrderBrowser(List<String> _browserList) {
        //   String listString = String.join("||", new ArrayList<String>(_browserList));

        StringBuilder listString = new StringBuilder();
        for (String s : _browserList) {
            if (!TextUtils.isEmpty(s))
                listString.append("||").append(s);
        }

        SPManager.setString(mApplicationContext, MobonKey.BROWSER_ORDER_DATA, listString.toString());
    }


    private void setMetaCode(Context context, String _mediaCode) {
        String mediaCode = TextUtils.isEmpty(_mediaCode) ? MobonUtils.getMetaData(context, MobonKey.META_DATA_MEDIA_CODE) : _mediaCode;
        LogPrint.d("Meta data code " + mediaCode);
        if (!TextUtils.isEmpty(mediaCode)) {
            if (mediaCode.startsWith("YOUR"))
                SPManager.setString(context, MobonKey.META_DATA_MEDIA_CODE, "mbot");
            else
                SPManager.setString(context, MobonKey.META_DATA_MEDIA_CODE, String.valueOf(mediaCode));
        } else {
            throw new IllegalArgumentException("empty _mediaCode");
        }


        if ("".equals(SPManager.getString(context, MobonKey.CHILD_PACKAGE_NAME)))
            SPManager.setString(context, MobonKey.CHILD_PACKAGE_NAME, context.getPackageName());


        synchronized (context) {
            onCreate();
        }
    }

    public void onDestroy() {
        try {

            mInstance = null;
            mApplicationContext = null;
        } catch (Exception e) {
            LogPrint.e("onDestroy() 예외 발생! " + e.getLocalizedMessage());
        }
    }

    /**
     * ending popup cancelable 설정.
     */
    public void setEndingCancelable(boolean _is) {
        //mContext = contextWeakReference.get();
        if (mApplicationContext == null) return;

        String packageName = SPManager.getString(mApplicationContext, MobonKey.CHILD_PACKAGE_NAME);
        SPManager.setBoolean(mApplicationContext, packageName, MobonKey.ENDING_POPUP_CANCELABLE, _is);
    }


    private final AtomicInteger EndingRetryCount = new AtomicInteger(0);

    private void getMobonAdList(String url, String s, Map<String, String> params, final boolean isList, final iMobonSDKAdCallback pMobonAdCallback) {
        // mContext = contextWeakReference.get();
        if (mApplicationContext == null) return;
        if (!MobonUtils.isConnectNetwork(mApplicationContext)) {
            if (pMobonAdCallback != null)
                pMobonAdCallback.onLoadedMobonAdData(false, null, "network error");
            return;
        }


        MobonUtils.getMobonAdData(mApplicationContext, s, params, isList, mImageLimit, true, new iCommonMobonAdCallback() {
            @Override
            public void onLoadedMobonAdData(boolean result, final JSONObject data, String errorStr) {
                if (result) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (!isList) {
                                try {
                                    JSONObject jObj = data.getJSONArray("client").getJSONObject(0);
                                    JSONArray jArray = jObj.getJSONArray("data");
                                    JSONObject lobj = jArray.getJSONObject(0);
                                    mViewMobonKey = mAdGubun = "";
                                    mAdGubun = jObj.optString("target");
                                    mViewMobonKey = lobj.optString("increaseViewMobonKey");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            if (pMobonAdCallback != null)
                                pMobonAdCallback.onLoadedMobonAdData(true, data, "");


                        }
                    });

                } else {
                    if (pMobonAdCallback != null)
                        pMobonAdCallback.onLoadedMobonAdData(false, null, errorStr);
                }
            }
        });


    }


    protected void onConnectGetAUID_API() {
        LogPrint.d("onConnectGetAUID_API");
        if (TextUtils.isEmpty(SPManager.getString(mApplicationContext, MobonKey.ADID))) {
            LogPrint.d("onConnectGetAUID_API adid empty");
            MobonUtils.getAdId(mContext, MobonSimpleSDK.this);
            return;
        }

        String prev_Time = SPManager.getString(mApplicationContext, MobonKey.AUID_GET_TIME);

        if (TextUtils.isEmpty(getAUID()))
            prev_Time = "";

        if (!TextUtils.isEmpty(prev_Time)) {
            try {
                long prev_TimeInMillis = Long.parseLong(prev_Time);
                int auid_fq = SPManager.getInteger(mApplicationContext, MobonKey.MOBON_MEDIA_AUID_FQ_VALUE);
                if (auid_fq < 1)
                    auid_fq = 7;

                if (SPManager.getBoolean(mApplicationContext, MobonKey.MOBON_AUID_REFRESH))
                    auid_fq = 1;

                if (System.currentTimeMillis() - prev_TimeInMillis < auid_fq * 24 * 3600000) {
                    return;
                }
            } catch (Exception e) {
                SPManager.setString(mApplicationContext, MobonKey.AUID_GET_TIME, "0");
                e.printStackTrace();
            }
        }

        LogPrint.d("onConnectGetAUID_API 호출 adid: " + SPManager.getString(mApplicationContext, MobonKey.ADID));

        new Thread() {
            public void run() {
                try {
                    if (!TextUtils.isEmpty(SPManager.getString(mApplicationContext, MobonKey.MOBON_API_KEY))) {
                        LogPrint.d("mobon api key is not empty");
                        try {

                            final String encrypt = AESCipher.AES_Encode("adid=" + SPManager.getString(mApplicationContext, MobonKey.ADID), SPManager.getString(mApplicationContext, MobonKey.MOBON_API_KEY));
                            final Map<String, String> params = new HashMap<String, String>();
                            params.put("mcid", SPManager.getString(mContext, MobonKey.META_DATA_MEDIA_CODE));
                            params.put("mb_secret", encrypt);
                            LogPrint.d("mcid:: " + SPManager.getString(mContext, MobonKey.META_DATA_MEDIA_CODE));
                            LogPrint.d("mb_secret:: " + encrypt);
                            MobonHttpService.get(mApplicationContext, MobonUrl.DOMAIN_PROTOCOL + MobonUrl.API_AUID, params).enqueue(new Callback() {
                                @Override
                                public void onResponse(Call call, MobonResponse mobonResponse) throws IOException {
                                    if (mobonResponse != null && mobonResponse.isSuccessful() && mobonResponse.body() != null) {
                                        String body = mobonResponse.body().string();
                                        try {

                                            JSONObject str = new JSONObject(body);
                                            String auid = str.getJSONObject("data").getString("au_id");

                                            if (!TextUtils.isEmpty(auid)) {
                                                SPManager.setString(mApplicationContext, MobonKey.AUID, auid);
                                                SPManager.setString(mApplicationContext, MobonKey.AUID_GET_TIME, String.valueOf(System.currentTimeMillis()));
                                                LogPrint.d("get auid :::" + auid);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        LogPrint.d("onConnectGetAUID_API ERROR error => " + mobonResponse.message());
                                    }
                                }

                                @Override
                                public void onFailure(Call call, IOException e) {
                                    LogPrint.d("onConnectGetAUID_API ERROR error => " + e.toString());
                                }

                            });

                        } catch (Exception e) {
                            LogPrint.d(e.toString());
                        } finally {

                        }
                    } else
                        LogPrint.d("mobon secret key empty!!!");


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }


    public String getAUID() {
        return TextUtils.isEmpty(SPManager.getString(mApplicationContext, MobonKey.AUID)) ? "" : SPManager.getString(mApplicationContext, MobonKey.AUID);
    }

//    public void onImpression() {
//        if (!TextUtils.isEmpty(mViewMobonKey))
//            onImpression(mViewMobonKey);
//    }

    private ArrayList<String> impressionMobonKeyList = new ArrayList<>();
    private boolean MobonKeyLock;

    synchronized public void onImpression(final String _viewMobonKey) {
        //   setImpressionMobonKey(_viewMobonKey);
//        if (MobonKeyLock)
//            return;
        //      final String MobonKey = getImpressionMobonKey();

        if (TextUtils.isEmpty(_viewMobonKey))
            return;

        String[] viewMobonKey = _viewMobonKey.split("&&&");
        String increaseViewMobonKey = viewMobonKey[0];
        String s_code = mUnitId;
        if (viewMobonKey.length > 1)
            s_code = viewMobonKey[1];

        if (TextUtils.isEmpty(increaseViewMobonKey) || TextUtils.isEmpty(s_code))
            return;

        Map<String, String> params = new HashMap<String, String>();
        params.put("adGubun", TextUtils.isEmpty(mAdGubun) ? "AD" : mAdGubun);
        params.put("productType", "banner");
        params.put("info", increaseViewMobonKey + "-1-1-1");
        MobonKeyLock = true;
        MobonHttpService.post(MobonUrl.DOMAIN_PROTOCOL + MobonUrl.API_AD_IMPRESSION + s_code + "/VIEW", params, "json").enqueue(new Callback() {
            @Override
            public void onResponse(Call call, MobonResponse mobonResponse) throws IOException {
                MobonKeyLock = false;
                //   onImpression("");
                if (mobonResponse != null && mobonResponse.isSuccessful() && mobonResponse.body() != null) {
                    String body = mobonResponse.body().string();
                    LogPrint.d("ad_json_impression_data : " + body + " : " + _viewMobonKey);
                } else {
                    LogPrint.d("ERROR => " + mobonResponse.message());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                MobonKeyLock = false;
                //   onImpression("");
                LogPrint.d("ERROR => " + e.toString());
            }
        });
    }

    private void setImpressionMobonKey(String MobonKey) {
        if (impressionMobonKeyList != null && !TextUtils.isEmpty(MobonKey))
            impressionMobonKeyList.add(MobonKey);
    }

    private String getImpressionMobonKey() {
        return (impressionMobonKeyList != null && impressionMobonKeyList.size() > 0) ? impressionMobonKeyList.remove(0) : null;
    }


}
