package com.juxin.predestinate.ui.agora.act.bean;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

/**
 * 聊天状态：更新付费信息
 * Created by Su on 2017/4/12.
 */

public class ChatStatus extends BaseData {
    private int chatState;  // 聊天状态 1：聊天中 2：已挂断
    private long timeLeft;  // 从扣费方余额算出的剩余秒数
    private int diamondLeft;// 钻石余额，扣费方包含此字段

    @Override
    public void parseJson(String jsonStr) {
        String jsonData = getJsonObject(jsonStr).optString("res");
        JSONObject jsonObject = getJsonObject(jsonData);

        this.setChatState(jsonObject.optInt("chat_stat"));
        this.setTimeLeft(jsonObject.optLong("time_left"));
        this.setDiamondLeft(jsonObject.optInt("diamond_left", -1));
    }

    public int getChatState() {
        return chatState;
    }

    public void setChatState(int chatState) {
        this.chatState = chatState;
    }

    public long getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(long timeLeft) {
        this.timeLeft = timeLeft;
    }

    public int getDiamondLeft() {
        return diamondLeft;
    }

    public void setDiamondLeft(int diamondLeft) {
        this.diamondLeft = diamondLeft;
    }
}
