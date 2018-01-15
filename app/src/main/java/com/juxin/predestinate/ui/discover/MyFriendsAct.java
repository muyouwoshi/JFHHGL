package com.juxin.predestinate.ui.discover;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.juxin.library.controls.smarttablayout.PagerItem;
import com.juxin.library.controls.smarttablayout.SmartTabLayout;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweightList;
import com.juxin.predestinate.module.local.statistics.StatisticsMessage;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.baseui.custom.CustomStatusListView;
import com.juxin.predestinate.module.logic.baseui.xlistview.ExListView;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.user.my.AttentionMePanel;
import com.juxin.predestinate.ui.user.my.MyAttentionPanel;
import com.juxin.predestinate.ui.user.my.adapter.ViewGroupPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的好友
 * Created by zhang on 2017/5/4.
 */

public class MyFriendsAct extends BaseActivity  {

    private SmartTabLayout stlTitles;
    private ViewPager vpViewChange;
    private List<PagerItem> listViews;//pagerItem集合
    private List<BasePanel> panls = new ArrayList<>(); // Tab页面列表

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_friends_act);
        initTitle();
        initView();
    }

    private void initView() {

        stlTitles = (SmartTabLayout) findViewById(R.id.my_attention_stl_titles);
        vpViewChange = (ViewPager) findViewById(R.id.my_attention_vPager);
        initViewsList();
        initViewPager();
        ((LinearLayout) stlTitles.getTabStrip()).setGravity(Gravity.CENTER_HORIZONTAL);//标题居中
        stlTitles.setCustomTabView(R.layout.f1_custom_table_view, R.id.tv_left_tab);//设置自定义标题
        stlTitles.setViewPager(vpViewChange);//设置viewpager
        stlTitles.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initTitle() {
        setBackView(R.id.base_title_back);
        setTitle(getResources().getString(R.string.my_friend_title));
        setTitleRight(getResources().getString(R.string.my_friend_righ_title), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StatisticsMessage.blackUser();
                UIShow.showMyDefriends(MyFriendsAct.this);
            }
        });
    }

    //添加两个panel
    private void initViewsList() {

        listViews = new ArrayList<>();
        if(( ModuleMgr.getCenterMgr().getMyInfo().isMan() && !ModuleMgr.getCenterMgr().getMyInfo().isB() ) || !ModuleMgr.getCenterMgr().getMyInfo().isMan()){
            panls.add(new MyFriendsPanel(this,MyFriendsPanel.GiftFriends));
            panls.add(new MyFriendsPanel(this,MyFriendsPanel.VideoVoiceFriends));
            listViews.add(new PagerItem(ModuleMgr.getCenterMgr().getMyInfo().isMan()?"送礼好友":"我的好友", panls.get(0).getContentView()));
            listViews.add(new PagerItem("视频/语音", panls.get(1).getContentView()));
        }else if(ModuleMgr.getCenterMgr().getMyInfo().isMan() && ModuleMgr.getCenterMgr().getMyInfo().isB()){
            panls.add(new MyFriendsPanel(this,MyFriendsPanel.GiftFriends));
            panls.add(new MyFriendsPanel(this,MyFriendsPanel.UnlockFriends));
            panls.add(new MyFriendsPanel(this,MyFriendsPanel.VideoVoiceFriends));
            listViews.add(new PagerItem("送礼好友", panls.get(0).getContentView()));
            listViews.add(new PagerItem("解锁好友", panls.get(1).getContentView()));
            listViews.add(new PagerItem("视频/语音", panls.get(2).getContentView()));
        }

    }

    private void initViewPager() {
        vpViewChange.setAdapter(new ViewGroupPagerAdapter(listViews));
    }


}
