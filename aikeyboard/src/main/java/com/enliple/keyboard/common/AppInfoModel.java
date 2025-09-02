package com.enliple.keyboard.common;

import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2017-09-13.
 */

public class AppInfoModel {
    private String mPackageName = null;
    private String mAppName = null;
    private Drawable mIcon = null;
    private boolean mIsAdd = false;
    private boolean mIsDel = false;

    public void setPackageName(String name) {
        mPackageName = name;
    }

    public void setAppName(String name) {
        mAppName = name;
    }

    public void setIcon(Drawable icon) {
        mIcon = icon;
    }

    public void setIsAdd(boolean isAdd) {
        mIsAdd = isAdd;
    }

    public void setDel(boolean del) {
        mIsDel = del;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public String getAppName() {
        return mAppName;
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public boolean getIsAdd() {
        return mIsAdd;
    }

    public boolean getDel() {
        return mIsDel;
    }
}
