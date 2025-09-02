package com.enliple.keyboard.models;

public class ClipboardModel {
    int seq;
    String clipboard;
    boolean isDeleteShow = false;

    public boolean isDeleteShow() {
        return isDeleteShow;
    }

    public void setDeleteShow(boolean isDeleteShow) {
        this.isDeleteShow = isDeleteShow;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getClipboard() {
        return clipboard;
    }

    public void setClipboard(String clipboard) {
        this.clipboard = clipboard;
    }
}
