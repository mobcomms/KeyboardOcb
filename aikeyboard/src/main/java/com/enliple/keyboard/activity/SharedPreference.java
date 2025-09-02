package com.enliple.keyboard.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreference
{
    public static void setString(Context context, String key, String value)
    {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor disSetEditor = sp.edit();
        disSetEditor.putString(key, value);
        disSetEditor.apply();
    }

    public static void setStringCommit(Context context, String key, String value)
    {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor disSetEditor = sp.edit();
        disSetEditor.putString(key, value);
        disSetEditor.commit();
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

    public static void setIntCommit(Context context, String key, int value)
    {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor disSetEditor = sp.edit();
        disSetEditor.putInt(key, value);
        disSetEditor.commit();
    }

//    public static int getInt(Context context, String key, int default_value)
//    {
//        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Activity.MODE_MULTI_PROCESS);
//        return sp.getInt(key, default_value);
//    }

    public static int getInt(Context context, String key)
    {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Activity.MODE_MULTI_PROCESS);
        return sp.getInt(key, -1);
    }

    public static int getZeroInt(Context context, String key)
    {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Activity.MODE_MULTI_PROCESS);
        return sp.getInt(key, 0);
    }

    public static void setBoolean(Context context, String key, boolean value)
    {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor disSetEditor = sp.edit();
        disSetEditor.putBoolean(key, value);
        disSetEditor.apply();
    }

    public static void setBooleanCommit(Context context, String key, boolean value)
    {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor disSetEditor = sp.edit();
        disSetEditor.putBoolean(key, value);
        disSetEditor.commit();
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

    public static void setFloatCommit(Context context, String key, float value)
    {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor disSetEditor = sp.edit();
        disSetEditor.putFloat(key, value);
        disSetEditor.commit();
    }

    public static float getFloat(Context context, String key)
    {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Activity.MODE_MULTI_PROCESS);
//        return sp.getFloat(key,0.5f);
        return sp.getFloat(key,-1);
    }

    public static void setLong(Context context, String key, long value)
    {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor disSetEditor = sp.edit();
        disSetEditor.putLong(key, value);
        disSetEditor.apply();
    }

    public static void setLongCommit(Context context, String key, long value)
    {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor disSetEditor = sp.edit();
        disSetEditor.putLong(key, value);
        disSetEditor.commit();
    }

    public static long getLong(Context context, String key)
    {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Activity.MODE_MULTI_PROCESS);
        return sp.getLong(key,-1);
    }
}
