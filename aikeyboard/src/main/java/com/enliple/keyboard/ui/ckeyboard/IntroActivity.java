package com.enliple.keyboard.ui.ckeyboard;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Insets;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.enliple.keyboard.R;
import com.enliple.keyboard.activity.AIKeyboardSDK;
import com.enliple.keyboard.common.Util;
import com.enliple.keyboard.ui.common.Common;
import com.enliple.keyboard.ui.common.DBHelper;
import com.enliple.keyboard.ui.common.Key;
import com.enliple.keyboard.ui.common.LogPrint;
import com.enliple.keyboard.ui.common.SharedPreference;
import com.enliple.keyboard.ui.common.UserIdModel;
import com.enliple.keyboard.ui.common.UserInfoModel;
import com.enliple.keyboard.ui.network.NetworkAsyncTask;

import org.json.JSONObject;

import java.lang.reflect.Method;


//import com.igaworks.IgawCommon;

/**
 * Created by Administrator on 2017-09-19.
 */

public class IntroActivity extends Activity {
    public static final int REQUEST_READ_PHONE_STATE = 2916;
    public static final int REQUEST_GET_ACCOUNT = 2831;
    private String mUserId = "";
    private String mGubun = "";
    private String mDeviceId = "";
    private NetworkAsyncTask mTask;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startCreate();
    }

    @Override
    public void onResume() {
        super.onResume();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        setScreenSize();
        super.onWindowFocusChanged(hasFocus);
    }

    private void setScreenSize() {
        Display display = getWindowManager().getDefaultDisplay();
        int realScreenWidth;
        int realScreenHeight;

        if (Build.VERSION.SDK_INT >= 17) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                WindowMetrics windowMetrics = wm.getCurrentWindowMetrics();
                Insets insets = windowMetrics.getWindowInsets().getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
                realScreenWidth = windowMetrics.getBounds().width() - insets.left - insets.right;
                LogPrint.d("realScreenWidth 2 :: " + realScreenWidth);
            } else {
                DisplayMetrics realMetrics = new DisplayMetrics();
                display.getRealMetrics(realMetrics);
                realScreenWidth = realMetrics.widthPixels;
                LogPrint.d("realScreenWidth 1 :: " + realScreenWidth);
            }

        } else if (Build.VERSION.SDK_INT >= 14) {
            //reflection for this weird in-between time
            try {
                Method mGetRawW = Display.class.getMethod("getRawWidth");
                realScreenWidth = (Integer) mGetRawW.invoke(display);
            } catch (Exception e) {
                //this may not be 100% accurate, but it's all we've got
                realScreenWidth = display.getWidth();
            }

        } else {
            //This should be close, as lower API devices should not have window navigation bars
            realScreenWidth = display.getWidth();
        }
        Rect rect = new Rect();
        Window win = this.getWindow();
        win.getDecorView().getWindowVisibleDisplayFrame(rect);
        realScreenHeight = rect.bottom - rect.top;

        SharedPreference.setInt(getApplicationContext(), Key.KEY_SCREEN_WIDTH, realScreenWidth);
        SharedPreference.setInt(getApplicationContext(), Key.KEY_SCREEN_HEIGHT, realScreenHeight);

        int deviceWidth = SharedPreference.getInt(getApplicationContext(), Key.KEY_SCREEN_WIDTH);
        LogPrint.e("deviceWidth :: " + deviceWidth);
    }

    private void startCreate() {
        String token = SharedPreference.getString(IntroActivity.this, Common.KEY_TOKEN);
        LogPrint.w("token :: " + token);
        if( token == null )
            token = "";

        boolean isADPushOn = SharedPreference.getBoolean(IntroActivity.this, Key.AD_NOTI_ON_OFF);
        AIKeyboardSDK.init(IntroActivity.this).SetDefaultTheme();
        Intent intent = new Intent(IntroActivity.this, KeyboardLoadActivity.class);

        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
        /**
        AIKeyboardSDK.init(IntroActivity.this).SetADPushStatus(isADPushOn);
        // 회원 로그인 정보에 아무 값이 없다면 device 아이디를 얻어와 저장한다.
        DBHelper helper = new DBHelper(IntroActivity.this);
        boolean isUserInfoExist = helper.isUserIdExist();
        if ( !isUserInfoExist ) {
            String deviceId = Common.getUuid(IntroActivity.this);
            helper.insertUserId("", "", deviceId);
        }
        UserIdModel model = helper.getUserId();
        if ( model != null ) {
            mUserId = model.getUserId();
            mGubun = model.getGubun();
            mDeviceId = model.getDeviceId();
        }

        LogPrint.e("IntroActivity mUserId : " + mUserId);
        LogPrint.e("IntroActivity mGubun : " + mGubun);
        LogPrint.e("IntroActivity mDeviceId : " + mDeviceId);

        mTask = new NetworkAsyncTask(IntroActivity.this);
        UserInfoModel ui_model = new UserInfoModel();
        if ( ui_model != null ) {
            ui_model.setUserId(mUserId);
            ui_model.setGubun(mGubun);
            ui_model.setDeviceId(mDeviceId);
        }

        mTask.connectVersionInfo(ui_model, token, new NetworkAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean rt, Object obj) {
                if ( rt ) {
                    try {
                        JSONObject jsonObject = (JSONObject) obj;
                        boolean result = false;
                        String recommendPoint = "";
                        String mobonStatus = "";
                        String doNotSeeText = "";
                        String phoneName = "";
                        result = jsonObject.optBoolean("Result", false);

                        if ( result ) {
                            recommendPoint = jsonObject.optString("recommValue");
                            mobonStatus = jsonObject.optString("mobonstatus");
                            doNotSeeText = jsonObject.optString("pop_notice_day");
                            phoneName = jsonObject.optString("phone_name");

                            SharedPreference.setString(IntroActivity.this, Key.RECOMMEND_POINT, recommendPoint);
                            SharedPreference.setString(IntroActivity.this, Key.MOBON_STATUS, mobonStatus);
                            SharedPreference.setString(IntroActivity.this, Key.DO_NOT_SEE_DATE, doNotSeeText);
                            SharedPreference.setString(IntroActivity.this, Key.PHONE_NAME, phoneName);

                            DBHelper helper = new DBHelper(IntroActivity.this);
                            boolean isUserInfoExist = helper.isUserIdExist();
                            String userId = "";
                            String deviceId = "";
                            String gubun = "";
                            if ( isUserInfoExist ) {
                                userId = helper.getUserId().getUserId();
                                deviceId = helper.getUserId().getDeviceId();
                                gubun = helper.getUserId().getGubun();
                            }
                            LogPrint.e("network success intro");
                            Intent intent = new Intent(IntroActivity.this, KeyboardLoadActivity.class);
                            intent.putExtra("NETWORK_STATE", "Y");
                            intent.putExtra("USER_ID", userId);
                            intent.putExtra("DEVICE_ID", deviceId);
                            intent.putExtra("GUBUN", gubun);

                            startActivity(intent);
                            finish();
                            overridePendingTransition(0, 0);
                        } else {
                            DBHelper helper = new DBHelper(IntroActivity.this);
                            boolean isUserInfoExist = helper.isUserIdExist();
                            String userId = "";
                            String deviceId = "";
                            String gubun = "";
                            if ( isUserInfoExist ) {
                                userId = helper.getUserId().getUserId();
                                deviceId = helper.getUserId().getDeviceId();
                                gubun = helper.getUserId().getGubun();
                            }
                            Intent intent = new Intent(IntroActivity.this, KeyboardLoadActivity.class);
                            intent.putExtra("NETWORK_STATE", "N");
                            intent.putExtra("USER_ID", userId);
                            intent.putExtra("DEVICE_ID", deviceId);
                            intent.putExtra("GUBUN", gubun);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(0, 0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        DBHelper helper = new DBHelper(IntroActivity.this);
                        boolean isUserInfoExist = helper.isUserIdExist();
                        String userId = "";
                        String deviceId = "";
                        String gubun = "";
                        if ( isUserInfoExist ) {
                            userId = helper.getUserId().getUserId();
                            deviceId = helper.getUserId().getDeviceId();
                            gubun = helper.getUserId().getGubun();
                        }
                        Intent intent = new Intent(IntroActivity.this, KeyboardLoadActivity.class);
                        intent.putExtra("NETWORK_STATE", "N");
                        intent.putExtra("USER_ID", userId);
                        intent.putExtra("DEVICE_ID", deviceId);
                        intent.putExtra("GUBUN", gubun);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0);
                    }
                } else { // 통신이 안되서 error가 떨어져도 이용을 할 수 있게해야함
                    DBHelper helper = new DBHelper(IntroActivity.this);
                    boolean isUserInfoExist = helper.isUserIdExist();
                    String userId = "";
                    String deviceId = "";
                    String gubun = "";
                    if ( isUserInfoExist ) {
                        userId = helper.getUserId().getUserId();
                        deviceId = helper.getUserId().getDeviceId();
                        gubun = helper.getUserId().getGubun();
                    }
                    Intent intent = new Intent(IntroActivity.this, KeyboardLoadActivity.class);
                    intent.putExtra("NETWORK_STATE", "N");
                    intent.putExtra("USER_ID", userId);
                    intent.putExtra("DEVICE_ID", deviceId);
                    intent.putExtra("GUBUN", gubun);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(0, 0);
                }
            }
        });**/
    }
}
