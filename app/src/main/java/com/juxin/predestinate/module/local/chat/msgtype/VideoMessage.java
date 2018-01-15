package com.juxin.predestinate.module.local.chat.msgtype;

import android.os.Bundle;

import com.juxin.library.log.PLogger;
import com.juxin.predestinate.module.util.TimeUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 视频消息
 * Created by Kind on 2017/4/19.
 */

public class VideoMessage extends BaseMessage {

    private long videoID;//视频聊天ID，一次视频聊天过程的唯一标识
    // 3,4存本地
    private int videoTp;//请求类型，1邀请加入聊天，2同意加入  3拒绝或取消 4挂断（挂断可能会收到不止一次）
    private int videoMediaTp;//现在所有消息都会包含此字段 1视频, 2语音
    private int videoVcEscCode;//拒绝或取消 只在vc_tp=3 时生效 1未接通，对方无应答 2接收方拒绝 3发送方取消
    private long videoVcTalkTime;//聊天耗时 单位秒 只在  vc_tp=4挂断时有效
    private String vc_channel_key;

    private EmLastStatus emLastStatus;

    public enum EmLastStatus {
        none, timeout, refuse, cancel, connect
    }

    public VideoMessage() {
        super();
    }

    public VideoMessage(String channelID, String whisperID, int type, long videoID, int videoTp, int videoVcEscCode) {
        super(channelID, whisperID);
        this.setVideoID(videoID);
        this.setSpecialMsgID(videoID);
        this.setVideoTp(videoTp);
        this.setVideoMediaTp(type);
        this.setVideoVcEscCode(videoVcEscCode);

        this.setEmLastStatus(getLastStatus(getVideoTp(), getVideoVcEscCode()));
        this.setType(BaseMessageType.video.getMsgType());
    }

    @Override
    public BaseMessage parseJson(String jsonStr) {
        super.parseJson(jsonStr);
        parseVideoJson(getJsonObject(jsonStr));
        return this;
    }

    @Override
    public String getJson(BaseMessage message) {
        JSONObject json = new JSONObject();
        try {
            json.put("tid", new JSONArray().put(message.getWhisperID()));
            json.put("mtp", message.getType());
            json.put("mct", message.getMsgDesc());
            json.put("mt", getCurrentTime());
            json.put("d", message.getMsgID());

            json.put("vc_id", ((VideoMessage) message).getVideoID());
            json.put("vc_tp", ((VideoMessage) message).getVideoTp());
            json.put("media_tp", ((VideoMessage) message).getVideoMediaTp());
            json.put("vc_esc_code", ((VideoMessage) message).getVideoVcEscCode());
            json.put("vc_talk_time", ((VideoMessage) message).getVideoVcTalkTime());
            json.put("vc_channel_key", ((VideoMessage) message).getVc_channel_key());
            return json.toString();
        } catch (JSONException e) {
            PLogger.printThrowable(e);
        }
        return null;
    }

    public long getVideoID() {
        return videoID;
    }

    public void setVideoID(long videoID) {
        this.videoID = videoID;
    }

    public int getVideoTp() {
        return videoTp;
    }

    public void setVideoTp(int videoTp) {
        this.videoTp = videoTp;
    }

    public int getVideoMediaTp() {
        return videoMediaTp;
    }

    public boolean isVideoMediaTp() {
        return videoMediaTp == 1;
    }

    public void setVideoMediaTp(int videoMediaTp) {
        this.videoMediaTp = videoMediaTp;
    }

    public int getVideoVcEscCode() {
        return videoVcEscCode;
    }

    public void setVideoVcEscCode(int videoVcEscCode) {
        this.videoVcEscCode = videoVcEscCode;
    }

    public long getVideoVcTalkTime() {
        return videoVcTalkTime;
    }

    public void setVideoVcTalkTime(long videoVcTalkTime) {
        this.videoVcTalkTime = videoVcTalkTime;
    }

    public String getVc_channel_key() {
        return vc_channel_key;
    }

    public void setVc_channel_key(String vc_channel_key) {
        this.vc_channel_key = vc_channel_key;
    }

    public EmLastStatus getEmLastStatus() {
        return emLastStatus;
    }

    public void setEmLastStatus(EmLastStatus emLastStatus) {
        this.emLastStatus = emLastStatus;
    }

    public VideoMessage(Bundle bundle) {
        super(bundle);
        convertJSON(getJsonStr());
    }

    //私聊列表
    public VideoMessage(Bundle bundle, boolean fletter) {
        super(bundle, fletter);
        convertJSON(getJsonStr());
    }

    @Override
    public void convertJSON(String jsonStr) {
        super.convertJSON(jsonStr);
        JSONObject object = getJsonObject(jsonStr);
        this.setMsgDesc(object.optString("mct")); //消息内容
        parseVideoJson(object);
    }

    private void parseVideoJson(JSONObject object) {
        this.setVideoID(object.optLong("vc_id"));
        this.setSpecialMsgID(getVideoID());
        this.setVideoTp(object.optInt("vc_tp"));
        this.setVideoMediaTp(object.optInt("media_tp"));
        this.setVideoVcEscCode(object.optInt("vc_esc_code"));
        this.setVideoVcTalkTime(object.optLong("vc_talk_time"));
        this.setVc_channel_key(object.optString("vc_channel_key"));

        this.setEmLastStatus(getLastStatus(getVideoTp(), getVideoVcEscCode()));
    }

    //最后一次视频、语音状态
    private EmLastStatus getLastStatus(int vc_tp, int vc_esc_code) {
        switch (vc_tp) {
            case 3:
                switch (vc_esc_code) {
                    case 1:
                        return EmLastStatus.timeout;
                    case 2:
                        return EmLastStatus.refuse;
                    case 3:
                        return EmLastStatus.cancel;
                    default:
                        return EmLastStatus.none;
                }
            case 4:
                return EmLastStatus.connect;
            default:
                return EmLastStatus.none;
        }
    }

    /**
     * 获取音视频消息聊天列表item展示状态
     *
     * @param status   当前音视频状态
     * @param sendTime 消息接收时间
     * @param isSender 是否是发送者
     * @return 转换之后的展示字符串
     */
    public static String transLastStatusText(EmLastStatus status, String sendTime, boolean isSender) {
        String result;
        switch (status) {
            case timeout:
            case cancel:
                result = "<font color='#ffac0c'>" + (isSender ? "[未接通]" : "[未接来电]") + "</font>";
                break;
            case refuse:
                result = "<font color='#ffac0c'>[未接通]</font>";
                break;
            case connect:
                result = "[通话结束]";
                break;
            default:
                result = "<font color='#ffac0c'>[未接通]</font>";
        }
        return result + " " + sendTime;
    }

    /**
     * 转换聊天窗口音视频消息展示内容
     *
     * @param status    当前音视频状态
     * @param talk_time 通话时长，仅在挂断状态时有效
     * @param isSender  是否是发送者
     * @return 转换之后的展示字符串
     */
    public static String getVideoChatContent(EmLastStatus status, long talk_time, boolean isSender) {
        switch (status) {
            case timeout:
                return isSender ? "对方无应答" : "未接来电";
            case refuse:
                return isSender ? "对方已拒绝" : "已拒绝";
            case none:
            case cancel:
                return isSender ? "已取消" : "对方已取消";
            case connect:
                return "聊天时长 " + TimeUtil.formatTimeLong(talk_time);
            default:
                return "网络忙，请重试";
        }
    }

    @Override
    public String toString() {
        return "VideoMessage{" +
                "videoID=" + videoID +
                ", videoTp=" + videoTp +
                ", videoMediaTp=" + videoMediaTp +
                ", videoVcEscCode=" + videoVcEscCode +
                ", videoVcTalkTime=" + videoVcTalkTime +
                ", vc_channel_key='" + vc_channel_key + '\'' +
                ", emLastStatus=" + emLastStatus +
                '}';
    }
}
