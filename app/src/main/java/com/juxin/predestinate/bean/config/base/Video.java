package com.juxin.predestinate.bean.config.base;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * config/GetSet#video_config节点
 * Created by ZRP on 2017/7/19.
 */

public class Video extends BaseData {

    private boolean isVideoCallNeedVip; //发起视频聊天是否需要VIP
    private boolean isAudioCallNeedVip; //发起音频聊天是否需要VIP

    private int screenshot_interval;                // 鉴黄检测间隔时间,单位秒
    private int screenshot_interval_first_minute;   // 鉴黄首次截图时间，单位秒

    private List<String> report_reasons;            // 视频中举报项
    private List<String> praise_reasons;            // 视频结束好评项

    @Override
    public void parseJson(String jsonStr) {
        JSONObject jsonObject = getJsonObject(jsonStr);

        isVideoCallNeedVip = jsonObject.optInt("video_required_vip") == 1;
        isAudioCallNeedVip = jsonObject.optInt("audio_required_vip") == 1;

        screenshot_interval = jsonObject.optInt("screenshot_interval");
        screenshot_interval_first_minute = jsonObject.optInt("screenshot_interval_first_minute");

        report_reasons = new ArrayList<>();
        JSONArray report_reasons_array = jsonObject.optJSONArray("report_criticism_reasons");
        for (int i = 0; report_reasons_array != null && i < report_reasons_array.length(); i++) {
            report_reasons.add(report_reasons_array.optString(i));
        }

        praise_reasons = new ArrayList<>();
        JSONArray praise_reasons_array = jsonObject.optJSONArray("report_praise_reasons");
        for (int i = 0; praise_reasons_array != null && i < praise_reasons_array.length(); i++) {
            praise_reasons.add(praise_reasons_array.optString(i));
        }
    }

    public boolean isVideoCallNeedVip() {
        return isVideoCallNeedVip;
    }

    public boolean isAudioCallNeedVip() {
        return isAudioCallNeedVip;
    }

    public int getScreenshot_interval() {
        return screenshot_interval;
    }

    public int getScreenshot_interval_first_minute() {
        return screenshot_interval_first_minute;
    }

    public List<String> getReport_reasons() {
        return report_reasons;
    }

    public List<String> getPraise_reasons() {
        return praise_reasons;
    }

    @Override
    public String toString() {
        return "Video{" +
                "isVideoCallNeedVip=" + isVideoCallNeedVip +
                ", isAudioCallNeedVip=" + isAudioCallNeedVip +
                ", screenshot_interval=" + screenshot_interval +
                ", screenshot_interval_first_minute=" + screenshot_interval_first_minute +
                ", report_reasons=" + report_reasons +
                '}';
    }
}
