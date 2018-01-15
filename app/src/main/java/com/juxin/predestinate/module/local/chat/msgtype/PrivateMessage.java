package com.juxin.predestinate.module.local.chat.msgtype;

import android.os.Bundle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * 私密消息，图片，视频，文字，语音
 * Created by Kind on 2017/6/27.
 */

public class PrivateMessage extends BaseMessage{

    private long qun_id;
    private String info;
    private long gift_id;
    private int unlocked;//[opt]是否已解锁 为1表示已解锁 0或者不填为未解锁

    //私密照片
    private List<String> photos = new ArrayList<>();

    //私密视频
    private String videoUrl;
    private int videoLen;
    private String videoThumb;

    //语音
    private String voiceUrl;
    private int voiceLen;

    @Override
    public BaseMessage parseJson(String jsonStr) {
        super.parseJson(jsonStr);
        parseVideoJson(getJsonObject(jsonStr));
        return this;
    }

    @Override
    public String getJson(BaseMessage message) {
        return super.getJson(message);
    }

    public long getQun_id() {
        return qun_id;
    }

    public void setQun_id(long qun_id) {
        this.qun_id = qun_id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public long getGift_id() {
        return gift_id;
    }

    public void setGift_id(long gift_id) {
        this.gift_id = gift_id;
    }

    public int getUnlocked() {
        return unlocked;
    }

    public void setUnlocked(int unlocked) {
        this.unlocked = unlocked;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }


    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public int getVideoLen() {
        return videoLen;
    }

    public void setVideoLen(int videoLen) {
        this.videoLen = videoLen;
    }

    public String getVideoThumb() {
        return videoThumb;
    }

    public void setVideoThumb(String videoThumb) {
        this.videoThumb = videoThumb;
    }


    public String getVoiceUrl() {
        return voiceUrl;
    }

    public void setVoiceUrl(String voiceUrl) {
        this.voiceUrl = voiceUrl;
    }

    public int getVoiceLen() {
        return voiceLen;
    }

    public void setVoiceLen(int voiceLen) {
        this.voiceLen = voiceLen;
    }

    public PrivateMessage(){
        super();
    }

    public PrivateMessage(Bundle bundle) {
        super(bundle);
        convertJSON(getJsonStr());
    }

    //私聊列表
    public PrivateMessage(Bundle bundle, boolean fletter) {
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
        this.setQun_id(object.optLong("qun_id"));
        this.setInfo(object.optString("info"));
        this.setGift_id(object.optLong("gift_id"));
        this.setUnlocked(object.optInt("unlocked"));

        //图片
        if(!object.isNull("photos")){
            JSONArray jsonArray = object.optJSONArray("photos");
            for(int i= 0; i < jsonArray.length(); i++){
                try {
                    photos.add(jsonArray.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        //语音
        if(!object.isNull("voice")){
            JSONObject jsonObject = object.optJSONObject("voice");
            this.setVoiceUrl(jsonObject.optString("url"));
            this.setVoiceLen(jsonObject.optInt("len"));
        }

        //视频
        if(!object.isNull("video")){
            JSONObject jsonObject = object.optJSONObject("video");
            this.setVideoUrl(jsonObject.optString("url"));
            this.setVideoLen(jsonObject.optInt("len"));
            this.setVideoThumb(jsonObject.optString("thumb"));
        }
    }
}
