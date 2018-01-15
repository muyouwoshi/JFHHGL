package com.juxin.predestinate.module.local.pay.goods;

import android.text.TextUtils;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 单个商品
 * Created by Kind on 2017/4/21.
 */

public class PayGood extends BaseData implements Serializable {

    private boolean isOK = false;//是否正常
    private int pay_id;
    private int index;
    private String pay_name;
    private String pay_desc;
    private String pay_price;
    private String pay_unitPrice;
    private String pay_free;
    private int pay_money = 0;
    private String pay_data;
    private int pay_group;

    @Override
    public void parseJson(String jsonStr) {
        JSONObject jsonObject = getJsonObject(jsonStr);
        this.setPay_name(jsonObject.optString("name"));
        this.setIndex(jsonObject.optInt("index"));
        this.setPay_desc(jsonObject.optString("desc"));
        this.setPay_price(jsonObject.optString("price"));
        this.setPay_unitPrice(jsonObject.optString("unitPrice"));
        this.setPay_free(jsonObject.optString("free"));
        this.setPay_money(jsonObject.optInt("money"));
        this.setPay_data(jsonObject.optString("date"));
    }

    public PayGood() {
        super();
    }

    public PayGood(String jsonStr) {
        if (!TextUtils.isEmpty(jsonStr) && !"null".equals(jsonStr) && jsonStr.length() > 3) {
            this.setOK(true);
            parseJson(jsonStr);
        }
    }


    public boolean isOK() {
        return isOK;
    }

    public void setOK(boolean OK) {
        isOK = OK;
    }

    public int getPay_id() {
        return pay_id;
    }

    public void setPay_id(int pay_id) {
        this.pay_id = pay_id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getPay_name() {
        return pay_name;
    }

    public void setPay_name(String pay_name) {
        this.pay_name = pay_name;
    }

    public String getPay_desc() {
        return pay_desc;
    }

    public void setPay_desc(String pay_desc) {
        this.pay_desc = pay_desc;
    }

    public String getPay_price() {
        return pay_price;
    }

    public void setPay_price(String pay_price) {
        this.pay_price = pay_price;
    }

    public String getPay_unitPrice() {
        return pay_unitPrice;
    }

    public void setPay_unitPrice(String pay_unitPrice) {
        this.pay_unitPrice = pay_unitPrice;
    }

    public String getPay_free() {
        return pay_free;
    }

    public void setPay_free(String pay_free) {
        this.pay_free = pay_free;
    }

    public int getPay_money() {
        return pay_money;
    }

    public void setPay_money(int pay_money) {
        this.pay_money = pay_money;
    }

    public String getPay_data() {
        return pay_data;
    }

    public void setPay_data(String pay_data) {
        this.pay_data = pay_data;
    }

    public int getPay_group() {
        return pay_group;
    }

    public void setPay_group(int pay_group) {
        this.pay_group = pay_group;
    }
}
