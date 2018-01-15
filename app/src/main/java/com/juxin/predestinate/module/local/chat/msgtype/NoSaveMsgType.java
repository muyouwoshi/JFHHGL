package com.juxin.predestinate.module.local.chat.msgtype;

/**
 * 不保存消息 type
 * Created by Kind on 2017/6/20.
 */
public enum NoSaveMsgType {

    // 关注
    Follow_MsgType(5),
    // 系统消息
    System_MsgType(7),
    // 红包余额变动消息
    RedEnvelopesBalance_MsgType(17),
    //消息送达
    recved_MsgType(1001),
    //女用户单独视频邀请
    aloneInviteVideo_MsgType(1003),

    //弹窗消息
    Pop_ups_MsgType(1006),
    ;

    public long msgType;

    NoSaveMsgType(long msgType) {
        this.msgType = msgType;
    }

    public static NoSaveMsgType getNoSaveMsgType(long msgType) {
        for (NoSaveMsgType noSaveMsgType : NoSaveMsgType.values()) {
            if (noSaveMsgType.msgType == msgType) {
                return noSaveMsgType;
            }
        }
        return null;

    }
}