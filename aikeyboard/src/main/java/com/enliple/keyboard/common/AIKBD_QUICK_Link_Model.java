package com.enliple.keyboard.common;

/**
 * Created by Administrator on 2017-11-08.
 */

public class AIKBD_QUICK_Link_Model {
    private String mIcon = "";
    private String mName = "";
    private String mContent = "";
    private String mLink = "";
    private String mMatchWord = "";

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public void setLink(String link) {
        mLink = link;
    }

    public void setMatchWord(String word) {
        mMatchWord = word;
    }

    public String getIcon() {
        return mIcon;
    }

    public String getName() {
        return mName;
    }

    public String getContent() {
        return mContent;
    }

    public String getLink() {
        return mLink;
    }

    public String getMatchWord() {
        return mMatchWord;
    }
}
