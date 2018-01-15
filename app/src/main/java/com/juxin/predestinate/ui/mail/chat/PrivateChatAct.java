package com.juxin.predestinate.ui.mail.chat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.juxin.library.log.PLogger;
import com.juxin.library.log.PSP;
import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.library.observe.PObserver;
import com.juxin.library.utils.InputUtils;
import com.juxin.library.view.roadlights.LMarqueeFactory;
import com.juxin.library.view.roadlights.LMarqueeView;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.StackNode;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.bean.config.CommonConfig;
import com.juxin.predestinate.bean.my.ChatInfo;
import com.juxin.predestinate.bean.my.MarqueeMessageList;
import com.juxin.predestinate.module.local.chat.ChatSpecialMgr;
import com.juxin.predestinate.module.local.chat.inter.ChatMsgInterface;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.chat.msgtype.ChumInviteMessage;
import com.juxin.predestinate.module.local.mail.MailSpecialID;
import com.juxin.predestinate.module.local.mail.MyChumTaskList;
import com.juxin.predestinate.module.local.msgview.ChatViewLayout;
import com.juxin.predestinate.module.local.msgview.chatview.ChatInterface;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.SourcePoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.ShareUtil;
import com.juxin.predestinate.module.util.TimerUtil;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.module.util.UIUtil;
import com.juxin.predestinate.module.util.my.GiftHelper;
import com.juxin.predestinate.ui.discover.SelectCallTypeDialog;
import com.juxin.predestinate.ui.mail.unlock.UnlockComm;
import com.juxin.predestinate.ui.mail.unlock.UnlockMsgDlg;
import com.juxin.predestinate.ui.mail.unlock.UnlockShareQQShareCallBack;
import com.juxin.predestinate.ui.user.my.CommonDlg.CloseFriendTaskView;
import com.juxin.predestinate.ui.user.my.view.GiftMessageInfoView;
import com.juxin.predestinate.ui.user.util.CenterConstant;
import com.juxin.predestinate.ui.user.util.OffNetPanel;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.Tencent;
import java.util.List;

/**
 * 聊天页
 * Created by Kind on 2017/3/23.
 */
public class PrivateChatAct extends BaseActivity implements View.OnClickListener, PObserver, ChatMsgInterface.WhisperMsgListener {

    private long whisperID = 0;
    private String name;
    private int kf_id;
    private String channel_uid = "";
    private ChatInfo chatInfo;
    private int triggerType = -1;//任务类型
    private ChatViewLayout privateChat = null;
    private ImageView cus_top_title_img, cus_top_title_task_num, inputAcceptChum, cus_top_img_phone;
    private TextView base_title_title, net_top_title, cus_top_title_txt, cusTopTvIntimacyLevel;
    private CloseFriendTaskView mCloseFriendTaskView;
    private LMarqueeView lmvMeassages;
    private LMarqueeFactory<RelativeLayout, MarqueeMessageList.MarqueeMessageInfo> marqueeView;

    private RelativeLayout llTitleRight;
    private OffNetPanel offNetPanel;
    private MyChumTaskList myChumTaskList;

    private boolean isB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanNotify(false);//设置该页面不弹出悬浮窗消息通知
        checkSingleState();
        setContentView(R.layout.p1_privatechatact);
        isB = getMyInfo().isB();
        whisperID = getIntent().getLongExtra("whisperID", 0);
        name = getIntent().getStringExtra("name");
        kf_id = getIntent().getIntExtra("kf_id", -1);
        triggerType = getIntent().getIntExtra("triggerType", -1);
        PSP.getInstance().put("whisperID", whisperID); //保存当前的whisperID
        PLogger.d("------>whisperID: " + whisperID + " , name: " + name + " , kf_id: " + kf_id);

        initView();
        MsgMgr.getInstance().attach(this);
        ChatSpecialMgr.getChatSpecialMgr().attachWhisperListener(this);

        ModuleMgr.getPhizMgr().reqCustomFace();

        checkIsCanSendMsg();

        if (triggerType != -1) {  //任务跳转
            UIShow.showPrivateChatAct(this, whisperID, true, triggerType);
        }
    }

    public void checkIsCanSendMsg() {
        UserDetail userDetail = getMyInfo();
        //缘分小秘书
        if (MailSpecialID.customerService.getSpecialID() == whisperID || MailSpecialID.shareMsg.getSpecialID() == whisperID) {
            privateChat.setInputGiftviewVisibility(View.GONE);
            boolean canEdit;
            if (isB) {
                canEdit = ModuleMgr.getCommonMgr().getCommonConfig().getCommon().canSecretaryShow()
                        || (userDetail.isVip() || userDetail.getDiamand() > 0);
            } else {
                canEdit = ModuleMgr.getCommonMgr().getCommonConfig().getCommon().canSecretaryShow()
                        || (userDetail.isVip() || userDetail.getYcoin() > 0 || userDetail.getDiamand() > 0);
            }
            if (canEdit) {
                privateChat.getChatAdapter().showIsCanChat(true);//输入框显示
            } else {
                privateChat.getChatAdapter().showInputGONE();//输入框不显示
            }
            return;
        }

        if (!userDetail.isMan() || ModuleMgr.getChatMgr().isChatShow()
                || (userDetail.getDiamand() > 0) || UnlockComm.getJueweiOk()
                || (UnlockComm.getShareNum() > 0) || (isB && userDetail.isVip())
                || (!isB && (userDetail.getYcoin() > 79))) {
            privateChat.getChatAdapter().showIsCanChat(true);
        } else {
            privateChat.getChatAdapter().showIsCanChat(false);
            InputUtils.HideKeyboard(privateChat);
        }
    }

    /**
     * @updateAuthor Mr.Huang
     * @updateDate 2017-07-14
     * 聊天窗口信息--整合接口
     */
    private void reqChatInfo() {
        privateChat.getChatAdapter().setTUID(whisperID);
        ModuleMgr.getCommonMgr().reqChatInfo(whisperID, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (!response.isOk()) {
                    PLogger.e("获取会话信息失败!");
                    return;
                }
                chatInfo = new ChatInfo();
                chatInfo.parseJson(response.getResponseString());
                privateChat.getChatAdapter().setChatInfo(chatInfo);

                getMyInfo().setDiamand(chatInfo.getDiamond());
                getMyInfo().setYcoin(chatInfo.getYcoin());

                if (myChumTaskList != null) {
                    myChumTaskList.setKnow(chatInfo.getKnow());
                }

                initNameId(chatInfo);
                //在线状态
                net_top_title.setText(chatInfo.getOtherInfo().getLast_online()
                        + (chatInfo.getOtherInfo().is_online() ?
                        "-" + chatInfo.getOtherInfo().netTp2Str(chatInfo.getOtherInfo().getNet_tp()) : ""));

                if (!isB) {
                    //音视频是否可见
                    if (MailSpecialID.customerService.getSpecialID() != whisperID && getMyInfo().isMan()) {
                        ChatInfo.OtherInfo.VideoConfig videoConfig = chatInfo.getOtherInfo().getVideoConfig();
                        if (videoConfig.isVideoChat()) {
                            privateChat.setInputLookAtHerVisibility(View.VISIBLE);
                        } else {
                            privateChat.setInputLookAtHerVisibility(View.GONE);
                        }
                    }
                }

                checkIsCanSendMsg();
            }
        });
    }

    private void initNameId(ChatInfo chatInfo) {
        kf_id = (int) chatInfo.getOtherInfo().getKf_id();
        channel_uid = String.valueOf(chatInfo.getOtherInfo().getChannel_uid());
        name = chatInfo.getOtherInfo().getNickname();
        setNickName(chatInfo.getOtherInfo().getShowName());
        String tempInfo = chatInfo.getOtherInfo().getShowName() + "-" + chatInfo.getOtherInfo().getAvatar() + "-" + chatInfo.getOtherInfo().getGender();
        PSP.getInstance().put(ModuleMgr.getCommonMgr().getPrivateKey(Constant.TEMP_CHAT_OTHER_INFO), tempInfo);
    }

    private void initLastGiftList() {
        if (MailSpecialID.customerService.getSpecialID() != whisperID)
            ModuleMgr.getCommonMgr().userBanner(30, new RequestComplete() {
                @Override
                public void onRequestComplete(final HttpResponse response) {
                    if (response.isOk() && lmvMeassages != null) {
                        lmvMeassages.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                MarqueeMessageList list = (MarqueeMessageList) response.getBaseData();
                                final List<MarqueeMessageList.MarqueeMessageInfo> lastGiftMessages = list.getGiftMessageList();
                                if (lmvMeassages != null && lastGiftMessages != null && !lastGiftMessages.isEmpty()) {
                                    lmvMeassages.setVisibility(View.VISIBLE);
                                    marqueeView.setData(lastGiftMessages);
                                    lmvMeassages.startFlipping();
                                }
                            }
                        }, 2500);
                    }
                }
            });
    }

    //初始化亲密度信息
    private void initFriendTask(final MyChumTaskList info) {
        ModuleMgr.getCommonMgr().getIntimateFriendTask(whisperID, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (response.isOk()) {
                    info.parseJson(response.getResponseString());
                    if (info.getCtime() > 0 && info.getLevel() > 0) {
                        if (privateChat.getChatAdapter().getChatInstance() != null && privateChat.getChatAdapter().getChatInstance().chatExtendPanel != null) {
                            privateChat.getChatAdapter().getChatInstance().chatExtendPanel.setChumLevle(info.getLevel());//设置亲密度等级
                        }
                        if (privateChat.getInputAcceptChum() != null) {
                            privateChat.getInputAcceptChum().setVisibility(View.GONE);
                        }
                        if (info.getTodayTaskStatus() == 1) {
                            cus_top_title_task_num.setVisibility(View.GONE);
                        } else {
                            cus_top_title_task_num.setVisibility(View.VISIBLE);
                        }
                        mCloseFriendTaskView.setVisibility(View.VISIBLE);
                        cusTopTvIntimacyLevel.setText("Lv" + info.getLevel());
                        if (chatInfo != null) {
                            myChumTaskList.setKnow(chatInfo.getKnow());
                        }
                        mCloseFriendTaskView.setData(info, whisperID, channel_uid);
                    } else {
                        mCloseFriendTaskView.setVisibility(View.INVISIBLE);
                    }
                } else {
                    mCloseFriendTaskView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    /**
     * 初始化标题
     */
    private void onTitleInit() {
        setBackView(R.id.base_title_back);
        setIsBottomLineShow(false);
        View baseTitleView = LayoutInflater.from(this).inflate(R.layout.f1_privatechatact_titleview, null);
        View baseTitleViewRight = LayoutInflater.from(this).inflate(R.layout.f1_privatechatact_titleview_right, null);
        setTitleCenterContainer(baseTitleView);
        setTitleRightContainer(baseTitleViewRight);
        llTitleRight = (RelativeLayout) baseTitleView.findViewById(R.id.cus_top_title_ll);
        base_title_title = (TextView) baseTitleView.findViewById(R.id.cus_top_title);
        net_top_title = (TextView) baseTitleView.findViewById(R.id.net_top_title);
        cus_top_title_txt = (TextView) baseTitleView.findViewById(R.id.cus_top_title_txt);
        cus_top_title_img = (ImageView) baseTitleView.findViewById(R.id.cus_top_title_img);
        cus_top_title_task_num = (ImageView) baseTitleViewRight.findViewById(R.id.cus_top_title_task_num);
        cus_top_img_phone = (ImageView) baseTitleViewRight.findViewById(R.id.cus_top_title_img_phone);
        cusTopTvIntimacyLevel = (TextView) baseTitleViewRight.findViewById(R.id.cus_top_title_tv_intimacy_level);
        cus_top_img_phone.setOnClickListener(this);

        setNickName(name);
        if (MailSpecialID.customerService.getSpecialID() == whisperID) {//缘分小秘书
            setTitleRight("活动", R.color.color_FE799A, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UIShow.showSysMessActivity(PrivateChatAct.this);
                    Statistics.userBehavior(SendPoint.page_xiaomishu_huodong);
                }
            });
        } else {
            if (MailSpecialID.shareMsg.getSpecialID() != whisperID) {
                setTitleRightImg(R.drawable.f2_user_ico, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UIShow.showUserOtherSetAct(PrivateChatAct.this, whisperID, null, CenterConstant.USER_SET_FROM_CHAT);
                    }
                });
            }
        }
    }

    private void setNickName(String nickName) {
        String str = whisperID + "";

        if (whisperID == MailSpecialID.customerService.getSpecialID()) {
            str = MailSpecialID.customerService.getSpecialIDName();
        }

        if (!TextUtils.isEmpty(nickName)) {
            str = nickName;
        }

        if (base_title_title != null) {
            base_title_title.setText(str.length() > 10 ? (str.substring(0, 10)) : (str));
        }
    }

    private void initView() {
        onTitleInit();

        RelativeLayout netStatusLayout = (RelativeLayout) findViewById(R.id.netStatusLayout);
        offNetPanel = new OffNetPanel(this, netStatusLayout);
        netStatusLayout.addView(offNetPanel);

        privateChat = (ChatViewLayout) findViewById(R.id.privatechat_view);
        inputAcceptChum = privateChat.getInputAcceptChum();
        privateChat.getChatAdapter().setWhisperId(whisperID);
        lmvMeassages = (LMarqueeView) findViewById(R.id.privatechat_lmv_messages);
        mCloseFriendTaskView = (CloseFriendTaskView) findViewById(R.id.privatechat_friend_task);
        marqueeView = new GiftMessageInfoView(this);
        lmvMeassages.setTranslationX(UIUtil.dip2px(this, -getWindowManager().getDefaultDisplay().getWidth()));
        lmvMeassages.setInterval(5000);
        lmvMeassages.setAnimInAndOut(R.anim.bottom_right_in, R.anim.bottom_left_out);
        lmvMeassages.setMarqueeFactory(marqueeView);
        lmvMeassages.getAnimIn().setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                lmvMeassages.setTranslationX(0);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        initLastGiftList();
        privateChat.getChatAdapter().setOnUserInfoListener(new ChatInterface.OnUserInfoListener() {
            @Override
            public void onComplete(UserInfoLightweight infoLightweight) {
                if (infoLightweight != null && whisperID == infoLightweight.getUid()) {
                    setNickName(infoLightweight.getShowName());
                    if (infoLightweight.getGender() == 1) {//是男的显示豪,显示头布局
                        cus_top_title_img.setImageResource(R.drawable.f1_top02);
                    }
                    if (infoLightweight.isToper()) {//Top榜
                        llTitleRight.setVisibility(View.VISIBLE);
                        cus_top_title_txt.setText("Top" + infoLightweight.getTop());
                    }

                    kf_id = infoLightweight.getKf_id();
                    if (kf_id != 0 || MailSpecialID.customerService.getSpecialID() == whisperID) {
                        privateChat.getChatAdapter().onDataUpdate();
                    }
                    SourcePoint.getInstance().lockPPCSource(infoLightweight.getUid(), infoLightweight.getChannel_uid()); //ppc统计
                }
            }
        });

        if (MailSpecialID.customerService.getSpecialID() != whisperID) {
            privateChat.isShowRedBagTip();
            isShowTopPhone();
            if (myChumTaskList == null) {
                myChumTaskList = new MyChumTaskList();
            }
            initFriendTask(myChumTaskList);
        }

        //状态栏 + 标题 +（关注TA、查看手机）// （去掉滚动条高度） 高度
        PSP.getInstance().put(Constant.PRIVATE_CHAT_TOP_H, UIUtil.getViewHeight(getTitleView()) + UIUtil.getStatusHeight(this));
    }

    private void isShowTopPhone() {
        ModuleMgr.getChatMgr().getNetSingleProfile(whisperID, new ChatMsgInterface.InfoComplete() {
            @Override
            public void onReqComplete(boolean ret, UserInfoLightweight infoLightweight) {
                if (MailSpecialID.customerService.getSpecialID() != whisperID && infoLightweight.getGender() == 2 &&
                        (infoLightweight.isVideo_available() || infoLightweight.isAudio_available())) {//女性用户显示可通话图标
                    cus_top_img_phone.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cus_top_title_img_phone://音视频
                if (ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
                    new SelectCallTypeDialog(this, whisperID, channel_uid);
                } else {
                    SourcePoint.getInstance().lockSource(App.activity, "_");
                    UIShow.showLookAtHerDlg(this, whisperID, channel_uid, true, false, 0, SourcePoint.getInstance().getSource());
                }
                break;
            case R.id.cus_top_title_rl_intimacy: //亲密度的展示与隐藏
                if (mCloseFriendTaskView.getVisibility() == View.VISIBLE) {
                    mCloseFriendTaskView.setVisibility(View.GONE);
                } else if (mCloseFriendTaskView.getVisibility() == View.GONE || mCloseFriendTaskView.getVisibility() == View.INVISIBLE) {
                    mCloseFriendTaskView.setVisibility(View.VISIBLE);
                    mCloseFriendTaskView.hideTask();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 更新备注名
        if (requestCode == CenterConstant.USER_SET_REQUEST_CODE && resultCode == CenterConstant.USER_SET_RESULT_CODE) {
            String remark = data.getStringExtra("remark");
            setNickName(TextUtils.isEmpty(remark) ? name : remark);
        }
        if (requestCode == Constants.REQUEST_QQ_SHARE || requestCode == Constants.REQUEST_QZONE_SHARE) {
            UnlockShareQQShareCallBack callBack = new UnlockShareQQShareCallBack();
            callBack.setChannel(requestCode == Constants.REQUEST_QQ_SHARE ? ShareUtil.CHANNEL_QQ_FRIEND : ShareUtil.CHANNEL_QQ_ZONE);
            Tencent.onActivityResultData(requestCode, resultCode, data, callBack);
        }
    }

    @Override
    public void onMessage(String key, Object value) {
        switch (key) {
            case MsgType.MT_Chat_Clear_History:
                long tmpID = (long) value;
                if (privateChat != null && whisperID == tmpID) {
                    privateChat.getChatAdapter().clearHistory();
                }
                break;

            case MsgType.MT_Chat_Can:
                checkIsCanSendMsg();
                break;

            case MsgType.MT_MyInfo_Change://更新个人资料
                if (getMyInfo().isMan() && getMyInfo().isVip()) {//男
                    if (isB) {
                        ModuleMgr.getCenterMgr().reqMyInfo();
                    } else {
                        privateChat.getChatAdapter().showIsCanChat(true);
                    }
                }
                break;

            case MsgType.MT_Update_Ycoin:
                if ((Boolean) value) {//去请求网络
                    reqChatInfo();
                } else {//不请求网络
                    if (!isB) {
                        checkIsCanSendMsg();
                    }
                }
                break;
            case MsgType.MESSAGE_UNLOCK://解锁类型 1 分享解锁 2.钻石消费解锁 3.爵位解锁
                //     Msg msg = (Msg) value;
                //    if("2".equals(msg.getKey())){//解锁类型 1 分享解锁 2.钻石消费解锁 3.爵位解锁
                //   checkIsCanSendMsg();
                //    }
                checkIsCanSendMsg();
                break;
            case MsgType.MT_Update_Diamond://钻石更新
                int diamondCost = (int) value;
                checkIsCanSendMsg();
                showDiamond();
                break;
            case MsgType.MT_Update_TASK://  密友状态更新
                if (myChumTaskList == null) {
                    myChumTaskList = new MyChumTaskList();
                }
                initFriendTask(myChumTaskList);
                break;
            case MsgType.MT_ADD_CHUM_INFORM:
                if ((boolean) value) {
                    chatInfo.getOtherInfo().setIsIntimateFriend(1);
                    privateChat.setInputAcceptChumImageResource(setInputChumImgWait());
                } else {
                    PToast.showShort("请求失败！");
                }
                break;
            case MsgType.MT_REMOVE_CHUM_INFORM: //密友任务解除
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCloseFriendTaskView.setVisibility(View.INVISIBLE);
                        chatInfo.getOtherInfo().setIsIntimateFriend(0);
                        privateChat.setInputAcceptChumImageResource(getMyInfo().isMan()
                                ? setInputChumImgShoufuta() : setInputChumImgFriend());
                    }
                });
                break;
            case MsgType.MT_GET_BAG_TIP:
                if (privateChat != null) {
                    privateChat.isShowRedBagTip();
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (lmvMeassages != null) {
            lmvMeassages.clearAnimation();
            lmvMeassages.stopFlipping();
        }
        ChatSpecialMgr.getChatSpecialMgr().detachWhisperListener(this);
        MsgMgr.getInstance().detach(this);
        privateChat.getChatAdapter().detach();
        lastActivity = null;
    }

    private static BaseActivity lastActivity = null;

    private void checkSingleState() {
        if (lastActivity != null) {
            try {
                lastActivity.finish();
            } catch (Exception e) {
                PLogger.printThrowable(e);
            }
        }
        lastActivity = this;
    }

    @Override
    protected void onResume() {
        GiftHelper.setBigDialogIsCanShow(true);
        super.onResume();
        reqChatInfo();
    }

    /**
     * @author Mr.Huang
     * @date 2017-07-18
     * 改变解锁后输入区的视图
     */
    public void changeEditMessage(boolean isGetRedPackage) {
        if (privateChat == null) {
            return;
        }
        privateChat.getChatAdapter().setUnlocked(true);
        privateChat.getChatAdapter().showIsCanChat(true);
        if (isGetRedPackage) {
            privateChat.getChatAdapter().showGetRedPackageTip();
        }
    }


    private void showDiamond() {
        if (privateChat != null) {
            handlerStop.removeMessages(1);
            handlerStop.sendEmptyMessageDelayed(1, 1500);
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler handlerStop = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (privateChat == null) {
                        return;
                    }
                    final ImageView imageView = privateChat.getchatSpreadDiamond();
                    imageView.setVisibility(View.VISIBLE);
                    TimerUtil.beginTime(new TimerUtil.CallBack() {
                        @Override
                        public void call() {
                            imageView.setVisibility(View.GONE);
                        }
                    }, 1500);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onUpdateWhisper(BaseMessage message) {
        if (whisperID == message.getLWhisperID() && message.getType() == BaseMessage.BaseMessageType.chumInvite.getMsgType()
                && message instanceof ChumInviteMessage) {
            ChumInviteMessage inviteMessage = (ChumInviteMessage) message;
            if (inputAcceptChum == null || chatInfo == null) return;
            switch (inviteMessage.getClose_tp()) {//密友消息类型1邀请密友2接受邀请3拒绝邀请
                case 2:
                    inputAcceptChum.setVisibility(View.GONE);
                    break;
                case 3:
                    chatInfo.getOtherInfo().setIsIntimateFriend();
                    privateChat.setInputAcceptChumImageResource(getMyInfo().isMan()
                            ? setInputChumImgShoufuta() : setInputChumImgFriend());
                    break;
                default:
                    chatInfo.getOtherInfo().setIsIntimateFriend(1);
                    privateChat.setInputAcceptChumImageResource(setInputChumImgWait());
                    break;
            }
        }
    }

    /**
     * 等待
     *
     * @return
     */
    private int setInputChumImgWait() {
        return R.drawable.f2_icon_friend_wait;
    }

    /**
     * 女--加密友
     *
     * @return
     */
    private int setInputChumImgFriend() {
        return R.drawable.f2_icon_friend;
    }

    /**
     * 男--收服她
     *
     * @return
     */
    private int setInputChumImgShoufuta() {
        return R.drawable.f2_icon_shoufuta;
    }

    private UserDetail getMyInfo() {
        return ModuleMgr.getCenterMgr().getMyInfo();
    }

    @Override
    protected void onStop() {
        GiftHelper.setBigDialogIsCanShow(false);
        super.onStop();
    }

    @Override
    public StackNode getStackNode() {
        StackNode node = super.getStackNode();
        if(node.stack.equals(UnlockMsgDlg.class.getName())){
            node = node.previous;
        }
        return node;
    }
}