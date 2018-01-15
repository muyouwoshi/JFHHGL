package com.juxin.predestinate.bean.my;

/**
 * 被邀请信息存储
 * Created by Administrator on 2017/7/7.
 */

public class InviteInfo {
    public long vcId;
    public long dstUid;
    public int chatType;
    public long price;

    public InviteInfo(long vcId, long dstUid, int chatType, long price) {
        this.vcId = vcId;
        this.dstUid = dstUid;
        this.chatType = chatType;
        this.price = price;
    }

    public long getVcId() {
        return vcId;
    }

    public long getDstUid() {
        return dstUid;
    }

    public int getChatType() {
        return chatType;
    }

    public long getPrice() {
        return price;
    }
}
