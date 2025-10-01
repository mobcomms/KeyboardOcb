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

/**
 *         Drawable myIcon = getResources().getDrawable(R.drawable.btn_keyboard_bookmark_black);
 myIcon.setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.enliple.keyboard.R;
import com.enliple.keyboard.common.AIKBD_DBHelper;
import com.enliple.keyboard.common.Common;
import com.enliple.keyboard.common.KeyboardLogPrint;
import com.enliple.keyboard.common.ThemeManager;
import com.enliple.keyboard.common.ThemeModel;
import com.enliple.keyboard.common.Util;
import com.enliple.keyboard.ui.common.LogPrint;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LatinKeyboardView extends KeyboardView {
    private static final int BITMAP_ADJUST = 40;
    private static final int FIT_QWERTY_SHIFT_DEL_WIDTH = 145;
    private static final int FIT_QWERTY_SYMBOL_ENTER_WIDTH = 152;
    private static final int FIT_QWERTY_LANG_WIDTH = 99;
    private static final int FIT_QWERTY_SPACE_WIDTH = 417;

    private static final int FIT_CHUNJIIN_DEL_ENTER_SPACE_WIDTH = 145;
    private static final int FIT_CHUNJIIN_LANG_SYMBOL_WIDTH = 99;

    static final int KEYCODE_OPTIONS = -100;
    private static final int KEYCODE_SHIFT = -1;
    private static final int KEYCODE_DELETE = -5;
    private static final int KEYCODE_SYMBOL = -2;
    private static final int KEYCODE_LANG = -6;
    private static final int KEYCODE_SPACE = 32;
    private static final int KEYCODE_ENTER = 10;
    private static final int KEYCODE_EMOJI = -226;
    private static final int KEYCODE_EMOJI_RECENT = -992;
    private static final int KEYCODE_EMOTICON_RECENT = -1001;
    private static final int KEYCODE_EMOTICON_FIRST = -1002;
    private static final int KEYCODE_EMOTICON_SECOND = -1003;
    private static final int KEYCODE_EMOTICON_THIRD = -1004;
    private static final int KEYCODE_EMOTICON_FOURTH = -1005;
    private static final int KEYCODE_EMOTICON_FIFTH = -1006;
    private static final int KEYCODE_EMOTICON_SIXTH = -1007;

    private static final int KEY_SIZE_1 = 0;
    private static final int KEY_SIZE_2 = 1;
    private static final int KEY_SIZE_3 = 2;
    private static final int KEY_SIZE_4 = 3;

    private Context mContext = null;
    private int mKeyboardGubun = -1;
    private int mExtKeyboard = 0;
    private boolean mIsKorean = false;
    private boolean mIsQwertyNumSet = true;
    private boolean isLongPressPreview = false;

    private Paint mOptTextPaint = new Paint();
    // private Paint mBGPaint = new Paint();
    private Paint mLabelPaint = new Paint();
    private Paint mNumLabelPaint = new Paint();

    private List<Key> mKeys = new ArrayList<>();
    private int mOptTextPadding;
    private int mOptTextHalf;
    private int mLabelTextHalf;
    private int mLabelNumTextHalf;
    private int mBtnpadding_W;
    private int mBtnpadding_H;

    private int mBtnAlpha = 0;
    private int mDarkBtnAlpha = 0;
    private boolean mIsTemp = false; // 포토테마 이미지 crop 화면 등에서 기존 저장되어 있는 테마의 특수키 아이콘이 나오는  것을 막기 위한 변수

    private Drawable mBtnBackground; // 일반키 배경
    private Drawable mDarkBtnBackground; // 특수키 배경

    private int mNormalBtnColor; //
    private int mSpecialBtnColor;
    private int mTxtColor;
    private int mOptTxtColor;
    private int mSpTxtColor;
    private ThemeModel mThemeModel;
    private Drawable mShiftDr;
    private Drawable mShiftDr1;
    private Drawable mShiftDr2;
    private Drawable mBackDr;
    private Drawable mSymbolDr;
    private Drawable mLangDr;
    private Drawable mSpaceDr;
    private Drawable mEnterDr;
    private Drawable mSEnterDr;
    private Drawable mEmojiDr;
    private Drawable mEmoticonDr;
    private Drawable mEmojiRecentDr;
    private Drawable mEmoticonRecentDr;
    private Drawable mEmoticonFirstDr;
    private Drawable mEmoticonSecondDr;
    private Drawable mEmoticonThirdDr;
    private Drawable mEmoticonFourthDr;
    private Drawable mEmoticonFifthDr;
    private Drawable mEmoticonSixthDr;

    private TextView mPreviewText = null;

    private boolean isReceiverRegistered = false;

    int val = 1000;
    public LatinKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        AIKBD_DBHelper helper = new AIKBD_DBHelper(mContext);
        if ( helper.getTheme() != null && !TextUtils.isEmpty(helper.getTheme()))
            mThemeModel = ThemeManager.GetThemeModel(helper.getTheme(),6);
        if ( mThemeModel == null )
            return;

        mShiftDr = ThemeManager.GetDrawableFromPath(mThemeModel.getShiftImg());
        if ( !TextUtils.isEmpty(mThemeModel.getShiftImg1())) {
            mShiftDr1 = ThemeManager.GetDrawableFromPath(mThemeModel.getShiftImg1());
        } else {
            mShiftDr1 = mShiftDr;
        }
        if ( !TextUtils.isEmpty(mThemeModel.getShiftImg2())) {
            mShiftDr2 = ThemeManager.GetDrawableFromPath(mThemeModel.getShiftImg2());
        } else {
            mShiftDr2 = mShiftDr;
        }
        mBackDr = ThemeManager.GetDrawableFromPath(mThemeModel.getBackImg());
        mSymbolDr = ThemeManager.GetDrawableFromPath(mThemeModel.getKeySymbol());
        mLangDr = ThemeManager.GetDrawableFromPath(mThemeModel.getKeyLang());
        mSpaceDr = ThemeManager.GetDrawableFromPath(mThemeModel.getSpaceImg());
        mEnterDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEnterImg());
        mSEnterDr = ThemeManager.GetDrawableFromPath(mThemeModel.getKeySearchEnter());
        mEmojiDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmojiImg());
        mEmojiRecentDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonRecent());
        mEmoticonDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonImg());
        mEmoticonRecentDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonRecent());
        mEmoticonFirstDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonFirst());
        mEmoticonSecondDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonSecond());
        mEmoticonThirdDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonThird());
        mEmoticonFourthDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonFourth());
        mEmoticonFifthDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonFifth());
        mEmoticonSixthDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonSixth());


        Random rand = new Random();
        val = rand.nextInt(100);
        LogPrint.d("regist receiver LatinKeyboardView " + val);
        try {
            //mContext.unregisterReceiver(mSetChange);
            if ( !isReceiverRegistered ) {
                // target 34 대응
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        mContext.registerReceiver(mSetChange, new IntentFilter("THEME_CHANGE"), Context.RECEIVER_EXPORTED);
                    } else {
                        mContext.registerReceiver(mSetChange, new IntentFilter("THEME_CHANGE"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                mContext.registerReceiver(mSetChange, new IntentFilter("THEME_CHANGE"));
                isReceiverRegistered = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        int level = SharedPreference.getInt(mContext, Common.PREF_KEYBOARD_SIZE_LEVEL) < 0 ? Common.DEFAULT_KEYBOARD_SIZE : SharedPreference.getInt(mContext, Common.PREF_KEYBOARD_SIZE_LEVEL);

        changeConfig(level);

        ArrayList<Integer> array = getResourceId(level);
        int bResId = array.get(0);
        int sResId = array.get(1);
        int bNResId = array.get(2);
        Drawable norBtnSelector = ThemeManager.GetNorSelector(mContext, mThemeModel); // 일반키 selector
        Drawable spBtnSelector = ThemeManager.GetSpSelector(mContext, mThemeModel); // 특수키 selector
        mBtnBackground = norBtnSelector;
        mDarkBtnBackground = spBtnSelector;

        mOptTextPadding = getResources().getDimensionPixelOffset(R.dimen.aikbd_key_opt_text_padding);
        mBtnpadding_W = (int) getResources().getDimensionPixelSize(R.dimen.aikbd_key_gap);
        mBtnpadding_H = (int) getResources().getDimensionPixelSize(R.dimen.aikbd_key_gap);
        mOptTextHalf = (int) (getResources().getDimensionPixelSize(sResId) / 2);
        mLabelTextHalf = (int) (getResources().getDimensionPixelSize(bResId) / 2);
        mLabelNumTextHalf = (int) (getResources().getDimensionPixelSize(bNResId) / 2);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LatinKeyboardView);
        isLongPressPreview = a.getBoolean(R.styleable.LatinKeyboardView_enable_longpress_preview, true);

    }

    public LatinKeyboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        int level = SharedPreference.getInt(mContext, Common.PREF_KEYBOARD_SIZE_LEVEL) < 0 ? Common.DEFAULT_KEYBOARD_SIZE : SharedPreference.getInt(mContext, Common.PREF_KEYBOARD_SIZE_LEVEL);

        AIKBD_DBHelper helper = new AIKBD_DBHelper(mContext);
        if ( helper.getTheme() != null && !TextUtils.isEmpty(helper.getTheme()))
            mThemeModel = ThemeManager.GetThemeModel(helper.getTheme(),7);

        if ( mThemeModel == null )
            return;

        mShiftDr = ThemeManager.GetDrawableFromPath(mThemeModel.getShiftImg());
        if ( !TextUtils.isEmpty(mThemeModel.getShiftImg1())) {
            mShiftDr1 = ThemeManager.GetDrawableFromPath(mThemeModel.getShiftImg1());
        } else {
            mShiftDr1 = mShiftDr;
        }
        if ( !TextUtils.isEmpty(mThemeModel.getShiftImg2())) {
            mShiftDr2 = ThemeManager.GetDrawableFromPath(mThemeModel.getShiftImg2());
        } else {
            mShiftDr2 = mShiftDr;
        }
        mBackDr = ThemeManager.GetDrawableFromPath(mThemeModel.getBackImg());
        mSymbolDr = ThemeManager.GetDrawableFromPath(mThemeModel.getKeySymbol());
        mLangDr = ThemeManager.GetDrawableFromPath(mThemeModel.getKeyLang());
        mSpaceDr = ThemeManager.GetDrawableFromPath(mThemeModel.getSpaceImg());
        mEnterDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEnterImg());
        mSEnterDr = ThemeManager.GetDrawableFromPath(mThemeModel.getKeySearchEnter());
        mEmojiDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmojiImg());
        mEmojiRecentDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonRecent());
        mEmoticonDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonImg());
        mEmoticonRecentDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonRecent());
        mEmoticonFirstDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonFirst());
        mEmoticonSecondDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonSecond());
        mEmoticonThirdDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonThird());
        mEmoticonFourthDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonFourth());
        mEmoticonFifthDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonFifth());
        mEmoticonSixthDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonSixth());
        LogPrint.d("regist receiver LatinKeyboardView 2");
        if ( !isReceiverRegistered ) {
            // target 34 대응
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mContext.registerReceiver(mSetChange, new IntentFilter("THEME_CHANGE"), Context.RECEIVER_EXPORTED);
            } else {
                mContext.registerReceiver(mSetChange, new IntentFilter("THEME_CHANGE"));
            }
//            mContext.registerReceiver(mSetChange, new IntentFilter("THEME_CHANGE"));
            isReceiverRegistered = true;
        }
        changeConfig(level);

        ArrayList<Integer> array = getResourceId(level);
        int bResId = array.get(0);
        int sResId = array.get(1);
        int bNResId = array.get(2);
        Drawable norBtnSelector = ThemeManager.GetNorSelector(mContext, mThemeModel); // 일반키 selector
        Drawable spBtnSelector = ThemeManager.GetSpSelector(mContext, mThemeModel); // 특수키 selector
        mBtnBackground = norBtnSelector;
        mDarkBtnBackground = spBtnSelector;
        mOptTextPadding = getResources().getDimensionPixelOffset(R.dimen.aikbd_key_opt_text_padding);
        mBtnpadding_W = (int) getResources().getDimensionPixelSize(R.dimen.aikbd_key_gap);
        mBtnpadding_H = (int) getResources().getDimensionPixelSize(R.dimen.aikbd_key_gap);
        mOptTextHalf = (int) (getResources().getDimensionPixelSize(sResId) / 2);
        mLabelTextHalf = (int) (getResources().getDimensionPixelSize(bResId) / 2);
        mLabelNumTextHalf = (int) (getResources().getDimensionPixelSize(bNResId) / 2);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LatinKeyboardView);
        isLongPressPreview = a.getBoolean(R.styleable.LatinKeyboardView_enable_longpress_preview, true);
    }

    @Override
    public void onDetachedFromWindow() {
        LogPrint.d("un regist receiver LatinKeyboardView ::" + val);
        //mContext.unregisterReceiver(mSetChange);
        super.onDetachedFromWindow();
        if ( isReceiverRegistered ) {
            mContext.unregisterReceiver(mSetChange);
            isReceiverRegistered = false;
        }
    }

    @Override
    public boolean handleBack() {
        return super.handleBack();
    }

    public void setTBackground(Drawable nor, Drawable sp) {
        mBtnBackground = nor;
        mDarkBtnBackground = sp;
    }

    public void setBackground(int selector,int d_selector) {
        mBtnBackground = ResourcesCompat.getDrawable(getResources(), selector, null);
        mDarkBtnBackground = ResourcesCompat.getDrawable(getResources(), d_selector, null);
    }

    public void setTemp(boolean temp) {
        mIsTemp = temp;
    }

    public boolean getTemp() {
        return mIsTemp;
    }

    public void changeConfig(int level, boolean isQwerty) {
        ArrayList<Integer> array = getResourceId(level);
        int bResId = array.get(0);
        int sResId = array.get(1);
        int bNResId = array.get(2);

        ThemeModel models = ThemeManager.GetThemeModel(mContext,8);
        if ( models == null )
            return;
        else
            mThemeModel = ThemeManager.GetThemeModel(mContext,9);
        LogPrint.d("test mode 111");
        mKeyboardGubun = SharedPreference.getInt(mContext, Common.PREF_KEYBOARD_MODE) < 0 ? 0 : SharedPreference.getInt(mContext, Common.PREF_KEYBOARD_MODE);
        if ( SoftKeyboard.IsBackupKeyboardExist() )
            mKeyboardGubun = 2;
        KeyboardLogPrint.e("LatinKeyboardView kind :: " + mKeyboardGubun);
        mIsKorean = SharedPreference.getBoolean(mContext, Common.PREF_IS_KOREAN_KEYBOARD);
        mIsQwertyNumSet = isQwerty;
        mExtKeyboard = SharedPreference.getInt(mContext, Common.PREF_EXT_KEYBOARD);
        try {
            mTxtColor = Color.parseColor(mThemeModel.getKeyText());
            mSpTxtColor = Color.parseColor(mThemeModel.getKeyText());
            String optTxt = mThemeModel.getKeyTextS();
            if ( TextUtils.isEmpty(optTxt))
                mOptTxtColor = Color.parseColor("#919191");
            else
                mOptTxtColor = Color.parseColor(mThemeModel.getKeyTextS());
        } catch (Exception e) {
            e.printStackTrace();
            mTxtColor = Color.parseColor("#ffffff"); // 키 텍스트 색상
            mSpTxtColor = Color.parseColor("#ffffff"); // 키 텍스트 색상
            mOptTxtColor = Color.parseColor("#ffffff");
        }

        mShiftDr = ThemeManager.GetDrawableFromPath(mThemeModel.getShiftImg());
        if ( !TextUtils.isEmpty(mThemeModel.getShiftImg1())) {
            mShiftDr1 = ThemeManager.GetDrawableFromPath(mThemeModel.getShiftImg1());
        } else {
            mShiftDr1 = mShiftDr;
        }
        if ( !TextUtils.isEmpty(mThemeModel.getShiftImg2())) {
            mShiftDr2 = ThemeManager.GetDrawableFromPath(mThemeModel.getShiftImg2());
        } else {
            mShiftDr2 = mShiftDr;
        }
        mBackDr = ThemeManager.GetDrawableFromPath(mThemeModel.getBackImg());
        mSymbolDr = ThemeManager.GetDrawableFromPath(mThemeModel.getKeySymbol());
        mLangDr = ThemeManager.GetDrawableFromPath(mThemeModel.getKeyLang());
        mSpaceDr = ThemeManager.GetDrawableFromPath(mThemeModel.getSpaceImg());
        mEnterDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEnterImg());
        mSEnterDr = ThemeManager.GetDrawableFromPath(mThemeModel.getKeySearchEnter());
        mEmojiDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmojiImg());
        mEmojiRecentDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonRecent());
        mEmoticonDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonImg());
        mEmoticonRecentDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonRecent());
        mEmoticonFirstDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonFirst());
        mEmoticonSecondDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonSecond());
        mEmoticonThirdDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonThird());
        mEmoticonFourthDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonFourth());
        mEmoticonFifthDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonFifth());
        mEmoticonSixthDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonSixth());

        Drawable norBtnSelector = ThemeManager.GetNorSelector(mContext, mThemeModel); // 일반키 selector
        Drawable spBtnSelector = ThemeManager.GetSpSelector(mContext, mThemeModel); // 특수키 selector
        mBtnBackground = norBtnSelector;
        mDarkBtnBackground = spBtnSelector;

        if ( mSpTxtColor < 0 )
            mSpTxtColor = R.color.aikbd_white;

        mOptTextPaint.setTextAlign(Paint.Align.CENTER);
        mOptTextPaint.setAntiAlias(true);
        mOptTextPaint.setTextSize(getResources().getDimensionPixelSize(sResId));

        mOptTextPaint.setColor(mOptTxtColor);

        mLabelPaint.setTextAlign(Paint.Align.CENTER);
        mLabelPaint.setAntiAlias(true);
        mLabelPaint.setTextSize(getResources().getDimensionPixelSize(bResId));

        mLabelPaint.setColor(mTxtColor);

        mNumLabelPaint.setTextAlign(Paint.Align.CENTER);
        mNumLabelPaint.setAntiAlias(true);
        mNumLabelPaint.setTextSize(getResources().getDimensionPixelSize(bNResId));

        mNumLabelPaint.setColor(mTxtColor);

        int alpha = mThemeModel.getNorAlpha();
        if (alpha < 0) alpha = 150;
        setBtnAlpha(alpha);

        int d_alpha = mThemeModel.getSpAlpha();
        if (d_alpha < 0) d_alpha = 150;
        setDarkBtnAlpha(d_alpha);

        int modelNorBtnColor = mThemeModel.getNorBtnColor();
        int modelSpBtnColor = mThemeModel.getSpBtnColor();
        mNormalBtnColor = mThemeModel.getNorBtnColor();
        mSpecialBtnColor = mThemeModel.getSpBtnColor();

        setPreviewBackColor();
    }

    public void changeConfig(int level) {
        ArrayList<Integer> array = getResourceId(level);
        int bResId = array.get(0);
        int sResId = array.get(1);
        int bNResId = array.get(2);

        ThemeModel models = ThemeManager.GetThemeModel(mContext,8);
        if ( models == null )
            return;
        else
            mThemeModel = ThemeManager.GetThemeModel(mContext,9);
        LogPrint.d("test mode 222");
        mKeyboardGubun = SharedPreference.getInt(mContext, Common.PREF_KEYBOARD_MODE) < 0 ? 0 : SharedPreference.getInt(mContext, Common.PREF_KEYBOARD_MODE);
        if ( SoftKeyboard.IsBackupKeyboardExist() )
            mKeyboardGubun = 2;
        KeyboardLogPrint.e("LatinKeyboardView kind :: " + mKeyboardGubun);
        mIsKorean = SharedPreference.getBoolean(mContext, Common.PREF_IS_KOREAN_KEYBOARD);
        mIsQwertyNumSet = SharedPreference.getBoolean(mContext, Common.PREF_QWERTY_NUM_SETTING);
        mExtKeyboard = SharedPreference.getInt(mContext, Common.PREF_EXT_KEYBOARD);
        try {
            mTxtColor = Color.parseColor(mThemeModel.getKeyText());
            mSpTxtColor = Color.parseColor(mThemeModel.getKeyText());
            String optTxt = mThemeModel.getKeyTextS();
            if ( TextUtils.isEmpty(optTxt))
                mOptTxtColor = Color.parseColor("#919191");
            else
                mOptTxtColor = Color.parseColor(mThemeModel.getKeyTextS());
        } catch (Exception e) {
            e.printStackTrace();
            mTxtColor = Color.parseColor("#ffffff"); // 키 텍스트 색상
            mSpTxtColor = Color.parseColor("#ffffff"); // 키 텍스트 색상
            mOptTxtColor = Color.parseColor("#ffffff");
        }

        mShiftDr = ThemeManager.GetDrawableFromPath(mThemeModel.getShiftImg());
        if ( !TextUtils.isEmpty(mThemeModel.getShiftImg1())) {
            mShiftDr1 = ThemeManager.GetDrawableFromPath(mThemeModel.getShiftImg1());
        } else {
            mShiftDr1 = mShiftDr;
        }
        if ( !TextUtils.isEmpty(mThemeModel.getShiftImg2())) {
            mShiftDr2 = ThemeManager.GetDrawableFromPath(mThemeModel.getShiftImg2());
        } else {
            mShiftDr2 = mShiftDr;
        }
        mBackDr = ThemeManager.GetDrawableFromPath(mThemeModel.getBackImg());
        mSymbolDr = ThemeManager.GetDrawableFromPath(mThemeModel.getKeySymbol());
        mLangDr = ThemeManager.GetDrawableFromPath(mThemeModel.getKeyLang());
        mSpaceDr = ThemeManager.GetDrawableFromPath(mThemeModel.getSpaceImg());
        mEnterDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEnterImg());
        mSEnterDr = ThemeManager.GetDrawableFromPath(mThemeModel.getKeySearchEnter());
        mEmojiDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmojiImg());
        mEmojiRecentDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonRecent());
        mEmoticonDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonImg());
        mEmoticonRecentDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonRecent());
        mEmoticonFirstDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonFirst());
        mEmoticonSecondDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonSecond());
        mEmoticonThirdDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonThird());
        mEmoticonFourthDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonFourth());
        mEmoticonFifthDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonFifth());
        mEmoticonSixthDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonSixth());

        Drawable norBtnSelector = ThemeManager.GetNorSelector(mContext, mThemeModel); // 일반키 selector
        Drawable spBtnSelector = ThemeManager.GetSpSelector(mContext, mThemeModel); // 특수키 selector
        mBtnBackground = norBtnSelector;
        mDarkBtnBackground = spBtnSelector;

        if ( mSpTxtColor < 0 )
            mSpTxtColor = R.color.aikbd_white;

        mOptTextPaint.setTextAlign(Paint.Align.CENTER);
        mOptTextPaint.setAntiAlias(true);
        mOptTextPaint.setTextSize(getResources().getDimensionPixelSize(sResId));

        mOptTextPaint.setColor(mOptTxtColor);

        mLabelPaint.setTextAlign(Paint.Align.CENTER);
        mLabelPaint.setAntiAlias(true);
        mLabelPaint.setTextSize(getResources().getDimensionPixelSize(bResId));

        mLabelPaint.setColor(mTxtColor);

        mNumLabelPaint.setTextAlign(Paint.Align.CENTER);
        mNumLabelPaint.setTextSize(getResources().getDimensionPixelSize(bNResId));

        mNumLabelPaint.setColor(mTxtColor);

        int alpha = mThemeModel.getNorAlpha();
        if (alpha < 0) alpha = 150;
        setBtnAlpha(alpha);

        int d_alpha = mThemeModel.getSpAlpha();
        if (d_alpha < 0) d_alpha = 150;
        setDarkBtnAlpha(d_alpha);

        int modelNorBtnColor = mThemeModel.getNorBtnColor();
        int modelSpBtnColor = mThemeModel.getSpBtnColor();
        mNormalBtnColor = mThemeModel.getNorBtnColor();
        mSpecialBtnColor = mThemeModel.getSpBtnColor();

        setPreviewBackColor();
    }

    public void setKeyTextColor(int txtColor, int spTxtColor, int optTxtColor) {
        mTxtColor = txtColor;
        mSpTxtColor = spTxtColor;
        mOptTxtColor = optTxtColor;
        invalidateAllKeys();
    }

    public void setAllBtnAlpha(int _alpha) {
        mBtnAlpha = _alpha;
        mDarkBtnAlpha = _alpha;
        invalidateAllKeys();
    }

    public void setBtnAlpha(int _alpha) {
        mBtnAlpha = _alpha;
        invalidateAllKeys();
    }

    public void setDarkBtnAlpha(int _alpha) {
        mDarkBtnAlpha = _alpha;
        invalidateAllKeys();
    }

    /**
     * mTxtColor 값으로 text 색상 변경
     */
    public void setLabelPaint() {
        int level = SharedPreference.getInt(mContext, Common.PREF_KEYBOARD_SIZE_LEVEL) < 0 ? Common.DEFAULT_KEYBOARD_SIZE : SharedPreference.getInt(mContext, Common.PREF_KEYBOARD_SIZE_LEVEL);
        ArrayList<Integer> array = getResourceId(level);
        int bResId = array.get(0);
        int sResId = array.get(1);
        int bNResId = array.get(2);

        mOptTextPaint = new Paint();
        mOptTextPaint.setTextAlign(Paint.Align.CENTER);
        mOptTextPaint.setAntiAlias(true);
        mOptTextPaint.setTextSize(getResources().getDimensionPixelSize(sResId));

        mOptTextPaint.setColor(mOptTxtColor);

        mLabelPaint = new Paint();
        mLabelPaint.setTextAlign(Paint.Align.CENTER);
        mLabelPaint.setAntiAlias(true);
        mLabelPaint.setTextSize(getResources().getDimensionPixelSize(bResId));

        mLabelPaint.setColor(mTxtColor);

        mNumLabelPaint.setTextAlign(Paint.Align.CENTER);
        mNumLabelPaint.setAntiAlias(true);
        mNumLabelPaint.setTextSize(getResources().getDimensionPixelSize(bNResId));

        mNumLabelPaint.setColor(mTxtColor);

    }

    /**
     * 포토테마 일반키 색상 변경
     * @param _color
     */
    public void setKeyColor(int _color) {
        mNormalBtnColor = _color;
        mBtnBackground.getCurrent().setColorFilter(_color, PorterDuff.Mode.SRC_ATOP);
    }

    /**
     * 포토테마 특수키 색상 변경
     * @param _color
     */
    public void setSpecialKeyColor(int _color) {
        mSpecialBtnColor = _color;
        mDarkBtnBackground.getCurrent().setColorFilter(_color, PorterDuff.Mode.SRC_ATOP);
    }

    public void invalidateAllKey()
    {
        invalidateAllKeys();
    }


    /**
     * keyboard button의 alpha 값 setting
     */
    public void setKeys() {
        Keyboard keyboard = getKeyboard();
        if ( keyboard != null ) {
            if (mKeys == null)
                mKeys = new ArrayList<>();
            else
                mKeys.clear();

            mKeys.addAll(keyboard.getKeys());

            int alpha = 150;
            int d_alpha = 150;

            if ( mThemeModel != null ) {
                alpha = mThemeModel.getNorAlpha();
                d_alpha = mThemeModel.getSpAlpha();
            }
            if (alpha < 0) alpha = 150;
            mBtnAlpha = alpha;

            if (d_alpha < 0) d_alpha = 150;
            mDarkBtnAlpha = d_alpha;
        }
    }

    public void setKeyboardMode(int _mode) {
        mKeyboardGubun = _mode;
    }

    public void setThemeModel(ThemeModel model) {
        KeyboardLogPrint.e("setThemeModel");
        mThemeModel = model;
        mDarkBtnAlpha = mThemeModel.getSpAlpha();
        mBtnAlpha = mThemeModel.getNorAlpha();
        mSpTxtColor = Color.parseColor(mThemeModel.getKeyText());
        mTxtColor = Color.parseColor(mThemeModel.getKeyText());
        String optTxtColor = mThemeModel.getKeyTextS();
        if ( TextUtils.isEmpty(optTxtColor) )
            mOptTxtColor = Color.parseColor("#919191");
        else
            mOptTxtColor = Color.parseColor(mThemeModel.getKeyTextS());
        mNormalBtnColor = mThemeModel.getNorBtnColor();
        mSpecialBtnColor= mThemeModel.getSpBtnColor();

        mShiftDr = ThemeManager.GetDrawableFromPath(mThemeModel.getShiftImg());
        if ( !TextUtils.isEmpty(mThemeModel.getShiftImg1())) {
            mShiftDr1 = ThemeManager.GetDrawableFromPath(mThemeModel.getShiftImg1());
        } else {
            mShiftDr1 = mShiftDr;
        }
        if ( !TextUtils.isEmpty(mThemeModel.getShiftImg2())) {
            mShiftDr2 = ThemeManager.GetDrawableFromPath(mThemeModel.getShiftImg2());
        } else {
            mShiftDr2 = mShiftDr;
        }
        mBackDr = ThemeManager.GetDrawableFromPath(mThemeModel.getBackImg());
        mSymbolDr = ThemeManager.GetDrawableFromPath(mThemeModel.getKeySymbol());
        mLangDr = ThemeManager.GetDrawableFromPath(mThemeModel.getKeyLang());
        mSpaceDr = ThemeManager.GetDrawableFromPath(mThemeModel.getSpaceImg());
        mEnterDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEnterImg());
        mSEnterDr = ThemeManager.GetDrawableFromPath(mThemeModel.getKeySearchEnter());
        mEmojiDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmojiImg());
        mEmojiRecentDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonRecent());
        mEmoticonDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonImg());
        mEmoticonRecentDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonRecent());
        mEmoticonFirstDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonFirst());
        mEmoticonSecondDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonSecond());
        mEmoticonThirdDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonThird());
        mEmoticonFourthDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonFourth());
        mEmoticonFifthDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonFifth());
        mEmoticonSixthDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonSixth());
    }

    public ThemeModel getThemeModel() {
        return mThemeModel;
    }

    @Override
    public void onDraw(Canvas canvas) {
        KeyboardLogPrint.e("LatinKeyboardView onDraw");
        if (mKeys.size() < 1) {
            if (getKeyboard() == null) return;
            mKeys.addAll(getKeyboard().getKeys());
        }
        if (mKeyboardGubun == Common.MODE_QUERTY) {
            for (Key key : mKeys) {
                setBackDrawable(key, canvas);
                if (!mIsKorean) {
                    KeyboardLogPrint.e(" qwerty not korean");
                    if (key.label != null) {
                        String changed;
                        if (isShifted()) {
                            changed = key.label.toString().toUpperCase();
                        } else {
                            changed = key.label.toString().toLowerCase();
                        }
                        KeyboardLogPrint.e("mNumLabelPaint.descent() :: " + mNumLabelPaint.descent());
                        KeyboardLogPrint.e("mLabelPaint.descent() :: " + mLabelPaint.descent());
//                        if ( key.codes[0] >= 48 && key.codes[0] <= 57)
//                            canvas.drawText(changed, key.x + (key.width / 2), key.y + (key.height / 2) + mLabelNumTextHalf - (mNumLabelPaint.descent() / 2) + 1, mNumLabelPaint);
//                        else
//                            canvas.drawText(changed, key.x + (key.width / 2), key.y + (key.height / 2) + mLabelTextHalf - mLabelPaint.descent() + 2, mLabelPaint);

                        if ( key.codes[0] >= 48 && key.codes[0] <= 57) {
                            Rect textBounds = new Rect();
                            mNumLabelPaint.getTextBounds(changed, 0, changed.length(), textBounds);
                            canvas.drawText(changed, key.x + (key.width / 2 ), key.y + (key.height / 2 ) - textBounds.exactCenterY(), mNumLabelPaint);
                        } else {
                            if( key.codes[0] == 95 ) {
                                LogPrint.d("label _ ");
                                canvas.drawText(changed, key.x + (key.width / 2), key.y + (key.height / 2) + mLabelTextHalf - mLabelPaint.descent() + 2, mLabelPaint);
                            } else {
                                LogPrint.d("label not _ ");
                                Rect textBounds = new Rect();
                                mLabelPaint.getTextBounds(changed, 0, changed.length(), textBounds);
                                canvas.drawText(changed, key.x + (key.width / 2 ), key.y + (key.height / 2 ) - textBounds.exactCenterY(), mLabelPaint);
                            }
                        }
                    } else {
                        Drawable myIcon = getSpKeyIcon(key.codes[0], mIsTemp);
                        if ( myIcon == null )
                            myIcon = key.icon;
                        if ( myIcon != null ) {
                            if ( mSpTxtColor < 0 )
                                mSpTxtColor = R.color.aikbd_white;
                            int colorCode = ContextCompat.getColor(mContext, mSpTxtColor);
                            Bitmap sourceBitmap = Util.convertDrawableToBitmap(myIcon);
                            Bitmap bitmap = Util.changeImageColor(sourceBitmap, colorCode);
                            if ( bitmap != null ) {
                                int bitmapHeight = Common.convertDpToPx(mContext, 42.6f);
                                int bitmapWidth = bitmapHeight * bitmap.getWidth() / bitmap.getHeight();

                                bitmap = Bitmap.createScaledBitmap(bitmap, bitmapWidth, bitmapHeight, true);
                                if ( bitmap != null ) {
                                    canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                                }
                            }

                            /*
                            if ( bitmap != null ) {
                                if ( bitmap.getHeight() <= key.height ) {
                                    canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                                } else {
                                    try {
                                        int resized_bitmap_height = key.height;
                                        int resized_bitmap_width = key.height * bitmap.getWidth() / bitmap.getHeight();
                                        Bitmap resized_bitmap = Bitmap.createScaledBitmap(bitmap, resized_bitmap_width, resized_bitmap_height, true);

                                        LogPrint.d(" *************************** ************************* ");
                                        canvas.drawBitmap(resized_bitmap, key.x + (key.width / 2) - (resized_bitmap_width / 2), key.y + (key.height / 2) - (resized_bitmap_height / 2), mLabelPaint);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                                    }
                                }
                                //canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);

                            }*/
                        }
                    }
                } else {
                    KeyboardLogPrint.e(" qwerty korean");
                    if (key.label != null) {
                        String changed = setKQwertySift(isShifted(), key);
//                        if ( key.codes[0] >= 48 && key.codes[0] <= 57)
//                            canvas.drawText(changed, key.x + (key.width / 2), key.y + (key.height / 2) + mLabelNumTextHalf - (mNumLabelPaint.descent() / 2) + 1, mNumLabelPaint);
//                        else
//                            canvas.drawText(changed, key.x + (key.width / 2), key.y + (key.height / 2) + mLabelTextHalf - (mLabelPaint.descent() / 2) + 2, mLabelPaint);
                        if ( key.codes[0] >= 48 && key.codes[0] <= 57) {
                            Rect textBounds = new Rect();
                            mNumLabelPaint.getTextBounds(changed, 0, changed.length(), textBounds);
                            canvas.drawText(changed, key.x + (key.width / 2 ), key.y + (key.height / 2 ) - textBounds.exactCenterY(), mNumLabelPaint);
                        } else {
                            Rect textBounds = new Rect();
                            mLabelPaint.getTextBounds(changed, 0, changed.length(), textBounds);
                            canvas.drawText(changed, key.x + (key.width / 2 ), key.y + (key.height / 2 ) - textBounds.exactCenterY(), mLabelPaint);
                        }
                    }
//                    canvas.drawText(key.label.toString(), key.x + (key.width / 2), key.y + (key.height / 2) + mLabelTextHalf, mLabelPaint);
                    else {
                        Drawable myIcon = getSpKeyIcon(key.codes[0], mIsTemp);
                        if ( myIcon == null )
                            myIcon = key.icon;
                        if ( myIcon != null ){
                            if ( mSpTxtColor < 0 )
                                mSpTxtColor = R.color.aikbd_white;
                            int colorCode = ContextCompat.getColor(mContext, mSpTxtColor);
                            Bitmap sourceBitmap = Util.convertDrawableToBitmap(myIcon);
                            Bitmap bitmap = Util.changeImageColor(sourceBitmap, colorCode);
                            if ( bitmap != null ) {
                                int bitmapHeight = Common.convertDpToPx(mContext, 42.2f);
                                int bitmapWidth = bitmapHeight * bitmap.getWidth() / bitmap.getHeight();

                                KeyboardLogPrint.d("key.height :: " + key.height + " , bitmap.height :: " + bitmap.getHeight() + " , bitmapHeight :: " + bitmapHeight);
                                bitmap = Bitmap.createScaledBitmap(bitmap, bitmapWidth, bitmapHeight, true);
                                if ( bitmap != null ) {
                                    canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                                }
                            }

                            /*
                            if ( bitmap != null ) {
                                if ( bitmap.getHeight() <= key.height ) {
                                    canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                                } else {
                                    try {
                                        int resized_bitmap_height = key.height - BITMAP_ADJUST;
                                        int resized_bitmap_width = key.height * bitmap.getWidth() / bitmap.getHeight();
                                        Bitmap resized_bitmap = Bitmap.createScaledBitmap(bitmap, resized_bitmap_width, resized_bitmap_height, true);

                                        LogPrint.d(" *************************** ************************* ");
                                        canvas.drawBitmap(resized_bitmap, key.x + (key.width / 2) - (resized_bitmap_width / 2), key.y + (key.height / 2) - (resized_bitmap_height / 2), mLabelPaint);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                                    }
                                }
                                //canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                            }*/
                        }
                    }
                }
                setCommonKeys(canvas, key, mOptTextPadding, mOptTextPaint);
            }
        } else if (mKeyboardGubun == Common.MODE_CHUNJIIN) {
            KeyboardLogPrint.d("chunjiin");
            // TODO :: 뭔가 문제로 간헐적으로 천지인 키보드가 올라오는데 mIsKorean 값이 false로 넘어오는 현상
            if (mIsKorean) {
                KeyboardLogPrint.d("korean");
                for (Key key : mKeys) {
                    setBackDrawable(key, canvas);
                    if (key.label != null) {
                        Rect textBounds = new Rect();
                        mLabelPaint.getTextBounds(key.label.toString(), 0, key.label.toString().length(), textBounds);
                        canvas.drawText(key.label.toString(), key.x + (key.width / 2 ), key.y + (key.height / 2 ) - textBounds.exactCenterY(), mLabelPaint);
                    }
                    else {
                        Drawable myIcon = getSpKeyIcon(key.codes[0], mIsTemp);
                        if ( myIcon == null )
                            myIcon = key.icon;
                        if ( myIcon != null ) {
                            if ( mSpTxtColor < 0 )
                                mSpTxtColor = R.color.aikbd_white;
                            int colorCode = ContextCompat.getColor(mContext, mSpTxtColor);
                            Bitmap sourceBitmap = Util.convertDrawableToBitmap(myIcon);
                            Bitmap bitmap = Util.changeImageColor(sourceBitmap, colorCode);
                            if ( bitmap != null ) {
                                int bitmapHeight = Common.convertDpToPx(mContext, 42.2f);
                                int bitmapWidth = bitmapHeight * bitmap.getWidth() / bitmap.getHeight();

                                KeyboardLogPrint.d("key.height :: " + key.height + " , bitmap.height :: " + bitmap.getHeight() + " , bitmapHeight :: " + bitmapHeight);
                                bitmap = Bitmap.createScaledBitmap(bitmap, bitmapWidth, bitmapHeight, true);
                                if ( bitmap != null ) {
                                    canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                                }
                            }


                            /*
                            if ( bitmap != null ) {
                                if ( bitmap.getHeight() <= key.height ) {
                                    canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                                } else {
                                    try {
                                        int resized_bitmap_height = key.height;
                                        int resized_bitmap_width = key.height * bitmap.getWidth() / bitmap.getHeight();
                                        Bitmap resized_bitmap = Bitmap.createScaledBitmap(bitmap, resized_bitmap_width, resized_bitmap_height, true);

                                        LogPrint.d(" *************************** ************************* ");
                                        canvas.drawBitmap(resized_bitmap, key.x + (key.width / 2) - (resized_bitmap_width / 2), key.y + (key.height / 2) - (resized_bitmap_height / 2), mLabelPaint);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                                    }
                                }
                                //canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                            }*/
                            /*
                            if (bitmap != null) {
                                canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                            }
                             */
                        }
                    }
                    setDrawChunjiinKeys(canvas, key, mOptTextPadding, mOptTextPaint);
                }
            } else {
                KeyboardLogPrint.d("not korean");
                for (Key key : mKeys) {
                    setBackDrawable(key, canvas);
                    if (key.label != null) {
                        String changed;
                        if (isShifted()) {
                            changed = key.label.toString().toUpperCase();
                        } else {
                            changed = key.label.toString().toLowerCase();
                        }
                        KeyboardLogPrint.d("key.y start :: " + key.y);
                        KeyboardLogPrint.d("key.y end :: " + key.y + key.height);
                        float val = key.y + (key.height / 2) + mLabelTextHalf - (mLabelPaint.descent() / 2) + 2;
                        KeyboardLogPrint.d("label start y :: " + val);

                        //canvas.drawText(changed, key.x + (key.width / 2), key.y + (key.height / 2) + mLabelTextHalf - (mLabelPaint.descent() / 2) + 2, mLabelPaint);
                        if( key.codes[0] == 95 ) {
                            LogPrint.d("label _ ");
                            canvas.drawText(changed, key.x + (key.width / 2), key.y + (key.height / 2) + mLabelTextHalf - mLabelPaint.descent() + 2, mLabelPaint);
                        } else {
                            LogPrint.d("label not _ ");
                            Rect textBounds = new Rect();
                            mLabelPaint.getTextBounds(changed, 0, changed.length(), textBounds);
                            canvas.drawText(changed, key.x + (key.width / 2 ), key.y + (key.height / 2 ) - textBounds.exactCenterY(), mLabelPaint);
                        }

                    } else {
                        Drawable myIcon = getSpKeyIcon(key.codes[0], mIsTemp);
                        if ( myIcon == null )
                            myIcon = key.icon;
                        if ( myIcon != null ){
                            if ( mSpTxtColor < 0 )
                                mSpTxtColor = R.color.aikbd_white;
                            int colorCode = ContextCompat.getColor(mContext, mSpTxtColor);
                            Bitmap sourceBitmap = Util.convertDrawableToBitmap(myIcon);
                            Bitmap bitmap = Util.changeImageColor(sourceBitmap, colorCode);
                            if ( bitmap != null ) {
                                int bitmapHeight = Common.convertDpToPx(mContext, 42.2f);
                                int bitmapWidth = bitmapHeight * bitmap.getWidth() / bitmap.getHeight();

                                KeyboardLogPrint.d("key.height :: " + key.height + " , bitmap.height :: " + bitmap.getHeight() + " , bitmapHeight :: " + bitmapHeight);
                                bitmap = Bitmap.createScaledBitmap(bitmap, bitmapWidth, bitmapHeight, true);
                                if ( bitmap != null ) {
                                    canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                                }
                            }

                            /*
                            if ( bitmap != null ) {
                                if ( bitmap.getHeight() <= key.height ) {
                                    canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                                } else {
                                    try {
                                        int resized_bitmap_height = key.height;
                                        int resized_bitmap_width = key.height * bitmap.getWidth() / bitmap.getHeight();
                                        Bitmap resized_bitmap = Bitmap.createScaledBitmap(bitmap, resized_bitmap_width, resized_bitmap_height, true);

                                        LogPrint.d(" *************************** ************************* ");
                                        canvas.drawBitmap(resized_bitmap, key.x + (key.width / 2) - (resized_bitmap_width / 2), key.y + (key.height / 2) - (resized_bitmap_height / 2), mLabelPaint);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                                    }
                                }
                                //canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                            }*/
                            /*
                            if (bitmap != null)
                                canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                        */
                        }
                    }
                    setCommonKeys(canvas, key, mOptTextPadding, mOptTextPaint);
                }
            }
        } else if (mKeyboardGubun == Common.MODE_DAN) {
            KeyboardLogPrint.d("dan");
            if (mIsKorean) {
                for (Key key : mKeys) {
                    setBackDrawable(key, canvas);
                    if (key.label != null) {
                        //canvas.drawText(key.label.toString(), key.x + (key.width / 2), key.y + (key.height / 2) + mLabelTextHalf, mLabelPaint);
                        Rect textBounds = new Rect();
                        mLabelPaint.getTextBounds(key.label.toString(), 0, key.label.toString().length(), textBounds);
                        canvas.drawText(key.label.toString(), key.x + (key.width / 2 ), key.y + (key.height / 2 ) - textBounds.exactCenterY(), mLabelPaint);
                    }

                    else {
                        Drawable myIcon = getSpKeyIcon(key.codes[0], mIsTemp);
                        if ( myIcon == null )
                            myIcon = key.icon;
                        if ( myIcon != null ){
                            if ( mSpTxtColor < 0 )
                                mSpTxtColor = R.color.aikbd_white;
                            int colorCode = ContextCompat.getColor(mContext, mSpTxtColor);
                            Bitmap sourceBitmap = Util.convertDrawableToBitmap(myIcon);
                            Bitmap bitmap = Util.changeImageColor(sourceBitmap, colorCode);

                            int bitmapHeight = Common.convertDpToPx(mContext, 42.2f);
                            int bitmapWidth = bitmapHeight * bitmap.getWidth() / bitmap.getHeight();

                            KeyboardLogPrint.d("key.height :: " + key.height + " , bitmap.height :: " + bitmap.getHeight() + " , bitmapHeight :: " + bitmapHeight);
                            bitmap = Bitmap.createScaledBitmap(bitmap, bitmapWidth, bitmapHeight, true);
                            if ( bitmap != null ) {
                                canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                            }
                            /*
                            if ( bitmap != null ) {
                                if ( bitmap.getHeight() <= key.height ) {
                                    canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                                } else {
                                    try {
                                        int resized_bitmap_height = key.height;
                                        int resized_bitmap_width = key.height * bitmap.getWidth() / bitmap.getHeight();
                                        Bitmap resized_bitmap = Bitmap.createScaledBitmap(bitmap, resized_bitmap_width, resized_bitmap_height, true);

                                        LogPrint.d(" *************************** ************************* ");
                                        canvas.drawBitmap(resized_bitmap, key.x + (key.width / 2) - (resized_bitmap_width / 2), key.y + (key.height / 2) - (resized_bitmap_height / 2), mLabelPaint);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                                    }
                                }
                                //canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                            }*/
                            /*
                            if (bitmap != null)
                                canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                        */
                        }
                    }
                    setDanKeys(canvas, key, mOptTextPadding, mOptTextPaint);
                }
            } else {
                for (Key key : mKeys) {
                    setBackDrawable(key, canvas);
                    if (key.label != null) {
                        String changed;
                        if (isShifted()) {
                            changed = key.label.toString().toUpperCase();
                        } else {
                            changed = key.label.toString().toLowerCase();
                        }

//                        if ( key.codes[0] >= 48 && key.codes[0] <= 57)
//                            canvas.drawText(changed, key.x + (key.width / 2), key.y + (key.height / 2) + mLabelNumTextHalf - (mNumLabelPaint.descent() / 2) + 1, mNumLabelPaint);
//                        else
//                            canvas.drawText(changed, key.x + (key.width / 2), key.y + (key.height / 2) + mLabelTextHalf - (mLabelPaint.descent() / 2) + 2, mLabelPaint);
                        if ( key.codes[0] >= 48 && key.codes[0] <= 57) {
                            Rect textBounds = new Rect();
                            mNumLabelPaint.getTextBounds(changed, 0, changed.length(), textBounds);
                            canvas.drawText(changed, key.x + (key.width / 2 ), key.y + (key.height / 2 ) - textBounds.exactCenterY(), mNumLabelPaint);
                        } else {
                            if( key.codes[0] == 95 ) {
                                LogPrint.d("label _ ");
                                canvas.drawText(changed, key.x + (key.width / 2), key.y + (key.height / 2) + mLabelTextHalf - mLabelPaint.descent() + 2, mLabelPaint);
                            } else {
                                LogPrint.d("label not _ ");
                                Rect textBounds = new Rect();
                                mLabelPaint.getTextBounds(changed, 0, changed.length(), textBounds);
                                canvas.drawText(changed, key.x + (key.width / 2 ), key.y + (key.height / 2 ) - textBounds.exactCenterY(), mLabelPaint);
                            }
                        }
                    } else {
                        Drawable myIcon = getSpKeyIcon(key.codes[0], mIsTemp);
                        if ( myIcon == null )
                            myIcon = key.icon;
                        if ( myIcon != null ){
                            if ( mSpTxtColor < 0 )
                                mSpTxtColor = R.color.aikbd_white;
                            int colorCode = ContextCompat.getColor(mContext, mSpTxtColor);
                            Bitmap sourceBitmap = Util.convertDrawableToBitmap(myIcon);
                            Bitmap bitmap = Util.changeImageColor(sourceBitmap, colorCode);
                            /*
                            if (bitmap != null)
                                canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                        */

                            int bitmapHeight = Common.convertDpToPx(mContext, 42.2f);
                            int bitmapWidth = bitmapHeight * bitmap.getWidth() / bitmap.getHeight();

                            KeyboardLogPrint.d("key.height :: " + key.height + " , bitmap.height :: " + bitmap.getHeight() + " , bitmapHeight :: " + bitmapHeight);
                            bitmap = Bitmap.createScaledBitmap(bitmap, bitmapWidth, bitmapHeight, true);
                            if ( bitmap != null ) {
                                canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                            }
                            /*
                            if ( bitmap != null ) {
                                if ( bitmap.getHeight() <= key.height ) {
                                    canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                                } else {
                                    try {
                                        int resized_bitmap_height = key.height;
                                        int resized_bitmap_width = key.height * bitmap.getWidth() / bitmap.getHeight();
                                        Bitmap resized_bitmap = Bitmap.createScaledBitmap(bitmap, resized_bitmap_width, resized_bitmap_height, true);

                                        LogPrint.d(" *************************** ************************* ");
                                        canvas.drawBitmap(resized_bitmap, key.x + (key.width / 2) - (resized_bitmap_width / 2), key.y + (key.height / 2) - (resized_bitmap_height / 2), mLabelPaint);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                                    }
                                }
                                //canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                            }*/
                        }
                    }
                    setCommonKeys(canvas, key, mOptTextPadding, mOptTextPaint);
                }
            }
        } else if (mKeyboardGubun == Common.MODE_NARA) {
            KeyboardLogPrint.d("nara");
            if (mIsKorean) {
                for (Key key : mKeys) {
                    setBackDrawable(key, canvas);
                    if (key.label != null) {
                        //canvas.drawText(key.label.toString(), key.x + (key.width / 2), key.y + (key.height / 2) + mLabelTextHalf, mLabelPaint);
                        Rect textBounds = new Rect();
                        mLabelPaint.getTextBounds(key.label.toString(), 0, key.label.toString().length(), textBounds);
                        canvas.drawText(key.label.toString(), key.x + (key.width / 2 ), key.y + (key.height / 2 ) - textBounds.exactCenterY(), mLabelPaint);
                    }
                        //canvas.drawText(key.label.toString(), key.x + (key.width / 2), key.y + (key.height / 2) + mLabelTextHalf, mLabelPaint);
                    else {
                        Drawable myIcon = getSpKeyIcon(key.codes[0], mIsTemp);
                        if ( myIcon == null )
                            myIcon = key.icon;
                        if ( myIcon != null ){
                            if ( mSpTxtColor < 0 )
                                mSpTxtColor = R.color.aikbd_white;
                            int colorCode = ContextCompat.getColor(mContext, mSpTxtColor);
                            Bitmap sourceBitmap = Util.convertDrawableToBitmap(myIcon);
                            Bitmap bitmap = Util.changeImageColor(sourceBitmap, colorCode);

                            int bitmapHeight = Common.convertDpToPx(mContext, 42.2f);
                            int bitmapWidth = bitmapHeight * bitmap.getWidth() / bitmap.getHeight();

                            KeyboardLogPrint.d("key.height :: " + key.height + " , bitmap.height :: " + bitmap.getHeight() + " , bitmapHeight :: " + bitmapHeight);
                            bitmap = Bitmap.createScaledBitmap(bitmap, bitmapWidth, bitmapHeight, true);
                            if ( bitmap != null ) {
                                canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                            }
                            /*
                            if ( bitmap != null ) {
                                if ( bitmap.getHeight() <= key.height ) {
                                    canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                                } else {
                                    try {
                                        int resized_bitmap_height = key.height;
                                        int resized_bitmap_width = key.height * bitmap.getWidth() / bitmap.getHeight();
                                        Bitmap resized_bitmap = Bitmap.createScaledBitmap(bitmap, resized_bitmap_width, resized_bitmap_height, true);

                                        LogPrint.d(" *************************** ************************* ");
                                        canvas.drawBitmap(resized_bitmap, key.x + (key.width / 2) - (resized_bitmap_width / 2), key.y + (key.height / 2) - (resized_bitmap_height / 2), mLabelPaint);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                                    }
                                }
                                //canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                            }*/
                            /*
                            if (bitmap != null)
                                canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                        */
                        }
                    }
                    setNaraKeys(canvas, key, mOptTextPadding, mOptTextPaint);

                }
            } else {
                for (Key key : mKeys) {
                    setBackDrawable(key, canvas);
                    if (key.label != null) {
                        String changed;
                        if (isShifted()) {
                            changed = key.label.toString().toUpperCase();
                        } else {
                            changed = key.label.toString().toLowerCase();
                        }
                        if( key.codes[0] == 95 ) {
                            LogPrint.d("label _ ");
                            canvas.drawText(changed, key.x + (key.width / 2), key.y + (key.height / 2) + mLabelTextHalf - mLabelPaint.descent() + 2, mLabelPaint);
                        } else {
                            LogPrint.d("label not _ ");
                            Rect textBounds = new Rect();
                            mLabelPaint.getTextBounds(changed, 0, changed.length(), textBounds);
                            canvas.drawText(changed, key.x + (key.width / 2 ), key.y + (key.height / 2 ) - textBounds.exactCenterY(), mLabelPaint);
                        }
                        //canvas.drawText(changed, key.x + (key.width / 2), key.y + (key.height / 2) + mLabelTextHalf - (mLabelPaint.descent() / 2) + 2, mLabelPaint);
                    } else {
                        Drawable myIcon = getSpKeyIcon(key.codes[0], mIsTemp);
                        if ( myIcon == null )
                            myIcon = key.icon;
                        if ( myIcon != null ){
                            if ( mSpTxtColor < 0 )
                                mSpTxtColor = R.color.aikbd_white;
                            int colorCode = ContextCompat.getColor(mContext, mSpTxtColor);
                            Bitmap sourceBitmap = Util.convertDrawableToBitmap(myIcon);
                            Bitmap bitmap = Util.changeImageColor(sourceBitmap, colorCode);

                            int bitmapHeight = Common.convertDpToPx(mContext, 42.2f);
                            int bitmapWidth = bitmapHeight * bitmap.getWidth() / bitmap.getHeight();

                            KeyboardLogPrint.d("key.height :: " + key.height + " , bitmap.height :: " + bitmap.getHeight() + " , bitmapHeight :: " + bitmapHeight);
                            bitmap = Bitmap.createScaledBitmap(bitmap, bitmapWidth, bitmapHeight, true);
                            if ( bitmap != null ) {
                                canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                            }
                            /*
                            if ( bitmap != null ) {
                                if ( bitmap.getHeight() <= key.height ) {
                                    canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                                } else {
                                    try {
                                        int resized_bitmap_height = key.height;
                                        int resized_bitmap_width = key.height * bitmap.getWidth() / bitmap.getHeight();
                                        Bitmap resized_bitmap = Bitmap.createScaledBitmap(bitmap, resized_bitmap_width, resized_bitmap_height, true);

                                        LogPrint.d(" *************************** ************************* ");
                                        canvas.drawBitmap(resized_bitmap, key.x + (key.width / 2) - (resized_bitmap_width / 2), key.y + (key.height / 2) - (resized_bitmap_height / 2), mLabelPaint);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                                    }
                                }
                                //canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                            }*/
                            /*
                            if (bitmap != null)
                                canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                        */
                        }
                    }
                    setCommonKeys(canvas, key, mOptTextPadding, mOptTextPaint);
                }
            }
        } else if (mKeyboardGubun == Common.MODE_CHUNJIIN_PLUS) {
            KeyboardLogPrint.d("chunjiin + ");
            // TODO :: 뭔가 문제로 간헐적으로 천지인 키보드가 올라오는데 mIsKorean 값이 false로 넘어오는 현상
            if (mIsKorean) {
                for (Key key : mKeys) {
                    setBackDrawable(key, canvas);
                    if (key.label != null) {
                        //canvas.drawText(key.label.toString(), key.x + (key.width / 2), key.y + (key.height / 2) + mLabelTextHalf, mLabelPaint);
                        Rect textBounds = new Rect();
                        mLabelPaint.getTextBounds(key.label.toString(), 0, key.label.toString().length(), textBounds);
                        canvas.drawText(key.label.toString(), key.x + (key.width / 2 ), key.y + (key.height / 2 ) - textBounds.exactCenterY(), mLabelPaint);
                    }
                    else {
                        Drawable myIcon = getSpKeyIcon(key.codes[0], mIsTemp);
                        if ( myIcon == null )
                            myIcon = key.icon;
                        if ( myIcon != null ) {
                            if ( mSpTxtColor < 0 )
                                mSpTxtColor = R.color.aikbd_white;
                            int colorCode = ContextCompat.getColor(mContext, mSpTxtColor);
                            Bitmap sourceBitmap = Util.convertDrawableToBitmap(myIcon);
                            Bitmap bitmap = Util.changeImageColor(sourceBitmap, colorCode);

                            int bitmapHeight = Common.convertDpToPx(mContext, 42.2f);
                            int bitmapWidth = bitmapHeight * bitmap.getWidth() / bitmap.getHeight();

                            KeyboardLogPrint.d("key.height :: " + key.height + " , bitmap.height :: " + bitmap.getHeight() + " , bitmapHeight :: " + bitmapHeight);
                            bitmap = Bitmap.createScaledBitmap(bitmap, bitmapWidth, bitmapHeight, true);
                            if ( bitmap != null ) {
                                canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                            }
                            /*
                            if ( bitmap != null ) {
                                if ( bitmap.getHeight() <= key.height ) {
                                    canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                                } else {
                                    try {
                                        int resized_bitmap_height = key.height;
                                        int resized_bitmap_width = key.height * bitmap.getWidth() / bitmap.getHeight();
                                        Bitmap resized_bitmap = Bitmap.createScaledBitmap(bitmap, resized_bitmap_width, resized_bitmap_height, true);

                                        LogPrint.d(" *************************** ************************* ");
                                        canvas.drawBitmap(resized_bitmap, key.x + (key.width / 2) - (resized_bitmap_width / 2), key.y + (key.height / 2) - (resized_bitmap_height / 2), mLabelPaint);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                                    }
                                }
                                //canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                            }*/
                            /*
                            if (bitmap != null)
                                canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                        */
                        }
                    }
                    setChunjiinPlusKeys(canvas, key, mOptTextPadding, mOptTextPaint);
                }
            } else {
                for (Key key : mKeys) {
                    setBackDrawable(key, canvas);
                    if (key.label != null) {
                        String changed;
                        if (isShifted()) {
                            changed = key.label.toString().toUpperCase();
                        } else {
                            changed = key.label.toString().toLowerCase();
                        }
                        if( key.codes[0] == 95 ) {
                            LogPrint.d("label _ ");
                            canvas.drawText(changed, key.x + (key.width / 2), key.y + (key.height / 2) + mLabelTextHalf - mLabelPaint.descent() + 2, mLabelPaint);
                        } else {
                            LogPrint.d("label not _ ");
                            Rect textBounds = new Rect();
                            mLabelPaint.getTextBounds(changed, 0, changed.length(), textBounds);
                            canvas.drawText(changed, key.x + (key.width / 2 ), key.y + (key.height / 2 ) - textBounds.exactCenterY(), mLabelPaint);
                        }
                        //canvas.drawText(changed, key.x + (key.width / 2), key.y + (key.height / 2) + mLabelTextHalf - (mLabelPaint.descent() / 2) + 2, mLabelPaint);

                    } else {
                        Drawable myIcon = getSpKeyIcon(key.codes[0], mIsTemp);
                        if ( myIcon == null )
                            myIcon = key.icon;
                        if ( myIcon != null ){
                            if ( mSpTxtColor < 0 )
                                mSpTxtColor = R.color.aikbd_white;
                            int colorCode = ContextCompat.getColor(mContext, mSpTxtColor);
                            Bitmap sourceBitmap = Util.convertDrawableToBitmap(myIcon);
                            Bitmap bitmap = Util.changeImageColor(sourceBitmap, colorCode);

                            int bitmapHeight = Common.convertDpToPx(mContext, 42.2f);
                            int bitmapWidth = bitmapHeight * bitmap.getWidth() / bitmap.getHeight();

                            KeyboardLogPrint.d("key.height :: " + key.height + " , bitmap.height :: " + bitmap.getHeight() + " , bitmapHeight :: " + bitmapHeight);
                            bitmap = Bitmap.createScaledBitmap(bitmap, bitmapWidth, bitmapHeight, true);
                            if ( bitmap != null ) {
                                canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                            }
                            /*
                            if ( bitmap != null ) {
                                if ( bitmap.getHeight() <= key.height ) {
                                    canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                                } else {
                                    try {
                                        int resized_bitmap_height = key.height;
                                        int resized_bitmap_width = key.height * bitmap.getWidth() / bitmap.getHeight();
                                        Bitmap resized_bitmap = Bitmap.createScaledBitmap(bitmap, resized_bitmap_width, resized_bitmap_height, true);

                                        LogPrint.d(" *************************** ************************* ");
                                        canvas.drawBitmap(resized_bitmap, key.x + (key.width / 2) - (resized_bitmap_width / 2), key.y + (key.height / 2) - (resized_bitmap_height / 2), mLabelPaint);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                                    }
                                }
                                //canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                            }*/
                            /*
                            if (bitmap != null)
                                canvas.drawBitmap(bitmap, key.x + (key.width / 2) - (bitmap.getWidth() / 2), key.y + (key.height / 2) - (bitmap.getHeight() / 2), mLabelPaint);
                        */
                        }
                    }
                    setCommonKeys(canvas, key, mOptTextPadding, mOptTextPaint);
                }
            }
        }
    }

    @Override
    protected boolean onLongPress(Key key) {
        if (key.codes[0] == Keyboard.KEYCODE_CANCEL) {
            getOnKeyboardActionListener().onKey(KEYCODE_OPTIONS, null);
            return true;
        } else if (key.codes[0] == Keyboard.KEYCODE_DELETE || key.codes[0] == 32) { // 32 spacebar
            return super.onLongPress(key);
        } else if (key.codes[0] == -6) {
            getOnKeyboardActionListener().onKey(-6, null);
            return true;
        } else {
            if (mKeyboardGubun == Common.MODE_QUERTY) {
                setCommonLongPressKeys(key);
            } else if (mKeyboardGubun == Common.MODE_CHUNJIIN) {
                if (mIsKorean) {
                    if (key.codes[0] == 63 || key.codes[0] == 33)
                        return super.onLongPress(key);
                    else {
                        setChunjiinLongPressKey(key);
                    }
                } else {
                    setCommonLongPressKeys(key);
                }
            } else if (mKeyboardGubun == Common.MODE_DAN) {
                if (mIsKorean) {
                    setDanLongPressKey(key);
                } else {
                    setCommonLongPressKeys(key);
                }
            } else if (mKeyboardGubun == Common.MODE_NARA) {
                if (mIsKorean) {
                    if (key.codes[0] == 63)
                        return super.onLongPress(key);
                    else {
                        setNaraLongPressKey(key);
                    }
                } else {
                    setCommonLongPressKeys(key);
                }
            } else if (mKeyboardGubun == Common.MODE_CHUNJIIN_PLUS) {
                if (mIsKorean) {
                    if (key.codes[0] == 63 || key.codes[0] == 33)
                        return super.onLongPress(key);
                    else {
                        setChunjiinPlusLongPressKey(key);
                    }
                } else {
                    setCommonLongPressKeys(key);
                }
            }
            return true;
        }
    }

    public void redrawKeys() {
        invalidateAllKeys();
    }

    private void setBackDrawable(Key _key, Canvas canvas) {
        if ( _key.codes[0] == -8081 )
            return;
        int[] drawableState = _key.getCurrentDrawableState();

        if( mIsKorean && mKeyboardGubun == Common.MODE_CHUNJIIN_PLUS && _key.codes[0] == -226){
            mBtnBackground.setState(drawableState);
            if (drawableState.length == 0 && Math.abs(mNormalBtnColor) >= 1) {
                mBtnBackground.getCurrent().setColorFilter(mNormalBtnColor, PorterDuff.Mode.SRC_ATOP);
            }

            final Rect bounds = mBtnBackground.getBounds();
            if (_key.width != bounds.right ||
                    _key.height != bounds.bottom) {
                mBtnBackground.setBounds(mBtnpadding_W, mBtnpadding_H, _key.width - mBtnpadding_W, _key.height - mBtnpadding_H);
            }
            canvas.translate(_key.x, _key.y);
            mBtnBackground.setAlpha(mBtnAlpha);
            mBtnBackground.draw(canvas);
        }else if (_key.codes[0] < 0 || _key.codes[0] == 32 || _key.codes[0] == 10){
            mDarkBtnBackground.setState(drawableState);

            if (drawableState.length == 0 && Math.abs(mSpecialBtnColor) >= 1){
                mDarkBtnBackground.getCurrent().setColorFilter(mSpecialBtnColor, PorterDuff.Mode.SRC_ATOP);
            } else if ( _key.codes[0] == -1 && Math.abs(mSpecialBtnColor) >= 1) {
                mDarkBtnBackground.getCurrent().setColorFilter(mSpecialBtnColor, PorterDuff.Mode.SRC_ATOP);
            }

            final Rect bounds = mDarkBtnBackground.getBounds();
            if (_key.width != bounds.right ||
                    _key.height != bounds.bottom) {
                mDarkBtnBackground.setBounds(mBtnpadding_W, mBtnpadding_H, _key.width - mBtnpadding_W, _key.height - mBtnpadding_H);
            }
            canvas.translate(_key.x, _key.y);
            mDarkBtnBackground.setAlpha(mDarkBtnAlpha);
            mDarkBtnBackground.draw(canvas);
        } else {
            if ( mIsKorean &&  mKeyboardGubun == Common.MODE_CHUNJIIN_PLUS ) {
                if ( _key.codes[0] == 66
//                        || _key.codes[0] == 63 || _key.codes[0] == 44
                        || _key.codes[0] == 46) {
                    mDarkBtnBackground.setState(drawableState);

                    if (drawableState.length == 0 && Math.abs(mSpecialBtnColor) >= 1) {
                        mDarkBtnBackground.getCurrent().setColorFilter(mSpecialBtnColor, PorterDuff.Mode.SRC_ATOP);
                    } else if ( _key.codes[0] == -1 && Math.abs(mSpecialBtnColor) >= 1) {
                        mDarkBtnBackground.getCurrent().setColorFilter(mSpecialBtnColor, PorterDuff.Mode.SRC_ATOP);
                    }

                    final Rect bounds = mDarkBtnBackground.getBounds();
                    if (_key.width != bounds.right ||
                            _key.height != bounds.bottom) {
                        mDarkBtnBackground.setBounds(mBtnpadding_W, mBtnpadding_H, _key.width - mBtnpadding_W, _key.height - mBtnpadding_H);
                    }
                    canvas.translate(_key.x, _key.y);
                    mDarkBtnBackground.setAlpha(mDarkBtnAlpha);
                    mDarkBtnBackground.draw(canvas);
                } else {
                    mBtnBackground.setState(drawableState);
                    if (drawableState.length == 0 && Math.abs(mNormalBtnColor) >= 1) {
                        mBtnBackground.getCurrent().setColorFilter(mNormalBtnColor, PorterDuff.Mode.SRC_ATOP);
                    }

                    final Rect bounds = mBtnBackground.getBounds();
                    if (_key.width != bounds.right ||
                            _key.height != bounds.bottom) {
                        mBtnBackground.setBounds(mBtnpadding_W, mBtnpadding_H, _key.width - mBtnpadding_W, _key.height - mBtnpadding_H);
                    }
                    canvas.translate(_key.x, _key.y);
                    mBtnBackground.setAlpha(mBtnAlpha);
                    mBtnBackground.draw(canvas);
                }
            } else {
                mBtnBackground.setState(drawableState);
                if (drawableState.length == 0 && Math.abs(mNormalBtnColor) >= 1) {
                    mBtnBackground.getCurrent().setColorFilter(mNormalBtnColor, PorterDuff.Mode.SRC_ATOP);
                }

                final Rect bounds = mBtnBackground.getBounds();
                if (_key.width != bounds.right ||
                        _key.height != bounds.bottom) {
                    mBtnBackground.setBounds(mBtnpadding_W, mBtnpadding_H, _key.width - mBtnpadding_W, _key.height - mBtnpadding_H);
                }
                canvas.translate(_key.x, _key.y);
                mBtnBackground.setAlpha(mBtnAlpha);
                mBtnBackground.draw(canvas);
            }
        }
        _key.iconPreview = mBtnBackground;
        canvas.translate(-_key.x, -_key.y);
    }


    private void setQwertyCommonKeys(Canvas canvas, Key key, int val, Paint paint) {
        if ( mExtKeyboard != SoftKeyboard.PHONE_SYMBOL )
        {
            if (key.codes != null) {
                if (key.codes[0] == 113)
                    canvas.drawText(String.valueOf(1), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                else if (key.codes[0] == 119)
                    canvas.drawText(String.valueOf(2), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                else if (key.codes[0] == 101)
                    canvas.drawText(String.valueOf(3), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                else if (key.codes[0] == 114)
                    canvas.drawText(String.valueOf(4), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                else if (key.codes[0] == 116)
                    canvas.drawText(String.valueOf(5), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                else if (key.codes[0] == 121)
                    canvas.drawText(String.valueOf(6), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                else if (key.codes[0] == 117)
                    canvas.drawText(String.valueOf(7), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                else if (key.codes[0] == 105)
                    canvas.drawText(String.valueOf(8), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                else if (key.codes[0] == 111)
                    canvas.drawText(String.valueOf(9), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                else if (key.codes[0] == 112)
                    canvas.drawText(String.valueOf(0), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                /**
                else if (key.codes[0] == 46) {
                    if (mExtKeyboard != SoftKeyboard.NUM_KEYBOARD && mExtKeyboard != SoftKeyboard.SYMBOL_KEYBOARD) {

                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.aikbd_more_dot);
                        int size = getResources().getDimensionPixelOffset(R.dimen.aikbd_long_key_image_size);
                        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);
                        if (resizedBitmap != null) {
                            canvas.drawBitmap(resizedBitmap, key.x + key.width - mOptTextPadding + (size / 2), key.y + (size / 2), mLabelPaint);
                        }
                    }
                }**/
            }
        }
    }

    private void setCommonKeys(Canvas canvas, Key key, int val, Paint paint) {
        if ( mExtKeyboard != SoftKeyboard.PHONE_SYMBOL )
        {
            if ( mIsKorean ) {
                if ( mKeyboardGubun == Common.MODE_QUERTY ) {
                    if ( mKeys.get(0).codes[0] == 113 ) {
                        if (key.codes != null) {
                            if (key.codes[0] == 113)
                                canvas.drawText(String.valueOf(1), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                            else if (key.codes[0] == 119)
                                canvas.drawText(String.valueOf(2), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                            else if (key.codes[0] == 101)
                                canvas.drawText(String.valueOf(3), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                            else if (key.codes[0] == 114)
                                canvas.drawText(String.valueOf(4), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                            else if (key.codes[0] == 116)
                                canvas.drawText(String.valueOf(5), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                            else if (key.codes[0] == 121)
                                canvas.drawText(String.valueOf(6), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                            else if (key.codes[0] == 117)
                                canvas.drawText(String.valueOf(7), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                            else if (key.codes[0] == 105)
                                canvas.drawText(String.valueOf(8), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                            else if (key.codes[0] == 111)
                                canvas.drawText(String.valueOf(9), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                            else if (key.codes[0] == 112)
                                canvas.drawText(String.valueOf(0), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                            /**
                            else if (key.codes[0] == 46) {
                                if (mExtKeyboard != SoftKeyboard.NUM_KEYBOARD && mExtKeyboard != SoftKeyboard.SYMBOL_KEYBOARD) {

                                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.aikbd_more_dot);
                                    int size = getResources().getDimensionPixelOffset(R.dimen.aikbd_long_key_image_size);
                                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);
                                    if (resizedBitmap != null)
                                        canvas.drawBitmap(resizedBitmap, key.x + key.width - mOptTextPadding + (size / 2), key.y + (size / 2), mLabelPaint);
                                }
                            }**/
                        }
                    }
                } else {
                    if (key.codes != null) {
                        if ( mKeys.get(0).codes[0] == 113 ) {
                            if (key.codes[0] == 113)
                                canvas.drawText(String.valueOf(1), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                            else if (key.codes[0] == 119)
                                canvas.drawText(String.valueOf(2), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                            else if (key.codes[0] == 101)
                                canvas.drawText(String.valueOf(3), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                            else if (key.codes[0] == 114)
                                canvas.drawText(String.valueOf(4), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                            else if (key.codes[0] == 116)
                                canvas.drawText(String.valueOf(5), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                            else if (key.codes[0] == 121)
                                canvas.drawText(String.valueOf(6), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                            else if (key.codes[0] == 117)
                                canvas.drawText(String.valueOf(7), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                            else if (key.codes[0] == 105)
                                canvas.drawText(String.valueOf(8), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                            else if (key.codes[0] == 111)
                                canvas.drawText(String.valueOf(9), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                            else if (key.codes[0] == 112)
                                canvas.drawText(String.valueOf(0), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                        }

                        if (key.codes[0] == 97)
                            canvas.drawText("~", key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                        else if (key.codes[0] == 115)
                            canvas.drawText("!", key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                        else if (key.codes[0] == 100)
                            canvas.drawText("@", key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                        else if (key.codes[0] == 102)
                            canvas.drawText("#", key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                        else if (key.codes[0] == 103)
                            canvas.drawText("^", key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                        else if (key.codes[0] == 104)
                            canvas.drawText("&", key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                        else if (key.codes[0] == 106)
                            canvas.drawText("(", key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                        else if (key.codes[0] == 107)
                            canvas.drawText(")", key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                        else if (key.codes[0] == 108)
                            canvas.drawText("_", key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                        else if (key.codes[0] == 122)
                            canvas.drawText(";", key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                        else if (key.codes[0] == 120)
                            canvas.drawText(":", key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                        else if (key.codes[0] == 99)
                            canvas.drawText("/", key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                        else if (key.codes[0] == 118)
                            canvas.drawText("\"", key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                        else if (key.codes[0] == 98)
                            canvas.drawText(",", key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                        else if (key.codes[0] == 110)
                            canvas.drawText("?", key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                        else if (key.codes[0] == 109)
                            canvas.drawText("+", key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                        /**
                        else if (key.codes[0] == 46) {
                            if (mExtKeyboard != SoftKeyboard.NUM_KEYBOARD && mExtKeyboard != SoftKeyboard.SYMBOL_KEYBOARD) {

                                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.aikbd_more_dot);
                                int size = getResources().getDimensionPixelOffset(R.dimen.aikbd_long_key_image_size);
                                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);
                                if (resizedBitmap != null)
                                    canvas.drawBitmap(resizedBitmap, key.x + key.width - mOptTextPadding + (size / 2), key.y + (size / 2), mLabelPaint);
                            }
                        }**/
                    }
                }
            } else {
                if ( !mIsQwertyNumSet ) {
                    if (key.codes != null) {
                        if (key.codes[0] == 113)
                            canvas.drawText(String.valueOf(1), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                        else if (key.codes[0] == 119)
                            canvas.drawText(String.valueOf(2), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                        else if (key.codes[0] == 101)
                            canvas.drawText(String.valueOf(3), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                        else if (key.codes[0] == 114)
                            canvas.drawText(String.valueOf(4), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                        else if (key.codes[0] == 116)
                            canvas.drawText(String.valueOf(5), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                        else if (key.codes[0] == 121)
                            canvas.drawText(String.valueOf(6), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                        else if (key.codes[0] == 117)
                            canvas.drawText(String.valueOf(7), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                        else if (key.codes[0] == 105)
                            canvas.drawText(String.valueOf(8), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                        else if (key.codes[0] == 111)
                            canvas.drawText(String.valueOf(9), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                        else if (key.codes[0] == 112)
                            canvas.drawText(String.valueOf(0), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
                        /**
                        else if (key.codes[0] == 46) {
                            if (mExtKeyboard != SoftKeyboard.NUM_KEYBOARD && mExtKeyboard != SoftKeyboard.SYMBOL_KEYBOARD) {

                                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.aikbd_more_dot);
                                int size = getResources().getDimensionPixelOffset(R.dimen.aikbd_long_key_image_size);
                                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);
                                if (resizedBitmap != null)
                                    canvas.drawBitmap(resizedBitmap, key.x + key.width - mOptTextPadding + (size / 2), key.y + (size / 2), mLabelPaint);
                            }
                        }**/
                    }
                }
            }
        }
    }

    private void setDrawChunjiinKeys(Canvas canvas, Key key, int val, Paint paint) {
        if (key.codes != null) {
            if (key.codes[0] == 108)
                canvas.drawText(String.valueOf(1), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 122)
                canvas.drawText(String.valueOf(2), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 109)
                canvas.drawText(String.valueOf(3), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 114)
                canvas.drawText(String.valueOf(4), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 115)
                canvas.drawText(String.valueOf(5), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 101)
                canvas.drawText(String.valueOf(6), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 113)
                canvas.drawText(String.valueOf(7), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 116)
                canvas.drawText(String.valueOf(8), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 119)
                canvas.drawText(String.valueOf(9), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 100)
                canvas.drawText(String.valueOf(0), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            /**
            else if (key.codes[0] == 63 || key.codes[0] == 33) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.aikbd_more_dot);
                int size = getResources().getDimensionPixelOffset(R.dimen.aikbd_long_key_image_size);
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);
                if (resizedBitmap != null)
                    canvas.drawBitmap(resizedBitmap, key.x + key.width - mOptTextPadding + (size / 2), key.y + (size / 2), mLabelPaint);
            }**/
        }
    }

    private void setDanKeys(Canvas canvas, Key key, int val, Paint paint) {
        if (key.codes != null) {
            if (key.codes[0] == 113)
                canvas.drawText(String.valueOf(1), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 119)
                canvas.drawText(String.valueOf(2), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 101)
                canvas.drawText(String.valueOf(3), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 114)
                canvas.drawText(String.valueOf(4), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 116)
                canvas.drawText(String.valueOf(5), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 104)
                canvas.drawText(String.valueOf(6), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 111)
                canvas.drawText(String.valueOf(7), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 112)
                canvas.drawText(String.valueOf(8), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 97)
                canvas.drawText(String.valueOf(9), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 115)
                canvas.drawText(String.valueOf(0), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            /**
            else if (key.codes[0] == 46) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.aikbd_more_dot);
                int size = getResources().getDimensionPixelOffset(R.dimen.aikbd_long_key_image_size);
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);
                if (resizedBitmap != null)
                    canvas.drawBitmap(resizedBitmap, key.x + key.width - mOptTextPadding + (size / 2), key.y + (size / 2), mLabelPaint);
            }**/
            /**
            if (key.codes[0] == 113)
                canvas.drawText(String.valueOf(1), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 119)
                canvas.drawText(String.valueOf(2), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 101)
                canvas.drawText(String.valueOf(3), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 114)
                canvas.drawText(String.valueOf(4), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 116)
                canvas.drawText(String.valueOf(5), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 104)
                canvas.drawText(String.valueOf(6), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 111)
                canvas.drawText(String.valueOf(7), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 112)
                canvas.drawText(String.valueOf(8), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 97)
                canvas.drawText("~", key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 115)
                canvas.drawText("!", key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 100)
                canvas.drawText("@", key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 102)
                canvas.drawText("#", key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 103)
                canvas.drawText("^", key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 106)
                canvas.drawText("$", key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 107)
                canvas.drawText(String.valueOf(9), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 108)
                canvas.drawText(String.valueOf(0), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 122)
                canvas.drawText(";", key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 120)
                canvas.drawText(":", key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 99)
                canvas.drawText("?", key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 118)
                canvas.drawText("\"", key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 110)
                canvas.drawText("(", key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 109)
                canvas.drawText(")", key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 46) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.aikbd_more_dot);
                int size = getResources().getDimensionPixelOffset(R.dimen.aikbd_long_key_image_size);
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);
                if (resizedBitmap != null)
                    canvas.drawBitmap(resizedBitmap, key.x + key.width - mOptTextPadding + (size / 2), key.y + (size / 2), mLabelPaint);
            }**/
        }
    }

    private void setNaraKeys(Canvas canvas, Key key, int val, Paint paint) {
        if (key.codes != null) {
            if (key.codes[0] == 114)
                canvas.drawText(String.valueOf(1), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 115)
                canvas.drawText(String.valueOf(2), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 107)
                canvas.drawText(String.valueOf(3), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 102)
                canvas.drawText(String.valueOf(4), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 97)
                canvas.drawText(String.valueOf(5), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 104)
                canvas.drawText(String.valueOf(6), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 116)
                canvas.drawText(String.valueOf(7), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 100)
                canvas.drawText(String.valueOf(8), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 108)
                canvas.drawText(String.valueOf(9), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            /**
            else if (key.codes[0] == 46 | key.codes[0] == 63) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.aikbd_more_dot);
                int size = getResources().getDimensionPixelOffset(R.dimen.aikbd_long_key_image_size);
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);
                if (resizedBitmap != null)
                    canvas.drawBitmap(resizedBitmap, key.x + key.width - mOptTextPadding + (size / 2), key.y + (size / 2), mLabelPaint);
            }**/
            else if (key.codes[0] == 109)
                canvas.drawText(String.valueOf(0), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
        }
    }

    // LongPress 시 노출되는 small text set
    private void setVegaKeys(Canvas canvas, Key key, int val, Paint paint) {
        if (key.codes != null) {
            if (key.codes[0] == 114)
                canvas.drawText(String.valueOf(1), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 108)
                canvas.drawText(String.valueOf(2), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 107)
                canvas.drawText(String.valueOf(3), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 101)
                canvas.drawText(String.valueOf(4), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 115)
                canvas.drawText(String.valueOf(5), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 106)
                canvas.drawText(String.valueOf(6), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 97)
                canvas.drawText(String.valueOf(7), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 113)
                canvas.drawText(String.valueOf(8), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 104)
                canvas.drawText(String.valueOf(9), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            /**
            else if (key.codes[0] == 46 | key.codes[0] == 63) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.aikbd_more_dot);
                int size = getResources().getDimensionPixelOffset(R.dimen.aikbd_long_key_image_size);
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);
                if (resizedBitmap != null)
                    canvas.drawBitmap(resizedBitmap, key.x + key.width - mOptTextPadding + (size / 2), key.y + (size / 2), mLabelPaint);
            }**/
            else if (key.codes[0] == 100)
                canvas.drawText(String.valueOf(0), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
        }
    }

    private void setChunjiinPlusKeys(Canvas canvas, Key key, int val, Paint paint) {
        if (key.codes != null) {
            if (key.codes[0] == 108)
                canvas.drawText(String.valueOf(1), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 122)
                canvas.drawText(String.valueOf(2), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 109)
                canvas.drawText(String.valueOf(3), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 114)
                canvas.drawText(String.valueOf(4), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 0x314B)
                canvas.drawText("ㄲ", key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 115)
                canvas.drawText(String.valueOf(5), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 101)
                canvas.drawText(String.valueOf(6), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 120)
                canvas.drawText("ㄸ", key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 113)
                canvas.drawText(String.valueOf(7), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 118)
                canvas.drawText("ㅃ", key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 116)
                canvas.drawText(String.valueOf(8), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 103)
                canvas.drawText("ㅆ", key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 119)
                canvas.drawText(String.valueOf(9), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 99)
                canvas.drawText("ㅉ", key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            else if (key.codes[0] == 100)
                canvas.drawText(String.valueOf(0), key.x + (key.width - val) + mOptTextHalf, key.y + val, paint);
            /**
            else if (key.codes[0] == 63 || key.codes[0] == 33 || key.codes[0] == 44 || key.codes[0] == 46) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.aikbd_more_dot);
                int size = getResources().getDimensionPixelOffset(R.dimen.aikbd_long_key_image_size);
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);
                if (resizedBitmap != null)
                    canvas.drawBitmap(resizedBitmap, key.x + key.width - mOptTextPadding + (size / 2), key.y + (size / 2), mLabelPaint);
            }**/
        }
    }

    private void setCommonLongPressKeys(Key key) {
        if (key.codes != null) {
            if ( !mIsQwertyNumSet ) {
                if (key.codes[0] == 113)
                    setLongPreview(49);
                else if (key.codes[0] == 119)
                    setLongPreview(50);
                else if (key.codes[0] == 101)
                    setLongPreview(51);
                else if (key.codes[0] == 114)
                    setLongPreview(52);
                else if (key.codes[0] == 116)
                    setLongPreview(53);
                else if (key.codes[0] == 121)
                    setLongPreview(54);
                else if (key.codes[0] == 117)
                    setLongPreview(55);
                else if (key.codes[0] == 105)
                    setLongPreview(56);
                else if (key.codes[0] == 111)
                    setLongPreview(57);
                else if (key.codes[0] == 112)
                    setLongPreview(48);
                else
                    getOnKeyboardActionListener().onKey(key.codes[0], null);
            } else {
                KeyboardLogPrint.d("kksskk mIsKorean :: " + mIsKorean);
                if ( mIsKorean ) {
                    Keyboard keyboard = getKeyboard();
                    if ( keyboard != null ) {
                        boolean isShift = keyboard.isShifted();
                        KeyboardLogPrint.d("kksskk isShift :: " + isShift);
                        if ( !isShift ) {
                            if (key.codes[0] == 113 || key.codes[0] == 119 || key.codes[0] == 116 || key.codes[0] == 101 || key.codes[0] == 114 || key.codes[0] == 111 ||
                                    key.codes[0] == 112) {
                                setLongPressPreview(key.codes[0]);
                                /*
                                Keyboard kb = getKeyboard();
                                if ( kb != null ) {
                                    kb.setShifted(true);
                                    getOnKeyboardActionListener().onKey(key.codes[0], null);
                                }
                                */
                            } else
                                getOnKeyboardActionListener().onKey(key.codes[0], null);
                        } else
                            getOnKeyboardActionListener().onKey(key.codes[0], null);
                    } else
                        getOnKeyboardActionListener().onKey(key.codes[0], null);
                } else
                    getOnKeyboardActionListener().onKey(key.codes[0], null);
            }

/**
 if (key.codes[0] == 113 || key.codes[0] == 49 )
 getOnKeyboardActionListener().onKey(49, null);
 else if (key.codes[0] == 119 || key.codes[0] == 50 )
 getOnKeyboardActionListener().onKey(50, null);
 else if (key.codes[0] == 101 || key.codes[0] == 51 )
 getOnKeyboardActionListener().onKey(51, null);
 else if (key.codes[0] == 114 || key.codes[0] == 52 )
 getOnKeyboardActionListener().onKey(52, null);
 else if (key.codes[0] == 116 || key.codes[0] == 53 )
 getOnKeyboardActionListener().onKey(53, null);
 else if (key.codes[0] == 121 || key.codes[0] == 54 )
 getOnKeyboardActionListener().onKey(54, null);
 else if (key.codes[0] == 117 || key.codes[0] == 55 )
 getOnKeyboardActionListener().onKey(55, null);
 else if (key.codes[0] == 105 || key.codes[0] == 56 )
 getOnKeyboardActionListener().onKey(56, null);
 else if (key.codes[0] == 111 || key.codes[0] == 57 )
 getOnKeyboardActionListener().onKey(57, null);
 else if (key.codes[0] == 112 || key.codes[0] == 48 )
 getOnKeyboardActionListener().onKey(48, null);
 else if (key.codes[0] == 97)
 getOnKeyboardActionListener().onKey(126, null);
 else if (key.codes[0] == 115)
 getOnKeyboardActionListener().onKey(33, null);
 else if (key.codes[0] == 100)
 getOnKeyboardActionListener().onKey(64, null);
 else if (key.codes[0] == 102)
 getOnKeyboardActionListener().onKey(35, null);
 else if (key.codes[0] == 103)
 getOnKeyboardActionListener().onKey(94, null);
 else if (key.codes[0] == 104)
 getOnKeyboardActionListener().onKey(38, null);
 else if (key.codes[0] == 106)
 getOnKeyboardActionListener().onKey(40, null);
 else if (key.codes[0] == 107)
 getOnKeyboardActionListener().onKey(41, null);
 else if (key.codes[0] == 108)
 getOnKeyboardActionListener().onKey(95, null);
 else if (key.codes[0] == 122)
 getOnKeyboardActionListener().onKey(59, null);
 else if (key.codes[0] == 120)
 getOnKeyboardActionListener().onKey(58, null);
 else if (key.codes[0] == 99)
 getOnKeyboardActionListener().onKey(47, null);
 else if (key.codes[0] == 118)
 getOnKeyboardActionListener().onKey(34, null);
 else if (key.codes[0] == 98)
 getOnKeyboardActionListener().onKey(44, null);
 else if (key.codes[0] == 110)
 getOnKeyboardActionListener().onKey(63, null);
 else if (key.codes[0] == 109)
 getOnKeyboardActionListener().onKey(43, null);
 else
 getOnKeyboardActionListener().onKey(key.codes[0], null);**/
        }

    }

    private void setLongPressPreview(final int code) {
        String val = "";
        if ( code == 101 ) {
            val = "ㄸ";
        } else if ( code == 111 ) {
            val = "ㅒ";
        } else if ( code == 112 ) {
            val = "ㅖ";
        } else if ( code == 113 ) {
            val = "ㅃ";
        } else if ( code == 114 ) {
            val = "ㄲ";
        } else if ( code == 116 ) {
            val = "ㅆ";
        } else if ( code == 119 ) {
            val = "ㅉ";
        }

        if(mPreviewText != null) mPreviewText.setText(val);
        Keyboard keyboard = getKeyboard();
        if ( keyboard != null )
            keyboard.setShifted(true);
        getOnKeyboardActionListener().onKey(code, null);
    }

    private void setLongPreview(final int keyCode) {
        char c = Character.toChars(keyCode)[0];

        if(mPreviewText != null) mPreviewText.setText(c + "");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getOnKeyboardActionListener().onKey(keyCode, null);
            }
        }, 500);
    }

    private void setChunjiinLongPressKey(Key key) {
        if (key.codes != null) {
            if (key.codes[0] == 108)
                getOnKeyboardActionListener().onKey(49, null);
            else if (key.codes[0] == 122)
                getOnKeyboardActionListener().onKey(50, null);
            else if (key.codes[0] == 109)
                getOnKeyboardActionListener().onKey(51, null);
            else if (key.codes[0] == 114)
                getOnKeyboardActionListener().onKey(52, null);
            else if (key.codes[0] == 115)
                getOnKeyboardActionListener().onKey(53, null);
            else if (key.codes[0] == 101)
                getOnKeyboardActionListener().onKey(54, null);
            else if (key.codes[0] == 113)
                getOnKeyboardActionListener().onKey(55, null);
            else if (key.codes[0] == 116)
                getOnKeyboardActionListener().onKey(56, null);
            else if (key.codes[0] == 119)
                getOnKeyboardActionListener().onKey(57, null);
            else if (key.codes[0] == 100)
                getOnKeyboardActionListener().onKey(48, null);
        }
    }

    private void setDanLongPressKey(Key key) {
        if (key.codes != null) {
            if (key.codes[0] == 113)
                getOnKeyboardActionListener().onKey(49, null);
            else if (key.codes[0] == 119)
                getOnKeyboardActionListener().onKey(50, null);
            else if (key.codes[0] == 101)
                getOnKeyboardActionListener().onKey(51, null);
            else if (key.codes[0] == 114)
                getOnKeyboardActionListener().onKey(52, null);
            else if (key.codes[0] == 116)
                getOnKeyboardActionListener().onKey(53, null);
            else if (key.codes[0] == 104)
                getOnKeyboardActionListener().onKey(54, null);
            else if (key.codes[0] == 111)
                getOnKeyboardActionListener().onKey(55, null);
            else if (key.codes[0] == 112)
                getOnKeyboardActionListener().onKey(56, null);
            else if (key.codes[0] == 97)
                getOnKeyboardActionListener().onKey(57, null);
            else if (key.codes[0] == 115)
                getOnKeyboardActionListener().onKey(48, null);
            else
                getOnKeyboardActionListener().onKey(key.codes[0], null);

            /**
            if (key.codes[0] == 113)
                getOnKeyboardActionListener().onKey(49, null);
            else if (key.codes[0] == 119)
                getOnKeyboardActionListener().onKey(50, null);
            else if (key.codes[0] == 101)
                getOnKeyboardActionListener().onKey(51, null);
            else if (key.codes[0] == 114)
                getOnKeyboardActionListener().onKey(52, null);
            else if (key.codes[0] == 116)
                getOnKeyboardActionListener().onKey(53, null);
            else if (key.codes[0] == 104)
                getOnKeyboardActionListener().onKey(54, null);
            else if (key.codes[0] == 111)
                getOnKeyboardActionListener().onKey(55, null);
            else if (key.codes[0] == 112)
                getOnKeyboardActionListener().onKey(56, null);
            else if (key.codes[0] == 97)
                getOnKeyboardActionListener().onKey(126, null);
            else if (key.codes[0] == 115)
                getOnKeyboardActionListener().onKey(33, null);
            else if (key.codes[0] == 100)
                getOnKeyboardActionListener().onKey(64, null);
            else if (key.codes[0] == 102)
                getOnKeyboardActionListener().onKey(35, null);
            else if (key.codes[0] == 103)
                getOnKeyboardActionListener().onKey(94, null);
            else if (key.codes[0] == 106)
                getOnKeyboardActionListener().onKey(36, null);
            else if (key.codes[0] == 107)
                getOnKeyboardActionListener().onKey(57, null);
            else if (key.codes[0] == 108)
                getOnKeyboardActionListener().onKey(48, null);
            else if (key.codes[0] == 122)
                getOnKeyboardActionListener().onKey(59, null);
            else if (key.codes[0] == 120)
                getOnKeyboardActionListener().onKey(58, null);
            else if (key.codes[0] == 99)
                getOnKeyboardActionListener().onKey(63, null);
            else if (key.codes[0] == 118)
                getOnKeyboardActionListener().onKey(34, null);
            else if (key.codes[0] == 110)
                getOnKeyboardActionListener().onKey(40, null);
            else if (key.codes[0] == 109)
                getOnKeyboardActionListener().onKey(41, null);**/
        }
    }

    private void setNaraLongPressKey(Key key) {
        if (key.codes != null) {
            if (key.codes[0] == 114)
                getOnKeyboardActionListener().onKey(49, null);
            else if (key.codes[0] == 115)
                getOnKeyboardActionListener().onKey(50, null);
            else if (key.codes[0] == 107)
                getOnKeyboardActionListener().onKey(51, null);
            else if (key.codes[0] == 102)
                getOnKeyboardActionListener().onKey(52, null);
            else if (key.codes[0] == 97)
                getOnKeyboardActionListener().onKey(53, null);
            else if (key.codes[0] == 104)
                getOnKeyboardActionListener().onKey(54, null);
            else if (key.codes[0] == 116)
                getOnKeyboardActionListener().onKey(55, null);
            else if (key.codes[0] == 100)
                getOnKeyboardActionListener().onKey(56, null);
            else if (key.codes[0] == 108)
                getOnKeyboardActionListener().onKey(57, null);
            else if (key.codes[0] == 109)
                getOnKeyboardActionListener().onKey(48, null);
        }
    }

    private void setChunjiinPlusLongPressKey(Key key) {
        if (key.codes != null) {
            if (key.codes[0] == 108)
                getOnKeyboardActionListener().onKey(49, null);  //1
            else if (key.codes[0] == 122)
                getOnKeyboardActionListener().onKey(50, null);  //2
            else if (key.codes[0] == 109)
                getOnKeyboardActionListener().onKey(51, null);  //3
            else if (key.codes[0] == 114)
                getOnKeyboardActionListener().onKey(52, null);  //4
            else if (key.codes[0] == 0x314B)
                getOnKeyboardActionListener().onKey(0x3132, null);  //ㄲ
            else if (key.codes[0] == 115)
                getOnKeyboardActionListener().onKey(53, null);  //5
            else if (key.codes[0] == 101)
                getOnKeyboardActionListener().onKey(54, null);  //6
            else if (key.codes[0] == 120)
                getOnKeyboardActionListener().onKey(0x3138, null);  //ㄸ
            else if (key.codes[0] == 113)
                getOnKeyboardActionListener().onKey(55, null);  //7
            else if (key.codes[0] == 118)
                getOnKeyboardActionListener().onKey(0x3143, null);  //ㅃ
            else if (key.codes[0] == 116)
                getOnKeyboardActionListener().onKey(56, null);  //8
            else if (key.codes[0] == 103)
                getOnKeyboardActionListener().onKey(0x3146, null);  //ㅆ
            else if (key.codes[0] == 119)
                getOnKeyboardActionListener().onKey(57, null);  //9
            else if (key.codes[0] == 99)
                getOnKeyboardActionListener().onKey(0x3149, null);  //ㅉ
            else if (key.codes[0] == 100)
                getOnKeyboardActionListener().onKey(48, null);  //0
        }
    }
/*
    private ArrayList<Integer> getResourceId(int level) {
        ArrayList<Integer> array = new ArrayList<Integer>();
        int bResId = 0;
        int sResId = 0;
        Log.e("TAG", "level :: " + level);
        if (level == 0) {
            bResId = R.dimen.aikbd_key_text_size0;
            sResId = R.dimen.aikbd_key_opt_text_size0;
        } else if (level == 1) {
            bResId = R.dimen.aikbd_key_text_size1;
            sResId = R.dimen.aikbd_key_opt_text_size1;
        } else if (level == 2) {
            bResId = R.dimen.aikbd_key_text_size2;
            sResId = R.dimen.aikbd_key_opt_text_size2;
        } else if (level == 3) {
            bResId = R.dimen.aikbd_key_text_size3;
            sResId = R.dimen.aikbd_key_opt_text_size3;
        } else if (level == 4) {
            bResId = R.dimen.aikbd_key_text_size4;
            sResId = R.dimen.aikbd_key_opt_text_size4;
        } else if (level == 5) {
            bResId = R.dimen.aikbd_key_text_size5;
            sResId = R.dimen.aikbd_key_opt_text_size5;
        } else if (level == 6) {
            bResId = R.dimen.aikbd_key_text_size6;
            sResId = R.dimen.aikbd_key_opt_text_size6;
        } else if (level == 7) {
            bResId = R.dimen.aikbd_key_text_size7;
            sResId = R.dimen.aikbd_key_opt_text_size7;
        } else if (level == 8) {
            bResId = R.dimen.aikbd_key_text_size8;
            sResId = R.dimen.aikbd_key_opt_text_size8;
        } else if (level == 9) {
            bResId = R.dimen.aikbd_key_text_size9;
            sResId = R.dimen.aikbd_key_opt_text_size9;
        } else if (level == 10) {
            bResId = R.dimen.aikbd_key_text_size10;
            sResId = R.dimen.aikbd_key_opt_text_size10;
        } else if (level == 11) {
            bResId = R.dimen.aikbd_key_text_size11;
            sResId = R.dimen.aikbd_key_opt_text_size11;
        } else if (level == 12) {
            bResId = R.dimen.aikbd_key_text_size12;
            sResId = R.dimen.aikbd_key_opt_text_size12;
        } else if (level == 13) {
            bResId = R.dimen.aikbd_key_text_size13;
            sResId = R.dimen.aikbd_key_opt_text_size13;
        } else {
//            bResId = R.dimen.aikbd_key_text_size7;
//            sResId = R.dimen.aikbd_key_opt_text_size7;
            bResId = R.dimen.aikbd_key_text_size13;
            sResId = R.dimen.aikbd_key_opt_text_size13;
        }

//        if (level == 0) {
//            bResId = R.dimen.aikbd_key_text_size0;
//            sResId = R.dimen.aikbd_key_opt_text_size0;
//        } else if (level == 1) {
//            bResId = R.dimen.aikbd_key_text_size1;
//            sResId = R.dimen.aikbd_key_opt_text_size1;
//        } else if (level == 2) {
//            bResId = R.dimen.aikbd_key_text_size2;
//            sResId = R.dimen.aikbd_key_opt_text_size2;
//        } else if (level == 3) {
//            bResId = R.dimen.aikbd_key_text_size3;
//            sResId = R.dimen.aikbd_key_opt_text_size3;
//            ;
//        } else if (level == 4) {
//            bResId = R.dimen.aikbd_key_text_size4;
//            sResId = R.dimen.aikbd_key_opt_text_size4;
//        } else if (level == 5) {
//            bResId = R.dimen.aikbd_key_text_size5;
//            sResId = R.dimen.aikbd_key_opt_text_size5;
//        } else if (level == 6) {
//            bResId = R.dimen.aikbd_key_text_size6;
//            sResId = R.dimen.aikbd_key_opt_text_size6;
//        } else if (level == 7) {
//            bResId = R.dimen.aikbd_key_text_size7;
//            sResId = R.dimen.aikbd_key_opt_text_size7;
//        } else if (level == 8) {
//            bResId = R.dimen.aikbd_key_text_size8;
//            sResId = R.dimen.aikbd_key_opt_text_size8;
//        } else if (level == 9) {
//            bResId = R.dimen.aikbd_key_text_size9;
//            sResId = R.dimen.aikbd_key_opt_text_size9;
//        } else if (level == 10) {
//            bResId = R.dimen.aikbd_key_text_size10;
//            sResId = R.dimen.aikbd_key_opt_text_size10;
//        } else {
//            bResId = R.dimen.aikbd_key_text_size5;
//            sResId = R.dimen.aikbd_key_opt_text_size5;
//        }

        array.add(bResId);
        array.add(sResId);
        return array;
    }
*/
    private ArrayList<Integer> getResourceId(int level) {
        ArrayList<Integer> array = new ArrayList<Integer>();
        int bResId = 0;
        int sResId = 0;
        int bNResId = 0;
        int keySizeGubun = KEY_SIZE_1;
        if (mKeyboardGubun == Common.MODE_QUERTY) {
            keySizeGubun = KEY_SIZE_1;
        } else if (mKeyboardGubun == Common.MODE_CHUNJIIN) {
            if (mIsKorean) {
                keySizeGubun = KEY_SIZE_2;
            } else {
                keySizeGubun = KEY_SIZE_1;
            }
        } else if (mKeyboardGubun == Common.MODE_DAN) {
            if (mIsKorean) {
                keySizeGubun = KEY_SIZE_1;
            } else {
                keySizeGubun = KEY_SIZE_1;
            }
        } else if (mKeyboardGubun == Common.MODE_NARA) {
            if (mIsKorean) {
                keySizeGubun = KEY_SIZE_2;
            } else {
                keySizeGubun = KEY_SIZE_1;
            }
        } else if (mKeyboardGubun == Common.MODE_CHUNJIIN_PLUS) {
            if (mIsKorean) {
                keySizeGubun = KEY_SIZE_2;
            } else {
                keySizeGubun = KEY_SIZE_1;
            }
        }

        if ( keySizeGubun == KEY_SIZE_1 ) {
            KeyboardLogPrint.d("level :: " + level);
            if (level == 0) {
                bResId = R.dimen.aikbd_key_text_size0;
                sResId = R.dimen.aikbd_key_opt_text_size;
                bNResId = R.dimen.aikbd_key_text_size0_n;
            } else if (level == 3) {
                bResId = R.dimen.aikbd_key_text_size1;
                sResId = R.dimen.aikbd_key_opt_text_size;
                bNResId = R.dimen.aikbd_key_text_size1_n;
            } else if (level == 6) {
                bResId = R.dimen.aikbd_key_text_size2;
                sResId = R.dimen.aikbd_key_opt_text_size;
                bNResId = R.dimen.aikbd_key_text_size2_n;
            } else if (level == 9) {
                bResId = R.dimen.aikbd_key_text_size3;
                sResId = R.dimen.aikbd_key_opt_text_size;
                bNResId = R.dimen.aikbd_key_text_size3_n;
            } else if (level == 12) {
                bResId = R.dimen.aikbd_key_text_size4;
                sResId = R.dimen.aikbd_key_opt_text_size;
                bNResId = R.dimen.aikbd_key_text_size4_n;
            } else if (level == 15) {
                bResId = R.dimen.aikbd_key_text_size5;
                sResId = R.dimen.aikbd_key_opt_text_size;
                bNResId = R.dimen.aikbd_key_text_size5_n;
            } else if (level == 18) {
                bResId = R.dimen.aikbd_key_text_size6;
                sResId = R.dimen.aikbd_key_opt_text_size;
                bNResId = R.dimen.aikbd_key_text_size6_n;
            } else if (level == 21) {
                bResId = R.dimen.aikbd_key_text_size7;
                sResId = R.dimen.aikbd_key_opt_text_size;
                bNResId = R.dimen.aikbd_key_text_size7_n;
            } else if (level == 24) {
                bResId = R.dimen.aikbd_key_text_size8;
                sResId = R.dimen.aikbd_key_opt_text_size;
                bNResId = R.dimen.aikbd_key_text_size8_n;
            } else if (level == 27) {
                bResId = R.dimen.aikbd_key_text_size9;
                sResId = R.dimen.aikbd_key_opt_text_size;
                bNResId = R.dimen.aikbd_key_text_size9_n;
            } else if (level == 30) {
                bResId = R.dimen.aikbd_key_text_size10;
                sResId = R.dimen.aikbd_key_opt_text_size;
                bNResId = R.dimen.aikbd_key_text_size10_n;
            } else if (level == 33) {
                bResId = R.dimen.aikbd_key_text_size11;
                sResId = R.dimen.aikbd_key_opt_text_size;
                bNResId = R.dimen.aikbd_key_text_size11_n;
            } else {
                bResId = R.dimen.aikbd_key_text_size6;
                sResId = R.dimen.aikbd_key_opt_text_size;
                bNResId = R.dimen.aikbd_key_text_size6_n;
            }
        } else if ( keySizeGubun == KEY_SIZE_2 ) {
            if (level == 0) {
                bResId = R.dimen.aikbd_key_text_size0_d;
                sResId = R.dimen.aikbd_key_opt_text_size_d;
                bNResId = R.dimen.aikbd_key_text_size0_n;
            } else if (level == 3) {
                bResId = R.dimen.aikbd_key_text_size1_d;
                sResId = R.dimen.aikbd_key_opt_text_size_d;
                bNResId = R.dimen.aikbd_key_text_size1_n;
            } else if (level == 6) {
                bResId = R.dimen.aikbd_key_text_size2_d;
                sResId = R.dimen.aikbd_key_opt_text_size_d;
                bNResId = R.dimen.aikbd_key_text_size2_n;
            } else if (level == 9) {
                bResId = R.dimen.aikbd_key_text_size3_d;
                sResId = R.dimen.aikbd_key_opt_text_size_d;
                bNResId = R.dimen.aikbd_key_text_size3_n;
            } else if (level == 12) {
                bResId = R.dimen.aikbd_key_text_size4_d;
                sResId = R.dimen.aikbd_key_opt_text_size_d;
                bNResId = R.dimen.aikbd_key_text_size4_n;
            } else if (level == 15) {
                bResId = R.dimen.aikbd_key_text_size5_d;
                sResId = R.dimen.aikbd_key_opt_text_size_d;
                bNResId = R.dimen.aikbd_key_text_size5_n;
            } else if (level == 18) {
                bResId = R.dimen.aikbd_key_text_size6_d;
                sResId = R.dimen.aikbd_key_opt_text_size_d;
                bNResId = R.dimen.aikbd_key_text_size6_n;
            } else if (level == 21) {
                bResId = R.dimen.aikbd_key_text_size7_d;
                sResId = R.dimen.aikbd_key_opt_text_size_d;
                bNResId = R.dimen.aikbd_key_text_size7_n;
            } else if (level == 24) {
                bResId = R.dimen.aikbd_key_text_size8_d;
                sResId = R.dimen.aikbd_key_opt_text_size_d;
                bNResId = R.dimen.aikbd_key_text_size8_n;
            } else if (level == 27) {
                bResId = R.dimen.aikbd_key_text_size9_d;
                sResId = R.dimen.aikbd_key_opt_text_size_d;
                bNResId = R.dimen.aikbd_key_text_size9_n;
            } else if (level == 30) {
                bResId = R.dimen.aikbd_key_text_size10_d;
                sResId = R.dimen.aikbd_key_opt_text_size_d;
                bNResId = R.dimen.aikbd_key_text_size10_n;
            } else if (level == 33) {
                bResId = R.dimen.aikbd_key_text_size11_d;
                sResId = R.dimen.aikbd_key_opt_text_size_d;
                bNResId = R.dimen.aikbd_key_text_size11_n;
            } else {
                bResId = R.dimen.aikbd_key_text_size6_d;
                sResId = R.dimen.aikbd_key_opt_text_size_d;
                bNResId = R.dimen.aikbd_key_text_size6_n;
            }
        }

        array.add(bResId);
        array.add(sResId);
        array.add(bNResId);
        return array;
    }

    private String setKQwertySift(boolean isShift, Key key) {
        String val = "";
        int code = key.codes[0];
        if ( code == 101 || code == 111 || code == 112 || code == 113 || code == 114 || code == 116 || code == 119 ) {
            if ( isShift ) {
                if ( code == 101 ) {
                    val = "ㄸ";
                } else if ( code == 111 ) {
                    val = "ㅒ";
                } else if ( code == 112 ) {
                    val = "ㅖ";
                } else if ( code == 113 ) {
                    val = "ㅃ";
                } else if ( code == 114 ) {
                    val = "ㄲ";
                } else if ( code == 116 ) {
                    val = "ㅆ";
                } else if ( code == 119 ) {
                    val = "ㅉ";
                }
            } else {
                if ( code == 101 ) {
                    val = "ㄷ";
                } else if ( code == 111 ) {
                    val = "ㅐ";
                } else if ( code == 112 ) {
                    val = "ㅔ";
                } else if ( code == 113 ) {
                    val = "ㅂ";
                } else if ( code == 114 ) {
                    val = "ㄱ";
                } else if ( code == 116 ) {
                    val = "ㅅ";
                } else if ( code == 119 ) {
                    val = "ㅈ";
                }
            }
        } else {
            val = key.label.toString();
        }
        return val;
    }

    private Drawable getSpKeyIcon(int keyCode, boolean isTemp) {
        Drawable dr = null;
        if ( isTemp || keyCode == -8081 ) {
            return null;
        } else {
            if ( mThemeModel != null ) {
//                if ( keyCode == KEYCODE_SHIFT && mShiftDr != null )
//                    dr = mShiftDr;
                if ( keyCode ==KEYCODE_SHIFT ) {
                    int status = 0;
                    if ( mIsKorean ) {
                        status = SoftKeyboard.GetKoShiftStatus();
                    } else {
                        status = SoftKeyboard.GetEnShiftStatus();
                    }

                    if ( status == SoftKeyboard.SHIFT_STATE_UPPER_CASE && mShiftDr1 != null ) {
                        dr = mShiftDr1;
                    } else if ( status == SoftKeyboard.SHIFT_STATE_ONLY_UPPER_CASE && mShiftDr2 != null ) {
                        dr = mShiftDr2;
                    } else {
                        if ( mShiftDr != null )
                            dr = mShiftDr;
                    }
                } else if ( keyCode == KEYCODE_DELETE && mBackDr != null )
                    dr = mBackDr;
                else if ( keyCode == KEYCODE_SYMBOL && mSymbolDr != null )
                    dr = mSymbolDr;
                else if ( keyCode == KEYCODE_LANG && mLangDr != null )
                    dr = mLangDr;
                else if ( keyCode == KEYCODE_SPACE && mSpaceDr != null )
                    dr = mSpaceDr;
                else if ( keyCode == KEYCODE_ENTER ) {
                    int options = SoftKeyboard.GetImeOption();
                    switch (options & (EditorInfo.IME_MASK_ACTION | EditorInfo.IME_FLAG_NO_ENTER_ACTION)) {
                        case EditorInfo.IME_ACTION_SEARCH:
                            if ( mSEnterDr != null )
                                dr = mSEnterDr;
                            break;
                        default:
                            if ( mEnterDr != null )
                                dr = mEnterDr;
                            break;
                    }
                }
//                else if ( keyCode == KEYCODE_EMOJI ) {
//                    if (mEmojiDr != null)
//                        dr = mEmojiDr;
//                }
                 else if ( keyCode == KEYCODE_EMOJI ) {
                    if ( mEmoticonDr != null )
                        dr = mEmoticonDr;
                 } else if ( keyCode == KEYCODE_EMOJI_RECENT ) {
                    if ( mEmojiRecentDr != null )
                        dr = mEmojiRecentDr;
                } else if ( keyCode == KEYCODE_EMOTICON_RECENT ) {
                    if ( mEmoticonRecentDr != null )
                        dr = mEmoticonRecentDr;
                } else if ( keyCode == KEYCODE_EMOTICON_FIRST ) {
                    if ( mEmoticonFirstDr != null )
                        dr = mEmoticonFirstDr;
                } else if ( keyCode == KEYCODE_EMOTICON_SECOND ) {
                    if ( mEmoticonSecondDr != null )
                        dr = mEmoticonSecondDr;
                } else if ( keyCode == KEYCODE_EMOTICON_THIRD ) {
                    if ( mEmoticonThirdDr != null )
                        dr = mEmoticonThirdDr;
                } else if ( keyCode == KEYCODE_EMOTICON_FOURTH ) {
                    if ( mEmoticonFourthDr != null )
                        dr = mEmoticonFourthDr;
                } else if ( keyCode == KEYCODE_EMOTICON_FIFTH ) {
                    if ( mEmoticonFifthDr != null )
                        dr = mEmoticonFifthDr;
                } else if ( keyCode == KEYCODE_EMOTICON_SIXTH ) {
                    if ( mEmoticonSixthDr != null )
                        dr = mEmoticonSixthDr;
                }

            }
        }
        return dr;
    }

    private void setPreviewBackColor(){
        Class c = LatinKeyboardView.class.getSuperclass();
        try {
            Field targetField = c.getDeclaredField("mPreviewText");
            if (targetField != null) {
                targetField.setAccessible(true);

                mPreviewText = (TextView) (targetField.get(LatinKeyboardView.this));
                Bitmap bgBitmap = null;

                if(mThemeModel.getKeyPreview() != null && !mThemeModel.getKeyPreview().isEmpty()) {
                    bgBitmap = ThemeManager.GetBitmapFromPath(mThemeModel.getKeyPreview());
                }

                if(bgBitmap != null){

                    int previewHeight = (int)getResources().getDimension(R.dimen.aikbd_key_preview_height);
                    int previewWidth = (bgBitmap.getWidth() * previewHeight) / bgBitmap.getHeight();

                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bgBitmap, previewWidth, previewHeight, false);

                    BitmapDrawable mBgDrawable = new BitmapDrawable(getResources(), scaledBitmap);
                    if(mPreviewText.getLayoutParams() instanceof FrameLayout.LayoutParams) {
                        mPreviewText.setLayoutParams(new FrameLayout.LayoutParams(previewWidth, previewHeight));
                    }else if(mPreviewText.getLayoutParams() instanceof ViewGroup.MarginLayoutParams){
                        mPreviewText.setLayoutParams(new ViewGroup.MarginLayoutParams(previewWidth, previewHeight));
                    }else{
                        mPreviewText.setLayoutParams(new ViewGroup.LayoutParams(previewWidth, previewHeight));
                    }
                    mPreviewText.setBackground(mBgDrawable);

                }else{
                    mPreviewText.setBackground(mBtnBackground);
                }

                mPreviewText.setTextColor(mTxtColor);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private BroadcastReceiver mSetChange = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("THEME_CHANGE".equals(action)) {
                try {
                    AIKBD_DBHelper helper = new AIKBD_DBHelper(mContext);
                    String str = helper.getTheme();
                    if ( !TextUtils.isEmpty(str) ) {
                        mThemeModel = ThemeManager.GetThemeModel(str, 100);
                    }


                    if (mThemeModel != null) {
                        try {
                            mShiftDr = ThemeManager.GetDrawableFromPath(mThemeModel.getShiftImg());
                            if ( !TextUtils.isEmpty(mThemeModel.getShiftImg1())) {
                                mShiftDr1 = ThemeManager.GetDrawableFromPath(mThemeModel.getShiftImg1());
                            } else {
                                mShiftDr1 = mShiftDr;
                            }
                            KeyboardLogPrint.e("shift2 img path :: " + mThemeModel.getShiftImg2());
                            if ( !TextUtils.isEmpty(mThemeModel.getShiftImg2())) {
                                mShiftDr2 = ThemeManager.GetDrawableFromPath(mThemeModel.getShiftImg2());
                            } else {
                                mShiftDr2 = mShiftDr;
                            }
                            mBackDr = ThemeManager.GetDrawableFromPath(mThemeModel.getBackImg());
                            mSymbolDr = ThemeManager.GetDrawableFromPath(mThemeModel.getKeySymbol());
                            mLangDr = ThemeManager.GetDrawableFromPath(mThemeModel.getKeyLang());
                            mSpaceDr = ThemeManager.GetDrawableFromPath(mThemeModel.getSpaceImg());
                            mEnterDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEnterImg());
                            mSEnterDr = ThemeManager.GetDrawableFromPath(mThemeModel.getKeySearchEnter());
                            mEmojiDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmojiImg());
                            mEmojiRecentDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonRecent());
                            mEmoticonDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonImg());
                            mEmoticonRecentDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonRecent());
                            mEmoticonFirstDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonFirst());
                            mEmoticonSecondDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonSecond());
                            mEmoticonThirdDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonThird());
                            mEmoticonFourthDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonFourth());
                            mEmoticonFifthDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonFifth());
                            mEmoticonSixthDr = ThemeManager.GetDrawableFromPath(mThemeModel.getEmoticonSixth());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            setPreviewBackColor();
        }
    };



}
