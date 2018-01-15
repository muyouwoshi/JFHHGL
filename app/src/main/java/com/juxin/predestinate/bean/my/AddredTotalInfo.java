package com.juxin.predestinate.bean.my;


import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

import java.util.List;

/**
 * 提现信息
 * Created by zm on 17/3/20.
 */
public class AddredTotalInfo extends BaseData {

    private List redbagLists;
    private String msg;
    private double sum;
    @Override
    public void parseJson(String s) {
        JSONObject jsonObject = getJsonObject(s);
        redbagLists = getBaseDataList(jsonObject.optJSONArray("result"), RedbagList.RedbagInfo.class);
        this.setMsg(jsonObject.optString("msg"));
        if (jsonObject.has("res")){
            this.setSum(jsonObject.optJSONObject("res").optDouble("sum"));
        }
    }

    public List getRedbagLists() {
        return redbagLists;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum/100f;
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
