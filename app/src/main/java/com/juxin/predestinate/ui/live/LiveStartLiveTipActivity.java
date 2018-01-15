package com.juxin.predestinate.ui.live;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.log.PLogger;
import com.juxin.library.log.PToast;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.config.base.NobilityList;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.baseui.custom.CustomStatusListView;
import com.juxin.predestinate.module.logic.baseui.xlistview.ExListView;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIUtil;
import com.juxin.predestinate.ui.live.bean.LiveStartLiveTipDetail;
import com.juxin.predestinate.ui.live.bean.LiveStartLiveTipDetailList;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 开播提醒设置页面
 * 结束直播 chengxiaobo on 2017/10/26 .
 */
public class LiveStartLiveTipActivity extends BaseActivity implements ExListView.IXListViewListener {

    private CustomStatusListView mCustomStatusListView;
    private ExListView mExListView;
    private List<LiveStartLiveTipDetail> mList = new ArrayList<LiveStartLiveTipDetail>();
    private int page = 0;
    //是否正在下载数据
    private boolean isGetData = false;
    private MyAdapter mMyAdapter;

    //一页返回多少条数据
    public static final int LIMIT = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_start_live_tip_layout);
        setBackView(getResources().getString(R.string.title_start_live_tip));

        initView();
        initStatus();

        getDataFromNet();
    }

    /**
     * 不用沉浸式状态栏
     */
    @Override
    public boolean isFullScreen() {
        return true;
    }

    /**
     * 初始化View
     */
    private void initView() {

        mCustomStatusListView = (CustomStatusListView) findViewById(R.id.lv_start_live_tip);
        mExListView = mCustomStatusListView.getExListView();
    }

    /**
     * 初始化状态
     */
    private void initStatus() {

        mCustomStatusListView.showExListView();
        mExListView.setXListViewListener(this);
        mExListView.setPullRefreshEnable(false);
        mExListView.setPullLoadEnable(true);

        setListViewData();
    }

    /**
     * 设置ListView数据
     */
    private void setListViewData() {

        mMyAdapter = new MyAdapter();
        mExListView.setAdapter(mMyAdapter);
        mExListView.setDivider(new ColorDrawable(Color.parseColor("#e6e6e6")));
        mExListView.setDividerHeight(UIUtil.dip2px(this, 1));

        View view = new View(this);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UIUtil.dip2px(this, 10)));
        mExListView.addHeaderView(view);
    }


    /**
     * 刷新
     */
    @Override
    public void onRefresh() {

    }

    /**
     * 设置没有更多
     */
    private void setNoMore() {

        mExListView.setPullLoadEnable(false);
    }

    /**
     * 加载更多
     */
    @Override
    public void onLoadMore() {
        getDataFromNet();
    }

    /**
     * 从网络获取数据
     */
    private void getDataFromNet() {

        if (isGetData) {
            return;
        }
        /**
         * "page": 1
         "limit": 10
         */
        final HashMap<String, Object> postParams = new HashMap<>();
        postParams.put("platform", "2");//android为2
        postParams.put("page", page + 1);
        postParams.put("limit", LIMIT);
        isGetData = true;

        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.GetLiveNotifyList, postParams, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {

                mExListView.stopRefresh();
                mExListView.stopLoadMore();

                isGetData = false;
                if (response.isOk()) {
                    try {
                        mCustomStatusListView.showExListView();
                        JSONObject resJo = response.getResponseJson().getJSONObject("res");
                        if (resJo != null) {
                            LiveStartLiveTipDetailList list = new LiveStartLiveTipDetailList();
                            list.parseJson(resJo);

                            if (list.getList() != null && list.getList().size() > 0) {

                                page++;
                                mList.addAll(list.getList());
                                mMyAdapter.notifyDataSetChanged();

                                if (list.getList().size() < LIMIT) {
                                    setNoMore();
                                }

                            } else {
                                setNoMore();
                            }
                        }
                        return;

                    } catch (JSONException e) {
                    }
                }
                setNetError();
                PToast.showShort(response.getMsg());
            }


        });
    }

    /**
     * 设置网络Error
     */
    private void setNetError() {

        if (page == 0) {
            mCustomStatusListView.showNetError(this.getString(R.string.tip_click_refresh), new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    getDataFromNet();
                }
            });
            return;
        }
    }

    /**
     * 从网络获取数据
     */
    private void updateNotifyStatus(final LiveStartLiveTipDetail bean) {

        /**
         * "page": 1
         "limit": 10
         */
        final HashMap<String, Object> postParams = new HashMap<>();
        postParams.put("platform", "2");//android为2
        postParams.put("tuid", bean.getUid());
        if (bean.getNotify() == LiveStartLiveTipDetail.NOTIFY_ON) {
            postParams.put("notify", LiveStartLiveTipDetail.NOTIFY_OFF);
        } else {
            postParams.put("notify", LiveStartLiveTipDetail.NOTIFY_ON);
        }

        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.SetLiveNotifyStatus, postParams, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (response.isOk()) {

                    if (bean.getNotify() == LiveStartLiveTipDetail.NOTIFY_ON) {
                        bean.setNotify(LiveStartLiveTipDetail.NOTIFY_OFF);
                    } else {
                        bean.setNotify(LiveStartLiveTipDetail.NOTIFY_ON);
                    }
                    mMyAdapter.notifyDataSetChanged();

                    PToast.showShort("操作成功");
                    return;
                }
                PToast.showShort(response.getMsg());
            }
        });
    }


    /**
     * listView adapter
     */
    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            LiveStartLiveTipActivity.MyHolder myHolder = new LiveStartLiveTipActivity.MyHolder();

            if (convertView == null) {

                convertView = LayoutInflater.from(LiveStartLiveTipActivity.this).inflate(R.layout.live_start_live_tip_item, parent, false);

                myHolder.ivHead = (ImageView) convertView.findViewById(R.id.iv_head);
                myHolder.tvNickName = (TextView) convertView.findViewById(R.id.tv_nickname);
                myHolder.ivJuewei = (ImageView) convertView.findViewById(R.id.iv_juewei);
                myHolder.ivOn = (ImageView) convertView.findViewById(R.id.iv_on);

                convertView.setTag(myHolder);

            } else {

                myHolder = (LiveStartLiveTipActivity.MyHolder) convertView.getTag();
            }

            final LiveStartLiveTipDetail bean = mList.get(position);

            //用户头像
            if (!"".equals(bean.getAvatar())) {
                ImageLoader.loadCircleAvatar(LiveStartLiveTipActivity.this, bean.getAvatar(), myHolder.ivHead);
            }

            //昵称
            myHolder.tvNickName.setText(bean.getNickname());

            //爵位
            NobilityList.Nobility tmpNobility = ModuleMgr.getCommonMgr().getCommonConfig().queryNobility(bean.getNobility(), bean.getGender());
            String icon = tmpNobility.getTitle_icon();

            if (icon == null || "".equals(icon)) {
                myHolder.ivJuewei.setVisibility(View.GONE);
            } else {
                ImageLoader.loadFitCenter(LiveStartLiveTipActivity.this, icon, myHolder.ivJuewei);
                myHolder.ivJuewei.setVisibility(View.VISIBLE);
            }

            //通知开关
            if (bean.getNotify() == LiveStartLiveTipDetail.NOTIFY_ON) {
                myHolder.ivOn.setImageResource(R.drawable.live_start_live_on);
            } else {
                myHolder.ivOn.setImageResource(R.drawable.live_start_live_off);
            }

            myHolder.ivOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    updateNotifyStatus(bean);
                }
            });

            return convertView;
        }
    }

    class MyHolder {

        ImageView ivHead;
        TextView tvNickName;
        ImageView ivJuewei;
        ImageView ivOn;

    }

}
