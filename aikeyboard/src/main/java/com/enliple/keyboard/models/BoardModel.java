package com.enliple.keyboard.models;

public class BoardModel {

    private String title;
    private String bbs_tp_code;
    private String url;
    private String contents;
    private String icon_url;

    public void setTitle(String title){this.title = title;}
    public String getTitle(){return title;}

    public void setBbs_tp_code(String bbs_tp_code){this.bbs_tp_code = bbs_tp_code;}
    public String getBbs_tp_code(){return bbs_tp_code;}

    public void setUrl(String url){this.url = url;}
    public String getUrl(){return url;}

    public void setContents(String contents){this.contents = contents;}
    public String getContents(){return contents;}

    public void setIcon_url(String icon_url){this.icon_url = icon_url;}
    public String getIcon_url(){return icon_url;}
}
