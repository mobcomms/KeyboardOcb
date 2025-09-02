package com.enliple.keyboard.common;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;


/**
 * Created by Administrator on 2017-01-19.
 */

public class KeyboardLogPrint
{
    private static final String TAG = "AIKeyboard";
    public static boolean debug = false;
    // public static boolean log_debug = BuildConfig.DEBUG;
    public static boolean log_debug = false;
    private static boolean flag = false;
    public static void t(Context context, String str)
    {
        if ( log_debug )
            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

    public static void e(String str)
    {
        if ( flag )
            Log.e(TAG, str);
        /**if ( log_debug )
            Log.e(TAG, str);**/
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

    public static void setDebugMode(boolean val)
    {
        debug = val;
    }
}
