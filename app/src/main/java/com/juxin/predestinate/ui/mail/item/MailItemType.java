package com.juxin.predestinate.ui.mail.item;

/**
 * 显示样式种类
 * 目前Mail_Item_Invite，Mail_Item_Gift的样式是一样的
 * 目前Mail_Item_Private，Mail_Item_Stranger的样式是一样的
 * Created by Kind on 16/2/2.
 */
public enum MailItemType {

    /**普通，目前是在私聊中用**/
    Mail_Item_Ordinary(0),

    /**其他 密友版本只有9999在使用**/
    Mail_Item_Other(1),

    /**打招呼的人**/
    Mail_Item_Greet(2),

    /**男-语音视频邀请**/
    Mail_Item_Invite(3),

    /**男-私密消息**/
    Mail_Item_Private(4),

    /**女-礼物消息**/
    Mail_Item_Gift(5),

    /**女-陌生人**/
    Mail_Item_Stranger(6),

    /**小标题头部**/
    Mail_Item_Title(7),

    /**小标题头部第二个**/
    Mail_Item_Title_TWO(8),

    /**小标题底部**/
    Mail_Item_Bottom(9),

    /**空白条**/
    Mail_Item_BlankBar(10),

    ;

    public int type;
    MailItemType(int type) {
        this.type = type;
    }

    public static MailItemType getMailMsgType(int type) {
        for (MailItemType mailItemType : MailItemType.values()) {
            if (mailItemType.type == type) {
                return mailItemType;
            }
        }
        return null;
    }
}
