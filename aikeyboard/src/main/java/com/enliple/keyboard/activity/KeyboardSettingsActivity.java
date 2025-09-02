package com.enliple.keyboard.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import com.enliple.keyboard.Cipher.E_Cipher;
import com.enliple.keyboard.R;
import com.enliple.keyboard.ad.Listener;
import com.enliple.keyboard.common.Common;
import com.enliple.keyboard.common.KeyboardLogPrint;
import com.enliple.keyboard.common.ThemeManager;
import com.enliple.keyboard.common.ThemeModel;
import com.enliple.keyboard.common.Util;
import com.enliple.keyboard.network.CustomAsyncTask;
import com.enliple.keyboard.ui.ckeyboard.IntroActivity;
import com.enliple.keyboard.ui.common.Key;
import com.enliple.keyboard.ui.common.LogPrint;
import com.rake.android.rkmetrics.RakeAPI;
import com.skplanet.pdp.sentinel.shuttle.OCBLogSentinelShuttle;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class KeyboardSettingsActivity extends Activity implements View.OnClickListener, KeyboardView.OnKeyboardActionListener {
    private static final int SOUND_0 = 0;
    private static final int SOUND_1 = 1;
    private static final int SOUND_2 = 2;
    private static final int SOUND_3 = 3;
    private static final int SOUND_4 = 4;
    public static Activity mActivity = null;
    private SeekBar mVolumeSeek = null;
    private SeekBar mVibrateSeek = null;

    private RelativeLayout mKindLayer = null;
    private RelativeLayout mKeyboardSizeLayer = null;
    private RelativeLayout mChangeSoundLayer = null;
    private RelativeLayout mThemeLayer = null;
    private Drawable mBgDrawable;
    private TextView mKindTxt = null;
    private ScrollView scroll;
    private RelativeLayout numOnOff, previewOnOff, vibrateOnOff, newsOnOff = null;
    private View numBg, numHead, previewBg, previewHead, vibrateBg, vibrateHead, newsBg, newsHead;
    private ImageView mBtnHideAd = null;
    private boolean mPreviewSet = false;
    private boolean mNewsSet = false;
    private boolean mQwertyNumSet = false;
    private boolean mHideAdSet = false;
    private CharSequence[] items = null;
    private View root_layout;
    private SoundPool mSoundPool = null;
    private int mSoundPosition = 0;
    private int mDialogSelect = 0;

    private Vibrator mVibrator = null;
    ArrayList<Integer> mResArray = null;
    private String mKeyboardName = null;
    private int mMediaVolume = 0;
    private AudioManager mAudioManager;
    private int mCalcVolume = 0;
    private long mPlayTime = 0;
    private long mVibrateTime = 0;
    private int mVibrate = 0;

    // for keyboard size
    private LatinKeyboardView kv;
    private SeekBar mSizeSeek = null;
    private volatile int mSizeLevel = 0;

    private ConstraintLayout mKeyboardViewLayer;
    private RelativeLayout mMatchLayer;

    // theme 적용
    private ImageView mImgFirst; // 이모지
    private ImageView mImgTimedeal; // 타임딜
    private ImageView mImgSecond; // 쿠팡
    private ImageView mImgShopping; // 쇼핑
    private ImageView mImgFourth; // my
    private ImageView mImgMore; // 설정
    private RelativeLayout mFirstTabLayer = null; // 이모지
    private RelativeLayout mTimedealTabLayer = null;// 타임딜
    private RelativeLayout mSecondTabLayer = null;//쿠팡
    private RelativeLayout mShoppingTabLayer = null;//쇼핑
    private RelativeLayout mFourthLayer = null;// my
    private RelativeLayout mMoreLayer = null; // 설정
    private RelativeLayout mGameLayer = null;

    private ConstraintLayout promotionLayer;
    private TextView txt_promotion;

    private TextView mTopLine;
    private int mStreamId = -1;
    private int mDefStreamId = -1;
    private ArrayList<Integer> mSoundIdArray;
    private boolean mHeaderVisible = true;
    private ThemeModel mThemeModel;
    private int mBgAlpha = 150;

    private RelativeLayout unuse_keyboard;
    private RelativeLayout seekLayout;
    private TextView txt_unuse;
    private Timer mTimer = null;
    private TextView sound_kind_txt;
    boolean isNotSet = false;
    private RakeAPI rake;
    private boolean isSizeSeekStarted = false;

    private int keyboardHeight = 0;

    private ConstraintLayout keyboard_total_layer;
    private ConstraintLayout ppz_guide_layer;
    private ConstraintLayout gpt_guide_layer;
    private CountDownTimer countDownTimer;
    private boolean isGuideVisibled;
    public void setRake(String page_id, String action_id) {
        new Thread() {
            public void run() {
                String track_id = SharedPreference.getString(KeyboardSettingsActivity.this, Key.KEY_OCB_TRACK_ID);
                String device_id = SharedPreference.getString(KeyboardSettingsActivity.this, Key.KEY_OCB_DEVICE_ID);
                try {
                    E_Cipher cp = E_Cipher.getInstance();
                    track_id = cp.Decode(KeyboardSettingsActivity.this, track_id);
                    device_id = cp.Decode(KeyboardSettingsActivity.this, device_id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String time = new SimpleDateFormat("yyyyMMddHHmmssSS").format(new Date());
                String session_id = time + "_" + device_id;
                OCBLogSentinelShuttle shuttle = new OCBLogSentinelShuttle();
                shuttle.page_id(page_id).action_id(action_id).keyboard_log_yn("yes").session_id(session_id).mbr_id(track_id);
                rake.track(shuttle.toJSONObject());
            }
        }.start();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ( CustomAsyncTask.GUBUN_RELEASE.equals(CustomAsyncTask.gubun) )
            rake = RakeAPI.getInstance(KeyboardSettingsActivity.this, Common.LIVE_TOKEN, RakeAPI.Env.LIVE, RakeAPI.Logging.DISABLE);
        else
            rake = RakeAPI.getInstance(KeyboardSettingsActivity.this, Common.DEV_TOKEN, RakeAPI.Env.DEV, RakeAPI.Logging.DISABLE);
        String ocbKeyboard = Common.TARGET_PACKAGENAME + "/com.enliple.keyboard.activity.SoftKeyboard";
        String currentKeyboard = Settings.Secure.getString(getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
        if ( !currentKeyboard.equals(ocbKeyboard) ) {
            isNotSet = true;
            startActivity(new Intent(KeyboardSettingsActivity.this, IntroActivity.class));
//            Toast.makeText(KeyboardSettingsActivity.this, "OK캐쉬백 돈버는 키보드를 기본키보드로 지정해주세요.", Toast.LENGTH_SHORT).show();
            finish();
        } else {

        }

        if ( !isNotSet ) {
            initKeyboard();
        }
    }

    private void initKeyboard() {
        setRake("/keyboard/setting", "");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.aikbd_activity_ocb_keyboard_setting);
        mActivity = this;
        Intent intent = getIntent();
        if (intent != null) {
            String headerVisible = intent.getStringExtra("HIDE_HEADER");
            if ("N".equals(headerVisible))
                mHeaderVisible = false;
            else
                mHeaderVisible = true;
        }

        root_layout = findViewById(R.id.root_layout);

        Common.SetInset(root_layout);

        unuse_keyboard = findViewById(R.id.unuse_keyboard);
        txt_unuse = findViewById(R.id.txt_unuse);
        //unuse_keyboard.setOnClickListener(this); // 해당 기능 제거 요청 from. 신효민 매니저 20210430

        scroll = findViewById(R.id.scroll);
        scroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (keyboard_total_layer.getVisibility() == View.VISIBLE) {
                    LogPrint.d("keyboard gone scrolled");
                    if ( ppz_guide_layer.getVisibility() == View.VISIBLE ) {
                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                            countDownTimer = null;
                        }
                        fadeOutAnimation(ppz_guide_layer);
                        fadeInAnimation(gpt_guide_layer);
                    } else if ( gpt_guide_layer.getVisibility() == View.VISIBLE ) {
                        if (countDownTimer != null) {
                            countDownTimer.cancel();
                            countDownTimer = null;
                        }
                        fadeOutAnimation(gpt_guide_layer);
                        if ( keyboard_total_layer != null ) {
                            LogPrint.d("skkim keyboard_total_layer gone 1");
                            keyboard_total_layer.setVisibility(View.GONE);
                        }
                    } else {
                        LogPrint.d("skkim keyboard_total_layer gone 2");
                        keyboard_total_layer.setVisibility(View.GONE);
                    }
                }
            }
        });

        SpannableString content = new SpannableString(txt_unuse.getText().toString());
        content.setSpan(new UnderlineSpan(), 0, txt_unuse.getText().toString().length(), 0);
        txt_unuse.setText(content);

        mFirstTabLayer = (RelativeLayout) findViewById(R.id.first_tab);
        mTimedealTabLayer = (RelativeLayout) findViewById(R.id.timedeal_tab);
        mSecondTabLayer = (RelativeLayout) findViewById(R.id.second_tab);
        mShoppingTabLayer = (RelativeLayout) findViewById(R.id.shopping_tab);
        mFourthLayer = (RelativeLayout) findViewById(R.id.fourth_tab);
        seekLayout = (RelativeLayout) findViewById(R.id.seek_layout);
        mMoreLayer = findViewById(R.id.more_tab);
        mGameLayer = findViewById(R.id.game_tab);
        mImgFirst = (ImageView) findViewById(R.id.img_first);
        mImgTimedeal = (ImageView) findViewById(R.id.img_timedeal);
        mImgSecond = (ImageView) findViewById(R.id.img_second);
        mImgShopping = (ImageView) findViewById(R.id.img_shopping);
        mImgFourth = (ImageView) findViewById(R.id.img_fourth);
        mImgMore = findViewById(R.id.img_more);
        promotionLayer = findViewById(R.id.promotionLayer);
        txt_promotion = findViewById(R.id.txt_promotion);

        keyboard_total_layer = findViewById(R.id.keyboard_total_layer);
        ppz_guide_layer = findViewById(R.id.ppz_guide_layer);
        gpt_guide_layer = findViewById(R.id.gpt_guide_layer);

        mTopLine = (TextView) findViewById(R.id.top_line);
//        if (mHeaderVisible) {
//            mHomeLayer.setVisibility(View.VISIBLE);
//        } else
//            mHomeLayer.setVisibility(View.GONE);

        Common.GetGameStatus(KeyboardSettingsActivity.this, new Listener.OnGameStatusListener() {
            @Override
            public void received(String status) {
                if ( mGameLayer == null )
                    return;
                if ("N".equals(status)) {
                    if (mGameLayer.getVisibility() == View.VISIBLE) {
                        mGameLayer.setVisibility(View.GONE);
                    }
                } else {
                    if (mGameLayer.getVisibility() == View.GONE) {
                        mGameLayer.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        findViewById(R.id.top_layer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goneKeyboard();
            }
        });

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRake("/keyboard/setting", "top_tap.backbtn");
                if ( keyboard_total_layer.getVisibility() == View.VISIBLE ) {
                    LogPrint.d("skkim keyboard_total_layer gone 3");
                    keyboard_total_layer.setVisibility(View.GONE);
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                        countDownTimer = null;
                    }
                } else {
                    finish();
                    overridePendingTransition(0, 0);
                }
            }
        });

        findViewById(R.id.btn_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRake("/keyboard/setting", "top_tap.homebtn");
                if ( keyboard_total_layer.getVisibility() == View.VISIBLE ) {
                    LogPrint.d("skkim keyboard_total_layer gone 4");
                    keyboard_total_layer.setVisibility(View.GONE);
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                        countDownTimer = null;
                    }
                } else {
                    try {
                        String url = "ocbt://com.skmc.okcashbag.home_google/main";
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        ppz_guide_layer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                    countDownTimer = null;
                }
                fadeOutAnimation(ppz_guide_layer);
                fadeInAnimation(gpt_guide_layer);
            }
        });

        gpt_guide_layer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                    countDownTimer = null;
                }
                fadeOutAnimation(gpt_guide_layer);
                if ( keyboard_total_layer != null ) {
                    LogPrint.d("skkim keyboard_total_layer gone 5");
                    keyboard_total_layer.setVisibility(View.GONE);
                }
            }
        });

        sound_kind_txt = findViewById(R.id.sound_kind_txt);

        mKeyboardViewLayer = (ConstraintLayout) findViewById(R.id.keyboard_view_layer);
        mMatchLayer = (RelativeLayout) findViewById(R.id.match_container);

//        mKeyboardViewLayer.setVisibility(View.GONE);
//        mMatchLayer.setVisibility(View.GONE);
        LogPrint.d("skkim keyboard_total_layer gone 6");
        keyboard_total_layer.setVisibility(View.GONE);
        
        mKeyboardName = SharedPreference.getString(KeyboardSettingsActivity.this, Common.PREF_KEYBOARD_NAME);
        mKeyboardName = getResources().getString(R.string.ocb_keyboard_name);
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMediaVolume = SharedPreference.getInt(KeyboardSettingsActivity.this, Common.MEDIA_VOLUME_LEVEL);
        if (mMediaVolume <= 0) {
            mMediaVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            SharedPreference.setIntCommit(KeyboardSettingsActivity.this, Common.MEDIA_VOLUME_LEVEL, mMediaVolume);
        }



        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        items = getResources().getStringArray(R.array.aikbd_sound_array);
        initSound();
        mPreviewSet = SharedPreference.getBoolean((KeyboardSettingsActivity.this), Common.PREF_PREVIEW_SETTING);
        mQwertyNumSet = SharedPreference.getBoolean((KeyboardSettingsActivity.this), Common.PREF_QWERTY_NUM_SETTING);
        mNewsSet = SharedPreference.getBoolean((KeyboardSettingsActivity.this), Common.PREF_NEWS_SETTING);
        mSizeSeek = (SeekBar) findViewById(R.id.size_seek);
        LogPrint.d("create news :: " + SharedPreference.getBoolean(KeyboardSettingsActivity.this, Common.PREF_NEWS_SETTING));
        mSizeLevel = SharedPreference.getInt(KeyboardSettingsActivity.this, Common.PREF_KEYBOARD_SIZE_LEVEL) < 0 ? Common.DEFAULT_KEYBOARD_SIZE : SharedPreference.getInt(KeyboardSettingsActivity.this, Common.PREF_KEYBOARD_SIZE_LEVEL);
        kv = (LatinKeyboardView) findViewById(R.id.keyboard_view);
        setKeyboard(mPreviewSet, mSizeLevel);
        kv.setOnKeyboardActionListener(this);

        mSizeSeek.setProgress(mSizeLevel / 3);

        mKindLayer = (RelativeLayout) findViewById(R.id.kind_layout);
        mChangeSoundLayer = (RelativeLayout) findViewById(R.id.change_sound_layout);
        mThemeLayer = (RelativeLayout) findViewById(R.id.theme_layout);

        mKindLayer.setOnTouchListener(mTouchListener);
        mChangeSoundLayer.setOnTouchListener(mTouchListener);
        mThemeLayer.setOnTouchListener(mTouchListener);

        mKindTxt = (TextView) findViewById(R.id.kind_txt);
        mVolumeSeek = (SeekBar) findViewById(R.id.volume_seek);
        mVibrateSeek = (SeekBar) findViewById(R.id.vibrate_seek);

        numOnOff = findViewById(R.id.numOnOff);
        numBg = findViewById(R.id.numBg);
        numHead = findViewById(R.id.numHead);
        numOnOff.setOnClickListener(this);

        previewOnOff = findViewById(R.id.previewOnOff);
        previewBg = findViewById(R.id.previewBg);
        previewHead = findViewById(R.id.previewHead);
        previewOnOff.setOnClickListener(this);

        newsOnOff = findViewById(R.id.newsOnOff);
        newsBg = findViewById(R.id.newsBg);
        newsHead = findViewById(R.id.newsHead);
        newsOnOff.setOnClickListener(this);

        vibrateOnOff = findViewById(R.id.vibrateOnOff);
        vibrateBg = findViewById(R.id.vibrateBg);
        vibrateHead = findViewById(R.id.vibrateHead);
        vibrateOnOff.setOnClickListener(this);

        mBtnHideAd = (ImageView) findViewById(R.id.btn_hide_ad_set);
        mBtnHideAd.setOnClickListener(this);

        if (mPreviewSet) {
            setRadio(previewBg, previewHead, true);
        } else {
            setRadio(previewBg, previewHead, false);
        }

        if (mQwertyNumSet) {
            setRadio(numBg, numHead, true);
        } else {
            setRadio(numBg, numHead, false);
        }

        if (mNewsSet) {
            setRadio(newsBg, newsHead, true);
        } else {
            setRadio(newsBg, newsHead, false);
        }

        if (isTimePassed())
            mHideAdSet = false;
        else
            mHideAdSet = true;

        if (mHideAdSet) {
            mBtnHideAd.setBackgroundResource(R.drawable.aikbd_btn_slide_on);
        } else {
            mBtnHideAd.setBackgroundResource(R.drawable.aikbd_btn_slide_off);
        }



        int volLevel = SharedPreference.getInt(KeyboardSettingsActivity.this, Common.PREF_I_VOLUME_LEVEL);

        long vibrateLevel = SharedPreference.getLong(KeyboardSettingsActivity.this, Common.PREF_VIBRATE_LEVEL);
        mVibrate = (int) vibrateLevel;

        if (volLevel < 0) {
            SharedPreference.setInt(KeyboardSettingsActivity.this, Common.PREF_I_VOLUME_LEVEL, Common.DEFAULT_SOUND_LEVEL);
            volLevel = SharedPreference.getInt(KeyboardSettingsActivity.this, Common.PREF_I_VOLUME_LEVEL);
            mCalcVolume = Common.DEFAULT_SOUND_LEVEL;
        } else {
            mCalcVolume = volLevel;
        }

        if (vibrateLevel < 0) {
            SharedPreference.setLong(KeyboardSettingsActivity.this, Common.PREF_VIBRATE_LEVEL, Common.DEFAULT_VIBRATE_LEVEL);
            vibrateLevel = SharedPreference.getLong(KeyboardSettingsActivity.this, Common.PREF_VIBRATE_LEVEL);
            mVibrate = (int) vibrateLevel;
        }

//        mVibrateLevel.setText(String.valueOf(vibrateLevel));
//        int iVibrate = mVibrate * 2; // 2017.08.25 대표님 지시사항으로 저장되는 vibrate 값이 1/2로 줄어듬 표시되는 것은 기존과 동일하게 하기 위해
        //mVibrateLevel.setText("" + (mVibrate * 10));

        mVolumeSeek.setProgress(mCalcVolume);

        mVolumeSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                int calcVol = (int)Math.round(progress/10.0)*10;
                mCalcVolume = progress;
                SharedPreference.setInt(KeyboardSettingsActivity.this, Common.PREF_I_VOLUME_LEVEL, mCalcVolume); // 2017.11.08 seek bar 움직일 때마다 저장하도록 변경
                try {
                    if (mSoundPool != null) {
//                        float fLevel = Common.getVolume("Keyboard_Setting_Activity", mMediaVolume, mCalcVolume);
                        float fLevel = getVolume();
                        KeyboardLogPrint.d("volume fLevel : " + fLevel);
                        int soundResId = mResArray.get(SharedPreference.getInt(KeyboardSettingsActivity.this, Common.PREF_SELECTED_SOUND));
                        if (mDefStreamId != -1)
                            mSoundPool.stop(mDefStreamId);
                        mDefStreamId = mSoundPool.play(soundResId, fLevel, fLevel, 0, 0, 1);
                    } else {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                long currentTime = System.currentTimeMillis();
//
//                if ( (currentTime - mPlayTime) > 100 )
//                {
//                    mPlayTime = currentTime;
//                    try {
//                        if ( mSoundPool != null )
//                        {
//                            float fLevel = Common.getVolume("Keyboard_Setting_Activity", mMediaVolume, mCalcVolume);
//                            mSoundPool.play(mResArray.get(SharedPreference.getInt(Keyboard_Settings_Activity.this, Common.PREF_SELECTED_SOUND)), fLevel, fLevel, 0, 0, 1);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
                //mVolumeLevel.setText((progress * 10) + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                KeyboardLogPrint.e("mVolumeSeek onStartTrackingTouch");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setRake("/keyboard/setting", "drag.keyboardvolume");
                KeyboardLogPrint.e("mVolumeSeek onStopTrackingTouch, mCalcValue :: " + mCalcVolume + " , mMediaVolume :: " + mMediaVolume);
                SharedPreference.setIntCommit(KeyboardSettingsActivity.this, Common.PREF_I_VOLUME_LEVEL, mCalcVolume); // 2017.11.08 seek bar 움직일 때마다 저장하도록 변경
                try {
                    if (mSoundPool != null) {
                        float fLevel = Common.getVolume("Keyboard_Setting_Activity", mMediaVolume, mCalcVolume);
                        int soundResId = mResArray.get(SharedPreference.getInt(KeyboardSettingsActivity.this, Common.PREF_SELECTED_SOUND));
                        if (mDefStreamId != -1)
                            mSoundPool.stop(mDefStreamId);
                        mDefStreamId = mSoundPool.play(soundResId, fLevel, fLevel, 0, 0, 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mVibrateSeek.setProgress(mVibrate);
        if ( mVibrate <= 0 ) {
            setRadio(vibrateBg, vibrateHead, false);
        } else {
            setRadio(vibrateBg, vibrateHead, true);
        }
        mVibrateSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mVibrate = progress;

                if (mVibrator != null) {
                    KeyboardLogPrint.w("mVibrate 1:: " + mVibrate);
                    if ( mVibrate > 0 ) {
                        SharedPreference.setLong(KeyboardSettingsActivity.this, Common.PREF_VIBRATE_LEVEL, mVibrate); // 2017.11.08 프로그래스바 이동시마다 seekbar 값을 저장
                        int amplitude = 225 + ((int)mVibrate * 3);
                        if ( mVibrate == 0 )
                            amplitude = 0;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            mVibrator.vibrate(VibrationEffect.createOneShot(5, amplitude));
                        } else {
                            mVibrator.vibrate(mVibrate * Common.VIBRATE_MUL);
                        }
//                    mVibrator.vibrate(mVibrateLevel * Common.VIBRATE_MUL);

                    }
                }

                if ( mVibrate <= 0 ) {
                    setRadio(vibrateBg, vibrateHead, false);
                } else {
                    setRadio(vibrateBg, vibrateHead, true);
                }

                /**
                 if (mVibrateLevel != null)
                 mVibrateLevel.setText("" + (mVibrate * 10));**/
//                mVibrate = (int)Math.round(progress/10.0)*10;
//                long currentTime = System.currentTimeMillis();
//                if ( (currentTime - mVibrateTime) > 100 )
//                {
//                    mVibrateTime = currentTime;
//                    if ( mVibrator != null ) {
//                        KeyboardLogPrint.w("mVibrate :: " + mVibrate);
////                        mVibrator.vibrate(mVibrate); // 2017.08.25 대표님 지시사항으로 vibrate 를 전체적으로 1/2로 줄임. 표시는 기존과 동일하게함
//                        int iVibrate = mVibrate / 2;
//                        KeyboardLogPrint.w("iVibrate :: " + iVibrate);
//                        mVibrator.vibrate(iVibrate);
//                    }
//                    if ( mVibrateLevel != null )
//                        mVibrateLevel.setText(String.valueOf(mVibrate));
//                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setRake("/keyboard/setting", "drag.keyboardvibration");
                /*
                if (mVibrator != null) {
                    KeyboardLogPrint.w("mVibrate 2 :: " + mVibrate);
                    mVibrator.vibrate(mVibrate * Common.VIBRATE_MUL);
                }
                */
                SharedPreference.setLongCommit(KeyboardSettingsActivity.this, Common.PREF_VIBRATE_LEVEL, mVibrate); // 2017.11.08 프로그래스바 이동시마다 seekbar 값을 저장
//                if ( mVibrator != null ) {
//                    KeyboardLogPrint.d("mVibrate :: " + mVibrate);
////                        mVibrator.vibrate(mVibrate); // 2017.08.25 대표님 지시사항으로 vibrate 를 전체적으로 1/2로 줄임. 표시는 기존과 동일하게함
//                    int iVibrate = mVibrate / 2;
//                    KeyboardLogPrint.w("iVibrate :: " + iVibrate);
//                    mVibrator.vibrate(iVibrate);
//                }
                /**
                 if (mVibrateLevel != null)
                 mVibrateLevel.setText("" + (mVibrate * 10));**/
            }
        });

//        mSizeSeek.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if ( event.getAction() == MotionEvent.ACTION_DOWN )
//                    kv.setVisibility(View.VISIBLE);
//                return false;
//            }
//        });

        mSizeSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                KeyboardLogPrint.d("size seek onProgressChanged");
                // 2017.08.25 키보드 사이즈 키우라는 대표님 지시로 xml에서 SeekBar의 max 값을 10에서 13으로 키우고 default 값을 5 ~ 7로 늘임
//                kv.setVisibility(View.VISIBLE);
//                if ( progress <= 9 )
//                    mSizeLevel = progress;
//                else if ( 9 < progress && progress <= 12 )
//                    mSizeLevel = progress + 8;
//                else
//                    mSizeLevel = progress + 15;

                mSizeLevel = progress * 3; // OCB KEYBOARD는 기존 30 단계에서 11단계로 축소 따라서 PROGRESS VALUE * 3으로 해서 요구값을 충족.

                setKeyboard(mPreviewSet, mSizeLevel);
                if (kv != null)
                    kv.setOnKeyboardActionListener(KeyboardSettingsActivity.this);
                setKeyboardBackground();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                LogPrint.d("skkim keyboard_total_layer visible 6");
                isSizeSeekStarted = true;
                keyboard_total_layer.setVisibility(View.VISIBLE);
                setRake("/keyboard/setting", "view.keyboard");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                KeyboardLogPrint.d("size seek onStopTrackingTouch");
                try {
                    if ( kv != null ) {
                        int wd = kv.getWidth();
                        int ht = kv.getHeight();
                        kv.post(new Runnable() {
                            @Override
                            public void run() {
                                if ( !TextUtils.isEmpty(mThemeModel.getBgOriginImg()) ) {
                                    ResizingAsync task = new ResizingAsync(wd,ht);
                                    task.execute();
                                } else {
                                    kv.setBackgroundDrawable(ThemeManager.GetDrawableFromPath(mThemeModel.getBgImg()));
                                    if (kv.getBackground() != null)
                                        kv.getBackground().setAlpha(mBgAlpha);
                                }
                            }
                        });
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                isSizeSeekStarted = false;
                setRake("/keyboard/setting", "drag.keyboardheight");
            }
        });

        CustomAsyncTask task = new CustomAsyncTask(KeyboardSettingsActivity.this);
        task.getPromotion(new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                if ( result ) {
                    try {
                        if ( obj != null ) {
                            JSONObject object = (JSONObject) obj;
                            if ( object != null ) {
                                boolean rt = object.optBoolean("Result");
                                if ( rt ) {
                                    JSONObject infoObj = object.optJSONObject("list_info");
                                    if ( infoObj != null ) {
                                        String title = infoObj.optString("title");
                                        String url = infoObj.optString("url");
                                        if ( !TextUtils.isEmpty(title) && !TextUtils.isEmpty(url) ) {
                                            promotionLayer.setVisibility(View.VISIBLE);
                                            txt_promotion.setText(title);
                                            txt_promotion.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    try {
                                                        String link = url;
                                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);
                                                        finish();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }
        });

        mThemeModel = ThemeManager.GetThemeModel(KeyboardSettingsActivity.this, 4);
        if (mThemeModel != null && kv != null ) {
            try {
                kv.setThemeModel(mThemeModel);
                NinePatchDrawable norNor = ThemeManager.GetNinePatch(KeyboardSettingsActivity.this, mThemeModel.getNorBtnNorI()); // 일반키 normal
                NinePatchDrawable norPre = ThemeManager.GetNinePatch(KeyboardSettingsActivity.this, mThemeModel.getNorBtnPreI()); // 일반키 pressed
                Drawable norBtnSelector = ThemeManager.GetImageSelector(norNor, norPre); // 일반키 selector

                NinePatchDrawable speNor = ThemeManager.GetNinePatch(KeyboardSettingsActivity.this, mThemeModel.getSpBtnNorI()); // 특수키 normal
                NinePatchDrawable spePre = ThemeManager.GetNinePatch(KeyboardSettingsActivity.this, mThemeModel.getSpBtnPreI()); // 특수키 pressed
                Drawable bg = ThemeManager.GetDrawableFromPath(mThemeModel.getBgImg()); // 배경이미지, 나인페치 미적용 2017.12.04
                Drawable spBtnSelector = ThemeManager.GetImageSelector(speNor, spePre); // 특수키 selector
                int txtColor = Color.parseColor(mThemeModel.getKeyText()); // 키 텍스트 색상

                Drawable tabEmoji = ThemeManager.GetDrawableFromPath(mThemeModel.getTabEmoji());
                Drawable tabEmojiOn = ThemeManager.GetDrawableFromPath(mThemeModel.getTabEmojiOn());
                Drawable tabOkCashbackLogo = ThemeManager.GetDrawableFromPath(mThemeModel.getTabCashbackLogo());
                Drawable tabOlabang = ThemeManager.GetDrawableFromPath(mThemeModel.getTabOlabang());
                //Drawable tabShopping = ThemeManager.GetDrawableFromPath(mThemeModel.getTabSaveShopping());
                Drawable tabShopping = null;
                if ( !TextUtils.isEmpty(mThemeModel.getTabSaveShopping()) )
                    tabShopping = ThemeManager.GetDrawableFromPath(mThemeModel.getTabSaveShopping());
                else {
                    String targetPath = mThemeModel.getTabEmoji();
                    if ( !TextUtils.isEmpty(targetPath) ) {
                        if ( targetPath.contains("theme_color_01") || targetPath.contains("theme_118")) {
                            tabShopping = getResources().getDrawable(R.drawable.aikbd_btn_save_shopping_icon_118);
                        } else if ( targetPath.contains("theme_119") ) {
                            tabShopping = getResources().getDrawable(R.drawable.aikbd_btn_save_shopping_icon_119);
                        } else if ( targetPath.contains("theme_120") ) {
                            tabShopping = getResources().getDrawable(R.drawable.aikbd_btn_save_shopping_icon_120);
                        } else if ( targetPath.contains("theme_115") ) {
                            tabShopping = getResources().getDrawable(R.drawable.aikbd_btn_save_shopping_icon_115);
                        } else if ( targetPath.contains("theme_121") ) {
                            tabShopping = getResources().getDrawable(R.drawable.aikbd_btn_save_shopping_icon_121);
                        } else {
                            tabShopping = getResources().getDrawable(R.drawable.aikbd_btn_save_shopping_icon_118);
                        }
                    }
                }
                Drawable tabMy = ThemeManager.GetDrawableFromPath(mThemeModel.getTabMy());

                Drawable tabMore = ThemeManager.GetDrawableFromPath(mThemeModel.getTabMore());
                Drawable tabOCBSearch = ThemeManager.GetDrawableFromPath(mThemeModel.getTabOCBSearch());
                Drawable tabMoreOn = ThemeManager.GetDrawableFromPath(mThemeModel.getTabMoreOn());
                Drawable tabMyOn = ThemeManager.GetDrawableFromPath(mThemeModel.getTabMyOn());
                Drawable tabOlabangOn = ThemeManager.GetDrawableFromPath(mThemeModel.getTabOlabangOn());
                Drawable tabOCBSearchOn = ThemeManager.GetDrawableFromPath(mThemeModel.getTabOCBSearchOn());
                Drawable TABShoppingOn = ThemeManager.GetDrawableFromPath(mThemeModel.getTabSaveShoppingOn());

                int tabNor = Color.parseColor(mThemeModel.getTabOff()); // 상단 tab off 색상
                int tabPre = Color.parseColor(mThemeModel.getTabOn()); // 상단 tab on 색상
                Drawable tabSelector_logo = ThemeManager.GetColorSelector(tabNor, tabPre); // 상단 tab selector
                Drawable tabSelector_first = ThemeManager.GetColorSelector(tabNor, tabPre); // 상단 tab selector
                Drawable tabSelector_second = ThemeManager.GetColorSelector(tabNor, tabPre); // 상단 tab selector
                Drawable tabSelector_third = ThemeManager.GetColorSelector(tabNor, tabPre); // 상단 tab selector
                Drawable tabSelector_fourth = ThemeManager.GetColorSelector(tabNor, tabPre); // 상단 tab selector
                Drawable tabSelector_fifth = ThemeManager.GetColorSelector(tabNor, tabPre); // 상단 tab selector
                Drawable tabSelector_more = ThemeManager.GetColorSelector(tabNor, tabPre); // 상단 tab selector
                Drawable tabSelector_chat = ThemeManager.GetColorSelector(tabNor, tabPre); // 상단 tab selector
                String strTopLine = mThemeModel.getTopLine();
                int iTopColor = Color.parseColor(strTopLine);
                mTopLine.setBackgroundColor(iTopColor);

                String mUsedTheme = "theme_color_01";
                String strRoot = getFilesDir().getAbsolutePath() + File.separator + "THEME" + File.separator;
                File rootFile = new File(strRoot);
                File[] fileList = rootFile.listFiles();
                if (fileList.length > 0) {
                    for (int i = 0; i < fileList.length; i++) {
                        File inFile = fileList[i];
                        if (inFile.isDirectory()) {
                            mUsedTheme = inFile.getName();
                        }
                    }
                }
                LogPrint.d("mUsedTheme :: " + mUsedTheme);
//                Drawable tabChatGpt = getResources().getDrawable(R.drawable.aikbd_chat_gpt_normal);
//                if ( !"theme_color_01".equals(mUsedTheme) && !"theme_118".equals(mUsedTheme) )
//                    tabChatGpt = getResources().getDrawable(R.drawable.aikbd_chat_gpt_theme);
//
//                mImgChat.setBackgroundDrawable(tabChatGpt);

                mImgSecond.setImageResource(R.drawable.aikbd_coupang_floating_icon);

                mFirstTabLayer.setBackgroundDrawable(tabSelector_first);
                mTimedealTabLayer.setBackgroundDrawable(tabSelector_second);
                mSecondTabLayer.setBackgroundDrawable(tabSelector_third);
                mShoppingTabLayer.setBackgroundDrawable(tabSelector_fourth);
                mFourthLayer.setBackgroundDrawable(tabSelector_fifth);
                mMoreLayer.setBackgroundDrawable(tabSelector_more);
                mGameLayer.setBackgroundDrawable(tabSelector_chat);
                mImgSecond.setImageResource(R.drawable.aikbd_coupang_floating_icon);
                mImgFirst.setBackgroundDrawable(tabEmoji);
                mImgTimedeal.setBackgroundDrawable(tabShopping);
                mImgShopping.setBackgroundDrawable(tabOCBSearch);
                mImgFourth.setBackgroundDrawable(tabMy);
                mImgMore.setBackgroundDrawable(tabMore);

                mBgAlpha = mThemeModel.getBgAlpha();
                kv.setBackgroundDrawable(bg);
                if (kv.getBackground() != null) {
                    KeyboardLogPrint.e("kv.getBackground() is not null");
                    kv.getBackground().setAlpha(mBgAlpha);
                } else {
                    KeyboardLogPrint.e("kv.getBackground() is null");
                }
                kv.setTBackground(norNor, speNor);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        setThemeSize();

        isGuideVisibled = SharedPreference.getBoolean(KeyboardSettingsActivity.this, Key.KEY_SETTING_GUIDE_VISIBLE);
        if ( !isGuideVisibled ) {
            LogPrint.d("skkim keyboard_total_layer visible 7");
            keyboard_total_layer.setVisibility(View.VISIBLE);
            fadeInAnimation(ppz_guide_layer);
            setRake("/keyboard/setting", "view.keyboard");
        }

        //connectPopularTheme();
    }

    private void setBackgroundResource(ImageView view, int resId) {
        Drawable dr = ResourcesCompat.getDrawable(getResources(), resId, null);
        Bitmap bitmap = Util.convertDrawableToBitmap(dr);
        view.setImageBitmap(bitmap);
    }

    private void changeImageColor(int color, ImageView view, int resId) {
        Drawable dr = ResourcesCompat.getDrawable(getResources(), resId, null);
        Bitmap logo = Util.convertDrawableToBitmap(dr);
        Bitmap bitmap = Util.changeImageColor(logo, color);
        view.setImageBitmap(bitmap);
    }

    public void onBackPressed() {
        if (mKeyboardViewLayer == null && mMatchLayer == null) //2017.11.23 예외처리
            super.onBackPressed();

        if (keyboard_total_layer.getVisibility() == View.VISIBLE) {
            LogPrint.d("skkim keyboard_total_layer gone 7");
            keyboard_total_layer.setVisibility(View.GONE);

        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        if ( !isNotSet ) {
            SharedPreference.setBooleanCommit(this, Common.PREF_HAS_SETTING, true);
        }
        if (mActivity != null)
            mActivity = null;
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();

        if ( KeyboardOfferwallListActivity.mActivity != null )
            KeyboardOfferwallListActivity.mActivity.finish();
        if ( KeyboardOfferwallWebViewActivity.mActivity != null )
            KeyboardOfferwallWebViewActivity.mActivity.finish();
        if ( KeyboardOfferwallWebView23Activity.mActivity != null )
            KeyboardOfferwallWebView23Activity.mActivity.finish();
        if ( KeyboardOfferwallWebView1Activity.mActivity != null )
            KeyboardOfferwallWebView1Activity.mActivity.finish();

        String ocbKeyboard = Common.TARGET_PACKAGENAME + "/com.enliple.keyboard.activity.SoftKeyboard";
        String currentKeyboard = Settings.Secure.getString(getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
        if ( !currentKeyboard.equals(ocbKeyboard) ) {
            isNotSet = true;
            startActivity(new Intent(KeyboardSettingsActivity.this, IntroActivity.class));
//            Toast.makeText(KeyboardSettingsActivity.this, "OK캐쉬백 돈버는 키보드를 기본키보드로 지정해주세요.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            if ( !isNotSet ) {
                Intent setKeyboard = new Intent(SoftKeyboard.SET_KEYBOARD_ON);
                sendBroadcast(setKeyboard);
                mQwertyNumSet = SharedPreference.getBoolean((KeyboardSettingsActivity.this), Common.PREF_QWERTY_NUM_SETTING);
                if ( mQwertyNumSet )
                    setRadio(numBg, numHead, true);
                else
                    setRadio(numBg, numHead, false);
                int kind = SharedPreference.getInt(this, Common.PREF_KEYBOARD_MODE);
                if (kind < 0) kind = 0;
                KeyboardLogPrint.e("Keyboard_Settings_Activity kind :: " + kind);
                switch (kind) {
                    case 0:
                        mKindTxt.setText(getResources().getString(R.string.aikbd_chunjin));
                        numOnOff.setClickable(false);
                        break;
                    case 1:
                        mKindTxt.setText(getResources().getString(R.string.aikbd_chunjin_plus));
                        numOnOff.setClickable(false);
                        break;
                    case 2:
                        mKindTxt.setText(getResources().getString(R.string.aikbd_querty));
                        numOnOff.setClickable(true);
                        break;
                    case 3:
                        mKindTxt.setText(getResources().getString(R.string.aikbd_nara));
                        numOnOff.setClickable(false);
                        break;
                    case 4:
                        mKindTxt.setText(getResources().getString(R.string.aikbd_dan));
                        numOnOff.setClickable(false);
                        break;
                    default:

                        break;
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if ( !isGuideVisibled )
            SharedPreference.setBoolean(KeyboardSettingsActivity.this, Key.KEY_SETTING_GUIDE_VISIBLE, true);

        SharedPreference.setIntCommit(KeyboardSettingsActivity.this, Common.PREF_KEYBOARD_SIZE_LEVEL, mSizeLevel);

        if ( !isNotSet ) {
            SharedPreference.setIntCommit(KeyboardSettingsActivity.this, Common.PREF_I_VOLUME_LEVEL, mCalcVolume);
//        SharedPreference.setLong(Keyboard_Settings_Activity.this, Common.PREF_VIBRATE_LEVEL, (long) mVibrate); //2017.08.25 대표님 지시사항으로 vibrate 를 1/2로 줄임
            SharedPreference.setLongCommit(KeyboardSettingsActivity.this, Common.PREF_VIBRATE_LEVEL, mVibrate);
            synchronized(this) {
                if (keyboardHeight != 0) {
                    KeyboardLogPrint.e("keyboardHeight setting save pref : " + keyboardHeight);
                    SharedPreference.setIntCommit(KeyboardSettingsActivity.this, Common.PREF_KEYBOARD_HEIGHT, keyboardHeight);
                }
            }

            Intent setKeyboard = new Intent(SoftKeyboard.SET_KEYBOARD_OFF);
            sendBroadcast(setKeyboard); // SOFTKEYBOARD에서 받음
            Intent setChange = new Intent(SoftKeyboard.SET_CHANGE);
            sendBroadcast(setChange); //MainKeyboardView에서 받음.

            int isNumOn = -1;
            if ( mQwertyNumSet )
                isNumOn = SoftKeyboard.NUM_ON;
            else
                isNumOn = SoftKeyboard.NUM_OFF;

            Intent changeKeyboardSize = new Intent(SoftKeyboard.SET_KEYBOARD_SIZE);
            changeKeyboardSize.putExtra("size_level", mSizeLevel);
            changeKeyboardSize.putExtra("num_value", isNumOn);
            sendBroadcast(changeKeyboardSize); // SOFTKEYBOARD에서 받음

            if(mSizeLevel != SharedPreference.getInt(KeyboardSettingsActivity.this, Common.PREF_KEYBOARD_SIZE_LEVEL))
                setThemeSize();
        }
    }

    private void setThemeSize() {
        if (TextUtils.isEmpty(mThemeModel.getBgImg())) {
            KeyboardLogPrint.e(" origin img empty onStopTrackingTouch");
            int isNumOn = -1;
            if ( mQwertyNumSet )
                isNumOn = SoftKeyboard.NUM_ON;
            else
                isNumOn = SoftKeyboard.NUM_OFF;

            SharedPreference.setIntCommit(KeyboardSettingsActivity.this, Common.PREF_KEYBOARD_SIZE_LEVEL, mSizeLevel);
            Intent setKeyboard = new Intent(SoftKeyboard.SET_KEYBOARD_SIZE);
            setKeyboard.putExtra("size_level", mSizeLevel);
            setKeyboard.putExtra("num_value", isNumOn);
            sendBroadcast(setKeyboard);
            return;
        }
/**
        if(kv.getWidth() == 0) {
            mKeyboardViewLayer.setVisibility(View.VISIBLE);
            mMatchLayer.setVisibility(View.VISIBLE);
        }
**/
        SharedPreference.setIntCommit(KeyboardSettingsActivity.this, Common.PREF_KEYBOARD_SIZE_LEVEL, mSizeLevel);
        Intent setKeyboard = new Intent(SoftKeyboard.SET_KEYBOARD_SIZE);
        sendBroadcast(setKeyboard);
        Intent themeChange = new Intent("THEME_CHANGE");
        sendBroadcast(themeChange);
/**
        kv.post(new Runnable() {
            @Override
            public void run() {

                int keyboardWidth = kv.getWidth();
                int keyboardHeight = kv.getHeight();
                mKeyboardViewLayer.setVisibility(View.GONE);
                mMatchLayer.setVisibility(View.GONE);

                Bitmap bgBitmap = ThemeManager.GetBitmapFromPath(mThemeModel.getBgOriginImg());
                int bitmapWidth = bgBitmap.getWidth();
                int bitmapHeight = bgBitmap.getHeight();

                if (keyboardWidth > 0) {
                    float m_ratio = (float) keyboardWidth / (float) bitmapWidth;
                    if (keyboardWidth == bitmapWidth) {
                        m_ratio = 1;
                    }
                    float tempResizeWidth = bitmapWidth * m_ratio;
                    float tempResizeHeight = bitmapHeight * m_ratio;
                    Bitmap bitmap = Bitmap.createScaledBitmap(bgBitmap, (int) tempResizeWidth, (int) tempResizeHeight, true);
                    float resizeWidth = tempResizeWidth;
                    float resizeHeight = 0;
                    int startY = 0;
                    if (tempResizeHeight <= keyboardHeight) {
                        resizeHeight = tempResizeHeight;
                    } else {
                        resizeHeight = keyboardHeight;
                        float remainHeight = tempResizeHeight - keyboardHeight;
                        startY = (int) (remainHeight / 2);
                    }
                    bitmap = Bitmap.createBitmap(bitmap, 0, startY, (int) resizeWidth, (int) resizeHeight);

                    String str = mThemeModel.getBgImg();
                    String[] strs = str.split("/");
                    String lastStr = strs[strs.length - 1];

                    String path = str.replaceAll("/" + lastStr, "");
                    File file = new File(path);
                    String fileName = lastStr;
                    final Bitmap bm = bitmap;
                    ThemeManager.saveBitmapToFile(file, fileName, bitmap, Bitmap.CompressFormat.PNG, 100, new ThemeManager.OnSaveFinishCallbackListener() {
                        @Override
                        public void onResponse(boolean result) {

                            Drawable dr = new BitmapDrawable(bm);
                            try {
                                kv.setBackgroundDrawable(dr);
                                if (kv.getBackground() != null) {

                                    kv.getBackground().setAlpha(mBgAlpha);
                                } else {

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            SharedPreference.setInt(KeyboardSettingsActivity.this, Common.PREF_KEYBOARD_SIZE_LEVEL, mSizeLevel);
                            Intent setKeyboard = new Intent(SoftKeyboard.SET_KEYBOARD_SIZE);
                            sendBroadcast(setKeyboard);
                            Intent themeChange = new Intent("THEME_CHANGE");
                            sendBroadcast(themeChange);
                        }
                    });
                }
            }
        });**/
    }

    public class ResizingAsync extends AsyncTask<Void, String, Boolean> {
        int keyboardWidth;
        int keyboardHeight;
        public ResizingAsync(int width, int height){
            this.keyboardWidth = width;
            this.keyboardHeight = height;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            // mWheelProgress.setVisibility(View.VISIBLE);
        }
        @Override
        protected Boolean doInBackground(Void... params) {
//            int keyboardWidth = kv.getWidth();
//            int keyboardHeight = kv.getHeight();
//            keyboardWidth = params[0];
//            keyboardHeight = params[1];
            try {
                Bitmap bgBitmap = ThemeManager.GetBitmapFromPath(mThemeModel.getBgOriginImg());
                if ( bgBitmap != null ) {
                    int bitmapWidth = bgBitmap.getWidth();
                    int bitmapHeight = bgBitmap.getHeight();
                    KeyboardLogPrint.e("crop keyboard width :: " + keyboardWidth);
                    KeyboardLogPrint.e("crop keyboard height :: " + keyboardHeight);
                    KeyboardLogPrint.e("crop bitmap width :: " + bitmapWidth);
                    KeyboardLogPrint.e("crop bitmap height :: " + bitmapHeight);

                    if ( keyboardWidth > 0 ) {
                        float m_ratio = (float)keyboardWidth / (float)bitmapWidth;
                        KeyboardLogPrint.e("crop m_ratio :: " + m_ratio);
                        if ( keyboardWidth == bitmapWidth ) {
                            m_ratio = 1;
                        }
                        float tempResizeWidth = bitmapWidth * m_ratio;
                        float tempResizeHeight = bitmapHeight * m_ratio;
                        KeyboardLogPrint.e("crop tempResizeWidth :: " + tempResizeWidth);
                        KeyboardLogPrint.e("crop tempResizeHeight :: " + tempResizeHeight);
                        Bitmap bitmap = Bitmap.createScaledBitmap(bgBitmap, (int)tempResizeWidth, (int)tempResizeHeight, true);
                        float resizeWidth = tempResizeWidth;
                        float resizeHeight = 0;
                        int startY = 0;
                        if ( tempResizeHeight <= keyboardHeight ) {
                            resizeHeight = tempResizeHeight;
                        } else {
                            resizeHeight = keyboardHeight;
                            float remainHeight = tempResizeHeight - keyboardHeight;
                            startY = (int)(remainHeight / 2);
                            KeyboardLogPrint.e("crop tempResizeHeight :: " + tempResizeHeight);
                            KeyboardLogPrint.e("crop startY :: " + startY);
                        }
                        bitmap = Bitmap.createBitmap(bitmap, 0, startY, (int)resizeWidth, (int)resizeHeight);

                        String str = mThemeModel.getBgImg();
                        String[] strs = str.split("/");
                        String lastStr = strs[strs.length - 1];

                        String path = str.replaceAll("/" + lastStr, "");
                        File file = new File(path);
                        String fileName = lastStr;
                        mBgDrawable = new BitmapDrawable(bitmap);
                        ThemeManager.saveBitmapToFile(file, fileName, bitmap, Bitmap.CompressFormat.PNG, 100);
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if ( result ) {
                if (kv != null && mBgDrawable != null) {
                    kv.setBackgroundDrawable(mBgDrawable);
                    if (kv.getBackground() != null)
                        kv.getBackground().setAlpha(mBgAlpha);
                }
            }
            //mWheelProgress.setVisibility(View.GONE);
        }
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            KeyboardLogPrint.e("motionEvent.getAction() :: " + motionEvent.getAction());
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_UP:
                    if (view.getId() == R.id.kind_layout) {
                        if ( keyboard_total_layer.getVisibility() == View.VISIBLE ) {
                            LogPrint.d("keyboard gone kind_layout touched");
                            if ( ppz_guide_layer.getVisibility() == View.VISIBLE ) {
                                if (countDownTimer != null) {
                                    countDownTimer.cancel();
                                    countDownTimer = null;
                                }
                                fadeOutAnimation(ppz_guide_layer);
                                fadeInAnimation(gpt_guide_layer);
                            } else if ( gpt_guide_layer.getVisibility() == View.VISIBLE ) {
                                if (countDownTimer != null) {
                                    countDownTimer.cancel();
                                    countDownTimer = null;
                                }
                                fadeOutAnimation(gpt_guide_layer);
                                if ( keyboard_total_layer != null ) {
                                    LogPrint.d("skkim keyboard_total_layer gone 8");
                                    keyboard_total_layer.setVisibility(View.GONE);
                                }
                            } else {
                                LogPrint.d("skkim keyboard_total_layer gone 9");
                                keyboard_total_layer.setVisibility(View.GONE);
                            }
                        } else {
                            if ( SettingSelectKeyboardActivity.mActivity != null )
                                SettingSelectKeyboardActivity.mActivity.finish();
                            setRake("/keyboard/setting", "tap.keyboardtype");
                            Intent intent = new Intent(KeyboardSettingsActivity.this, SettingSelectKeyboardActivity.class);
                            intent.putExtra("IS_FROM_SETTING", true);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(0, 0);
                        }
                    } else if (view.getId() == R.id.change_sound_layout) {
                        setRake("/keyboard/setting", "tap.keyboardsound");
                        if ( keyboard_total_layer.getVisibility() == View.VISIBLE ) {
                            LogPrint.d("keyboard gone change_sound_layout touched");
                            if ( ppz_guide_layer.getVisibility() == View.VISIBLE ) {
                                if (countDownTimer != null) {
                                    countDownTimer.cancel();
                                    countDownTimer = null;
                                }
                                fadeOutAnimation(ppz_guide_layer);
                                fadeInAnimation(gpt_guide_layer);
                            } else if ( gpt_guide_layer.getVisibility() == View.VISIBLE ) {
                                if (countDownTimer != null) {
                                    countDownTimer.cancel();
                                    countDownTimer = null;
                                }
                                fadeOutAnimation(gpt_guide_layer);
                                if ( keyboard_total_layer != null ) {
                                    LogPrint.d("skkim keyboard_total_layer gone 10");
                                    keyboard_total_layer.setVisibility(View.GONE);
                                }
                            } else {
                                LogPrint.d("skkim keyboard_total_layer gone 11");
                                keyboard_total_layer.setVisibility(View.GONE);
                            }
                        } else {
                            mSoundPosition = SharedPreference.getInt(KeyboardSettingsActivity.this, Common.PREF_SELECTED_SOUND);
                            LogPrint.e("#################### mSoundPosition : " + mSoundPosition);
                            if ( mSoundPosition >= 0 )
                                mDialogSelect = mSoundPosition;
                            LogPrint.e("#################### mDialogSelect : " + mDialogSelect);
                            AlertDialog.Builder builder = new AlertDialog.Builder(KeyboardSettingsActivity.this);
                            builder.setTitle(getResources().getString(R.string.aikbd_select_key));
                            builder.setCancelable(true);
                            builder.setSingleChoiceItems(items, mSoundPosition, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int item) {
                                    LogPrint.e("#################### onClick");
                                    switch (item) {
                                        case 0:
                                            mDialogSelect = SOUND_0;
                                            try {
                                                if (mSoundPool != null) {
                                                    float volume = getVolume();
                                                    if (mStreamId != -1)
                                                        mSoundPool.stop(mStreamId);
                                                    mStreamId = mSoundPool.play(mResArray.get(0), volume, volume, 0, 0, 1);
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            break;
                                        case 1:
                                            mDialogSelect = SOUND_1;
                                            try {
                                                if (mSoundPool != null) {
                                                    float volume = getVolume();
                                                    if (mStreamId != -1)
                                                        mSoundPool.stop(mStreamId);
                                                    mStreamId = mSoundPool.play(mResArray.get(1), volume, volume, 0, 0, 1);
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                            break;
                                        case 2:
                                            mDialogSelect = SOUND_2;
                                            try {
                                                if (mSoundPool != null) {
                                                    float volume = getVolume();
                                                    if (mStreamId != -1)
                                                        mSoundPool.stop(mStreamId);
                                                    mStreamId = mSoundPool.play(mResArray.get(2), volume, volume, 0, 0, 1);
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                            break;
                                        case 3:
                                            mDialogSelect = SOUND_3;
                                            try {
                                                if (mSoundPool != null) {
                                                    float volume = getVolume();
                                                    if (mStreamId != -1)
                                                        mSoundPool.stop(mStreamId);
                                                    mStreamId = mSoundPool.play(mResArray.get(3), volume, volume, 0, 0, 1);
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                            break;
                                        case 4:
                                            mDialogSelect = SOUND_4;
                                            try {
                                                if (mSoundPool != null) {
                                                    float volume = getVolume();
                                                    if (mStreamId != -1)
                                                        mSoundPool.stop(mStreamId);
                                                    mStreamId = mSoundPool.play(mResArray.get(4), volume, volume, 0, 0, 1);
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                            break;

                                    }
                                }
                            })
                                    .setPositiveButton(getResources().getString(R.string.aikbd_key_ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            LogPrint.e("#################### onClick mDialogSelect : " + mDialogSelect);
                                            mSoundPosition = mDialogSelect;
                                            if ( mSoundPosition >= 0 ) {
                                                sound_kind_txt.setText(items[mSoundPosition]);
                                            }

                                            int val = SharedPreference.getInt(KeyboardSettingsActivity.this, Common.PREF_SELECTED_SOUND);
                                            KeyboardLogPrint.e("######################## val :: " + val);
                                            KeyboardLogPrint.e("######################## mSoundPosition :: " + mSoundPosition);
                                            if (val != mSoundPosition) {
                                                if (mSoundPool != null) {
                                                    if (mStreamId != -1) {
                                                        mDefStreamId = -1;
                                                        mStreamId = -1;
                                                        mSoundPool.stop(mStreamId);
                                                    }
                                                }
                                                SharedPreference.setInt(KeyboardSettingsActivity.this, Common.PREF_SELECTED_SOUND, mSoundPosition);
                                                Intent soundChange = new Intent("SOUND_CHANGE");
                                                soundChange.putExtra("change_sound", mSoundPosition);
                                                LogPrint.d("################# send sound change position ::: " + mSoundPosition);
                                                sendBroadcast(soundChange);
                                                dialog.dismiss();
                                            }
                                        }
                                    })
                                    .setNegativeButton(getResources().getString(R.string.aikbd_cancel), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            mDefStreamId = -1;
                                            mStreamId = -1;
                                            dialog.dismiss();
                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            ListView list = dialog.getListView();
                            list.setSoundEffectsEnabled(false);
//                            mDialogSelect = 0;
                            dialog.show();
                        }
                    }
                    else if (view.getId() == R.id.theme_layout) {
                        setRake("/keyboard/setting", "tap.keyboardtheme");
                        Intent intent = new Intent(KeyboardSettingsActivity.this, KeyboardOCBCategoryThemeActivity.class);
                        intent.putExtra("IS_FROM_SETTING", true);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0);
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (seekLayout != null &&  keyboard_total_layer.getVisibility() == View.VISIBLE) {
            Rect rect1 = new Rect();
            Rect rect2 = new Rect();
            Rect rect3 = new Rect();
            seekLayout.getGlobalVisibleRect(rect1);
            mKeyboardViewLayer.getGlobalVisibleRect(rect2);
            mMatchLayer.getGlobalVisibleRect(rect3);

            int x = (int) ev.getX(), y = (int) ev.getY();
            if (!rect1.contains(x, y) && !rect2.contains(x, y) && !rect3.contains(x, y) && !isSizeSeekStarted) {
                LogPrint.d("keyboard gone dispatchTouchEvent");
                LogPrint.d("skkim visible ppz_guide_layer.getVisibility() :: " + ppz_guide_layer.getVisibility());
                LogPrint.d("skkim visible gpt_guide_layer.getVisibility() :: " + gpt_guide_layer.getVisibility());
                if ( keyboard_total_layer.getVisibility() == View.VISIBLE ) {
                    if ( ev.getAction() == MotionEvent.ACTION_UP ) {
                        if ( ppz_guide_layer.getVisibility() == View.VISIBLE ) {
                            if (countDownTimer != null) {
                                countDownTimer.cancel();
                                countDownTimer = null;
                            }
                            fadeOutAnimation(ppz_guide_layer);
                            fadeInAnimation(gpt_guide_layer);
                        } else if ( gpt_guide_layer.getVisibility() == View.VISIBLE ) {
                            if (countDownTimer != null) {
                                countDownTimer.cancel();
                                countDownTimer = null;
                            }
                            fadeOutAnimation(gpt_guide_layer);
                            if ( keyboard_total_layer != null ) {
                                LogPrint.d("skkim keyboard_total_layer gone 12");
                                keyboard_total_layer.setVisibility(View.GONE);
                            }
                        } else {
                            LogPrint.d("skkim keyboard_total_layer gone 13");
                            keyboard_total_layer.setVisibility(View.GONE);
                        }
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.previewOnOff) {
            if (mPreviewSet) {
                SharedPreference.setBooleanCommit(KeyboardSettingsActivity.this, Common.PREF_PREVIEW_SETTING, false);
                setRadio(previewBg, previewHead, false);
            } else {
                SharedPreference.setBooleanCommit(KeyboardSettingsActivity.this, Common.PREF_PREVIEW_SETTING, true);
                setRadio(previewBg, previewHead, true);
            }
            togglePreview();
        } else if (view.getId() == R.id.numOnOff) {
            setRake("/keyboard/setting", "tap.keyboardqwertynumber");
            if (mQwertyNumSet) {
                SharedPreference.setBooleanCommit(KeyboardSettingsActivity.this, Common.PREF_QWERTY_NUM_SETTING, false);
                setRadio(numBg, numHead, false);
            } else {
                SharedPreference.setBooleanCommit(KeyboardSettingsActivity.this, Common.PREF_QWERTY_NUM_SETTING, true);
                setRadio(numBg, numHead, true);
            }
            toggleQwertyNum();
            setKeyboard(mPreviewSet, mSizeLevel);
            if (kv != null) {
                kv.setOnKeyboardActionListener(KeyboardSettingsActivity.this);
            }
            keyboard_total_layer.setVisibility(View.VISIBLE);
            setRake("/keyboard/setting", "view.keyboard");
        } else if (view.getId() == R.id.btn_hide_ad_set) {
            if (mHideAdSet) {
                SharedPreference.setStringCommit(KeyboardSettingsActivity.this, Common.PREF_AD_VIEW_TIME, "");
                mBtnHideAd.setBackgroundResource(R.drawable.aikbd_btn_slide_off);
            } else {
                setAdTime();
                mBtnHideAd.setBackgroundResource(R.drawable.aikbd_btn_slide_on);
                SharedPreference.setStringCommit(KeyboardSettingsActivity.this, Common.PREF_AD_JSON, "");
            }
            toggleHideAd();
        } else if (view.getId() == R.id.point_layer) {
            try {
                Intent intents = getPackageManager().getLaunchIntentForPackage("com.enliple.keyboard.ui.ckeyboard");
                intents.setAction(Intent.ACTION_MAIN);
                startActivity(intents);
                finish();
                overridePendingTransition(0, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ( view.getId() == R.id.unuse_keyboard) {
            /**
            mTimer = new Timer();
            startTimeTask();
            startActivityForResult(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS), 0);**/
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showInputMethodPicker();
        } else if ( view.getId() == R.id.vibrateOnOff) {
            if ( mVibrate <= 0 ) {
                SharedPreference.setLongCommit(KeyboardSettingsActivity.this, Common.PREF_VIBRATE_LEVEL, Common.DEFAULT_VIBRATE_LEVEL);
                long vibrateLevel = SharedPreference.getLong(KeyboardSettingsActivity.this, Common.PREF_VIBRATE_LEVEL);
                mVibrate = (int) vibrateLevel;
                mVibrator.vibrate(mVibrate * Common.VIBRATE_MUL);
                setRadio(vibrateBg, vibrateHead, true);
                mVibrateSeek.setProgress(mVibrate);
            } else {
                SharedPreference.setLongCommit(KeyboardSettingsActivity.this, Common.PREF_VIBRATE_LEVEL, 0);
                mVibrate = 0;
                setRadio(vibrateBg, vibrateHead, true);
                mVibrateSeek.setProgress(mVibrate);
            }
        } else if (view.getId() == R.id.newsOnOff) {
            if (mNewsSet) {
                SharedPreference.setBooleanCommit(KeyboardSettingsActivity.this, Common.PREF_NEWS_SETTING, false);
                setRadio(newsBg, newsHead, false);
            } else {
                SharedPreference.setBooleanCommit(KeyboardSettingsActivity.this, Common.PREF_NEWS_SETTING, true);
                setRadio(newsBg, newsHead, true);
            }
            LogPrint.d("after toggle news :: " + SharedPreference.getBoolean(KeyboardSettingsActivity.this, Common.PREF_NEWS_SETTING));
            toggleNews();
        }
    }

    private void startTimeTask() {
        mTimer.scheduleAtFixedRate(new mainTask(), 0, 1000);
    }

    private class mainTask extends TimerTask {
        @Override
        public void run() {
            if (!isUsingCustomInputMethod()) {
                if (mTimer != null) {
                    mTimer.cancel();
                    mTimer = null;
                }

                try {
                    String url = "ocbt://com.skmc.okcashbag.home_google/detail/event?title=돈버는 키보드&url=https%3a%2f%2falp-webview.okcashbag.com%2fv1.0%2fearnkbd%2findex.html";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
/**
    public boolean isUsingCustomInputMethod() {
        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        List<InputMethodInfo> mInputMethodProperties = imm.getEnabledInputMethodList();
        final int N = mInputMethodProperties.size();
        for (int i = 0; i < N; i++) {
            InputMethodInfo imi = mInputMethodProperties.get(i);
            String label = imi.loadLabel(getPackageManager()).toString();

            if (label != null) {
//                if (label.equals(getResources().getString(R.string.aikbd_app_name)))
                if (label.equals(mKeyboardName))
                    return true;
            }
        }
        return false;
    }
**/
    private void toggleNews() {
        mNewsSet = !mNewsSet;
    }

    private void togglePreview() {
        mPreviewSet = !mPreviewSet;
    }

    private void toggleQwertyNum() {
        mQwertyNumSet = !mQwertyNumSet;
    }

    private void toggleHideAd() {
        mHideAdSet = !mHideAdSet;
    }

    private void initSound() {
//        int selected_sound = SharedPreference.getInt(Keyboard_Settings_Activity.this, Common.PREF_SELECTED_SOUND);
//        if (selected_sound < 0)
//            SharedPreference.setInt(Keyboard_Settings_Activity.this, Common.PREF_SELECTED_SOUND, SOUND_0);
        mSoundPosition = SharedPreference.getInt(KeyboardSettingsActivity.this, Common.PREF_SELECTED_SOUND);
        if (mSoundPosition < 0) {
            SharedPreference.setIntCommit(KeyboardSettingsActivity.this, Common.PREF_SELECTED_SOUND, SOUND_0);
            mSoundPosition = SOUND_0;
        }

        if ( mSoundPosition >= 0 ) {
            sound_kind_txt.setText(items[mSoundPosition]);
        }

        mResArray = new ArrayList<>();
        mSoundIdArray = new ArrayList<Integer>();
        ArrayList<Integer> array = new ArrayList<>();
        array.add(R.raw.aikbd_sound0);
        array.add(R.raw.aikbd_sound1);
        array.add(R.raw.aikbd_sound2);
        array.add(R.raw.aikbd_sound3);
        array.add(R.raw.aikbd_sound4);
        try {
//            mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mSoundPool = new SoundPool.Builder()
                        .setMaxStreams(2)
                        .build();
            } else {
                mSoundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
            }
            for (int i = 0; i < array.size(); i++) {
                mResArray.add(mSoundPool.load(this, array.get(i), 1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getVersionInfo() {
        String version = "";
        try {
            PackageInfo i = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = i.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
//        version = "1.0.1";
        return version;
    }

    private void setAdTime() {
        try {
            Date date = new Date();
            SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DATE, 1);
//            cal.add(Calendar.MINUTE, 1);
            KeyboardLogPrint.w("cal.getTime() 2222 :: " + sdformat.format(cal.getTime()));
            SharedPreference.setStringCommit(KeyboardSettingsActivity.this, Common.PREF_AD_VIEW_TIME, sdformat.format(cal.getTime()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isTimePassed() {
        try {
            String from = SharedPreference.getString(KeyboardSettingsActivity.this, Common.PREF_AD_VIEW_TIME);
            if (!TextUtils.isEmpty(from)) {
                SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date savedDate = sdformat.parse(from);
                Calendar cal = Calendar.getInstance();
                cal.setTime(savedDate);

                Date date = new Date();
                cal.setTime(date);
                int compare = date.compareTo(savedDate);

                KeyboardLogPrint.w("compare 1 ::: " + compare);

                if (compare > 0) {
                    SharedPreference.setStringCommit(KeyboardSettingsActivity.this, Common.PREF_AD_VIEW_TIME, "");
                    return true;
                } else
                    return false;
            } else
                return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private float getVolume() {
        mMediaVolume = SharedPreference.getInt(KeyboardSettingsActivity.this, Common.MEDIA_VOLUME_LEVEL);
        int iVolume = SharedPreference.getInt(KeyboardSettingsActivity.this, Common.PREF_I_VOLUME_LEVEL);
        return Common.getVolume("Keyboard_Setting_Activity getVolume", mMediaVolume, iVolume);
    }

    private void setKeyboard(boolean setValue, int level) {
        if (kv == null) // 2017.11.23 예외처리
            return;

        if (level > 0) {
            SharedPreference.setInt(getApplicationContext(), Common.PREF_KEYBOARD_SIZE_LEVEL, level);
        }

//        float fLevel = Float.parseFloat(String.format("%.2f", 0.90 + (float) (2 * level) / 100));
        float fLevel = Common.GetHeightValue(level);
        KeyboardLogPrint.w("adjustKeyboardKeyHeight :: " + fLevel);
        int kind = SharedPreference.getInt(this, Common.PREF_KEYBOARD_MODE);
        if (kind < 0) kind = 0;
        KeyboardLogPrint.e("Keyboard_Settings_Activity kind :: " + kind);
        if (kind == Common.MODE_CHUNJIIN) {
            LatinKeyboard sejong = new LatinKeyboard(this, R.xml.aikbd_sejong_setting);
            adjustKeyboardKeyHeight(sejong, fLevel);
            kv.setKeyboard(sejong);
            kv.setPreviewEnabled(false);
        } else if (kind == Common.MODE_QUERTY) {
            LatinKeyboard korean = new LatinKeyboard(this, mQwertyNumSet ? R.xml.aikbd_korean_n_setting : R.xml.aikbd_korean_setting);
            adjustKeyboardKeyHeight(korean, fLevel);
            kv.setKeyboard(korean);
            if (setValue)
                kv.setPreviewEnabled(true);
            else
                kv.setPreviewEnabled(false);
        } else if (kind == Common.MODE_NARA) {
            LatinKeyboard nara = new LatinKeyboard(this, R.xml.aikbd_nara_setting);
            adjustKeyboardKeyHeight(nara, fLevel);
            kv.setKeyboard(nara);
            kv.setPreviewEnabled(false);
        } else if (kind == Common.MODE_DAN) {
            LatinKeyboard dan = new LatinKeyboard(this, R.xml.aikbd_dan_setting);
            adjustKeyboardKeyHeight(dan, fLevel);
            kv.setKeyboard(dan);
            if (setValue)
                kv.setPreviewEnabled(true);
            else
                kv.setPreviewEnabled(false);
        } else if (kind == Common.MODE_CHUNJIIN_PLUS) {
            LatinKeyboard sejongPlus = new LatinKeyboard(this, R.xml.aikbd_sejong_plus_setting);
            adjustKeyboardKeyHeight(sejongPlus, fLevel);
            kv.setKeyboard(sejongPlus);
            kv.setPreviewEnabled(false);
        } else {
            LatinKeyboard sejong = new LatinKeyboard(this, R.xml.aikbd_sejong_setting);
            adjustKeyboardKeyHeight(sejong, fLevel);
            kv.setKeyboard(sejong);
            kv.setPreviewEnabled(false);
        }
        kv.setKeys();
        kv.changeConfig(level);
    }

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {

    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }

    public void setKeyboardBackground() {
        Drawable bg = ThemeManager.GetDrawableFromPath(mThemeModel.getBgImg()); // 배경이미지, 나인페치 미적용 2017.12.04
        kv.setBackgroundDrawable(bg);
        if (kv.getBackground() != null) {
            KeyboardLogPrint.e("kv.getBackground() is not null");
            kv.getBackground().setAlpha(mBgAlpha);
        } else {
            KeyboardLogPrint.e("kv.getBackground() is null");
        }
    }

    private void adjustKeyboardKeyHeight(LatinKeyboard keyboard, float newKeyHeight) {
        int height = 0;
        if ( keyboard != null ) {
            for (Keyboard.Key key : keyboard.getKeys()) {
                key.height *= newKeyHeight;
                KeyboardLogPrint.e("keyboardHeight KeyboardSettingActivity key.height : " + key.height);
                key.y *= newKeyHeight;
                KeyboardLogPrint.e("keyboardHeight KeyboardSettingActivity key.y : " + key.y);
                height = key.height;
                KeyboardLogPrint.e("keyboardHeight KeyboardSettingActivity height : " + height);
            }
            KeyboardLogPrint.e("keyboardHeight KeyboardSettingActivity adjustKeyboardKeyHeight : 해당 키보드의 높이를 설정");
            keyboard.setHeight(height);
            synchronized(this) {
                keyboardHeight = keyboard.getHeight();
                if (keyboardHeight != 0) {
                    KeyboardLogPrint.e("keyboardHeight 키보드 높이값을 가져와서 프리퍼런스에 저장 : " + keyboard.getHeight());
                    SharedPreference.setIntCommit(getApplicationContext(), Common.PREF_KEYBOARD_HEIGHT, keyboard.getHeight());
                }
            }
        }
    }
/**
    private void connectPopularTheme() {
        UserIdDBHelper helper = new UserIdDBHelper(this);
        KeyboardUserIdModel model = helper.getUserInfo();
        String userId = "";
        if ( model != null ) {
            userId = model.getUserId();
        }

        if ( userId == null || TextUtils.isEmpty(userId) )
            userId = "";

        int scaleType = ThemeManager.GetScaleLevel(this);
        CustomAsyncTask task = new CustomAsyncTask(this);
        KeyboardLogPrint.e("userId :: " + userId);
        KeyboardLogPrint.e("scaleType :: " + scaleType);
        task.connectPopularThemeList(userId, scaleType, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                try {
                    JSONObject object = (JSONObject) obj;
                    if ( object != null ) {

                        JSONArray array = object.optJSONArray("data");

                        if ( array != null && array.length() > 0 ) {
                            JSONObject in_obj1 = array.getJSONObject(0);
                            JSONObject in_obj2 = array.getJSONObject(1);


                            DisplayMetrics dm = getResources().getDisplayMetrics();
                            int width = dm.widthPixels; // 휴대폰의 가로 사이즈를 구한다.

                            float part_size = width / 2;
                            part_size = part_size - convertDpToPixel(24, KeyboardSettingsActivity.this);
                            int mImageWidth = (int)(part_size - convertDpToPixel(12, KeyboardSettingsActivity.this));
                            int mImageHeight = (int)(mImageWidth / 1.44);
                            LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(mImageWidth, mImageHeight);
                            LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(mImageWidth, mImageHeight);

                            iv_popular1.setLayoutParams(param1);
                            iv_popular2.setLayoutParams(param2);

                            ImageLoader.with(KeyboardSettingsActivity.this).from(in_obj1.optString("image")).load(iv_popular1);
                            ImageLoader.with(KeyboardSettingsActivity.this).from(in_obj2.optString("image")).load(iv_popular2);

                            tv_popular1.setText(in_obj1.optString("name"));
                            tv_popular2.setText(in_obj2.optString("name"));


                            ll_popular1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(KeyboardSettingsActivity.this, KeyboardNewThemeActivity.class);
                                    intent.putExtra("IS_FROM_SETTING", true);
                                    intent.putExtra("SELECT_POPULARITY_THEME", in_obj1.optString("unzip_file_name"));
                                    startActivity(intent);
                                    finish();
                                    overridePendingTransition(0, 0);
                                }
                            });
                            ll_popular2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(KeyboardSettingsActivity.this, KeyboardNewThemeActivity.class);
                                    intent.putExtra("IS_FROM_SETTING", true);
                                    intent.putExtra("SELECT_POPULARITY_THEME", in_obj2.optString("unzip_file_name"));
                                    startActivity(intent);
                                    finish();
                                    overridePendingTransition(0, 0);
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }**/

    private void setRadio(View bg, View head, boolean isSet) {
        int headerSize = Common.convertDpToPx(KeyboardSettingsActivity.this, 26);
        if ( isSet ) {
            bg.setBackgroundResource(R.drawable.aikbd_radio_bg_on);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(headerSize,headerSize);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            head.setLayoutParams(params);
        } else {
            bg.setBackgroundResource(R.drawable.aikbd_radio_bg_off);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(headerSize,headerSize);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            head.setLayoutParams(params);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        String ocbKeyboard = Common.TARGET_PACKAGENAME + "/com.enliple.keyboard.activity.SoftKeyboard";
        String currentKeyboard = Settings.Secure.getString(getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);

        if ( !currentKeyboard.equals(ocbKeyboard) ) {
            finish();
        }
    }

    public boolean isUsingCustomInputMethod() {
        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        List<InputMethodInfo> mInputMethodProperties = imm.getEnabledInputMethodList();
        final int N = mInputMethodProperties.size();
        for (int i = 0; i < N; i++) {
            InputMethodInfo imi = mInputMethodProperties.get(i);
            String label = imi.loadLabel(getPackageManager()).toString();

            if (label != null) {
//                if (label.equals(getResources().getString(R.string.aikbd_app_name)))
                if (label.equals(mKeyboardName))
                    return true;
            }
        }
        return false;
    }

    private void goneKeyboard() {
        if ( keyboard_total_layer.getVisibility() == View.VISIBLE ) {
            LogPrint.d("keyboard gone goneKeyboard");
            if ( ppz_guide_layer.getVisibility() == View.VISIBLE ) {
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                    countDownTimer = null;
                }
                fadeOutAnimation(ppz_guide_layer);
                fadeInAnimation(gpt_guide_layer);
            } else if ( gpt_guide_layer.getVisibility() == View.VISIBLE ) {
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                    countDownTimer = null;
                }
                fadeOutAnimation(gpt_guide_layer);
                if ( keyboard_total_layer != null ) {
                    LogPrint.d("skkim keyboard_total_layer gone 14");
                    keyboard_total_layer.setVisibility(View.GONE);
                }
            } else {
                LogPrint.d("skkim keyboard_total_layer gone 15");
                keyboard_total_layer.setVisibility(View.GONE);
            }
        }
    }




    private void fadeInAnimation(ConstraintLayout layout) {
        layout.setVisibility(View.VISIBLE);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(layout, "alpha", 0f, 1f);
        fadeIn.setDuration(500);
        fadeIn.start();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                    countDownTimer = null;
                }

                countDownTimer = new CountDownTimer(2000, 1000) {
                    @Override
                    public void onTick(long leftTimeInMilliseconds) {
                    }

                    @Override
                    public void onFinish() {
                        fadeOutAnimation(layout);
                        if ( layout == ppz_guide_layer ) {
                            fadeInAnimation(gpt_guide_layer);
                        } else {
                            LogPrint.d("skkim keyboard_total_layer gone 16");
                            keyboard_total_layer.setVisibility(View.GONE);
                        }
                    }
                }.start();
            }
        });
        if ( layout == ppz_guide_layer ) {
            setRake("/keyboard/setting", "view.ppztooltip");
        } else {
            setRake("/keyboard/setting", "view.chatbottooltip");
        }
    }

    private void fadeOutAnimation(ConstraintLayout layout) {
        layout.setVisibility(View.GONE);
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(layout, "alpha", 1f, 0f);
        fadeOut.setDuration(500);
        fadeOut.start();
    }
}
