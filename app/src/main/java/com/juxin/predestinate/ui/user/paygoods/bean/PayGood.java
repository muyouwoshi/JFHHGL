package com.juxin.predestinate.ui.user.paygoods.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.juxin.predestinate.bean.net.BaseData;
import com.juxin.predestinate.module.util.ChineseFilter;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 单个商品信息
 * <p>
 * Created by Su on 2016/9/18.
 */
public class PayGood extends BaseData implements Serializable, Parcelable {
    private int id;     //商品ID
    private int sub_id; //二级商品ID：目前用于新Y币弹框中勾选开通vip后的商品交易ID
    private String name;//商品名称
    private int num; // vip: 有效天数，钻石: 钻石数
    private String desc;//商品活动, 例如：优惠50%/首冲三个月送100话费
    private String privilege;// 商品优惠提示
    private double price;//商品原价(分),置灰的价格

    // 未提供字段
    private String icon; // 图标url
    private int type; // 1-包月写信， 2-vip会员， 3-钻石充值
    private double discount; // 商品折扣价格(分)-实际显示的价格

    @Override
    public void parseJson(String s) {
        JSONObject jsonObject = getJsonObject(s);
        this.setId(jsonObject.optInt("cid"));
        this.setName(jsonObject.optString("cname"));
        this.setNum(jsonObject.optInt("cnum"));
        this.setDesc(jsonObject.optString("desc"));
        this.setPrivilege(jsonObject.optString("privilege"));
        this.setPrice(jsonObject.optDouble("price"));
        this.setIcon(jsonObject.optString("curl"));
        this.setType(jsonObject.optInt("ctype"));
        this.setDiscount(jsonObject.optDouble("discount"));

        this.sub_id = jsonObject.optInt("sub_cid");
    }

    public String getPrivilege() {
        return privilege;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    public int getSub_id() {
        return sub_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getDiscount() {
        return ChineseFilter.subZeroString(ChineseFilter.formatNum(discount, 2) + "");
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public double getDoublePrice() {
        return price;
    }

    public String getPrice() {
        return ChineseFilter.subZeroString(ChineseFilter.formatNum(price, 2) + "");
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String iconUrl) {
        this.icon = iconUrl;
    }

    @Override
    public String toString() {
        return "PayGood{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                ", type=" + type +
                ", num=" + num +
                ", desc='" + desc + '\'' +
                ", price=" + price +
                ", discount=" + discount +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.icon);
        dest.writeInt(this.type);
        dest.writeInt(this.num);
        dest.writeString(this.desc);
        dest.writeDouble(this.price);
        dest.writeDouble(this.discount);
    }

    public PayGood() {
    }

    public PayGood(String str) {
        parseJson(str);
    }

    protected PayGood(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.icon = in.readString();
        this.type = in.readInt();
        this.num = in.readInt();
        this.desc = in.readString();
        this.price = in.readDouble();
        this.discount = in.readDouble();
    }

    public static final Creator<PayGood> CREATOR = new Creator<PayGood>() {
        @Override
        public PayGood createFromParcel(Parcel source) {
            return new PayGood(source);
        }

        @Override
        public PayGood[] newArray(int size) {
            return new PayGood[size];
        }
    };
}
