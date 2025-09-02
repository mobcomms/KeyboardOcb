package com.enliple.keyboard.mobonAD;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.enliple.httpmodule.Call;
import com.enliple.httpmodule.Callback;
import com.enliple.httpmodule.MobonResponse;
import com.enliple.keyboard.activity.RewardAdWebViewActivity;
import com.enliple.keyboard.activity.SharedPreference;
import com.enliple.keyboard.mobonAD.graphic.Palette;
import com.enliple.keyboard.mobonAD.manager.SPManager;
import com.enliple.keyboard.network.MobonHttpService;
import com.enliple.keyboard.ui.common.Key;
import com.enliple.keyboard.ui.common.LogPrint;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.util.IOUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * import com.enliple.httpmodule.Call;
 * import com.enliple.httpmodule.Callback;
 * import com.enliple.httpmodule.MobonResponse;
 * import com.enliple.mobonAD.graphic.Palette;
 * import com.enliple.mobonAD.manager.AESCipher;
 * import com.enliple.mobonAD.manager.SPManager;
 * import com.enliple.network.MobonHttpService;
 * import com.enliple.ui.common.LogPrint;
 **/

public class MobonUtils {

    public static final String getMetaData(Context context, String metadataKey) {
        String key = "";
        ApplicationInfo ai = null;
        try {
            LogPrint.d(context.getPackageName());
            ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (ai != null) {
                Bundle bundle = ai.metaData;
                if (bundle != null)
                    key = bundle.getString(metadataKey);
            }
        } catch (NameNotFoundException e) {
            LogPrint.e("getMetaData() Exception! : " + e.getLocalizedMessage());
            // e.toString();
        }

        return key;
    }

    public static String getAppVersion(Context _context) {
        try {
            PackageInfo packageInfo = _context.getPackageManager().getPackageInfo(_context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (NameNotFoundException e) {
            return null;
        }
    }

    public static String getAdid(Context _context) {
        return SPManager.getString(_context, MobonKey.ADID);
    }

    public static void getADID(Context context) {
        String adid = "";
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    AdvertisingIdClient.Info adInfo;
                    adInfo = null;
                    if (context != null) {
                        adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
                        if (adInfo != null && !adInfo.isLimitAdTrackingEnabled()) {
                            final String adid = adInfo.getId();
                            if (TextUtils.isEmpty(adid) || adid.startsWith("00")) {
                                SPManager.setString(context, MobonKey.ADID, getUUID(context));
                            } else
                                SPManager.setString(context, MobonKey.ADID, adid);
                        } else {
                            if (TextUtils.isEmpty(SPManager.getString(context, MobonKey.ADID))) {
                                SPManager.setString(context, MobonKey.ADID, getUUID(context));
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    SPManager.setString(context, MobonKey.ADID, getUUID(context));
                }
            }
        });
    }

    public static void getAdId(final Context context, final MobonSimpleSDK mobonSDK) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    AdvertisingIdClient.Info adInfo;
                    adInfo = null;
                    if (context != null) {
                        adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
                        if (adInfo != null && !adInfo.isLimitAdTrackingEnabled()) {
                            final String adid = adInfo.getId();
                            if (TextUtils.isEmpty(adid) || adid.startsWith("00")) {
                                LogPrint.d("adid set 1");
                                SPManager.setString(context, MobonKey.ADID, getUUID(context));
                            } else {
                                LogPrint.d("adid set 2");
                                SPManager.setString(context, MobonKey.ADID, adid);
                            }
                        } else {
                            if (TextUtils.isEmpty(SPManager.getString(context, MobonKey.ADID))) {
                                LogPrint.d("adid set 3");
                                SPManager.setString(context, MobonKey.ADID, getUUID(context));
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogPrint.d("adid set 4");
                    SPManager.setString(context, MobonKey.ADID, getUUID(context));
                } finally {
                mobonSDK.onConnectGetAUID_API();
                }
            }
        });
    }



    private static String getUUID(Context context) {

        String uuid = SPManager.getString(context, MobonKey.UUID);

        if (TextUtils.isEmpty(uuid)) {
            UUID id = UUID.randomUUID();
            uuid = id != null ? id.toString() : "";
        }
        SPManager.setString(context, MobonKey.UUID, uuid);
        return uuid;
    }


    public static Map<String, String> getDefaultParams(Context _context) {

        String today = DateManagers.getDate();
        String saveDate = SPManager.getString(_context, MobonKey.RUNNING_SAVE_DATE);
        if ("".equals(saveDate) || !today.equals(saveDate)) {
            SPManager.setString(_context, MobonKey.AT_FREQUENCY_DATA, "");
            SPManager.setString(_context, MobonKey.RUNNING_SAVE_DATE, today);
        }

        Map<String, String> params = new HashMap<String, String>();

        params.put("sdk_version", "1");
        params.put("fbAt", getAtFrequency(_context));
        params.put("us", SPManager.getString(_context, MobonKey.MOBON_MEDIA_US_VALUE));
        params.put("bntype", "99");
        params.put("cntsr", "1");
        params.put("cntad", "1");
        params.put("au_id", SPManager.getString(_context, MobonKey.AUID));
        params.put("deviceInfo", "android");
        params.put("increaseViewCnt", "false");
        if (MobonKey.isMediation)
            params.put("sdkYn", "true");

        return params;
    }

    public static void setWebViewOpen(Context _context, String url, boolean isBacon) {
        LogPrint.d("kksskk setWebViewOpen url 1 :: " + url);
        String scForPoint = getParameter(url, "sc");
        LogPrint.d("ScForPoint :: " + scForPoint);
        try {

            //   url = "https://mbris.loobig.co.kr/mediaCategory/externalcontent/mobLanding.html?siteUrl=" + URLEncoder.encode(url);
            if (!isBacon) {
                Uri uri = Uri.parse(url);
                String auid = uri.getQueryParameter("au_id");
                if (TextUtils.isEmpty(auid)) {
                    url = url.replace("&au_id=", "");
                    Uri builtUri =
                            Uri.parse(url).buildUpon()
                                    .appendQueryParameter("au_id", SPManager.getString(_context, MobonKey.AUID)).build();
                    url = builtUri.toString();
                }
            }
            LogPrint.d("url :: " + url);
            //url = "https://www.mediacategory.com/servlet/drc?no=2169&kno=2169&kwrdSeq=0&s=551812&adgubun=FR&gb=FR&sc=c45055bea04ee65befca19b2e606b8ac&mc=551812&userid=coupang1&u=coupang1&product=mbw&slink=https%253A%252F%252Flink.coupang.com%252Fre%252FDA0005%253FpageKey%253D7527%2526src%253D1019097%2526spec%253D10999004%2526lptag%253DMobon_GB_210201_m&freqLog=1&viewTime=_551812_1657689401886_5446&hId=13&subadgubun=FR&abt=BA98|B24|AT012|C45|P80|H03|R43|AL57|BD27|D70|BJ61|K94|AW88|A19|N54|AZ040|Y31|AI01|TB13|AA06|AF01|AC05|BI10|AD02|AE65&salePriceYn=N&targetingTime=20220706123937&mobImgCode=00&mobon_ad_grp_id_i=22767&ad_grp_tp_code_i=01&mobon_ad_grp_id_c=9870&ad_grp_tp_code_c=02&cp_tp_code=99&frameMatrExposureYN=N&au_id=2698e1711d2e999c-551aad1317c97cfa1bc7daf";
            Intent intent = new Intent(_context, RewardAdWebViewActivity.class);
            intent.putExtra("reward_link", url);
            intent.putExtra("sc", scForPoint);
            intent.putExtra("isNews", false);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean getBrowserPackageName(Context _context, String url, boolean isBacon) {
        if (_context == null)
            return false;
        try {

            //   url = "https://mbris.loobig.co.kr/mediaCategory/externalcontent/mobLanding.html?siteUrl=" + URLEncoder.encode(url);
            String default_pkg = "";
            if (!isBacon) {
                Uri uri = Uri.parse(url);
                String auid = uri.getQueryParameter("au_id");
                if (TextUtils.isEmpty(auid)) {
                    url = url.replace("&au_id=", "");
                    Uri builtUri =
                            Uri.parse(url).buildUpon()
                                    .appendQueryParameter("au_id", SPManager.getString(_context, MobonKey.AUID)).build();
                    url = builtUri.toString();
                }
            }

            ArrayList<String> pkgs = new ArrayList<>();
            String orderData = SPManager.getString(_context, MobonKey.BROWSER_ORDER_DATA);
            if (TextUtils.isEmpty(orderData)) {
                String cross = SPManager.getString(_context, MobonKey.MOBON_MEDIA_CROSS_BROWSER_VALUE); //y 일경우 실행 브라우저 순으로 ...


                if (TextUtils.equals(cross, "y") || TextUtils.equals(cross, "Y")) {
                    pkgs = new ArrayList<String>(Arrays.asList("com.nhn.android.search", "com.android.chrome", "com.sec.android.app.sbrowser", "net.daum.android.daum"));

                    if (!SPManager.getBoolean(_context, MobonKey.NAVER_BROWSER_RUN) && isPackageInstalled(pkgs.get(0), _context)) {
                        default_pkg = pkgs.get(0);
                        SPManager.setBoolean(_context, MobonKey.NAVER_BROWSER_RUN, true);
                    } else if (!SPManager.getBoolean(_context, MobonKey.CHROME_BROWSER_RUN) && isPackageInstalled(pkgs.get(1), _context)) {
                        default_pkg = pkgs.get(1);
                        SPManager.setBoolean(_context, MobonKey.CHROME_BROWSER_RUN, true);
                    } else if (!SPManager.getBoolean(_context, MobonKey.SAMSUNG_BROWSER_RUN) && isPackageInstalled(pkgs.get(2), _context)) {
                        default_pkg = pkgs.get(2);
                        SPManager.setBoolean(_context, MobonKey.SAMSUNG_BROWSER_RUN, true);
                    } else if (!SPManager.getBoolean(_context, MobonKey.DAUM_BROWSER_RUN) && isPackageInstalled(pkgs.get(3), _context)) {
                        default_pkg = pkgs.get(3);
                        SPManager.setBoolean(_context, MobonKey.DAUM_BROWSER_RUN, true);
                    } else {
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
                        PackageManager pm = _context.getPackageManager();
                        final ResolveInfo mInfo = pm.resolveActivity(i, 0);
                        if (mInfo != null && mInfo.activityInfo != null && mInfo.activityInfo.applicationInfo != null && !TextUtils.equals(mInfo.activityInfo.applicationInfo.packageName, "android")) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            browserIntent.setPackage(mInfo.activityInfo.applicationInfo.packageName);
                            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            _context.startActivity(browserIntent);
                            return true;
                        } else {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(url));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            _context.startActivity(intent);
                            return true;
                        }
                    }
                    pkgs = null;
                } else {
                    SPManager.setBoolean(_context, MobonKey.NAVER_BROWSER_RUN, false);
                    SPManager.setBoolean(_context, MobonKey.CHROME_BROWSER_RUN, false);
                    SPManager.setBoolean(_context, MobonKey.SAMSUNG_BROWSER_RUN, false);
                    SPManager.setBoolean(_context, MobonKey.DAUM_BROWSER_RUN, false);

                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
                    PackageManager pm = _context.getPackageManager();
                    final ResolveInfo mInfo = pm.resolveActivity(i, 0);
                    if (mInfo != null && mInfo.activityInfo != null && mInfo.activityInfo.applicationInfo != null && !TextUtils.equals(mInfo.activityInfo.applicationInfo.packageName, "android")) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        browserIntent.setPackage(mInfo.activityInfo.applicationInfo.packageName);
                        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        _context.startActivity(browserIntent);
                        return true;
                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        _context.startActivity(intent);
                        return true;
                    }
                }
            } else
                pkgs = new ArrayList<String>(Arrays.asList(orderData.split("\\|\\|")));

            if (TextUtils.isEmpty(default_pkg) && pkgs != null) {
                for (String pkgName : pkgs) {
                    if (isPackageInstalled(pkgName, _context)) {
                        default_pkg = pkgName;
                        break;
                    }
                }
            }

            LogPrint.d("getBrowserPackageName :: " + default_pkg);

            if (TextUtils.isEmpty(default_pkg) || default_pkg.equalsIgnoreCase("android")) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                _context.startActivity(intent);
            } else {
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    browserIntent.setPackage(default_pkg);
                    browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    _context.startActivity(browserIntent);
                    return true;
                } catch (Exception e) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    _context.startActivity(intent);
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isPackageInstalled(String packagename, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }


    public static String getAtFrequency(Context _context) {
        String fData = SPManager.getString(_context, MobonKey.AT_FREQUENCY_DATA);

        JSONObject obj = null;
        if (TextUtils.isEmpty(fData))
            return "";

        try {
            obj = new JSONObject(fData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }


    public static void setApFrequency(Context _context, String _siteCode) {

        Map<String, Integer> frequencyList = new HashMap<>();
        String fData = SPManager.getString(_context, MobonKey.AT_FREQUENCY_DATA);
        JSONObject obj = null;

        try {
            if (TextUtils.isEmpty(fData))
                obj = new JSONObject();
            else
                obj = new JSONObject(fData);

            Iterator i = obj.keys();
            while (i.hasNext()) {
                String b = i.next().toString();
                frequencyList.put(b, obj.getInt(b));
            }

            if (frequencyList.containsKey(_siteCode)) {
                int count = frequencyList.get(_siteCode) + 1;
                frequencyList.put(_siteCode, count);
            } else
                frequencyList.put(_siteCode, 1);

            for (Map.Entry<String, Integer> entry : frequencyList.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                obj.put(key, value);
            }

            SPManager.setString(_context, MobonKey.AT_FREQUENCY_DATA, obj.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    synchronized public static void sendAdImpression(final Context context, final String _url, final String _viewKey, final String _adGubun, final int _count) {

        String filterKey = _viewKey.contains("&&&") ? _viewKey.substring(0, _viewKey.lastIndexOf("&&&")) : _viewKey;
        Map<String, String> params = new HashMap<String, String>();
        params.put("adGubun", _adGubun);
        params.put("productType", "banner");
        params.put("info", filterKey + "-1-1-1");

        try {
            MobonHttpService.post(MobonUrl.DOMAIN_PROTOCOL + _url, params, "json").enqueue(new Callback() {
                @Override
                public void onResponse(Call call, MobonResponse mobonResponse) throws IOException {
                    if (mobonResponse != null && mobonResponse.isSuccessful() && mobonResponse.body() != null) {
                        String body = mobonResponse.body().string();
                        mobonResponse.close();
                    } else {
                        LogPrint.d("ERROR => " + mobonResponse.message());
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    LogPrint.d("ERROR => " + e.toString());
                }
            });
        } catch (Exception e) {
        }
    }


    public static String getParameter(String url, String _param) {
        Uri uri = Uri.parse(url);
        String paramValue = uri.getQueryParameter(_param);
        return paramValue;
    }




    public static String getCommaNumeric(String str) {
        if (str.equals(""))
            return "";

        long value = 0;

        try {
            value = Long.parseLong(str);
        } catch (Exception e) {
            return str;
        }

        DecimalFormat format = new DecimalFormat("###,###");
        return format.format(value);
    }

    public static String getHtml(String _url) {
        String Html = "";

        HttpURLConnection con = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        try {
            URL url = new URL(_url);
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            isr = new InputStreamReader(con.getInputStream());
            br = new BufferedReader(isr);

            String str = null;
            while ((str = br.readLine()) != null) {
                Html += str + "\n";
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.disconnect();
                } catch (Exception e) {
                }
            }

            if (isr != null) {
                try {
                    isr.close();
                } catch (Exception e) {
                }
            }

            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                }
            }
        }
        return Html;
    }

    protected static void getMobonAdData(final Context context, final String s, Map<String, String> params, final boolean isList, final int limit, final boolean isJsonData, final iCommonMobonAdCallback callback) {
        if (TextUtils.isEmpty(s)) {
            if (callback != null)
                callback.onLoadedMobonAdData(false, null, "Ad UnitId Empty");
            return;
        } else {

        }

        MobonHttpService.post(MobonUrl.DOMAIN_PROTOCOL + MobonUrl.API_MOBILE_BANNER + "?s=" + s, params, "urlencoded").enqueue(new Callback() {
            @Override
            public void onResponse(Call call, MobonResponse mobonResponse) throws IOException {
                if (mobonResponse != null && mobonResponse.isSuccessful() && mobonResponse.body() != null) {
                    final String body = mobonResponse.body().string();
                    LogPrint.d("keyboardad mobon getMobonAdData data : " + body);
                    mobonResponse.close();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            processAdData(context, body, s, isList, limit, isJsonData, callback);
                        }
                    });
                } else {
                    LogPrint.d("getMobonAdData data111 : " + mobonResponse.message());
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null)
                                callback.onLoadedMobonAdData(false, null, "NoConnectNetwork");
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, final IOException e) {
                LogPrint.d("API_MOBILE_BANNER ERROR error => " + e.toString());
                final String mediationData = SPManager.getString(context, MobonKey.MEDIATION_INFO_ARRAY + s);
                if (!TextUtils.isEmpty(mediationData)) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            processAdData(context, mediationData, s, isList, limit, isJsonData, callback);
                        }
                    });
                } else {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onLoadedMobonAdData(false, null, e.toString());
                        }
                    });
                }
            }
        });
    }


    private static void processAdData(final Context context, String body, final String s, final boolean isList, final int limit, final boolean isJsonData, final iCommonMobonAdCallback callback) {
        try {
            if (TextUtils.isEmpty(body)) {
                String mediationData = SPManager.getString(context, MobonKey.MEDIATION_INFO_ARRAY + s);
                if (!TextUtils.isEmpty(mediationData)) {
                    body = mediationData;
                } else {
                    if (callback != null)
                        callback.onLoadedMobonAdData(false, null, "WARNING!!! Empty data.. ");
                    return;
                }
            }

            //  body = "{\"list\":[{\"name\":\"mbmixadapter\",\"unitid\":\"52_320_50\",\"adtype\":\"banner_250_250\",\"mediaKey\":\"52_320_50\"},{\"name\":\"mobon\",\"unitid\":\"460329\",\"adtype\":\"banner_250_250\",\"data\":{\"client\":[{\"target\":\"AD\",\"length\":1,\"bntype\":\"99\",\"mobonLogo\":\"//img.mobon.net/newAd/img/logoImg/mobonLogo02.png\",\"mobonInfo\":\"//img.mobon.net/ad/linfo.php\",\"data\":[{\"telPurl\":\"\",\"t0\":\"\",\"pcode\":\"\",\"pnm\":\"\",\"site_name\":\"\",\"site_title\":\"\",\"price\":\"\",\"img\":\"http://img.mobon.net/servlet/image/otjalnam/250x250.jpg\",\"purl\":\"/servlet/drc?no=0&kno=0&kwrdSeq=0&s=460329&adgubun=AD&gb=AD&sc=5da193d328913cbfee927fedd5925727&mc=460329&userid=123&u=123&product=mbw&viewTime=_460329_1605060138219_7188&hId=21&salePriceYn=N\",\"logo\":\"\",\"logo2\":\"\",\"img_logo\":\"\",\"schon_logo\":\"\",\"desc\":\"옷잘남-멋진 남자들의 필수 앱, 남자쇼핑몰 모음\",\"desc_web\":\"옷잘남-멋진 남자들의 필수 앱, 남자쇼핑몰 모음\",\"site_url\":\"http://www.otjalnam.com/\",\"site_desc1\":\"\",\"site_desc2\":\"\",\"site_desc3\":\"\",\"site_desc4\":\"\",\"icon19\":\"\",\"icon20\":\"\",\"imageSqreYn\":\"N\",\"advrtsReplcNm\":\"\",\"advrtsReplcCode\":\"01\",\"point\":0,\"pSlink\":\"\",\"cta_text\":\"바로가기\",\"user_cate\":\"\",\"user_cate_nm\":\"\",\"remarks\":\"\",\"mimg_720_120\":\"http://img.mobon.net/servlet/image/otjalnam/250x250.jpg\",\"mimg_250_250\":\"http://img.mobon.net/servlet/image/otjalnam/250x250.jpg\",\"mimg_120_600\":\"http://img.mobon.net/servlet/image/otjalnam/250x250.jpg\",\"mimg_728_90\":\"http://img.mobon.net/servlet/image/otjalnam/250x250.jpg\",\"mimg_300_180\":\"http://img.mobon.net/servlet/image/otjalnam/250x250.jpg\",\"mimg_800_1500\":\"http://img.mobon.net/servlet/image/otjalnam/250x250.jpg\",\"mimg_160_300\":\"http://img.mobon.net/servlet/image/otjalnam/250x250.jpg\",\"mimg_300_65\":\"http://img.mobon.net/servlet/image/otjalnam/250x250.jpg\",\"mimg_850_800\":\"http://img.mobon.net/servlet/image/otjalnam/250x250.jpg\",\"mimg_960_100\":\"http://img.mobon.net/servlet/image/otjalnam/250x250.jpg\",\"mimg_720_1230\":\"http://img.mobon.net/servlet/image/otjalnam/250x250.jpg\",\"mimg_160_600\":\"http://img.mobon.net/servlet/image/otjalnam/250x250.jpg\",\"mimg_640_350\":\"http://img.mobon.net/servlet/image/otjalnam/250x250.jpg\",\"mimg_300_250\":\"http://img.mobon.net/servlet/image/otjalnam/250x250.jpg\",\"mimg_320_100\":\"http://img.mobon.net/servlet/image/otjalnam/250x250.jpg\",\"mimg_300_300\":\"http://img.mobon.net/servlet/image/otjalnam/250x250.jpg\",\"increaseViewKey\":\"0#5da193d328913cbfee927fedd5925727#0\",\"increaseViewKeyForAPP\":\"0#5da193d328913cbfee927fedd5925727#0\"}]}]}}]}";


            body = body.replaceAll(":\"//img.mobon.net", ":\"" + MobonUrl.DOMAIN_PROTOCOL + "img.mobon.net");
            body = body.replaceAll("http://img.mobon.net", MobonUrl.DOMAIN_PROTOCOL + "img.mobon.net");
            body = body.replaceAll("https://img.mobon.net", MobonUrl.DOMAIN_PROTOCOL + "img.mobon.net");
            body = body.replace("\\/", "/");

            final JSONObject obj = new JSONObject(body);
            String auid = SPManager.getString(context, MobonKey.AUID);
            if (TextUtils.isEmpty(auid))
                auid = "";
            JSONObject jObj = null;
            JSONArray array = new JSONArray();
            if (MobonKey.isMediation) {

                JSONArray emptyArray = new JSONArray(); // 최후의 순간시 미디에이션을 돌리기 위해 모비온 제외한 json 값..

                array = obj.optJSONArray("list");
                if (array != null) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj1 = array.getJSONObject(i);
                        String name = obj1.optString("name");
                        if (TextUtils.equals(name, "mobon")) {
                            jObj = obj1.getJSONObject("data").getJSONArray("client").getJSONObject(0);
                        } else {
                            emptyArray.put(obj1);
                        }
                    }

                    if (emptyArray.length() > 0) {
                        JSONObject obj2 = new JSONObject();
                        obj2.put("list", emptyArray);
                        SPManager.setString(context, MobonKey.MEDIATION_INFO_ARRAY + s, obj2.toString());
                    }

                    if (jObj == null) {
                        if (callback != null)
                            callback.onLoadedMobonAdData(true, obj, "");

                        return;
                    }
                } else {
                    jObj = obj.getJSONArray("client").getJSONObject(0);
                }

            } else
                jObj = obj.getJSONArray("client").getJSONObject(0);

            JSONArray jArray = jObj.getJSONArray("data");
            JSONArray tempjArray = new JSONArray();
            String html = obj.optString("html");

            int length = jArray.length();
            String adGubun = jObj.optString("target");
            LogPrint.d("getMobonAdData AD Length : " + length);
            jArray = shuffleJsonArray(jArray);

            boolean isLimitImg_searching = true;
            for (int i = 0; i < length; i++) {
                JSONObject lobj = jArray.getJSONObject(i);
                final String drcUrl = lobj.optString("drcUrl");
                final String purl = lobj.optString("purl");
                final String site_code = MobonUtils.getParameter(purl, "sc");
                final String img = lobj.optString("img");
                final String increaseViewKey = TextUtils.isEmpty(lobj.optString("increaseViewKeyForAPP")) ? lobj.optString("increaseViewKey") : lobj.optString("increaseViewKeyForAPP");


                if (purl.contains("adgubun=AT") && !TextUtils.isEmpty(site_code) && isJsonData)
                    MobonUtils.setApFrequency(context, site_code);

                if (!TextUtils.isEmpty(drcUrl) && drcUrl.contains(MobonKey.EMPTY_AD_KEYWORD)) {
                    LogPrint.d("MobonAdListAPI house AD : " + drcUrl);
                    continue;
                }
                if (!TextUtils.isEmpty(purl) && purl.contains(MobonKey.EMPTY_AD_KEYWORD)) {
                    LogPrint.d("MobonAdListAPI house AD : " + purl);
                    continue;
                }

                if (MobonUrl.DOMAIN_PROTOCOL.startsWith("https") && !TextUtils.isEmpty(img) && img.startsWith("http://"))
                    lobj.put("img", "https://img.en-mobon.com/?src=" + img);

                if (limit > 0 && isLimitImg_searching) {
                    String[] imageUrls = {"mimg_720_1230", "mimg_800_1500"};

                    for (String imgVal : imageUrls) {
                        String imgUrl = lobj.optString(imgVal);
                        if (!TextUtils.isEmpty(imgUrl)) {
                            try {
                                URL url = new URL(imgUrl);
                                int size = IOUtils.toByteArray(url.openStream()).length;
                                if (limit * 1000 < size)
                                    lobj.put(imgVal, "http://img.mobon.net");
                                else if (size > 0) {
                                    if (!isList)
                                        isLimitImg_searching = false;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                continue;
                            }
                        }
                    }
                }

                if (!TextUtils.isEmpty(drcUrl)) {
                    if (drcUrl.startsWith("http"))
                        lobj.put("purl", drcUrl + "&au_id=" + auid);
                } else if (!TextUtils.isEmpty(purl)) {
                    if (purl.startsWith("http"))
                        lobj.put("purl", purl + "&au_id=" + auid);
                    else if (purl.startsWith("drc."))
                        lobj.put("purl", "https://" + purl + "&au_id=" + auid);
                    else
                        lobj.put("purl",
                                "https://" + MobonUrl.DOMAIN_ROOT + purl + "&au_id=" + auid);

                    Uri uri = Uri.parse(lobj.optString("purl"));
                    lobj.put("s", uri.getQueryParameter("s"));
                }

                if (!TextUtils.isEmpty(increaseViewKey))
                    lobj.put("increaseViewKey",
                            increaseViewKey + "&&&" + s);

                String logo = lobj.optString("logo");
                if (TextUtils.isEmpty(logo))
                    logo = lobj.optString("logo2");

                if (TextUtils.isEmpty(logo))
                    logo = lobj.optString("img");

                String price = lobj.optString("price");
                if (!TextUtils.isEmpty(price))
                    lobj.put("price",
                            MobonUtils.getCommaNumeric(price));

                lobj.put("logo",
                        MobonUrl.DOMAIN_PROTOCOL + MobonUrl.MOBON_IMAGE_URL + logo);
                lobj.put("logo2",
                        MobonUrl.DOMAIN_PROTOCOL + MobonUrl.MOBON_IMAGE_URL + lobj.get("logo2"));

                lobj.put("mobon_logo", MobonUrl.DOMAIN_PROTOCOL + "img.mobon.net/newAd/img/logoImg/mobonLogo02.png");
                lobj.put("link_txt", "바로가기");


                LogPrint.d("MobonAdListAPI edit purl : " + lobj.getString("purl"));
                tempjArray.put(lobj);
            }

            if (array == null && (tempjArray.length() < 1)) {
                LogPrint.d("MobonAdListAPI : " + tempjArray.toString());

                if (callback != null)
                    callback.onLoadedMobonAdData(false, null, MobonKey.NOFILL);

                return;
            }

            jArray = new JSONArray();
            jArray.put(isList ? tempjArray : tempjArray.length() > 0 ? tempjArray.get(0) : tempjArray);
            jObj.put("length", isList ? tempjArray.length() : tempjArray.length() > 0 ? 1 : 0);
            jObj.put("data", isList ? tempjArray : tempjArray.length() > 0 ? new JSONArray().put(tempjArray.get(0)) : tempjArray);
            JSONArray finalArray = new JSONArray().put(jObj);

            if (MobonKey.isMediation && isJsonData) {
                final JSONObject finalObj = new JSONObject();
                finalObj.put("client", finalArray);

                if (callback != null)
                    callback.onLoadedMobonAdData(true, finalObj, "");

                return;
            }
            obj.put("client", finalArray);
//                        if (!TextUtils.isEmpty(html))
//                            finalJson.put("html", html);

            //    final JSONObject obj3 = new JSONObject("{\"list\":[{\"name\":\"adfitsdk\",\"unitid\":\"DAN-1h7qzupqzgdie\",\"adtype\":\"banner_250_250\",\"mediaKey\":\"B-057619\"},{\"name\":\"criteosdk\",\"unitid\":\"1483763\",\"adtype\":\"banner_250_250\",\"mediaKey\":\"B-057619\"},{\"name\":\"mobon\",\"unitid\":\"53515\",\"adtype\":\"banner_250_250\",\"data\":{\"client\":[{\"target\":\"AD\",\"length\":0,\"bntype\":\"99\",\"mobonLogo\":\"http:\\/\\/img.mobon.net\\/newAd\\/img\\/logoImg\\/mobonLogo02.png\",\"mobonInfo\":\"http:\\/\\/img.mobon.net\\/ad\\/linfo.php\",\"adType\":\"A\",\"data\":[]}]}}],\"client\":[{\"target\":\"AD\",\"length\":0,\"bntype\":\"99\",\"mobonLogo\":\"http:\\/\\/img.mobon.net\\/newAd\\/img\\/logoImg\\/mobonLogo02.png\",\"mobonInfo\":\"http:\\/\\/img.mobon.net\\/ad\\/linfo.php\",\"adType\":\"A\",\"data\":[]}]}");


            LogPrint.d("final json : " + obj.toString());

            if (callback != null)
                callback.onLoadedMobonAdData(true, obj, "");


        } catch (final Exception e) {
            LogPrint.d("API_MOBILE_BANNER ERROR error => " + e.toString());

            if (callback != null)
                callback.onLoadedMobonAdData(false, null, e.toString());
        }
    }

    public static JSONArray shuffleJsonArray(JSONArray array) throws JSONException {
        // Implementing Fisher–Yates shuffle
        Random rnd = new Random();
        for (int i = array.length() - 1; i >= 0; i--) {
            int j = rnd.nextInt(i + 1);
            Object object = array.get(j);
            array.put(j, array.get(i));
            array.put(i, object);
        }
        return array;
    }

    public static boolean isNumber(String str_num) {
        try {
            double str = Double.parseDouble(str_num);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static String getManPlusParam(Context context) {
        String params;

        params = "&adid=" + SPManager.getString(context, MobonKey.ADID);
        params += "&osIndex=3";
        params += "&osVer=" + Build.VERSION.RELEASE;
        params += "&appId=" + context.getPackageName();
        params += "&appName=" + getApplicationName(context);
        return params;
    }

    public static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    public static int convertDpToPx(Context pContext, int pDp) {
        try {
            return ((int) (pDp * pContext.getResources().getDisplayMetrics().density));
        } catch (Exception e) {
            LogPrint.e("convertDpToPx() Exception! : " + e.getLocalizedMessage());
            return 0;
        }
    }

    public static int convertpxTodp(Context pContext, int pPx) {
        try {
            return ((int) (pPx / pContext.getResources().getDisplayMetrics().density));
        } catch (Exception e) {
            LogPrint.e("convertPxToDp() Exception! : " + e.getLocalizedMessage());
            return 0;
        }
    }


    public static boolean isConnectNetwork(Context context) {

        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }

        return false;
    }

    public static int setPalette(Bitmap bitmap) {
        int color = 0xFFFFFF; // default white

        if (bitmap == null)
            return color;

        Palette.Builder paletteBuilder = Palette.from(bitmap);

        Palette palette = paletteBuilder.generate();

        if (palette != null && palette.getLightVibrantSwatch() != null) {

            color = palette.getLightVibrantSwatch().getRgb();

        } else if (palette != null && palette.getLightMutedSwatch() != null) {

            color = palette.getLightMutedSwatch().getRgb();

        } else if (palette != null && palette.getDarkVibrantSwatch() != null) {

            color = palette.getDarkVibrantSwatch().getRgb();

        } else if (palette != null && palette.getDarkMutedSwatch() != null) {

            color = palette.getDarkMutedSwatch().getRgb();

        }

        return color;
    }

}
