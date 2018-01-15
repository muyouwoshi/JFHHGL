package com.juxin.predestinate.module.local.chat.msgtype;

import android.os.Bundle;

import com.juxin.library.log.PLogger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 文本消息包括打招呼
 * Created by Kind on 2017/3/31.
 */

public class TextMessage extends BaseMessage {

    private String htm;//HTM文字

    public TextMessage() {
        super();
    }

    @Override
    public BaseMessage parseJson(String jsonStr) {
        super.parseJson(jsonStr);

        this.setHtm(getJsonObject(jsonStr).optString("htm"));
        return this;
    }

    @Override
    public String getJson(BaseMessage message) {
        JSONObject json = new JSONObject();
        try {
            json.put("tid", new JSONArray().put(message.getWhisperID()));
            json.put("mtp", message.getType());
            json.put("mct", message.getMsgDesc());
            json.put("mt", getCurrentTime());
            json.put("d", message.getMsgID());

            return json.toString();
        } catch (JSONException e) {
            PLogger.printThrowable(e);
        }
        return null;
    }

    public String getHtm() {
        return htm;
    }

    public void setHtm(String htm) {
        this.htm = htm;
    }

    public TextMessage(Bundle bundle) {
        super(bundle);
        convertJSON(getJsonStr());
    }

    //私聊列表
    public TextMessage(Bundle bundle, boolean fletter) {
        super(bundle, fletter);
        convertJSON(getJsonStr());
    }

    @Override
    public void convertJSON(String jsonStr) {
        super.convertJSON(jsonStr);
        JSONObject object = getJsonObject(jsonStr);
        this.setHtm(object.optString("htm"));
        this.setMsgDesc(object.optString("mct")); //消息内容
    }
}
