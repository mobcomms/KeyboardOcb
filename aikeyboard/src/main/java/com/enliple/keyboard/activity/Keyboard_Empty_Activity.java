package com.enliple.keyboard.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.enliple.keyboard.R;
import com.enliple.keyboard.common.Common;
import com.enliple.keyboard.common.KeyboardLogPrint;

/**
 * Created by Administrator on 2017-10-26.
 */

public class Keyboard_Empty_Activity extends Activity {

    private int mTxtId = -1;
    private int mSpIconId = -1;
    private int mOptTxtId = -1;
    private int mNorSelector = -1;
    private int mSpeSelector = -1;

    private LatinKeyboardView kv;

    private int mBgId = -1;
    private int mNorBtnColor = -1;
    private int mSpBtnColor = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aikbd_activity_empty);

        kv = (LatinKeyboardView) findViewById(R.id.keyboard_view);
        boolean setPreview = SharedPreference.getBoolean(Keyboard_Empty_Activity.this, Common.PREF_PREVIEW_SETTING);
        setKeyboard(setPreview);

        mBgId = -1;
        mTxtId = R.color.aikbd_white;
        mSpIconId = R.color.aikbd_white;
        mOptTxtId = R.color.aikbd_white;
        mNorSelector = R.drawable.aikbd_btn_selector;
        mSpeSelector = R.drawable.aikbd_dark_btn_selector;
        kv.setBackground(mNorSelector, mSpeSelector);
//        mNorBtnColor = SharedPreference.getInt(mContext, Common.PREF_KEYBOARD_BUTTON_COLOR);
//        mSpBtnColor = SharedPreference.getInt(mContext, Common.PREF_KEYBOARD_SBUTTON_COLOR);
//        if ( mNorBtnColor == -1 )
//            mNorBtnColor = Color.parseColor(getColorStr(R.color.aikbd_keyboard_theme_0_nor_btn_color));
//        if ( mSpBtnColor == -1 )
//            mSpBtnColor = Color.parseColor(getColorStr(R.color.aikbd_keyboard_theme_0_sp_btn_color));
        mNorBtnColor = Color.parseColor(getColorStr(R.color.aikbd_keyboard_theme_0_nor_btn_color));
        mSpBtnColor = Color.parseColor(getColorStr(R.color.aikbd_keyboard_theme_0_sp_btn_color));
        kv.setKeyColor(mNorBtnColor);
        kv.setSpecialKeyColor(mSpBtnColor);
        kv.setKeyTextColor(mTxtId, mSpIconId, mOptTxtId);
        kv.setAllBtnAlpha(155);
        kv.setLabelPaint();
//        kv.setBackground(mNorSelector, mSpeSelector);
        kv.invalidateAllKey();

        SharedPreference.setInt(Keyboard_Empty_Activity.this, Common.PREF_KEYBOARD_BG_DRAWABLE, -1);
        SharedPreference.setString(Keyboard_Empty_Activity.this, Common.PREF_KEYBOARD_BG_GALLERY, "");
        SharedPreference.setString(Keyboard_Empty_Activity.this, Common.PREF_KEYBOARD_BG_URL, "");
        SharedPreference.setInt(Keyboard_Empty_Activity.this, Common.PREF_THEME_TXT_COLOR, mTxtId);
        SharedPreference.setInt(Keyboard_Empty_Activity.this, Common.PREF_THEME_TXT_COLOR_S, mOptTxtId);
        SharedPreference.setInt(Keyboard_Empty_Activity.this, Common.PREF_THEME_SP_ICON_COLOR, mSpIconId);
        SharedPreference.setInt(Keyboard_Empty_Activity.this, Common.PREF_KEYBOARD_BUTTON_ALPHA, 150);
        SharedPreference.setInt(Keyboard_Empty_Activity.this, Common.PREF_KEYBOARD_DARK_BUTTON_ALPHA, 150);
        SharedPreference.setInt(Keyboard_Empty_Activity.this, Common.PREF_KEYBOARD_BUTTON_COLOR, mNorBtnColor);
        SharedPreference.setInt(Keyboard_Empty_Activity.this, Common.PREF_KEYBOARD_SBUTTON_COLOR, mSpBtnColor);
        SharedPreference.setInt(Keyboard_Empty_Activity.this, Common.PREF_BTN_SELECTOR, mNorSelector);
        SharedPreference.setInt(Keyboard_Empty_Activity.this, Common.PREF_DARK_BTN_SELECTOR, mSpeSelector);
        kv.setLabelPaint();

        Intent themeChange = new Intent("THEME_CHANGE");
        sendBroadcast(themeChange);
        Intent getIntent = getIntent();
        boolean isFromSetting = false;
        if ( getIntent != null ) {
            isFromSetting = getIntent.getBooleanExtra("IS_FROM_SETTING", false);

        }

        Intent intent = new Intent(Keyboard_Empty_Activity.this, SettingSelectKeyboardActivity.class);
        KeyboardLogPrint.e("Keyboard_Empty_Activity isFromSetting : " + isFromSetting);
        intent.putExtra("IS_FROM_SETTING", isFromSetting);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }

    private String getColorStr(int colorId) {
        String strColorValue = "#ffffffff";
        try {
            int colorValue = getResources().getColor(colorId);
            strColorValue = "#"+ Integer.toHexString(colorValue);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return strColorValue;
    }

    private void setKeyboard(boolean setValue) {
        int kind = SharedPreference.getInt(this, Common.PREF_KEYBOARD_MODE);
        if (kind < 0) kind = 0;
        KeyboardLogPrint.e("Keyboard_Empty_Activity kind :: " + kind);
        if (kind == Common.MODE_CHUNJIIN) {
            LatinKeyboard sejong = new LatinKeyboard(this, R.xml.aikbd_sejong_setting);
            kv.setKeyboard(sejong);
            kv.setPreviewEnabled(false);
        } else if (kind == Common.MODE_QUERTY) {
            LatinKeyboard korean;
            korean = new LatinKeyboard(this, R.xml.aikbd_korean_n_setting);
            kv.setKeyboard(korean);
            if (setValue)
                kv.setPreviewEnabled(true);
            else
                kv.setPreviewEnabled(false);
        } else if (kind == Common.MODE_NARA) {
            LatinKeyboard nara = new LatinKeyboard(this, R.xml.aikbd_nara_setting);
            kv.setKeyboard(nara);
            kv.setPreviewEnabled(false);
        } else if (kind == Common.MODE_DAN) {
            LatinKeyboard dan = new LatinKeyboard(this, R.xml.aikbd_dan_setting);
            kv.setKeyboard(dan);
            if (setValue)
                kv.setPreviewEnabled(true);
            else
                kv.setPreviewEnabled(false);
        } else if (kind == Common.MODE_CHUNJIIN_PLUS) {
            LatinKeyboard sejongPlus = new LatinKeyboard(this, R.xml.aikbd_sejong_plus_setting);
            kv.setKeyboard(sejongPlus);
            kv.setPreviewEnabled(false);
        } else {
            LatinKeyboard sejong = new LatinKeyboard(this, R.xml.aikbd_sejong_setting);
            kv.setKeyboard(sejong);
            kv.setPreviewEnabled(false);
        }

        kv.setKeys();
        kv.changeConfig(0);
    }
}
