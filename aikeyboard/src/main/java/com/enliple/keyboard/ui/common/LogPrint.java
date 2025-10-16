package com.enliple.keyboard.ui.common;

import android.util.Log;

/**
 * Created by Administrator on 2017-10-09.
 */

public class LogPrint {
    private static final String TAG = "CashKeyboard";
    public static boolean debug = false;
    public static boolean log_debug = false;
    public static boolean flag = true;
    public static void e(String str)
    {
        if ( flag )
            Log.e(TAG, str);
    }

    public static void d(String str)
    {
        if ( flag )
            Log.d(TAG, str);
    }

    public static void i(String str)
    {
        if ( flag )
            Log.i(TAG, str);
    }

    public static void w(String str)
    {
        if ( flag )
            Log.w(TAG, str);
    }
}
