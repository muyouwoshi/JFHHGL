package com.juxin.predestinate.ui.web;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.juxin.library.controls.smarttablayout.PagerItem;
import com.juxin.library.controls.smarttablayout.SmartTabLayout;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.baseui.BaseFragment;
import com.juxin.predestinate.module.logic.baseui.WebPanel;
import com.juxin.predestinate.module.logic.config.Hosts;
import com.juxin.predestinate.module.logic.invoke.Invoker;
import com.juxin.predestinate.module.util.WebUtil;
import com.juxin.predestinate.ui.user.my.adapter.ViewGroupPagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 风云榜fragment
 * Created by ZRP on 2017/4/30.
 */
public class RankFragment extends BaseFragment implements View.OnClickListener {

    //jcmd预定义的值
    private static final int TYPE_THIS_WEEK = 0;
    private static final int TYPE_LAST_WEEK = 1;
    private static final String TYPE_KEY = "index";

    private WebPanel webPanel;
    private ViewPager viewPager;
    private SmartTabLayout stlTitles;

    private ArrayList<PagerItem> listViews;
    private Map<String, Object> typeMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.f1_main_web_fragment);
        initView();
        initViewsList();
        initViewPager();
        initTitle();
        return getContentView();
    }

    private void initView() {
        View rank_title = LayoutInflater.from(getActivity()).inflate(R.layout.f1_main_rank_title, null);
        setTitleCenterContainer(rank_title);
        stlTitles = (SmartTabLayout) findViewById(R.id.smart_tab);

        LinearLayout web_container = (LinearLayout) findViewById(R.id.web_container);
        webPanel = new WebPanel(getActivity());
        webPanel.loadUrl(WebUtil.jointUrl(Hosts.H5_DUKE_RANKING));
        web_container.removeAllViews();
        web_container.addView(webPanel.getContentView());
    }

    private void initViewsList() {
        listViews = new ArrayList<>();
        listViews.add(new PagerItem(getString(R.string.rank_this_week), null));
        listViews.add(new PagerItem(getString(R.string.rank_last_week), null));
    }

    private void initViewPager() {
        viewPager = new ViewPager(getContext());
        viewPager.setAdapter(new ViewGroupPagerAdapter(listViews));
    }

    private void initTitle() {
        ((LinearLayout) stlTitles.getTabStrip()).setGravity(Gravity.CENTER_HORIZONTAL); //标题居中
        stlTitles.setCustomTabView(R.layout.f2_custom_table, R.id.tv_left_tab);         //设置自定义标题
        stlTitles.setViewPager(viewPager);
        stlTitles.setOnTabClickListener(onTabClickListener);
    }

    @Override
    public void onClick(View v) {
    }

    private void tabEvent(int position) {
        switch (position) {
            case TYPE_THIS_WEEK: //通过jcmd打开本周排行榜
                Statistics.userBehavior(SendPoint.menu_fengyunbang_bz);
                onChecked(TYPE_THIS_WEEK);
                break;
            case TYPE_LAST_WEEK: //通过jcmd打开上周排行榜
                Statistics.userBehavior(SendPoint.menu_fengyunbang_sz);
                onChecked(TYPE_LAST_WEEK);
                break;
            default:
                break;
        }
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
        if (webPanel != null) webPanel.onDestroy();
        super.onDestroy();
    }

    /**
     * 设置选中
     *
     * @param type 类型 0 TYPE_THIS_WEEK , 1 TYPE_LAST_WEEK
     */
    private void onChecked(int type) {
        changeTitles(type);
        typeMap.put(TYPE_KEY, type);
        Invoker.getInstance().doInJS(Invoker.JSCMD_ranking_btn_click, typeMap);
    }

    /**
     * 如果来自cmd 不用执行onChecked()方法里的doInJS
     *
     * @param type 类型 0 TYPE_THIS_WEEK , 1 TYPE_LAST_WEEK
     */
    public void changeTitles(int type) {
        viewPager.setCurrentItem(type);
    }

    private SmartTabLayout.OnTabClickListener onTabClickListener = new SmartTabLayout.OnTabClickListener() {
        @Override
        public void onTabClicked(int position) {
            tabEvent(position);
        }
    };
}
