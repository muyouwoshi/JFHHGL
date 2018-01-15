package com.juxin.predestinate.bean.my;


import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

/**
 * 标签列表
 * Created by zm on 17/3/20.
 */
public class PhoneVerifyInfo extends BaseData {

    public static final int VERIFICATIONCODE_ERROR = 0;
    public static final int VERIFICATIONCODE_SUCCEED = 1;
    public static final int PHONE_INKED = 2;
    public static final int OTHER_INKED = 3;

    private int resCode;

    public int getResCode() {
        return resCode;
    }

    public void setResCode(int resCode) {
        this.resCode = resCode;
    }

    @Override
    public void parseJson(String s) {
        JSONObject jsonObject = getJsonObject(s);
        this.setResCode(getPhoneVerify(jsonObject));
    }
    /**
     * 手机验证提交手机解析 result 0 失败,1 成功,2 当前帐号已绑定手机,3 当前手机号已被别人绑定
     *
     * @author dengxiaohong
     */
    public int getPhoneVerify(JSONObject jsonObject){
        String resCode = jsonObject.optString("respCode");
        int errno = jsonObject.optInt("errNo");
        if (resCode == null) {
            return 0;
        }
        if (resCode.equals("success")) {
            return 1;
        }
        if (errno == 1) {
            return 2;
        } else if (errno == 2) {
            return 3;
        }
        return 0;
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
