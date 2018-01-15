package com.juxin.predestinate.bean.my;


import com.juxin.predestinate.bean.net.BaseData;
import com.juxin.predestinate.module.logic.application.ModuleMgr;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 背包
 * Created by zm on 17/10/11.
 */
public class BagList extends BaseData {

    private List<BagInfo> bagInfoList;
    private BagInfo redInfo;

    @Override
    public void parseJson(String s) {
        JSONObject jsonObject = getJsonObject(s);
        jsonObject = jsonObject.optJSONObject("res");
        if (jsonObject == null) return;
        bagInfoList = (List<BagInfo>) getBaseDataList(jsonObject.optJSONArray("list"), BagInfo.class);
    }

    /**
     * @return 获取背包列表
     */
    public List<GiftsList.GiftInfo> getBagGifts() {
        return getGiftsFromIds(bagInfoList);
    }

    /**
     * 根据礼物id列表获取礼物信息列表<p>
     * 在线配置请求回来之后有可能礼物信息还未请求返回[礼物接口需登录才能进行请求]，故公开此方法，外部在使用时单独进行查询
     *
     * @param ids 礼物id列表
     * @return 礼物信息列表
     */
    public List<GiftsList.GiftInfo> getGiftsFromIds(List<BagInfo> ids) {
        List<GiftsList.GiftInfo> giftInfos = new ArrayList<>();
        if (ids == null || ids.isEmpty()) return giftInfos;

        GiftsList giftLists = ModuleMgr.getCommonMgr().getGiftLists();
        GiftsList.GiftInfo info;
        for (BagInfo bagInfo : ids) {
            if (bagInfo.getType() == 1) {
                redInfo = bagInfo;
            } else {
                info = giftLists.getGiftInfo(bagInfo.getGift_id());
                GiftsList.GiftInfo giftInfo = new GiftsList.GiftInfo();
                if (info != null) {
                    giftInfo.setId(info.getId());
                    giftInfo.setHasData(info.isHasData());
                    giftInfo.setName(info.getName());
                    giftInfo.setPic(info.getPic());
                    giftInfo.setType(bagInfo.getType());
                    giftInfo.setGiftfrom(1);
                    giftInfo.setCount(bagInfo.getCount());
                    giftInfos.add(giftInfo);
                }
            }
        }
        if (redInfo != null) {
            GiftsList.GiftInfo giftInfo = new GiftsList.GiftInfo();
            giftInfo.setGiftfrom(1);
            giftInfo.setType(redInfo.getType());
            giftInfo.setCount(redInfo.getCount());
            giftInfo.setName("红包");
            if (giftInfos.size() > 0) {
                giftInfos.add(0, giftInfo);
            } else {
                giftInfos.add(giftInfo);
            }
        }
        return giftInfos;
    }

    /**
     * 单个礼物信息
     */
    public static class BagInfo extends BaseData {

        private int type;    //物品类型 1 红包 2 礼物
        private int count;   //物品数量
        private int gift_id; //[opt]礼物id

        @Override
        public void parseJson(String s) {
            JSONObject jsonObject = getJsonObject(s);
            //json串解析
            this.setType(jsonObject.optInt("type"));
            this.setCount(jsonObject.optInt("count"));
            this.setGift_id(jsonObject.optInt("gift_id"));
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

        public void setCount(int count) {
            this.count = count;
        }

        public int getGift_id() {
            return gift_id;
        }

        public void setGift_id(int gift_id) {
            this.gift_id = gift_id;
        }

        @Override
        public String toString() {
            return "BagInfo{" +
                    "type=" + type +
                    ", count=" + count +
                    ", gift_id=" + gift_id +
                    '}';
        }
    }
}
