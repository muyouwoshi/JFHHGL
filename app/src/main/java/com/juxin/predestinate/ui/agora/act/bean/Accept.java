package com.juxin.predestinate.ui.agora.act.bean;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

/**
 * 接受请求
 * Created by Su on 2017/4/21.
 */
public class Accept extends BaseData {
    private String channelKey;       // 渠道加密Key
    private int msgVer;              // 消息版本号

    @Override
    public void parseJson(String jsonStr) {
        String jsonData = getJsonObject(jsonStr).optString("res");
        JSONObject jsonObject = getJsonObject(jsonData);
        this.setChannelKey(jsonObject.optString("channel_key"));
        this.setMsgVer(jsonObject.optInt("confer_msgver"));
    }

    public int getMsgVer() {
        return msgVer;
    }

    public void setMsgVer(int msgVer) {
        this.msgVer = msgVer;
    }

    public String getChannelKey() {
        return channelKey;
    }

    public void setChannelKey(String channelKey) {
        this.channelKey = channelKey;
    }
}