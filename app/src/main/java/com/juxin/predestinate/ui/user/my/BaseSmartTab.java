package com.juxin.predestinate.ui.user.my;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.juxin.library.controls.smarttablayout.PagerItem;
import com.juxin.library.controls.smarttablayout.SmartTabLayout;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.ui.user.my.adapter.ViewGroupPagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * 创建日期：2017/10/26
 * 描述:SmartTab 基类
 *
 * @author :lc
 */
public class BaseSmartTab extends BaseActivity {
    private ViewPager viewPager;
    private SmartTabLayout stlTitles;
    private TabClickListener tabClickListener;

    private ArrayList<PagerItem> pagerItems = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initTitles();
    }

    private void initTitles() {
        View title = LayoutInflater.from(this).inflate(R.layout.f1_main_rank_title, null);
        setTitleCenterContainer(title);
        stlTitles = (SmartTabLayout) title.findViewById(R.id.smart_tab);
    }

    /**
     * 设置ViewPager
     * @param viewPager 非默认viewPager
     */
    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
    }

    public void setPagerItem(LinkedHashMap<String, View> linkedHashMap) {
        if (linkedHashMap == null || linkedHashMap.isEmpty()) {
            return;
        }
        for (String item : linkedHashMap.keySet()) {
            pagerItems.add(new PagerItem(item, linkedHashMap.get(item)));
        }
        if (viewPager == null) {
            viewPager = new ViewPager(this);
        }
        viewPager.setAdapter(new ViewGroupPagerAdapter(pagerItems));
        //标题居中
        ((LinearLayout) stlTitles.getTabStrip()).setGravity(Gravity.CENTER_HORIZONTAL);
        //设置自定义标题
        stlTitles.setCustomTabView(R.layout.f2_custom_table, R.id.tv_left_tab);
        stlTitles.setViewPager(viewPager);
        stlTitles.setOnTabClickListener(onTabClickListener);
        stlTitles.setOnPageChangeListener(onPageChangeListener);
    }

    public void setTabClickListener(TabClickListener listener) {
        this.tabClickListener = listener;
    }

    /**
     * 如果来自cmd 不用执行onChecked()方法里的doInJS
     *
     * @param type 类型
     */
    public void changeTitles(int type) {
        if (viewPager != null) {
            viewPager.setCurrentItem(type);
        }
    }

    private SmartTabLayout.OnTabClickListener onTabClickListener = new SmartTabLayout.OnTabClickListener() {
        @Override
        public void onTabClicked(int position) {
            if (tabClickListener != null) {
                tabClickListener.tabClick(position, true);
            }
        }
    };

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (tabClickListener != null) {
                tabClickListener.tabClick(position, false);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    interface TabClickListener {
        /**
         * 被点击的Tab
         *
         * @param position       位置
         * @param isTabClick     是否来自点击事件
         */
        void tabClick(int position, boolean isTabClick);
    }
}
