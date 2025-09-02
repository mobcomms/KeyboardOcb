package com.enliple.keyboard.models;

import java.io.Serializable;

public class OfferwallData implements Serializable {
    public String getReg_date() {
        return reg_date;
    }

    public void setReg_date(String reg_date) {
        this.reg_date = reg_date;
    }

    public double getMedia_point() {
        return media_point;
    }

    public void setMedia_point(double media_point) {
        this.media_point = media_point;
    }

    public int getDaily_participation() {
        return daily_participation;
    }

    public void setDaily_participation(int daily_participation) {
        this.daily_participation = daily_participation;
    }

    public String getAdver_url() {
        return adver_url;
    }

    public void setAdver_url(String adver_url) {
        this.adver_url = adver_url;
    }

    public String getIntro_img() {
        return intro_img;
    }

    public void setIntro_img(String intro_img) {
        this.intro_img = intro_img;
    }

    public String getMission_id() {
        return mission_id;
    }

    public void setMission_id(String mission_id) {
        this.mission_id = mission_id;
    }

    public int getDaily_participation_cnt() {
        return daily_participation_cnt;
    }

    public void setDaily_participation_cnt(int daily_participation_cnt) {
        this.daily_participation_cnt = daily_participation_cnt;
    }

    public double getUser_point() {
        return user_point;
    }

    public void setUser_point(double user_point) {
        this.user_point = user_point;
    }

    public int getMission_seq() {
        return mission_seq;
    }

    public void setMission_seq(int mission_seq) {
        this.mission_seq = mission_seq;
    }

    public String getAdver_name() {
        return adver_name;
    }

    public void setShop_name(String adver_name) {
        this.adver_name = adver_name;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getThumb_img() {
        return thumb_img;
    }

    public void setThumb_img(String thumb_img) {
        this.thumb_img = thumb_img;
    }

    public String getMission_class() {
        return mission_class;
    }

    public void setMission_class(String mission_class) {
        this.mission_class = mission_class;
    }

    public String getScreenshot() {
        return screenshot;
    }

    public void setScreenshot(String screenshot) {
        this.screenshot = screenshot;
    }

    public int getCheck_time() {
        return check_time;
    }

    public void setCheck_time(int check_thime) {
        this.check_time = check_thime;
    }

    public String getCheck_url() {
        return check_url;
    }

    public void setCheck_url(String check_url) {
        this.check_url = check_url;
    }

    public String getTarget_name() {
        return target_name;
    }

    public void setTarget_name(String target_name) {
        this.target_name = target_name;
    }

    private String screenshot;
    private String mission_class;
    private String reg_date;
    private double media_point = 0;
    private String intro_img;
    private String mission_id;
    private int daily_participation_cnt = 0;
    private int daily_participation = 0;
    private double user_point = 0;
    private int mission_seq = 0;
    private String keyword;
    private String thumb_img;
    private String adver_url;
    private String adver_name;
    private int check_time = 0;
    private String check_url;
    private String target_name;
}
