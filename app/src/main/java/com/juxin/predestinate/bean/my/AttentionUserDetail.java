package com.juxin.predestinate.bean.my;

import android.text.TextUtils;

import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.bean.net.BaseData;
import com.juxin.predestinate.module.util.my.AttentionUtil;

import org.json.JSONObject;

/**
 * 关注列表
 * Created by zm on 17/3/20.
 */
public class AttentionUserDetail extends BaseData {

    private String login_id;
    private String other_id;
    private long follow_time;
    private int city;
    private String nickname;
    private String remark;      // 备注名
    private int age;
    private int photoNum;
    private boolean is_vip;
    private String avatar;
    private int gender;
    private int avatar_status;
    private int type; // 有没有被我关注，0 没有，1 双向关注
    private int isRead;//是否已读 0未读 1已读
    private long uid;
    private int kf_id;

    @Override
    public void parseJson(String s) {
        JSONObject jsonObject = getJsonObject(s);
        if (jsonObject.has("type"))
            this.setType(getJsonObject(s).optInt("type"));
        this.setUid(jsonObject.optLong("uid"));
        this.setAge(jsonObject.optInt("age"));
        this.setAvatar(jsonObject.optString("avatar"));
        this.setAvatar_status(jsonObject.optInt("avatar_status"));
        this.setCity(jsonObject.optInt("city"));
        this.setGender(jsonObject.optInt("gender"));
        this.setIs_vip(jsonObject.optBoolean("is_vip"));
        this.setNickname(jsonObject.optString("nickname"));
        this.setRemark(jsonObject.optString("remark"));
        this.setPhotoNum(jsonObject.optInt("photoNum"));
        this.setKf_id(jsonObject.isNull("kf_id") ? 0 : jsonObject.optInt("kf_id"));
        //        UserDetail detail ;
        //        detail.isVip()

    }

    public void parse(UserDetail info) {
        this.setUid(info.getUid());
        this.setAge(info.getAge());
        this.setAvatar(info.getAvatar());
        this.setAvatar_status(info.getAvatar_status());
        this.setCity(info.getScity());
        this.setGender(info.getGender());
        this.setIs_vip(info.isVip());
        this.setNickname(info.getNickname());
        this.setRemark(info.getRemark());
        if (info.getUserPhotos() != null)
            this.setPhotoNum(info.getUserPhotos().size());
        this.setKf_id(info.getKf_id());
    }

    public void parseJs(UserInfoLightweight info) {
        this.setUid(info.getUid());
        this.setAge(info.getAge());
        this.setAvatar(info.getAvatar());
        this.setAvatar_status(info.getAvatar_status());
        this.setCity(info.getScity());
        this.setGender(info.getGender());
        this.setIs_vip(info.isVip());
        this.setNickname(info.getNickname());
        this.setPhotoNum(info.getPhotoNum());
        this.setRemark(info.getRemark());
        this.setKf_id(info.getKf_id());
        if (AttentionUtil.isContainsUid(info.getUid()))
            this.setType(1);
    }

    public int getKf_id() {
        return kf_id;
    }

    public void setKf_id(int kf_id) {
        this.kf_id = kf_id;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getLogin_id() {
        return login_id;
    }

    public void setLogin_id(String login_id) {
        this.login_id = login_id;
    }

    public String getOther_id() {
        return other_id;
    }

    public void setOther_id(String other_id) {
        this.other_id = other_id;
    }

    public long getFollow_time() {
        return follow_time;
    }

    public void setFollow_time(long follow_time) {
        this.follow_time = follow_time;
    }

    public int getCity() {
        return city;
    }

    public void setCity(int city) {
        this.city = city;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getPhotoNum() {
        return photoNum;
    }

    public void setPhotoNum(int photoNum) {
        this.photoNum = photoNum;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public boolean is_vip() {
        return is_vip;
    }

    public void setIs_vip(boolean is_vip) {
        this.is_vip = is_vip;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getAvatar_status() {
        return avatar_status;
    }

    public void setAvatar_status(int avatar_status) {
        this.avatar_status = avatar_status;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


    public String getShowName() {
        return TextUtils.isEmpty(getRemark()) ? getNickname() : getRemark();
    }

}
