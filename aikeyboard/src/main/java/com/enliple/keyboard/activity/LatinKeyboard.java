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
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.util.Log;
import android.view.inputmethod.EditorInfo;

import com.enliple.keyboard.R;
import com.enliple.keyboard.common.Common;
import com.enliple.keyboard.common.KeyboardLogPrint;
import com.enliple.keyboard.common.ThemeManager;
import com.enliple.keyboard.common.ThemeModel;
import com.enliple.keyboard.ui.common.LogPrint;

@SuppressLint("InlinedApi")
@SuppressWarnings("unused")
public class LatinKeyboard extends Keyboard {
    private Drawable storedEnter;
    private Context mContext = null;
    private Key mEnterKey;
    private int mResId = 0;
    private int height;

    public LatinKeyboard(Context context, int xmlLayoutResId) {
        super(context, xmlLayoutResId);
        mContext = context;
        mResId = xmlLayoutResId;
        height = super.getHeight();
    }

    public LatinKeyboard(Context context, int layoutTemplateResId,
                         CharSequence characters, int columns, int horizontalPadding) {
        super(context, layoutTemplateResId, characters, columns, horizontalPadding);
        mContext = context;
    }

    @Override
    protected Key createKeyFromXml(Resources res, Row parent, int x, int y,
                                   XmlResourceParser parser) {
        Key key = new LatinKey(res, parent, x, y, parser);
        if (key.codes[0] == 10) {
            mEnterKey = key;
        }
        return key;
    }

    /**
     * This looks at the ime options given by the current editor, to set the
     * appropriate label on the keyboard's enter key (if it has one).
     */
    void setImeOptions(Resources res, int options) {
        if (mEnterKey == null) {
            return;
        } else {}
        ThemeModel model = ThemeManager.GetThemeModel(mContext,5);
        Drawable enter;
        Drawable searchEnter;
        if ( model != null ) {
            enter = ThemeManager.GetDrawableFromPath(model.getEnterImg());
            searchEnter = ThemeManager.GetDrawableFromPath(model.getKeySearchEnter());
        } else {
            enter = res.getDrawable(R.drawable.aikbd_sym_keyboard_return);
            searchEnter = res.getDrawable(R.drawable.aikbd_sym_keyboard_search);
        }

//        int option = (options & (EditorInfo.IME_MASK_ACTION | EditorInfo.IME_FLAG_NO_ENTER_ACTION));
//        KeyboardLogPrint.e("setImeOption option :: " + option);
//        if ( option == EditorInfo.IME_ACTION_SEARCH ) {
//            mEnterKey.iconPreview = null;
//            mEnterKey.icon = searchEnter;
//        } else {
//            mEnterKey.iconPreview = null;
//            mEnterKey.icon = enter;
//        }

//        if ( option == EditorInfo.IME_ACTION_GO ) {
//            mEnterKey.iconPreview = null;
//            mEnterKey.icon = enter;
//        } else if ( option == EditorInfo.IME_ACTION_NEXT ) {
//            mEnterKey.iconPreview = null;
//            mEnterKey.icon = enter;
//        } else if ( option == EditorInfo.IME_ACTION_SEARCH ) {
//            mEnterKey.iconPreview = null;
//            mEnterKey.icon = searchEnter;
//        } else if ( option == EditorInfo.IME_ACTION_SEND ) {
//            mEnterKey.iconPreview = null;
//            mEnterKey.icon = enter;
//        } else {
//            mEnterKey.iconPreview = null;
//            mEnterKey.icon = enter;
//        }




        switch (options & (EditorInfo.IME_MASK_ACTION | EditorInfo.IME_FLAG_NO_ENTER_ACTION)) {
            case EditorInfo.IME_ACTION_GO:
                mEnterKey.iconPreview = null;
                mEnterKey.icon = enter;
                break;
            case EditorInfo.IME_ACTION_NEXT:
                mEnterKey.iconPreview = null;
                mEnterKey.icon = enter;
                break;
            case EditorInfo.IME_ACTION_SEARCH:
                mEnterKey.iconPreview = null;
                mEnterKey.icon = searchEnter;
                break;
            case EditorInfo.IME_ACTION_SEND:
                mEnterKey.iconPreview = null;
                mEnterKey.icon = enter;
                break;
            default:
                mEnterKey.iconPreview = null;
                mEnterKey.icon = enter;
                break;
        }
    }

    public void setOriginEnter(Drawable dr) {
        storedEnter = dr;
    }

    public void setEnter(Resources res, boolean isSet) {
        if ( isSet ) {
            ThemeModel model = ThemeManager.GetThemeModel(mContext,5);
            Drawable enter;
            Drawable searchEnter;
            if ( model != null ) {
                searchEnter = ThemeManager.GetDrawableFromPath(model.getKeySearchEnter());
            } else {
                searchEnter = res.getDrawable(R.drawable.aikbd_sym_keyboard_search);
            }
            mEnterKey.icon = searchEnter;
        } else {
            if ( storedEnter != null ) {
                mEnterKey.icon = storedEnter;
                setOriginEnter(null);
            }
        }
    }

    static class LatinKey extends Key {

        public LatinKey(Resources res, Row parent, int x, int y, XmlResourceParser parser) {
            super(res, parent, x, y, parser);
        }

        /**
         * Overriding this aikbd_method so that we can reduce the target area for the key that
         * closes the keyboard.
         */
        @Override
        public boolean isInside(int x, int y) {
            return super.isInside(x, codes[0] == KEYCODE_CANCEL ? y - 10 : y);
        }
    }

    public void changeKeyHeight(double height_modifier) {
        int height = 0;

        for (Key key : getKeys()) {
            key.height *= height_modifier;
            key.y *= height_modifier;
            height = key.height;
        }
        setKeyHeight(height);
        getNearestKeys(0, 0); //somehow adding this fixed a weird bug where bottom row keys could not be pressed if keyboard height is too tall.. from the Keyboard source code seems like calling this will recalculate some values used in keypress detection calculation
    }

    @Override
    public int getKeyHeight() {
        return super.getKeyHeight();
    }


    @Override
    public int getVerticalGap() {
        return super.getVerticalGap();
    }

    public void setHeight(int newHeight) {
        height = newHeight;
    }

    public int getOrgHeight(){
        return height;
    }
    // getVerticalGap 에 * 4 -> * 5, * 3 -> * 4 변경
    @Override
    public int getHeight() {
        boolean isSetNum = SharedPreference.getBoolean(mContext, Common.PREF_QWERTY_NUM_SETTING);
        double heightResult = height;
        if (mResId != R.xml.aikbd_emoji && mResId != R.xml.aikbd_emoticon) {
            if (mResId == R.xml.aikbd_korean_n || mResId == R.xml.aikbd_qwerty_n || mResId == R.xml.aikbd_korean_n_setting || mResId == R.xml.aikbd_symbol_q_1 || mResId == R.xml.aikbd_symbol_q_2) {
                heightResult = heightResult * 5;
                heightResult += getVerticalGap() * 5;
            } else {
                heightResult = heightResult * 4;
                heightResult += getVerticalGap() * 4;
            }
        } else {

        }
        return (int) heightResult;
    }
}
