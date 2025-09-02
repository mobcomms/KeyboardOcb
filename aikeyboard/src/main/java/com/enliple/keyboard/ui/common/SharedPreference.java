package com.enliple.keyboard.ui.common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreference
{
    public static void initSharedPreference(Context context) {
        SharedPreferences pref = context.getSharedPreferences(context.getPackageName(), Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }
    public static void setString(Context context, String key, String value)
    {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor disSetEditor = sp.edit();
        disSetEditor.putString(key, value);
        disSetEditor.apply();
    }

    public static String getString(Context context, String key)
    {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Activity.MODE_MULTI_PROCESS);
        return sp.getString(key, "");
    }

    public static void setInt(Context context, String key, int value)
    {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor disSetEditor = sp.edit();
        disSetEditor.putInt(key, value);
        disSetEditor.apply();
    }

    public static int getInt(Context context, String key)
    {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Activity.MODE_MULTI_PROCESS);
        return sp.getInt(key, -1);
    }

    public static void setBoolean(Context context, String key, boolean value)
    {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor disSetEditor = sp.edit();
        disSetEditor.putBoolean(key, value);
        disSetEditor.apply();
    }

    public static boolean getBoolean(Context context, String key)
    {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Activity.MODE_MULTI_PROCESS);
        return sp.getBoolean(key, false);
    }

    public static boolean getTrueBoolean(Context context, String key)
    {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Activity.MODE_MULTI_PROCESS);
        return sp.getBoolean(key, true);
    }

    public static void setFloat(Context context, String key, float value)
    {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor disSetEditor = sp.edit();
        disSetEditor.putFloat(key, value);
        disSetEditor.apply();
    }

    public static float getFloat(Context context, String key)
    {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Activity.MODE_MULTI_PROCESS);
        return sp.getFloat(key,0.5f);
    }

    public static void setLong(Context context, String key, long value)
    {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor disSetEditor = sp.edit();
        disSetEditor.putLong(key, value);
        disSetEditor.apply();
    }

    public static long getLong(Context context, String key)
    {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Activity.MODE_MULTI_PROCESS);
        return sp.getLong(key,-1);
    }
}
