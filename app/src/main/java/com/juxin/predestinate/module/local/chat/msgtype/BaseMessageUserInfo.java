package com.juxin.predestinate.module.local.chat.msgtype;

import android.text.TextUtils;
import com.juxin.library.log.PLogger;
import com.juxin.predestinate.R;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 消息里的用户资料
 * Created by Kind on 2017/8/14.
 */

public class BaseMessageUserInfo {

    //这几个字段 目前基本是给私聊用的
    private String infoJson;//个人资料json
    private String name;// 名称
    private String avatar;// 头像
    private int age;             // 年龄
    private int height;          // 身高
    private int gender;          // 性别 1男2女
    private int distance;           // 距离
    private int localAvatar;// 本地头像
    private int top;
    private String aboutme;// 内心读白
    private int isVip;
    private int kfID;//是否是机器人

    private int nobility_rank;      //用户爵位等级
    private long nobility_cmpprocess;
    private long intimateLevel;     //密友等级

    private int channel_uid; //聊天冲vip/Y币/钥匙/钻石/当前聊天页面/他人的信息:为当前聊天页面用户channel_uid;

    /**
     * 解析 个人资料
     *
     * @param jsonStr
     */
    public void parseInfoJson(String jsonStr) {
        if (TextUtils.isEmpty(jsonStr)) return;
//        PLogger.d("BaseMessageUserInfo=" + jsonStr);
        JSONObject object = getJsonObject(jsonStr);
        this.setAvatar(object.optString("avatar"));
        this.setName(TextUtils.isEmpty(object.optString("remark")) ? object.optString("nickname") : object.optString("remark"));
        this.setIsVip(object.optInt("group"));
        this.setTop(object.optInt("top"));
        this.setGender(object.optInt("gender"));
        this.setAge(object.optInt("age"));
        this.setHeight(object.optInt("height"));
        this.setDistance(object.optInt("distance"));
        this.setIntimateLevel(object.optInt("intimateLevel"));
        this.setChannel_uid(object.optInt("channel_uid"));

        if (!object.isNull("nobility")) {
            JSONObject nobilityJSON = object.optJSONObject("nobility");
            this.setNobility_rank(nobilityJSON.optInt("rank"));
            this.setNobility_cmpprocess(nobilityJSON.optLong("cmpprocess"));
        }
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getInfoJson() {
        return infoJson;
    }

    public void setInfoJson(String infoJson) {
        this.infoJson = infoJson;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAboutme() {
        return aboutme;
    }

    public void setAboutme(String aboutme) {
        this.aboutme = aboutme;
    }

    public int getIsVip() {
        return isVip;
    }

    public void setIsVip(int isVip) {
        this.isVip = isVip;
    }

    public int getKfID() {
        return kfID;
    }

    public void setKfID(int kfID) {
        this.kfID = kfID;
    }

    public int getLocalAvatar() {
        if (localAvatar == 0) {
            localAvatar = R.drawable.default_head;
        }
        return localAvatar;
    }

    public void setLocalAvatar(int localAvatar) {
        this.localAvatar = localAvatar;
    }

    public int getTop() {
        return top;
    }

    public boolean isTop() {
        return top != 0;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getNobility_rank() {
        return nobility_rank;
    }

    public void setNobility_rank(int nobility_rank) {
        this.nobility_rank = nobility_rank;
    }

    public long getNobility_cmpprocess() {
        return nobility_cmpprocess;
    }

    public void setNobility_cmpprocess(long nobility_cmpprocess) {
        this.nobility_cmpprocess = nobility_cmpprocess;
    }

    public long getIntimateLevel() {
        return intimateLevel;
    }

    public void setIntimateLevel(long intimateLevel) {
        this.intimateLevel = intimateLevel;
    }

    public void setChannel_uid(int channel_uid) {
        this.channel_uid = channel_uid;
    }

    public int getChannel_uid(){
        return channel_uid;
    }

    public JSONObject getJsonObject(String str) {
        try {
            if (!TextUtils.isEmpty(str)) {
                return new JSONObject(str);
            }
        } catch (JSONException var3) {
            PLogger.printThrowable(var3);
        }
        return new JSONObject();
    }
}