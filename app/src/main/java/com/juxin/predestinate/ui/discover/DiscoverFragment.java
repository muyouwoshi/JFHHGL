package com.juxin.predestinate.ui.discover;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONArray;
import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.library.observe.PObserver;
import com.juxin.library.utils.ViewUtils;
import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweightList;
import com.juxin.predestinate.bean.discover.BannerConfig;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.local.statistics.StatisticsDiscovery;
import com.juxin.predestinate.module.local.statistics.StatisticsManager;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.baseui.custom.CustomStatusListView;
import com.juxin.predestinate.module.logic.baseui.xlistview.ExListView;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.ui.discover.banner.BannerHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 推荐页
 *
 * @updateAuthor Mr.Huang
 * @updateDate 2017/4/20.
 */
public class DiscoverFragment extends BasePanel implements RequestComplete, View.OnClickListener, PObserver, ExListView.IXListViewListener {

    private static final int Look_All = 0; //查看全部
    private static final int Look_Near = 1; //只看附近的人

    private static final int Group_sayHai_Msg = 100; //群打招呼

    private CustomStatusListView customStatusListView;
    private ExListView exListView;
    private Button groupSayhiBtn;

    private DiscoverAdapter adapter;
    private BannerHelper bannerHelper;
    private List<BannerConfig.Banner> bannerList = new ArrayList<>();

    private List<UserInfoLightweight> infos = new ArrayList<>();

    private int page = 0;
    private boolean isNearPage = false;
    private boolean isB;
    private boolean isOneLoad = true;

    DiscoverFragment(Context context) {
        super(context);
        setContentView(R.layout.discover_fragment);
        isB = ModuleMgr.getCenterMgr().getMyInfo().isB();
        initView();
        onRefresh(); //默认加载全部
        MsgMgr.getInstance().attach(this);
    }

    public void detach() {
        MsgMgr.getInstance().detach(this);
    }

    private void initView() {
        groupSayhiBtn = (Button) findViewById(R.id.discover_group_sayhi_btn);
        groupSayhiBtn.setOnClickListener(this);
        groupSayhiBtn.setVisibility(ModuleMgr.getCenterMgr().getMyInfo().isMan() ? View.VISIBLE : View.GONE);
        if (!isB) {
            setGroupSayhiBtn(false);
        }

        customStatusListView = (CustomStatusListView) findViewById(R.id.discover_content);

        //@updateAuthor Mr.Huang
        //@updateDate 2017-07-26
        View mViewTop;
        if (ModuleMgr.getCenterMgr().getMyInfo().isMan() && isB) {
            //依照下面的代码布局添加banner
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setLayoutParams(new ExListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            FrameLayout layout = new FrameLayout(getContext());
            layout.setBackgroundColor(getContext().getResources().getColor(R.color.bg_color));
            layout.setLayoutParams(new ExListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            layout.setPadding(0, ViewUtils.dpToPx(5), 0, 0);
            View view = LayoutInflater.from(getContext()).inflate(R.layout.abtest_search_label, layout, false);

            View bannerView = LayoutInflater.from(getContext()).inflate(R.layout.f2_banner, null);
            layout.addView(view);
            linearLayout.addView(layout);
            linearLayout.addView(bannerView);
            mViewTop = linearLayout;
        } else {
            //如果是女号隐藏搜索
            mViewTop = LayoutInflater.from(getContext()).inflate(R.layout.layout_margintop_banner, null);
        }

        bannerHelper = new BannerHelper(getContext(), mViewTop, bannerList);
        bannerHelper.showBanner();

        exListView = customStatusListView.getExListView();
        exListView.setXListViewListener(this);
        exListView.setPullRefreshEnable(true);
        exListView.setPullLoadEnable(true);
        exListView.addHeaderView(mViewTop);
        adapter = new DiscoverAdapter(getContext(), infos);
        adapter.setNear(false);
        exListView.setAdapter(adapter);
        exListView.setHeaderStr(null, null);
        exListView.setHeaderHintType(2);
        customStatusListView.showLoading();
    }

    @Override
    public void onRefresh() {
        exListView.setPullRefreshEnable(true);
        if (!isNearPage) {
            exListView.setPullLoadEnable(true);
            page = 1;
            ModuleMgr.getCommonMgr().getMainPage(page, 1, this);
        } else {
            exListView.setPullLoadEnable(false);
            getNearData();
        }
        if (bannerList != null && bannerList.isEmpty()) {
            ModuleMgr.getCommonMgr().reqBanner(this);
        }
    }

    @Override
    public void onLoadMore() {
        page++;
        ModuleMgr.getCommonMgr().getMainPage(page, 0, this);
    }

    public void showDiscoverSelectDialog() {
        final DiscoverSelectDialog dialog = new DiscoverSelectDialog();
        dialog.setNear(isNearPage);
        dialog.setOnItemClick(new DiscoverSelectDialog.OnDialogItemClick() {
            @Override
            public void onDialogItemCilck(int position) {
                switch (position) {
                    case Look_All: //查看全部
                        isNearPage = false;
                        adapter.setNear(false);
                        if (!isB) {
                            setGroupSayhiBtn(false);
                        }
                        onRefresh();
                        //统计
                        StatisticsDiscovery.onClickLookAll();
                        dialog.dismiss();
                        break;
                    case Look_Near: //只看附近的人
                        isNearPage = true;
                        adapter.setNear(true);
                        onRefresh();
                        //统计
                        StatisticsDiscovery.onClickLookNear();
                        dialog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.showDialog((FragmentActivity) getContext());
    }

    @Override
    public void onRequestComplete(HttpResponse response) {
        exListView.stopRefresh();
        exListView.stopLoadMore();
        if (response.getUrlParam() == UrlParam.getMainPage) {
            setMainData(response);
        } else if (response.getUrlParam() == UrlParam.getNearUsers2) {
            setNearData(response);
        } else if (response.getUrlParam() == UrlParam.reqBanner) {
            setBannerData(response);
        }
    }

    private void setMainData(HttpResponse response) {
        if (response.isOk()) {
            if (!response.isCache()) {
                UserInfoLightweightList lightweightList = new UserInfoLightweightList();
                lightweightList.parseJson(response.getResponseString());
                statistics(lightweightList.getLightweightLists());
                if (lightweightList.getUserInfos().size() != 0) {
                    //统计
                    if (page == 1) {
                        StatisticsDiscovery.onRecommendRefresh(lightweightList.getLightweightLists(), isNearPage);
                    }

                    //ref 如果是 true 并且请求的如果非第一页
                    //那么返回来的就是第一页 应该把之前的数据都清掉，把返回的数据作为第一页
                    if (page == 1 || lightweightList.isRef()) {
                        if (infos.size() != 0) {
                            infos.clear();
                        }
                        page = 1;
                    }

                    infos.addAll(lightweightList.getUserInfos());
                    if (infos.size() < 10) {
                        exListView.setPullLoadEnable(false);
                    } else {
                        exListView.setPullLoadEnable(true);
                    }
                    adapter.notifyDataSetChanged();
                    customStatusListView.showExListView();
                    if (page > 1) {
                        StatisticsDiscovery.onRecommendLoadMore(lightweightList.getLightweightLists(), isNearPage);
                    }
                } else {
                    if (page == 1) {
                        customStatusListView.showNoData("暂无数据", "重试", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                customStatusListView.showLoading();
                                onRefresh();
                            }
                        });
                    } else {
                        exListView.setPullLoadEnable(false);
                    }
                }
            } else {
                if (page == 1) {
                    UserInfoLightweightList lightweightList = new UserInfoLightweightList();
                    lightweightList.parseJson(response.getResponseString());
                    if (lightweightList != null && lightweightList.getUserInfos().size() != 0) {
                        if (infos.size() != 0) {
                            infos.clear();
                        }
                        infos.addAll(lightweightList.getUserInfos());
                        exListView.setPullLoadEnable(false);
                        adapter.notifyDataSetChanged();
                        customStatusListView.showExListView();
                    }
                }
            }
        } else {
            if (infos.size() != 0) {
                if (infos.size() < 10) {
                    exListView.setPullLoadEnable(false);
                }
                adapter.notifyDataSetChanged();
                customStatusListView.showExListView();
            } else {
                customStatusListView.showNoData("暂无数据", "重试", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        customStatusListView.showLoading();
                        onRefresh();
                    }
                });
            }
        }
    }

    private void setNearData(HttpResponse response) {
        if (response.isOk()) {
            if (!response.isCache()) {
                UserInfoLightweightList lightweightList = new UserInfoLightweightList();
                lightweightList.parseJson(response.getResponseString());
                statistics(lightweightList.getLightweightLists());
                if (lightweightList.getUserInfos().size() != 0) {
                    if (infos.size() != 0) {
                        infos.clear();
                        //统计
                        StatisticsDiscovery.onRecommendRefresh(lightweightList.getLightweightLists(), isNearPage);
                    }
                    infos.addAll(lightweightList.getUserInfos());
                    if (infos.size() < 10) {
                        exListView.setPullLoadEnable(false);
                    } else {
                        exListView.setPullLoadEnable(true);
                    }
                    adapter.notifyDataSetChanged();
                    customStatusListView.showExListView();
                    if (!isB) {
                        if (!ModuleMgr.getCenterMgr().isRobot(infos.get(0).getKf_id())) {
                            setGroupSayhiBtn(false);
                        } else {
                            setGroupSayhiBtn(true);
                        }
                    }
                } else {
                    customStatusListView.showNoData("暂无数据", "重试", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            customStatusListView.showLoading();
                            getNearData();
                        }
                    });
                    if (!isB) {
                        groupSayhiBtn.setVisibility(View.GONE);
                    }
                }
            } else {
                UserInfoLightweightList lightweightList = new UserInfoLightweightList();
                lightweightList.parseJson(response.getResponseString());
                if (lightweightList.getUserInfos().size() != 0) {
                    if (infos.size() != 0) {
                        infos.clear();
                    }
                    infos.addAll(lightweightList.getUserInfos());
                    if (infos.size() < 10) {
                        exListView.setPullLoadEnable(false);
                    }
                    adapter.notifyDataSetChanged();
                    customStatusListView.showExListView();
                    if (!isB) {
                        if (!ModuleMgr.getCenterMgr().isRobot(infos.get(0).getKf_id())) {
                            setGroupSayhiBtn(false);
                        } else {
                            setGroupSayhiBtn(true);
                        }
                    }
                }
            }
        } else {
            if (infos.size() != 0) {
                adapter.notifyDataSetChanged();
                customStatusListView.showExListView();
                if (!isB) {
                    if (!ModuleMgr.getCenterMgr().isRobot(infos.get(0).getKf_id())) {
                        setGroupSayhiBtn(false);
                    } else {
                        setGroupSayhiBtn(true);
                    }
                }
            }
        }
    }

    private void getNearData() {
        ModuleMgr.getCommonMgr().getNearUsers2(this);
    }

    private void setBannerData(HttpResponse response) {
        if (response.isOk()) {
            BannerConfig bannerConfig = new BannerConfig();
            bannerConfig.parseJson(response.getResponseString());
            List<BannerConfig.Banner> tempList = bannerConfig.getBanner_config();
            if (bannerHelper != null) {
                bannerHelper.updData(tempList);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.discover_group_sayhi_btn:
                Statistics.userBehavior(SendPoint.menu_faxian_personfilter);
                doGroupSayHi();
                break;
            default:
                break;
        }
    }

    private Map<Integer, Long> onclickData = null;

    private void doGroupSayHi() {
        if (!isB) {
            if (isHasNoSayHi()) {
                if (ModuleMgr.getCenterMgr().isCanGroupSayHi(getContext())) {
                    LoadingDialog.show((FragmentActivity) getContext());
                    handler.sendEmptyMessage(Group_sayHai_Msg);
                }
            } else {
                PToast.showShort(getContext().getString(R.string.say_hi_group_refresh));
            }
        } else {
            GroupSayHelloDialog dialog = new GroupSayHelloDialog();
            dialog.showDialog((FragmentActivity) getContext());
        }

        //统计
        onclickData = new HashMap<>();
        if (infos.size() != 0) {
            for (int i = 0; i < infos.size(); i++) {
                if (!infos.get(i).isSayHello()) {
                    onclickData.put(i, infos.get(i).getUid());
                }
            }
        }
        StatisticsDiscovery.onNearGroupSayHello(onclickData);
    }

    /**
     * 一键打招呼
     */
    private void doSayHi() {
        Iterator<UserInfoLightweight> userInfos = infos.iterator();
        while (userInfos.hasNext()) {
            final UserInfoLightweight infoLightweight = userInfos.next();
            if (!infoLightweight.isSayHello()) {
                ModuleMgr.getChatMgr().sendSayHelloMsg(String.valueOf(infoLightweight.getUid()), getContext().getString(R.string.say_hello_txt),
                        infoLightweight.getKf_id(),
                        ModuleMgr.getCenterMgr().isRobot(infoLightweight.getKf_id()) ?
                                Constant.SAY_HELLO_TYPE_NEAR : Constant.SAY_HELLO_TYPE_SIMPLE, null);
                notifyAdapter(infoLightweight.getUid());
                handler.sendEmptyMessage(Group_sayHai_Msg);
                break;
            }
        }

        if (!isHasNoSayHi()) {
            LoadingDialog.closeLoadingDialog();
        }
    }

    /**
     * 刷新打招呼的状态
     *
     * @param uid
     */
    private void notifyAdapter(long uid) {
        for (UserInfoLightweight info : infos) {
            if (info.getUid() == uid) {
                info.setSayHello(true);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Group_sayHai_Msg:
                    doSayHi();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 当前列表内有没有还未打招呼的人
     *
     * @return true有 false没有
     */
    private boolean isHasNoSayHi() {
        if (infos.size() != 0) {
            for (UserInfoLightweight info : infos) {
                if (!info.isSayHello()) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    @Override
    public void onMessage(String key, Object value) {
        switch (key) {
            case MsgType.MT_Say_Hello_Notice: //一键打招呼完成后 当前列表内有一键打招呼的人的数据的时候 更新列表
                if (isB) {
                    JSONArray data = (JSONArray) value;
                    for (int i = 0; i < data.size(); i++) {
                        notifyAdapter(data.getJSONObject(i).getIntValue("uid"));
                    }
                } else {
                    if (value instanceof JSONArray) {
                        JSONArray data = (JSONArray) value;
                        for (int i = 0; i < data.size(); i++) {
                            notifyAdapter(data.getJSONObject(i).getIntValue("uid"));
                        }
                    } else {
                        List<UserInfoLightweight> data = (List<UserInfoLightweight>) value;
                        for (int i = 0; i < data.size(); i++) {
                            notifyAdapter(data.get(i).getUid());
                        }
                    }
                }
                break;
            case MsgType.MT_Say_HI_Notice:
                long uid = (long) value;
                notifyAdapter(uid);
                break;
            default:
                break;
        }
    }

    /**
     * 是否显示群打招呼
     *
     * @param isCanShow 状态更新是否显示（该参数控制在数据未加载的时候 不显示群打招呼） 默认传false
     */
    private void setGroupSayhiBtn(boolean isCanShow) {
        //我是VIP的时候不显示
        if (ModuleMgr.getCenterMgr().getMyInfo().isVip() || !ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
            groupSayhiBtn.setVisibility(View.GONE);
        } else {  //非VIP附近的人显示
            if (!isNearPage) {
                groupSayhiBtn.setVisibility(View.GONE);
            } else {
                if (isCanShow) {
                    groupSayhiBtn.setVisibility(View.VISIBLE);
                } else {
                    groupSayhiBtn.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * 刷新首页
     */
    public void refreshList() {
        onRefresh();
        if (adapter != null && adapter.getList() != null && adapter.getList().size() != 0) {
            exListView.setSelection(0);
        }
    }

    /**
     * 统计
     *
     * @param list 数据
     */
    private void statistics(List<UserInfoLightweight> list) {
        if (isOneLoad) {
            StatisticsManager.menu_faxian_tuijian_first_and_flush(list, true);
            isOneLoad = false;
        } else {
            StatisticsManager.menu_faxian_tuijian_first_and_flush(list, false);
        }
    }
}
