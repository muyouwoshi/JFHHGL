package com.juxin.predestinate.module.local.chat.msgtype;

import android.os.Bundle;

import org.json.JSONObject;

/**
 * 小秘书小图标消息
 * Created by Kind on 2017/7/17.
 */

public class SmallIconMessage extends BaseMessage {

    private String info;//消息详细文本
    private String icon;//图标
    private String btn_text;//按钮文字
    private String btn_action;//按钮动作

    @Override
    public BaseMessage parseJson(String jsonStr) {
        super.parseJson(jsonStr);
        parseNobilityJson(getJsonObject(jsonStr));
        return this;
    }

    @Override
    public String getJson(BaseMessage message) {
        return super.getJson(message);
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getBtn_text() {
        return btn_text;
    }

    public void setBtn_text(String btn_text) {
        this.btn_text = btn_text;
    }

    public String getBtn_action() {
        return btn_action;
    }

    public void setBtn_action(String btn_action) {
        this.btn_action = btn_action;
    }

    public SmallIconMessage(){
        super();
    }

    public SmallIconMessage(Bundle bundle) {
        super(bundle);
        convertJSON(getJsonStr());
    }

    //私聊列表
    public SmallIconMessage(Bundle bundle, boolean fletter) {
        super(bundle, fletter);
        convertJSON(getJsonStr());
    }

    @Override
    public void convertJSON(String jsonStr) {
        super.convertJSON(jsonStr);
        JSONObject object = getJsonObject(jsonStr);
        this.setMsgDesc(object.optString("mct")); //消息内容
        parseNobilityJson(object);
    }

    private void parseNobilityJson(JSONObject object) {
        this.setInfo(object.optString("info"));
        this.setIcon(object.optString("icon"));
        this.setBtn_text(object.optString("btn_text"));
        this.setBtn_action(object.optString("btn_action"));
    }
}
