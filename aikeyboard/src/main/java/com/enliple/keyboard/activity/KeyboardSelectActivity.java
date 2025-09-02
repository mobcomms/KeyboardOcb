package com.enliple.keyboard.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Insets;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.enliple.keyboard.Cipher.E_Cipher;
import com.enliple.keyboard.R;
import com.enliple.keyboard.ad.Listener;
import com.enliple.keyboard.adapter.KeyboardSelectAdapter;
import com.enliple.keyboard.adapter.KeyboardSelectData;
import com.enliple.keyboard.common.Common;
import com.enliple.keyboard.common.KeyboardLogPrint;
import com.enliple.keyboard.common.ThemeManager;
import com.enliple.keyboard.common.ThemeModel;
import com.enliple.keyboard.imgmodule.ImageModule;
import com.enliple.keyboard.imgmodule.load.DataSource;
import com.enliple.keyboard.imgmodule.load.engine.ImageModuleException;
import com.enliple.keyboard.imgmodule.load.resource.gif.GifDrawable;
import com.enliple.keyboard.imgmodule.request.RequestListener;
import com.enliple.keyboard.imgmodule.request.target.Target;
import com.enliple.keyboard.network.CustomAsyncTask;
import com.enliple.keyboard.ui.AikbdSelectDialog;
import com.enliple.keyboard.ui.common.Key;
import com.enliple.keyboard.ui.common.LogPrint;
import com.rake.android.rkmetrics.RakeAPI;
import com.skplanet.pdp.sentinel.shuttle.OCBLogSentinelShuttle;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class KeyboardSelectActivity extends Activity implements KeyboardSelectAdapter.ItemClickListener, KeyboardView.OnKeyboardActionListener {
    public static Activity mActivity = null;
    private String C_KEYBOARD;
    private Timer mTimer = null;
    private boolean mIsOpenPicker = false;
    private boolean mIsMovedSelectKeyboard = false;
    private Button mBtn = null;
    private TextView btnClose = null;
    private RelativeLayout stepTwoLayer, stepThreeLayer;
    private NestedScrollView stepOneLayer;
    private ImageView one_gif, two_gif;
    private AnimationDrawable oneDrawable, twoDrawable;
//    private TextView bot_exp;
    private NestedScrollView scrollView;
    private RecyclerView selectRecyclerView;
    //private TextView mExplain = null;
    private String mKeyboardName = null;
    //private ImageView mBackgroundLayer = null;
    //private RelativeLayout mCustomLayer = null;
    //private String mBackgroundResourceId = null;
    private String mGetGoneSetting = "N";
    private KeyboardSelectAdapter mAdapter;
    private int selectPosition = 0;
    private ArrayList<KeyboardSelectData> kbdArray = new ArrayList<KeyboardSelectData>();
    private RakeAPI rake;
    private boolean isStepOneSetted = false;
    private boolean isStepTwoSetted = false;
    private boolean isStepThreeSetted = false;
    private ImageView point_guide;

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
    private LinearLayout keyboard_view_layer;
    private RelativeLayout match_container;
    private ThemeModel mThemeModel;
    private int mBgAlpha;
    private TextView mTopLine;
    private boolean mSetPreview = false;
    private boolean mIsQwertyNumSet = false;
    private LatinKeyboardView kv;
    private boolean isQwertySelected = false;
    private View bot_empty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (CustomAsyncTask.GUBUN_RELEASE.equals(CustomAsyncTask.gubun))
            rake = RakeAPI.getInstance(KeyboardSelectActivity.this, Common.LIVE_TOKEN, RakeAPI.Env.LIVE, RakeAPI.Logging.DISABLE);
        else
            rake = RakeAPI.getInstance(KeyboardSelectActivity.this, Common.DEV_TOKEN, RakeAPI.Env.DEV, RakeAPI.Logging.DISABLE);
        mKeyboardName = SharedPreference.getString(KeyboardSelectActivity.this, Common.PREF_KEYBOARD_NAME);
        mKeyboardName = getResources().getString(R.string.ocb_keyboard_name);
//        IgawCommon.startApplication(getApplicationContext());
        // 2017.08.25 대표님 지시사항으로 첫 설치 시 10일간 광고 미노출 되도록 설정
        long firstInstallTime = SharedPreference.getLong(KeyboardSelectActivity.this, Common.PREF_FIRST_INSTALL_TIME);
        if (firstInstallTime < 0) {
            firstInstallTime = System.currentTimeMillis();
            SharedPreference.setLong(KeyboardSelectActivity.this, Common.PREF_FIRST_INSTALL_TIME, firstInstallTime);
        }
        String currentKeyboard = Settings.Secure.getString(getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
        KeyboardLogPrint.w("currentKeyboard :: " + currentKeyboard);
        //String packageName = SharedPreference.getString(KeyboardSelectActivity.this, Common.PREF_APP_PACKAGE);

//        C_KEYBOARD = getPackageName() + "/com.enliple.keyboard.activity.SoftKeyboard";
        C_KEYBOARD = Common.TARGET_PACKAGENAME + "/com.enliple.keyboard.activity.SoftKeyboard";
        KeyboardLogPrint.w("C_KEYBOARD :: " + C_KEYBOARD);

        if (C_KEYBOARD.equals(currentKeyboard)) {
            Intent intent = new Intent(KeyboardSelectActivity.this, KeyboardSettingsActivity.class);
            startActivity(intent);
            finish();
            return;
        } else {
            if (CustomAsyncTask.GUBUN_RELEASE.equals(CustomAsyncTask.gubun))
                rake = RakeAPI.getInstance(KeyboardSelectActivity.this, Common.LIVE_TOKEN, RakeAPI.Env.LIVE, RakeAPI.Logging.DISABLE);
            else
                rake = RakeAPI.getInstance(KeyboardSelectActivity.this, Common.DEV_TOKEN, RakeAPI.Env.DEV, RakeAPI.Logging.DISABLE);
            mActivity = this;
            setContentView(R.layout.activity_keyboard_select);

            View root_layout = findViewById(R.id.root_layout);
            Common.SetInset(root_layout);

            btnClose = findViewById(R.id.btn_close);
            btnClose.setOnClickListener(mClickListener);

            stepOneLayer = findViewById(R.id.stepOneLayer);
            stepTwoLayer = findViewById(R.id.stepTwoLayer);
            stepThreeLayer = findViewById(R.id.stepThreeLayer);
            selectRecyclerView = findViewById(R.id.selectRecyclerView);
//            bot_exp = findViewById(R.id.bot_exp);
            point_guide = findViewById(R.id.point_guide);
            scrollView = findViewById(R.id.scrollView);
            mFirstTabLayer = (RelativeLayout) findViewById(R.id.first_tab);
            mTimedealTabLayer = (RelativeLayout) findViewById(R.id.timedeal_tab);
            mSecondTabLayer = (RelativeLayout) findViewById(R.id.second_tab);
            mShoppingTabLayer = (RelativeLayout) findViewById(R.id.shopping_tab);
            mFourthLayer = (RelativeLayout) findViewById(R.id.fourth_tab);
            mMoreLayer = findViewById(R.id.more_tab);
            mGameLayer = findViewById(R.id.game_tab);
            mImgFirst = (ImageView) findViewById(R.id.img_first);
            mImgTimedeal = (ImageView) findViewById(R.id.img_timedeal);
            mImgSecond = (ImageView) findViewById(R.id.img_second);
            mImgShopping = (ImageView) findViewById(R.id.img_shopping);
            mImgFourth = (ImageView) findViewById(R.id.img_fourth);
            mImgMore = findViewById(R.id.img_more);
            keyboard_view_layer = findViewById(R.id.keyboard_view_layer);
            match_container = findViewById(R.id.match_container);
            mTopLine = (TextView) findViewById(R.id.top_line);
            kv = (LatinKeyboardView) findViewById(R.id.keyboard_view);
            bot_empty = findViewById(R.id.bot_empty);

            Common.GetGameStatus(KeyboardSelectActivity.this, new Listener.OnGameStatusListener() {
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

            int screenHeight = SharedPreference.getInt(KeyboardSelectActivity.this, Key.KEY_SCREEN_HEIGHT);
            if ( screenHeight <= 760 ) {
                bot_empty.setVisibility(View.GONE);
            }
            mSetPreview = SharedPreference.getBoolean(this, Common.PREF_PREVIEW_SETTING);
            setKeyboard(mSetPreview, SharedPreference.getInt(this, Common.PREF_KEYBOARD_MODE));
            if (SharedPreference.getInt(this, Common.PREF_KEYBOARD_MODE) == Common.MODE_QUERTY) {
                isQwertySelected = true;
            } else {
                isQwertySelected = false;
            }
            kv.setOnKeyboardActionListener(this);

            mThemeModel = ThemeManager.GetThemeModel(KeyboardSelectActivity.this, 14);
            if (mThemeModel != null) {
                try {
                    kv.setThemeModel(mThemeModel);
                    NinePatchDrawable norNor = ThemeManager.GetNinePatch(KeyboardSelectActivity.this, mThemeModel.getNorBtnNorI()); // 일반키 normal
                    NinePatchDrawable norPre = ThemeManager.GetNinePatch(KeyboardSelectActivity.this, mThemeModel.getNorBtnPreI()); // 일반키 pressed
                    Drawable norBtnSelector = ThemeManager.GetImageSelector(norNor, norPre); // 일반키 selector

                    NinePatchDrawable speNor = ThemeManager.GetNinePatch(KeyboardSelectActivity.this, mThemeModel.getSpBtnNorI()); // 특수키 normal
                    NinePatchDrawable spePre = ThemeManager.GetNinePatch(KeyboardSelectActivity.this, mThemeModel.getSpBtnPreI()); // 특수키 pressed
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

                    setKeyboardBackground();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
//            String extStr = bot_exp.getText().toString();
//            String str = "절대 수집 및 저장하지 않습니다.";
//            int start = extStr.indexOf(str);
//            int end = start + str.length();
//            SpannableString spannableString = new SpannableString(extStr);
//            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            spannableString.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            bot_exp.setText(spannableString);

            one_gif = findViewById(R.id.one_gif);
            two_gif = findViewById(R.id.two_gif);
            oneDrawable = (AnimationDrawable) one_gif.getBackground();
            twoDrawable = (AnimationDrawable) two_gif.getBackground();


            kbdArray.add(new KeyboardSelectData("천지인", R.drawable.aikbd_kb_kb_thumbnail_img_01));
            kbdArray.add(new KeyboardSelectData("천지인+", R.drawable.aikbd_kb_kb_thumbnail_img_05));
            kbdArray.add(new KeyboardSelectData("쿼티", R.drawable.aikbd_kb_kb_thumbnail_img_02));
            kbdArray.add(new KeyboardSelectData("나랏글", R.drawable.aikbd_kb_kb_thumbnail_img_03));
            kbdArray.add(new KeyboardSelectData("단모음", R.drawable.aikbd_kb_kb_thumbnail_img_04));
//            kbdArray.add(new KeyboardSelectData("", -1));

            mIsQwertyNumSet = SharedPreference.getBoolean(this, Common.PREF_QWERTY_NUM_SETTING);
            selectPosition = SharedPreference.getInt(this, Common.PREF_KEYBOARD_MODE);
            if (selectPosition < 0) selectPosition = 0;

            int numberOfColumns = 2;
            selectRecyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

            Display display = getWindowManager().getDefaultDisplay();
            int realScreenWidth = 0;
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
            mAdapter = new KeyboardSelectAdapter(this, kbdArray, selectPosition, realScreenWidth);
            mAdapter.setClickListener(this);
            selectRecyclerView.setAdapter(mAdapter);

            /**
             mBackgroundResourceId = SharedPreference.getString(KeyboardSelectActivity.this, Common.PREF_APP_BACKGROUND);
             mBackgroundLayer = (ImageView) findViewById(R.id.bg_layer);
             mCustomLayer = (RelativeLayout) findViewById(R.id.custom_layer);
             if ( !TextUtils.isEmpty(mBackgroundResourceId) )
             {
             mBackgroundLayer.setVisibility(View.VISIBLE);
             mCustomLayer.setVisibility(View.GONE);
             mBackgroundLayer.setBackgroundResource(getBackgroundId(KeyboardSelectActivity.this, mBackgroundResourceId));
             }
             else
             {
             mBackgroundLayer.setVisibility(View.GONE);
             mCustomLayer.setVisibility(View.VISIBLE);
             }
             mExplain = (TextView) findViewById(R.id.explain);**/
            mBtn = (Button) findViewById(R.id.btn_select_keyboard);
            mBtn.setOnClickListener(mClickListener);

            /**TextView title = (TextView)findViewById(R.id.title);
             title.setText(getResources().getString(R.string.aikbd_select_enliple_keyboard, mKeyboardName));
             if ( !TextUtils.isEmpty(mKeyboardName) )
             {
             String str = getResources().getString(R.string.aikbd_go_picker1, mKeyboardName);
             mExplain.setText(setHighlightString(str, mKeyboardName));
             }**/

//            mExplain.setText(setHighlightString(getResources().getString(R.string.aikbd_go_picker1), getResources().getString(R.string.aikbd_app_name)));

//            String today = Common.getDate();
//            String savedDate = SharedPreference.getString(Keyboard_Main_Activity.this, Common.PREF_MATCHED_EMOJI_DATE);
//            if (savedDate.isEmpty() || !today.equals(savedDate)) {
//                String adid = SharedPreference.getString(getApplicationContext(), Common.PREF_ADID);
//                connectLiveCount(adid);
//                connectMatchedEmojiList();
//            }
        }

    }

    public void goneKeyboard() {
        if (keyboard_view_layer.getVisibility() == View.VISIBLE) {
            LogPrint.d("skkim keyboard gone 2");
            keyboard_view_layer.setVisibility(View.GONE);
            match_container.setVisibility(View.GONE);
        }
    }

    public void visibleKeyboard() {
        if (keyboard_view_layer.getVisibility() == View.GONE) {
            LogPrint.d("skkim keyboard visible 2");
            keyboard_view_layer.setVisibility(View.VISIBLE);
            match_container.setVisibility(View.VISIBLE);
        }
    }

    public int getKeyboardVibility() {
        if (keyboard_view_layer.getVisibility() == View.VISIBLE) {
            return View.VISIBLE;
        } else {
            return View.GONE;
        }
    }

    private void adjustKeyboardKeyHeight(LatinKeyboard keyboard, double newKeyHeight) {
        int height = 0;
        if ( keyboard != null ) {
            for (Keyboard.Key key : keyboard.getKeys()) {
                key.height *= newKeyHeight;
                key.y *= newKeyHeight;
                height = key.height;
            }
            KeyboardLogPrint.e("khskkim height setting select keyboard : " + height);
            keyboard.setHeight(height);
        }
    }

    private void setKeyboard(boolean setValue, int kind) {
        if (kind < 0) kind = 0;
        int level = SharedPreference.getInt(KeyboardSelectActivity.this, Common.PREF_KEYBOARD_SIZE_LEVEL) < 0 ? Common.DEFAULT_KEYBOARD_SIZE : SharedPreference.getInt(KeyboardSelectActivity.this, Common.PREF_KEYBOARD_SIZE_LEVEL);
//        level = 0; // keyboard height를 최소로
//        float val = Float.parseFloat(String.format("%.2f", 0.90 + (float) (2 * level) / 100));
        float val = Common.GetHeightValue(level);
        if (kind == Common.MODE_CHUNJIIN) {
            isQwertySelected = false;
            LatinKeyboard sejong = new LatinKeyboard(this, R.xml.aikbd_sejong_setting);
            adjustKeyboardKeyHeight(sejong, val);
            kv.setKeyboard(sejong);
            kv.setPreviewEnabled(false);
        } else if (kind == Common.MODE_QUERTY) {
            isQwertySelected = true;
            LatinKeyboard korean = new LatinKeyboard(this, R.xml.aikbd_korean_n_setting);
            adjustKeyboardKeyHeight(korean, val);
            kv.setKeyboard(korean);
            if (setValue)
                kv.setPreviewEnabled(true);
            else
                kv.setPreviewEnabled(false);
        } else if (kind == Common.MODE_NARA) {
            isQwertySelected = false;
            LatinKeyboard nara = new LatinKeyboard(this, R.xml.aikbd_nara_setting);
            adjustKeyboardKeyHeight(nara, val);
            kv.setKeyboard(nara);
            kv.setPreviewEnabled(false);
        } else if (kind == Common.MODE_DAN) {
            isQwertySelected = false;
            LatinKeyboard dan = new LatinKeyboard(this, R.xml.aikbd_dan_setting);
            adjustKeyboardKeyHeight(dan, val);
            kv.setKeyboard(dan);
            if (setValue)
                kv.setPreviewEnabled(true);
            else
                kv.setPreviewEnabled(false);
        } else if (kind == Common.MODE_CHUNJIIN_PLUS) {
            isQwertySelected = false;
            LatinKeyboard sejongPlus = new LatinKeyboard(this, R.xml.aikbd_sejong_plus_setting);
            adjustKeyboardKeyHeight(sejongPlus, val);
            kv.setKeyboard(sejongPlus);
            kv.setPreviewEnabled(false);
        } else {
            isQwertySelected = false;
            LatinKeyboard sejong = new LatinKeyboard(this, R.xml.aikbd_sejong_setting);
            adjustKeyboardKeyHeight(sejong, val);
            kv.setKeyboard(sejong);
            kv.setPreviewEnabled(false);
        }

        kv.setKeys();
        kv.changeConfig(0, isQwertySelected);
        kv.setKeyboardMode(kind);
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
    }

    public void onBackPressed() {
        if (stepOneLayer.getVisibility() == View.VISIBLE || stepTwoLayer.getVisibility() == View.VISIBLE) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(KeyboardSelectActivity.this, R.style.CustomAlertDialog);
            ViewGroup viewGroup = findViewById(android.R.id.content);
            View dialogView = LayoutInflater.from(KeyboardSelectActivity.this).inflate(R.layout.aikbd_select_dialog, viewGroup, false);
            Button aikbd_dialog_cancel = dialogView.findViewById(R.id.aikbd_dialog_cancel);
            Button aikbd_dialog_ok = dialogView.findViewById(R.id.aikbd_dialog_ok);
            builder.setView(dialogView);
            final AlertDialog alertDialog = builder.create();
            aikbd_dialog_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    KeyboardSelectActivity.super.onBackPressed();
                }
            });
            aikbd_dialog_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
            alertDialog.getWindow().setLayout(Common.convertDpToPx(KeyboardSelectActivity.this, 320), Common.convertDpToPx(KeyboardSelectActivity.this, 218));
            /**
             new AikbdSelectDialog(KeyboardSelectActivity.this, new AikbdSelectDialog.Listener() {
            @Override public void cancel() {
            KeyboardSelectActivity.super.onBackPressed();
            }

            @Override public void ok() {

            }
            }).show();**/
        } else {
            if ( keyboard_view_layer.getVisibility() == View.VISIBLE && match_container.getVisibility() == View.VISIBLE ) {
                LogPrint.d("skkim keyboard gone 1");
                keyboard_view_layer.setVisibility(View.GONE);
                match_container.setVisibility(View.GONE);
                if ( mBtn.getVisibility() == View.VISIBLE ) {
                    LogPrint.d("Btn gone 3");
                    mBtn.setVisibility(View.GONE);
                }

            } else {
                super.onBackPressed();
            }
        }
    }

    public static int getBackgroundId(Context _context, String resName) {
        String packageName = SharedPreference.getString(_context, Common.PREF_APP_PACKAGE);
        KeyboardLogPrint.w("resName ::: " + resName);
        KeyboardLogPrint.w("packageName ::: " + packageName);
        Context context = null;
        int id = -1;
        try {
            context = _context.createPackageContext(packageName, 0);
            if (context != null) {
                KeyboardLogPrint.w("context not null");
                Resources res = context.getResources();
                id = res.getIdentifier(resName, "drawable", packageName);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (id < 0)
            id = R.drawable.aikbd_step_ocb_icon;
        return id;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (mActivity != null)
            mActivity = null;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        String content = null;
        String currentKeyboard = Settings.Secure.getString(getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
        /**
         if (C_KEYBOARD.equals(currentKeyboard))
         content = getResources().getString(R.string.aikbd_go_setting, mKeyboardName);
         else {
         if (isUsingCustomInputMethod()) {
         if (mIsMovedSelectKeyboard)
         content = getResources().getString(R.string.aikbd_go_picker, mKeyboardName);
         else
         content = getResources().getString(R.string.aikbd_go_picker1, mKeyboardName);
         } else
         content = getResources().getString(R.string.aikbd_go_check, mKeyboardName, mKeyboardName);
         }
         mExplain.setText(setHighlightString(content, mKeyboardName));
         **/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isUsingCustomInputMethod()) {
                    if (stepThreeLayer.getVisibility() != View.VISIBLE) {
                        mIsOpenPicker = true;
                        if (!"완료".equals(mBtn.getText().toString())) {
                            mBtn.setText("선택하기");
                        }

                        if ("설정하기".equals(mBtn.getText().toString())) {
                            CustomAsyncTask task = new CustomAsyncTask(KeyboardSelectActivity.this);
                            task.postStats("activity", new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                                @Override
                                public void onResponse(boolean result, Object obj) {

                                }
                            });
                        } else {

                        }

                        mBtn.setBackgroundResource(R.drawable.aikbd_step_btn_bg);
                        LogPrint.d("Btn visible 3");
                        mBtn.setVisibility(View.VISIBLE);
//                        bot_exp.setVisibility(View.GONE);
                        stepTwoLayer.setVisibility(View.VISIBLE);
                        stepOneLayer.setVisibility(View.GONE);
                        stepThreeLayer.setVisibility(View.GONE);
                        point_guide.setVisibility(View.VISIBLE);
                        LogPrint.d("isFinishing() :: " + isFinishing());
                        if ( isFinishing() || KeyboardSelectActivity.this == null )
                            return;
                        try {
                            ImageModule.with(KeyboardSelectActivity.this).load(R.drawable.aikbd_ob_btn_img_2).listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable ImageModuleException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    if (resource instanceof GifDrawable) {
                                        ((GifDrawable) resource).setLoopCount(1);
                                    }
                                    return false;
                                }
                            }).into(point_guide);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        twoDrawable.start();
                        oneDrawable.stop();
                        if (!mIsMovedSelectKeyboard) {
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    String currentKeyboard = Settings.Secure.getString(getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
                                    KeyboardLogPrint.w("currentKeyboard :: " + currentKeyboard);
                                    if (C_KEYBOARD.equals(currentKeyboard)) {
                                        if (!isStepThreeSetted) {
                                            setRake("/keyboard/onboarding3", "");
                                            isStepThreeSetted = true;
                                        }

                                        mBtn.setText("완료");
                                        mBtn.setBackgroundResource(R.drawable.aikbd_step_btn_bg);
//                                        bot_exp.setVisibility(View.GONE);
                                        stepOneLayer.setVisibility(View.GONE);
                                        stepTwoLayer.setVisibility(View.GONE);
                                        stepThreeLayer.setVisibility(View.VISIBLE);
                                        if ( keyboard_view_layer.getVisibility() != View.VISIBLE ) {
                                            LogPrint.d("Btn gone 1");
                                            mBtn.setVisibility(View.GONE);
                                        }

                                        point_guide.setVisibility(View.GONE);
                                        selectPosition = SharedPreference.getInt(KeyboardSelectActivity.this, Common.PREF_KEYBOARD_MODE);
                                        if (selectPosition < 0)
                                            selectPosition = 0;
                                        if (selectPosition != 2)
                                            SharedPreference.setBoolean((KeyboardSelectActivity.this), Common.PREF_QWERTY_NUM_SETTING, false);
                                        SharedPreference.setInt(KeyboardSelectActivity.this, Common.PREF_KEYBOARD_MODE, selectPosition);
                                        /**
                                         //                            Intent intent = new Intent(Keyboard_Main_Activity.this, KeyboardSelectActivity.class);
                                         Intent intent = new Intent(KeyboardSelectActivity.this, Keyboard_Empty_Activity.class);
                                         intent.putExtra("IS_FROM_SETTING", false);
                                         startActivity(intent);
                                         mIsMovedSelectKeyboard = true;
                                         finish();**/
                                    } else {
                                        if (!isStepTwoSetted) {
                                            setRake("/keyboard/onboarding2", "");
                                            isStepTwoSetted = true;
                                        }
                                    }
                                }
                            }, 500);
                        } else {

                        }
                    } else {
                        mBtn.setText("완료");
                        mBtn.setBackgroundResource(R.drawable.aikbd_step_btn_bg);
//                        bot_exp.setVisibility(View.GONE);
                        stepOneLayer.setVisibility(View.GONE);
                        stepTwoLayer.setVisibility(View.GONE);
                        stepThreeLayer.setVisibility(View.VISIBLE);

                        if ( keyboard_view_layer.getVisibility() != View.VISIBLE ) {
                            LogPrint.d("Btn gone 2");
                            mBtn.setVisibility(View.GONE);
                        }
                        point_guide.setVisibility(View.GONE);
                        selectPosition = SharedPreference.getInt(KeyboardSelectActivity.this, Common.PREF_KEYBOARD_MODE);
                        if (selectPosition < 0)
                            selectPosition = 0;
                        if (selectPosition != 2)
                            SharedPreference.setBoolean((KeyboardSelectActivity.this), Common.PREF_QWERTY_NUM_SETTING, false);
                        SharedPreference.setInt(KeyboardSelectActivity.this, Common.PREF_KEYBOARD_MODE, selectPosition);
                    }

                    if (stepThreeLayer.getVisibility() != View.VISIBLE) {

                    }
                } else {
                    if (!isStepOneSetted) {
                        setRake("/keyboard/onboarding1", "");
                        isStepOneSetted = true;
                    }
                    mIsOpenPicker = false;
                    LogPrint.d("Btn visible 1");
                    mBtn.setVisibility(View.VISIBLE);
                    mBtn.setBackgroundResource(R.drawable.aikbd_step_btn_bg);
                    mBtn.setText("설정하기");
//                    bot_exp.setVisibility(View.VISIBLE);
                    stepOneLayer.setVisibility(View.VISIBLE);
                    if ( isFinishing() || KeyboardSelectActivity.this == null )
                        return;
                    try {
                        ImageModule.with(KeyboardSelectActivity.this).load(R.drawable.aikbd_ob_btn_img_1).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable ImageModuleException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                if (resource instanceof GifDrawable) {
                                    ((GifDrawable) resource).setLoopCount(1);
                                }
                                return false;
                            }
                        }).into(point_guide);
                    } catch( Exception e) {
                        e.printStackTrace();
                    }

                    stepTwoLayer.setVisibility(View.GONE);
                    stepThreeLayer.setVisibility(View.GONE);
                    point_guide.setVisibility(View.VISIBLE);
                    twoDrawable.stop();
                    oneDrawable.start();
                }
            }
        }, 500);
    }

    private CharSequence setHighlightString(String fullSentence, String highlightSentence) {
        int highlightStringLength = highlightSentence.length();
        int userIdStartIndex = fullSentence.indexOf(highlightSentence);
        int userIdEndIndex = userIdStartIndex + highlightStringLength;

        Spannable spanMessage = new SpannableString(fullSentence);
        //      spanMessage.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.highlight_color)), userIdStartIndex, userIdEndIndex, 0);

        return spanMessage;
    }

    private void startTimeTask() {
        mTimer.scheduleAtFixedRate(new mainTask(), 0, 1000);
    }

    @Override
    public void onItemClick(View view, int position) {
        selectPosition = position;
        if (position == 0) {
            setRake("/keyboard/onboarding3", "tap.keyboardtype1");
        } else if (position == 1) {
            setRake("/keyboard/onboarding3", "tap.keyboardtype5");
        } else if (position == 2) {
            setRake("/keyboard/onboarding3", "tap.keyboardtype2");
        } else if (position == 3) {
            setRake("/keyboard/onboarding3", "tap.keyboardtype3");
        } else if (position == 4) {
            setRake("/keyboard/onboarding3", "tap.keyboardtype4");
        }
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if ( position == 0 || position == 1 ) {
                    scrollView.scrollTo(0, Common.convertDpToPx(KeyboardSelectActivity.this, 190));
                } else if ( position == 2 || position == 3 ) {
                    scrollView.scrollTo(0, Common.convertDpToPx(KeyboardSelectActivity.this, 295));
                } else {
                    scrollView.scrollTo(0, Common.convertDpToPx(KeyboardSelectActivity.this, 400));
                }
            }
        }, 100);


        mBtn.setBackgroundResource(R.drawable.aikbd_step_btn_bg);
        if ( mBtn.getVisibility() == View.GONE ) {
            mBtn.setVisibility(View.VISIBLE);
            LogPrint.d("Btn visible 2");
        }
        LogPrint.d("skkim keyboard visible 1");
        keyboard_view_layer.setVisibility(View.VISIBLE);
        match_container.setVisibility(View.VISIBLE);
        selectPosition = position;
        setKeyboard(mSetPreview, selectPosition);
        String keyboardName = "";
        if ( kbdArray != null && kbdArray.size() > 0 ) {
            try {
                if ( selectPosition >= 0 ) {
                    keyboardName = kbdArray.get(selectPosition).kbd_title;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        if ( selectRecyclerView.getLayoutManager() instanceof GridLayoutManager ) {
//            LinearLayoutManager manager = new LinearLayoutManager(KeyboardSelectActivity.this);
//            manager.setOrientation(RecyclerView.HORIZONTAL);
//            selectRecyclerView.setLayoutManager(manager);
//            runLayoutAnimation(selectRecyclerView, manager);
//        }
    }

    @Override
    public void onPress(int i) {
        
    }

    @Override
    public void onRelease(int i) {

    }

    @Override
    public void onKey(int i, int[] ints) {

    }

    @Override
    public void onText(CharSequence charSequence) {

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

    private class mainTask extends TimerTask {
        @Override
        public void run() {
            if (isUsingCustomInputMethod()) {
                // main activity 호출 및 timer cancel
                mIsOpenPicker = true;
                if (mTimer != null) {
                    mTimer.cancel();
                    mTimer = null;
                }
                finish();
                Intent intent = new Intent(KeyboardSelectActivity.this, KeyboardSelectActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showInputMethodPicker();
            }
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

    public void onClickKeyboard(View view) {
        String currentKeyboard = Settings.Secure.getString(getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
        KeyboardLogPrint.w("currentKeyboard :: " + currentKeyboard);
        if (C_KEYBOARD.equals(currentKeyboard)) {
            if (mIsMovedSelectKeyboard) {
                Intent intent = new Intent(KeyboardSelectActivity.this, KeyboardSettingsActivity.class);
                startActivity(intent);
                finish();
            } else {
//                Intent intent = new Intent(Keyboard_Main_Activity.this, KeyboardSelectActivity.class);
                Intent intent = new Intent(KeyboardSelectActivity.this, Keyboard_Empty_Activity.class);
                intent.putExtra("IS_FROM_SETTING", false);
                startActivity(intent);
                finish();
            }
        } else {
            if (mIsOpenPicker) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showInputMethodPicker();
            } else {
                if (mTimer != null) {
                    mTimer.cancel();
                    mTimer = null;
                    mTimer = new Timer();
                } else
                    mTimer = new Timer();

                mIsOpenPicker = false;

                startTimeTask();
                startActivityForResult(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS), 0);
            }
        }
    }

    public static String UnicodeCharToString(char[] Char) {
        String strTemp = "";

        for (int i = 1; i < Char.length; i += 2) {
            String s1 = Integer.toHexString((int) Char[i]);
            String s2 = Integer.toHexString((int) Char[i - 1]);

            // ????????
            if (s1.length() == 1)
                s1 = "0" + s1;

            if (s2.length() == 1)
                s2 = "0" + s2;

            strTemp += s1 + s2;
        }

        return UnicodeToString(strTemp).trim();
    }

    public static String UnicodeToString(String Hex) {
        String enUnicode = null;
        String deUnicode = null;

        for (int i = 0; i < Hex.length(); i++) {
            if (enUnicode == null)
                enUnicode = String.valueOf(Hex.charAt(i));
            else
                enUnicode = enUnicode + Hex.charAt(i);

            if (i % 4 == 3) {
                if (enUnicode != null) {
                    if (deUnicode == null)
                        deUnicode = String.valueOf((char) Integer.valueOf(
                                enUnicode, 16).intValue());
                    else
                        deUnicode = deUnicode
                                + String.valueOf((char) Integer.valueOf(
                                enUnicode, 16).intValue());
                }

                enUnicode = null;
            }
        }

        return deUnicode;
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_select_keyboard) {
                String currentKeyboard = Settings.Secure.getString(getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
                if (C_KEYBOARD.equals(currentKeyboard)) {
                    if (mIsMovedSelectKeyboard) {
                        Intent intent = new Intent(KeyboardSelectActivity.this, KeyboardSettingsActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        if (selectPosition < 0) {
                            Toast.makeText(KeyboardSelectActivity.this, "입력 레이아웃을 선택해주세요.", Toast.LENGTH_SHORT).show();
                        } else {
                            SharedPreference.setInt(getApplicationContext(), Common.PREF_KEYBOARD_MODE, selectPosition);
                            if (selectPosition != 2)
                                SharedPreference.setBoolean(getApplicationContext(), Common.PREF_QWERTY_NUM_SETTING, false);
                            else
                                SharedPreference.setBoolean(getApplicationContext(), Common.PREF_QWERTY_NUM_SETTING, true);
                            boolean numSet = SharedPreference.getBoolean(getApplicationContext(), Common.PREF_QWERTY_NUM_SETTING);
                            String keyboardName = "";
                            if (kbdArray != null && kbdArray.size() > 0) {
                                try {
                                    if (selectPosition >= 0) {
                                        keyboardName = kbdArray.get(selectPosition).kbd_title;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            if (!TextUtils.isEmpty(keyboardName)) {
                                CustomAsyncTask task = new CustomAsyncTask(KeyboardSelectActivity.this);
                                task.setUserInfo(keyboardName, "", "", "", new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                                    @Override
                                    public void onResponse(boolean result, Object obj) {
                                    }
                                });
                            }
                            setRake("/keyboard/onboarding3", "tap.keyboardcomplete");
                            Intent it = new Intent("KIND_CHANGE");
                            it.putExtra("kind", selectPosition);
                            it.putExtra("num_set", numSet);
                            sendBroadcast(it);

                            Intent intent = new Intent(KeyboardSelectActivity.this, KeyboardSettingsActivity.class);
                            intent.putExtra("IS_FROM_SETTING", true);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(0, 0);
                        }
//                        Intent intent = new Intent(Keyboard_Main_Activity.this, KeyboardSelectActivity.class);
                        /**
                         Intent intent = new Intent(KeyboardSelectActivity.this, Keyboard_Empty_Activity.class);
                         intent.putExtra("IS_FROM_SETTING", false);
                         startActivity(intent);
                         finish();**/
                    }
                } else {
                    if (mIsOpenPicker) {
                        setRake("/keyboard/onboarding2", "tap.keyboardselect");
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showInputMethodPicker();


                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                KeyboardLogPrint.e("mainTask 키보드 설정 됨 picker");
//                                    Dialog mDialog = new Dialog(MainActivity.this);
//                                    mDialog.setCanceledOnTouchOutside(true);
////                                    mDialog.getWindow().setType(
////                                            WindowManager.LayoutParams.TYPE_INPUT_METHOD_DIALOG);
////                                    mDialog.getWindow().getAttributes().privateFlags |=
////                                            WindowManager.LayoutParams.PRIVATE_FLAG_SHOW_FOR_ALL_USERS;
////                                    mDialog.getWindow().getAttributes().setTitle("Select aikbd_input aikbd_method");
//
//                                    mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_STATUS_BAR_PANEL);
//                                    mDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
//                                            | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
//                                    WindowManager.LayoutParams attrs = getWindow().getAttributes();
//                                    attrs.setTitle(getClass().getSimpleName());
//                                    mDialog.getWindow().setAttributes(attrs);
//                                    mDialog.show();
                            }
                        }, 500);


                    } else {
                        setRake("/keyboard/onboarding1", "tap.keyboardon");
                        if (mTimer != null) {
                            mTimer.cancel();
                            mTimer = null;
                            mTimer = new Timer();
                        } else
                            mTimer = new Timer();

                        mIsOpenPicker = false;
                        startTimeTask();
                        startActivityForResult(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS), 0);
                        Toast.makeText(KeyboardSelectActivity.this, "목록에서 OK캐쉬백 돈버는 키보드를 켜주세요", Toast.LENGTH_LONG).show();
                    }
                }

            } else if (v.getId() == R.id.btn_close) {
                if (stepOneLayer.getVisibility() == View.VISIBLE) {
                    setRake("/keyboard/onboarding1", "top_tap.closebtn");
                } else if (stepTwoLayer.getVisibility() == View.VISIBLE) {
                    setRake("/keyboard/onboarding2", "top_tap.closebtn");
                } else if (stepThreeLayer.getVisibility() == View.VISIBLE) {
                    setRake("/keyboard/onboarding3", "top_tap.closebtn");
                }
                if (stepOneLayer.getVisibility() == View.VISIBLE || stepTwoLayer.getVisibility() == View.VISIBLE) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(KeyboardSelectActivity.this, R.style.CustomAlertDialog);
                    ViewGroup viewGroup = findViewById(android.R.id.content);
                    View dialogView = LayoutInflater.from(KeyboardSelectActivity.this).inflate(R.layout.aikbd_select_dialog, viewGroup, false);
                    Button aikbd_dialog_cancel = dialogView.findViewById(R.id.aikbd_dialog_cancel);
                    Button aikbd_dialog_ok = dialogView.findViewById(R.id.aikbd_dialog_ok);
                    builder.setView(dialogView);
                    final AlertDialog alertDialog = builder.create();
                    aikbd_dialog_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                            KeyboardSelectActivity.super.onBackPressed();
                        }
                    });
                    aikbd_dialog_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                    alertDialog.getWindow().setLayout(Common.convertDpToPx(KeyboardSelectActivity.this, 320), Common.convertDpToPx(KeyboardSelectActivity.this, 218));
                } else {
                    finish();
                }


                /**
                 try {
                 String url = "ocbt://com.skmc.okcashbag.home_google/detail/event?title=돈버는 키보드&url=https%3a%2f%2falp-webview.okcashbag.com%2fv1.0%2fearnkbd%2findex.html";
                 Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                 startActivity(intent);
                 finish();
                 } catch (Exception e) {
                 e.printStackTrace();
                 }**/
            }
        }
    };

    public void setRake(String page_id, String action_id) {
        new Thread() {
            public void run() {
                String track_id = SharedPreference.getString(KeyboardSelectActivity.this, Key.KEY_OCB_TRACK_ID);
                String device_id = SharedPreference.getString(KeyboardSelectActivity.this, Key.KEY_OCB_DEVICE_ID);
                try {
                    E_Cipher cp = E_Cipher.getInstance();
                    track_id = cp.Decode(KeyboardSelectActivity.this, track_id);
                    device_id = cp.Decode(KeyboardSelectActivity.this, device_id);
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

    private void runLayoutAnimation(final RecyclerView recyclerView, LinearLayoutManager manager ) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation);
        recyclerView.setLayoutAnimation(controller);

        recyclerView.getAdapter().notifyItemRangeChanged(0, mAdapter.getItemCount());
        final LinearSmoothScroller linearSmoothScroller =
                new LinearSmoothScroller(recyclerView.getContext()) {
                    private static final float MILLISECONDS_PER_INCH = 50f;

                    @Override
                    protected float calculateSpeedPerPixel
                            (DisplayMetrics displayMetrics) {
                        return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
                    }
                };
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                linearSmoothScroller.setTargetPosition(selectPosition);
                manager.startSmoothScroll(linearSmoothScroller);

            }
        }, 500);
        recyclerView.scheduleLayoutAnimation();
    }
}
