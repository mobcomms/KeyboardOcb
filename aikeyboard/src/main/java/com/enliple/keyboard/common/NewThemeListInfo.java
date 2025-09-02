package com.enliple.keyboard.common;

public class NewThemeListInfo {
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
    private String downloadCount = "0";

    public String getIsNew() {
        return isNew;
    }

    public void setIsNew(String isNew) {
        this.isNew = isNew;
    }

    public String getIsPopular() {
        return isPopular;
    }

    public void setIsPopular(String isPopular) {
        this.isPopular = isPopular;
    }

    private String isNew = "N";
    private String isPopular = "N";

    public String getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(String downloadCount) {
        this.downloadCount = downloadCount;
    }

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
