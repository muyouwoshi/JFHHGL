package com.juxin.predestinate.bean.my;


import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 礼物列表
 * Created by zm on 17/11/2.
 */
public class NobilityLeftGifts extends BaseData {

    private String strGiftConfig = "";

    private List<GiftsList.GiftInfo> arrCommonGifts;  //普通礼物列表

    public List<GiftsList.GiftInfo> getGifts() {
        List<GiftsList.GiftInfo> gifts = new ArrayList<>();
        if (arrCommonGifts != null) gifts.addAll(arrCommonGifts);
        return gifts;
    }

    public String getStrGiftConfig() {
        return strGiftConfig;
    }

    public void setStrGiftConfig(String strGiftConfig) {
        this.strGiftConfig = strGiftConfig;
    }

    @Override
    public void parseJson(String s) {
        strGiftConfig = s;
        JSONObject jsonObject = getJsonObject(s);
        arrCommonGifts = (List<GiftsList.GiftInfo>) getBaseDataList(jsonObject.optJSONArray("res"), GiftsList.GiftInfo.class);
    }
}
