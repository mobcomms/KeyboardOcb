package com.enliple.keyboard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.enliple.keyboard.common.Common;
import com.enliple.keyboard.common.KeyboardLogPrint;
import com.enliple.keyboard.common.KeyboardUserIdModel;
import com.enliple.keyboard.common.UserIdDBHelper;
import com.enliple.keyboard.network.CustomAsyncTask;
import com.enliple.keyboard.network.Url;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2018-04-30.
 */

public class KeyboardEmptyActivity extends AppCompatActivity {
    private static Timer mTimer;
    private String mLink, mTotalUrl;
    private String mStrMessage;
    private int mCounter;
    private boolean mTimerRun = false; // start timer가 2번이상호출되는 현상이 있어 startTimer가 stopTimer 이후 다시 호출 됨 . 이를 막기위한 flag
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                try {
                    if ( !TextUtils.isEmpty(mStrMessage))
                        Toast.makeText(KeyboardEmptyActivity.this, mStrMessage, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    stopTimer(true);
//                    finish();
                }
            } else if ( msg.what == 1 ) {
                try {
                    KeyboardLogPrint.e("Push mStrMessage :: " + mStrMessage);
                    if ( !TextUtils.isEmpty(mStrMessage) )
                        Toast.makeText(KeyboardEmptyActivity.this, mStrMessage, Toast.LENGTH_SHORT).show();
                    stopTimer(true);
//                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    stopTimer(true);
//                    finish();
                }
            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KeyboardLogPrint.d("KeyboardEmptyActivity onCreate");
        Intent intent = getIntent();
        if ( intent != null ) {
            mTotalUrl = intent.getStringExtra("WEB_URL");
            mLink = intent.getStringExtra("LINK");
        }
    }

    public void onResume() {
        super.onResume();
        if ( mTimer == null ) {
            KeyboardLogPrint.e("KeyboardEmptyActivity onResume timer null");
            Common.onWebSiteOpen(KeyboardEmptyActivity.this, mTotalUrl);
        } else {
            KeyboardLogPrint.e("KeyboardEmptyActivity onResume timer not null");
            stopTimer(true);
//            finish();
        }
    }

    public void onPause() {
        super.onPause();
        if ( mTimer == null ) {
            KeyboardLogPrint.e("KeyboardEmptyActivity onPause timer null");
            startTimer();
        } else {
            KeyboardLogPrint.e("KeyboardEmptyActivity onPause timer not null");
//            stopTimer(true);
//            finish();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        KeyboardLogPrint.e("KeyboardEmptyActivity onDestroy");
    }

    private void startTimer() {
        if ( mTimerRun )
            return;

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        KeyboardLogPrint.e("KeyboardEmptyActivity startTimer");
        mTimerRun = true;
        mCounter = 0;
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new ForegroundTask(), 0, 900);
    }

    private void stopTimer(boolean isNull) {
        if (mTimer != null) {
            KeyboardLogPrint.e("KeyboardEmptyActivity stopTimer timer not null");
            mTimer.cancel();
            if ( isNull ) {
                mTimer = null;
                finish();
            }
        } else {
            KeyboardLogPrint.e("KeyboardEmptyActivity stopTimer timer null");
        }
        mCounter = 0;
    }

    private class ForegroundTask extends TimerTask {
        @Override
        public void run() {
            KeyboardLogPrint.e("forground task for point pay");
            if ( mCounter == 5 ) {
                KeyboardLogPrint.e("forground task counter five");

                UserIdDBHelper helper = new UserIdDBHelper(KeyboardEmptyActivity.this);
                KeyboardUserIdModel userInfoModel = helper.getUserInfo();

                String userId = userInfoModel.getUserId();
                String gubun = userInfoModel.getGubun();
                String deviceId = userInfoModel.getDeviceId();
                String url = "";
                if ( KeyboardLogPrint.debug )
                    url = Url.TEST_DOMAIN + Url.URL_AD_POINT;
                else
                    url = Url.DOMAIN + Url.URL_AD_POINT;

                KeyboardLogPrint.e("link userId :: " + userId + " and user gubun :: " + gubun);

                String totalUrl = url + mLink + "&service_code=01&user_id=" + userId + "&gubun=" + gubun;
                KeyboardLogPrint.e("totalUrl :::: " + totalUrl);
                CustomAsyncTask task = new CustomAsyncTask(KeyboardEmptyActivity.this);
                task.connectAdPoint(totalUrl, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                    @Override
                    public void onResponse(boolean rt, Object obj) {
                        stopTimer(false);
                        if ( rt ) {
                            try {
                                JSONObject object = (JSONObject) obj;
                                if ( object != null ) {
                                    boolean result = object.optBoolean("Result");
                                    String message = object.optString("errstr");
                                    if (result ) {
                                        mStrMessage = "포인트 지급이 완료되었습니다.";
                                        if ( mHandler != null )
                                            mHandler.sendEmptyMessage(1);
                                    } else {
                                        mStrMessage = message;
                                        if ( mHandler != null )
                                            mHandler.sendEmptyMessage(1);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                mStrMessage = "데이터가 올바르지 않습니다.";
                                if ( mHandler != null )
                                    mHandler.sendEmptyMessage(1);
                            }
                        } else {
                            mStrMessage = "";
                            if ( mHandler != null )
                                mHandler.sendEmptyMessage(1);
                        }
                    }
                });
            } else if (mCounter < 5 ) {
                KeyboardLogPrint.e("forground task counter less five");
                int remainTime = 5 - mCounter;
//                mStrMessage = remainTime + "초 후 포인트가 지급됩니다.";
                if ( mCounter%2 == 1 )
                    mStrMessage = "포인트 적립요청중입니다. 잠시 기다려 주시기 바랍니다.";
                else
                    mStrMessage = "";
                if ( mHandler != null )
                    mHandler.sendEmptyMessage(0);
            } else {
                KeyboardLogPrint.e("forground task counter else");
            }
            mCounter ++;
        }
    }
}
