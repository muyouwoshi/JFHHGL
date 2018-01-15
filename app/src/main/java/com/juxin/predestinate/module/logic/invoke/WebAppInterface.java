package com.juxin.predestinate.module.logic.invoke;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import com.juxin.library.log.PLogger;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * js交互interface类
 */
public class WebAppInterface {

    private final WeakReference<Context> actRef;
    private Object webView;

    public WebAppInterface(Context context, Object webView) {
        actRef = new WeakReference<>(context);
        this.webView = webView;
    }

    /**
     * @return 获取弱引用窗体
     */
    public Activity getAct() {
        Context context = actRef.get();
        if (context != null && context instanceof Activity) {
            return (Activity) context;
        }
        return null;
    }

    /**
     * @return 获取webView实例
     */
    public Object getWebView() {
        return webView;
    }

    //--------------------CMD处理start--------------------
    @JavascriptInterface
    public String command(String cmd) {
        if (TextUtils.isEmpty(cmd)) return "error";

        PLogger.d(cmd);
        try {
            JSONObject jsonObject = new JSONObject(cmd);
            String cmdType = jsonObject.optString("cmd");
            String cmdData = jsonObject.optString("data");

            Invoker.getInstance().doInApp(this, cmdType, cmdData);
        } catch (Exception e) {
            PLogger.printThrowable(e);
        }
        return "ok";
    }
    //--------------------CMD处理end--------------------
}