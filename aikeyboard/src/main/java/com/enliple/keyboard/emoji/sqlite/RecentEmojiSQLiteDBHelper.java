package com.enliple.keyboard.emoji.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.enliple.keyboard.common.KeyboardLogPrint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Administrator on 2017-03-10.
 */

public class RecentEmojiSQLiteDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "recent_emoji.db";
    private static final int DATABASE_VERSION = 1;
    private static String TABLE_EMOJI_CATEGORY = "table_recent_emoji";

    private static final String COL_SEQ = "seq";
    private static final String COL_TIME = "date";
    private static final String COL_EMOJI_UNICODE = "unicode";
    private static final int MAX_SAVE = 35;
    public RecentEmojiSQLiteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql;

        sql = "create table IF NOT EXISTS " + TABLE_EMOJI_CATEGORY + "(" + COL_SEQ + " integer primary key autoincrement, "
                + COL_TIME + " text default '', "
                + COL_EMOJI_UNICODE + " datetime default current_timestamp);";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<String> getRecentEmoji()
    {
        SQLiteDatabase db = null;
        Cursor c = null;
        ArrayList<String> array = new ArrayList<String>();
        try
        {
            db = getWritableDatabase();
//            c = db.rawQuery("SELECT * FROM " + TABLE_EMOJI_CATEGORY + " order by date desc", null);
            c = db.rawQuery("SELECT * FROM " + TABLE_EMOJI_CATEGORY + " ORDER BY " + COL_TIME + " DESC", null);
            KeyboardLogPrint.w("getRecentEmoji c.getCount :: " + c.getCount());
            if (c != null && c.getCount() > 0)
            {
                while(c.moveToNext())
                {
                    array.add(c.getString(c.getColumnIndex(COL_EMOJI_UNICODE)));
                    KeyboardLogPrint.e("when getRecentEmoji :: unicode :: " + c.getString(c.getColumnIndex(COL_EMOJI_UNICODE)));
                    KeyboardLogPrint.e("when getRecentEmoji :: time :: " + c.getString(c.getColumnIndex(COL_EMOJI_UNICODE)));
                }
            }
            c.close();
            db.close();
            db = null;

            return array;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        finally {
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

    public void insertEmojiUnicode(String unicode)
    {
        SQLiteDatabase db = null;
        Cursor c = null;
        try
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String sDate = dateFormat.format(date);
            db = getWritableDatabase();
            c = db.rawQuery("SELECT * FROM " + TABLE_EMOJI_CATEGORY + " WHERE " + COL_EMOJI_UNICODE + " = '" + unicode + "'", null);
            if ( c != null && c.getCount() > 0 )
            {
                String sql = "UPDATE " + TABLE_EMOJI_CATEGORY + " SET " + COL_TIME + " = '" + sDate + "' WHERE " + COL_EMOJI_UNICODE + " = '" + unicode + "'";
                db.execSQL(sql);
            }
            else
            {
                ContentValues values = new ContentValues();
                values.put(COL_EMOJI_UNICODE, unicode);
                values.put(COL_TIME, sDate);
                db.insert(TABLE_EMOJI_CATEGORY, null, values);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (c != null)
            {
                c.close();
                c = null;
            }
            if ( db != null )
            {
                db.close();
                db = null;
            }
        }
    }

    public void removeEmojiList() {
        SQLiteDatabase db = null;

        try {
            db = getWritableDatabase();
            String sql = "DELETE * FROM " + TABLE_EMOJI_CATEGORY;
            db.execSQL(sql);
            db.close();
            db = null;
        } catch (Exception e) {
            KeyboardLogPrint.e("removeEmojiList exception");
            e.printStackTrace();
        } finally {

            if (db != null) {
                db.close();
                db = null;
            }
        }
    }

    public int getEmojiSize() {
        SQLiteDatabase db = null;
        Cursor c = null;
        db = getWritableDatabase();
        try {
            c = db.rawQuery("SELECT * FROM " + TABLE_EMOJI_CATEGORY, null);

            if (c != null)
                return c.getCount();
            else
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
}
