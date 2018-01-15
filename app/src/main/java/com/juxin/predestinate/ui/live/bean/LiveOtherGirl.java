package com.juxin.predestinate.ui.live.bean;

import org.json.JSONObject;

/**
 * Created by chengxiaobo on 2017/10/25.
 */

public class LiveOtherGirl {

    /**
     * "roomid:"0          //房间ID
     * "uid": 110874791,   //主播id
     * "pic": "http://image1.yuanfenba.net/uploads/oss/avatar/201707/27/1139255641.jpg",
     * "nickname": "",     //昵称
     */

    private String roomid;
    private String uid;
    private String pic;
    private String nickname;

    /**
     * 解析成对象
     *
     * @param jsonStr
     */
    public void parseJson(JSONObject jsonStr) {

        if (jsonStr != null) {
            roomid = jsonStr.optString("roomid");
            nickname = jsonStr.optString("nickname");
            uid = jsonStr.optString("uid");
            pic = jsonStr.optString("pic");
        }
    }

    public String getRoomid() {
        return roomid;
    }

    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
