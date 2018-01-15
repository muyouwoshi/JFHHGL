package com.juxin.predestinate.ui.live.bean;

import org.json.JSONObject;

/**
 * Created by chengxiaobo on 2017/10/23.
 */

public class LiveConsumeList {


    /**
     * {
     * "nickname": "fff",  //昵称
     * "avatar": "41.jpg", //头像
     * "nobilitylevel:"0   //爵位
     * "rank":1            //排名
     * "vip":1,            //vip
     * "uid": 110874791,   //用户id
     * "gender:"0,         //性别
     * "consume:0,         //消费的钻石数量
     * },{...}
     */

    private String avatar;
    private String nickname;
    private String uid;
    private String title;
    private int gender;
    private int consume;
    private int nobilitylevel;

    /**
     * 解析成对象
     *
     * @param jsonStr
     */
    public void parseJson(JSONObject jsonStr) {

        if (jsonStr != null) {
            avatar = jsonStr.optString("avatar");
            nickname = jsonStr.optString("nickname");
            uid = jsonStr.optString("uid");
            title = jsonStr.optString("title");
            gender = jsonStr.optInt("gender");
            consume = jsonStr.optInt("consume");
            nobilitylevel = jsonStr.optInt("nobilitylevel");
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getConsume() {
        return consume;
    }

    public void setConsume(int consume) {
        this.consume = consume;
    }

    public int getNobilitylevel() {
        return nobilitylevel;
    }

    public void setNobilitylevel(int nobilitylevel) {
        this.nobilitylevel = nobilitylevel;
    }
}
