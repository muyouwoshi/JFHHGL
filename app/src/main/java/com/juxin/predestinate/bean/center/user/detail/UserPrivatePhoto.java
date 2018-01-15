package com.juxin.predestinate.bean.center.user.detail;

import android.os.Parcel;
import android.os.Parcelable;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

/**
 * 用户相册
 */
public class UserPrivatePhoto extends BaseData implements Parcelable {
    private long albumid;       //相册id
    private String pic;         //图片地址
    private int status;         //图片状态
    private String info;        //自我描述

    @Override
    public void parseJson(String s) {
        JSONObject photoObject = getJsonObject(s);
        this.setAlbumid(photoObject.optInt("albumid"));
        this.setPic(photoObject.optString("pic"));
        this.setStatus(photoObject.optInt("status"));
        this.setInfo(photoObject.optString("info"));
    }

    public UserPrivatePhoto(int status) {
        this.status = status;
    }

    public long getAlbumid() {
        return albumid;
    }

    public void setAlbumid(long albumid) {
        this.albumid = albumid;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.albumid);
        dest.writeString(this.pic);
        dest.writeInt(this.status);
        dest.writeString(this.info);
    }

    public UserPrivatePhoto() {
    }

    protected UserPrivatePhoto(Parcel in) {
        this.albumid = in.readLong();
        this.pic = in.readString();
        this.status = in.readInt();
        this.info = in.readString();
    }

    public static final Creator<UserPrivatePhoto> CREATOR = new Creator<UserPrivatePhoto>() {
        @Override
        public UserPrivatePhoto createFromParcel(Parcel source) {
            return new UserPrivatePhoto(source);
        }

        @Override
        public UserPrivatePhoto[] newArray(int size) {
            return new UserPrivatePhoto[size];
        }
    };

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
