package com.enliple.keyboard.common;

/**
 * Created by Administrator on 2017-11-09.
 */

public class SiteModel {
    private String mName = "";
    private String mPackage = "";

    public void setName(String name) {
        mName = name;
    }

    public void setPackage(String packageName) {
        mPackage = packageName;
    }

    public String getName() {
        return mName;
    }

    public String getPackageName() {
        return mPackage;
    }
}
