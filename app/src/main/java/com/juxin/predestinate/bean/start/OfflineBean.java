package com.juxin.predestinate.bean.start;

import com.juxin.library.log.PLogger;
import com.juxin.predestinate.bean.net.BaseData;
import com.juxin.predestinate.module.logic.application.App;

import org.json.JSONObject;

/**
 * 离线消息实体类
 * Created by Su on 2017/5/25.
 */
public class OfflineBean extends BaseData {
    private String jsonStr;     // json串

    private long tid;           // 发给谁ID
    private long fid;           // 发消息人ID
    private int mtp;            // 消息类型
    private long mt;            // 消息时间（时间戳）
    private long d;             // 消息ID

    //用于送达
    private long msg_id;

    // voice消息
    private String mct;         // 消息文本内容
    private int mod;

    // 音视频消息
    private long vc_id;         // 视频聊天ID，一次视频聊天过程的唯一标识
    private int vc_tp;          // 请求类型，1邀请加入聊天，2同意加入 3拒绝加入 4挂断（挂断可能会收到不止一次）
    private int media_tp;       // [opt]媒体类型只在vc_tp=1邀请加入聊天时会包含此字段， 1视频, 2语音
    private int vc_esc_code;    // [opt]拒绝或取消 只在vc_tp=3 时生效 1未接通，对方无应答 2接收方拒绝 3发送方取消
    private int vc_talk_time;   // [opt]聊天耗时 单位秒 只在  vc_tp=4挂断时有效
    private String vc_channel_key;// [opt]视频聊天要用的的channel_key 只在  vc_tp=2同意时有效

    @Override
    public void parseJson(String jsonStr) {
        JSONObject object = getJsonObject(jsonStr);
        PLogger.d("offlineMsg:  " + jsonStr);

        this.jsonStr = jsonStr;
        long t_id = object.optLong("tid");
        this.tid = t_id == 0 ? App.uid : t_id;
        this.fid = object.optLong("fid");
        this.mtp = object.optInt("mtp");
        this.mt = object.optLong("mt");
        this.d = object.optLong("d");

        this.msg_id = object.optLong("msg_id");

        this.mct = object.optString("mct");
        this.mod = object.optInt("mod");

        this.vc_id = object.optLong("vc_id");
        this.vc_tp = object.optInt("vc_tp");
        this.media_tp = object.optInt("media_tp");
        this.vc_esc_code = object.optInt("vc_esc_code");
        this.vc_talk_time = object.optInt("vc_talk_time");
        this.vc_channel_key = object.optString("vc_channel_key");
    }

    public String getJsonStr() {
        return jsonStr;
    }

    public long getTid() {
        return tid;
    }

    public long getFid() {
        return fid;
    }

    public int getMtp() {
        return mtp;
    }

    public long getMt() {
        return mt;
    }

    public long getD() {
        return d;
    }

    public long getMsg_id() {
        return msg_id;
    }

    public String getMct() {
        return mct;
    }

    public int getMod() {
        return mod;
    }

    public long getVc_id() {
        return vc_id;
    }

    public int getVc_tp() {
        return vc_tp;
    }

    public int getMedia_tp() {
        return media_tp;
    }

    public int getVc_esc_code() {
        return vc_esc_code;
    }

    public int getVc_talk_time() {
        return vc_talk_time;
    }

    public String getVc_channel_key() {
        return vc_channel_key;
    }

}
