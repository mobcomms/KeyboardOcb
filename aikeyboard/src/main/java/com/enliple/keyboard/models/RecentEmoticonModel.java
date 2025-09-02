package com.enliple.keyboard.models;

public class RecentEmoticonModel {

    private String text;
    private long lastDate;

    public void setText(String text){this.text = text;}
    public String getText(){return text;}

    public void setLastDate(long _ldata){this.lastDate = _ldata;}
    public long getLastDate(){return lastDate;}
}
