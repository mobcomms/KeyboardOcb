package com.enliple.keyboard.mobonAD;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SPManager {
    public static final String KEY_APP_STATE_FLAG = "SpManager.KEY_APP_STATE_FLAG";
    public static final int KEY_APP_STATE_START = 0;
    public static final int KEY_APP_STATE_DESTROY = 1;

    public static String getString(Context context, String key) {
        try {
            SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
            return sp.getString(key, "");
        } catch (Exception e) {
            return "";
        }
    }

    public static int getInteger(Context context, String key) {
        try {
            SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
            return sp.getInt(key, 0);
        } catch (Exception e) {
            return 0;
        }
    }

    public static float getFloat(Context context) {
        try {
            SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
            return sp.getFloat(MobonKey.ENDING_POPPUP_DIM_ALPHA, -1f);
        } catch (Exception e) {
            return -1f;
        }
    }

    public static boolean getBoolean(Context context, String key) {
        try {
            SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
            return sp.getBoolean(key, false);
        } catch (Exception e) {
            return false;
        }

    }

    public static void setLong(Context context, String key, long value) {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        SharedPreferences.Editor disSetEditor = sp.edit();
        disSetEditor.putLong(key, value);
        disSetEditor.commit();
    }


    public static void setString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        SharedPreferences.Editor disSetEditor = sp.edit();
        disSetEditor.putString(key, value);
        disSetEditor.commit();
    }

    public static void setString(Context context, String packageName, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(packageName, Activity.MODE_PRIVATE);
        SharedPreferences.Editor disSetEditor = sp.edit();
        disSetEditor.putString(key, value);
        disSetEditor.commit();
    }

    public static long getLong(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        return sp.getLong(key, 0);
    }

    public static void setString(Context context, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        SharedPreferences.Editor disSetEditor = sp.edit();
        disSetEditor.putString(key, String.valueOf(value));
        disSetEditor.commit();
    }

    public static void setString(Context context, String packageName, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(packageName, Activity.MODE_PRIVATE);
        SharedPreferences.Editor disSetEditor = sp.edit();
        disSetEditor.putString(key, String.valueOf(value));
        disSetEditor.commit();
    }

    public static void setInteger(Context context, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        SharedPreferences.Editor disSetEditor = sp.edit();
        disSetEditor.putInt(key, value);
        disSetEditor.commit();
    }

    public static void setInteger(Context context, String packageName, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(packageName, Activity.MODE_PRIVATE);
        SharedPreferences.Editor disSetEditor = sp.edit();
        disSetEditor.putInt(key, value);
        disSetEditor.commit();
    }

    public static void setFloat(Context context, String packageName, float value) {
        SharedPreferences sp = context.getSharedPreferences(packageName, Activity.MODE_PRIVATE);
        SharedPreferences.Editor disSetEditor = sp.edit();
        disSetEditor.putFloat(MobonKey.ENDING_POPPUP_DIM_ALPHA, value);
        disSetEditor.commit();
    }

    public static void setBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        SharedPreferences.Editor disSetEditor = sp.edit();
        disSetEditor.putBoolean(key, value);
        disSetEditor.commit();
    }

    public static void setBoolean(Context context, String packageName, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(packageName, Activity.MODE_PRIVATE);
        SharedPreferences.Editor disSetEditor = sp.edit();
        disSetEditor.putBoolean(key, value);
        disSetEditor.commit();
    }






}
