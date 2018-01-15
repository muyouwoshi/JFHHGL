package com.juxin.predestinate.ui.live.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.juxin.library.log.PSP;
import com.juxin.library.log.PToast;
import com.juxin.library.observe.RxBus;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.live.RoomPayInfo;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.SourcePoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.invoke.Invoker;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.ShareUtil;
import com.juxin.predestinate.module.util.TimeUtil;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.live.bean.DoubleGiftBean;
import com.juxin.predestinate.ui.live.bean.EnterLiveBean;
import com.juxin.predestinate.ui.live.bean.LiveRoomUserStatus;
import com.juxin.predestinate.ui.live.bean.LiveStatusEnum;
import com.juxin.predestinate.ui.live.bean.SendGiftCallbackBean;
import com.juxin.predestinate.ui.live.bean.ShareMatrial;
import com.juxin.predestinate.ui.live.callback.LiveShareQQShareCallBack;
import com.juxin.predestinate.ui.live.callback.OnOpenNewGirlLiveListener;
import com.juxin.predestinate.ui.live.callback.OnSendGiftCallbackListener;
import com.juxin.predestinate.ui.live.event.LiveBusEvent;
import com.juxin.predestinate.ui.live.event.LiveCmdBusEvent;
import com.juxin.predestinate.ui.live.view.LiveDoubleHitGiftView;
import com.juxin.predestinate.ui.live.view.LiveLiaoPanel;
import com.juxin.predestinate.ui.share.ShareDialog;
import com.juxin.predestinate.ui.user.util.CenterConstant;
import com.juxin.predestinate.ui.utils.NoDoubleClickListener;
import com.tangzi.sharelibrary.utils.ShareUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * 轻量分支观看直播间
 * Created by terry on 2017/7/12.
 */
public class UserLiveFragment extends LiveTopFragment {

    private FrameLayout mLiaoFl;                //撩按钮
    private ImageView mGiftIv;                  //礼物按钮
    private ImageView mChatIvUser;              //聊天按钮用户端
    private ImageView mShareIV;                 //分享按钮
    public ImageView mAddFriendIv;              //添加好友按钮
    private LiveDoubleHitGiftView mDoubleHitGiftView;    //礼物连击按钮
    private LiveLiaoPanel mLiaoPanel;
    EnterLiveBean enterLiveInfo;                //房间信息
    DoubleGiftBean doubleGiftBean = null;

    //分享相关
    private ArrayList<Integer> mShareChannel = null; //分享的渠道
    private ShareMatrial mShareMatial = null; //分享的素材

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUIRootView.setVisibility(View.GONE);

        mLiaoFl = (FrameLayout) view.findViewById(R.id.live_liao_container);
        mGiftIv = (ImageView) view.findViewById(R.id.live_gift_iv);
        mChatIvUser = (ImageView) view.findViewById(R.id.live_chat_iv_user);
        mShareIV = (ImageView) view.findViewById(R.id.live_share);
        mDoubleHitGiftView = (LiveDoubleHitGiftView) view.findViewById(R.id.l1_gift_double_view);
        mAddFriendIv = (ImageView) view.findViewById(R.id.live_tcontainer_follw_iv);

        mLiaoPanel = new LiveLiaoPanel(getActivity());
        mLiaoPanel.setRoomId(roomid);
        mLiaoFl.addView(mLiaoPanel.getContentView());

        initGiftDoubleHit();
        //用来处理视频开始 结束的操作
        Disposable disposable = RxBus.getInstance().toFlowable(LiveBusEvent.class).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<LiveBusEvent>() {
                    @Override
                    public void accept(@NonNull LiveBusEvent liveBusEvent) throws Exception {
                        if (liveBusEvent == LiveBusEvent.LIVE_PLAY_SUCCESS && getActivity() != null && !getActivity().isFinishing()) {
                            mUIRootView.setVisibility(View.VISIBLE);
//                            ((BaseLiveAct) getActivity()).setLoadingView(false);
                        } else if (liveBusEvent == LiveBusEvent.LIVE_CLOSE) {
                            if (getActivity() != null && !getActivity().isFinishing()) {
                                getActivity().finish();
                            }
                        }
                    }
                });
        rxDisposable.add(disposable);
        getEnterLiveUrl();
        App.liveStatus = LiveStatusEnum.USER_LIVING;
        mGiftIv.setOnClickListener(buttonListener);
        mChatIvUser.setOnClickListener(buttonListener);
        mShareIV.setOnClickListener(buttonListener);
        mAddFriendIv.setOnClickListener(buttonListener);

        mLiaoFl.setVisibility(View.VISIBLE);
        mGiftIv.setVisibility(View.VISIBLE);
        mChatIvUser.setVisibility(View.VISIBLE);
        mShareIV.setVisibility(View.VISIBLE);
        mLiveBottomView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mShareIV.setVisibility(View.VISIBLE);
    }

    /**
     * 用户进入直播间获取所有的UI值
     */
    public void getEnterLiveUrl() {
//        ((BaseLiveAct) getActivity()).setLoadingView(true);

        HashMap<String, Object> postParam = new HashMap<>();
        postParam.put("uid", uid);//主播ID
        postParam.put("roomid", roomid);//房间id
        postParam.put("platform", 2);// 平台 ios/android  1为ios  2为android
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.getEnterLiveUrl, postParam, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (response.isOk()) {
                    try {
                        if (response.getResponseJson().has("res") && !response.getResponseJson().isNull("res")) {
                            JSONObject resJo = response.getResponseJson().getJSONObject("res");

                            enterLiveInfo = JSON.parseObject(resJo.toString(),EnterLiveBean.class);

                            enterLiveInfo.setEnterTime(TimeUtil.getSecondTimeMil());
                            mOnLinePeopleCount = resJo.optInt("user_total");

                            boolean isMan = ModuleMgr.getCenterMgr().getMyInfo().isMan();
                            if (resJo.optInt("status") == LiveRoomUserStatus.ROOM_CONTROL_STATUS_DEFRIEND) {
                                PToast.showShort("您已被拉黑,无法进入直播间");
                                getActivity().finish();
                                return;
                            } else if (resJo.optInt("status") == LiveRoomUserStatus.ROOM_CONTROL_STATUS_KICK_OUT) {
                                PToast.showShort("您已被踢出,本次无法进入直播间");
                                getActivity().finish();
                                return;
                            }

                            if (resJo.isNull("room_status")) {
                                if (getActivity() != null && !getActivity().isFinishing()) {
                                    PToast.showShort("未知的房间状态");
                                    getActivity().finish();
                                }
                                return;
                            } else if (resJo.optInt("room_status") == EnterLiveBean.ROOM_STATUS_0) {//未直播
                                if (enterLiveInfo != null && getActivity() != null) {
                                    UIShow.showLiveStop(getActivity(), enterLiveInfo.nickname, enterLiveInfo.avatar, enterLiveInfo.pic, false,
                                            enterLiveInfo.tm, enterLiveInfo.charm,isMan?enterLiveInfo.is_friend:true, uid, roomid, enterLiveInfo == null ? "0" : "" + enterLiveInfo.channel_uid);
                                }
                                if (getActivity() != null && !getActivity().isFinishing()) {
                                    getActivity().finish();
                                }
                                return;
                            } else if (resJo.optInt("room_status") == EnterLiveBean.ROOM_STATUS_1) {//被禁播
                                if (getActivity() != null && !getActivity().isFinishing()) {
//                                    ((BaseLiveAct) getActivity()).showRoomFreezeView(resJo.optString("pic"));
                                }
                                return;
                            }

                            initHeaderViewData(resJo.optString("ws_url"),resJo.optString("charm"),resJo.getInt("user_total"),resJo.optString("nickname"),resJo.optString("avatar"));
                            mChatPanel.setRoomId(roomid,enterLiveInfo.single_card,enterLiveInfo.service_card);
                            if (isMan) {
                                mAddFriendIv.setVisibility(enterLiveInfo.is_friend ? View.GONE : View.VISIBLE);
                            }else{
                                mAddFriendIv.setVisibility(View.GONE);
                            }

//                            RxBus.getInstance().post(new LivePlayBusEvent(resJo.optString("rtmpurl"), resJo.optString("pic"), roomid)); //通知视频层播放
                            mRecyclerView.initEnterRoomUserHeadPortrait(getActivity(), false, roomid, enterLiveInfo);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        PToast.showShort("获取视频失败，请重试");
                        getActivity().finish();
                    }
                } else {
                    PToast.showShort(response.getMsg());
                    if (getActivity() != null && !getActivity().isFinishing()) {
//                        ((BaseLiveAct) getActivity()).setErrorView(true);
                    }
                }
            }
        });
    }

    /**
     * 房间页面按钮监听事件 聊天 礼物
     */
    NoDoubleClickListener buttonListener = new NoDoubleClickListener() {
        @Override
        public void onNoDoubleClick(View v) {
            if (v.getId() == R.id.live_gift_iv) {
                SourcePoint.getInstance().lockSource(getActivity(), "gift").lockRoompaySource(getRoomPayInfo())
                        .lockPPCSource(Long.parseLong(uid), enterLiveInfo.channel_uid);
                UIShow.showLiveBottomGiftDlg(getActivity(), roomid, giftComplete);
                Statistics.userBehavior(SendPoint.page_live_girl);
            } else if (v.getId() == R.id.live_chat_iv_user) {
                resetBottomViewStatus(View.GONE);
                mChatPanel.focus();
                Statistics.userBehavior(SendPoint.page_live_pubchat);
            } else if (v.getId() == R.id.live_tcontainer_follw_iv) {
                SourcePoint.getInstance().lockSource(getActivity(), "faceplusfirend_sendgift").lockRoompaySource(getRoomPayInfo())
                        .lockPPCSource(Long.parseLong(uid), enterLiveInfo.channel_uid);
                UIShow.showAddFriendGiftDialog(App.activity, uid, roomid, "", addFriendComplete);
                Statistics.userBehavior(SendPoint.page_live_avatar_mkfriendadd);
            } else if (v.getId() == R.id.live_share) {
                clickShare();
                Statistics.userBehavior(SendPoint.page_live_share);
            }
        }
    };

    /**
     * 点击分享按钮
     *
     * 判断是否获取 分享的渠道，如果获取，直接弹分享对话框
     *                      如果没有获取，则获取
     */
    private void clickShare() {
        if (mShareChannel != null) {

            //显示分享的对话框
            showShareDialog();
        } else {

            //获取分享的平台
            /**
             * uid 是 string 用户的uid
             * group 是 int 1,其他分享 2,分享解锁 3,直播分享
             */
            LoadingDialog.show(getActivity());
            ShareUtil.getShareChannels(String.valueOf(ModuleMgr.getCenterMgr().getMyInfo().getUid()),"3",new ShareUtil.GetChannelCallBack() {
                @Override
                public void channelBack(ArrayList<Integer> channels, int flag, String msg) {

                    LoadingDialog.closeLoadingDialog();

                    if (flag == ShareUtil.GET_DATA_OK) {

                        if (channels != null && channels.size() == 0) {
                            PToast.showShort("没有可分享的渠道");
                        } else {
                            mShareChannel = channels;
                            //显示分享的对话框
                            showShareDialog();
                        }
                    } else if (flag == ShareUtil.GET_DATA_PARSE_ERROR) {
                        PToast.showShort(msg);
                    } else if (flag == ShareUtil.GET_DATA_FAILED) {
                        PToast.showShort(msg);
                    }
                }
            });
        }
    }

    /**
     * 显示分享的对话框
     */
    private void showShareDialog(){

        UIShow.showWebLinkShareDialog(getActivity(),mShareChannel, new ShareDialog.OnWebShareListener(){

            @Override
            public void sendToWechatFriend() {
                getShareMatrial(ShareUtil.CHANNEL_WX_FRIEND);
            }

            @Override
            public void sendToWechatFriendCricle() {
                getShareMatrial(ShareUtil.CHANNEL_WX_FRIEND_CRICLE);
            }

            @Override
            public void sendToQQFriend() {
                getShareMatrial(ShareUtil.CHANNEL_QQ_FRIEND);
            }

            @Override
            public void sendToQQZone() {
                getShareMatrial(ShareUtil.CHANNEL_QQ_ZONE);
            }
        });
    }

    /**
     * 下载素材
     */
    private void getShareMatrial(final int channelId) {

        if (mShareMatial == null) {

            LoadingDialog.show(getActivity());

            PSP.getInstance().put(ModuleMgr.getCommonMgr().getPrivateKey(Constant.TEMP_SHARE_TYPE), CenterConstant.SHARE_TYPE_LIVE);
            ShareUtil.getShareMatriData(enterLiveInfo.room_id, "","2","live", enterLiveInfo.pic, new ShareUtil.GetShareMatrialCallBack() {
                        @Override
                        public void shareMatrialCallBack(List<ShareMatrial> list, int flag, String msg) {

                            LoadingDialog.closeLoadingDialog();

                            if (flag == ShareUtil.GET_DATA_OK) {

                                if (list != null && list.size() > 0) {
                                    mShareMatial=list.get(0);
                                    openShareSdk(mShareMatial,channelId);
                                }
                            }else if (flag == ShareUtil.GET_DATA_PARSE_ERROR) {
                                PToast.showShort(msg);
                            } else if (flag == ShareUtil.GET_DATA_FAILED) {
                                PToast.showShort(msg);
                            }
                        }
                    }
            );

        } else {

            openShareSdk(mShareMatial,channelId);
        }
    }

    /**
     * 开启分享平台
     */
    private void openShareSdk(ShareMatrial shareMatrial,int channelId){
        ShareUtil.shareFunction = ShareUtil.FUNCTION_LIVE_SHARE;
        ShareUtil.opt = ShareUtil.OPT_LIVE_SHARE;
        ShareUtil.scontent = ShareUtil.SCONTENT_LIVE_SHARE;
        ShareUtil.channel = channelId;
        if (channelId == ShareUtil.CHANNEL_WX_FRIEND) {
            ShareUtils.sendWebpageToWechat(shareMatrial.getLandingPageUrl(), shareMatrial.getShareContentTitle(),
                    shareMatrial.getShareContentSubTitle(),
                    shareMatrial.getShareContentIcon(),ShareUtils.WECHAT_FRIEND);
        }else if(channelId == ShareUtil.CHANNEL_WX_FRIEND_CRICLE){

            ShareUtils.sendWebpageToWechat(shareMatrial.getLandingPageUrl(), shareMatrial.getShareContentTitle(),
                    shareMatrial.getShareContentSubTitle(),
                    shareMatrial.getShareContentIcon(),ShareUtils.WECHAT_FRIEND_CIRCLE);

        }else if(channelId == ShareUtil.CHANNEL_QQ_FRIEND){

            ShareUtils.shareToQQ(getActivity(), shareMatrial.getShareContentTitle(),
                    shareMatrial.getShareContentSubTitle(), shareMatrial.getLandingPageUrl(),
                    shareMatrial.getShareContentIcon(), new LiveShareQQShareCallBack());

        }else if(channelId == ShareUtil.CHANNEL_QQ_ZONE){

            ArrayList<String> list=new ArrayList<>();
            list.add(shareMatrial.getShareContentIcon());

            ShareUtils.shareToQQZone(getActivity(), shareMatrial.getShareContentTitle(),
                    shareMatrial.getShareContentSubTitle(), shareMatrial.getLandingPageUrl(),
                    list, new LiveShareQQShareCallBack());

        }
    }

    @Override
    public RoomPayInfo getRoomPayInfo() {
        RoomPayInfo info = new RoomPayInfo();
        info.room_uid = Long.parseLong(uid);
        info.room_id = Long.parseLong(roomid);
        //本次直播唯一ID
        info.room_live_id = enterLiveInfo.live_id;
        //本次主播开播时长秒数
        info.room_live_time = enterLiveInfo.current_time + TimeUtil.getSecondTimeMil() - enterLiveInfo.enter_time;
        return info;
    }

    /**
     * 抢热一活动控制按钮可见状态
     */
    @Override
    public void setHotNumberOneViewsStatus(int visibleStatus) {
        super.setHotNumberOneViewsStatus(visibleStatus);

        mLiaoFl.setVisibility(visibleStatus);
        mChatIvUser.setVisibility(visibleStatus);
        mGiftIv.setVisibility(View.VISIBLE);
    }


    /**
     * 重置底部view可见状态
     */
    @Override
    public void resetBottomViewStatus(int visibleStatus) {
        super.resetBottomViewStatus(visibleStatus);
        mLiaoFl.setVisibility(visibleStatus);
        mGiftIv.setVisibility(visibleStatus);
        mChatIvUser.setVisibility(visibleStatus);
        mDoubleHitGiftView.setVisibility(View.GONE);
        mShareIV.setVisibility(visibleStatus);
    }

    /**
     * 打开个人卡片
     * @param uid
     */
    @Override
    public void openUserDetailCard(String uid,String message){
//        ((BaseLiveAct) getActivity()).getUserDetail(uid, enterLiveInfo == null ? false : enterLiveInfo.is_admin);
    }

    /**
     * 房间控制
     *
     * @param liveBusEvent
     */
    @Override
    public void roomControlCmd(LiveCmdBusEvent liveBusEvent) {
        if (liveBusEvent.target_uid == ModuleMgr.getCenterMgr().getMyInfo().getUid() && getActivity() != null && !getActivity().isFinishing()) {
            switch (liveBusEvent.room_control_type) {
                case LiveRoomUserStatus.ROOM_CONTROL_STATUS_GAG:
                    enterLiveInfo.status = LiveRoomUserStatus.ROOM_CONTROL_STATUS_GAG;
                    PToast.showShort(ModuleMgr.getCenterMgr().getMyInfo().getNickname() + "已被禁言");
                    break;
                case LiveRoomUserStatus.ROOM_CONTROL_STATUS_DEFRIEND:
                    PToast.showShort(ModuleMgr.getCenterMgr().getMyInfo().getNickname() + "已被拉黑");
                    getActivity().finish();
                    break;
                case LiveRoomUserStatus.ROOM_CONTROL_STATUS_KICK_OUT:
                    PToast.showShort(ModuleMgr.getCenterMgr().getMyInfo().getNickname() + "已被踢出直播间");
                    getActivity().finish();
                    break;
                case LiveRoomUserStatus.ROOM_CONTROL_STATUS_ADD_ADMIN:
                    enterLiveInfo.is_admin = true;
                    break;
                case LiveRoomUserStatus.ROOM_CONTROL_STATUS_CANCEL_ADMIN:
                    enterLiveInfo.is_admin = false;
                    break;
            }
        }
    }

    @Override
    public void needDiamondSource(){
        SourcePoint.getInstance().lockSource(getActivity(), "danmu_gempay")
                .lockPPCSource(Long.parseLong(uid), enterLiveInfo.channel_uid)
                .lockRoompaySource(getRoomPayInfo());
    }

    private OnSendGiftCallbackListener addFriendComplete = new OnSendGiftCallbackListener() {
        @Override
        public void onSendGiftCallback(boolean success, SendGiftCallbackBean bean) {
            if (success) {
                mAddFriendIv.setVisibility(View.GONE);
                if (enterLiveInfo != null) {
                    enterLiveInfo.is_friend = true;
                }
                if (bean != null) {
                    showFormatCharm(bean.charm);
                }
            }
        }
    };

    /**
     * 送礼物回调
     */
    private OnSendGiftCallbackListener giftComplete = new OnSendGiftCallbackListener() {
        @Override
        public void onSendGiftCallback(boolean success, SendGiftCallbackBean bean) {
            if (success) {
                if (bean != null) {
                    //回调bean不为空才会显示连击
                    doubleGiftBean = new DoubleGiftBean(bean.channel_id, bean.room_id, bean.gift_id, bean.gift_count);
                    mDoubleHitGiftView.setVisibility(View.VISIBLE);
                    showFormatCharm(bean.charm);
                    mAddFriendIv.setVisibility(View.GONE);
                    mGiftIv.setVisibility(View.GONE);
                    mDoubleHitGiftView.setDoubleGiftBean(doubleGiftBean);
                    mDoubleHitGiftView.startLoadingDoubleHint();
                    if (enterLiveInfo != null) {
                        enterLiveInfo.is_friend = true;
                    }
                }
            } else { //发送失败
                doubleGiftBean = null;
                mDoubleHitGiftView.setDoubleGiftBean(doubleGiftBean);
            }
        }
    };

    /**
     *初始化礼物连击接口回调
     */
    public void initGiftDoubleHit() {
        mDoubleHitGiftView.setOnDoubleHitListener(new LiveDoubleHitGiftView.OnDoubleHitListener() {
            @Override
            public void onHitEnd() {//连击结束显示礼物按钮
                mGiftIv.setVisibility(View.VISIBLE);
            }
        }, giftComplete);
    }

    @Override
    public void onTouchUp() {
        super.onTouchUp();
        if (mLiaoPanel != null) {
            mLiaoPanel.onTouchScreen();
        }
    }


    @Override
    public void goToLiveStop(String nickName,String avatar,long time, long charm){

        boolean isFriend = enterLiveInfo == null ? true : enterLiveInfo.is_friend;
        //女性用户默认不显示
        if (!ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
            isFriend = true;
        }
        UIShow.showLiveStop(getActivity(), nickName, avatar, enterLiveInfo == null ? "" : enterLiveInfo.pic, false,
                time, charm, isFriend, uid, roomid, enterLiveInfo == null ? "0" : "" + enterLiveInfo.channel_uid);

        if (getActivity() != null && !getActivity().isFinishing()){
            getActivity().finish();
        }
    }

    @Override
    public void onOpenNewGirlLive(String uid, String roomid,OnOpenNewGirlLiveListener listener) {
        super.onOpenNewGirlLive(uid, roomid,listener);

        this.uid = uid;
        this.roomid = roomid;
        mUIRootView.setVisibility(View.GONE);
        mLiaoPanel.setRoomId(roomid);
        mDoubleHitGiftView.onReset();
        //观众离开房间
        Invoker.getInstance().audienceLeave(roomid);
        enterLiveInfo = null;
        getEnterLiveUrl();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //观众离开房间
        Invoker.getInstance().audienceLeave(roomid);
    }
}
