package com.enliple.keyboard.common;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class TimeDealModel implements Serializable {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageInfo() {
        return imageInfo;
    }

    public void setImageInfo(String imageInfo) {
        this.imageInfo = imageInfo;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public int getBenefitAmount() {
        return benefitAmount;
    }

    public void setBenefitAmount(int benefitAmount) {
        this.benefitAmount = benefitAmount;
    }

    public int getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(int salePrice) {
        this.salePrice = salePrice;
    }

    public int getOriginPrice() {
        return originPrice;
    }

    public void setOriginPrice(int originPrice) {
        this.originPrice = originPrice;
    }

    public long getTimeDealStartDate() {
        return timeDealStartDate;
    }

    public void setTimeDealStartDate(long timeDealStartDate) {
        this.timeDealStartDate = timeDealStartDate;
    }

    public long getTimeDealEndDate() {
        return timeDealEndDate;
    }

    public void setTimeDealEndDate(long timeDealEndDate) {
        this.timeDealEndDate = timeDealEndDate;
    }

    public String getImmdSaveYn() {
        return immdSaveYn;
    }

    public void setImmdSaveYn(String immdSaveYn) {
        this.immdSaveYn = immdSaveYn;
    }

    public int getImmdSavePoint() {
        return immdSavePoint;
    }

    public void setImmdSavePoint(int immdSavePoint) {
        this.immdSavePoint = immdSavePoint;
    }

    private String id;
    private String title;
    private String description;
    private String imageInfo;
    private String linkUrl;
    private long startDate;
    private long endDate;
    private int benefitAmount;
    private int salePrice;
    private int originPrice;
    private long timeDealStartDate;
    private long timeDealEndDate;
    private String immdSaveYn;
    private int immdSavePoint;
/**
    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(imageInfo);
        parcel.writeString(linkUrl);
        parcel.writeLong(startDate);
        parcel.writeLong(endDate);
        parcel.writeLong(timeDealStartDate);
        parcel.writeLong(timeDealEndDate);
        parcel.writeInt(benefitAmount);
        parcel.writeInt(salePrice);
        parcel.writeInt(originPrice);
        parcel.writeInt(immdSavePoint);
    }

    public TimeDealModel(Parcel in)
    {
        if (in != null)
        {
            id = in.readString();
            title = in.readString();
            description = in.readString();
            imageInfo = in.readString();
            linkUrl = in.readString();
            startDate = in.readLong();
            endDate = in.readLong();
            timeDealStartDate = in.readLong();
            timeDealEndDate = in.readLong();
            benefitAmount = in.readInt();
            salePrice = in.readInt();
            originPrice = in.readInt();
            immdSavePoint = in.readInt();
        }
    }

    @Override
    public int describeContents()
    {
        return hashCode();
    }

    public TimeDealModel()
    {

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public TimeDealModel createFromParcel(Parcel in)
        {
            return new TimeDealModel(in);
        }

        public TimeDealModel[] newArray(int size)
        {
            return new TimeDealModel[size];
        }
    };**/
}
