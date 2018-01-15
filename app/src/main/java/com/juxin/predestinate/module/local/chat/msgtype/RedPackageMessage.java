package com.juxin.predestinate.module.local.chat.msgtype;

import org.json.JSONObject;

/**
 * 红包消息类型
 * @author Mr.Huang
 * @date 2017-07-18
 */
public class RedPackageMessage extends BaseMessage {

    private Integer fid;//发送者uid
    private Integer redPackageId;//红包ID
    private Double money;//钱数

    @Override
    public BaseMessage parseJson(String jsonStr) {
        super.parseJson(jsonStr);
        JSONObject object = getJsonObject(jsonStr);

        this.fid = object.optInt("fid");
        this.redPackageId = object.optInt("red_id");
        this.money = object.optDouble("red_money");

        return this;
    }

    public Integer getFid() {
        return fid;
    }

    public void setFid(Integer fid) {
        this.fid = fid;
    }

    public Integer getRedPackageId() {
        return redPackageId;
    }

    public void setRedPackageId(Integer redPackageId) {
        this.redPackageId = redPackageId;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

}
