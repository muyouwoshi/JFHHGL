package com.juxin.predestinate.bean.my;


import com.juxin.predestinate.bean.net.BaseData;

import java.io.Serializable;

/**
 * 离线送达信息
 * Created by zm on 17/3/20.
 */
public class OffMsgInfo extends BaseData implements Serializable {

    private long uid;
    private long msg_id;
    private int mtp;

    public OffMsgInfo(long uid, long msg_id, int mtp) {
        this.uid = uid;
        this.msg_id = msg_id;
        this.mtp = mtp;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(long msg_id) {
        this.msg_id = msg_id;
    }

    public int getMtp() {
        return mtp;
    }

    public void setMtp(int mtp) {
        this.mtp = mtp;
    }

    @Override
    public void parseJson(String jsonStr) {

    }

    @Override
    public String toString() {
        return "{" +
                "uid=" + uid +
                ", msg_id=" + msg_id +
                ", mtp=" + mtp +
                '}';
    }
}
