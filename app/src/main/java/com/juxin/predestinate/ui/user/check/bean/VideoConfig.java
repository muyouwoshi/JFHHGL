package com.juxin.predestinate.ui.user.check.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

/**
 * 他人音视频开关配置
 * Created by Su on 2017/5/3.
 */

public class VideoConfig extends BaseData implements Parcelable {
    private int videoChat;      // 视频开关 1：开启 0：关闭
    private int voiceChat;      // 音频开关 1：开启 0：关闭
    private int videoVertify;   // 视频认证 1：未通过 3：通过
    private int videoPrice;     // 视频价格
    private int audioPrice;     // 音频价格

    @Override
    public void parseJson(String jsonStr) {
        String jsonData = getJsonObject(jsonStr).optString("res");
        JSONObject jsonObject = getJsonObject(jsonData);

        this.videoChat = jsonObject.optInt("videochat");
        this.voiceChat = jsonObject.optInt("audiochat");
        this.videoVertify = jsonObject.optInt("videoverify");
        this.videoPrice = jsonObject.optInt("videoprice");
        this.audioPrice = jsonObject.optInt("audioprice");
    }

    /**
     * 视频验证是否通过
     */
    public boolean isVerifyVideo() {
        return videoVertify == 3;
    }

    /**
     * 是否可视频
     */
    public boolean isVideoChat() {
        return videoChat == 1;
    }

    /**
     * 是否可音频
     */
    public boolean isVoiceChat() {
        return voiceChat == 1;
    }

    public int getVideoPrice() {
        return videoPrice;
    }

    public int getAudioPrice() {
        return audioPrice;
    }

    public int getVideoChat() {
        return videoChat;
    }

    public int getVoiceChat() {
        return voiceChat;
    }

    public int getVideoVertify() {
        return videoVertify;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.videoChat);
        dest.writeInt(this.voiceChat);
        dest.writeInt(this.videoVertify);
        dest.writeInt(this.videoPrice);
        dest.writeInt(this.audioPrice);
    }

    public VideoConfig() {
    }

    protected VideoConfig(Parcel in) {
        this.videoChat = in.readInt();
        this.voiceChat = in.readInt();
        this.videoVertify = in.readInt();
        this.videoPrice = in.readInt();
        this.audioPrice = in.readInt();
    }

    public static final Creator<VideoConfig> CREATOR = new Creator<VideoConfig>() {
        @Override
        public VideoConfig createFromParcel(Parcel source) {
            return new VideoConfig(source);
        }

        @Override
        public VideoConfig[] newArray(int size) {
            return new VideoConfig[size];
        }
    };
}
