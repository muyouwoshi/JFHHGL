package com.juxin.predestinate.bean.config.base;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

/**
 * 聊天开关控制
 * Created by Kind on 2017/11/10.
 */

public class TalkConfig extends BaseData {

    private boolean free_talk_one;//true可以免费聊一条

    @Override
    public void parseJson(String jsonStr) {
        JSONObject jsonObject = getJsonObject(jsonStr);
        this.setFree_talk_one(jsonObject.optBoolean("free_talk_one"));
    }

    public boolean isFree_talk_one() {
        return free_talk_one;
    }

    public void setFree_talk_one(boolean free_talk_one) {
        this.free_talk_one = free_talk_one;
    }
}
