package com.juxin.predestinate.bean.config;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

/**
 * 视频聊天配置
 * Created by IQQ on 2016/12/20.
 */

public class VideoVerifyBean extends BaseData {
    private int videochat;
    private int audiochat;
    private String imgurl;
    private String videourl;
    private int status;      // 0:未认证 ,1:审核中， 2：审核未通过，3：审核通过
    private int videoprice;  // 视频价格
    private int audioprice;  //音频价格
    private int rank_video_agio;//爵位视频打折
    private int rank_audio_agio;//爵位音频打折

    public boolean isVerifyVideo() {
        return status == 3;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getVideochat() {
        return videochat;
    }

    public boolean getBooleanVideochat() {
        return videochat != 0;
    }

    public void setVideochat(int videochat) {
        this.videochat = videochat;
    }

    public int getAudiochat() {
        return audiochat;
    }

    public boolean getBooleanAudiochat() {
        return audiochat != 0;
    }

    public void setAudiochat(int audiochat) {
        this.audiochat = audiochat;
    }


    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getVideourl() {
        return videourl;
    }

    public void setVideourl(String videourl) {
        this.videourl = videourl;
    }

    public int getVideoprice() {
        return videoprice;
    }

    public void setVideoprice(int videoprice) {
        this.videoprice = videoprice;
    }

    public int getAudioprice() {
        return audioprice;
    }

    public void setAudioprice(int audioprice) {
        this.audioprice = audioprice;
    }

    public int getRank_video_agio() {
        return rank_video_agio;
    }

    public int getRank_audio_agio() {
        return rank_audio_agio;
    }

    @Override
    public void parseJson(String jsonStr) {
        JSONObject jsonObject = getJsonObject(jsonStr);
        String status = jsonObject.optString("status");
        if ("ok".equals(status)) {
            JSONObject resJob = jsonObject.optJSONObject("res");
            if (resJob != null) {
                this.videochat = resJob.optInt("videochat");
                this.audiochat = resJob.optInt("audiochat");
                JSONObject verifyJob = resJob.optJSONObject("videoverify");
                this.imgurl = verifyJob.optString("imgurl");
                this.videourl = verifyJob.optString("videourl");
                this.status = verifyJob.optInt("status");
                this.videoprice = verifyJob.optInt("videoprice");
                this.audioprice = verifyJob.optInt("audioprice");
                this.rank_video_agio = verifyJob.optInt("rank_video_agio");
                this.rank_audio_agio = verifyJob.optInt("rank_audio_agio");
            }
        }
    }
}


