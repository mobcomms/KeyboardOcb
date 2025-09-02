package com.enliple.keyboard.common;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import com.enliple.keyboard.models.BrandTabModel;
import com.enliple.keyboard.models.ClipboardModel;
import com.enliple.keyboard.ui.common.LogPrint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Administrator on 2017-09-12.
 */

public class AIKBD_DBHelper extends SQLiteOpenHelper {
    private static String DATABASE_NAME = "aikbd_khelper_ocb.db";
    private static String TABLE_MEMO = "table_memo";
    private static String TABLE_APPINFO = "table_appinfo";
    private static String TABLE_THEME = "table_theme";
    private static String TABLE_KWD = "table_kwd";
    private static String TABLE_CLIPBOARD = "table_clipboard";
    private static String TABLE_BRAND_INFO = "table_brand_info";
    private static String TABLE_FIRST_EXECUTE = "table_first_execute";

    private static String TABLE_KEYBOARD_KIND = "table_enkeyboard_kind";

    //    private static final int DATABASE_VERSION = 2; // 최초 업데이트 버전 1
    //private static final int DATABASE_VERSION = 3; // 키보드 이모티콘 탭 버튼을 광고노출 버튼으로 교체하면서 키워드를 저장해야할 필요가 생겨 키워드 저장 table을 만듬
    //private static final int DATABASE_VERSION = 4; // 클립보드 저장 table 생성
    private static final int DATABASE_VERSION = 7; // Brand info 저장 table 생성
    private Context mContext;

    private static final String COL_SEQ = "seq";
    private static final String COL_TITLE = "title";
    private static final String COL_MEMO = "memo";
    private static final String COL_BOOKMARK = "bookmark";
    private static final String COL_TIME = "time";
    private static final String COL_PACKAGE = "packageName";
    private static final String COL_THEME = "theme";

    private static final String COL_KWD = "kwd";
    private static final String COL_CLIPBOARD = "clipboard";

    private static final String COL_BRAND_LINK_URL = "linkUrl";
    private static final String COL_BRAND_ICON_URL = "iconUrl";

    private static final String COL_FIRST_EXECUTE = "firstExecute";

    private static final String COL_KEYBOARD_KIND = "enkeyboard_kind";

    private Lock lock;
    public AIKBD_DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    public interface Listener {
        void onDeleted();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String sql;

            sql = "create table IF NOT EXISTS " + TABLE_MEMO + "(" + COL_SEQ + " integer primary key autoincrement, "
                    + COL_TITLE + " text default '', "
                    + COL_MEMO + " text default '', "
                    + COL_BOOKMARK + " text default '', "
                    + COL_TIME + " text default '');";

            db.execSQL(sql);

            sql = "create table IF NOT EXISTS " + TABLE_APPINFO + "(" + COL_SEQ + " integer primary key autoincrement, "
                    + COL_PACKAGE + " text default '');";

            db.execSQL(sql);

            sql = "create table IF NOT EXISTS " + TABLE_THEME + "(" + COL_SEQ + " integer primary key autoincrement, "
                    + COL_THEME + " text default '');";

            db.execSQL(sql);

            sql = "create table IF NOT EXISTS " + TABLE_KWD + "(" + COL_SEQ + " integer primary key autoincrement, "
                    + COL_KWD + " text default '');";

            db.execSQL(sql);

            sql = "create table IF NOT EXISTS " + TABLE_CLIPBOARD + "(" + COL_SEQ + " integer primary key autoincrement, "
                    + COL_CLIPBOARD + " text default '');";

            db.execSQL(sql);

            sql = "create table IF NOT EXISTS " + TABLE_BRAND_INFO + "(" + COL_SEQ + " integer primary key autoincrement, "
                    + COL_BRAND_ICON_URL + " text default '', "
                    + COL_BRAND_LINK_URL + " text default '');";

            db.execSQL(sql);

            sql = "create table IF NOT EXISTS " + TABLE_FIRST_EXECUTE + "(" + COL_SEQ + " integer primary key autoincrement, "
                    + COL_FIRST_EXECUTE + " text default '');";

            db.execSQL(sql);

            sql = "create table IF NOT EXISTS " + TABLE_KEYBOARD_KIND + "(" + COL_SEQ + " integer primary key autoincrement, "
                    + COL_KEYBOARD_KIND + " integer default '');";

            db.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        KeyboardLogPrint.e("onUpdate AIKBD_DBHelper");
        if (oldVersion == 1 && newVersion == 2) {
            String sql = "create table IF NOT EXISTS " + TABLE_THEME + "(" + COL_SEQ + " integer primary key autoincrement, "
                    + COL_THEME + " text default '');";

            db.execSQL(sql);
        }

        if (oldVersion == 2 && newVersion == 3) {
            String sql = "create table IF NOT EXISTS " + TABLE_KWD + "(" + COL_SEQ + " integer primary key autoincrement, "
                    + COL_KWD + " text default '');";

            db.execSQL(sql);
        }

        if ( oldVersion == 3 && newVersion == 4 ) {
            String sql = "create table IF NOT EXISTS " + TABLE_CLIPBOARD + "(" + COL_SEQ + " integer primary key autoincrement, "
                    + COL_CLIPBOARD + " text default '');";

            db.execSQL(sql);
        }

        if ( oldVersion == 4 && newVersion == 5 ) {
            String sql = "create table IF NOT EXISTS " + TABLE_BRAND_INFO + "(" + COL_SEQ + " integer primary key autoincrement, "
                    + COL_BRAND_ICON_URL + " text default '', "
                    + COL_BRAND_LINK_URL + " text default '');";

            db.execSQL(sql);
        }

        if ( oldVersion == 5 && newVersion == 6 ) {
            String sql = "create table IF NOT EXISTS " + TABLE_FIRST_EXECUTE + "(" + COL_SEQ + " integer primary key autoincrement, "
                    + COL_FIRST_EXECUTE + " text default '');";

            db.execSQL(sql);
        }

        if ( oldVersion <= 6 && newVersion == 7 ) {
            String sql = "create table IF NOT EXISTS " + TABLE_KEYBOARD_KIND + "(" + COL_SEQ + " integer primary key autoincrement, "
                    + COL_KEYBOARD_KIND + " integer default '');";

            db.execSQL(sql);
        }
    }

    public void setFirstExecute() {
        LogPrint.d("skkim first execute setFirstExecute");
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_FIRST_EXECUTE, "Y");
            db.insert(TABLE_FIRST_EXECUTE, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
                db = null;
            }
        }
    }

    public boolean isFirstExecuteExist() {
        boolean isFirstExecuteExist = false;
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = getWritableDatabase();
            c = db.rawQuery("SELECT * FROM " + TABLE_FIRST_EXECUTE + " order by " + COL_SEQ + " desc", null);
            if (c != null) {
                LogPrint.d("skkim first execute isFirstExecuteExist cursor not null");
                if (c.getCount() > 0) {
                    LogPrint.d("skkim first execute isFirstExecuteExist cursor count over 0");
                    isFirstExecuteExist = true;
                } else {
                    LogPrint.d("skkim first execute isFirstExecuteExist cursor count less or 0");
                }
            } else {
                LogPrint.d("skkim first execute isFirstExecuteExist cursor null");
            }

            if ( !isFirstExecuteExist ) {
                setFirstExecute();
            }

            return isFirstExecuteExist;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
            if (db != null) {
                db.close();
                db = null;
            }
        }
    }

    public void insertBrandUrl(String linkUrl, String iconUrl) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_BRAND_ICON_URL, iconUrl);
            values.put(COL_BRAND_LINK_URL, linkUrl);
            db.insert(TABLE_BRAND_INFO, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
                db = null;
            }
        }
    }

    public BrandTabModel getBrandUrl() {
        BrandTabModel model = null;
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = getWritableDatabase();
            c = db.rawQuery("SELECT * FROM " + TABLE_BRAND_INFO + " order by " + COL_SEQ + " desc", null);
            if (c != null) {
                if (c.getCount() > 0) {
                    while (c.moveToNext()) {
                        String tLinkUrl = c.getString(c.getColumnIndex(COL_BRAND_LINK_URL));
                        String tImageUrl = c.getString(c.getColumnIndex(COL_BRAND_ICON_URL));
                        if ( !TextUtils.isEmpty(tLinkUrl) && !TextUtils.isEmpty(tImageUrl) ) {
                            model = new BrandTabModel();
                            model.setImage(tImageUrl);
                            model.setLink(tLinkUrl);
                            LogPrint.d("tImageUrl :: " + tImageUrl);
                            LogPrint.d("tLinkUrl :: " + tLinkUrl);
                        }
                    }
                }
            }
            return model;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
            if (db != null) {
                db.close();
                db = null;
            }
        }
    }

    public void deleteBrandUrl() {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            String sql = String.format("DELETE FROM " + TABLE_BRAND_INFO);
            db.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
                db = null;
            }
        }
    }

    public void insertClipboard(String clipboard) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_CLIPBOARD, clipboard);
            db.insert(TABLE_CLIPBOARD, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
                db = null;
            }
        }
    }

    public ArrayList<ClipboardModel> getClipboards() {
        SQLiteDatabase db = null;
        Cursor c = null;
        String clipboard = null;
        ArrayList<ClipboardModel> models = new ArrayList<>();
        try {
            db = getWritableDatabase();
            c = db.rawQuery("SELECT * FROM " + TABLE_CLIPBOARD + " order by " + COL_SEQ + " desc", null);

            if (c != null) {
                if (c.getCount() > 0) {
                    while (c.moveToNext()) {
                        ClipboardModel model = new ClipboardModel();
                        clipboard = c.getString(c.getColumnIndex(COL_CLIPBOARD));
                        model.setClipboard(clipboard);
                        models.add(model);
                    }
                }
            }
            return models;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
            if (db != null) {
                db.close();
                db = null;
            }
        }
    }


//        {
//            db = getWritableDatabase();
//            cursor = db.rawQuery("SELECT * FROM " + TABLE_THEME + " order by " + COL_SEQ + " desc", null);
//
//            if ( cursor != null )
//            {
//                if ( cursor.getCount() > 0 )
//                {
//                    while( cursor.moveToNext() )
//                    {
//                        int seq = cursor.getInt(0);
//                        String sql = String.format("DELETE FROM " + TABLE_THEME + " where " + COL_SEQ + " = " + seq);
//                        db.execSQL(sql);
//                        Log.e("TAG", "deleteTheme");
//                    }
//                }
//            }
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally
//        {
//            if (cursor != null)
//            {
//                cursor.close();
//                cursor = null;
//            }
//            if (db != null)
//            {
//                db.close();
//                db = null;
//            }
//        }

    public void deleteClipboards() {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            String sql = String.format("DELETE FROM " + TABLE_CLIPBOARD);
            db.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
                db = null;
            }
        }
    }

    public synchronized void deleteClipboard(String clipboard, Listener listener) {
        lock = new ReentrantLock();
        lock.lock();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        LogPrint.d("deleteClipboard");
        try {
            db = getWritableDatabase();
            cursor = db.rawQuery("SELECT * FROM " + TABLE_CLIPBOARD, null);
            if ( cursor != null && cursor.getCount() > 0 ) {
                while( cursor.moveToNext() ) {
                    String cb = cursor.getString(cursor.getColumnIndex(COL_CLIPBOARD));
                    if ( cb.equals(clipboard) ) {
                        String sql = String.format("DELETE FROM " + TABLE_CLIPBOARD + " where " + COL_CLIPBOARD + " = '" + cb + "'");
                        LogPrint.d("clip delete sql :: " + sql);
                        db.execSQL(sql);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if ( cursor != null ) {
                cursor.close();
                cursor = null;
            }

            if (db != null) {
                db.close();
                db = null;
            }
            if ( lock != null )
                lock.unlock();
            if ( listener != null )
                listener.onDeleted();
        }

/*
        if ( isExists(clipboard) ) {
            Log.e("TAG", "EXIST");
            SQLiteDatabase db = null;
            try {
                db = getWritableDatabase();
                db.delete(TABLE_CLIPBOARD, COL_CLIPBOARD + "=" + clipboard, null);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (db != null) {
                    db.close();
                    db = null;
                }
            }
        } else {
            Log.e("TAG", "not EXIST");
        }*/
    }

    public void insertKwd(String kwd) {
        KeyboardLogPrint.e("insertKwd");
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_KWD, kwd);
            db.insert(TABLE_KWD, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
                db = null;
            }
        }
    }

    public boolean deleteKwd() {
        SQLiteDatabase db = null;
        boolean isRemoved = false;
        try {
            db = getWritableDatabase();
            String sql = String.format("DELETE FROM " + TABLE_KWD);
            db.execSQL(sql);

            return isRemoved;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) {
                db.close();
                db = null;
            }
        }
    }

    public String getKwd() {
        SQLiteDatabase db = null;
        Cursor c = null;
        String theme = null;
        try {
            db = getWritableDatabase();
            c = db.rawQuery("SELECT * FROM " + TABLE_KWD + " order by " + COL_SEQ + " desc", null);

            if (c != null) {
                if (c.getCount() > 0) {
                    while (c.moveToNext()) {
                        theme = c.getString(c.getColumnIndex(COL_KWD));
                    }
                }
            }
            return theme;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
            if (db != null) {
                db.close();
                db = null;
            }
        }
    }

    public void insertTheme(String jsonStr) {
        LogPrint.e("insertTheme :: " + jsonStr);
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_THEME, jsonStr);
            db.insert(TABLE_THEME, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
                db = null;
            }
        }
    }

    public boolean isThemeExist() {
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = getWritableDatabase();
            c = db.rawQuery("SELECT * FROM " + TABLE_THEME, null);

            if (c != null && c.getCount() > 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
            if (db != null) {
                db.close();
                db = null;
            }
        }
    }

//    public void deleteTheme()
//    {
//        Log.e("TAG", "deleteTheme");
//        SQLiteDatabase db = null;
//        Cursor cursor = null;
//
//        try
//        {
//            db = getWritableDatabase();
//            cursor = db.rawQuery("SELECT * FROM " + TABLE_THEME + " order by " + COL_SEQ + " desc", null);
//
//            if ( cursor != null )
//            {
//                if ( cursor.getCount() > 0 )
//                {
//                    while( cursor.moveToNext() )
//                    {
//                        int seq = cursor.getInt(0);
//                        String sql = String.format("DELETE FROM " + TABLE_THEME + " where " + COL_SEQ + " = " + seq);
//                        db.execSQL(sql);
//                        Log.e("TAG", "deleteTheme");
//                    }
//                }
//            }
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally
//        {
//            if (cursor != null)
//            {
//                cursor.close();
//                cursor = null;
//            }
//            if (db != null)
//            {
//                db.close();
//                db = null;
//            }
//        }
//    }

    public boolean deleteTheme() {
        SQLiteDatabase db = null;
        boolean isRemoved = false;
        try {
            db = getWritableDatabase();
            String sql = String.format("DELETE FROM " + TABLE_THEME);
            db.execSQL(sql);

            return isRemoved;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) {
                db.close();
                db = null;
            }
        }
    }

    public String getTheme() {
        SQLiteDatabase db = null;
        Cursor c = null;
        String theme = null;
        try {
            db = getWritableDatabase();
            c = db.rawQuery("SELECT * FROM " + TABLE_THEME + " order by " + COL_SEQ + " desc", null);

            if (c != null) {
                if (c.getCount() > 0) {
                    KeyboardLogPrint.e("getTheme cursor count over 0");
                    while (c.moveToNext()) {
                        theme = c.getString(c.getColumnIndex(COL_THEME));
                    }
                } else
                    KeyboardLogPrint.e("getTheme cursor count <= 0 ");
            } else
                KeyboardLogPrint.e("getTheme cursor null");
            return theme;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
            if (db != null) {
                db.close();
                db = null;
            }
        }
    }


    public void insertAppInfo(String packageName) {
        KeyboardLogPrint.e("insertAppInfo :: packageName :: " + packageName);
        // db에 저장 요청된 값이 존재하면 추가하지 않는다.
        if (isAppExist(packageName))
            return;
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_PACKAGE, packageName);
            db.insert(TABLE_APPINFO, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
                db = null;
            }
        }
    }

    public boolean isAppExist(String packageName) {
        KeyboardLogPrint.e("isAppExist");
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = getWritableDatabase();
            cursor = db.rawQuery("SELECT * FROM " + TABLE_APPINFO + " WHERE " + COL_PACKAGE + " = '" + packageName + "'", null);

            if (cursor != null && cursor.getCount() > 0)
                return true;
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
            if (db != null) {
                db.close();
                db = null;
            }
        }
    }

    public boolean isAppExist() {
        KeyboardLogPrint.e("isAppExist");
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = getWritableDatabase();
            c = db.rawQuery("SELECT * FROM " + TABLE_APPINFO, null);

            if (c != null && c.getCount() > 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
            if (db != null) {
                db.close();
                db = null;
            }
        }
    }

    public void removeAppInfo(String packageName) {
        KeyboardLogPrint.e("removeAppInfo");
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = getWritableDatabase();
            cursor = db.rawQuery("SELECT * FROM " + TABLE_APPINFO + " WHERE " + COL_PACKAGE + " = '" + packageName + "'", null);

            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        String sql = String.format("DELETE FROM " + TABLE_APPINFO + " WHERE " + COL_PACKAGE + " = '" + packageName + "'");
                        db.execSQL(sql);
                        KeyboardLogPrint.e("removeAppInfo exec");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
            if (db != null) {
                db.close();
                db = null;
            }
        }
    }

    public ArrayList<String> getAppInfoOnlyPackageName() {
        SQLiteDatabase db = null;
        Cursor c = null;
        ArrayList<String> pArray = null;
        try {
            db = getWritableDatabase();
            c = db.rawQuery("SELECT * FROM " + TABLE_APPINFO, null);

            if (c != null) {
                if (c.getCount() > 0) {
                    pArray = new ArrayList<String>();

                    while (c.moveToNext()) {
                        String packageName = c.getString(c.getColumnIndex(COL_PACKAGE));
                        pArray.add(packageName);
                    }
                }
            }
            return pArray;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
            if (db != null) {
                db.close();
                db = null;
            }
        }
    }

    public ArrayList<AppInfoModel> getAppInfo() {
        SQLiteDatabase db = null;
        Cursor c = null;
        AppInfoModel model = null;
        ArrayList<AppInfoModel> modelArray = null;
        try {
            db = getWritableDatabase();
            c = db.rawQuery("SELECT * FROM " + TABLE_APPINFO, null);

            if (c != null) {
                if (c.getCount() > 0) {
                    modelArray = new ArrayList<AppInfoModel>();

                    while (c.moveToNext()) {
                        String packageName = c.getString(c.getColumnIndex(COL_PACKAGE));
                        Drawable icon = null;
                        String appName = "";
                        try {
                            icon = mContext.getPackageManager().getApplicationIcon(packageName);
                            appName = getAppName(packageName);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }

                        if (icon != null) {
                            model = new AppInfoModel();
                            model.setPackageName(packageName);
                            model.setAppName(appName);
                            model.setIcon(icon);
                            model.setIsAdd(false);
                            model.setDel(false);

                            modelArray.add(model);
                        }
                    }
                }
            }
            return modelArray;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
            if (db != null) {
                db.close();
                db = null;
            }
        }
    }

    public void insertMemo(String title, String memo, String time, String bookmark) {
        KeyboardLogPrint.e("insertMemo");
        SQLiteDatabase db = null;
        try {

            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_TITLE, title);
            values.put(COL_MEMO, memo);
            values.put(COL_BOOKMARK, bookmark);
            values.put(COL_TIME, time);
            db.insert(TABLE_MEMO, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
                db = null;
            }
        }
    }

    public boolean isMemoExist(String memo) {
        KeyboardLogPrint.e("isMemoExist");
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = getWritableDatabase();
            String q_query = "SELECT * FROM " + TABLE_MEMO + " WHERE " + COL_MEMO + " = ?";
            cursor = db.rawQuery(q_query, new String[]{memo});
//            cursor = db.rawQuery("SELECT * FROM " + TABLE_MEMO + " WHERE " + COL_MEMO + " = '" + memo + "'", null);

            if (cursor != null && cursor.getCount() > 0)
                return true;
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
            if (db != null) {
                db.close();
                db = null;
            }
        }
    }

//    public boolean isMemoExist() {
//        KeyboardLogPrint.e("isMemoExist");
//        SQLiteDatabase db = null;
//        Cursor c = null;
//        try {
//            db = getWritableDatabase();
//            c = db.rawQuery("SELECT * FROM " + TABLE_MEMO, null);
//
//            if (c != null && c.getCount() > 0) {
//                return true;
//            }
//            return false;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        } finally {
//            if (c != null) {
//                c.close();
//                c = null;
//            }
//            if (db != null) {
//                db.close();
//                db = null;
//            }
//        }
//    }

    public boolean removeMemoInfo(String memo, String time) {
        KeyboardLogPrint.e("removeMemoInfo");
        SQLiteDatabase db = null;
        Cursor cursor = null;
        boolean isRemoved = false;
        try {
            db = getWritableDatabase();
            String q_query = "SELECT * FROM " + TABLE_MEMO + " WHERE " + COL_MEMO + " = ? AND " + COL_TIME + " = '" + time + "'";
            cursor = db.rawQuery(q_query, new String[]{memo});
//            cursor = db.rawQuery("SELECT * FROM " + TABLE_MEMO + " WHERE " + COL_MEMO + " = '" + memo + "' AND " + COL_TIME + " = '" + time + "'", null);
            if (cursor != null) {
                KeyboardLogPrint.e("cursor is not null");
                if (cursor.getCount() > 0) {
                    KeyboardLogPrint.e("cursor count :: " + cursor.getCount());
                    while (cursor.moveToNext()) {
                        String sql = "DELETE FROM " + TABLE_MEMO + " WHERE " + COL_MEMO + " = ? AND " + COL_TIME + " = '" + time + "'";
                        cursor = db.rawQuery(sql, new String[]{memo});
//                        String sql = String.format("DELETE FROM " + TABLE_MEMO + " WHERE " + COL_MEMO + " = '" + memo + "' AND " + COL_TIME + " = '" + time + "'");
//                        db.execSQL(sql);
                        isRemoved = true;
                        KeyboardLogPrint.e("removeRewordInfo exec");
                    }
                } else {
                    KeyboardLogPrint.e("cursor count zero");
                }
            } else {
                KeyboardLogPrint.e("cursor null");
            }

            return isRemoved;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
            if (db != null) {
                db.close();
                db = null;
            }
        }
    }

    public ArrayList<MemoModel> getBookmarkMemo() {
        KeyboardLogPrint.w("getBookmarkMemo");
        SQLiteDatabase db = null;
        Cursor c = null;
        MemoModel model = null;
        ArrayList<MemoModel> modelArray = null;

        try {
            db = getWritableDatabase();
            c = db.rawQuery("SELECT * FROM " + TABLE_MEMO + " WHERE " + COL_BOOKMARK + " = 'Y'", null);

            if (c != null) {
                if (c.getCount() > 0) {
                    modelArray = new ArrayList<MemoModel>();

                    while (c.moveToNext()) {
                        String title = c.getString(c.getColumnIndex(COL_TITLE));
                        String memo = c.getString(c.getColumnIndex(COL_MEMO));
                        String bookmark = c.getString(c.getColumnIndex(COL_BOOKMARK));
                        String time = c.getString(c.getColumnIndex(COL_TIME));
                        model = new MemoModel();
                        model.setTitle(title);
                        model.setMemo(memo);
                        model.setBookMark(bookmark);
                        model.setAdd("N");
                        model.setTime(time);
                        modelArray.add(model);
                    }
                }
            }
            return modelArray;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
            if (db != null) {
                db.close();
                db = null;
            }
        }
    }

    public ArrayList<MemoModel> getMemo() {
        KeyboardLogPrint.w("getMemo");
        SQLiteDatabase db = null;
        Cursor c = null;
        MemoModel model = null;
        ArrayList<MemoModel> modelArray = null;
        try {
            db = getWritableDatabase();
            c = db.rawQuery("SELECT * FROM " + TABLE_MEMO + " order by " + COL_TIME + " desc", null);

            if (c != null) {
                if (c.getCount() > 0) {
                    KeyboardLogPrint.w("memo count :: " + c.getCount());
                    modelArray = new ArrayList<MemoModel>();

                    while (c.moveToNext()) {
                        String title = c.getString(c.getColumnIndex(COL_TITLE));
                        String memo = c.getString(c.getColumnIndex(COL_MEMO));
                        String bookmark = c.getString(c.getColumnIndex(COL_BOOKMARK));
                        String time = c.getString(c.getColumnIndex(COL_TIME));
                        model = new MemoModel();
                        model.setTitle(title);
                        model.setMemo(memo);
                        model.setBookMark(bookmark);
                        model.setAdd("N");
                        model.setTime(time);
                        modelArray.add(model);
                    }
                }
            }
            return modelArray;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
            if (db != null) {
                db.close();
                db = null;
            }
        }
    }

    public int getMemoSize() {
        SQLiteDatabase db = null;
        Cursor c = null;
        MemoModel model = null;
        ArrayList<MemoModel> modelArray = null;
        try {
            db = getWritableDatabase();
            c = db.rawQuery("SELECT * FROM " + TABLE_MEMO, null);

            if (c != null && c.getCount() > 0) {
                return c.getCount();
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
            if (db != null) {
                db.close();
                db = null;
            }
        }
    }

    public void toggleBookmark(String memo, String time) {
        KeyboardLogPrint.e("toggleBookmark");
        SQLiteDatabase db = null;
        Cursor cursor = null;
        KeyboardLogPrint.w("toggleBookmark memo : " + memo);
        KeyboardLogPrint.w("toggleBookmark time : " + time);
        try {
            db = getWritableDatabase();
            String q_query = "SELECT * FROM " + TABLE_MEMO + " WHERE " + COL_MEMO + " = ? AND " + COL_TIME + " = '" + time + "'";
            cursor = db.rawQuery(q_query, new String[]{memo});
//            cursor = db.rawQuery("SELECT * FROM " + TABLE_MEMO + " WHERE " + COL_MEMO + " = '" + memo + "' AND " + COL_TIME + " = '" + time + "'", null);

            if (cursor != null) {
                KeyboardLogPrint.w("cursor is not null");
                if (cursor.getCount() > 0) {
                    KeyboardLogPrint.w("cursor count > 0 ");
                    while (cursor.moveToNext()) {
                        String bookmark = cursor.getString(cursor.getColumnIndex(COL_BOOKMARK));
                        KeyboardLogPrint.w("origin book mark :: " + bookmark);
                        String toggle_bookmark = "N";
                        if ("Y".equals(bookmark)) {
                            toggle_bookmark = "N";
                        } else {
                            toggle_bookmark = "Y";
                        }
                        KeyboardLogPrint.w("changed book mark :: " + toggle_bookmark);
                        String sql = "UPDATE " + TABLE_MEMO + " SET " + COL_BOOKMARK + " = '" + toggle_bookmark + "' WHERE " + COL_MEMO + " = ? AND " + COL_TIME + " = '" + time + "'";
                        cursor = db.rawQuery(sql, new String[]{memo});
//                        String sql = "UPDATE " + TABLE_MEMO + " SET " + COL_BOOKMARK + " = '" + toggle_bookmark + "' WHERE " + COL_MEMO + " = '" + memo + "' AND " + COL_TIME + " = '" + time + "'";
//                        db.execSQL(sql);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
            if (db != null) {
                db.close();
                db = null;
            }
        }
    }

    private String getAppName(String packageName) {
        PackageManager pm = mContext.getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            ai = null;
        }
        return (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
    }

//    public void insertKeyboardKind(int kind) {
//        SQLiteDatabase db = null;
//        try {
//            db = getWritableDatabase();
//            ContentValues values = new ContentValues();
//            values.put(COL_KEYBOARD_KIND, kind);
//            db.insert(TABLE_KEYBOARD_KIND, null, values);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (db != null) {
//                db.close();
//                db = null;
//            }
//        }
//    }

    public void insertKeyboardKind(int kind) {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            db.beginTransaction();
            db.delete(TABLE_KEYBOARD_KIND, null, null);

            ContentValues values = new ContentValues();
            values.put(COL_KEYBOARD_KIND, kind);
            db.insert(TABLE_KEYBOARD_KIND, null, values);

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
                db = null;
            }
        }
    }

    public int getKeyboardkind() {
        int keyboardKind = -1;
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = getWritableDatabase();
            c = db.rawQuery("SELECT * FROM " + TABLE_KEYBOARD_KIND + " order by " + COL_SEQ + " desc", null);
            if (c != null) {
                if (c.getCount() > 0) {
                    do {
                        int columnIndex = c.getColumnIndex(COL_KEYBOARD_KIND);
                        LogPrint.d("columnIndex :: " + columnIndex);
                        if ( columnIndex >= 0 ) {
                            keyboardKind = c.getInt(0);
                        }
                    } while(c.moveToNext() );
                }
            }
            return keyboardKind;
        } catch (Exception e) {
            e.printStackTrace();
            return keyboardKind;
        } finally {
            if (c != null) {
                c.close();
                c = null;
            }
            if (db != null) {
                db.close();
                db = null;
            }
        }
    }

    public void deleteKeyboardKind() {
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            String sql = String.format("DELETE FROM " + TABLE_KEYBOARD_KIND);
            db.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
                db = null;
            }
        }
    }
}
