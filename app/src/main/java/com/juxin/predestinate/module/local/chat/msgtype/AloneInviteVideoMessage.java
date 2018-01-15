package com.juxin.predestinate.module.local.chat.msgtype;

import android.os.Bundle;
import android.text.TextUtils;
import com.juxin.library.log.PLogger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 女用户单独视频邀请
 * Created by Kind on 2017/6/21.
 */

public class AloneInviteVideoMessage extends BaseMessage {

    private long vc_id;
    private int vc_tp;
    private int media_tp;
    private long vc_price;

    public AloneInviteVideoMessage() {
        super();
    }

    @Override
    public BaseMessage parseJson(String jsonStr) {
        super.parseJson(jsonStr);
        JSONObject object = getJsonObject(jsonStr);
        this.setType(object.optInt("mtp")); //消息类型
        this.setMsgDesc(object.optString("mct")); //消息内容
        this.setTime(object.optLong("mt")); //消息时间 int64
        this.setKnow(object.optInt("know"));

        parseCommonJson(object);
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

            return json.toString();
        } catch (JSONException e) {
            PLogger.printThrowable(e);
        }
        return null;
    }

    public AloneInviteVideoMessage(Bundle bundle, boolean fletter) {
        super(bundle, fletter);
        convertJSON(getJsonStr());
    }

    /**
     * 转换类 fmessage
     */
    public AloneInviteVideoMessage(Bundle bundle) {
        super(bundle);
        convertJSON(getJsonStr());
    }

    public long getVc_id() {
        return vc_id;
    }

    public void setVc_id(long vc_id) {
        this.vc_id = vc_id;
    }

    public int getVc_tp() {
        return vc_tp;
    }

    public void setVc_tp(int vc_tp) {
        this.vc_tp = vc_tp;
    }

    public int getMedia_tp() {
        return media_tp;
    }

    public void setMedia_tp(int media_tp) {
        this.media_tp = media_tp;
    }

    public long getVc_price() {
        return vc_price;
    }

    public void setVc_price(long vc_price) {
        this.vc_price = vc_price;
    }

    @Override
    public void convertJSON(String jsonStr) {
        super.convertJSON(jsonStr);
        JSONObject object = getJsonObject(jsonStr);
        this.setMsgDesc(object.optString("mct")); //消息内容

        parseCommonJson(object);
    }

    /**
     * 礼物消息数据解析
     */
    private void parseCommonJson(JSONObject object) {
        this.setVc_id(object.optLong("vc_id"));
        this.setVc_tp(object.optInt("vc_tp"));
        this.setMedia_tp(object.optInt("media_tp"));
        this.setVc_price(object.optLong("vc_price"));
    }
}