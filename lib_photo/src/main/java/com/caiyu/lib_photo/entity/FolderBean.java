package com.caiyu.lib_photo.entity;

import java.util.List;

/**
 * Created by Administrator on 2015/10/18 0018.
 */
public class FolderBean {
    //当前文件夹路径
    private String dir;
    private String firstImgPath;
    private String name;
    private int count;
    private boolean isVideo;
    private List<FileEntity> allFile;

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public List<FileEntity> getAllFile() {
        return allFile;
    }

    public void setAllFile(List<FileEntity> allFile) {
        this.allFile = allFile;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
        int lastIndexOf = this.dir.lastIndexOf("/") + 1;
        this.name = this.dir.substring(lastIndexOf);
    }

    public String getFirstImgPath() {
        return firstImgPath;
    }

    public void setFirstImgPath(String firstImgPath) {
        this.firstImgPath = firstImgPath;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
