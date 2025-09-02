package com.enliple.keyboard.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017-09-12.
 */

public class PointDBHelper extends SQLiteOpenHelper {
    private static String DATABASE_NAME = "aikbd_point.db";
    private static String TABLE_POINT = "table_point";
    private static String TABLE_MAX_POINT = "table_max_point";
    private static String TABLE_SAVE_POINT = "table_save_point";
    private static String TABLE_RANDOM_POINT = "table_random_point";

    private static final int DATABASE_VERSION = 1;
    private Context mContext;

    private static final String COL_SEQ = "seq";
    private static final String COL_POINT = "point";

    public PointDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String sql;

            sql = "create table IF NOT EXISTS " + TABLE_POINT + "(" + COL_SEQ + " integer primary key autoincrement, "
                    + COL_POINT + " integer default 0);";

            db.execSQL(sql);

            sql = "create table IF NOT EXISTS " + TABLE_MAX_POINT + "(" + COL_SEQ + " integer primary key autoincrement, "
                    + COL_POINT + " integer default 0);";

            db.execSQL(sql);

            sql = "create table IF NOT EXISTS " + TABLE_SAVE_POINT + "(" + COL_SEQ + " integer primary key autoincrement, "
                    + COL_POINT + " integer default 0);";

            db.execSQL(sql);

            sql = "create table IF NOT EXISTS " + TABLE_RANDOM_POINT + "(" + COL_SEQ + " integer primary key autoincrement, "
                    + COL_POINT + " integer default 0);";

            db.execSQL(sql);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insertRandomPoint(int point) {
        KeyboardLogPrint.e("about point insertSavePoint :: " + point);
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_POINT, point);
            db.insert(TABLE_RANDOM_POINT, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
                db = null;
            }
        }
    }

    public void deleteRandomPoint()
    {
        KeyboardLogPrint.e("about point deleteRandomPoint");
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try
        {
            db = getWritableDatabase();
            cursor = db.rawQuery("SELECT * FROM " + TABLE_RANDOM_POINT + " order by " + COL_SEQ + " desc", null);

            if ( cursor != null )
            {
                if ( cursor.getCount() > 0 )
                {
                    while( cursor.moveToNext() )
                    {
                        int seq = cursor.getInt(0);
                        String sql = String.format("DELETE FROM " + TABLE_RANDOM_POINT + " where " + COL_SEQ + " = " + seq);
                        db.execSQL(sql);
                        KeyboardLogPrint.e("deleteRandomPoint");
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

    public int getRandomPoint() {
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = getWritableDatabase();
            c = db.rawQuery("SELECT * FROM " + TABLE_RANDOM_POINT, null);
            int point = 0;
            if (c != null) {
                if (c.getCount() > 0) {
                    c.moveToNext();
                    point = c.getInt(c.getColumnIndex(COL_POINT));
                }
            }
            KeyboardLogPrint.e("about point getRandomPoint :: " + point);
            return point;
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

    public void insertSavePoint(int point) {
        KeyboardLogPrint.e("about point insertSavePoint :: " + point);
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_POINT, point);
            db.insert(TABLE_SAVE_POINT, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
                db = null;
            }
        }
    }

    public void deleteSavePoint()
    {
        KeyboardLogPrint.e("about point deleteSavePoint");
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try
        {
            db = getWritableDatabase();
            cursor = db.rawQuery("SELECT * FROM " + TABLE_SAVE_POINT + " order by " + COL_SEQ + " desc", null);

            if ( cursor != null )
            {
                if ( cursor.getCount() > 0 )
                {
                    while( cursor.moveToNext() )
                    {
                        int seq = cursor.getInt(0);
                        String sql = String.format("DELETE FROM " + TABLE_SAVE_POINT + " where " + COL_SEQ + " = " + seq);
                        db.execSQL(sql);
                        KeyboardLogPrint.e("deleteSavePoint");
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

    public int getSavePoint() {
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = getWritableDatabase();
            c = db.rawQuery("SELECT * FROM " + TABLE_SAVE_POINT, null);
            int point = 0;
            if (c != null) {
                if (c.getCount() > 0) {
                    c.moveToNext();
                    point = c.getInt(c.getColumnIndex(COL_POINT));
                }
            }
            KeyboardLogPrint.e("about point getSavePoint :: " + point);
            return point;
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



    public void insertPoint(int point) {
        KeyboardLogPrint.e("about point insertPoint :: " + point);
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_POINT, point);
            db.insert(TABLE_POINT, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
                db = null;
            }
        }
    }

    public void deletePoint()
    {
        KeyboardLogPrint.e("about point deletePoint");
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try
        {
            db = getWritableDatabase();
            cursor = db.rawQuery("SELECT * FROM " + TABLE_POINT + " order by " + COL_SEQ + " desc", null);

            if ( cursor != null )
            {
                if ( cursor.getCount() > 0 )
                {
                    while( cursor.moveToNext() )
                    {
                        int seq = cursor.getInt(0);
                        String sql = String.format("DELETE FROM " + TABLE_POINT + " where " + COL_SEQ + " = " + seq);
                        db.execSQL(sql);
                        KeyboardLogPrint.e("deletePoint");
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

    public int getPoint() {
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = getWritableDatabase();
            c = db.rawQuery("SELECT * FROM " + TABLE_POINT, null);
            int point = 0;
            if (c != null) {
                if (c.getCount() > 0) {
                    c.moveToNext();
                    point = c.getInt(c.getColumnIndex(COL_POINT));
                }
            }
            KeyboardLogPrint.e("about point getPoint :: " + point);
            return point;
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




    public void insertMaxPoint(int point) {
        KeyboardLogPrint.e("about point insertMaxPoint :: " + point);
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_POINT, point);
            db.insert(TABLE_MAX_POINT, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
                db = null;
            }
        }
    }

    public void deleteMaxPoint()
    {
        KeyboardLogPrint.e("about point deleteMaxPoint");
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try
        {
            db = getWritableDatabase();
            cursor = db.rawQuery("SELECT * FROM " + TABLE_MAX_POINT + " order by " + COL_SEQ + " desc", null);

            if ( cursor != null )
            {
                if ( cursor.getCount() > 0 )
                {
                    while( cursor.moveToNext() )
                    {
                        int seq = cursor.getInt(0);
                        String sql = String.format("DELETE FROM " + TABLE_MAX_POINT + " where " + COL_SEQ + " = " + seq);
                        db.execSQL(sql);
                        KeyboardLogPrint.e("deleteMaxPoint");
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

    public int getMaxPoint() {
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = getWritableDatabase();
            c = db.rawQuery("SELECT * FROM " + TABLE_MAX_POINT, null);
            int point = 0;
            if (c != null) {
                if (c.getCount() > 0) {
                    c.moveToNext();
                    point = c.getInt(c.getColumnIndex(COL_POINT));
                }
            }
            KeyboardLogPrint.e("about point getMaxPoint :: " + point);
            return point;
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
