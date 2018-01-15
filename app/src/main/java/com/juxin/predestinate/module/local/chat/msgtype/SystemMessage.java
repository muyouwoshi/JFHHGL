package com.juxin.predestinate.module.local.chat.msgtype;

import android.os.Bundle;

import org.json.JSONObject;

/**
 * 系统消息
 * Created by Kind on 2017/4/19.
 */
public class SystemMessage extends BaseMessage {

    private int xtType;//系统消息类型（3为已读通知，5为正在输入，6为取消正在输入）
    private long fid;
    private long tid;
    private long d;

    public SystemMessage() {
        super();
    }

    @Override
    public BaseMessage parseJson(String jsonStr) {
        super.parseJson(jsonStr);
        JSONObject object = getJsonObject(jsonStr);
        this.setXtType(object.optInt("xt"));
        this.setFid(object.optLong("fid"));
        this.setTid(object.optLong("tid"));

        this.setD(object.optLong("d"));
        this.setMsgID(object.optLong("msg_id"));
        return this;
    }

    @Override
    public String getJson(BaseMessage message) {
        return super.getJson(message);
    }

    public long getFid() {
        return fid;
    }

    public void setFid(long fid) {
        this.fid = fid;
    }

    public long getTid() {
        return tid;
    }

    public void setTid(long tid) {
        this.tid = tid;
    }

    public SystemMessage(Bundle bundle, boolean fletter) {
        super(bundle, fletter);
        parseJson(getJsonStr());
    }

    /**
     * 转换类 fmessage
     */
    public SystemMessage(Bundle bundle) {
        super(bundle);
        parseJson(getJsonStr());
    }

    public int getXtType() {
        return xtType;
    }

    public void setXtType(int xtType) {
        this.xtType = xtType;
    }

    public long getD() {
        return d;
    }

    public void setD(long d) {
        this.d = d;
    }
}
