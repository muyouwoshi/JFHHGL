package com.juxin.predestinate.ui.discover;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.juxin.library.controls.smarttablayout.PagerItem;
import com.juxin.library.controls.smarttablayout.SmartTabLayout;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.local.statistics.StatisticsDiscovery;
import com.juxin.predestinate.module.local.statistics.StatisticsShareUnlock;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseFragment;
import com.juxin.predestinate.module.logic.baseui.WebPanel;
import com.juxin.predestinate.module.logic.config.Hosts;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.module.util.WebUtil;
import com.juxin.predestinate.module.util.my.GiftHelper;
import com.juxin.predestinate.ui.user.my.adapter.ViewGroupPagerAdapter;
import com.juxin.predestinate.ui.user.util.CenterConstant;
import com.juxin.predestinate.ui.user.util.OffNetPanel;
import com.juxin.predestinate.ui.utils.NoDoubleClickListener;
import java.util.ArrayList;
import java.util.List;

/**
 * 创建日期：2017/6/6
 * 描述:
 * 作者:lc
 */
public class DiscoverMFragment extends BaseFragment {

    private ChatFragment chatFragment;
    private DiscoverFragment discoverFragment;
    private WebPanel webPanel;

    private LinearLayout discover_red_card_lin;
    private ImageView discover_red_card;
    private ViewPager viewPager;
    private SmartTabLayout stlTitles;

    private boolean isHidden;
    private List<PagerItem> listViews;

    private Class childClass = ChatFragment.class;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.discover_m_fragment);
        setTopView();
        initView();
        return getContentView();
    }

    private void setTopView() {
        setTitleLeftImg(R.drawable.f2_ic_rank, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Statistics.userBehavior(SendPoint.menu_faxian_rank);
                UIShow.showRankAct(getContext());
            }
        });
        setTitleRightImg(R.drawable.f1_discover_select_ico, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Statistics.userBehavior(SendPoint.menu_faxian_filter);
                discoverFragment.showDiscoverSelectDialog();
            }
        });
    }

    private void initView() {
        View disMainTitle = LayoutInflater.from(getContext()).inflate(R.layout.f1_main_rank_title, null);
        setTitleCenterContainer(disMainTitle);

        stlTitles = (SmartTabLayout) findViewById(R.id.smart_tab);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        discover_red_card_lin = (LinearLayout) findViewById(R.id.discover_red_card_lin);
        discover_red_card = (ImageView) findViewById(R.id.discover_red_card);
        findViewById(R.id.discover_red_card_close).setOnClickListener(clickListener);
        discover_red_card.setOnClickListener(clickListener);

        RelativeLayout netStatusLayout = (RelativeLayout) findViewById(R.id.netStatusLayout);
        OffNetPanel offNetPanel = new OffNetPanel(getActivity(), netStatusLayout);
        netStatusLayout.addView(offNetPanel);

        initFragment();
        initViewsList();
        initTitle();

        if (ModuleMgr.getCommonMgr().getTodayChatShow()) {//true显示
            discover_red_card_lin.setVisibility(View.VISIBLE);
        }
    }

    private void initFragment() {
        chatFragment = new ChatFragment(getContext());
        discoverFragment = new DiscoverFragment(getContext());
        webPanel = new WebPanel(getActivity());
    }

    private void initViewsList() {
        listViews = new ArrayList<>();
        if (ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
            listViews.add(new PagerItem(getString(R.string.discover_man_title_chat), chatFragment.getContentView()));
            changeChecked(false);
        }
        listViews.add(new PagerItem(getString(R.string.discover_man_title_hot), discoverFragment.getContentView()));
        if (ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
            listViews.add(new PagerItem(getString(R.string.discover_man_title_catch), webPanel.getContentView()));
            changeChecked(false);
        }
        viewPager.setAdapter(new ViewGroupPagerAdapter(listViews));
        viewPager.addOnPageChangeListener(tabListener);
    }

    private void initTitle() {
        if (ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
            ((LinearLayout) stlTitles.getTabStrip()).setGravity(Gravity.CENTER_HORIZONTAL); //标题居中
            stlTitles.setCustomTabView(R.layout.f2_custom_table, R.id.tv_left_tab);         //设置自定义标题
            stlTitles.setViewPager(viewPager);                                              //设置viewpager
            stlTitles.setOnTabClickListener(onTabClickListener);
        } else {
            setTitle(getString(R.string.discover_man_title_hot));
        }
    }

    public void onRefresh() {
        if (ModuleMgr.getCenterMgr().getMyInfo().isMan() && chatFragment != null) {
            chatFragment.refreshList();
        }
    }

    /**
     * 位置变化
     *
     * @param position 位置
     * @param flag     是否来自点击事件
     */
    private void tabEvent(int position, boolean flag) {
        if (listViews == null || position >= listViews.size()) return;
        String title = listViews.get(position).getTitle();
        switch (title) {
            case "语聊":
                changeChecked(false);
                if (flag) { //点击刷新
                    chatFragment.refreshList();
                }
                childClass = ChatFragment.class;
                break;
            case "热门":
                changeChecked(true);
                if (flag) { //点击刷新
                    discoverFragment.refreshList();
                }
                Statistics.userBehavior(SendPoint.menu_faxian_yuliao);
                StatisticsDiscovery.onClickRecommend();
                childClass = DiscoverFragment.class;
                break;
            case "追女神":
                Statistics.userBehavior(SendPoint.menu_faxian_zhuinvshen);
                changeChecked(false);
                webPanel.loadUrl(WebUtil.jointUrl(Hosts.H5_GAME_CATCH_LOLITA));
            default:
                break;
        }
    }

    /**
     * 右侧筛选按钮是否可见
     *
     * @param flag true 可见，false Gone
     */
    private void changeChecked(boolean flag) {
        if (flag) {
            setTitleRightImgVisible();
        } else {
            setTitleRightImgGone();
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

    @Override
    public void onResume() {
        super.onResume();
        if (!isHidden) {
            GiftHelper.setBigDialogIsCanShow(true);
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
    }

    @Override
    public void onStop() {
        if (!isHidden) {
            GiftHelper.setBigDialogIsCanShow(false);
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != discoverFragment) discoverFragment.detach();
        if (null != chatFragment) chatFragment.detach();
        if (webPanel != null) webPanel.onDestroy();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        isHidden = hidden;
        if (!hidden) {
            // 当前fragment可见，且展示捕女神panel时，重设webView引用
            if (viewPager != null && viewPager.getCurrentItem() == 2) {
                if (webPanel != null) webPanel.resetWebView(true);
            }
            GiftHelper.setBigDialogIsCanShow(true);
        } else {
            GiftHelper.setBigDialogIsCanShow(false);
        }
    }

    public Class getChildClass() {
        return childClass;
    }

    private NoDoubleClickListener clickListener = new NoDoubleClickListener() {

        @Override
        public void onNoDoubleClick(View view) {
            switch (view.getId()) {
                case R.id.discover_red_card:
                    UIShow.showCardRedBagDlg(getActivity(), CenterConstant.TYPE_INIT, 0);
                    StatisticsShareUnlock.onFloat_button_redpacketcard_click();
                    break;
                case R.id.discover_red_card_close:
                    ModuleMgr.getCommonMgr().setShowredCard();
                    setRedCardGone();
                    StatisticsShareUnlock.onFloat_button_redpacketcard_close();
                    break;
                default:
                    break;
            }
        }
    };

    public void setRedCardGone() {
        if (discover_red_card_lin != null) {
            discover_red_card_lin.setVisibility(View.GONE);
        }
    }
}