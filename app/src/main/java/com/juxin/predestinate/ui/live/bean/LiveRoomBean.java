package com.juxin.predestinate.ui.live.bean;

import org.json.JSONObject;

/**
 * LiveRoom
 * @author gwz
 */
public class LiveRoomBean {
    /**主播id*/
    public String uid;
    /**直播间id*/
    public String roomid;
    /**直播封面*/
    public String pic;
    /**直播标题*/
    public String title;
    /**直播位置*/
    public String location;
    /**直播状态*/
    public int status;
    /**观看人数*/
    public long total;
    /**主播头像*/
    public String avatar;
    /**主播昵称*/
    public String nickname;
    /**和主播距离*/
    public String distance;
    /**星*/
    public int star;

    public LiveRoomBean parseJson(JSONObject jo){
        avatar = jo.optString("avatar");
        distance = jo.optString("distance");
        location = jo.optString("location");
        nickname = jo.optString("nickname");
        pic = jo.optString("pic");
        roomid = jo.optString("roomid");
        star = jo.optInt("star");
        status = jo.optInt("status");
        title = jo.optString("title");
        total = jo.optInt("total");
        uid = jo.optString("uid");
        return this;
    }
}
