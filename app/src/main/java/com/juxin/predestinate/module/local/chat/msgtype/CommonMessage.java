package com.juxin.predestinate.module.local.chat.msgtype;

import android.os.Bundle;
import android.text.TextUtils;
import com.juxin.library.log.PLogger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 普通消息
 * Created by Kind on 2017/4/18.
 */

public class CommonMessage extends BaseMessage {

    //图片
    private String img;
    private String localImg;

    //语音
    private String voiceUrl;
    private String localVoiceUrl;
    private int voiceLen;
    private long voiceUserid;
    private int voiceDownStatus = -1;//-1是未初始化 ，0是未下载,1是下载中,2是下载完了

    public int getVoiceDownStatus() {
        return voiceDownStatus;
    }

    public void setVoiceDownStatus(int voiceDownStatus) {
        this.voiceDownStatus = voiceDownStatus;
    }

    //视频
    private String videoUrl;
    private String localVideoUrl;
    private int videoLen;
    private long videoSize;
    private String videoThumb;
    private int videoWidth;
    private int videoHeight;

    //打招呼
    private int kf;
    private int sayHelloType;

    public CommonMessage() {
        super();
    }

    /**
     * 文本消息
     *
     * @param whisperID
     * @param content
     */
    public CommonMessage(String channelID, String whisperID, String content) {
        super(channelID, whisperID);
        this.setMsgDesc(content);
        this.setType(BaseMessageType.common.getMsgType());
    }

    /**
     * 图片消息
     *
     * @param channelID
     * @param whisperID
     * @param img
     */
    public CommonMessage(String channelID, String whisperID, String img, String no) {
        super(channelID, whisperID);
        this.setImg(img);
        this.setType(BaseMessageType.common.getMsgType());
    }

    /**
     * 打招呼
     * @param whisperID
     * @param content
     * @param kf
     * @param sayHelloType
     */
    public CommonMessage(String whisperID, String content, int kf, int sayHelloType) {
        super(null, whisperID);
        this.setMsgDesc(content);
        this.setType(BaseMessageType.hi.getMsgType());
        this.setKf(kf);
        this.setSayHelloType(sayHelloType);
    }

    /**
     * 语音消息
     *
     * @param channelID
     * @param whisperID
     * @param url
     * @param length
     */
    public CommonMessage(String channelID, String whisperID, String url, int length) {
        super(channelID, whisperID);
        this.setVoiceUrl(url);
        this.setVoiceLen(length);
        this.setType(BaseMessageType.common.getMsgType());
    }

    @Override
    public BaseMessage parseJson(String jsonStr) {
        super.parseJson(jsonStr);

        JSONObject object = getJsonObject(jsonStr);
        this.setImg(object.optString("img"));
        parseCommonJson(object);
        return this;
    }

    @Override
    public String getJson(BaseMessage message) {
        JSONObject json = new JSONObject();
        try {
            json.put("tid", new JSONArray().put(message.getWhisperID()));
            json.put("mtp", message.getType());
            json.put("mt", message.getTime());
            json.put("d", message.getMsgID());

            if (!TextUtils.isEmpty(message.getMsgDesc())) {
                json.put("mct", message.getMsgDesc());
            }

            //图片
            String img = ((CommonMessage) message).getImg();
            String localImg = ((CommonMessage) message).getLocalImg();
            if (!TextUtils.isEmpty(img) || !TextUtils.isEmpty(localImg)) {
                json.put("img", img);
                json.put("localImgUrl", localImg);
            }

            //语音
            String voiceUrl = ((CommonMessage) message).getVoiceUrl();
            String localVoiceUrl = ((CommonMessage) message).getLocalVoiceUrl();
            if (!TextUtils.isEmpty(voiceUrl) || !TextUtils.isEmpty(localVoiceUrl)) {
                JSONObject tmpVoice = new JSONObject();
                tmpVoice.put("url", voiceUrl);
                tmpVoice.put("localUrl", localVoiceUrl);
                tmpVoice.put("len", ((CommonMessage) message).getVoiceLen());
                tmpVoice.put("voice_userid", ((CommonMessage) message).getVoiceUserid());
                json.put("voice", tmpVoice);
            }

            //视频
            String videoUrl = ((CommonMessage) message).getVideoUrl();
            String localVideoUrl = ((CommonMessage) message).getLocalVideoUrl();
            if (!TextUtils.isEmpty(videoUrl) || !TextUtils.isEmpty(localVideoUrl)) {
                JSONObject tmpVideo = new JSONObject();
                tmpVideo.put("url", videoUrl);
                tmpVideo.put("localUrl", localVideoUrl);
                tmpVideo.put("len", ((CommonMessage) message).getVideoLen());
                tmpVideo.put("size", ((CommonMessage) message).getVideoSize());
                tmpVideo.put("thumb", ((CommonMessage) message).getVideoThumb());
                tmpVideo.put("width", ((CommonMessage) message).getVideoWidth());
                tmpVideo.put("height", ((CommonMessage) message).getVideoHeight());
                json.put("video", tmpVideo);
            }


            int kf = ((CommonMessage)message).getKf();
            int sayHelloType = ((CommonMessage)message).getSayHelloType();

            if(kf != -1){
                json.put("kf", kf);
            }
            if(sayHelloType != -1){
                json.put("j", sayHelloType);
            }

            return json.toString();
        } catch (JSONException e) {
            PLogger.printThrowable(e);
        }
        return null;
    }

    public String getLocalImg() {
        return localImg;
    }

    public void setLocalImg(String localImg) {
        this.localImg = localImg;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getVoiceUrl() {
        return voiceUrl;
    }

    public void setVoiceUrl(String voiceUrl) {
        this.voiceUrl = voiceUrl;
    }

    public String getLocalVoiceUrl() {
        return localVoiceUrl;
    }

    public void setLocalVoiceUrl(String localVoiceUrl) {
        this.localVoiceUrl = localVoiceUrl;
    }

    public int getVoiceLen() {
        return voiceLen;
    }

    public void setVoiceLen(int voiceLen) {
        this.voiceLen = voiceLen;
    }

    public long getVoiceUserid() {
        return voiceUserid;
    }

    public void setVoiceUserid(long voiceUserid) {
        this.voiceUserid = voiceUserid;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getLocalVideoUrl() {
        return localVideoUrl;
    }

    public void setLocalVideoUrl(String localVideoUrl) {
        this.localVideoUrl = localVideoUrl;
    }

    public int getVideoLen() {
        return videoLen;
    }

    public void setVideoLen(int videoLen) {
        this.videoLen = videoLen;
    }

    public long getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(long videoSize) {
        this.videoSize = videoSize;
    }

    public String getVideoThumb() {
        return videoThumb;
    }

    public void setVideoThumb(String videoThumb) {
        this.videoThumb = videoThumb;
    }

    public int getVideoWidth() {
        return videoWidth;
    }

    public void setVideoWidth(int videoWidth) {
        this.videoWidth = videoWidth;
    }

    public int getVideoHeight() {
        return videoHeight;
    }

    public void setVideoHeight(int videoHeight) {
        this.videoHeight = videoHeight;
    }

    public int getKf() {
        return kf;
    }

    public void setKf(int kf) {
        this.kf = kf;
    }

    public int getSayHelloType() {
        return sayHelloType;
    }

    public void setSayHelloType(int sayHelloType) {
        this.sayHelloType = sayHelloType;
    }

    /**
     * 转换类 fmessage
     */
    public CommonMessage(Bundle bundle) {
        super(bundle);
        convertJSON(getJsonStr());
    }

    public CommonMessage(Bundle bundle, boolean fletter) {
        super(bundle, fletter);
        convertJSON(getJsonStr());
    }

    @Override
    public void convertJSON(String jsonStr) {
        super.convertJSON(jsonStr);
        JSONObject object = getJsonObject(jsonStr);

        this.setMsgDesc(object.optString("mct")); //消息内容
        this.setImg(object.optString("img"));
        parseCommonJson(object);
    }

    private void parseCommonJson(JSONObject object) {
        if (!object.isNull("voice")) {
            JSONObject voiceJSON = object.optJSONObject("voice");
            this.setLocalVoiceUrl(voiceJSON.optString("localUrl"));
            this.setVoiceUrl(voiceJSON.optString("url"));
            this.setVoiceLen(voiceJSON.optInt("len"));
            this.setVoiceUserid(voiceJSON.optLong("voice_userid"));
        }

        if (!object.isNull("video")) {
            JSONObject videoJSON = object.optJSONObject("video");
            this.setVideoUrl(videoJSON.optString("url"));
            this.setVideoLen(videoJSON.optInt("len"));
            this.setVideoSize(videoJSON.optInt("size"));
            this.setVideoThumb(videoJSON.optString("thumb"));
            this.setVideoWidth(videoJSON.optInt("width"));
            this.setVideoHeight(videoJSON.optInt("height"));
        }
    }

    @Override
    public String toString() {
        return "CommonMessage{" +
                "img='" + img + '\'' +
                ", localImg='" + localImg + '\'' +
                ", voiceUrl='" + voiceUrl + '\'' +
                ", localVoiceUrl='" + localVoiceUrl + '\'' +
                ", voiceLen=" + voiceLen +
                ", voiceUserid=" + voiceUserid +
                ", videoUrl='" + videoUrl + '\'' +
                ", localVideoUrl='" + localVideoUrl + '\'' +
                ", videoLen=" + videoLen +
                ", videoSize=" + videoSize +
                ", videoThumb='" + videoThumb + '\'' +
                ", videoWidth=" + videoWidth +
                ", videoHeight=" + videoHeight +
                '}';
    }
}
