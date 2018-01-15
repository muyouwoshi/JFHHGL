package com.juxin.predestinate.ui.user.paygoods.bean;

import com.juxin.predestinate.bean.config.base.Goods;
import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 支付商品信息
 * <p>
 * Created by Su on 2016/9/18.
 */
public class PayGoods extends BaseData {

    private Goods payGood;      // 单个商品详细信息
    private ArrayList<Goods> payGoodList; // 商品列表

    @Override
    public void parseJson(String s) {
        JSONObject jsonObject = getJsonObject(s);

        //商品详细信息
        if (!jsonObject.isNull("detail")) {
            payGood = new Goods(jsonObject.optString("detail"));
        }

        // 商品列表
        if (!jsonObject.isNull("list")) {
            payGoodList = new ArrayList<>();
            this.payGoodList = (ArrayList<Goods>) getBaseDataList(jsonObject.optJSONArray("list"), Goods.class);
        }
    }

    public Goods getCommodityInfo() {
        return payGood;
    }

    public List<Goods> getCommodityList() {
        return payGoodList;
    }
}
