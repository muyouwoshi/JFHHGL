package com.juxin.predestinate.ui.live.bean;

/**
 * Created by terry on 2017/7/24.
 * 用于切换房间
 * 正在直播的主播列表item
 */

public class SwitchRoomBean {

    public String uid;
    public String roomid;

    public SwitchRoomBean(String uid, String roomid){
        this.uid = uid;
        this.roomid = roomid;
    }
}
