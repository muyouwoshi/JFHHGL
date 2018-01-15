package com.juxin.predestinate.bean.center.user.detail;

import android.os.Parcel;
import android.os.Parcelable;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

/**
 * 创建日期：2017/7/17
 * 描述:贵族相关
 * 作者:lc
 */
public class NobilityInfo extends BaseData implements Parcelable {
    private int rank;                //等级
    private long exptime;            //爵位到期时间,秒时间戳
    private int cmpprocess;          //爵位完成进度

    @Override
    public void parseJson(String jsonStr) {
        JSONObject object = getJsonObject(jsonStr);
        if (!object.isNull("rank"))
            setRank(object.optInt("rank"));
        if (!object.isNull("exptime"))
            setExptime(object.optLong("exptime"));
        if (!object.isNull("cmpprocess"))
            setCmpprocess(object.optInt("cmpprocess"));
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public long getExptime() {
        return exptime;
    }

    public void setExptime(long exptime) {
        this.exptime = exptime;
    }

    public int getCmpprocess() {
        return cmpprocess;
    }

    public void setCmpprocess(int cmpprocess) {
        this.cmpprocess = cmpprocess;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.rank);
        dest.writeLong(this.exptime);
        dest.writeInt(this.cmpprocess);
    }

    public NobilityInfo() {
    }

    protected NobilityInfo(Parcel in) {
        this.rank = in.readInt();
        this.exptime = in.readLong();
        this.cmpprocess = in.readInt();
    }

    public static final Creator<NobilityInfo> CREATOR = new Creator<NobilityInfo>() {
        @Override
        public NobilityInfo createFromParcel(Parcel source) {
            return new NobilityInfo(source);
        }

        @Override
        public NobilityInfo[] newArray(int size) {
            return new NobilityInfo[size];
        }
    };
}
