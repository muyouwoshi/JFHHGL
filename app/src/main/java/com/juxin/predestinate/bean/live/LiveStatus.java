package com.juxin.predestinate.bean.live;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

/**
 * 用户直播状态
 * @author gwz
 */
public class LiveStatus extends BaseData implements Parcelable {
    //直播封面
    private String cover;
    //上次直播时间
    private long last_live_time;
    //直播间ID
    private long roomId;
    //是否在直播
    private boolean isLive;
    //直播标题
    private String title;
    @Override
    public void parseJson(String jsonStr) {
        if(TextUtils.isEmpty(jsonStr)){
            return;
        }
        JSONObject jo = getJsonObject(jsonStr);
        cover = jo.optString("cover");
        last_live_time = jo.optLong("modify_time");
        roomId = jo.optLong("room_id");
        isLive = jo.optLong("status",0) == 1;
        title = jo.optString("title");
    }

    public String getCover() {
        return cover;
    }

    public long getLast_live_time() {
        return last_live_time;
    }

    public long getRoomId() {
        return roomId;
    }

    public boolean isLive() {
        return isLive;
    }

    public String getTitle() {
        return title;
    }

    public void setLive(boolean live) {
        isLive = live;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cover);
        dest.writeLong(this.last_live_time);
        dest.writeLong(this.roomId);
        dest.writeByte(this.isLive ? (byte) 1 : (byte) 0);
        dest.writeString(this.title);
    }

    public LiveStatus() {
    }

    protected LiveStatus(Parcel in) {
        this.cover = in.readString();
        this.last_live_time = in.readLong();
        this.roomId = in.readLong();
        this.isLive = in.readByte() != 0;
        this.title = in.readString();
    }

    public static final Parcelable.Creator<LiveStatus> CREATOR = new Parcelable.Creator<LiveStatus>() {
        @Override
        public LiveStatus createFromParcel(Parcel source) {
            return new LiveStatus(source);
        }

        @Override
        public LiveStatus[] newArray(int size) {
            return new LiveStatus[size];
        }
    };
}
