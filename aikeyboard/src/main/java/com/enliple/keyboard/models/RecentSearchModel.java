package com.enliple.keyboard.models;

public class RecentSearchModel {

    private String searchWord;
    private String date;
    private long lastDate;
    private int sequence;

    public void setSearchWord(String _searchword){this.searchWord = _searchword;}
    public String getSearchWord(){return searchWord;}

    public void setDate(String _date){this.date = _date;}
    public String getDate(){return date;}

    public void setLastDate(long _ldata){this.lastDate = _ldata;}
    public long getLastDate(){return lastDate;}

    public void setSequence(int _sq){this.sequence = _sq;}
    public int getSequence(){return sequence;}
}
