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
import com.juxin.predestinate.module.util.my.AttentionUtil;
import com.juxin.predestinate.ui.user.my.adapter.MyAttentionAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * 我关注的
 * Created by zm on 2017/4/25
 */
public class MyAttentionPanel extends BasePanel implements RequestComplete, ExListView.IXListViewListener {

    private Context mContext;
    //有关控件
    private CustomStatusListView crvView;
    private ExListView rvList;
    //数据相关
    private List<AttentionUserDetail> mUserDetails = new ArrayList<>();
    private MyAttentionAdapter mAttentionMeAdapter;

    public MyAttentionPanel(Context context) {
        super(context);
        mContext = context;
        setContentView(R.layout.f1_my_attention_panel);
        initView();
        reqData();
    }

    //请求数据
    private void reqData() {
        ModuleMgr.getCommonMgr().getFollowing(this);
    }

    private void initView() {
        crvView = (CustomStatusListView) findViewById(R.id.my_attention_panel_crlv_list);
        rvList = crvView.getExListView();
        mAttentionMeAdapter = new MyAttentionAdapter(mContext, mUserDetails);
        rvList.setAdapter(mAttentionMeAdapter);
        rvList.setPullLoadEnable(false);
        rvList.setXListViewListener(this);
    }

    //请求数据返回
    @Override
    public void onRequestComplete(HttpResponse response) {
        rvList.stopRefresh();
        rvList.stopLoadMore();
        crvView.showExListView();
        if (response.getUrlParam() == UrlParam.getFollowing) {
            if (response.isOk()) {
                AttentionList lists = new AttentionList();
                lists.parseJson(response.getResponseString());

                List<AttentionList.AttentionInfo> infos = lists.getArr_lists();
                if (infos != null && !infos.isEmpty()) {
                    AttentionUtil.saveUserIdsById(infos);
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
            userDetail.setType(1);
            mUserDetails.add(userDetail);//添加到数据列表
            if (i == size - 1) {
                mAttentionMeAdapter.setList(mUserDetails);
            }
        }
    }

    //刷新界面
    public void reFreshUI() {
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