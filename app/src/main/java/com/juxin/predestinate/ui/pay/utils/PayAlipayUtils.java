package com.juxin.predestinate.ui.pay.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.pay.PayWX;
import com.juxin.predestinate.module.local.pay.goods.PayGood;
import com.juxin.predestinate.module.local.statistics.SourcePoint;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIUtil;
import com.juxin.predestinate.ui.pay.BasePayPannel;
import com.juxin.predestinate.ui.pay.PayConst;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * Created by Kind on 2017/4/25.
 */
public class PayAlipayUtils {

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_CHECK_FLAG = 2;
    private WebView mWebView;
    private Activity activity;

    public PayAlipayUtils(Activity activity) {
        super();
        this.activity = activity;
    }

    /**
     * 判断有没有安装支付宝
     *
     * @param context
     * @return
     */
    public static boolean isAliPayInstalled(Context context) {
        Uri uri = Uri.parse("alipays://platformapi/startApp");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        ComponentName componentName = intent.resolveActivity(context.getPackageManager());
        return componentName != null;
    }

    public void onPayment(final PayGood payGood) {
        PayConst.payFlag = false;
        PayConst.payGood = payGood;
        PayConst.out_trade_no = "";

        String source = SourcePoint.getInstance().getSource(payGood.getPay_name());
        //支付宝支付
        ModuleMgr.getCommonMgr().reqCUPOrAlipayMethod(UrlParam.reqAlipay, BasePayPannel.getOutTradeNo(), payGood.getPay_name(),
                payGood.getPay_id(), payGood.getPay_money(), source, new RequestComplete() {
                    @Override
                    public void onRequestComplete(final HttpResponse response) {
                        PayWX payWX = new PayWX(response.getResponseString(), true);
                        if (!payWX.isOK()) {
                            PToast.showShort("支付出错，请重试！");
                            UIUtil.closeNullAct(activity);
                            return;
                        }
                        PayConst.out_trade_no = payWX.getOut_trade_no();

                        To_Pay(payWX.getCupPayType(), payWX.getParam());
                    }
                });
    }

    private void To_Pay(int payType, final String payInfo) {
        if (payType == 2) {
            try {
                //改为内置浏览器
                AlipayToWebView(payInfo);
                PayConst.payFlag = true;
            } catch (Exception e) {
                PToast.showShort(R.string.pay_alipay_not_install);
            }

            return;
        }

        if (!isAliPayInstalled(activity))
            PayConst.payFlag = true;

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(activity);
                // 调用支付接口
                String result = alipay.pay(payInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    private void AlipayToWebView(String url) {
        //应用过程中将其隐藏掉效果更佳
        mWebView = new WebView(activity);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // 设置可以访问文件
        webSettings.setAllowFileAccess(true);
        // 设置支持缩放
        webSettings.setBuiltInZoomControls(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // webSettings.setDatabaseEnabled(true);

        // 使用localStorage则必须打开
        webSettings.setDomStorageEnabled(true);
        webSettings.setGeolocationEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (parseScheme(url)) {
                    try {
                        Uri uri = Uri.parse(url);
                        Intent intent;
                        intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        intent.addCategory("android.intent.category.BROWSABLE");
                        intent.setComponent(null);
                        // intent.setSelector(null);
                        activity.startActivity(intent);
                    } catch (Exception e) {
                    }
                } else {
                    view.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                PToast.showShort("支付失败");
                UIUtil.closeNullAct(activity);
            }

            @Override
            public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
                sslErrorHandler.proceed();
            }
        });
        mWebView.loadUrl(url);
        mWebView.onResume();
        mWebView.resumeTimers();
    }

    public boolean parseScheme(String url) {
        if (url.toLowerCase().contains("platformapi/startapp")) {
            return true;
        } else {
            return false;
        }
    }


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    Result resultObj = new Result((String) msg.obj);
                    String resultStatus = resultObj.resultStatus;

                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        PayConst.payFlag = true;
                        PToast.showShort("支付成功");
                        //更新信息
                        MsgMgr.getInstance().sendMsg(MsgType.MT_Update_MyInfo, null);
//                        if(activity != null && activity instanceof PayListAct)
//                            ((PayListAct) activity).result();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”
                        // 代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            PayConst.payFlag = true;
                            PToast.showShort("支付结果确认中");
                        } else {
                            PayConst.payFlag = false;
                            PToast.showShort("支付失败");
                        }
                    }
                    break;
                }
                case SDK_CHECK_FLAG: {
                    PToast.showShort("检查结果为：" + msg.obj);
                    break;
                }
                default:
                    break;
            }
            //通知刷个人资料
            MsgMgr.getInstance().sendMsg(MsgType.MT_Update_MyInfo, null);
        }
    };
}
