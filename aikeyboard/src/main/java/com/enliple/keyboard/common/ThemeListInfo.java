package com.enliple.keyboard.common;

/**
 * Created by Administrator on 2017-11-15.
 */

public class ThemeListInfo {
    private String mName;
    private String mImage;
    private String mUnZipFileName;
    private String mCategory;
    private String mCommonDownloadUrl;
    private String mCustomDownloadUrl;
    private String mCommonUnZipFileName;
    private String mCustomUnZipFileName;
    private String mCommonZipFileName;
    private String mCustomZipFileName;

    public String getCommonUnZipFileName() {
        return mCommonUnZipFileName;
    }

    public void setCommonUnZipFileName(String mCommonUnZipFileName) {
        this.mCommonUnZipFileName = mCommonUnZipFileName;
    }

    public String getCustomUnZipFileName() {
        return mCustomUnZipFileName;
    }

    public void setCustomUnZipFileName(String mCustomUnZipFileName) {
        this.mCustomUnZipFileName = mCustomUnZipFileName;
    }


    public String getCommonZipFileName() {
        return mCommonZipFileName;
    }

    public void setCommonZipFileName(String mCommonZipFileName) {
        this.mCommonZipFileName = mCommonZipFileName;
    }

    public String getCustomZipFileName() {
        return mCustomZipFileName;
    }

    public void setCustomZipFileName(String mCustomZipFileName) {
        this.mCustomZipFileName = mCustomZipFileName;
    }

    public String getCommonDownloadUrl() {
        return mCommonDownloadUrl;
    }

    public void setCommonDownloadUrl(String mCommonDownloadUrl) {
        this.mCommonDownloadUrl = mCommonDownloadUrl;
    }

    public String getCustomDownloadUrl() {
        return mCustomDownloadUrl;
    }

    public void setCustomDownloadUrl(String mCustomDownloadUrl) {
        this.mCustomDownloadUrl = mCustomDownloadUrl;
    }

    public void setCategory(String cate) { mCategory = cate; }

    public String getCategory() { return mCategory; }

    public void setName(String name) {
        mName = name;
    }

    public void setImage(String image) {
        mImage = image;
    }

    public void setUnZipFileName(String unzipFileName) {
        mUnZipFileName = unzipFileName;
    }

    public String getName() {
        return mName;
    }

    public String getImage() {
        return mImage;
    }

    public String getUnZipFileName() {
        return mUnZipFileName;
    }
}
