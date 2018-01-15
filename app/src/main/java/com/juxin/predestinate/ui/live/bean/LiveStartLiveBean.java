package com.juxin.predestinate.ui.live.bean;

import org.json.JSONObject;

/**
 * 长连接消息开启直播
 * <p>
 * Created by chengxiaobo on 2017/10/27.
 */

public class LiveStartLiveBean {

    /**
     * {
     * "mct":"你的好友张三正在直播，要不要去看看",	//消息内容
     * "fid":100221,					//发送者uid
     * "tid":100221,					//接收者uid
     * "mtp":1004,						//消息类型 和包头一致
     * "mt":442112121,					//消息时间 int64
     * "d":43432423423,				//消息id int64
     * "cover":"http://",					//封面图
     * "room_id":"123211",				//直播间ID
     * }
     */

    private String mct;
    private String fid;
    private String tid;
    private String mtp;
    private String d;
    private String cover;
    private String room_id;

    /**
     * 解析成对象
     *
     * @param jsonStr
     */
    public void parseJson(JSONObject jsonStr) {

        if (jsonStr != null) {
            mct = jsonStr.optString("mct");
            fid = jsonStr.optString("fid");
            tid = jsonStr.optString("tid");
            mtp = jsonStr.optString("mtp");
            d = jsonStr.optString("d");
            cover = jsonStr.optString("cover");
            room_id = jsonStr.optString("room_id");
        }
    }

    public String getMct() {
        return mct;
    }

    public void setMct(String mct) {
        this.mct = mct;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getMtp() {
        return mtp;
    }

    public void setMtp(String mtp) {
        this.mtp = mtp;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }
}
