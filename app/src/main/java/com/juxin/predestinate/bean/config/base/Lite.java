package com.juxin.predestinate.bean.config.base;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

/**
 * config/GetSet#android_config节点
 * Created by ZRP on 2017/7/19.
 */

public class Lite extends BaseData {

    // 轻量包升级完整包控制字段
    private int forceup_login_num;  //第几次登录强制升级完整包
    private int forceup_switch;     //强制升级完整包

    @Override
    public void parseJson(String jsonStr) {
        JSONObject jsonObject = getJsonObject(jsonStr);
        forceup_login_num = jsonObject.optInt("forceup_login_num");
        forceup_switch = jsonObject.optInt("forceup_switch");
    }

    /**
     * @return 是否强制升级完整包
     */
    public boolean isForceup_switch() {
        return forceup_switch == 1;
    }

    // ------------------------

    public int getForceup_login_num() {
        return forceup_login_num;
    }

    public int getForceup_switch() {
        return forceup_switch;
    }

    @Override
    public String toString() {
        return "Lite{" +
                "forceup_login_num=" + forceup_login_num +
                ", forceup_switch=" + forceup_switch +
                '}';
    }
}
