package com.enliple.keyboard.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Insets;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.enliple.keyboard.Cipher.E_Cipher;
import com.enliple.keyboard.R;
import com.enliple.keyboard.ad.Listener;
import com.enliple.keyboard.adapter.AikbdThemeAdapter;
import com.enliple.keyboard.adapter.OCBThemeCategoryAdapter;
import com.enliple.keyboard.common.AIKBD_DBHelper;
import com.enliple.keyboard.common.Common;
import com.enliple.keyboard.common.KeyboardLogPrint;
import com.enliple.keyboard.common.KeyboardUserIdModel;
import com.enliple.keyboard.common.NewThemeListInfo;
import com.enliple.keyboard.common.ThemeManager;
import com.enliple.keyboard.common.ThemeModel;
import com.enliple.keyboard.common.UserIdDBHelper;
import com.enliple.keyboard.imageloader.ImageLoader;
import com.enliple.keyboard.imageloader.ImageUtils;
import com.enliple.keyboard.imgmodule.ImageModule;
import com.enliple.keyboard.imgmodule.request.target.CustomTarget;
import com.enliple.keyboard.imgmodule.request.transition.Transition;
import com.enliple.keyboard.models.AdChoices;
import com.enliple.keyboard.models.CategoryData;
import com.enliple.keyboard.network.CustomAsyncTask;
import com.enliple.keyboard.ui.common.Key;
import com.enliple.keyboard.ui.common.LogPrint;
import com.rake.android.rkmetrics.RakeAPI;
import com.skplanet.pdp.sentinel.shuttle.OCBLogSentinelShuttle;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class KeyboardOCBCategoryThemeActivity extends Activity implements KeyboardView.OnKeyboardActionListener {
    private static final int BUFFER_SIZE = 1024 * 10;
    private ArrayList<NewThemeListInfo> mThemeListArray;
    private ArrayList<CategoryData> mCategoryArray;
    private boolean mIsFromSetting = true;
    private boolean mIsFromKeyboard = false;
    private int mBgAlpha = 150;
    private Drawable mBgDrawable;
    private Context mContext = null;
    private AikbdThemeAdapter themeAdapter;
    private OCBThemeCategoryAdapter categoryAdapter;
    private boolean mSetPreview = false;
    private boolean mIsQwertyNumSet = false;
    private String mUsedTheme = "";
    private ThemeModel mThemeModel;
    private RecyclerView themeRecyclerView;
    private RecyclerView categoryRecyclerView;
    private RelativeLayout mProgressLayer;
    private ImageView ocb_loading;
    private AnimationDrawable ocbDrawable;
    //private CircleProgressView mCircleView;
    //private ProgressWheel mWheelProgress;
    private RelativeLayout mKeyboardLayer;
    private RelativeLayout mTabLayer;
    private TextView mTopLine;
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
    private RelativeLayout mGameLayer = null; // game zone
    private LinearLayout mBtnLayer;
    private TextView mHideKbdBtn;
    private LatinKeyboardView kv;
    private NewThemeListInfo mThemeListInfo;

    private ConstraintLayout category_layer;
    private ConstraintLayout search_layer;
    private TextView cancel_search;
    private RelativeLayout btn_clear;
    private EditText search_edit;

    private String selectedCategory = "00";

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                // downloadAndUnzipContent(true);
                downloadAndUnZip(true);
            } else if (msg.what == 1) {
                setTheme();
            }
        }
    };
    private RakeAPI rake;
    public void setRake(String page_id, String action_id) {
        new Thread() {
            public void run() {
                String track_id = SharedPreference.getString(KeyboardOCBCategoryThemeActivity.this, Key.KEY_OCB_TRACK_ID);
                String device_id = SharedPreference.getString(KeyboardOCBCategoryThemeActivity.this, Key.KEY_OCB_DEVICE_ID);
                try {
                    E_Cipher cp = E_Cipher.getInstance();
                    track_id = cp.Decode(KeyboardOCBCategoryThemeActivity.this, track_id);
                    device_id = cp.Decode(KeyboardOCBCategoryThemeActivity.this, device_id);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.aikbd_ocb_category_theme_activity);

        View root_layout = findViewById(R.id.root);
        Common.SetInset(root_layout);

        if ( CustomAsyncTask.GUBUN_RELEASE.equals(CustomAsyncTask.gubun) )
            rake = RakeAPI.getInstance(KeyboardOCBCategoryThemeActivity.this, Common.LIVE_TOKEN, RakeAPI.Env.LIVE, RakeAPI.Logging.DISABLE);
        else
            rake = RakeAPI.getInstance(KeyboardOCBCategoryThemeActivity.this, Common.DEV_TOKEN, RakeAPI.Env.DEV, RakeAPI.Logging.DISABLE);
        setRake("/keyboard/theme", "");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mIsFromSetting = bundle.getBoolean("IS_FROM_SETTING");
            mIsFromKeyboard = bundle.getBoolean("IS_FROM_KEYBOARD", false);
        } else
            finish();
        LinearLayout top_btn_layer = findViewById(R.id.top_btn_layer);
        top_btn_layer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        initTheme();

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRake("/keyboard/theme", "top_tap.backbtn");
                Intent intent = null;
                if ( !mIsFromKeyboard ) {
                    if (mIsFromSetting) {
                        if ( KeyboardSettingsActivity.mActivity != null )
                            KeyboardSettingsActivity.mActivity.finish();
                        intent = new Intent(KeyboardOCBCategoryThemeActivity.this, KeyboardSettingsActivity.class);
                        intent.putExtra("IS_FROM_SETTING", mIsFromSetting);
                        startActivity(intent);
                    } else {
                        intent = new Intent(KeyboardOCBCategoryThemeActivity.this, SettingSelectKeyboardActivity.class);
                        intent.putExtra("IS_FROM_SETTING", mIsFromSetting);
                        startActivity(intent);
                    }
                }

                finish();
                //overridePendingTransition(0, 0);
            }
        });
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
        sendBroadcast(new Intent(SoftKeyboard.SET_KEYBOARD_ON));
    }

    @Override
    public void onPause() {
        super.onPause();
        sendBroadcast(new Intent(SoftKeyboard.SET_KEYBOARD_OFF));
        Intent themeChange = new Intent("THEME_CHANGE");
        sendBroadcast(themeChange);
        UserIdDBHelper helper = new UserIdDBHelper(KeyboardOCBCategoryThemeActivity.this);
        KeyboardUserIdModel model = helper.getUserInfo();
        String userId = "";
        String deviceId = "";
        if ( model != null ) {
            userId = model.getUserId();
            deviceId = model.getDeviceId();
        }

        if ( userId == null || TextUtils.isEmpty(userId) )
            userId = "";

        if ( deviceId == null || TextUtils.isEmpty(deviceId) )
            deviceId = "";

        if ( mUsedTheme == null || TextUtils.isEmpty(mUsedTheme) )
            mUsedTheme = "";


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }

    public void onBackPressed() {
        if (mProgressLayer.getVisibility() == View.VISIBLE)
            return;
        if ( mKeyboardLayer.getVisibility() == View.VISIBLE ) {
//                mBtnLayer.setVisibility(View.GONE);
            repositioningBtn(false);
            mTabLayer.setVisibility(View.GONE);
            mKeyboardLayer.setVisibility(View.GONE);
            mHideKbdBtn.setVisibility(View.GONE);
        } else {
            Intent intent = null;
            if ( !mIsFromKeyboard ) {
                if (mIsFromSetting) {
                    intent = new Intent(KeyboardOCBCategoryThemeActivity.this, KeyboardSettingsActivity.class);
                    intent.putExtra("IS_FROM_SETTING", mIsFromSetting);
                    startActivity(intent);
                } else {
                    intent = new Intent(KeyboardOCBCategoryThemeActivity.this, SettingSelectKeyboardActivity.class);
                    intent.putExtra("IS_FROM_SETTING", mIsFromSetting);
                    startActivity(intent);
                }
            }
            finish();
            //overridePendingTransition(0, 0);
            //            super.onBackPressed();
        }
    }

    private void repositioningBtn(boolean isKeyboardVisible) {
        if ( isKeyboardVisible ) {
            RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            param.addRule(RelativeLayout.CENTER_HORIZONTAL);
            param.addRule(RelativeLayout.ABOVE, mTabLayer.getId());
            param.bottomMargin = 5;
            mBtnLayer.setLayoutParams(param);
        } else {
            RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            param.bottomMargin = 5;
            mBtnLayer.setLayoutParams(param);
        }
    }

    private void initTheme() {
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
        LogPrint.d("skkim initTheme mUsedTheme :: " + mUsedTheme);
        /**
        mWheelProgress = (ProgressWheel) findViewById(R.id.progress);
        mWheelProgress.setVisibility(View.GONE);**/
        mProgressLayer = (RelativeLayout) findViewById(R.id.progress_layer);
        mProgressLayer.setVisibility(View.GONE);
        ocb_loading = (ImageView) findViewById(R.id.ocb_loading);
        ocbDrawable = (AnimationDrawable) ocb_loading.getBackground();
        /**
        mCircleView = (CircleProgressView) findViewById(R.id.circleView);
        mCircleView.setOnProgressChangedListener(new CircleProgressView.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(float value) {
                KeyboardLogPrint.d("Progress Changed: " + value);
            }
        });

        mCircleView.setShowTextWhileSpinning(true); // Show/hide text in spinning mode
//        mCircleView.setText("Connecting...");
        mCircleView.setOnAnimationStateChangedListener(
                new AnimationStateChangedListener() {
                    @Override
                    public void onAnimationStateChanged(AnimationState _animationState) {
                        switch (_animationState) {
                            case IDLE:
                            case ANIMATING:
                            case START_ANIMATING_AFTER_SPINNING:
                                mCircleView.setTextMode(TextMode.PERCENT); // show percent if not spinning
                                mCircleView.setUnitVisible(true);
                                break;
                            case SPINNING:
                                mCircleView.setTextMode(TextMode.TEXT); // show text while spinning
                                mCircleView.setUnitVisible(false);
                            case END_SPINNING:
                                break;
                            case END_SPINNING_START_ANIMATING:
                                break;

                        }
                    }
                }
        );**/

        category_layer = findViewById(R.id.category_layer);
        search_layer = findViewById(R.id.search_layer);
        cancel_search = findViewById(R.id.cancel_search);
        btn_clear = findViewById(R.id.btn_clear);
        search_edit = findViewById(R.id.search_edit);

        mTopLine = (TextView) findViewById(R.id.top_line);

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
        mHideKbdBtn = (TextView) findViewById(R.id.hide_kbd_btn);
        mBtnLayer = (LinearLayout) findViewById(R.id.btn_layer);
        kv = (LatinKeyboardView) findViewById(R.id.keyboard_view);
        mKeyboardLayer = (RelativeLayout) findViewById(R.id.keyboard_layer);
        mTabLayer = (RelativeLayout) findViewById(R.id.match_container);
//        mBtnLayer.setVisibility(View.GONE);
        repositioningBtn(false);
        mTabLayer.setVisibility(View.GONE);
        mKeyboardLayer.setVisibility(View.GONE);
        mHideKbdBtn.setVisibility(View.GONE);

        Common.GetGameStatus(KeyboardOCBCategoryThemeActivity.this, new Listener.OnGameStatusListener() {
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

        mHideKbdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repositioningBtn(false);
                mKeyboardLayer.setVisibility(View.GONE);
                mTabLayer.setVisibility(View.GONE);
                mHideKbdBtn.setVisibility(View.GONE);
            }
        });

        search_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if ( search_edit != null && !TextUtils.isEmpty(search_edit.getText().toString()))
                        connectSearchedTheme(search_edit.getText().toString());
                    else
                        Toast.makeText(KeyboardOCBCategoryThemeActivity.this, "검색어를 입력하세요", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });

        cancel_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( category_layer != null )
                    category_layer.setVisibility(View.VISIBLE);
                if ( search_layer != null )
                    search_layer.setVisibility(View.GONE);
                if ( search_edit != null )
                    search_edit.setText("");
                connectThemeList(selectedCategory);
            }
        });

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( search_edit != null )
                    search_edit.setText("");
            }
        });

        mContext = this;
        mIsQwertyNumSet = SharedPreference.getBoolean(KeyboardOCBCategoryThemeActivity.this, Common.PREF_QWERTY_NUM_SETTING);
        mSetPreview = SharedPreference.getBoolean(KeyboardOCBCategoryThemeActivity.this, Common.PREF_PREVIEW_SETTING);
        setKeyboard(mSetPreview);
        kv.setOnKeyboardActionListener(this);

        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        LinearLayoutManager cManager = new LinearLayoutManager(KeyboardOCBCategoryThemeActivity.this);
        cManager.setOrientation(RecyclerView.HORIZONTAL);
        categoryRecyclerView.setLayoutManager(cManager);

        themeRecyclerView = findViewById(R.id.themeRecyclerView);
        GridLayoutManager manager = new GridLayoutManager(KeyboardOCBCategoryThemeActivity.this, 2);
        themeRecyclerView.setLayoutManager(manager);

        themeRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if ( mKeyboardLayer.getVisibility() == View.VISIBLE ) {
                    mKeyboardLayer.setVisibility(View.GONE);
                    mHideKbdBtn.setVisibility(View.GONE);
                    mTabLayer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        Display display = getWindowManager().getDefaultDisplay();
        int realScreenWidth = 0;
        if (Build.VERSION.SDK_INT >= 17) {
            //new pleasant way to get real metrics
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

        themeAdapter = new AikbdThemeAdapter(KeyboardOCBCategoryThemeActivity.this, realScreenWidth);
        themeAdapter.setThemeClickListener(new AikbdThemeAdapter.ThemeClickListener() {
            @Override
            public void onThemeClicked(NewThemeListInfo info, int position) {
                int index = position + 1;
                setRake("/keyboard/theme", "tap.keyboardtheme" + index);
                /*
                if ( position == 0 ) {
                    setRake("/keyboard/theme", "tap.keyboardtheme1");
                } else if ( position == 1 ) {
                    setRake("/keyboard/theme", "tap.keyboardtheme2");
                } else if ( position == 2 ) {
                    setRake("/keyboard/theme", "tap.keyboardtheme3");
                } else if ( position == 3 ) {
                    setRake("/keyboard/theme", "tap.keyboardtheme4");
                } else if ( position == 4 ) {
                    setRake("/keyboard/theme", "tap.keyboardtheme5");
                }
                */
                if ( mKeyboardLayer.getVisibility() == View.VISIBLE ) {
                    mKeyboardLayer.setVisibility(View.GONE);
                    mHideKbdBtn.setVisibility(View.GONE);
                    mTabLayer.setVisibility(View.GONE);
                } else {
                    if ( !TextUtils.isEmpty(info.getName()) )
                        selectTheme(position);
                }

            }
        });

        themeRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( mKeyboardLayer.getVisibility() == View.VISIBLE ) {
                    mKeyboardLayer.setVisibility(View.GONE);
                    mHideKbdBtn.setVisibility(View.GONE);
                    mTabLayer.setVisibility(View.GONE);
                }
            }
        });

        themeRecyclerView.setAdapter(themeAdapter);
        themeAdapter.setUseFileName(mUsedTheme);

        mThemeModel = ThemeManager.GetThemeModel(KeyboardOCBCategoryThemeActivity.this, 1);
        if (mThemeModel != null && kv != null ) {
            try {
                kv.setThemeModel(mThemeModel);
                NinePatchDrawable norNor = ThemeManager.GetNinePatch(KeyboardOCBCategoryThemeActivity.this, mThemeModel.getNorBtnNorI()); // 일반키 normal
                NinePatchDrawable norPre = ThemeManager.GetNinePatch(KeyboardOCBCategoryThemeActivity.this, mThemeModel.getNorBtnPreI()); // 일반키 pressed

                NinePatchDrawable speNor = ThemeManager.GetNinePatch(KeyboardOCBCategoryThemeActivity.this, mThemeModel.getSpBtnNorI()); // 특수키 normal
                NinePatchDrawable spePre = ThemeManager.GetNinePatch(KeyboardOCBCategoryThemeActivity.this, mThemeModel.getSpBtnPreI()); // 특수키 pressed
//                NinePatchDrawable bg = ThemeManager.GetNinePatch(Keyboard_Background_Activity.this, mThemeModel.getBgImg()); // 배경이미지
                Drawable bg = ThemeManager.GetDrawableFromPath(mThemeModel.getBgImg()); // 배경이미지, 나인페치 미적용 2017.12.04
//                Drawable bg = ThemeManager.GetNinePatch1(Keyboard_Background_Activity.this, mThemeModel.getBgImg()); // 배경이미지
                int txtColor = Color.parseColor(mThemeModel.getKeyText()); // 키 텍스트 색상
                String strOptColor = "#919191";
                if ( !TextUtils.isEmpty(mThemeModel.getKeyTextS()) )
                    strOptColor = mThemeModel.getKeyTextS();

                int optTxtColor = Color.parseColor(strOptColor);
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

                mImgSecond.setImageResource(R.drawable.aikbd_coupang_floating_icon);
                LogPrint.d("mUsedTheme :: " + mUsedTheme);
                Drawable tabChatGpt = getResources().getDrawable(R.drawable.aikbd_chat_gpt_theme);
                if ( "theme_color_01".equals(mUsedTheme) && !"theme_118".equals(mUsedTheme) )
                    tabChatGpt = getResources().getDrawable(R.drawable.aikbd_chat_gpt_normal);

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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        connectThemeList(selectedCategory);

        int kind = SharedPreference.getInt(this, Common.PREF_KEYBOARD_THEME_INDEX);
        if (kind < 0) kind = 0;

        Button cancelButton = (Button) findViewById(R.id.btn_cancel);
        Button nextButton = (Button) findViewById(R.id.btn_next);

        if ( mIsFromSetting ) {
            nextButton.setText(getResources().getString(R.string.aikbd_complete));
        } else {
            nextButton.setText(getResources().getString(R.string.aikbd_next));
        }

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                if ( !mIsFromKeyboard ) {
                    if (mIsFromSetting) {
                        intent = new Intent(KeyboardOCBCategoryThemeActivity.this, KeyboardSettingsActivity.class);
                        intent.putExtra("IS_FROM_SETTING", mIsFromSetting);
                        startActivity(intent);
                    } else {
                        intent = new Intent(KeyboardOCBCategoryThemeActivity.this, SettingSelectKeyboardActivity.class);
                        intent.putExtra("IS_FROM_SETTING", mIsFromSetting);
                        startActivity(intent);
                    }
                }

                finish();
                //overridePendingTransition(0, 0);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        findViewById(R.id.btn_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    setRake("/keyboard/theme", "top_tap.homebtn");
                    String url = "ocbt://com.skmc.okcashbag.home_google/main";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( search_layer != null ) {
                    search_layer.setVisibility(View.VISIBLE);
                }
                if ( category_layer != null )
                    category_layer.setVisibility(View.GONE);
            }
        });
    }

    private void setTheme() {
        String result = "";
        JSONObject object = new JSONObject();
        String destination = getFilesDir().getAbsolutePath() + File.separator  + "THEME" + File.separator + mThemeListInfo.getUnZipFileName() + File.separator;
        File file = new File(destination);
        if ( file.exists() ) {
            File[] fileList = file.listFiles();
            if ( fileList.length > 0 ) {
                for ( int i = 0 ; i < fileList.length ; i ++ ) {
                    File inFile = fileList[i];
                    if ( inFile.isDirectory() ) {
                        KeyboardLogPrint.e("inFile directory name :: " + inFile.getName());
                    } else {
                        KeyboardLogPrint.i("inFile not directory name :: " + inFile.getName());
                        String fileName = inFile.getName();
                        object = makeJSON(object, fileName, destination);
                    }
                }
                result = object.toString();
            }
        } else {
            return;
        }
        mThemeModel = ThemeManager.GetThemeModel(result,2);
        if ( mThemeModel != null ) {
            KeyboardLogPrint.e("mThemeModel is not null");
            try {
                AIKBD_DBHelper helper = new AIKBD_DBHelper(KeyboardOCBCategoryThemeActivity.this);
                helper.deleteTheme();
                helper.insertTheme(result);
                kv.setThemeModel(mThemeModel);

                double scale = ThemeManager.GetScale(KeyboardOCBCategoryThemeActivity.this);
                KeyboardLogPrint.e("scale :: " + scale);
                if (scale != 1 && mThemeModel != null ) {
                    KeyboardLogPrint.e("scale is not 1");
                    ThemeManager.ResizingSpImage(mThemeModel.getBackImg(), Bitmap.CompressFormat.PNG, 100, scale);
                    ThemeManager.ResizingSpImage(mThemeModel.getSpaceImg(), Bitmap.CompressFormat.PNG, 100, scale);
                    ThemeManager.ResizingSpImage(mThemeModel.getEnterImg(), Bitmap.CompressFormat.PNG, 100, scale);
                    ThemeManager.ResizingSpImage(mThemeModel.getKeySearchEnter(), Bitmap.CompressFormat.PNG, 100, scale);
                    ThemeManager.ResizingSpImage(mThemeModel.getEmojiImg(), Bitmap.CompressFormat.PNG, 100, scale);
                    ThemeManager.ResizingSpImage(mThemeModel.getKeySymbol(), Bitmap.CompressFormat.PNG, 100, scale);
                    ThemeManager.ResizingSpImage(mThemeModel.getKeyLang(), Bitmap.CompressFormat.PNG, 100, scale);
                    ThemeManager.ResizingSpImage(mThemeModel.getShiftImg(), Bitmap.CompressFormat.PNG, 100, scale);
                    if ( !TextUtils.isEmpty(mThemeModel.getShiftImg1()) ) {
                        ThemeManager.ResizingSpImage(mThemeModel.getShiftImg1(), Bitmap.CompressFormat.PNG, 100, scale);
                    }
                    if ( !TextUtils.isEmpty(mThemeModel.getShiftImg2()) ) {
                        ThemeManager.ResizingSpImage(mThemeModel.getShiftImg2(), Bitmap.CompressFormat.PNG, 100, scale);
                    }
//                        ThemeManager.ResizingSpImage(mThemeModel.getBgImg(), Bitmap.CompressFormat.PNG, 100, scale);
                } else {
                    KeyboardLogPrint.e("scale is 1");
                }

                NinePatchDrawable norNor = ThemeManager.GetNinePatch(KeyboardOCBCategoryThemeActivity.this, mThemeModel.getNorBtnNorI()); // 일반키 normal
                NinePatchDrawable norPre = ThemeManager.GetNinePatch(KeyboardOCBCategoryThemeActivity.this, mThemeModel.getNorBtnPreI()); // 일반키 pressed
                Drawable norBtnSelector = ThemeManager.GetImageSelector(norNor, norPre); // 일반키 selector
                int norAlpha = mThemeModel.getNorAlpha();

                NinePatchDrawable speNor = ThemeManager.GetNinePatch(KeyboardOCBCategoryThemeActivity.this, mThemeModel.getSpBtnNorI()); // 특수키 normal
                NinePatchDrawable spePre = ThemeManager.GetNinePatch(KeyboardOCBCategoryThemeActivity.this, mThemeModel.getSpBtnPreI()); // 특수키 pressed
                Drawable spBtnSelector = ThemeManager.GetImageSelector(speNor, spePre); // 특수키 selector
                int spAlpha = mThemeModel.getSpAlpha();
//                    NinePatchDrawable bg = ThemeManager.GetNinePatch(Keyboard_Background_Activity.this, mThemeModel.getBgImg()); // 배경이미지
                final Drawable bg = ThemeManager.GetDrawableFromPath(mThemeModel.getBgImg()); // 배경이미지, 나인페치 미적용 2017.12.04
//                    Drawable bg = ThemeManager.GetNinePatch1(Keyboard_Background_Activity.this, mThemeModel.getBgImg()); // 배경이미지
                mBgAlpha = mThemeModel.getBgAlpha();
                int txtColor = Color.parseColor(mThemeModel.getKeyText()); // 키 텍스트 색상
                String strOptColor = "#919191";
                if ( !TextUtils.isEmpty(mThemeModel.getKeyTextS()) )
                    strOptColor = mThemeModel.getKeyTextS();

                int optTxtColor = Color.parseColor(strOptColor);
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

                kv.setKeyTextColor(txtColor, txtColor, optTxtColor);
                kv.setTBackground(norBtnSelector, spBtnSelector);

                kv.setLabelPaint();
                kv.invalidateAllKey();
                /**
                Intent themeChange = new Intent("THEME_CHANGE");
                sendBroadcast(themeChange);
**/
                kv.setBackgroundDrawable(bg);
                if (kv.getBackground() != null)
                    kv.getBackground().setAlpha(mBgAlpha);

                //mHideKbdBtn.setVisibility(View.VISIBLE);
                mTabLayer.setVisibility(View.VISIBLE);
                mKeyboardLayer.setVisibility(View.VISIBLE);
                setKeyboard(mSetPreview);
//                    mBtnLayer.setVisibility(View.VISIBLE);
                repositioningBtn(true);
                mUsedTheme = mThemeListInfo.getUnZipFileName();
                LogPrint.d("skkim mUsedTheme :: " + mUsedTheme);

                themeAdapter.setUseFileName(mThemeListInfo.getUnZipFileName());
                themeAdapter.notifyDataSetChanged();
/**
                if ( !TextUtils.isEmpty(mUsedTheme) ) {
                    CustomAsyncTask task = new CustomAsyncTask(KeyboardOCBCategoryThemeActivity.this);
                    task.setUserInfo("", mUsedTheme, "", "",  new CustomAsyncTask.OnDefaultObjectCallbackListener() {
                        @Override
                        public void onResponse(boolean result, Object obj) {

                        }
                    });
                }
**/
                try {
                    if ( kv != null ) {
                        kv.post(new Runnable() {
                            @Override
                            public void run() {
                                if ( !TextUtils.isEmpty(mThemeModel.getBgOriginImg()) ) {
                                    if ( kv != null ) {
                                        ResizingAsync task = new ResizingAsync(kv.getWidth(),kv.getHeight());
                                        task.execute();
                                    }
                                } else {
                                    kv.setBackgroundDrawable(bg);
                                    if (kv.getBackground() != null)
                                        kv.getBackground().setAlpha(mBgAlpha);
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ocbDrawable.stop();


                //mCircleView.stopSpinning();
                mProgressLayer.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            KeyboardLogPrint.e("mThemeModel is null");
    }

    private void downloadAndUnZip(boolean isCommon ) {
        String destination = ""; // 다운받을 파일 위치
        String unZipFileName = ""; // 압축 풀 파일명
        String path = ""; // 다운받을 파일의 서버주소
        if ( isCommon ) {
            destination = getFilesDir().getAbsolutePath() + File.separator + "THEME" + File.separator + mThemeListInfo.getCommonZipFileName();
            unZipFileName = mThemeListInfo.getCommonUnZipFileName();
            path = mThemeListInfo.getCommonDownloadUrl();
        } else {
            destination = getFilesDir().getAbsolutePath() + File.separator + "THEME" + File.separator + mThemeListInfo.getCustomZipFileName();
            unZipFileName = mThemeListInfo.getCustomUnZipFileName();
            path = mThemeListInfo.getCustomDownloadUrl();
        }
        Download download = new Download(destination, unZipFileName, isCommon);
        download.execute(path);
    }

    public class Download extends AsyncTask<String, String, String> {
        private File file;
        private String downloadLocation;
        private boolean isCommon = false;
        public Download(String downloadLocation, String folderName, boolean isCommon) {
            this.downloadLocation = downloadLocation;
            this.isCommon = isCommon;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressLayer.setVisibility(View.VISIBLE);
            ocbDrawable.start();
        }

        @Override
        protected String doInBackground(String... aurl) {
            int count;

            try {
                URL url = new URL(aurl[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                int lenghtOfFile = connection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream());
                file = new File(downloadLocation);
                FileOutputStream output = new FileOutputStream(file);

                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress(""+(int)((total*100)/lenghtOfFile));
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
            /**
            try {
                int val = Integer.valueOf(progress[0]);
                mCircleView.setValueAnimated(val, 100);
            } catch (Exception e) {
                e.printStackTrace();
            }**/
        }

        @Override
        protected void onPostExecute(String unused) {
            KeyboardLogPrint.e("download " + isCommon + " :: complete");
            ocbDrawable.start();
            /**
            mCircleView.setText("압축해제중");
            mCircleView.spin();**/
            UnZip unzip = new UnZip(isCommon);
            unzip.execute("");
        }
    }

    public class UnZip extends AsyncTask<String, String, Boolean> {
        boolean isCommon = false;
        String destination = "";
        public UnZip(boolean isCommon ) {
            this.isCommon = isCommon;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... str) {
            //String resultStr = "";
            String zipFile = "";
            if ( isCommon ) {
                zipFile = getFilesDir().getAbsolutePath() + File.separator + "THEME" + File.separator + mThemeListInfo.getCommonZipFileName();
                destination = getFilesDir().getAbsolutePath() + File.separator  + "THEME" + File.separator + mThemeListInfo.getCommonUnZipFileName() + File.separator;
            } else {
                zipFile = getFilesDir().getAbsolutePath() + File.separator + "THEME" + File.separator + mThemeListInfo.getCustomZipFileName();
                destination = getFilesDir().getAbsolutePath() + File.separator  + "THEME" + File.separator + mThemeListInfo.getCustomUnZipFileName() + File.separator;
            }
            int finalSize = (int) new File(zipFile).length();
            KeyboardLogPrint.e("percentage final size :: " + finalSize);
            dirChecker(destination, "");
            byte[] buffer = new byte[BUFFER_SIZE];
            try {
                FileInputStream fin = new FileInputStream(zipFile);
                ZipInputStream zin = new ZipInputStream(fin);
                ZipEntry ze = null;
                long inSize = 0;
                while ((ze = zin.getNextEntry()) != null) {
                    inSize = inSize + ze.getCompressedSize();
                    KeyboardLogPrint.e("percentage ze.getSize() ::" + ze.getCompressedSize());
                    KeyboardLogPrint.d("percentage inSize :: " + inSize);
                    if (ze.isDirectory()) {
                        dirChecker(destination, ze.getName());
                    } else {
                        // File f = new File(destination + ze.getName());
                        File f = new File(destination, ze.getName());
                        try {
                            ensureZipPathSafety(f, destination);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }

                        KeyboardLogPrint.d("after throw");
                        if (!f.exists()) {
                            FileOutputStream fout = new FileOutputStream(destination + ze.getName());
                            int count;
                            while ((count = zin.read(buffer)) != -1) {
                                fout.write(buffer, 0, count);
                            }
                            zin.closeEntry();
                            fout.close();
                        }
                    }
                }
                zin.close();

                String destPath = getFilesDir().getAbsolutePath() + File.separator  + "THEME" + File.separator + mThemeListInfo.getUnZipFileName() + File.separator;
                File dFile = new File(destPath);
                if ( !dFile.exists() )
                    dFile.mkdirs();

                File file = new File(destination);
                File[] fileList = file.listFiles();
                if ( fileList.length > 0 ) {
                    for ( int i = 0 ; i < fileList.length ; i ++ ) {
                        File inFile = fileList[i];
                        if ( inFile.isDirectory() ) {
                            KeyboardLogPrint.e("inFile directory name :: " + inFile.getName());
                        } else {
                            KeyboardLogPrint.i("copy inFile not directory name :: " + inFile.getName());
                            inFile.renameTo(new File(destPath + inFile.getName()));
                        }
                    }
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        protected void onProgressUpdate(String... progress) {
            KeyboardLogPrint.d("unzip :: " + progress[0]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            KeyboardLogPrint.e("unzip post");
            if ( result ) {
                ocbDrawable.stop();
                //mCircleView.stopSpinning();
                mProgressLayer.setVisibility(View.GONE);
                /**
                String path = "";
                if ( isCommon ) {
                    path = getFilesDir().getAbsolutePath() + File.separator  + "THEME" + File.separator + mThemeListInfo.getCommonZipFileName();
                } else {
                    path = getFilesDir().getAbsolutePath() + File.separator  + "THEME" + File.separator + mThemeListInfo.getCustomZipFileName();
                }
                path = getFilesDir().getAbsolutePath() + File.separator  + "THEME" + File.separator + mThemeListInfo.getUnZipFileName();
                Delete delTask = new Delete(isCommon);
                delTask.execute(getFilesDir().getAbsolutePath() + File.separator  + "THEME" + File.separator);**/
                if ( isCommon ) {
                    downloadAndUnZip(false);
                } else {
                    Delete del = new Delete();
                    del.execute(getFilesDir().getAbsolutePath() + File.separator  + "THEME" + File.separator + mThemeListInfo.getUnZipFileName());

                }
            }
        }
    }

    public class Delete extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String exceptFolder = params[0];
            ThemeManager.DeleteFile(KeyboardOCBCategoryThemeActivity.this, getFilesDir().getAbsolutePath() + File.separator  + "THEME" + File.separator, exceptFolder);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if ( result ) {
                if ( mHandler != null ) {
                    mHandler.sendEmptyMessage(1);
                }
            }
        }

        @Override
        protected void onPreExecute() {

        }
    }

    public class ResizingAsync extends AsyncTask<Void, String, Boolean> {
        int keyboardWidth;
        int keyboardHeight;
        public ResizingAsync(int width, int height){
            LogPrint.d("keyboard width :: " + width );
            LogPrint.d("keyboard height :: " + height );
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

                        SharedPreference.setBoolean(KeyboardOCBCategoryThemeActivity.this, Key.KEY_HAS_THEME, true);

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
                    //kv.setBackgroundDrawable(mBgDrawable);
                    kv.setBackground(mBgDrawable);
                    if (kv.getBackground() != null)
                        kv.getBackground().setAlpha(mBgAlpha);
                }
            }
            //mWheelProgress.setVisibility(View.GONE);
        }
    }

    private void ensureZipPathSafety(final File outputFile, final String destDirectory) throws Exception {
        String destDirCanonicalPath = (new File(destDirectory)).getCanonicalPath();
        String outputFileCanonicalPath = outputFile.getCanonicalPath();
        if (!outputFileCanonicalPath.startsWith(destDirCanonicalPath)) {
            throw new Exception(String.format("Found Zip Path Traversal Vulnerability with %s", outputFileCanonicalPath));
        }
    }

    private JSONObject makeJSON(JSONObject object, String fileName, String destination) {
        try {
            String fName = destination + fileName;
            if ( ThemeManager.KEY_DEL.equals(fileName) ) {
                object.put("key_del", destination + fileName);
            } else if ( ThemeManager.KEY_SHIFT.equals(fileName) ) {
                object.put("shift_img", destination + fileName);
            } else if ( ThemeManager.KEY_SHIFT_1.equals(fileName) ) {
                object.put("shift_img_1", destination + fileName);
            } else if ( ThemeManager.KEY_SHIFT_2.equals(fileName) ) {
                object.put("shift_img_2", destination + fileName);
            } else if ( ThemeManager.KEY_ENTER.equals(fileName) ) {
                object.put("enter_img", destination + fileName);
            } else if ( ThemeManager.KEY_EMOJI.equals(fileName) ) {
                object.put("emoji_img", destination + fileName);
            } else if ( ThemeManager.KEY_EMOTICON.equals(fileName) ) {
                object.put("emoticon_img", destination + fileName);
            } else if ( ThemeManager.KEY_NOR_OFF.equals(fileName) ) {
                object.put("nor_btn_nor_i", destination + fileName);
            } else if ( ThemeManager.KEY_NOR_ON.equals(fileName) ) {
                object.put("nor_btn_pre_i", destination + fileName);
            } else if ( ThemeManager.KEY_SPACE.equals(fileName) ) {
                object.put("space_img", destination + fileName);
            } else if ( ThemeManager.KEY_SPE_OFF.equals(fileName) ) {
                object.put("sp_btn_nor_i", destination + fileName);
            } else if ( ThemeManager.KEY_SPE_ON.equals(fileName) ) {
                object.put("sp_btn_pre_i", destination + fileName);
            } else if ( ThemeManager.BG.equals(fileName) ) {
                object.put("bgimg", destination + fileName);
            } else if ( ThemeManager.BG_ORIGIN.equals(fileName) ) {
                object.put("bgoriginimg", destination + fileName);
            } else if ( ThemeManager.TAB_BOOKMARK.equals(fileName) ) {
                object.put("tab_bookmark", destination + fileName);
            } else if ( ThemeManager.TAB_CASH.equals(fileName) ) {
                object.put("tab_cash", destination + fileName);
            } else if ( ThemeManager.TAB_ZERO_CASH.equals(fileName) ) {
                object.put("tab_zero_cash", destination + fileName);
            } else if ( ThemeManager.KEY_SEARCH_ENTER.equals(fileName) ) {
                object.put("key_search_enter", destination + fileName);
            } else if ( ThemeManager.TAB_LOGO.equals(fileName) ) {
                object.put("tab_logo", destination + fileName);
            } else if ( ThemeManager.TAB_MEMO.equals(fileName) ) {
                object.put("tab_memo", destination + fileName);
            } else if ( ThemeManager.TAB_MEMO_PLUS.equals(fileName) ) {
                object.put("tab_memo_plus", destination + fileName);
            } else if ( ThemeManager.KEY_SYMBOL.equals(fileName) ) {
                object.put("key_symbol", destination + fileName);
            } else if ( ThemeManager.KEY_LANG.equals(fileName) ) {
                object.put("key_lang", destination + fileName);
            } else if ( ThemeManager.KEY_MEMO_PLUS.equals(fileName) ) {
                object.put("key_memo_plus", destination + fileName);
            } else if ( ThemeManager.FAV_BOT_OFF.equals(fileName) ) {
                object.put("fav_bot_off", destination + fileName);
            } else if ( ThemeManager.FAV_BOT_ON.equals(fileName) ) {
                object.put("fav_bot_on", destination + fileName);
            } else if ( ThemeManager.FAV_MEMO_OFF.equals(fileName) ) {
                object.put("fav_memo_off", destination + fileName);
            } else if ( ThemeManager.FAV_MEMO_ON.equals(fileName) ) {
                object.put("fav_memo_on", destination + fileName);
            } else if ( ThemeManager.DEL_BOT.equals(fileName) ) {
                object.put("del_bot", destination + fileName);
            } else if ( ThemeManager.KEYBOARD_BOT.equals(fileName) ) {
                object.put("keyboard_bot", destination + fileName);
            } else if ( ThemeManager.GO_MEMO.equals(fileName) ) {
                object.put("go_memo", destination + fileName);
            } else if ( ThemeManager.TAB_EMOJI.equals(fileName) ) {
                object.put("tab_emoji", destination + fileName);
            } else if ( ThemeManager.TAB_EMOJI_ON.equals(fileName) ) {
                object.put(ThemeManager.KEY_TAB_EMOJI_ON, destination + fileName);
            } else if ( ThemeManager.TAB_OK_CASHBACK_LOGO.equals(fileName) ) {
                object.put(ThemeManager.KEY_TAB_OK_CASHBACK, destination + fileName);
            } else if ( ThemeManager.TAB_OLABANG.equals(fileName) ) {
                object.put(ThemeManager.KEY_TAB_ORABANG, destination + fileName);
            } else if ( ThemeManager.TAB_SHOPPING.equals(fileName) ) {
                object.put(ThemeManager.KEY_TAB_SHOPPING, destination + fileName);
            } else if ( ThemeManager.TAB_MORE.equals(fileName) ) {
                object.put(ThemeManager.KEY_TAB_MORE, destination + fileName);
            } else if ( ThemeManager.TAB_OCB_SEARCH.equals(fileName) ) {
                object.put(ThemeManager.KEY_TAB_OCB_SEARCH, destination + fileName);
            } else if ( ThemeManager.TAB_MORE_ON.equals(fileName) ) {
                object.put(ThemeManager.KEY_TAB_MORE_ON, destination + fileName);
            } else if ( ThemeManager.TAB_MY_ON.equals(fileName) ) {
                object.put(ThemeManager.KEY_TAB_MY_ON, destination + fileName);
            } else if ( ThemeManager.TAB_OLABANG_ON.equals(fileName) ) {
                object.put(ThemeManager.KEY_TAB_ORABANG_ON, destination + fileName);
            } else if ( ThemeManager.TAB_OCB_SEARCH_ON.equals(fileName) ) {
                object.put(ThemeManager.KEY_TAB_OCB_SEARCH_ON, destination + fileName);
            } else if ( ThemeManager.TAB_SHOPPING_ON.equals(fileName) ) {
                object.put(ThemeManager.KEY_TAB_SHOPPING_ON, destination + fileName);
            } else if ( ThemeManager.TAB_MY.equals(fileName) ) {
                object.put(ThemeManager.KEY_TAB_MY, destination + fileName);
            } else if ( ThemeManager.THEME_COLOR.equals(fileName) ) {
                object = parseColor(object, fileName, destination);
            } else if ( ThemeManager.BTN_EMOTICON.equals(fileName) ) {
                object.put(ThemeManager.KEY_EMOTICON_IMG, destination + fileName);
            } else if ( ThemeManager.IMG_RECENT_EMOTICON.equals(fileName) ) {
                object.put(ThemeManager.KEY_EMOTICON_RECENT, destination + fileName);
            } else if ( ThemeManager.IMG_FIRST_EMOTICON.equals(fileName) ) {
                object.put(ThemeManager.KEY_EMOTICON_FIRST, destination + fileName);
            } else if ( ThemeManager.IMG_SECOND_EMOTICON.equals(fileName) ) {
                object.put(ThemeManager.KEY_EMOTICON_SECOND, destination + fileName);
            } else if ( ThemeManager.IMG_THIRD_EMOTICON.equals(fileName) ) {
                object.put(ThemeManager.KEY_EMOTICON_THIRD, destination + fileName);
            } else if ( ThemeManager.IMG_FOURTH_EMOTICON.equals(fileName) ) {
                object.put(ThemeManager.KEY_EMOTICON_FOURTH, destination + fileName);
            } else if ( ThemeManager.IMG_FIFTH_EMOTICON.equals(fileName) ) {
                object.put(ThemeManager.KEY_EMOTICON_FIFTH, destination + fileName);
            } else if ( ThemeManager.IMG_SIXTH_EMOTICON.equals(fileName) ) {
                object.put(ThemeManager.KEY_EMOTICON_SIXTH, destination + fileName);
            } else if ( ThemeManager.IMG_AD_DEL.equals(fileName)) {
                object.put(ThemeManager.KEY_AD_DEL, destination + fileName);
            } else if ( ThemeManager.BRAND_ICON_OFF.equals(fileName)) {
                object.put(ThemeManager.KEY_ICON_BRAND_OFF, destination + fileName);
            } else if ( ThemeManager.BRAND_ICON_ON.equals(fileName)) {
                object.put(ThemeManager.KEY_ICON_BRAND_ON, destination + fileName);
            } else if ( ThemeManager.BRAND_ICON_ON_2.equals(fileName)) {
                object.put(ThemeManager.KEY_ICON_BRAND_ON_2, destination + fileName);
            } else if ( ThemeManager.IMG_KEY_PREVIEW.equals(fileName) ) {
                object.put(ThemeManager.KEY_PREVIEW, destination + fileName);
            } else if ( ThemeManager.TAB_OCB_SAVE_SHOPPING.equals(fileName) ) {
                object.put(ThemeManager.KEY_TAB_OCB_SAVE_SHOPPING, destination + fileName);
            } else if ( ThemeManager.TAB_OCB_SAVE_SHOPPING_ON.equals(fileName) ) {
                object.put(ThemeManager.KEY_TAB_OCB_SAVE_SHOPPING_ON, destination + fileName);
            }
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String ReadTextFile(String fileName, String destination ) {
        String text = null;
        try {
            File file = new File(destination + fileName);
            FileInputStream fis = new FileInputStream(file);
            Reader in = new InputStreamReader(fis);
            int size = fis.available();
            char[] buffer = new char[size];
            in.read(buffer);
            in.close();

            text = new String(buffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return text;
    }

    private JSONObject parseColor(JSONObject object, String fileName, String destination) {
        try {
            String colorStr = ReadTextFile(fileName, destination);
            KeyboardLogPrint.e("colorStr :: " + colorStr);
            if ( !TextUtils.isEmpty(colorStr) ) {
                JSONObject obj = new JSONObject(colorStr);
                object.put("top_line", obj.optString("top_line"));
                object.put("bot_line", obj.optString("bot_line"));
                object.put("key_text", obj.optString("key_text"));
                object.put("key_text_s", obj.optString("key_text_s"));
                object.put("tab_off", obj.optString("tab_off"));
                object.put("tab_on", obj.optString("tab_on"));
                object.put("fav_text", obj.optString("fav_text"));
                object.put("down_theme", obj.optString("down_theme"));
                object.put("sp_key_alpha", obj.optString("sp_key_alpha"));
                object.put("nor_key_alpha", obj.optString("nor_key_alpha"));
                object.put("bg_alpha", obj.optString("bg_alpha"));
                object.put("nor_btn_color", obj.optInt("nor_btn_color"));
                object.put("sp_btn_color", obj.optInt("sp_btn_color"));
                object.put("bot_tab_color", obj.optString("bot_tab_color"));
            }

            return object;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void dirChecker(String destination, String dir) {
        File f = new File(destination + dir);

        if (!f.isDirectory()) {
            boolean success = f.mkdirs();
            if (!success) {
                KeyboardLogPrint.w("Failed to create folder " + f.getName());
            }
        }
    }

    private void selectTheme(int position) {
        if ( mProgressLayer.getVisibility() == View.VISIBLE )
            return;

        mThemeListInfo = themeAdapter.getItem(position);
        if ( !mThemeListInfo.getUnZipFileName().equals(mUsedTheme) ) {
            mHandler.sendEmptyMessage(0);
        } else {
            if ( mKeyboardLayer.getVisibility() == View.VISIBLE ) {
                mHideKbdBtn.setVisibility(View.GONE);
                mTabLayer.setVisibility(View.GONE);
                mKeyboardLayer.setVisibility(View.GONE);
                repositioningBtn(false);
            } else {
                //mHideKbdBtn.setVisibility(View.VISIBLE);
                mTabLayer.setVisibility(View.VISIBLE);
                mKeyboardLayer.setVisibility(View.VISIBLE);
                repositioningBtn(true);
            }
        }
    }

    private void setKeyboard(boolean setValue) {

        int kind = SharedPreference.getInt(this, Common.PREF_KEYBOARD_MODE);
        if (kind < 0) kind = 0;
        KeyboardLogPrint.e("Keyboard_Background_Activity kind :: " + kind);
        int level = SharedPreference.getInt(mContext, Common.PREF_KEYBOARD_SIZE_LEVEL) < 0 ? Common.DEFAULT_KEYBOARD_SIZE : SharedPreference.getInt(mContext, Common.PREF_KEYBOARD_SIZE_LEVEL);
//        level = 0; // keyboard height를 최소로
//        float fLevel = Float.parseFloat(String.format("%.2f", 0.90 + (float) (2 * level) / 100));
        float fLevel = Common.GetHeightValue(level);
        if (kind == Common.MODE_CHUNJIIN) {
            LatinKeyboard sejong = new LatinKeyboard(this, R.xml.aikbd_sejong_setting);
            adjustKeyboardKeyHeight(sejong, fLevel);
            kv.setKeyboard(sejong);
            kv.setPreviewEnabled(false);
        } else if (kind == Common.MODE_QUERTY) {
            //LatinKeyboard korean = new LatinKeyboard(this, R.xml.aikbd_korean);
            LatinKeyboard korean = new LatinKeyboard(this, mIsQwertyNumSet ? R.xml.aikbd_korean_n_setting : R.xml.aikbd_korean_setting);
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
        }else if (kind == Common.MODE_CHUNJIIN_PLUS) {
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

    private void adjustKeyboardKeyHeight(LatinKeyboard keyboard, double newKeyHeight) {
        int height = 0;
        if ( keyboard != null ) {
            for (Keyboard.Key key : keyboard.getKeys()) {
                key.height *= newKeyHeight;
                key.y *= newKeyHeight;
                height = key.height;
            }
            LogPrint.d("khskkim ocb theme height :: " + height);
            keyboard.setHeight(height);
        }
    }

    private void connectThemeList(String categoryCode) {

        UserIdDBHelper helper = new UserIdDBHelper(KeyboardOCBCategoryThemeActivity.this);
        KeyboardUserIdModel model = helper.getUserInfo();
        String userId = "";
        if (model != null) {
            userId = model.getUserId();
        }

        if (userId == null || TextUtils.isEmpty(userId))
            userId = "";

        int scaleType = ThemeManager.GetScaleLevel(KeyboardOCBCategoryThemeActivity.this);
        CustomAsyncTask task = new CustomAsyncTask(KeyboardOCBCategoryThemeActivity.this);
        KeyboardLogPrint.e("userId :: " + userId);
        KeyboardLogPrint.e("scaleType :: " + scaleType);
        task.connectNewThemeList(userId, scaleType, categoryCode, "", new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                if ( result ) {
                    try {
                        JSONObject object = (JSONObject) obj;
                        if (object != null) {
                            if ( mCategoryArray == null ) {
                                JSONArray cArray = object.optJSONArray("category");
                                if (cArray != null && cArray.length() > 0) {
                                    mCategoryArray = new ArrayList<>();
                                    for (int i = 0; i < cArray.length(); i++) {
                                        JSONObject in_c_obj = cArray.getJSONObject(i);
                                        CategoryData cData = new CategoryData();
                                        if ( in_c_obj != null ) {
                                            String code_id = in_c_obj.optString("code_id");
                                            String code_val = in_c_obj.optString("code_val");
                                            cData.setCategoryCode(code_id);
                                            cData.setCategoryName(code_val);
                                            if ( i == 0 )
                                                cData.setSelected(true);
                                            else
                                                cData.setSelected(false);
                                            mCategoryArray.add(cData);
                                        }
                                    }
                                    categoryAdapter = new OCBThemeCategoryAdapter(KeyboardOCBCategoryThemeActivity.this, new OCBThemeCategoryAdapter.OnClickListener() {
                                        @Override
                                        public void onCategoryClicked(CategoryData data) {
                                            selectedCategory = data.getCategoryCode();
                                            connectThemeList(selectedCategory);
                                        }
                                    });
                                    categoryAdapter.setItems(mCategoryArray);
                                    categoryRecyclerView.setAdapter(categoryAdapter);
                                }
                            }
                            JSONArray array = object.optJSONArray("data");
                            if (array != null && array.length() > 0) {
                                mThemeListArray = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject in_obj = array.getJSONObject(i);
                                    NewThemeListInfo info = new NewThemeListInfo();
                                    if (in_obj != null) {
                                        String commonDownloadUrl = in_obj.optString("common_down_path");
                                        String customDownloadUrl = in_obj.optString("custom_down_path");
                                        String commonZipFileName = "";
                                        String customZipFileName = "";
                                        String commonUnZipFileName = "";
                                        String customUnZipFileName = "";
                                        if (!TextUtils.isEmpty(commonDownloadUrl) && commonDownloadUrl.contains("/")) {
                                            commonZipFileName = commonDownloadUrl.substring(commonDownloadUrl.lastIndexOf("/") + 1);
                                            String tName = commonZipFileName.replaceAll(".zip", "");
                                            commonUnZipFileName = "un_" + tName;

                                        }

                                        if (!TextUtils.isEmpty(customDownloadUrl) && customDownloadUrl.contains("/")) {
                                            customZipFileName = customDownloadUrl.substring(customDownloadUrl.lastIndexOf("/") + 1);
                                            String tName = customZipFileName.replaceAll(".zip", "");
                                            customUnZipFileName = "un_" + tName;
                                        }
                                        info.setDownloadCount(in_obj.optString("down_cnt"));
                                        info.setCommonZipFileName(commonZipFileName);
                                        info.setCommonUnZipFileName(commonUnZipFileName);
                                        info.setCustomZipFileName(customZipFileName);
                                        info.setCustomUnZipFileName(customUnZipFileName);
                                        info.setCommonDownloadUrl(commonDownloadUrl);
                                        info.setCustomDownloadUrl(customDownloadUrl);
                                        info.setImage(in_obj.optString("image"));
                                        info.setName(in_obj.optString("name"));
                                        info.setUnZipFileName(in_obj.optString("unzip_file_name"));
                                        info.setCategory(in_obj.optString("cat"));
                                        info.setIsNew(in_obj.optString("theme_new_YN"));
                                        info.setIsPopular(in_obj.optString("theme_popular_YN"));
                                        KeyboardLogPrint.e("sub cate :: " + in_obj.optString("cat"));
                                        mThemeListArray.add(info);
                                    }
                                }
                                if ( mThemeListArray.size() > 0 ) {
                                    if ( mThemeListArray.size()%2 != 0 ) {
                                        NewThemeListInfo info = new NewThemeListInfo();
                                        info.setName("");
                                        mThemeListArray.add(info);
                                    }
                                }


                                themeAdapter.setItems(mThemeListArray);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        JSONObject object = (JSONObject) obj;
                        if (object != null) {
                            String error = object.optString(Common.NETWORK_ERROR);
                            String dError = object.optString(Common.NETWORK_DISCONNECT);
                            if ( !TextUtils.isEmpty(error) ) {
                                Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show();
                            } else {
                                if ( !TextUtils.isEmpty(dError) ) {
                                    Toast.makeText(mContext, dError, Toast.LENGTH_SHORT).show();
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

    private void connectSearchedTheme(String keyword) {

        UserIdDBHelper helper = new UserIdDBHelper(KeyboardOCBCategoryThemeActivity.this);
        KeyboardUserIdModel model = helper.getUserInfo();
        String userId = "";
        if (model != null) {
            userId = model.getUserId();
        }

        if (userId == null || TextUtils.isEmpty(userId))
            userId = "";

        int scaleType = ThemeManager.GetScaleLevel(KeyboardOCBCategoryThemeActivity.this);
        CustomAsyncTask task = new CustomAsyncTask(KeyboardOCBCategoryThemeActivity.this);
        KeyboardLogPrint.e("userId :: " + userId);
        KeyboardLogPrint.e("scaleType :: " + scaleType);
        task.connectNewThemeList(userId, scaleType, "", keyword, new CustomAsyncTask.OnDefaultObjectCallbackListener() {
            @Override
            public void onResponse(boolean result, Object obj) {
                if ( result ) {
                    try {
                        JSONObject object = (JSONObject) obj;
                        if (object != null) {
                            if ( mCategoryArray == null ) {
                                JSONArray cArray = object.optJSONArray("category");
                                if (cArray != null && cArray.length() > 0) {
                                    mCategoryArray = new ArrayList<>();
                                    for (int i = 0; i < cArray.length(); i++) {
                                        JSONObject in_c_obj = cArray.getJSONObject(i);
                                        CategoryData cData = new CategoryData();
                                        if ( in_c_obj != null ) {
                                            String code_id = in_c_obj.optString("code_id");
                                            String code_val = in_c_obj.optString("code_val");
                                            cData.setCategoryCode(code_id);
                                            cData.setCategoryName(code_val);
                                            if ( i == 0 )
                                                cData.setSelected(true);
                                            else
                                                cData.setSelected(false);
                                            mCategoryArray.add(cData);
                                        }
                                    }
                                    categoryAdapter = new OCBThemeCategoryAdapter(KeyboardOCBCategoryThemeActivity.this, new OCBThemeCategoryAdapter.OnClickListener() {
                                        @Override
                                        public void onCategoryClicked(CategoryData data) {
                                            selectedCategory = data.getCategoryCode();
                                            connectThemeList(selectedCategory);
                                        }
                                    });
                                    categoryAdapter.setItems(mCategoryArray);
                                    categoryRecyclerView.setAdapter(categoryAdapter);
                                }
                            }
                            if ( mCategoryArray != null && mCategoryArray.size() > 0 ) {
                                JSONArray array = object.optJSONArray("data");
                                if (array != null && array.length() > 0) {
                                    mThemeListArray = new ArrayList<>();
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject in_obj = array.getJSONObject(i);
                                        NewThemeListInfo info = new NewThemeListInfo();
                                        if (in_obj != null) {
                                            String commonDownloadUrl = in_obj.optString("common_down_path");
                                            String customDownloadUrl = in_obj.optString("custom_down_path");
                                            String commonZipFileName = "";
                                            String customZipFileName = "";
                                            String commonUnZipFileName = "";
                                            String customUnZipFileName = "";
                                            if (!TextUtils.isEmpty(commonDownloadUrl) && commonDownloadUrl.contains("/")) {
                                                commonZipFileName = commonDownloadUrl.substring(commonDownloadUrl.lastIndexOf("/") + 1);
                                                String tName = commonZipFileName.replaceAll(".zip", "");
                                                commonUnZipFileName = "un_" + tName;

                                            }

                                            if (!TextUtils.isEmpty(customDownloadUrl) && customDownloadUrl.contains("/")) {
                                                customZipFileName = customDownloadUrl.substring(customDownloadUrl.lastIndexOf("/") + 1);
                                                String tName = customZipFileName.replaceAll(".zip", "");
                                                customUnZipFileName = "un_" + tName;
                                            }
                                            info.setDownloadCount(in_obj.optString("down_cnt"));
                                            info.setCommonZipFileName(commonZipFileName);
                                            info.setCommonUnZipFileName(commonUnZipFileName);
                                            info.setCustomZipFileName(customZipFileName);
                                            info.setCustomUnZipFileName(customUnZipFileName);
                                            info.setCommonDownloadUrl(commonDownloadUrl);
                                            info.setCustomDownloadUrl(customDownloadUrl);
                                            info.setImage(in_obj.optString("image"));
                                            info.setName(in_obj.optString("name"));
                                            info.setUnZipFileName(in_obj.optString("unzip_file_name"));
                                            info.setCategory(in_obj.optString("cate"));
                                            KeyboardLogPrint.e("sub cate :: " + in_obj.optString("cate"));
                                            mThemeListArray.add(info);
                                        }
                                    }
                                    if ( mThemeListArray.size() > 0 ) {
                                        if ( mThemeListArray.size()%2 != 0 ) {
                                            NewThemeListInfo info = new NewThemeListInfo();
                                            info.setName("");
                                            mThemeListArray.add(info);
                                        }
                                    }


                                    themeAdapter.setItems(mThemeListArray);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        JSONObject object = (JSONObject) obj;
                        if (object != null) {
                            String error = object.optString(Common.NETWORK_ERROR);
                            String dError = object.optString(Common.NETWORK_DISCONNECT);
                            if ( !TextUtils.isEmpty(error) ) {
                                Toast.makeText(mContext, error, Toast.LENGTH_SHORT).show();
                            } else {
                                if ( !TextUtils.isEmpty(dError) ) {
                                    Toast.makeText(mContext, dError, Toast.LENGTH_SHORT).show();
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
}
