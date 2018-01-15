package com.juxin.predestinate.ui.pay.wepayother.h5;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.invoke.Invoker;
import com.juxin.predestinate.ui.pay.PayConst;

/**
 * 微信h5支付
 * Created by @author ZRP on 2016/12/9
 * Fix by IQQ on 2017/05/27.
 */
public class PayWebActivity extends BaseActivity {

    private WebPanelPayH5 webPanel;
    public static int payResutl = 9999;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        isCanBack(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_web_activity);
        View base_title = findViewById(R.id.base_title);
        base_title.setVisibility(View.VISIBLE);
        setBackView();
        String url = getIntent().getStringExtra("url");
        setTitle(getString(R.string.goods_pay_weixin1));
        LinearLayout web_container = (LinearLayout) findViewById(R.id.web_container);
        webPanel = new WebPanelPayH5(this, url, true, new H5PayStart() {
            @Override
            public void OnPayStart() {
                finish();
            }
        });
        webPanel.setWebListener(new WebPanelPayH5.WebListener() {
            @Override
            public void onTitle(String title) {
                //一直显示支付中...
                setTitle(getString(R.string.paying));
            }

            @Override
            public void onLoadFinish(WebPanelPayH5.WebLoadStatus loadStatus) {
            }
        });
        web_container.addView(webPanel.getContentView());
        PayConst.payFlag = true;
    }

    @Override
    public void onBackPressed() {
        if (webPanel != null && webPanel.getWebView() != null && webPanel.getWebView().canGoBack()) {
            webPanel.getWebView().goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (webPanel != null) {
            webPanel.getWebView().onResume();
            webPanel.getWebView().resumeTimers();
        }

        Invoker.getInstance().setWebView(webPanel == null ? null : webPanel.getWebView());
        //通知刷个人资料  在
        MsgMgr.getInstance().sendMsg(MsgType.MT_Update_MyInfo, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webPanel != null) {
            webPanel.getWebView().onPause();
            webPanel.getWebView().pauseTimers();
        }
        Invoker.getInstance().setWebView(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webPanel != null) {
            webPanel.onDestroy();
        }
    }

    /**
     * 关闭loading，显示webView
     */
    public void hideLoading() {
        if (webPanel != null) {
            webPanel.hideLoading();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != RESULT_OK) {
            return;
        }
        if (requestCode == PayWebActivity.payResutl) {
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
