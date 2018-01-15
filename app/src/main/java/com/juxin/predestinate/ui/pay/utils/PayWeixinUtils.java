package com.juxin.predestinate.ui.pay.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;

import com.juxin.library.log.PLogger;
import com.juxin.library.log.PToast;
import com.juxin.library.utils.EncryptUtil;
import com.juxin.library.utils.NetworkUtils;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.net.BasicNameValuePair;
import com.juxin.predestinate.bean.net.NameValuePair;
import com.juxin.predestinate.module.local.pay.PayWX;
import com.juxin.predestinate.module.local.pay.goods.PayGood;
import com.juxin.predestinate.module.local.statistics.SourcePoint;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.module.util.UIUtil;
import com.juxin.predestinate.ui.pay.PayConst;
import com.juxin.predestinate.ui.pay.wepayother.third.JXWechatPay;
import com.juxin.predestinate.ui.pay.wepayother.third.ZFWechatPay;
import com.juxin.predestinate.ui.pay.wepayother.third.ZYWechatPay;
import com.switfpass.pay.MainApplication;
import com.switfpass.pay.activity.PayPlugin;
import com.switfpass.pay.bean.RequestMsg;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import eposp.wtf_library.core.WtfPlugin;

/**
 * Created by Kind on 2017/4/24.
 * Edited by siow
 */

public class PayWeixinUtils {

    private Context context;
    private IWXAPI api;

    private int iPayIdx;
    private int[] aryPayType = {0, 3, 6};
    private WebView mWebView;
    private boolean isToPay = false; //确保每次只调用一次js

    public PayWeixinUtils(Context context) {
        this.context = context;
        api = WXAPIFactory.createWXAPI(context, "");
    }

    public void onPayment(final PayGood payGood) {
        PayConst.payFlag = false;
        PayConst.payGood = payGood;
        PayConst.out_trade_no = "";

        if (!api.isWXAppInstalled()) {
            PToast.showShort("未安装微信!");
            UIUtil.closeNullAct(context);
            return;
        }

        int payCType = -1;
        if (iPayIdx < aryPayType.length && aryPayType[iPayIdx] != 0) {
            payCType = aryPayType[iPayIdx];
        }
        String source = SourcePoint.getInstance().getSource(payGood.getPay_name());
        ModuleMgr.getCommonMgr().reqWXMethod(payGood.getPay_name(), payGood.getPay_id(), payGood.getPay_money(), payCType, source, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                PayWX payWX = new PayWX(response.getResponseString());
                if (payWX.getPayData() != null) {
                    PayConst.out_trade_no = payWX.getOut_trade_no();

                    To_Pay(payWX);
                    return;
                }

                //支付数据请求失败，自动切换支付通道
                if (payWX.getPayType() == 1 && iPayIdx < aryPayType.length - 1) {
                    iPayIdx++;
                    onPayment(payGood);
                    return;
                }

                PToast.showShort(R.string.request_error);
                UIUtil.closeNullAct(context);
            }
        });
    }

    private void To_Pay(PayWX payWX) {
        switch (payWX.getPayType()) {
            case 1:
                To_Pay_WeiXin_SDK(payWX);
                break;
            case 2:
                To_Pay_WeiXin_Protocol(payWX.getPayData());
                break;
            case 3:
            case 6:
                To_Pay_WeiXin_Http(payWX.getPayData());
                break;
            case 4:
                To_Pay_WeiXin_Wft_Wap(payWX.getPayData());
                break;
            case 5:
                To_Pay_WeiXin_Wft_App(payWX.getPayData());
                break;
            case 8:
                PayConst.payFlag = true;
                UIShow.showWxpayForQRCode(context, payWX.getQrcode_url(), payWX.getQrcode_time(),
                        PayConst.payGood.getPay_money(), payWX.getUri());
                break;
            case 9:
                PayConst.payFlag = true;
                JXWechatPay.Pay(context, payWX);
                break;
            case 10:
                ZFWechatPay.Pay(context, payWX.getJsonParamPost());
                break;
            case 11:
                UIShow.showWePayForH5(context, payWX.getPayData());
                break;
            case 12:
                ZYWechatPay.Pay(context, payWX.getJsonParamPost());
                break;
            case 13:
                To_Pay_WeiXin_Http_Click(payWX.getPayData(), payWX.getQrcode_url());
                break;
            default:
                PToast.showShort("不支持的支付类型！");
                UIUtil.closeNullAct(context);
                break;
        }
    }

    private void To_Pay_WeiXin_SDK(PayWX payWX) {
        try {
            Constant.WEIXIN_APP_ID = payWX.getApp_id();

//            Random random = new Random();
            PayReq req = new PayReq();
            req.appId = payWX.getApp_id();
            req.partnerId = payWX.getMch_id();
            req.prepayId = payWX.getPrepay_id();
//            req.nonceStr = MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
            //MD5.encode(String.valueOf(random.nextInt(10000)));
            req.nonceStr = genOutTradNo();
            req.timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
            req.packageValue = "Sign=WXPay";

            List<NameValuePair> signParams = new LinkedList<>();
            signParams.add(new BasicNameValuePair("appid", req.appId));
            signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
            signParams.add(new BasicNameValuePair("package", req.packageValue));
            signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
            signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
            signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

            req.sign = genAppSign(signParams);

            api.registerApp(req.appId);
            api.sendReq(req);
        } catch (Exception e) {
            e.printStackTrace();
            PToast.showShort(R.string.pay_order_error);
            UIUtil.closeNullAct(context);
        }
    }

    private static String genAppSign(List<NameValuePair> params) {
        StringBuilder ss = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            ss.append(params.get(i).getName());
            ss.append('=');
            ss.append(params.get(i).getValue());
            ss.append('&');
        }
        ss.append("key=");

        ss.append(Constant.WEIXIN_App_Key);

//		sb.append("sign str\n" + ss.toString() + "\n\n");
        //String appSign = MD5.getMessageDigest(ss.toString().getBytes()).toUpperCase();
        String appSign = EncryptUtil.md5(ss.toString()).toUpperCase();
        Log.e("orion", appSign);
        return appSign;
    }

    private String genOutTradNo() {
        Random random = new Random();
        return EncryptUtil.md5(String.valueOf(random.nextInt(10000)));
//        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    private void To_Pay_WeiXin_Protocol(String payData) {
        try {
            isToPay = true;
            if (null == context) return;  //context为空？特殊金立机型。
            if (!payData.toLowerCase().startsWith("weixin://")) {
                PToast.showShort(R.string.request_error);
                UIUtil.closeNullAct(context);
                return;
            }
            Uri uri = Uri.parse(payData);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                if (context instanceof Activity) {
                    ((Activity) context).startActivityForResult(intent, Constant.PAY_WEIXIN);
                } else {
                    context.startActivity(intent);
                }
                PayConst.payFlag = true;
            } else {
                PToast.showShort("微信未安装");
                UIUtil.closeNullAct(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
            PToast.showShort(R.string.pay_order_error);
            UIUtil.closeNullAct(context);
        }
    }


    private void To_Pay_WeiXin_Http(String payData) {
        if (!payData.toLowerCase().startsWith("http://") &&
                !payData.toLowerCase().startsWith("https://")) {
            PToast.showShort(R.string.request_error);
            UIUtil.closeNullAct(context);
            return;
        }
        LoadingDialog.show((FragmentActivity) context, "");

        mWebView = new WebView(context);
        // 设置WebView上的支持效果
        WebSettings webSettings = mWebView.getSettings();
        // 多窗口
        webSettings.supportMultipleWindows();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowFileAccess(true);

        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                PLogger.d("----Pay Jump---" + url);
                if (url.toLowerCase().startsWith("https") || url.toLowerCase().startsWith("http")) {
                    String ref = NetworkUtils.getDomainName(view.getUrl());
                    Map extraHeaders = new HashMap();
                    extraHeaders.put("Referer", ref);
                    view.loadUrl(url, extraHeaders);
                } else {
                    To_Pay_WeiXin_Protocol(url);
                    LoadingDialog.closeLoadingDialog(500);
                }
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                LoadingDialog.closeLoadingDialog(500);
                PToast.showShort("支付失败");
                UIUtil.closeNullAct(context);
            }

            @Override
            public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
                sslErrorHandler.proceed();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            dealJavascriptLeak(mWebView);
        }

        mWebView.loadUrl(payData);
        mWebView.onResume();
        mWebView.resumeTimers();
    }

    private void To_Pay_WeiXin_Http_Click(String payData, final String id) {
        isToPay = false;
        if (!payData.toLowerCase().startsWith("http://") &&
                !payData.toLowerCase().startsWith("https://")) {
            PToast.showShort(R.string.request_error);
            UIUtil.closeNullAct(context);
            return;
        }
        LoadingDialog.show((FragmentActivity) context, "");

        mWebView = new WebView(context);
        // 设置WebView上的支持效果
        WebSettings webSettings = mWebView.getSettings();
        // 多窗口
        webSettings.supportMultipleWindows();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.toLowerCase().startsWith("https") || url.toLowerCase().startsWith("http")) {
                    String ref = NetworkUtils.getDomainName(view.getUrl());
                    Map extraHeaders = new HashMap();
                    extraHeaders.put("Referer", ref);
                    view.loadUrl(url, extraHeaders);

                } else {
                    To_Pay_WeiXin_Protocol(url);
                    LoadingDialog.closeLoadingDialog(500);
                }
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                LoadingDialog.closeLoadingDialog(500);
                PToast.showShort("支付失败");
                UIUtil.closeNullAct(context);
            }

            @Override
            public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
                sslErrorHandler.proceed();
            }

            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);
                String js = "javascript:document.querySelectorAll(\"" + id + "\")[0].click();";
                if (!isToPay) webView.loadUrl(js);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            dealJavascriptLeak(mWebView);
        }

        mWebView.loadUrl(payData);
        mWebView.onResume();
        mWebView.resumeTimers();
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void dealJavascriptLeak(WebView webView) {
        webView.removeJavascriptInterface("searchBoxJavaBridge_");
        webView.removeJavascriptInterface("accessibility");
        webView.removeJavascriptInterface("accessibilityTraversal");
    }


    private void To_Pay_WeiXin_Wft_Wap(String payData) {
        try {
            if (TextUtils.isEmpty(payData)) {
                PToast.showShort(R.string.pay_order_error);
                UIUtil.closeNullAct(context);
                return;
            }

//            payData = "{\"tradeType\":\"pay.weixin.app\",\"productDesc\":\"钻石VIP月付\",\"callbackUrl\":\"\",\"orderNo\":\"2016102717573088224\",\"wx_key\":\"wx59b3a81e268f96a6\",\"privateKey\":\"8a4d7ee09f2db1dc793dcd0f9aeafc1f\",\"remark\":\"2016102717573088224\",\"orderAmount\":1,\"payType\":\"1\",\"merchantNo\":\"898875454113076\",\"notifyUrl\":\"http:\\/\\/p.app.yuanfenba.net\\/Pay\\/shzyAppPayNotify\",\"productName\":\"钻石VIP月付\"}";
            JSONObject jso = new JSONObject(payData);

            Constant.WEIXIN_APP_ID = jso.optString("wx_key");

            Map<String, String> data = new HashMap<>();
            data.put("merchantNo", jso.optString("merchantNo")); // 微通付商户号
            data.put("orderAmount", jso.optString("orderAmount")); //交易金额，分为单位
            data.put("orderNo", jso.optString("orderNo")); //第三方平台订单号
            data.put("notifyUrl", jso.optString("notifyUrl")); //
            data.put("callbackUrl", jso.optString("callbackUrl")); //
            data.put("payType", jso.optString("payType")); //交易方式，1：app支付；
            data.put("productName", jso.optString("productName")); //产品名
            data.put("productDesc", jso.optString("productDesc")); //产品描述
            data.put("remark", jso.optString("remark")); //备注
            data.put("privateKey", jso.optString("privateKey")); //微通付商户密钥
            data.put("wx_key", jso.optString("wx_key")); //微保平台
            data.put("tradeType", jso.optString("tradeType")); //交易类型，微信支付
            new WtfPlugin().getOrderForPay((Activity) context, data);
            PayConst.payFlag = true;
        } catch (Exception e) {
            e.printStackTrace();
            PToast.showShort(R.string.pay_order_error);
            UIUtil.closeNullAct(context);
        }
    }

    private void To_Pay_WeiXin_Wft_App(String payData) {
        try {
            if (TextUtils.isEmpty(payData)) {
                PToast.showShort(R.string.pay_order_error);
                UIUtil.closeNullAct(context);
                return;
            }

            JSONObject jso = new JSONObject(payData);

            Constant.WEIXIN_APP_ID = jso.optString("appid");

            RequestMsg msg = new RequestMsg();
            msg.setTokenId(jso.optString("token_id"));
            msg.setTradeType(MainApplication.WX_APP_TYPE);
            msg.setAppId(jso.optString("appid"));

            PayPlugin.unifiedAppPay((Activity) context, msg);
        } catch (Exception e) {
            e.printStackTrace();
            PToast.showShort(R.string.pay_order_error);
            UIUtil.closeNullAct(context);
        }
    }
}
