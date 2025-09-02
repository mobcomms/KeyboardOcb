package com.enliple.keyboard.common;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017-09-12.
 */

public class MemoModel implements Parcelable {
    private String mTitle = null;
    private String mMemo = null;
    private String mBookMark = "N";
    private String mSaveTime = "0";
    private String mAdd = "N";
    private String mIsDel = "N";

    public MemoModel() {
    }

    public MemoModel(Parcel in) {
        readFromParcel(in);
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setMemo(String memo) {
        mMemo = memo;
    }

    public void setBookMark(String bookmark) {
        mBookMark = bookmark;
    }

    public void setTime(String time) {
        mSaveTime = time;
    }

    public void setAdd(String add) {
        mAdd = add;
    }

    public void setDel(String del) {
        mIsDel = del;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getMemo() {
        return mMemo;
    }

    public String getBookMark() {
        return mBookMark;
    }

    public String getTime() {
        return mSaveTime;
    }

    public String getAdd() {
        return mAdd;
    }

    public String getDel() {
        return mIsDel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mMemo);
        dest.writeString(mBookMark);
        dest.writeString(mSaveTime);
        dest.writeString(mAdd);
        dest.writeString(mIsDel);
    }

    private void readFromParcel(Parcel in){
        mTitle = in.readString();
        mMemo = in.readString();
        mBookMark = in.readString();
        mSaveTime = in.readString();
        mAdd = in.readString();
        mIsDel = in.readString();
    }

    public static final Creator CREATOR = new Creator() {
        public MemoModel createFromParcel(Parcel in) {
            return new MemoModel(in);
        }

        public MemoModel[] newArray(int size) {
            return new MemoModel[size];
        }
    };
}
