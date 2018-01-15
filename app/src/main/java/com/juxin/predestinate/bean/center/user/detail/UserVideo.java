package com.juxin.predestinate.bean.center.user.detail;

import android.os.Parcel;
import android.os.Parcelable;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

/**
 * 用户视频
 * Created by Su on 2017/5/24.
 */

public class UserVideo extends BaseData implements Parcelable {
    private String content;         // 文本
    private int cost;               // 价格（钻石）
    private String create_time;     // 视频创建时间
    private int duration;           // 时长（秒）
    private int giftid;             // 礼物id
    private int giftnum;            // 礼物数量
    private long id;                // 视频id
    private int open;               // 是否可看  1 不可看 2 可看
    private String pic;             // 封面图片
    private int status;             // 视频状态
    private long uid;               // 用户id
    private String video;           // 视频地址
    private int viewTimes;          // 查看次数

    @Override
    public void parseJson(String jsonStr) {
        JSONObject jsonObject = getJsonObject(jsonStr);
        this.content = jsonObject.optString("content");
        this.cost = jsonObject.optInt("cost");
        this.create_time = jsonObject.optString("create_time");
        this.duration = jsonObject.optInt("duration");
        this.giftid = jsonObject.optInt("giftid");
        this.giftnum = jsonObject.optInt("giftnum");
        this.id = jsonObject.optLong("id");
        this.open = jsonObject.optInt("open");
        this.pic = jsonObject.optString("pic");
        this.status = jsonObject.optInt("status");
        this.uid = jsonObject.optLong("uid");
        this.video = jsonObject.optString("video");
        this.viewTimes = jsonObject.optInt("viewtimes");
    }

    /**
     * 是否可查看视频
     */
    public boolean isCanView() {
        return open == 2;
    }

    public void setCanView(){
        open = 2;
    }

    public String getContent() {
        return content;
    }

    public int getCost() {
        return cost;
    }

    public String getCreate_time() {
        return create_time;
    }

    public int getDuration() {
        return duration;
    }

    public int getGiftid() {
        return giftid;
    }

    public int getGiftnum() {
        return giftnum;
    }

    public long getId() {
        return id;
    }

    public int getOpen() {
        return open;
    }

    public String getPic() {
        return pic;
    }

    public int getStatus() {
        return status;
    }

    public long getUid() {
        return uid;
    }

    public String getVideo() {
        return video;
    }

    public int getViewTimes() {
        return viewTimes;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setGiftid(int giftid) {
        this.giftid = giftid;
    }

    public void setGiftnum(int giftnum) {
        this.giftnum = giftnum;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public void setViewTimes(int viewTimes) {
        this.viewTimes = viewTimes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.content);
        dest.writeInt(this.cost);
        dest.writeString(this.create_time);
        dest.writeInt(this.duration);
        dest.writeInt(this.giftid);
        dest.writeInt(this.giftnum);
        dest.writeLong(this.id);
        dest.writeInt(this.open);
        dest.writeString(this.pic);
        dest.writeInt(this.status);
        dest.writeLong(this.uid);
        dest.writeString(this.video);
        dest.writeInt(this.viewTimes);
    }

    public UserVideo() {
    }

    protected UserVideo(Parcel in) {
        this.content = in.readString();
        this.cost = in.readInt();
        this.create_time = in.readString();
        this.duration = in.readInt();
        this.giftid = in.readInt();
        this.giftnum = in.readInt();
        this.id = in.readLong();
        this.open = in.readInt();
        this.pic = in.readString();
        this.status = in.readInt();
        this.uid = in.readLong();
        this.video = in.readString();
        this.viewTimes = in.readInt();
    }

    public static final Creator<UserVideo> CREATOR = new Creator<UserVideo>() {
        @Override
        public UserVideo createFromParcel(Parcel source) {
            return new UserVideo(source);
        }

        @Override
        public UserVideo[] newArray(int size) {
            return new UserVideo[size];
        }
    };
}
