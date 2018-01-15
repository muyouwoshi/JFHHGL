package com.juxin.predestinate.bean.my;

import com.juxin.library.log.PLogger;
import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by duanzheng on 2017/7/12.
 */

public class WomanAutoReplyList extends BaseData {
    private autoAvInvite auto_av_invite= new autoAvInvite();
    private ArrayList<AutoReply> auto_reply = new ArrayList<>();

    public autoAvInvite getAuto_av_invite() {
        return auto_av_invite;
    }

    public void setAuto_av_invite(autoAvInvite auto_av_invite) {
        this.auto_av_invite = auto_av_invite;
    }

    public ArrayList<AutoReply> getAuto_reply() {
        return auto_reply;
    }

    public void setAuto_reply(ArrayList<AutoReply> auto_reply) {
        this.auto_reply = auto_reply;
    }

    @Override
    public void parseJson(String jsonStr) {
        JSONObject jsonObj = getJsonObject(jsonStr);
        if(jsonObj!=null){
            String jsonData = jsonObj.optString("res");
            JSONObject jsonObject = getJsonObject(jsonData);
            JSONObject auto_av_invite = jsonObject.optJSONObject("auto_av_invite");
            if(auto_av_invite!=null){
                this.auto_av_invite.setAudio(auto_av_invite.optInt("audio")==1);
                this.auto_av_invite.setVideo(auto_av_invite.optInt("video")==1);
            }
            this.auto_reply = (ArrayList<AutoReply>) getBaseDataList(jsonObject.optJSONArray("auto_reply"), AutoReply.class);
        }

    }

    public static class autoAvInvite {
        private boolean audio;//1=自动邀请对方语音 0=否
        private boolean video;//1=自动邀请对方视频 0=否

        public boolean isAudio() {
            return audio;
        }

        public void setAudio(boolean audio) {
            this.audio = audio;
        }

        public boolean isVideo() {
            return video;
        }

        public void setVideo(boolean video) {
            this.video = video;
        }
    }

    public static class AutoReply extends BaseData{
        private int arid;
        private boolean selected;//设置为自动回复	1=选中	0=未选中
        private String speech_url;//[可选] 语音内容地址
        private String text;//[可选] 文本内容
        private String timespan;//[可选] 语音时长，若speech_url不为空，则语音时长为必选内容，单位秒
        private int status; //0=未审核 1=审核通过	2=审核失败

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getArid() {
            return arid;
        }

        public void setArid(int arid) {
            this.arid = arid;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public String getSpeech_url() {
            return speech_url;
        }

        public void setSpeech_url(String speech_url) {
            this.speech_url = speech_url;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getTimespan() {
            return timespan;
        }

        public void setTimespan(String timespan) {
            this.timespan = timespan;
        }

        @Override
        public void parseJson(String jsonStr) {
            JSONObject jsonObject = getJsonObject(jsonStr);
            this.setArid(jsonObject.optInt("arid"));
            this.setSelected(jsonObject.optInt("selected")==1);
            this.setSpeech_url(jsonObject.optString("speech_url"));
            this.setText(jsonObject.optString("text"));
            this.setTimespan(jsonObject.optString("timespan"));
            this.setStatus(jsonObject.optInt("status"));
        }
    }
}
