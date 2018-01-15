package com.juxin.predestinate.ui.live.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.juxin.library.controls.smarttablayout.PagerItem;
import com.juxin.library.controls.smarttablayout.SmartTabLayout;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseFragment;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.module.util.UIUtil;
import com.juxin.predestinate.ui.live.view.LiveListPanel;
import com.juxin.predestinate.ui.user.my.adapter.ViewGroupPagerAdapter;
import com.juxin.predestinate.ui.user.util.OffNetPanel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 直播tab页面
 *
 * @author gwz
 */
public class LiveMainFragment extends BaseFragment {

    private List<PagerItem> pagerItems = new ArrayList<>();
    private ViewPager viewPager;
    private SmartTabLayout stlTitles;
    private ArrayList<LiveListPanel> panels = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.fragment_live_main);
        setTopView();
        initView();
        getLiveTags();
        return getContentView();
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private void setTopView() {
        setTitleLeftImg(R.drawable.ic_live_search, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIShow.showSearchLive(getActivity());
            }
        });
    }

    private void initView() {
        View disMainTitle = LayoutInflater.from(getContext()).inflate(R.layout.f1_main_rank_title, null);
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llp.leftMargin = UIUtil.dip2px(getContext(), 40);
        llp.rightMargin = UIUtil.dip2px(getContext(), 40);
        disMainTitle.setLayoutParams(llp);
        setTitleCenterContainer(disMainTitle);

        stlTitles = (SmartTabLayout) findViewById(R.id.smart_tab);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        RelativeLayout netStatusLayout = (RelativeLayout) findViewById(R.id.netStatusLayout);
        OffNetPanel offNetPanel = new OffNetPanel(getActivity(), netStatusLayout);
        netStatusLayout.addView(offNetPanel);

    }

    private void initTitle() {
        ((LinearLayout) stlTitles.getTabStrip()).setGravity(Gravity.CENTER_HORIZONTAL);
        stlTitles.setCustomTabView(R.layout.f2_custom_table, R.id.tv_left_tab);
        stlTitles.setViewPager(viewPager);
        stlTitles.setOnTabClickListener(onTabClickListener);
        // 页面初始化时默认选中第二个热门条目[2017/11/9:李东宇]
        if (pagerItems.size() >= 2) {
            viewPager.setCurrentItem(1);
        }
    }

    private void getLiveTags() {
        ModuleMgr.getHttpMgr().reqGetAndCacheHttp(UrlParam.getLiveTagList, null, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (response.isOk()) {
                    parseTags(response.getResponseString());
                    return;
                }
            }
        });
    }

    private void parseTags(String strJson) {
        try {
            JSONObject resJo = new JSONObject(strJson).getJSONObject("res");
            JSONArray tagJa = resJo.getJSONArray("list");
            pagerItems.clear();
            panels.clear();
            for (int i = 0; i < tagJa.length(); i++) {
                JSONObject jo = tagJa.getJSONObject(i);
                int tagId = jo.getInt("id");
                PagerItem pagerItem = new PagerItem();
                pagerItem.setId(tagId);
                pagerItem.setTitle(jo.getString("name"));
                LiveListPanel liveListPanel = new LiveListPanel(getContext(), pagerItem);
                pagerItem.setView(liveListPanel.getContentView());
                panels.add(liveListPanel);
                pagerItems.add(pagerItem);
            }
            viewPager.setAdapter(new ViewGroupPagerAdapter(pagerItems));
            viewPager.addOnPageChangeListener(tabListener);
            initTitle();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void tabEvent(int position, boolean flag) {
        //点击分页标签刷新数据
        if (flag) {
            panels.get(position).onRefresh();
        }
    }

    public void refresh() {
        for (LiveListPanel panel : panels) {
            panel.onRefresh();
        }
    }

    private SmartTabLayout.OnTabClickListener onTabClickListener = new SmartTabLayout.OnTabClickListener() {
        @Override
        public void onTabClicked(int position) {
            tabEvent(position, true);
        }
    };

    private ViewPager.OnPageChangeListener tabListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            tabEvent(position, false);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
