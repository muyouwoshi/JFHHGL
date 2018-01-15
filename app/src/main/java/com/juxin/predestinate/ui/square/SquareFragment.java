package com.juxin.predestinate.ui.square;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.juxin.library.controls.smarttablayout.PagerItem;
import com.juxin.library.controls.smarttablayout.SmartTabLayout;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseFragment;
import com.juxin.predestinate.module.logic.baseui.WebPanel;
import com.juxin.predestinate.module.logic.config.Hosts;
import com.juxin.predestinate.module.util.BaseUtil;
import com.juxin.predestinate.module.util.GamePlayer;
import com.juxin.predestinate.module.util.WebUtil;
import com.juxin.predestinate.ui.user.my.adapter.ViewGroupPagerAdapter;

import java.util.ArrayList;

/**
 * 创建日期：2017/8/25
 * 描述:广场fragment
 * 作者:lc
 */
@SuppressLint("ValidFragment")
public class SquareFragment extends BaseFragment {

    private String title, url, catchGirlUrl;
    private ArrayList<PagerItem> listViews;

    private WebPanel webPanel;
    private ViewPager viewPager;
    private SmartTabLayout stlTitles;
    private LinearLayout web_container;

    public SquareFragment() {

    }

    public SquareFragment(String title, String url) {
        this.title = title;
        this.url = url;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.f1_main_web_fragment);
        initView();
        initViewsList();//在initWebView()之上
        initWebView(null);
        initViewPager();
        initTitle();
        return getContentView();
    }

    private void initView() {
        View rank_title = LayoutInflater.from(getActivity()).inflate(R.layout.f1_main_rank_title, null);
        setTitleCenterContainer(rank_title);
        stlTitles = (SmartTabLayout) findViewById(R.id.smart_tab);
        web_container = (LinearLayout) findViewById(R.id.web_container);
    }

    private void initWebView(String urlParm) {
        urlParm = TextUtils.isEmpty(urlParm) ? (TextUtils.isEmpty(catchGirlUrl) ? url : catchGirlUrl) : urlParm;
        webPanel = new WebPanel(getActivity());
        webPanel.loadUrl(urlParm);
        web_container.removeAllViews();
        web_container.addView(webPanel.getContentView());
    }

    private void initViewsList() {
        listViews = new ArrayList<>();
        if (ModuleMgr.getCenterMgr().getMyInfo().isMan() && ModuleMgr.getCenterMgr().getMyInfo().isBeauty_switch()) { //男号并且服务器开关开启显示
            catchGirlUrl = WebUtil.jointUrl(Hosts.H5_GAME_CATCH_LOLITA);//追女神
            listViews.add(new PagerItem(getString(R.string.discover_man_title_catch), null));
        } else {
            url = WebUtil.jointUrl(url);//直播
        }
        listViews.add(new PagerItem(getString(R.string.discover_man_title_live), null));
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

    private void tabEvent(int position) {
        if (listViews == null || listViews.isEmpty()) return;
        String title = listViews.get(position).getTitle();
        switch (title) {
            case "追女神":
                initWebView(catchGirlUrl);
                break;
            case "直播":
                GamePlayer.getInstance().stop();
                initWebView(url);
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
            if (viewPager != null && viewPager.getAdapter().getCount() > 1 && viewPager.getCurrentItem() == 0) {
                GamePlayer.getInstance().setPlayFlag(true);
                GamePlayer.getInstance().playRawSound(BaseUtil.getResId("background"), true);
            }
        } else {
            GamePlayer.getInstance().stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.isVisible()) {
            GamePlayer.getInstance().setPlayFlag(true);
        } else {
            GamePlayer.getInstance().setPlayFlag(false);
        }
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
        GamePlayer.getInstance().stop();
        GamePlayer.getInstance().setPlayFlag(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webPanel != null) webPanel.onDestroy();
    }

    private SmartTabLayout.OnTabClickListener onTabClickListener = new SmartTabLayout.OnTabClickListener() {
        @Override
        public void onTabClicked(int position) {
            tabEvent(position);
        }
    };
}

