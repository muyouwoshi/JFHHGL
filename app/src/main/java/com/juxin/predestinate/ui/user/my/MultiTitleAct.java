package com.juxin.predestinate.ui.user.my;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.my.MTitle;
import com.juxin.predestinate.bean.my.MultiTitle;
import com.juxin.predestinate.module.logic.baseui.WebPanel;
import com.juxin.predestinate.module.logic.config.Hosts;
import com.juxin.predestinate.ui.user.util.CenterConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * 创建日期：2017/10/30
 * 描述:多标签
 *
 * @author :lc
 */
public class MultiTitleAct extends BaseSmartTab implements BaseSmartTab.TabClickListener {
    private int rankType;

    private String url;
    private WebPanel webPanel;
    private LinearLayout webContainer;
    private MultiTitle multiTitle;
    private ArrayList<MTitle> mTitles;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        isCanBack(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f1_main_web_fragment);
        setBackView();
        url = getIntent().getStringExtra("url");
        multiTitle = getIntent().getParcelableExtra(CenterConstant.RANK_TYPE_TITLES);
        rankType = multiTitle.getType();
        mTitles = multiTitle.getMultiTitle();
        initView();
        loadUrl(0);
        initSmartTab();
    }

    private void initView() {
        webContainer = findViewById(R.id.web_container);
        webPanel = new WebPanel(this);
    }

    private void loadUrl(int position) {
        if ("income".equals(mTitles.get(position).getParams().getTopType())) {
            url = Hosts.H5_SHARE_TOPLIST + "?topType=income";
        } else if ("share".equals(mTitles.get(position).getParams().getTopType())) {
            url = Hosts.H5_SHARE_TOPLIST + "?topType=share";
        }
        webPanel.loadUrl(url);
        webContainer.removeAllViews();
        webContainer.addView(webPanel.getContentView());
    }

    /**
     * 初始化 smartTab
     */
    private void initSmartTab() {
        LinkedHashMap<String, View> itemMap = new LinkedHashMap<>(mTitles.size());
        for (MTitle mTitle : mTitles) {
            itemMap.put(mTitle.getTitle(), null);
        }
        setPagerItem(itemMap);
        setTabClickListener(this);
    }

    @Override
    public void tabClick(int position, boolean isTabClick) {
        changeTitles(position);
        loadUrl(position);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (webPanel != null) {
            webPanel.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (webPanel != null) {
            webPanel.onPause();
        }
    }

    @Override
    public void onDestroy() {
        if (webPanel != null) {
            webPanel.onDestroy();
        }
        super.onDestroy();
    }
}
