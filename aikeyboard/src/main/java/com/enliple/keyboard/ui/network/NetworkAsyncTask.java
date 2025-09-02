package com.enliple.keyboard.ui.network;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Patterns;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import com.enliple.keyboard.network.CustomAsyncTask;
import com.enliple.keyboard.network.Url;;
import com.enliple.keyboard.ui.common.Common;
import com.enliple.keyboard.ui.common.LogPrint;
import com.enliple.keyboard.ui.common.UserIdModel;
import com.enliple.keyboard.ui.common.UserInfoModel;

import static android.content.Context.TELEPHONY_SERVICE;

//import org.apache.http.conn.util.InetAddressUtils;

/**
 * Created by Administrator on 2017-10-07.
 */

public class NetworkAsyncTask {
    private final int MODE_STORE_LIST = 1;
    private final int MODE_GET_COUPON = 2;
    private final int MODE_TOTAL_CPI = 4;
    private final int MODE_REWARD_TRY = 5;
    private final int MODE_TRY = 6;
    private final int MODE_COUPON_LIST = 7;
    private static final int MODE_FQ_LIST = 8;
    private static final int MODE_NOTI = 9;
    private static final int MODE_VERSION_INFO = 10;
    private static final int MODE_QA_LIST = 11;
    private static final int MODE_QA_WRITE = 12;
    private static final int MODE_QA_REPLY_LIST = 13;
    private static final int MODE_DELETE_QA = 14;
    private static final int MODE_MEMBER_OUT = 15;
    private static final int MODE_TUTORIAL_LIST = 16;
    private static final int MODE_LOGIN = 17;
    private static final int MODE_MAIN_V1 = 18;
    private static final int MODE_ID_CHECK = 19;
    private static final int MODE_DOCS = 20;
    private static final int MODE_JOIN_MEMBER = 21;
    private static final int MODE_GET_USER_INFO = 22;
    private static final int MODE_MY_PAGE = 23;
    private static final int MODE_ADPOPCORN_REQUIRE = 24;
    private static final int MODE_REWORD_REQUIRED = 25;
    private static final int MODE_PWD_RESET = 26;
    private static final int MODE_RANKING = 27;
    private static final int MODE_SAVING_LIST = 28;
    private static final int MODE_SAVING_LIST_DETAIL = 29;
    private static final int MODE_REC_DETAIL = 30;
    private static final int MODE_GET_REC_ID = 31;
    private static final int MODE_GET_USER_POINT = 32;
    private static final int MODE_AUTH_NO = 33;
    private static final int MODE_AUTH_NO_RESEND = 34;
    private static final int MODE_SEND_CPI_MAIL = 35;
    private static final int MODE_NO_HISTORY = 36;
    private static final int MODE_NOT_INSTALLED = 37;
    private static final int MODE_FIND_ID = 38;
    private static final int MODE_UPDATED_INFO = 39;
    private static final int MODE_CONFIG = 40;
    private static final int MODE_AD_SETTING = 41;
    private static final int MODE_SEND_CODE8 = 42;
    private static final int MODE_SHOPTREE_LIST = 43;
    private static final int MODE_SHOPTREE_DETAIL = 44;
    private static final int MODE_SHOPTREE_ORDERS = 45;
    private static final int MODE_SHOPTREE_GO_CART = 46;
    private static final int MODE_SHOPTREE_CART_LIST = 47;
    private static final int MODE_SHOPTREE_CART_DELETE = 48;
    private static final int MODE_SHOPTREE_CART_OPTION_DELETE = 49;
    private static final int MODE_SHOPTREE_PURCHASE_LIST = 50;
    private static final int MODE_SHOPTREE_COMPLAINTS = 51;
    private static final int MODE_AD_POINT = 52;
    private static final int MODE_SHOPTREE_ADDED_DELIVERYPAY = 53;
    private static final int MODE_AD_API = 54;
    private Context mContext = null;
    private int mModeIndex = 0;

    private CustomAsyncTask.CallbackObjectResponse callback;
    private OnDefaultObjectCallbackListener mOnDefaultCallbackListener = null;

    public String mUrl;
    public Map<String, String> mParam;

    public NetworkAsyncTask(Context context) {
        mContext = context;
    }

    public void connectVersionInfo(UserInfoModel model, String fcm_token, OnDefaultObjectCallbackListener defaultCallbackListener) {
        mUrl = Url.URL_VERSION_INFO;
        if ( LogPrint.debug )
            mUrl = Url.TEST_DOMAIN + Url.URL_VERSION_INFO;
        else
            mUrl = Url.DOMAIN + Url.URL_VERSION_INFO;
        LogPrint.e("fcm_token :: " + fcm_token);
        mOnDefaultCallbackListener = defaultCallbackListener;
        mParam = new HashMap<String, String>();
        mParam.put("userid", model.getUserId());
        mParam.put("gubun", model.getGubun());
        mParam.put("deviceid", model.getDeviceId());
        mParam.put("fcm_token", fcm_token);
        mParam.put("service_code", Common.SERVICE_CODE);

        mModeIndex = MODE_VERSION_INFO;
        execute();
    }


    public void execute() {
        switch (mModeIndex) {
            case MODE_SHOPTREE_LIST:
            case MODE_SHOPTREE_DETAIL:
            case MODE_SHOPTREE_CART_LIST:
            case MODE_SHOPTREE_PURCHASE_LIST:
            case MODE_SHOPTREE_COMPLAINTS:
            case MODE_AD_POINT:
            case MODE_SHOPTREE_ADDED_DELIVERYPAY:
            case MODE_AD_API:
                new com.enliple.keyboard.network.asyncApi(mContext, mUrl, mParam, "GET", new CustomAsyncTask.CallbackObjectResponse() {
                    @Override
                    public void onResponse(Object result) {

                        if (mOnDefaultCallbackListener != null)
                            mOnDefaultCallbackListener.onResponse(true, result);
                    }

                    @Override
                    public void onError(String error) {
                        try {
                            JSONObject object = new JSONObject();
                            object.put(Common.NETWORK_ERROR, error);
                            if (mOnDefaultCallbackListener != null)
                                mOnDefaultCallbackListener.onResponse(false, object);
                        } catch (Exception e) {

                        }
                    }
                });
                break;
//            case MODE_EMOJI_MATCHING:
//            case MODE_LIVE_CHECK:
            case MODE_STORE_LIST:
            case MODE_GET_COUPON:
            case MODE_TOTAL_CPI:
            case MODE_REWARD_TRY:
            case MODE_TRY:
            case MODE_REWORD_REQUIRED:
            case MODE_ADPOPCORN_REQUIRE:
            case MODE_COUPON_LIST:
            case MODE_FQ_LIST:
            case MODE_NOTI:
            case MODE_VERSION_INFO:
            case MODE_QA_LIST:
            case MODE_QA_WRITE:
            case MODE_QA_REPLY_LIST:
            case MODE_DELETE_QA:
            case MODE_MEMBER_OUT:
            case MODE_TUTORIAL_LIST:
            case MODE_LOGIN:
            case MODE_MAIN_V1:
            case MODE_ID_CHECK:
            case MODE_DOCS:
            case MODE_JOIN_MEMBER:
            case MODE_GET_USER_INFO:
            case MODE_MY_PAGE:
            case MODE_PWD_RESET:
            case MODE_RANKING:
            case MODE_SAVING_LIST:
            case MODE_SAVING_LIST_DETAIL:
            case MODE_REC_DETAIL:
            case MODE_GET_USER_POINT:
            case MODE_GET_REC_ID:
            case MODE_AUTH_NO:
            case MODE_AUTH_NO_RESEND:
            case MODE_SEND_CPI_MAIL:
            case MODE_NO_HISTORY:
            case MODE_NOT_INSTALLED:
            case MODE_FIND_ID:
            case MODE_UPDATED_INFO:
            case MODE_CONFIG:
            case MODE_AD_SETTING:
            case MODE_SEND_CODE8:
            case MODE_SHOPTREE_ORDERS:
            case MODE_SHOPTREE_GO_CART:
            case MODE_SHOPTREE_CART_DELETE:
            case MODE_SHOPTREE_CART_OPTION_DELETE:
                new com.enliple.keyboard.network.asyncApi(mContext, mUrl, mParam, "POST", new CustomAsyncTask.CallbackObjectResponse() {
                    @Override
                    public void onResponse(Object result) {
                        try {
                            if (mOnDefaultCallbackListener != null) {
                                mOnDefaultCallbackListener.onResponse(true, new JSONObject(result.toString()));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        try {
                            JSONObject object = new JSONObject();
                            object.put(Common.NETWORK_ERROR, error);
                            if (mOnDefaultCallbackListener != null)
                                mOnDefaultCallbackListener.onResponse(false, object);
                        } catch (Exception e) {
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
}
