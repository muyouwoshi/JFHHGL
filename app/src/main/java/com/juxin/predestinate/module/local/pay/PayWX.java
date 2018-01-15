package com.juxin.predestinate.module.local.pay;

import android.text.TextUtils;

import com.juxin.library.utils.JniUtil;
import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Kind on 2017/4/25.
 */

public class PayWX extends BaseData implements Serializable {

    private boolean isOK = false;//是否正常


    private String payID;
    private String payContent;
    private String param;
    private int cupPayType;
    private String out_trade_no;

    //银联语音
    private String result;
    private String content;
    private String realName;
    private String mobile;
    private String pan;
    private String idcard;

    //微信支付
    private int payType;//payType 1为自有炬鑫支付，2为weixin协议三方支付, 3为webview弹微信支付 4威富通支付
    private String payData;//如果为三方支付，直接为微信协议支付：weixin://
    private String prepay_id;
    private String mch_id;
    private String app_id;
    private String qrcode_url;
    private int qrcode_time;
    private String uri;
    private JSONObject jsonParamPost;

    @Override
    public void parseJson(String jsonStr) {

    }

    public PayWX() {
        super();
    }

    public PayWX(String jsonStr, int cup) {
        if (TextUtils.isEmpty(jsonStr)) {
            return;
        }
        JSONObject jsonObject = getJsonObject(jsonStr);
        String result = jsonObject.optString("result");
        if ("1".equals(result)) {
            this.setOK(true);
            this.setResult(result);
            this.setPayID(jsonObject.optString("content"));
            this.setParam(jsonObject.optString("param"));
            this.setCupPayType(jsonObject.optInt("payType"));
        }
    }

    //微信
    public PayWX(String jsonStr) {

        if (TextUtils.isEmpty(jsonStr)) {
            return;
        }
        //支付改成返回加密数据，做解密处理
        if(!jsonStr.startsWith("{") || !jsonStr.endsWith("}")){
            jsonStr = new String(JniUtil.GetDecryptString(jsonStr));
        }

        JSONObject jsonObject = getJsonObject(jsonStr);
        if (!jsonObject.isNull("payType")) {
            this.setOK(true);
            this.setPayType(jsonObject.optInt("payType"));
            this.setPayData(jsonObject.isNull("payData") ? null : jsonObject.optString("payData"));
            this.setOut_trade_no(jsonObject.optString("out_trade_no"));
            switch (getPayType()) {
                case 1:
                    if (!jsonObject.isNull("payData")) {
                        JSONObject jsoC = getJsonObject(jsonObject.optString("payData"));
                        if (("SUCCESS".equals(jsoC.optString("return_code"))) &&
                                ("SUCCESS".equals(jsoC.optString("result_code")))) {
                            this.setPrepay_id(jsoC.optString("prepay_id"));
                            this.setMch_id(jsoC.optString("mch_id"));
                            this.setApp_id(jsoC.optString("appid"));
                        }
                    }
                    break;
                case 8:
                    JSONObject jo = getJsonObject(jsonObject.optString("payData"));
                    if (null == jo)
                        return;
                    this.setQrcode_url(jo.optString("code_img_url"));
                    this.setQrcode_time(jo.optInt("expire"));
                    this.setUri(jo.optString("code_url"));
                    break;
                case 9:
                    if (!jsonObject.isNull("payData")) {
                        JSONObject jsoC = getJsonObject(jsonObject.optString("payData"));
                        if (null == jsoC)
                            return;
                        this.setJsonParamPost(jsoC.optJSONObject("param_post"));
                        this.setPayData(jsoC.optString("payurl"));
                        this.setApp_id(jsoC.optString("appid"));
                    }
                    break;
                case 10:
                    if (!jsonObject.isNull("payData")) {
                        JSONObject jsoC = getJsonObject(jsonObject.optString("payData"));
                        this.setJsonParamPost(jsoC.optJSONObject("param_post"));
                    }
                    break;
                case 12:
                    if (!jsonObject.isNull("payData")) {
                        this.setJsonParamPost(jsonObject.optJSONObject("payData"));
                    }
                    break;
                case 13:
                    this.setQrcode_url(jsonObject.optString("btnid"));
                    break;
            }
            return;
        }
        if (!jsonObject.isNull("param")) {
            this.setOK(true);
            this.setContent(jsonObject.optString("content"));
            this.setParam(jsonObject.optString("param"));
            this.setCupPayType(jsonObject.optInt("payType"));
        }
    }

    public PayWX(String jsonStr, boolean isAlipay) {
        if (TextUtils.isEmpty(jsonStr)) {
            return;
        }

        //支付改成返回加密数据，做解密处理
        if(!jsonStr.startsWith("{") || !jsonStr.endsWith("}")){
            jsonStr = new String(JniUtil.GetDecryptString(jsonStr));
        }

        JSONObject jsonObject = getJsonObject(jsonStr);
        if (!jsonObject.isNull("param")) {
            this.setOK(true);
            this.setContent(jsonObject.optString("content"));
            this.setParam(jsonObject.optString("param"));
            this.setCupPayType(jsonObject.optInt("payType"));
            this.setOut_trade_no(jsonObject.optString("out_trade_no"));
        }
    }


    /**
     * 手机卡
     *
     * @param jsonStr
     */
    public void onPayPhoneCard(String jsonStr) {
        JSONObject jsonObject = getJsonObject(jsonStr);
        this.setResult(jsonObject.optString("result"));
        this.setPayContent(jsonObject.optString("content"));
    }

    public void onPayAngelPayF(String jsonStr) {
        if (TextUtils.isEmpty(jsonStr)) {
            return;
        }
        JSONObject jsonObject = getJsonObject(jsonStr);

        String result = jsonObject.optString("result");
        if ("1".equals(result)) {
            this.setOK(true);
            this.setResult(result);
            JSONObject object = jsonObject.optJSONObject("data");
            this.setRealName(object.optString("realName"));
            this.setMobile(object.optString("mobile"));
            this.setPan(object.optString("pan"));
            this.setIdcard(object.optString("idcard"));
        } else {
            this.setContent(jsonObject.optString("content"));
        }

    }

    public boolean isOK() {
        return isOK;
    }

    public void setOK(boolean OK) {
        isOK = OK;
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public String getPayData() {
        return payData;
    }

    public void setPayData(String payData) {
        this.payData = payData;
    }

    public String getPrepay_id() {
        return prepay_id;
    }

    public void setPrepay_id(String prepay_id) {
        this.prepay_id = prepay_id;
    }

    public String getMch_id() {
        return mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }


    public String getPayID() {
        return payID;
    }

    public void setPayID(String payID) {
        this.payID = payID;
    }

    public String getPayContent() {
        return payContent;
    }

    public void setPayContent(String payContent) {
        this.payContent = payContent;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public int getCupPayType() {
        return cupPayType;
    }

    public void setCupPayType(int cupPayType) {
        this.cupPayType = cupPayType;
    }


    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getQrcode_url() {
        return qrcode_url;
    }

    public void setQrcode_url(String qrcode_url) {
        this.qrcode_url = qrcode_url;
    }

    public int getQrcode_time() {
        return qrcode_time;
    }

    public void setQrcode_time(int qrcode_time) {
        this.qrcode_time = qrcode_time;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public JSONObject getJsonParamPost() {
        return jsonParamPost;
    }

    public void setJsonParamPost(JSONObject jsonParamPost) {
        this.jsonParamPost = jsonParamPost;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }
}