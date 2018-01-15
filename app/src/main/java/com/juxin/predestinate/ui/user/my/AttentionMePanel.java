package com.juxin.predestinate.ui.user.my;

import android.content.Context;
import android.view.View;

import com.juxin.library.log.PToast;
import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweightList;
import com.juxin.predestinate.bean.my.AttentionList;
import com.juxin.predestinate.bean.my.AttentionUserDetail;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.custom.CustomStatusListView;
import com.juxin.predestinate.module.logic.baseui.xlistview.ExListView;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.JsonUtil;
import com.juxin.predestinate.ui.user.my.adapter.AttentionMeAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * 关注我的
 * Created by zm on 2017/4/25
 */
public class AttentionMePanel extends BasePanel implements RequestComplete, ExListView.IXListViewListener {

    private Context mContext;
    //有关控件
    private CustomStatusListView crvView;
    private ExListView rvList;
    //数据相关
    private List<AttentionUserDetail> mUserDetails = new ArrayList<>();
    private AttentionMeAdapter mAttentionMeAdapter;

    public AttentionMePanel(Context context) {
        super(context);
        mContext = context;
        setContentView(R.layout.f1_my_attention_panel);
        initView();
        reqData();
        crvView.showLoading();
    }

    //请求数据（关注我的列表）
    private void reqData() {
        ModuleMgr.getCommonMgr().getFollowers(this);
    }

    //初始化ui
    private void initView() {
        crvView = (CustomStatusListView) findViewById(R.id.my_attention_panel_crlv_list);
        rvList = crvView.getExListView();
        mAttentionMeAdapter = new AttentionMeAdapter(mContext, mUserDetails);
        rvList.setAdapter(mAttentionMeAdapter);
        rvList.setPullLoadEnable(false);//没有加载更多，所有数据一次返回
        rvList.setXListViewListener(this);
    }

    //请求数据返回
    @Override
    public void onRequestComplete(HttpResponse response) {
        rvList.stopRefresh();
        rvList.stopLoadMore();
        crvView.showExListView();
        if (response.getUrlParam() == UrlParam.getFollowers) {
            if (response.isOk()) {
                AttentionList lists = new AttentionList();
                lists.parseJson(response.getResponseString());

                List<AttentionList.AttentionInfo> infos = lists.getArr_lists();
                if (infos != null && !infos.isEmpty()) {//缓存中处理完毕后，还有消息记录，则该记录没有获取过，去服务器请求用户详细信息
                    List<Long> userIds = new ArrayList<>();
                    int size = infos.size();
                    for (int i = 0; i < size; i++) {
                        userIds.add(infos.get(i).getUid());
                    }
                    ModuleMgr.getCommonMgr().reqUserInfoSummary(userIds, this);//批量获取用户信息
                    return;
                }
                if (mUserDetails.size() <= 0) {
                    crvView.showNoData(mContext.getString(R.string.tip_data_empty), mContext.getString(R.string.tip_click_refresh), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            crvView.showLoading();
                            reqData();
                        }
                    });
                }
                return;
            }
            if (mUserDetails.size() <= 0) {
                crvView.showNetError(mContext.getString(R.string.tip_click_refresh), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        crvView.showLoading();
                        reqData();
                    }
                });
                return;
            }
            PToast.showShort(mContext.getString(R.string.net_error_check_your_net));
            return;
        }
        mUserDetails.clear();
        UserInfoLightweightList userInfos = new UserInfoLightweightList();
        userInfos.parseJsonSummary(JsonUtil.getJsonObject(response.getResponseString()));
        List<UserInfoLightweight> userList = userInfos.getLightweightLists();
        int size = userList.size();
        for (int i = 0; i < size; i++) {
            AttentionUserDetail userDetail = new AttentionUserDetail();
            userDetail.parseJs(userList.get(i));
            mUserDetails.add(userDetail);//添加到数据列表
            if (i == size - 1) {
                mAttentionMeAdapter.setList(mUserDetails);
            }
        }
    }

    //刷新界面
    public void reFreshUI() {
        if (mAttentionMeAdapter != null)
            mAttentionMeAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        reqData();
    }

    @Override
    public void onLoadMore() {//加载更多

    }
}