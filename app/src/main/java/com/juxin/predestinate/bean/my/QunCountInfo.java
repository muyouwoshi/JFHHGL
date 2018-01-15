package com.juxin.predestinate.bean.my;


import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

/**
 * 提现信息
 * Created by zm on 17/3/20.
 */
public class QunCountInfo extends BaseData {

    private int count;
    private boolean isOk;

    @Override
    public void parseJson(String s) {
        JSONObject jsonObject = getJsonObject(s);
        if ("ok".equalsIgnoreCase(jsonObject.optString("status"))){
            this.setIsOk(true);
        }else {
            this.setIsOk(false);
        }
        if (jsonObject.has("res")){
            this.setCount(jsonObject.optJSONObject("res").optInt("count"));
        }
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isOk() {
        return isOk;
    }

    public void setIsOk(boolean isOk) {
        this.isOk = isOk;
    }

    @Override
    public String toString() {
        return "RankList{" +
                //                    "uid=" + uid +
                //                    ", avatar=" + avatar +
                //                    ", nickname=" + nickname +
                //                    ", gender=" + gender +
                //                    ", score=" + score +
                //                    ", exp=" + exp +
                '}';
    }
}
