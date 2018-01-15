package com.juxin.predestinate.bean.my;


import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 分享渠道列表
 * Created by zm on 17/11/7.
 */
public class ShareChannelInfos extends BaseData {

    /**
     * 分享渠道类型集合
     */
    private List<Integer> channels = new ArrayList<>();

    public List<Integer> getChannels() {
        return channels;
    }

    @Override
    public void parseJson(String jsonStr) {
        JSONObject jsonObject = getJsonObjectRes(jsonStr);
        //json串解析
        JSONArray array = jsonObject.optJSONArray("channels");
        if (channels == null) {
            channels = new ArrayList<>(array.length());
        }
        for (int i = 0; i < array.length(); i++) {
            channels.add(array.optInt(i));
        }
    }

    @Override
    public String toString() {
        return "ShareChannelInfos{" +
                "channels=" + channels +
                '}';
    }
}
