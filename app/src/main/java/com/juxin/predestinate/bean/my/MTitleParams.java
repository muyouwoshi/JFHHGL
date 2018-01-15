package com.juxin.predestinate.bean.my;

import android.os.Parcel;
import android.os.Parcelable;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

/**
 * 创建日期：2017/10/30
 * 描述:
 * 作者:lc
 */
public class MTitleParams extends BaseData implements Parcelable {
    private String topType;              //参数

    @Override
    public void parseJson(String jsonStr) {
        JSONObject jsonObject = getJsonObjectRes(jsonStr);
        this.topType = jsonObject.optString("topType");
    }

    public String getTopType() {
        return topType;
    }

    public void setTopType(String topType) {
        this.topType = topType;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.topType);
    }

    public MTitleParams() {
    }

    protected MTitleParams(Parcel in) {
        this.topType = in.readString();
    }

    public static final Creator<MTitleParams> CREATOR = new Creator<MTitleParams>() {
        @Override
        public MTitleParams createFromParcel(Parcel source) {
            return new MTitleParams(source);
        }

        @Override
        public MTitleParams[] newArray(int size) {
            return new MTitleParams[size];
        }
    };
}
