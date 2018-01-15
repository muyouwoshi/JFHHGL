package com.juxin.predestinate.ui.user.my;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.library.log.PToast;
import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.my.RedOneKeyList;
import com.juxin.predestinate.bean.my.RedbagList;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.baseui.custom.CustomStatusListView;
import com.juxin.predestinate.module.logic.baseui.xlistview.ExListView;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.ui.user.my.adapter.RedBagTabAdapter;
import com.switfpass.pay.utils.Util;

import java.util.List;

/**
 * 收入详情
 * Created by zm on 2017/4/25
 */
public class RedBagRecordPanel extends BasePanel implements RequestComplete, ExListView.IXListViewListener, View.OnClickListener {

    private Context mContext;
    //有关控件
    private Button butOneKey;
    private CustomStatusListView crvView;
    private ExListView rvList;
    private LinearLayout tvNoData;
    //数据相关
    private List<RedbagList.RedbagInfo> mRedbagInfos;
    private RedBagTabAdapter mRedBagTabAdapter;

    public RedBagRecordPanel(Context context) {
        super(context);
        mContext = context;
        setContentView(R.layout.f1_wode_redbag_record_tab_panel);
        initView();
        reqData();
        crvView.showLoading();
    }

    //请求数据
    private void reqData() {
        ModuleMgr.getCommonMgr().getRedbagList(this);//请求收入详情列表
    }

    private void initView() {
        butOneKey = (Button) findViewById(R.id.wode_record_panel_btn_one_key);
        crvView = (CustomStatusListView) findViewById(R.id.wode_record_panel_crv_list);
        tvNoData = (LinearLayout) findViewById(R.id.withdraw_record_panel_tv_data_tip);
        tvNoData.setVisibility(View.GONE);
        butOneKey.setOnClickListener(this);

        rvList = crvView.getExListView();
        rvList.setHeaderStr(getContext().getString(R.string.xlistview_header_hint_normal),
                getContext().getString(R.string.xlistview_header_hint_loading));
        mRedBagTabAdapter = new RedBagTabAdapter(mContext, this);
        rvList.setAdapter(mRedBagTabAdapter);
        rvList.setPullLoadEnable(false);
        rvList.setXListViewListener(this);
    }

    //请求数据返回
    @Override
    public void onRequestComplete(HttpResponse response) {
        LoadingDialog.closeLoadingDialog();
        tvNoData.setVisibility(View.GONE);
        crvView.showExListView();
        rvList.stopRefresh();
        rvList.stopLoadMore();
        if (response.getUrlParam() == UrlParam.reqRedbagList) {
            RedbagList redbagList = new RedbagList();
            redbagList.parseJson(response.getResponseString());
            if (response.isOk()) {
                ((RedBoxRecordAct) context).refreshView(redbagList.getTotal());
                mRedbagInfos = redbagList.getRedbagLists();
            }
        } else if (response.getUrlParam() == UrlParam.reqAddredonekey) {
            if (response.isOk()) {
                RedOneKeyList redOneKeyList = (RedOneKeyList) response.getBaseData();
                ((RedBoxRecordAct) context).refreshView(redOneKeyList.getSum());
                mRedbagInfos = redOneKeyList.getRedbagFailLists();
                if (redOneKeyList.getSucnum() == 0) {
                    PToast.showShort(mContext.getString(R.string.no_add_redbag));
                } else {
                    PToast.showShort(mContext.getString(R.string.succeed) + redOneKeyList.getSucnum() + mContext.getString(R.string.hour_into_the_bag));
                }
            }
            PToast.showShort(response.getMsg());
        }
        handleData();
    }

    //处理数据
    public void handleData() {
        if (mRedbagInfos != null && !mRedbagInfos.isEmpty()) {
            mRedBagTabAdapter.setList(mRedbagInfos);
            butOneKey.setEnabled(true);
        } else {
            if (mRedbagInfos != null)
                mRedBagTabAdapter.setList(mRedbagInfos);
            butOneKey.setEnabled(false);
            showNoData();
        }
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
    public void onClick(View v) {//单击事件
        LoadingDialog.show((FragmentActivity) mContext);
        ModuleMgr.getCommonMgr().reqAddredonekey(ModuleMgr.getCenterMgr().getMyInfo().getUid(), this);//一键入袋
        Statistics.userBehavior(SendPoint.menu_me_money_onekey);
    }
}