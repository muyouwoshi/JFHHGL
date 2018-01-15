package com.juxin.predestinate.bean.my;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

/**
 * 红包接口返回
 * Created by zm on 2017/10/12.
 */

public class RedbagInfo extends BaseData {

    private long red_id;      //红包id
    private int red_amount;   //红包金额
    private long msg_id;      //消息ID

    @Override
    public void parseJson(String jsonStr) {
        JSONObject jsonObject = getJsonObjectRes(jsonStr);
        this.setRed_id(jsonObject.optLong("id"));
        this.setRed_amount(jsonObject.optInt("amount"));
        this.setMsg_id(jsonObject.optLong("msg_id"));
    }

    public long getRed_id() {
        return red_id;
    }

    public void setRed_id(long red_id) {
        this.red_id = red_id;
    }

    public int getRed_amount() {
        return red_amount;
    }

    public void setRed_amount(int red_amount) {
        this.red_amount = red_amount;
    }

    public long getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(long msg_id) {
        this.msg_id = msg_id;
    }

    @Override
    public String toString() {
        return "RedbagInfo{" +
                "red_id=" + red_id +
                ", red_amount=" + red_amount +
                '}';
    }
}
