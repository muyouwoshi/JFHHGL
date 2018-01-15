package com.juxin.predestinate.bean.my;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 直播认证状态
 * @author gwz
 * 创建日期:2017.10.25
 */

public class ApplyLiveStatusBean extends BaseData {
    private static final String OK = "ok";
    public int status;
    public int level;
    @Override
    public void parseJson(String jsonStr) {
        try {
            JSONObject jo = new JSONObject(jsonStr);
            String s = jo.optString("status");
            if(OK.equals(s)) {
                JSONObject resJo = jo.getJSONObject("res");
                status = resJo.getInt("status");
                level = resJo.getInt("level");
            }
        }catch (JSONException e){

        }
    }
}
