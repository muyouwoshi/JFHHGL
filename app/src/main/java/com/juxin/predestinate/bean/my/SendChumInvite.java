package com.juxin.predestinate.bean.my;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

/**
 * 发送密友
 * Created by Kind on 2017/7/20.
 */

public class SendChumInvite extends BaseData {

    private long invite_id;
    private long expire;//邀请超时时间
    private long msgID;


    @Override
    public void parseJson(String jsonStr) {
        JSONObject jsonObject = getJsonObjectRes(jsonStr);
        this.setInvite_id(jsonObject.optLong("req_id"));
        this.setExpire(jsonObject.optLong("expire"));
        this.setMsgID(jsonObject.optLong("msgid"));
    }


    public long getInvite_id() {
        return invite_id;
    }

    public void setInvite_id(long invite_id) {
        this.invite_id = invite_id;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public long getMsgID() {
        return msgID;
    }

    public void setMsgID(long msgID) {
        this.msgID = msgID;
    }
}
