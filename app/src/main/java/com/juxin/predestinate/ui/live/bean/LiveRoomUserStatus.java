package com.juxin.predestinate.ui.live.bean;

/**
 * Created by terry on 2017/8/2.
 */

public class LiveRoomUserStatus {

    /**
     * 0-正常，1-禁言，2-添加场控 3-踢出 4- 取消场控
     */
    public static final int ROOM_CONTROL_STATUS_GAG = 1;        //直播间禁言
    public static final int ROOM_CONTROL_STATUS_DEFRIEND = -1;  //直播间拉黑
    public static final int ROOM_CONTROL_STATUS_KICK_OUT = 3;  //直播间踢出
    public static final int ROOM_CONTROL_STATUS_ADD_ADMIN = 2;  //添加场控
    public static final int ROOM_CONTROL_STATUS_CANCEL_ADMIN = 4;  //取消场控

}
