package com.juxin.predestinate.ui.user.my;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.baseui.WebPanel;
import com.juxin.predestinate.module.logic.invoke.Invoker;

/**
 * 大转盘设置页面
 * Created by ZRP on 2016/12/9.
 */
public class RotarySetActivity extends BaseActivity {

    private WebPanel webPanel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        isCanBack(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_web_activity);
        String url = getIntent().getStringExtra("url");

        setBackView();
        setTitleRight(getString(R.string.save), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击发送jcmd，保存用户大转盘设置
                Invoker.getInstance().doInJS(Invoker.JSCMD_header_right_btn_click, null);
            }
        });

        LinearLayout web_container = (LinearLayout) findViewById(R.id.web_container);
        webPanel = new WebPanel(this);
        webPanel.loadUrl(url);
        webPanel.setWebListener(new WebPanel.WebListener() {
            @Override
            public void onTitle(String title) {
                setTitle(title);
            }

            @Override
            public void onLoadFinish(WebPanel.WebLoadStatus loadStatus) {
            }
        });
        web_container.addView(webPanel.getContentView());
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
            webPanel.resetWebView(false);
            webPanel.getWebView().onResume();
            webPanel.getWebView().resumeTimers();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (webPanel != null) {
            webPanel.getWebView().onPause();
            webPanel.getWebView().pauseTimers();
        }
    }

    @Override
    protected void onDestroy() {
        if (webPanel != null) webPanel.onDestroy();
        super.onDestroy();
    }

    /**
     * 关闭loading，显示webView
     */
    public void hideLoading() {
        if (webPanel != null) webPanel.hideLoading();
    }
}
