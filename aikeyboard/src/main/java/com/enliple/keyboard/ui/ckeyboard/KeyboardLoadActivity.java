package com.enliple.keyboard.ui.ckeyboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.enliple.keyboard.CKeyboard;
import com.enliple.keyboard.R;
import com.enliple.keyboard.activity.AIKeyboardSDK;
import com.enliple.keyboard.activity.KeyboardSettingsActivity;
import com.enliple.keyboard.common.Util;
import com.enliple.keyboard.ui.common.LogPrint;
import com.enliple.keyboard.ui.common.UserIdModel;

import java.util.ArrayList;

import static com.enliple.keyboard.common.Common.BUILD_APP;

/**
 * Created by Administrator on 2017-10-19.
 * 키보드 sdk 로드 시키는 클래스
 */

public class KeyboardLoadActivity extends Activity {
    private String mNetState;
    private UserIdModel model;
    private String mUserId = "";
    private String mGubun = "";
    private String mDeviceId = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
        Intent intent = getIntent();

        if ( intent != null ) {
            mNetState = intent.getStringExtra("NETWORK_STATE");
            mUserId = intent.getStringExtra("USER_ID");
            mDeviceId = intent.getStringExtra("DEVICE_ID");
            mGubun = intent.getStringExtra("GUBUN");
            LogPrint.e("KeyboardLoadActivity mUserId :: " + mUserId);
            LogPrint.e("KeyboardLoadActivity mDeviceId :: " + mDeviceId);
            LogPrint.e("KeyboardLoadActivity mGubun :: " + mGubun);
            if ( !TextUtils.isEmpty(mUserId) || !TextUtils.isEmpty(mGubun) || !TextUtils.isEmpty(mDeviceId) ) {
                ArrayList<String> array = AIKeyboardSDK.init(KeyboardLoadActivity.this).GetUserId();
                if ( array == null ) {
                    LogPrint.e("KeyboardLoadActivity array is null");
                    AIKeyboardSDK.init(KeyboardLoadActivity.this).SetUserId(mUserId, mGubun, mDeviceId);
                } else {
                    LogPrint.e("KeyboardLoadActivity array is not  null");
                    String userId = array.get(AIKeyboardSDK.USER_ID);
                    String gubun = array.get(AIKeyboardSDK.GUBUN);
                    String deviceId = array.get(AIKeyboardSDK.DEVICE_ID);
                    if ( TextUtils.isEmpty(userId) && TextUtils.isEmpty(gubun) && TextUtils.isEmpty(deviceId) ) {
                        AIKeyboardSDK.init(KeyboardLoadActivity.this).SetUserId(mUserId, mGubun, mDeviceId);
                    }
                }
            }
        } else {
            finish();
            overridePendingTransition(0, 0);
            return;
        }
**/
        if (CKeyboard.isSelectedCKeyboard(this)) {
            if(Util.getAppName(getApplicationContext()).equals(BUILD_APP)) {
                // 기존 키보드에서는 앱으로 넘어갈때 여기서 앱의 메인 화면을 호출했음.
            } else
                startActivity(new Intent(KeyboardLoadActivity.this, KeyboardSettingsActivity.class));

            finish();
            overridePendingTransition(0, 0);
        } else {
            if (Util.getAppName(getApplicationContext()).equals(com.enliple.keyboard.common.Common.BUILD_SDK_WHOWHO))
                AIKeyboardSDK.init(KeyboardLoadActivity.this).RunKeyboardSetting(getResources().getString(R.string.app_name),
                    "app_icon", "img_intro_bg_whowho", "img_setting_bg_whowho", mUserId, mGubun, mDeviceId);
            else
                AIKeyboardSDK.init(KeyboardLoadActivity.this).RunKeyboardSetting(getResources().getString(R.string.app_name),
                        "app_icon", "img_intro_bg", "img_setting_bg", mUserId, mGubun, mDeviceId);
            AIKeyboardSDK.setDebug(LogPrint.debug);
            finish();
            overridePendingTransition(0, 0);
        }
    }
}
