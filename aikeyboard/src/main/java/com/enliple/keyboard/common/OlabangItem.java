package com.enliple.keyboard.common;

import java.io.Serializable;

public class OlabangItem implements Serializable {
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public String getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(String episodeId) {
        this.episodeId = episodeId;
    }

    public String getPointText() {
        return pointText;
    }

    public void setPointText(String pointText) {
        this.pointText = pointText;
    }

    public String getBenefitText() {
        return benefitText;
    }

    public void setBenefitText(String benefitText) {
        this.benefitText = benefitText;
    }

    public String getLiveType() {
        return liveType;
    }

    public void setLiveType(String liveType) {
        this.liveType = liveType;
    }

    public boolean isSoldOut() {
        return isSoldOut;
    }

    public void setSoldOut(boolean soldOut) {
        isSoldOut = soldOut;
    }

    public long getLiveStartDate() {
        return liveStartDate;
    }

    public void setLiveStartDate(long liveStartDate) {
        this.liveStartDate = liveStartDate;
    }

    public long getLiveEndDate() {
        return liveEndDate;
    }

    public void setLiveEndDate(long liveEndDate) {
        this.liveEndDate = liveEndDate;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public boolean isMaster() {
        return isMaster;
    }

    public void setMaster(boolean master) {
        isMaster = master;
    }

    private String eventId;
    private String title;
    private String imageUrl;
    private String linkUrl;
    private long startDate;
    private long endDate;
    private String episodeId;
    private String pointText;
    private String benefitText;
    private String liveType;
    private boolean isSoldOut;
    private long liveStartDate;
    private long liveEndDate;
    private long createDate;

    public long getRemainTime() {
        return remainTime;
    }

    public void setRemainTime(long remainTime) {
        this.remainTime = remainTime;
    }

    private long remainTime;

    public long getSaleEndDate() {
        return saleEndDate;
    }

    public void setSaleEndDate(long saleEndDate) {
        this.saleEndDate = saleEndDate;
    }

    private long saleEndDate;
    private boolean isMaster;
}
