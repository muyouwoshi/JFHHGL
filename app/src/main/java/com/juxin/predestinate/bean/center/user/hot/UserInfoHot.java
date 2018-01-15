package com.juxin.predestinate.bean.center.user.hot;

import android.os.Parcel;
import android.text.TextUtils;

import com.juxin.predestinate.bean.center.user.detail.UserBasic;

import org.json.JSONObject;

/**
 * 个人资料 热门页
 */
public class UserInfoHot extends UserBasic {

    private boolean isOk = false;
    private long time;
    private String infoJson;    //存储json

    private int channel_uid;
    private int photoNum;

    private boolean onlineState;     // 在线状态
    private String lastOnLineTime;  // 上次在线时间

    private boolean isVip;      // 是否vip

    private String distance; //距离
    private boolean isMobileValidation = false;//手机是否验证
    private boolean isIdcardValidation = false;//身份证是否验证  0 未提交 1 待审核 2 审核通过 3 审核不通过
    private boolean isVideoValidation = false;//视频认证是否通过
    private boolean isVideoBusy = false;//是否正在视频或者音频中

    private int videoPrice;//视频价格
    private int audioPrice;//音频价格

    private boolean video_available = false; //可否视频
    private boolean audio_available = false; //可否语音

    private int kf_id = 0; //不为0就是机器人

    private boolean is_sayHello = false; //是否打过招呼

    public UserInfoHot() {
    }

    @Override
    public void parseJson(String jsonResult) {
        super.parseJson(jsonResult);
        if (TextUtils.isEmpty(jsonResult)) return;
        this.setInfoJson(jsonResult);
        JSONObject jsonObject = getJsonObject(jsonResult);
        this.setOk(true);
        this.setInfoJson(jsonObject.toString());

        this.setUid(jsonObject.optLong("uid"));
        this.setAge(jsonObject.optInt("age"));
        this.setAvatar(jsonObject.optString("avatar"));
        this.setAvatar_status(jsonObject.optInt("avatarstatus"));
        this.setChannel_uid(jsonObject.optInt("channel_uid"));
        this.setCity(jsonObject.optString("city"));
        this.setProvince(jsonObject.optString("province"));
        this.setDistance(jsonObject.optString("distance"));
        this.setGender(jsonObject.optInt("gender"));
        this.setVip(jsonObject.optInt("group") >= 2);
        this.setHeight(jsonObject.optInt("height"));
        this.setKf_id(jsonObject.optInt("kf_id"));
        this.setOnlineState(jsonObject.optBoolean("is_online"));
        this.setLastOnLineTime(jsonObject.optString("last_online"));
        this.setNickname(jsonObject.optString("nickname"));
        this.setMobileValidation(jsonObject.optInt("mobile_validation", 0) == 1);
        this.setIdcardValidation(jsonObject.optInt("idcard_validation", 0) == 2);
        this.setVideoBusy(jsonObject.optInt("video_busy", 0) == 1);
        this.setPhotoNum(jsonObject.optInt("photonum"));
        this.setIs_sayHello(jsonObject.optBoolean("isSayHello"));

        if (!jsonObject.isNull("videochatconfig")) {
            JSONObject configJsonObj = jsonObject.optJSONObject("videochatconfig");
            this.setVideo_available(configJsonObj.optInt("videochat") == 1);
            this.setAudio_available(configJsonObj.optInt("audiochat") == 1);
            this.setVideoValidation(configJsonObj.optInt("videoverify") == 3);
            this.setVideoPrice(configJsonObj.optInt("videoprice"));
            this.setAudioPrice(configJsonObj.optInt("audioprice"));
        }
    }

    public void parseUserInfoHot(long userID, String infoJson, long time) {
        this.setUid(userID);
        this.setTime(time);
        parseJson(infoJson);
    }

    public boolean isOk() {
        return isOk;
    }

    public void setOk(boolean ok) {
        isOk = ok;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getInfoJson() {
        return infoJson;
    }

    public void setInfoJson(String infoJson) {
        this.infoJson = infoJson;
    }


    public boolean isVip() {
        return isVip;
    }

    public void setVip(boolean vip) {
        isVip = vip;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public boolean isVideo_available() {
        return video_available;
    }

    public void setVideo_available(boolean video_available) {
        this.video_available = video_available;
    }

    public boolean isAudio_available() {
        return audio_available;
    }

    public void setAudio_available(boolean audio_available) {
        this.audio_available = audio_available;
    }

    public int getKf_id() {
        return kf_id;
    }

    public void setKf_id(int kf_id) {
        this.kf_id = kf_id;
    }

    public int getChannel_uid() {
        return channel_uid;
    }

    public void setChannel_uid(int channel_uid) {
        this.channel_uid = channel_uid;
    }

    public int getVideoPrice() {
        return videoPrice;
    }

    public void setVideoPrice(int videoPrice) {
        this.videoPrice = videoPrice;
    }

    public int getAudioPrice() {
        return audioPrice;
    }

    public void setAudioPrice(int audioPrice) {
        this.audioPrice = audioPrice;
    }

    public int getPhotoNum() {
        return photoNum;
    }

    public void setPhotoNum(int photoNum) {
        this.photoNum = photoNum;
    }

    public boolean getOnlineState() {
        return onlineState;
    }

    public void setOnlineState(boolean onlineState) {
        this.onlineState = onlineState;
    }

    public String getLastOnLineTime() {
        return lastOnLineTime;
    }

    public void setLastOnLineTime(String lastOnLineTime) {
        this.lastOnLineTime = lastOnLineTime;
    }

    public boolean isMobileValidation() {
        return isMobileValidation;
    }

    public void setMobileValidation(boolean mobileValidation) {
        isMobileValidation = mobileValidation;
    }

    public boolean isIdcardValidation() {
        return isIdcardValidation;
    }

    public void setIdcardValidation(boolean idcardValidation) {
        isIdcardValidation = idcardValidation;
    }

    public boolean isVideoValidation() {
        return isVideoValidation;
    }

    public void setVideoValidation(boolean videoValidation) {
        isVideoValidation = videoValidation;
    }

    public boolean isVideoBusy() {
        return isVideoBusy;
    }

    public void setVideoBusy(boolean videoBusy) {
        isVideoBusy = videoBusy;
    }

    public boolean is_sayHello() {
        return is_sayHello;
    }

    public void setIs_sayHello(boolean is_sayHello) {
        this.is_sayHello = is_sayHello;
    }

    @Override
    public String toString() {
        return "UserInfoLightweight{" +
                "time=" + time +
                ", infoJson='" + infoJson + '\'' +
                ", isVip=" + isVip +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByte(this.isOk ? (byte) 1 : (byte) 0);
        dest.writeLong(this.time);
        dest.writeString(this.infoJson);
        dest.writeInt(this.channel_uid);
        dest.writeInt(this.photoNum);
        dest.writeByte(this.onlineState ? (byte) 1 : (byte) 0);
        dest.writeString(this.lastOnLineTime);
        dest.writeByte(this.isVip ? (byte) 1 : (byte) 0);
        dest.writeString(this.distance);
        dest.writeByte(this.isMobileValidation ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isIdcardValidation ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isVideoValidation ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isVideoBusy ? (byte) 1 : (byte) 0);
        dest.writeInt(this.videoPrice);
        dest.writeInt(this.audioPrice);
        dest.writeByte(this.video_available ? (byte) 1 : (byte) 0);
        dest.writeByte(this.audio_available ? (byte) 1 : (byte) 0);
        dest.writeInt(this.kf_id);
        dest.writeByte(this.is_sayHello ? (byte) 1 : (byte) 0);
    }

    protected UserInfoHot(Parcel in) {
        super(in);
        this.isOk = in.readByte() != 0;
        this.time = in.readLong();
        this.infoJson = in.readString();
        this.channel_uid = in.readInt();
        this.photoNum = in.readInt();
        this.onlineState = in.readByte() != 0;
        this.lastOnLineTime = in.readString();
        this.isVip = in.readByte() != 0;
        this.distance = in.readString();
        this.isMobileValidation = in.readByte() != 0;
        this.isIdcardValidation = in.readByte() != 0;
        this.isVideoValidation = in.readByte() != 0;
        this.isVideoBusy = in.readByte() != 0;
        this.videoPrice = in.readInt();
        this.audioPrice = in.readInt();
        this.video_available = in.readByte() != 0;
        this.audio_available = in.readByte() != 0;
        this.kf_id = in.readInt();
        this.is_sayHello = in.readByte() != 0;
    }

    public static final Creator<UserInfoHot> CREATOR = new Creator<UserInfoHot>() {
        @Override
        public UserInfoHot createFromParcel(Parcel source) {
            return new UserInfoHot(source);
        }

        @Override
        public UserInfoHot[] newArray(int size) {
            return new UserInfoHot[size];
        }
    };
}