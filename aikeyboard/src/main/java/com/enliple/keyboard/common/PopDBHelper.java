package com.enliple.keyboard.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018-02-27.
 */

public class PopDBHelper extends SQLiteOpenHelper {

    private static String DATABASE_NAME = "aikbd_pop.db";
    private static String TABLE_ADINFO = "table_adinfo";
    private static final int DATABASE_VERSION = 1;
    private Context mContext;

    private static final String COL_SEQ = "seq";
    private static final String COL_LIST = "list";
    private static final String COL_POINT = "point";
    private static final String COL_TITLE = "title";
    private static final String COL_CONTENT = "content";
    private static final String COL_LINK = "link";
    private static final String COL_IMAGE = "image";
    private static final String COL_PCODE = "pcode";
    private static final String COL_SITECODE = "sitecode";
    private static final String COL_PRICE = "price";
    private static final String COL_ADTYPE = "adtype";
    private static final String COL_POINTGUBUN = "pointgubun";
    private static final String COL_GUBUN = "gubun";
    private static final String COL_LOGO = "logo";

    public PopDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String sql;

            sql = "create table IF NOT EXISTS " + TABLE_ADINFO + "(" + COL_SEQ + " integer primary key autoincrement, "
                    + COL_TITLE + " text default '', "
                    + COL_POINT + " text default '', "
                    + COL_CONTENT + " text default '', "
                    + COL_LINK + " text default '', "
                    + COL_IMAGE + " text default '', "
                    + COL_PCODE + " text default '', "
                    + COL_SITECODE + " text default '', "
                    + COL_PRICE + " text default '', "
                    + COL_GUBUN + " text default '', "
                    + COL_ADTYPE + " text default '', "
                    + COL_LOGO + " text default '', "
                    + COL_POINTGUBUN + " text default '');";
            db.execSQL(sql);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertPopList(ArrayList<PopADModel> list) {
        KeyboardLogPrint.e("insertPopList");
        SQLiteDatabase db = null;
        if ( list != null && list.size() > 0 ) {
            try {
                deletePopList();
                db = getWritableDatabase();
                ContentValues values = new ContentValues();
                for ( int i = 0 ; i < list.size() ; i ++ ) {
                    PopADModel model = list.get(i);
                    values.put(COL_POINT, model.getPoint());
                    values.put(COL_TITLE, model.getTitle());
                    values.put(COL_CONTENT, model.getContent());
                    values.put(COL_LINK, model.getLink());
                    values.put(COL_IMAGE, model.getImage());
                    values.put(COL_SITECODE, model.getSiteCode());
                    values.put(COL_PCODE, model.getPCode());
                    values.put(COL_ADTYPE, model.getAdType());
                    values.put(COL_PRICE, model.getPrice());
                    values.put(COL_POINTGUBUN, model.getPointGubun());
                    values.put(COL_GUBUN, model.getGubun());
                    values.put(COL_LOGO, model.getLogo());

                    db.insert(TABLE_ADINFO, null, values);
                }
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

    public boolean deletePopList() {
        KeyboardLogPrint.e("deletePopList");
        SQLiteDatabase db = null;
        boolean isRemoved = false;
        try {
            db = getWritableDatabase();
            String sql = String.format("DELETE FROM " + TABLE_ADINFO);
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

    public ArrayList<PopADModel> getPopAD() {
        SQLiteDatabase db = null;
        Cursor c = null;
        ArrayList<PopADModel> pArray = null;
        try {
            db = getWritableDatabase();
            c = db.rawQuery("SELECT * FROM " + TABLE_ADINFO, null);

            if (c != null) {
                if (c.getCount() > 0) {
                    pArray = new ArrayList<PopADModel>();

                    while (c.moveToNext()) {
                        String point = c.getString(c.getColumnIndex(COL_POINT));
                        String title = c.getString(c.getColumnIndex(COL_TITLE));
                        String content = c.getString(c.getColumnIndex(COL_CONTENT));
                        String link = c.getString(c.getColumnIndex(COL_LINK));
                        String image = c.getString(c.getColumnIndex(COL_IMAGE));
                        String pcode = c.getString(c.getColumnIndex(COL_PCODE));
                        String sitecode = c.getString(c.getColumnIndex(COL_SITECODE));
                        String adtype = c.getString(c.getColumnIndex(COL_ADTYPE));
                        String pointgubun = c.getString(c.getColumnIndex(COL_POINTGUBUN));
                        String price = c.getString(c.getColumnIndex(COL_PRICE));
                        String gubun = c.getString(c.getColumnIndex(COL_GUBUN));
                        String logo = c.getString(c.getColumnIndex(COL_LOGO));

                        PopADModel model = new PopADModel();
                        model.setPoint(point);
                        model.setTitle(title);
                        model.setContent(content);
                        model.setLink(link);
                        model.setImage(image);
                        model.setPCode(pcode);
                        model.setSiteCode(sitecode);
                        model.setAdType(adtype);
                        model.setPointGubun(pointgubun);
                        model.setPrice(price);
                        model.setGubun(gubun);
                        model.setLogo(logo);

                        pArray.add(model);
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
}
