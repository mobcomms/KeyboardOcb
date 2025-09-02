package com.enliple.keyboard.ui.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017-10-16.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static String DATABASE_NAME = "paykeyboard_infos.db";
    private static final int DATABASE_VERSION = 1;

    private static String TABLE_USER_ID = "table_user_id";
    private static String TABLE_REWORD_INSTALL_INFO = "table_reword_install_info";
    private static String TABLE_ADPOPORN_INSTALL_INFO = "table_adpopcorn_install_info";

    public static final String COL_SEQ = "seq";
    public static final String COL_LIST_CODE = "list_code";
    public static final String COL_MID = "mid";
    public static final String COL_U_KEY = "u_key";
    public static final String COL_AD_CUS = "ad_cus";
    public static final String COL_S = "account";

    public static final String COL_DESCRIPTION = "description";
    public static final String COL_CAMPAIGNDESCRIPTION = "campaignDescription";
    public static final String COL_CAMPAIGNKEY = "campaignKey";
    public static final String COL_CAMPAIGNTYPE = "campaignType";
    public static final String COL_LANDSCAPEIMAGE = "landscapeImage";
    public static final String COL_PORTRAITIMAGE = "portraitImage";
    public static final String COL_LISTICON = "listIcon";
    public static final String COL_PACKAGENAME = "packageName";
    public static final String COL_PURCHASE = "purchase";
    public static final String COL_REDIRECTURL = "redirectUrl";
    public static final String COL_REWORDQUANTITY = "rewordQuantity";
    public static final String COL_TITLE = "title";
    public static final String COL_DATE = "date";

    public static final String COL_USER_ID = "user_id";
    public static final String COL_USER_ID_GUBUN = "user_id_gubun";
    public static final String COL_USER_DEVICE_ID = "user_device_id";

    private Context mContext;
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        try
        {
            String sql;

            sql = "create table IF NOT EXISTS " + TABLE_REWORD_INSTALL_INFO + "(" + COL_SEQ + " integer primary key autoincrement, "
                    + COL_LIST_CODE + " text default '', "
                    + COL_MID + " text default '', "
                    + COL_U_KEY + " text default '', "
                    + COL_AD_CUS + " text default '', "
                    + COL_S + " text default '');";

            db.execSQL(sql);

            sql = "create table IF NOT EXISTS " + TABLE_ADPOPORN_INSTALL_INFO + "(" + COL_SEQ + " integer primary key autoincrement, "
                    + COL_DESCRIPTION + " text default '', "
                    + COL_CAMPAIGNDESCRIPTION + " text default '', "
                    + COL_CAMPAIGNKEY + " text default '', "
                    + COL_CAMPAIGNTYPE + " text default '', "
                    + COL_LANDSCAPEIMAGE + " text default '', "
                    + COL_PORTRAITIMAGE + " text default '', "
                    + COL_LISTICON + " text default '', "
                    + COL_PACKAGENAME + " text default '', "
                    + COL_PURCHASE + " text default '', "
                    + COL_REDIRECTURL + " text default '', "
                    + COL_REWORDQUANTITY + " text default '', "
                    + COL_DATE + " text default '', "
                    + COL_TITLE + " text default '');";

            db.execSQL(sql);

            sql = "create table IF NOT EXISTS " + TABLE_USER_ID + "(" + COL_SEQ + " integer primary key autoincrement, "
                    + COL_USER_DEVICE_ID + " text default '', "
                    + COL_USER_ID_GUBUN + " text default '', "
                    + COL_USER_ID + " text default '');";

            db.execSQL(sql);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }

    public void insertUserId(String userId, String gubun, String deviceId)
    {
        LogPrint.d("insertUserId userId : " + userId);
        SQLiteDatabase db = null;

        try
        {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_USER_ID, userId);
            values.put(COL_USER_ID_GUBUN, gubun);
            values.put(COL_USER_DEVICE_ID, deviceId);

            db.insert(TABLE_USER_ID, null, values);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (db != null)
            {
                db.close();
                db = null;
            }
        }
    }

    public boolean isUserIdExist()
    {
        SQLiteDatabase db = null;
        Cursor c = null;
        boolean isUserIdExist = false;
        try
        {
            db = getWritableDatabase();
            c = db.rawQuery("SELECT * FROM " + TABLE_USER_ID , null);

            if ( c != null && c.getCount() > 0 )
            {
                isUserIdExist = true;
            }
            LogPrint.w("isUserIdExist :: " + isUserIdExist);
            return isUserIdExist;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        finally
        {
            if (c != null)
            {
                c.close();
                c = null;
            }
            if (db != null)
            {
                db.close();
                db = null;
            }
        }
    }

    public UserIdModel getUserId()
    {
        LogPrint.e("getUserId");
        SQLiteDatabase db = null;
        Cursor c = null;
        try
        {
            db = getWritableDatabase();
            c = db.rawQuery("SELECT * FROM " + TABLE_USER_ID, null);

            if ( c != null && c.getCount() > 0 )
            {
                c.moveToFirst();
                String userId;
                String gubun;
                String deviceId;
                userId = c.getString(c.getColumnIndex(COL_USER_ID));
                gubun = c.getString(c.getColumnIndex(COL_USER_ID_GUBUN));
                deviceId = c.getString(c.getColumnIndex(COL_USER_DEVICE_ID));
                UserIdModel model = new UserIdModel();
                model.setUserId(userId);
                model.setGubun(gubun);
                model.setDeviceId(deviceId);
                LogPrint.w("userId :: " + userId);
                LogPrint.w("gubun :: " + gubun);
                LogPrint.w("deviceId :: " + deviceId);
                return model;
            }
            return null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        finally
        {
            if (c != null)
            {
                c.close();
                c = null;
            }
            if (db != null)
            {
                db.close();
                db = null;
            }
        }
    }
}
