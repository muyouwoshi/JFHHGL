package com.juxin.predestinate.ui.user.my;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.LinearLayout;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.baseui.WebPanel;

/**
 * 创建日期：2017/7/28
 * 描述:密友说明页
 * 作者:lc
 */
public class IntimateFriendExplainAct extends BaseActivity {

    private WebPanel webPanel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        isCanBack(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_web_activity);
        String url = getIntent().getStringExtra("url");

        setBackView(getString(R.string.intimate_friend_explain));

        LinearLayout web_container = (LinearLayout) findViewById(R.id.web_container);
        webPanel = new WebPanel(this);
        webPanel.loadUrl(url);
        webPanel.setWebListener(new WebPanel.WebListener() {
            @Override
            public void onTitle(String title) {
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
            webPanel.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webPanel != null) {
            webPanel.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        if (webPanel != null) webPanel.onDestroy();
        super.onDestroy();
    }
}
