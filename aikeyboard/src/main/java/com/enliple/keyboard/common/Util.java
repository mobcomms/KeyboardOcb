package com.enliple.keyboard.common;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Calendar;

/**
 * Created by shoppul-pc1 on 2017-03-22.
 */

public class Util {

    public static Drawable drawableFromUrl(String url) throws IOException {
        Bitmap x;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.connect();
        InputStream input = connection.getInputStream();

        x = BitmapFactory.decodeStream(input);
        return new BitmapDrawable(x);
    }

    public static Bitmap cropCenter(int height, Bitmap bmp) {
        int dimension = Math.min(bmp.getWidth(), bmp.getHeight());
        return ThumbnailUtils.extractThumbnail(bmp, dimension, height);
    }

    public static final String getMetaData(Context context, String metadataKey) {
        Object key = "";
        ApplicationInfo ai = null;
        try {

            ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (ai != null) {
                Bundle bundle = ai.metaData;
                if (bundle != null) {
                    key = bundle.get(metadataKey);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            KeyboardLogPrint.e("getMetaData() Exception! :: " +  e);
        }

        return key.toString();
    }

    public static Bitmap changeImageColor(Bitmap sourceBitmap, int color) {
        Bitmap resultBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth() - 1, sourceBitmap.getHeight() - 1);
        Paint p = new Paint();
        ColorFilter filter = new LightingColorFilter(color, 1);
        p.setColorFilter(filter);
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, p);
        return resultBitmap;
    }

    public static Drawable covertBitmapToDrawable(Context context, Bitmap bitmap) {
        Drawable d = new BitmapDrawable(context.getResources(), bitmap);
        return d;
    }

    public static Bitmap convertDrawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap pathToBitmap(String path, boolean isThumbnail) {
        Bitmap bitmap;
        File imgFile = new File(path);
        if(imgFile.exists()){

            bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        } else {
            try {
                URL url = new URL(path);
                bitmap = BitmapFactory.decodeStream(url.openStream());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        if(isThumbnail)
            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()/2, bitmap.getHeight()/2, false);

        return bitmap;
    }

    public static String getAppName(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            ApplicationInfo appInfo = pm.getApplicationInfo(ctx.getPackageName(), 0);
            String label = pm.getApplicationLabel(appInfo).toString();
            return label;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    public static String GetTodayDate() {
        DecimalFormat format = new DecimalFormat("00");
        Calendar cal = Calendar.getInstance();
        cal.add(cal.DATE, 0);
        String year = Integer.toString(cal.get(Calendar.YEAR));
        String month = format.format(cal.get(Calendar.MONTH) + 1);
        String day = format.format(cal.get(Calendar.DAY_OF_MONTH));
        return year + "/" + month + "/" + day;
    }

    public static String GetTodayDateDash() {
        DecimalFormat format = new DecimalFormat("00");
        Calendar cal = Calendar.getInstance();
        cal.add(cal.DATE, 0);
        String year = Integer.toString(cal.get(Calendar.YEAR));
        String month = format.format(cal.get(Calendar.MONTH) + 1);
        String day = format.format(cal.get(Calendar.DAY_OF_MONTH));
        return year + "-" + month + "-" + day;
    }
}
