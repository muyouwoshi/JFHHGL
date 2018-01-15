package com.juxin.predestinate.ui.live.bean;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 直播页面，单击个人资料页
 *
 * Created by chengxiaobo on 2017/9/13.
 */

public class LiveUserDetail implements Serializable{

    /**
     res:{
     "avatar":"dddd"        // 头像
     "nickname":"ddd"       // 昵称
     "vip":1                // vip  1-普通用户 2-vip用户
     "nobility_level":1     // 爵位
     "nobility_icon":"dddd"  //爵位图标
     "gender":0             // 性别  1男2女
     "diamond":100		   //钻石余额
     "time":5			   //在线时长
     "isfriend":true		   //好友关系  true-好友  false-非好友
     "admin":false          //被查看的用户是否为场控
     "consume":11		   //本场消费（元）礼物
     }
     */

    private String avatar;//头像
    private String nickname;// 昵称
    private int vip; // vip  1-普通用户 2-vip用户
    private int nobility_level;// 爵位等级
    private String nobility_icon;//爵位图标
    private int beiGender;// 性别 1男2女
    private int diamond;//钻石余额
    private long time;  //在线时长
    private boolean isfriend;//好友关系  true-好友  false-非好友
    private boolean isBeiAdmin;//被查看的用户是否为场控
    private String consume;//本场消费（元）礼物

    private String girlUid; //主播id
    private String userId; //用户id
    private String beiUserId; //用户查看的另一个用户的id
    private int userSex;//用户的sex 1男2女
    private String roomId; //房间
    private boolean isAdmin;//该用户是不是场控
    private String channel_uid; //用户渠道
    private String channel_sid;//推广渠道子ID
    private String cmdMessage=""; //cmd的message(用于禁言和踢出)

    public static final int SEX_MAN=1;
    public static final int SEX_WOMAN=2;

    public static final int VIP_NO=1;
    public static final int VIP_YES=2;

    /**
     * 解析成对象
     * @param jsonStr
     */
    public void parseJson(JSONObject jsonStr) {

        if (jsonStr!=null ) {
            avatar = jsonStr.optString("avatar");
            nickname = jsonStr.optString("nickname");
            vip = jsonStr.optInt("vip");
            nobility_level = jsonStr.optInt("nobility_level");
            nobility_icon = jsonStr.optString("nobility_icon");
            beiGender = jsonStr.optInt("gender");
            diamond = jsonStr.optInt("diamond");
            time = jsonStr.optLong("time");
            isfriend = jsonStr.optBoolean("isfriend");
            isBeiAdmin = jsonStr.optBoolean("admin");
            consume = jsonStr.optString("consume");
            channel_sid=jsonStr.optString("channel_sid");
            channel_uid=jsonStr.optString("channel_uid");
        }
    }

    /**
     * 设置其他的信息
     * @param girlUid
     * @param userId
     * @param beiUserId
     * @param roomId
     * @param isAdmin
     */
    public void setOtherMessage(String girlUid,String userId,String beiUserId,String roomId,boolean isAdmin,int userSex){

        this.girlUid=girlUid;
        this.userId=userId;
        this.beiUserId=beiUserId;
        this.roomId=roomId;
        this.isAdmin=isAdmin;
        this.userSex=userSex;
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

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public int getNobility_level() {
        return nobility_level;
    }

    public void setNobility_level(int nobility_level) {
        this.nobility_level = nobility_level;
    }

    public String getNobility_icon() {
        return nobility_icon;
    }

    public void setNobility_icon(String nobility_icon) {
        this.nobility_icon = nobility_icon;
    }

    public int getBeiGender() {
        return beiGender;
    }

    public void setBeiGender(int beiGender) {
        this.beiGender = beiGender;
    }

    public int getDiamond() {
        return diamond;
    }

    public void setDiamond(int diamond) {
        this.diamond = diamond;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isfriend() {
        return isfriend;
    }

    public void setIsfriend(boolean isfriend) {
        this.isfriend = isfriend;
    }

    public boolean isBeiAdmin() {
        return isBeiAdmin;
    }

    public void setBeiAdmin(boolean beiAdmin) {
        isBeiAdmin = beiAdmin;
    }

    public String getConsume() {
        return consume;
    }

    public void setConsume(String consume) {
        this.consume = consume;
    }

    public String getGirlUid() {
        return girlUid;
    }

    public void setGirlUid(String girlUid) {
        this.girlUid = girlUid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBeiUserId() {
        return beiUserId;
    }

    public void setBeiUserId(String beiUserId) {
        this.beiUserId = beiUserId;
    }

    public int getUserSex() {
        return userSex;
    }

    public void setUserSex(int userSex) {
        this.userSex = userSex;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getChannel_uid() {
        return channel_uid;
    }

    public void setChannel_uid(String channel_uid) {
        this.channel_uid = channel_uid;
    }

    public String getChannel_sid() {
        return channel_sid;
    }

    public void setChannel_sid(String channel_sid) {
        this.channel_sid = channel_sid;
    }

    public String getCmdMessage() {
        return cmdMessage;
    }

    public void setCmdMessage(String cmdMessage) {
        this.cmdMessage = cmdMessage;
    }
}
