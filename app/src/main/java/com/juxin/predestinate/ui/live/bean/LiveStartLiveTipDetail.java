package com.juxin.predestinate.ui.live.bean;

import org.json.JSONObject;

import java.io.Serializable;


/**
 * 开播提醒
 * <p>
 * Created by chengxiaobo on 2017/10/26.
 */

public class LiveStartLiveTipDetail implements Serializable {

    /**
     * {
     * "age": 0,
     * "avatar": "http:\/\/image1.yuanfenba.net\/uploads\/oss\/mrtx02.png",
     * "avatarstatus": 3,
     * "gender": 2,
     * "group": 1,
     * "height": 0,
     * "nickname": "\u56fe\u5f3a\u6bdb\u5dfe-\u674e\u5a1c",
     * "nobility": 0, //爵位等级
     * "notify": 0,  // 1 开启上线通知 0 关闭
     * "remark": "", //备注
     * "uid": 15690
     * },
     */

    private String avatar;
    private String nickname;
    private String uid;
    private int nobility; //爵位等级
    private int gender;
    private int notify;//是提醒开播

    public static final int NOTIFY_ON = 1;
    public static final int NOTIFY_OFF = 0;

    /**
     * 解析成对象
     *
     * @param jsonStr
     */
    public void parseJson(JSONObject jsonStr) {

        if (jsonStr != null) {
            avatar = jsonStr.optString("avatar");
            nickname = jsonStr.optString("nickname");
            nobility = jsonStr.optInt("nobility");
            uid = jsonStr.optString("uid");
            gender = jsonStr.optInt("gender");
            notify = jsonStr.optInt("notify");
        }
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getNobility() {
        return nobility;
    }

    public void setNobility(int nobility) {
        this.nobility = nobility;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getNotify() {
        return notify;
    }

    public void setNotify(int notify) {
        this.notify = notify;
    }
}
