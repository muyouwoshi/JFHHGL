package com.juxin.predestinate.bean.settting;

import android.os.Parcel;
import android.os.Parcelable;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

/**
 * 用户设置信息解析
 * Created YAO on 2017/4/12.
 */

public class Setting extends BaseData implements Parcelable {
    private int voice_price;//接电话价格
    private int video_price;//接视频价格
    private int stealth;//0为隐身 1为不隐身

    public int getVoice_price() {
        return voice_price;
    }

    public void setVoice_price(int voice_price) {
        this.voice_price = voice_price;
    }

    public int getVideo_price() {
        return video_price;
    }

    public void setVideo_price(int video_price) {
        this.video_price = video_price;
    }

    public int getStealth() {
        return stealth;
    }

    public boolean isStealth() {
        return stealth == 0;
    }

    public void setStealth(int stealth) {
        this.stealth = stealth;
    }

    @Override
    public void parseJson(String jsonStr) {
        JSONObject jsonObject = getJsonObject(jsonStr);
        voice_price = jsonObject.optInt("voice_price");
        video_price = jsonObject.optInt("video_price");
        stealth = jsonObject.optInt("stealth");
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.voice_price);
        dest.writeInt(this.video_price);
        dest.writeInt(this.stealth);
    }

    public Setting() {
    }

    protected Setting(Parcel in) {
        this.voice_price = in.readInt();
        this.video_price = in.readInt();
        this.stealth = in.readInt();
    }

    public static final Creator<Setting> CREATOR = new Creator<Setting>() {
        @Override
        public Setting createFromParcel(Parcel source) {
            return new Setting(source);
        }

        @Override
        public Setting[] newArray(int size) {
            return new Setting[size];
        }
    };
}
