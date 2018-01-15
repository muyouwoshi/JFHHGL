package com.juxin.predestinate.ui.discover;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweightList;
import com.juxin.predestinate.module.local.statistics.StatisticsDiscovery;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.custom.CustomStatusListView;
import com.juxin.predestinate.module.logic.baseui.xlistview.ExListView;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建日期：2017/7/10
 * 描述:首页贵族
 * 作者:lc
 */
@Deprecated
class PeerageFragment extends BasePanel implements RequestComplete, ExListView.IXListViewListener {

    private ExListView exListView;
    private PeerageAdapter peerageAdapter;
    private CustomStatusListView lv_peerage;

    private int pager = 1;
    private int reload = 1;
    private List<UserInfoLightweight> arrList = new ArrayList<>();

    PeerageFragment(Context context) {
        super(context);
        setContentView(R.layout.p1_peerage_fragment);
        initView();
        initData();
    }

    private void initView() {
        View viewTop = LayoutInflater.from(getContext()).inflate(R.layout.layout_margintop, null);
        lv_peerage = (CustomStatusListView) findViewById(R.id.lv_peerage);
        exListView = lv_peerage.getExListView();
        exListView.setXListViewListener(this);
        exListView.setPullRefreshEnable(true);
        exListView.setPullLoadEnable(true);
        exListView.addHeaderView(viewTop);
        peerageAdapter = new PeerageAdapter(getContext(), arrList);
        exListView.setAdapter(peerageAdapter);
        exListView.setHeaderStr(null, null);
        exListView.setHeaderHintType(2);
    }

    private void initData() {
        ModuleMgr.getCommonMgr().getTitleUsers(pager, reload, this);
    }

    @Override
    public void onRefresh() {
        pager = 1;
        reload = 1;
        initData();
    }

    @Override
    public void onLoadMore() {
        ++pager;
        reload = 0;
        initData();
    }

    @Override
    public void onRequestComplete(HttpResponse response) {
        try {
            exListView.stopRefresh();
            exListView.stopLoadMore();
            if (response.isOk()) {
                String result = response.getResponseString();
                boolean isCache = response.isCache();
                if (!isCache) {
                    updArrList(result, false);
                } else {
                    if (pager == 1) {
                        updArrList(result, true);
                    }
                }
            } else {
                belowTenNum();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新数据
     *
     * @param result  //网络数据
     * @param isCache //是否为网络缓存
     */
    private synchronized void updArrList(String result, boolean isCache) throws Exception {
        UserInfoLightweightList lightweightList = new UserInfoLightweightList();
        lightweightList.parseJson(result);
        if (lightweightList.getUserInfos().size() != 0) {
            //非网络缓存进行-->统计
            if (!isCache && pager == 1) {
                StatisticsDiscovery.onRecommendRefresh(lightweightList.getLightweightLists(), false);
            }
            //是否重置分页 如果为true 则分页重新开始（清除之前数据） 为0为可以往下翻
            if (pager == 1 || lightweightList.isRef()) {
                if (arrList.size() != 0) {
                    arrList.clear();
                }
                pager = 1;
            }
            arrList.addAll(lightweightList.getUserInfos());
            belowTenNum();
        } else {
            if (!isCache) {
                showNoData();
            }
        }
    }

    /**
     * 暂无数据
     */
    private void showNoData() throws Exception {
        if (pager == 1) {
            lv_peerage.showNoData("暂无数据", "重试", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    lv_peerage.showLoading();
                    onRefresh();
                }
            });
        } else {
            exListView.setPullLoadEnable(false);
        }
    }

    /**
     * 总数小于10
     */
    private synchronized void belowTenNum() throws Exception {
        if (arrList != null && arrList.size() != 0) {
            if (arrList.size() < 10) {
                exListView.setPullLoadEnable(false);
            }
            peerageAdapter.notifyDataSetChanged();
            lv_peerage.showExListView();
        }
    }
}
