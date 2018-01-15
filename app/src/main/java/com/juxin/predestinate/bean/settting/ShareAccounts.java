package com.juxin.predestinate.bean.settting;

import android.text.TextUtils;

import com.juxin.predestinate.BuildConfig;
import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

/**
 * 分享帐号信息
 *
 * @author ZRP
 * @date 2017/11/7
 */
public class ShareAccounts extends BaseData {

    /**
     * qq分享app_id
     */
    private String qqAppId;
    /**
     * qq分享app_key
     */
    private String qqAppKey;

    /**
     * 微信分享app_id
     */
    private String wxAppId;
    /**
     * 微信分享app_key
     */
    private String wxAppKey;

    @Override
    public void parseJson(String jsonStr) {
        JSONObject jsonObject = getJsonObjectRes(jsonStr);
        qqAppId = jsonObject.optString("qq_app_id");
        qqAppKey = jsonObject.optString("qq_app_key");

        wxAppId = jsonObject.optString("wx_app_id");
        wxAppKey = jsonObject.optString("wx_app_key");
    }

    public String getQqAppId() {
        return TextUtils.isEmpty(qqAppId) ? BuildConfig.QQ_SHARE_APPID : qqAppId;
    }

    public String getQqAppKey() {
        return qqAppKey;
    }

    public String getWxAppId() {
        return TextUtils.isEmpty(wxAppId) ? BuildConfig.WX_SHARE_APPID : wxAppId;
    }

    public String getWxAppKey() {
        return wxAppKey;
    }

    @Override
    public String toString() {
        return "ShareAccounts{" +
                "qqAppId='" + qqAppId + '\'' +
                ", qqAppKey='" + qqAppKey + '\'' +
                ", wxAppId='" + wxAppId + '\'' +
                ", wxAppKey='" + wxAppKey + '\'' +
                '}';
    }
}
