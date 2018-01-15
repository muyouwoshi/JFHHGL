package com.juxin.predestinate.bean.center.user.detail;

import android.os.Parcel;
import android.os.Parcelable;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

/**
 * 用户相册
 */
public class UserPhoto extends BaseData implements Parcelable {
    private long albumid;       //相册id
    private String pic;         //图片地址
    private int status;         //图片状态

    @Override
    public void parseJson(String s) {
        JSONObject photoObject = getJsonObject(s);
        this.setAlbumid(photoObject.optInt("albumid"));
        this.setPic(photoObject.optString("pic"));
        this.setStatus(photoObject.optInt("status"));
    }

    public UserPhoto(int status) {
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
    }

    public UserPhoto() {
    }

    protected UserPhoto(Parcel in) {
        this.albumid = in.readLong();
        this.pic = in.readString();
        this.status = in.readInt();
    }

    public static final Creator<UserPhoto> CREATOR = new Creator<UserPhoto>() {
        @Override
        public UserPhoto createFromParcel(Parcel source) {
            return new UserPhoto(source);
        }

        @Override
        public UserPhoto[] newArray(int size) {
            return new UserPhoto[size];
        }
    };
}
