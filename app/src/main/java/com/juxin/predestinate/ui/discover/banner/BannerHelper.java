package com.juxin.predestinate.ui.discover.banner;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.discover.BannerConfig;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.util.UIUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建日期：2017/9/10
 * 描述:
 * 作者:lc
 */
public class BannerHelper {

    private Context context;
    private View viewTop;
    private LinearLayout ll_dot;
    private FrameLayout fl_banner;
    private BannerAdapter bannerAdapter;
    private AutoScrollBanner custom_banner;

    private List<BannerConfig.Banner> data;

    public BannerHelper(Context context, View viewTop, List<BannerConfig.Banner> data) {
        this.context = context;
        this.viewTop = viewTop;
        this.data = data;
        initView();
    }

    public void updData(List<BannerConfig.Banner> list) {
        if (data != null && list.size() > 0) {
            data.clear();
            data.addAll(list);
            if (!data.isEmpty()) {
                isShow(true);
                initDotImg();
                bannerAdapter.notifyDataSetChanged();
            }
        }else {
            isShow(false);
        }
    }

    private void initView() {
        custom_banner = (AutoScrollBanner) viewTop.findViewById(R.id.custom_banner);
        fl_banner = (FrameLayout) viewTop.findViewById(R.id.fl_banner);
        ll_dot = (LinearLayout) viewTop.findViewById(R.id.ll_dot);
    }

    public void showBanner() {
        initBanner();
        initDotImg();
    }

    /**
     * 有数据时显示banner
     * @param isShow true显示，false不显示
     */
    public void isShow(boolean isShow) {
        if (fl_banner != null) {
            if (isShow) {
                fl_banner.setVisibility(View.VISIBLE);
            } else {
                fl_banner.setVisibility(View.GONE);
            }
        }
    }

    private void initBanner() {
        data = new ArrayList<>();
        bannerAdapter = new BannerAdapter(context, data);
        custom_banner.setAdapter(bannerAdapter);
        custom_banner.setSpeed(5000);
        custom_banner.setSmoothScroll(true);
        custom_banner.setMethod(AutoScrollBanner.METHOD_RIGHT);
        custom_banner.addOnPageChangeListener(bannerListener);
        custom_banner.startScroll();
    }

    /**
     * 初始化白点
     */
    private void initDotImg() {
        ll_dot.removeAllViews();
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(UIUtil.dip2px(context, 8), UIUtil.dip2px(context, 8));
        layout.setMargins(0, 0, 10, 0);
        if (data.size() < 1) {
            return;
        }
        for (int i = 0; i < data.size(); i++) {
            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(layout);
            if (i == 0) {
                imageView.setBackgroundResource(R.drawable.f2_dot_sel);
            } else {
                imageView.setBackgroundResource(R.drawable.f2_dot_nor);
            }
            ll_dot.addView(imageView);
        }
    }

    private void changeDot(int position) {
        if (data.size() < 2 || ll_dot == null) {
            return;
        }
        for (int i = 0; i < data.size(); i++) {
            ImageView imageView = (ImageView) ll_dot.getChildAt(i);
            if (imageView == null) {
                return;
            }
            if (i == position % data.size()) {
                imageView.setBackgroundResource(R.drawable.f2_dot_sel);
            } else {
                imageView.setBackgroundResource(R.drawable.f2_dot_nor);
            }
        }
    }

    private ViewPager.OnPageChangeListener bannerListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            changeDot(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
