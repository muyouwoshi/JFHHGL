package com.juxin.predestinate.bean.config;

import android.text.TextUtils;

import com.juxin.library.log.PSP;
import com.juxin.library.utils.FileUtil;
import com.juxin.predestinate.bean.config.base.Action;
import com.juxin.predestinate.bean.config.base.Common;
import com.juxin.predestinate.bean.config.base.Gift;
import com.juxin.predestinate.bean.config.base.H5;
import com.juxin.predestinate.bean.config.base.Lite;
import com.juxin.predestinate.bean.config.base.NobilityList;
import com.juxin.predestinate.bean.config.base.Pay;
import com.juxin.predestinate.bean.config.base.TalkConfig;
import com.juxin.predestinate.bean.config.base.Video;
import com.juxin.predestinate.bean.discover.Active;
import com.juxin.predestinate.bean.net.BaseData;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.config.FinalKey;

import org.json.JSONObject;

import java.util.List;

/**
 * 在线配置
 * Created by ZRP on 2017/4/14.
 */
public class CommonConfig extends BaseData {

    public static final String STATIC_CONFIG_FILE = "static_config.json";

    private Lite lite;                  // 轻量包升级全量包配置
    private Common common;              // 一些杂乱无法归类的配置
    private Gift gift;                  // 发送礼物选择列表，根据不同场景选择不同的列表
    private H5 h5;                      // h5资源文件下载配置
    private NobilityList nobilityList;  // 爵位信息配表
    private Pay pay;                    // 支付信息配置
    private Video video;                // 视频信息配置
    private Action action;              // 活动推送配置
    private List<Active> activeList;    // 活动礼包回馈
    private TalkConfig talkConfig;      //聊天信息配置

    @Override
    public void parseJson(String jsonStr) {
        JSONObject rootObject = getJsonObject(jsonStr);
        if (TextUtils.isEmpty(jsonStr) || rootObject.isNull("res")
                || TextUtils.isEmpty(rootObject.optString("res"))) {
            String pspServerString = PSP.getInstance().getString(FinalKey.SERVER_STATIC_CONFIG, "");
            jsonStr = TextUtils.isEmpty(pspServerString) ?
                    FileUtil.getFromAssets(App.getContext(), STATIC_CONFIG_FILE) : pspServerString;
        }
        PSP.getInstance().put(FinalKey.SERVER_STATIC_CONFIG, jsonStr);
        JSONObject jsonObject = getJsonObject(rootObject.optString("res"));

        lite = new Lite();
        lite.parseJson(jsonObject.optString("android_config"));

        common = new Common();
        common.parseJson(jsonObject.optString("common_config"));

        gift = new Gift();
        gift.parseJson(jsonObject.optString("gift_config"));

        h5 = new H5();
        h5.parseJson(jsonObject.optString("h5_config"));

        nobilityList = new NobilityList();
        nobilityList.parseJson(jsonObject.optString("nobility_config"));

        pay = new Pay();
        pay.parseJson(jsonObject.optString("pay_config"));

        video = new Video();
        video.parseJson(jsonObject.optString("video_config"));

        action = new Action();
        action.parseJson(jsonObject.optString("web_activity_config"));

        activeList = (List<Active>) getBaseDataList(jsonObject.optJSONArray("activity_config"), Active.class);

        talkConfig = new TalkConfig();
        talkConfig.parseJson(jsonObject.optString("talk_config"));
    }

    public Lite getLite() {
        return lite == null ? new Lite() : lite;
    }

    public Common getCommon() {
        return common == null ? new Common() : common;
    }

    public Gift getGift() {
        return gift == null ? new Gift() : gift;
    }

    public H5 getH5() {
        return h5 == null ? new H5() : h5;
    }

    public NobilityList getNobilityList() {
        return nobilityList == null ? new NobilityList() : nobilityList;
    }

    public Pay getPay() {
        return pay == null ? new Pay() : pay;
    }

    public Video getVideo() {
        return video == null ? new Video() : video;
    }

    public Action getAction() {
        return action == null ? new Action() : action;
    }

    // ----------------外层获取内层方法------------------

    public List<NobilityList.Nobility> getNobilitys() {
        return getNobilityList().getNobilityList();
    }

    /**
     * 获取第二个爵位
     * ( 每个爵位分 5 级 )
     */
    public NobilityList.Nobility queryLv2Nobility(int gender) {
        return getNobilityList().queryLv2Nobility(gender);
    }

    /**
     * 获取Nobility
     * ( 每个爵位分 5 级 )
     */
    public NobilityList.Nobility queryNobility(int level, int gender) {
        return getNobilityList().queryNobility(level, gender);
    }

    /**
     * 获取Nobility 小于6级的是空数据对象
     * ( 每个爵位分 5 级 )
     */
    public NobilityList.Nobility queryNobility2(int level, int gender) {
        return getNobilityList().queryNobility2(level, gender);
    }

    /**
     * 通过礼包Id 获取对应信息
     *
     * @param packId 礼包Id
     */
    public Active queryActiveConfig(int packId) {
        if (activeList == null || activeList.isEmpty()) {
            return new Active();
        }
        for (Active temp : activeList) {
            if (packId == temp.getPack_id()) return temp;
        }
        return new Active();
    }

    public TalkConfig getTalkConfig() {
        if(talkConfig == null){
            talkConfig = new TalkConfig();
        }
        return talkConfig;
    }

    public void setTalkConfig(TalkConfig talkConfig) {
        this.talkConfig = talkConfig;
    }

    @Override
    public String toString() {
        return "CommonConfig{" +
                "lite=" + lite +
                ", common=" + common +
                ", gift=" + gift +
                ", h5=" + h5 +
                ", nobilityList=" + nobilityList +
                ", pay=" + pay +
                ", video=" + video +
                ", action=" + action +
                '}';
    }
}
