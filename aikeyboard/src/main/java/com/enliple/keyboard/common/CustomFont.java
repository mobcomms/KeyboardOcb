package com.enliple.keyboard.common;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.widget.TextView;

import com.enliple.keyboard.ui.common.LogPrint;

import java.util.Random;

public class CustomFont {
    public static final int FONT_TYPE_MEDIUM = 0;
    public static final int FONT_TYPE_BOLD = 1;
    public static final int FONT_TYPE_SEMI_BOLD = 2;
    public static final int FONT_TYPE_REGULAR = 3;

    public static void SetFont(Context context, TextView view, int type ) {
        String path = "font/subset-Pretendard-Regular.ttf";
        if ( type == FONT_TYPE_SEMI_BOLD ) {
            path = "font/subset-Pretendard-SemiBold.ttf";
        } else if ( type == FONT_TYPE_BOLD ) {
            path = "font/subset-Pretendard-Bold.ttf";
        } else if ( type == FONT_TYPE_MEDIUM ) {
            path = "font/subset-Pretendard-Medium.ttf";
        } else if ( type == FONT_TYPE_REGULAR ) {
            path = "font/subset-Pretendard-Regular.ttf";
        }
        AssetManager assetManager = context.getAssets();
        Typeface customFont = Typeface.createFromAsset(assetManager, path);
        view.setTypeface(customFont);
    }
}
