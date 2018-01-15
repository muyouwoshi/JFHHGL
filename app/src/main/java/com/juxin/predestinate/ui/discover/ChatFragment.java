package com.juxin.predestinate.ui.discover;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.juxin.library.log.PToast;
import com.juxin.library.utils.NetworkUtils;
import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweightList;
import com.juxin.predestinate.bean.discover.BannerConfig;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.statistics.SourcePoint;
import com.juxin.predestinate.module.local.statistics.StatisticsManager;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.custom.CustomStatusListView;
import com.juxin.predestinate.module.logic.baseui.xlistview.ExListView;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.logic.socket.IMProxy;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.agora.model.AgoraConstant;
import com.juxin.predestinate.ui.agora.util.VideoAudioChatHelper;
import com.juxin.predestinate.ui.discover.banner.BannerHelper;
import com.juxin.predestinate.ui.user.paygoods.GoodsConstant;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 语聊
 * <p>
 * Created by Su on 2017/8/9.
 */
public class ChatFragment extends BasePanel implements RequestComplete, ExListView.IXListViewListener, View.OnClickListener, MainAVChatFragmentAdapter.OnItemClickListener, IMProxy.IMListener {

    private Button groupSayhiBtn;
    private ExListView exListView;
    private BannerHelper bannerHelper;
    private MainAVChatFragmentAdapter adapter;
    private CustomStatusListView customStatusListView;

    private boolean isOneLoad = true;
    private List<BannerConfig.Banner> bannerList = new ArrayList<>();

    public ChatFragment(Context context) {
        super(context!=null?context: App.getActivity());
        setContentView(R.layout.chat_fragment);
        initView();
        onRefresh(); //默认加载全部
        IMProxy.getInstance().attach(this);
    }

    private void initView() {
        customStatusListView = (CustomStatusListView) findViewById(R.id.chat_content);
        groupSayhiBtn = (Button) findViewById(R.id.group_sayhi_btn);
        groupSayhiBtn.setOnClickListener(this);
        groupSayhiBtn.setVisibility(ModuleMgr.getCenterMgr().getMyInfo().isMan() ? View.VISIBLE : View.GONE);

        View mViewTop = LayoutInflater.from(getContext()).inflate(R.layout.layout_margintop_banner, null);
        bannerHelper = new BannerHelper(getContext(), mViewTop, bannerList);
        bannerHelper.showBanner();
        exListView = customStatusListView.getExListView();
        exListView.setXListViewListener(this);
        exListView.setPullRefreshEnable(true);
        exListView.setPullLoadEnable(false);
        exListView.addHeaderView(mViewTop);

        adapter = new MainAVChatFragmentAdapter();
        adapter.setOnItemClickListener(this);

        exListView.setAdapter(adapter);
        exListView.setHeaderStr(null, null);
        exListView.setHeaderHintType(2);
        customStatusListView.showLoading();
    }

    @Override
    public void onRefresh() {
        exListView.setPullRefreshEnable(true);
        ModuleMgr.getCommonMgr().getVideoChatUsers(this);
        if (bannerList != null && bannerList.isEmpty()) {
            ModuleMgr.getCommonMgr().reqBanner(this);
        }
    }

    @Override
    public void onLoadMore() {
    }

    @Override
    public void onRequestComplete(HttpResponse response) {
        exListView.stopRefresh();
        exListView.stopLoadMore();
        if (response.getUrlParam() == UrlParam.getVideoChatUsers) {
            setChatData(response);
        } else if (response.getUrlParam() == UrlParam.reqBanner) {
            setBannerData(response);
        }
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

    private void setChatData(HttpResponse response) {
        // 请求失败
        if (!response.isOk()) {
            if (adapter.size() != 0) {
                customStatusListView.showExListView();
                return;
            }
            customStatusListView.showNoData("暂无数据", "重试", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    customStatusListView.showLoading();
                    onRefresh();
                }
            });
            return;
        }

        // 非缓存
        if (!response.isCache()) {
            UserInfoLightweightList lightweightList = new UserInfoLightweightList();
            lightweightList.parseJson(response.getResponseString());
            List<UserInfoLightweight> list = lightweightList.getUserInfos();
            if (isOneLoad) {
                isOneLoad = false;
                StatisticsManager.menu_faxian_yuliao_first_and_flush(list, false);
            } else {
                StatisticsManager.menu_faxian_yuliao_first_and_flush(list, true);
            }
            if (list.size() != 0) {
                adapter.clear();
                adapter.setListData(list);
                adapter.addAll(transform(list));
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
            return;
        }

        // 缓存
        UserInfoLightweightList lightweightList = new UserInfoLightweightList();
        lightweightList.parseJson(response.getResponseString());
        if (lightweightList.getUserInfos().size() != 0) {
            adapter.clear();
            adapter.addAll(transform(lightweightList.getUserInfos()));
            adapter.notifyDataSetChanged();
            customStatusListView.showExListView();
        }
    }

    public List<MainAVChatFragmentAdapter.Item> transform(List<UserInfoLightweight> list) {
        List<MainAVChatFragmentAdapter.Item> groups = new ArrayList<>();
        Iterator<UserInfoLightweight> iterator = list.iterator();
        MainAVChatFragmentAdapter.Item item;
        while (iterator.hasNext()) {
            item = new MainAVChatFragmentAdapter.Item();
            item.one = iterator.next();
            item.size = 1;
            if (iterator.hasNext()) {
                item.two = iterator.next();
                item.size = 2;
            }
            if (iterator.hasNext()) {
                item.three = iterator.next();
                item.size = 3;
            }
            groups.add(item);
        }
        return groups;
    }

    /**
     * 刷新首页
     */
    public void refreshList() {
        onRefresh();
        if (adapter != null && adapter.getCount() != 0) {
            exListView.setSelection(0);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.group_sayhi_btn:
//                if (ModuleMgr.getCenterMgr().getMyInfo().isVip()) {
                if (!NetworkUtils.isConnected(getContext())) {
                    PToast.showShort("网络异常,请稍后再试");
                    return;
                }
                GroupSayHelloDialog dialog = new GroupSayHelloDialog();
                dialog.showDialog((FragmentActivity) context);
//                } else {
//                    UIShow.showOpenVipDialogNew(getContext(), null, "您非VIP用户，不可以解锁一键打招呼功能", false, "确认支付");
//                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(View child, final UserInfoLightweight userInfoLightweight) {//此处视频语音是个大坑，开发前咨询相关人员
        // 防止快速点击
        if (isFastClick()) {
            return;
        }
        StatisticsManager.menu_faxian_yuliao_click_head(userInfoLightweight.getUid(), userInfoLightweight.getVtype(), adapter.getListUsers());
        SourcePoint.getInstance().lockPPCSource(userInfoLightweight.getUid(), userInfoLightweight.getChannel_uid());//ppc统计
        if (userInfoLightweight.getVtype() == 1) {
            SourcePoint.getInstance().lockSource((Activity) getContext(), "video");
            final String source = SourcePoint.getInstance().getSource();
            VideoAudioChatHelper.getInstance().peeragesHome((Activity) getContext(), userInfoLightweight.getUid(),
                    userInfoLightweight.getVtype() == 1 ? AgoraConstant.RTC_CHAT_VIDEO : AgoraConstant.RTC_CHAT_VOICE,
                    false, Constant.APPEAR_TYPE_NO, userInfoLightweight.getChannel_uid() + "", false, new VideoAudioChatHelper.VideoCardPeeragesHomeBackCall() {

                        @Override
                        public boolean showDiamondDlg() {
                            UIShow.showBottomChatDiamondDlg(getContext(), userInfoLightweight.getUid(),
                                    userInfoLightweight.getVtype(), userInfoLightweight.getPrice(), false,
                                    0, 2, GoodsConstant.DLG_DIAMOND_YULIAO, userInfoLightweight, true
                            );
                            return true;
                        }

                        @Override
                        public boolean showVipDlg() {
                            UserDetail userDetail = new UserDetail();
                            userDetail.setAvatar(userInfoLightweight.getAvatar());
                            userDetail.setNickname(userInfoLightweight.getNickname());
                            userDetail.setAge(userInfoLightweight.getAge());
                            userDetail.setGender(userInfoLightweight.getGender());
                            userDetail.getChatInfo().setVideoPrice(userInfoLightweight.getPrice());
                            userDetail.setGroup(userInfoLightweight.getGroup());
                            userDetail.getNobilityInfo().setRank(userInfoLightweight.getNobility_rank());
                            UIShow.showCallingVipDialog(userDetail, userInfoLightweight.getDistance());
                            return true;
                        }

                        @Override
                        public boolean oldProcess(Activity context, boolean isUseVideoCard, int videochat_len) {
                            return false;
                        }
                    }, userInfoLightweight, source);
        } else {
            SourcePoint.getInstance().lockSource((Activity) getContext(), "voice");
            final String source = SourcePoint.getInstance().getSource();
            VideoAudioChatHelper.getInstance().chatFragmentInvite((Activity) getContext(), userInfoLightweight.getUid(),
                    userInfoLightweight.getInvite_id(), userInfoLightweight.getVtype(), userInfoLightweight.getPrice(), userInfoLightweight, source, new RequestComplete() {
                        @Override
                        public void onRequestComplete(HttpResponse response) {
                            userInfoLightweight.setInvite_id(0);
                            if (response.isOk()) {
                                return;
                            }
                            VideoAudioChatHelper.getInstance().executeInviteChat((Activity) getContext(),
                                    userInfoLightweight.getUid(), AgoraConstant.RTC_CHAT_VOICE, "", false, 0, source);
                        }
                    });
        }
    }

    @Override
    public void onMessage(long msgId, boolean group, String groupId, long sender, String contents) {
        try {
            JSONObject object = new JSONObject(contents);
            int type = object.optInt("mtp");
            if (type == BaseMessage.BaseMessageType.inviteVideoMass.getMsgType()) {
                long fid = object.optInt("fid");
                int invite_id = object.optInt("invite_id");
                List<MainAVChatFragmentAdapter.Item> list = adapter.getList();
                for (MainAVChatFragmentAdapter.Item item : list) {
                    if (item.one.getUid() == fid) {
                        item.one.setInvite_id(invite_id);
                        return;
                    }
                    if (item.two != null && item.two.getUid() == fid) {
                        item.two.setInvite_id(invite_id);
                        return;
                    }
                    if (item.three != null && item.three.getUid() == fid) {
                        item.three.setInvite_id(invite_id);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void detach() {
        IMProxy.getInstance().detach(this);
    }

    // -----------------防止快速点击处理-----------------

    private long clickTime;

    private boolean isFastClick() {
        long currentClickTime = System.currentTimeMillis();
        boolean b = currentClickTime - clickTime < 1000;
        clickTime = currentClickTime;
        return b;
    }
}
