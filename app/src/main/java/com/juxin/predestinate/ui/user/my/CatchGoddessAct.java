package com.juxin.predestinate.ui.user.my;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.baseui.WebPanel;
import com.juxin.predestinate.module.logic.invoke.Invoker;
import com.juxin.predestinate.module.logic.webserver.WebService;
import com.juxin.predestinate.module.logic.webserver.WebServiceConfig;
import com.juxin.predestinate.module.util.WebUtil;

import java.util.HashMap;

/**
 * 创建日期：2017/8/18
 * 描述:追女神相关H5
 * 作者:lc
 */
public class CatchGoddessAct extends BaseActivity {

    private int openCount = 0;
    private String otherUid, url;
    private WebPanel webPanel;
    private LinearLayout web_container;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        isCanBack(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_web_activity);
        otherUid = getIntent().getStringExtra("otherUid");
        WebService.getInstance().StartWebServiceForSDCard();
        url = WebUtil.jointUrl(WebServiceConfig.catchGirl.getUrl(), new HashMap<String, Object>() {{
            put("target_id", otherUid);
        }});
        web_container = (LinearLayout) findViewById(R.id.web_container);
        initTitle();
        initWebview();
    }

    private void initTitle() {
        setBackView();
        findViewById(R.id.base_title_title).setVisibility(View.GONE);
        findViewById(R.id.base_title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Invoker.getInstance().doInJS(Invoker.JSCMD_close_game, null);
                CatchGoddessAct.this.finish();
            }
        });
    }

    private void initWebview() {
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
        ++openCount;
        if (webPanel != null) {
            if (openCount == 1) {
                webPanel.onResume();
            } else {
                webPanel.resetWebView(true);
            }
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
        WebService.getInstance().stop();
        super.onDestroy();
    }
}
