package com.juxin.predestinate.bean.config.base;

import android.text.TextUtils;

import com.juxin.predestinate.bean.net.BaseData;
import com.juxin.predestinate.module.logic.config.Hosts;

import org.json.JSONObject;

/**
 * config/GetSet#common_cofig节点
 * Created by ZRP on 2017/7/19.
 */

public class Common extends BaseData {

    private int secretary_dialog;   //小秘书对话框是否开放，1为开放，0为不开放
    private String service_qq;      //客服QQ
    private String square_url;      //广场页面地址
    private int free_speak_level;   //可以免费聊天的爵位等级

    @Override
    public void parseJson(String jsonStr) {
        JSONObject jsonObject = getJsonObject(jsonStr);
        secretary_dialog = jsonObject.optInt("secretary_dialog");
        service_qq = jsonObject.optString("service_qq");
        square_url = jsonObject.optString("square_url");
        free_speak_level = jsonObject.optInt("free_speak_level");
    }

    /**
     * @return 小秘书对话框是否开放，暂时只判断是否为1
     */
    public boolean canSecretaryShow() {
        return secretary_dialog == 1;
    }

    // -----------------------------------------

    public int getSecretary_dialog() {
        return secretary_dialog;
    }

    public String getService_qq() {
        return service_qq;
    }

    public String getSquare_url() {
        return TextUtils.isEmpty(square_url) ? Hosts.LOCAL_SQUARE_URL : square_url;
    }

    public int getFree_speak_level() {
        return free_speak_level;
    }

    @Override
    public String toString() {
        return "Common{" +
                "secretary_dialog=" + secretary_dialog +
                ", service_qq='" + service_qq + '\'' +
                ", square_url='" + square_url + '\'' +
                '}';
    }
}
