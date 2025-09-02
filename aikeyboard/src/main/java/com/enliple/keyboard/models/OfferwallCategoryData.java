package com.enliple.keyboard.models;

public class OfferwallCategoryData {
    public String getMission_class() {
        return mission_class;
    }

    public void setMission_class(String mission_class) {
        this.mission_class = mission_class;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    private String mission_class;
    private String class_name;
    private boolean isSelected;
}
