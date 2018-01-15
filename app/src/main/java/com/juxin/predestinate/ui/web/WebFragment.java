package com.juxin.predestinate.ui.web;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.baseui.BaseFragment;
import com.juxin.predestinate.module.logic.baseui.WebPanel;
import com.juxin.predestinate.module.util.WebUtil;

/**
 * 网页容器fragment
 * Created by ZRP on 2017/4/21.
 */
@SuppressLint("ValidFragment")
public class WebFragment extends BaseFragment {

    private String title, url;
    private WebPanel webPanel;
    private boolean isHideTitle;
    private int rightImgResId;
    private View.OnClickListener rightImgClickListener;

    public WebFragment() {
    }

    public WebFragment(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public WebFragment(boolean isHideTitle, String url) {
        this.isHideTitle = isHideTitle;
        this.url = url;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.f1_main_web_fragment);
        if (isHideTitle) hideTitle();
        if (!TextUtils.isEmpty(title)) setTitle(title);
        initView();
        if (rightImgResId != 0) setTitleRightImg(rightImgResId, rightImgClickListener);
        return getContentView();
    }

    public void initTitleRightImg(int resId, View.OnClickListener listener) {
        this.rightImgResId = resId;
        this.rightImgClickListener = listener;
    }

    private void initView() {
        LinearLayout web_container = (LinearLayout) findViewById(R.id.web_container);

        webPanel = new WebPanel(getActivity());
        webPanel.loadUrl(WebUtil.jointUrl(url));
        web_container.removeAllViews();
        web_container.addView(webPanel.getContentView());
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (webPanel != null) webPanel.resetWebView(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (webPanel != null) {
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
    public void onDestroy() {
        super.onDestroy();
        if (webPanel != null) webPanel.onDestroy();
    }
}
