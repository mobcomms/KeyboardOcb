package com.enliple.keyboard.common;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by Administrator on 2018-01-22.
 */

public class ADViewModel {
    private ImageView mView;
    private Bitmap mBitmap;
    private String mPath;

    public void setPath(String path) {
        mPath = path;
    }

    public void setView(ImageView view) {
        mView = view;
    }

    public void setBitmap(Bitmap bm) {
        mBitmap = bm;
    }

    public String getPath() { return mPath; }

    public ImageView getView() {
        return mView;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }
}
