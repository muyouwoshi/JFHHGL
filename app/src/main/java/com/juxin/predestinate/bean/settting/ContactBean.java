package com.juxin.predestinate.bean.settting;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

/**
 * 客服信息
 * Created by xy on 2017/6/5.
 */

public class ContactBean extends BaseData {
    private String qq;//客服qq
    private String tel;//电话
    private String work_time;//客服工作时间

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getWork_time() {
        return work_time;
    }

    public void setWork_time(String work_time) {
        this.work_time = work_time;
    }

    @Override
    public void parseJson(String jsonStr) {
        JSONObject jsonObject = getJsonObject(jsonStr);
        this.qq = jsonObject.optString("qq");
        this.work_time = jsonObject.optString("work_time");
        this.tel = jsonObject.optString("tel");
    }
}
