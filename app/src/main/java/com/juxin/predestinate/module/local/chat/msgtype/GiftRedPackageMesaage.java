package com.juxin.predestinate.module.local.chat.msgtype;

import android.os.Bundle;

import com.juxin.library.log.PLogger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 礼物红包版本，红包消息
 * Created by Kind on 2017/10/10.
 */

public class GiftRedPackageMesaage extends BaseMessage {

    private int from_type;//红包消息类型1赠送红包2通知红包已收取3系统已自动帮你领取
    private long red_id;//红包id
    private int red_money;//红包里的钱数，以分为单位

    public GiftRedPackageMesaage() {
        super();
    }


    public GiftRedPackageMesaage(String whisperID, long red_id, int red_money) {
        super(null, whisperID);
        this.setRed_id(red_id);
        this.setRed_money(red_money);
        this.setFrom_type(1);
        this.setType(BaseMessageType.giftRedPackage.getMsgType());
        this.setSpecialMsgID(red_id);
    }

    @Override
    public BaseMessage parseJson(String jsonStr) {
        super.parseJson(jsonStr);
        parseGiftRedPackageJson(getJsonObject(jsonStr));
        return this;
    }

    @Override
    public String getJson(BaseMessage message) {
        JSONObject json = new JSONObject();
        try {
            json.put("tid", new JSONArray().put(message.getWhisperID()));
            json.put("mtp", message.getType());
            json.put("mt", message.getTime());
            json.put("d", message.getMsgID());
            json.put("mct", message.getMsgDesc());

            json.put("from_type", ((GiftRedPackageMesaage) message).getFrom_type());
            json.put("red_id", ((GiftRedPackageMesaage) message).getRed_id());
            json.put("red_money", ((GiftRedPackageMesaage) message).getRed_money());

            return json.toString();
        } catch (JSONException e) {
            PLogger.printThrowable(e);
        }
        return null;
    }

    public int getFrom_type() {
        return from_type;
    }

    public void setFrom_type(int from_type) {
        this.from_type = from_type;
    }

    public long getRed_id() {
        return red_id;
    }

    public void setRed_id(long red_id) {
        this.red_id = red_id;
        this.setSpecialMsgID(red_id);
    }

    public int getRed_money() {
        return red_money;
    }

    public void setRed_money(int red_money) {
        this.red_money = red_money;
    }

    public GiftRedPackageMesaage(Bundle bundle, boolean fletter) {
        super(bundle, fletter);
        convertJSON(getJsonStr());
    }

    public GiftRedPackageMesaage(Bundle bundle) {
        super(bundle);
        convertJSON(getJsonStr());
    }

    @Override
    public void convertJSON(String jsonStr) {
        super.convertJSON(jsonStr);
        JSONObject object = getJsonObject(jsonStr);
        this.setMsgDesc(object.optString("mct")); //消息内容

        parseGiftRedPackageJson(object);
    }

    private void parseGiftRedPackageJson(JSONObject object) {
        this.setFrom_type(object.optInt("from_type"));
        this.setRed_id(object.optLong("red_id"));
        this.setRed_money(object.optInt("red_money"));
        this.setSpecialMsgID(this.getRed_id());
    }
}