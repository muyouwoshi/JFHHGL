package com.juxin.predestinate.ui.user.my;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.library.observe.PObserver;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.my.WithdrawList;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.baseui.custom.CustomStatusListView;
import com.juxin.predestinate.module.logic.baseui.xlistview.ExListView;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.user.my.adapter.WithDrawTabAdapter;

import java.util.List;


/**
 * 提现记录
 * Created by zm on 2017/4/25
 */
public class WithDrawRecordAct extends BaseActivity implements RequestComplete, ExListView.IXListViewListener, PObserver {

    //有关控件
    private CustomStatusListView crvView;
    private ExListView rvList;
    private LinearLayout tvNoData;
    //数据相关
    private List<WithdrawList.WithdrawInfo> mWithdrawInfos;
    private WithDrawTabAdapter mRedBagTabAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f1_withdraw_record_tab_panel);
        MsgMgr.getInstance().attach(this);
        initView();
        reqData();
        crvView.showLoading();
    }

    //请求数据
    private void reqData() {
        ModuleMgr.getCommonMgr().reqWithdrawlist(this);
    }

    private void initView() {
        setBackView(R.id.base_title_back);
        crvView = (CustomStatusListView) findViewById(R.id.withdraw_record_panel_crv_list);
        tvNoData = (LinearLayout) findViewById(R.id.withdraw_record_panel_tv_data_tip);
        tvNoData.setVisibility(View.GONE);
        rvList = crvView.getExListView();
        rvList.setHeaderStr(this.getString(R.string.xlistview_header_hint_normal),
                this.getString(R.string.xlistview_header_hint_loading));
        mRedBagTabAdapter = new WithDrawTabAdapter(this);
//        rvList.setEmptyView(tvNoData);
        rvList.setAdapter(mRedBagTabAdapter);
        rvList.setPullLoadEnable(false);//没有加载更多，所有数据一次返回
        rvList.setXListViewListener(this);
        setTitle(this.getString(R.string.withdraw_record), R.color.black);
        setTitleRight(getString(R.string.withdrawexplain), R.color.color_9ea3b5, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到提现说明页
                UIShow.showWithDrawExplainAct(WithDrawRecordAct.this);
                Statistics.userBehavior(SendPoint.menu_me_money_explain);
            }
        });
    }

    //请求数据返回
    @Override
    public void onRequestComplete(HttpResponse response) {
        rvList.stopRefresh();
        rvList.stopLoadMore();
        crvView.showExListView();
        if (response.isOk()) {
            WithdrawList withdrawList = new WithdrawList();
            withdrawList.parseJson(response.getResponseString());
            mWithdrawInfos = withdrawList.getRedbagLists();
            if (mWithdrawInfos != null && !mWithdrawInfos.isEmpty()) {
                tvNoData.setVisibility(View.GONE);
                mRedBagTabAdapter.setList(mWithdrawInfos);
                return;
            }
            showNoData();
            return;
        }
        if (mWithdrawInfos != null && !mWithdrawInfos.isEmpty()) {
            return;
        }
        showNoData();
        crvView.showExListView();
        PToast.showShort(this.getString(R.string.net_error_check_your_net));
    }

    //暂无数据
    private void showNoData() {
        tvNoData.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRefresh() {//刷新
        tvNoData.setVisibility(View.GONE);
        reqData();
    }

    @Override
    public void onLoadMore() {//加载更多

    }

    @Override
    public void onMessage(String key, Object value) {
        switch (key) {
            case MsgType.MT_GET_MONEY_Notice:
                onRefresh();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MsgMgr.getInstance().detach(this);
    }
}