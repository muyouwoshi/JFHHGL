package com.juxin.predestinate.bean.my;


import com.juxin.predestinate.bean.net.BaseData;
import com.juxin.predestinate.module.logic.application.ModuleMgr;

import org.json.JSONObject;

/**
 * 认证信息
 * Created by zm on 17/3/20.
 */

public class IdCardVerifyStatusInfo extends BaseData {

    private boolean isOk;
    private long uid;            //110871516
    private String id_num;       //9223372036854775807
    private String id_front_img; //http://www.sdfsa.netsdfsadf/sdfsafsadfasdfsadfssadgsag.jpg
    private String id_back_img;  //http://www.sdfsa.netsdfsadf/sdfsafsadfasdfsadfssadsdfgsag.jpg
    private String face_img;     //http://www.sdfsa.netsdfsadf/sdfsafsadfasdfsadfssa3423dsdfgsag.jpg
    private int paytype;         //类型 1银行卡 2支付宝
    private int status;          //认证状态  1 待审核 2 审核通过 3审核不通过 4打回
    private String create_time;  //2017-05-16 17:48:08
    private String accountname;  //测试12
    private String accountnum;   //234242323423423wer
    private String bank;         //中国银行
    private String subbank;      //长沙支行
    //缩略图地址
    private String id_front_img_small;
    private String id_back_img_small;
    private String face_img_small;

    @Override
    public void parseJson(String s) {
        this.setIsOk(getJsonObject(s).optString("status"));
        JSONObject jsonObject = getJsonObject(s).optJSONObject("list");
        if (jsonObject != null){
            //json串解析
            this.setUid(jsonObject.optLong("uid"));
            this.setId_num(jsonObject.optString("id_num"));
            this.setId_front_img(jsonObject.optString("id_front_img"));
            this.setId_back_img(jsonObject.optString("id_back_img"));
            this.setFace_img(jsonObject.optString("face_img"));
            if (jsonObject.has("paytype"))
                this.setPaytype(jsonObject.optInt("paytype"));
            if (jsonObject.has("status"))
                this.setStatus(jsonObject.optInt("status"));
            this.setCreate_time(jsonObject.optString("create_time"));
            this.setAccountname(jsonObject.optString("accountname"));
            this.setAccountnum(jsonObject.optString("accountnum"));
            this.setBank(jsonObject.optString("bank"));
            this.setSubbank(jsonObject.optString("subbank"));
            this.setId_front_img_small(jsonObject.optString("id_front_img_small"));
            this.setId_back_img_small(jsonObject.optString("id_back_img_small"));
            this.setFace_img_small(jsonObject.optString("face_img_small"));
        }
    }

    public boolean isOk() {
        return isOk;
    }

    public void setIsOk(String isOk) {
        if ("ok".equalsIgnoreCase(isOk)){
            this.isOk = true;
        }else {
            this.isOk = false;
        }
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getId_num() {
        return id_num;
    }

    public void setId_num(String id_num) {
        this.id_num = id_num != null?id_num:"";
    }

    public String getId_front_img() {
        return id_front_img;
    }

    public void setId_front_img(String id_front_img) {
        this.id_front_img = id_front_img;
    }

    public String getId_back_img() {
        return id_back_img;
    }

    public void setId_back_img(String id_back_img) {
        this.id_back_img = id_back_img;
    }

    public String getFace_img() {
        return face_img;
    }

    public void setFace_img(String face_img) {
        this.face_img = face_img;
    }

    public int getPaytype() {
        return paytype;
    }

    public void setPaytype(int paytype) {
        this.paytype = paytype;
    }

    public int getStatus() {
        return status == 0?ModuleMgr.getCenterMgr().getMyInfo().getIdcard_validation():status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getAccountname() {
        return accountname;
    }

    public void setAccountname(String accountname) {
        this.accountname = accountname;
    }

    public String getAccountnum() {
        return accountnum;
    }

    public void setAccountnum(String accountnum) {
        this.accountnum = accountnum != null?accountnum:"";
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank != null?bank:"";
    }

    public String getSubbank() {
        return subbank;
    }

    public void setSubbank(String subbank) {
        this.subbank = subbank;
    }

    public String getId_front_img_small() {
        return id_front_img_small;
    }

    public void setId_front_img_small(String id_front_img_small) {
        this.id_front_img_small = id_front_img_small;
    }

    public String getId_back_img_small() {
        return id_back_img_small;
    }

    public void setId_back_img_small(String id_back_img_small) {
        this.id_back_img_small = id_back_img_small;
    }

    public String getFace_img_small() {
        return face_img_small;
    }

    public void setFace_img_small(String face_img_small) {
        this.face_img_small = face_img_small;
    }

    public boolean getIsVerifyIdCard(){
        if (status == 2 || ModuleMgr.getCenterMgr().getMyInfo().getIdcard_validation() == 2)
            return true;
        else
            return false;
    }

    @Override
    public String toString() {
        return "RankList{" +
                //                    "uid=" + uid +
                //                    ", avatar=" + avatar +
                //                    ", nickname=" + nickname +
                //                    ", gender=" + gender +
                //                    ", score=" + score +
                //                    ", exp=" + exp +
                '}';
    }
}
