package com.enliple.keyboard.models;

public class RecentEmojiModel {

    private String unicode;
    private int sequence;
    private long lastDate;

    public void setUniCode(String _ucode){this.unicode = _ucode;}
    public String getUnicode(){return unicode;}

    public void setSequence(int _sq){this.sequence = _sq;}
    public int getSequence(){return sequence;}

    public void setLastDate(long _ldata){this.lastDate = _ldata;}
    public long getLastDate(){return lastDate;}
}
