/*
 * Copyright (C) 2008-2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

/*
 * halbae87: this project is created from Soft Keyboard Sample source
 */

package com.enliple.keyboard.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.Rect;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.text.method.MetaKeyKeyListener;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.view.inputmethod.CursorAnchorInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.enliple.keyboard.Cipher.E_Cipher;
import com.enliple.keyboard.CoupangAdPopup;
import com.enliple.keyboard.R;
import com.enliple.keyboard.ad.Listener;
import com.enliple.keyboard.automata.Automata;
import com.enliple.keyboard.automata.ChunjiinAutomata;
import com.enliple.keyboard.automata.ChunjiinPlusAutomata;
import com.enliple.keyboard.automata.DanAutomata;
import com.enliple.keyboard.automata.KoreanAutomata;
import com.enliple.keyboard.automata.NaraAutomata;
import com.enliple.keyboard.common.AIKBD_DBHelper;
import com.enliple.keyboard.common.Common;
import com.enliple.keyboard.common.KeyboardLogPrint;
import com.enliple.keyboard.common.Sound;
import com.enliple.keyboard.common.Util;
import com.enliple.keyboard.mobonAD.MobonSimpleSDK;
import com.enliple.keyboard.mobonAD.MobonUtils;
import com.enliple.keyboard.network.CustomAsyncTask;
import com.enliple.keyboard.receiver.MyReceiver;
import com.enliple.keyboard.ui.common.Key;
import com.enliple.keyboard.ui.common.LogPrint;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 각 동작바다 호출하는 함수 flow
 * <p>
 * 키보드가 노출이 아닌 상태에서 orientation change 시
 * onInitializeInterface
 * onStartInput
 *
 * @ 입력창을 눌러서 키보드 올라올 때
 * onFinishInput
 * onStartInput
 * onStartInputView
 * @ 가로/세로 변경 시
 * onInitializeInterface
 * onStartInput
 * onUpdateExtractingVisibility
 * onCreateInputView
 * onStartInputView
 * onUpdateExtractingVisibility
 */

@SuppressLint("InlinedApi")
@SuppressWarnings("unused")
public class SoftKeyboard extends InputMethodService implements KeyboardView.OnKeyboardActionListener {
    private static final int START_COMPLETE_TIMER = 100;
    private static final int REPEAT_COMPLETE_TIMER = 200;
    private static final int STOP_COMPLETE_TIMER = 300;
    private static final int START_ACTIVE_TIMER = 400;
    private static final int REPEAT_ACTIVE_TIMER = 500;
    private static final int STOP_ACTIVE_TIMER = 600;


    public static final int SHOW_COUPANG_AD_DIALOG = 700;
    public static final int NOTI_COUPANG_AD_DIALOG = 750;
    public static final int HIDE_COUPANG_AD_DIALOG = 800;
    public static final int OPEN_COUPANG_AD = 900;
    public static boolean isKeyboardShow = false;
    public static final int SHIFT_STATE_LOWER_CASE = 0;
    public static final int SHIFT_STATE_UPPER_CASE = 1;
    public static final int SHIFT_STATE_ONLY_UPPER_CASE = 2;

    public static final int NUM_ON = 1;
    public static final int NUM_OFF = 2;

    private static final int AD_TYPE_NONE = -1;
    private static final int AD_TYPE_MOBON = 0;
    private static final int AD_TYPE_MEDIATION = 1;
    private static final int AD_TYPE_BANNER = 2;
    private static final int AD_TYPE_COUPANG = 3;
    private static final int AD_TYPE_CRITEO = 4;
    private static final int AD_TYPE_REWARD = 5;

    private static Context staticApplicationContext;

    private static final int KEYCODE_SYMBOL = -2;

    public static final int NUM_KEYBOARD = 1;
    public static final int SYMBOL_KEYBOARD = 2;
    public static final int PHONE_SYMBOL = 3;
    private static final int SOUND_0 = 0;
    private static final int SOUND_1 = 1;
    private static final int SOUND_2 = 2;
    private static final int SOUND_3 = 3;
    private static final int SOUND_4 = 4;
    static final boolean DEBUG = false;
    public static final String CHAT_GPT_RESUME = "chat_gpt_resume";
    public static final String CHAT_GPT_PAUSE = "chat_gpt_pause";
    public static final String SET_KEYBOARD_ON = "set_keyboard_on";
    public static final String SET_KEYBOARD_OFF = "set_keyboard_off";
    public static final String SET_KEYBOARD_SIZE = "set_keyboard_size";
    public static final String SET_CHANGE = "set_change";
    public static final String CHANGE_CONFIG = "CHANGE_CONFIG";
    public static int mOption = -1;
    // 동동이 코드
//    private ArrayList<String> adPackageNameArray = new ArrayList<>();
//    private boolean isTargetAdShow = false;

    /**
     public String liveDate = "";
     public int liveFrequency = 0;
     public long liveTime = 0L;**/
    /**
     * This boolean indicates the optional example code for performing
     * processing of hard keys in addition to regular text generation
     * from on-screen interaction.  It would be used for input methods that
     * perform language translations (such as converting text entered on
     * a QWERTY keyboard to Chinese), but may not be used for input methods
     * that are primarily intended to be used for on-screen text entry.
     */
    static final boolean PROCESS_HARD_KEYS = true;
    private final String TAG = "SoftKeyboard";

    //    private KeyboardView mInputView;
    private static MainKeyboardView mMainKeyboardView;
    private boolean mQuickRun = false;

    // no suggestion for Korean
    // private CandidateView mCandidateView;
    // private CompletionInfo[] mCompletions;

    private StringBuilder mComposing = new StringBuilder();
    //    private StringBuilder mBackComposing = new StringBuilder();
//    private ArrayList<Integer> mPrevPrimaryKeys = new ArrayList<Integer>();
//    private ArrayList<String> mPrevComposingString = new ArrayList<String>();
//    private boolean isTimeOut;
//    private int startEditPostion;
//    private int endEditPostion;
    //    private StringBuilder mTempComposing = new StringBuilder();
    // private boolean mPredictionOn;
    // private boolean mCompletionOn;
    private int mLastDisplayWidth;
    private boolean mCapsLock;
    private boolean mKCapsLock;
    private long mLastShiftTime;
    private long mMetaState;
    private boolean mHwShift = false;

    private LatinKeyboard mQSymbolsKeyboard;
    private LatinKeyboard mQSymbolsKeyboard1;

    private LatinKeyboard mQSymbolsKeyboard_35;
    private LatinKeyboard mQSymbolsKeyboard1_35;
    private LatinKeyboard mSymbolsKeyboard;
    private LatinKeyboard mSymbolsShiftedKeyboard;
    private LatinKeyboard mSymbolsShiftedKeyboard1;
    private LatinKeyboard mNumberKeyboard;
    private LatinKeyboard mNumberOnlyKeyboard;
    private LatinKeyboard mNumberSignedKeyboard;
    private LatinKeyboard mNumberDecimalKeyboard;
    private LatinKeyboard mNumberPhoneKeyboard;
    private LatinKeyboard mPhoneSymbolKeyboard;
    //    private LatinKeyboard mQwertyKeyboard;
    private LatinKeyboard mQwertyNum;
    private LatinKeyboard mQwertyNum35;
    private LatinKeyboard mQwerty;
    private LatinKeyboard mEQwerty;
    private LatinKeyboard mQwerty35;
    private LatinKeyboard mEmojiKeyboard;
    private LatinKeyboard mEmoticonKeyboard;
    // special key definitions.
    static final int KEYCODE_HANGUL = 204; // KeyEvent.KEYCODE_KANA is available from API 16
    static final int KEYCODE_HANJA = 212;  // KeyEvent.KEYCODE_EISU is available from API 16
    static final int KEYCODE_WIN_LEFT = 117; // KeyEvent.KEYCODE_META_LEFT is available from API 11
    static final int KEYCODE_SYSREQ = 120; // KeyEvent.KEYCODE_SYSREQ is available from API 11

    private boolean mHwCapsLock = false;

    private LatinKeyboard mQKoreanKeyboard;
    private LatinKeyboard mQKoreanKeyboard35;
    private LatinKeyboard mKoreanKeyboard; // 쿼티 한글
    //    private LatinKeyboard mKoreanShiftedKeyboard;
    private LatinKeyboard mBackupKeyboard;
    private LatinKeyboard mSejongKeyboard; // 천지인
    private LatinKeyboard mDanKeyboard; // 단모음
    private LatinKeyboard mDanKeyboard35;
    private LatinKeyboard mNaraKeyboard; // 나랏글
    private LatinKeyboard mCurKeyboard;
    private LatinKeyboard mSejongPlusKeyboard;

    private LatinKeyboard mLKeyboard;
//    private EmojiKeyboardView mEKeyboard;

    private String mWordSeparators;

    private ChunjiinAutomata cAutomata;
    //    private ChunjiinPlusAutomata cpAutomata;
    private KoreanAutomata qAutomata;
    private DanAutomata dAutomata;
    private NaraAutomata nAutomata;
    private ChunjiinPlusAutomata cpAutomata;
    private Automata kauto;
    //    private boolean mNoKorean = false;
    private int mKoreanKeyboardMode = -1;

    private int mBackupKoreanKeyboardMode = -1;
    private static LatinKeyboard mBackupCurKeyboard = null;
    private static Automata mBackupKauto = null;
    private LatinKeyboard mBackupKoreanKeyboard = null;
    private EditorInfo mAttribute;
    private String mRotatedVal1 = "";
    private String mRotatedVal2 = "";
    private boolean mIsSettingOn = false;
    private int mSymbolMode = 0;
//    private Handler mHandler = null;

//    private EmojiKeyboardView emojiKeyboardView;

    private InputConnection inputConnection;

    private InputMethodManager previousInputMethodManager;
    private IBinder iBinder;
    private boolean mIsEmojiView = false;
    private View mKView = null;
    private RelativeLayout mEPridictLayer;
    private TextView mEPridict;

    private Vibrator mVibrator = null;
    private boolean mIsComplete = true;
    private boolean mIsCompleteFromHandler = false;
    //    private float mVolumeLevel = 0;
    private int mVolumeLevel = 0;
    private long mVibrateLevel = 0;
    private boolean mIsPreviewSet = false;
    private SoundPool mSoundPool = null;
    private int mSoundId = 0;
    private boolean mSoundLoaded = false;
    private ArrayList<String> mAutomataValues = null;
    //    private ADModel mModel = null;
//    private ADModel mTempModel = null;
    private String mADStr = null;
    private int mPressPrimaryKey = 0;
    private boolean mIsFromInit = false;

    String regexPattern = "[\uD83C-\uDBFF\uDC00-\uDFFF]+";
    Pattern pattern = Pattern.compile(regexPattern);
    private List<String> keywordList = new ArrayList<String>();
    private int keywordLimit = 100;
    private AudioManager mAudioManager;
    private int mAudioMode = 0;
    private int mMediaVolume = 0;
    private float mVol;
    // 20180430 이모티콘 제거
//    private static Timer mOTimer = null; //
    //private static Timer mTimer = null;

    // 20180423 팝업케시 제거
//    private Timer mPopTimer = null;
    private int mCompleteCount = 0;
    private int mStreamId = 0;
    private boolean isSetChangeRegistered = false;
    public static boolean mIsKeyPointPossible = false;
    // 20180430 이모티콘 제거
//    private String mMatchedString = ""; //
    /**
     * mPointCount
     * 키보드가 올때 liveCount에서 받아온 값으로 Random 함수를 돌린다.(ex 서버에서 넘어온 값이 100이면 0 ~ 99까지 중하나의 수가 나옴 이 값이 mPointCount가 됨.
     * 키보드에서 키를 하나씩 누를때마다 mPointCount를 -- 시키고 이 값이 0이 될 때 사용자에게 포인트를 1 쌓아준다.
     */
    private int mPointCount = 0;
    private String mEditStr = "";
    private long mCallTime = 0;
    private long mADCallTime = 0; //onFinishInputView가 2-3번 연달아 호출되는 경우에 광고 요청이 연달아 들어감.이를 막기 위한 flag
    private int mADCount = 0; // 광고 노출 조건에사용 됨
    private static int mShiftState = 0;

    // 20180423 팝업케시 제거
//    private boolean mIsPopShow = false;
//    private String mKakaoStr;
//    private WindowManager mPopWindowManager;
//    private MyRelativeLayout mPopView;

    private RelativeLayout mPagerLayer = null;
    private RelativeLayout mADPointBackground = null;
    private RelativeLayout mMangoPointLayer = null;
    private VerticalPager mPager = null;
    private TextView mMangoPoint = null;
    private TextView mAdPoint = null;
    private TextView mAdPointTitle = null;
    private TextView mBottomTouch = null;
    private View mPagerView = null;
    private RelativeLayout mTopArrow = null;
    private RelativeLayout mBottomArrow = null;

    private int mTouchDownX = 0;
    private int mTouchDownY = 0;
    private int mTouchUpX = 0;
    private int mTouchUpY = 0;

    private String mClickedGubun = "";
    private String mClickedTitle = "";
    private String mClickedUniqueCode = "";
    private int mPagerIndex = 0;

    private int mKeyboardActiveCount;
    private Timer mKeyboardActiveTimer;
    public int mobwithAdCount = 0;
    private int adCount = 0;
    private int bannerIndex = 0;

    private int mOCBPointCount = 0; // 5회마다 적립포인트 올려줄 횟수 카운트
    private int mOCBSavePoint = 0; // 사용자가 적립받을 수 있는 포인트, 총포인트라고 보면된다.
    //private int mOCBTodaySavePoint = 0;
    private SearchListener searchListener;
    private boolean isCharactorEntered = false;
    //private boolean isCorrectEnter = false;
    //private int lastEnteredPrimaryCode = -1;
    private int originalBarColor = 0;
    private boolean isSearchLayerVisible = false;

    private CustomAsyncTask apiAsyncTask = null;
    private CoupangAdPopup coupangdialog = null;
    private static CoupangAdPopup.AdVO advo = null;

    private static final String regExp = "^[a-zA-Z가-힣0-9\\s]*$";
    private Pattern searcPattern = Pattern.compile(regExp);
    private String beforeKeyword = "";
    boolean isInAppKeyboard = false;

    private MyReceiver myReceiver;

    private View mView;
    private WindowManager wm;
    private boolean isDraggable = false;
    private WindowManager.LayoutParams params;

    /**
     * Main initialization of the input method component.  Be sure to call
     * to super class.
     */

    public static Context getStaticApplicationContext() {
        return staticApplicationContext;
    }

    public SoftKeyboard() {
        super();
        if (Build.VERSION.SDK_INT >= 17) {
            enableHardwareAcceleration();
        }
    }

    private Handler mActiveHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == START_ACTIVE_TIMER) {
                mKeyboardActiveCount = 0;
                this.removeMessages(REPEAT_ACTIVE_TIMER);
                this.sendEmptyMessage(REPEAT_ACTIVE_TIMER);
            } else if (msg.what == REPEAT_ACTIVE_TIMER) {
                this.sendEmptyMessageDelayed(REPEAT_ACTIVE_TIMER, 1000);
                mKeyboardActiveCount++;
            } else if (msg.what == STOP_ACTIVE_TIMER) {
/*
                String uuid = SharedPreference.getString(getApplicationContext(), Key.KEY_OCB_USER_ID);
                try {
                    E_Cipher cp = E_Cipher.getInstance();
                    uuid = cp.Decode(getApplicationContext(), uuid);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                CustomAsyncTask apiAsyncTask = new CustomAsyncTask(getApplicationContext());
                apiAsyncTask.postFrequencyLiveTime(uuid, String.valueOf(mKeyboardActiveCount), (result, obj) -> {

                });

 */
                mKeyboardActiveCount = 0;
                this.removeMessages(REPEAT_ACTIVE_TIMER);
            }
        }
    };

    private Handler downloadHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                if (msg.getData() != null) {
                    String imagePath = msg.getData().getString("image_path");
                    String linkUrl = msg.getData().getString("link_url");
                    downloadBrandIcon(imagePath, linkUrl);
                }
            }
        }
    };

    private Handler mCompleteHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            KeyboardLogPrint.d("completeHandler msg :: " + msg.what);
            if (msg.what == START_COMPLETE_TIMER) {
                this.removeMessages(REPEAT_COMPLETE_TIMER);
                this.sendEmptyMessage(REPEAT_COMPLETE_TIMER);
            } else if (msg.what == REPEAT_COMPLETE_TIMER) {
                if (mCompleteCount >= 3) {
                    KeyboardLogPrint.d("mCompleteCount : " + mCompleteCount + ", isInputViewShown : " + isInputViewShown());
                    if (inputConnection != null && isInputViewShown()) {
                        ExtractedText et = inputConnection.getExtractedText(new ExtractedTextRequest(), 0);
                        if (et != null) {
                            mAutomataValues = kauto.getAutomataValue();
                            kauto.setInitState();
                            kauto.FinishAutomataWithoutInput();
                            mComposing.setLength(0);
                            inputConnection.finishComposingText();
                            mIsComplete = true;
                            mIsCompleteFromHandler = true;
                        } else {
                            KeyboardLogPrint.d("complete et null");
                        }
                    } else {
                        if ( inputConnection == null )
                            LogPrint.d("inputConnection null1111");
                    }
                    KeyboardLogPrint.d("stop complete called 16");
                    this.sendEmptyMessage(STOP_COMPLETE_TIMER);
                    mCompleteCount = 0;
                } else {
                    mCompleteCount++;
                    this.sendEmptyMessageDelayed(REPEAT_COMPLETE_TIMER, 250);
                }
            } else if (msg.what == STOP_COMPLETE_TIMER) {
                this.removeMessages(REPEAT_COMPLETE_TIMER);
            }
        }
    };
    private Handler mSoundHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                KeyboardLogPrint.w("SoftKeyboard mSoundLoaded  :: " + mSoundLoaded);
//                try {
//                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//                    r.play();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                if (mSoundLoaded) {
                    try {
                        KeyboardLogPrint.w("SoftKeyboard mVolumeLevel :: " + mVolumeLevel);
                        KeyboardLogPrint.w("SoftKeyboard mAudioMode :: " + mAudioMode);
                        if (mSoundPool == null) {
                            KeyboardLogPrint.w("SoftKeyboard mSoundPool null");
                            mSoundHandler.sendEmptyMessage(2);
                        }
                        mAudioMode = mAudioManager.getRingerMode();
                        if (mVolumeLevel > 0 && mAudioMode == AudioManager.RINGER_MODE_NORMAL && mSoundPool != null) {
                            KeyboardLogPrint.w("SoftKeyboard mVol :: " + mVol);
                            KeyboardLogPrint.w("SoftKeyboard mSoundId :: " + mSoundId);

                            Sound.soundPlay(getApplicationContext(), mAudioManager, "SoftKeyboard", mSoundPool, mSoundId);
//                            mSoundPool.stop(mStreamId);
//                            mStreamId = mSoundPool.play(mSoundId, mVol, mVol, 0, 0, 1);
                        } else {
                            KeyboardLogPrint.w("SoftKeyboard Keyboard no volume");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } else if (msg.what == 1) {
                if (mSoundPool != null) {
                    mSoundPool.stop(mStreamId);
//                    mSoundPool.release();
//                    mSoundPool = null;
                }
            } else if (msg.what == 2) {
                if (!mSoundLoaded)
                    initSound();
            }
            // 20180430 이모티콘 제거
//            else if (msg.what == 3) { ///
//                boolean matched = mMainKeyboardView.sendStr(mMatchedString);
//                KeyboardLogPrint.e("matched :: " + matched);
//                KeyboardLogPrint.e("last word :: " + mMatchedString);
//            } ///
        }
    };
/**
    private Handler mDialogHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            if (msg.what == SHOW_COUPANG_AD_DIALOG) {
//                if(coupangdialog != null){
//                    try {
//                        coupangdialog.dismiss();
//                    }catch (Exception e){}
//                    coupangdialog = null;
//                }
//                try{
//                    coupangdialog = new CoupangAdPopup(getApplicationContext(), mKView.getRootView(), advo, mDialogHandler);
//                }catch (Exception e){
//
//                }
                if (mMainKeyboardView != null && advo != null) {
                    mMainKeyboardView.coupangBannerShowHide(true);
                    mMainKeyboardView.coupangBannerData(advo, mDialogHandler);
                }

            } else if (msg.what == NOTI_COUPANG_AD_DIALOG) {
//                if(coupangdialog != null && advo != null)
//                    coupangdialog.setVO(advo);
                if (mMainKeyboardView != null && advo != null) {
                    mMainKeyboardView.coupangBannerShowHide(true);
                    mMainKeyboardView.coupangBannerData(advo, mDialogHandler);
                }
            } else if (msg.what == HIDE_COUPANG_AD_DIALOG) {
//                if (coupangdialog != null){
//                    try {
//                        coupangdialog.dismiss();
//                    } catch (Exception e) {
//                    }
//                    coupangdialog = null;
//                }
                if (mMainKeyboardView != null) {
                    mMainKeyboardView.coupangBannerShowHide(false);
                }
            } else if (msg.what == OPEN_COUPANG_AD) {
                if (msg.obj != null) {
                    handleClose();

                    String url = (String) msg.obj;
                    if (url != null && url.startsWith("http:")) {
                        url = url.replace("http:", "https:");
                    }

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(browserIntent);

                }
            }
        }
    };
**/
    public void copyClipboard(String link) {
        ClipboardManager manager = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText("label", link);

        if ( data != null ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    PersistableBundle extras = new PersistableBundle();
                    extras.putBoolean(ClipDescription.EXTRA_IS_SENSITIVE, true);
                    data.getDescription().setExtras(extras);
                } else {
                    PersistableBundle extras = new PersistableBundle();
                    extras.putBoolean("android.content.extra.IS_SENSITIVE", true);
                    data.getDescription().setExtras(extras);
                }
            }

            manager.setPrimaryClip(data);
            Toast.makeText(getApplicationContext(), "맴버십 번호가 복사되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void hide() {
        requestHideSelf(0);
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopSelf();
        LogPrint.d("skkim onTaskRemoved");
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onTaskRemoved(rootIntent);

        System.out.println("!!!!!!!!!!!!!!!!!!!!! onTaskRemoved");
        super.startService(rootIntent);
    }


    @Override
    public void onDestroy() {
        LogPrint.d("skkim onDestroy");
        if (mSoundPool != null) {
            KeyboardLogPrint.e("onDestroy soundPool not null");
            mSoundPool.unload(mSoundId);
            mSoundPool.stop(mStreamId);
            mSoundPool.release();
            mSoundPool = null;
        } else {
            KeyboardLogPrint.e("onDestroy soundPool null");
        }
        super.onDestroy();
        if ( isSetChangeRegistered ) {
            unregisterReceiver(mSetKeyboard);
            isSetChangeRegistered = false;
        }
        if ( mMainKeyboardView != null )
            mMainKeyboardView.unregisterReceiver();
        unregisterVolumeReceiver();
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    public void onCreate() {
        super.onCreate();
        LogPrint.d("skkim onCreate");
        registerVolumeReceiver();
        initShiftState();
        mWordSeparators = getResources().getString(R.string.aikbd_word_separators);
        AIKBD_DBHelper thelper = new AIKBD_DBHelper(getApplicationContext());
        if (!thelper.isThemeExist()) {
            MainKeyboardView.SetDefaultTheme(getApplicationContext());
        } else {
            Intent themeUpdated = new Intent("THEME_CHANGE");
            sendBroadcast(themeUpdated);
        }
        mWordSeparators = getResources().getString(R.string.aikbd_word_separators);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // enable for debug purpose only. otherwise, it will stuck here.
        // android.os.Debug.waitForDebugger();
        mAttribute = null;
        if ( !isSetChangeRegistered ) {
            isSetChangeRegistered = true;
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("KIND_CHANGE");
            intentFilter.addAction("SOUND_CHANGE");
            intentFilter.addAction(CHAT_GPT_RESUME);
            intentFilter.addAction(CHAT_GPT_PAUSE);
            intentFilter.addAction(SET_KEYBOARD_ON);
            intentFilter.addAction(SET_KEYBOARD_OFF);
            intentFilter.addAction(SET_KEYBOARD_SIZE);
            intentFilter.addAction(SoftKeyboard.SET_CHANGE);
            intentFilter.addAction(MyReceiver.VOLUME_CHANGE);
            // target 34 대응
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                registerReceiver(mSetKeyboard, intentFilter, Context.RECEIVER_EXPORTED);
            } else {
                registerReceiver(mSetKeyboard, intentFilter);
            }
//            registerReceiver(mSetKeyboard, intentFilter);
        }

        LogPrint.d("regist receiver SoftKeyboard 1");
//        registerReceiver(mSetKeyboard, new IntentFilter("KIND_CHANGE"));
//        registerReceiver(mSetKeyboard, new IntentFilter("SOUND_CHANGE"));
//        registerReceiver(mSetKeyboard, new IntentFilter(SET_KEYBOARD_ON));
//        registerReceiver(mSetKeyboard, new IntentFilter(SET_KEYBOARD_OFF));
//        registerReceiver(mSetKeyboard, new IntentFilter(SET_KEYBOARD_SIZE));
//        keywordLimit = SharedPreference.getInt(getApplicationContext(), Common.PREF_KEYWORD_CNT);
//        KeyboardLogPrint.w("keywordLimit ::::::: " + keywordLimit);
//        String keyword = SharedPreference.getString(getApplicationContext(), Common.PREF_SAVED_KEYWORD);
//        KeyboardLogPrint.w("keyword ::::::: " + keyword);
//        setKeywordList(keyword);
/**
 UserIdDBHelper helper = new UserIdDBHelper(getApplicationContext());
 KeyboardUserIdModel model = helper.getUserInfo();
 String deviceId = "";
 deviceId = model.getDeviceId();
 KeyboardLogPrint.e("SoftMeyboard onCreate deviceId");
 if (!TextUtils.isEmpty(deviceId)) {
 String today = Common.getDate();
 String savedDate = SharedPreference.getString(getApplicationContext(), Common.PREF_MATCHED_EMOJI_DATE);
 if (savedDate.isEmpty() || !today.equals(savedDate)) {

 connectLiveCount(deviceId);
 SharedPreference.setBoolean(getApplicationContext(), Common.PREF_AD_POSSIBLE, true);
 }
 }**/
    }

    /**
     * This is the point where you can do all of your UI initialization.  It
     * is called after creation and any configuration change.
     */
    @Override
    public void onInitializeInterface() {
        LogPrint.d("skkim onInitializeInterface");

        if (mEQwerty != null && mQwertyNum != null) {
            // Configuration changes can happen after the keyboard gets recreated,
            // so we need to be able to re-build the keyboards if the available
            // space has changed.
            int displayWidth = getMaxWidth();
            if (displayWidth == mLastDisplayWidth) return;
            mLastDisplayWidth = displayWidth;
        }

//        if (mMainKeyboardView != null ) {
//            mMainKeyboardView.orientationChanged();
//        }

        cAutomata = new ChunjiinAutomata();
        qAutomata = new KoreanAutomata();
        dAutomata = new DanAutomata();
        nAutomata = new NaraAutomata();
        cpAutomata = new ChunjiinPlusAutomata();

        mQSymbolsKeyboard = new LatinKeyboard(this, R.xml.aikbd_symbol_q_1); // 쿼티용 기호
        mQSymbolsKeyboard1 = new LatinKeyboard(this, R.xml.aikbd_symbol_q_2); // 쿼티용 기호 shift
        mQSymbolsKeyboard_35 = new LatinKeyboard(this, R.xml.aikbd_symbol_q_1_35); // 쿼티용 기호
        mQSymbolsKeyboard1_35 = new LatinKeyboard(this, R.xml.aikbd_symbol_q_2_35); // 쿼티용 기호 shift
        mSymbolsKeyboard = new LatinKeyboard(this, R.xml.aikbd_symbols); // 기호
        mNumberKeyboard = new LatinKeyboard(this, R.xml.aikbd_number); // 기호 SHIFT
        mNumberOnlyKeyboard = new LatinKeyboard(this, R.xml.aikbd_number_only); // mumber only
        mNumberSignedKeyboard = new LatinKeyboard(this, R.xml.aikbd_number_signed); // aikbd_number signed
        mNumberDecimalKeyboard = new LatinKeyboard(this, R.xml.aikbd_number_decimal); // aikbd_number decimal
        mNumberPhoneKeyboard = new LatinKeyboard(this, R.xml.aikbd_number_phone); // aikbd_number phone
        mPhoneSymbolKeyboard = new LatinKeyboard(this, R.xml.aikbd_phone_symbol); // phone symbol
        mSymbolsShiftedKeyboard = new LatinKeyboard(this, R.xml.aikbd_symbols_1);
        mSymbolsShiftedKeyboard1 = new LatinKeyboard(this, R.xml.aikbd_symbols_2);
        boolean isQwertyNumSet = SharedPreference.getBoolean(getApplicationContext(), Common.PREF_QWERTY_NUM_SETTING);
        if (isQwertyNumSet) {
            mQwertyNum = new LatinKeyboard(this, R.xml.aikbd_qwerty_n); // 영문 쿼티 + 숫자
            mQwertyNum35 = new LatinKeyboard(this, R.xml.aikbd_qwerty_n_35); // 영문 쿼티 + 숫자
            mQwerty = new LatinKeyboard(this, R.xml.aikbd_qwerty); // 영문 쿼티
            mQwerty35 = new LatinKeyboard(this, R.xml.aikbd_qwerty_35); // 영문 쿼티 target 35
            mQKoreanKeyboard = new LatinKeyboard(this, R.xml.aikbd_korean_n); // 한글
            mQKoreanKeyboard35 = new LatinKeyboard(this, R.xml.aikbd_korean_n_35); // 한글
//            mKoreanShiftedKeyboard = new LatinKeyboard(this, R.xml.aikbd_n_korean_shifted, true); // 한글 SHIFT
            mSejongKeyboard = new LatinKeyboard(this, R.xml.aikbd_sejong); // 천지인 keyboard
            mDanKeyboard = new LatinKeyboard(this, R.xml.aikbd_dan); // 단모음 keyboard
            mDanKeyboard35 = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_dan_35); // 단모음 keyboard
            mNaraKeyboard = new LatinKeyboard(this, R.xml.aikbd_nara); // 나랏글 keyboard
            mSejongPlusKeyboard = new LatinKeyboard(this, R.xml.aikbd_sejong_plus);
        } else {
            mQwertyNum = new LatinKeyboard(this, R.xml.aikbd_qwerty_n); // 영문 쿼티 + 숫자
            mQwertyNum35 = new LatinKeyboard(this, R.xml.aikbd_qwerty_n_35);
            mQwerty = new LatinKeyboard(this, R.xml.aikbd_qwerty); // 영문 쿼티
            mQwerty35 = new LatinKeyboard(this, R.xml.aikbd_qwerty_35); // 영문 쿼티 target 35
            mQKoreanKeyboard = new LatinKeyboard(this, R.xml.aikbd_korean); // 한글
            mQKoreanKeyboard35 = new LatinKeyboard(this, R.xml.aikbd_korean_35); // 한글
//            mKoreanShiftedKeyboard = new LatinKeyboard(this, R.xml.aikbd_korean_shifted, true); // 한글 SHIFT
            mSejongKeyboard = new LatinKeyboard(this, R.xml.aikbd_sejong); // 천지인 keyboard
            mDanKeyboard = new LatinKeyboard(this, R.xml.aikbd_dan); // 단모음 keyboard
            mDanKeyboard35 = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_dan_35); // 단모음 keyboard
            mNaraKeyboard = new LatinKeyboard(this, R.xml.aikbd_nara); // 나랏글 keyboard
            mSejongPlusKeyboard = new LatinKeyboard(this, R.xml.aikbd_sejong_plus);
        }

        mEmojiKeyboard = new LatinKeyboard(this, R.xml.aikbd_emoji);
        mEmoticonKeyboard = new LatinKeyboard(this, R.xml.aikbd_emoticon);

        WindowManager windowService = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int currentRatation = windowService.getDefaultDisplay().getRotation();
        if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
            if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM ) {
                mEQwerty = mQwerty35;
            } else
                mEQwerty = mQwerty;
        } else
            mEQwerty = mQwerty;
        mEmojiKeyboard = new LatinKeyboard(this, R.xml.aikbd_emoji);
        mEmoticonKeyboard = new LatinKeyboard(this, R.xml.aikbd_emoticon);

        LogPrint.d("keyboard_height currentRatation :: " + currentRatation);
        int keyHeightLevel = 0;
        if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
        } else
            keyHeightLevel = SharedPreference.getInt(getApplicationContext(), Common.PREF_KEYBOARD_SIZE_LEVEL) < 0 ? Common.DEFAULT_KEYBOARD_SIZE : SharedPreference.getInt(getApplicationContext(), Common.PREF_KEYBOARD_SIZE_LEVEL);

        try {
//            float fLevel = Float.parseFloat(String.format("%.2f", 0.90 + (float) (2 * keyHeightLevel) / 100));
            float fLevel = Common.GetHeightValue(keyHeightLevel);
            LogPrint.d("keyboard_height onInitializeInterface fLevel :: " + fLevel + " , keyHeightLevel :: " + keyHeightLevel);
            setKeyHeight(fLevel);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mBackupKeyboard = null;

        mKoreanKeyboardMode = SharedPreference.getInt(getApplicationContext(), Common.PREF_KEYBOARD_MODE);
        if ( IsBackupKeyboardExist() )
            mKoreanKeyboardMode = 2;
        if (mKoreanKeyboardMode < 0) mKoreanKeyboardMode = 0;
        KeyboardLogPrint.e("SoftKeyboard 1 kind :: " + mKoreanKeyboardMode);


        if (mKoreanKeyboardMode == Common.MODE_CHUNJIIN) {
            if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                LogPrint.d("kauto qAutomata 10");
                kauto = qAutomata;
                mKoreanKeyboard = mQKoreanKeyboard;
                if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM )
                    mKoreanKeyboard = mQKoreanKeyboard35;
            } else {
                LogPrint.d("kauto cAutomata 11");
                kauto = cAutomata;
                mKoreanKeyboard = mSejongKeyboard;
            }
        } else if (mKoreanKeyboardMode == Common.MODE_QUERTY) {
            LogPrint.d("kauto qAutomata 12");
            kauto = qAutomata;
            mKoreanKeyboard = mQKoreanKeyboard;
            if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM ) {
                    mEQwerty = mQwerty35;
                    mKoreanKeyboard = mQKoreanKeyboard35;
                } else
                    mEQwerty = mQwerty;
            } else
                mEQwerty = mQwerty;
        } else if (mKoreanKeyboardMode == Common.MODE_DAN) {
            LogPrint.d("kauto dAutomata 13");
            kauto = dAutomata;
            mKoreanKeyboard = mDanKeyboard;
            if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM )
                    mKoreanKeyboard = mDanKeyboard35;
            }

        } else if (mKoreanKeyboardMode == Common.MODE_NARA) {
            if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                LogPrint.d("kauto qAutomata 14");
                kauto = qAutomata;
                mKoreanKeyboard = mQKoreanKeyboard;
                if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM )
                    mKoreanKeyboard = mQKoreanKeyboard35;
            } else {
                LogPrint.d("kauto nAutomata 15");
                kauto = nAutomata;
                mKoreanKeyboard = mNaraKeyboard;
            }
        } else if (mKoreanKeyboardMode == Common.MODE_CHUNJIIN_PLUS) {
            if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                LogPrint.d("kauto qAutomata 16");
                kauto = qAutomata;
                mKoreanKeyboard = mQKoreanKeyboard;
                if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM )
                    mKoreanKeyboard = mQKoreanKeyboard35;
            } else {
                LogPrint.d("kauto cpAutomata 17");
                kauto = cpAutomata;
                mKoreanKeyboard = mSejongPlusKeyboard;
            }
        }
    }

    /**
     * Called by the framework when your view for creating aikbd_input needs to
     * be generated.  This will be called the first time your aikbd_input aikbd_method
     * is displayed, and every time it needs to be re-created such as due to
     * a configuration change.
     */
    @Override
    public View onCreateInputView() {
        KeyboardLogPrint.w("skkim onCreateInputView");
//        staticApplicationContext = getApplicationContext();
        KeyboardView mInputView = (KeyboardView) getLayoutInflater().inflate(R.layout.aikbd_input, null);
        if (mInputView == null) return null;

//        ADModel model = null;
//        if (mTempModel != null) {
//            model = mTempModel;
//            mTempModel = null;
//        }

		int keyboardMode = SharedPreference.getInt(this, Common.PREF_KEYBOARD_MODE);
        if ( IsBackupKeyboardExist() )
            keyboardMode = 2;
        LogPrint.d("keyboardMode :: " + keyboardMode);
        LatinKeyboard currKeyboard = getSelectKeyboard(keyboardMode);
        if ( currKeyboard != null && mInputView != null ) {
            mInputView.setKeyboard(currKeyboard);
            LogPrint.d("keyboard_height currKeyboard height :: " + currKeyboard.getHeight());
        }


        mMainKeyboardView = new MainKeyboardView(this,  getApplication(), currKeyboard, mEmojiKeyboard, mEmoticonKeyboard, mSejongKeyboard.getHeight(),
                new MainKeyboardView.OnClickCallbackListener() {
                    @Override
                    public void onReceive(String matchStr, String emoji) {
                        KeyboardLogPrint.d("stop complete called 1");
//                if (mHandler != null)
//                    mHandler.removeCallbacksAndMessages(null);
                        isCharactorEntered = true;
                        //timerCancel(0);
                        KeyboardLogPrint.d("stop complete called 17");
                        if (mCompleteHandler != null)
                            mCompleteHandler.sendEmptyMessage(STOP_COMPLETE_TIMER);
                        mCompleteCount = 0;
                        mComposing.append(emoji);
                        //inputConnection = getCurrentInputConnection();
                        if (inputConnection != null) {
//                    inputConnection.setComposingText(mComposing, 1);
                            KeyboardLogPrint.d("skkim null commitText 28 :: " + mComposing.toString());
                            inputConnection.commitText(mComposing, 1);
                            inputConnection.finishComposingText();
                            if (kauto != null) {
                                kauto.setInitState();
                                kauto.FinishAutomataWithoutInput();
                            }
                            mComposing.setLength(0);
                            mIsComplete = true;
                            mIsCompleteFromHandler = false;
                        }
                    }
                },
                new MainKeyboardView.OnEClickCallbackListener() {
                    @Override
                    public void onReceive(int primaryKey) {
                        if (primaryKey == -2) {
                            if (mMainKeyboardView != null) {
                                Keyboard curKeyboard = mMainKeyboardView.getKeyboard();
                                boolean isQwertySymbol = false;
                                if (curKeyboard == mEQwerty || curKeyboard == mQwertyNum || curKeyboard == mQwertyNum35) {
                                    isQwertySymbol = true;
                                } else if (curKeyboard == mKoreanKeyboard) {
                                    if (mKoreanKeyboardMode == Common.MODE_QUERTY || mKoreanKeyboardMode == Common.MODE_DAN)
                                        isQwertySymbol = true;
                                }
                                if (isQwertySymbol) {
                                    WindowManager windowService = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                                    int currentRatation = windowService.getDefaultDisplay().getRotation();
                                    mMainKeyboardView.setKeyboard(mQSymbolsKeyboard); // 기호 클릭 시 쿼티 심볼로 설정
                                    if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                                        if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM )
                                            mMainKeyboardView.setKeyboard(mQSymbolsKeyboard_35); // 기호 클릭 시 쿼티 심볼로 설정
                                    }

                                    mMainKeyboardView.selectKeyboard(MainKeyboardView.GUBUN_KEYBOARD);
                                } else {
                                    mMainKeyboardView.setKeyboard(mSymbolsKeyboard); // 기호 클릭 시 논 쿼티 심볼로 설정
                                    mMainKeyboardView.selectKeyboard(MainKeyboardView.GUBUN_KEYBOARD);
                                }
                            }
                        } else if (primaryKey == -6) {
                            if (mMainKeyboardView != null) {
                                Keyboard curKeyboard = mMainKeyboardView.getKeyboard();
                                if (curKeyboard == mEQwerty) {
                                    mCurKeyboard = mEQwerty;
                                    mMainKeyboardView.setKeyboard(mCurKeyboard); // 한영버튼 클릭 시 쿼티의 영문(not num)으로 설정
//                                    mMainKeyboardView.setKeyboard(mQwertyKeyboard);
                                    setKeyBoardMode(false, -1, 11);
                                } else if (curKeyboard == mQwertyNum) {
                                    mCurKeyboard = mQwertyNum;
                                    mMainKeyboardView.setKeyboard(mCurKeyboard); // 한영버튼 클릭 시 쿼티의 영문(num)으로 설정
//                                    mMainKeyboardView.setKeyboard(mQwertyKeyboard);
                                    setKeyBoardMode(false, -1, 21);
                                } else if (curKeyboard == mQwertyNum35) {
                                    mCurKeyboard = mQwertyNum35;
                                    mMainKeyboardView.setKeyboard(mCurKeyboard); // 한영버튼 클릭 시 쿼티의 영문(num)으로 설정
//                                    mMainKeyboardView.setKeyboard(mQwertyKeyboard);
                                    setKeyBoardMode(false, -1, 21);
                                } else if (curKeyboard == mKoreanKeyboard) {
                                    WindowManager windowService = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                                    int currentRatation = windowService.getDefaultDisplay().getRotation();
                                    if (mKoreanKeyboardMode == Common.MODE_CHUNJIIN) {
                                        if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                                            mKoreanKeyboard = mQKoreanKeyboard;
                                            if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM )
                                                mKoreanKeyboard = mQKoreanKeyboard35;
                                        } else {
                                            mKoreanKeyboard = mSejongKeyboard;
                                        }
                                    } else if (mKoreanKeyboardMode == Common.MODE_QUERTY) {
                                        mKoreanKeyboard = mQKoreanKeyboard;
                                        if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                                            if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM ) {
                                                mKoreanKeyboard = mQKoreanKeyboard35;
                                                mEQwerty = mQwerty35;
                                            } else
                                                mEQwerty = mQwerty;
                                        } else
                                            mEQwerty = mQwerty;
                                    } else if (mKoreanKeyboardMode == Common.MODE_DAN) {
                                        mKoreanKeyboard = mDanKeyboard;
                                        if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                                            if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM )
                                                mKoreanKeyboard = mDanKeyboard35;
                                        }
                                    } else if (mKoreanKeyboardMode == Common.MODE_NARA) {
                                        if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                                            mKoreanKeyboard = mQKoreanKeyboard;
                                            if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM )
                                                mKoreanKeyboard = mQKoreanKeyboard35;
                                        } else {
                                            mKoreanKeyboard = mNaraKeyboard;
                                        }
                                    } else if (mKoreanKeyboardMode == Common.MODE_CHUNJIIN_PLUS) {
                                        if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                                            mKoreanKeyboard = mQKoreanKeyboard;
                                            if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM )
                                                mKoreanKeyboard = mQKoreanKeyboard35;
                                        } else {
                                            mKoreanKeyboard = mSejongPlusKeyboard;
                                        }
                                    }


                                    mCurKeyboard = mKoreanKeyboard;
                                    mMainKeyboardView.setKeyboard(mCurKeyboard); // 한영버튼 클릭 시 천지인, 천지인+,단모음, 나랏글 등의 한글 키보드로 설정
//                                    mMainKeyboardView.setKeyboard(mKoreanKeyboard);
                                    setKeyBoardMode(true, -1, 12);

                                    if (mKoreanKeyboard == mQKoreanKeyboard || mKoreanKeyboard == mQKoreanKeyboard35 )
                                        ((LatinKeyboardView) mMainKeyboardView.getKeyboardView()).setKeyboardMode(Common.MODE_QUERTY);
                                } else {
                                    // 2017.05.02 mNoKorean flag 삭제
                                    if (!kauto.IsKoreanMode()) {
//                                    if ((mNoKorean || !kauto.IsKoreanMode())) {
                                        boolean isQwertyNumSet = SharedPreference.getBoolean(getApplicationContext(), Common.PREF_QWERTY_NUM_SETTING);
                                        if (mKoreanKeyboardMode == Common.MODE_QUERTY && isQwertyNumSet) {
                                            mCurKeyboard = mQwertyNum;
                                            WindowManager windowService = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                                            int currentRatation = windowService.getDefaultDisplay().getRotation();
                                            if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                                                if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM )
                                                    mCurKeyboard = mQwertyNum35;
                                            }
                                        } else
                                            mCurKeyboard = mEQwerty;
                                        setKeyBoardMode(false, -1, 13);
                                    } else {
                                        WindowManager windowService = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                                        int currentRatation = windowService.getDefaultDisplay().getRotation();
                                        if (mKoreanKeyboardMode == Common.MODE_CHUNJIIN) {
                                            if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                                                mKoreanKeyboard = mQKoreanKeyboard;
                                                if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM )
                                                    mKoreanKeyboard = mQKoreanKeyboard35;
                                            } else {
                                                mKoreanKeyboard = mSejongKeyboard;
                                            }
                                        } else if (mKoreanKeyboardMode == Common.MODE_QUERTY) {
                                            mKoreanKeyboard = mQKoreanKeyboard;
                                            if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                                                if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM ) {
                                                    mEQwerty = mQwerty35;
                                                    mKoreanKeyboard = mQKoreanKeyboard35;
                                                } else
                                                    mEQwerty = mQwerty;
                                            } else
                                                mEQwerty = mQwerty;
                                        } else if (mKoreanKeyboardMode == Common.MODE_DAN) {
                                            mKoreanKeyboard = mDanKeyboard;
                                            if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                                                if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM )
                                                    mKoreanKeyboard = mDanKeyboard35;
                                            }
                                        } else if (mKoreanKeyboardMode == Common.MODE_NARA) {
                                            if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                                                mKoreanKeyboard = mQKoreanKeyboard;
                                                if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM )
                                                    mKoreanKeyboard = mQKoreanKeyboard35;
                                            } else {
                                                mKoreanKeyboard = mNaraKeyboard;
                                            }
                                        } else if (mKoreanKeyboardMode == Common.MODE_CHUNJIIN_PLUS) {
                                            if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                                                mKoreanKeyboard = mQKoreanKeyboard;
                                                if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM )
                                                    mKoreanKeyboard = mQKoreanKeyboard35;
                                            } else {
                                                mKoreanKeyboard = mSejongPlusKeyboard;
                                            }
                                        }

                                        mCurKeyboard = mKoreanKeyboard;

                                        setKeyBoardMode(true, -1, 14);
                                        if (mKoreanKeyboard == mQKoreanKeyboard || mKoreanKeyboard == mQKoreanKeyboard35)
                                            ((LatinKeyboardView) mMainKeyboardView.getKeyboardView()).setKeyboardMode(Common.MODE_QUERTY);
                                    }
                                }
                                mMainKeyboardView.selectKeyboard(MainKeyboardView.GUBUN_KEYBOARD);
                            }
                        } else if (primaryKey == -5) {
                            handleBackspace();
                            /**
                             keyDownUp(KeyEvent.KEYCODE_DEL);
                             if (mShiftState != SHIFT_STATE_ONLY_UPPER_CASE)
                             updateShiftKeyState(getCurrentInputEditorInfo(), -2); // 이모티콘 키보드에서 삭제 버튼 눌렀을 경우**/
                        }

                        if (inputConnection != null && isInputViewShown()) {
                            ExtractedText et = inputConnection.getExtractedText(new ExtractedTextRequest(), 0);
                            if (et != null) {
                                CharSequence seq = et.text;
                                String temp = seq.toString();
                            }
                        }
                    }
                },
                new MainKeyboardView.BannerAdCallbackListener() {
                    @Override
                    public void onBannerCallResult(boolean result) {
                        // 택스트베너 호출 결과
                        if (result) {
                            bannerIndex++;
                            SharedPreference.setInt(getApplicationContext(), Key.KEY_OCB_AD_BANNER_INDEX, bannerIndex);
                        } else {
                            // 2022.12.23 loadBanner에서 mobwith 광고로 변경
                            if ( mMainKeyboardView != null )
                                mMainKeyboardView.loadMobWithAd();
                            /*
                            if (mMainKeyboardView != null)
                                mMainKeyboardView.loadBanner(false);

                             */
                            //mMainKeyboardView.loadCriteoBanner();
                            //mMainKeyboardView.loadMediaBanner();
                        }
                    }
                }
        );
        mInputView = mMainKeyboardView.getKeyboardView();
        mInputView.setOnKeyboardActionListener(this);
        int mode = SharedPreference.getInt(this, Common.PREF_KEYBOARD_MODE);
        if ( IsBackupKeyboardExist() )
            mode = 2;
        mCurKeyboard = getSelectKeyboard(mode);
        KeyboardLogPrint.e("SoftKeyboard 4 kind :: " + mCurKeyboard);
        setKeyBoardMode(true, -1, 15);

        mKView = mMainKeyboardView.getView();

        RelativeLayout layout = new RelativeLayout(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layout.addView(mKView);
        layout.setLayoutParams(params);

        mMainKeyboardView.selectKeyboard(MainKeyboardView.GUBUN_KEYBOARD);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                KeyboardLogPrint.e("onCreateInputView invalidateAllKeys");
                mMainKeyboardView.getKeyboardView().invalidateAllKeys();

            }
        }, 200);

        return layout;
    }

    private LatinKeyboard getSelectKeyboard(int kind) {
        if (kind < 0) kind = 0;
        if (kind == Common.MODE_CHUNJIIN) {
            if (mMainKeyboardView != null)
                mMainKeyboardView.setPreviewEnabled(mIsPreviewSet, false);
            return mSejongKeyboard;
        } else if (kind == Common.MODE_QUERTY) {
            if (mMainKeyboardView != null)
                mMainKeyboardView.setPreviewEnabled(mIsPreviewSet, true);
            return mKoreanKeyboard;
        } else if (kind == Common.MODE_NARA) {
            if (mMainKeyboardView != null)
                mMainKeyboardView.setPreviewEnabled(mIsPreviewSet, true);
            return mNaraKeyboard;
        } else if (kind == Common.MODE_DAN) {
            if (mMainKeyboardView != null)
                mMainKeyboardView.setPreviewEnabled(mIsPreviewSet, true);
            WindowManager windowService = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            int currentRatation = windowService.getDefaultDisplay().getRotation();
            if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM )
                    return mDanKeyboard35;
            }
            return mDanKeyboard;
        } else if (kind == Common.MODE_CHUNJIIN_PLUS) {
            if (mMainKeyboardView != null)
                mMainKeyboardView.setPreviewEnabled(mIsPreviewSet, false);
            return mSejongPlusKeyboard;
        } else {
            if (mMainKeyboardView != null)
                mMainKeyboardView.setPreviewEnabled(mIsPreviewSet, false);
            return mSejongKeyboard;
        }
    }

    /**
     * Called by the framework when your view for showing candidates needs to
     * be generated, like {@link #onCreateInputView}.
     */
    // no candidate window for Korean
//    @Override public View onCreateCandidatesView() {
//        LogPrint.d("onCreateCandidatesView");
//        EmojiKeyboardView emojiKeyboardView = (EmojiKeyboardView) getLayoutInflater()
//                .inflate(R.layout.emoji_keyboard_layout, null);
//        return emojiKeyboardView.getView();
////        mCandidateView = new CandidateView(this);
////        mCandidateView.setService(this);
////        return mCandidateView;
//    }

    // add this. no need full screen IME mode for this IME. ---- KGS need to check...
//    @Override
//    public void onUpdateExtractingVisibility(EditorInfo ei) {
//        ei.imeOptions |= EditorInfo.IME_FLAG_NO_EXTRACT_UI;
//        ei.imeOptions |= EditorInfo.IME_FLAG_NO_FULLSCREEN;
//        super.onUpdateExtractingVisibility(ei);
//    }

    @Override
    public boolean onExtractTextContextMenuItem(int id) {
        LogPrint.d("skkim onExtractTextContextMenuItem");
        return super.onExtractTextContextMenuItem(id);
    }

    @Override
    public void onExtractedTextClicked() {
        super.onExtractedTextClicked();
        LogPrint.d("skkim onExtractedTextClicked");
    }
    @Override
    public void onExtractedSelectionChanged(int start, int end) {
        super.onExtractedSelectionChanged(start, end);
        LogPrint.d("skkim onExtractedSelectionChanged start :: " + start + " , end :: " + end);
    }

    @Override
    public void onUpdateExtractingVisibility(EditorInfo ei) {
        super.onUpdateExtractingVisibility(ei);
        LogPrint.d("skkim onUpdateExtractingVisibility");
    }

    @Override
    public void onUpdateCursorAnchorInfo(CursorAnchorInfo ci) {
        super.onUpdateCursorAnchorInfo(ci);
        LogPrint.d("skkim onUpdateCursorAnchorInfo");

    }

    @Override
    public void onUpdateExtractedText(int token, ExtractedText et) {
        super.onUpdateExtractedText(token, et);
        LogPrint.d("skkim onUpdateExtractedText");
    }

    @Override
    public void onUpdateExtractingViews(EditorInfo ei) {
        super.onUpdateExtractingViews(ei);
        LogPrint.d("skkim onUpdateExtractingViews");
    }

    @Override
    public void onUpdateCursor(Rect nc) {
        super.onUpdateCursor(nc);
        LogPrint.d("skkim onUpdateCursor");
    }


    /**
     * This is the main point where we do our initialization of the input method
     * to begin operating on an application.  At this point we have been
     * bound to the client, and are now receiving all of the detailed information
     * about the target of our edits.
     */
    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);
        KeyboardLogPrint.w("skkim onStartInput : " + attribute);
        inputConnection = getCurrentInputConnection();

        mAttribute = attribute;
        // Reset our state.  We want to do this even if restarting, because
        // the underlying state of the text editor could have changed in any way.
        mComposing.setLength(0);
        // updateCandidates();

        if (!restarting) {
            // Clear shift states.
            mMetaState = 0;
        }

        // mPredictionOn = false;
        // mCompletionOn = false;
        // mCompletions = null;
        kauto.FinishAutomataWithoutInput();

        // We are now going to initialize our state based on the type of
        // text being edited.
        int val = attribute.inputType & EditorInfo.TYPE_MASK_CLASS;
        KeyboardLogPrint.e("attr onStartInput :: " + val);
        switch (attribute.inputType & EditorInfo.TYPE_MASK_CLASS) {
            //숫자나 날짜, 시간, 전화번호 타입이면 ...키보드를 mSymbolsKeyboard로 정한다.

            case EditorInfo.TYPE_CLASS_NUMBER:
            case EditorInfo.TYPE_CLASS_DATETIME:
            case EditorInfo.TYPE_CLASS_PHONE:
                // Numbers and dates default to the symbols keyboard, with
                // no extra features.

                //Keyboard curKeyboard = mMainKeyboardView.getKeyboard();
                boolean isQwertySymbol = false;
                if (mCurKeyboard == mEQwerty || mCurKeyboard == mQwertyNum || mCurKeyboard == mQwertyNum35) {
                    isQwertySymbol = true;
                } else if (mCurKeyboard == mKoreanKeyboard) {
                    if (mKoreanKeyboardMode == Common.MODE_QUERTY || mKoreanKeyboardMode == Common.MODE_DAN)
                        isQwertySymbol = true;
                }
                if (isQwertySymbol) {
                    mCurKeyboard = mQSymbolsKeyboard;
                    WindowManager windowService = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                    int currentRatation = windowService.getDefaultDisplay().getRotation();
                    if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                        if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM )
                            mCurKeyboard = mQSymbolsKeyboard_35;
                    }
                }

                else
                    mCurKeyboard = mSymbolsKeyboard;
                setKeyBoardMode(false, SYMBOL_KEYBOARD, 27);

                // 2017.05.02 korean flag 삭제
//                mNoKorean = true;
                if (kauto.IsKoreanMode())
                    kauto.ToggleMode();
                // 현재 한글 모드이면 한글 모드 아닌것으로 바꿔준다.
                kauto.FinishAutomataWithoutInput();
                break;
//           case EditorInfo.TYPE_CLASS_PHONE:
//                // Phones will also default to the symbols keyboard, though
//                // often you will want to have a dedicated phone keyboard.
//                mCurKeyboard = mSymbolsKeyboard;
//                mNoKorean = true;
//                if (kauto.IsKoreanMode())
//                	kauto.ToggleMode();
//                break;
            // 모든 글자 타입일 경우
            case EditorInfo.TYPE_CLASS_TEXT:
                // This is general text editing.  We will default to the
                // normal alphabetic keyboard, and assume that we should
                // be doing predictive text (showing candidates as the
                // user types).

                // mPredictionOn = false;

                // We now look for a few special variations of text that will
                // modify our behavior.
                int variation = attribute.inputType & EditorInfo.TYPE_MASK_VARIATION;
                KeyboardLogPrint.d("skkim variation :: " + variation);
                if (variation == EditorInfo.TYPE_TEXT_VARIATION_PASSWORD ||
                        variation == EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    // Do not display predictions / what the user is typing
                    // when they are entering a password.
                    //mNoKorean = true;
                    // mPredictionOn = false;
                }

                // 2017.02.27 설청창에서 키보드가 호출되었을 경우 오토마타의 한글 모드 설정이 한글이 아닐경우 강제적으로 한글로 바꿔준다.
                KeyboardLogPrint.d("skkim mIsSettingOn :: " + mIsSettingOn);
                if (mIsSettingOn) {
                    if (!kauto.IsKoreanMode()) {
                        kauto.ToggleMode();
                    }
                }
                // 2017.05.02 mNoKorean flag 삭제
                KeyboardLogPrint.d("skkim kauto.IsKoreanMode() :: " + kauto.IsKoreanMode());
                if (!kauto.IsKoreanMode()) {
//                if ((mNoKorean || !kauto.IsKoreanMode())) {
                    boolean isQwertyNumSet = SharedPreference.getBoolean(getApplicationContext(), Common.PREF_QWERTY_NUM_SETTING);
                    KeyboardLogPrint.d("skkim isQwertyNumSet :: " + isQwertyNumSet);
                    if (mKoreanKeyboardMode == Common.MODE_QUERTY && isQwertyNumSet) {
                        mCurKeyboard = mQwertyNum;
                        WindowManager windowService = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                        int currentRatation = windowService.getDefaultDisplay().getRotation();
                        if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                            if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM )
                                mCurKeyboard = mQwertyNum35;
                        }
                    } else
                        mCurKeyboard = mEQwerty;
                    setKeyBoardMode(false, -1, 28);
                } else {
                    if (mKoreanKeyboardMode == Common.MODE_QUERTY) {
                        mCurKeyboard = mKoreanKeyboard;
                    } else if (mKoreanKeyboardMode == Common.MODE_CHUNJIIN) {
                        mCurKeyboard = mSejongKeyboard;
                    } else if (mKoreanKeyboardMode == Common.MODE_DAN) {
                        mCurKeyboard = mDanKeyboard;
                        WindowManager windowService = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                        int currentRatation = windowService.getDefaultDisplay().getRotation();
                        if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                            if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM )
                                mCurKeyboard = mDanKeyboard35;
                        }
                    } else if (mKoreanKeyboardMode == Common.MODE_NARA) {
                        mCurKeyboard = mNaraKeyboard;
                    } else if (mKoreanKeyboardMode == Common.MODE_CHUNJIIN_PLUS) {
                        mCurKeyboard = mSejongPlusKeyboard;
                    } else {
                        mCurKeyboard = mKoreanKeyboard;
                    }
                    setKeyBoardMode(true, -1, 29);
                }
                updateShiftKeyState(attribute, -1); // onStartInput 에서 모든글자 타입(TYPE_CLASS_TEXT)일 경우
                break;

            default:
                // For all unknown input types, default to the alphabetic
                // keyboard with no special features.
                // mCurKeyboard = mQwertyKeyboard;

                if (kauto.IsKoreanMode()) {
                    mCurKeyboard = mKoreanKeyboard;
                    setKeyBoardMode(true, -1, 30);
                } else {
                    boolean isQwertyNumSet = SharedPreference.getBoolean(getApplicationContext(), Common.PREF_QWERTY_NUM_SETTING);
                    if (mKoreanKeyboardMode == Common.MODE_QUERTY && isQwertyNumSet) {
                        mCurKeyboard = mQwertyNum;
                        WindowManager windowService = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                        int currentRatation = windowService.getDefaultDisplay().getRotation();
                        if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                            if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM )
                                mCurKeyboard = mQwertyNum35;
                        }
                    } else
                        mCurKeyboard = mEQwerty;
                    setKeyBoardMode(false, -1, 31);
                }

                updateShiftKeyState(attribute, 0); // onStartInput의 default
        }

        if ( mCurKeyboard != null )
            mCurKeyboard.setImeOptions(getResources(), attribute.imeOptions);
    }

    @Override
    public void onFinishInputView(boolean finishingInput) {
        LogPrint.d("skkim onFinishInputView");

        if (mBackupCurKeyboard != null && mBackupKauto != null) {
            LogPrint.d("backup keyboard gubun  backup not null return");
            return;
        } else {
            LogPrint.d("backup keyboard gubun  backup null keep going");
        }
        recoverInputConnection();
        int keyboardHeight = 0;
        if ( mMainKeyboardView != null )
            keyboardHeight = mMainKeyboardView.getKeyboardHeight();
        Intent keyboardStatusIntent = new Intent("KEYBOARD_STATUS");
        keyboardStatusIntent.putExtra("isShow", false);
        keyboardStatusIntent.putExtra("keyboardHeight", keyboardHeight);
        sendBroadcast(keyboardStatusIntent);
        /**
         liveTime = liveTime + mKeyboardActiveCount;
         liveFrequency++;
         SharedPreference.setLong(getApplicationContext(), Key.KEY_LIVE_TIME, liveTime);
         SharedPreference.setInt(getApplicationContext(), Key.KEY_LIVE_FREQUENCY, liveFrequency);
         if ( mActiveHandler != null ) {
         mActiveHandler.sendEmptyMessage(STOP_ACTIVE_TIMER);
         }**/
        String uuid = SharedPreference.getString(getApplicationContext(), Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            uuid = cp.Decode(getApplicationContext(), uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }

/**
 CustomAsyncTask apiAsyncTask = new CustomAsyncTask(getApplicationContext());
 apiAsyncTask.postFrequencyLiveTime(uuid, String.valueOf(mKeyboardActiveCount), (result, obj) -> {

 if (result && obj != null)
 LogPrint.d("postFrequencyLiveTime : " + (JSONObject) obj);
 else
 LogPrint.d("postFrequencyLiveTime result : " + result);
 });**/
/**
 mKeyboardActiveTimer.cancel();
 mKeyboardActiveCount = 0;
 **/

        SetImeOption(-1);
        if (mSoundHandler != null) {
            mSoundHandler.sendEmptyMessage(1);
        }
        KeyboardLogPrint.d("stop complete called 2");
        //timerCancel(1);
        if (mCompleteHandler != null)
            mCompleteHandler.sendEmptyMessage(STOP_COMPLETE_TIMER);
        mCompleteCount = 0;
        InputConnection ic = getCurrentInputConnection();
        ic = inputConnection;
        if (ic != null) {
            KeyboardLogPrint.d("onFinishInputView ic not null");
            mAutomataValues = kauto.getAutomataValue();
            kauto.setInitState();
            kauto.FinishAutomataWithoutInput();
            mComposing.setLength(0);
            inputConnection.finishComposingText();

            /**
             * 2018.01.03 keyword add 추가
             */
            /**
             * ocb에서 사용하지 않으므로 주석
             String temp = mEditStr;
             if (!TextUtils.isEmpty(temp) && mQuickRun) {
             String[] temps = temp.split(" ");
             KeyboardLogPrint.d("temps length  :: " + temps.length);
             if (temps.length > 0) {
             String tStr = removedEmojiStr(temps[0]);
             KeyboardLogPrint.e("tStr :: " + tStr);
             addKeyword(tStr);
             }

             long nextTime = SharedPreference.getLong(getApplicationContext(), Common.PREF_NEXT_TIME);
             long currentTime = System.currentTimeMillis();
             KeyboardLogPrint.d("nextTime :: " + nextTime);
             KeyboardLogPrint.d("currentTime :: " + currentTime);
             if (nextTime < 0 || nextTime <= currentTime) {
             String keyword = getKeywordList();
             KeyboardLogPrint.e("result keyword :: " + keyword);
             if (model == null) {
             return;
             }
             if ((currentTime - mCallTime) > 2000) {
             mCallTime = currentTime;
             KeyboardLogPrint.e("send keyword api called");
             CustomAsyncTask task = new CustomAsyncTask(getApplicationContext());
             task.connectSendKeyword(model, keyword, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override public void onResponse(boolean rt, Object obj) {
            if (rt) {
            if (obj != null) {
            JSONObject object = (JSONObject) obj;
            try {
            if (object != null) {
            boolean result = object.optBoolean("Result");
            long term = object.optLong("time");
            long nextTime = System.currentTimeMillis() + term;
            SharedPreference.setLong(getApplicationContext(), Common.PREF_NEXT_TIME, nextTime);
            }
            } catch (Exception e) {
            e.printStackTrace();
            }
            }
            }
            }
            });
             }
             } else {
ㄷ
             }
             }**/

        } else {
            KeyboardLogPrint.e("ic null");
        }

        if (mMainKeyboardView != null) {
            // 음성인식 주석처리
            // mMainKeyboardView.stopVoiceRecognizer();

            mMainKeyboardView.setADVisible(false, null);
            mMainKeyboardView.goneMobwithADView();
            mMainKeyboardView.goneMobonADView();
            mMainKeyboardView.goneSearchLayout();
            mMainKeyboardView.stopAllTimer();
            mMainKeyboardView.goneADView();
            mMainKeyboardView.goneRewardBanner();
            mMainKeyboardView.goneBannerView();
            mMainKeyboardView.goneCoupangDYView();
            mMainKeyboardView.goneCriteoADView();
            mMainKeyboardView.goneMixerLayer();
            mMainKeyboardView.setTabImages(MainKeyboardView.KEYBOARD);
            mMainKeyboardView.init();
        }

        // 다음번 키보드 올라오는 것이 5회째이므로 4회 째 내려갔을 경우 point 1 올려줌.
        //if ( lastEnteredPrimaryCode == 10 && isCorrectEnter && isCharactorEntered ) {

        if ( isKeyboardShow ) {
            isKeyboardShow = false;
            // 2022.11.17 키보드 내릴 때 api 통합할 부분
            if (!TextUtils.isEmpty(uuid)) {
                if (isCharactorEntered) {
                    CustomAsyncTask task1 = new CustomAsyncTask(getApplicationContext());
                    task1.getJointFinish(MainKeyboardView.SPOT_POINT_KEYBOARD, String.valueOf(mKeyboardActiveCount),  new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                        @Override
                        public void onResponse(boolean result, Object obj) {
                            LogPrint.d("joint finish result :: " + result);
                            if (result) {
                                try {
                                    JSONObject object = (JSONObject) obj;
                                    if (object != null) {
                                        LogPrint.d("object != null obj :: " + object.toString());
                                        JSONObject spot_obj = object.optJSONObject("spot");
                                        if ( spot_obj != null ) {
                                            boolean spot_rt = spot_obj.optBoolean("Result");
                                            if (spot_rt) {
                                                String message = spot_obj.optString("message");
                                                if (mMainKeyboardView != null) {
                                                    mMainKeyboardView.showSurpriseToast(isInAppKeyboard, getApplicationContext(), message);
                                                }
                                            }
                                        }

                                        JSONObject frequency_obj = object.optJSONObject("frequency");
                                        if ( frequency_obj != null ) {
                                            LogPrint.d("frequency_obj != null obj :: " + frequency_obj.toString());
                                            boolean frequency_rt = frequency_obj.optBoolean("Result");
                                            if ( frequency_rt ) {
                                                String ratio_str = frequency_obj.optString("ratio");
                                                String mobon_str = frequency_obj.optString("mobon");
                                                String mediation_str = frequency_obj.optString("mediation");
                                                String banner_str = frequency_obj.optString("banner");
                                                String coupang_str = frequency_obj.optString("coupang");
                                                String notice_str = frequency_obj.optString("notice");
                                                String criteo_str = frequency_obj.optString("criteo");
                                                String reward_str = frequency_obj.optString("reward");

                                                JSONObject new_obj = new JSONObject();
                                                new_obj.put("ratio", ratio_str);
                                                new_obj.put("mobon", mobon_str);
                                                new_obj.put("mediation", mediation_str);
                                                new_obj.put("banner", banner_str);
                                                new_obj.put("coupang", coupang_str);
                                                new_obj.put("notice", notice_str);
                                                new_obj.put("criteo", criteo_str);
                                                new_obj.put("reward", reward_str);

                                                LogPrint.d("new_obj :: " + new_obj.toString());

                                                SharedPreference.setString(getApplicationContext(), Key.KEY_OCB_AD_FREQUENCY, new_obj.toString());
                                            }
                                        }

                                        JSONObject offerwall_obj = object.optJSONObject("offerwall");
                                        if ( offerwall_obj != null ) {
                                            boolean offerwall_result = offerwall_obj.optBoolean("Result");
                                            if ( offerwall_result ) {
                                                String logic = offerwall_obj.optString("logic");
                                                LogPrint.d("logic :: " + logic);
                                                boolean isHybrid = true;
                                                if ( "Native".equals(logic) ) {
                                                    isHybrid = false;
                                                }
                                                if ( mMainKeyboardView != null ) {
                                                    mMainKeyboardView.setOfferwallStatus(isHybrid);
                                                }
                                            }
                                        }

                                        JSONObject game_obj = object.optJSONObject("gamezone");
                                        if ( game_obj != null ) {
                                            boolean game_result = game_obj.optBoolean("Result");
                                            String game_YN = game_obj.optString("use_YN");
                                            if ( game_result ) {
                                                if ( mMainKeyboardView != null ) {
                                                    int status = Common.GAME_STATUS_NO;
                                                    if ( "Y".equals(game_YN) ) {
                                                        status = Common.GAME_STATUS_YES;
                                                    }
                                                    SharedPreference.setInt(getApplicationContext(), Key.KEY_GAME_STATUS, status);
                                                    mMainKeyboardView.gameZoneVisible(game_YN);
                                                }
                                            }
                                        }
                                    } else {
                                        LogPrint.d("object is null 1 ");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });

                    if (mOCBPointCount >= 2) { // 적립조건 맞음 2회가 되었을 떄 다음번에 적립 1 올려야함. 원래 5회마다 1p 적립이었으나 4/30 3회바다 1p적립으로 변경
                        CustomAsyncTask task = new CustomAsyncTask(getApplicationContext());
                        task.getUserCheckPoint(new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                            @Override
                            public void onResponse(boolean result, Object obj) {
                                if (result) {
                                    try {
                                        JSONObject object = (JSONObject) obj;
                                        if (object != null) {
                                            boolean rt = object.optBoolean("Result");
                                            int totalPoint = object.optInt("total_point", -1); // 적립 성공이든 실패든 total point는 내려옴
                                            if (totalPoint >= 0) { // 넘어온 총 포인트가 0이거나 0보다 클 경우총 포인트 저장하고
                                                if (mMainKeyboardView != null) {
                                                    // 넘어온 총포인트를 저장 및 세팅한다.
                                                    SharedPreference.setInt(getApplicationContext(), Key.OCB_TOTAL_POINT, totalPoint);
                                                    mOCBSavePoint = totalPoint; // 1카운트 올려달라는 요청 하고 response
                                                    LogPrint.d("kkkssskkkkkk getUserCheckPoint setOCBPoint mOCBSavePoint :: " + mOCBSavePoint);
                                                    mMainKeyboardView.setOCBPoint(totalPoint);
                                                    LogPrint.d("set ocb point 3회당 1포인트 지급 시");
                                                }
                                            }

                                            if (!rt) {
                                                /**
                                                 String error = object.optString("errstr", "");
                                                 if ( !TextUtils.isEmpty(error) )
                                                 Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();**/
                                                int errCode = object.optInt("errcode", -1);
                                                if (errCode >= 0) {
                                                    if (errCode == 90) {
                                                        String err = object.optString("errstr");
                                                        Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            } else {
                                                mOCBPointCount = 0;
                                                SharedPreference.setInt(getApplicationContext(), Key.KEY_OCB_COUNT, mOCBPointCount);
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    } else {
                        mOCBPointCount++;
                        SharedPreference.setInt(getApplicationContext(), Key.KEY_OCB_COUNT, mOCBPointCount);
                    }
                } else {
                    CustomAsyncTask task1 = new CustomAsyncTask(getApplicationContext());
                    task1.getJointFinish("", String.valueOf(mKeyboardActiveCount),  new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                        @Override
                        public void onResponse(boolean result, Object obj) {
                            if (result) {
                                try {
                                    JSONObject object = (JSONObject) obj;
                                    if (object != null) {
                                        JSONObject spot_obj = object.optJSONObject("spot");
                                        if ( spot_obj != null ) {
                                            boolean spot_rt = spot_obj.optBoolean("Result");
                                            if (spot_rt) {
                                                String message = spot_obj.optString("message");
                                                if (mMainKeyboardView != null) {
                                                    mMainKeyboardView.showSurpriseToast(isInAppKeyboard, getApplicationContext(), message);
                                                }
                                            }
                                        }

                                        JSONObject frequency_obj = object.optJSONObject("frequency");
                                        if ( frequency_obj != null ) {
                                            LogPrint.d("frequency_obj != null obj :: " + frequency_obj.toString());
                                            boolean frequency_rt = frequency_obj.optBoolean("Result");
                                            LogPrint.d("frequency_rt :: " + frequency_rt);
                                            if ( frequency_rt ) {
                                                String ratio_str = frequency_obj.optString("ratio");
                                                String mobon_str = frequency_obj.optString("mobon");
                                                String mediation_str = frequency_obj.optString("mediation");
                                                String banner_str = frequency_obj.optString("banner");
                                                String coupang_str = frequency_obj.optString("coupang");
                                                String notice_str = frequency_obj.optString("notice");
                                                String criteo_str = frequency_obj.optString("criteo");
                                                String reward_str = frequency_obj.optString("reward");
                                                LogPrint.d("ratio_str :: " + ratio_str);
                                                LogPrint.d("mobon_str :: " + mobon_str);
                                                LogPrint.d("mediation_str :: " + mediation_str);
                                                LogPrint.d("banner_str :: " + banner_str);
                                                LogPrint.d("coupang_str :: " + coupang_str);
                                                LogPrint.d("notice_str :: " + notice_str);
                                                LogPrint.d("criteo_str :: " + criteo_str);
                                                LogPrint.d("reward_str :: " + reward_str);
                                                JSONObject new_obj = new JSONObject();
                                                new_obj.put("ratio", ratio_str);
                                                new_obj.put("mobon", mobon_str);
                                                new_obj.put("mediation", mediation_str);
                                                new_obj.put("banner", banner_str);
                                                new_obj.put("coupang", coupang_str);
                                                new_obj.put("notice", notice_str);
                                                new_obj.put("criteo", criteo_str);
                                                new_obj.put("reward", reward_str);

                                                LogPrint.d("new_obj :: " + new_obj.toString());
                                                LogPrint.d("new_obj 1 :: " + new_obj.toString());
                                                SharedPreference.setString(getApplicationContext(), Key.KEY_OCB_AD_FREQUENCY, new_obj.toString());
                                            }
                                        }

                                        JSONObject offerwall_obj = object.optJSONObject("offerwall");
                                        if ( offerwall_obj != null ) {
                                            boolean offerwall_result = offerwall_obj.optBoolean("Result");
                                            if ( offerwall_result ) {
                                                String logic = offerwall_obj.optString("logic");
                                                LogPrint.d("logic :: " + logic);
                                                boolean isHybrid = true;
                                                if ( "Native".equals(logic) ) {
                                                    isHybrid = false;
                                                }
                                                if ( mMainKeyboardView != null ) {
                                                    mMainKeyboardView.setOfferwallStatus(isHybrid);
                                                }
                                            }
                                        }

                                        JSONObject game_obj = object.optJSONObject("gamezone");
                                        if ( game_obj != null ) {
                                            boolean game_result = game_obj.optBoolean("Result");
                                            String game_YN = game_obj.optString("use_YN");
                                            if ( game_result ) {
                                                if ( mMainKeyboardView != null ) {
                                                    int status = Common.GAME_STATUS_NO;
                                                    if ( "Y".equals(game_YN) ) {
                                                        status = Common.GAME_STATUS_YES;
                                                    }
                                                    SharedPreference.setInt(getApplicationContext(), Key.KEY_GAME_STATUS, status);
                                                    mMainKeyboardView.gameZoneVisible(game_YN);
                                                }
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
            } else {
                LogPrint.d("uuid null ~~~~~");
            }

            String adPointDate = SharedPreference.getString(getApplicationContext(), Key.KEY_OCB_AD_BANNER_DATE);
            LogPrint.d("adPointDate :: " + adPointDate);
            if (TextUtils.isEmpty(adPointDate)) {

                showAdWithDate();
            } else {
                if (!adPointDate.equals(Util.GetTodayDate())) {
                    showAdWithDate();
                } else {
                    //showAd();
                    showNewAd();
                }
            }
        }

        if (mActiveHandler != null) {
            mActiveHandler.sendEmptyMessage(STOP_ACTIVE_TIMER);
        }
        /**
         SharedPreference.setInt(getApplicationContext(), Key.KEY_OCB_COUNT, mOCBPointCount);
         SharedPreference.setInt(getApplicationContext(), Key.KEY_OCB_POINT, mOCBSavePoint);
         **/


        super.onFinishInputView(finishingInput);

        if (kauto.IsKoreanMode())
            kauto.FinishAutomataWithoutInput();
        else {
            kauto.ToggleMode(); // 키보드 내려갈때 초기화
            // 2017.05.02 mNoKorean flag 삭제
//            mNoKorean = false; // 키보드 내려갈때 초기화
        }
        mKoreanKeyboard.setShifted(false); // 키보드 내려갈 때 setShifted false로 강제 세팅함
        KeyboardLogPrint.e("setShift, 키보드 내려갈 때 shift값을 false로 강제 세팅함");
        KeyboardLogPrint.e("after onFinishInputView super");
        if (mMainKeyboardView != null) {
            KeyboardLogPrint.e("after onFinishInputView super mainkeyboardview not null");
            mMainKeyboardView.selectKeyboard(MainKeyboardView.GUBUN_KEYBOARD);

            /**
             * 이모티콘 검색 관련 기능 제거 2017.10.31(키보드 내려갈 떄 이모티콘 초기화)
             */
            // 2017.05.29 키보드 내려갈 때 matched emoji 사라지도록 변경. 한경수 실장 요청
            mMainKeyboardView.initEmojiArray();
//            mMainKeyboardView.savePoint();
        } else
            KeyboardLogPrint.e("after onFinishInputView super mainkeyboardview null");
/*
쿠팡 실시간 검색 삭제
        if (mDialogHandler != null)
            mDialogHandler.sendEmptyMessage(HIDE_COUPANG_AD_DIALOG);
            */

        // 동동이 코드
//        showOverlayView();
    }

    /**
     * This is called when the user is done editing a field.  We can use
     * this to reset our state.
     */
    // 키보드 사라질때 호출되는 함수.
    @Override
    public void onFinishInput() {
        KeyboardLogPrint.w("skkim onFinishInput");
//        if (mHandler != null)
//            mHandler.removeCallbacksAndMessages(null);
        //timerCancel(2);
/*
        if (mDialogHandler != null)
            mDialogHandler.sendEmptyMessage(HIDE_COUPANG_AD_DIALOG);
*/
        KeyboardLogPrint.d("stop complete called 3");
        if (mCompleteHandler != null)
            mCompleteHandler.sendEmptyMessage(STOP_COMPLETE_TIMER);
        mCompleteCount = 0;
        super.onFinishInput();
        // Clear current composing text and candidates.
        if ( kauto != null ) {
            kauto.FinishAutomataWithoutInput();
            // 2017.05.02 mNoKorean flag 삭제
//        mNoKorean = false; // 키보드 내려갈때 초기화
            if (!kauto.IsKoreanMode())
                kauto.ToggleMode();
        }

        /*

        if (ic != null)
        	commitTyped(ic);
        */
        mComposing.setLength(0);
        // updateCandidates();

        // We only hide the candidates window when finishing input on
        // a particular editor, to avoid popping the underlying applicationq
        // up and down if the user is entering text into the bottom of
        // its window.
        setCandidatesViewShown(false);

        mCurKeyboard = mKoreanKeyboard; // 2017.02.27 기본 한글 키보드 변경
        setKeyBoardMode(true, -1, 16);
        if (mMainKeyboardView != null) {
            // let's reset Korean input mode in soft keyboard mode. H/W keyboard will not be affected with this.
            // if (kauto.IsKoreanMode())
            //	kauto.ToggleMode();
            mMainKeyboardView.closing();
        }
    }

    public static void SetImeOption(int option) {
        mOption = option;
    }

    public static int GetImeOption() {
        return mOption;
    }

    public static int GetEnShiftStatus() {
        return mShiftState;
    }

    public static int GetKoShiftStatus() {
        try {
            if ( mMainKeyboardView == null )
                LogPrint.d("mMainKeyboardView == null");
            boolean val = false;
            if ( mMainKeyboardView != null )
                val = mMainKeyboardView.isShifted();
            if (val)
                return SHIFT_STATE_UPPER_CASE;
            else
                return SHIFT_STATE_LOWER_CASE;
        } catch (Exception e) {
            e.printStackTrace();
            return SHIFT_STATE_LOWER_CASE;
        }
    }

    private boolean isColorLight(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness < 0.5;
    }

    // 키보드가 나타날 때 현재 키보드를 어떤것으로 할지 결정하는 함수
    @Override
    public void onStartInputView(EditorInfo attribute, boolean restarting) {
        super.onStartInputView(attribute, restarting);
        LogPrint.d("skkim onStartInputView");
        recoverBackupKeyboard();
        // 동동이 코드
//        isTargetAdShow = false;
        isKeyboardShow = true;
        int keyboardHeight = 0;
        if ( mMainKeyboardView != null )
            keyboardHeight = mMainKeyboardView.getKeyboardHeight();
        Intent keyboardStatusIntent = new Intent("KEYBOARD_STATUS");
        keyboardStatusIntent.putExtra("isShow", isKeyboardShow);
        keyboardStatusIntent.putExtra("keyboardHeight", keyboardHeight);
        sendBroadcast(keyboardStatusIntent);

        /*
        동동이 코드
        LogPrint.d("overlay attribute.packageName :: " + attribute.packageName);
        if ( adPackageNameArray == null )
            adPackageNameArray = new ArrayList<>();
        else {
            LogPrint.d("overlay adPackageNameArray not null ");
            if ( adPackageNameArray != null && adPackageNameArray.size() > 0 && !TextUtils.isEmpty(attribute.packageName) ) {
                LogPrint.d("overlay adPackageNameArray.size :: " + adPackageNameArray.size());
                for ( int i = 0 ; i < adPackageNameArray.size() ; i ++ ) {
                    if ( attribute.packageName.equals(adPackageNameArray.get(i)) ) {
                        LogPrint.d("overlay attribute.packageName     :: " + attribute.packageName);
                        LogPrint.d("overlay adPackageNameArray.get(i) :: " + adPackageNameArray.get(i));
                        LogPrint.d(" *************************** ");
                        if ( Settings.canDrawOverlays(getApplicationContext()) ) {
                            LogPrint.d("overlay Settings.canDrawOverlays true");
                            isTargetAdShow = true;
                        } else {
                            LogPrint.d("overlay Settings.canDrawOverlays false");
                        }
                    }
                }
            }
        }
*/
        if (attribute.packageName.equals(Common.TARGET_PACKAGENAME))
            isInAppKeyboard = true;
        else
            isInAppKeyboard = false;
        // 적립조건에 사용되는 변수 초기화
        isCharactorEntered = false;
        //isCorrectEnter = false;
        //lastEnteredPrimaryCode = -1;

        if (mMainKeyboardView != null) {
            mMainKeyboardView.setEKeyboardHeightResize();
/**
 mKeyboardActiveCount = 0;
 if(mKeyboardActiveTimer != null)
 mKeyboardActiveTimer.cancel();
 mKeyboardActiveTimer = new Timer();
 mKeyboardActiveTimer.schedule(new TimerTask() {
@Override public void run() {
mKeyboardActiveCount++;
}
}, 0, 1000);**/

            if (Build.VERSION.SDK_INT >= 21) { // setNavigationBarColor는 21 이후부터 지원
                try {
                    String botColor = mMainKeyboardView.getBotColor();
                    if (botColor != null) {
                        this.getWindow().getWindow().setNavigationBarColor(Color.parseColor(botColor));
                        View decorView = this.getWindow().getWindow().getDecorView();
                        int flags = decorView.getSystemUiVisibility();
                        if (isColorLight(Color.parseColor(botColor))) {
                            flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
                        } else {
                            flags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
                        }
                        decorView.setSystemUiVisibility(flags);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //mMainKeyboardView.setBrandIcon(false);

            String uuid = SharedPreference.getString(getApplicationContext(), Key.KEY_OCB_USER_ID);
            try {
                E_Cipher cp = E_Cipher.getInstance();
                uuid = cp.Decode(getApplicationContext(), uuid);
            } catch (Exception e) {
                e.printStackTrace();
            }


/**
 mOCBSavePoint = SharedPreference.getZeroInt(getApplicationContext(), Key.KEY_OCB_POINT);

 mMainKeyboardView.setOCBPoint(mOCBSavePoint);
 if ( mOCBSavePoint > 0 )
 mMainKeyboardView.setPointVisibie(View.VISIBLE);
 else
 mMainKeyboardView.setPointVisibie(View.GONE);
 **/

            mMainKeyboardView.setIsInAppKeyboard(isInAppKeyboard);

            String dailyPointDate = SharedPreference.getString(getApplicationContext(), Key.KEY_OCB_DAILY_POINT_DATE);
            if (TextUtils.isEmpty(dailyPointDate)) {
                if (TextUtils.isEmpty(uuid)) {
                    setNoUUIDUserInfo();
                } else {
                    CustomAsyncTask task = new CustomAsyncTask(getApplicationContext());
                    task.setOnePoint(new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                        @Override
                        public void onResponse(boolean result, Object obj) {
                            int tPoint = -1;
                            LogPrint.d("kkkssskkkkkk onStart 1-1 tPoint                                           :: " + tPoint);
                            if (result) {
                                if (obj != null) {
                                    try {
                                        JSONObject object = (JSONObject) obj;
                                        if (object != null) {
                                            LogPrint.d("kkkssskkkkkk onStart object :: " + object.toString());
                                            boolean rt = object.optBoolean("Result");
                                            if (rt) {
                                                int totalPoint = object.optInt("total_point");
                                                tPoint = totalPoint;
                                                LogPrint.d("kkkssskkkkkk onStart 1-2 tPoint :: " + tPoint);
                                            } else {
                                                int totalPoint = object.optInt("total_point", -1);
                                                LogPrint.d("kkkssskkkkkk onStart 1-3 tPoint :: " + tPoint);
                                                tPoint = totalPoint;
                                                int errCode = object.optInt("errcode", -1);
                                                if (errCode >= 0) {
                                                    if (errCode == 90) {
                                                        String err = object.optString("errstr");
                                                        Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }

                                        } else {

                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        tPoint = -1;
                                    }
                                } else {

                                }
                            }
                            if (tPoint >= 0) {
                                SharedPreference.setInt(getApplicationContext(), Key.OCB_TOTAL_POINT, tPoint);
                                if (tPoint > 0) {
                                    mMainKeyboardView.setPointVisibie(View.VISIBLE);
                                    SharedPreference.setString(getApplicationContext(), Key.KEY_OCB_DAILY_POINT_DATE, Util.GetTodayDate());
                                } else {
                                    mMainKeyboardView.setPointVisibie(View.GONE);
                                }

                                mOCBSavePoint = tPoint;
                                LogPrint.d("kkkssskkkkkk onStart 1 mOCBSavePoint :: " + mOCBSavePoint);
                                mMainKeyboardView.setOCBPoint(tPoint);
                                LogPrint.d("set ocb point set one point");
                            }
                            getTotalPoint();
                        }
                    });
                }
            } else {
                if (!dailyPointDate.equals(Util.GetTodayDate())) {
                    if (TextUtils.isEmpty(uuid)) {
                        setNoUUIDUserInfo();
                    } else {
                        CustomAsyncTask task = new CustomAsyncTask(getApplicationContext());
                        task.setOnePoint(new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                            @Override
                            public void onResponse(boolean result, Object obj) {
                                int tPoint = -1;
                                if (result) {
                                    if (obj != null) {
                                        try {
                                            JSONObject object = (JSONObject) obj;
                                            if (object != null) {
                                                boolean rt = object.optBoolean("Result");
                                                if (rt) {
                                                    int totalPoint = object.optInt("total_point");
                                                    tPoint = totalPoint;
                                                } else {
                                                    int totalPoint = object.optInt("total_point", -1);
                                                    tPoint = totalPoint;
                                                    int errCode = object.optInt("errcode", -1);
                                                    if (errCode >= 0) {
                                                        if (errCode == 90) {
                                                            String err = object.optString("errstr");
                                                            Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }

                                            } else {

                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            tPoint = -1;
                                        }
                                    } else {

                                    }
                                }
                                if (tPoint >= 0) {
                                    SharedPreference.setInt(getApplicationContext(), Key.OCB_TOTAL_POINT, tPoint);
                                    if (tPoint > 0) {
                                        mMainKeyboardView.setPointVisibie(View.VISIBLE);
                                        SharedPreference.setString(getApplicationContext(), Key.KEY_OCB_DAILY_POINT_DATE, Util.GetTodayDate());
                                    } else {
                                        mMainKeyboardView.setPointVisibie(View.GONE);
                                    }

                                    mOCBSavePoint = tPoint;
                                    mMainKeyboardView.setOCBPoint(tPoint);
                                    LogPrint.d("kkkssskkkkkk onStart 1-4 mOCBSavePoint :: " + mOCBSavePoint);
                                    LogPrint.d("set ocb point set one point1");
                                }
                                getTotalPoint();
                            }
                        });
                    }
                } else {
                    getTotalPoint();
                }
            }

            String savedToday = SharedPreference.getString(getApplicationContext(), Key.KEY_OCB_OLABANG_DATE);
            if (TextUtils.isEmpty(savedToday)) {
                SharedPreference.setBoolean(getApplicationContext(), Common.PREF_OFFERWALL_FIRST, false);
                SharedPreference.setBoolean(getApplicationContext(), Common.PREF_OFFERWALL_SECOND, false);
                saveOlabangDataAndShow();
            } else {
                if (!savedToday.equals(Util.GetTodayDate())) {
                    SharedPreference.setBoolean(getApplicationContext(), Common.PREF_OFFERWALL_FIRST, false);
                    SharedPreference.setBoolean(getApplicationContext(), Common.PREF_OFFERWALL_SECOND, false);
                    saveOlabangDataAndShow();
                } else {
                    if (mMainKeyboardView != null) {
                        mMainKeyboardView.getOlabangItem();
                    }
                }
            }

            if ( mMainKeyboardView != null )
                mMainKeyboardView.keyboardShow();

            String savedADToday = SharedPreference.getString(getApplicationContext(), Key.KEY_OCB_AD_FREQUENCY_DATE);
            LogPrint.d("savedADToday :: " + savedADToday);

            if (TextUtils.isEmpty(savedADToday)) {
                getAdInfo();
            } else {
                if (!savedADToday.equals(Util.GetTodayDate())) {
                    getAdInfo();
                }
            }
/*
// 2022.06.27 NEWS NOTI 적용 안함.
            String savedNotificationDate = SharedPreference.getString(getApplicationContext(), Key.KEY_OCB_NOTIFICATION_DATE);
            if (TextUtils.isEmpty(savedNotificationDate)) {
                LogPrint.d("kksskk savedNotificationDate empty");
                getNotificationInfo();
            } else {
                if (!savedNotificationDate.equals(Util.GetTodayDate())) {
                    LogPrint.d("kksskk savedNotificationDate not today");
                    getNotificationInfo();
                }
            }
*/
/**
 String savePointToday = SharedPreference.getString(getApplicationContext(), Key.KEY_OCB_POINT_DATE);
 if ( TextUtils.isEmpty(savePointToday) ) {

 mOCBTodaySavePoint = 0;
 SharedPreference.setInt(getApplication(), Key.KEY_OCB_TODAY_POINT, 0);
 SharedPreference.setString(getApplicationContext(), Key.KEY_OCB_POINT_DATE, Util.GetTodayDate());
 } else {
 if ( !savePointToday.equals(Util.GetTodayDate()) ) {

 mOCBTodaySavePoint = 0;
 SharedPreference.setInt(getApplication(), Key.KEY_OCB_TODAY_POINT, 0);
 SharedPreference.setString(getApplicationContext(), Key.KEY_OCB_POINT_DATE, Util.GetTodayDate());
 } else {

 }
 }**/
            // 뉴스 광고 적용 후 키보드 처음 올라올 경우 뉴스 광고 받아놓은게 없어 빈 영역이 노출됨.(BECAUSE. 뉴스 광고는 키보드 내려갈 때 미리 받아놓는데 업데이트 후 올라올 경우 미리 받아놓은 광고가 없으므로)
            if (!mMainKeyboardView.isNewsExist()) {
                mMainKeyboardView.setNews("Y", new MainKeyboardView.NewsCallbackListener() {
                    @Override
                    public void onNewsReceived() {

                    }
                });
            }
        } else {

        }

        Common.GetGameStatus(getApplicationContext(), new Listener.OnGameStatusListener() {
            @Override
            public void received(String status) {
                if ( mMainKeyboardView != null ) {
                    mMainKeyboardView.gameZoneVisible(status);
                }
            }
        });

        mKeyboardActiveCount = 0;

        if (mActiveHandler != null) {
            mActiveHandler.sendEmptyMessage(START_ACTIVE_TIMER);
        }
/**
 if ( mTimerHandler != null ) {
 mTimerHandler.post(new Runnable() {
@Override public void run() {
if(mKeyboardActiveTimer != null)
mKeyboardActiveTimer.cancel();
mKeyboardActiveTimer = new Timer();
mKeyboardActiveTimer.schedule(new TimerTask() {
@Override public void run() {
mKeyboardActiveCount++;
}
}, 0, 1000);
}
});
 }**/


        initShiftState(); // 키보드가 소문자, 대문자, 대문자 등으로 세팅되어 있는 상태에서 키패드가 내려가지 않고 onStartInputView , onFinishInputView 등이 호출될 때 state는 초기화 됨
        KeyboardLogPrint.e("onStartInputView packageName :: " + attribute.packageName);
        KeyboardLogPrint.e("onStartInputView attribute.imeOptions");
        SetImeOption(attribute.imeOptions);
        /**
         * 특정 app의 package명과  hint 값과 일치하면 실행 안함
         */
        String packageName = attribute.packageName;
        if (TextUtils.isEmpty(packageName))
            packageName = "";
        KeyboardLogPrint.e("onStartInputView packageName :: " + packageName);
        try {
            KeyboardLogPrint.e("mQuickRun :: " + mQuickRun);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mMainKeyboardView != null) {
            mMainKeyboardView.setPackageName(packageName);
        }
        KeyboardLogPrint.d("stop complete called 4");
        //timerCancel(3);
        if (mCompleteHandler != null)
            mCompleteHandler.sendEmptyMessage(STOP_COMPLETE_TIMER);
        mCompleteCount = 0;
        if (!mSoundLoaded)
            initSound();

        if (mAudioManager == null)
            mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mIsPreviewSet = SharedPreference.getBoolean(getApplicationContext(), Common.PREF_PREVIEW_SETTING);
        mVolumeLevel = SharedPreference.getInt(getApplicationContext(), Common.PREF_I_VOLUME_LEVEL);
        if (mVolumeLevel < 0) {
            SharedPreference.setInt(getApplicationContext(), Common.PREF_I_VOLUME_LEVEL, Common.DEFAULT_SOUND_LEVEL);
            mVolumeLevel = Common.DEFAULT_SOUND_LEVEL;
        }

        mMediaVolume = Common.getStreamLevel(getApplicationContext());
        mVol = Common.getVolume("SoftKeyboard", mMediaVolume, mVolumeLevel);

        mVibrateLevel = SharedPreference.getLong(getApplicationContext(), Common.PREF_VIBRATE_LEVEL);

        if (mVibrateLevel < 0) {
            SharedPreference.setLong(getApplicationContext(), Common.PREF_VIBRATE_LEVEL, Common.DEFAULT_VIBRATE_LEVEL);
            mVibrateLevel = SharedPreference.getLong(getApplicationContext(), Common.PREF_VIBRATE_LEVEL);
        }

        mAudioMode = mAudioManager.getRingerMode();
        KeyboardLogPrint.w("onStartInputView mAudioMode :: " + mAudioMode);
        mKoreanKeyboardMode = SharedPreference.getInt(getApplicationContext(), Common.PREF_KEYBOARD_MODE);
        if ( IsBackupKeyboardExist() )
            mKoreanKeyboardMode = 2;
        if (mKoreanKeyboardMode < 0) mKoreanKeyboardMode = 0;
        KeyboardLogPrint.e("SoftKeyboard 5 kind :: " + mKoreanKeyboardMode);
        WindowManager windowService = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int currentRatation = windowService.getDefaultDisplay().getRotation();
        if (mKoreanKeyboardMode == Common.MODE_CHUNJIIN) {
            if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                kauto = qAutomata;
                LogPrint.d("kauto qAutomata 1");
                mKoreanKeyboard = mQKoreanKeyboard;
                if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM )
                    mKoreanKeyboard = mQKoreanKeyboard35;
            } else {
                kauto = cAutomata;
                LogPrint.d("kauto cAutomata 2");
                mKoreanKeyboard = mSejongKeyboard;
            }
        } else if (mKoreanKeyboardMode == Common.MODE_QUERTY) {
            kauto = qAutomata;
            LogPrint.d("kauto qAutomata 3");
            mKoreanKeyboard = mQKoreanKeyboard;
            mKoreanKeyboard = mQKoreanKeyboard35;

        } else if (mKoreanKeyboardMode == Common.MODE_DAN) {
            LogPrint.d("kauto dAutomata 4");
            kauto = dAutomata;
            mKoreanKeyboard = mDanKeyboard;
            if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM )
                    mKoreanKeyboard = mDanKeyboard35;
            }
        } else if (mKoreanKeyboardMode == Common.MODE_NARA) {
            if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                kauto = qAutomata;
                LogPrint.d("kauto qAutomata 5");
                mKoreanKeyboard = mQKoreanKeyboard;
                if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM )
                    mKoreanKeyboard = mQKoreanKeyboard35;
            } else {
                LogPrint.d("kauto nAutomata 6");
                kauto = nAutomata;
                mKoreanKeyboard = mNaraKeyboard;
            }
        } else if (mKoreanKeyboardMode == Common.MODE_CHUNJIIN_PLUS) {
            if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                LogPrint.d("kauto qAutomata 7");
                kauto = qAutomata;
                mKoreanKeyboard = mQKoreanKeyboard;
                if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM )
                    mKoreanKeyboard = mQKoreanKeyboard35;
            } else {
                LogPrint.d("kauto cpAutomata 8");
                kauto = cpAutomata;
                mKoreanKeyboard = mSejongPlusKeyboard;
            }
        }
        // 키보드 올라올 때 automata 초기화 필요. 2025.08.06
        kauto.setInitState();
        kauto.FinishAutomataWithoutInput();

        mAttribute = attribute;
        int types = (attribute.inputType & EditorInfo.TYPE_MASK_CLASS);
        KeyboardLogPrint.e("types :: " + types);
        switch (types) {
            case EditorInfo.TYPE_CLASS_NUMBER:
            case EditorInfo.TYPE_CLASS_PHONE:
            case EditorInfo.TYPE_CLASS_DATETIME:

                if (attribute.inputType == 2 || attribute.inputType == 18 || attribute.inputType == 4 || attribute.inputType == 20) {
                    mCurKeyboard = mNumberOnlyKeyboard;
                    setKeyBoardMode(false, NUM_KEYBOARD, 32);
                } else if (attribute.inputType == 4098) {
                    mCurKeyboard = mNumberSignedKeyboard;
                    setKeyBoardMode(false, NUM_KEYBOARD, 33);
                } else if (attribute.inputType == 8194) {
                    mCurKeyboard = mNumberDecimalKeyboard;
                    setKeyBoardMode(false, NUM_KEYBOARD, 34);
                } else if (attribute.inputType == 3 || attribute.inputType == 36) {
                    mCurKeyboard = mNumberPhoneKeyboard;
                    setKeyBoardMode(false, NUM_KEYBOARD, 35);
                } else {
                    mCurKeyboard = mNumberKeyboard;
                    setKeyBoardMode(false, NUM_KEYBOARD, 36);
                }
                break;
            default:
                int attr = attribute.inputType;
                KeyboardLogPrint.w("attr :: " + attr);
                if (attr == 17 || attr == 33 || attr == 129 || attr == 145 || attr == 209 || attr == 225 || attr == 65553) { // 65553 특정 삼성 브라우저에서 url 창 누를 경우 넘어오는 attr 값
                    if (kauto != null) {
                        if (kauto.IsKoreanMode())
                            kauto.ToggleMode();
                        kauto.FinishAutomataWithoutInput();
                    }
                    boolean isQwertyNumSet = SharedPreference.getBoolean(getApplicationContext(), Common.PREF_QWERTY_NUM_SETTING);
                    if (mKoreanKeyboardMode == Common.MODE_QUERTY && isQwertyNumSet) {
                        mCurKeyboard = mQwertyNum;
                        if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                            if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM )
                                mCurKeyboard = mQwertyNum35;
                        }
                    } else
                        mCurKeyboard = mEQwerty;
                    setKeyBoardMode(false, -1, 37);
                } else {
                    if (kauto.IsKoreanMode()) {
                        mCurKeyboard = mKoreanKeyboard;
                        setKeyBoardMode(true, -1, 38);
                        if (mKoreanKeyboard == mQKoreanKeyboard || mKoreanKeyboard == mQKoreanKeyboard35)
                            ((LatinKeyboardView) mMainKeyboardView.getKeyboardView()).setKeyboardMode(Common.MODE_QUERTY);
                    } else {
                        boolean isQwertyNumSet = SharedPreference.getBoolean(getApplicationContext(), Common.PREF_QWERTY_NUM_SETTING);
                        if (mKoreanKeyboardMode == Common.MODE_QUERTY && isQwertyNumSet) {
                            mCurKeyboard = mQwertyNum;
                            if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                                if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM )
                                    mCurKeyboard = mQwertyNum35;
                            }
                        } else
                            mCurKeyboard = mEQwerty;
                        setKeyBoardMode(false, -1, 39);
                    }
                }
                break;
        }

        mMainKeyboardView.closing();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                KeyboardLogPrint.e("onStartInputView invalidateAllKeys");
                mMainKeyboardView.getKeyboardView().invalidateAllKeys();
            }
        }, 200);

//        int val = mAttribute.imeOptions & (EditorInfo.IME_MASK_ACTION | EditorInfo.IME_FLAG_NO_ENTER_ACTION);
//        boolean isExceptPackage = isExceptPackage();
//        if(advo != null && val == EditorInfo.IME_ACTION_SEARCH && !isExceptPackage) {
//            if (coupangdialog != null && coupangdialog.isShowing() ) {
//                mDialogHandler.sendEmptyMessage(NOTI_COUPANG_AD_DIALOG);
//            } else {
//                mDialogHandler.sendEmptyMessage(SHOW_COUPANG_AD_DIALOG);
//            }
//        }

    }

    /**
     * Deal with the editor reporting movement of its cursor.
     * oldSelStart :: edittext의 이전 text cursor position 인듯
     * newSelStart :: edittext의 현재 text cursor position 인듯
     */
    //새로운 택스트 부분이 보고 되면 호출된다.
    @Override
    public void onUpdateSelection(int oldSelStart, int oldSelEnd,
                                  int newSelStart, int newSelEnd,
                                  int candidatesStart, int candidatesEnd) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd,
                candidatesStart, candidatesEnd);
        KeyboardLogPrint.w("skkim onUpdateSelection oldSelStart :: " + oldSelStart + " , oldSelEnd :: " + oldSelEnd
                + " , newSelStart :: " + newSelStart + " , newSelEnd :: " + newSelEnd
                + " , candidatesStart :: " + candidatesStart + " , candidatesEnd :: " + candidatesEnd);
        // If the current selection in the text view changes, we should
        // clear whatever candidate text we have.
        if (mBackupCurKeyboard != null && mBackupKauto != null) {
            if (mComposing.length() > 0 && (newSelStart != candidatesEnd || newSelEnd != candidatesEnd)) {
                mComposing.setLength(0);
                kauto.FinishAutomataWithoutInput();
                // updateCandidates();
                InputConnection ic = getCurrentInputConnection();
                ic = inputConnection;
                if (ic != null) {
                    ic.finishComposingText();
                    mIsComplete = true;
                    mIsCompleteFromHandler = false;
                }
            }
        } else {
            if (mMainKeyboardView != null && mMainKeyboardView.getKeyboard() != mSejongKeyboard && mMainKeyboardView.getKeyboard() != mSejongPlusKeyboard) {
                if (mComposing.length() > 0 && (newSelStart != candidatesEnd || newSelEnd != candidatesEnd)) {
                    mComposing.setLength(0);
                    kauto.FinishAutomataWithoutInput();
                    // updateCandidates();
                    InputConnection ic = getCurrentInputConnection();
                    ic = inputConnection;
                    if (ic != null) {
                        ic.finishComposingText();
                        mIsComplete = true;
                        mIsCompleteFromHandler = false;
                    }
                }
            }
        }


        /**
         * ocb 메모기능 삭제로 제외
         if (inputConnection != null && isInputViewShown()) {
         ExtractedText et = inputConnection.getExtractedText(new ExtractedTextRequest(), 0);
         if (et != null) {
         CharSequence seq = et.text;
         String temp = seq.toString();

         int etLength = temp.length();

         if (etLength > 0) {
         if (mMainKeyboardView != null) {
         mMainKeyboardView.memoVisibleSetting(true);
         }
         } else {
         if (mMainKeyboardView != null) {
         mMainKeyboardView.memoVisibleSetting(false);
         }
         }
         }
         }**/

//        if ( mComposing.length() > 0 )
//        {
//            ExtractedText et;
//            if ( inputConnection != null )
//                et = inputConnection.getExtractedText(new ExtractedTextRequest(), 0);
//            else
//                et = getCurrentInputConnection().getExtractedText(new ExtractedTextRequest(), 0);
//
//            if ( et != null )
//            {
//                CharSequence seq = et.text;
//                if ( seq != null )
//                {
//                    LogPrint.w("onupdateSelection seq.length() :: " + seq.length());
//                    if ( seq.length() > newSelEnd )
//                    {
//                        if (mHandler != null)
//                            mHandler.removeCallbacksAndMessages(null);
//
//                        if (inputConnection != null) {
//                            inputConnection.finishComposingText();
//                            kauto.setInitState();
//                            mComposing.setLength(0);
//                            kauto.FinishAutomataWithoutInput();
//                        }
//
//                        mIsCompleteFromHandler = false;
//                    }
//                }
//            }
//        }
    }

    /**
     * This tells us about completions that the editor has determined based
     * on the current text in it.  We want to use this in fullscreen mode
     * to show the completions ourself, since the editor can not be seen
     * in that situation.
     */
    /* do nothing
    @Override public void onDisplayCompletions(CompletionInfo[] completions) {
        if (mCompletionOn) {
            mCompletions = completions;
            if (completions == null) {
                setSuggestions(null, false, false);
                return;
            }

            List<String> stringList = new ArrayList<String>();
            for (int i=0; i<(completions != null ? completions.length : 0); i++) {
                CompletionInfo ci = completions[i];
                if (ci != null) stringList.add(ci.getText().toString());
            }
            setSuggestions(stringList, true, true);
        }
    }
    */

    /**
     * This translates incoming hard key events in to edit operations on an
     * InputConnection.  It is only needed when using the
     * PROCESS_HARD_KEYS option.
     */
    private boolean translateKeyDown(int keyCode, KeyEvent event) {
        KeyboardLogPrint.d("skkim translateKeyDown keyCode :: " + keyCode + ", event :: " + event.getAction());
        /**
         if ( mMainKeyboardView != null && mMainKeyboardView.getKeyboard() == mSejongKeyboard && isKoreanKeyboaard() ) {
         if ( keyCode == 46 ) {
         onPress(114);
         onKey(114, null);
         } else if ( keyCode == 39 ) {
         onPress(108);
         onKey(108, null);
         onPress(122);
         onKey(122, null);
         }
         return true;
         }**/
        mMetaState = MetaKeyKeyListener.handleKeyDown(mMetaState, keyCode, event);
        int c = event.getUnicodeChar(MetaKeyKeyListener.getMetaState(mMetaState));
        mMetaState = MetaKeyKeyListener.adjustMetaAfterKeypress(mMetaState);
        InputConnection ic = getCurrentInputConnection();
        ic = inputConnection;
        if (c == 0 || ic == null)
            return false;
        KeyboardLogPrint.d("translateKeyDown c :: " + c);
        if ((c & KeyCharacterMap.COMBINING_ACCENT) != 0)
            c = c & KeyCharacterMap.COMBINING_ACCENT_MASK;
        KeyboardLogPrint.d("translateKeyDown c1 :: " + c + " , mComposing length :: " + mComposing.length());
        if (mComposing.length() > 0) {
            char accent = mComposing.charAt(mComposing.length() - 1);
            int composed = KeyEvent.getDeadChar(accent, c);
            KeyboardLogPrint.d("translateKeyDown accent :: " + accent + " , composed :: " + composed);
            /*
            if (composed != 0) {
                c = composed;
                KeyboardLogPrint.d("translateKeyDown c2 :: " + c);
                mComposing.setLength(mComposing.length() - 1);
            }
            */
        }

        if (c == Keyboard.KEYCODE_DELETE)
            return false;
        KeyboardLogPrint.d("translateKeyDown c3 :: " + c);

        onKey(c, null);

        return true;
    }

    /**
     * Use this to monitor key events being delivered to the application.
     * We get first crack at them, and can either resume them or let them
     * continue to the app.
     */
    @SuppressWarnings({})
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        KeyboardLogPrint.d("skkim onKeyDown keyCode :: " + keyCode);
        KeyboardLogPrint.d("skkim event shift pressed :: " + event.isShiftPressed());
        KeyboardLogPrint.d("skkim event cap on :: " + event.isCapsLockOn());

        if ( mMainKeyboardView == null ) {
            LogPrint.d("skkim main keyboard view null");
            int keyboardMode = SharedPreference.getInt(this, Common.PREF_KEYBOARD_MODE);
            if ( IsBackupKeyboardExist() )
                keyboardMode = 2;
            LatinKeyboard currKeyboard = getSelectKeyboard(keyboardMode);

            int height = 732;
            if (mSejongKeyboard != null )
                height = mSejongKeyboard.getHeight();
            LogPrint.d("height :: " + height);

            // currKeyboard, mEmojiKeyboard, mEmoticonKeyboard null 일 경우 새로 setting
            if ( currKeyboard == null )
                currKeyboard = getNullKeyboard();

            if ( mEmojiKeyboard == null )
                mEmojiKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_emoji);

            if ( mEmoticonKeyboard == null )
                mEmoticonKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_emoticon);

            mMainKeyboardView = new MainKeyboardView(this,  getApplication(), currKeyboard, mEmojiKeyboard, mEmoticonKeyboard, height,
                    new MainKeyboardView.OnClickCallbackListener() {
                        @Override
                        public void onReceive(String matchStr, String emoji) {
//                if (mHandler != null)
//                    mHandler.removeCallbacksAndMessages(null);
                            isCharactorEntered = true;
                            //timerCancel(0);
                            KeyboardLogPrint.d("stop complete called 5");
                            if (mCompleteHandler != null)
                                mCompleteHandler.sendEmptyMessage(STOP_COMPLETE_TIMER);
                            mCompleteCount = 0;
                            mComposing.append(emoji);
                            //inputConnection = getCurrentInputConnection();
                            if (inputConnection != null) {
//                    inputConnection.setComposingText(mComposing, 1);
                                KeyboardLogPrint.d("skkim null commitText 29 :: " + mComposing.toString());
                                inputConnection.commitText(mComposing, 1);
                                inputConnection.finishComposingText();
                                if (kauto != null) {
                                    kauto.setInitState();
                                    kauto.FinishAutomataWithoutInput();
                                }
                                mComposing.setLength(0);
                                mIsComplete = true;
                                mIsCompleteFromHandler = false;
                            }
                        }
                    },
                    new MainKeyboardView.OnEClickCallbackListener() {
                        @Override
                        public void onReceive(int primaryKey) {
                            if (primaryKey == -2) {
                                if (mMainKeyboardView != null) {
                                    Keyboard curKeyboard = mMainKeyboardView.getKeyboard();
                                    boolean isQwertySymbol = false;
                                    if (curKeyboard == mEQwerty || curKeyboard == mQwertyNum || curKeyboard == mQwertyNum35) {
                                        isQwertySymbol = true;
                                    } else if (curKeyboard == mKoreanKeyboard) {
                                        if (mKoreanKeyboardMode == Common.MODE_QUERTY || mKoreanKeyboardMode == Common.MODE_DAN)
                                            isQwertySymbol = true;
                                    }
                                    if (isQwertySymbol) {
                                        mMainKeyboardView.setKeyboard(mQSymbolsKeyboard); // 기호 클릭 시 쿼티 심볼로 설정
                                        WindowManager windowService = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                                        int currentRatation = windowService.getDefaultDisplay().getRotation();
                                        if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                                            if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM )
                                                mMainKeyboardView.setKeyboard(mQSymbolsKeyboard_35); // 기호 클릭 시 쿼티 심볼로 설정
                                        }
                                        mMainKeyboardView.selectKeyboard(MainKeyboardView.GUBUN_KEYBOARD);
                                    } else {
                                        mMainKeyboardView.setKeyboard(mSymbolsKeyboard); // 기호 클릭 시 논 쿼티 심볼로 설정
                                        mMainKeyboardView.selectKeyboard(MainKeyboardView.GUBUN_KEYBOARD);
                                    }
                                }
                            } else if (primaryKey == -6) {
                                if (mMainKeyboardView != null) {
                                    Keyboard curKeyboard = mMainKeyboardView.getKeyboard();
                                    if (curKeyboard == mEQwerty) {
                                        mCurKeyboard = mEQwerty;
                                        mMainKeyboardView.setKeyboard(mCurKeyboard); // 한영버튼 클릭 시 쿼티의 영문(not num)으로 설정
//                                    mMainKeyboardView.setKeyboard(mQwertyKeyboard);
                                        setKeyBoardMode(false, -1, 11);
                                    } else if (curKeyboard == mQwertyNum) {
                                        mCurKeyboard = mQwertyNum;
                                        WindowManager windowService = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                                        int currentRatation = windowService.getDefaultDisplay().getRotation();
                                        if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                                            if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM )
                                                mCurKeyboard = mQwertyNum35;
                                        }
                                        mMainKeyboardView.setKeyboard(mCurKeyboard); // 한영버튼 클릭 시 쿼티의 영문(num)으로 설정
//                                    mMainKeyboardView.setKeyboard(mQwertyKeyboard);
                                        setKeyBoardMode(false, -1, 21);
                                    } else if (curKeyboard == mKoreanKeyboard) {
                                        WindowManager windowService = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                                        int currentRatation = windowService.getDefaultDisplay().getRotation();
                                        if (mKoreanKeyboardMode == Common.MODE_CHUNJIIN) {
                                            if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                                                mKoreanKeyboard = mQKoreanKeyboard;
                                                if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM )
                                                    mKoreanKeyboard = mQKoreanKeyboard35;
                                            } else {
                                                mKoreanKeyboard = mSejongKeyboard;
                                            }
                                        } else if (mKoreanKeyboardMode == Common.MODE_QUERTY) {
                                            mKoreanKeyboard = mQKoreanKeyboard;
                                            if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                                                if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM ) {
                                                    mEQwerty = mQwerty35;
                                                    mKoreanKeyboard = mQKoreanKeyboard35;
                                                } else
                                                    mEQwerty = mQwerty;
                                            } else
                                                mEQwerty = mQwerty;

                                        } else if (mKoreanKeyboardMode == Common.MODE_DAN) {
                                            mKoreanKeyboard = mDanKeyboard;
                                            if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                                                if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM )
                                                    mKoreanKeyboard = mDanKeyboard35;
                                            }
                                        } else if (mKoreanKeyboardMode == Common.MODE_NARA) {
                                            if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                                                mKoreanKeyboard = mQKoreanKeyboard;
                                                if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM )
                                                    mKoreanKeyboard = mQKoreanKeyboard35;
                                            } else {
                                                mKoreanKeyboard = mNaraKeyboard;
                                            }
                                        } else if (mKoreanKeyboardMode == Common.MODE_CHUNJIIN_PLUS) {
                                            if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                                                mKoreanKeyboard = mQKoreanKeyboard;
                                                if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM )
                                                    mKoreanKeyboard = mQKoreanKeyboard35;
                                            } else {
                                                mKoreanKeyboard = mSejongPlusKeyboard;
                                            }
                                        }


                                        mCurKeyboard = mKoreanKeyboard;
                                        mMainKeyboardView.setKeyboard(mCurKeyboard); // 한영버튼 클릭 시 천지인, 천지인+,단모음, 나랏글 등의 한글 키보드로 설정
//                                    mMainKeyboardView.setKeyboard(mKoreanKeyboard);
                                        setKeyBoardMode(true, -1, 12);

                                        if (mKoreanKeyboard == mQKoreanKeyboard || mKoreanKeyboard == mQKoreanKeyboard35)
                                            ((LatinKeyboardView) mMainKeyboardView.getKeyboardView()).setKeyboardMode(Common.MODE_QUERTY);
                                    } else {
                                        // 2017.05.02 mNoKorean flag 삭제
                                        if (!kauto.IsKoreanMode()) {
//                                    if ((mNoKorean || !kauto.IsKoreanMode())) {
                                            boolean isQwertyNumSet = SharedPreference.getBoolean(getApplicationContext(), Common.PREF_QWERTY_NUM_SETTING);
                                            if (mKoreanKeyboardMode == Common.MODE_QUERTY && isQwertyNumSet) {
                                                mCurKeyboard = mQwertyNum;
                                                WindowManager windowService = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                                                int currentRatation = windowService.getDefaultDisplay().getRotation();
                                                if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                                                    if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM )
                                                        mCurKeyboard = mQwertyNum35;
                                                }
                                            } else
                                                mCurKeyboard = mEQwerty;
                                            setKeyBoardMode(false, -1, 13);
                                        } else {
                                            WindowManager windowService = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                                            int currentRatation = windowService.getDefaultDisplay().getRotation();
                                            if (mKoreanKeyboardMode == Common.MODE_CHUNJIIN) {
                                                if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                                                    mKoreanKeyboard = mQKoreanKeyboard;
                                                    if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM )
                                                        mKoreanKeyboard = mQKoreanKeyboard35;
                                                } else {
                                                    mKoreanKeyboard = mSejongKeyboard;
                                                }
                                            } else if (mKoreanKeyboardMode == Common.MODE_QUERTY) {
                                                mKoreanKeyboard = mQKoreanKeyboard;
                                                if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                                                    if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM ) {
                                                        mEQwerty = mQwerty35;
                                                        mKoreanKeyboard = mQKoreanKeyboard35;
                                                    } else
                                                        mEQwerty = mQwerty;
                                                } else
                                                    mEQwerty = mQwerty;
                                            } else if (mKoreanKeyboardMode == Common.MODE_DAN) {
                                                mKoreanKeyboard = mDanKeyboard;
                                                if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                                                    if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM )
                                                        mKoreanKeyboard = mDanKeyboard35;
                                                }
                                            } else if (mKoreanKeyboardMode == Common.MODE_NARA) {
                                                if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                                                    mKoreanKeyboard = mQKoreanKeyboard;
                                                    if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM )
                                                        mKoreanKeyboard = mQKoreanKeyboard35;
                                                } else {
                                                    mKoreanKeyboard = mNaraKeyboard;
                                                }
                                            } else if (mKoreanKeyboardMode == Common.MODE_CHUNJIIN_PLUS) {
                                                if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                                                    mKoreanKeyboard = mQKoreanKeyboard;
                                                    if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM )
                                                        mKoreanKeyboard = mQKoreanKeyboard35;
                                                } else {
                                                    mKoreanKeyboard = mSejongPlusKeyboard;
                                                }
                                            }

                                            mCurKeyboard = mKoreanKeyboard;

                                            setKeyBoardMode(true, -1, 14);
                                            if (mKoreanKeyboard == mQKoreanKeyboard || mKoreanKeyboard == mQKoreanKeyboard35)
                                                ((LatinKeyboardView) mMainKeyboardView.getKeyboardView()).setKeyboardMode(Common.MODE_QUERTY);
                                        }
                                    }
                                    mMainKeyboardView.selectKeyboard(MainKeyboardView.GUBUN_KEYBOARD);
                                }
                            } else if (primaryKey == -5) {
                                handleBackspace();
                                /**
                                 keyDownUp(KeyEvent.KEYCODE_DEL);
                                 if (mShiftState != SHIFT_STATE_ONLY_UPPER_CASE)
                                 updateShiftKeyState(getCurrentInputEditorInfo(), -2); // 이모티콘 키보드에서 삭제 버튼 눌렀을 경우**/
                            }

                            if (inputConnection != null && isInputViewShown()) {
                                ExtractedText et = inputConnection.getExtractedText(new ExtractedTextRequest(), 0);
                                if (et != null) {
                                    CharSequence seq = et.text;
                                    String temp = seq.toString();
                                    /**
                                     * 이모티콘 검색 관련 기능 제거 2017.10.31 (문자열 을 잘라내서 array에 담는다)
                                     */
                                    /**
                                     String[] temps = temp.split(" ");
                                     */

                                    /**
                                     * ocb 메모 기능 삭제로 제외
                                     int etLength = temp.length();

                                     if (etLength > 0) {
                                     if (mMainKeyboardView != null) {
                                     mMainKeyboardView.memoVisibleSetting(true);
                                     }
                                     } else {
                                     if (mMainKeyboardView != null) {
                                     mMainKeyboardView.memoVisibleSetting(false);
                                     }
                                     } **/
                                }
                            }
                        }
                    },
                    new MainKeyboardView.BannerAdCallbackListener() {
                        @Override
                        public void onBannerCallResult(boolean result) {
                            // 택스트베너 호출 결과
                            if (result) {
                                bannerIndex++;
                                SharedPreference.setInt(getApplicationContext(), Key.KEY_OCB_AD_BANNER_INDEX, bannerIndex);
                            } else {
                                // 2022.12.23 loadBanner에서 mobwith 광고로 변경
                                if ( mMainKeyboardView != null )
                                    mMainKeyboardView.loadMobWithAd();
                            /*
                            if (mMainKeyboardView != null)
                                mMainKeyboardView.loadBanner(false);
                             */
                                //mMainKeyboardView.loadCriteoBanner();
                                //mMainKeyboardView.loadMediaBanner();
                            }
                        }
                    }
            );
            //return super.onKeyDown(keyCode, event);
        } else {
            LogPrint.d("skkim main keyboard view not null");
        }
        if (mBackupKauto == null && mBackupCurKeyboard == null) {

            //SharedPreference.setInt(getApplicationContext(), Common.PREF_KEYBOARD_MODE, Common.MODE_QUERTY);
            mBackupKoreanKeyboardMode = mKoreanKeyboardMode;
            LogPrint.d("onKeyDown mBackupKoreanKeyboardMode :: " + mBackupKoreanKeyboardMode);
            mBackupCurKeyboard = mCurKeyboard;
            mBackupKauto = kauto;
            mBackupKoreanKeyboard = mKoreanKeyboard;

            mKoreanKeyboardMode = Common.MODE_QUERTY;
            mCurKeyboard = getSelectKeyboard(Common.MODE_QUERTY);
            LogPrint.d("kauto qAutomata 9 backup에 kauto 넣어놓고 q로 kauto를 변경");
            kauto = qAutomata;
            mKoreanKeyboard = mQKoreanKeyboard;
            WindowManager windowService = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            int currentRatation = windowService.getDefaultDisplay().getRotation();
            if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM)
                    mKoreanKeyboard = mQKoreanKeyboard35;
            }

            hide();
        }

        if (event.isShiftPressed()) {
            mHwShift = true;
        }

        if (event.isCapsLockOn()) {
            mHwCapsLock = true;
        }
        // if ALT or CTRL meta keys are using, the key event should not be touched here and be passed through to.
        if ((event.getMetaState() & (KeyEvent.META_ALT_MASK | KeyEvent.META_CTRL_MASK)) == 0) {
            KeyboardLogPrint.d("skkim onKeyDown keyCode1  :: " + keyCode);
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
//                    mPrevPrimaryKeys.clear();
                    // The InputMethodService already takes care of the back
                    // key for us, to dismiss the input method if it is shown.
                    // However, our keyboard could be showing a pop-up window
                    // that back should dismiss, so we first allow it to do that.
                    LogPrint.d("KEYCODE_BACK");
                    recoverBackupKeyboard();
                    if (event.getRepeatCount() == 0 && mMainKeyboardView != null) {
                        if (mMainKeyboardView.handleBack()) {
                            return true;
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_SPACE:
                    if ( event.isShiftPressed() ) {
                        LogPrint.d("space and shift ");
                        onPress(-6);
                        onKey(-6, null);
                        return true;
                    } else {
                        LogPrint.d("space and not shift ");
                    }
                    //onPress(32);
                    //onKey(32, null);

                    /*
                    // 2017.05.02 mNoKorean flag 삭제
                    if (kauto.IsKoreanMode() ) {
//                    if ( !mNoKorean && event.isShiftPressed()) {
                        if (mComposing.length() > 0) {
                            InputConnection ic = getCurrentInputConnection();
                            ic = inputConnection;
                            if (ic != null) {
                                commitTyped(ic);
                            }
                        }
                        if (kauto.IsKoreanMode()) {
                            kauto.FinishAutomataWithoutInput();
                        }
                        kauto.ToggleMode(); // 한글 모드이면서 시프트가 눌려 있다면 토글
                        return true;
                    } else {
                        KeyboardLogPrint.d("skkim translateKeyDown :: " + translateKeyDown(keyCode, event));
                        if (!translateKeyDown(keyCode, event))
                            return super.onKeyDown(keyCode, event);
                        else
                            return true;
                    }*/
                    //            	break;

                    // for Korean Keyboard only.
                    break;
                case KEYCODE_HANGUL:
                case KeyEvent.KEYCODE_KANA:
                case 1005:
                    // 2017.05.02 mNoKorean flag 삭제
                    onPress(-6);
                    onKey(-6, null);
                    /*
                    if (kauto != null) {
                        if (kauto.IsKoreanMode()) {

                        }

                        if (mComposing.length() > 0) {
                            InputConnection ic = getCurrentInputConnection();
                            ic = inputConnection;
                            if (ic != null)
                                commitTyped(ic);
                        }
                        kauto.FinishAutomataWithoutInput();
                        kauto.ToggleMode();
                        // consume this event.
                        return true;

                    }
*/
//                    if (!mNoKorean) {
//                        if (mComposing.length() > 0) {
//                            InputConnection ic = getCurrentInputConnection();
//                            if (ic != null)
//                                commitTyped(ic);
//                        }
//                        if (kauto.IsKoreanMode()) {
//                            kauto.FinishAutomataWithoutInput();
//                        }
//                        kauto.ToggleMode();
//                        // consume this event.
//                        return true;
//                    }
                    break;
                case KeyEvent.KEYCODE_DEL:
                    // Special handling of the delete key: if we currently are
                    // composing text for the user, we want to modify that instead
                    // of let the application to the delete itself.
                    onPress(-5);
                    onKey(-5, null);
                    return true;
                    /*
                    InputConnection ic = getCurrentInputConnection();
                    if (ic != null) {
                        commitTyped(ic);
                    }
                    if (kauto.IsKoreanMode()) {
                        kauto.FinishAutomataWithoutInput();
                    }*/
                    //return super.onKeyDown(keyCode, event);
                    /*
                    if (mComposing.length() > 0) {
                        onKey(Keyboard.KEYCODE_DELETE, null)
                        return true;
                    }*/
                case KeyEvent.KEYCODE_ENTER:
                    // Let the underlying text editor always handle these.
                    if (kauto.IsKoreanMode())
                        kauto.FinishAutomataWithoutInput();
                    return false;

                case KeyEvent.KEYCODE_TAB:
                    return false;

                default:
                    LogPrint.d("isKorean isKoreanKeyboaard() :: " + isKoreanKeyboaard());
                    if (PROCESS_HARD_KEYS) {
                        if (keyCode == KeyEvent.KEYCODE_SPACE && (event.getMetaState() & KeyEvent.META_ALT_ON) != 0) {
                            // A silly example: in our input method, Alt+Space
                            // is a shortcut for 'android' in lower case.
                            InputConnection i_connection = getCurrentInputConnection();
                            i_connection = inputConnection;
                            if (i_connection != null) {
                                // First, tell the editor that it is no longer in the
                                // shift state, since we are consuming this.
                                i_connection.clearMetaKeyStates(KeyEvent.META_ALT_ON);
                                keyDownUp(KeyEvent.KEYCODE_A);
                                keyDownUp(KeyEvent.KEYCODE_N);
                                keyDownUp(KeyEvent.KEYCODE_D);
                                keyDownUp(KeyEvent.KEYCODE_R);
                                keyDownUp(KeyEvent.KEYCODE_O);
                                keyDownUp(KeyEvent.KEYCODE_I);
                                keyDownUp(KeyEvent.KEYCODE_D);
                                // And we consume this event.
                                return true;
                            }
                        }

                        if (!translateKeyDown(keyCode, event)) {
                            return super.onKeyDown(keyCode, event);
                        } else {
                            return true;
                        }
                    }
                    // For all other keys, if we want to do transformations on
                    // text being entered with a hard keyboard, we need to process
                    // it and do the appropriate action.

            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Use this to monitor key events being delivered to the application.
     * We get first crack at them, and can either resume them or let them
     * continue to the app.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        KeyboardLogPrint.w("skkim onKeyUp");

        // If we want to do transformations on text being entered with a hard
        // keyboard, we need to process the up events to update the meta key
        // state we are tracking.
        if (PROCESS_HARD_KEYS) {
            // if (mPredictionOn) {
            mMetaState = MetaKeyKeyListener.handleKeyUp(mMetaState, keyCode, event);
            // }
        }
        mHwShift = false;
        mHwCapsLock = false;
        return super.onKeyUp(keyCode, event);
    }

    /**
     * Helper function to commit any text being composed in to the editor.
     */
    private void commitTyped(InputConnection inputConnection) {
        if (mComposing.length() > 0) {
            KeyboardLogPrint.d("skkim null commitText 30 :: " + mComposing.toString());
            inputConnection.commitText(mComposing, 1);
            mComposing.setLength(0);
            // updateCandidates();
        }
    }

    /**
     * Helper to update the shift state of our keyboard based on the initial
     * editor state.
     */
    // KGS need to fix..
    private void updateShiftKeyState(EditorInfo attr, int position) {
        KeyboardLogPrint.w("SoftKeyboard updateShiftKeyState, position :: " + position);
        if (attr != null && mMainKeyboardView != null) {
            if (mKoreanKeyboardMode == Common.MODE_QUERTY || mKoreanKeyboardMode == Common.MODE_DAN || mKoreanKeyboardMode == Common.MODE_NARA || mKoreanKeyboardMode == Common.MODE_CHUNJIIN_PLUS) {
                if (mQwertyNum == mMainKeyboardView.getKeyboard() || mEQwerty == mMainKeyboardView.getKeyboard()
                        || mQwertyNum35 == mMainKeyboardView.getKeyboard()) {
                    int caps = 0;
                    EditorInfo ei = getCurrentInputEditorInfo();
                    if (ei != null && ei.inputType != EditorInfo.TYPE_NULL) {
                        caps = getCurrentInputConnection().getCursorCapsMode(attr.inputType);
                        caps = inputConnection.getCursorCapsMode(attr.inputType);
                    }
//                    mMainKeyboardView.setShifted(mCapsLock || caps != 0);
                    if (mShiftState != SHIFT_STATE_ONLY_UPPER_CASE) {
                        mShiftState = SHIFT_STATE_LOWER_CASE;
                        mMainKeyboardView.setShifted(false, 0); // updateShiftKeyState 키보드종류가 쿼티, 단, 나라 이면서 영문키보드일 경우 setShifted false
                        KeyboardLogPrint.e("setShift, updateShiftKeyState 키보드 종류가 쿼티, 단, 나라 이면서 영문키보드일 경우 setShift false");
                    }
                } else if (mQKoreanKeyboard == mMainKeyboardView.getKeyboard()
                        || mQKoreanKeyboard35 == mMainKeyboardView.getKeyboard()) {
//                    mKoreanShiftedKeyboard.setShifted(false);
//                    setKeyBoardMode(true, -1);
//                    mMainKeyboardView.setKeyboard(mKoreanKeyboard);
//                    mKoreanKeyboard.setShifted(false);

                    mMainKeyboardView.setShifted(false, 1); // updateShiftKeyState 키보드 종류가 쿼티, 단, 나라 이면서 한글 키보드 쿼티 인 경우 setShifted false
                    KeyboardLogPrint.e("setShift, updateShiftKeyState 키보드 종류가 쿼티, 단, 나라 이면서 한글 키보드 쿼티 인 경우");
                    WindowManager windowService = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                    int currentRatation = windowService.getDefaultDisplay().getRotation();
                    if (mKoreanKeyboardMode == Common.MODE_NARA) {
                        if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation)
                            ((LatinKeyboardView) mMainKeyboardView.getKeyboardView()).setKeyboardMode(Common.MODE_QUERTY);
                    }
                } else {
                    KeyboardLogPrint.e("setShift, updateShiftKeyState 키보드 종류가 쿼티, 단, 나라 이면서 한글쿼티, 영문쿼티가 아닐 경우");
                }
            } else if (mKoreanKeyboardMode == Common.MODE_CHUNJIIN) {
                if (mEQwerty == mMainKeyboardView.getKeyboard() || mQwertyNum == mMainKeyboardView.getKeyboard()
                        || mQwertyNum35 == mMainKeyboardView.getKeyboard()) {
                    int caps = 0;
                    EditorInfo ei = getCurrentInputEditorInfo();
                    if (ei != null && ei.inputType != EditorInfo.TYPE_NULL) {
                        caps = getCurrentInputConnection().getCursorCapsMode(attr.inputType);
                        caps = inputConnection.getCursorCapsMode(attr.inputType);
                    }
//                    mMainKeyboardView.setShifted(mCapsLock || caps != 0);
                    if (mShiftState != SHIFT_STATE_ONLY_UPPER_CASE) {
                        mShiftState = SHIFT_STATE_LOWER_CASE;
                        mMainKeyboardView.setShifted(false, 2); // updateShiftKeyState 키보드모드가 천지인이면서 영문쿼티키보드일 경우
                        KeyboardLogPrint.d("setShift, updateShiftKeyState 키보드모드가 천지인이면서 영문쿼티키보드일 경우");
                    }
                } else if (mQKoreanKeyboard == mMainKeyboardView.getKeyboard()
                        || mQKoreanKeyboard35 == mMainKeyboardView.getKeyboard()) {
                    mMainKeyboardView.setShifted(false, 3); // updateShiftKeyState 키보드모드가 천지인이면서 쿼티 한글일 경우, 한글 천지인이나와야하지만 가로모드일 경우에는 천지인 키보드라도 쿼티 키보드가 나와야함, setShift false
                    KeyboardLogPrint.e("setShift, updateShiftKeyState 키보드모드가 천지인이면서 쿼티 한글일 경우, 한글 천지인이나와야하지만 가로모드일 경우에는 천지인 키보드라도 쿼티 키보드가 나와야하므로");
                    WindowManager windowService = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                    int currentRatation = windowService.getDefaultDisplay().getRotation();
                    if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation)
                        ((LatinKeyboardView) mMainKeyboardView.getKeyboardView()).setKeyboardMode(Common.MODE_QUERTY);
                } else {
                    KeyboardLogPrint.w("updateShiftKeyState 키보드모드가 천지인이면서 한글쿼티, 영문쿼티가 아닐 경우");
                }
            }

        }
    }

    /**
     * Helper to determine if a given character code is alphabetic.
     */
    private boolean isAlphabet(int code) {
        if (Character.isLetter(code)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Helper to send a key down / key up pair to the current editor.
     */
    private void keyDownUp(int keyEventCode) {
        KeyboardLogPrint.e("skkim keyDownUp keyEventCode :: " + keyEventCode);
        if (inputConnection != null) {
            KeyboardLogPrint.e("keyDownUp inputConnection not null");
            inputConnection.sendKeyEvent(
                    new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
            inputConnection.sendKeyEvent(
                    new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
        } else {
            KeyboardLogPrint.e("keyDownUp inputConnection null");
            /**getCurrentInputConnection().sendKeyEvent(
             new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
             getCurrentInputConnection().sendKeyEvent(
             new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));**/
            inputConnection.sendKeyEvent(
                    new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
            inputConnection.sendKeyEvent(
                    new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
        }

    }

    /**
     * Helper to send a character to the editor as raw key events.
     */
    private void sendKey(int keyCode) {
        KeyboardLogPrint.e("skkim sendKey keycode :: " + keyCode);
        switch (keyCode) {
            case '\n':
                setEditorAction();
                break;
            default:
                if (keyCode >= '0' && keyCode <= '9') {
                    keyDownUp(keyCode - '0' + KeyEvent.KEYCODE_0);
                } else {
                    if (mMainKeyboardView != null && (isChunjiinKeyboard() || isChunjiinPlusKeyboard())) {
                        KeyboardLogPrint.w("sendKey keycode :: " + keyCode);
                        KeyboardLogPrint.w("sendKey mIsComplete :: " + mIsComplete);
                        if (keyCode == 32 && !mIsComplete) {
                            KeyboardLogPrint.e("quest sendKey 32 not complete");
                            inputConnection.finishComposingText();
                            if (kauto != null) {
                                kauto.setInitState();
                                kauto.FinishAutomataWithoutInput();
                            }
                            mComposing.setLength(0);
                            mIsComplete = true;
                            mIsCompleteFromHandler = false;
                        } else {
                            KeyboardLogPrint.d("skkim null commitText 31 :: " + String.valueOf((char) keyCode));
                            inputConnection.commitText(String.valueOf((char) keyCode), 1);
                            mIsComplete = false;
                        }
                    } else {
                        KeyboardLogPrint.d("num sendKey 32 else else");
                        if ( mCurKeyboard == mNumberKeyboard ) {
                            KeyboardLogPrint.d(" num keyboard and mIsComplete :: " + mIsComplete);
                            if (keyCode == 32 ) {
                                if ( !mIsComplete ) {
                                    KeyboardLogPrint.e("quest sendKey 32 complete false");
                                    inputConnection.finishComposingText();
                                    if (kauto != null) {
                                        kauto.setInitState();
                                        kauto.FinishAutomataWithoutInput();
                                    }
                                    mComposing.setLength(0);
                                    mIsComplete = true;
                                    mIsCompleteFromHandler = false;
                                } else {
                                    KeyboardLogPrint.d("skkim null commitText 32 :: " + String.valueOf((char) keyCode));
                                    inputConnection.commitText(String.valueOf((char) keyCode), 1);
                                    mIsComplete = false;
                                }
                            } else {
                                KeyboardLogPrint.d("skkim null commitText 33 :: " + String.valueOf((char) keyCode));
                                inputConnection.commitText(String.valueOf((char) keyCode), 1);
                                mIsComplete = true;
                                mIsCompleteFromHandler = false;
                                inputConnection.finishComposingText();
                                if (kauto != null) {
                                    kauto.setInitState();
                                    kauto.FinishAutomataWithoutInput();
                                }
                                mComposing.setLength(0);
                            }
                        } else {
                            KeyboardLogPrint.d("not num keyboard");
                            KeyboardLogPrint.d("skkim null commitText 34 :: " + String.valueOf((char) keyCode));
                            inputConnection.commitText(String.valueOf((char) keyCode), 1);
                            mIsComplete = true;
                            mIsCompleteFromHandler = false;
                            inputConnection.finishComposingText();
                            if (kauto != null) {
                                kauto.setInitState();
                                kauto.FinishAutomataWithoutInput();
                            }
                            mComposing.setLength(0);
                        }
                    }
                }
                break;
        }
    }

    private void setEditorAction() {
        int val = mAttribute.imeOptions & (EditorInfo.IME_MASK_ACTION | EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        KeyboardLogPrint.e("setEditorAction imeOptions :: " + mAttribute.imeOptions);
        KeyboardLogPrint.e("setEditorAction val :: " + val);
        if (searchListener != null) {
            searchListener.onSearch();
        }
        switch (mAttribute.imeOptions & (EditorInfo.IME_MASK_ACTION | EditorInfo.IME_FLAG_NO_ENTER_ACTION)) {
            case EditorInfo.IME_ACTION_GO:
                inputConnection.performEditorAction(EditorInfo.IME_ACTION_GO);
                //isCorrectEnter = true;
                break;
            case EditorInfo.IME_ACTION_NEXT:
                inputConnection.performEditorAction(EditorInfo.IME_ACTION_NEXT);
                //isCorrectEnter = false;
                break;
            case EditorInfo.IME_ACTION_SEARCH:
                inputConnection.performEditorAction(EditorInfo.IME_ACTION_SEARCH);
                //isCorrectEnter = true;
                break;
            case EditorInfo.IME_ACTION_SEND:
                inputConnection.performEditorAction(EditorInfo.IME_ACTION_SEND);
                //isCorrectEnter = true;
                break;
            case EditorInfo.IME_ACTION_DONE:
                inputConnection.performEditorAction(EditorInfo.IME_ACTION_DONE);
                //isCorrectEnter = true;
                break;
            default:
                keyDownUp(KeyEvent.KEYCODE_ENTER);
                //isCorrectEnter = true;
                break;
        }
    }

//    public void setImeOption() {
//        mCurKeyboard.setImeOptions(getResources(), 0);
//    }

    // Implementation of KeyboardViewListener
    private final static int[] CHUNJIIN_63_CODE_MAP = {46, 44, 63, 33}; //.,?!
    private final static int[] NUMBER_63_CODE_MAP = {46, 44, 45, 47}; //.,-/
    public void onKey(int primaryCode, int[] keyCodes) {
        KeyboardLogPrint.d("skkim onKey primaryCode :: " + primaryCode);
        if (primaryCode == 0 || primaryCode == -8080 || primaryCode == -8081)
            return;
        //lastEnteredPrimaryCode = primaryCode;
        if (primaryCode > 0 || primaryCode != 32 || primaryCode != 10)
            isCharactorEntered = true;

        WindowManager windowService = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int currentRatation = windowService.getDefaultDisplay().getRotation();
        if (mMainKeyboardView != null && isChunjiinKeyboard() && inputConnection != null) {
            CharSequence charBeforecursor = inputConnection.getTextBeforeCursor(1, 0);
            if (charBeforecursor != null && charBeforecursor.length() > 0 && primaryCode >= 100) {
                if (mComposing.length() == 0 && mIsCompleteFromHandler && mAutomataValues != null) {
                    ArrayList<Integer> returnVal = kauto.getState(charBeforecursor.toString());
                    int lcCode = returnVal.get(1);
                    if (lcCode == primaryCode) {
                        // ROTATION 값일 경우 COMPLETE 됬어도 동작해야함
                        kauto.setInitState();
                        kauto.FinishAutomataWithoutInput();
                    } else if (charBeforecursor.length() == 1 && charBeforecursor.charAt(0) == InputTables.DotCode || charBeforecursor.charAt(0) == InputTables.DoubleDotCode) {

                        delete();
                        kauto.setAutomataValue(null);
                        kauto.setAutomata("", charBeforecursor.toString(), 4);
                    } else {
                        delete();
                        kauto.setAutomataValue(mAutomataValues);
                    }
                }
            }
        }
        mIsCompleteFromHandler = false;

        if (isChunjiinKeyboard() && (primaryCode < 48 || primaryCode > 57)) {
            // ?! 키가 들어올 경우에는 제외
            if (primaryCode != 63) {
                primaryCode = mPressPrimaryKey;
            }
        }

        if (isChunjiinPlusKeyboard() && (primaryCode < 48 || primaryCode > 57)) {
            // ?! 키가 들어올 경우에는 제외
            if (primaryCode != 63 && primaryCode != 44 && primaryCode != 12594 && primaryCode != 12600 && primaryCode != 12611 && primaryCode != 12614 && primaryCode != 12617) {
                primaryCode = mPressPrimaryKey;
            }
        }
        // 2017.02.27 설정창에서 키보드를 호출할 경우 모든키 이벤트 안먹도록 막음
        if (mIsSettingOn)
            return;
        if (primaryCode != 8230)
            mRotatedVal1 = "";

        if (primaryCode != 8231)
            mRotatedVal2 = "";
        mIsEmojiView = false;
        LogPrint.d("skkimmm shift onKey primaryCode :: " + primaryCode);
        if (isWordSeparator(primaryCode)) {
            // Handle separator
            if (isChunjiinPlusKeyboard() && (primaryCode == 63 || primaryCode == 44)) {
                KeyboardLogPrint.d("chunjiin plus and 63 or 44");
                if (mComposing.length() > 0) {
                    char lastChar = mComposing.charAt(mComposing.length() - 1);
                    String replaceWord = null;

                    if (primaryCode == 63) {
                        if (lastChar == (char) 33) {
                            replaceWord = String.valueOf((char) 63);
                        } else if (lastChar == (char) 63) {
                            replaceWord = String.valueOf((char) 33);
                        }
                    }

                    if (primaryCode == 44) {
                        if (lastChar == (char) 46) {
                            replaceWord = String.valueOf((char) 44);
                        } else if (lastChar == (char) 44) {
                            replaceWord = String.valueOf((char) 46);
                        }
                    }

                    if (replaceWord != null) {
                        mComposing.replace(mComposing.length() - 1, mComposing.length(), replaceWord);
                        mIsComplete = false;
                        if (inputConnection != null) {
                            KeyboardLogPrint.d("skkim null mComposing 18 :: " + mComposing.toString());
                            inputConnection.setComposingText(mComposing, 1);
                        }
                        return;

                    } else {
                        if (inputConnection != null) {
                            inputConnection.finishComposingText();
                            mComposing.setLength(0);
                            mIsCompleteFromHandler = false;
                            mIsComplete = true;
                            if (kauto != null) {
                                kauto.setInitState();
                                kauto.FinishAutomataWithoutInput();
                            }
                            mComposing.append(String.valueOf((char) primaryCode));
                            KeyboardLogPrint.d("skkim null mComposing 19 :: " + mComposing.toString());
                            inputConnection.setComposingText(mComposing, 1);
                        }
                        return;
                    }
                } else {
                    mComposing.append(String.valueOf((char) primaryCode));
                    mIsComplete = (getArrayIndex(CHUNJIIN_63_CODE_MAP, primaryCode) == -1);
                    if (inputConnection != null) {
                        KeyboardLogPrint.d("skkim null mComposing 20 :: " + mComposing.toString());
                        inputConnection.setComposingText(mComposing, 1);
                    }
                    return;
                }
            } else if (isChunjiinKeyboard() && primaryCode == 63) {
                KeyboardLogPrint.d("chunjiin and 63");
                if (mComposing.length() > 0) {
                    char lastChar = mComposing.charAt(mComposing.length() - 1);
                    boolean isFoundChar = false;
                    int index = -1;
                    for (int i = 0; i < CHUNJIIN_63_CODE_MAP.length; i++) {
                        if (lastChar == CHUNJIIN_63_CODE_MAP[i]) {
                            index = (i + 1) % 4;
                            isFoundChar = true;
                        }
                    }

                    if (isFoundChar) {
                        String replaceWord = String.valueOf((char) CHUNJIIN_63_CODE_MAP[index]);
                        mComposing.replace(mComposing.length() - 1, mComposing.length(), replaceWord);
                        mIsComplete = false;
                        if (inputConnection != null) {
                            inputConnection.setComposingText(mComposing, 1);
                            KeyboardLogPrint.d("skkim null mComposing 21 :: " + mComposing.toString());
                        }
                        return;
                    } else {
                        if (inputConnection != null) {
                            inputConnection.finishComposingText();
                            mComposing.setLength(0);
                            mIsCompleteFromHandler = false;
                            mIsComplete = true;
                            if (kauto != null) {
                                kauto.setInitState();
                                kauto.FinishAutomataWithoutInput();
                            }
                            mComposing.append(String.valueOf((char) CHUNJIIN_63_CODE_MAP[0]));
                            KeyboardLogPrint.d("skkim null mComposing 22 :: " + mComposing.toString());
                            inputConnection.setComposingText(mComposing, 1);
                        }
                        return;
                    }

                } else {
                    mComposing.append(String.valueOf((char) CHUNJIIN_63_CODE_MAP[0]));
                    mIsComplete = (getArrayIndex(CHUNJIIN_63_CODE_MAP, primaryCode) == -1);
                    if (inputConnection != null) {
                        KeyboardLogPrint.d("skkim null mComposing 23 :: " + mComposing.toString());
                        inputConnection.setComposingText(mComposing, 1);
                    }
                    return;
                }
            } else if ( mCurKeyboard != null && mCurKeyboard == mNumberKeyboard && primaryCode == 63) { // 20230112 숫자 키보드 로테이션 안되는 현상
                KeyboardLogPrint.d("number keyboard mComposing length :: " + mComposing.length());
                if (mComposing.length() > 0) {
                    char lastChar = mComposing.charAt(mComposing.length() - 1);
                    boolean isFoundChar = false;
                    int index = -1;
                    for (int i = 0; i < NUMBER_63_CODE_MAP.length; i++) {
                        if (lastChar == NUMBER_63_CODE_MAP[i]) {
                            index = (i + 1) % 4;
                            isFoundChar = true;
                        }
                    }

                    if (isFoundChar) {
                        String replaceWord = String.valueOf((char) NUMBER_63_CODE_MAP[index]);
                        mComposing.replace(mComposing.length() - 1, mComposing.length(), replaceWord);
                        mIsComplete = false;
                        if (inputConnection != null) {
                            KeyboardLogPrint.d("skkim null mComposing 24 :: " + mComposing.toString());
                            inputConnection.setComposingText(mComposing, 1);
                        }
                        return;
                    } else {
                        if (inputConnection != null) {
                            inputConnection.finishComposingText();
                            mComposing.setLength(0);
                            mIsCompleteFromHandler = false;
                            mIsComplete = true;
                            if (kauto != null) {
                                kauto.setInitState();
                                kauto.FinishAutomataWithoutInput();
                            }
                            mComposing.append(String.valueOf((char) NUMBER_63_CODE_MAP[0]));
                            KeyboardLogPrint.d("skkim null mComposing 1 :: " + mComposing.toString());
                            inputConnection.setComposingText(mComposing, 1);
                        }
                        return;
                    }
                } else {
                    mComposing.append(String.valueOf((char) NUMBER_63_CODE_MAP[0]));
                    mIsComplete = (getArrayIndex(NUMBER_63_CODE_MAP, primaryCode) == -1);

                    if (inputConnection != null) {
                        inputConnection.setComposingText(mComposing, 1);
                        KeyboardLogPrint.d("skkim null mComposing 2 :: " + mComposing.toString());
                    }

                    // 20230116 rotation 첫글자 입력 시 complete false로 바꿔줘야 space 입력 시 한칸 띄지 않고 complete만 됨.
                    mIsComplete = false;
                    return;
                }
            }
            KeyboardLogPrint.e("quest mComposing.length :: " + mComposing.length());
            KeyboardLogPrint.e("quest kauto.IsKoreanMode() :: " + kauto.IsKoreanMode());
            if (mComposing.length() > 0)
                commitTyped(inputConnection);
            if (kauto.IsKoreanMode())
                kauto.FinishAutomataWithoutInput();
            sendKey(primaryCode);
            if (mMainKeyboardView != null) {
                KeyboardLogPrint.e("111 mShiftState :: " + mShiftState);
                if (kauto.IsKoreanMode()) {
                    KeyboardLogPrint.e("입력된 값이 구분자(space, ., , 등)인 경우, 한글키보드");
                    updateShiftKeyState(getCurrentInputEditorInfo(), 1); // 입력된 값이 구분자(space, ., , 등)인 경우, 한글키보드
                } else {
                    if (mShiftState != SHIFT_STATE_ONLY_UPPER_CASE) {
                        KeyboardLogPrint.e("입력된 값이 구분자(space, ., , 등)인 경우, 한글키보드 아님");
                        updateShiftKeyState(getCurrentInputEditorInfo(), 111); // 입력된 값이 구분자(space, ., , 등)인 경우
                    }
                }
            }
        } else if (primaryCode == Keyboard.KEYCODE_DELETE) {
            handleBackspace();
        } else if (primaryCode == Keyboard.KEYCODE_SHIFT) { // 대소문자 변경 등 왼쪽 시프트 키
            if (mMainKeyboardView != null && (mQwertyNum == mMainKeyboardView.getKeyboard()
                    || mEQwerty == mMainKeyboardView.getKeyboard()) || mQwertyNum35 == mMainKeyboardView.getKeyboard()) { // 영문 쿼티 키보드일 경우
                if (mShiftState == SHIFT_STATE_LOWER_CASE || mShiftState == SHIFT_STATE_ONLY_UPPER_CASE)
                    handleShift();
                setShiftState();
            } else { // 영문 쿼티 키보드가 아닐 경우
                handleShift();
            }
        } else if (primaryCode == Keyboard.KEYCODE_CANCEL) {
            handleClose();
            return;
        } else if (primaryCode == Keyboard.KEYCODE_ALT) { // 한/영버튼
            KeyboardLogPrint.e("keycode alt");
            // Show a menu or somethin'
            // 2017.05.02 mNoKorean flag 삭제
            if (mMainKeyboardView != null) {
                if (mCurKeyboard == mSymbolsKeyboard || mCurKeyboard == mSymbolsShiftedKeyboard || mCurKeyboard == mSymbolsShiftedKeyboard1
                        || mCurKeyboard == mQSymbolsKeyboard || mCurKeyboard == mQSymbolsKeyboard_35 || mCurKeyboard == mQSymbolsKeyboard1
                        || mCurKeyboard == mQSymbolsKeyboard1_35 || mCurKeyboard == mNumberKeyboard) {

                    if (kauto.IsKoreanMode()) {
                        mCurKeyboard = mKoreanKeyboard;
                        setKeyBoardMode(true, -1, 17);
                    } else {
                        boolean isQwertyNumSet = SharedPreference.getBoolean(getApplicationContext(), Common.PREF_QWERTY_NUM_SETTING);
                        if (mKoreanKeyboardMode == Common.MODE_QUERTY && isQwertyNumSet) {
                            mCurKeyboard = mQwertyNum;
                            if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                                if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM )
                                    mCurKeyboard = mQwertyNum35;
                            }
                        } else
                            mCurKeyboard = mEQwerty;
                        setKeyBoardMode(false, -1, 18);
                    }
                    mMainKeyboardView.setKeyboard(mCurKeyboard); // 한영버튼 클릭 시 한글 키보드 혹은 영문키보드로 mCurKeyboard 설정
                    if (mComposing.length() > 0)
                        commitTyped(inputConnection);
                    if (mCurKeyboard == mQwertyNum || mCurKeyboard == mEQwerty
                            || mCurKeyboard == mKoreanKeyboard || mCurKeyboard == mQwertyNum35) {
                        if (mCurKeyboard == mKoreanKeyboard) {
                            LogPrint.d("kksskk onKey shift false1");
                            mCurKeyboard.setShifted(false); // onKey, keycode alt, 현재의 키보드가 심볼 또는 숫자 , curkeyboard가 쿼티 또는 한글, 키보드 종류 무관하게 기호 숫자 키패드에서 한/영 버튼 눌렀을 경우
                            KeyboardLogPrint.e("setShift, onKey, keycode alt, 현재의 키보드가 심볼 또는 숫자 , curkeyboard가 쿼티 또는 한글");
                        } else {
                            if (mShiftState == SHIFT_STATE_UPPER_CASE || mShiftState == SHIFT_STATE_ONLY_UPPER_CASE) { // 영문키보드로 바뀌는데 상태가 대문자일 경우 대문자 유지
                                LogPrint.d("kksskk onKey shift true");
                                mCurKeyboard.setShifted(true); // 영문키보드로 바뀌었을 때 이전 상태가 대문자였다면 유지
                            } else {
                                LogPrint.d("kksskk onKey shift false");
                                mCurKeyboard.setShifted(false); // 영문키보드로 바뀌었을 때 이전 상태가 소문자였다면 유지
                            }
                        }
                    }
                } else {
                    if (kauto.IsKoreanMode()) {
                        boolean isQwertyNumSet = SharedPreference.getBoolean(getApplicationContext(), Common.PREF_QWERTY_NUM_SETTING);
                        if (mKoreanKeyboardMode == Common.MODE_QUERTY && isQwertyNumSet) {
                            mCurKeyboard = mQwertyNum;
                            if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                                if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM )
                                    mCurKeyboard = mQwertyNum35;
                            }
                        } else {
                            LogPrint.d("ttss mCurKeybaord null @@@@@ ##%%%## mCurKeyboard = mEQwerty");
                            mCurKeyboard = mEQwerty;
                        }

                        setKeyBoardMode(false, -1, 19);
                    } else {
                        mCurKeyboard = mKoreanKeyboard;
                        setKeyBoardMode(true, -1, 20);
                    }
                    mMainKeyboardView.setKeyboard(mCurKeyboard);
                    if (mComposing.length() > 0)
                        commitTyped(inputConnection);
                    kauto.ToggleMode();
                    if (mCurKeyboard == mQwertyNum || mCurKeyboard == mEQwerty
                            || mCurKeyboard == mKoreanKeyboard || mCurKeyboard == mQwertyNum35) {
                        if (mCurKeyboard == mKoreanKeyboard) {
                            LogPrint.d("kksskk onKey shift false1-1");
                            mCurKeyboard.setShifted(false); // onKey, keycode alt, 영문 키보드에서 한글 키보드로 바뀌었을 때 shift false로 setting 함
                            KeyboardLogPrint.e("setShift, onKey, keycode alt, 현재의 키보드가 심볼 또는 숫자아님, curkeyboard가 쿼티 또는 한글");
                        } else {
                            if (mShiftState == SHIFT_STATE_UPPER_CASE || mShiftState == SHIFT_STATE_ONLY_UPPER_CASE) { // 영문키보드로 바뀌는데 상태가 대문자일 경우 대문자 유지
                                LogPrint.d("kksskk onKey shift true1");
                                mCurKeyboard.setShifted(true); // onKey, keycode alt 한글 키보드에서 영문키보드로 바뀌었을 때 이전 상태가 대문자였다면 유지
                            } else {
                                LogPrint.d("kksskk onKey shift false1");
                                mCurKeyboard.setShifted(false); // onKey, keycode alt 한글 키보드에서 영문키보드로 바뀌었을 때 이전 상태가 소문자였다면 유지
                            }
                        }
                    }
                }

                if (mKoreanKeyboardMode == Common.MODE_CHUNJIIN || mKoreanKeyboardMode == Common.MODE_NARA || mKoreanKeyboardMode == Common.MODE_CHUNJIIN_PLUS) {
                    if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation)
                        ((LatinKeyboardView) mMainKeyboardView.getKeyboardView()).setKeyboardMode(Common.MODE_QUERTY);
                }
            }
//            if (mMainKeyboardView != null) {
//                if (mCurKeyboard == mSymbolsKeyboard || mCurKeyboard == mSymbolsShiftedKeyboard || mCurKeyboard == mSymbolsShiftedKeyboard1
//                        || mCurKeyboard == mNumberKeyboard) {
//                    if (!mNoKorean) {
//                        if (kauto.IsKoreanMode()) {
//                            setKeyBoardMode(true, -1);
//                            mCurKeyboard = mKoreanKeyboard;
//                        } else {
//                            mCurKeyboard = mQwertyKeyboard;
//                            setKeyBoardMode(false, -1);
//                        }
//                        mMainKeyboardView.setKeyboard(mCurKeyboard);
//                        if (mComposing.length() > 0)
//                            commitTyped(getCurrentInputConnection());
//                        if (mCurKeyboard == mQwertyKeyboard || mCurKeyboard == mKoreanKeyboard)
//                            mCurKeyboard.setShifted(false);
//                    }
//                } else {
//                    LogPrint.w("mNoKorean :: " + mNoKorean);
//                    LogPrint.w("kauto.IsKoreanMode :: " + kauto.IsKoreanMode());
//                    if (!mNoKorean) {
//                        if (kauto.IsKoreanMode()) {
//                            mCurKeyboard = mQwertyKeyboard;
//                            setKeyBoardMode(false, -1);
//                        } else {
//                            setKeyBoardMode(true, -1);
//                            mCurKeyboard = mKoreanKeyboard;
//                        }
//                        mMainKeyboardView.setKeyboard(mCurKeyboard);
//                        if (mComposing.length() > 0)
//                            commitTyped(getCurrentInputConnection());
//                        kauto.ToggleMode();
//                        if (mCurKeyboard == mQwertyKeyboard || mCurKeyboard == mKoreanKeyboard)
//                            mCurKeyboard.setShifted(false);
//                    }
//                }
//
//                WindowManager windowService = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
//                int currentRatation = windowService.getDefaultDisplay().getRotation();
//
//                if (mKoreanKeyboardMode == Common.MODE_CHUNJIIN || mKoreanKeyboardMode == Common.MODE_NARA) {
//                    if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation)
//                        ((LatinKeyboardView) mMainKeyboardView.getKeyboardView()).setKeyboardMode(Common.MODE_QUERTY);
//                }
//            }
        } else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE && mMainKeyboardView != null) { // 기호/123 버튼 클릭 시
            if (isChunjiinKeyboard() || isChunjiinPlusKeyboard() || isNaraKeyboard()) {
                mCurKeyboard = (LatinKeyboard) mMainKeyboardView.getKeyboard();
                if (mCurKeyboard == mSymbolsKeyboard || mCurKeyboard == mSymbolsShiftedKeyboard || mCurKeyboard == mSymbolsShiftedKeyboard1) {
                    mCurKeyboard = mNumberKeyboard;
                    setKeyBoardMode(false, NUM_KEYBOARD, 21);
                } else if (mCurKeyboard == mNumberKeyboard) {
                    mCurKeyboard = mSymbolsKeyboard;
                    setKeyBoardMode(false, SYMBOL_KEYBOARD, 22);
                } else {
                    mBackupKeyboard = mCurKeyboard;
                    mCurKeyboard = mNumberKeyboard;
                    setKeyBoardMode(false, NUM_KEYBOARD, 23);
                }
//                mMainKeyboardView.setKeyboard(mCurKeyboard); // 2017.05.10 setKeyboardMode에서 하고 있는 작업이므로 주석처리
                if (mCurKeyboard == mSymbolsKeyboard || mCurKeyboard == mSymbolsShiftedKeyboard || mCurKeyboard == mSymbolsShiftedKeyboard1) { //현재 키보드가 심볼인 상태에서 기호/123 클릭
                    KeyboardLogPrint.e("shift isKorean :: " + kauto.IsKoreanMode());
                    mCurKeyboard.setShifted(false); // 천지인이면서 onKey, 기호/123 클릭 시 , 현재의 키보드가 심볼
                    KeyboardLogPrint.e("setShift, 천지인이면서 onKey, 기호/123 클릭 시 , 현재의 키보드가 심볼");
                }
            } else {
                mCurKeyboard = (LatinKeyboard) mMainKeyboardView.getKeyboard();
                boolean isQwertySymbol = false;
                if (kauto.IsKoreanMode()) {
                    if (mKoreanKeyboardMode == Common.MODE_DAN) {
                        isQwertySymbol = true;
                    } else if (mKoreanKeyboardMode == Common.MODE_QUERTY) {
                        isQwertySymbol = true;
                    }
                } else {
                    if (mCurKeyboard == mEQwerty || mCurKeyboard == mQwertyNum || mCurKeyboard == mQwertyNum35) {
                        isQwertySymbol = true;
                    }
                }
                if (!isQwertySymbol) {
                    if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation)
                        isQwertySymbol = true;
                }
                if (isQwertySymbol) {
                    mBackupKeyboard = mCurKeyboard;
                    mCurKeyboard = mQSymbolsKeyboard;
                    if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                        if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM )
                            mCurKeyboard = mQSymbolsKeyboard_35;
                    }
                    setKeyBoardMode(false, SYMBOL_KEYBOARD, 26);

                    if (mCurKeyboard == mQSymbolsKeyboard || mCurKeyboard == mQSymbolsKeyboard1
                            || mCurKeyboard ==mQSymbolsKeyboard_35 || mCurKeyboard == mQSymbolsKeyboard1_35) {
                        mCurKeyboard.setShifted(false); // 천지인이 아니면서 기호/123 눌렀을 경우 현재 키보드가 심볼
                        KeyboardLogPrint.e("setShift, 천지인이 아니면서 기호/123 눌렀을 경우 현재 키보드가 심볼");
                    }
                } else {
                    if (mCurKeyboard == mSymbolsKeyboard || mCurKeyboard == mSymbolsShiftedKeyboard || mCurKeyboard == mSymbolsShiftedKeyboard1) {
                        mCurKeyboard = mNumberKeyboard;
                        setKeyBoardMode(false, NUM_KEYBOARD, 24);
                    } else if (mCurKeyboard == mNumberKeyboard) {
                        mCurKeyboard = mSymbolsKeyboard;
                        setKeyBoardMode(false, SYMBOL_KEYBOARD, 25);
                    } else {
                        mBackupKeyboard = mCurKeyboard;
                        mCurKeyboard = mSymbolsKeyboard;
                        setKeyBoardMode(false, SYMBOL_KEYBOARD, 26);
                    }
//                mMainKeyboardView.setKeyboard(mCurKeyboard); // 2017.05.10 setKeyboardMode에서 하고 있는 작업이므로 주석처리
                    if (mCurKeyboard == mSymbolsKeyboard || mCurKeyboard == mSymbolsShiftedKeyboard || mCurKeyboard == mSymbolsShiftedKeyboard1) {
                        mCurKeyboard.setShifted(false); // 천지인이 아니면서 기호/123 눌렀을 경우 현재 키보드가 심볼
                        KeyboardLogPrint.e("setShift, 천지인이 아니면서 기호/123 눌렀을 경우 현재 키보드가 심볼");
                    }
                }
            }
        } else {
            if (isChunjiinPlusKeyboard()) {
                if (mComposing.length() > 0) {
                    char lastChar = mComposing.charAt(mComposing.length() - 1);
                    if (lastChar == (char) 33 || lastChar == (char) 63 || lastChar == (char) 46 || lastChar == (char) 44) {
//                        if (mHandler != null)
//                            mHandler.removeCallbacksAndMessages(null);
                        //timerCancel(4);
                        KeyboardLogPrint.d("stop complete called 6");
                        if (mCompleteHandler != null)
                            mCompleteHandler.sendEmptyMessage(STOP_COMPLETE_TIMER);
                        mCompleteCount = 0;

                        mComposing.setLength(0);
                        mIsCompleteFromHandler = false;
                        kauto.setInitState();
                        mIsComplete = true;
                        kauto.FinishAutomataWithoutInput();
                        if (inputConnection != null)
                            inputConnection.finishComposingText();
                    }
                }
            } else if (isChunjiinKeyboard()) {
                if (mComposing.length() > 0) {
                    char lastChar = mComposing.charAt(mComposing.length() - 1);
                    if (getArrayIndex(CHUNJIIN_63_CODE_MAP, Character.getNumericValue(lastChar)) > -1) {
//                        if (mHandler != null)
//                            mHandler.removeCallbacksAndMessages(null);
                        //timerCancel(4);
                        KeyboardLogPrint.d("stop complete called 7");
                        if (mCompleteHandler != null)
                            mCompleteHandler.sendEmptyMessage(STOP_COMPLETE_TIMER);
                        mCompleteCount = 0;

                        mComposing.setLength(0);
                        mIsCompleteFromHandler = false;
                        kauto.setInitState();
                        mIsComplete = true;
                        kauto.FinishAutomataWithoutInput();
                        if (inputConnection != null)
                            inputConnection.finishComposingText();
                    }
                }
            }
            handleCharacter(primaryCode, keyCodes);
        }
/*
        String keyword = getEditStr();

        long beforeTime = SharedPreference.getLong(getApplicationContext(), Common.PREF_COUPANG_CLOSE_TIME);
        long currTime = System.currentTimeMillis();

        if ((currTime - beforeTime) > (60 * 60 * 1000)) {
            final String keywordText = keyword.trim();

            if (keywordText == null || keywordText.isEmpty()) {
                advo = null;
                beforeKeyword = null;
                mDialogHandler.sendEmptyMessage(HIDE_COUPANG_AD_DIALOG);

                return;
            }

            int val = mAttribute.imeOptions & (EditorInfo.IME_MASK_ACTION | EditorInfo.IME_FLAG_NO_ENTER_ACTION);

            boolean isExceptPackage = isExceptPackage();
            if (beforeKeyword != null && beforeKeyword.equals(keywordText)) {
                if (val == EditorInfo.IME_ACTION_SEARCH && !isExceptPackage) {
                    if (coupangdialog != null && coupangdialog.isShowing()) {
                        mDialogHandler.sendEmptyMessage(NOTI_COUPANG_AD_DIALOG);
                    } else {
                        mDialogHandler.sendEmptyMessage(SHOW_COUPANG_AD_DIALOG);
                    }
                }
                return;
            }

            if (!keywordText.isEmpty() && val == EditorInfo.IME_ACTION_SEARCH && !isExceptPackage) {
                showCoupangAd(keywordText);
            }
        }*/
    }
/*
    private void showCoupangAd(final String keywordText) {
        Matcher matcher = searcPattern.matcher(keywordText);

        CustomAsyncTask.cancel();

        if (!matcher.matches()) {
            mDialogHandler.sendEmptyMessage(HIDE_COUPANG_AD_DIALOG);
            return;
        }

        apiAsyncTask = new CustomAsyncTask(getApplicationContext());

        apiAsyncTask.getCoupangAd(keywordText, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {

                beforeKeyword = keywordText.trim();

                if (result) {
                    if (!TextUtils.isEmpty(obj.toString())) {
                        try {
                            JSONObject object = new JSONObject(obj.toString());
                            String landingUrl = object.optString("landingUrl");
                            JSONArray array = new JSONObject(obj.toString()).getJSONArray("productData");

                            if (array != null) {
                                advo = new CoupangAdPopup.AdVO();
                                advo.setTargetUrl(landingUrl);
                                int randomNum = (int) (Math.random() * 3);
                                boolean isFound = false;

                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject item = array.getJSONObject(i);

                                    String productName = item.optString("productName");
                                    advo.setTitle(productName);

                                    String productImage = item.optString("productImage");
                                    advo.setImgUrl(productImage);

                                    isFound = true;

                                    break;
                                }

                                String keyword = getEditStr();
                                if (keyword.trim().equals("")) {
                                    mDialogHandler.sendEmptyMessage(HIDE_COUPANG_AD_DIALOG);
                                } else if (isFound) {
                                    if (coupangdialog != null && coupangdialog.isShowing()) {
                                        mDialogHandler.sendEmptyMessage(NOTI_COUPANG_AD_DIALOG);
                                    } else {
                                        mDialogHandler.sendEmptyMessage(SHOW_COUPANG_AD_DIALOG);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            mDialogHandler.sendEmptyMessage(HIDE_COUPANG_AD_DIALOG);
                        }
                    } else {
                        mDialogHandler.sendEmptyMessage(HIDE_COUPANG_AD_DIALOG);
                    }
                } else {
                }
            }
        });
    }*/

    private int getArrayIndex(int[] target, int codeValue) {
        int findIndex = -1;
        for (int i = 0; i < target.length; i++) {
            if (target[i] == codeValue) {
                findIndex = i;
                break;
            }
        }

        return findIndex;
    }

    private static int tempcount = 0;

    public void onText(CharSequence text) {
        InputConnection ic = inputConnection;
        if (ic == null) return;
        ic.beginBatchEdit();
        if (mComposing.length() > 0)
            commitTyped(ic);
        KeyboardLogPrint.d("skkim null commitText 35 :: " + text);
        ic.commitText(text, 0);
        ic.endBatchEdit();
        updateShiftKeyState(getCurrentInputEditorInfo(), 2); // onText
    }

    /**
     * Update the list of available candidates from the current composing
     * text.  This will need to be filled in by however you are determining
     * candidates.
     */
    /* no candidate window
    private void updateCandidates() {
        if (!mCompletionOn) {
            if (mComposing.length() > 0) {
                ArrayList<String> list = new ArrayList<String>();
                list.add(mComposing.toString());
                setSuggestions(list, true, true);
            } else {
                setSuggestions(null, false, false);
            }
        }
    }

    public void setSuggestions(List<String> suggestions, boolean completions,
            boolean typedWordValid) {
        if (suggestions != null && suggestions.size() > 0) {
            setCandidatesViewShown(true);
        } else if (isExtractViewShown()) {
            setCandidatesViewShown(true);
        }
        if (mCandidateView != null) {
            mCandidateView.setSuggestions(suggestions, completions, typedWordValid);
        }
    }
    */

    // backspace 눌렀을 경우 동작 부
    private void handleBackspace() {
        KeyboardLogPrint.w("skkim handleBackspace ~~~");
//        if (mHandler != null)
//            mHandler.removeCallbacksAndMessages(null);
        //timerCancel(5);
        /*
        if (mDialogHandler != null)
            mDialogHandler.sendEmptyMessage(HIDE_COUPANG_AD_DIALOG);
*/
        KeyboardLogPrint.d("stop complete called 8");
        if (mCompleteHandler != null)
            mCompleteHandler.sendEmptyMessage(STOP_COMPLETE_TIMER);
        mCompleteCount = 0;

        if (!TextUtils.isEmpty(inputConnection.getSelectedText(0))) {
            KeyboardLogPrint.d("skkim null commitText 36 :: empty");
            inputConnection.commitText("", 1);
            return;
        }

        if (kauto.IsKoreanMode()) {
            KeyboardLogPrint.e("handleBackspace kauto.IsKoreanMode");
            if (isChunjiinKeyboard()) {
                mIsComplete = true;
                boolean removed = false; // 분해한 array에서 값이 remove 되었는지 유무
                if (mComposing.length() > 1) { // mComposing 문자열이 2글자 이상일 경우
                    boolean isCompolete = false;
                    String lStr = mComposing.substring(mComposing.length() - 1, mComposing.length());
                    String lastStr = "";
                    if (CharTables.isCompletedChar(lStr)) { // mComposing 마지막 문자열이 완성형 글자일 경우
                        isCompolete = true;
                        lastStr = mComposing.substring(mComposing.length() - 2, mComposing.length()); // lastStr에 마지막 2글자 저장
                    } else { // mComposing 마지막 문자열이 완성형 글자가 아닐 경우
                        isCompolete = false;
                        lastStr = lStr; // lastStr에 마지막 1글자 저장
                    }
                    ArrayList<Integer> codeArray = CharTables.getBackCodeArray(lastStr); // 마지막 1글자 or 2글자를 array 형태로 분해

                    if (codeArray != null && codeArray.size() > 0) { // 분해한 값이 존재할 경우
                        int lastCode = codeArray.get(codeArray.size() - 1); // 분해한 값의 최종 값
                        if (lastCode == 108 || lastCode == 109 || lastCode == 122) { // 분해한 값의 최종값이 모음 dot or ㅡ or ㅣ 일 경우
                            ArrayList<Integer> tempArray = new ArrayList<Integer>(); // 분해된 값들 중 연속적으로 나열된 모음들만 저장하는 array
                            ArrayList<Integer> reversArray = new ArrayList<Integer>();
                            Collections.reverse(codeArray); // 분해한 값이 담긴 array 를 reverse
                            for (int i = 0; i < codeArray.size(); i++) {
                                int code = codeArray.get(i);
                                // 연속적으로 계속 모음이 나올경우 모음들만 tempArray에 넣음
                                if (code == 108 || code == 109 || code == 122) {
                                    tempArray.add(code);
                                } else
                                    break;
                            }
                            Collections.reverse(tempArray);
                            int size = tempArray.size();
                            int removeCount = 0;
                            // 연속된 모음 값이 2개 일 경우
                            if (size == 2) {
                                // 연속된 모음이 ㅡ 와 ㅣ 일 경우 ㅣ 하나만 지워 줌 ex) 의 -> back key -> 으
                                if (tempArray.get(0) == 109 && tempArray.get(1) == 108) // ㅢ
                                    removeCount = 1;
                                else if (tempArray.get(0) == 108 && tempArray.get(1) == 122) // ㅏ
                                    removeCount = 2;
                                else if (tempArray.get(0) == 122 && tempArray.get(1) == 108) // ㅓ
                                    removeCount = 2;
                                else if (tempArray.get(0) == 122 && tempArray.get(1) == 109) // ㅗ
                                    removeCount = 2;
                                else if (tempArray.get(0) == 109 && tempArray.get(1) == 122) // ㅜ
                                    removeCount = 2;
                                // 연속된 모음이 3개 일 경우
                            } else if (size == 3) {
                                // 연속된 모음이 dot + ㅡ + ㅣ (ㅚ) 일 경우 back key를 누르면 ㅗ 가 되어야 하므로 마지막 ㅣ 하나만 지워야하므로 removeCount = 1
                                if (tempArray.get(0) == 122 && tempArray.get(1) == 109 && tempArray.get(2) == 108) // ㅚ
                                    removeCount = 1;
                                else if (tempArray.get(0) == 109 && tempArray.get(1) == 122 && tempArray.get(2) == 108) // ㅟ
                                    removeCount = 1;
                                else if (tempArray.get(0) == 108 && tempArray.get(1) == 122 && tempArray.get(2) == 122) // ㅑ
                                    removeCount = 3;
                                else if (tempArray.get(0) == 122 && tempArray.get(1) == 108 && tempArray.get(2) == 108) // ㅔ
                                    removeCount = 3;
                                else if (tempArray.get(0) == 122 && tempArray.get(1) == 122 && tempArray.get(2) == 109) // ㅛ
                                    removeCount = 3;
                                else if (tempArray.get(0) == 122 && tempArray.get(1) == 122 && tempArray.get(2) == 108) // ㅕ
                                    removeCount = 3;
                                else if (tempArray.get(0) == 109 && tempArray.get(1) == 122 && tempArray.get(2) == 122) // ㅠ
                                    removeCount = 3;
                                else if (tempArray.get(0) == 108 && tempArray.get(1) == 122 && tempArray.get(2) == 108) // ㅐ
                                    removeCount = 3;
                            } else if (size == 4) {
                                if (tempArray.get(0) == 122 && tempArray.get(1) == 109 && tempArray.get(2) == 108 && tempArray.get(3) == 122) // ㅘ
                                    removeCount = 2;
                                else if (tempArray.get(0) == 109 && tempArray.get(1) == 122 && tempArray.get(2) == 122 && tempArray.get(3) == 108) // ㅝ
                                    removeCount = 2;
                                else if (tempArray.get(0) == 108 && tempArray.get(1) == 122 && tempArray.get(2) == 122 && tempArray.get(3) == 108) // ㅒ
                                    removeCount = 4;
                                else if (tempArray.get(0) == 122 && tempArray.get(1) == 122 && tempArray.get(2) == 108 && tempArray.get(3) == 108) // ㅖ
                                    removeCount = 4;
                            } else if (size == 5) {
                                if (tempArray.get(0) == 122 && tempArray.get(1) == 109 && tempArray.get(2) == 108 && tempArray.get(3) == 122 && tempArray.get(4) == 108) // ㅙ
                                    removeCount = 3;
                                else if (tempArray.get(0) == 109 && tempArray.get(1) == 122 && tempArray.get(2) == 122 && tempArray.get(3) == 108 && tempArray.get(4) == 108)
                                    removeCount = 3;
                            }
                            if (removeCount > 0) { // removeCount > 0 일 경우. 즉 위 조건들 중 하나에 부합하는 경우
                                // removeCount 만큼 codeArray에서 값을 지움
                                for (int j = 0; j < removeCount; j++) {
                                    codeArray.remove(0);
                                }
                                // 분해한 array는 현재 reversed 된 상태이므로 이를 다시 원상 복구 함
                                Collections.reverse(codeArray);
                            } else { // 위 조건들 중 하나도 부합하지 않는 경우 codeArray에서 값을 1개만 지움
                                codeArray.remove(0);
                                // 분해한 array는 현재 reversed 된 상태이므로 이를 다시 원상 복구 함
                                Collections.reverse(codeArray);
                            }
                            removed = true;
                        } else if (isCanRotateChar(lastCode)) { // 분해한 값의 마지막 값이 모음 ( dot, ㅡ or ㅣ ) 이 아니고 순환값 (ㄱ, ㄴ, ㄷ, ㅂ, ㅅ, ㅈ, ㅇ ) 일 경우
                            // 그 이전에 동일한 값들이 연속적으로 있을경우 이값들을 모두 지워줌 ex ) ㄱ,ㅣ,ㄱ,ㄱ,ㄱ -> 긱 , 이때 back key를 눌렀을 때 마지막 3개의 ㄱ이 모두 array 에서 지워져야함
                            Collections.reverse(codeArray);
                            Iterator<Integer> it = codeArray.iterator();
                            while (it.hasNext()) {
                                int val = it.next();
                                if (val == lastCode)
                                    it.remove();
                                else
                                    break;
                            }
                            // 모두 지워지고 난 이후 revers 됬던 array를 다시 원상복구 함
                            Collections.reverse(codeArray);
                            // 이미 codeArray에서 값들이 remove 됬기 때문에 뒷쪽 로직에서 remove를 하지 않도록 removed 값을 true로 변경
                            removed = true;
                        }
                        int defVal = 0;

                        if (removed)
                            defVal = 0;
                        else
                            defVal = 1;

                        if (codeArray.size() > defVal) {
                            if (!removed) { // removed false 인경우는 위 조건( 마지막 배열값이 모음 or rotated가 아닌 경우에는 code array의 마지막 값을 remove 시켜야하므로 reverse 하여 마지막 하나를 지우고 다시 원상복구 해야함
                                Collections.reverse(codeArray);
                                codeArray.remove(0);
                                Collections.reverse(codeArray);
                            }
                            kauto.setInitState();
                            // codeArray값들을 automata를 돌려 문자열을 만들고 이를 기존 문자열의 마지막 2글자 혹은 1글자를 replace 시킴
                            String replaceVal = kauto.DecomposeConsonant(codeArray);
                            if (isCompolete) {
                                mComposing.replace(mComposing.length() - 2, mComposing.length(), "");
                            } else {
                                mComposing.replace(mComposing.length() - 1, mComposing.length(), "");
                            }
                            mComposing.append(replaceVal);
                            KeyboardLogPrint.d("skkim null mComposing 3 :: " + mComposing.toString());
                            inputConnection.setComposingText(mComposing, 1);
                            return;
                        } else {
                            kauto.setInitState();
                        }
                    }
                } else if (mComposing.length() == 1) {
                    String lastStr = mComposing.substring(mComposing.length() - 1, mComposing.length());
                    ArrayList<Integer> codeArray = CharTables.getBackCodeArray(lastStr);

                    if (codeArray != null && codeArray.size() > 0) {
                        int lastCode = codeArray.get(codeArray.size() - 1);
                        if (lastCode == 108 || lastCode == 109 || lastCode == 122) {
                            ArrayList<Integer> tempArray = new ArrayList<Integer>();
                            ArrayList<Integer> reversArray = new ArrayList<Integer>();
                            Collections.reverse(codeArray);
                            for (int i = 0; i < codeArray.size(); i++) {
                                int code = codeArray.get(i);
                                if (code == 108 || code == 109 || code == 122) {
                                    tempArray.add(code);
                                } else
                                    break;
                            }
                            Collections.reverse(tempArray);
                            int size = tempArray.size();
                            int removeCount = 0;
                            if (size == 2) {
                                if (tempArray.get(0) == 109 && tempArray.get(1) == 108) // ㅢ
                                    removeCount = 1;
                                else if (tempArray.get(0) == 108 && tempArray.get(1) == 122) // ㅏ
                                    removeCount = 2;
                                else if (tempArray.get(0) == 122 && tempArray.get(1) == 108) // ㅓ
                                    removeCount = 2;
                                else if (tempArray.get(0) == 122 && tempArray.get(1) == 109) // ㅗ
                                    removeCount = 2;
                                else if (tempArray.get(0) == 109 && tempArray.get(1) == 122) // ㅜ
                                    removeCount = 2;
                            } else if (size == 3) {
                                if (tempArray.get(0) == 122 && tempArray.get(1) == 109 && tempArray.get(2) == 108) // ㅚ
                                    removeCount = 1;
                                else if (tempArray.get(0) == 109 && tempArray.get(1) == 122 && tempArray.get(2) == 108) // ㅟ
                                    removeCount = 1;
                                else if (tempArray.get(0) == 108 && tempArray.get(1) == 122 && tempArray.get(2) == 122) // ㅑ
                                    removeCount = 3;
                                else if (tempArray.get(0) == 122 && tempArray.get(1) == 108 && tempArray.get(2) == 108) // ㅔ
                                    removeCount = 3;
                                else if (tempArray.get(0) == 122 && tempArray.get(1) == 122 && tempArray.get(2) == 109) // ㅛ
                                    removeCount = 3;
                                else if (tempArray.get(0) == 122 && tempArray.get(1) == 122 && tempArray.get(2) == 108) // ㅕ
                                    removeCount = 3;
                                else if (tempArray.get(0) == 109 && tempArray.get(1) == 122 && tempArray.get(2) == 122) // ㅠ
                                    removeCount = 3;
                                else if (tempArray.get(0) == 108 && tempArray.get(1) == 122 && tempArray.get(2) == 108) // ㅐ
                                    removeCount = 3;
                            } else if (size == 4) {
                                if (tempArray.get(0) == 122 && tempArray.get(1) == 109 && tempArray.get(2) == 108 && tempArray.get(3) == 122) // ㅘ
                                    removeCount = 2;
                                else if (tempArray.get(0) == 109 && tempArray.get(1) == 122 && tempArray.get(2) == 122 && tempArray.get(3) == 108) // ㅝ
                                    removeCount = 2;
                                else if (tempArray.get(0) == 108 && tempArray.get(1) == 122 && tempArray.get(2) == 122 && tempArray.get(3) == 108) // ㅒ
                                    removeCount = 4;
                                else if (tempArray.get(0) == 122 && tempArray.get(1) == 122 && tempArray.get(2) == 108 && tempArray.get(3) == 108) // ㅖ
                                    removeCount = 4;
                            } else if (size == 5) {
                                if (tempArray.get(0) == 122 && tempArray.get(1) == 109 && tempArray.get(2) == 108 && tempArray.get(3) == 122 && tempArray.get(4) == 108) // ㅙ
                                    removeCount = 3;
                                else if (tempArray.get(0) == 109 && tempArray.get(1) == 122 && tempArray.get(2) == 122 && tempArray.get(3) == 108 && tempArray.get(4) == 108)
                                    removeCount = 3;
                            }

                            if (removeCount > 0) {
                                for (int j = 0; j < removeCount; j++) {
                                    codeArray.remove(0);
                                }
                                Collections.reverse(codeArray);
                            } else {
                                codeArray.remove(0);
                                Collections.reverse(codeArray);
                            }

                            removed = true;
                        } else if (isCanRotateChar(lastCode)) {
                            Collections.reverse(codeArray);
                            Iterator<Integer> it = codeArray.iterator();
                            while (it.hasNext()) {
                                int val = it.next();
                                if (val == lastCode)
                                    it.remove();
                                else
                                    break;
                            }
                            Collections.reverse(codeArray);

                            removed = true;
                        }
                        int defVal = 0;
                        if (removed)
                            defVal = 0;
                        else
                            defVal = 1;
                        if (codeArray.size() > defVal) {
                            if (!removed) {
                                Collections.reverse(codeArray);
                                codeArray.remove(0);
                                Collections.reverse(codeArray);
                            }
                            kauto.setInitState();
                            String replaceVal = kauto.DecomposeConsonant(codeArray);
                            mComposing.replace(mComposing.length() - 1, mComposing.length(), "");
                            mComposing.append(replaceVal);
                            KeyboardLogPrint.d("skkim null mComposing 4 :: " + mComposing.toString());
                            inputConnection.setComposingText(mComposing, 1);

                            return;
                        } else {
                            kauto.setInitState();
                        }
                    }
                }
            } else {
                int ret = kauto.DoBackSpace();
                LogPrint.d("handleBackspace ret :: " + ret);
                if (ret == KoreanAutomata.ACTION_ERROR) {
                    updateShiftKeyState(getCurrentInputEditorInfo(), 3); // backpressed not chunjiin action_error
                    return;
                }

                if ((ret & KoreanAutomata.ACTION_UPDATE_COMPOSITIONSTR) != 0) {
                    LogPrint.d("handleBackspace 1");
                    if (!TextUtils.isEmpty(kauto.GetCompositionString())) {
                        LogPrint.d("handleBackspace kauto.GetCompositionString() :: " + kauto.GetCompositionString());
                        // mComposing.setLength(0);
                        LogPrint.d("handleBackspace mComposing.length() :: " + mComposing.length());
                        if (mComposing.length() > 0) {
                            mComposing.replace(mComposing.length() - 1, mComposing.length(), kauto.GetCompositionString());
                            KeyboardLogPrint.d("skkim null mComposing 5 :: " + mComposing.toString());
                            inputConnection.setComposingText(mComposing, 1);
                        }
                        updateShiftKeyState(getCurrentInputEditorInfo(), 4); // backpressed not chunjiin pompositionString is not empty

                        if (isChunjiinKeyboard()) {
                            if (mComposing.length() == 0) {
                                kauto.setInitState();
                                kauto.FinishAutomataWithoutInput();
                            }
                        }

                        return;
                    }
                }
            }
        } else {
            KeyboardLogPrint.e("handleBackspace kauto.IsKoreanMode not");
        }

        final int length = mComposing.length();
        KeyboardLogPrint.e("handleBackspace length :: " + length);
        if (length > 1) {
            mComposing.delete(length - 1, length);
            if (mComposing != null)
                KeyboardLogPrint.d("handleBackspace mComposing :: " + mComposing.toString());
            else
                KeyboardLogPrint.d("handleBackspace mComposing null");
            KeyboardLogPrint.d("skkim null mComposing 6 :: " + mComposing.toString());
            inputConnection.setComposingText(mComposing, 1);
            // updateCandidates();
        } else if (length > 0) {
            mComposing.setLength(0);
            if (isChunjiinKeyboard()) {
//                keyDownUp(KeyEvent.KEYCODE_DEL);
                KeyboardLogPrint.d("skkim null commitText 1 :: empty");
                inputConnection.commitText("", 0);
            } else {
                KeyboardLogPrint.d("skkim null commitText 2 :: empty");
                inputConnection.commitText("", 0);
            }
            // updateCandidates();
        } else {
            KeyboardLogPrint.d("handleBackspace else");
            if (isChunjiinKeyboard()) {
                KeyboardLogPrint.d("handleBackspace else chunjiin");
                if (mComposing.length() == 0) {
                    KeyboardLogPrint.d("handleBackspace else chunjiin composing length == 0 ");
                    kauto.setInitState();
                    kauto.FinishAutomataWithoutInput();
                }
            }
//            keyDownUp(KeyEvent.KEYCODE_DEL);
            //inputConnection = getCurrentInputConnection();
            CharSequence charBeforecursor = inputConnection.getTextBeforeCursor(1, 0);
            if (isEmoji(charBeforecursor)) {
                KeyboardLogPrint.d("handleBackspace else is emoji");
                if (mMainKeyboardView != null && mMainKeyboardView.isSearchVisible())
                    delete();
                else
                    keyDownUp(KeyEvent.KEYCODE_DEL);
            } else {
                KeyboardLogPrint.d("handleBackspace else is not emoji");
                if (mCurKeyboard == mNumberOnlyKeyboard || mCurKeyboard == mNumberSignedKeyboard || mCurKeyboard == mNumberDecimalKeyboard || mCurKeyboard == mNumberPhoneKeyboard || mCurKeyboard == mNumberKeyboard) {
                    KeyboardLogPrint.i("handleBackspace else is not emoji, cur is num");
                    if (mMainKeyboardView != null && mMainKeyboardView.isSearchVisible())
                        delete();
                    else
                        keyDownUp(KeyEvent.KEYCODE_DEL);
                } else {
                    KeyboardLogPrint.i("handleBackspace else is not emoji, cur is not num");
                    delete();
                }
//                delete();
            }
        }
        if (mCurKeyboard == mEQwerty || mCurKeyboard == mQwertyNum || mCurKeyboard == mQwertyNum35) {
            if (mShiftState != SHIFT_STATE_ONLY_UPPER_CASE) {
                KeyboardLogPrint.e("backpressed shift, shift state is not upper only");
                updateShiftKeyState(getCurrentInputEditorInfo(), 5); //backpressed
            } else {
                KeyboardLogPrint.e("backpressed shift, shift state is upper only");
            }
        } else {
            KeyboardLogPrint.e("backpressed shift, shift state is not qwerty");
            updateShiftKeyState(getCurrentInputEditorInfo(), 5); //backpressed
        }
    }

    public void recoverInputConnection() {
        if (inputConnection != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                inputConnection.closeConnection();
            } else {
                inputConnection.finishComposingText();
            }
            inputConnection = getCurrentInputConnection();
            mCurKeyboard.setEnter(getResources(), false);
        }
    }

    public void setInputConnection(InputConnection input, EditorInfo info) {
        try {
            if (inputConnection != null) {
                mCurKeyboard.setEnter(getResources(), true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    inputConnection.closeConnection();
                } else {
                    inputConnection.finishComposingText();
                }
                inputConnection = input;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // shift key 눌렀을 경우 동작 부
    private void handleShift() {
        KeyboardLogPrint.w("skkimmm shift SoftKeyboard handleShift");
        if (mMainKeyboardView == null)
            return;
        WindowManager windowService = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int currentRatation = windowService.getDefaultDisplay().getRotation();
        Keyboard currentKeyboard = mMainKeyboardView.getKeyboard();
        if (mEQwerty == currentKeyboard || mQwertyNum == currentKeyboard || mQwertyNum35 == currentKeyboard) {
            KeyboardLogPrint.e("skkimmm shift handleShift currentKeyboard is qwerty");
            // Alphabet keyboard
//            if ( mShiftState == SHIFT_STATE_LOWER_CASE || mShiftState == SHIFT_STATE_ONLY_UPPER_CASE ) // 0일 경우 소문자 -> 대문자 변경, 2일 경우 대문자 -> 소문자 변경, 1일 경우에는 대문자 -> 대문자 고정이므로 shift 되지 않음
            checkToggleCapsLock();
            KeyboardLogPrint.e("skkimmm shift handleShift mCapsLock :: " + mCapsLock + " , mainkeyboardview isShifted :: " + mMainKeyboardView.isShifted());
            mMainKeyboardView.setShifted(mCapsLock || !mMainKeyboardView.isShifted(), 4);
            KeyboardLogPrint.e("skkimmm shift setShift, handleShift 현재 키보드 쿼티, mCapsLock : " + mCapsLock + " main keyboard shift : " + mMainKeyboardView.isShifted());
        }
        // add Korean Keyboards
        else if (currentKeyboard == mKoreanKeyboard) {
            KeyboardLogPrint.e("skkimmm shift handleShift currentKeyboard is korean");
            setKeyBoardMode(true, -1, 8);
            checkToggleKCapsLock();
            mMainKeyboardView.setShifted(mKCapsLock || !mMainKeyboardView.isShifted(), 5);
            KeyboardLogPrint.e("skkimmm shift setShift, handleShift 현재 키보드 한글, mKCapsLock : " + mKCapsLock + " main keyboard shift : " + mMainKeyboardView.isShifted());
//            mKoreanKeyboard.setShifted(true);
//            mMainKeyboardView.setKeyboard(mKoreanShiftedKeyboard);
//            mKoreanShiftedKeyboard.setShifted(true);

            if (mKoreanKeyboardMode == Common.MODE_CHUNJIIN || mKoreanKeyboardMode == Common.MODE_NARA || mKoreanKeyboardMode == Common.MODE_CHUNJIIN_PLUS) {
                if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation)
                    ((LatinKeyboardView) mMainKeyboardView.getKeyboardView()).setKeyboardMode(Common.MODE_QUERTY);
            }

        }
        // end of Korean keyboard care..
        else if (currentKeyboard == mSymbolsKeyboard || currentKeyboard == mSymbolsShiftedKeyboard || currentKeyboard == mSymbolsShiftedKeyboard1) {
            mSymbolsKeyboard.setShifted(true);
            KeyboardLogPrint.e("skkimmm shift setShift, handleShift 현재 키보드 심볼");
            setKeyBoardMode(false, NUM_KEYBOARD, 9);
            mMainKeyboardView.setKeyboard(mNumberKeyboard);
            mNumberKeyboard.setShifted(true);
            KeyboardLogPrint.e("skkimmm shift setShift, handleShift 현재 키보드 심볼1");
        } else if (currentKeyboard == mNumberKeyboard) {
            mNumberKeyboard.setShifted(false);
            KeyboardLogPrint.e("skkimmm shift setShift, handleShift 현재 키보드 숫자");
            setKeyBoardMode(false, SYMBOL_KEYBOARD, 10);
            mMainKeyboardView.setKeyboard(mSymbolsKeyboard);
            mSymbolsKeyboard.setShifted(false);
            KeyboardLogPrint.e("skkimmm shift setShift, handleShift 현재 키보드 숫자1");
        }
    }

    private void handleCharacter(int primaryCode, int[] keyCodes) {
        if (mMainKeyboardView != null && isChunjiinKeyboard())
            mIsComplete = false;
        int keyState = InputTables.KEYSTATE_NONE;
        if (isInputViewShown()) {
            if (mMainKeyboardView.isShifted()) {
                primaryCode = Character.toUpperCase(primaryCode);
                keyState |= InputTables.KEYSTATE_SHIFT;
            }
        }
        // for h/w keyboard....
        LogPrint.d("skkim handleCharacter mHwShift :: " + mHwShift + " , mHwCapsLock :: " + mHwCapsLock);
        if (mHwShift)
            keyState |= InputTables.KEYSTATE_SHIFT;
        LogPrint.d("skkimmm shift kauto.IsKoreanMode() :: " + kauto.IsKoreanMode());
        if (mHwCapsLock && isAlphabet(primaryCode) && !kauto.IsKoreanMode()) { // 한글 키보드일 경우에는 cap lock의 영향을 받으면 안된다.
            if (mHwShift) {
                primaryCode = Character.toLowerCase(primaryCode);
            } else {
                primaryCode = Character.toUpperCase(primaryCode);
                keyState |= InputTables.KEYSTATE_SHIFT;
            }
        } else {

        }
        KeyboardLogPrint.d("handleCharactor primaryCode :: " + primaryCode);
//        if (isAlphabet(primaryCode) && mPredictionOn ) {
        if (isAlphabet(primaryCode)) {
            KeyboardLogPrint.w("chunjiin symbol");
//            if (cAutomata.mPState != 0)
//                sendDownAndUpKeyEvent(KeyEvent.KEYCODE_DEL, 0);
            int ret = kauto.DoAutomata((char) primaryCode, keyState);
            if (mMainKeyboardView != null) {
                Keyboard keyboard = mMainKeyboardView.getKeyboard();
                if (isChunjiinKeyboard() || keyboard == mDanKeyboard || keyboard == mDanKeyboard35) {
                    KeyboardLogPrint.w("timer start");
                    if (mCompleteHandler != null) {
                        mCompleteHandler.sendEmptyMessage(START_COMPLETE_TIMER);
                    }
                    /**
                     if ( mCompleteHandler != null ) {
                     mCompleteHandler.post(new Runnable() {
                    @Override public void run() {
                    mTimer = new Timer();
                    mTimer.scheduleAtFixedRate(new CompleteTask(), 0, 300);
                    }
                    });
                     }**/

//                    mTimer .schedule(new CompleteTask(), 1000);


//                    if (mHandler != null) {
//                        mHandler.removeCallbacksAndMessages(null);
//                        mHandler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                InputConnection ic = getCurrentInputConnection();
//                                if (ic != null && isInputViewShown()) {
//                                    ExtractedText et = ic.getExtractedText(new ExtractedTextRequest(), 0);
//                                    if (et != null) {
//                                        mAutomataValues = kauto.getAutomataValue();
//                                        kauto.setInitState();
//                                        kauto.FinishAutomataWithoutInput();
//                                        mComposing.setLength(0);
//                                        ic.finishComposingText();
//                                        mIsComplete = true;
//                                        mIsCompleteFromHandler = true;
//                                        CharSequence seq = et.text;
//                                        String temp = seq.toString();
//                                        String[] temps = temp.split(" ");
//                                        if (temps.length > 0) {
//                                            String tStr = removedEmojiStr(temps[0]);
//                                            if ( !TextUtils.isEmpty(tStr))
//                                            {
//                                                mADStr = tStr;
//                                            }
//                                            KeyboardLogPrint.w("입력창의 문자열 첫단어 " + mADStr + " 로 광고 요청");
////                                connectGetAD(getApplicationContext(), mADStr);
//                                            if (setKeywordAdd(mADStr, false)) {
//                                                KeyboardLogPrint.w("setKeywordAdd true");
////                                                connectGetAD(getApplicationContext(), getKeywordList());
//                                                connectGetAD(getApplicationContext(), mADStr);
//                                            } else
//                                                KeyboardLogPrint.w("setKeywordAdd false");
//
//                                        }
//                                    }
//                                }
//                            }
//                        }, 1400);
//                    }
                } else {
                    // 20180430 이모티콘 제거
//                    mOTimer = new Timer(); ///
//                    mOTimer.schedule(new OCompleteTask(), 1000); ///
                }
            }

            if (ret < 0) {
                if (kauto.IsKoreanMode()) {
                    kauto.ToggleMode();
                }
            } else {
                int variation;

                if (mAttribute != null) {
                    variation = mAttribute.inputType & EditorInfo.TYPE_MASK_VARIATION;
                } else {
                    variation = -1000;
                }
                KeyboardLogPrint.e("variation :: " + variation);
                if (!kauto.IsKoreanMode() && (variation == EditorInfo.TYPE_TEXT_VARIATION_PASSWORD)) {
                    if (mComposing.length() > 0) {
                        KeyboardLogPrint.d("skkim null commitText 3 :: " + mComposing.toString());
                        inputConnection.commitText(mComposing, 1);
                        mComposing.setLength(0);
                    }
                    kauto.FinishAutomataWithoutInput();
                    KeyboardLogPrint.d("skkim null commitText 4 :: " + String.valueOf((char) primaryCode));
                    inputConnection.commitText(String.valueOf((char) primaryCode), 1);
                    updateShiftKeyState(getCurrentInputEditorInfo(), 6); // handleCharactor 한글아니면서 비밀번호
                    return;
                } else {
                    if ((ret & ChunjiinAutomata.ACTION_REMOVE_PREV_CHAR) != 0) {
                        if (mComposing.length() > 0) {
                            mComposing.replace(mComposing.length() - 1, mComposing.length(), "");
                        }
                    }

                    if ((ret & KoreanAutomata.ACTION_UPDATE_COMPLETESTR) != 0) {
                        if (mComposing.length() > 0) {
                            mComposing.replace(mComposing.length() - 1, mComposing.length(), kauto.GetCompleteString());
                        } else {
                            mComposing.append(kauto.GetCompleteString());
                        }
                        if (mComposing.length() > 0) {
                            KeyboardLogPrint.d("skkim null mComposing 7 :: " + mComposing.toString());
                            inputConnection.setComposingText(mComposing, 1);
                            if (kauto.IsKoreanMode() && (variation == EditorInfo.TYPE_TEXT_VARIATION_PASSWORD)
                                    && (!kauto.GetCompleteString().equalsIgnoreCase(""))) {
                                KeyboardLogPrint.d("skkim null commitText 5 :: " + mComposing.toString());
                                inputConnection.commitText(mComposing, 1);
                                mComposing.setLength(0);
                            }
                        }
                    }

                    if ((ret & ChunjiinAutomata.ACTION_REMAKECHAR) != 0) // 천지인에서 각. 다음 ㅣ가 왔을 때 '각.' 을 '가'로 바꿔줘야함
                    {
                        if (mComposing.length() > 1) {
                            mComposing.replace(mComposing.length() - 2, mComposing.length(), kauto.GetCompleteString());
                            mComposing.append(kauto.GetCompositionString());
                        } else
                            mComposing.append(kauto.GetCompleteString());
                        KeyboardLogPrint.d("skkim null mComposing 8 :: " + mComposing.toString());
                        inputConnection.setComposingText(mComposing, 1);
                        if (mComposing.length() > 1) {
                            KeyboardLogPrint.d("skkim null mComposing 9 :: " + mComposing.toString());
                            inputConnection.setComposingText(mComposing, 1);
                            if (kauto.IsKoreanMode() && (variation == EditorInfo.TYPE_TEXT_VARIATION_PASSWORD)
                                    && (!kauto.GetCompleteString().equalsIgnoreCase(""))) {
                                KeyboardLogPrint.d("skkim null commitText 6 :: " + mComposing.toString());
                                inputConnection.commitText(mComposing, 1);
                                mComposing.setLength(0);
                            }
                        }
                    }

                    if ((ret & KoreanAutomata.ACTION_UPDATE_COMPOSITIONSTR) != 0) {
                        if ((mComposing.length() > 0) && ((ret & KoreanAutomata.ACTION_UPDATE_COMPLETESTR) == 0) && ((ret & KoreanAutomata.ACTION_APPEND) == 0)) {
                            mComposing.replace(mComposing.length() - 1, mComposing.length(), kauto.GetCompositionString());
                        } else
                            mComposing.append(kauto.GetCompositionString());

                        String ss = mComposing.toString();
                        KeyboardLogPrint.d("skkim null mComposing 10 :: " + ss);
                        inputConnection.setComposingText(ss, 1);
                    }


                    if ((ret & ChunjiinAutomata.ACTION_MAKE_VOWEL) != 0) // 천지인일 경우 . 단자음 + dot 조합에서 모음 들어왔을 경우
                    {
                        try {
                            if (mComposing.length() > 0) {
                                mComposing.replace(mComposing.length() - kauto.getStrLength(), mComposing.length(), "");
                                mComposing.append(kauto.GetCompositionString());
                                KeyboardLogPrint.d("skkim null mComposing 11 :: " + mComposing.toString());
                                inputConnection.setComposingText(mComposing, 1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            if ((ret & KoreanAutomata.ACTION_USE_INPUT_AS_RESULT) != 0) {
                mComposing.append((char) primaryCode);
                KeyboardLogPrint.d("skkim null mComposing 12 :: " + mComposing.toString());
                inputConnection.setComposingText(mComposing, 1);
            }
            if ((mMainKeyboardView.getKeyboard() == mEQwerty && mShiftState == SHIFT_STATE_ONLY_UPPER_CASE)
                    || (mMainKeyboardView.getKeyboard() == mQwertyNum && mShiftState == SHIFT_STATE_ONLY_UPPER_CASE)
                    || (mMainKeyboardView.getKeyboard() == mQwertyNum35 && mShiftState == SHIFT_STATE_ONLY_UPPER_CASE)) {

            } else {
                updateShiftKeyState(getCurrentInputEditorInfo(), 7); // handleCharactor alphabet, 쿼티 영문키보드가 아니고 대문자 고정이아닐 경우
            }

            // updateCandidates();
        } else {
            if (mMainKeyboardView.getKeyboard() == mNumberKeyboard) {
                if (primaryCode == 8230) // symbol shift keyboard의 .,-/ 버튼 눌렀을 경우 동작부
                {
                    kauto.FinishAutomataWithoutInput();

                    if ("".equals(mRotatedVal1)) {
                        if (mComposing.length() > 0) {
                            KeyboardLogPrint.d("skkim null commitText 7 :: " + mComposing.toString());
                            inputConnection.commitText(mComposing, 1);
                            mComposing.setLength(0);
                        }
                        mRotatedVal1 = getNextChar(mRotatedVal1);
                        mComposing.append(mRotatedVal1);
                        KeyboardLogPrint.d("skkim null mComposing 13 :: " + mComposing.toString());
                        inputConnection.setComposingText(mComposing, 1);
                    } else if (".,-/".contains(mRotatedVal1)) {
                        if (mComposing.length() > 0) {
                            mComposing.replace(mComposing.length() - 1, mComposing.length(), "");
                        }
                        mRotatedVal1 = getNextChar(mRotatedVal1);
                        mComposing.append(mRotatedVal1);
                        KeyboardLogPrint.d("skkim null mComposing 14 :: " + mComposing.toString());
                        inputConnection.setComposingText(mComposing, 1);
                    }
                } else if (primaryCode == 10000) {
                    kauto.FinishAutomataWithoutInput();

                    Intent intent = new Intent(this, KeyboardSettingsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else if (primaryCode == -226) {
                    mMainKeyboardView.selectKeyboard(MainKeyboardView.GUBUN_EMOTICON);
                    setKeyBoardMode(false, -1, 0);
                } else {
                    if (mComposing.length() > 0) {
                        KeyboardLogPrint.d("skkim null commitText 8 :: " + mComposing.toString());
                        inputConnection.commitText(mComposing, 1);
                        mComposing.setLength(0);
                    }
                    kauto.FinishAutomataWithoutInput();
                    KeyboardLogPrint.d("skkim null commitText 9 :: " + String.valueOf((char) primaryCode));
                    inputConnection.commitText(String.valueOf((char) primaryCode), 1);
                }
            } else if (mMainKeyboardView.getKeyboard() == mNumberOnlyKeyboard || mMainKeyboardView.getKeyboard() == mNumberSignedKeyboard
                    || mMainKeyboardView.getKeyboard() == mNumberDecimalKeyboard) {
                if (primaryCode == 10000) {
                    kauto.FinishAutomataWithoutInput();

                    Intent intent = new Intent(this, KeyboardSettingsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    if (mComposing.length() > 0) {
                        KeyboardLogPrint.d("skkim null commitText 10 :: " + mComposing.toString());
                        inputConnection.commitText(mComposing, 1);
                        mComposing.setLength(0);
                    }
                    kauto.FinishAutomataWithoutInput();
                    KeyboardLogPrint.d("skkim null commitText 11 :: " + String.valueOf((char) primaryCode));
                    inputConnection.commitText(String.valueOf((char) primaryCode), 1);
                }
            } else if (mMainKeyboardView.getKeyboard() == mNumberPhoneKeyboard) {
                if (primaryCode == 10000) {
                    kauto.FinishAutomataWithoutInput();

                    Intent intent = new Intent(this, KeyboardSettingsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else if (primaryCode == 12000) {
                    mPhoneSymbolKeyboard.setShifted(true);
                    KeyboardLogPrint.e("setShift, handlecharactor not alphabet keyboard number phone primaryCode 12000");
                    setKeyBoardMode(false, PHONE_SYMBOL, 1);
                    mMainKeyboardView.setKeyboard(mPhoneSymbolKeyboard);
                } else {
                    if (mComposing.length() > 0) {
                        KeyboardLogPrint.d("skkim null commitText 12 :: " + mComposing.toString());
                        inputConnection.commitText(mComposing, 1);
                        mComposing.setLength(0);
                    }
                    kauto.FinishAutomataWithoutInput();
                    KeyboardLogPrint.d("skkim null commitText 13 :: " + String.valueOf((char) primaryCode));
                    inputConnection.commitText(String.valueOf((char) primaryCode), 1);
                }
            } else if (mMainKeyboardView.getKeyboard() == mPhoneSymbolKeyboard) {
                if (primaryCode == 10000) {
                    kauto.FinishAutomataWithoutInput();

                    Intent intent = new Intent(this, KeyboardSettingsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else if (primaryCode == 12000) {
                    setKeyBoardMode(false, NUM_KEYBOARD, 2);
                    mMainKeyboardView.setKeyboard(mNumberPhoneKeyboard);
                } else {
                    if (mComposing.length() > 0) {
                        KeyboardLogPrint.d("skkim null commitText 14 :: " + mComposing.toString());
                        inputConnection.commitText(mComposing, 1);
                        mComposing.setLength(0);
                    }
                    kauto.FinishAutomataWithoutInput();
                    KeyboardLogPrint.d("skkim null commitText 15 :: " + String.valueOf((char) primaryCode));
                    inputConnection.commitText(String.valueOf((char) primaryCode), 1);
                }
            } else if (mMainKeyboardView.getKeyboard() == mNaraKeyboard) {
                if (primaryCode == 8231) // 나랏글 keyboard의 .? 버튼 눌렀을 경우 동작부
                {
                    kauto.FinishAutomataWithoutInput();
                    if ("".equals(mRotatedVal2)) {
                        if (mComposing.length() > 0) {
                            KeyboardLogPrint.d("skkim null commitText 16 :: " + mComposing.toString());
                            inputConnection.commitText(mComposing, 1);
                            mComposing.setLength(0);
                        }
                        mRotatedVal2 = getNaraNextChar(mRotatedVal2);
                        mComposing.append(mRotatedVal2);
                        KeyboardLogPrint.d("skkim null mComposing 15 :: " + mComposing.toString());
                        inputConnection.setComposingText(mComposing, 1);
                    } else if (".?".contains(mRotatedVal2)) {
                        if (mComposing.length() > 0) {
                            mComposing.replace(mComposing.length() - 1, mComposing.length(), "");
                        }
                        mRotatedVal2 = getNaraNextChar(mRotatedVal2);
                        mComposing.append(mRotatedVal2);
                        KeyboardLogPrint.d("skkim null mComposing 16 :: " + mComposing.toString());
                        inputConnection.setComposingText(mComposing, 1);
                    }
                } else if (primaryCode == -226) {
                    mMainKeyboardView.selectKeyboard(MainKeyboardView.GUBUN_EMOTICON);
                    setKeyBoardMode(false, -1, 3);
                } else {
                    if (mComposing.length() > 0) {
                        KeyboardLogPrint.d("skkim null commitText 17 :: " + mComposing.toString());
                        inputConnection.commitText(mComposing, 1);
                        mComposing.setLength(0);
                    }
                    kauto.FinishAutomataWithoutInput();
                    KeyboardLogPrint.d("skkim null commitText 18 :: " + String.valueOf((char) primaryCode));
                    inputConnection.commitText(String.valueOf((char) primaryCode), 1);
                }
            } else {
                if (primaryCode == -900) {
                    if (mMainKeyboardView.getKeyboard() == mSymbolsKeyboard) {
                        setKeyBoardMode(false, -1, 4);
                        mMainKeyboardView.setKeyboard(mSymbolsShiftedKeyboard);
                    } else if (mMainKeyboardView.getKeyboard() == mSymbolsShiftedKeyboard) {
                        setKeyBoardMode(false, -1, 5);
                        mMainKeyboardView.setKeyboard(mSymbolsShiftedKeyboard1);
                    } else if (mMainKeyboardView.getKeyboard() == mSymbolsShiftedKeyboard1) {
                        setKeyBoardMode(false, SYMBOL_KEYBOARD, 6);
                        mMainKeyboardView.setKeyboard(mSymbolsKeyboard);
                    } else if (mMainKeyboardView.getKeyboard() == mQSymbolsKeyboard) {
                        setKeyBoardMode(false, -1, 7);
                        mMainKeyboardView.setKeyboard(mQSymbolsKeyboard1);
                    } else if (mMainKeyboardView.getKeyboard() == mQSymbolsKeyboard1) {
                        setKeyBoardMode(false, -1, 8);
                        mMainKeyboardView.setKeyboard(mQSymbolsKeyboard);
                    } else if (mMainKeyboardView.getKeyboard() == mQSymbolsKeyboard_35) {
                        setKeyBoardMode(false, -1, 7);
                        mMainKeyboardView.setKeyboard(mQSymbolsKeyboard1_35);
                    } else if (mMainKeyboardView.getKeyboard() == mQSymbolsKeyboard1_35) {
                        setKeyBoardMode(false, -1, 8);
                        mMainKeyboardView.setKeyboard(mQSymbolsKeyboard_35);
                    }
                } else if (primaryCode == -226) {
                    if (mMainKeyboardView.getKeyboard() == mSejongPlusKeyboard) {
                        if (mComposing.length() > 0) {
                            KeyboardLogPrint.d("skkim null commitText 19 :: " + mComposing.toString());
                            inputConnection.commitText(mComposing, 1);
                            kauto.FinishAutomataWithoutInput();
                            mComposing.setLength(0);
                        }
                        KeyboardLogPrint.d("skkim null commitText 20 :: 쉼표");
                        inputConnection.commitText(",", 1);
                    } else {
                        mMainKeyboardView.selectKeyboard(MainKeyboardView.GUBUN_EMOTICON);
                        setKeyBoardMode(false, -1, 7);
                    }
                } else if (primaryCode == 12322) { // 키보드의 이모티콘 키보드 이동 버튼
                    if (mComposing.length() > 0) {
                        KeyboardLogPrint.d("skkim null commitText 21 :: " + mComposing.toString());
                        inputConnection.commitText(mComposing, 1);
                        kauto.FinishAutomataWithoutInput();
                        mComposing.setLength(0);
                    }
                    KeyboardLogPrint.d("skkim null commitText 22 :: \u263A");
                    inputConnection.commitText("\u263A", 1);
                } else {
                    if (mMainKeyboardView.getKeyboard() == mEQwerty || mMainKeyboardView.getKeyboard() == mQwertyNum
                            || mMainKeyboardView.getKeyboard() == mQwertyNum35
                            || mMainKeyboardView.getKeyboard() == mQKoreanKeyboard
                            || mMainKeyboardView.getKeyboard() == mQKoreanKeyboard35) {
                        if ((primaryCode >= 48 && primaryCode <= 57) || primaryCode == 34 || primaryCode == 35 || primaryCode == 94 || primaryCode == 126) {
                            if (mMainKeyboardView.getKeyboard() == mEQwerty
                                    || mMainKeyboardView.getKeyboard() == mQwertyNum
                                    || mMainKeyboardView.getKeyboard() == mQwertyNum35) {
                                if (mShiftState == SHIFT_STATE_LOWER_CASE || mShiftState == SHIFT_STATE_UPPER_CASE) {
                                    KeyboardLogPrint.e("handleCharactor alphabet 아니고 키보드가 그외 어떤 상황에서인지 쿼티, 쿼티 한글인 경우이면서 글자키 인 경우, shift state 가 대문 혹은 소문자");
                                    updateShiftKeyState(getCurrentInputEditorInfo(), 8); // handleCharactor alphabet 아니고 키보드가 그외 어떤 상황에서인지 쿼티, 쿼티 한글인 경우이면서 글자키 인 경우
                                } else
                                    KeyboardLogPrint.e("handleCharactor alphabet 아니고 키보드가 그외 어떤 상황에서인지 쿼티, 쿼티 한글인 경우이면서 글자키 인 경우, shift state 가 대문자 고정");
                            } else {
                                KeyboardLogPrint.e("handleCharactor alphabet 아니고 키보드가 그외 어떤 상황에서인지 쿼티, 쿼티 한글인 경우이면서 글자키 인 경우");
                                updateShiftKeyState(getCurrentInputEditorInfo(), 8); // handleCharactor alphabet 아니고 키보드가 그외 어떤 상황에서인지 쿼티, 쿼티 한글인 경우이면서 글자키 인 경우
                            }
                        } else {
//                            if ( primaryCode == 34 || primaryCode == 35 || primaryCode == 94 || primaryCode == 126 ) { // 쿼티 한글, 영문의 경우 shift 한 상태에서 해당키(롱클릭키 ^, #, ^, ")들도 shift가 풀려야함.
//                                KeyboardLogPrint.e("handleCharactor alphabet 아니고 키보드가 그외 어떤 상황에서인지 쿼티, 쿼티 한글인 경우이면서 글자키 아닌경우이나 예외로 shift 동작해야 할 경우");
//                                updateShiftKeyState(getCurrentInputEditorInfo(), 112); // handleCharactor alphabet 아니고 키보드가 그외 어떤 상황에서인지 쿼티, 쿼티 한글인 경우이면서 글자키 아닌경우이나 예외로 shift 동작해야 할 경우
//                            } else {
//                                KeyboardLogPrint.e("handleCharactor alphabet 아니고 키보드가 그외 어떤 상황에서인지 쿼티, 쿼티 한글인 경우이면서 글자키 아닌경우");
//                            }
                        }
                    }
                    if (mComposing.length() > 0) {
                        KeyboardLogPrint.d("skkim null commitText 23 :: " + mComposing.toString());
                        inputConnection.commitText(mComposing, 1);
                        kauto.FinishAutomataWithoutInput();
                        mComposing.setLength(0);
                    }
                    KeyboardLogPrint.d("skkim null commitText 24 :: " + String.valueOf((char) primaryCode) + " , primarycode :: " + primaryCode);
                    inputConnection.commitText(String.valueOf((char) primaryCode), 1);
                }
            }
        }
    }

    private void handleClose() {
        commitTyped(inputConnection);
        requestHideSelf(0);
        mMainKeyboardView.closing();
    }

    private void checkToggleCapsLock() {
        mCapsLock = !mCapsLock;
    }

    private void checkToggleKCapsLock() {
        KeyboardLogPrint.d("skkimmm shift checkToggleKCapsLock mKCapsLock change");
        mKCapsLock = !mKCapsLock;
    }

    private String getWordSeparators() {
        return mWordSeparators;
    }

    public boolean isWordSeparator(int code) {
        String separators = getWordSeparators();
        KeyboardLogPrint.d("seperate :: " + String.valueOf((char) code));
        return separators.contains(String.valueOf((char) code));
    }

    /* no candidate view for Korean. at least, not now.
    public void pickDefaultCandidate() {
        pickSuggestionManually(0);
    }

    public void pickSuggestionManually(int index) {
        if (mCompletionOn && mCompletions != null && index >= 0
                && index < mCompletions.length) {
            CompletionInfo ci = mCompletions[index];
            getCurrentInputConnection().commitCompletion(ci);
            if (mCandidateView != null) {
                mCandidateView.clear();
            }
            updateShiftKeyState(getCurrentInputEditorInfo());
        } else if (mComposing.length() > 0) {
            // If we were generating candidate suggestions for the current
            // text, we would commit one of them here.  But for this sample,
            // we will just commit the current text.
            commitTyped(getCurrentInputConnection());
        }
    }
    */
    public void pickSuggestionManually(int index) {
    } // just fake it to build.


    public void swipeRight() {
//        if (mCompletionOn) {
//            pickDefaultCandidate();
//        }
    }

    public void swipeLeft() {
        handleBackspace();
    }

    public void swipeDown() {
        handleClose();
    }

    public void swipeUp() {
    }

    public void onPressed(int primaryCode) {
    }

    public void onPress(int primaryCode) {
        KeyboardLogPrint.w("skkim checkTime onPress primaryCode **************************  :: " + primaryCode);
        if (primaryCode == 0 || primaryCode == -8080 || primaryCode == -8081)
            return;
        if ((primaryCode == -2 || primaryCode == -6
//                || primaryCode == -226
                || primaryCode == 46)) {
            //timerCancel(6);
            KeyboardLogPrint.d("stop complete called 9");
            if (mCompleteHandler != null)
                mCompleteHandler.sendEmptyMessage(STOP_COMPLETE_TIMER);
            // 20180430 이모티콘 제거
//            otimerCancel(6); ///
            mCompleteCount = 0;
            mComposing.setLength(0);
            mIsCompleteFromHandler = false;
            kauto.setInitState();
            mIsComplete = true;
            kauto.FinishAutomataWithoutInput();
            if (inputConnection != null)
                inputConnection.finishComposingText();
        }
        mPressPrimaryKey = primaryCode;
        KeyboardLogPrint.d("stop complete called 10");
        if (mCompleteHandler != null)
            mCompleteHandler.sendEmptyMessage(STOP_COMPLETE_TIMER);
        mCompleteCount = 0;
        mSoundHandler.sendEmptyMessage(0);
        KeyboardLogPrint.e("mVibrateLevel :: " + mVibrateLevel);
        if (mVibrateLevel > 0) {
            mSoundHandler.post(new Runnable() {
                @Override
                public void run() {
                    KeyboardLogPrint.d("vibrate called 1");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        int amplitude = 225 + ((int)mVibrateLevel * 3);
                        if ( mVibrateLevel == 0 )
                            amplitude = 0;

                        if ( isSpecialKey(primaryCode) ) {
                            LogPrint.d("sp amplitude :: " + amplitude);
                            mVibrator.vibrate(VibrationEffect.createOneShot(10, amplitude));
                        } else {
                            LogPrint.d("nor amplitude :: " + amplitude);
                            mVibrator.vibrate(VibrationEffect.createOneShot(5, amplitude));
                        }
                    } else {
                        mVibrator.vibrate(mVibrateLevel * Common.VIBRATE_MUL);
                    }
                }
            });
        }
        if (mCurKeyboard != mSejongKeyboard) {
            KeyboardLogPrint.d("not sejong");
            if (primaryCode < 0 || primaryCode == 32 || primaryCode == 10) {
                if ( mMainKeyboardView != null )
                    mMainKeyboardView.setPreviewEnabled(mIsPreviewSet, false);
            } else {
                if (mCurKeyboard == mNaraKeyboard) {
                    if (primaryCode == 119 || primaryCode == 99) {
                        if ( mMainKeyboardView != null )
                            mMainKeyboardView.setPreviewEnabled(mIsPreviewSet, false);
                    } else {
                        if ( mMainKeyboardView != null )
                            mMainKeyboardView.setPreviewEnabled(mIsPreviewSet, true);
                    }
                } else {
                    if ( mMainKeyboardView != null )
                        mMainKeyboardView.setPreviewEnabled(mIsPreviewSet, true);
                }
            }

        } else {
            if ( mMainKeyboardView != null )
                mMainKeyboardView.setPreviewEnabled(mIsPreviewSet, false);
        }

    }

    private String setTime(long milliSec) {
        DurationFormatUtils utils = new DurationFormatUtils();
        return utils.formatDurationHMS(milliSec);
    }

    public void onRelease(int primaryCode) {
        // 2024.05.08 exception 발생하는 부분 있음. 해당 부분 현재 사용하지 않아 전부 삭제
    }

    private String getNaraNextChar(String input) {
        String returnChar = "";
        if ("".equals(input))
            returnChar = ".";
        else if (".".equals(input))
            returnChar = "?";
        else if ("?".equals(input))
            returnChar = ".";
        else
            returnChar = ".";
        return returnChar;
    }

    private String getNextChar(String input) {
        String returnChar = "";
        if ("".equals(input))
            returnChar = ".";
        else if (".".equals(input))
            returnChar = ",";
        else if (",".equals(input))
            returnChar = "-";
        else if ("-".equals(input))
            returnChar = "/";
        else if ("/".equals(input))
            returnChar = ".";
        else
            returnChar = ".";
        return returnChar;
    }

    public Keyboard getCurrentKeyboard() {
        if (mCurKeyboard != null) {
            return mCurKeyboard;
        }
        return null;
    }

    private BroadcastReceiver mSetKeyboard = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogPrint.d("mSetKeyboard called action :: " + action);
            if (SET_KEYBOARD_ON.equals(action)) { // 키보드 설정화면 진입할 때
                mIsSettingOn = true;

                if (mMainKeyboardView != null) {
                    int level = SharedPreference.getInt(getApplicationContext(), Common.PREF_KEYBOARD_SIZE_LEVEL) < 0 ? Common.DEFAULT_KEYBOARD_SIZE : SharedPreference.getInt(getApplicationContext(), Common.PREF_KEYBOARD_SIZE_LEVEL);
                    int keyboardMode = SharedPreference.getInt(getApplicationContext(), Common.PREF_KEYBOARD_MODE);
                    if ( IsBackupKeyboardExist() )
                        keyboardMode = 2;
                    mCurKeyboard = getSelectKeyboard(keyboardMode);
                    KeyboardLogPrint.e("SoftKeyboard 7 kind :: " + mCurKeyboard);
                    mKoreanKeyboard = mCurKeyboard;
                    mMainKeyboardView.setKeyboard(mCurKeyboard);
                    ((LatinKeyboardView) mMainKeyboardView.getKeyboardView()).changeConfig(level);
                }
            } else if (SET_KEYBOARD_OFF.equals(action)) { // 키보드 설정화면 나갈 때
                mIsSettingOn = false;
                if (mMainKeyboardView != null) {
                    int keyboardMode = SharedPreference.getInt(getApplicationContext(), Common.PREF_KEYBOARD_MODE);
                    if ( IsBackupKeyboardExist() )
                        keyboardMode = 2;
                    mCurKeyboard = getSelectKeyboard(keyboardMode);
                    KeyboardLogPrint.e("SoftKeyboard 8 kind :: " + mCurKeyboard);
                    mKoreanKeyboard = mCurKeyboard;
                    mMainKeyboardView.setKeyboard(mCurKeyboard);
                    int level = SharedPreference.getInt(getApplicationContext(), Common.PREF_KEYBOARD_SIZE_LEVEL) < 0 ? Common.DEFAULT_KEYBOARD_SIZE : SharedPreference.getInt(getApplicationContext(), Common.PREF_KEYBOARD_SIZE_LEVEL);
                    ((LatinKeyboardView) mMainKeyboardView.getKeyboardView()).changeConfig(level);
                }
            } else if (SET_KEYBOARD_SIZE.equals(action)) {
                // keyboard view를 새로 생성하지 않으면 호출될떄마다 key height 값이 계속 작아짐, onInitializeInterface 를호출하면 호출하자마자 바로 finish 되서 사용할 수 없음
                mQSymbolsKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_symbol_q_1); // 쿼티용 기호
                mQSymbolsKeyboard1 = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_symbol_q_2); // 쿼티용 기호 shift
                mQSymbolsKeyboard_35 = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_symbol_q_1_35); // 쿼티용 기호
                mQSymbolsKeyboard1_35 = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_symbol_q_2_35); // 쿼티용 기호 shift
                mSymbolsKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_symbols); // 기호
                mNumberKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_number); // 기호 SHIFT
                mNumberOnlyKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_number_only); // mumber only
                mNumberSignedKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_number_signed); // mumber signed
                mNumberDecimalKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_number_decimal); // mumber decimal
                mNumberPhoneKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_number_phone); // mumber phone
                mPhoneSymbolKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_phone_symbol); // phone symbol
                mSymbolsShiftedKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_symbols_1);
                mSymbolsShiftedKeyboard1 = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_symbols_2);

                if (intent != null) {
                    int num_value = intent.getIntExtra("num_value", -1);
                    int size_level = intent.getIntExtra("size_level", -1);
                    if (num_value > 0) {
                        if (num_value == NUM_ON)
                            SharedPreference.setBoolean(getApplicationContext(), Common.PREF_QWERTY_NUM_SETTING, true);
                        else
                            SharedPreference.setBoolean(getApplicationContext(), Common.PREF_QWERTY_NUM_SETTING, false);
                    }
                    int keyHeightLevel = SharedPreference.getInt(getApplicationContext(), Common.PREF_KEYBOARD_SIZE_LEVEL) < 0 ? Common.DEFAULT_KEYBOARD_SIZE : SharedPreference.getInt(getApplicationContext(), Common.PREF_KEYBOARD_SIZE_LEVEL);
                    LogPrint.d("keyboard_height intent not null SoftKeyboard receiver_mSetKeyboard size_level :: " + size_level + " , keyHeightLevel :: " + keyHeightLevel);
                    if (size_level > 0) {
                        SharedPreference.setInt(getApplicationContext(), Common.PREF_KEYBOARD_SIZE_LEVEL, size_level);
                    }
                }

                boolean isQwertyNumSet = SharedPreference.getBoolean(getApplicationContext(), Common.PREF_QWERTY_NUM_SETTING);
                if (isQwertyNumSet) {
                    mQwertyNum = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_qwerty_n); // 영문 쿼티 + 숫자
                    mQwertyNum35 = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_qwerty_n_35);
                    mQwerty = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_qwerty);
                    mQwerty35 = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_qwerty_35);
                    mQKoreanKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_korean_n); // 한글
//                    mKoreanShiftedKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_n_korean_shifted, true); // 한글 SHIFT
                    mSejongKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_sejong); // 천지인 keyboard
                    mDanKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_dan); // 단모음 keyboard
                    mDanKeyboard35 = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_dan_35); // 단모음 keyboard
                    mNaraKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_nara); // 나랏글 keyboard
                    mSejongPlusKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_sejong_plus); // 천지인 플러스 keyboard
                } else {
                    mQwerty = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_qwerty); // 영문 쿼티
                    mQwerty35 = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_qwerty_35);
                    mQwertyNum = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_qwerty_n); // 영문 쿼티 + 숫자
                    mQKoreanKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_korean); // 한글
                    mQwertyNum = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_qwerty_n); // 영문 쿼티 + 숫자
                    mQwertyNum35 = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_qwerty_n_35);
//                    mKoreanShiftedKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_korean_shifted, true); // 한글 SHIFT
                    mSejongKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_sejong); // 천지인 keyboard
                    mDanKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_dan); // 단모음 keyboard
                    mDanKeyboard35 = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_dan_35); // 단모음 keyboard
                    mNaraKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_nara); // 나랏글 keyboard
                    mSejongPlusKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_sejong_plus); // 천지인 플러스 keyboard
                }
                WindowManager windowService = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                int currentRatation = windowService.getDefaultDisplay().getRotation();
                if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                    if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM ) {
                        mEQwerty = mQwerty35;
                    } else
                        mEQwerty = mQwerty;
                } else
                    mEQwerty = mQwerty;
                mEmojiKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_emoji);
                mEmoticonKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_emoticon);

                int keyHeightLevel = SharedPreference.getInt(getApplicationContext(), Common.PREF_KEYBOARD_SIZE_LEVEL) < 0 ? Common.DEFAULT_KEYBOARD_SIZE : SharedPreference.getInt(getApplicationContext(), Common.PREF_KEYBOARD_SIZE_LEVEL);
                float fLevel = 0.9f;
                try {
//                    fLevel = Float.parseFloat(String.format("%.2f", 0.90 + (float) (2 * keyHeightLevel) / 100));
                    fLevel = Common.GetHeightValue(keyHeightLevel);
                    LogPrint.d("keyboard_height intent not null SoftKeyboard receiver_mSetKeyboard fLevel :: " + fLevel);
                    setKeyHeight(fLevel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                float fLevel = Float.parseFloat(String.format("%.2f", 0.90 + (float) (2 * keyHeightLevel) / 100));
//                KeyboardLogPrint.w("fLevel :: " + fLevel);
//                setKeyHeight(fLevel);
                // onCreateInputView를 호출 해서 MainKeyboardView를 재생성한 후 keyboardview의 height를 다시 setting 해줌
                // setInputView(onCreateInputView());
                if (mMainKeyboardView != null) {
                    mCurKeyboard = getSelectKeyboard(SharedPreference.getInt(getApplicationContext(), Common.PREF_KEYBOARD_MODE));
                    mMainKeyboardView.setKeyboard(mCurKeyboard);
                    mMainKeyboardView.changeKeyboardHeight(SharedPreference.getInt(context, Common.PREF_KEYBOARD_HEIGHT), mEmojiKeyboard, mEmoticonKeyboard);
                }
            } else if ("SOUND_CHANGE".equals(action)) {
                mSoundLoaded = false;
                if (intent != null) {
                    int sound = intent.getIntExtra("change_sound", 0);
                    SharedPreference.setInt(getApplicationContext(), Common.PREF_SELECTED_SOUND, sound);
                    LogPrint.d("################# get SoftKeyboard sound change position ::: " + sound);
                }
                initSound();
            } else if (CHANGE_CONFIG.equals(action)) {
                if (intent != null) {
                    int num_value = intent.getIntExtra("num_value", -1);
                    if (num_value > 0) {
                        if (num_value == NUM_ON)
                            SharedPreference.setBoolean(getApplicationContext(), Common.PREF_QWERTY_NUM_SETTING, true);
                        else
                            SharedPreference.setBoolean(getApplicationContext(), Common.PREF_QWERTY_NUM_SETTING, false);
                    }
                }
            } else if ("KIND_CHANGE".equals(action)) {
                if (intent != null) {
                    int kind = intent.getIntExtra("kind", 0);
                    SharedPreference.setInt(getApplicationContext(), Common.PREF_KEYBOARD_MODE, kind);
                    boolean numSet = intent.getBooleanExtra("num_set", false);

                    SharedPreference.setBoolean(getApplicationContext(), Common.PREF_QWERTY_NUM_SETTING, numSet);

                    mQSymbolsKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_symbol_q_1); // 쿼티용 기호
                    mQSymbolsKeyboard1 = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_symbol_q_2); // 쿼티용 기호 shift
                    mQSymbolsKeyboard_35 = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_symbol_q_1_35); // 쿼티용 기호
                    mQSymbolsKeyboard1_35 = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_symbol_q_2_35); // 쿼티용 기호 shift
                    mSymbolsKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_symbols); // 기호
                    mNumberKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_number); // 기호 SHIFT
                    mNumberOnlyKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_number_only); // mumber only
                    mNumberSignedKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_number_signed); // mumber signed
                    mNumberDecimalKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_number_decimal); // mumber decimal
                    mNumberPhoneKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_number_phone); // mumber phone
                    mPhoneSymbolKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_phone_symbol); // phone symbol
                    mSymbolsShiftedKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_symbols_1);
                    mSymbolsShiftedKeyboard1 = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_symbols_2);

                    boolean isQwertyNumSet = SharedPreference.getBoolean(getApplicationContext(), Common.PREF_QWERTY_NUM_SETTING);
                    if (isQwertyNumSet) {
                        mQwertyNum = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_qwerty_n); // 영문 쿼티 + 숫자
                        mQwertyNum35 = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_qwerty_n_35);
                        mQwerty = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_qwerty);
                        mQwerty35 = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_qwerty_35);
                        mQKoreanKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_korean_n); // 한글
//                    mKoreanShiftedKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_n_korean_shifted, true); // 한글 SHIFT
                        mSejongKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_sejong); // 천지인 keyboard
                        mDanKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_dan); // 단모음 keyboard
                        mDanKeyboard35 = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_dan_35); // 단모음 keyboard
                        mNaraKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_nara); // 나랏글 keyboard
                        mSejongPlusKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_sejong_plus); // 천지인 플러스 keyboard
                    } else {
                        mQwerty = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_qwerty); // 영문 쿼티
                        mQwerty35 = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_qwerty_35);
                        mQwertyNum = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_qwerty_n); // 영문 쿼티 + 숫자
                        mQwertyNum35 = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_qwerty_n_35);
                        mQKoreanKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_korean); // 한글
//                    mKoreanShiftedKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_korean_shifted, true); // 한글 SHIFT
                        mSejongKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_sejong); // 천지인 keyboard
                        mDanKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_dan); // 단모음 keyboard
                        mDanKeyboard35 = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_dan_35); // 단모음 keyboard
                        mNaraKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_nara); // 나랏글 keyboard
                        mSejongPlusKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_sejong_plus); // 천지인 플러스 keyboard
                    }
                    WindowManager windowService = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                    int currentRatation = windowService.getDefaultDisplay().getRotation();
                    if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                        if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.VANILLA_ICE_CREAM ) {
                            mEQwerty = mQwerty35;
                        } else
                            mEQwerty = mQwerty;
                    } else
                        mEQwerty = mQwerty;

                    mEmojiKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_emoji);
                    mEmoticonKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_emoticon);

                    int keyHeightLevel = SharedPreference.getInt(getApplicationContext(), Common.PREF_KEYBOARD_SIZE_LEVEL) < 0 ? Common.DEFAULT_KEYBOARD_SIZE : SharedPreference.getInt(getApplicationContext(), Common.PREF_KEYBOARD_SIZE_LEVEL);
                    float fLevel = 0.9f;
                    try {
//                        fLevel =  Float.parseFloat(String.format("%.2f", 0.90 + (float) (2 * keyHeightLevel) / 100));
                        fLevel = Common.GetHeightValue(keyHeightLevel);
                        setKeyHeight(fLevel);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (mMainKeyboardView != null) {
                        KeyboardView mInputView = (KeyboardView) getLayoutInflater().inflate(R.layout.aikbd_input, null);
                        mCurKeyboard = getSelectKeyboard(kind);
                        mInputView.setKeyboard(mCurKeyboard);
                        mMainKeyboardView.changeKeyboardHeight(SharedPreference.getInt(context, Common.PREF_KEYBOARD_HEIGHT), mEmojiKeyboard, mEmoticonKeyboard);
                    }
//                    setInputView(onCreateInputView());
                }
            } else if (MyReceiver.VOLUME_CHANGE.equals(action)) {
//                mMediaVolume = SharedPreference.getInt(context, Common.MEDIA_VOLUME_LEVEL);
                mMediaVolume = Common.getStreamLevel(context);
            } else if (SET_CHANGE.equals(action)) {
                mVolumeLevel = SharedPreference.getInt(context, Common.PREF_I_VOLUME_LEVEL);
                if (mVolumeLevel < 0) {
                    SharedPreference.setInt(context, Common.PREF_I_VOLUME_LEVEL, Common.DEFAULT_SOUND_LEVEL);
                    mVolumeLevel = Common.DEFAULT_SOUND_LEVEL;
                }
                mVibrateLevel = SharedPreference.getLong(context, Common.PREF_VIBRATE_LEVEL);
                KeyboardLogPrint.w("RecentAdapter onReceive volume :: " + mVolumeLevel);
                KeyboardLogPrint.w("RecentAdapter onReceive vibrate :: " + mVibrateLevel);
            }
        }
    };

    public void setKeyBoardMode(boolean isKorean, int val, int index) {
        KeyboardLogPrint.e("SMALL_BTN_WRONG :: isKorean :: " + isKorean + " , index :: " + index);
        SharedPreference.setInt(getApplicationContext(), Common.PREF_EXT_KEYBOARD, val);
        if (mCurKeyboard == mSejongKeyboard || mCurKeyboard == mSejongPlusKeyboard) {
            // 2018.08.21 현재 키보드가 천지인인데 긴헐적으로 isKorean이 false로 넘어가 키패드 롱키세팅이 영문쿼티로 되는 현상을 막기 위해 현재 키보드가 천지인 한글일 경우에는 isKorean 변수를 강제로 true 설정해줌
            KeyboardLogPrint.d("mCurKeyboard is chunjiin hangul");
            isKorean = true;
        }
        SharedPreference.setBoolean(getApplicationContext(), Common.PREF_IS_KOREAN_KEYBOARD, isKorean);
        /*
        if (mMainKeyboardView != null && mCurKeyboard != null) {
            int level = SharedPreference.getInt(getApplicationContext(), Common.PREF_KEYBOARD_SIZE_LEVEL) < 0 ? Common.DEFAULT_KEYBOARD_SIZE : SharedPreference.getInt(getApplicationContext(), Common.PREF_KEYBOARD_SIZE_LEVEL);

            mMainKeyboardView.setKeyboard(mCurKeyboard);
            LatinKeyboardView keyboardView = ((LatinKeyboardView) mMainKeyboardView.getKeyboardView());
            if ( keyboardView != null )
                keyboardView.changeConfig(level);
        }*/
        if (mMainKeyboardView != null) {
            int level = SharedPreference.getInt(getApplicationContext(), Common.PREF_KEYBOARD_SIZE_LEVEL) < 0 ? Common.DEFAULT_KEYBOARD_SIZE : SharedPreference.getInt(getApplicationContext(), Common.PREF_KEYBOARD_SIZE_LEVEL);

            mMainKeyboardView.setKeyboard(mCurKeyboard);
            ((LatinKeyboardView) mMainKeyboardView.getKeyboardView()).changeConfig(level);
        }
    }

    public void sendText(String text) {
        KeyboardLogPrint.d("skkim null commitText 25 :: " + text);
        inputConnection.commitText(text, 1);
    }

    public void sendDownKeyEvent(int keyEventCode, int flags) {
        inputConnection.sendKeyEvent(
                new KeyEvent(
                        SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(),
                        KeyEvent.ACTION_DOWN,
                        keyEventCode,
                        0,
                        flags
                )
        );
    }

    public void sendUpKeyEvent(int keyEventCode, int flags) {
        inputConnection.sendKeyEvent(
                new KeyEvent(
                        SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(),
                        KeyEvent.ACTION_UP,
                        keyEventCode,
                        0,
                        flags
                )
        );
    }

    public void sendDownAndUpKeyEvent(int keyEventCode, int flags) {
        sendDownKeyEvent(keyEventCode, flags);
        sendUpKeyEvent(keyEventCode, flags);
    }


//    public void switchToPreviousInputMethod() {
//        try {
//            previousInputMethodManager.switchToLastInputMethod(iBinder);
//        } catch (Throwable t) { // java.lang.NoSuchMethodError if API_level<11
//            Context context = getApplicationContext();
////            CharSequence text = "Unfortunately aikbd_input method switching isn't supported in your version of Android! You will have to do it manually :(";
//            CharSequence text = "안드로이드 버전이 낮아 입력방식 변경을 지원하지 않습니다. 수동으로 변경하시기 바랍니다.";
//            int duration = Toast.LENGTH_SHORT;
//
//            Toast toast = Toast.makeText(context, text, duration);
//            toast.show();
//        }
//    }

    private void initSound() {
        KeyboardLogPrint.w("SoftKeyboard initSound");
        int selected_sound = SharedPreference.getInt(getApplicationContext(), Common.PREF_SELECTED_SOUND) < 0 ? 0 : SharedPreference.getInt(getApplicationContext(), Common.PREF_SELECTED_SOUND);
        LogPrint.d("################# get SoftKeyboard initSound sound change position ::: " + selected_sound);
        int resId;
        if (selected_sound == SOUND_0)
            resId = R.raw.aikbd_sound0;
        else if (selected_sound == SOUND_1)
            resId = R.raw.aikbd_sound1;
        else if (selected_sound == SOUND_2)
            resId = R.raw.aikbd_sound2;
        else if (selected_sound == SOUND_3)
            resId = R.raw.aikbd_sound3;
        else if (selected_sound == SOUND_4)
            resId = R.raw.aikbd_sound4;
        else
            resId = R.raw.aikbd_sound0;
//        int maxStreams = 1;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mSoundPool = new SoundPool.Builder()
//                    .setMaxStreams(maxStreams)
//                    .build();
//        } else {
//            mSoundPool = new SoundPool(maxStreams, AudioManager.STREAM_MUSIC, 0);
//        }
        try {
//            if ( mSoundPool != null ) {
//                mSoundPool.release();
//            }
//            mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                KeyboardLogPrint.w("SoftKeyboard sound pool create 1");
                mSoundPool = new SoundPool.Builder()
                        .setMaxStreams(2)
                        .build();
            } else {
                KeyboardLogPrint.w("SoftKeyboard sound pool create 2");
                mSoundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
            }
            mSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    mSoundLoaded = true;
                }
            });
            mSoundId = mSoundPool.load(getApplicationContext(), resId, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onShowInputRequested(int flags, boolean configChange) {
        KeyboardLogPrint.w("skkim onShowInputRequested");
        if (configChange) {
            //timerCancel(100);
            KeyboardLogPrint.d("stop complete called 11");
            if (mCompleteHandler != null)
                mCompleteHandler.sendEmptyMessage(STOP_COMPLETE_TIMER);
            // 20180430 이모티콘 제거
//            otimerCancel(100); ///
            mCompleteCount = 0;
            if (inputConnection != null) {
                inputConnection.finishComposingText();
                kauto.setInitState();
                mComposing.setLength(0);
                kauto.FinishAutomataWithoutInput();
            }
        }
        mIsCompleteFromHandler = false;
        return super.onShowInputRequested(flags, configChange);
    }

    private boolean isCanRotateChar(int lastCode) {
        int[] target = {100, 101, 113, 114, 115, 116, 119}; // ㄱ,ㄴ,ㄷ,ㅂ,ㅅ,ㅈ,ㅇ 와 같이 연속으로 값이 들어와 rotate 되는 값들
        for (int i = 0; i < target.length; i++) {
            if (lastCode == target[i])
                return true;
        }

        return false;
    }

    private int convertDpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private void setKeyHeight(float val) {
        LogPrint.d("keyboard_height SoftKeyboard setKeyHeight fLevel :: " + val);
        adjustKeyboardKeyHeight(mQwerty, val);
        adjustKeyboardKeyHeight(mQwerty35, val);
        adjustKeyboardKeyHeight(mQwertyNum, val);
        adjustKeyboardKeyHeight(mQwertyNum35, val);
        adjustKeyboardKeyHeight(mSymbolsKeyboard, val);
        adjustKeyboardKeyHeight(mQSymbolsKeyboard, val);
        adjustKeyboardKeyHeight(mQSymbolsKeyboard1, val);
        adjustKeyboardKeyHeight(mQSymbolsKeyboard_35, val);
        adjustKeyboardKeyHeight(mQSymbolsKeyboard1_35, val);
        adjustKeyboardKeyHeight(mNumberKeyboard, val);
        adjustKeyboardKeyHeight(mNumberOnlyKeyboard, val);
        adjustKeyboardKeyHeight(mNumberSignedKeyboard, val);
        adjustKeyboardKeyHeight(mNumberDecimalKeyboard, val);
        adjustKeyboardKeyHeight(mNumberPhoneKeyboard, val);
        adjustKeyboardKeyHeight(mPhoneSymbolKeyboard, val);
        adjustKeyboardKeyHeight(mSymbolsShiftedKeyboard, val);
        adjustKeyboardKeyHeight(mSymbolsShiftedKeyboard1, val);
        adjustKeyboardKeyHeight(mQKoreanKeyboard, val);
        adjustKeyboardKeyHeight(mQKoreanKeyboard35, val);
//        adjustKeyboardKeyHeight(mKoreanShiftedKeyboard, val);
        adjustKeyboardKeyHeight(mSejongKeyboard, val);
        adjustKeyboardKeyHeight(mDanKeyboard, val);
        adjustKeyboardKeyHeight(mDanKeyboard35, val);
        adjustKeyboardKeyHeight(mNaraKeyboard, val);
        adjustKeyboardKeyHeight(mEmojiKeyboard, val);
        adjustKeyboardKeyHeight(mEmoticonKeyboard, val);
        adjustKeyboardKeyHeight(mSejongPlusKeyboard, val);
        if (mMainKeyboardView != null)
            mMainKeyboardView.changeKeyHeight();
    }

    private void adjustKeyboardKeyHeight(LatinKeyboard keyboard, double newKeyHeight) {
        if (keyboard != null) {
            LogPrint.d("keyboardHeight SoftKeyboard adjustKeyboardKeyHeight keyboard not null");
            int height = 0;
            for (Keyboard.Key key : keyboard.getKeys()) {
                key.height *= newKeyHeight;
                KeyboardLogPrint.e("keyboardHeight SoftKeyboard key.height : " + key.height);
                key.y *= newKeyHeight;
                KeyboardLogPrint.e("keyboardHeight SoftKeyboard key.y : " + key.y);
                height = key.height;
                KeyboardLogPrint.e("keyboardHeight SoftKeyboard height : " + height);
            }
            KeyboardLogPrint.e("keyboardHeight SoftKeyboard adjustKeyboardKeyHeight : 해당 키보드의 높이를 설정");
            if (keyboard == mEmoticonKeyboard || keyboard == mEmojiKeyboard) {
                height = (int) (height + (20 * newKeyHeight));
            }
            LogPrint.d("khskkim keyboard_height soft 1 height 111 :: " + height);
            keyboard.setHeight(height);
        } else
            LogPrint.d("khskkim keyboard_height adjustKeyboardKeyHeight keyboard null");
    }

    private boolean isChunjiinWordKey(int code) {
        if (code == 108 || code == 122 || code == 109 || code == 114 || code == 115 || code == 101
                || code == 113 || code == 116 || code == 119 || code == 100)
            return true;
        else
            return false;
    }

    private int getRemoveCount(int code) {
        int removeCount = 1;
        for (int i = 0; i < CharTables.STROKE_COUNT.CODE.length; i++) {
            if (code == CharTables.STROKE_COUNT.CODE[i])
                removeCount = CharTables.STROKE_COUNT.STROKE[i];
        }

        return removeCount;
    }

    private boolean isNaraKeyboard() {
        if (mBackupCurKeyboard != null && mBackupKauto != null) {
            return false;
        }
        WindowManager windowService = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int currentRatation = windowService.getDefaultDisplay().getRotation();
        if (mMainKeyboardView != null && mMainKeyboardView.getKeyboard() == mNaraKeyboard) {
            if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                return false;
            } else
                return true;
        } else
            return false;
    }

    private boolean isChunjiinPlusKeyboard() {
        if (mBackupCurKeyboard != null && mBackupKauto != null) {
            return false;
        }
        WindowManager windowService = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int currentRatation = windowService.getDefaultDisplay().getRotation();
        if (mMainKeyboardView != null && mMainKeyboardView.getKeyboard() == mSejongPlusKeyboard) {
            if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                return false;
            } else
                return true;
        } else
            return false;
    }

    private boolean isChunjiinKeyboard() {
        if (mBackupCurKeyboard != null && mBackupKauto != null) {
            return false;
        }
        WindowManager windowService = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int currentRatation = windowService.getDefaultDisplay().getRotation();
        if (mMainKeyboardView != null && mMainKeyboardView.getKeyboard() == mSejongKeyboard) {
            if (Surface.ROTATION_90 == currentRatation || Surface.ROTATION_270 == currentRatation) {
                return false;
            } else
                return true;
        } else
            return false;
    }

    /**
     * 현재 사용하지 않음
     * protected void connectGetAD(final Context context, String keyword) {
     * KeyboardLogPrint.w("connectGetAD :: " + keyword);
     * //        if (TextUtils.isEmpty(keyword))
     * //            keyword = "";
     * //        else
     * //            SharedPreference.setString(context, Common.PREF_SAVED_KEYWORD, keyword);
     * <p>
     * long firstInstallTime = SharedPreference.getLong(context, Common.PREF_FIRST_INSTALL_TIME);
     * long currentTime = System.currentTimeMillis();
     * long diffTenDay = 1000 * 1 * 60 * 60 * 24 * 10; // 10일의 millis 값
     * KeyboardLogPrint.w("firstInstallTime :: " + firstInstallTime);
     * KeyboardLogPrint.w("currentTime :: " + currentTime);
     * KeyboardLogPrint.w("diffTenDay :: " + diffTenDay);
     * <p>
     * if ((currentTime - firstInstallTime) <= diffTenDay) {
     * KeyboardLogPrint.w("첫 설치 후 10일이 안지남 return ");
     * mMainKeyboardView.initAd();
     * return;
     * }
     * if (!isTimePassed()) {
     * KeyboardLogPrint.w("광고 요청시간이 안지남 return ");
     * //            mModel = null;
     * mMainKeyboardView.initAd();
     * return;
     * }
     * KeyboardLogPrint.w("광고 요청시간이 지났거나 세팅되어있지 않음 광고 요청");
     * CustomAsyncTask apiAsyncTask = new CustomAsyncTask(context);
     * apiAsyncTask.requestADInfo(keyword, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
     *
     * @Override public void onResponse(boolean result, Object obj) {
     * try {
     * if (result) {
     * if (!TextUtils.isEmpty(obj.toString())) {
     * JSONObject object = new JSONObject(obj.toString());
     * JSONObject object1 = object.getJSONObject("mobadbn");
     * JSONArray array = object1.getJSONArray("data");
     * JSONObject object2 = array.getJSONObject(0);
     * String title = object2.optString("snm");
     * String desc = object2.optString("sdsc");
     * String link = object2.optString("link");
     * String imgPath = object2.optString("logo");
     * String gubun = object2.optString("adgubun");
     * KeyboardLogPrint.w("keyad title :: " + title);
     * KeyboardLogPrint.w("keyad desc :: " + desc);
     * KeyboardLogPrint.w("keyad link :: " + link);
     * KeyboardLogPrint.w("keyad imgPath :: " + imgPath);
     * KeyboardLogPrint.w("keyad gubun :: " + gubun);
     * if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(desc) && !TextUtils.isEmpty(link) && !TextUtils.isEmpty(imgPath) && "KL".equals(gubun)) {
     * SharedPreference.setString(getApplicationContext(), Common.PREF_AD_JSON, object2.toString());
     * //                                        if (mMainKeyboardView != null)
     * //                                            mMainKeyboardView.setAd();
     * }
     * <p>
     * if ("KL".equals(gubun)) // KL 광고가 넘어온 키워드에 한해 저장하고 있어야 함
     * SharedPreference.setString(getApplicationContext(), Common.PREF_SAVED_KEYWORD, mADStr);
     * <p>
     * } else
     * KeyboardLogPrint.w("전달받은 json 값이 empty");
     * <p>
     * } else
     * KeyboardLogPrint.w("광고 요청 결과 false");
     * } catch (Exception e) {
     * e.printStackTrace();
     * }
     * }
     * }
     * );
     * }
     * <p>
     * protected void connectMaxPoint(String today) {
     * UserIdDBHelper helper = new UserIdDBHelper(getApplicationContext());
     * KeyboardUserIdModel model = helper.getUserInfo();
     * if (model != null) {
     * connectMaxPoint(model, today);
     * }
     * }
     * <p>
     * protected void connectLiveCount(String deviceId) {
     * KeyboardLogPrint.e("connectLiveCount deviceId :: " + deviceId);
     * connectLiveCheck(deviceId);
     * //        if () {
     * //            KeyboardLogPrint.w("adid is empty");
     * //            AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
     * //                @Override
     * //                protected String doInBackground(Void... params) {
     * //                    AdvertisingIdClient.Info idInfo = null;
     * //                    try {
     * //                        idInfo = AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext());
     * //                    } catch (GooglePlayServicesNotAvailableException e) {
     * //                        e.printStackTrace();
     * //                    } catch (GooglePlayServicesRepairableException e) {
     * //                        e.printStackTrace();
     * //                    } catch (IOException e) {
     * //                        e.printStackTrace();
     * //                    }
     * //                    String advertId = null;
     * //                    try {
     * //                        advertId = idInfo.getId();
     * //                    } catch (NullPointerException e) {
     * //                        e.printStackTrace();
     * //                    }
     * //
     * //                    return advertId;
     * //                }
     * //
     * //                @Override
     * //                protected void onPostExecute(String advertId) {
     * //
     * //                    Toast.makeText(getApplicationContext(), advertId, Toast.LENGTH_SHORT).show();
     * //                    SharedPreference.setString(getApplicationContext(), Common.PREF_ADID, advertId);
     * //                    connectLiveCheck(advertId);
     * //                }
     * //
     * //            };
     * //            task.execute();
     * //        } else {
     * //            KeyboardLogPrint.w("adid is not empty");
     * //            connectLiveCheck(adid);
     * //        }
     * }
     * <p>
     * 현재는 사용하지 않음
     * protected void connectMaxPoint(KeyboardUserIdModel model, final String today) {
     * CustomAsyncTask apiAsyncTask = new CustomAsyncTask(getApplicationContext());
     * apiAsyncTask.requestMaxPoint(model, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
     * @Override public void onResponse(boolean rt, Object obj) {
     * if (rt) {
     * try {
     * JSONObject object = new JSONObject(obj.toString());
     * boolean result = object.optBoolean("Result");
     * String errStr = object.optString("errstr");
     * String result_day = object.optString("ResultDay");
     * KeyboardLogPrint.e("result_day :: " + result_day);
     * if (result) {
     * int point = object.optInt("useablePoint");
     * KeyboardLogPrint.e("about point max point result true, point :: " + point);
     * if (mMainKeyboardView != null) {
     * if (result_day != null && result_day.equals(today)) {
     * mMainKeyboardView.setLimitMax(point);
     * SharedPreference.setString(getApplicationContext(), Common.PREF_DATE_POINT, Common.getDate());
     * }
     * }
     * } else {
     * <p>
     * }
     * <p>
     * //                        KeyboardLogPrint.t(getApplicationContext(), "today :: " + today + " \n" + "ResultDay :: " + result_day);  point관려 부분 삭제필요
     * <p>
     * } catch (Exception e) {
     * e.printStackTrace();
     * }
     * } else {
     * <p>
     * }
     * }
     * });
     * }
     * <p>
     * protected void connectLiveCheck(String deviceId) {
     * CustomAsyncTask apiAsyncTask = new CustomAsyncTask(getApplicationContext());
     * apiAsyncTask.requestLiveCheck(deviceId, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
     * @Override public void onResponse(boolean result, Object obj) {
     * try {
     * KeyboardLogPrint.w("connectLiveCheck obj.toString skkim :: " + obj.toString());
     * JSONObject object = new JSONObject(obj.toString());
     * String rlt = object.optString("result");
     * String errmsg = object.optString("errmsg");
     * int keywordCnt = object.optInt("keywordcnt");
     * int randCnt = object.optInt("rand_cnt");
     * if ("success".equals(rlt)) {
     * KeyboardLogPrint.w("result is success");
     * SharedPreference.setString(getApplicationContext(), Common.PREF_MATCHED_EMOJI_DATE, Common.getDate());
     * } else {
     * KeyboardLogPrint.w("result is not success");
     * }
     * SharedPreference.setInt(getApplicationContext(), Common.PREF_KEYWORD_CNT, keywordCnt);
     * SharedPreference.setInt(getApplicationContext(), Common.PREF_RAND_CNT, randCnt);
     * } catch (Exception e) {
     * e.printStackTrace();
     * }
     * }
     * });
     * }
     * <p>
     * <p>
     * protected void connectQuickLinkList() {
     * CustomAsyncTask apiAsyncTask = new CustomAsyncTask(getApplicationContext());
     * apiAsyncTask.requestQuickLinkList(new CustomAsyncTask.OnDefaultObjectCallbackListener() {
     * @Override public void onResponse(boolean result, Object obj) {
     * if (result) {
     * if (!TextUtils.isEmpty(obj.toString())) {
     * try {
     * JSONObject object = new JSONObject(obj.toString());
     * boolean rt = object.optBoolean("Result");
     * if (rt) {
     * ArrayList<AIKBD_QUICK_Link_Model> modelArray = new ArrayList<AIKBD_QUICK_Link_Model>();
     * ArrayList<SiteModel> siteModelArray = new ArrayList<SiteModel>();
     * JSONArray array = object.optJSONArray("data");
     * JSONArray siteArray = object.optJSONArray("site");
     * JSONObject popList = object.optJSONObject("pop_list");
     * <p>
     * if ( popList != null && !TextUtils.isEmpty(popList.toString()) ) {
     * String list = popList.toString();
     * SharedPreference.setString(getApplicationContext(), Common.PREF_POP_LIST, list);
     * }
     * <p>
     * if (siteArray != null && siteArray.length() > 0) {
     * for (int i = 0; i < siteArray.length(); i++) {
     * SiteModel siteModel = new SiteModel();
     * JSONObject siteObject = siteArray.optJSONObject(i);
     * String name = siteObject.optString("name");
     * String packageName = siteObject.optString("package");
     * siteModel.setName(name);
     * siteModel.setPackage(packageName);
     * <p>
     * siteModelArray.add(siteModel);
     * }
     * }
     * <p>
     * if (array != null && array.length() > 0) {
     * for (int i = 0; i < array.length(); i++) {
     * AIKBD_QUICK_Link_Model model = new AIKBD_QUICK_Link_Model();
     * JSONObject subObject = array.optJSONObject(i);
     * String icon = subObject.optString("icon");
     * String name = subObject.optString("site");
     * String matchword = subObject.optString("word");
     * String link = subObject.optString("url");
     * String content = subObject.optString("desc");
     * model.setIcon(icon);
     * model.setName(name);
     * model.setMatchWord(matchword);
     * model.setLink(link);
     * model.setContent(content);
     * KeyboardLogPrint.e("ql icon :: " + icon);
     * KeyboardLogPrint.e("ql name :: " + name);
     * KeyboardLogPrint.e("ql matchword :: " + matchword);
     * KeyboardLogPrint.e("ql link :: " + link);
     * KeyboardLogPrint.e("ql content :: " + content);
     * modelArray.add(model);
     * }
     * }
     * }
     * } catch (Exception e) {
     * e.printStackTrace();
     * }
     * } else {
     * <p>
     * }
     * }
     * }
     * });
     * <p>
     * }
     **/

    private void delete() {
        //inputConnection = getCurrentInputConnection();
        if (inputConnection != null) {
            KeyboardLogPrint.w("time_log delete 1");
            inputConnection.deleteSurroundingText(1, 0);
            KeyboardLogPrint.w("time_log delete 2");
        }
    }


    private boolean isEmoji(CharSequence seq) {
        if (seq != null && pattern != null) {
            Matcher matcher = pattern.matcher(seq.toString());
            return matcher.find();
        } else
            return false;
    }

    // edit text click 했을 때 complete 시키도록
    @Override
    public void onViewClicked(boolean focusChanged) {
        super.onViewClicked(focusChanged);
        KeyboardLogPrint.w("skkim onViewClicked");
//        if (mHandler != null)
//            mHandler.removeCallbacksAndMessages(null);
        //timerCancel(8);
        KeyboardLogPrint.d("stop complete called 12");
        if (mCompleteHandler != null)
            mCompleteHandler.sendEmptyMessage(STOP_COMPLETE_TIMER);
        mCompleteCount = 0;
        if (inputConnection != null) {
            inputConnection.finishComposingText();
            kauto.setInitState();
            mComposing.setLength(0);
            kauto.FinishAutomataWithoutInput();
        }
        mIsCompleteFromHandler = false;

        // EDIT TEXT 클릭 시 키보드의 모드를 키보드로 변경하도록 함 // 2017.09.14
        if (mMainKeyboardView != null) {
            mMainKeyboardView.selectKeyboard(MainKeyboardView.GUBUN_KEYBOARD);
        }
    }

    public boolean inputHasText() {
        boolean hasText = false;
        try {
            if (inputConnection != null && isInputViewShown()) {
                ExtractedText et = inputConnection.getExtractedText(new ExtractedTextRequest(), 0);
                if (et != null) {
                    CharSequence seq = et.text;
                    String temp = seq.toString();
                    int length = temp.length();
                    if (length > 0)
                        hasText = true;
                }
            }
            return hasText;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean setKeywordAdd(String _keyword, boolean isKeyboardDown) {
        if (TextUtils.isEmpty(_keyword) || _keyword.length() < 2)
            return false;
        else {
            if (isKeyboardDown)
                return true;
            else {
                String savedKeyword = SharedPreference.getString(getApplicationContext(), Common.PREF_SAVED_KEYWORD);
                if (_keyword.equals(savedKeyword))
                    return false;
                else
                    return true;
            }
        }
    }

    public void addKeyword(String keyword) {
        if (keywordLimit < 1)
            keywordLimit = 100;

        if (TextUtils.isEmpty(keyword) || keyword.length() < 2)
            return;

        if (keywordList.size() > keywordLimit - 1)
            keywordList = keywordList.subList(0, keywordLimit - 1);

        int index = keywordList.indexOf(keyword);

        if (index < 0)
            keywordList.add(0, keyword);
        else if (index > 0) {
            keywordList.remove(index);
            keywordList.add(0, keyword);
        }

        if (keywordList != null && keywordList.size() > 0) {
            AIKBD_DBHelper helper = new AIKBD_DBHelper(getApplicationContext());
            helper.deleteKwd();
            helper.insertKwd(getKeywordList());
        }
    }

    private String getAppVersion() {
        String version = "";
        try {
            PackageInfo i = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = i.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    private String getKeywordList() {
        return TextUtils.join("||", keywordList);
    }

//    public boolean setKeywordAdd(String _keyword, boolean isKeyboardDown) {
//        if (keywordLimit < 1)
//            keywordLimit = 10;
//
//        if (TextUtils.isEmpty(_keyword) || _keyword.length() < 2)
//            return false;
//
//        if (keywordList.size() > keywordLimit - 1)
//            keywordList = keywordList.subList(0, keywordLimit - 1);
//
//        int index = keywordList.indexOf(_keyword);
//
//        if (index < 0)
//            keywordList.add(0, _keyword);
//        else if (index > 0) {
//            keywordList.remove(index);
//            keywordList.add(0, _keyword);
//        }
//        else
//        {
//            KeyboardLogPrint.w("index :: " + index);
//            if ( !isKeyboardDown )
//                return false;
////            return false; // 동일 키워드일 경우에도 광고 호출하도록 변경
//        }
//        return true;
//    }
//
//    private String getKeywordList() {
//        return TextUtils.join("||", keywordList);
//    }
//
//    private void setKeywordList(String str) {
//        if (!TextUtils.isEmpty(str)) {
//            try {
//                String[] array = str.split("\\|\\|");
//                if (array != null && array.length > 0)
//                    keywordList = new ArrayList<String>(Arrays.asList(array));
//                else
//                    keywordList = new ArrayList<String>();
//            } catch (Exception e) {
//                keywordList = new ArrayList<String>();
//                e.printStackTrace();
//            }
//        }
//    }

    private boolean isTimePassed() {
        try {
            String from = SharedPreference.getString(getApplicationContext(), Common.PREF_AD_VIEW_TIME);
            if (!TextUtils.isEmpty(from)) {
                SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date savedDate = sdformat.parse(from);
                Calendar cal = Calendar.getInstance();
                cal.setTime(savedDate);

                Date date = new Date();
                cal.setTime(date);
                int compare = date.compareTo(savedDate);

                if (compare > 0) {
                    SharedPreference.setString(getApplicationContext(), Common.PREF_AD_VIEW_TIME, "");
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

    private String removedEmojiStr(String str) {
        if (TextUtils.isEmpty(str))
            return "";
        Pattern emoticons = Pattern.compile("[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]+");
        Matcher emoticonsMatcher = emoticons.matcher(str);
        return emoticonsMatcher.replaceAll("");
    }

    // 20180423 팝업케시 제거
//    private void stopPopTask() {
//        if (mPopTimer != null) {
//            mPopTimer.cancel();
//            mPopTimer = null;
//        }
//    }

    // 20180423 팝업케시 제거
//    private void startPopTask() {
//        KeyboardLogPrint.e("startPopTask ");
//        if (mPopTimer != null) {
//            mPopTimer.cancel();
//            mPopTimer = null;
//        }
//
//        mPopTimer = new Timer();
//        mPopTimer.scheduleAtFixedRate(new ForegroundTask(), 0, 800);
//    }

    // 20180423 팝업케시 제거
//    private class ForegroundTask extends TimerTask {
//        @Override
//        public void run() {
//            String foregroundApp = printForegroundTask();
//            String launcherPackageName = getLauncherPackage(getApplicationContext());
//
//            if ( foregroundApp.equals(launcherPackageName) && mIsPopShow ) {
//                if ( mMainKeyboardView != null ) {
//                    if ( !mMainKeyboardView.isPopVisible() )
//                        mMainKeyboardView.showPopIcon();
//                }
//                stopPopTask();
//            }
//        }
//    }


    // 20180430 이모티콘 제거
//    private class OCompleteTask extends TimerTask { ///
//        @Override
//        public void run() {
//            KeyboardLogPrint.e("o timer run");
//            if (inputConnection != null && isInputViewShown()) {
//                ExtractedText et = inputConnection.getExtractedText(new ExtractedTextRequest(), 0);
//                if (et != null) {
//                    CharSequence seq = et.text;
//                    String temp = seq.toString();
//                    String[] temps = temp.split(" ");
//                    if (temps.length > 0) {
//                        String str = temps[temps.length - 1];
//                        mMatchedString = str;
//                        mSoundHandler.sendEmptyMessage(3);
//                    }
//                }
//            }
//            otimerCancel(0);
//        }
//    } ///
/**
 private class CompleteTask extends TimerTask {
 public void run() {
 KeyboardLogPrint.e("timer run");
 KeyboardLogPrint.w("mCompleteCount :: " + mCompleteCount);

 if (mTimer == null) {
 KeyboardLogPrint.i("quest timer is null");
 cancel();
 } else {
 KeyboardLogPrint.i("questtimer is not null");
 }


 if (mCompleteCount >= 3) {
 //                InputConnection ic = getCurrentInputConnection();  // 2017.11.03 InputConnection 전역 변수 사용하도록 변경
 if (inputConnection != null && isInputViewShown()) {
 ExtractedText et = inputConnection.getExtractedText(new ExtractedTextRequest(), 0);
 if (et != null) {
 mAutomataValues = kauto.getAutomataValue();
 kauto.setInitState();
 kauto.FinishAutomataWithoutInput();
 mComposing.setLength(0);
 inputConnection.finishComposingText();
 mIsComplete = true;
 mIsCompleteFromHandler = true;
 }
 }
 timerCancel(9);
 mCompleteCount = 0;
 }
 mCompleteCount++;
 }
 }**/

    // 20180430 이모티콘 제거
//    private void otimerCancel(int position) { ///
//        KeyboardLogPrint.e("o timer cancel position is :: " + position);
//        try {
//            if (mOTimer != null) {
//                mOTimer.cancel();
//                mOTimer.purge();
//                mOTimer = null;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    } ///

    /**
     * private void timerCancel(int position) {
     * KeyboardLogPrint.e("timerCancel position is :: " + position);
     * try {
     * if (mTimer != null) {
     * KeyboardLogPrint.w("quest timerCancel : " + position);
     * mTimer.cancel();
     * mTimer.purge();
     * mTimer = null;
     * }
     * } catch (Exception e) {
     * e.printStackTrace();
     * }
     * }
     **/
    public String getEditSentence() {
        CharSequence str = "";
        if (inputConnection != null) {
            ExtractedText et = inputConnection.getExtractedText(new ExtractedTextRequest(), 0);
            if (et != null) {
                str = et.text;
            }
        }
        LogPrint.d("edit str 1 :: " + str.toString());
        return str.toString();
    }
    public String getEditStr() {
        InputConnection ic = getCurrentInputConnection();
        ic = inputConnection;
        CharSequence str = "";
        if (ic != null) {
            ExtractedText et = ic.getExtractedText(new ExtractedTextRequest(), 0);
            if (et != null) {
                str = et.text;
            }
        }
        LogPrint.d("edit str :: " + str.toString());
        return str.toString();
    }

    public void setEditStr(String message) {
        InputConnection ic = getCurrentInputConnection();
        ic = inputConnection;
        if (ic != null) {
            KeyboardLogPrint.d("skkim null commitText 26 :: " + message);
            ic.commitText(message, 1);
        }
    }

    public void completeInputConnection() {
        KeyboardLogPrint.e("completeInputConnection called");
        try {
            KeyboardLogPrint.d("stop complete called 13");
            if (mCompleteHandler != null)
                mCompleteHandler.sendEmptyMessage(STOP_COMPLETE_TIMER);
            //timerCancel(6);
            mCompleteCount = 0;
            mComposing.setLength(0);
            mIsCompleteFromHandler = false;
            kauto.setInitState();
            mIsComplete = true;
            kauto.FinishAutomataWithoutInput();
            if (inputConnection != null)
                inputConnection.finishComposingText();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setShiftState() {
        KeyboardLogPrint.e("setShiftState before :: " + mShiftState);
        if (mShiftState == SHIFT_STATE_LOWER_CASE) {
            mShiftState = SHIFT_STATE_UPPER_CASE;
        } else if (mShiftState == SHIFT_STATE_UPPER_CASE) {
            mShiftState = SHIFT_STATE_ONLY_UPPER_CASE;
        } else if (mShiftState == SHIFT_STATE_ONLY_UPPER_CASE) {
            mShiftState = SHIFT_STATE_LOWER_CASE;
        } else {
            mShiftState = SHIFT_STATE_LOWER_CASE;
        }
        KeyboardLogPrint.e("setShiftState after :: " + mShiftState);
    }

    private void initShiftState() {
        mShiftState = SHIFT_STATE_LOWER_CASE;
    }

    public int getOCBPoint() {
        return mOCBSavePoint;
    }

    //public void setOCBPointZero() { mOCBSavePoint = 0; }

    public void setTotalPoint(int tp) {
        mOCBSavePoint = tp;
        SharedPreference.setInt(getApplicationContext(), Key.OCB_TOTAL_POINT, tp);
    }

    public void setMemberNumber(String number) {
        if (mComposing != null) {
            mComposing.append(number);
            KeyboardLogPrint.d("skkim null mComposing 17 :: " + mComposing.toString());
            inputConnection.setComposingText(mComposing, 1);
        }
    }

    private void showAdWithDate() {
        CustomAsyncTask task = new CustomAsyncTask(getApplicationContext());
        task.getBannerPointInfo(new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                if (result) {
                    try {
                        JSONObject object = (JSONObject) obj;
                        if (object != null) {
                            SharedPreference.setString(getApplicationContext(), Key.KEY_OCB_AD_BANNER_DATE, Util.GetTodayDate());
                            int reward_max_view = object.optInt("reward_max_view", 0);
                            int reward_point = object.optInt("reward_point", 0);
                            int reward_holding_time = object.optInt("reward_holding_time", 5);
                            reward_point = Math.abs(reward_point);
                            SharedPreference.setInt(getApplicationContext(), Key.KEY_OCB_AD_BANNER_POINT, reward_point);
                            SharedPreference.setInt(getApplicationContext(), Key.KEY_REWARD_COUNT, reward_holding_time);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //showAd();
                showNewAd();
            }
        });
    }

    private void showNewAd() {
        if (mMainKeyboardView != null) {
            mMainKeyboardView.setNews("N", new MainKeyboardView.NewsCallbackListener() {
                @Override
                public void onNewsReceived() {
                    if ( mobwithAdCount%3 == 0 ) {
                        mMainKeyboardView.loadMobWithAd();
                    } else {
                        mMainKeyboardView.loadMobWithAdWithoutCheck();
                    }
                    if ( mobwithAdCount >= 12 )
                        mobwithAdCount = 1;
                    else
                        mobwithAdCount++;
                    LogPrint.d("mobwith ad test mobwithAdCount :: " + mobwithAdCount);
                }
            });
        }
    }

    private void showAd() {
        // 2022.12.23 mobwith 광고로 대체되면서 아래 광고들 사용 안함.
        /*
        adCount++;
        String savedDate = SharedPreference.getString(getApplicationContext(), Key.KEY_OCB_AD_FREQUENCY_DATE);
        if (Util.GetTodayDate().equals(savedDate)) {
            try {
                String obj = SharedPreference.getString(getApplicationContext(), Key.KEY_OCB_AD_FREQUENCY);
                KeyboardLogPrint.d("freq obj :: " + obj);

                if (!TextUtils.isEmpty(obj)) {
                    JSONObject object = new JSONObject(obj);
                    String mobonFrequency = object.optString("mobon");
                    String mediationFrequency = object.optString("mediation");
                    String bannerFrequency = object.optString("banner");
                    String coupangFrequency = object.optString("coupang");
                    String criteoFrequency = object.optString("criteo");
                    String rewardFrequency = object.optString("reward");
                    if (TextUtils.isEmpty(mobonFrequency))
                        mobonFrequency = "-1";
                    if (TextUtils.isEmpty(mediationFrequency))
                        mediationFrequency = "-1";
                    if (TextUtils.isEmpty(bannerFrequency))
                        bannerFrequency = "-1";
                    if (TextUtils.isEmpty(coupangFrequency))
                        coupangFrequency = "-1";
                    if (TextUtils.isEmpty(criteoFrequency))
                        criteoFrequency = "-1";
                    if (TextUtils.isEmpty(rewardFrequency))
                        rewardFrequency = "-1";

                    String[] mobonArr = mobonFrequency.split(",");
                    String[] mediationArr = mediationFrequency.split(",");
                    String[] bannerArr = bannerFrequency.split(",");
                    String[] coupangArr = coupangFrequency.split(",");
                    String[] criteoArr = criteoFrequency.split(",");
                    String[] rewardArr = rewardFrequency.split(",");

                    int adType = AD_TYPE_NONE;
                    for (int i = 0; i < mobonArr.length; i++) {
                        if (adCount == Integer.valueOf(mobonArr[i])) {
                            adType = AD_TYPE_MOBON;
                            break;
                        }
                    }

                    if (adType == AD_TYPE_NONE) {
                        for (int i = 0; i < mediationArr.length; i++) {
                            if (adCount == Integer.valueOf(mediationArr[i])) {
                                adType = AD_TYPE_MEDIATION;
                                break;
                            }
                        }
                    }

                    if (adType == AD_TYPE_NONE) {
                        for (int i = 0; i < bannerArr.length; i++) {
                            if (adCount == Integer.valueOf(bannerArr[i])) {
                                adType = AD_TYPE_BANNER;
                                break;
                            }
                        }
                    }

                    if (adType == AD_TYPE_NONE) {
                        for (int i = 0; i < coupangArr.length; i++) {
                            if (adCount == Integer.valueOf(coupangArr[i])) {
                                adType = AD_TYPE_COUPANG;
                                break;
                            }
                        }
                    }

                    if (adType == AD_TYPE_NONE) {
                        for (int i = 0; i < criteoArr.length; i++) {
                            if (adCount == Integer.valueOf(criteoArr[i])) {
                                adType = AD_TYPE_CRITEO;
                                break;
                            }
                        }
                    }

                    if (adType == AD_TYPE_NONE) {
                        for (int i = 0; i < rewardArr.length; i++) {
                            if (adCount == Integer.valueOf(rewardArr[i])) {
                                adType = AD_TYPE_REWARD;
                                break;
                            }
                        }
                    }

                    LogPrint.d("AdType :: " + adType);
                    if (adType == AD_TYPE_MOBON) {
                        if (mMainKeyboardView != null) {
                            //mMainKeyboardView.loadMediaBanner();
                            mMainKeyboardView.setNews("N", new MainKeyboardView.NewsCallbackListener() {
                                @Override
                                public void onNewsReceived() {
                                    mMainKeyboardView.loadBanner(false);
                                }
                            });
                        }
                    } else if (adType == AD_TYPE_CRITEO) {
                        if (mMainKeyboardView != null) {
                            mMainKeyboardView.setNews("N", new MainKeyboardView.NewsCallbackListener() {
                                @Override
                                public void onNewsReceived() {
                                    if ( mMainKeyboardView != null )
                                        mMainKeyboardView.loadCriteoAD();
                                }
                            });
                        }
                    } else if (adType == AD_TYPE_MEDIATION) {
                        if (mMainKeyboardView != null) {
                            mMainKeyboardView.setNews("N", new MainKeyboardView.NewsCallbackListener() {
                                @Override
                                public void onNewsReceived() {
                                    if ( mMainKeyboardView != null )
                                        mMainKeyboardView.loadMixerBanner(0);
                                }
                            });
                        }
                    } else if (adType == AD_TYPE_COUPANG) {
                        if (mMainKeyboardView != null) {
                            mMainKeyboardView.setNews("N", new MainKeyboardView.NewsCallbackListener() {
                                @Override
                                public void onNewsReceived() {
                                    //mMainKeyboardView.loadCoupangBanner();
                                    mMainKeyboardView.loadCoupangDY_AD();
                                }
                            });
                        }
                    } else if (adType == AD_TYPE_BANNER) {
                        bannerIndex = SharedPreference.getInt(getApplicationContext(), Key.KEY_OCB_AD_BANNER_INDEX);
                        if (bannerIndex <= 2) {
                            if (mMainKeyboardView != null) {
                                mMainKeyboardView.setNews("N", new MainKeyboardView.NewsCallbackListener() {
                                    @Override
                                    public void onNewsReceived() {
                                        mMainKeyboardView.loadCustomBanner(bannerIndex);
                                    }
                                });
                            }
                        } else {
                            // 텍스트배너 2회 노출 이후부터 모비온 베너 호출
                            if (mMainKeyboardView != null) {

                                // mMainKeyboardView.loadCriteoBanner();
                                //mMainKeyboardView.loadMediaBanner();
                                mMainKeyboardView.setNews("N", new MainKeyboardView.NewsCallbackListener() {
                                    @Override
                                    public void onNewsReceived() {
                                        mMainKeyboardView.loadBanner(false);
                                    }
                                });
                            }
                        }
                    } else if ( adType == AD_TYPE_REWARD ) {
                        if (mMainKeyboardView != null) {
                            //mMainKeyboardView.loadMediaBanner();
                            mMainKeyboardView.setNews("N", new MainKeyboardView.NewsCallbackListener() {
                                @Override
                                public void onNewsReceived() {
                                    mMainKeyboardView.loadRewardBanner();
                                }
                            });
                        }
                    } else {
                        if (mMainKeyboardView != null) {
                            mMainKeyboardView.setNews("Y", new MainKeyboardView.NewsCallbackListener() {
                                @Override
                                public void onNewsReceived() {

                                }
                            });
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (mMainKeyboardView != null) {
                    mMainKeyboardView.setNews("Y", new MainKeyboardView.NewsCallbackListener() {
                        @Override
                        public void onNewsReceived() {

                        }
                    });
                }
            }
        } else {
            if (mMainKeyboardView != null)
                mMainKeyboardView.setNews("Y", new MainKeyboardView.NewsCallbackListener() {
                    @Override
                    public void onNewsReceived() {

                    }
                });
        }

        if (adCount >= 10)
            adCount = 0;

         */
    }

    private void getAdInfo() {
/**
 SharedPreference.setString(getApplicationContext(), Key.KEY_OCB_AD_FREQUENCY, "{\n" +
 "    \"mobon\": \"3,6\",\n" +
 "    \"mediation\": \"9\"\n" +
 "}");**/
        LogPrint.d("getAdInfo called");
        SharedPreference.setString(getApplicationContext(), Key.KEY_OCB_AD_FREQUENCY_DATE, Util.GetTodayDate());
        SharedPreference.setInt(getApplicationContext(), Key.KEY_OCB_AD_BANNER_INDEX, 0);
        CustomAsyncTask task = new CustomAsyncTask(getApplicationContext());
        task.getAdFrequency(new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                if (result) {
                    try {
                        JSONObject object = (JSONObject) obj;
                        if (object != null) {
                            SharedPreference.setString(getApplicationContext(), Key.KEY_OCB_AD_FREQUENCY, object.toString());
                            SharedPreference.setString(getApplicationContext(), Key.KEY_OCB_AD_FREQUENCY_DATE, Util.GetTodayDate());
                            if (downloadHandler != null) {
                                LogPrint.d("downloadHandler not null");
                                try {
                                    String linkPath = object.optString("brand_url");
                                    String iconPath = object.optString("brand_img_path");
                                    // for test
/*
                                    linkPath = "668^|^https://www.naver.com/";
                                    iconPath = "668^|^https://okcashbag.cashkeyboard.co.kr/img/util/2022/util_icon_img_668.png";
*/
                                    if ( !TextUtils.isEmpty(linkPath) && !TextUtils.isEmpty(iconPath) ) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("image_path", iconPath);
                                        bundle.putString("link_url", linkPath);
                                        Message msg = new Message();
                                        msg.setData(bundle);
                                        msg.what = 0;
                                        downloadHandler.sendMessage(msg);
                                    } else {
                                        if (mMainKeyboardView != null) {
                                            mMainKeyboardView.initBrandInfo();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                LogPrint.d("downloadHandler null");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    boolean isReload;

    private void saveOlabangDataAndShow() {
        LogPrint.d("saveOlabangDataAndShow");
        // 오늘 첫 키보드활성화 이므로 브랜드 아이콘을 다시 컬러가 있는 상태로 변경한다.
        if (mMainKeyboardView != null) {
            //mMainKeyboardView.setBrandIcon(true);
            mMainKeyboardView.initAdCloseCount();
        }
        LogPrint.d("isReload :: " + isReload);
        if (!isReload) {
            // 현재 사용하지 않는 블럭
            MobonUtils.getADID(getApplicationContext());
            MobonSimpleSDK sdk = new MobonSimpleSDK(getApplicationContext(), "okaycashbag");
            //MobonSimpleSDK sdk = MobonSimpleSDK.get(getApplicationContext());
            if (sdk != null) {
                LogPrint.d("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!   saveOlabangDataAndShow sdk run");
                sdk.onConnectSDKUrlAPI();
            }
        }

        //String lDate = SharedPreference.getString(getApplicationContext(), Key.KEY_LIVE_DATE);
        String uuid = SharedPreference.getString(getApplicationContext(), Key.KEY_OCB_USER_ID);
        CustomAsyncTask task = new CustomAsyncTask(getApplicationContext());
        task.getOlabangList(new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                /**
                 liveDate = Util.GetTodayDateDash();
                 liveFrequency = 1;
                 liveTime = 0L;
                 SharedPreference.setString(getApplicationContext(), Key.KEY_LIVE_DATE, liveDate);
                 SharedPreference.setLong(getApplicationContext(), Key.KEY_LIVE_TIME, liveTime);
                 SharedPreference.setInt(getApplicationContext(), Key.KEY_LIVE_FREQUENCY, liveFrequency);**/

                if (result) {
                    isReload = false;
                    try {
                        JSONObject object = (JSONObject) obj;
                        JSONArray arr = new JSONArray();
                        if (object != null) {
                            JSONObject streamingsObj = object.optJSONObject("streamings");
                            if (streamingsObj != null) {
                                JSONArray liveBoardsArr = streamingsObj.optJSONArray("liveBoards");
                                if (liveBoardsArr != null && liveBoardsArr.length() > 0) {
                                    for (int i = 0; i < liveBoardsArr.length(); i++) {
                                        JSONObject liveObj = liveBoardsArr.optJSONObject(i);
                                        liveObj.put("isDel", false);
                                        arr.put(i, liveObj);
                                    }
                                    streamingsObj.remove("liveBoards");
                                    streamingsObj.put("liveBoards", arr);
                                }
                            }

                            SharedPreference.setString(getApplicationContext(), Key.KEY_OCB_OLABANG_DATE, Util.GetTodayDate());
                            SharedPreference.setString(getApplicationContext(), Key.KEY_OCB_OLABANG_DATA, object.toString());
                            if (mMainKeyboardView != null) {
                                mMainKeyboardView.getOlabangItem();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        isReload = true;
                    }

                } else
                    isReload = true;
            }
        });
    }

    public void clear() {
        try {
            //timerCancel(6);
/*
            if (mDialogHandler != null)
                mDialogHandler.sendEmptyMessage(HIDE_COUPANG_AD_DIALOG);
*/
            KeyboardLogPrint.d("stop complete called 14");
            if (mCompleteHandler != null)
                mCompleteHandler.sendEmptyMessage(STOP_COMPLETE_TIMER);
            mCompleteCount = 0;
            if (mComposing != null) {
                mComposing.setLength(0);
            }

            mIsCompleteFromHandler = false;
            mIsComplete = true;
            if (kauto != null) {
                kauto.setInitState();
                kauto.FinishAutomataWithoutInput();
            }

            if (inputConnection != null)
                inputConnection.finishComposingText();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSearchListener(SearchListener listener) {
        this.searchListener = listener;
    }

    public interface SearchListener {
        void onSearch();
    }

    public int getScreenWidth() {
        Display display = this.getWindow().getWindow().getWindowManager().getDefaultDisplay();
        int realScreenWidth = 0;
        if (Build.VERSION.SDK_INT >= 17) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                WindowMetrics windowMetrics = wm.getCurrentWindowMetrics();
                android.graphics.Insets insets = windowMetrics.getWindowInsets().getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
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
        return realScreenWidth;
    }

    private void getTotalPoint() {
        LogPrint.d("getTotalPoint");
        String uuid = SharedPreference.getString(getApplicationContext(), Key.KEY_OCB_USER_ID);
        try {
            E_Cipher cp = E_Cipher.getInstance();
            uuid = cp.Decode(getApplicationContext(), uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogPrint.d("uuid :: " + uuid);
        if (TextUtils.isEmpty(uuid)) {
            mMainKeyboardView.setPointVisibie(View.GONE);
            SharedPreference.setInt(getApplicationContext(), Key.OCB_TOTAL_POINT, -1);
            SharedPreference.setInt(getApplicationContext(), Key.KEY_OCB_COUNT, 0);
        } else {
            int totalPoint = SharedPreference.getInt(getApplicationContext(), Key.OCB_TOTAL_POINT);
            mOCBPointCount = SharedPreference.getZeroInt(getApplicationContext(), Key.KEY_OCB_COUNT);
            if (totalPoint < 0) {
                mOCBSavePoint = 0;
                mMainKeyboardView.setPointVisibie(View.GONE);
                CustomAsyncTask task = new CustomAsyncTask(getApplicationContext());
                task.getTotalPoint(new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                    @Override
                    public void onResponse(boolean result, Object obj) {
                        if (result) {
                            try {
                                JSONObject object = (JSONObject) obj;
                                if (object != null) {
                                    boolean rt = object.optBoolean("Result");
                                    if (rt) {
                                        int totalPoint = object.optInt("total_point");
                                        LogPrint.d("totalPint :: " + totalPoint);
                                        if (totalPoint >= 0) {
                                            SharedPreference.setInt(getApplicationContext(), Key.OCB_TOTAL_POINT, totalPoint);
                                            if (totalPoint > 0) {
                                                mMainKeyboardView.setPointVisibie(View.VISIBLE);
                                            } else {
                                                mMainKeyboardView.setPointVisibie(View.GONE);
                                            }
                                            mOCBSavePoint = totalPoint;
                                            LogPrint.d("kkkssskkkkkk getTotalPoint mOCBSavePoint :: " + mOCBSavePoint);
                                            mMainKeyboardView.setOCBPoint(totalPoint);
                                            LogPrint.d("set ocb point get total point ");
                                        }
                                    } else {
                                    }
                                } else {
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                        }
                    }
                });
            } else {
                mOCBSavePoint = totalPoint;
                LogPrint.d("kkkssskkkkkk getTotalPoint 1 mOCBSavePoint :: " + mOCBSavePoint);
                mMainKeyboardView.setOCBPoint(totalPoint);
                LogPrint.d("set ocb point get total point 1");
                if (totalPoint > 0)
                    mMainKeyboardView.setPointVisibie(View.VISIBLE);
                else
                    mMainKeyboardView.setPointVisibie(View.GONE);
            }
        }
    }

    private void setNoUUIDUserInfo() {
        mMainKeyboardView.setPointVisibie(View.GONE);
        SharedPreference.setInt(getApplicationContext(), Key.OCB_TOTAL_POINT, -1);
        SharedPreference.setInt(getApplicationContext(), Key.KEY_OCB_COUNT, 0);
    }

    public void past(String str) {
        isCharactorEntered = true;
        KeyboardLogPrint.d("stop complete called 15");
        if (mCompleteHandler != null)
            mCompleteHandler.sendEmptyMessage(STOP_COMPLETE_TIMER);
        mCompleteCount = 0;
        mComposing.append(str);
        if (inputConnection != null) {
            KeyboardLogPrint.d("skkim null commitText 27 :: " + mComposing.toString());
            inputConnection.commitText(mComposing, 1);
            inputConnection.finishComposingText();
            if (kauto != null) {
                kauto.setInitState();
                kauto.FinishAutomataWithoutInput();
            }
            mComposing.setLength(0);
            mIsComplete = true;
            mIsCompleteFromHandler = false;
        }
    }

    private boolean isExceptPackage() {
        String[] exceptPackage = {
                "com.samsung.android.dialer",
                "com.samsung.android.app.contacts",
                "com.android.vending",
                "com.android.contacts",
                "com.kakao.talk",
                "kr.co.hiworks.mobile",
                "kr.co.hiworks.messenger",
                "com.nhn.android.band"
        };

        String currentApp = mAttribute.packageName;

        boolean isFind = false;

        if (currentApp != null) {
            KeyboardLogPrint.e("currentApp : " + currentApp);
            for (String ep : exceptPackage) {
                if (currentApp.equalsIgnoreCase(ep) || currentApp.startsWith(ep)) {
                    isFind = true;
                    break;
                }
            }
        }
        return isFind;
    }

    public boolean isKoreanKeyboaard() {
        if (kauto != null) {
            return kauto.IsKoreanMode();
        }
        return true;
    }

    public void downloadBrandIcon(String imagePath, String linkUrl) {
        LogPrint.d("imagePath :: " + imagePath + " , linkUrl :: " + linkUrl);
        if (!TextUtils.isEmpty(imagePath) && !TextUtils.isEmpty(linkUrl)) {
            try {
                String[] arr = imagePath.split("\\^\\|\\^");
                if (arr != null && arr.length == 2) {
                    String path = arr[1];
                    String fileName = path.substring(path.lastIndexOf('/') + 1);
                    LogPrint.d("fileName :: " + fileName);
                    String destination = getFilesDir().getAbsolutePath() + File.separator + "BRAND_TEMP" + File.separator; // 임시 다운받을 파일 위치
                    Download download = new Download(destination, fileName, linkUrl);
                    download.execute(imagePath);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class Download extends AsyncTask<String, String, String> {
        private File file;
        private String downloadLocation, linkUrl, iconUrl, fileName;

        public Download(String downloadLocation, String fileName, String linkUrl) {
            this.downloadLocation = downloadLocation;
            this.linkUrl = linkUrl;
            this.fileName = fileName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... aurl) {
            int count;
            try {
                iconUrl = aurl[0];
                String[] arr = iconUrl.split("\\^\\|\\^");
                KeyboardLogPrint.d("arr.length :: " + arr.length);
                KeyboardLogPrint.d("arr[0] :: " + arr[0]);
                KeyboardLogPrint.d("arr[1] :: " + arr[1]);
                String realUrl = arr[1];
                URL url = new URL(realUrl);
                URLConnection connection = url.openConnection();
                connection.connect();

                int lenghtOfFile = connection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream());
                file = new File(downloadLocation);
                if (!file.exists())
                    file.mkdirs();
                file = new File(downloadLocation + fileName);
                FileOutputStream output = new FileOutputStream(file);

                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    output.write(data, 0, count);
                    KeyboardLogPrint.d("write");
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            KeyboardLogPrint.d(progress[0]);
        }

        @Override
        protected void onPostExecute(String unused) {
            KeyboardLogPrint.e("download onPostExecute");
            if (!TextUtils.isEmpty(linkUrl) && !TextUtils.isEmpty(iconUrl)) {
                try {
                    String[] linkArr = linkUrl.split("\\^\\|\\^");
                    String[] iconArr = iconUrl.split("\\^\\|\\^");
                    String linkPre = linkArr[0];
                    String iconPre = iconArr[0];
                    String linkPost = linkArr[1];
                    LogPrint.d("linkPre :: " + linkPre);
                    LogPrint.d("iconPre :: " + iconPre);
                    if (!TextUtils.isEmpty(linkPre) && !TextUtils.isEmpty(iconPre)) {
                        if (linkPre.equals(iconPre)) {
                            String tempPath = getFilesDir().getAbsolutePath() + File.separator + "BRAND_TEMP" + File.separator + fileName; // 임시 다운받을 파일 위치
                            String realPath = getFilesDir().getAbsolutePath() + File.separator + "BRAND" + File.separator + fileName; // 다운받을 파일 위치
                            File brandDirectory = new File(getFilesDir().getAbsolutePath() + File.separator + "BRAND" + File.separator);
                            if (!brandDirectory.exists())
                                brandDirectory.mkdir();
                            else {
                                LogPrint.d("list file length :: " + brandDirectory.listFiles().length);
                                if (brandDirectory.listFiles().length > 0) {
                                    for (int i = 0; i < brandDirectory.listFiles().length; i++) {
                                        File inFile = brandDirectory.listFiles()[i];
                                        inFile.delete();
                                    }
                                }
                            }
                            File tempFile = new File(tempPath);
                            File realFile = new File(realPath);
                            LogPrint.d("tempFile exist :: " + tempFile.exists());
                            LogPrint.d("realFile exist :: " + realFile.exists());
                            tempFile.renameTo(realFile);
                            LogPrint.d("realFile exist 1 :: " + realFile.exists());
                            tempFile.delete();
                            LogPrint.d("realFile exist 2 :: " + realFile.exists() + " , realPath :: " + realPath);
                            AIKBD_DBHelper helper = new AIKBD_DBHelper(getApplicationContext());
                            helper.deleteBrandUrl();
                            helper.insertBrandUrl(linkPost, realPath);

                            if (mMainKeyboardView != null) {
                                mMainKeyboardView.setBrandTabIcon();
                            }
                        } else {
                            AIKBD_DBHelper helper = new AIKBD_DBHelper(getApplicationContext());
                            helper.deleteBrandUrl();
                        }
                    } else {
                        AIKBD_DBHelper helper = new AIKBD_DBHelper(getApplicationContext());
                        helper.deleteBrandUrl();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    AIKBD_DBHelper helper = new AIKBD_DBHelper(getApplicationContext());
                    helper.deleteBrandUrl();
                }
            } else {
                AIKBD_DBHelper helper = new AIKBD_DBHelper(getApplicationContext());
                helper.deleteBrandUrl();
            }
        }
    }

    private void recoverBackupKeyboard() {
        mBackupKoreanKeyboardMode = -1;
        mBackupCurKeyboard = null;
        mBackupKauto = null;
//        if (mBackupCurKeyboard != null && mBackupKauto != null) {
//            LogPrint.d("skkim mBackupKoreanKeyboardMode :: " + mBackupKoreanKeyboardMode);
//            //SharedPreference.setInt(getApplicationContext(), Common.PREF_KEYBOARD_MODE, mBackupKoreanKeyboardMode);
//            mKoreanKeyboardMode = mBackupKoreanKeyboardMode;
//            mCurKeyboard = mBackupCurKeyboard;
//            kauto = mBackupKauto;
//            mKoreanKeyboard = mBackupKoreanKeyboard;
//
//            mBackupKoreanKeyboardMode = -1;
//            mBackupCurKeyboard = null;
//            mBackupKauto = null;
//        } else {
//            LogPrint.d("skkim mBackupCurKeyboard nulkl :: ");
//        }
    }

    public static boolean IsBackupKeyboardExist() {
        if (mBackupKauto == null && mBackupCurKeyboard == null) {
            LogPrint.d("backup keyboard gubun IsBackupKeyboardExist null");
            return false;
        } else {
            LogPrint.d("backup keyboard gubun IsBackupKeyboardExist not null");
            return true;
        }
    }


    private void registerVolumeReceiver() {
        if ( myReceiver == null )
            myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.media.VOLUME_CHANGED_ACTION");
//        registerReceiver(myReceiver, filter);
        // target 34 대응
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(myReceiver, filter, Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(myReceiver, filter);
        }
        LogPrint.d("regist receiver SoftKeyboard 2");
    }

    private boolean isSpecialKey(int primaryCode) {
        if (mCurKeyboard == mKoreanKeyboard && mKoreanKeyboardMode == Common.MODE_CHUNJIIN_PLUS && primaryCode == -226) {
            return false;
        } else if (primaryCode < 0 || primaryCode == 32 || primaryCode == -226) {
            return true;
        } else {
            if (mCurKeyboard == mKoreanKeyboard && mKoreanKeyboardMode == Common.MODE_CHUNJIIN_PLUS) {
                if (primaryCode == 66 || primaryCode == 46) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    private void unregisterVolumeReceiver() {
        if ( myReceiver != null ) {
            LogPrint.d("un regist receiver SoftKeyboard 1");
            unregisterReceiver(myReceiver);
        }
        else
            LogPrint.d("myReceiver null");
/*
        if ( mMainKeyboardView != null ) {
            mMainKeyboardView.unregisterReceiver();
        }

 */
    }
// 동동이 코드
//    private void showOverlayView() {
//        LogPrint.d("overlay showOverlayView isTargetAdShow :: " + isTargetAdShow);
//        if ( !isTargetAdShow )
//            return;
//
//
//        getEditSentence();
//
//        if (TextUtils.isEmpty(getEditStr()) )
//            return;
//        // api 통신 후 아래 로직 수행.
//        try {
//            if(wm != null) {
//                if(mView != null) {
//                    wm.removeView(mView); // View 초기화
//                    wm.removeViewImmediate(mView);
//                    mView = null;
//                }
//                wm = null;
//            }
//
//            LayoutInflater inflate = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            // inflater 를 사용하여 layout 을 가져오자
//            wm = (WindowManager) getSystemService(WINDOW_SERVICE);
//            // 윈도우매니저 설정
//
//            params = new WindowManager.LayoutParams(
//                    Common.convertDpToPx(getApplicationContext(), 100),
//                    Common.convertDpToPx(getApplicationContext(), 100),
//                    Build.VERSION.SDK_INT < Build.VERSION_CODES.O?
//                            WindowManager.LayoutParams.TYPE_PHONE : WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
//                    // Android O 이상인 경우 TYPE_APPLICATION_OVERLAY 로 설정
//                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                    PixelFormat.TRANSLUCENT);
//
//
//            params.gravity = Gravity.RIGHT|Gravity.BOTTOM;
//            // 위치 지정
//            ArrayList<Integer> size = getScreenSize();
//
//            int topMargin = size.get(1) - Common.convertDpToPx(getApplicationContext(), 150);
//            int leftMargin = size.get(0) - Common.convertDpToPx(getApplicationContext(), 100);
//            mView = inflate.inflate(R.layout.test_window_manager, null);
//            LogPrint.d("topMargin :: " + topMargin + " , deviceHeight :: " + size.get(1) + " , m size :: " + Common.convertDpToPx(getApplicationContext(), 150));
//            LogPrint.d("leftMargin :: " + leftMargin + " , deviceWidth :: " + size.get(0) + " , m size :: " + Common.convertDpToPx(getApplicationContext(), 100));
////            params.y = topMargin;
////            params.x = leftMargin;
//            // view_in_service.xml layout 불러오기
//            // mView.setOnTouchListener(onTouchListener);
//            // Android O 이상의 버전에서는 터치리스너가 동작하지 않는다. ( TYPE_APPLICATION_OVERLAY 터치 미지원)
//
//            final ImageView btn_img = mView.findViewById(R.id.btn_img);
//
//            ImageModule.with(getApplicationContext()).load("https://shop-phinf.pstatic.net/20191003_38/1570080208791p7ClY_JPEG/7440043339968259_380868623.jpg?type=m510").apply(new RequestOptions().circleCrop()).into(btn_img);
//
//            GestureDetector detector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
//                //화면이 눌렸을 때
//                @Override
//                public boolean onDown(MotionEvent e) {
//                    LogPrint.d("onDown() 호출됨");
//                    return true;
//                }
//
//                //화면이 눌렸다 떼어지는 경우
//                @Override
//                public void onShowPress(MotionEvent e) {
//                    LogPrint.d("onShowPress() 호출됨");
//                    if(wm != null) {
//                        if(mView != null) {
//                            wm.removeView(mView); // View 초기화
//                            wm.removeViewImmediate(mView);
//                            mView = null;
//                        }
//                        wm = null;
//                    }
//
//                    String link = "https://smartstore.naver.com/banessum/products/4686774702?n_media=27758&n_query=%EC%B2%AD%EB%B0%94%EC%A7%80&n_rank=2&n_ad_group=grp-a001-02-000000014402699&n_ad=nad-a001-02-000000087112017&n_campaign_type=2&n_mall_id=ncp_1nptwc_01&n_mall_pid=4686774702&n_ad_group_type=2&NaPm=ct%3Dlfw2kbjk%7Cci%3D0A000016nMny%2DrVRwv2p%7Ctr%3Dpla%7Chk%3D370dc0c21051d77ae3863d9f04a655a07a5a9763";
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setData(Uri.parse(link));
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    getApplicationContext().startActivity(intent);
//                }
//
//                //화면이 한 손가락으로 눌렸다 떼어지는 경우
//                @Override
//                public boolean onSingleTapUp(MotionEvent e) {
//                    LogPrint.d("onSingleTapUp() 호출됨");
//                    return true;
//                }
//
//                //화면이 눌린채 일정한 속도와 방향으로 움직였다 떼어지는 경우
//                @Override
//                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//                    LogPrint.d("onScroll() 호출됨 => " + distanceX + ", " + distanceY);
//                    return false;
//                }
//
//                //화면을 손가락으로 오랫동안 눌렀을 경우
//                @Override
//                public void onLongPress(MotionEvent e) {
//                    LogPrint.d("onLongPress() 호출됨");
//                }
//
//                //화면이 눌린채 손가락이 가속해서 움직였다 떼어지는 경우
//                @Override
//                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//                    LogPrint.d("onFling() 호출됨 => " + velocityX + ", " + velocityY);
//                    if(wm != null) {
//                        if(mView != null) {
//                            wm.removeView(mView); // View 초기화
//                            wm.removeViewImmediate(mView);
//                            mView = null;
//                        }
//                        wm = null;
//                    }
//                    return true;
//                }
//            });
//            btn_img.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    detector.onTouchEvent(event);
//                    return true;
//                }
//            });
//            // btn_img 에 android:filterTouchesWhenObscured="true" 속성 추가하면 터치리스너가 동작한다.
////            btn_img.setOnTouchListener(new View.OnTouchListener() {
////                @Override
////                public boolean onTouch(View view, MotionEvent motionEvent) {
////                    switch (motionEvent.getAction()){
////                        case MotionEvent.ACTION_DOWN:
////                            Log.d("test","touch DOWN ");
////                            break;
////                        case MotionEvent.ACTION_UP:
////                            Log.d("test","touch UP");
////                            break;
////                        case MotionEvent.ACTION_MOVE:
////                            Log.d("test","touch move ");
////                            break;
////                    }
////                    return false;
////                }
////            });
//            wm.addView(mView, params); // 윈도우에 layout 을 추가 한다.
//
//            ObjectAnimator animation = ObjectAnimator.ofFloat(mView, "rotationY", 0.0f, 360f);
//            animation.setDuration(3600);
//            animation.setRepeatCount(3);
//            animation.setInterpolator(new AccelerateDecelerateInterpolator());
//            animation.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private ArrayList<Integer> getScreenSize() {
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        WindowManager windowService = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
//        windowService.getDefaultDisplay().getMetrics(displayMetrics);
//        int height = displayMetrics.heightPixels;
//        int width = displayMetrics.widthPixels;
//        ArrayList<Integer> arr = new ArrayList<Integer>();
//        arr.add(width);
//        arr.add(height);
//        return arr;
//    }

    private LatinKeyboard getNullKeyboard() {
        mQSymbolsKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_symbol_q_1); // 쿼티용 기호
        mQSymbolsKeyboard1 = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_symbol_q_2); // 쿼티용 기호 shift
        mSymbolsKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_symbols); // 기호
        mNumberKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_number); // 기호 SHIFT
        mNumberOnlyKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_number_only); // mumber only
        mNumberSignedKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_number_signed); // mumber signed
        mNumberDecimalKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_number_decimal); // mumber decimal
        mNumberPhoneKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_number_phone); // mumber phone
        mPhoneSymbolKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_phone_symbol); // phone symbol
        mSymbolsShiftedKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_symbols_1);
        mSymbolsShiftedKeyboard1 = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_symbols_2);

        boolean isQwertyNumSet = SharedPreference.getBoolean(getApplicationContext(), Common.PREF_QWERTY_NUM_SETTING);
        if (isQwertyNumSet) {
            mQwertyNum = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_qwerty_n); // 영문 쿼티 + 숫자
            mQwerty = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_qwerty);
            mQKoreanKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_korean_n); // 한글
//                    mKoreanShiftedKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_n_korean_shifted, true); // 한글 SHIFT
            mSejongKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_sejong); // 천지인 keyboard
            mDanKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_dan); // 단모음 keyboard
            mNaraKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_nara); // 나랏글 keyboard
            mSejongPlusKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_sejong_plus); // 천지인 플러스 keyboard
        } else {
            mQwerty = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_qwerty); // 영문 쿼티
            mQwertyNum = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_qwerty_n); // 영문 쿼티 + 숫자
            mQKoreanKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_korean); // 한글
//                    mKoreanShiftedKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_korean_shifted, true); // 한글 SHIFT
            mSejongKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_sejong); // 천지인 keyboard
            mDanKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_dan); // 단모음 keyboard
            mNaraKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_nara); // 나랏글 keyboard
            mSejongPlusKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_sejong_plus); // 천지인 플러스 keyboard
        }

        mEmojiKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_emoji);
        mEmoticonKeyboard = new LatinKeyboard(getApplicationContext(), R.xml.aikbd_emoticon);

        int keyHeightLevel = SharedPreference.getInt(getApplicationContext(), Common.PREF_KEYBOARD_SIZE_LEVEL) < 0 ? Common.DEFAULT_KEYBOARD_SIZE : SharedPreference.getInt(getApplicationContext(), Common.PREF_KEYBOARD_SIZE_LEVEL);
        float fLevel = 0.9f;
        try {
//                        fLevel =  Float.parseFloat(String.format("%.2f", 0.90 + (float) (2 * keyHeightLevel) / 100));
            fLevel = Common.GetHeightValue(keyHeightLevel);
            setKeyHeight(fLevel);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int kind = SharedPreference.getInt(this, Common.PREF_KEYBOARD_MODE);
        if (kind == Common.MODE_CHUNJIIN) {
            if (mMainKeyboardView != null)
                mMainKeyboardView.setPreviewEnabled(mIsPreviewSet, false);
            return mSejongKeyboard;
        } else if (kind == Common.MODE_QUERTY) {
            if (mMainKeyboardView != null)
                mMainKeyboardView.setPreviewEnabled(mIsPreviewSet, true);
            mKoreanKeyboard = mQKoreanKeyboard;
            return mKoreanKeyboard;
        } else if (kind == Common.MODE_NARA) {
            if (mMainKeyboardView != null)
                mMainKeyboardView.setPreviewEnabled(mIsPreviewSet, true);
            return mNaraKeyboard;
        } else if (kind == Common.MODE_DAN) {
            if (mMainKeyboardView != null)
                mMainKeyboardView.setPreviewEnabled(mIsPreviewSet, true);
            return mDanKeyboard;
        } else if (kind == Common.MODE_CHUNJIIN_PLUS) {
            if (mMainKeyboardView != null)
                mMainKeyboardView.setPreviewEnabled(mIsPreviewSet, false);
            return mSejongPlusKeyboard;
        } else {
            if (mMainKeyboardView != null)
                mMainKeyboardView.setPreviewEnabled(mIsPreviewSet, false);
            return mSejongKeyboard;
        }
    }

    public void getGameStatus() {
        int game_status = SharedPreference.getInt(getApplicationContext(), Key.KEY_GAME_STATUS);
        if ( Common.GAME_STATUS_NONE == game_status ) {
            CustomAsyncTask task1 = new CustomAsyncTask(getApplicationContext());
            task1.getJointFinish("", String.valueOf(mKeyboardActiveCount),  new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                @Override
                public void onResponse(boolean result, Object obj) {
                    if (result) {
                        try {
                            JSONObject object = (JSONObject) obj;
                            if (object != null) {
                                JSONObject game_obj = object.optJSONObject("gamezone");
                                if ( game_obj != null ) {
                                    boolean game_result = game_obj.optBoolean("Result");
                                    String game_YN = game_obj.optString("use_YN");
                                    if ( game_result ) {
                                        if ( mMainKeyboardView != null ) {
                                            int status = Common.GAME_STATUS_NO;
                                            if ( "Y".equals(game_YN) ) {
                                                status = Common.GAME_STATUS_YES;
                                            }
                                            SharedPreference.setInt(getApplicationContext(), Key.KEY_GAME_STATUS, status);
                                            mMainKeyboardView.gameZoneVisible(game_YN);
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }
}
