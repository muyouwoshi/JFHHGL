package com.juxin.predestinate.ui.agora.act.bean;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

/**
 * 发起聊天
 * Created by Su on 2017/4/17.
 */

public class Invited extends BaseData {
    private int vcId;       // 聊天渠道ID
    private int msgVer;     // 消息版本号

    @Override
    public void parseJson(String jsonStr) {
        String jsonData = getJsonObject(jsonStr).optString("res");
        JSONObject jsonObject = getJsonObject(jsonData);
        this.setVcId(jsonObject.optInt("vc_id"));
        this.setMsgVer(jsonObject.optInt("confer_msgver"));
    }

    public int getVcId() {
        return vcId;
    }

    public void setVcId(int vcId) {
        this.vcId = vcId;
    }

    public int getMsgVer() {
        return msgVer;
    }

    public void setMsgVer(int msgVer) {
        this.msgVer = msgVer;
    }
}
