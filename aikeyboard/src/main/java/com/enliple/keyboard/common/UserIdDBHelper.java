package com.enliple.keyboard.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

/**
 * Created by Administrator on 2017-10-30.
 */

// point
public class UserIdDBHelper extends SQLiteOpenHelper {
    private static String DATABASE_NAME = "aikbd_user_info.db";
    private static String TABLE_USER_INFO = "table_userinfo";
    private static final int DATABASE_VERSION = 1;
    private Context mContext;

    private static final String COL_SEQ = "seq";
    private static final String COL_ID = "userId";
    private static final String COL_DEVICEID = "deviceId";
    private static final String COL_GUBUN = "gubun";

    public UserIdDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String sql;

            sql = "create table IF NOT EXISTS " + TABLE_USER_INFO + "(" + COL_SEQ + " integer primary key autoincrement, "
                    + COL_ID + " text default '', "
                    + COL_DEVICEID + " text default '', "
                    + COL_GUBUN + " text default '');";

            db.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insertUserInfo(KeyboardUserIdModel model) {
        KeyboardLogPrint.e("insertUserInfo :: " + model.getUserId());
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_ID, model.getUserId());
            values.put(COL_DEVICEID, model.getDeviceId());
            values.put(COL_GUBUN, model.getGubun());
            db.insert(TABLE_USER_INFO, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
                db = null;
            }
        }
    }

    public void deleteUserInfo()
    {
        KeyboardLogPrint.e("deleteUserInfo");
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try
        {
            db = getWritableDatabase();
            cursor = db.rawQuery("SELECT * FROM " + TABLE_USER_INFO + " order by " + COL_SEQ + " desc", null);

            if ( cursor != null )
            {
                if ( cursor.getCount() > 0 )
                {
                    while( cursor.moveToNext() )
                    {
                        int seq = cursor.getInt(0);
                        String sql = String.format("DELETE FROM " + TABLE_USER_INFO + " where " + COL_SEQ + " = " + seq);
                        db.execSQL(sql);
                        KeyboardLogPrint.e("deleteUserInfo");
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (cursor != null)
            {
                cursor.close();
                cursor = null;
            }
            if (db != null)
            {
                db.close();
                db = null;
            }
        }
    }

    public KeyboardUserIdModel getUserInfo() {
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = getWritableDatabase();
            c = db.rawQuery("SELECT * FROM " + TABLE_USER_INFO, null);
            String userId = "";
            String deviceId = "";
            String gubun = "";
            KeyboardUserIdModel model = new KeyboardUserIdModel();
            if (c != null) {
                if (c.getCount() > 0) {
                    c.moveToNext();
                    userId = c.getString(c.getColumnIndex(COL_ID));
                    deviceId = c.getString(c.getColumnIndex(COL_DEVICEID));
                    gubun = c.getString(c.getColumnIndex(COL_GUBUN));
                    if (TextUtils.isEmpty(userId) && TextUtils.isEmpty(deviceId) && TextUtils.isEmpty(gubun) )
                        return null;
                    model.setUserId(userId);
                    model.setDeviceId(deviceId);
                    model.setGubun(gubun);
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
}
