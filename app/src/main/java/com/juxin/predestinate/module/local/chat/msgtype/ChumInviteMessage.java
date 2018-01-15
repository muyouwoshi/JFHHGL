package com.juxin.predestinate.module.local.chat.msgtype;

import android.os.Bundle;
import android.text.TextUtils;
import com.juxin.library.log.PLogger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 密友邀请
 * Created by Kind on 2017/7/17.
 */

public class ChumInviteMessage extends BaseMessage {

    private int close_tp;//密友消息类型1邀请密友2接受邀请3拒绝邀请
    private long invite_id;//邀请ID
    private int gift_id;//夹带的礼物ID
    private long expire;//邀请超时时间

    public ChumInviteMessage(String whisperID, int giftID) {
        super(null, whisperID);
        this.setGift_id(giftID);
        this.setClose_tp(1);
        this.setType(BaseMessageType.chumInvite.getMsgType());
    }

    @Override
    public BaseMessage parseJson(String jsonStr) {
        super.parseJson(jsonStr);
        parseChumInviteJson(getJsonObject(jsonStr));
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

            if (!TextUtils.isEmpty(message.getMsgDesc())) {
                json.put("mct", message.getMsgDesc());
            }
            json.put("close_tp", ((ChumInviteMessage) message).getClose_tp());
            json.put("invite_id", ((ChumInviteMessage) message).getInvite_id());
            json.put("gift_id", ((ChumInviteMessage) message).getGift_id());
            json.put("expire", ((ChumInviteMessage) message).getExpire());
            if (((ChumInviteMessage) message).getClose_tp() == 3){
                json.put("fid", message.getSendID());
            }

            return json.toString();
        } catch (JSONException e) {
            PLogger.printThrowable(e);
        }
        return null;
    }

    public int getClose_tp() {
        return close_tp;
    }

    public void setClose_tp(int close_tp) {
        this.close_tp = close_tp;
    }

    public long getInvite_id() {
        return invite_id;
    }

    public void setInvite_id(long invite_id) {
        this.setSpecialMsgID(invite_id);
        this.invite_id = invite_id;
    }

    public int getGift_id() {
        return gift_id;
    }

    public void setGift_id(int gift_id) {
        this.gift_id = gift_id;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public ChumInviteMessage(){
        super();
    }

    public ChumInviteMessage(Bundle bundle) {
        super(bundle);
        convertJSON(getJsonStr());
    }

    //私聊列表
    public ChumInviteMessage(Bundle bundle, boolean fletter) {
        super(bundle, fletter);
        convertJSON(getJsonStr());
    }

    @Override
    public void convertJSON(String jsonStr) {
        super.convertJSON(jsonStr);
        JSONObject object = getJsonObject(jsonStr);
        this.setMsgDesc(object.optString("mct")); //消息内容
        parseChumInviteJson(object);
    }

    private void parseChumInviteJson(JSONObject object) {
        this.setClose_tp(object.optInt("close_tp"));
        this.setInvite_id(object.optLong("invite_id"));
        this.setGift_id(object.optInt("gift_id"));
        this.setExpire(object.optLong("expire"));
        this.setSpecialMsgID(getInvite_id());
    }
}
