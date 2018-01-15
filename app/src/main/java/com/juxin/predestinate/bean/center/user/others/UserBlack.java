package com.juxin.predestinate.bean.center.user.others;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

/**
 * 用户拉黑状态
 * Created by Su on 2017/5/23.
 */

public class UserBlack extends BaseData {
    private int inblack;  // 是否黑名单 1为黑名单 0 不是黑名单

    @Override
    public void parseJson(String jsonStr) {
        String jsonData = getJsonObject(jsonStr).optString("res");
        JSONObject jsonObject = getJsonObject(jsonData);

        this.inblack = jsonObject.optInt("inblack");
    }

    public boolean inBlack() {
        return inblack == 1;
    }
}
