package com.juxin.predestinate.bean.discover;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

/**
 * 创建日期：2017/9/6
 * 描述:活动礼包回馈
 * 作者:lc
 */
public class Active extends BaseData {
    private int pack_id;                //礼包id
    private String vip_days;            //VIP剩余天数
    private String ycoin;               //YB数量
    private int keys;                   //钥匙数量
    private int diamonds;               //钻石数量
    private int exp;                    //经验值
    private String cost;                //
    private boolean is_b_version;       //是否是B版

    @Override
    public void parseJson(String jsonStr) {
        JSONObject jsonObject = getJsonObject(jsonStr);
        this.setPack_id(jsonObject.optInt("pack_id"));
        this.setVip_days(jsonObject.optString("vip_days"));
        this.setYcoin(jsonObject.optString("ycoin"));
        this.setKeys(jsonObject.optInt("keys"));
        this.setDiamonds(jsonObject.optInt("diamonds"));
        this.setExp(jsonObject.optInt("exp"));
        this.setCost(jsonObject.optString("cost"));
        this.setIs_b_version(jsonObject.optBoolean("is_b_version"));
    }

    public int getPack_id() {
        return pack_id;
    }

    public void setPack_id(int pack_id) {
        this.pack_id = pack_id;
    }

    public String getVip_days() {
        return vip_days;
    }

    public void setVip_days(String vip_days) {
        this.vip_days = vip_days;
    }

    public String getYcoin() {
        return ycoin;
    }

    public void setYcoin(String ycoin) {
        this.ycoin = ycoin;
    }

    public int getKeys() {
        return keys;
    }

    public void setKeys(int keys) {
        this.keys = keys;
    }

    public int getDiamonds() {
        return diamonds;
    }

    public void setDiamonds(int diamonds) {
        this.diamonds = diamonds;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public boolean isB() {
        return is_b_version;
    }

    public void setIs_b_version(boolean is_b_version) {
        this.is_b_version = is_b_version;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }
}
