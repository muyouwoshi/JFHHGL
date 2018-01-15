package com.juxin.predestinate.module.local.mail;

/**
 *  缘分小秘书-特殊号
 * Created by Kind on 16/3/25.
 */
public enum MailSpecialID {

    customerService(9999, "在线客服"),

    systemMsg(9998, "系统消息"),

    shareMsg(9997, "分享赚钱"),

    ;

    public long specialID;
    public String specialIDName;
    MailSpecialID(long specialID, String specialIDName) {
        this.specialID = specialID;
        this.specialIDName = specialIDName;
    }

    public static boolean getMailSpecialID(long specialID) {
        for (MailSpecialID tmp : MailSpecialID.values()) {
            if (tmp.specialID == specialID) {
                return true;
            }
        }
        return false;
    }

    public long getSpecialID() {
        return this.specialID;
    }

    public String getSpecialIDName() {
        return this.specialIDName;
    }
}
