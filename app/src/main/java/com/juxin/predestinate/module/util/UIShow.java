package com.juxin.predestinate.module.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.WindowManager;

import com.juxin.library.log.PLogger;
import com.juxin.library.log.PSP;
import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.library.utils.APKUtil;
import com.juxin.library.utils.NetworkUtils;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.StackNode;
import com.juxin.predestinate.bean.center.update.AppUpdate;
import com.juxin.predestinate.bean.center.update.UpgradeSource;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.bean.center.user.detail.UserVideo;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.bean.config.base.Action;
import com.juxin.predestinate.bean.config.base.NobilityList;
import com.juxin.predestinate.bean.live.RoomPayInfo;
import com.juxin.predestinate.bean.my.BigGiftInfo;
import com.juxin.predestinate.bean.my.GiftsList;
import com.juxin.predestinate.bean.my.IdCardVerifyStatusInfo;
import com.juxin.predestinate.bean.my.MultiTitle;
import com.juxin.predestinate.bean.my.ShareTypeData;
import com.juxin.predestinate.bean.my.WithdrawAddressInfo;
import com.juxin.predestinate.bean.net.BaseData;
import com.juxin.predestinate.module.local.center.CenterMgr;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.chat.msgtype.PrivateMessage;
import com.juxin.predestinate.module.local.mail.MyChum;
import com.juxin.predestinate.module.local.mail.MyChumTaskList;
import com.juxin.predestinate.module.local.msgview.chatview.input.ChatMediaPlayer;
import com.juxin.predestinate.module.local.msgview.chatview.msgpanel.ChatPanelInvite;
import com.juxin.predestinate.module.local.pay.goods.PayGood;
import com.juxin.predestinate.module.local.statistics.PPCSource;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.SourcePoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.local.statistics.StatisticsShareUnlock;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.baseui.WebActivity;
import com.juxin.predestinate.module.logic.baseui.custom.ShareDlg;
import com.juxin.predestinate.module.logic.baseui.custom.SimpleTipDialog;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.config.FinalKey;
import com.juxin.predestinate.module.logic.config.Hosts;
import com.juxin.predestinate.module.logic.notify.view.LockScreenActivity;
import com.juxin.predestinate.module.logic.notify.view.UserMailNotifyAct;
import com.juxin.predestinate.module.logic.request.HTCallBack;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.my.AttentionUtil;
import com.juxin.predestinate.module.util.my.GiftHelper;
import com.juxin.predestinate.ui.agora.act.bean.RtcComment;
import com.juxin.predestinate.ui.agora.act.comment.RtcCommentActivity;
import com.juxin.predestinate.ui.agora.model.AgoraConstant;
import com.juxin.predestinate.ui.agora.util.VideoAudioChatHelper;
import com.juxin.predestinate.ui.discover.ActiveDlg;
import com.juxin.predestinate.ui.discover.ChatInviteAct;
import com.juxin.predestinate.ui.discover.ChatSecretAct;
import com.juxin.predestinate.ui.discover.ChatStrangerAct;
import com.juxin.predestinate.ui.discover.DefriendAct;
import com.juxin.predestinate.ui.discover.MyDefriendAct;
import com.juxin.predestinate.ui.discover.MyFriendsAct;
import com.juxin.predestinate.ui.discover.SayHelloUserAct;
import com.juxin.predestinate.ui.discover.SureLiveRoomDlg;
import com.juxin.predestinate.ui.discover.SureUserDlg;
import com.juxin.predestinate.ui.discover.UserAvatarUploadAct;
import com.juxin.predestinate.ui.discover.task.ChatGiftAct;
import com.juxin.predestinate.ui.discover.task.JoinGuildDialog;
import com.juxin.predestinate.ui.discover.task.MakeMoneyNewTipDialog;
import com.juxin.predestinate.ui.discover.task.UserAuthDialog;
import com.juxin.predestinate.ui.friend.PeerageSayHelloActivity;
import com.juxin.predestinate.ui.live.GirlLiveEndActivity;
import com.juxin.predestinate.ui.live.LiveSearchActivity;
import com.juxin.predestinate.ui.live.LiveStartLiveTipActivity;
import com.juxin.predestinate.ui.live.bean.LiveStartLiveTipDetailList;
import com.juxin.predestinate.ui.live.bean.LiveUserDetail;
import com.juxin.predestinate.ui.live.callback.OnSendGiftCallbackListener;
import com.juxin.predestinate.ui.live.ui.LiveBaseDialog;
import com.juxin.predestinate.ui.live.ui.LiveConsumeListDialog;
import com.juxin.predestinate.ui.live.ui.LiveReportDialog;
import com.juxin.predestinate.ui.live.ui.LiveUserCardDialog;
import com.juxin.predestinate.ui.live.view.AddFriendGiftDialog;
import com.juxin.predestinate.ui.mail.FriendTaskDlg;
import com.juxin.predestinate.ui.mail.SysMessAct;
import com.juxin.predestinate.ui.mail.chat.OffNetDescribeAct;
import com.juxin.predestinate.ui.mail.chat.PrivateChatAct;
import com.juxin.predestinate.ui.mail.popup.RandomRedBoxActivity;
import com.juxin.predestinate.ui.mail.unlock.UnlockMsgDlg;
import com.juxin.predestinate.ui.mail.unlock.UnlockMsgResultDlg;
import com.juxin.predestinate.ui.main.MainActivity;
import com.juxin.predestinate.ui.pay.PayListAct;
import com.juxin.predestinate.ui.pay.PayOrderAct;
import com.juxin.predestinate.ui.pay.PayRefreshDlg;
import com.juxin.predestinate.ui.pay.PayResultDlg;
import com.juxin.predestinate.ui.pay.PayWebAct;
import com.juxin.predestinate.ui.pay.utils.PayAlipayUtils;
import com.juxin.predestinate.ui.pay.utils.PayWeixinUtils;
import com.juxin.predestinate.ui.pay.wepayother.h5.PayWebActivity;
import com.juxin.predestinate.ui.pay.wepayother.qrcode.OpenWxDialog;
import com.juxin.predestinate.ui.pay.wepayother.qrcode.WepayQRCodeAct;
import com.juxin.predestinate.ui.push.WebPushDialog;
import com.juxin.predestinate.ui.setting.AboutAct;
import com.juxin.predestinate.ui.setting.BottomBannedDialog;
import com.juxin.predestinate.ui.setting.SearchTestActivity;
import com.juxin.predestinate.ui.setting.SettingAct;
import com.juxin.predestinate.ui.setting.SuggestAct;
import com.juxin.predestinate.ui.setting.UserModifyPwdAct;
import com.juxin.predestinate.ui.share.QQShareActivity;
import com.juxin.predestinate.ui.share.QQShareCallBack;
import com.juxin.predestinate.ui.share.ScreenShot;
import com.juxin.predestinate.ui.share.ShareDialog;
import com.juxin.predestinate.ui.share.ShareOtherInfo;
import com.juxin.predestinate.ui.share.ShareSuccessDialog;
import com.juxin.predestinate.ui.start.FindPwdAct;
import com.juxin.predestinate.ui.start.NavUserAct;
import com.juxin.predestinate.ui.start.UserLoginExtAct;
import com.juxin.predestinate.ui.start.UserRegInfoAct;
import com.juxin.predestinate.ui.start.UserRegInfoCompleteAct;
import com.juxin.predestinate.ui.tips.PeerageGetVideoCardDialog;
import com.juxin.predestinate.ui.user.auth.IDCardAuthenticationAct;
import com.juxin.predestinate.ui.user.auth.IDCardAuthenticationSucceedAct;
import com.juxin.predestinate.ui.user.auth.MyAuthenticationAct;
import com.juxin.predestinate.ui.user.auth.MyAuthenticationVideoAct;
import com.juxin.predestinate.ui.user.auth.PhoneVerifyAct;
import com.juxin.predestinate.ui.user.auth.PhoneVerifyCompleteAct;
import com.juxin.predestinate.ui.user.auth.RecordVideoAct;
import com.juxin.predestinate.ui.user.check.UserCheckInfoAct;
import com.juxin.predestinate.ui.user.check.bean.VideoConfig;
import com.juxin.predestinate.ui.user.check.edit.EditContentAct;
import com.juxin.predestinate.ui.user.check.edit.UserEditSignAct;
import com.juxin.predestinate.ui.user.check.edit.info.UserEditInfoAct;
import com.juxin.predestinate.ui.user.check.other.UserBlockAct;
import com.juxin.predestinate.ui.user.check.other.UserOtherLabelAct;
import com.juxin.predestinate.ui.user.check.other.UserOtherSetAct;
import com.juxin.predestinate.ui.user.check.secret.UserSecretAct;
import com.juxin.predestinate.ui.user.check.secret.dialog.SecretDiamondDlg;
import com.juxin.predestinate.ui.user.check.secret.dialog.SecretGiftDlg;
import com.juxin.predestinate.ui.user.check.secret.dialog.SecretVideoPlayerAct;
import com.juxin.predestinate.ui.user.check.self.album.UserPhotoAct;
import com.juxin.predestinate.ui.user.my.AskForGiftDialog;
import com.juxin.predestinate.ui.user.my.AskforPicGiftDlg;
import com.juxin.predestinate.ui.user.my.AskforVideoGiftDlg;
import com.juxin.predestinate.ui.user.my.BottomGiftDialog;
import com.juxin.predestinate.ui.user.my.CatchGoddessAct;
import com.juxin.predestinate.ui.user.my.CatchGoddessInfoAct;
import com.juxin.predestinate.ui.user.my.CloseBalanceDlg;
import com.juxin.predestinate.ui.user.my.CommonDlg.AccumulatedSystemDlg;
import com.juxin.predestinate.ui.user.my.CommonDlg.BigGiftDlg;
import com.juxin.predestinate.ui.user.my.CommonDlg.BusyRandomDlg;
import com.juxin.predestinate.ui.user.my.CommonDlg.CloseFriendUpgradeDlg;
import com.juxin.predestinate.ui.user.my.CommonDlg.RemoveCloseFriendDlg;
import com.juxin.predestinate.ui.user.my.CommonDlg.TitleUpgradeDlg;
import com.juxin.predestinate.ui.user.my.CommonDlg.UpgradeGiftSendDlg;
import com.juxin.predestinate.ui.user.my.DiamondOpenVideoDlg;
import com.juxin.predestinate.ui.user.my.DiamondSendGiftDlg;
import com.juxin.predestinate.ui.user.my.EarnMoneyAct;
import com.juxin.predestinate.ui.user.my.GetRedBagDlg;
import com.juxin.predestinate.ui.user.my.IntimateFriendExplainAct;
import com.juxin.predestinate.ui.user.my.InvitationExpiredDlg;
import com.juxin.predestinate.ui.user.my.LiveCardRedBagDlg;
import com.juxin.predestinate.ui.user.my.LookAtHerDlg;
import com.juxin.predestinate.ui.user.my.LookPrivateDlg;
import com.juxin.predestinate.ui.user.my.MultiTitleAct;
import com.juxin.predestinate.ui.user.my.MyAttentionAct;
import com.juxin.predestinate.ui.user.my.MyDiamondsAct;
import com.juxin.predestinate.ui.user.my.MyDiamondsExplainAct;
import com.juxin.predestinate.ui.user.my.OpenVaDlg;
import com.juxin.predestinate.ui.user.my.PhotoFeelDlg;
import com.juxin.predestinate.ui.user.my.RankAct;
import com.juxin.predestinate.ui.user.my.RedBoxPhoneVerifyAct;
import com.juxin.predestinate.ui.user.my.RedBoxRecordAct;
import com.juxin.predestinate.ui.user.my.RotarySetActivity;
import com.juxin.predestinate.ui.user.my.ShareCodeAct;
import com.juxin.predestinate.ui.user.my.ShareMakeMoneyDlg;
import com.juxin.predestinate.ui.user.my.ShareRewardDlg;
import com.juxin.predestinate.ui.user.my.TitlePrivilegeAct;
import com.juxin.predestinate.ui.user.my.WantMoneyDlg;
import com.juxin.predestinate.ui.user.my.WithDrawApplyAct;
import com.juxin.predestinate.ui.user.my.WithDrawExplainAct;
import com.juxin.predestinate.ui.user.my.WithDrawRecordAct;
import com.juxin.predestinate.ui.user.my.WithDrawSuccessAct;
import com.juxin.predestinate.ui.user.my.WomanAutoReplyAct;
import com.juxin.predestinate.ui.user.paygoods.GoodsConstant;
import com.juxin.predestinate.ui.user.paygoods.diamond.BottomChatDiamondDlg;
import com.juxin.predestinate.ui.user.paygoods.diamond.GoodsDiamondBottomDlg;
import com.juxin.predestinate.ui.user.paygoods.diamond.GoodsDiamondDialog;
import com.juxin.predestinate.ui.user.paygoods.vip.GoodsVipBottomDlg;
import com.juxin.predestinate.ui.user.paygoods.vip.GoodsVipCallingDialog;
import com.juxin.predestinate.ui.user.paygoods.vip.OpenVipDialogNew;
import com.juxin.predestinate.ui.user.paygoods.ycoin.GoodsYCoinBottomDlg;
import com.juxin.predestinate.ui.user.update.UpdateDialog;
import com.juxin.predestinate.ui.user.util.CenterConstant;
import com.juxin.predestinate.ui.utils.PhotoDisplayAct;
import com.juxin.predestinate.ui.utils.PrivatePhotoDisplayAct;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.juxin.predestinate.module.logic.application.App.context;

/**
 * 应用内页面跳转工具
 * Created by ZRP on 2016/12/9.
 */
public class UIShow {

    // ----------------------------activity跳转码------------------------------

    // ---------------------------应用内弹出及跳转------------------------------

    public static void show(Context context, Intent intent) {
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
    }

    public static void show(Context context, Class clz, int flag) {
        Intent intent = new Intent(context, clz);
        if (flag != -1) intent.addFlags(flag);
        show(context, intent);
    }

    public static void show(Context context, Class clz) {
        show(context, clz, -1);
    }

    /**
     * 显示activity并清空栈里其他activity
     *
     * @param activity 要启动的activity
     */
    public static void showActivityClearTask(Context context, Class activity) {
        show(context, activity, Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    /**
     * 调用改方法启动activity，可将应用从后台唤起到前台，
     */
    public static void showActivityFromBack(Class activity) {
        show(App.context, activity, Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    /**
     * 跳转到主页并清除栈里的其他页面
     */
    public static void showMainClearTask(Context context) {
        showActivityClearTask(context, MainActivity.class);
    }

    /**
     * 模拟软件退出[携带特殊的intent参数跳转到首页]
     *
     * @param context 上下文
     */
    public static void simulateExit(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(FinalKey.SIMULATE_EXIT_KEY, FinalKey.SIMULATE_EXIT_KEY);
        show(context, intent);
    }

    /**
     * 跳转到首页的指定tab，并传值，通过MainActivity的OnNewIntent接收
     *
     * @param tabType 指定的跳转tab
     * @param dataMap 跳转时通过intent传递的map值
     */
    public static void showMainWithTabData(Context context, int tabType, Map<String, String> dataMap) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(FinalKey.HOME_TAB_TYPE, tabType);
        if (dataMap != null)
            for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                intent.putExtra(entry.getKey(), entry.getValue());
            }
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
    }

    /**
     * 应用外消息提示点击跳转到信箱fragment
     *
     * @param context 上下文
     */
    public static void showMainWithBackMessage(Context context) {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mainIntent.setClass(context, MainActivity.class);
        mainIntent.putExtra(FinalKey.HOME_TAB_TYPE, FinalKey.MAIN_TAB_2);//消息列表
        StackNode.appendIntent(context, mainIntent);
        context.startActivity(mainIntent);
    }

    /**
     * 跳转到网页
     *
     * @param type     1-正常导航条，2-全屏页面（全屏时显示loading条），3-带分享按钮导航条
     * @param url      网页地址
     * @param gameName 游戏名 catch_girl
     */
    public static void showWebActivity(Context context, int type, String url, String gameName) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("url", url);
        intent.putExtra("type", type);
        intent.putExtra("gameName", gameName);
        show(context, intent);
    }

    /**
     * 跳转到网页，默认为正常带导航条样式
     *
     * @param url 网页地址
     */
    public static void showWebActivity(Context context, String url) {
        showWebActivity(context, 1, url, null);
    }

    /**
     * 带有分享类型
     *
     * @param url           网页地址
     * @param shareTypeData 分享类型数据
     */
    public static void showWebActivity(Context context, String url, ShareTypeData shareTypeData) {
        showWebActivity(context, 1, url, null, shareTypeData);
    }

    /**
     * 带有分享类型
     *
     * @param type          1-正常导航条，2-全屏页面（全屏时显示loading条），3-带分享按钮导航条
     * @param url           网页地址
     * @param gameName      游戏名 catch_girl
     * @param shareTypeData 分享类型数据
     */
    public static void showWebActivity(Context context, int type, String url, String gameName, ShareTypeData shareTypeData) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("url", url);
        intent.putExtra("type", type);
        intent.putExtra("gameName", gameName);
        intent.putExtra("shareTypeData", shareTypeData);
        show(context, intent);
    }

    /**
     * 跳转到系统设置面面
     *
     * @param context
     */
    public static void showNetworkSettings(Context context) {
        Intent intent = null;
        //判断手机系统的版本  即API大于10 就是3.0或以上版本
        if (android.os.Build.VERSION.SDK_INT > 10) {
            intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            intent = new Intent();
            ComponentName component = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
            intent.setComponent(component);
            intent.setAction("android.intent.action.VIEW");
        }
        context.startActivity(intent);
    }

    /**
     * 打开导航页
     */
    public static void showNavUserAct(FragmentActivity activity) {
        Intent intent = new Intent(activity, NavUserAct.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        StackNode.appendIntent(activity, intent);
        activity.startActivity(intent);
    }

    /**
     * 打开登录页
     */
    public static void showUserLoginExtAct(FragmentActivity activity) {
        Intent intent = new Intent(activity, UserLoginExtAct.class);
        StackNode.appendIntent(activity, intent);
        activity.startActivity(intent);
    }

    /**
     * 打开注册页
     */
    public static void showUserRegInfoAct(FragmentActivity activity) {
        Intent intent = new Intent(activity, UserRegInfoAct.class);
        StackNode.appendIntent(activity, intent);
        activity.startActivity(intent);
    }

    /**
     * 打开资料完善页
     *
     * @param gender 性别
     */
    public static void showUserInfoCompleteAct(Context activity, int gender) {
        Intent intent = new Intent(activity, UserRegInfoCompleteAct.class);
        intent.putExtra("gender", gender);
        StackNode.appendIntent(activity, intent);
        activity.startActivity(intent);
    }

    /**
     * 手机绑定
     */
    public static void showPhoneVerifyAct(final FragmentActivity activity, final int requestCode) {
        Intent intent = new Intent(activity, PhoneVerifyAct.class);
        StackNode.appendIntent(activity, intent);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 打开意见反馈页面
     *
     * @param activity
     */
    public static void showSuggestAct(FragmentActivity activity) {
        Intent intent = new Intent(activity, SuggestAct.class);
        StackNode.appendIntent(activity, intent);
        activity.startActivity(intent);
    }

    //============================== 小友模块相关跳转 =============================

    /**
     * 打开设置页
     */
    public static void showUserSetAct(final Activity context, final int resultCode) {
        Intent intent = new Intent(context, SettingAct.class);
        StackNode.appendIntent(context, intent);
        context.startActivityForResult(intent, resultCode);
    }

    /**
     * 打开关于页面
     */
    public static void showAboutAct(Context context) {
        if (CommonUtil.isPurePackage()) {
            showWebActivity(context, WebUtil.jointUrl(Hosts.H5_APP_ABOUT));
        } else {
            context.startActivity(new Intent(context, AboutAct.class));
        }
    }

    /**
     * 打开关于页面
     */
    public static void showLiveStartTipAct(Context context, LiveStartLiveTipDetailList list) {
        Intent intent = new Intent(context, LiveStartLiveTipActivity.class);
        intent.putExtra("list", list);
        context.startActivity(intent);
    }

    /**
     * 打开主播管理规范
     */
    public static void showAnchorManage(Context context) {
        showWebActivity(context, WebUtil.jointUrl(Hosts.H5_APP_ANCHOR_MGR));
    }

    /**
     * 打开平台规范
     */
    public static void showPlatformSpecific(Context context) {
        showWebActivity(context, WebUtil.jointUrl(Hosts.H5_APP_PLATFORN_MGR));
    }

    /**
     * 打开隐私政策
     */
    public static void showPrivacyPolicy(Context context) {
        showWebActivity(context, WebUtil.jointUrl(Hosts.H5_APP_PRIVACY_POLICY));
    }

    /**
     * 打开用户注册协议
     */
    public static void showRegisterAgree(Context context) {
        showWebActivity(context, WebUtil.jointUrl(Hosts.H5_APP_REGISTER_AGREE));
    }

    /**
     * 打开交友注意事项
     */
    public static void showAppTips(Context context) {
        showWebActivity(context, WebUtil.jointUrl(Hosts.H5_APP_TIPS));
    }

    /**
     * 打开用户搜索测试彩蛋页面
     */
    public static void showSearchTestActivity(final Activity context) {
        Intent intent = new Intent(context, SearchTestActivity.class);
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
    }

    /**
     * 打开修改密码页面
     */
    public static void showModifyAct(final Activity context) {
        Intent intent = new Intent(context, UserModifyPwdAct.class);
        StackNode.appendIntent(context, intent);
        context.startActivityForResult(intent, 100);
    }

    /**
     * 打开个人信息页
     */
    public static void showUserPhotoAct(Context context) {
        Intent intent = new Intent(context, UserPhotoAct.class);
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
    }

    /**
     * 打开个人信息查看编辑页
     */
    public static void showUserEditInfoAct(Context context) {
        Intent intent = new Intent(context, UserEditInfoAct.class);
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
    }

    /**
     * 打开TA人资料查看页
     */
    public static void showCheckOtherInfoAct(final Context context, UserDetail userProfile) {
        showCheckOtherInfoAct(context, userProfile.getUid(), CenterConstant.USER_CHECK_INFO_OTHER, userProfile);
    }

    /**
     * 打开TA人资料查看页
     */
    public static void showCheckOtherInfoAct(final Context context, long uid) {
        showCheckOtherInfoAct(context, uid, CenterConstant.USER_CHECK_INFO_OTHER, null);
    }

    /**
     * 打开TA人资料查看页: 查看联系方式
     */
    public static void showCheckOtherContactAct(final Context context, long uid) {
        showCheckOtherInfoAct(context, uid, CenterConstant.USER_CHECK_CONNECT_OTHER, null);
    }

    /**
     * 打开TA人资料查看页: 贵族
     */
    public static void showCheckOtherContactAct(final Context context, long uid, int channel) {
        showCheckOtherInfoAct(context, uid, channel, null);
    }

    /**
     * 打开TA人资料查看页
     */
    private static void showCheckOtherInfoAct(final Context context, long uid, final int channel, UserDetail userProfile) {
        if (userProfile != null) {
            AttentionUtil.updateUserDetails(userProfile);
            skipCheckOtherInfoAct(context, channel, userProfile);
            return;
        }

        LoadingDialog.show((FragmentActivity) context, context.getString(R.string.user_info_require));
        ModuleMgr.getCenterMgr().reqOtherInfo(uid, new RequestComplete() {
            @Override
            public void onRequestComplete(final HttpResponse response) {
                LoadingDialog.closeLoadingDialog(200, new TimerUtil.CallBack() {
                    @Override
                    public void call() {
                        if (!response.isOk()) {
                            PToast.showShort(context.getString(R.string.request_error));
                            return;
                        }

                        UserDetail userProfile = (UserDetail) response.getBaseData();
                        //更新缓存
                        AttentionUtil.updateUserDetails(response.getResponseString());
                        skipCheckOtherInfoAct(context, channel, userProfile);
                    }
                });
            }
        });
    }

    private static void skipCheckOtherInfoAct(Context context, int channel, UserDetail userProfile) {
        if (userProfile == null)
            return;

        if (!userProfile.isUserNormal()) {
            showUserBlockAct(context);
            return;
        }

        Intent intent = new Intent(context, UserCheckInfoAct.class);
        intent.putExtra(CenterConstant.USER_CHECK_INFO_KEY, channel);
        intent.putExtra(CenterConstant.USER_CHECK_OTHER_KEY, userProfile);
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
    }

    /**
     * 打开自己资料查看页
     */
    public static void showCheckOwnInfoAct(Context context) {
        Intent intent = new Intent(context, UserCheckInfoAct.class);
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
    }

    /**
     * 打开用户账号封禁页
     */
    public static void showUserBlockAct(Context context) {
        Intent intent = new Intent(context, UserBlockAct.class);
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
    }

    /**
     * 打开编辑昵称页
     */
    public static void showEditContentAct(FragmentActivity context) {
        Intent intent = new Intent(context, EditContentAct.class);
        context.startActivity(intent);
    }

    /**
     * 打开编辑个性签名页
     */
    public static void showUserEditSignAct(FragmentActivity context, String sign) {
        Intent intent = new Intent(context, UserEditSignAct.class);
        intent.putExtra("sign", sign);
        context.startActivity(intent);
    }

    /**
     * 打开私密相册/视频
     *
     * @param userProfile 查看自己的时候传null
     */
    public static void showUserSecretAct(FragmentActivity activity, int channel, UserDetail userProfile, int requestCode) {
        Intent intent = new Intent(activity, UserSecretAct.class);
        intent.putExtra(CenterConstant.USER_CHECK_INFO_KEY, channel);
        intent.putExtra(CenterConstant.USER_CHECK_OTHER_KEY, userProfile);
        StackNode.appendIntent(activity, intent);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 打开他人资料设置页
     *
     * @param uid     他人用户id，无详细资料UserProfile对象时，传递uid, UserProfile传递null
     * @param channel 跳转来源渠道{@link CenterConstant}
     */
    public static void showUserOtherSetAct(final FragmentActivity context, long uid, UserDetail userDetail, final int channel) {
        UIShow.showUserOtherSetAct(context, uid, userDetail, null, channel);
    }

    /**
     * 打开他人资料设置页
     *
     * @param uid     他人用户id，无详细资料UserProfile对象时，传递uid, UserProfile传递null
     * @param channel 跳转来源渠道{@link CenterConstant}
     */
    public static void showUserOtherSetAct(final FragmentActivity context, long uid, UserDetail userDetail, final BaseData otherData, final int channel) {
        if (userDetail != null) {
            skipUserOtherSetAct(context, userDetail, channel);
            return;
        }

        LoadingDialog.show(context, context.getString(R.string.user_info_require));
        ModuleMgr.getCenterMgr().reqOtherInfo(uid, new RequestComplete() {
            @Override
            public void onRequestComplete(final HttpResponse response) {
                LoadingDialog.closeLoadingDialog(200, new TimerUtil.CallBack() {
                    @Override
                    public void call() {
                        if (!response.isOk()) {
                            PToast.showShort(context.getString(R.string.request_error));
                            return;
                        }

                        UserDetail userProfile = (UserDetail) response.getBaseData();
                        if (otherData instanceof MyChum) {
                            ((MyChum) otherData).setRemark(userProfile.getRemark());
                        }
                        //更新缓存
                        AttentionUtil.updateUserDetails(response.getResponseString());
                        skipUserOtherSetAct(context, userProfile, channel);
                    }
                });
            }
        });
    }

    private static void skipUserOtherSetAct(FragmentActivity context, UserDetail userDetail, int channel) {
        Intent intent = new Intent(context, UserOtherSetAct.class);
        intent.putExtra(CenterConstant.USER_CHECK_OTHER_KEY, userDetail);
        intent.putExtra(CenterConstant.USER_SET_KEY, channel);
        StackNode.appendIntent(context, intent);
        context.startActivityForResult(intent, CenterConstant.USER_SET_REQUEST_CODE);
    }

    /**
     * 打开他人标签页
     */
    public static void showUserOtherLabelAct(Context context) {
        Intent intent = new Intent(context, UserOtherLabelAct.class);
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
    }

    /**
     * 查看大图界面
     *
     * @param activity
     * @param list     Serializable 图片的数据链表对象 （看相册的 传List<UserPhoto>链表 看大图的 传List<String>链表）
     * @param position 选中的 图片 position
     * @param type     需要界面显示的类型
     *                 PhotoDisplayAct.DISPLAY_TYPE_USER; //看自己相册
     *                 PhotoDisplayAct.DISPLAY_TYPE_OTHER; //看别人相册
     *                 PhotoDisplayAct.DISPLAY_TYPE_BIG_IMG; //看大图
     */
    private static void showPhotoDisplayAct(FragmentActivity activity, Serializable list, int position, int type) {
        if (list == null || ((List<String>) list).size() == 0) {
            PToast.showShort("没有图片数据");
            return;
        }
        Intent intent = new Intent(activity, PhotoDisplayAct.class);
        intent.putExtra("list", list);
        intent.putExtra("position", position);
        intent.putExtra("type", type);
        StackNode.appendIntent(activity, intent);
        activity.startActivity(intent);
    }

    /**
     * 查看大图界面 看我自己的相册
     *
     * @param activity
     * @param list
     * @param position
     */
    public static void showPhotoOfSelf(FragmentActivity activity, Serializable list, int position) {
        showPhotoDisplayAct(activity, list, position, PhotoDisplayAct.DISPLAY_TYPE_USER);
    }

    /**
     * 查看大图界面 看别人的相册
     *
     * @param activity
     * @param list
     * @param position
     */
    public static void showPhotoOfOther(FragmentActivity activity, Serializable list, int position) {
        showPhotoDisplayAct(activity, list, position, PhotoDisplayAct.DISPLAY_TYPE_OTHER);
    }

    /**
     * 查看大图界面 url
     */
    public static void showPhotoOfBigImg(FragmentActivity activity, String url) {
        ArrayList<String> pics = new ArrayList<>();
        pics.add(url);
        showPhotoDisplayAct(activity, pics, 0, PhotoDisplayAct.DISPLAY_TYPE_BIG_IMG);
    }

    // -----------------------消息提示跳转 start--------------------------

    /**
     * 打开锁屏弹窗弹出的activity
     */
    public static void showLockScreenActivity() {
        Intent intent = new Intent(App.context, LockScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        StackNode.appendIntent(App.activity, intent);
        App.context.startActivity(intent);
    }

    /**
     * 跳转到解锁后顶部消息提示
     *
     * @param type       消息类型
     * @param simpleData 简单的用户个人资料
     * @param content    聊天内容
     */
    public static void showUserMailNotifyAct(int type, UserInfoLightweight simpleData, String content) {
        int flags = Intent.FLAG_ACTIVITY_NEW_TASK;
        if (Build.VERSION.SDK_INT >= 11)
            flags = flags | Intent.FLAG_ACTIVITY_CLEAR_TASK;

        Intent intent = new Intent(App.context, UserMailNotifyAct.class);
        intent.addFlags(flags);
        intent.putExtra("type", type);
        intent.putExtra("simple_data", simpleData);
        intent.putExtra("content", content);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        StackNode.appendIntent(App.activity, intent);
        App.context.startActivity(intent);
    }

    // -----------------------消息提示跳转 end----------------------------

    private static void showOtherDialog(FragmentActivity activity) {
        UIShow.showSayHelloDialog(activity);//判断男性展示一键打招呼弹窗
        UIShow.showMakeMoneyDlg(activity);//判断女性展示我要赚钱弹窗
    }

    /**
     * @param activity      FragmentActivity上下文
     * @param appUpdate     软件升级信息
     * @param isShowTip     是否展示界面提示
     * @param upgradeSource 弹窗来源
     * @updateAuthor Mr.Huang
     * @date 2017-07-11
     * 软件升级逻辑处理
     */
    public static void showUpdateDialog(final FragmentActivity activity, final AppUpdate appUpdate, UpgradeSource upgradeSource, boolean isShowTip) {
        final boolean isMainActivity = UpgradeSource.Main == upgradeSource;
        if (appUpdate == null) {
            if (isMainActivity) {
                showOtherDialog(activity);
            }
            return;
        }

        // 直接返回服务器没有返回包名的情况
        if (TextUtils.isEmpty(appUpdate.getPackage_name())) {
            if (isShowTip) {
                PToast.showShort(App.getContext().getString(R.string.update_server_error));
            }
            //@update Mr.Huang
            if (isMainActivity) {
                showOtherDialog(activity);
            }
            return;
        }

        // 相同包名
        if (ModuleMgr.getAppMgr().getPackageName().equals(appUpdate.getPackage_name())) {
            if (appUpdate.getVersion() > ModuleMgr.getAppMgr().getVerCode()) {
                createUpdateDialog(activity, appUpdate, upgradeSource);
            } else {
                if (isShowTip) {
                    PToast.showShort(App.getContext().getString(R.string.update_already_new));
                }
                if (isMainActivity) {
                    showOtherDialog(activity);
                }
            }
            return;
        }

        // 不同包名
        if (APKUtil.isAppInstalled(App.context, appUpdate.getPackage_name())) {
            // 如果本地已安装该包名的包，弹窗跳转到已安装的软件并退出当前软件，在新软件中处理升级逻辑
            PickerDialogUtil.showTipDialogCancelBack(activity, new SimpleTipDialog.ConfirmListener() {
                        @Override
                        public void onCancel() {
                            if (isMainActivity) {
                                showOtherDialog(activity);
                            }
                        }

                        @Override
                        public void onSubmit() {
                            APKUtil.launchApp(App.context, appUpdate.getPackage_name());
                        }
                    }, App.getContext().getString(R.string.update_has_install),
                    App.getContext().getString(R.string.tip), "",
                    App.getContext().getString(R.string.ok),
                    false, false);
            return;
        }

        if (appUpdate.getVersion() > 0) {//防止服务器没有返回升级结构的情况
            createUpdateDialog(activity, appUpdate, upgradeSource);
        } else {
            if (isShowTip) {
                PToast.showShort(App.getContext().getString(R.string.update_already_new));
            }
            //@author Mr.Huang
            if (isMainActivity) {
                showOtherDialog(activity);
            }
        }
    }

    /**
     * @param appUpdate     软件升级信息
     * @param upgradeSource 弹窗来源
     * @updateAuthor Mr.Huang
     * @date 2017-07-11
     * 创建软件升级弹框
     */
    private static void createUpdateDialog(FragmentActivity activity, AppUpdate appUpdate, UpgradeSource upgradeSource) {
        //更新时先保存用户信息备用
        ModuleMgr.getCommonMgr().updateSaveUP(appUpdate.getPackage_name(), appUpdate.getVersion());

        UpdateDialog updateDialog = new UpdateDialog();
        updateDialog.setData(appUpdate, upgradeSource);
        updateDialog.showDialog(activity);
    }

    /**
     * 跳转到公告页面
     */
    public static void showSysMessActivity(Context context) {
        Intent intent = new Intent(context, SysMessAct.class);
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
    }

    /**
     * 打开私信聊天内容页
     *
     * @param mContext  上下文
     * @param whisperID 私聊ID
     * @param name      名称（可有可无）
     */
    public static void showPrivateChatAct(Context mContext, long whisperID, String name) {
        showPrivateChatAct(mContext, whisperID, name, -1, null);
    }

    /**
     * 打开私信聊天内容页
     *
     * @param mContext  上下文
     * @param whisperID 私聊ID
     * @param name      名称（可有可无）
     * @param replyMsg  回复消息。一般情况是null
     */
    public static void showPrivateChatAct(Context mContext, long whisperID, String name, String replyMsg) {
        showPrivateChatAct(mContext, whisperID, name, -1, replyMsg);
    }

    /**
     * 打开私信聊天内容页
     *
     * @param mContext  上下文
     * @param whisperID 私聊ID
     * @param name      名称（可有可无）
     * @param kf_id     是否机器人（可有可无）
     */
    public static void showPrivateChatAct(Context mContext, long whisperID, String name, int kf_id) {
        showPrivateChatAct(mContext, whisperID, name, kf_id, null);
    }

    /**
     * 打开私信聊天内容页（带任务类型的跳转）
     *
     * @param mContext           上下文
     * @param whisperID          私聊ID
     * @param isInPrivateChatAct 是否在私聊页面中
     * @param triggerType        任务类型
     */
    public static void showPrivateChatAct(Context mContext, long whisperID, boolean isInPrivateChatAct, int triggerType) {
        showPrivateChatAct(mContext, whisperID, null, 0, null, isInPrivateChatAct, triggerType);
    }

    /**
     * 打开私信聊天内容页
     *
     * @param mContext  上下文
     * @param whisperID 私聊ID
     * @param name      名称（可有可无）
     * @param kf_id     是否机器人（可有可无）
     * @param replyMsg  回复消息。一般情况是null
     */
    public static void showPrivateChatAct(Context mContext, long whisperID, String name, int kf_id, String replyMsg) {
        showPrivateChatAct(mContext, whisperID, name, kf_id, replyMsg, false, 0);
    }

    /**
     * 打开私信聊天内容页
     *
     * @param mContext           上下文
     * @param whisperID          私聊ID
     * @param name               名称（可有可无）
     * @param kf_id              是否机器人（可有可无）
     * @param replyMsg           回复消息。一般情况是null
     * @param isInPrivateChatAct 是否在私聊页面中
     * @param triggerType        任务类型
     */
    public static void showPrivateChatAct(final Context mContext, final long whisperID, final String name, final int kf_id, final String replyMsg, boolean isInPrivateChatAct, int triggerType) {
        if (!isInPrivateChatAct) {
            Intent intent = new Intent(mContext, PrivateChatAct.class);
            intent.putExtra("whisperID", whisperID);
            intent.putExtra("name", name);
            if (replyMsg != null)
                intent.putExtra("replyMsg", replyMsg);
            intent.putExtra("kf_id", kf_id);
            intent.putExtra("triggerType", triggerType);
            StackNode.appendIntent(mContext, intent);
            mContext.startActivity(intent);
        } else {
            privateChatJump(triggerType, whisperID);
        }
    }

    /**
     * 私聊页面任务跳转
     *
     * @param triggerType
     * @param whisperID
     */
    public static void privateChatJump(int triggerType, long whisperID) {
        switch (triggerType) {
            case 1://视频&语音
            case 4://大转盘
                MsgMgr.getInstance().sendMsg(MsgType.MT_TASK_TO_JUMP, null);
                break;
            case 2://送礼物
                UIShow.showBottomGiftDlg(App.getActivity(), whisperID, 0, null);
                break;
            default:
                break;
        }
    }

    /**
     * 弹出聊天随机红包弹窗
     *
     * @param red_log_id 红包流水号
     * @param msg        红包消息mct字段，若无，传null或空字符串即可
     */
    public static void showChatRedBoxDialog(Activity context, int red_log_id, String msg) {
        Intent intent = new Intent(context, RandomRedBoxActivity.class);
        intent.putExtra("red_log_id", red_log_id);
        intent.putExtra("msg", msg);
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
    }

    /**
     * 通过配置调起的web-dialog弹框
     *
     * @param activity FragmentActivity实例
     */
    public static void showWebPushDialog(FragmentActivity activity) {
        Action action = ModuleMgr.getCommonMgr().getCommonConfig().getAction();
        if (action.canPushShow()) {
            WebPushDialog webPushAct = new WebPushDialog(activity, action.getUrl(), action.getDialog_rate());
            webPushAct.showDialog(activity);
        }
    }

    /**
     * 选择支付
     *
     * @param activity
     */
    public static void showPayListAct(final FragmentActivity activity, final int orderID) {
        LoadingDialog.show(activity, "生成订单中");
        ModuleMgr.getCommonMgr().reqGenerateOrders(orderID, new RequestComplete() {
            @Override
            public void onRequestComplete(final HttpResponse response) {
                PLogger.d("Re===" + response.getResponseString());
                LoadingDialog.closeLoadingDialog(800, new TimerUtil.CallBack() {
                    @Override
                    public void call() {
                        PayGood payGood = new PayGood(response.getResponseString());
                        if (payGood.isOK()) {
                            payGood.setPay_id(orderID);

                            Intent intent = new Intent(activity, PayListAct.class);
                            intent.putExtra("payGood", payGood);
                            StackNode.appendIntent(activity, intent);
                            activity.startActivityForResult(intent, Constant.REQ_PAYLISTACT);
                        } else {
                            PToast.showShort(response.getMsg());
                        }
                    }
                });
            }
        });
    }

    /**
     * 选择支付
     *
     * @param context
     * @param commodity_Id 订单ID
     * @param payType      类型
     */
    public static void showPayNoListAct(FragmentActivity context, int commodity_Id, int payType, long uid, String channel_uid) {

        Bundle bundle = new Bundle();
        bundle.putInt("commodity_Id", commodity_Id);
        bundle.putInt("payType", payType);
        bundle.putLong("uid", uid);
        bundle.putString("channel_uid", channel_uid);

        Intent intent = new Intent(context, PayOrderAct.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 选择支付
     *
     * @param activity
     * @param commodity_Id 订单
     * @param payType      类型
     */
    public static void showPay(final FragmentActivity activity, final int commodity_Id, final int payType, long uid, String channel_uid) {
        LoadingDialog.show(activity, "生成订单中");
        PPCSource ppcSource = SourcePoint.getInstance().getPPCSource();//获取ppc统计信息
        if (ppcSource != null)
            Statistics.payStatistic(String.valueOf(ppcSource.getUid()), String.valueOf(ppcSource.getChannelID()));

        RoomPayInfo info = SourcePoint.getInstance().getRoomPaySource(true);  //获取Roompay统计
        if (info != null) Statistics.roomPay(info);

        ModuleMgr.getCommonMgr().reqGenerateOrders(commodity_Id, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                PLogger.d("Re===" + response.getResponseString());
                LoadingDialog.closeLoadingDialog(2000);
                final PayGood payGood = new PayGood(response.getResponseString());
                if (!payGood.isOK()) {
                    PToast.showShort("支付出错，请重试！");
                    return;
                }

                payGood.setPay_id(commodity_Id);

                if (payType == GoodsConstant.PAY_TYPE_WECHAT) {//微信支付
                    new PayWeixinUtils(activity).onPayment(payGood);
                    return;
                }

                //支付宝支付
                new PayAlipayUtils(activity).onPayment(payGood);
            }
        });
    }


    public static void showPayWebAct(FragmentActivity activity, PayGood payGood) {
        Intent intent_web = new Intent(activity, PayWebAct.class);
        intent_web.putExtra("payGood", payGood);
        StackNode.appendIntent(activity, intent_web);
        activity.startActivityForResult(intent_web, Constant.PAYMENTACT_TO);
    }

    /**
     * 显示订单确认界面
     *
     * @param activity
     */
    public static void showRefreshDlg(FragmentActivity activity) {
        Intent intent = new Intent(activity, PayRefreshDlg.class);
        StackNode.appendIntent(activity, intent);
        activity.startActivity(intent);
    }

    /**
     * @param activity
     * @param stat     支付结果 1支付成功 2支付失败 3订单初始化 -1暂无此订单
     */
    public static void showResultDlg(FragmentActivity activity, int stat) {
        Intent intent = new Intent(activity, PayResultDlg.class);
        intent.putExtra("stat", stat);
        StackNode.appendIntent(activity, intent);
        activity.startActivity(intent);
    }

    /**
     * 打开QQ客服
     */
    public static void showQQService(Context context) {
        showQQService(context, ModuleMgr.getCommonMgr().getCommonConfig().getCommon().getService_qq());
    }

    /**
     * 打开随机QQ客服
     */
    public static void showQQService(Context context, String qq) {
        try {
            String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + qq;
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            } else {
                PToast.showShort(context.getString(R.string.qq_not_install));
            }
        } catch (Exception e) {
            PToast.showShort(context.getString(R.string.qq_open_error));
        }
    }

    // -----------------------我的提示跳转 start----------------------------

    /**
     * 跳转到开通vip页面
     */
    public static void showOpenVipActivity(Context context) {
        if (ModuleMgr.getCenterMgr().getMyInfo().isB()) {
            showWebActivity(context, WebUtil.jointUrl(Hosts.H5_PREPAID, ModuleMgr.getCenterMgr().getChargeH5Params(2)));
        } else {
            showWebActivity(context, WebUtil.jointUrl(Hosts.H5_PREPAID_Y, ModuleMgr.getCenterMgr().getChargeH5Params(2)));
        }
    }

    /**
     * 跳转到购买Y币页面
     */
    public static void showBuyCoinActivity(Context context) {
        showWebActivity(context, WebUtil.jointUrl(Hosts.H5_PREPAID_Y, ModuleMgr.getCenterMgr().getChargeH5Params(1)));
    }

    /**
     * @author Mr.Huang
     * @date 2017-07-19
     * 跳转到购买钥匙页面
     */
    public static void showBuyKeyActivity(Context context) {
        showWebActivity(context, WebUtil.jointUrl(Hosts.H5_PREPAID, ModuleMgr.getCenterMgr().getChargeH5Params(3)));
    }

    /**
     * 跳转到我的礼物页面
     */
    public static void showMyGiftActivity(Context context) {
        showWebActivity(context, WebUtil.jointUrl(Hosts.H5_GIFT));
    }

    /**
     * 跳转到活动相关页面
     */
    public static void showActionActivity(Context context) {
        showWebActivity(context, WebUtil.jointUrl(Hosts.H5_ACTION));
    }

    /**
     * 跳转到话费领取页面
     */
    public static void showBillCollectionActivity(Context context) {
        showWebActivity(context, WebUtil.jointUrl(Hosts.H5_BILL));
    }

    /**
     * 跳转到大转盘设置页面
     */
    public static void showRotaryActivity(Context context) {
        Intent intent = new Intent(context, RotarySetActivity.class);
        intent.putExtra("url", WebUtil.jointUrl(Hosts.H5_ROTARY));
        show(context, intent);
    }

    /**
     * 打开我的关注页面
     *
     * @param context
     */
    public static void showMyAttentionAct(Context context) {
        Intent intent = new Intent(context, MyAttentionAct.class);
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
    }

    /**
     * 打开我的钻石页面
     *
     * @param context
     */
    public static void showMyDiamondsAct(Context context) {
        Intent intent = new Intent(context, MyDiamondsAct.class);
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
    }

    /**
     * 打开我要赚红包H5
     *
     * @param context
     */
    public static void showEarnRedBagAct(Context context) {
        showWebActivity(context, WebUtil.jointUrl(Hosts.H5_EARN_RED_BAG));
    }

    /**
     * 打开普通索要礼物弹窗
     *
     * @param context
     */
    public static void showNormalAskGiftDlg(final Context context) {
        if (!NetworkUtils.isConnected(context)) { //无网络
            PToast.showShort(context.getString(R.string.net_error_retry));
            return;
        }
        Statistics.userBehavior(SendPoint.menu_me_redpackage_sylw);

        if (ModuleMgr.getCommonMgr().getCommonConfig().getGift().getUnlock_text_ids().size() > 0) {
            AskForGiftDialog dialog = new AskForGiftDialog(context, ModuleMgr.getCommonMgr().getCommonConfig().getGift().getUnlockTextGifts());
            dialog.show();
            return;
        }
    }

    /**
     * 打开钻石说明页面
     *
     * @param context
     */
    public static void showMyDiamondsExplainAct(Context context) {
        Intent intent = new Intent(context, MyDiamondsExplainAct.class);
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
    }

    /**
     * 对话框送礼物
     *
     * @param context
     * @param giftid  要送的礼物id
     * @param to_id   统计id
     */
    public static void showDiamondSendGiftDlg(final Context context, final int giftid, final String to_id, final String channel_uid) {
        DiamondSendGiftDlg dlg = null;
        final GiftsList.GiftInfo giftInfo = ModuleMgr.getCommonMgr().getGiftLists().getGiftInfo(giftid);
        if (giftInfo != null && giftInfo.isHasData()) {
            dlg = new DiamondSendGiftDlg(context, giftid, to_id, channel_uid);
            dlg.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            dlg.show();
            return;
        }
        ModuleMgr.getCommonMgr().requestGiftList(new GiftHelper.OnRequestGiftListCallback() {
            @Override
            public void onRequestGiftListCallback(boolean isOk) {
                if (isOk) {
                    if (giftInfo != null && giftInfo.isHasData()) {
                        DiamondSendGiftDlg dlg = new DiamondSendGiftDlg(context, giftid, to_id, channel_uid);
                        dlg.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                        dlg.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                        dlg.show();
                        return;
                    }
                    PToast.showShort(context.getString(R.string.gift_not_find));
                } else {
                    PToast.showShort(context.getString(R.string.net_error_retry));
                }
            }
        });
    }

    private static HTCallBack htCallBack;
    private static BottomGiftDialog dialog;

    /**
     * 消息页面送礼物底部弹框
     *
     * @param context
     * @param to_id   他人id
     */
    public static void showBottomGiftDlg(final Context context, final long to_id, final int from_tag, final String channel_uid) {
        if (ModuleMgr.getCommonMgr().getGiftLists().getArrCommonGifts().size() > 0) {
            if (htCallBack != null) {
                return;
            }
            if (dialog == null || (dialog != null && dialog.isDismiss())) {
                dialog = new BottomGiftDialog();
            } else {
                return;
            }
            htCallBack = ModuleMgr.getCommonMgr().requestMyBag(new RequestComplete() {
                @Override
                public void onRequestComplete(HttpResponse response) {

                    dialog.setToId(to_id, channel_uid);
                    dialog.setCtx(context);
                    dialog.setFromTag(from_tag);
                    dialog.showDialog((FragmentActivity) context);
                    htCallBack = null;
                }
            });
        } else {
            LoadingDialog.show((FragmentActivity) context);
            ModuleMgr.getCommonMgr().requestGiftList(new GiftHelper.OnRequestGiftListCallback() {
                @Override
                public void onRequestGiftListCallback(boolean isOk) {
                    LoadingDialog.closeLoadingDialog();
                    if (isOk) {
                        if (ModuleMgr.getCommonMgr().getGiftLists().getArrCommonGifts().size() > 0) {
                            BottomGiftDialog dialog = new BottomGiftDialog();
                            dialog.setToId(to_id, channel_uid);
                            dialog.setCtx(context);
                            dialog.setFromTag(from_tag);
                            dialog.showDialog((FragmentActivity) context);
                        }
                    } else {
                        PToast.showShort(context.getString(R.string.net_error_retry));
                    }
                }
            });
        }
    }

    /**
     * 账号封禁弹框
     *
     * @param context
     * @param isLogin    是否处于登录状态
     * @param bannedTime 封禁时间戳
     */
    public static void showBottomBannedDlg(final Context context, final boolean isLogin, long bannedTime) {
        BottomBannedDialog bannedDialog = new BottomBannedDialog();
        bannedDialog.setCtx(context, isLogin, bannedTime);
        bannedDialog.showDialog((FragmentActivity) context);
    }

    /**
     * 当用户首次产生钻石消费后，弹窗提示用户。
     */
    public static void showAccumulatedSystemDlg() {
        AccumulatedSystemDlg accumulatedSystemDlg = new AccumulatedSystemDlg();
        accumulatedSystemDlg.showDialog((FragmentActivity) App.activity);
    }

    /**
     * 爵位升级弹框。
     */
    public static void showTitleUpgradeDlg() {
        ModuleMgr.getCenterMgr().reqMyInfo(new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (response.isOk()) {
                    PSP.getInstance().put(CenterMgr.INFO_SAVE_KEY, response.getResponseString());
                    TitleUpgradeDlg titleUpgradeDlg = new TitleUpgradeDlg();
                    titleUpgradeDlg.showDialog((FragmentActivity) App.activity);
                }
            }
        });
    }

    /**
     * 大额礼物弹框。
     */
    public static void showBigGiftDlg(BigGiftInfo info) {
        BigGiftDlg bigGiftDlg = new BigGiftDlg();
        bigGiftDlg.setData(info);
        bigGiftDlg.showDialog((FragmentActivity) App.activity);
    }

    /**
     * 密友升级弹框。
     *
     * @param name  对方昵称
     * @param level 要升级的等级
     */
    public static void showCloseFriendUpgradeDlg(String name, int level) {
        CloseFriendUpgradeDlg closeFriendUpgradeDlg = new CloseFriendUpgradeDlg();
        closeFriendUpgradeDlg.setData(name, level);
        closeFriendUpgradeDlg.showDialog((FragmentActivity) App.activity);
    }

    /**
     * 解除密友弹框。
     *
     * @param myChum          对方uid
     * @param requestComplete 请求回调
     */
    public static void showRemoveCloseFriendDlg(MyChum myChum, RequestComplete requestComplete) {
        RemoveCloseFriendDlg removeCloseFriendDlg = new RemoveCloseFriendDlg();
        removeCloseFriendDlg.setData(myChum, requestComplete);
        removeCloseFriendDlg.showDialog((FragmentActivity) App.activity);
    }

    /**
     * 送礼弹框
     *
     * @param sendType 送礼弹框类型  BANNERETUPGRADEB:爵位升级弹框  BANNERETUPGRADEB:爵位升级弹框 ADDCLOSEFRIEND：加密友索要礼物
     * @param toUid    对方uid
     */
    public static void showUpgradeGiftSendDlg(int sendType, long toUid, RequestComplete complete) {
        showUpgradeGiftSendDlg(sendType, toUid, null, complete);
    }

    /**
     * 送礼弹框
     *
     * @param sendType 送礼弹框类型  BANNERETUPGRADEB:爵位升级弹框  BANNERETUPGRADEB:爵位升级弹框 ADDCLOSEFRIEND：加密友索要礼物
     * @param message  消息
     */
    public static void showUpgradeGiftSendDlg(int sendType, BaseMessage message, RequestComplete complete) {
        showUpgradeGiftSendDlg(sendType, 0, message, complete);
    }

    /**
     * 送礼弹框(toUid 和 message两者传一个即可，传message时toUid随意，传toUid时message可以设置为null)
     *
     * @param sendType 送礼弹框类型  BANNERETUPGRADEB:爵位升级弹框  BANNERETUPGRADEB:爵位升级弹框 ADDCLOSEFRIEND：加密友索要礼物
     * @param toUid    对方uid
     * @param message  消息（可传null）
     */
    public static void showUpgradeGiftSendDlg(final int sendType, long toUid, BaseMessage message, final RequestComplete complete) {
        final UpgradeGiftSendDlg upgradeGiftSendDlg = new UpgradeGiftSendDlg();
        if (message == null) {
            message = new BaseMessage();
            message.setWhisperID(toUid + "");
        }
        if (sendType == UpgradeGiftSendDlg.BANNERETUPGRADEB) { //送礼爵位升级
            final BaseMessage finalMessage1 = message;
            ModuleMgr.getCenterMgr().reqMyInfo(new RequestComplete() {
                @Override
                public void onRequestComplete(HttpResponse response) {
                    if (response.isOk()) {
                        PSP.getInstance().put(CenterMgr.INFO_SAVE_KEY, response.getResponseString());
                        int rank = ModuleMgr.getCenterMgr().getMyInfo().getNobilityInfo().getRank();
                        NobilityList.Nobility nobility = ModuleMgr.getCommonMgr().getCommonConfig().queryNobility(rank + 1, ModuleMgr.getCenterMgr().getMyInfo().getGender());
                        int process = nobility.getUpgrade_condition() - ModuleMgr.getCenterMgr().getMyInfo().getNobilityInfo().getCmpprocess();
                        if (process > 0 && process < 100) {
                            upgradeGiftSendDlg.setGiftTypeAndData(App.getContext(), sendType, ModuleMgr.getCommonMgr().getCommonConfig().getGift().getNobilityLevelupGifts(), finalMessage1, complete);
                            upgradeGiftSendDlg.showDialog((FragmentActivity) App.activity);
                        }
                    }
                }
            });
            return;
        } else if (sendType == UpgradeGiftSendDlg.PROMISEGIFT) { //男送定情礼物弹框
            upgradeGiftSendDlg.setGiftTypeAndData(App.getContext(), sendType, ModuleMgr.getCommonMgr().getCommonConfig().getGift().getCloseFriendsSendGifts(), message, complete);
        } else if (sendType == UpgradeGiftSendDlg.ADDCLOSEFRIEND) { //女加密友索要礼物
            upgradeGiftSendDlg.setGiftTypeAndData(App.getContext(), sendType, ModuleMgr.getCommonMgr().getCommonConfig().getGift().getCloseFriendsAskGifts(), message, complete);
        } else if (sendType == UpgradeGiftSendDlg.CLOSEFRIENDUPGRADE) { //送礼亲密度升级
            final BaseMessage finalMessage = message;
            ModuleMgr.getCommonMgr().getIntimateFriendTask(message.getLWhisperID(), new RequestComplete() {
                @Override
                public void onRequestComplete(HttpResponse response) {
                    if (response.isOk()) {
                        MyChumTaskList info = new MyChumTaskList();
                        info.parseJson(response.getResponseString());
                        NobilityList.CloseFriend friend = ModuleMgr.getCommonMgr().getCommonConfig().getNobilityList().queryCloseFriend(info.getLevel() + 1);
                        long experience = friend.getExperience() - info.getExperience();
                        if (experience <= 100 && experience > 0) {
                            finalMessage.setNobility_rank(info.getLevel() + 1);
                            finalMessage.setNobility_cmpprocess(experience);
                            upgradeGiftSendDlg.setGiftTypeAndData(App.getContext(), sendType, ModuleMgr.getCommonMgr().getCommonConfig().getGift().getIntimacyLevelupGifts(), finalMessage, complete);
                            upgradeGiftSendDlg.showDialog((FragmentActivity) App.activity);
                        }
                    }
                }
            });
            return;
        }
        upgradeGiftSendDlg.showDialog((FragmentActivity) App.activity);
    }

    /**
     * 看看她出场 / 邀请她方式选项   加入埋点信息source
     *
     * @param source 视频发起来源
     */
    public static void showLookAtHerDlg(final Context context, long otherId, String channel_uid, boolean isInvate, boolean isUseVideoCard, int videochat_len, String source, LookAtHerDlg.BackCall call) {
        LookAtHerDlg dialog = new LookAtHerDlg();
        dialog.setContext(context);
        dialog.setOtherId(otherId, channel_uid);
        dialog.setIsInvate(isInvate);
        dialog.setIsUseVideoCard(isUseVideoCard);
        dialog.setVideochat_len(videochat_len);
        dialog.setSource(source);
        dialog.setBackCall(call);
        dialog.showDialog((FragmentActivity) context);
    }

    public static void showLookAtHerDlg(final Context context, long otherId, String channel_uid, boolean isInvate, boolean isUseVideoCard, int videochat_len, String source) {
        showLookAtHerDlg(context, otherId, channel_uid, isInvate, isUseVideoCard, videochat_len, source, null);
    }

    /**
     * 接收邀请时选择出场方式
     */
    public static void showLookAtHerDlg(final Context context, long otherId, String channel_uid, long inviteId, String source) {
        LookAtHerDlg dialog = new LookAtHerDlg();
        dialog.setContext(context);
        dialog.setOtherId(otherId, channel_uid);
        dialog.setAcceptInvitat(true, inviteId);
        dialog.setSource(source);
        dialog.showDialog((FragmentActivity) context);
    }

    /**
     * 接收邀请时选择出场方式
     */
    public static void showLookAtHerDlg(final Context context, long otherId, String channel_uid, long inviteId, RequestComplete complete) {
        LookAtHerDlg dialog = new LookAtHerDlg();
        dialog.setContext(context);
        dialog.setOtherId(otherId, channel_uid);
        dialog.setAcceptInvitat(true, inviteId);
        dialog.setRequestComplete(complete);
        dialog.setShowToast(false);
        dialog.showDialog((FragmentActivity) context);
    }

    /**
     * 邀请过期弹框
     */
    public static void showInvitaExpiredDlg(final Context context, final long otherId, final String channel_uid, final int type, final int price, final String source, final ChatPanelInvite.HasDiamondBackCall call) {
        if (type == 1) {//视频
            VideoAudioChatHelper.getInstance().inviteVAChat((Activity) context, otherId, AgoraConstant.RTC_CHAT_VIDEO,
                    true, Constant.APPEAR_TYPE_NO, channel_uid, false, new VideoAudioChatHelper.VideoCardBackCall() {
                        @Override
                        public boolean showDiamondDlg() {//回调显示老的充值钻石
                            if (call != null) {
                                call.Call();
                            }
                            return true;
                        }

                        @Override
                        public boolean showVipDlg() {
                            return false;
                        }

                        @Override
                        public boolean oldProcess(final Activity context, int serviceSetVideoPrice, boolean isTip, final long dstUid, final int type, final boolean flag, final int singleType, final String channel_uid, final boolean isInvate, final boolean isUseVideoCard, final int videochat_len) {
                            InvitationExpiredDlg dialog = new InvitationExpiredDlg();
                            dialog.setData(context, otherId, channel_uid, type, price, source);
                            dialog.setInviteVAChatBackCall(new InvitationExpiredDlg.inviteVAChatBackCall() {
                                @Override
                                public void Call() {//已经检查完视频卡 vip了 开始走老流程了 如 是否默认露脸这些
                                    VideoAudioChatHelper.getInstance().inviteVAChatOld(context, dstUid, type, flag, singleType, channel_uid, isInvate, isUseVideoCard, videochat_len, source);
                                }
                            });
                            dialog.showDialog((FragmentActivity) context);
                            return true;//返回true 不在走老流程 ，这里自己处理 直接显示过期对话框
                        }
                    }, false, source);
        } else {
            InvitationExpiredDlg dialog = new InvitationExpiredDlg();
            dialog.setData(context, otherId, channel_uid, type, price, source);
            dialog.showDialog((FragmentActivity) context);
        }
    }

    /**
     * 关闭余额提示
     *
     * @param callBack 确定关闭浮动提示回调
     * @param otherId  产生交互的uid，大数据统计用
     */
    public static void showYTipsCloseDlg(final Context context, CloseBalanceDlg.IsCloseYTips callBack, long otherId) {
        CloseBalanceDlg dialog = new CloseBalanceDlg();
        dialog.setParams(callBack, otherId);
        dialog.showDialog((FragmentActivity) context);
    }

    /**
     * 打开我的钱包页面
     *
     * @param context
     */
    public static void showRedBoxRecordAct(Context context) {
        Intent intent = new Intent(context, RedBoxRecordAct.class);
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
    }

    /**
     * 打开提现页面
     *
     * @param context
     */
    public static void showWithDrawApplyAct(final int id, final double money, final boolean fromEdit, final int status, final FragmentActivity context) {
        LoadingDialog.show(context, context.getString(R.string.xlistview_header_hint_loading));
        ModuleMgr.getCommonMgr().reqWithdrawAddress(new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                LoadingDialog.closeLoadingDialog();
                WithdrawAddressInfo info = new WithdrawAddressInfo();
                Intent intent = new Intent(context, WithDrawApplyAct.class);
                intent.putExtra("id", id);
                intent.putExtra("money", money);
                intent.putExtra("status", status);
                intent.putExtra("fromEdit", fromEdit);
                if (response.isOk()) {
                    info.parseJson(response.getResponseString());
                    intent.putExtra("info", info);
                    StackNode.appendIntent(context, intent);
                    context.startActivity(intent);
                } else if ("未设置！".equalsIgnoreCase(response.getMsg())) {
                    intent.putExtra("info", info);
                    context.startActivity(intent);
                } else {
                    if (!NetworkUtils.isConnected(context))
                        PToast.showShort(context.getString(R.string.net_error_retry));
                    else
                        PToast.showShort(response.getMsg());
                }
            }
        });
    }

    /**
     * 打开提现说明页面
     *
     * @param context
     */
    public static void showWithDrawExplainAct(Context context) {
        Intent intent = new Intent(context, WithDrawExplainAct.class);
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
    }

    /**
     * 打开提现记录页面
     *
     * @param context
     */
    public static void showWithDrawRecordAct(Context context) {
        Intent intent = new Intent(context, WithDrawRecordAct.class);
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
    }

    /**
     * 打开提现申请成功页面
     *
     * @param context
     */
    public static void showWithDrawSuccessAct(Context context, String money) {
        Intent intent = new Intent(context, WithDrawSuccessAct.class);
        intent.putExtra("money", money);
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
    }

    /**
     * 打开手机验证页面
     *
     * @param context
     */
    public static void showRedBoxPhoneVerifyAct(Context context) {
        Intent intent = new Intent(context, RedBoxPhoneVerifyAct.class);
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
    }

    /**
     * 打开身份证认证页面
     *
     * @param context
     */
    public static void showIDCardAuthenticationAct(final FragmentActivity context, final int requestCode) {
        IdCardVerifyStatusInfo info = ModuleMgr.getCommonMgr().getIdCardVerifyStatusInfo();
        if (info.isOk() || ModuleMgr.getCenterMgr().getMyInfo().getIdcard_validation() == 0) {
            Intent intent = new Intent(context, IDCardAuthenticationAct.class);
            StackNode.appendIntent(context, intent);
            context.startActivityForResult(intent, requestCode);
            return;
        }
        LoadingDialog.show(context);
        ModuleMgr.getCommonMgr().getVerifyStatus(new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                LoadingDialog.closeLoadingDialog();
                if (response.isOk()) {
                    Intent intent = new Intent(context, IDCardAuthenticationAct.class);
                    StackNode.appendIntent(context, intent);
                    context.startActivityForResult(intent, requestCode);
                } else {
                    PToast.showShort(context.getString(R.string.net_error_retry));
                }
            }
        });
    }

    /**
     * 打开身份证认证成功页面
     *
     * @param context
     */
    public static void showIDCardAuthenticationSucceedAct(FragmentActivity context, int requestCode) {
        Intent intent = new Intent(context, IDCardAuthenticationSucceedAct.class);
        StackNode.appendIntent(context, intent);
        context.startActivityForResult(intent, requestCode);
    }

    /**
     * 打开视频语音邀请
     */
    public static void showChatInviteAct(Context context) {
        Intent intent = new Intent(context, ChatInviteAct.class);
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
    }

    /**
     * 打开私密消息
     */
    public static void showChatSecretAct(Context context) {
        Intent intent = new Intent(context, ChatSecretAct.class);
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
    }

    /**
     * 打开陌生人消息
     */
    public static void showChatStrangerAct(Context context) {
        Intent intent = new Intent(context, ChatStrangerAct.class);
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
    }

    /**
     * 打开送礼物的人
     */
    public static void showGiftFromAct(Context context) {
        Intent intent = new Intent(context, ChatGiftAct.class);
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
    }

    // -----------------------我的提示跳转 end----------------------------

    //============================发现 start =============================\\

    /**
     * 打开举报界面
     *
     * @param tuid    被举报人的uid
     * @param context 上下文
     */
    public static void showDefriendAct(long tuid, Context context) {
        Intent intent = new Intent(context, DefriendAct.class);
        intent.putExtra("tuid", tuid);
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
    }

    /**
     * 打开我的好友界面
     *
     * @param context
     */
    public static void showMyFriendsAct(Context context) {
        Intent intent = new Intent(context, MyFriendsAct.class);
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
    }

    /**
     * 打开黑名单界面
     *
     * @param context
     */
    public static void showMyDefriends(Context context) {
        Intent intent = new Intent(context, MyDefriendAct.class);
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
    }

    /**
     * 打开上传头像界面
     *
     * @param context
     * @param isResetAvatar 是不是重新上传头像
     */
    public static void showUploadAvatarAct(Context context, boolean isResetAvatar) {
        Intent intent = new Intent(context, UserAvatarUploadAct.class);
        intent.putExtra("type", -1);
        intent.putExtra("isResetAvatar", isResetAvatar);
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
    }

    /**
     * 打开上传头像界面
     *
     * @param context
     * @param isResetAvatar 是不是重新上传头像
     */
    public static void showUploadAvatarActToMain(Context context, boolean isResetAvatar) {
        Intent intent = new Intent(context, UserAvatarUploadAct.class);
        intent.putExtra("type", 0);
        intent.putExtra("isResetAvatar", isResetAvatar);
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
    }

    /**
     * 打开打招呼的人界面
     *
     * @param context
     */
    public static void showSayHelloUserAct(Context context) {
        Intent intent = new Intent(context, SayHelloUserAct.class);
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
    }

    /**
     * 爵位版本跳转打招呼
     *
     * @param activity
     * @param data     json数据
     * @author Mr.Huang
     */
    public static void showSayHelloWithPeerage(Activity activity, String data) {
        Intent intent = new Intent(activity, PeerageSayHelloActivity.class);
        intent.putExtra(PeerageSayHelloActivity.EXTRA_DATA, data);
        StackNode.appendIntent(activity, intent);
        activity.startActivity(intent);
    }

    //============================发现 end =============================\\

    // -----------------------各种充值弹框跳转 start----------------------------

    /**
     * @param context
     * @param fromTag 打开充值来源 （统计用 可选）
     * @param touid   是否因为某个用户充值 （统计用 可选）
     */
    public static void showGoodsDiamondDialogAndTag(Context context, int fromTag, long touid, String channel_uid) {
        //showGoodsDiamondDialog(context, 0, GoodsConstant.DLG_DIAMOND_NORMAL, fromTag, touid, channel_uid);
        showGoodsDiamondBottomDlg(context);
    }

    /**
     * 送礼钻石充值弹框
     *
     * @param needDiamond 所需钻石差值
     */
    public static void showGoodsDiamondDialog(Context context, int needDiamond, int fromTag, long touid, String channel_uid) {
        //showGoodsDiamondDialog(context, needDiamond, GoodsConstant.DLG_DIAMOND_GIFT_SHORT, fromTag, touid, channel_uid);
        showGoodsDiamondBottomDlg(context);
    }

    /**
     * @param context
     * @param needDiamond
     * @param type
     * @param fromTag     打开来源
     * @param touid       是否因为某个用户充值 （统计用 可选）
     * @param channel_uid 是否因为某个用户充值渠道id （统计用 可选）
     */
    private static void showGoodsDiamondDialog(Context context, int needDiamond, int type, int fromTag, long touid, String channel_uid) {
        Intent intent = new Intent(context, GoodsDiamondDialog.class);
        intent.putExtra(GoodsConstant.DLG_TYPE_KEY, type);
        intent.putExtra(GoodsConstant.DLG_GIFT_NEED, needDiamond);
        intent.putExtra(GoodsConstant.DLG_OPEN_FROM, fromTag);
        intent.putExtra(GoodsConstant.DLG_OPEN_TOUID, touid);
        intent.putExtra(GoodsConstant.DLG_OPEN_CHANNEL_UID, channel_uid);
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
    }

    /**
     * 聊天钻石充值弹框
     *
     * @param otherID       女用户头像
     * @param chatType      视频，语音邀请
     * @param price         通信价格
     * @param isAloneInvite 是否是单邀
     * @param videoID       vc_id
     *                      type       0是2个头像 ，1是露出一半的一个头像,2是在panel里一个头像
     */
    public static void showBottomChatDiamondDlg(Context context, long otherID, int chatType, int price, boolean isAloneInvite, long videoID) {
        showBottomChatDiamondDlg(context, otherID, chatType, price, isAloneInvite, videoID, 0, GoodsConstant.DLG_DIAMOND_CHAT, null, false);
    }

    public static void showBottomChatDiamondDlg(Context context, long otherID, int chatType, int price, boolean isAloneInvite, long videoID, int type, int goodsPanelType, UserInfoLightweight userInfoLightweight, boolean isShowPrice) {
        BottomChatDiamondDlg chatDialog = new BottomChatDiamondDlg();
        chatDialog.UserInfoLightweight(userInfoLightweight);
        chatDialog.setInfo(otherID, chatType, price, isAloneInvite, videoID, type, goodsPanelType, isShowPrice);
        chatDialog.showDialog((FragmentActivity) context);
    }

    /**
     * 聊天VIP充值弹框
     *
     * @param vipType vip充值来源展示
     *                GoodsConstant.DLG_VIP_AV_CHAT:  发起音视频
     *                GoodsConstant.DLG_VIP_SEARCH_B:  B环境搜索
     *                GoodsConstant.DLG_VIP_CHOOSE_B:  B环境vip筛选
     *                avaterUrl 头像地址 ，为GoodsConstant.DLG_VIP_INDEX_YULIAO_AVATER必须传
     */
    public static void showGoodsVipBottomDlg(Context context, int vipType) {
        showGoodsVipBottomDlg(context, vipType, null, null, 0, null, false, false, false, null);
    }

    public static void showGoodsVipBottomDlg(Context context, int vipType, String avaterUrl,
                                             String nickname, int age, String distanceict, boolean Vip, boolean isMan, boolean isOnline, String vipTips) {
        GoodsVipBottomDlg vipBottomDlg = new GoodsVipBottomDlg();
        vipBottomDlg.setInfo(vipType, avaterUrl, nickname, age, distanceict, Vip, isMan, isOnline, vipTips);
        vipBottomDlg.showDialog((FragmentActivity) context);
    }


    //chatType -1是为了兼容老代码 ，由于调用的地方太多无法一一修改且这个值只有首页和资料页用到。这个值是判断视频还是音频，如果传-1代表未知
    public static void showGoodsVipBottomDlg(Context context, int vipType, String avaterUrl,
                                             String nickname, int age, String distanceict, boolean Vip, boolean isMan, boolean isOnline, String vipTips, int chatType, int price) {
        GoodsVipBottomDlg vipBottomDlg = new GoodsVipBottomDlg();
        vipBottomDlg.setInfo(vipType, avaterUrl, nickname, age, distanceict, Vip, isMan, isOnline, vipTips, chatType, price);
        vipBottomDlg.showDialog((FragmentActivity) context);
    }

    /**
     * 聊天Y币充值弹框
     */
    public static void showGoodsYCoinBottomDlg(Context context) {
        GoodsYCoinBottomDlg yCoinBottomDlg = new GoodsYCoinBottomDlg();
        yCoinBottomDlg.showDialog((FragmentActivity) context);
    }

    /**
     * 聊天钻石充值弹框
     */
    public static void showGoodsDiamondBottomDlg(Context context) {
        GoodsDiamondBottomDlg diamondBottomDlg = new GoodsDiamondBottomDlg();
        diamondBottomDlg.showDialog((FragmentActivity) context);
    }

    /**
     * vip弹框
     *
     * @param userDetail 用户资料
     */
    public static void showOpenVipDialogNew(Context context, UserInfoLightweight userDetail, String desc, boolean isShowSayHello, String sendTxt) {
        OpenVipDialogNew vipBottomDlg = new OpenVipDialogNew();
        vipBottomDlg.setData(userDetail, desc, isShowSayHello, sendTxt);
        vipBottomDlg.showDialog((FragmentActivity) context);
    }

    /**
     * 打开爵位星级不够时vip购买弹窗
     *
     * @param detail 对方用户资料
     */
    public static void showCallingVipDialog(UserDetail detail) {
        showCallingVipDialog(detail, "");
    }

    /**
     * 打开爵位星级不够时vip购买弹窗
     *
     * @param detail   对方用户资料
     * @param distance 双方距离
     */
    public static void showCallingVipDialog(UserDetail detail, String distance) {
        GoodsVipCallingDialog dialog = new GoodsVipCallingDialog();
        dialog.setUserInfo(detail);
        if (!TextUtils.isEmpty(distance)) dialog.setDistance(distance);
        dialog.show(((FragmentActivity) App.activity).getSupportFragmentManager());
    }

    public static void showCallingVipDialog(long uid) {
        GoodsVipCallingDialog dialog = new GoodsVipCallingDialog();
        dialog.show(((FragmentActivity) App.activity).getSupportFragmentManager(), uid);
    }

    /**
     * 查看视频：送礼弹框
     */
    public static void showSecretGiftDlg(FragmentActivity activity, UserVideo userVideo, int requestCode) {
        Intent intent = new Intent(activity, SecretGiftDlg.class);
        intent.putExtra(CenterConstant.USER_CHECK_VIDEO_KEY, userVideo);
        StackNode.appendIntent(activity, intent);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 查看视频：钻石充值弹框
     */
    public static void showSecretDiamondDlg(FragmentActivity context) {
        Intent intent = new Intent(context, SecretDiamondDlg.class);
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
        context.finish();
    }

    /**
     * 查看视频：视频播放页
     */
    public static void showSecretVideoPlayerDlg(FragmentActivity context, UserVideo userVideo) {
        showSecretVideoPlayerDlg(context, userVideo, false);
    }

    /**
     * 查看视频：视频播放页
     */
    public static void showSecretVideoPlayerDlg(FragmentActivity context, UserVideo userVideo, Boolean showTitle) {
        Intent intent = new Intent(context, SecretVideoPlayerAct.class);
        intent.putExtra(CenterConstant.USER_CHECK_VIDEO_KEY, userVideo);
        intent.putExtra(CenterConstant.USER_CHECK_VIDEO_TITLE_KEY, showTitle);
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
    }

    /**
     * 打开录制视频页
     *
     * @param context
     * @param requestCode
     */
    public static void showRecordVideoAct(FragmentActivity context, int requestCode) {
        Intent intent = new Intent(context, RecordVideoAct.class);
        StackNode.appendIntent(context, intent);
        context.startActivityForResult(intent, requestCode);
    }

    /**
     * 打开视频认证页
     *
     * @param context
     */
    public static void showMyAuthenticationVideoAct(FragmentActivity context, int requestCode) {
        Intent intent = new Intent(context, MyAuthenticationVideoAct.class);
        StackNode.appendIntent(context, intent);
        context.startActivityForResult(intent, requestCode);
    }

    /**
     * 打开我的认证页面
     *
     * @param context
     */
    public static void showMyAuthenticationAct(FragmentActivity context, int requestCode) {
        Intent intent = new Intent(context, MyAuthenticationAct.class);
        StackNode.appendIntent(context, intent);
        context.startActivityForResult(intent, requestCode);
    }

    /**
     * 打开重置密码
     *
     * @param context
     */
    public static void showFindPwdAct(FragmentActivity context) {
        Intent intent = new Intent(context, FindPwdAct.class);
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
    }

    /**
     * 打开手机认证完成页面
     *
     * @param context
     * @param requestCode
     */
    public static void showPhoneVerifyCompleteAct(final FragmentActivity context, final int requestCode) {
        Intent intent = new Intent(context, PhoneVerifyCompleteAct.class);
        StackNode.appendIntent(context, intent);
        context.startActivityForResult(intent, requestCode);
    }

    /**
     * 显示微信二维码支付
     *
     * @param context
     * @param QRUrl   二维码URL
     */

    public static void showWxpayForQRCode(Context context, String QRUrl, int time, int money, String uri) {
        Intent intent = new Intent(context, WepayQRCodeAct.class);
        intent.putExtra("qrurl", QRUrl);
        intent.putExtra("time", time);
        intent.putExtra("money", money);
        intent.putExtra("uri", uri);
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
    }

    /**
     * 显示打开微信对话框
     *
     * @param context
     */
    public static void showWxpayOpenWx(Context context, String UIR) {
        Dialog dialog = new OpenWxDialog(context, UIR);
        dialog.show();
    }

    public static void showWePayForH5(Context context, String url) {
        Intent intent = new Intent(context, PayWebActivity.class);
        intent.putExtra("url", url);
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
    }

    /**
     * 女性进入首页索要礼物弹框
     */
    public static void showWantMoneyDlg(Context context) {
        if (ModuleMgr.getCenterMgr().getMyInfo().isMan()) return;
        WantMoneyDlg dlg = new WantMoneyDlg(context);
        dlg.show();
    }

    /**
     * 私密视频解锁对话框
     */
    public static void showOpenvideoDlg(Context context, int giftId) {
        DiamondOpenVideoDlg dlg = new DiamondOpenVideoDlg(context, giftId, "", "");
        dlg.show();
    }

    /**
     * 视频图片评价
     */
    public static void showPhotoFeelDlg(Context context, long qun_id, long tuid) {
        PhotoFeelDlg dlg = new PhotoFeelDlg();
        dlg.setData(context, qun_id, tuid);
        dlg.showDialog((FragmentActivity) context);
    }

    /**
     * 显示私密照片
     *
     * @param context
     */
    public static void showPrivatePhotoDisplayAct(Context context, long qun_id, long tuid, String desc, ArrayList<String> imgList) {
        Intent intent = new Intent(context, PrivatePhotoDisplayAct.class);
        intent.putExtra("qun_id", qun_id);
        intent.putExtra("tuid", tuid);
        intent.putExtra("desc", desc);
        intent.putStringArrayListExtra("imgList", imgList);
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
    }

    /**
     * 打开我要赚钱界面
     *
     * @param context
     */
    public static void showEarnMoney(Context context) {
        Intent intent = new Intent(context, EarnMoneyAct.class);
        intent.putExtra("url", WebUtil.jointUrl(Hosts.H5_EARN_MONEY));
        show(context, intent);
    }

    /**
     * 密友说明
     */
    public static void showIntimateFriendExplain(Context context) {
        Intent intent = new Intent(context, IntimateFriendExplainAct.class);
        intent.putExtra("url", WebUtil.jointUrl(Hosts.H5_INTIMATE_FRIEND_EXPLAIN));
        show(context, intent);
    }

    /**
     * 爵位特权
     */
    public static void showTitlePrivilegeAct(Context context) {
        Intent intent = new Intent(context, TitlePrivilegeAct.class);
        intent.putExtra("url", WebUtil.jointUrl(Hosts.H5_TITLE_PRIVILEGE));
        show(context, intent);
    }

    /**
     * 女神抓捕详情
     */
    public static void showCatchGoddessInfoAct(Context context) {
        Intent intent = new Intent(context, CatchGoddessInfoAct.class);
        show(context, intent);
    }

    /**
     * 女神抓捕
     *
     * @param otherUid 对方UID
     */
    public static void showCatchGoddessAct(Context context, long otherUid) {
        Intent intent = new Intent(context, CatchGoddessAct.class);
        intent.putExtra("otherUid", String.valueOf(otherUid));
        show(context, intent);
    }

    /**
     * 女性自动回复
     */
    public static void showWomanAutoReply(Context context) {
        Intent intent = new Intent(context, WomanAutoReplyAct.class);
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
    }

    /**
     * 打开群发视频界面
     *
     * @param context
     */
    public static void showMassVideo(Context context) {
        showWebActivity(context, WebUtil.jointUrl(Hosts.H5_VIDEO_INVITE));
    }

    /**
     * 打开群发语音界面
     *
     * @param context
     */
    public static void showMassVoice(Context context) {
        showWebActivity(context, WebUtil.jointUrl(Hosts.H5_MASS_VOICE));
    }

    /**
     * 打开索要礼物界面
     *
     * @param context
     */
    public static void showAskGift(Context context) {
        showWebActivity(context, WebUtil.jointUrl(Hosts.H5_ASK_FOR_GIFT));
    }

    /**
     * 打开收入明细界面
     *
     * @param context
     */
    public static void showIncomeDetail(Context context) {
        showWebActivity(context, WebUtil.jointUrl(Hosts.H5_INCOME_DETAIL));
    }

    /**
     * 打开我的等级界面
     *
     * @param context
     */
    public static void showMyLevel(Context context) {
        showWebActivity(context, WebUtil.jointUrl(Hosts.H5_MY_LEVEL));
    }

    /**
     * 女性进入首页我要赚钱弹框
     *
     * @param context
     */
    public static void showMakeMoneyDlg(final FragmentActivity context) {
        TimerUtil.beginTime(new TimerUtil.CallBack() {
            @Override
            public void call() {
                UserDetail userDetail = ModuleMgr.getCenterMgr().getMyInfo();
                if (!userDetail.isMan() /*&& !ModuleMgr.getCommonMgr().getIsShowMakeMoneyDlg()*/) {
                    /*if (!userDetail.isVerifyAll()) {
                        showUserAuthDlg(context, Constant.OPEN_FROM_HOME);
                        return;
                    }
                    MakeMoneyTipDialog dialog = new MakeMoneyTipDialog();
                    dialog.showDialog(context);*/
                    MakeMoneyNewTipDialog dialog = new MakeMoneyNewTipDialog();
                    dialog.showDialog(context);
                }
            }
        }, 1000);
    }

    /**
     * 打开资料认证提示弹框
     *
     * @param context
     * @param fromTag Constant.OPEN_FROM_HOME //首页打开资料认证弹框 Constant.OPEN_FROM_CMD //CMD打开资料认证弹框
     */
    public static void showUserAuthDlg(FragmentActivity context, int fromTag) {
        UserAuthDialog dialog = new UserAuthDialog();
        dialog.setFromType(fromTag);
        dialog.showDialog(context);
    }

    /**
     * 图片索要礼物弹框
     *
     * @param context 上下文
     */
    public static void showAskforPicGiftDlg(Context context) {
        AskforPicGiftDlg dlg = new AskforPicGiftDlg();
        dlg.setContext(context);
        dlg.showDialog((FragmentActivity) context);
    }

    /**
     * 视频索要礼物弹框
     *
     * @param context 上下文
     */
    public static void showAskforVideoGiftDlg(Context context) {
        AskforVideoGiftDlg dlg = new AskforVideoGiftDlg();
        dlg.setContext(context);
        dlg.showDialog((FragmentActivity) context);
    }

    /**
     * 加入公会弹框
     *
     * @param context
     */
    public static void showJoinGuildDlg(FragmentActivity context) {
        JoinGuildDialog dlg = new JoinGuildDialog();
        dlg.showDialog(context);
    }

    /**
     * 查看私密
     *
     * @param context 上下文
     */
    public static void showLookPrivateDlg(Context context, PrivateMessage msg, int type) {
        LookPrivateDlg dlg = new LookPrivateDlg();
        dlg.setData(context, msg, type);
        dlg.showDialog((FragmentActivity) context);
    }

    public static void showOpenVaDlg(FragmentActivity context, int type) {
        OpenVaDlg dlg = new OpenVaDlg();
        dlg.setvType(type);
        dlg.showDialog(context);
    }

    /**
     * 我的密友
     *
     * @param context
     */
    public static void showMyChumActAct(Context context) {
//        context.startActivity(new Intent(context, MyChumAct.class));
        showMainWithTabData(context, FinalKey.MAIN_TAB_3, null);
    }

    public static void showOffNetDesAct(Context context) {
        Intent intent = new Intent(context, OffNetDescribeAct.class);
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
    }

    /**
     * @author Mr.Huang
     * @date 2017-07-11
     * 判断男性展示一键打招呼弹窗
     * 代码拷贝自MainActivity.java
     */
    public static void showSayHelloDialog(final FragmentActivity activity) {
        TimerUtil.beginTime(new TimerUtil.CallBack() {
            @Override
            public void call() {
                ModuleMgr.getCommonMgr().showSayHelloDialog(activity);
            }
        }, 3000);
    }

    /**
     * @param activity
     * @param count    数量
     * @param len      时长
     * @author Mr.Huang
     * 首页获得免费视频卡
     */
    public static void showGetVideoCardDialog(final FragmentActivity activity, int count, int len) {
        PeerageGetVideoCardDialog dialog = new PeerageGetVideoCardDialog(activity);
        dialog.show(len);
    }

    /**
     * 风云榜
     */
    public static void showRankAct(Context context) {
        Intent intent = new Intent(context, RankAct.class);
        context.startActivity(intent);
    }

    /**
     * 分享二维码
     *
     * @param gender 1男 2女
     */
    public static void showShareCodeAct(Context context, int gender) {
        showShareCodeAct(context, gender, 1, null);
    }

    /**
     * 分享二维码
     *
     * @param gender 1男 2女
     * @param type   分享链接方式 1分享用户 2分享直播
     */
    public static void showShareCodeAct(Context context, int gender, int type, ShareTypeData shareTypeData) {
        Intent intent = new Intent(context, ShareCodeAct.class);
        intent.putExtra("gender", gender);
        intent.putExtra("type", type);
        intent.putExtra("shareTypeData", shareTypeData);
        context.startActivity(intent);
    }

    /**
     * 榜单类型
     *
     * @param context 上下文
     */
    public static void showMultiTitle(Context context, String url, MultiTitle multiTitle) {
        Intent intent = new Intent(context, MultiTitleAct.class);
        intent.putExtra("url", url);
        intent.putExtra(CenterConstant.RANK_TYPE_TITLES, multiTitle);
        context.startActivity(intent);
    }

    /**
     * 惊喜回馈
     */
    public static void showActiveDlg(Context context, int packId) {
        ActiveDlg activeDlg = new ActiveDlg();
        activeDlg.setData(context, packId);
        activeDlg.showDialog((FragmentActivity) context);
    }

    /**
     * 恭喜你获得红包弹框
     *
     * @param redType 红包类型： 2 视频聊天， 3 礼物， 4 充值 （注：无1 服务器规定）
     */
    public static void showGetRedBagDlg(Context context, int redType) {
        GetRedBagDlg getRedBagDlg = new GetRedBagDlg();
        getRedBagDlg.setData(redType);
        getRedBagDlg.showDialog((FragmentActivity) context);
    }

    /**
     * 打开打卡红包页面
     *
     * @param redType 类型： CenterConstant.TYPE_INIT 打卡红包获取界面， CenterConstant.TYPE_SUCCEED 打卡红包分享成功页面， CenterConstant.TYPE_FAILURE 打卡红包分享失败页面
     */
    public static void showCardRedBagDlg(Context context, int redType, double money) {
        if((redType == CenterConstant.TYPE_INIT && ShareUtil.shareGapTimeOut(Constant.SHARE_GAP_TIME)) || redType != CenterConstant.TYPE_INIT ){
            LiveCardRedBagDlg getRedBagDlg = new LiveCardRedBagDlg();
            getRedBagDlg.setData(redType, money);
            getRedBagDlg.showDialog((FragmentActivity) context);
        }
    }

    /**
     * 分享立即赚钱弹框
     *
     * @param context 上下文
     */
    public static void showShareMakeMoneyDlg(Context context) {
        final long promotionuid = ModuleMgr.getCommonMgr().getPromotionuid();
        if (promotionuid == 0) {
            //有要找的人弹窗（SureUserDlg）先弹要找的人 ，关闭弹窗，弹分享赚钱；如果从（SureUserDlg）进入聊天页，则下次返回首页弹分享赚钱；弹窗只弹一次（分享赚钱2.0）
            boolean isShowAgin = PSP.getInstance().getBoolean(ModuleMgr.getCommonMgr().getPrivateKey(Constant.SHARE_MAKE_MONEY), false);
            if (!isShowAgin) {
                ShareMakeMoneyDlg shareMakeMoneyDlg = new ShareMakeMoneyDlg();
                shareMakeMoneyDlg.showDialog((FragmentActivity) context);
                StatisticsShareUnlock.onAlert_share();
            }
        }
    }

    /**
     * 分享奖励弹框
     *
     * @param context 上下文
     */
    public static void showShareRewardDlg(Context context) {
        ShareRewardDlg shareRewardDlg = new ShareRewardDlg();
        shareRewardDlg.showDialog((FragmentActivity) context);
    }

    /**
     * 同随机的一个女用户视频
     *
     * @param dstUid  对方id
     * @param vtype   期望 聊天媒体类型 1视频, 2语音
     * @param usecard 是否使用视频卡
     */
    public static void showBusyRandomDlg(final Context context, long dstUid, final int vtype, final boolean usecard, final String source) {
        if (!ModuleMgr.getCenterMgr().getMyInfo().isMan()) return;
        ModuleMgr.getCenterMgr().reqVideoChatConfig(dstUid, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (response.isOk()) {
                    VideoConfig videoConfig = (VideoConfig) response.getBaseData();
                    int price = vtype == 1 ? videoConfig.getVideoPrice() : videoConfig.getAudioPrice();
                    BusyRandomDlg busyRandomDlg = new BusyRandomDlg();
                    busyRandomDlg.setData(context, vtype, price, usecard, source);
                    busyRandomDlg.showDialog((FragmentActivity) context);
                }
            }
        });
    }

    /**
     * 任务
     *
     * @param myChum
     */
    public static void showFriendTaskDlg(final MyChum myChum) {
        ModuleMgr.getCommonMgr().reqGetIntimateFriendTask(myChum.getUid(), new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (!response.isOk()) return;
                MyChumTaskList myChumTaskList = (MyChumTaskList) response.getBaseData();
                List<MyChumTaskList.MyChumTask> myChumTasks = myChumTaskList.getMyChumTasks();
                if (myChumTasks.size() <= 0) {
                    PToast.showShort("没有任务！");
                    return;
                }

                FriendTaskDlg friendTaskDlg = new FriendTaskDlg();
                friendTaskDlg.setData(myChum, myChumTaskList);
                friendTaskDlg.showDialog((FragmentActivity) App.getActivity());

            }
        });
    }

    /**
     * 视频评论页面
     */
    public static void showRtcCommentAct(final Context activity) {
//        // 只有视频时弹评价结算页面  （爵位版语音视频都显示结算界面--注销此处代码）
//        if (ModuleMgr.getRtcEnginMgr().getEngineConfig().mChatType != AgoraConstant.RTC_CHAT_VIDEO) {
//            return;
//        }

        // 男性
        if (ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
            ModuleMgr.getRtcEnginMgr().reqGetFeedbackInfo(new RequestComplete() {
                @Override
                public void onRequestComplete(HttpResponse response) {
                    if (!response.isOk()) return;
                    RtcComment comment = (RtcComment) response.getBaseData();
                    showRtcCommentAct(activity, comment);
                }
            });
            return;
        }

        // 女性
        ModuleMgr.getRtcEnginMgr().reqGetSettlement(new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (!response.isOk()) return;
                RtcComment comment = (RtcComment) response.getBaseData();
                showRtcCommentAct(activity, comment);
            }
        });
    }

    private static void showRtcCommentAct(Context activity, RtcComment comment) {
        Intent intent = new Intent(activity, RtcCommentActivity.class);
        intent.putExtra("RtcComment", comment);
        activity.startActivity(intent);
    }


    /****************************直播*****************************/

    private static AddFriendGiftDialog friendGiftDialog = null;

    /**
     * 直播
     * 送礼物底部弹框
     *
     * @param context
     * @param to_id   他人id
     */
    public static void showLiveBottomGiftDlg(final Context context, final String to_id, final OnSendGiftCallbackListener complete) {
        BottomGiftDialog dialog = null;
        if (ModuleMgr.getCommonMgr().getGiftLists().getArrCommonGifts().size() > 0) {
            dialog = new BottomGiftDialog();
            dialog.setCtx(context);
            dialog.setToId(Long.parseLong(to_id), "");
            dialog.setFromTag(Constant.OPEN_FROM_LIVE);
            dialog.setComplete(complete);
            dialog.showDialog((FragmentActivity) context);
        } else {
            LoadingDialog.show((FragmentActivity) context);
            ModuleMgr.getCommonMgr().requestGiftList(new GiftHelper.OnRequestGiftListCallback() {
                @Override
                public void onRequestGiftListCallback(boolean isOk) {
                    LoadingDialog.closeLoadingDialog();
                    if (isOk) {
                        if (ModuleMgr.getCommonMgr().getGiftLists().getArrCommonGifts().size() > 0) {
                            BottomGiftDialog dialog = new BottomGiftDialog();
                            dialog.setCtx(context);
                            dialog.setToId(Long.parseLong(to_id), "");
                            dialog.setFromTag(Constant.OPEN_FROM_LIVE);
                            dialog.setComplete(complete);
                            dialog.showDialog((FragmentActivity) context);
                        }
                    } else {
                        PToast.showShort(context.getString(R.string.net_error_retry));
                    }
                }
            });
        }
    }

    /**
     * 打开主播直播结束页面
     *
     * @param context
     * @param girlName    名称
     * @param girlHeadUrl 头像
     * @param liveBg      主播背景
     * @param isGirl      是不是主播
     * @param time        直播时长UNIX时间戳
     * @param charm       当场魅力总值
     * @param isFriend    是否是好友
     * @param uid         是否是好友
     * @param roomid      是否是好友
     */
    public static void showLiveStop(Context context, String girlName, String girlHeadUrl, String liveBg, boolean isGirl, long time, long charm, boolean isFriend, String uid, String roomid, String channel_uid) {
        Intent intent = new Intent();
        intent.putExtra("name", girlName);
        intent.putExtra("url", girlHeadUrl);
        intent.putExtra("isGirl", isGirl);
        intent.putExtra("liveBg", liveBg);
        intent.putExtra("time", time);
        intent.putExtra("charm", charm);
        intent.putExtra("isFriend", isFriend);
        intent.putExtra("uid", uid);
        intent.putExtra("roomid", roomid);
        intent.putExtra("channel_uid", channel_uid);

        intent.setClass(context, GirlLiveEndActivity.class);
        context.startActivity(intent);
    }

    /**
     * 主播结束直播，进入直播页面
     */
    public static void showLiveStop(Context context, String girlName, String girlHeadUrl, String liveBg, boolean isGirl, long time, long charm) {
        showLiveStop(context, girlName, girlHeadUrl, liveBg, isGirl, time, charm, true, "", "", "0");
    }


    /**
     * 打开直播搜索页面
     */
    public static void showSearchLive(Activity activity) {
        Intent intent = new Intent(activity, LiveSearchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    /**
     * 直播间 添加好友 弹出送礼物弹窗(给主播礼物)
     */
    public static void showAddFriendGiftDialog(final Context context, final String uid, final String roomid, final String channel_id, final OnSendGiftCallbackListener complete) {
        showAddFriendGiftDialog(context, uid, roomid, channel_id, complete, null, AddFriendGiftDialog.FROM_ZHIBO_ZHUBO);
    }

    /**
     * 直播间 添加好友 弹出送礼物弹窗(给用户礼物)
     */
    public static void showAddFriendGiftDialog(final Context context, final String uid, final String toUserId, final String channel_id, final RequestComplete complete2) {
        showAddFriendGiftDialog(context, uid, toUserId, channel_id, null, complete2, AddFriendGiftDialog.FROM_ZHIBO_USER);
    }

    /**
     * 直播间 添加好友 弹出送礼物弹窗(给用户&主播礼物)
     */
    private static void showAddFriendGiftDialog(final Context context, final String uid, final String roomid, final String channel_id, final OnSendGiftCallbackListener complete, final RequestComplete complete2, int from) {

        final Bundle bundle = new Bundle();
        bundle.putInt("from", from);

        friendGiftDialog = null;

        if (ModuleMgr.getCommonMgr().getGiftLists().getArrCommonGifts().size() > 0) {
            friendGiftDialog = new AddFriendGiftDialog();
            friendGiftDialog.setArguments(bundle);
            friendGiftDialog.setToId(Long.parseLong(uid), roomid, channel_id);
            friendGiftDialog.setGiftIds(context);
            friendGiftDialog.setRequestComplete(complete);
            friendGiftDialog.setRequestComplete2(complete2);
            friendGiftDialog.showDialog((FragmentActivity) context);
            return;
        }
        ModuleMgr.getCommonMgr().requestGiftList(new GiftHelper.OnRequestGiftListCallback() {
            @Override
            public void onRequestGiftListCallback(boolean isOk) {
                if (!isOk) {
                    PToast.showShort(context.getString(R.string.net_error_retry));
                    return;
                }

                if (ModuleMgr.getCommonMgr().getGiftLists().getArrCommonGifts().size() <= 0)
                    return;
                friendGiftDialog = new AddFriendGiftDialog();
                friendGiftDialog.setArguments(bundle);
                friendGiftDialog.setToId(Long.parseLong(uid), roomid, channel_id);
                friendGiftDialog.setGiftIds(context);
                friendGiftDialog.setRequestComplete(complete);
                friendGiftDialog.setRequestComplete2(complete2);
                friendGiftDialog.showDialog((FragmentActivity) context);
            }
        });
    }

    /**
     * 显示 用户资料卡 Dialog
     *
     * @param context
     */
    public static BaseDialogFragment showLiveUserCardDialog(FragmentActivity context, LiveUserDetail userDetail, LiveUserCardDialog.DissmissAddFirend dissmissAddFirend) {

        LiveUserCardDialog dialog = new LiveUserCardDialog();
        dialog.setmDissmissAddFirend(dissmissAddFirend);

        Bundle bundle = new Bundle();
        bundle.putSerializable("liveUserDetail", userDetail);

        dialog.setArguments(bundle);
        dialog.showDialog(context);

        return dialog;
    }

    /**
     * 显示举报对话框
     *
     * @param context
     * @param fromUid
     * @param toUid
     */
    public static void showLiveReportDialog(FragmentActivity context, String fromUid, String toUid, LiveReportDialog.OnItemClickListener listener) {

        LiveReportDialog dialog = new LiveReportDialog();
        dialog.setOnItemClickListener(listener);
        Bundle bundle = new Bundle();
        bundle.putString("fromUid", fromUid);
        bundle.putString("toUid", toUid);
        dialog.setArguments(bundle);

        dialog.showDialog(context);
    }

    public static void showLiveBaseDialog(FragmentActivity context, String info, String confirmStr, String cancelStr, LiveBaseDialog.OnItemClickListener listener) {
        new LiveBaseDialog().setInformation(info).setConfirmText(confirmStr).setCancelText(cancelStr)
                .setOnItemClickListener(listener)
                .showDialog(context);
    }

    /**
     * 直播间->点击魅力值->消费列表
     *
     * @param context
     * @param roomid
     * @return
     */
    public static BaseDialogFragment showLiveConsumeListDialog(FragmentActivity context, String roomid) {

        LiveConsumeListDialog dialog = new LiveConsumeListDialog();
        Bundle bundle = new Bundle();
        bundle.putString("roomid", roomid);
        dialog.setArguments(bundle);
        dialog.showDialog(context);
        dialog.setCancelable(true);

        return dialog;
    }

    /**
     * 我的页面分享
     */
    public static void showShareForPromotion(Context context) {
        showWebActivity(context, Hosts.H5_SHARE_PROMOTION);
    }

    /**
     * H5页面分享//赚钱
     */
    public static void showShareForH5(Context context) {
        showWebActivity(context, 3, Hosts.H5_SHARE_WANTMONEY, null);
    }

    /**
     * 用户资料页面分享
     */
    public static void showShareForUserinfo(Context context, UserDetail userDetail) {
        Intent intent = new Intent(context, ShareOtherInfo.class);
        intent.putExtra(CenterConstant.USER_CHECK_OTHER_KEY, userDetail);
        StackNode.appendIntent(context, intent);
        context.startActivity(intent);
    }

    /**
     * 显示保存分享图片弹框
     *
     * @param context
     * @param bitmap   要保存的图片
     * @param callBack 保存回调
     */
    public static void showShareDlg(FragmentActivity context, Bitmap bitmap, ScreenShot.SaveCallBack callBack) {
        if (bitmap != null && callBack != null) {
            ShareDlg dlg = new ShareDlg();
            dlg.setSave_bitmap(bitmap);
            dlg.setSaveCallBack(callBack);
            dlg.showDialog(context);
        } else if (bitmap == null) {
            PToast.showShort(context.getString(R.string.share_act_nodata));
        } else if (callBack == null) {
            PToast.showShort(context.getString(R.string.share_act_nocallback));
        }
    }

    /**
     * 分享赚钱、解锁弹框
     *
     * @param context              activity
     * @param bitmapPath           分享图片的路径
     * @param shareLink            分享的连接地址
     * @param content              分享连接展示的内容
     * @param title                分享连接展示的标题
     * @param shareContentImageUri 分享出去的网页连接消息展示的小图片的本地路径或者网络路径
     */
    public static void showShareDialog(FragmentActivity context, String bitmapPath, String shareLink, String content, String title, String shareContentImageUri, int uid,List<Integer> channels, String shareCode, int mould) {
        ShareDialog dialog = new ShareDialog();
        dialog.setSharedFilePath(bitmapPath);
        dialog.setShareLink(shareLink);
        dialog.setShareTitle(title);
        dialog.setShareContent(content);
        dialog.setShareContentImageUri(shareContentImageUri);
        dialog.setUid(uid);
        dialog.setShareCode(shareCode);
        dialog.setMould(mould);
        dialog.setShareChannels(channels);
        dialog.showDialog(context);
    }

    /**
     * 直播分享
     *
     * @param context              activity
     * @param channels             分享渠道 {@link ShareDialog#channels}
     *  @param listener 点击时微信，朋友圈....的回调
     */
    public static void showWebLinkShareDialog(FragmentActivity context,ArrayList<Integer> channels,ShareDialog.OnWebShareListener listener) {
        ShareDialog dialog = new ShareDialog();
        dialog.setShareChannels(channels);
        dialog.setShareListener(listener);
        dialog.showDialog(context);
    }

    /**
     * 启动微信
     */
    public static void startWexin() {

        IWXAPI api = WXAPIFactory.createWXAPI(context, "");
        if (!api.isWXAppInstalled()) {
            PToast.showShort("微信末安装");
            return;
        }
        Intent intent = new Intent();
        ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");// 报名该有activity
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(cmp);
        context.startActivity(intent);
    }

    /**
     * 显示要找的人弹窗
     */
    public static void showSureUserDlg(final FragmentActivity context) {
        showSureUserDlg(context, false);
    }

    /**
     * 显示要找的人弹窗
     *
     * @param showOther 弹框结束，是否显示其它弹框
     */
    public static void showSureUserDlg(final FragmentActivity context, final boolean showOther) {
        final long promotionuid = ModuleMgr.getCommonMgr().getPromotionuid();
        if (promotionuid != 0) {
            ModuleMgr.getCenterMgr().reqOtherInfo(promotionuid, new RequestComplete() {
                @Override
                public void onRequestComplete(HttpResponse response) {
                    if (response.isOk() && !response.isCache()) {
                        UserDetail userDetail = new UserDetail();
                        userDetail.parseJson(response.getResponseString());
                        if (userDetail.getGender() != ModuleMgr.getCenterMgr().getMyInfo().getGender()) {
                            if (ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
                                if (!ModuleMgr.getCommonMgr().checkDate(ModuleMgr.getCommonMgr().getSayHelloKey())
                                        || ModuleMgr.getCenterMgr().getMyInfo().isVip()) {
                                    showSureUserDlg(context, userDetail, promotionuid, showOther);
                                }
                            } else {
                                if (ModuleMgr.getCommonMgr().isMMoneyDlgShow()) {
                                    showSureUserDlg(context, userDetail, promotionuid, showOther);
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    /**
     * 显示要找的人弹窗
     */
    private static void showSureUserDlg(FragmentActivity context, UserDetail userDetail, long promotionuid, boolean showOther) {
        SureUserDlg dlg = new SureUserDlg();
        dlg.setUserDetail(userDetail);
        dlg.setUid(promotionuid);
        dlg.setShowOther(showOther);
        dlg.showDialog(context);
    }

    /**
     * 显示要找的直播间弹框
     *
     * @param liveTheme 直播主题
     */
    public static void showSureLiveRoomDlg(Context context, long uid, String liveTheme) {
        SureLiveRoomDlg dlg = new SureLiveRoomDlg();
        dlg.setData(uid, liveTheme);
        dlg.showDialog((FragmentActivity) context);
    }

    /**
     * 显示解锁聊天弹框
     *
     * @param activity
     */
    public static void showMessageUnlockDlg(FragmentActivity activity, Long touid) {
        UnlockMsgDlg unlockMsgDlg = new UnlockMsgDlg();
        unlockMsgDlg.setToUid(touid);
        unlockMsgDlg.showDialog(activity);
    }

    /**
     * 显示解锁聊天成功弹框
     *
     * @param activity
     * @param type     1,分享 2钻石
     */
    public static void showMessageUnlockOKDlg(FragmentActivity activity, int type) {
        UnlockMsgResultDlg unlockMsgDlg = new UnlockMsgResultDlg();
        unlockMsgDlg.setType(type);
        unlockMsgDlg.showDialog(activity);

        if (type == 1) StatisticsShareUnlock.onAlert_unlockchatsuccess_ok();
        if (type == 2) StatisticsShareUnlock.onAlert_sendsuccess_ok();
    }

    /**
     * 显示分享成功弹框
     *
     * @param activity
     */
    public static void showShareSuccessDlg(FragmentActivity activity) {
        ShareSuccessDialog successDialog = new ShareSuccessDialog();
        successDialog.showDialog(activity);
    }

    public static void showQQShareActivity(Activity activity) {
        show(activity, QQShareActivity.class);
    }
}
