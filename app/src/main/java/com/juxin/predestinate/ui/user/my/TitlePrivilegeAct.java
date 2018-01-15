package com.juxin.predestinate.ui.user.my;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.juxin.library.view.CustomFrameLayout;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.baseui.WebPanel;
import com.juxin.predestinate.module.util.UIUtil;

/**
 * 创建日期：2017/7/28
 * 描述:爵位特权
 * 作者:lc
 */
public class TitlePrivilegeAct extends BaseActivity {

    private WebPanel webPanel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        isCanBack(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f2_title_privilege_act);
        String url = getIntent().getStringExtra("url");

        setBackView();
        setTitle(getString(R.string.title_privilege), ContextCompat.getColor(this, R.color.white));
        setTitleBackground(R.color.transparent);
        setIsBottomLineShow(false);

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
                if (webPanel.isLoadError()) {//加载失败页
                    setBackView();
                    setTitle(getString(R.string.title_privilege), ContextCompat.getColor(TitlePrivilegeAct.this, R.color.color_title_new));
                    setTitleBackground(R.color.white);
                    CustomFrameLayout customFrameLayout = webPanel.getCustomFrameLayout();
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.topMargin = UIUtil.dip2px(TitlePrivilegeAct.this, 100);
                    customFrameLayout.setLayoutParams(layoutParams);
                } else {
                    setTitleBackground(R.color.transparent);
                }
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
