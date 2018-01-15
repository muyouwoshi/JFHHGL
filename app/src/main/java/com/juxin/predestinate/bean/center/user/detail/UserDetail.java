package com.juxin.predestinate.bean.center.user.detail;

import android.os.Parcel;

import com.juxin.library.log.PLogger;
import com.juxin.predestinate.bean.live.LiveStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户详细信息
 */
public class UserDetail extends UserInfo {

    private List<UserPhoto> userPhotos = new ArrayList<>();
    private List<UserVideo> userVideos = new ArrayList<>();
    private UserChatInfo chatInfo = new UserChatInfo();
    private int voice = 1;          //1为开启语音，0为关闭
    private int videopopularity;    // 私密视频人气值

    private int unlock_ycoin;//控制Y币开关
    private int unlock_vip;//控制VIP开关
    private LiveStatus liveStatus = new LiveStatus();

    private boolean isB;//是否B版本用户 1 是B版本 否则为0 [opt]B版本相关信息，如果不是B版本，可能不包含此字段

    private boolean beauty_switch; //追女神开关(广场面板)，true:打开， false：关闭


    @Override
    public void parseJson(String s) {
        String jsonData = getJsonObject(s).optString("res");
        JSONObject jsonObject = getJsonObject(jsonData);

        // 详细信息
        if (!jsonObject.isNull("userDetail")) {
            String json = jsonObject.optString("userDetail");
            super.parseJson(json);
        }

        // 用户相册
        if (!jsonObject.isNull("myPhoto")) {
            this.userPhotos = (List<UserPhoto>) getBaseDataList(jsonObject.optJSONArray("myPhoto"), UserPhoto.class);
        }

        // 开启语音状态
        if (!jsonObject.isNull("voice")) {
            this.voice = jsonObject.optInt("voice");
        }

        // 音视频状态
        if (!jsonObject.isNull("videochatconfig")) {
            this.chatInfo.parseJson(jsonObject.optString("videochatconfig"));
        }

        // -------- 他人 -----
        // 视频列表
        if (!jsonObject.isNull("videolist")) {
            this.userVideos = (List<UserVideo>) getBaseDataList(jsonObject.optJSONArray("videolist"), UserVideo.class);
        }

        // 视频人气值
        if (!jsonObject.isNull("videopopularity")) {
            this.videopopularity = jsonObject.optInt("videopopularity");
        }

        if (!jsonObject.isNull("unlock_ycoin")) {
            this.setUnlock_ycoin(jsonObject.optInt("unlock_ycoin"));
        }

        if (!jsonObject.isNull("unlock_vip")) {
            this.setUnlock_vip(jsonObject.optInt("unlock_vip"));
        }
        liveStatus.parseJson(jsonObject.optString("liveStatus"));

        if(!jsonObject.isNull("beauty_switch")) {
            this.setBeauty_switch(jsonObject.optBoolean("beauty_switch"));
        }

        //@author Mr.Huang
        //@date 2017-07-14
        //下面解析钥匙数量数据
        if (!jsonObject.isNull("ab_info")) {
            try {
                JSONObject abInfo = jsonObject.getJSONObject("ab_info");
                if (!abInfo.isNull("is_b")) {
                    this.isB = abInfo.getInt("is_b") == 1;
                }
            } catch (JSONException e) {
                e.printStackTrace();//调试需要堆栈信息
                PLogger.e("abtest abinfo data parse error!!!!");
            }
        }
    }

    public UserChatInfo getChatInfo() {
        return chatInfo;
    }

    public List<UserVideo> getUserVideos() {
        return userVideos;
    }

    public List<UserPhoto> getUserPhotos() {
        return userPhotos;
    }

    public int getVoice() {
        return voice;
    }

    public int getVideopopularity() {
        return videopopularity;
    }


    public int getUnlock_ycoin() {
        return unlock_ycoin;
    }

    public boolean isUnlock_ycoin() {
        return unlock_ycoin == 1;
    }

    public void setUnlock_ycoin(int unlock_ycoin) {
        this.unlock_ycoin = unlock_ycoin;
    }

    public int getUnlock_vip() {
        return unlock_vip;
    }

    public boolean isUnlock_vip() {
        return unlock_vip == 1;
    }

    public void setUnlock_vip(int unlock_vip) {
        this.unlock_vip = unlock_vip;
    }

    public LiveStatus getLiveStatus() {
        return liveStatus;
    }

    public boolean isBeauty_switch() {
        return beauty_switch;
    }

    public void setBeauty_switch(boolean beauty_switch) {
        this.beauty_switch = beauty_switch;
    }

    public boolean isB() {
        return this.isB;
    }

    public void isB(boolean isB) {
        this.isB = isB;
    }

    public UserDetail() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.userPhotos);
        dest.writeTypedList(this.userVideos);
        dest.writeParcelable(this.chatInfo, flags);
        dest.writeInt(this.voice);
        dest.writeInt(this.videopopularity);
        dest.writeInt(this.unlock_ycoin);
        dest.writeInt(this.unlock_vip);
        dest.writeParcelable(liveStatus, flags);
        dest.writeInt(this.isB ? 1 : 0);
        dest.writeInt(this.isBeauty_switch() ? 1 : 0);
    }

    protected UserDetail(Parcel in) {
        super(in);
        this.userPhotos = in.createTypedArrayList(UserPhoto.CREATOR);
        this.userVideos = in.createTypedArrayList(UserVideo.CREATOR);
        this.chatInfo = in.readParcelable(UserChatInfo.class.getClassLoader());
        this.voice = in.readInt();
        this.videopopularity = in.readInt();
        this.unlock_ycoin = in.readInt();
        this.unlock_vip = in.readInt();
        liveStatus = in.readParcelable(LiveStatus.class.getClassLoader());
        this.isB = in.readInt() == 1;
        this.beauty_switch = in.readInt() == 1;
    }

    public static final Creator<UserDetail> CREATOR = new Creator<UserDetail>() {
        @Override
        public UserDetail createFromParcel(Parcel source) {
            return new UserDetail(source);
        }

        @Override
        public UserDetail[] newArray(int size) {
            return new UserDetail[size];
        }
    };
}
