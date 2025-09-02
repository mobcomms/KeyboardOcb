package com.enliple.keyboard.models;

public class NewsNotiModel {
    public String getPoint() { return point; }

    public void setPoint(String point) { this.point = point; }

    public String getSeq() { return seq; }

    public void setSeq(String seq) { this.seq = seq; }

    public String getTitle() { return title; }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoint_image() {
        return point_image;
    }

    public void setPoint_image(String point_image) {
        this.point_image = point_image;
    }

    public String getNews_image() {
        return news_image;
    }

    public void setNews_image(String news_image) {
        this.news_image = news_image;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public double getMonth_point() {
        return month_point;
    }

    public void setMonth_point(double month_point) {
        this.month_point = month_point;
    }

    public double getToday_point() {
        return today_point;
    }

    public void setToday_point(double today_point) {
        this.today_point = today_point;
    }

    private String point;
    private String seq;
    private String title;
    private String point_image;
    private String news_image;
    private int count;
    private String link;
    private double month_point;
    private double today_point;
}
