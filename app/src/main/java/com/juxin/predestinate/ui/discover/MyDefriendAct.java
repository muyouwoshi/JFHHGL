package com.juxin.predestinate.ui.discover;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.juxin.library.observe.Msg;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.library.observe.PObserver;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweightList;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.baseui.custom.CustomStatusListView;
import com.juxin.predestinate.module.logic.baseui.xlistview.ExListView;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 黑名单列表
 * Created by zhang on 2017/5/4.
 */

public class MyDefriendAct extends BaseActivity implements RequestComplete, ExListView.IXListViewListener, PObserver {

    private CustomStatusListView customStatusListView;
    private ExListView exListView;

    private MyDefriendAdapter adapter;
    private List<UserInfoLightweight> data = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f1_my_friend_act);
        initTitle();
        initView();
        onRefresh();
    }

    private void initTitle() {
        setBackView(R.id.base_title_back);
        setTitle(getResources().getString(R.string.my_defriend_title));
    }

    private void initView() {
        customStatusListView = (CustomStatusListView) findViewById(R.id.myfriend_list);
        View mViewTop = LayoutInflater.from(this).inflate(R.layout.layout_margintop, null);
        exListView = customStatusListView.getExListView();
        exListView.setXListViewListener(this);
        exListView.setPullRefreshEnable(true);
        exListView.setPullLoadEnable(false);
        exListView.addHeaderView(mViewTop);
        adapter = new MyDefriendAdapter(this, data);
        exListView.setAdapter(adapter);

        customStatusListView.showLoading();
        MsgMgr.getInstance().attach(this);
    }

    @Override
    public void onRefresh() {
        ModuleMgr.getCommonMgr().getMyDefriends(this);
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onRequestComplete(HttpResponse response) {
        if (response.isOk()) {
            if (!response.isCache()) {
                UserInfoLightweightList lightweightList = new UserInfoLightweightList();
                lightweightList.parseJson(response.getResponseString());
                if (lightweightList != null && lightweightList.getUserInfos().size() != 0) {
                    if (data.size() != 0) {
                        data.clear();
                    }
                    data.addAll(lightweightList.getUserInfos());
                    if (data.size() < 10) {
                        exListView.setPullLoadEnable(false);
                    }
                    adapter.notifyDataSetChanged();
                    customStatusListView.showExListView();
                } else {
                    customStatusListView.showNoData(getString(R.string.my_defriend_nodata), "关闭", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            back();
                        }
                    });
                }

                Map<String, Object> params = new HashMap<>();
                long[] uids = new long[data.size()];
                int i = 0;
                for (UserInfoLightweight userInfoLightweight : data) {
                    uids[i] = userInfoLightweight.getUid();
                    i++;
                }
                params.put("uids", uids);
                Statistics.userBehavior(SendPoint.menu_haoyou_blacklist, params);
                exListView.stopRefresh();
            }
        } else {
            customStatusListView.showNoData("请求出错", "重试", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    customStatusListView.showLoading();
                    onRefresh();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        MsgMgr.getInstance().detach(this);
        super.onDestroy();
    }

    @Override
    public void onMessage(String key, Object value) {
        if (key == MsgType.MT_REMARK_INFORM) {
            Msg msg = (Msg) value;
            if (adapter != null && msg.getData() instanceof String){
                for (int i = 0 ;i < adapter.getList().size(); i++){
                    if (adapter.getList().get(i).getUid() == Long.valueOf(msg.getKey())){
                        adapter.getList().get(i).setRemark(String.valueOf(msg.getData()));
                        break;
                    }
                }
            }
            adapter.notifyDataSetChanged();
        }else if (key == MsgType.MT_BLACK_STATUS){
            onRefresh();
        }
    }
}