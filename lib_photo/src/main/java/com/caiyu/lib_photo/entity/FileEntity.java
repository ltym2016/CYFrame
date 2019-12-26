package com.caiyu.lib_photo.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 一句话功能简述
 * 功能详细描述
 *
 * @author 杨晨 on 2017/4/27 10:01
 * @e-mail 247067345@qq.com
 * @see [相关类/方法](可选)
 */

public class FileEntity implements Parcelable {

    public static final int IMAGE=1;
    public static final int VIDEO=2;

    private String path;
    private long dateTaken;
    private int type;
    private long videoId;
    private long duration;
    private int width;
    private int height;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(long dateTaken) {
        this.dateTaken = dateTaken;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getVideoId() {
        return videoId;
    }

    public void setVideoId(long videoId) {
        this.videoId = videoId;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public FileEntity() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeLong(this.dateTaken);
        dest.writeInt(this.type);
        dest.writeLong(this.videoId);
        dest.writeLong(this.duration);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
    }

    protected FileEntity(Parcel in) {
        this.path = in.readString();
        this.dateTaken = in.readLong();
        this.type = in.readInt();
        this.videoId = in.readLong();
        this.duration = in.readLong();
        this.width = in.readInt();
        this.height = in.readInt();
    }

    public static final Creator<FileEntity> CREATOR = new Creator<FileEntity>() {
        @Override
        public FileEntity createFromParcel(Parcel source) {
            return new FileEntity(source);
        }

        @Override
        public FileEntity[] newArray(int size) {
            return new FileEntity[size];
        }
    };
}
