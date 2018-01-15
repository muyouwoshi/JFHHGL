package com.juxin.predestinate.ui.user.check.bean;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

/**
 * 当前用户对他人是否接受音视频配置
 * Created by Su on 2017/5/15.
 */

public class VideoSetting extends BaseData {
    private int acceptvideo;      // 视频开关 1：接受 0：不接受
    private int acceptvoice;      // 音频开关 1：接受 0：不接受

    @Override
    public void parseJson(String jsonStr) {
        String jsonData = getJsonObject(jsonStr).optString("res");
        JSONObject jsonObject = getJsonObject(jsonData);

        this.acceptvideo = jsonObject.optInt("acceptvideo");
        this.acceptvoice = jsonObject.optInt("acceptvoice");
    }

    public int getAcceptvideo() {
        return acceptvideo;
    }

    public int getAcceptvoice() {
        return acceptvoice;
    }
}
