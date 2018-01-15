package com.juxin.predestinate.ui.discover;

import android.content.Context;
import android.support.annotation.IntDef;
import android.view.LayoutInflater;
import android.view.View;

import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweightList;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.baseui.custom.CustomStatusListView;
import com.juxin.predestinate.module.logic.baseui.xlistview.ExListView;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;

import java.util.ArrayList;
import java.util.List;

/**
 * 送礼好友
 * Created by duanzhneg on 2017/8/22.
 */

public class MyFriendsPanel extends BasePanel implements RequestComplete, ExListView.IXListViewListener{

    private CustomStatusListView customStatusListView;
    private ExListView exListView;

    private MyFriendsAdapter adapter;
    private List<UserInfoLightweight> data = new ArrayList<>();

    private int page = 1;
    private Context mContext;
    public final static int GiftFriends=1;//送礼或者我的好友
    public final static int UnlockFriends=2;//解锁好友
    public final static int VideoVoiceFriends=3;//音视频好友
    public int mType=GiftFriends;

    @IntDef({GiftFriends,UnlockFriends,VideoVoiceFriends})
    public @interface Type{};

    public MyFriendsPanel(Context context, @Type int type ) {
        super(context);
        setContentView(R.layout.f1_my_friend_act);
        mContext=context;
        mType=type;
        initView();
        onRefresh();
    }



    private void initView() {
        findViewById(R.id.base_title).setVisibility(View.GONE);
        customStatusListView = (CustomStatusListView) findViewById(R.id.myfriend_list);
        View mViewTop = LayoutInflater.from(mContext).inflate(R.layout.layout_margintop, null);
        exListView = customStatusListView.getExListView();
        exListView.setXListViewListener(this);
        exListView.setPullRefreshEnable(true);
        exListView.setPullLoadEnable(true);
        exListView.addHeaderView(mViewTop);
        adapter = new MyFriendsAdapter(mContext, data);
        exListView.setAdapter(adapter);

        customStatusListView.showLoading();
    }

    @Override
    public void onRefresh() {
        exListView.setPullRefreshEnable(true);
        exListView.setPullLoadEnable(true);
        page = 1;
        ModuleMgr.getCommonMgr().getMyFriends(page, this,mType);
    }

    @Override
    public void onLoadMore() {
        page++;
        ModuleMgr.getCommonMgr().getMyFriends(page, this,mType);
    }

    @Override
    public void onRequestComplete(HttpResponse response) {
        if (response.isOk()) {
            if (!response.isCache()) {
                UserInfoLightweightList lightweightList = new UserInfoLightweightList();
                lightweightList.parseJsonFriends(response.getResponseString());
                if (lightweightList != null && lightweightList.getUserInfos().size() != 0) {

                    //更新好友数量 （如果列表中的数量与个人资料内好友数量不一致 更新个人资料的好友数量并发出更新好友数量的通知）
                    if (ModuleMgr.getCenterMgr().getMyInfo().getGiftfriendscnt() != lightweightList.getTotalcnt()) {
                        ModuleMgr.getCenterMgr().getMyInfo().setGiftfriendscnt(lightweightList.getTotalcnt());
                        MsgMgr.getInstance().sendMsg(MsgType.MT_Friend_Num_Notice, lightweightList.getTotalcnt());
                    }

                    if (page == 1) {
                        if (data.size() != 0) {
                            data.clear();
                        }
                    }
                    data.addAll(lightweightList.getUserInfos());
                    if (data.size() < 10) {
                        exListView.setPullLoadEnable(false);
                    }
                    adapter.notifyDataSetChanged();
                    customStatusListView.showExListView();
                } else {
                    if (page == 1) {
                        customStatusListView.showNoData(context.getString(R.string.my_friend_nodata), "去添加", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ((BaseActivity)context).back();
                            }
                        });
                    } else {
                        exListView.setPullLoadEnable(false);
                    }
                }

                if (page == 1) {
                    exListView.stopRefresh();
                } else {
                    exListView.stopLoadMore();
                }
            }
        } else {
            if (!response.isCache()) {
                customStatusListView.showNoData("请求出错", "重试", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        customStatusListView.showLoading();
                        onRefresh();
                    }
                });
            }
        }
    }
}
