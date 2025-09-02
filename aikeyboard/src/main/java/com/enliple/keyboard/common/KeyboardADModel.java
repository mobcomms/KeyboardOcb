package com.enliple.keyboard.common;

/**
 * Created by Administrator on 2018-01-18.
 */

public class KeyboardADModel {
    private String mTitle;
    private String mContent;
    private String mPrice;
    private String mPoint;
    private String mLogo;
    private String mImg;
    private String mLink;
    private String mGubun;
    private String mPkg;

    public void setPkg(String pkg) { mPkg = pkg; }

    public void setTitle(String val) {
        mTitle = val;
    }

    public void setContent(String val) {
        mContent = val;
    }

    public void setPrice(String val) {
        mPrice = val;
    }

    public void setPoint(String val) {
        mPoint = val;
    }

    public void setLogo(String val) {
        mLogo = val;
    }

    public void setImg(String val) {
        mImg = val;
    }

    public void setLink(String val) {
        mLink = val;
    }

    public void setGubun(String val) {
        mGubun = val;
    }

    public String getPkg() { return mPkg; }
    public String getTitle() { return mTitle; }
    public String getContent() { return mContent; }
    public String getPrice() { return mPrice; }
    public String getPoint() { return mPoint; }
    public String getLogo() { return mLogo; }
    public String getImg() { return mImg; }
    public String getLink() { return mLink; }
    public String getGubun() { return mGubun; }
}
