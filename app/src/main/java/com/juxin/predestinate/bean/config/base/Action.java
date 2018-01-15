package com.juxin.predestinate.bean.config.base;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

/**
 * config/GetSet#web_activity_config节点
 * Created by ZRP on 2017/7/19.
 */

public class Action extends BaseData {

    private double dialog_rate; //活动弹框展示宽高比
    private int show;           //是否进行活动推送的展示，1的时候展示，其他关闭
    private String url;         //活动页面地址

    @Override
    public void parseJson(String jsonStr) {
        JSONObject jsonObject = getJsonObject(jsonStr);
        dialog_rate = jsonObject.optDouble("dialog_rate");
        show = jsonObject.optInt("show");
        url = jsonObject.optString("url");
    }

    /**
     * @return 是否展示活动弹窗
     */
    public boolean canPushShow() {
        return show == 1;
    }

    // -----------------------------------------

    public double getDialog_rate() {
        return dialog_rate;
    }

    public int getShow() {
        return show;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "Action{" +
                "dialog_rate=" + dialog_rate +
                ", show=" + show +
                ", url='" + url + '\'' +
                '}';
    }
}
