package com.caiyu.lib_photo.entity;

public class SelectImageEntity {

    private String path;
    private boolean isChoose = true;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isChoose() {
        return isChoose;
    }

    public void setChoose(boolean choose) {
        isChoose = choose;
    }
}
