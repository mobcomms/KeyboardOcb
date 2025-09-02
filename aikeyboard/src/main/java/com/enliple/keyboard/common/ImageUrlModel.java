package com.enliple.keyboard.common;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017-03-17.
 */

public class ImageUrlModel implements Parcelable
{
    private String mLinkUrl = null;
    private String mThumbUrl = null;
    private String mGalleryUrl = null;
    private int mResourceId = -1;

    public ImageUrlModel(Parcel in)
    {
        readFromParcel(in);
    }

    public ImageUrlModel(String linkUrl, String thumbUrl, String galleryUrl, int resourceId)
    {
        mLinkUrl = linkUrl;
        mThumbUrl = thumbUrl;
        mResourceId = resourceId;
        mGalleryUrl = galleryUrl;
    }

    public String getGalleryUrl()
    {
        return mGalleryUrl;
    }

    public String getLinkUrl()
    {
        return mLinkUrl;
    }

    public String getThumbUrl()
    {
        return mThumbUrl;
    }

    public int getResourceId()
    {
        return mResourceId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(mLinkUrl);
        dest.writeString(mThumbUrl);
        dest.writeString(mGalleryUrl);
        dest.writeInt(mResourceId);
    }

    private void readFromParcel(Parcel in)
    {
        mLinkUrl = in.readString();
        mThumbUrl = in.readString();
        mGalleryUrl = in.readString();
        mResourceId = in.readInt();
    }

    public static final Creator CREATOR = new Creator()
    {
        public ImageUrlModel createFromParcel(Parcel in)
        {
            return new ImageUrlModel(in);
        }

        public ImageUrlModel[] newArray(int size)
        {
            return new ImageUrlModel[size];
        }
    };
}
