package com.juxin.predestinate.module.local.unread;

/**
 * 未读角标消息类型
 * Created by Kind on 16/4/8.
 */
public enum UnreadReceiveMsgType {

    // 谁关注我消息类型
    follow_msgType(5),
    // 聊天红包消息类型
    chatRed_msgType(12),
    // 红包余额变动消息
    wallet_msgType(17),
    ;

    public long msgType;

    UnreadReceiveMsgType(long msgType) {
        this.msgType = msgType;
    }

    public static UnreadReceiveMsgType getUnreadReceiveMsgID(long msgType) {
        for (UnreadReceiveMsgType receiveMsgType : UnreadReceiveMsgType.values()) {
            if (receiveMsgType.msgType == msgType) {
                return receiveMsgType;
            }
        }
        return null;
    }
}