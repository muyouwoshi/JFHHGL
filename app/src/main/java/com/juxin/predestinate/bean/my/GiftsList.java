package com.juxin.predestinate.bean.my;


import android.util.Log;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 礼物列表
 * Created by zm on 17/3/20.
 */
public class GiftsList extends BaseData {

    private String strGiftConfig = "";

    private List<GiftInfo> arrCommonGifts;  //普通礼物列表

    public List<GiftInfo> getArrCommonGifts() {
        List<GiftInfo> gifts = new ArrayList<>();
        if (arrCommonGifts != null) gifts.addAll(arrCommonGifts);
        return gifts;
    }

    /**
     * 根据礼物id获取礼物列表中的信息
     *
     * @param id 礼物id
     * @return 从礼物列表中获取到的单个礼物信息
     */
    public GiftInfo getGiftInfo(int id) {
        if (arrCommonGifts != null && !arrCommonGifts.isEmpty()) {
            for (GiftInfo giftInfo : arrCommonGifts) {
                if (giftInfo.getId() == id) return giftInfo;
            }
        }
        return new GiftInfo();
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
        jsonObject = jsonObject.optJSONObject("res");
        if (jsonObject == null) {
            return;
        }
        arrCommonGifts = (List<GiftInfo>) getBaseDataList(jsonObject.optJSONArray("gifts"), GiftInfo.class);
    }

    /**
     * 单个礼物信息
     */
    public static class GiftInfo extends BaseData {

        private boolean isSelect = false;
        private String pic = "";
        private String name = "";
        private int money = 0;
        private int id = 0;
        private String gif = "";
        private boolean isShow;
        private boolean isHasData; //是否有数据
        private int giftfrom = 2;  //[opt] 礼物出处 1 =从礼物背包获取 2=充钻石购买,不传参数时默认为2
        private int type = 2;      //物品类型 1 红包 2 礼物
        private int count;         //[opt]礼物id

        @Override
        public void parseJson(String s) {
            JSONObject jsonObject = getJsonObject(s);
            //json串解析
            this.setId(jsonObject.optInt("id"));
            this.setName(jsonObject.optString("name"));
            this.setGif(jsonObject.optString("packet"));
            this.setMoney(jsonObject.optInt("cost"));
            this.setPic(jsonObject.optString("img"));
        }

        public boolean isSelect() {
            return isSelect;
        }

        public void setIsSelect(boolean isSelect) {
            this.isSelect = isSelect;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getMoney() {
            return money;
        }

        public void setMoney(int money) {
            this.money = money;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
            isHasData = true;
        }

        public String getGif() {
            return gif;
        }

        public void setGif(String gif) {
            this.gif = gif;
        }

        public boolean isShow() {
            return isShow;
        }

        public void setIsShow(boolean isShow) {
            this.isShow = isShow;
        }

        public boolean isHasData() {
            return isHasData;
        }

        public void setSelect(boolean select) {
            isSelect = select;
        }

        public void setShow(boolean show) {
            isShow = show;
        }

        public void setHasData(boolean hasData) {
            isHasData = hasData;
        }

        public int getGiftfrom() {
            return giftfrom;
        }

        public void setGiftfrom(int giftfrom) {
            this.giftfrom = giftfrom;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getCount() {
            return count;
        }

        @Override
        public String toString() {
            return "GiftInfo{" +
                    "isSelect=" + isSelect +
                    ", pic='" + pic + '\'' +
                    ", name='" + name + '\'' +
                    ", money=" + money +
                    ", id=" + id +
                    ", gif='" + gif + '\'' +
                    ", isShow=" + isShow +
                    '}';
        }
    }
}
