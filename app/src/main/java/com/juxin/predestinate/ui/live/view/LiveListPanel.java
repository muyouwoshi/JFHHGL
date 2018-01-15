package com.juxin.predestinate.ui.live.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.juxin.library.controls.smarttablayout.PagerItem;
import com.juxin.library.log.PToast;
import com.juxin.library.utils.NetworkUtils;
import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.location.LocationMgr;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.custom.CustomStatusListView;
import com.juxin.predestinate.module.logic.baseui.xlistview.ExListView;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.ui.live.adapter.LiveListAdapter;
import com.juxin.predestinate.ui.live.bean.LiveRoomBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author gwz
 */

public class LiveListPanel extends BasePanel implements RequestComplete, ExListView.IXListViewListener, View.OnClickListener, LiveHotHeader.BigSmallSwitchListener {
    /**
     * 发现附近标签
     */
    public static final int TAG_DISCOVERTY = 1;
    /**
     * 热门标签
     */
    public static final int TAG_HOT = 2;
    /**
     * 更多签约推荐
     */
    public static final int TAG_RECOMMEND_MORE = -100;
    private int tagId;
    private int page = 1;
    private int limit = 20;
    private CustomStatusListView customStatusListView;
    private ExListView exListView;
    private View viewNoData;
    private LiveListAdapter liveListAdapter;
    private boolean isRefresh, isNoLoc;
    private LiveHotHeader liveHotHeader;
    private ImageView ivNoLiveData;
    private TextView tvNetAnomaly;
    private PagerItem pagerItem;

    public LiveListPanel(Context context, int tagId) {
        super(context);
        this.tagId = tagId;
        setContentView(R.layout.panel_live_list);
        initView();
        getData();
    }

    public LiveListPanel(Context context, PagerItem pagerItem) {
        super(context);
        if (pagerItem != null) {
            this.pagerItem = pagerItem;
            tagId = pagerItem.getId();
        }
        setContentView(R.layout.panel_live_list);
        initView();
        if (tagId == TAG_DISCOVERTY && !LocationMgr.getInstance().isOk()) {
            isNoLoc = true;
            showNoDataView();
            return;
        }
        getData();
    }

    public static LiveListPanel newRecommendPanel(Context context) {
        return new LiveListPanel(context, TAG_RECOMMEND_MORE);
    }

    private void initView() {
        customStatusListView = (CustomStatusListView) findViewById(R.id.lv_live_list);
        viewNoData = findViewById(R.id.view_no_data);
        ivNoLiveData = (ImageView) findViewById(R.id.iv_no_live_data);
        tvNetAnomaly = (TextView) findViewById(R.id.tv_net_anomaly);
        viewNoData.setOnClickListener(this);
        exListView = customStatusListView.getExListView();
        exListView.setXListViewListener(this);
        exListView.setPullRefreshEnable(true);
        exListView.setPullLoadEnable(true);
        exListView.setHeaderStr(getContext().getString(R.string.xlistview_header_hint_normal), getContext().getString(R.string.xlistview_header_hint_loading));
        //hot tag add header view
        if (tagId == TAG_HOT) {
            liveHotHeader = new LiveHotHeader(getContext());
            liveHotHeader.setListener(this);
            exListView.addHeaderView(liveHotHeader.getContentView());
        }
        liveListAdapter = new LiveListAdapter();
        liveListAdapter.setTagId(tagId);
        exListView.setAdapter(liveListAdapter);
    }

    @Override
    public void onClick(View view) {
        if (isNoLoc) {
            return;
        }
        viewNoData.setVisibility(View.GONE);
        getData();
    }

    @Override
    public void onRequestComplete(HttpResponse response) {
        exListView.stopRefresh();
        exListView.stopLoadMore();
        if (response.isOk()) {
            parseLiveList(response.getResponseString());
            return;
        }
        showNoDataView();
    }

    @Override
    public void onRefresh() {
        isRefresh = true;
        page = 1;
        getData();
        if (liveHotHeader != null) {
            liveHotHeader.refreshData();
        }
    }

    @Override
    public void onLoadMore() {
        isRefresh = false;
        page++;
        getData();
    }

    @Override
    public void onSwitch(boolean isSmall) {
        liveListAdapter.switchBigSmall(isSmall);
    }

    private void getData() {
        HashMap<String, Object> params = new HashMap<>(3);
        if (!isRefresh && page == 1) {
            customStatusListView.showLoading();
        }
        UrlParam urlParam = UrlParam.getLiveListByTag;
        if (tagId == TAG_DISCOVERTY) {
            urlParam = UrlParam.getNearbyLiveList;
            params.put("longitude", LocationMgr.getInstance().getPointD().longitude);
            params.put("latitude", LocationMgr.getInstance().getPointD().latitude);
        } else if (tagId == TAG_RECOMMEND_MORE) {
            urlParam = UrlParam.getRecommendLiveMoreList;
        }
        params.put("tag_id", tagId);
        params.put("page", page);
        params.put("limit", limit);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(urlParam, params, this);
    }

    private void parseLiveList(String strJson) {
        try {
            ArrayList<LiveRoomBean> data = new ArrayList<>(limit);
            JSONObject resJo = new JSONObject(strJson).getJSONObject("res");
            JSONArray liveJa = resJo.getJSONArray("list");
            for (int i = 0; i < liveJa.length(); i++) {
                LiveRoomBean liveRoomBean = new LiveRoomBean();
                JSONObject liveJo = liveJa.getJSONObject(i);
                data.add(liveRoomBean.parseJson(liveJo));
            }

            if (data.size() < limit) {
                exListView.setPullLoadEnable(false);
            } else {
                exListView.setPullLoadEnable(true);
            }
            if (page == 1) {
                liveListAdapter.setData(data);
            } else {
                liveListAdapter.addData(data);
            }

            if (liveListAdapter.getCount() == 0) {
                showNoDataView();
            } else {
                viewNoData.setVisibility(View.GONE);
            }
            statistics(data);
            liveListAdapter.notifyDataSetChanged();
            customStatusListView.showExListView();
        } catch (JSONException e) {
            e.printStackTrace();
            showNoDataView();
        }
    }

    private void showNoDataView() {
        viewNoData.setVisibility(View.VISIBLE);
        if (isNoLoc) {
            ivNoLiveData.setImageResource(R.drawable.ic_no_live_data);
            tvNetAnomaly.setText(R.string.label_open_loc);
            return;
        }
        if (NetworkUtils.isConnected(getContext())) {
            ivNoLiveData.setImageResource(R.drawable.ic_no_live_data);
            tvNetAnomaly.setText(R.string.tip_click_refresh);
        } else {
            ivNoLiveData.setImageResource(R.drawable.ic_no_live_data_no_net);
            tvNetAnomaly.setText(R.string.label_net_excep_retry);
        }
    }

    /**
     * 统计数据
     */
    private void statistics(ArrayList<LiveRoomBean> data) {
        if (page == 1 && pagerItem != null) {
            SendPoint sp = isRefresh ? SendPoint.menu_zhibo_flush : SendPoint.menu_zhibo_firstload;
            HashMap<String, Object> params = new HashMap<>(2);
            JSONArray ja = new JSONArray();
            for (LiveRoomBean bean : data) {
                ja.put(bean.uid);
            }
            params.put("title", pagerItem.getTitle());
            params.put("uids", ja.toString());
            Statistics.userBehavior(sp);
        }
    }


}
