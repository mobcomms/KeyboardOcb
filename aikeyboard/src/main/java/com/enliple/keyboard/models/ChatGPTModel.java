package com.enliple.keyboard.models;

public class ChatGPTModel {
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLoading(boolean isLoading) { this.isLoading = isLoading; }

    public boolean isLoading() { return isLoading; }

    public String getAnswerType() {
        return answerType;
    }

    public void setAnswerType(String answerType) {
        this.answerType = answerType;
    }

    private String content;
    private String date;
    private String type;
    private String answerType = "Y";
    private boolean isLoading = true;
}
