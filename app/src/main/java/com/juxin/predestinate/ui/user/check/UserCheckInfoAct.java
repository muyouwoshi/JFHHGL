package com.juxin.predestinate.ui.user.check;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.juxin.library.log.PSP;
import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.library.observe.PObserver;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.bean.center.user.detail.UserVideo;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.module.local.chat.MessageRet;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.SourcePoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.local.statistics.StatisticsSpread;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.logic.socket.IMProxy;
import com.juxin.predestinate.module.logic.socket.NetData;
import com.juxin.predestinate.module.util.TimerUtil;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.agora.model.AgoraConstant;
import com.juxin.predestinate.ui.agora.util.VideoAudioChatHelper;
import com.juxin.predestinate.ui.user.check.bean.VideoConfig;
import com.juxin.predestinate.ui.user.paygoods.GoodsConstant;
import com.juxin.predestinate.ui.user.util.CenterConstant;
import com.juxin.predestinate.ui.utils.CheckIntervalTimeUtil;
import com.juxin.predestinate.ui.utils.NoDoubleClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 查看用户资料详情
 * Created by Su on 2016/5/30.
 */
public class UserCheckInfoAct extends BaseActivity implements PObserver, RequestComplete {

    public static final int REQUEST_CODE_UNLOCK_VIDEO = 100;//请求跳转到私密视频页面
    private int channel;  // 查看用户资料区分Tag，默认查看自己个人资料

    private ImageView iv_sayhello;
    private TextView tv_sayhi;
    private LinearLayout videoBottom, voiceBottom, sayHibottom;

    private UserDetail userDetail;                  // 用户资料
    private UserCheckInfoHeadPanel headPanel;       // panel
    private UserCheckInfoFootPanel footPanel;       // panel
    private ScrollView scrollLayout;    // ScrollView
    private VideoConfig config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p1_user_info_layout);
        initData();
        initView();
        MsgMgr.getInstance().attach(this);
    }

    private void initData() {
        channel = getIntent().getIntExtra(CenterConstant.USER_CHECK_INFO_KEY, CenterConstant.USER_CHECK_INFO_OWN);
        if (channel == CenterConstant.USER_CHECK_INFO_OWN) {
            userDetail = ModuleMgr.getCenterMgr().getMyInfo();
            return;
        }
        userDetail = getIntent().getParcelableExtra(CenterConstant.USER_CHECK_OTHER_KEY);
        SourcePoint.getInstance().lockPPCSource(userDetail.getUid(),userDetail.getChannel_uid());//ppc统计
    }

    private void initView() {
        initTitle();
        scrollLayout = (ScrollView) findViewById(R.id.layout_scroll);
        LinearLayout container = (LinearLayout) findViewById(R.id.container);
        headPanel = new UserCheckInfoHeadPanel(this, channel, userDetail);
        footPanel = new UserCheckInfoFootPanel(this, channel, userDetail);
        footPanel.setSlideIgnoreView(this);
        container.addView(headPanel.getContentView());
        container.addView(footPanel.getContentView());
        //男用户点击首页贵族进入TA人资料，底部隐藏
        if (ModuleMgr.getCenterMgr().getMyInfo().isMan() && channel == CenterConstant.USER_CHECK_PEERAGE_OTHER)
            return;
        initBottom();
    }

    private void initTitle() {
        setBackView();
        setTitle(userDetail.getShowName(), ContextCompat.getColor(this, R.color.white));
        setTitleBackground(R.color.transparent);
        setIsBottomLineShow(false);
        // 男用户点击首页贵族进入TA人资料，右侧按钮隐藏
        if (channel == CenterConstant.USER_CHECK_INFO_OWN || (ModuleMgr.getCenterMgr().getMyInfo().isMan()
                && channel == CenterConstant.USER_CHECK_PEERAGE_OTHER)) return;
        setTitleRightImg(R.drawable.f1_more_vertical_dot, 3, listener);
        setTitleRightImg2(R.drawable.spread_share_btn, listener);
    }

    // 底部功能按钮展示逻辑
    private void initBottom() {
        if (channel == CenterConstant.USER_CHECK_INFO_OWN) return;

        videoBottom = (LinearLayout) findViewById(R.id.ll_userinfo_bottom_video);
        voiceBottom = (LinearLayout) findViewById(R.id.ll_userinfo_bottom_voice);
        sayHibottom = (LinearLayout) findViewById(R.id.ll_userinfo_bottom_hi);
        iv_sayhello = (ImageView) findViewById(R.id.iv_sayhello);
        tv_sayhi = (TextView) findViewById(R.id.tv_sayhello_text);
        TextView tv_look_look = (TextView) findViewById(R.id.tv_look_look);

        videoBottom.setOnClickListener(listener);
        voiceBottom.setOnClickListener(listener);
        findViewById(R.id.iv_gift).setVisibility(View.VISIBLE);
        findViewById(R.id.iv_gift).setOnClickListener(listener);
        findViewById(R.id.userinfo_bottom).setVisibility(View.VISIBLE);
        findViewById(R.id.ll_userinfo_bottom_send).setOnClickListener(listener);

        if (userDetail == null) return;

        ModuleMgr.getCenterMgr().reqVideoChatConfig(userDetail.getUid(), this); // 请求音视频开关配置

        if (userDetail.isMan()) {
            tv_look_look.setText(R.string.user_info_look_at_he);
        } else {
            tv_look_look.setText(R.string.user_info_look_at_her);
        }

        if (userDetail.isSayHello()) {   // 已打招呼
            initSayHi();
        }
        sayHibottom.setOnClickListener(listener);
    }

    private NoDoubleClickListener listener = new NoDoubleClickListener() {
        @Override
        public void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.base_title_right_img_container:// 标题右侧按钮
                    UIShow.showUserOtherSetAct(UserCheckInfoAct.this, userDetail.getUid(), userDetail, CenterConstant.USER_SET_FROM_CHECK);
                    break;

                case R.id.ll_userinfo_bottom_send:  // 底部发信
                    Statistics.userBehavior(SendPoint.userinfo_btnsendmessage, userDetail.getUid());
                    SourcePoint.getInstance().lockSource(UserCheckInfoAct.this, "sendchat");
                    // 爵位版取消发信限制，改为在私聊页面的消费行为[2017.9.29订正]
                    UIShow.showPrivateChatAct(UserCheckInfoAct.this, userDetail.getUid(), null);
                    break;

                case R.id.ll_userinfo_bottom_hi: // 底部打招呼
                    Statistics.userBehavior(SendPoint.userinfo_btnsayhello, userDetail.getUid());
                    handleSayHi();
                    break;

                case R.id.ll_userinfo_bottom_video: // 底部发视频
                    if (checkInviteInterval()) return;
                    Statistics.userBehavior(SendPoint.userinfo_btnvideo, userDetail.getUid());
                    sendVideo();
                    break;

                case R.id.ll_userinfo_bottom_voice: // 底部发语音
                    if (checkInviteInterval()) return;
                    Statistics.userBehavior(SendPoint.userinfo_btnvoice, userDetail.getUid());
                    SourcePoint.getInstance().lockSource(App.activity, "sendvoice");
                    VideoAudioChatHelper.getInstance().inviteVAChat(UserCheckInfoAct.this, userDetail.getUid(),
                            AgoraConstant.RTC_CHAT_VOICE, String.valueOf(userDetail.getChannel_uid()), SourcePoint.getInstance().getSource());
                    break;

                case R.id.iv_gift: // 底部礼物悬浮框
                    Statistics.userBehavior(SendPoint.userinfo_btngirl);
                    SourcePoint.getInstance().lockSource(UserCheckInfoAct.this, "gift");
                    UIShow.showBottomGiftDlg(UserCheckInfoAct.this, userDetail.getUid(), Constant.OPEN_FROM_INFO, String.valueOf(userDetail.getChannel_uid()));
                    break;

                case R.id.base_title_right_img2:
                    PSP.getInstance().put(ModuleMgr.getCommonMgr().getPrivateKey(Constant.TEMP_SHARE_TYPE), CenterConstant.SHARE_TYPE_USERMAIN);
                    UIShow.showShareForUserinfo(UserCheckInfoAct.this,userDetail);
                    StatisticsSpread.onPage_info_share();
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * 发起音视频操作间隔限制
     */
    private CheckIntervalTimeUtil checkIntervalTimeUtil;

    private boolean checkInviteInterval() {
        if (checkIntervalTimeUtil == null)
            checkIntervalTimeUtil = new CheckIntervalTimeUtil();

        if (checkIntervalTimeUtil.check(AgoraConstant.INVITE_CHAT_INTERVAL)) {
            return false;
        }

        PToast.showShort(getString(R.string.chat_tips_invite_fast));
        return true;
    }

    private void initSayHi() {
        tv_sayhi.setText(getString(R.string.user_info_has_hi));
        iv_sayhello.setImageResource(R.drawable.f2_userinfo_hi_def);
        tv_sayhi.setTextColor(ContextCompat.getColor(this, R.color.color_989898));
        sayHibottom.setEnabled(false);
    }

    /**
     * 底部已打招呼处理
     */
    private void handleSayHi() {
        if (userDetail.isSayHello()) {
            PToast.showShort(getString(R.string.user_info_hi_repeat));
            return;
        }

        ModuleMgr.getChatMgr().sendSayHelloMsg(String.valueOf(userDetail.getUid()),
                getString(R.string.say_hello_txt),
                userDetail.getKf_id(),
                ModuleMgr.getCenterMgr().isRobot(userDetail.getKf_id()) ?
                        Constant.SAY_HELLO_TYPE_ONLY : Constant.SAY_HELLO_TYPE_SIMPLE, new IMProxy.SendCallBack() {
                    @Override
                    public void onResult(long msgId, boolean group, String groupId, long sender, String contents) {
                        MessageRet messageRet = new MessageRet();
                        messageRet.parseJson(contents);

                        if (messageRet.getS() == 0) { // 成功
                            PToast.showShort(getString(R.string.user_info_hi_suc));
                            MsgMgr.getInstance().sendMsg(MsgType.MT_Say_HI_Notice, userDetail.getUid());
                            userDetail.setSayHello(true);
                            initSayHi();
                        } else {
                            //170824 UPDATE START 打招呼提示使用消息体返回的错误信息
                            //PToast.showShort(getString(R.string.user_info_hi_fail));
                            if (!TextUtils.isEmpty(messageRet.getFailMsg())) {
                                PToast.showShort(messageRet.getFailMsg());
                            } else {
                                PToast.showShort(getString(R.string.user_info_hi_fail));
                            }
                            //170824 UPDATE END 打招呼提示使用消息体返回的错误信息
                        }
                    }

                    @Override
                    public void onSendFailed(NetData data) {
                        PToast.showShort(getString(R.string.user_info_hi_fail));
                    }
                });
    }

    /**
     * 滚动到底部
     */
    private void scrollToBottom() {
        TimerUtil.beginTime(new TimerUtil.CallBack() {
            @Override
            public void call() {
                scrollLayout.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 100);
    }

    @Override
    protected void onResume() {
        super.onResume();
        footPanel.refreshCar();
        if (channel == CenterConstant.USER_CHECK_CONNECT_OTHER)
            scrollToBottom();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MsgMgr.getInstance().detach(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 更新备注名
        if (requestCode == CenterConstant.USER_SET_RESULT_CODE) {
            switch (resultCode) {
                case CenterConstant.USER_SET_RESULT_CODE:
                    String remark = data.getStringExtra("remark");
                    userDetail.setRemark(remark);
                    setTitle(userDetail.getShowName());
                    footPanel.refreshView(userDetail);
                    break;
            }
        } else if (requestCode == REQUEST_CODE_UNLOCK_VIDEO && resultCode == RESULT_OK && data != null) {
            ArrayList<Long> unlockList = (ArrayList<Long>) data.getSerializableExtra(CenterConstant.USER_CHECK_UNLOCK_VIDEO_LIST_KEY);
            List<UserVideo> videoList = userDetail.getUserVideos();
            for (Long id : unlockList) {
                for (UserVideo video : videoList) {
                    if (video.getId() == id) {
                        video.setCanView();
                        break;
                    }
                }
            }
            //刷新私密视频
            footPanel.freshSecretVideo();
        }
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void onRequestComplete(HttpResponse response) {
        if (response.getUrlParam() == UrlParam.reqVideoChatConfig) {
            if (response.isOk()) {
                config = (VideoConfig) response.getBaseData();
                footPanel.refreshChatPrice(config);
                if (channel == CenterConstant.USER_CHECK_INFO_OWN) return;

                // 女号默认展示
                if (!ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
                    videoBottom.setVisibility(View.VISIBLE);
                    voiceBottom.setVisibility(View.VISIBLE);
                    return;
                }

                // 男号根据对方开启情况展示
                if (config.getVideoChat() == 1) {  // 展示发视频
                    videoBottom.setVisibility(View.VISIBLE);
                }

                if (config.getVoiceChat() == 1) {
                    voiceBottom.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onMessage(String key, Object value) {
        switch (key) {
            case MsgType.MT_MyInfo_Change:
                if (channel == CenterConstant.USER_CHECK_INFO_OWN) {
                    userDetail = ModuleMgr.getCenterMgr().getMyInfo();
                    footPanel.refreshView(userDetail);
                }
                break;
            default:
                break;
        }
    }

    public void sendVideo() {
        final UserInfoLightweight userInfoLightweight = new UserInfoLightweight(userDetail);
        userInfoLightweight.setDistance(userDetail.getDistance() > 5 ? getString(R.string.user_info_distance_far) :
                getString(R.string.user_info_distance_near));
        userInfoLightweight.setAge(userDetail.getAge());
        userInfoLightweight.setPrice(config != null ? config.getVideoPrice() : 20);

        SourcePoint.getInstance().lockSource(App.activity, "lookta");
        final String source = SourcePoint.getInstance().getSource();
        VideoAudioChatHelper.getInstance().peeragesHome(UserCheckInfoAct.this, userInfoLightweight.getUid(),
                AgoraConstant.RTC_CHAT_VIDEO,
                false, Constant.APPEAR_TYPE_NO, userInfoLightweight.getChannel_uid() + "", false, new VideoAudioChatHelper.VideoCardPeeragesHomeBackCall() {

                    @Override
                    public boolean showDiamondDlg() {
                        UIShow.showBottomChatDiamondDlg(UserCheckInfoAct.this, userInfoLightweight.getUid(),
                                AgoraConstant.RTC_CHAT_VIDEO, userInfoLightweight.getPrice(), false,
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
    }
}
