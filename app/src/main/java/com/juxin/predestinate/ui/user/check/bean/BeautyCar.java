package com.juxin.predestinate.ui.user.check.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

/**
 * 创建日期：2017/8/24
 * 描述:女神座驾信息
 * 作者:lc
 */
public class BeautyCar extends BaseData implements Parcelable {
    private int win_count;      //胜利次数
    private int lose_count;     //失败次数

    @Override
    public void parseJson(String jsonStr) {
        try {
            String jsonData = getJsonObject(jsonStr).optString("res");
            JSONObject jsonObject = getJsonObject(jsonData).optJSONObject("beauty_car");
            setWin_count(jsonObject.optInt("win_count"));
            setLose_count(jsonObject.optInt("lose_count"));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getWin_count() {
        return win_count;
    }

    public void setWin_count(int win_count) {
        this.win_count = win_count;
    }

    public int getLose_count() {
        return lose_count;
    }

    public void setLose_count(int lose_count) {
        this.lose_count = lose_count;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.win_count);
        dest.writeInt(this.lose_count);
    }

    public BeautyCar() {
    }

    protected BeautyCar(Parcel in) {
        this.win_count = in.readInt();
        this.lose_count = in.readInt();
    }

    public static final Creator<BeautyCar> CREATOR = new Creator<BeautyCar>() {
        @Override
        public BeautyCar createFromParcel(Parcel source) {
            return new BeautyCar(source);
        }

        @Override
        public BeautyCar[] newArray(int size) {
            return new BeautyCar[size];
        }
    };
}
