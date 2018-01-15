package com.juxin.predestinate.bean.config.base;

import android.os.Parcel;
import android.os.Parcelable;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 商品在线配置
 * Created by ZRP on 2017/4/18.
 */
public class Goods extends BaseData implements Serializable, Parcelable {

    private int num;        //购买个数
    private int cost;       //商品价格
    private int pid;        //商品ID
    private String name;    //商品名称
    private String desc;    //商品描述

    @Override
    public void parseJson(String jsonStr) {
        JSONObject jsonObject = getJsonObject(jsonStr);
        this.setNum(jsonObject.optInt("num"));
        this.setCost(jsonObject.optInt("cost"));
        this.setPid(jsonObject.optInt("pid"));
        this.setName(jsonObject.optString("name"));
        this.setDesc(jsonObject.optString("desc"));
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "Goods{" +
                "num=" + num +
                ", cost=" + cost +
                ", pid=" + pid +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.num);
        dest.writeInt(this.cost);
        dest.writeInt(this.pid);
        dest.writeString(this.name);
        dest.writeString(this.desc);
    }

    public Goods() {
    }

    public Goods(String str) {
        parseJson(str);
    }

    protected Goods(Parcel in) {
        this.num = in.readInt();
        this.cost = in.readInt();
        this.pid = in.readInt();
        this.name = in.readString();
        this.desc = in.readString();
    }

    public static final Parcelable.Creator<Goods> CREATOR = new Parcelable.Creator<Goods>() {
        @Override
        public Goods createFromParcel(Parcel source) {
            return new Goods(source);
        }

        @Override
        public Goods[] newArray(int size) {
            return new Goods[size];
        }
    };
}
