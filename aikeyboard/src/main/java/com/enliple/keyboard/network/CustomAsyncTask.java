package com.enliple.keyboard.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.enliple.keyboard.Cipher.E_Cipher;
import com.enliple.keyboard.CoupangAdPopup;
import com.enliple.keyboard.activity.SharedPreference;
import com.enliple.keyboard.activity.SoftKeyboard;
import com.enliple.keyboard.common.Common;
import com.enliple.keyboard.common.KeyboardLogPrint;
import com.enliple.keyboard.common.KeyboardUserIdModel;
import com.enliple.keyboard.mobonAD.AESCipher;
import com.enliple.keyboard.mobonAD.MobonKey;
import com.enliple.keyboard.mobonAD.MobonSimpleSDK;
import com.enliple.keyboard.mobonAD.MobonUrl;
import com.enliple.keyboard.mobonAD.MobonUtils;
import com.enliple.keyboard.mobonAD.manager.SPManager;
import com.enliple.keyboard.models.OfferwallData;
import com.enliple.keyboard.models.OfferwallParticipationReq;
import com.enliple.keyboard.ui.common.Key;
import com.enliple.keyboard.ui.common.LogPrint;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

public class CustomAsyncTask {
    public static final int FORMISSION_PAGE_COUNT_20 = 20;
    public static final int FORMISSOIN_PAGE_COUNT_40 = 40;
    public static final String FORMISSION_MEDIA_ID = "okcashbag";
    public static final String FORMISSION_REFRESH_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2Njc5ODU3Njl9.NF3PTQocr-rBksmcZvp70ZnnqSMnCAhX-KHjinNJrgs";
    public static final String FORMISSION_APP_NAME = "OK캐시백";

    public static final String GUBUN_DEV = "DEV";
    public static final String GUBUN_ALPHA = "ALPHA";
    public static final String GUBUN_RELEASE = "RELEASE";
    public static String gubun = GUBUN_ALPHA;
    private final int MODE_IMAGE_SEARCH = 10;
    private final int MODE_EMOJI_MATCHING = 20;
    private final int MODE_KEYBOARD_AD = 30;
    private final int MODE_LIVE_CHECK = 40;
    private final int MODE_CHARGE_POINT = 50;
    private final int MODE_USER_MAX_POINT = 60;
    private final int MODE_GET_USER_POINT = 70;
    private final int MODE_QUICK_LINK = 80;
    private final int MODE_THEME_LIST = 90;
    private final int MODE_THEME_POPULAR = 91;
    private final int MODE_ZERO_POINT = 100;
    private final int MODE_SEND_KEYWORD = 110;
    private final int MODE_KEYWORD_AD = 120;
    private final int MODE_AD_RATE = 130;
    private final int MODE_THEME_DOWN = 140;
    private final int MODE_AD_POINT = 150;
    private final int MODE_POP_AD = 160;
    private final int MODE_WHOWHO = 170;
    private final int MODE_MOBON = 171;
    private final int MODE_MOBON_POST = 172;
    private final int MODE_FREQUENCY = 173;
    private final int MODE_NOTICE = 174;
    private final int MODE_KAKAO_SAFE_KEY = 175;
    private final int MODE_NEW_THEME_LIST = 180;
    private final int MODE_UPDATE_USER_INFO = 190;
    private final int MODE_GET_MY = 200;
    private final int MODE_SAVE_OCB_POINT = 201;
    private final int MODE_OLABANG_LIST = 202;
    private final int MODE_SEARCH_LIST = 203;
    private final int MODE_TIME_DEAL_LIST = 204;
    private final int MODE_AD_FREQUENCY = 205;
    private final int MODE_BRAND_AD = 206;
    private final int MODE_BANNER_AD = 207;
    private final int MODE_GET_TOKEN = 208;
    private final int MODE_OCB_STATS = 209;
    private final int MODE_OCB_TOTAL_POINT = 210;
    private final int MODE_USER_CHECK_POINT = 211;
    private final int MODE_ONE_POINT = 212;
    private final int MODE_USER_INFO = 213;
    private final int MODE_GET_PROMOTION = 214;

    private final int MODE_GET_COUPANG = 220;
    private final int MODE_GET_NATIVE_BANNER = 221;
    private final int MODE_SURPRISE_LIST = 222;
    private final int MODE_SEND_SPOT_POINT = 223;
    private final int MODE_GET_NEWS = 224;
    private final int MODE_GET_NEWS_VIEWCOUNT = 225;
    private final int MODE_GET_TYPING_GAME = 226;
    private final int MODE_GET_SHOPPING_LIST = 227;
    private final int MODE_GET_NEWS_NEW = 228;

    private final int MODE_GET_NOTI_NEWS_TIME = 229;
    private final int MODE_GET_NOTI_NEWS = 230;
    private final int MODE_SAVE_NOTI_POINT = 231;

    private final int MODE_BANNER_POINT_INFO = 232;
    private final int MODE_SET_BANNER_POINT = 233;

    private final int MODE_GET_COUPANG_DATA = 234;

    private final int MODE_SEND_REWARD_POINT = 235;

    private final int MODE_CHECK_REWARD_SC = 236;

    private final int MODE_REWARD_POSSIBLE = 237;

    private final int MODE_GET_MONEYTREE_AD = 238;

    private final int MODE_GET_COUPANG_REWARD_DATA = 239;

    private final int MODE_GET_REWARD_NEWS = 240;

    private final int MODE_GET_CRITEO_AD = 241;

    private final int MODE_GET_OFFERWALL_LIST = 242;

    private final int MODE_GET_OFFERWALL_ACCESS_TOKEN = 243;

    private final int MODE_OFFERWALL_MISSION_CLICK = 244;

    private final int MODE_OFFERWALL_MISSION_PARTICIPATION = 245;

    private final int MODE_OFFERWALL_MISSION_PARTICIPATION_CHECK = 246;

    private final int MODE_JOINT_REWARD_AD = 247;

    private final int MODE_NEWS_ARRAY = 248;

    private final int MODE_JOINT_FINISH = 249;

    private final int MODE_SAVE_OCB_POINT_V2 = 250;

    private final int MODE_OFFERWALL_MISSION_CATEGORY = 251;

    private final int MODE_NEW_REWARD_POSSIBLE = 252;

    private final int MODE_NEW_REWARD_SEND_POINT = 253;

    private final int MODE_NEW_REWARD_WITH_BRAND_POSSIBLE = 254;

    private final int MODE_BRAND_SEND_POINT = 255;

    private final int MODE_SEND_CHAT = 256;

    private final int MODE_GET_GPT_TICKER_COUNT = 257;

    private final int MODE_CHARGE_GPT_TICKET = 258;

    private final int MODE_CHAT_GPT_AD = 259;

    private final int MODE_SEND_CHAT_NEW = 260;
    private final int MODE_GET_GAME_URL = 261;

    public static  asyncApi masyncApi;

    private Context mContext = null;
    private int mModeIndex = 0;

    public String mUrl;
    public Map<String, String> mParam;

    private CallbackObjectResponse callback;
    private OnDefaultObjectCallbackListener mOnDefaultCallbackListener = null;

    public interface CallbackStringResponse {
        void onResponse(String result);
        void onError(String error);
    }

    public interface CallbackObjectResponse {
        void onResponse(Object result);

        void onError(String error);
    }

    public CustomAsyncTask(Context context) {
        gubun = GUBUN_ALPHA;
        mContext = context;
    }

    public void getGameUrl(OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        mUrl = Url.DOMAIN + Url.OCB_GET_GAME_URL;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mModeIndex = MODE_GET_GAME_URL;
        execute();
    }

    public void getChatGptAd(OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        String id = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            id = cp.Decode(mContext, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String adid = MobonUtils.getAdid(mContext);
        mUrl = Url.DOMAIN + Url.OCB_GET_CHAT_GPT_AD;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("uuid", id);
        mParam.put("deviceId", adid);
        LogPrint.d("criteo uuid :: " + id);
        LogPrint.d("criteo deviceId :: " + adid);
        mModeIndex = MODE_CHAT_GPT_AD;
        execute();
    }

    public void getGptTickerCount(OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        String id = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            id = cp.Decode(mContext, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mUrl = Url.DOMAIN + Url.OCB_GET_GPT_TICKET_COUNT;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("uuid", id);

        mModeIndex = MODE_GET_GPT_TICKER_COUNT;
        execute();
    }

    public void sendChat(String question, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        String id = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            id = cp.Decode(mContext, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mUrl = Url.DOMAIN + Url.OCB_GET_GPT_ANSWER;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("uuid", id);
        mParam.put("quest", question);
        mModeIndex = MODE_SEND_CHAT;
        execute();
    }

    public void chargeGptTicket(OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        String id = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            id = cp.Decode(mContext, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mUrl = Url.DOMAIN + Url.OCB_GET_GPT_CHARGE_TICKET;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("uuid", id);

        mModeIndex = MODE_CHARGE_GPT_TICKET;
        execute();
    }

    public void sendChatNew(String question, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        mUrl = "https://api.openai.com/v1/chat/completions";
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("question", question);

        mModeIndex = MODE_SEND_CHAT_NEW;
        execute();
    }

    public void sendBrandPoint(String zone, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        String id = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            id = cp.Decode(mContext, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mUrl = Url.DOMAIN + Url.OCB_SEND_BRAND_POINT;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("server_type", gubun);
        mParam.put("uuid", id);
        mParam.put("zone_id", zone);

        mModeIndex = MODE_BRAND_SEND_POINT;
        execute();
    }

    public void newSendPoint(String zone, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        String id = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            id = cp.Decode(mContext, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogPrint.d("newSendPoint id :: " + id);
        mUrl = Url.DOMAIN + Url.OCB_NEW_REWARD_SEND_POINT;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("server_type", gubun);
        mParam.put("uuid", id);
        mParam.put("zone_id", zone);

        mModeIndex = MODE_NEW_REWARD_SEND_POINT;
        execute();
    }
    public void getJointFinish(String code_id, String liveTime, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        String id = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            id = cp.Decode(mContext, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogPrint.d("getJointFinish use_time :: " + liveTime + " , gubun :: " + gubun + " , code_id :: " + code_id);
        LogPrint.d("getJointFinish id :: " + id);
        mUrl = Url.DOMAIN + Url.OCB_DAY_STATS_POINT;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("type", "live");
        mParam.put("uuid", id);
        mParam.put("use_time", liveTime);
        mParam.put("server_type", gubun);
        mParam.put("code_id", code_id);

        mModeIndex = MODE_JOINT_FINISH;
        execute();
    }

    public void getJointRewardAD(OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        String adid = MobonUtils.getAdid(mContext);
        mUrl = Url.DOMAIN + Url.OCB_JOINT_REWARD_AD;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("deviceId", adid);

        mModeIndex = MODE_JOINT_REWARD_AD;
        execute();
    }

    public void getOfferwallCategory(OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        mUrl = Url.FORMISSION_DOMAIN + "/v1/mission/category";
        mOnDefaultCallbackListener = defaultObjectCallbackListener;

        mParam = new HashMap<String, String>();

        mModeIndex = MODE_OFFERWALL_MISSION_CATEGORY;
        execute();
    }

    public void offerwallParticipationCheck(int mission_seq, String mission_id, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        String adid = MobonUtils.getAdid(mContext);
        String id = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            id = cp.Decode(mContext, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogPrint.d("id :: " + id);
        LogPrint.d("mission_seq :: " + mission_seq);
        LogPrint.d("mission_id :: " + mission_id);
        mUrl = Url.FORMISSION_DOMAIN + "/v1/mission/participationCheck";
        mOnDefaultCallbackListener = defaultObjectCallbackListener;

        mParam = new HashMap<String, String>();
        mParam.put("mission_seq", mission_seq + "");
        mParam.put("mission_id", mission_id);
        mParam.put("media_user_key", id);
        mParam.put("media_user_phone", "");
        mParam.put("media_user_ad_id", adid);
        mParam.put("media_user_email", "");
        mParam.put("client_ip", "");
        mParam.put("android_id", "");
        mParam.put("device_name", Build.MODEL);
        mParam.put("carrier", "");
        mParam.put("ifa", "");
        mParam.put("custom", "");
        mParam.put("server_type", gubun);
        mModeIndex = MODE_OFFERWALL_MISSION_PARTICIPATION_CHECK;
        execute();
    }

    public void offerwallParticipation(OfferwallParticipationReq req, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        String adid = MobonUtils.getAdid(mContext);
        mUrl = Url.FORMISSION_DOMAIN + "/v1/mission/participation";
        mOnDefaultCallbackListener = defaultObjectCallbackListener;

        mParam = new HashMap<String, String>();
        mParam.put("mission_seq", req.getMission_seq() + "");
        mParam.put("mission_id", req.getMission_id());
        mParam.put("media_user_key", req.getMedia_user_key());
        mParam.put("media_user_phone", req.getMedia_user_phone());
        mParam.put("media_user_ad_id", adid);
        mParam.put("media_user_email", req.getMedia_user_email());
        mParam.put("server_type", gubun);
        mModeIndex = MODE_OFFERWALL_MISSION_PARTICIPATION;
        execute();
    }

    public void offerwallClick(int mission_seq, String mission_id, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        mUrl = Url.FORMISSION_DOMAIN + "/v1/mission/click";
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("mission_seq", mission_seq + "");
        mParam.put("mission_id", mission_id);

        mModeIndex = MODE_OFFERWALL_MISSION_CLICK;
        execute();
    }

    public void getOfferwallToken(OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        mUrl = Url.FORMISSION_DOMAIN + "/v1/common/getAccessToken";
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("media_id", FORMISSION_MEDIA_ID);

        mModeIndex = MODE_GET_OFFERWALL_ACCESS_TOKEN;
        execute();
    }

    public void getOfferwallList(String lastMissionSeq, String mission_class, String pageCount, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        String id = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            id = cp.Decode(mContext, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String adid = MobonUtils.getAdid(mContext);
        if ( TextUtils.isEmpty(adid) ) {
            MobonUtils.getADID(mContext);
            adid = MobonUtils.getAdid(mContext);
        }
        LogPrint.d("id :: " + id);
        LogPrint.d("adid :: " + adid);
        mUrl = Url.FORMISSION_DOMAIN + "/v2/mission/list"; // 20221130 개인화목록에 P2,P3포함으로 v1->v2 변경
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("media_user_key", id);
        mParam.put("media_user_ad_id", adid);
        mParam.put("last_mission_seq", lastMissionSeq);
        mParam.put("mission_page_count", pageCount);
        mParam.put("mission_class", mission_class);

        mModeIndex = MODE_GET_OFFERWALL_LIST;
        execute();
    }

    public void getCriteoAd(OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        String adid = MobonUtils.getAdid(mContext);
        mUrl = Url.DOMAIN + Url.OCB_GET_CRITEO_AD;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("deviceid", adid);

        mModeIndex = MODE_GET_CRITEO_AD;
        execute();
    }

    public void getRewardNews(String statiFlag, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        String id = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            id = cp.Decode(mContext, id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mUrl = Url.DOMAIN + Url.OCB_GET_REWARD_NEWS;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("uuid", id);
        mParam.put("statiFlag", statiFlag);

        mModeIndex = MODE_GET_REWARD_NEWS;
        execute();
    }

    public void getMoneyTreeAD(OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        mUrl = Url.DOMAIN + Url.OCB_MONEYTREE_AD;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();

        mModeIndex = MODE_GET_MONEYTREE_AD;
        execute();
    }

    public void isNewRewardWithBrandPossible(String zone_id, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        String id = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            id = cp.Decode(mContext, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mUrl = Url.DOMAIN + Url.OCB_NEW_REWARD_WITH_BRAND_POSSIBLE;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("uuid", id);
        mParam.put("zone_id", zone_id);

        mModeIndex = MODE_NEW_REWARD_WITH_BRAND_POSSIBLE;
        execute();
    }

    public void isNewRewardPossible(String zone_id, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        String id = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            id = cp.Decode(mContext, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mUrl = Url.DOMAIN + Url.OCB_NEW_REWARD_POSSIBLE;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("uuid", id);
        mParam.put("zone_id", zone_id);

        mModeIndex = MODE_NEW_REWARD_POSSIBLE;
        execute();
    }

    public void isRewardPossible(OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        String id = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            id = cp.Decode(mContext, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mUrl = Url.DOMAIN + Url.OCB_REWARD_POSSIBLE;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("uuid", id);

        mModeIndex = MODE_REWARD_POSSIBLE;
        execute();
    }

    public void checkRewardSc(String sc, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        String id = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            id = cp.Decode(mContext, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mUrl = Url.DOMAIN + Url.OCB_REWARD_SC;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("uuid", id);
        mParam.put("sc", sc);

        mModeIndex = MODE_CHECK_REWARD_SC;
        execute();
    }

    public void sendRewardPoint(String sc, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        String id = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            id = cp.Decode(mContext, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogPrint.d("sendPoint id :: " + id);
        mUrl = Url.DOMAIN + Url.OCB_SEND_REWARD_POINT;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("server_type", gubun);
        mParam.put("uuid", id);
        mParam.put("sc", sc);

        mModeIndex = MODE_SEND_REWARD_POINT;
        execute();
    }

    public void getCoupangData(String subId, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        mUrl = Url.DOMAIN + Url.OCB_COUPANG_AD_DATA;
        // ?deviceId=11111111&subId=test
        String adid = MobonUtils.getAdid(mContext);
        LogPrint.d("adid val :: " + adid);
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("deviceId", adid);
        mParam.put("subId", subId);
        mModeIndex = MODE_GET_COUPANG_DATA;
        execute();

    }

    public void getBannerPointInfo(OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        mUrl = Url.DOMAIN + Url.OCB_BANNER_POINT_INFO;
        String id = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            id = cp.Decode(mContext, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("uuid", id);
        mModeIndex = MODE_BANNER_POINT_INFO;
        execute();
    }

    public void sendBannerPoint(OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        mUrl = Url.DOMAIN + Url.OCB_SET_BANNER_POINT;
        String id = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            id = cp.Decode(mContext, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("uuid", id);
        mModeIndex = MODE_SET_BANNER_POINT;
        execute();
    }

    public void getNotiNews(String statiFlag, String seq, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        mUrl = Url.DOMAIN + Url.OCB_NOTI_NEWS_DATA;
        String id = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            id = cp.Decode(mContext, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("statiFlag", statiFlag);
        mParam.put("seq", seq);
        mParam.put("uuid", id);

        mModeIndex = MODE_GET_NOTI_NEWS;
        execute();
    }

    public void saveNotiNewsPoint(String seq, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        mUrl = Url.DOMAIN + Url.OCB_SAVE_NOTI_POINT;
        String id = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            id = cp.Decode(mContext, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("server_type", gubun);
        mParam.put("seq", seq);
        mParam.put("uuid", id);

        mModeIndex = MODE_SAVE_NOTI_POINT;
        execute();
    }

    public void getNewsNotiTime(OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        mUrl = Url.DOMAIN + Url.OCB_NOTI_TIME;

        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mModeIndex = MODE_GET_NOTI_NEWS_TIME;
        execute();
    }

    public void getShoppingList(String categoryId, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        mUrl = Url.DOMAIN + Url.OCB_SHOPPING_LIST;

        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("categoryId", categoryId);
        mModeIndex = MODE_GET_SHOPPING_LIST;
        execute();
    }

    public void getTypingGameStatus(OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        String id = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            id = cp.Decode(mContext, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mUrl = Url.DOMAIN + Url.OCB_TYPING_GAME;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("uuid", id);
        mModeIndex = MODE_GET_TYPING_GAME;
        execute();
    }

    public void sendViewCount(String root_domain, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        String id = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            id = cp.Decode(mContext, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogPrint.d("get_news_view uuid :: " + id);
        LogPrint.d("get_news_view news_domain :: " + root_domain);
        mUrl = Url.DOMAIN + Url.OCB_NEWS_VIEWCOUNT;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("root_domain", root_domain);
        mParam.put("uuid", id);
        mModeIndex = MODE_GET_NEWS_VIEWCOUNT;
        execute();
    }

    public void getArrayNews(String statiFlag, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        String adid = MobonUtils.getAdid(mContext);
        String id = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            id = cp.Decode(mContext, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogPrint.d("array news uuid :: " + id);
        mUrl = Url.DOMAIN + Url.OCB_NEWS_ARRAY;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("deviceId", adid);
        mParam.put("uuid", id);
        mParam.put("statiFlag", statiFlag);
        mModeIndex = MODE_NEWS_ARRAY;
        execute();
    }

    public void getNewsNew(String statiFlag, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        String id = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            id = cp.Decode(mContext, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mUrl = Url.DOMAIN + Url.OCB_NEWS_NEW;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("uuid", id);
        mParam.put("statiFlag", statiFlag);
        mModeIndex = MODE_GET_NEWS_NEW;
        execute();
    }

    public void getNews(String statiFlag, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        String id = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            id = cp.Decode(mContext, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mUrl = Url.DOMAIN + Url.OCB_NEWS;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("uuid", id);
        mParam.put("statiFlag", statiFlag);
        mModeIndex = MODE_GET_NEWS;
        execute();
    }

    public void sendSpotPoint(String code_id, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        String id = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            id = cp.Decode(mContext, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogPrint.d("sendSpotPoint id :: " + id + " , server_type :: " + gubun + " , code_id :: " + code_id);
        mUrl = Url.DOMAIN + Url.OCB_SEND_SPOT_POINT;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("server_type", gubun);
        mParam.put("code_id", code_id);
        mParam.put("uuid", id);
        mModeIndex = MODE_SEND_SPOT_POINT;
        execute();
    }

    public void getSurpriseList(OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        String id = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            id = cp.Decode(mContext, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mUrl = Url.DOMAIN + Url.OCB_SURPRISE_LIST;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("uuid", id);
        mModeIndex = MODE_SURPRISE_LIST;
        execute();
    }

    public void getNativeBannerInfo(OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        mUrl = "https://native.mediacategory.com/servlet/adNativeNonSDKMobileApi";
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("s", "548448");
        mParam.put("adid", MobonUtils.getAdid(mContext));
        mParam.put("maxCnt", "1");
        mParam.put("sdkYn", "true");

        mModeIndex = MODE_GET_NATIVE_BANNER;
        execute();
    }

    public void getPromotion(OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        mUrl = Url.DOMAIN + Url.OCB_PROMOTION;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();

        mModeIndex = MODE_GET_PROMOTION;
        execute();
    }

    public void setUserInfo(String keyboardType, String theme, String appVersion, String useYN, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        String id = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            id = cp.Decode(mContext, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mUrl = Url.DOMAIN + Url.OCB_USER_INFO;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("uuid", id);
        mParam.put("user_app_type ", keyboardType);
        mParam.put("user_app_theme", theme);
        mParam.put("user_app_version", appVersion);
        mParam.put("user_keyboard_YN ", useYN);
        mModeIndex = MODE_USER_INFO;
        execute();
    }

    public void setOnePoint(OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        String id = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            id = cp.Decode(mContext, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mUrl = Url.DOMAIN + Url.OCB_ONE_POINT;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("uuid", id);
        mModeIndex = MODE_ONE_POINT;
        execute();
    }

    public void getTotalPoint(OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        String id = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            id = cp.Decode(mContext, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogPrint.d("getTotalPoint uuid :: " + id);
        mUrl = Url.DOMAIN + Url.OCB_TOTAL_POINT;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("uuid", id);
        mModeIndex = MODE_OCB_TOTAL_POINT;
        execute();
    }

    public void getUserCheckPoint(OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        String id = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            id = cp.Decode(mContext, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mUrl = Url.DOMAIN + Url.OCB_USER_CHECK_POINT;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("uuid", id);
        mModeIndex = MODE_USER_CHECK_POINT;
        execute();
    }

    public void postFrequencyLiveTime(String uuid, String liveTime, OnDefaultObjectCallbackListener defaultCallbackListener) {
        mUrl = Url.DOMAIN + Url.OCB_DAY_STATS_V2;
        mOnDefaultCallbackListener = defaultCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("type", "live");
        mParam.put("user_trmnl_unq_key", uuid);
        mParam.put("live_time", liveTime);

        mModeIndex = MODE_FREQUENCY;
        execute();
    }

    public void postStats(String type, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        String uuid = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            uuid = cp.Decode(mContext, uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mUrl = Url.DOMAIN + Url.OCB_DAY_STATS;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("type", type);
        mParam.put("uuid", uuid);
        mModeIndex = MODE_OCB_STATS;
        execute();
    }

    public void getToken(String ocbPoint, String event_id, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        String uuid = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            uuid = cp.Decode(mContext, uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mUrl = Url.DOMAIN + Url.OCB_TOKEN;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("server_type", gubun);
        mParam.put("uuid", uuid);
        mParam.put("user_point", ocbPoint);
        mParam.put("event_id", event_id);
        mModeIndex = MODE_GET_TOKEN;
        execute();
    }

    public void getBannerInfo(String index, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        mUrl = Url.DOMAIN + Url.OCB_BANNER_AD_V2;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("index", index);
        mModeIndex = MODE_BANNER_AD;
        execute();
    }

    public void getBrandInfo(OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        mUrl = Url.DOMAIN + Url.OCB_BRAND_AD;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();

        mModeIndex = MODE_BRAND_AD;
        execute();
    }

    public void getAdFrequency(OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        String adid = MobonUtils.getAdid(mContext);
        mUrl = Url.DOMAIN + Url.OCB_AD_FREQNEUCY;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("adid", adid);
        mModeIndex = MODE_AD_FREQUENCY;
        execute();
    }

    public void getTimeDealList(OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        mUrl = Url.DOMAIN + Url.OCB_TIME_DEAL_LIST;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("server_type", gubun);
        mModeIndex = MODE_TIME_DEAL_LIST;
        execute();
    }

    public void getSearchList(String keyword, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        //mUrl = Url.DOMAIN + Url.OCB_SEARCH_LIST + "?keyword=" + keyword;
        mUrl = Url.DOMAIN + Url.OCB_SEARCH_LIST;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("server_type", gubun);
        mParam.put("keyword", keyword);
        mModeIndex = MODE_SEARCH_LIST;
        execute();
    }

    public void getOlabangList(OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        mUrl = Url.DOMAIN + Url.OCB_OLABANG_LIST_V2;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("server_type", gubun);
        mModeIndex = MODE_OLABANG_LIST;
        execute();
    }

    public void savePointV2(String ocbPoint, String event_id, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        String uuid = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            uuid = cp.Decode(mContext, uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogPrint.d("save point :: " + uuid);
        mUrl = Url.DOMAIN + Url.OCB_SAVE_POINT_V2;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        //mParam.put("user_point", ocbPoint);
        //mParam.put("event_id", event_id);
        mParam.put("server_type", gubun);
        mParam.put("uuid", uuid);
        mParam.put("user_point", ocbPoint);
        mParam.put("event_id", event_id);
        mModeIndex = MODE_SAVE_OCB_POINT_V2;
        execute();
    }

    public void savePoint(String ocbPoint, String event_id, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        String uuid = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            uuid = cp.Decode(mContext, uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mUrl = Url.DOMAIN + Url.OCB_SAVE_POINT;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        //mParam.put("user_point", ocbPoint);
        //mParam.put("event_id", event_id);
        mParam.put("server_type", gubun);
        mParam.put("uuid", uuid);
        mParam.put("user_point", ocbPoint);
        mParam.put("event_id", event_id);
        mModeIndex = MODE_SAVE_OCB_POINT;
        execute();
    }

    public void getUserInfo(OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        String uuid = SharedPreference.getString(mContext, Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            uuid = cp.Decode(mContext, uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mUrl = Url.DOMAIN + Url.OCB_GET_MY;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("uuid", uuid);

        mModeIndex = MODE_GET_MY;
        execute();
    }

    public void updateUserInfo(String uuid, long user_point, String card_number, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        mUrl = Url.DOMAIN + Url.OCB_UPDATE_USER_INFO;
        mOnDefaultCallbackListener = defaultObjectCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("uuid", uuid);
        mModeIndex = MODE_UPDATE_USER_INFO;
        execute();
    }

    public void connectPopAD(KeyboardUserIdModel model, String keyword, String version, OnDefaultObjectCallbackListener defaultCallbackListener) {
        KeyboardLogPrint.e("connectPopAD");
        KeyboardLogPrint.e("connectPopAD userid :: " + model.getUserId());
        KeyboardLogPrint.e("connectPopAD gubun :: " + model.getGubun());
        KeyboardLogPrint.e("connectPopAD deviceid :: " + model.getDeviceId());
        KeyboardLogPrint.e("connectPopAD key :: " + keyword);
        mUrl = Url.POP_AD_URL;
        if ( KeyboardLogPrint.debug )
            mUrl = Url.TEST_DOMAIN + Url.POP_AD_URL;
        else
            mUrl = Url.DOMAIN + Url.POP_AD_URL;
        mOnDefaultCallbackListener = defaultCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("userid", model.getUserId());
        mParam.put("gubun", model.getGubun());
        mParam.put("deviceid", model.getDeviceId());
//        mParam.put("rate", ""+rate); // 이떄는 필요 없음
        mParam.put("key", "" + keyword);
        mParam.put("ver", "" + version);
        mParam.put("service_code", Common.SERVICE_CODE);

        mModeIndex = MODE_POP_AD;
        execute();
    }

    public void connectAdPoint(String url, OnDefaultObjectCallbackListener defaultCallbackListener) {
        mUrl = url;
        mOnDefaultCallbackListener = defaultCallbackListener;
        mParam = new HashMap<String, String>();

        mModeIndex = MODE_AD_POINT;
        execute();
    }

    public void connectThemeDown(String userId, String deviceId, String fileName, OnDefaultObjectCallbackListener defaultCallbackListener) {
        mUrl = Url.URL_THEME_DOWN;
        if ( KeyboardLogPrint.debug )
            mUrl = Url.TEST_DOMAIN + Url.URL_THEME_DOWN;
        else
            mUrl = Url.DOMAIN + Url.URL_THEME_DOWN;
        mOnDefaultCallbackListener = defaultCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("userid", userId);
        mParam.put("deviceid", deviceId);
        mParam.put("unzip_filename", fileName);
        mParam.put("service_code", Common.SERVICE_CODE);

        mModeIndex = MODE_THEME_DOWN;
        execute();
    }

    public void connectADRate(String deviceId, int adLevel, OnDefaultObjectCallbackListener defaultCallbackListener) {
        mUrl = Url.URL_AD_RATE;
        if ( KeyboardLogPrint.debug )
            mUrl = Url.TEST_DOMAIN + Url.URL_AD_RATE;
        else
            mUrl = Url.DOMAIN + Url.URL_AD_RATE;
        mOnDefaultCallbackListener = defaultCallbackListener;
        mParam = new HashMap<String, String>();
        KeyboardLogPrint.e("connectADRate deviceId :: " + deviceId);
        KeyboardLogPrint.e("connectADRate adLevel :: " + adLevel);
        int level = adLevel + 1; // 서버에서 0을 던지면 처리가 안되서 0 - 5레벨로 던질경우 문제가 있다고 함. 그래서 1을 더한 1 - 6까지를 던짐 ( 서버에는 0 - 5까지가 저장)
        mParam.put("deviceid", deviceId);
        mParam.put("adRate", ""+level);
        mParam.put("service_code", Common.SERVICE_CODE);

        mModeIndex = MODE_AD_RATE;
        execute();
    }

    public void connectSendKeyword(KeyboardUserIdModel model, String keyword, OnDefaultObjectCallbackListener defaultCallbackListener) {
        KeyboardLogPrint.e("connectSendKeyword");
        KeyboardLogPrint.e("connectSendKeyword userid :: " + model.getUserId());
        KeyboardLogPrint.e("connectSendKeyword gubun :: " + model.getGubun());
        KeyboardLogPrint.e("connectSendKeyword deviceid :: " + model.getDeviceId());
        KeyboardLogPrint.e("connectSendKeyword key :: " + keyword);
        mUrl = Url.SEND_KEYWORD_URL;
        if ( KeyboardLogPrint.debug )
            mUrl = Url.TEST_DOMAIN + Url.SEND_KEYWORD_URL;
        else
            mUrl = Url.DOMAIN + Url.SEND_KEYWORD_URL;
        mOnDefaultCallbackListener = defaultCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("userid", model.getUserId());
        mParam.put("gubun", model.getGubun());
        mParam.put("deviceid", model.getDeviceId());
        mParam.put("key", keyword);
        mParam.put("service_code", Common.SERVICE_CODE);

        mModeIndex = MODE_SEND_KEYWORD;
        execute();
    }

    public void connectAD(KeyboardUserIdModel model, String keyword, int rate, String version, OnDefaultObjectCallbackListener defaultCallbackListener) {
        KeyboardLogPrint.e("connectAD");
        KeyboardLogPrint.e("connectAD userid :: " + model.getUserId());
        KeyboardLogPrint.e("connectAD gubun :: " + model.getGubun());
        KeyboardLogPrint.e("connectAD deviceid :: " + model.getDeviceId());
        KeyboardLogPrint.e("connectAD key :: " + keyword);
        mUrl = Url.KEYWORD_AD_URL;
        if ( KeyboardLogPrint.debug )
            mUrl = Url.TEST_DOMAIN + Url.KEYWORD_AD_URL;
        else
            mUrl = Url.DOMAIN + Url.KEYWORD_AD_URL;
        mOnDefaultCallbackListener = defaultCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("userid", model.getUserId());
        mParam.put("gubun", model.getGubun());
        mParam.put("deviceid", model.getDeviceId());
//        mParam.put("rate", ""+rate); // 이떄는 필요 없음
        mParam.put("key", keyword);
        mParam.put("ver", version);
        mParam.put("service_code", Common.SERVICE_CODE);

        mModeIndex = MODE_KEYWORD_AD;
        execute();
    }

    public void connectZeroPoint(KeyboardUserIdModel model, OnDefaultObjectCallbackListener defaultCallbackListener) {
        mUrl = Url.ZERO_POINT_URL;
        if ( KeyboardLogPrint.debug )
            mUrl = Url.TEST_DOMAIN + Url.ZERO_POINT_URL;
        else
            mUrl = Url.DOMAIN + Url.ZERO_POINT_URL;
        mOnDefaultCallbackListener = defaultCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("userid", model.getUserId());
        mParam.put("gubun", model.getGubun());
        mParam.put("deviceid", model.getDeviceId());
        mParam.put("service_code", Common.SERVICE_CODE);

        mModeIndex = MODE_ZERO_POINT;
        execute();
    }

    public void connectNewThemeList(String userId, int scaleType, String category, String keyword, OnDefaultObjectCallbackListener defaultCallbackListener) {
        mUrl = Url.DOMAIN + Url.OCB_OFFERWALL_THEME;
        //mUrl = Url.DOMAIN + Url.NEW_THEME_LIST;
        mOnDefaultCallbackListener = defaultCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("userid", userId);
        mParam.put("scale_type", ""+scaleType);
        mParam.put("partner_code", Common.OCB_PARTNER_CODE);
        mParam.put("cate", category);
        mParam.put("keyword", keyword);
        mModeIndex = MODE_NEW_THEME_LIST;
        execute();
    }

    public void connectThemeList(String userId, int scaleType, OnDefaultObjectCallbackListener defaultCallbackListener) {
        mUrl = Url.THEME_LIST;
        if ( KeyboardLogPrint.debug )
            mUrl = Url.TEST_DOMAIN + Url.THEME_LIST;
        else
            mUrl = Url.DOMAIN + Url.THEME_LIST;
        mUrl = "http://ocbapi.cashkeyboard.co.kr/API/OCB/theme.php";


        mOnDefaultCallbackListener = defaultCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("userid", userId);
        mParam.put("scale_type", ""+scaleType);
        mParam.put("service_code", Common.SERVICE_CODE);

        mModeIndex = MODE_THEME_LIST;
        execute();
    }

    public void connectPopularThemeList(String userId, int scaleType, OnDefaultObjectCallbackListener defaultCallbackListener) {
        mUrl = Url.THEME_POPULAR;
        if ( KeyboardLogPrint.debug )
            mUrl = Url.TEST_DOMAIN + Url.THEME_POPULAR;
        else
            mUrl = Url.DOMAIN + Url.THEME_POPULAR;
        mOnDefaultCallbackListener = defaultCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("userid", userId);
        mParam.put("scale_type", ""+scaleType);
        mParam.put("service_code", Common.SERVICE_CODE);

        mModeIndex = MODE_THEME_POPULAR;
        execute();
    }

    public void requestUserPoint(KeyboardUserIdModel model, OnDefaultObjectCallbackListener defaultCallbackListener) {
        mUrl = Url.USER_POINT_URL;
        if ( KeyboardLogPrint.debug )
            mUrl = Url.TEST_DOMAIN + Url.USER_POINT_URL;
        else
            mUrl = Url.DOMAIN + Url.USER_POINT_URL;
        mOnDefaultCallbackListener = defaultCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("userid", model.getUserId());
        mParam.put("gubun", model.getGubun());
        mParam.put("deviceid", model.getDeviceId());
        mParam.put("service_code", Common.SERVICE_CODE);

        mModeIndex = MODE_GET_USER_POINT;
        execute();
    }

    public void requestMaxPoint(KeyboardUserIdModel model, OnDefaultObjectCallbackListener defaultCallbackListener) {
        mUrl = Url.BASE_POINT;
        if ( KeyboardLogPrint.debug )
            mUrl = Url.TEST_DOMAIN + Url.BASE_POINT;
        else
            mUrl = Url.DOMAIN + Url.BASE_POINT;
        mOnDefaultCallbackListener = defaultCallbackListener;
        mParam = new HashMap<String, String>();
        KeyboardLogPrint.e("requestMaxPoint :: userId :: " + model.getUserId());
        KeyboardLogPrint.e("requestMaxPoint :: gubun :: " + model.getGubun());
        KeyboardLogPrint.e("requestMaxPoint :: deviceid :: " + model.getDeviceId());
        mParam.put("userid", model.getUserId());
        mParam.put("gubun", model.getGubun());
        mParam.put("deviceid", model.getDeviceId());
        mParam.put("service_code", Common.SERVICE_CODE);

        mModeIndex = MODE_USER_MAX_POINT;
        execute();
    }

    public void requestChargePoint(KeyboardUserIdModel model, int point, int randomCount, OnDefaultObjectCallbackListener defaultCallbackListener) {
        mUrl = Url.CHARGE_POINT;
        if ( KeyboardLogPrint.debug )
            mUrl = Url.TEST_DOMAIN + Url.CHARGE_POINT;
        else
            mUrl = Url.DOMAIN + Url.CHARGE_POINT;
        mOnDefaultCallbackListener = defaultCallbackListener;
        KeyboardLogPrint.d("random point :: " + randomCount);
        KeyboardLogPrint.d("point :: " + point);
        mParam = new HashMap<String, String>();
        mParam.put("userid", model.getUserId());
        mParam.put("gubun", model.getGubun());
        mParam.put("deviceid", model.getDeviceId());
        mParam.put("point", ""+point);
        mParam.put("random_count", ""+randomCount);
        mParam.put("service_code", Common.SERVICE_CODE);

        mModeIndex = MODE_CHARGE_POINT;
        execute();
    }
    /**
     * 이미지 리스트 <br>
     */

    public void requestConnectList(String SearchKey, OnDefaultObjectCallbackListener defaultCallbackListener) {
//        mUrl = Url.BASE_URL;
//        mOnDefaultCallbackListener = defaultCallbackListener;
//        mParam = new HashMap<String, String>();
//        mModeIndex = MODE_IMAGE_SEARCH;
//        execute();

//        mUrl = "https://apis.daum.net/search/image?apikey=d0c8781c1eb728dc0efde58897761520&q=" + SearchKey + "&output=json&result=20";
        mUrl = "https://apis.daum.net/search/image?apikey=b20f3512e531cec6039a2f3d7f12db45&q=" + SearchKey + "&output=json&result=20";
        mOnDefaultCallbackListener = defaultCallbackListener;
        mParam = new HashMap<String, String>();

        mModeIndex = MODE_IMAGE_SEARCH;
        execute();
    }

    public void requestImageList(String searchWord, OnDefaultObjectCallbackListener defaultCallbackListener) {
//        String packageName = mContext.getPackageName();
        // 한경수 실장님이 매체 구분을 위한 값이므로 mdeiacode 값으로 넣으라고 함
        String packageName = SharedPreference.getString(mContext, Common.META_DATA_MEDIA_CODE);
        KeyboardLogPrint.w("requestImageList packageName :: " + packageName);
        KeyboardLogPrint.w("searchWord :: " + searchWord);
        if ( KeyboardLogPrint.debug )
            mUrl = Url.TEST_DOMAIN + Url.BASE_URL;
        else
            mUrl = Url.DOMAIN + Url.BASE_URL;

        mOnDefaultCallbackListener = defaultCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("refpage", "list");
        mParam.put("appid", packageName);
        mParam.put("search", searchWord);
        mParam.put("service_code", Common.SERVICE_CODE);

        mModeIndex = MODE_IMAGE_SEARCH;
        execute();
    }

    public void requestADInfo(String keyword, OnDefaultObjectCallbackListener defaultCallbackListener) {
        String us = SharedPreference.getString(mContext, Common.META_DATA_U_VALUE);
        String mc = SharedPreference.getString(mContext, Common.META_DATA_S_VALUE);
        String media = SharedPreference.getString(mContext, Common.META_DATA_MEDIA_CODE);
        KeyboardLogPrint.w("us :: " + us);
        KeyboardLogPrint.w("mc :: " + mc);
        KeyboardLogPrint.w("media :: " + media);
        mUrl = Url.API_KEYBOARD_AD;
        mOnDefaultCallbackListener = defaultCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("scriptId", media);
        mParam.put("imgType", "1");
        mParam.put("adCnt", "1");
        mParam.put("addViewCnt", "add");
        mParam.put("addMedia", "1");
        mParam.put("product", "mba");
        mParam.put("next", "KL");
        mParam.put("mc", mc);
        mParam.put("us", us);
        mParam.put("kwd", keyword);
        mParam.put("service_code", Common.SERVICE_CODE);

        mModeIndex = MODE_KEYBOARD_AD;
        execute();
    }

    public void requestLiveCheck(String deviceId, OnDefaultObjectCallbackListener defaultCallbackListener)
    {
//        String packageName = mContext.getPackageName();
        // 한경수 실장님이 매체 구분을 위한 값이므로 mdeiacode 값으로 넣으라고 함
        String packageName = SharedPreference.getString(mContext, Common.META_DATA_MEDIA_CODE);
        KeyboardLogPrint.w("requestLiveCheck packageName :: " + packageName);
        KeyboardLogPrint.w("requestLiveCheck deviceId :: " + deviceId);
        mUrl = Url.API_LIVE_CHECK;
        if ( KeyboardLogPrint.debug )
            mUrl = Url.TEST_DOMAIN + Url.API_LIVE_CHECK;
        else
            mUrl = Url.DOMAIN + Url.API_LIVE_CHECK;

        mOnDefaultCallbackListener = defaultCallbackListener;

        boolean adPushOnOff = SharedPreference.getBoolean(mContext, Common.PREF_AD_PUSH);
        KeyboardLogPrint.w("requestLiveCheck adPushOnOff :: " + adPushOnOff);
        mParam = new HashMap<String, String>();
        mParam.put("refpage", "livechk");
        mParam.put("deviceid", deviceId);

        mParam.put("service_code", Common.SERVICE_CODE);
        mParam.put("appid", packageName);
        mParam.put("push_yn", ""+adPushOnOff);
        mModeIndex = MODE_LIVE_CHECK;
        execute();
    }

    public void requestQuickLinkList(OnDefaultObjectCallbackListener defaultCallbackListener) {
        mUrl = Url.API_QUICK_LINK;
        if ( KeyboardLogPrint.debug )
            mUrl = Url.TEST_DOMAIN + Url.API_QUICK_LINK;
        else
            mUrl = Url.DOMAIN + Url.API_QUICK_LINK;
        mOnDefaultCallbackListener = defaultCallbackListener;

        mParam = new HashMap<String, String>();
        mParam.put("service_code", Common.SERVICE_CODE);

        mModeIndex = MODE_QUICK_LINK;
        execute();
    }

    public void requestMatchEmoji(OnDefaultObjectCallbackListener defaultCallbackListener) {
//        String packageName = mContext.getPackageName();
        // 한경수 실장님이 매체 구분을 위한 값이므로 mdeiacode 값으로 넣으라고 함
        String packageName = SharedPreference.getString(mContext, Common.META_DATA_MEDIA_CODE);
        KeyboardLogPrint.w("requestMatchEmoji packageName :: " + packageName);
        String version = SharedPreference.getString(mContext, Common.PREF_KEYWORD_VERSION);
        if ( TextUtils.isEmpty(version) )
            version = "0.9";
        KeyboardLogPrint.w("requestMatchEmoji version :: " + version);
        if ( KeyboardLogPrint.debug )
            mUrl = Url.TEST_DOMAIN + Url.BASE_URL;
        else
            mUrl = Url.DOMAIN + Url.BASE_URL;
//        mUrl = Url.BASE_URL;
        mOnDefaultCallbackListener = defaultCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("ver", version);
        mParam.put("refpage", "keyword");
        mParam.put("appid", packageName);
        mParam.put("service_code", Common.SERVICE_CODE);

        mModeIndex = MODE_EMOJI_MATCHING;
        execute();
    }

    public void requestUnSafeCheck(OnDefaultObjectCallbackListener defaultCallbackListener) {
        mUrl = Url.WHOWHO;
        mOnDefaultCallbackListener = defaultCallbackListener;

        mParam = new HashMap<String, String>();

        mModeIndex = MODE_WHOWHO;
        execute();
    }

    public void postMobonAd(String url, Map<String, String> param, OnDefaultObjectCallbackListener defaultCallbackListener) {
        mUrl = url;
        mOnDefaultCallbackListener = defaultCallbackListener;

        mParam = param;

        mModeIndex = MODE_MOBON_POST;
        execute();
    }

    public void getCoupangAd(String keyword, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
        StringBuffer sb = new StringBuffer("https://api.cashkeyboard.co.kr/API/WHOWHO/coupang_search.php?");
        mOnDefaultCallbackListener = defaultObjectCallbackListener;

        try {
            Locale systemLocale;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                systemLocale = mContext.getResources().getConfiguration().getLocales().get(0);
            }else{
                systemLocale = mContext.getResources().getConfiguration().locale;
            }

            String country = systemLocale.getCountry();

            sb.append("keyword="+URLEncoder.encode(keyword, "utf-8"));
            sb.append("&limit=1");
            sb.append("&country="+country);
            sb.append("&appname=okcashbag");

            mUrl = sb.toString();
            mModeIndex = MODE_GET_COUPANG;

            execute();

        }catch (Exception e){

        }
    }

    public class CoupangRunable implements Runnable {
        private final static String REQUEST_METHOD = "GET";
        private final static String DOMAIN = "https://api-gateway.coupang.com";
        private final static String SEARCH_URL = "/v2/providers/affiliate_open_api/apis/openapi/products/search";
        private final static String ACCESS_KEY = "6566d8b3-141d-4bde-8de9-606126385670";
        private final static String SECRET_KEY = "dcfcadfdd0e4cf4610fd08b886859f975fde5a8f";

        private OnDefaultObjectCallbackListener defaultObjectCallbackListener = null;
        private String keyword = null;

        public CoupangRunable(String keyword, OnDefaultObjectCallbackListener defaultObjectCallbackListener) {
            this.keyword = keyword;
            this.defaultObjectCallbackListener = defaultObjectCallbackListener;
        }

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run() {
            String REQUEST_JSON = "{\"keyword\": \"" + keyword + "\", \"limit\": 1}";
            String REQUEST_PARAM = "?keyword=" + keyword + "&limit=1";
            String authorization = CoupangAdPopup.generate(REQUEST_METHOD, SEARCH_URL, SECRET_KEY, ACCESS_KEY);
            InputStream in = null;
            HttpURLConnection myConnection = null;
            BufferedReader reader = null;
            try {
                URL coupangUrl = new URL(DOMAIN + SEARCH_URL + REQUEST_PARAM);
                myConnection = (HttpURLConnection ) coupangUrl.openConnection();
                myConnection.setRequestMethod(REQUEST_METHOD);

                myConnection.setRequestProperty("User-Agent", "Android");
                myConnection.setRequestProperty("Authorization", authorization);
//                myConnection.setRequestProperty("Content-Type", "application/json");
                myConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

                myConnection.setRequestProperty("Accept-Charset", "UTF-8");
                myConnection.setConnectTimeout(5000);
                myConnection.setReadTimeout(5000);
                myConnection.setDoOutput(true);
                myConnection.setDoInput(true);

//                OutputStreamWriter wr = new OutputStreamWriter(myConnection.getOutputStream());
//                wr.write(REQUEST_JSON);
//                wr.flush();

                int responseCode = myConnection.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    in = myConnection.getInputStream();
                } else {
                    in = myConnection.getErrorStream();
                }

                reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                reader.close();
                defaultObjectCallbackListener.onResponse(true, sb.toString());

            }catch (Exception e){
                e.printStackTrace();
                defaultObjectCallbackListener.onResponse(false, e.getMessage());

            } finally {

                if (reader != null) {
                    try {
                        reader.close();
                    }catch (Exception e){}
                }
                if (myConnection != null) { myConnection.disconnect(); }

            }
        }
    }

    public void execute() {
        LogPrint.d("apilist check modeIndex :: " + mModeIndex + " , mUrl :: " + mUrl);
        switch (mModeIndex) {
//            case MODE_IMAGE_SEARCH:
            case MODE_KEYBOARD_AD:
            case MODE_AD_POINT:
//            case MODE_WHOWHO:
            case MODE_NOTICE:
            case MODE_KAKAO_SAFE_KEY:
            case MODE_MOBON:
            case MODE_GET_MY:
            case MODE_OLABANG_LIST:
            case MODE_SEARCH_LIST:
            case MODE_TIME_DEAL_LIST:
            case MODE_AD_FREQUENCY:
            case MODE_BRAND_AD:
            case MODE_BANNER_AD:
            case MODE_OCB_TOTAL_POINT:
            case MODE_USER_CHECK_POINT:
            case MODE_UPDATE_USER_INFO:
            case MODE_NEW_THEME_LIST:
            case MODE_SAVE_OCB_POINT:
            case MODE_SAVE_OCB_POINT_V2:
            case MODE_GET_TOKEN:
            case MODE_OCB_STATS:
            case MODE_ONE_POINT:
            case MODE_USER_INFO:
            case MODE_GET_PROMOTION:
            case MODE_GET_COUPANG:
            case MODE_GET_NATIVE_BANNER:
            case MODE_SURPRISE_LIST:
            case MODE_GET_NEWS:
            case MODE_GET_NEWS_NEW:
            case MODE_NEWS_ARRAY:
            case MODE_GET_TYPING_GAME:
            case MODE_GET_NOTI_NEWS_TIME:
            case MODE_GET_NOTI_NEWS:
            case MODE_BANNER_POINT_INFO:
            case MODE_GET_COUPANG_DATA:
            case MODE_CHECK_REWARD_SC:
            case MODE_REWARD_POSSIBLE:
            case MODE_GET_MONEYTREE_AD:
            case MODE_GET_COUPANG_REWARD_DATA:
            case MODE_GET_REWARD_NEWS:
            case MODE_GET_CRITEO_AD:
            case MODE_GET_OFFERWALL_LIST:
            case MODE_GET_OFFERWALL_ACCESS_TOKEN:
            case MODE_JOINT_REWARD_AD:
            case MODE_OFFERWALL_MISSION_CATEGORY:
            case MODE_NEW_REWARD_POSSIBLE:
            case MODE_NEW_REWARD_WITH_BRAND_POSSIBLE:
            case MODE_GET_GPT_TICKER_COUNT:
            case MODE_CHARGE_GPT_TICKET:
            case MODE_CHAT_GPT_AD:
            case MODE_GET_GAME_URL:
            case MODE_SEND_CHAT:
                masyncApi = new asyncApi(mContext, mUrl, mParam, "GET", new CallbackObjectResponse() {
                    @Override
                    public void onResponse(Object result) {
                        if ( result != null )
                            LogPrint.d("response asyncApi result :: " + result.toString());
                        else
                            LogPrint.d("response asyncApi result null");
                        if (mOnDefaultCallbackListener != null)
                            mOnDefaultCallbackListener.onResponse(true, result);
                    }

                    @Override
                    public void onError(String error) {
                        LogPrint.d("onError :: " + error);
                        if ( TextUtils.isEmpty(error) ) {
                            if ( !Common.IsNetworkConnected(mContext)) {
                                try {
                                    JSONObject object = new JSONObject();
                                    object.put(Common.NETWORK_DISCONNECT, "Wi-Fi 혹은 모바일 데이터에 연결할 수 없습니다.확인 후 다시 시도해주세요.");
                                    if (mOnDefaultCallbackListener != null)
                                        mOnDefaultCallbackListener.onResponse(false, object);
                                } catch (Exception e) {
                                }
                            } else {
                                try {
                                    if ( SoftKeyboard.isKeyboardShow ) {
                                        JSONObject object = new JSONObject();
                                        object.put(Common.NETWORK_ERROR, "서버와의 통신이 원활하지 않습니다. 잠시 후 다시 시도해주세요.");
                                        if (mOnDefaultCallbackListener != null)
                                            mOnDefaultCallbackListener.onResponse(false, object);
                                    }
                                } catch (Exception e) {
                                }
                            }
                        } else {
                            if ( !Common.IsNetworkConnected(mContext)) {
                                try {
                                    JSONObject object = new JSONObject();
                                    object.put(Common.NETWORK_DISCONNECT, "Wi-Fi 혹은 모바일 데이터에 연결할 수 없습니다.확인 후 다시 시도해주세요.");
                                    LogPrint.d("network error 111");
                                    if (mOnDefaultCallbackListener != null)
                                        mOnDefaultCallbackListener.onResponse(false, object);
                                } catch (Exception e) {
                                }
                            } else {
                                try {
                                    if ( SoftKeyboard.isKeyboardShow ) {
                                        JSONObject object = new JSONObject();
                                        object.put(Common.NETWORK_ERROR, "서버와의 통신이 원활하지 않습니다. 잠시 후 다시 시도해주세요.");
                                        if (mOnDefaultCallbackListener != null)
                                            mOnDefaultCallbackListener.onResponse(false, object);
                                    }
                                } catch (Exception e) {
                                }
                            }
                        }
                    }
                });
                break;
            case MODE_GET_SHOPPING_LIST:
                masyncApi = new asyncApi(mContext, mUrl, mParam, "GET", true, new CallbackObjectResponse() {
                    @Override
                    public void onResponse(Object result) {
                        if (mOnDefaultCallbackListener != null)
                            mOnDefaultCallbackListener.onResponse(true, result);
                    }

                    @Override
                    public void onError(String error) {
                        LogPrint.d("onError :: " + error);
                        if ( TextUtils.isEmpty(error) ) {
                            if ( !Common.IsNetworkConnected(mContext)) {
                                try {
                                    JSONObject object = new JSONObject();
                                    object.put(Common.NETWORK_DISCONNECT, "Wi-Fi 혹은 모바일 데이터에 연결할 수 없습니다.확인 후 다시 시도해주세요.");
                                    if (mOnDefaultCallbackListener != null)
                                        mOnDefaultCallbackListener.onResponse(false, object);
                                } catch (Exception e) {
                                }
                            } else {
                                try {
                                    if ( SoftKeyboard.isKeyboardShow ) {
                                        JSONObject object = new JSONObject();
                                        object.put(Common.NETWORK_ERROR, "서버와의 통신이 원활하지 않습니다. 잠시 후 다시 시도해주세요.");
                                        if (mOnDefaultCallbackListener != null)
                                            mOnDefaultCallbackListener.onResponse(false, object);
                                    }
                                } catch (Exception e) {
                                }
                            }
                        } else {
                            if ( !Common.IsNetworkConnected(mContext)) {
                                try {
                                    JSONObject object = new JSONObject();
                                    object.put(Common.NETWORK_DISCONNECT, "Wi-Fi 혹은 모바일 데이터에 연결할 수 없습니다.확인 후 다시 시도해주세요.");
                                    if (mOnDefaultCallbackListener != null)
                                        mOnDefaultCallbackListener.onResponse(false, object);
                                } catch (Exception e) {
                                }
                            } else {
                                try {
                                    if ( SoftKeyboard.isKeyboardShow ) {
                                        JSONObject object = new JSONObject();
                                        object.put(Common.NETWORK_ERROR, "서버와의 통신이 원활하지 않습니다. 잠시 후 다시 시도해주세요.");
                                        if (mOnDefaultCallbackListener != null)
                                            mOnDefaultCallbackListener.onResponse(false, object);
                                    }
                                } catch (Exception e) {
                                }
                            }
                        }
                    }
                });
                break;
            case MODE_QUICK_LINK:
            case MODE_EMOJI_MATCHING:
            case MODE_LIVE_CHECK:
            case MODE_IMAGE_SEARCH:
            case MODE_CHARGE_POINT:
            case MODE_USER_MAX_POINT:
            case MODE_GET_USER_POINT:
            case MODE_THEME_LIST:
            //case MODE_NEW_THEME_LIST:
            case MODE_THEME_POPULAR:
            case MODE_ZERO_POINT:
            case MODE_SEND_KEYWORD:
            case MODE_KEYWORD_AD:
            case MODE_POP_AD:
            case MODE_AD_RATE:
            case MODE_THEME_DOWN:
            case MODE_MOBON_POST:
            case MODE_FREQUENCY:
            case MODE_SEND_SPOT_POINT:
            case MODE_GET_NEWS_VIEWCOUNT:
            case MODE_SAVE_NOTI_POINT:
            case MODE_SET_BANNER_POINT:
            case MODE_SEND_REWARD_POINT:
            case MODE_JOINT_FINISH:
            case MODE_NEW_REWARD_SEND_POINT:
            case MODE_BRAND_SEND_POINT:
                masyncApi =  new asyncApi(mContext, mUrl, mParam, "POST", new CallbackObjectResponse() {
                    @Override
                    public void onResponse(Object result) {

                        if ( result != null )
                            LogPrint.d("response result :: " + result.toString());
                        else
                            LogPrint.d("response result null");

                        try {
                            if (mOnDefaultCallbackListener != null)
                                mOnDefaultCallbackListener.onResponse(true, new JSONObject(result.toString()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        if ( TextUtils.isEmpty(error) ) {
                            if ( !Common.IsNetworkConnected(mContext) ) {
                                try {
                                    JSONObject object = new JSONObject();
                                    object.put(Common.NETWORK_DISCONNECT, "Wi-Fi 혹은 모바일 데이터에 연결할 수 없습니다.확인 후 다시 시도해주세요.");
                                    if (mOnDefaultCallbackListener != null)
                                        mOnDefaultCallbackListener.onResponse(false, object);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    if ( SoftKeyboard.isKeyboardShow ) {
                                        JSONObject object = new JSONObject();
                                        object.put(Common.NETWORK_ERROR, "서버와의 통신이 원활하지 않습니다. 잠시 후 다시 시도해주세요.");
                                        if (mOnDefaultCallbackListener != null)
                                            mOnDefaultCallbackListener.onResponse(false, object);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            if ( !Common.IsNetworkConnected(mContext) ) {
                                try {
                                    JSONObject object = new JSONObject();
                                    object.put(Common.NETWORK_DISCONNECT, "Wi-Fi 혹은 모바일 데이터에 연결할 수 없습니다.확인 후 다시 시도해주세요.");
                                    if (mOnDefaultCallbackListener != null)
                                        mOnDefaultCallbackListener.onResponse(false, object);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    if ( SoftKeyboard.isKeyboardShow ) {
                                        JSONObject object = new JSONObject();
                                        object.put(Common.NETWORK_ERROR, "서버와의 통신이 원활하지 않습니다. 잠시 후 다시 시도해주세요.");
                                        if (mOnDefaultCallbackListener != null)
                                            mOnDefaultCallbackListener.onResponse(false, object);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
                break;
            case MODE_OFFERWALL_MISSION_CLICK:
            case MODE_OFFERWALL_MISSION_PARTICIPATION:
            case MODE_OFFERWALL_MISSION_PARTICIPATION_CHECK:
                masyncApi =  new asyncApi(mContext, mUrl, mParam, "POST_PARAM_MAP", new CallbackObjectResponse() {
                    @Override
                    public void onResponse(Object result) {

                        if ( result != null )
                            LogPrint.d("response result :: " + result.toString());
                        else
                            LogPrint.d("response result null");

                        try {
                            if (mOnDefaultCallbackListener != null)
                                mOnDefaultCallbackListener.onResponse(true, new JSONObject(result.toString()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        LogPrint.d("error :: " + error);
                        if ( TextUtils.isEmpty(error) ) {
                            if ( !Common.IsNetworkConnected(mContext) ) {
                                try {
                                    JSONObject object = new JSONObject();
                                    object.put(Common.NETWORK_DISCONNECT, "Wi-Fi 혹은 모바일 데이터에 연결할 수 없습니다.확인 후 다시 시도해주세요.");
                                    if (mOnDefaultCallbackListener != null)
                                        mOnDefaultCallbackListener.onResponse(false, object);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    if ( SoftKeyboard.isKeyboardShow ) {
                                        JSONObject object = new JSONObject();
                                        object.put(Common.NETWORK_ERROR, "서버와의 통신이 원활하지 않습니다. 잠시 후 다시 시도해주세요.");
                                        if (mOnDefaultCallbackListener != null)
                                            mOnDefaultCallbackListener.onResponse(false, object);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            if ( !Common.IsNetworkConnected(mContext) ) {
                                try {
                                    JSONObject object = new JSONObject();
                                    object.put(Common.NETWORK_DISCONNECT, "Wi-Fi 혹은 모바일 데이터에 연결할 수 없습니다.확인 후 다시 시도해주세요.");
                                    if (mOnDefaultCallbackListener != null)
                                        mOnDefaultCallbackListener.onResponse(false, object);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    if ( SoftKeyboard.isKeyboardShow ) {
                                        JSONObject object = new JSONObject();
                                        object.put(Common.NETWORK_ERROR, "서버와의 통신이 원활하지 않습니다. 잠시 후 다시 시도해주세요.");
                                        if (mOnDefaultCallbackListener != null)
                                            mOnDefaultCallbackListener.onResponse(false, object);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
                break;
            case MODE_SEND_CHAT_NEW:
                masyncApi =  new asyncApi(mContext, mUrl, mParam, "post_chat", new CallbackObjectResponse() {
                    @Override
                    public void onResponse(Object result) {
                        if ( result != null )
                            LogPrint.d("response result :: " + result.toString());
                        else
                            LogPrint.d("response result null");

                        try {
                            if (mOnDefaultCallbackListener != null)
                                mOnDefaultCallbackListener.onResponse(true, new JSONObject(result.toString()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        if ( TextUtils.isEmpty(error) ) {
                            if ( !Common.IsNetworkConnected(mContext) ) {
                                try {
                                    JSONObject object = new JSONObject();
                                    object.put(Common.NETWORK_DISCONNECT, "Wi-Fi 혹은 모바일 데이터에 연결할 수 없습니다.확인 후 다시 시도해주세요.");
                                    if (mOnDefaultCallbackListener != null)
                                        mOnDefaultCallbackListener.onResponse(false, object);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    if ( SoftKeyboard.isKeyboardShow ) {
                                        JSONObject object = new JSONObject();
                                        object.put(Common.NETWORK_ERROR, "서버와의 통신이 원활하지 않습니다. 잠시 후 다시 시도해주세요.");
                                        if (mOnDefaultCallbackListener != null)
                                            mOnDefaultCallbackListener.onResponse(false, object);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            if ( !Common.IsNetworkConnected(mContext) ) {
                                try {
                                    JSONObject object = new JSONObject();
                                    object.put(Common.NETWORK_DISCONNECT, "Wi-Fi 혹은 모바일 데이터에 연결할 수 없습니다.확인 후 다시 시도해주세요.");
                                    if (mOnDefaultCallbackListener != null)
                                        mOnDefaultCallbackListener.onResponse(false, object);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    if ( SoftKeyboard.isKeyboardShow ) {
                                        JSONObject object = new JSONObject();
                                        object.put(Common.NETWORK_ERROR, "서버와의 통신이 원활하지 않습니다. 잠시 후 다시 시도해주세요.");
                                        if (mOnDefaultCallbackListener != null)
                                            mOnDefaultCallbackListener.onResponse(false, object);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });
                break;
            default:
                break;
        }
    }



    public interface OnDefaultObjectCallbackListener {
        public void onResponse(boolean result, Object obj);
    }

    public static void cancel(){
        if(masyncApi != null)
            masyncApi.cancel();
    }
}
