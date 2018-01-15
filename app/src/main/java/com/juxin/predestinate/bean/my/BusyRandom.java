package com.juxin.predestinate.bean.my;

import android.os.Parcel;
import android.os.Parcelable;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

/**
 * 创建日期：2017/9/13
 * 描述:女号忙线中...
 * 作者:lc
 */
public class BusyRandom extends BaseData implements Parcelable {
    private long to_uid;            //对方uid
    private long vc_id;             //视频聊天ID
    private int media_tp;           //聊天媒体类型 1视频, 2语音
    private int price;              //视频价格

    @Override
    public void parseJson(String jsonStr) {
        String jsonData = getJsonObject(jsonStr).optString("res");
        JSONObject jsonObject = getJsonObject(jsonData);

        this.to_uid = jsonObject.optLong("to_uid");
        this.vc_id = jsonObject.optLong("vc_id");
        this.media_tp = jsonObject.optInt("media_tp");
        this.price = jsonObject.optInt("price");
    }

    public long getTo_uid() {
        return to_uid;
    }

    public void setTo_uid(long to_uid) {
        this.to_uid = to_uid;
    }

    public long getVc_id() {
        return vc_id;
    }

    public void setVc_id(long vc_id) {
        this.vc_id = vc_id;
    }

    public int getMedia_tp() {
        return media_tp;
    }

    public void setMedia_tp(int media_tp) {
        this.media_tp = media_tp;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.to_uid);
        dest.writeLong(this.vc_id);
        dest.writeInt(this.media_tp);
        dest.writeInt(this.price);
    }

    public BusyRandom() {
    }

    protected BusyRandom(Parcel in) {
        this.to_uid = in.readLong();
        this.vc_id = in.readLong();
        this.media_tp = in.readInt();
        this.price = in.readInt();
    }

    public static final Creator<BusyRandom> CREATOR = new Creator<BusyRandom>() {
        @Override
        public BusyRandom createFromParcel(Parcel source) {
            return new BusyRandom(source);
        }

        @Override
        public BusyRandom[] newArray(int size) {
            return new BusyRandom[size];
        }
    };
}
