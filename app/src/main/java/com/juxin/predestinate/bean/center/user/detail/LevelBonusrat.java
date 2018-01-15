package com.juxin.predestinate.bean.center.user.detail;

import android.os.Parcel;
import android.os.Parcelable;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

/**
 * 创建日期：2017/7/18
 * 描述:等级利率
 * 作者:lc
 */
public class LevelBonusrat extends BaseData implements Parcelable {
    private int bonusrate;          //活跃等级收益比例
    private int level;              //活跃等级 0= C级 1=B级 2=A级

    @Override
    public void parseJson(String jsonStr) {
        JSONObject jsonObj = getJsonObject(jsonStr);
        setBonusrate(jsonObj.optInt("bonusrate"));
        setLevel(jsonObj.optInt("level"));
    }

    public int getBonusrate() {
        return bonusrate;
    }

    public void setBonusrate(int bonusrate) {
        this.bonusrate = bonusrate;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.bonusrate);
        dest.writeInt(this.level);
    }

    public LevelBonusrat() {
    }

    protected LevelBonusrat(Parcel in) {
        this.bonusrate = in.readInt();
        this.level = in.readInt();
    }

    public static final Creator<LevelBonusrat> CREATOR = new Creator<LevelBonusrat>() {
        @Override
        public LevelBonusrat createFromParcel(Parcel source) {
            return new LevelBonusrat(source);
        }

        @Override
        public LevelBonusrat[] newArray(int size) {
            return new LevelBonusrat[size];
        }
    };
}
