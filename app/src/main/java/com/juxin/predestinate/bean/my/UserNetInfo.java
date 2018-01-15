package com.juxin.predestinate.bean.my;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

/**
 * 创建日期：2017/6/21
 * 描述:对方网络状态
 * 作者:lc
 */
public class UserNetInfo extends BaseData {
    private boolean isOk;
    private String netType;              //用户上网方式（2017-06-20）Wifi 1 4G 2 3G/2G 3 其它 4
    private String phoneInfo;      //用户设备描述

    @Override
    public void parseJson(String s) {
        JSONObject jsonObject = getJsonObject(s);
        if ("ok".equalsIgnoreCase(jsonObject.optString("status"))){
            this.setOk(true);
        }else {
            this.setOk(false);
        }
        if (jsonObject.has("res")){
            String tempType = "";
            int netType = jsonObject.optJSONObject("res").optInt("net_tp");
            switch (netType) {
                case 1:
                    tempType = "Wifi";
                    break;
                case 2:
                    tempType = "4G";
                    break;
                case 3:
                    tempType = "3G/2G";
                    break;
                case 4:
                    tempType = "其它";
                    break;
                default:
                    tempType = "其它";
                    break;
            }
            this.setNetType(tempType);
            this.setPhoneInfo(jsonObject.optJSONObject("res").optString("phone_info"));
        }
    }

    public boolean isOk() {
        return isOk;
    }

    public void setOk(boolean ok) {
        isOk = ok;
    }

    public String getNetType() {
        return netType;
    }

    public void setNetType(String netType) {
        this.netType = netType;
    }

    public String getPhoneInfo() {
        return phoneInfo;
    }

    public void setPhoneInfo(String phoneInfo) {
        this.phoneInfo = phoneInfo;
    }

    @Override
    public String toString() {
        return "UserNetInfo{" +
                //                    "uid=" + uid +
                //                    ", avatar=" + avatar +
                //                    ", nickname=" + nickname +
                //                    ", gender=" + gender +
                //                    ", score=" + score +
                //                    ", exp=" + exp +
                '}';
    }
}
