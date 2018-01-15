package com.juxin.predestinate.ui.mail.item;

/**
 * 特殊消息消息ID等
 */
public enum MailMsgID {

    /**
     * 谁关注我
     **/
    Follow_Msg(1),

    /**
     * 我的好友
     **/
    MyFriend_Msg(2),

    /**
     * 打招呼的人
     **/
    Greet_Msg(3),
    /**
     * 我要赚钱
     **/
    Money_Msg(4),

    /**
     * 视频/语音邀请
     **/
    Invite_Msg(5),

    /**
     * 私聊消息
     **/
    Private_Msg(6),

    /**
     * 陌生人消息
     **/
    Stranger_Msg(7),

    /**
     * 礼物消息
     **/
    Gift_Msg(8),

    /**
     * 视频/语音邀请更多
     **/
    Invite_More_Msg(9),

    /**
     * 私聊消息更多
     **/
    Private_More_Msg(10),

    /**
     * 陌生人消息更多
     **/
    Stranger_More_Msg(11),

    /**
     * 礼物消息更多
     **/
    Gift_More_Msg(12),

    /**
     * 空白条
     **/
    Blank_Bar_Msg(13),

    /**
     * 密友
     **/
    Chum_Msg(14),

    ;
    public long type;

    MailMsgID(long type) {
        this.type = type;
    }

    public static MailMsgID getMailMsgID(long type) {
        for (MailMsgID mailMsgID : MailMsgID.values()) {
            if (mailMsgID.type == type) {
                return mailMsgID;
            }
        }
        return null;
    }
    }