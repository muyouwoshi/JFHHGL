package com.juxin.predestinate.bean.config.base;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * config/GetSet#pay_config节点，支付相关配置
 * Created by ZRP on 2017/7/19.
 */

public class Pay extends BaseData {

    private int min_withdraw_money;             // 最小提现金额（分）
    private LinkedList<String> payTypes;        // 支付方式控制列表
    private List<Goods> diamondList;            // 钻石购买配比列表
    private List<Goods> keyList;                // 钥匙购买配比列表

    private List<Goods> buyVipList;             // 购买VIP在线配置
    private List<Goods> bBuyVipList;            // B版购买VIP在线配置
    private List<Goods> yCoinList;              // 购买Y币在线配置

    @Override
    public void parseJson(String jsonStr) {
        JSONObject jsonObject = getJsonObject(jsonStr);
        min_withdraw_money = jsonObject.optInt("min_withdraw_money");

        payTypes = new LinkedList<>();
        JSONArray paytype_array = jsonObject.optJSONArray("paytype");
        for (int i = 0; paytype_array != null && i < paytype_array.length(); i++) {
            payTypes.add(paytype_array.optString(i));
        }

        JSONObject products_config = getJsonObject(jsonObject.optString("products_config"));
        diamondList = (List<Goods>) getBaseDataList(getJsonArray(products_config.optString("diamonds")), Goods.class);
        keyList = (List<Goods>) getBaseDataList(getJsonArray(products_config.optString("keys")), Goods.class);

        buyVipList = (List<Goods>) getBaseDataList(getJsonArray(jsonObject.optString("vip_config")), Goods.class);
        bBuyVipList = (List<Goods>) getBaseDataList(getJsonArray(jsonObject.optString("vip_config_b")), Goods.class);
        yCoinList = (List<Goods>) getBaseDataList(getJsonArray(jsonObject.optString("ycoin_config")), Goods.class);
    }

    public int getMin_withdraw_money() {
        return min_withdraw_money;
    }

    public LinkedList<String> getPayTypes() {
        return payTypes;
    }

    public List<Goods> getDiamondList() {
        return diamondList;
    }

    public List<Goods> getKeyList() {
        return keyList;
    }

    public List<Goods> getBuyVipList() {
        return buyVipList;
    }

    public List<Goods> getBBuyVipList() {
        return bBuyVipList;
    }

    public List<Goods> getYCoinList() {
        return yCoinList;
    }

    @Override
    public String toString() {
        return "Pay{" +
                "min_withdraw_money=" + min_withdraw_money +
                ", payTypes=" + payTypes +
                ", diamondList=" + diamondList +
                ", keyList=" + keyList +
                ", buyVipList=" + buyVipList +
                ", bBuyVipList=" + bBuyVipList +
                ", yCoinList=" + yCoinList +
                '}';
    }
}
