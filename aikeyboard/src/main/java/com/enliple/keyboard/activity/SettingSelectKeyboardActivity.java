package com.enliple.keyboard.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Insets;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.enliple.keyboard.common.Util;
import com.enliple.keyboard.network.CustomAsyncTask;
import com.enliple.keyboard.ui.common.Key;
import com.enliple.keyboard.ui.common.LogPrint;
import com.rake.android.rkmetrics.RakeAPI;
import com.skplanet.pdp.sentinel.shuttle.OCBLogSentinelShuttle;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SettingSelectKeyboardActivity extends Activity implements KeyboardSelectAdapter.ItemClickListener, KeyboardView.OnKeyboardActionListener {

    private KeyboardSelectAdapter mAdapter;
    private LatinKeyboardView kv;
    private int selectPosition = 0;
    private boolean mIsFromSetting = true;
    private boolean mSetPreview = false;
    private boolean mIsQwertyNumSet = false;

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
    private int mSizeLevel;

    private int mBgId = -1;
    private int mTxtId = -1;
    private int mOptTxtId = -1;
    private int mSpIconId = -1;
    private int mNorSelector = -1;
    private int mSpeSelector = -1;
    private int mNorBtnColor = -1;
    private int mSpBtnColor = -1;
    private int mBtnAlpha = -1;
    private int mDarkBtnAlpha = -1;

    private ThemeModel mThemeModel;
    private int mBgAlpha;
    private TextView mTopLine;
    private TextView btn_home;
    private RelativeLayout r_container;
    public static Activity mActivity;
    private boolean isQwertySelected = false;
    private String selectKeyboardName = "";
    private ArrayList<KeyboardSelectData> kbdArray = new ArrayList<KeyboardSelectData>();
    private RakeAPI rake;
    public void setRake(String page_id, String action_id) {
        new Thread() {
            public void run() {
                String track_id = SharedPreference.getString(SettingSelectKeyboardActivity.this, Key.KEY_OCB_TRACK_ID);
                String device_id = SharedPreference.getString(SettingSelectKeyboardActivity.this, Key.KEY_OCB_DEVICE_ID);
                try {
                    E_Cipher cp = E_Cipher.getInstance();
                    track_id = cp.Decode(SettingSelectKeyboardActivity.this, track_id);
                    device_id = cp.Decode(SettingSelectKeyboardActivity.this, device_id);
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.aikbd_activity_select_keyboard);
        View root_layout = findViewById(R.id.root_layout);
        Common.SetInset(root_layout);
        if ( CustomAsyncTask.GUBUN_RELEASE.equals(CustomAsyncTask.gubun) )
            rake = RakeAPI.getInstance(SettingSelectKeyboardActivity.this, Common.LIVE_TOKEN, RakeAPI.Env.LIVE, RakeAPI.Logging.DISABLE);
        else
            rake = RakeAPI.getInstance(SettingSelectKeyboardActivity.this, Common.DEV_TOKEN, RakeAPI.Env.DEV, RakeAPI.Logging.DISABLE);
        setRake("/keyboard/type", "");
        mActivity = this;
        mIsQwertyNumSet = SharedPreference.getBoolean(SettingSelectKeyboardActivity.this, Common.PREF_QWERTY_NUM_SETTING);
        selectPosition = SharedPreference.getInt(SettingSelectKeyboardActivity.this, Common.PREF_KEYBOARD_MODE);
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getBooleanExtra("IS_FROM_SETTING", false))
                mIsFromSetting = true;
            else
                mIsFromSetting = false;
        }



        kbdArray.add(new KeyboardSelectData("천지인", R.drawable.aikbd_kb_kb_thumbnail_img_01));
        kbdArray.add(new KeyboardSelectData("천지인+", R.drawable.aikbd_kb_kb_thumbnail_img_05));
        kbdArray.add(new KeyboardSelectData("쿼티", R.drawable.aikbd_kb_kb_thumbnail_img_02));
        kbdArray.add(new KeyboardSelectData("나랏글", R.drawable.aikbd_kb_kb_thumbnail_img_03));
        kbdArray.add(new KeyboardSelectData("단모음", R.drawable.aikbd_kb_kb_thumbnail_img_04));
        kbdArray.add(new KeyboardSelectData("", -1));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycleview);
        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (keyboard_view_layer.getVisibility() == View.VISIBLE) {
                    keyboard_view_layer.setVisibility(View.GONE);
                    match_container.setVisibility(View.GONE);
                }
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (keyboard_view_layer.getVisibility() == View.VISIBLE) {
                    keyboard_view_layer.setVisibility(View.GONE);
                    match_container.setVisibility(View.GONE);
                }
            }
        });
        /**int kind = SharedPreference.getInt(this, Common.PREF_KEYBOARD_MODE);
         if (kind < 0) kind = 0;
         KeyboardLogPrint.e("SettingSelectKeyboardActivity kind :: " + kind);**/
        r_container = findViewById(R.id.r_container);
        r_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (keyboard_view_layer.getVisibility() == View.VISIBLE) {
                    keyboard_view_layer.setVisibility(View.GONE);
                    match_container.setVisibility(View.GONE);
                }
            }
        });
        mFirstTabLayer = (RelativeLayout) findViewById(R.id.first_tab);
        mTimedealTabLayer = (RelativeLayout) findViewById(R.id.timedeal_tab);
        mSecondTabLayer = (RelativeLayout) findViewById(R.id.second_tab);
        mShoppingTabLayer = (RelativeLayout) findViewById(R.id.shopping_tab);
        mFourthLayer = (RelativeLayout) findViewById(R.id.fourth_tab);
        mMoreLayer = findViewById(R.id.more_tab);
        mGameLayer = findViewById(R.id.game_tab);
        btn_home = findViewById(R.id.btn_home);
        mImgFirst = (ImageView) findViewById(R.id.img_first);
        mImgTimedeal = (ImageView) findViewById(R.id.img_timedeal);
        mImgSecond = (ImageView) findViewById(R.id.img_second);
        mImgShopping = (ImageView) findViewById(R.id.img_shopping);
        mImgFourth = (ImageView) findViewById(R.id.img_fourth);
        mImgMore = findViewById(R.id.img_more);
        keyboard_view_layer = findViewById(R.id.keyboard_view_layer);
        match_container = findViewById(R.id.match_container);
        mTopLine = (TextView) findViewById(R.id.top_line);

        mBgId = SharedPreference.getInt(this, Common.PREF_KEYBOARD_BG_DRAWABLE);
        mTxtId = SharedPreference.getInt(this, Common.PREF_THEME_TXT_COLOR);
        mOptTxtId = SharedPreference.getInt(this, Common.PREF_THEME_TXT_COLOR_S);
        mSpIconId = SharedPreference.getInt(this, Common.PREF_THEME_SP_ICON_COLOR);
        mBtnAlpha = SharedPreference.getInt(this, Common.PREF_KEYBOARD_BUTTON_ALPHA);
        mDarkBtnAlpha = SharedPreference.getInt(this, Common.PREF_KEYBOARD_DARK_BUTTON_ALPHA);
        mNorBtnColor = SharedPreference.getInt(this, Common.PREF_KEYBOARD_BUTTON_COLOR);
        mSpBtnColor = SharedPreference.getInt(this, Common.PREF_KEYBOARD_SBUTTON_COLOR);
        mNorSelector = SharedPreference.getInt(this, Common.PREF_BTN_SELECTOR);
        mSpeSelector = SharedPreference.getInt(this, Common.PREF_DARK_BTN_SELECTOR);

        Common.GetGameStatus(SettingSelectKeyboardActivity.this, new Listener.OnGameStatusListener() {
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
        mAdapter = new KeyboardSelectAdapter(this, kbdArray, selectPosition, realScreenWidth, mActivity);
        mAdapter.setClickListener(this);
        recyclerView.setAdapter(mAdapter);
        mSetPreview = SharedPreference.getBoolean(this, Common.PREF_PREVIEW_SETTING);
        kv = (LatinKeyboardView) findViewById(R.id.keyboard_view);
        KeyboardLogPrint.e("SettingSelectKeyboardActivity 1 kind :: " + SharedPreference.getInt(this, Common.PREF_KEYBOARD_MODE));
        setKeyboard(mSetPreview, SharedPreference.getInt(this, Common.PREF_KEYBOARD_MODE));
        if (SharedPreference.getInt(this, Common.PREF_KEYBOARD_MODE) == Common.MODE_QUERTY) {
            isQwertySelected = true;
        } else {
            isQwertySelected = false;
        }
        kv.setOnKeyboardActionListener(this);

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRake("/keyboard/type", "top_tap.backbtn");
                Intent intent = null;
                intent = new Intent(SettingSelectKeyboardActivity.this, KeyboardSettingsActivity.class);
                intent.putExtra("IS_FROM_SETTING", mIsFromSetting);
                startActivity(intent);
                finish();
                //overridePendingTransition(0, 0);
            }
        });

        Button closeButton = (Button) findViewById(R.id.btn_close);
        if (mIsFromSetting)
            closeButton.setText(getResources().getString(R.string.aikbd_key_ok));
        else
            closeButton.setText(getResources().getString(R.string.aikbd_next));

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsFromSetting) {
                    SharedPreference.setInt(getApplicationContext(), Common.PREF_KEYBOARD_MODE, selectPosition);
                    if (selectPosition != 2)
                        SharedPreference.setBoolean((SettingSelectKeyboardActivity.this), Common.PREF_QWERTY_NUM_SETTING, false);
                    finish();
                    overridePendingTransition(0, 0);
                } else {
                    SharedPreference.setInt(getApplicationContext(), Common.PREF_KEYBOARD_MODE, selectPosition);
                    if (selectPosition != 2)
                        SharedPreference.setBoolean((SettingSelectKeyboardActivity.this), Common.PREF_QWERTY_NUM_SETTING, false);
                    Intent intent = new Intent(SettingSelectKeyboardActivity.this, KeyboardOCBCategoryThemeActivity.class);
                    intent.putExtra("IS_FROM_SETTING", mIsFromSetting);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(0, 0);
                }
            }
        });

        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    setRake("/keyboard/type", "top_tap.homebtn");
                    String url = "ocbt://com.skmc.okcashbag.home_google/main";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        mThemeModel = ThemeManager.GetThemeModel(SettingSelectKeyboardActivity.this, 14);
        if (mThemeModel != null) {
            try {
                kv.setThemeModel(mThemeModel);
                NinePatchDrawable norNor = ThemeManager.GetNinePatch(SettingSelectKeyboardActivity.this, mThemeModel.getNorBtnNorI()); // 일반키 normal
                NinePatchDrawable norPre = ThemeManager.GetNinePatch(SettingSelectKeyboardActivity.this, mThemeModel.getNorBtnPreI()); // 일반키 pressed
                Drawable norBtnSelector = ThemeManager.GetImageSelector(norNor, norPre); // 일반키 selector

                NinePatchDrawable speNor = ThemeManager.GetNinePatch(SettingSelectKeyboardActivity.this, mThemeModel.getSpBtnNorI()); // 특수키 normal
                NinePatchDrawable spePre = ThemeManager.GetNinePatch(SettingSelectKeyboardActivity.this, mThemeModel.getSpBtnPreI()); // 특수키 pressed
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

                setKeyboardBackground();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onBackPressed() {
        Intent intent = null;
        intent = new Intent(SettingSelectKeyboardActivity.this, KeyboardSettingsActivity.class);
        intent.putExtra("IS_FROM_SETTING", mIsFromSetting);
        startActivity(intent);
        finish();
        //overridePendingTransition(0, 0);
    }

    private void setBackgroundResource(ImageView view, int resId) {
        Drawable dr = ResourcesCompat.getDrawable(getResources(), resId, null);
        Bitmap bitmap = Util.convertDrawableToBitmap(dr);
        view.setImageBitmap(bitmap);
    }

    public void goneKeyboard() {
        if (keyboard_view_layer.getVisibility() == View.VISIBLE) {
            keyboard_view_layer.setVisibility(View.GONE);
            match_container.setVisibility(View.GONE);
        }
    }

    private void changeTabColor(int resId) {
        mFirstTabLayer.setBackgroundResource(resId);
        mTimedealTabLayer.setBackgroundResource(resId);
        mSecondTabLayer.setBackgroundResource(resId);
        mShoppingTabLayer.setBackgroundResource(resId);
        mFourthLayer.setBackgroundResource(resId);
        mMoreLayer.setBackgroundResource(resId);
    }

    private String getColorStr(int colorId) {
        String strColorValue = "#ffffffff";
        try {
            int colorValue = getResources().getColor(colorId);
            strColorValue = "#" + Integer.toHexString(colorValue);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return strColorValue;
    }

    private void changeImageColor(int color, ImageView view, int resId) {
        Drawable dr = ResourcesCompat.getDrawable(getResources(), resId, null);
        Bitmap logo = Util.convertDrawableToBitmap(dr);
        Bitmap bitmap = Util.changeImageColor(logo, color);
        view.setImageBitmap(bitmap);
    }

    private void setKeyboard(boolean setValue, int kind) {
        if (kind < 0) kind = 0;
        int level = SharedPreference.getInt(SettingSelectKeyboardActivity.this, Common.PREF_KEYBOARD_SIZE_LEVEL) < 0 ? Common.DEFAULT_KEYBOARD_SIZE : SharedPreference.getInt(SettingSelectKeyboardActivity.this, Common.PREF_KEYBOARD_SIZE_LEVEL);
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
        sendBroadcast(new Intent(SoftKeyboard.SET_KEYBOARD_ON));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (selectPosition != 2) {
            mIsQwertyNumSet = false;
            SharedPreference.setBoolean((SettingSelectKeyboardActivity.this), Common.PREF_QWERTY_NUM_SETTING, false);
        } else {
            mIsQwertyNumSet = true;
            SharedPreference.setBoolean((SettingSelectKeyboardActivity.this), Common.PREF_QWERTY_NUM_SETTING, true);
        }

        SharedPreference.setInt(getApplicationContext(), Common.PREF_KEYBOARD_MODE, selectPosition);
        Intent intent = new Intent("KIND_CHANGE");
        intent.putExtra("kind", selectPosition);
        intent.putExtra("num_set", mIsQwertyNumSet);
        sendBroadcast(intent);
        sendBroadcast(new Intent(SoftKeyboard.SET_KEYBOARD_OFF));



    }

    public int getKeyboardVibility() {
        if (keyboard_view_layer.getVisibility() == View.VISIBLE) {
            return View.VISIBLE;
        } else {
            return View.GONE;
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        if ( position == 0 ) {
            setRake("/keyboard/type", "tap.keyboardtype1");
        } else if ( position == 1 ) {
            setRake("/keyboard/type", "tap.keyboardtype5");
        } else if ( position == 2 ) {
            setRake("/keyboard/type", "tap.keyboardtype2");
        } else if ( position == 3 ) {
            setRake("/keyboard/type", "tap.keyboardtype3");
        } else if ( position == 4 ) {
            setRake("/keyboard/type", "tap.keyboardtype4");
        }
        if (keyboard_view_layer.getVisibility() == View.VISIBLE) {
            keyboard_view_layer.setVisibility(View.GONE);
            match_container.setVisibility(View.GONE);
        } else {
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
            if ( !TextUtils.isEmpty(keyboardName) ) {
                CustomAsyncTask task = new CustomAsyncTask(SettingSelectKeyboardActivity.this);
                task.setUserInfo(keyboardName, "", "", "", new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                    @Override
                    public void onResponse(boolean result, Object obj) {
                    }
                });
            }
        }
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
}