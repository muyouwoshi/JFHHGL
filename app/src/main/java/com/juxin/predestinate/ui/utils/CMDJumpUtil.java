package com.juxin.predestinate.ui.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import com.juxin.library.request.DownloadListener;
import com.juxin.predestinate.bean.center.update.UpgradeSource;
import com.juxin.predestinate.module.local.mail.MailSpecialID;
import com.juxin.predestinate.module.local.statistics.SourcePoint;
import com.juxin.predestinate.module.local.statistics.StatisticsMessage;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.util.ApkUnit;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.agora.model.AgoraConstant;
import com.juxin.predestinate.ui.agora.util.VideoAudioChatHelper;
import com.juxin.predestinate.ui.user.auth.MyAuthenticationAct;
import java.net.URLDecoder;

/**
 * cmd跳转类
 * Created by Kind on 2017/10/19.
 */

public class CMDJumpUtil {

    private final static String URL_TYPE_UPLOAD_HEAD_PIC = "1";                         //上传头像
    private final static String URL_TYPE_COMPLETE_INFO = "2";                           //完善资料
    private final static String URL_TYPE_BIND_PHONE = "3";                              //绑定手机
    private final static String URL_TYPE_CONNECT_QQ_SERVICE = "4";                      //QQ客服
    private final static String URL_TYPE_VIDEO_CERTIFICATION = "video_certification";   //视频认证
    private final static String URL_TYPE_RECHARGE_YB = "recharge_yb";                   //充值Y币
    private final static String URL_TYPE_RECHARGE_VIP = "recharge_vip";                 //充值VIP
    private final static String URL_TYPE_RECHARGE_KEY = "recharge_key";                 //充值钥匙
    private final static String URL_TYPE_CHECK_UPDATE = "check_update";                 //检查升级

    private final static String URL_TYPE_CERTIFY_REAL_NAME = "certify_real_name";       //实名认证
    private final static String URL_TYPE_CERTIFY_PHONE = "certify_phone";               //手机认证
    private final static String URL_TYPE_INVITE_VIDEO = "invite_video";                 //发起视频聊天
    private final static String URL_TYPE_SEND_GIFT = "send_gift";                       //送礼提示
    private final static String URL_TYPE_JUMP_KF = "jump_kf";                           //跳转到小秘书聊天框
    private final static String URL_TYPE_JOIN_GROUP = "join_group";                     //加入公会
    // private final static String URL_TYPE_CERTIFY_IDCARD = "certify_idcard";          //身份认证
    private final static String URL_TYPE_VIEW_TITLE_RIGHT = "view_title_right";         //跳转查看特权
    private final static String URL_TYPE_VIEW_JUMP_TALK = "jump_talk";                  //解除密友消息点击咨询  跳转私聊页
    private final static String URL_TYPE_VIEW_CLOSE_FRIEND = "closefriend_info";        //密友等级标识  跳转密友列表

    private final static String URL_TYPE_URL_JUMP_WEBVIEW = "open_web_self?url=";       //open_web_self?url=%s 在软件内打开超级链接
    private final static String URL_TYPE_URL_JUMP_WEBBROWSER = "open_web_blank?url=";   //open_web_blank?url=%s" 外部打开超级链接

    private final static String[] DownExUrlProtocol = {"downex://", "downex1://", "downex2://", "downex3://"};
    private final static String[] DownExUrlReplace = {"http://", "http://", "ftp://", "https://"};


    private Context mContext;
    private String mUrl;
    private long otherID;
    private String channel_uid;

    public CMDJumpUtil(Context mContext, String url, long otherID, String channel_uid) {
        this.mContext = mContext;
        this.mUrl = getUrl(url);
        this.otherID = getOtherId(url, otherID);
        this.channel_uid = channel_uid;
    }

    private String getUrl(String url) {
        if (TextUtils.isEmpty(url)) return url;
        if (url.contains(URL_TYPE_VIEW_JUMP_TALK)) {
            return URL_TYPE_VIEW_JUMP_TALK;
        }
        return url;
    }

    private long getOtherId(String url, long otherID) {
        if (TextUtils.isEmpty(url)) return otherID;
        try {
            if (url.contains(URL_TYPE_VIEW_JUMP_TALK)) {
                otherID = Long.parseLong(url.substring(url.lastIndexOf("=") + 1, url.length()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return otherID;
    }


    public void onCMD() {
        try {
            if (TextUtils.isEmpty(mUrl)) return;
            switch (mUrl) {
                //上传头像
                case URL_TYPE_UPLOAD_HEAD_PIC:
                    UIShow.showUploadAvatarAct(mContext, false);
                    break;
                //完善资料
                case URL_TYPE_COMPLETE_INFO:
                    UIShow.showUserEditInfoAct(mContext);
                    break;
                //绑定手机
                case URL_TYPE_BIND_PHONE:
                    UIShow.showPhoneVerifyAct((FragmentActivity) mContext, MyAuthenticationAct.AUTHENTICSTION_REQUESTCODE);
                    break;
                //QQ客服
                case URL_TYPE_CONNECT_QQ_SERVICE:
                    UIShow.showQQService(mContext);
                    break;
                //视频认证
                case URL_TYPE_VIDEO_CERTIFICATION:
                    UIShow.showMyAuthenticationVideoAct((FragmentActivity) mContext, 0);
                    break;
                //充值Y币
                case URL_TYPE_RECHARGE_YB:
                    UIShow.showBuyCoinActivity(mContext);
                    break;
                case URL_TYPE_RECHARGE_KEY://购买钥匙
                    UIShow.showBuyKeyActivity(mContext);
                    break;
                //充值VIP
                case URL_TYPE_RECHARGE_VIP:
                    UIShow.showOpenVipActivity(mContext);
                    break;
                //检查升级
                case URL_TYPE_CHECK_UPDATE:
                    ModuleMgr.getCommonMgr().checkUpdate((FragmentActivity) App.getActivity(), UpgradeSource.Chat, true);
                    break;
                //实名认证
                case URL_TYPE_CERTIFY_REAL_NAME:
                    UIShow.showIDCardAuthenticationAct((FragmentActivity) App.getActivity(), 0);
                    break;
                //手机认证
                case URL_TYPE_CERTIFY_PHONE:
                    UIShow.showPhoneVerifyAct((FragmentActivity) App.getActivity(), MyAuthenticationAct.AUTHENTICSTION_REQUESTCODE);
                    break;
                //发起视频聊天
                case URL_TYPE_INVITE_VIDEO:
                    SourcePoint.getInstance().lockSource(App.activity, "");
                    VideoAudioChatHelper.getInstance().inviteVAChat((Activity) App.getActivity(),
                            otherID, AgoraConstant.RTC_CHAT_VIDEO, true, Constant.APPEAR_TYPE_NO, channel_uid, false, SourcePoint.getInstance().getSource());
                    break;
                //送礼提示
                case URL_TYPE_SEND_GIFT:
                    UIShow.showBottomGiftDlg(App.getActivity(), otherID, Constant.OPEN_FROM_CHAT_FRAME, channel_uid);
                    break;
                //跳转到小秘书聊天框
                case URL_TYPE_JUMP_KF:
                    UIShow.showPrivateChatAct(App.getActivity(), MailSpecialID.customerService.getSpecialID(), null);
                    break;
                //加入公会
                case URL_TYPE_JOIN_GROUP:
                    StatisticsMessage.chatAddUnion(otherID);
                    UIShow.showJoinGuildDlg((FragmentActivity) App.getActivity());
                    break;
                //身份认证
//                case URL_TYPE_CERTIFY_IDCARD:
//                    UIShow.showMyAuthenticationAct((FragmentActivity) App.getActivity(), 0);
//                    break;
                case URL_TYPE_VIEW_TITLE_RIGHT:
                    UIShow.showTitlePrivilegeAct(App.getActivity());
                    break;
                case URL_TYPE_VIEW_JUMP_TALK:
                    UIShow.showPrivateChatAct(App.getActivity(), otherID, "");
                    break;
                case URL_TYPE_VIEW_CLOSE_FRIEND:
                    UIShow.showMyChumActAct(App.getActivity());
                    break;
                default:
                    String url = getWebJumpUrl(true, mUrl);
                    if (!"".equals(url)) {
                        UIShow.showWebActivity(mContext, URLDecoder.decode(url, "UTF8"));
                        return;
                    }
                    url = getWebJumpUrl(false, mUrl);
                    if (!"".equals(url)) {
                        openWebOnDefBrowser(mContext, URLDecoder.decode(url, "UTF8"));
                        return;
                    }

                    int i = checkDownExUrl(mUrl);
                    //是否自定义下载协议
                    if (i >= 0) {
                        String sUrl = mUrl.replace(DownExUrlProtocol[i], DownExUrlReplace[i]);
                        ModuleMgr.getHttpMgr().downloadApk(sUrl, downloadListener);
                    } else {
                        UIShow.showWebActivity(mContext, mUrl);
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String getWebJumpUrl(Boolean isMyWebViwe, String Url) {
        String sStart = isMyWebViwe ? URL_TYPE_URL_JUMP_WEBVIEW : URL_TYPE_URL_JUMP_WEBBROWSER;
        if (Url.startsWith(sStart)) {
            return Url.replace(sStart, "");
        }
        return "";
    }

    private void openWebOnDefBrowser(Context context, String url) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

    /**
     * 检测URL是否自定义下载协议URL
     *
     * @param url
     * @return -1 不是自定义下载协议URL  >= 0 自定义协议头在DownExUrlProtocol数组中的下标索引值
     */
    private int checkDownExUrl(String url) {
        for (int i = DownExUrlProtocol.length - 1; i >= 0; i--) {
            if (url.startsWith(DownExUrlProtocol[i])) {
                return i;
            }
        }
        return -1;
    }

    private DownloadListener downloadListener = new DownloadListener() {
        @Override
        public void onStart(String url, String filePath) {
        }

        @Override
        public void onProcess(String url, int process, long size) {
        }

        @Override
        public void onSuccess(String url, String filePath) {
            ApkUnit.ExecApkFile(mContext, filePath);
        }

        @Override
        public void onFail(String url, Throwable throwable) {
        }
    };
}
