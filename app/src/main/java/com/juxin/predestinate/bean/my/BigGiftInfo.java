package com.juxin.predestinate.bean.my;


import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

/**
 * 礼物数量对应
 * Created by zm on 17/3/20.
 */
public class BigGiftInfo extends BaseData {

    private int effect_type;//特效类型 1 特大礼物特效
    private String effect_data;//特效数据
    private String effect_id;//特效动画资源ID
    private int count;
    private long from_uid;
    private String from_name;
    private String from_avatar;
    private long to_uid;
    private String to_name;
    private String to_avatar;
    private int gift_id;

    @Override
    public void parseJson(String jsonStr) {
        JSONObject object = getJsonObject(jsonStr);
        this.setEffect_type(object.optInt("effect_type"));
        this.setEffect_data(object.optString("effect_data"));
        this.setEffect_id(object.optString("effect_id"));
        if (object.has("effect_data")){
            object = object.optJSONObject("effect_data");
            this.setCount(object.optInt("count"));
            this.setFrom_uid(object.optLong("from_uid"));
            this.setFrom_name(object.optString("from_name"));
            this.setFrom_avatar(object.optString("from_avatar"));

            this.setTo_uid(object.optLong("to_uid"));
            this.setTo_name(object.optString("to_name"));
            this.setTo_avatar(object.optString("to_avatar"));
            this.setGift_id(object.optInt("gift_id"));
        }
    }

    public int getEffect_type() {
        return effect_type;
    }

    public void setEffect_type(int effect_type) {
        this.effect_type = effect_type;
    }

    public String getEffect_data() {
        return effect_data;
    }

    public void setEffect_data(String effect_data) {
        this.effect_data = effect_data;
    }

    public String getEffect_id() {
        return effect_id;
    }

    public void setEffect_id(String effect_id) {
        this.effect_id = effect_id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getFrom_uid() {
        return from_uid;
    }

    public void setFrom_uid(long from_uid) {
        this.from_uid = from_uid;
    }

    public String getFrom_name() {
        return from_name;
    }

    public void setFrom_name(String from_name) {
        this.from_name = from_name;
    }

    public String getFrom_avatar() {
        return from_avatar;
    }

    public void setFrom_avatar(String from_avatar) {
        this.from_avatar = from_avatar;
    }

    public long getTo_uid() {
        return to_uid;
    }

    public void setTo_uid(long to_uid) {
        this.to_uid = to_uid;
    }

    public String getTo_name() {
        return to_name;
    }

    public void setTo_name(String to_name) {
        this.to_name = to_name;
    }

    public String getTo_avatar() {
        return to_avatar;
    }

    public void setTo_avatar(String to_avatar) {
        this.to_avatar = to_avatar;
    }

    public int getGift_id() {
        return gift_id;
    }

    public void setGift_id(int gift_id) {
        this.gift_id = gift_id;
    }

    @Override
    public String toString() {
        return "BigGiftInfo{" +
                "effect_type=" + effect_type +
                ", effect_data='" + effect_data + '\'' +
                ", effect_id='" + effect_id + '\'' +
                ", count=" + count +
                ", from_uid=" + from_uid +
                ", from_name='" + from_name + '\'' +
                ", from_avatar='" + from_avatar + '\'' +
                ", to_uid=" + to_uid +
                ", to_name='" + to_name + '\'' +
                ", to_avatar='" + to_avatar + '\'' +
                ", gift_id=" + gift_id +
                '}';
    }
}
