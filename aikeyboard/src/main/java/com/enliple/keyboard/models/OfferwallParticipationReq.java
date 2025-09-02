package com.enliple.keyboard.models;

public class OfferwallParticipationReq {
    public int getMission_seq() {
        return mission_seq;
    }

    public void setMission_seq(int mission_seq) {
        this.mission_seq = mission_seq;
    }

    public String getMission_id() {
        return mission_id;
    }

    public void setMission_id(String mission_id) {
        this.mission_id = mission_id;
    }

    public String getMedia_user_key() {
        return media_user_key;
    }

    public void setMedia_user_key(String media_user_key) {
        this.media_user_key = media_user_key;
    }

    public String getMedia_user_phone() {
        return media_user_phone;
    }

    public void setMedia_user_phone(String media_user_phone) {
        this.media_user_phone = media_user_phone;
    }

    public String getMedia_user_ad_id() {
        return media_user_ad_id;
    }

    public void setMedia_user_ad_id(String media_user_ad_id) {
        this.media_user_ad_id = media_user_ad_id;
    }

    public String getMedia_user_email() {
        return media_user_email;
    }

    public void setMedia_user_email(String meda_user_email) {
        this.media_user_email = meda_user_email;
    }

    public String getServer_type() {
        return server_type;
    }

    public void setServer_type(String server_type) {
        this.server_type = server_type;
    }

    private int mission_seq;
    private String mission_id;
    private String media_user_key;
    private String media_user_phone;
    private String media_user_ad_id;
    private String media_user_email;
    private String server_type;
}
