package com.juxin.predestinate.bean.start;


import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.bean.net.BaseData;
import com.juxin.predestinate.module.logic.application.ModuleMgr;

import org.json.JSONObject;

/**
 * 登录解析类
 * Created YAO on 2017/4/25.
 */
public class LoginResult extends BaseData {

    private long uid;
    private String nickname;
    private String avatar;
    private int avatar_status;
    private int gender;
    private int group;
    private int ycoin;

    private int loginstatus;//登录状态 0 登陆成功 1 登录失败
    private int miss_info;// 判断是否缺失数据,缺失则继续跳转到用户注册

    private int failCode;//失败代码 1 用户或者密码错误 2 被举报封禁 3 登录过于频繁被禁用 4 管理员禁用
    private long expire;//[opt] 封禁过期时间等 0 未封禁 -1 永久封禁 该字段仅在failCode==2时有效

    private String msg;//登录失败原因

    private String token;


    /**
     * @return 判断用户是否缺失信息
     */
    public boolean isValidDetailInfo() {
        return miss_info != 1;
    }

    @Override
    public void parseJson(String jsonStr) {
        JSONObject jsonFirst = getJsonObject(jsonStr);
        JSONObject jsonRes = jsonFirst.optJSONObject("res");
        if (jsonRes==null)
            return;
        this.loginstatus= jsonRes.optInt("loginstatus");
        JSONObject jsonNext = jsonRes.optJSONObject("userdata");
        if (jsonNext != null) {
            this.setUid(jsonNext.optLong("uid"));
//            this.setNickname(jsonNext.optString("nickname"));
//            this.setAvatar(jsonNext.optString("avatar"));
//            this.setAvatar_status(jsonNext.optInt("avatar_status"));
            this.setGender(jsonNext.optInt("gender"));
            this.setGroup(jsonNext.optInt("group"));
            this.setYcoin(jsonNext.optInt("ycoin"));
            this.setMiss_info(jsonNext.optInt("missinfo"));
            this.token= jsonNext.optString("token");
        }
        JSONObject jsonfail = jsonRes.optJSONObject("faildata");
        if (jsonfail != null) {
            this.failCode = jsonfail.optInt("failCode");
            this.msg = jsonfail.optString("msg");
            this.expire = jsonfail.optLong("expire");
        }
    }


    /**
     * 登录成功之后根据返回信息重设个人资料
     */
    public void setUserInfo() {
        UserDetail myInfo = ModuleMgr.getCenterMgr().getMyInfo();
        myInfo.setUid(getUid());
        myInfo.setAvatar_status(getAvatar_status());
        myInfo.setGender(getGender());
        myInfo.setGroup(getGroup());
        myInfo.setYcoin(getYcoin());
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getLoginstatus() {
        return loginstatus;
    }

    public void setLoginstatus(int loginstatus) {
        this.loginstatus = loginstatus;
    }

    public int getFailCode() {
        return failCode;
    }

    public void setFailCode(int failCode) {
        this.failCode = failCode;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public int getYcoin() {
        return ycoin;
    }

    public void setYcoin(int ycoin) {
        this.ycoin = ycoin;
    }

    public int getMiss_info() {
        return miss_info;
    }

    public void setMiss_info(int miss_info) {
        this.miss_info = miss_info;
    }
}
