package com.juxin.predestinate.ui.user.my;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.LinearLayout;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.baseui.WebPanel;
import com.juxin.predestinate.module.logic.config.Hosts;
import com.juxin.predestinate.module.util.GamePlayer;
import com.juxin.predestinate.module.util.WebUtil;

/**
 * 创建日期：2017/9/6
 * 描述:追女神详情页
 * 作者:lc
 */
public class CatchGoddessInfoAct extends BaseActivity {
    private WebPanel webPanel;
    private LinearLayout web_container;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        isCanBack(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_web_activity);
        web_container = (LinearLayout) findViewById(R.id.web_container);
        initTitle();
        initWebview();
    }

    private void initTitle() {
        setBackView("追女神");
    }

    private void initWebview() {
        webPanel = new WebPanel(this);
        webPanel.loadUrl(WebUtil.jointUrl(Hosts.H5_GAME_CATCH_LOLITA));
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
        GamePlayer.getInstance().setPlayFlag(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webPanel != null) {
            webPanel.onPause();
        }
        GamePlayer.getInstance().stop();
    }

    @Override
    protected void onDestroy() {
        if (webPanel != null) webPanel.onDestroy();
        super.onDestroy();
    }
}
