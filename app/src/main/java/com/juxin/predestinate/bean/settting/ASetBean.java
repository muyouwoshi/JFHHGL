package com.juxin.predestinate.bean.settting;

import android.text.TextUtils;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 通用配置
 * Created by lc on 2016/12/20.
 */

public class ASetBean extends BaseData implements Serializable{

    private String entrance_url;//切水果游戏入口地址
    private String extra_url;//切水果游戏帮助地址
    private String base_url;//切水果音效base地址
    private String sound_url;//切水果游戏音效配置地址

    private String cashcow_logic_url;//摇钱树游戏逻辑接口地址
    private String cashcow_url;//摇钱树游戏入口地址
    private String cashcow_asset_url;//摇钱树音效base地址
    private String cashcow_sound_url;//摇钱树音效配置地址

    private String status;
    private String miniqinmi;   //最小亲密值
    private int mmoney;   //最小提现金额（分）
    private List<StrengthBean> strength; //体力购买配置
    private List<QinMiDuBean> qinmi; //亲密度配置
    private List<DiamondBean> diamand;

    private int video_cost_per_minute;//视频通话每分钟费用
    private int voice_cost_per_minute;//语音通话每分钟费用
    private String video_chat_apk_url;//视频插件地址
    private long videoPluginVer;

    private String pushUrl;
    private int pushShow;
    private double pushrate;

    public String getPushUrl() {
        return pushUrl;
    }

    public void setPushUrl(String pushUrl) {
        this.pushUrl = pushUrl;
    }

    public int getPushShow() {
        return pushShow;
    }

    public void setPushShow(int pushShow) {
        this.pushShow = pushShow;
    }

    public double getPushrate() {
        return pushrate;
    }

    public void setPushrate(double pushrate) {
        this.pushrate = pushrate;
    }

    public String getEntrance_url() {
        return entrance_url;
    }

    public void setEntrance_url(String entrance_url) {
        this.entrance_url = entrance_url;
    }

    public String getExtra_url() {
        return extra_url;
    }

    public void setExtra_url(String extra_url) {
        this.extra_url = extra_url;
    }

    public String getBase_url() {
        return base_url;
    }

    public void setBase_url(String base_url) {
        this.base_url = base_url;
    }

    public String getSound_url() {
        return sound_url;
    }

    public void setSound_url(String sound_url) {
        this.sound_url = sound_url;
    }

    public String getCashcow_url() {
        return cashcow_url;
    }

    public void setCashcow_url(String cashcow_url) {
        this.cashcow_url = cashcow_url;
    }

    public String getCashcow_asset_url() {
        return cashcow_asset_url;
    }

    public void setCashcow_asset_url(String cashcow_asset_url) {
        this.cashcow_asset_url = cashcow_asset_url;
    }

    public String getCashcow_sound_url() {
        return cashcow_sound_url;
    }

    public void setCashcow_sound_url(String cashcow_sound_url) {
        this.cashcow_sound_url = cashcow_sound_url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMiniqinmi() {
        return miniqinmi;
    }

    public void setMiniqinmi(String miniqinmi) {
        this.miniqinmi = miniqinmi;
    }

    public int getMmoney() {
        return mmoney;
    }

    public void setMmoney(int mmoney) {
        this.mmoney = mmoney;
    }

    public List<StrengthBean> getStrength() {
        return strength;
    }

    public void setStrength(List<StrengthBean> strength) {
        this.strength = strength;
    }

    public List<QinMiDuBean> getQinmi() {
        return qinmi;
    }

    public void setQinmi(List<QinMiDuBean> qinmi) {
        this.qinmi = qinmi;
    }

    public List<DiamondBean> getDiamand() {
        return diamand;
    }

    public void setDiamand(List<DiamondBean> diamand) {
        this.diamand = diamand;
    }

    public String getCashcow_logic_url() {
        return cashcow_logic_url;
    }

    public void setCashcow_logic_url(String cashcow_logic_url) {
        this.cashcow_logic_url = cashcow_logic_url;
    }
    public int getVideo_cost_per_minute() {
        return video_cost_per_minute;
    }
    public void setVideo_cost_per_minute(int video_cost_per_minute) {
        this.video_cost_per_minute = video_cost_per_minute;
    }

    public int getVoice_cost_per_minute() {
        return voice_cost_per_minute;
    }

    public void setVoice_cost_per_minute(int voice_cost_per_minute) {
        this.voice_cost_per_minute = voice_cost_per_minute;
    }

    public String getVideo_chat_apk_url() {
        return video_chat_apk_url;
    }

    public void setVideo_chat_apk_url(String video_chat_apk_url) {
        this.video_chat_apk_url = video_chat_apk_url;
    }

    public long getVideoPluginVer() {
        return videoPluginVer;
    }

    public void setVideoPluginVer(long videoPluginVer) {
        this.videoPluginVer = videoPluginVer;
    }

    /**
     * @return 数据是否正常返回，该方法根据实际接口返回确定是否有效
     */
    public boolean isOk() {
        if (TextUtils.isEmpty(status)) return false;
        return "ok".equals(status) || "success".equals(status);
    }


    @Override
    public void parseJson(String jsonStr) {
        JSONObject strJob = getJsonObject(jsonStr);
        status = strJob.optString("status");
        parseJson(strJob);
    }

    private void parseJson(JSONObject strJob){
        List<DiamondBean> list = new ArrayList<DiamondBean>();
        JSONArray jsonArray = strJob.optJSONArray("diamand");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.optJSONObject(i);
            DiamondBean bean = new DiamondBean();
            bean.setCost(object.optInt("cost"));
            bean.setNum(object.optInt("num"));
            bean.setPid(object.optInt("pid"));
            list.add(bean);
        }
        List<StrengthBean> strList = new ArrayList<StrengthBean>();
        JSONArray strJsonArray = strJob.optJSONArray("strength");
        for (int i = 0; i < strJsonArray.length(); i++) {
            JSONObject object = strJsonArray.optJSONObject(i);
            StrengthBean bean = new StrengthBean();
            bean.setStrength(object.optInt("strength"));
            bean.setY(object.optInt("y"));
            bean.setImg(object.optString("img"));
            strList.add(bean);
        }
        List<QinMiDuBean> qinmiList = new ArrayList<QinMiDuBean>();
        JSONArray qinmiJsonArray = strJob.optJSONArray("qinmi");
        for (int i = 0; i < qinmiJsonArray.length(); i++) {
            JSONObject object = qinmiJsonArray.optJSONObject(i);
            QinMiDuBean bean = new QinMiDuBean();
            bean.setQ(object.optInt("q"));
            bean.setRed(object.optString("red"));
            qinmiList.add(bean);
        }
        this.setStatus(status);
        this.setMmoney(strJob.optInt("mmoney"));
        this.setEntrance_url(strJob.optString("entrance_url"));
        this.setExtra_url(strJob.optString("extra_url"));
        this.setSound_url(strJob.optString("sound_url"));
        this.setBase_url(strJob.optString("base_url"));
        this.setCashcow_url(strJob.optString("cashcow_url"));
        this.setCashcow_asset_url(strJob.optString("cashcow_asset_url"));
        this.setCashcow_sound_url(strJob.optString("cashcow_sound_url"));
        this.setCashcow_logic_url(strJob.optString("cashcow_logic_url"));
        this.setDiamand(list);
        this.setStrength(strList);
        this.setQinmi(qinmiList);
        this.setMiniqinmi(strJob.optString("miniqinmi"));

        this.setPushUrl(strJob.optString("push_url"));
        this.setPushrate(strJob.optDouble("pushrate"));
        this.setPushShow(strJob.optInt("pushshow"));

        this.setVideo_cost_per_minute(strJob.optInt("videochat_minute_cost"));
        this.setVoice_cost_per_minute(strJob.optInt("audiochat_minute_cost"));
        this.setVideo_chat_apk_url(strJob.optString("videochat_apk_url"));
        this.setVideoPluginVer(strJob.optLong("plugin_version"));
    }
}
