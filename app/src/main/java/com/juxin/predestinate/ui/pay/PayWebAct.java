package com.juxin.predestinate.ui.pay;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.library.utils.NetworkUtils;
import com.juxin.library.view.CustomFrameLayout;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.pay.goods.PayGood;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UrlEnc;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Kind on 2017/4/26.
 */
public class PayWebAct extends BaseActivity {

    private WebView payalipay_web_webview;
    private PayGood payGood;
    private CustomFrameLayout customFrameLayout;

    private boolean isB = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f1_pay_alipay_web);
        setBackView(R.id.base_title_back, "信用卡支付");
        setTitleLeft("", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (payalipay_web_webview.canGoBack()) {
                    payalipay_web_webview.goBack();
                } else {
                    finish();
                }
            }
        });
        initView();
    }

    /**
     * 模拟点击某个指定坐标作用在View上 * @param view * @param x * @param y
     */
    public void clickView(View view, float x, float y) {
        long downTime = SystemClock.uptimeMillis();
        MotionEvent downEvent = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, x, y, 0);
        MotionEvent upEvent = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_UP, x, y, 0);
        view.onTouchEvent(downEvent);
        view.onTouchEvent(upEvent);
        downEvent.recycle();
        upEvent.recycle();
    }

    public void initView() {
        payGood = (PayGood) getIntent().getSerializableExtra("payGood");
        customFrameLayout = (CustomFrameLayout) findViewById(R.id.payalipay_web_custom);
        payalipay_web_webview = (WebView) findViewById(R.id.payalipay_web_webview);
        customFrameLayout.setList(new int[]{R.id.payalipay_web_webview, R.id.common_loading, R.id.common_net_error});
        payalipay_web_webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        initData();
        findViewById(R.id.error_btn).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                initData();
            }
        });
        customFrameLayout.show(R.id.common_loading);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initData() {
        if (NetworkUtils.isNotConnected(this)) {
            customFrameLayout.show(R.id.common_net_error);
            return;
        }

        // 设置WebView上的支持效果
        WebSettings webSettings = payalipay_web_webview.getSettings();
        // 多窗口
        webSettings.supportMultipleWindows();
        webSettings.setJavaScriptEnabled(true);
        //webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAllowFileAccess(true);
        payalipay_web_webview.setWebChromeClient(new WebChromeClient());
        payalipay_web_webview.setWebViewClient(new PhoneCardWebViewClient());
        payalipay_web_webview.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");

        // 获取hash参数
        Map<String, Object> getParams = new HashMap<String, Object>();
        //   getParams.put("ts", TimeUtil.getCurrentTimeMil());
        getParams.put("productid", payGood.getPay_id() + "");// 订单ID
        getParams.put("subject", payGood.getPay_name());// 标题
        getParams.put("body", "androidwap-" + payGood.getPay_name());
        getParams.put("total_fee", payGood.getPay_money() + "");// 钱
        //getParams.put("total_fee",  "0.01");
        String subject = null;
        String body = null;
        try {
            subject = URLEncoder.encode(payGood.getPay_name(), "utf-8");// 标题
            body = URLEncoder.encode("androidwap-" + payGood.getPay_name(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = UrlEnc.appendUrl(UrlParam.reqAliWapPay.getFinalUrl(), getParams, null, true);
        synCookies(url);
        payalipay_web_webview.loadUrl(url);
        payalipay_web_webview.requestFocus();
    }

    private final class InJavaScriptLocalObj {
        @JavascriptInterface
        public void showSource(String html) {
            Message msg = mHandler.obtainMessage(1, html);
            mHandler.sendMessage(msg);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    String html = (String) msg.obj;

                    int start = html.indexOf("<pre");
                    int end = html.indexOf("</pre>");
                    if (start != -1 && end != -1) {
                        String string = html.substring(start, end + 6);

                        String regex = "<pre.*?>(.*?)</pre>";
                        Pattern p = Pattern.compile(regex);
                        Matcher m = p.matcher(string);
                        if (m.find()) {
                            string = m.group(1);
                            try {
                                JSONObject jsonObject = new JSONObject(string);
                                String result = jsonObject.optString("result");
                                if ("1".equals(result)) {
                                    // 更新信息
                                    getUpdateInfo();
                                    PToast.showShort(jsonObject.optString("content"));
                                } else {
                                    PToast.showShort(jsonObject.optString("content"));
                                    PayWebAct.this.finish();
                                }
                            } catch (Exception e) {
                                PToast.showShort("支付出错");
                                PayWebAct.this.finish();
                                e.printStackTrace();
                            }
                            return;
                        }
                    } else {
                        PToast.showShort("支付出错");
                        PayWebAct.this.finish();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    private void getUpdateInfo() {
        LoadingDialog.show(this, "更新中...");
        ModuleMgr.getCenterMgr().reqMyInfo(new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                LoadingDialog.closeLoadingDialog();
                JSONObject jsonObject = response.getResponseJson();
                if (!jsonObject.isNull("userDetail")) {
                    PToast.showShort("更新失败!");
                }
                PayWebAct.this.setResult(Constant.PAY_ALIPAY_WEB_ACT);
                PayWebAct.this.finish();
            }
        });
    }

    /**
     * 同步一下cookie
     */
    public void synCookies(String url) {
        String cookie = ModuleMgr.getLoginMgr().getCookie();
        if (cookie != null) {
            CookieSyncManager.createInstance(this);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.removeAllCookie();// 移除
            cookieManager.setCookie(url, "auth=" + cookie);// 指定要修改的cookies
            CookieSyncManager.getInstance().sync();
        }
    }

    private class PhoneCardWebViewClient extends WebViewClient {

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            PToast.showShort("加载失败");
            customFrameLayout.show(R.id.common_net_error);
        }

        String str_login = "https://wappaygw.alipay.com/cashier/wapcashier_login.htm";
        String str_cashier = "https://wapcashier.alipay.com/cashier/ex_gateway_cashier.htm";

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.e("URL", url);
            customFrameLayout.show(R.id.payalipay_web_webview);

            if (url.contains(str_login) && isB) {
                float f = (float) (0.155 * ModuleMgr.getAppMgr().getScreenHeight());
                if (f < 10) {
                    f = 100;
                }
                clickView(view, 300, f);
            }
            if (url.contains(str_cashier)) {
                isB = false;
            }
            //String callback = appCfg.FATE_IT_HTTP + "pay/aliWapCallBack";
            //String error = appCfg.FATE_IT_HTTP + "pay/aliWapError";

            String callback = "http://p.app.yuanfenba.net/pay/aliWapCallBack";
            String error = "http://p.app.yuanfenba.net/pay/aliWapError";
            if (url.contains(callback) || url.contains(error)) {
                view.loadUrl("javascript:window.local_obj.showSource('<head>'+" + "document.getElementsByTagName('html')[0].innerHTML+'</head>');");
            }
            super.onPageFinished(view, url);
        }
    }

    @Override
    public void onBackPressed() {
        if (payalipay_web_webview.canGoBack()) {
            payalipay_web_webview.goBack();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        if (payalipay_web_webview != null) {
            payalipay_web_webview.clearCache(true);
            payalipay_web_webview.clearHistory();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //通知刷个人资料  在
        MsgMgr.getInstance().sendMsg(MsgType.MT_Update_MyInfo, null);
    }
}
