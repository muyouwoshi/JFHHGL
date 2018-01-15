package com.juxin.predestinate.ui.agora.util;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.juxin.library.log.PSP;
import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.bean.config.VideoVerifyBean;
import com.juxin.predestinate.bean.config.base.Video;
import com.juxin.predestinate.bean.my.InviteInfo;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.agora.act.bean.Invited;
import com.juxin.predestinate.ui.agora.model.AgoraConstant;
import com.juxin.predestinate.ui.agora.model.EngineConfig;
import com.juxin.predestinate.ui.discover.SendVideoDlg;
import com.juxin.predestinate.ui.mail.popup.VideoCardDlg;
import com.juxin.predestinate.ui.user.check.bean.VideoConfig;
import com.juxin.predestinate.ui.user.my.LookAtHerDlg;
import com.juxin.predestinate.ui.user.paygoods.GoodsConstant;
import com.juxin.predestinate.ui.user.util.CenterConstant;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.juxin.predestinate.module.logic.application.ModuleMgr.getCommonMgr;

/**
 * 音视频通话相关功能类
 * //此处视频语音是个大坑，开发前咨询相关人员
 */
public class VideoAudioChatHelper {

    private static final String RTC_SAVE_VCID = "VCID";   // 存储vcid到SP
    private static VideoAudioChatHelper instance;

    private int singleType;     // 非默认情况值, 0:还没选择,1:自己露脸，2:自己不露脸
    private boolean isGroupInvite = false;  // 用户是否处于群发状态
    private static long inviteTime = 0;

    private boolean isBeInvite = false;            //  用户是否处于被邀请状态
    private InviteInfo mInviteInfo;
    private boolean isInACall = false;                         //  是否在通话中
    private List<BaseMessage> dlgMessages = new ArrayList<>();  //  用于视频中保存弹框消息（视频结束之后设置为null）

    public static VideoAudioChatHelper getInstance() {
        if (instance == null) {
            synchronized (VideoAudioChatHelper.class) {
                if (instance == null)
                    instance = new VideoAudioChatHelper();
            }
        }
        return instance;
    }

    // ====================== 存储vcid  消息列表使用 ===============================
    private List<Long> getLongList() {
        try {
            String tmpStr = PSP.getInstance().getString(RTC_SAVE_VCID + App.uid, "");
            if (!TextUtils.isEmpty(tmpStr)) {
                return JSON.parseObject(tmpStr, new TypeReference<List<Long>>() {
                });
            }
        } catch (Exception e) {
        }
        return new ArrayList<>();
    }

    public boolean isContain(long vcID) {
        List<Long> longList = getLongList();
        if (longList.size() <= 0) {
            return false;
        }
        for (long tmp : longList) {
            if (vcID == tmp) {
                return true;
            }
        }
        return false;
    }

    public void remove(long vcID) {
        List<Long> longList = getLongList();
        if (longList.size() > 0) {
            longList.remove(vcID);
            PSP.getInstance().put(RTC_SAVE_VCID + App.uid, JSON.toJSONString(longList));
        }
    }

    private void addvcID(long vcID) {
        List<Long> longList = getLongList();
        if (longList == null) {
            longList = new ArrayList<>();
        }
        longList.add(vcID);
        PSP.getInstance().put(RTC_SAVE_VCID + App.uid, JSON.toJSONString(longList));
    }

    /**
     * 设置是否处于群发状态
     */
    public void setGroupInviteStatus(boolean isGroupInvite) {
        this.isGroupInvite = isGroupInvite;
    }

    public boolean getGroupInviteStatus() {
        return isGroupInvite;
    }

    /**
     * 处于群发要请时获取inviteTime
     */
    public long getInviteTime() {
        return inviteTime;
    }

    /**
     * 处于群发要请时设置inviteTime
     */
    public void setInviteTime(long inviteTime) {
        VideoAudioChatHelper.inviteTime = inviteTime;
    }

    /**
     * 设置是否处于群发状态
     */
    public void setIsInACall(boolean isInACall) {
        this.isInACall = isInACall;
    }

    /**
     * 获取是否正处于聊天中
     */
    public boolean isInACall() {
        return isInACall;
    }

    /**
     * 设置是否处于群发状态
     */
    public boolean isBeInvite() {
        return isBeInvite;
    }

    /**
     * 用于在视频中存储弹框消息
     */
    public void setDlgMessages(BaseMessage message) {
        if (message != null) {
            BaseMessage msg = new BaseMessage();
            msg.setJsonStr(message.getJsonStr());
            msg.setWhisperID(message.getWhisperID());
            msg.setType(message.getType());
            msg.setChannelID(message.getChannelID());
            dlgMessages.add(msg);
        }
    }

    /**
     * 视频后用于获取弹框消息
     */
    public List<BaseMessage> getDlgMessage() {
        return dlgMessages;
    }

    /**
     * 设置是否处于群发状态InviteInfo
     */
    public InviteInfo getmInviteInfo() {
        return mInviteInfo;
    }

    /**
     * 调起插件后重置状态
     */
    public void resetStatus() {
        isBeInvite = false;
        mInviteInfo = null;
    }
    // ========================================= 普通发起音视频聊天 ==================================

    public interface VideoCardBackCall {
        public boolean showDiamondDlg();//显示钻石充值

        public boolean showVipDlg();

        public boolean oldProcess(Activity context, int serviceSetVideoPrice, boolean isTip, long dstUid, int type, boolean flag, int singleType, String channel_uid, boolean isInvate, boolean isUseVideoCard, int videochat_len);//true 拦截 不往下执行 老流程
    }


    //视频卡爵位首页调用
    public interface VideoCardPeeragesHomeBackCall {
        public boolean showDiamondDlg();//显示钻石充值

        public boolean showVipDlg();

        public boolean oldProcess(Activity context, boolean isUseVideoCard, int videochat_len);//true 拦截 不往下执行 老流程
    }


    /**
     * 邀请对方音频或视频聊天
     *
     * @param context
     * @param dstUid
     * @param type
     * @param flag       判断是否显示进场dlg
     * @param singleType 非默认情况值, 0:还没选择,1:自己露脸，2:自己不露脸
     * @param isInvate   是否来自邀请他按钮，只有女号有。布局和出场方式 singleType 不同
     *                   isForceVideoCard 强制使用视频卡，如果true 不分a，b环境,false只有b环境用视频卡
     */
    public void inviteVAChat(final Activity context, final long dstUid, final int type, final boolean flag, final int singleType, final String channel_uid, final boolean isInvate, boolean isForceVideoCard, String source) {
        inviteVAChat(context, dstUid, type, flag, singleType, channel_uid, isInvate, null, isForceVideoCard, source);
    }

    public void inviteVAChat(final Activity context, final long dstUid, final int type, final boolean flag, final int singleType, final String channel_uid, final boolean isInvate, String source) {
        inviteVAChat(context, dstUid, type, flag, singleType, channel_uid, isInvate, null, false, source);
    }

    public void inviteVAChat(final Activity context, final long dstUid, final int type, final boolean flag, final int singleType, final String channel_uid, final boolean isInvate, final VideoCardBackCall backCall, String source) {
        inviteVAChat(context, dstUid, type, flag, singleType, channel_uid, isInvate, backCall, false, source);
    }

    public void inviteVAChat(final Activity context, final long dstUid, final int type
            , final boolean flag, final int singleType, final String channel_uid, final boolean isInvate
            , final VideoCardBackCall backCall, boolean isForceVideoCard, final String source) {
        boolean isMan = ModuleMgr.getCenterMgr().getMyInfo().isMan();  // 女号不弹框
        UserDetail userDetail = ModuleMgr.getCenterMgr().getMyInfo();
        Video video = getCommonMgr().getCommonConfig().getVideo();
        final boolean isVip = (video.isVideoCallNeedVip() && !userDetail.isVip());
        if (type == AgoraConstant.RTC_CHAT_VIDEO && isMan && (isForceVideoCard || ModuleMgr.getCenterMgr().getMyInfo().isB())) {
            final int diamandCount = ModuleMgr.getCenterMgr().getMyInfo().getDiamand();

            HashMap<String, Object> post_param = new HashMap<>();
            post_param.put("uid", dstUid);
            ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqVideoChatConfig, post_param, new RequestComplete() {
                @Override
                public void onRequestComplete(HttpResponse response) {
                    if (response.isOk()) {
                        VideoConfig setting = (VideoConfig) response.getBaseData();
                        if (!isVip) {//是vip
                            if (diamandCount >= setting.getVideoPrice()) {//有钻石,走老流程
                                if (backCall != null) {
                                    if (!backCall.oldProcess(context, setting.getVideoPrice(), isVip, dstUid, type, flag, singleType, channel_uid, isInvate, false, 0)) {
                                        inviteVAChatOld(context, dstUid, type, flag, singleType, channel_uid, isInvate, false, 0, source);
                                    }
                                } else {
                                    inviteVAChatOld(context, dstUid, type, flag, singleType, channel_uid, isInvate, false, 0, source);
                                }
                            } else {//检测视频卡
                                checkVideoCard(context, diamandCount, setting.getVideoPrice(), dstUid, type, channel_uid, isVip, flag, isInvate, source, backCall);
                            }
                        } else {//检测视频卡
                            checkVideoCard(context, diamandCount, setting.getVideoPrice(), dstUid, type, channel_uid, isVip, flag, isInvate, source, backCall);
                        }
                    } else {
                        JSONObject jo = response.getResponseJson();
                        PToast.showShort(TextUtils.isEmpty(jo.optString("msg")) ? "数据异常" : jo.optString("msg"));
                    }
                }
            });
        } else {
            if (backCall != null && isMan) {

                HashMap<String, Object> post_param = new HashMap<>();
                post_param.put("uid", dstUid);
                ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.reqVideoChatConfig, post_param, new RequestComplete() {

                    @Override
                    public void onRequestComplete(HttpResponse response) {
                        if (response.isOk()) {
                            VideoConfig setting = (VideoConfig) response.getBaseData();
                            if (!backCall.oldProcess(context, setting != null ? setting.getVideoPrice() : 20, isVip, dstUid, type, flag, singleType, channel_uid, isInvate, false, 0)) {
                                inviteVAChatOld(context, dstUid, type, flag, singleType, channel_uid, isInvate, false, 0, source);
                            }
                        } else {
                            JSONObject jo = response.getResponseJson();
                            PToast.showShort(TextUtils.isEmpty(jo.optString("msg")) ? "数据异常" : jo.optString("msg"));
                        }
                    }
                });
            } else {
                inviteVAChatOld(context, dstUid, type, flag, singleType, channel_uid, isInvate, false, 0, source);
            }
        }
    }


    //爵位版首页逻辑

    /**
     * 邀请对方音频或视频聊天
     *
     * @param context
     * @param dstUid
     * @param type
     * @param flag       判断是否显示进场dlg
     * @param singleType 非默认情况值, 0:还没选择,1:自己露脸，2:自己不露脸
     * @param isInvate   是否来自邀请他按钮，只有女号有。布局和出场方式 singleType 不同
     */
    public void peeragesHome(final Activity context, final long dstUid, final int type
            , final boolean flag, final int singleType, final String channel_uid, final boolean isInvate, final VideoCardPeeragesHomeBackCall backCall, final UserInfoLightweight userInfoLightweight, final String source) {
        if (ModuleMgr.getCenterMgr().getMyInfo().isMan()) {//男
            //Video video = getCommonMgr().getCommonConfig().getVideo();
            //if (!video.isVideoCallNeedVip()) {//不需要vip
                peeragesHomeVideoCardCheck(context, dstUid, type, flag, singleType, channel_uid, isInvate, backCall, userInfoLightweight, source);
           // } else {
                //if (userInfoLightweight.getNobility_rank() >= (  )) {//是否大于1星勋爵
                    /*if (ModuleMgr.getCenterMgr().getMyInfo().isVip()) {//是vip
                        peeragesHomeVideoCardCheck(context, dstUid, type, flag, singleType, channel_uid, isInvate, backCall, userInfoLightweight, source);
                    } else {
                        peeragesHomeCheckVideoCard(context, dstUid, type, flag, singleType, channel_uid, isInvate, backCall, userInfoLightweight, source);
                    }*/
                /*} else {
                    peeragesHomeVideoCardCheck(context, dstUid, type, flag, singleType, channel_uid, isInvate, backCall, userInfoLightweight, source);
                }*/

            //}
        } else {
            inviteVAChatOld(context, dstUid, type, flag, singleType, channel_uid, isInvate, false, 0, source);
        }

    }

    //红色视频卡弹窗
    private void peeragesHomeCheckVideoCard(final Activity context, final long dstUid, final int type
            , final boolean flag, final int singleType, final String channel_uid, final boolean isInvate, final VideoCardPeeragesHomeBackCall backCall, final UserInfoLightweight userInfoLightweight, final String source) {
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.GET_VIDEOCARDCOUNT, null, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                final JSONObject joCount = response.getResponseJson();
                if (response.isOk()) {
                    final int count = joCount.optJSONObject("res").optInt("count", 0);
                    final int videochat_len = joCount.optJSONObject("res").optInt("videochat_len", 0);
                    if (count > 0) {//有视频卡 ,弹出视频卡
                        final VideoCardDlg dlg = new VideoCardDlg();
                        dlg.setCountTimeLength(dstUid, count, videochat_len);
                        dlg.setCall(new VideoCardDlg.BackCall() {
                            @Override
                            public void call(boolean isUseVideoCard) {
                                dlg.dismiss();
                                if (isUseVideoCard) {//使用视频卡  拨打
                                    if (backCall != null) {
                                        if (!backCall.oldProcess(context, isUseVideoCard, videochat_len)) {
                                            DialCallback(context, type, isUseVideoCard, videochat_len, userInfoLightweight, source);
                                        }
                                    } else {
                                        DialCallback(context, type, isUseVideoCard, videochat_len, userInfoLightweight, source);
                                    }
                                } else {//老流程 vip  钻石之类的
                                    final UserDetail userDetail = ModuleMgr.getCenterMgr().getMyInfo();
                                    final Video video = getCommonMgr().getCommonConfig().getVideo();
                                    //final boolean isVip = (video.isVideoCallNeedVip() && !userDetail.isVip());
                                    final int diamandCount = ModuleMgr.getCenterMgr().getMyInfo().getDiamand();

                                    VideoCardBackCall call = new VideoCardBackCall() {//转换接口类型

                                        @Override
                                        public boolean showDiamondDlg() {
                                            if (backCall != null) {
                                                return backCall.showDiamondDlg();
                                            }
                                            return false;
                                        }

                                        @Override
                                        public boolean showVipDlg() {
                                            if (backCall != null) {
                                                return backCall.showVipDlg();
                                            }
                                            return false;
                                        }

                                        @Override
                                        public boolean oldProcess(Activity context, int serviceSetVideoPrice, boolean isTip, long dstUid, int type, boolean flag, int singleType, String channel_uid, boolean isInvate, boolean isUseVideoCard, int videochat_len) {
                                            if (backCall != null) {
                                                return backCall.oldProcess(context, isUseVideoCard, videochat_len);
                                            }
                                            return false;
                                        }
                                    };
                                    checkVipDiamondsNext(context, video.isVideoCallNeedVip() && !userDetail.isVip(), diamandCount, userInfoLightweight.getPrice(), dstUid, type, flag, singleType, channel_uid, isInvate, source, call);
                                }
                            }
                        });
                        dlg.showDialog((BaseActivity) context);
                    } else {//弹出vip
                        UserDetail userDetail = new UserDetail();
                        userDetail.setAvatar(userInfoLightweight.getAvatar());
                        userDetail.setNickname(userInfoLightweight.getNickname());
                        userDetail.setAge(userInfoLightweight.getAge());
                        userDetail.setGender(userInfoLightweight.getGender());
                        userDetail.getChatInfo().setVideoPrice(userInfoLightweight.getPrice());
                        userDetail.setGroup(userInfoLightweight.getGroup());
                        userDetail.getNobilityInfo().setRank(userInfoLightweight.getNobility_rank());
                        if (backCall != null) {
                            if (!backCall.showVipDlg()) {
                                UIShow.showCallingVipDialog(userDetail, userInfoLightweight.getDistance());
                            }
                        } else {
                            UIShow.showCallingVipDialog(userDetail, userInfoLightweight.getDistance());
                        }
                    }
                } else {
                    JSONObject jo = response.getResponseJson();
                    PToast.showShort(TextUtils.isEmpty(jo.optString("msg")) ? "数据异常" : jo.optString("msg"));
                }
            }
        });
    }

    //不需要vip逻辑 小窗灰色头的视频卡
    private void peeragesHomeVideoCardCheck(final Activity context, final long dstUid, final int type
            , final boolean flag, final int singleType, final String channel_uid, final boolean isInvate, final VideoCardPeeragesHomeBackCall backCall, final UserInfoLightweight userInfoLightweight, final String source) {
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.GET_VIDEOCARDCOUNT, null, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                final JSONObject joCount = response.getResponseJson();
                if (response.isOk()) {
                    final int diamandCount = ModuleMgr.getCenterMgr().getMyInfo().getDiamand();//我自己的钻
                    final int count = joCount.optJSONObject("res").optInt("count", 0);
                    final int videochat_len = joCount.optJSONObject("res").optInt("videochat_len", 0);

                    if (count > 0) {//有视频卡
                        final SendVideoDlg dlg = new SendVideoDlg();
                        dlg.viewSwitchVipDiamonds(count, userInfoLightweight.getPrice());
                        dlg.setCallAndPrice(new SendVideoDlg.BackCall() {
                            @Override
                            public void call() {
                                dlg.dismiss();
                                if (dlg.SendType == 0) {//视频卡接受或者拨打视频  并判断是否有效
                                    if (backCall != null) {
                                        if (!backCall.oldProcess(context, true, videochat_len)) {
                                            DialCallback(context, type, true, videochat_len, userInfoLightweight, source);
                                        }
                                    } else {
                                        DialCallback(context, type, true, videochat_len, userInfoLightweight, source);
                                    }
                                } else {
                                    if (userInfoLightweight.getPrice() <= diamandCount) {//钻石接受或者拨打视频 并判断是否有效
                                        if (backCall != null) {
                                            if (!backCall.oldProcess(context, true, videochat_len)) {
                                                DialCallback(context, type, false, 0, userInfoLightweight, source);
                                            }
                                        } else {
                                            DialCallback(context, type, false, 0, userInfoLightweight, source);
                                        }
                                    }/*else{//去购买钻石
                                                    PToast.showShort("去购买钻石逻辑");
                                                }*/
                                }
                            }
                        }, userInfoLightweight.getPrice(), SendVideoDlg.VIDEO, userInfoLightweight);
                        dlg.showDialog((BaseActivity) context);
                    } else {//判断钻石是否充足

                        if (userInfoLightweight.getPrice() <= diamandCount) {//钻石充足
                            final SendVideoDlg dlg = new SendVideoDlg();
                            dlg.setViewType(SendVideoDlg.viewVideoSend);
                            dlg.setCallAndPrice(new SendVideoDlg.BackCall() {

                                @Override
                                public void call() {//判断是接听还是拨打
                                    dlg.dismiss();
                                    if (backCall != null) {
                                        if (!backCall.oldProcess(context, true, videochat_len)) {
                                            DialCallback(context, type, false, 0, userInfoLightweight, source);
                                        }
                                    } else {
                                        DialCallback(context, type, false, 0, userInfoLightweight, source);
                                    }
                                }
                            }, userInfoLightweight.getPrice(), SendVideoDlg.VIDEO, userInfoLightweight);
                            dlg.showDialog((BaseActivity) context);
                        } else {//钻石不足
                            if (backCall != null) {
                                if (!backCall.showDiamondDlg()) {
                                    UIShow.showGoodsDiamondDialogAndTag(context, Constant.OPEN_FROM_CHAT_PLUGIN, dstUid, channel_uid);
                                }
                            } else {
                                UIShow.showGoodsDiamondDialogAndTag(context, Constant.OPEN_FROM_CHAT_PLUGIN, dstUid, channel_uid);
                            }
                        }
                    }


                } else {
                    PToast.showShort(TextUtils.isEmpty(joCount.optString("msg")) ? "数据异常" : joCount.optString("msg"));
                }
            }
        });
    }


    private void DialCallback(final Activity activity, final int type, final boolean isUseVideoCard, final int videochat_len, final UserInfoLightweight userInfoLightweight, final String source) {
        if (VideoAudioChatHelper.getInstance().inviteCancel(userInfoLightweight.getInvite_id())) {
            userInfoLightweight.setInvite_id(0);
            VideoAudioChatHelper.getInstance().inviteVAChatPeeragesHomeOld(activity, userInfoLightweight.getUid(), AgoraConstant.RTC_CHAT_VIDEO,
                    true, Constant.APPEAR_TYPE_NO, "", false, isUseVideoCard, videochat_len, source);
        } else {
            // 回应邀请
            int show = PSP.getInstance().getInt(ModuleMgr.getCommonMgr().getPrivateKey(Constant.APPEAR_FOREVER_TYPE), 0);
            VideoAudioChatHelper.getInstance().acceptInviteVAChat(userInfoLightweight.getInvite_id(), isUseVideoCard, show, false, source, new RequestComplete() {
                @Override
                public void onRequestComplete(HttpResponse response) {
                    userInfoLightweight.setInvite_id(0);
                    if (response.isOk()) {
                        if (type == AgoraConstant.RTC_CHAT_VIDEO) {
                            ModuleMgr.getRtcEnginMgr().getEngineConfig().isUseVideoCard = isUseVideoCard;
                            ModuleMgr.getRtcEnginMgr().getEngineConfig().videoCardTime = videochat_len;
                        }
                        return;
                    }


                    if(checkHeightPeerages(response,isUseVideoCard,type,userInfoLightweight.getUid(),source) ){
                        return;
                    }
                    //拨打
                    VideoAudioChatHelper.getInstance().inviteVAChatPeeragesHomeOld(activity, userInfoLightweight.getUid(), AgoraConstant.RTC_CHAT_VIDEO,
                            true, Constant.APPEAR_TYPE_NO, "", false, isUseVideoCard, videochat_len, source);

                }
            });
        }
    }

    public boolean checkHeightPeerages(HttpResponse response,boolean isUseVideoCard,int type,long uid,
                                       String source){
        JSONObject jo = response.getResponseJson();
        int code = jo.optInt("code");
        if(code == AgoraConstant.RTC_VIDEOCHAT_NEED_VIP && !isUseVideoCard && type ==AgoraConstant.RTC_CHAT_VIDEO
                && ("faxian_yuliao_listitem_video".equals(source) || "tip_video_accept".equals(source))){
            UIShow.showCallingVipDialog(uid);
            return true;
        }
       return false;
    }

    private void inviteVAChatPeeragesHomeOld(final Activity context, long dstUid, int type, boolean flag, int singleType, String channel_uid, boolean isInvate, boolean isUseVideoCard, int videochat_len, String source) {
        PSP.getInstance().put("ISINVITE", false);
        boolean isMan = ModuleMgr.getCenterMgr().getMyInfo().isMan();  // 女号不弹框
        if (isInvate || (isMan && flag && AgoraConstant.RTC_CHAT_VIDEO == type &&
                PSP.getInstance().getInt(ModuleMgr.getCommonMgr().getPrivateKey(Constant.APPEAR_FOREVER_TYPE), 0) == 0)) {
            UIShow.showLookAtHerDlg(context, dstUid, channel_uid, isInvate, isUseVideoCard, videochat_len, source, new LookAtHerDlg.BackCall() {
                @Override
                public void call(Activity context, long dstUid, int type, boolean flag, int singleType, String channel_uid, boolean isInvate, boolean isUseVideoCard, int videochat_len, String source) {
                    VideoAudioChatHelper.this.singleType = singleType;
                    executeInviteChat(context, dstUid, type, channel_uid, isUseVideoCard, videochat_len, source);
                }
            });
            return;
        }
        this.singleType = singleType;
        executeInviteChat(context, dstUid, type, channel_uid, isUseVideoCard, videochat_len, source);
    }

    //一般情况下调用上面的 的方法,是老的视频逻辑，现在一般先要做视频卡检测
    public void inviteVAChatOld(final Activity context, long dstUid, int type, boolean flag, int singleType, String channel_uid, boolean isInvate, boolean isUseVideoCard, int videochat_len, String source) {
        PSP.getInstance().put("ISINVITE", false);
        boolean isMan = ModuleMgr.getCenterMgr().getMyInfo().isMan();  // 女号不弹框
        if (isInvate || (isMan && flag && AgoraConstant.RTC_CHAT_VIDEO == type &&
                PSP.getInstance().getInt(ModuleMgr.getCommonMgr().getPrivateKey(Constant.APPEAR_FOREVER_TYPE), 0) == 0)) {

            // 男性，且不使用视频卡，进行以下检测
            if (isMan && !isUseVideoCard) {
                // vip检测
//                if ((ModuleMgr.getCommonMgr().getCommonConfig().getVideo().isVideoCallNeedVip()
//                        && !ModuleMgr.getCenterMgr().getMyInfo().isVip())) {
//                    UIShow.showGoodsVipBottomDlg(context, GoodsConstant.DLG_VIP_AV_CHAT);
//                    return;
//                }

                // 钻石检测
                if (ModuleMgr.getCenterMgr().getMyInfo().getDiamand() <= 0) {
                    UIShow.showGoodsDiamondBottomDlg(context);
                    return;
                }
            }
            UIShow.showLookAtHerDlg(context, dstUid, channel_uid, isInvate, isUseVideoCard, videochat_len,source);
            return;
        }
        this.singleType = singleType;
        inviteVAChat(context, dstUid, type, channel_uid, isUseVideoCard, videochat_len, source);
    }

    /**
     * 视频卡检测
     */
    private void checkVideoCard(final Activity context, final int diamandCount, final int serviceSetVideoPrice, final long dstUid, final int type, final String channel_uid, final boolean isTip, final boolean flag, final boolean isInvate, final String source, final VideoCardBackCall backCall) {
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.GET_VIDEOCARDCOUNT, null, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                final JSONObject joCount = response.getResponseJson();
                if (response.isOk()) {
                    final int count = joCount.optJSONObject("res").optInt("count", 0);
                    final int videochat_len = joCount.optJSONObject("res").optInt("videochat_len", 0);
                    if (joCount.optJSONObject("res") != null && count > 0) {
                        //是否有视频卡，如果有,弹出视频卡
                        VideoCardDlg dlg = new VideoCardDlg();
                        dlg.setCountTimeLength(dstUid, count, videochat_len);
                        dlg.setCall(new VideoCardDlg.BackCall() {
                            @Override
                            public void call(boolean isUseVideoCard) {
                                if (isUseVideoCard) {
                                    if (backCall != null) {
                                        if (!backCall.oldProcess(context, serviceSetVideoPrice, isTip, dstUid, type, flag, singleType, channel_uid, isInvate, isUseVideoCard, videochat_len)) {//false 代表上层方法直接返回的false，没做任何处理，true代表上层处理了，目前暂时 返回true指的是聊天列表页的回拨 ChatPanelInvite 引用uishow.showInvitaExpiredDlg方法
                                            inviteVAChatOld(context, dstUid, type, flag, singleType, channel_uid, isInvate, isUseVideoCard, videochat_len, source);
                                        }
                                    } else {
                                        inviteVAChatOld(context, dstUid, type, flag, singleType, channel_uid, isInvate, isUseVideoCard, videochat_len, source);
                                    }
                                } else {
                                    checkVipDiamondsNext(context, isTip, diamandCount, serviceSetVideoPrice, dstUid, type, flag, singleType, channel_uid, isInvate, source, backCall);
                                }
                            }
                        });
                        dlg.showDialog((BaseActivity) context);
                    } else {
                        //老流程
                        checkVipDiamondsNext(context, isTip, diamandCount, serviceSetVideoPrice, dstUid, type, flag, singleType, channel_uid, isInvate, source, backCall);
                    }
                } else {
                    PToast.showShort(TextUtils.isEmpty(joCount.optString("msg")) ? "数据异常" : joCount.optString("msg"));
                }
            }
        });
    }

    /**
     * 检测vip，钻石并走老流程
     *
     * @param context
     * @param isTip
     * @param diamandCount
     * @param dstUid
     * @param type
     * @param flag
     * @param singleType
     * @param channel_uid
     * @param isInvate
     */
    public void checkVipDiamondsNext(final Activity context, boolean isTip, int diamandCount, int serviceSetVideoPrice, final long dstUid, final int type, final boolean flag, final int singleType, final String channel_uid, final boolean isInvate, final String source, final VideoCardBackCall backCall) {
//        if (isTip) {//不是会员弹出会员
//            if (backCall != null) {
//                if (!backCall.showVipDlg()) {
//                    UIShow.showGoodsVipBottomDlg(context, GoodsConstant.DLG_VIP_AV_CHAT);
//                }
//            } else {
//                UIShow.showGoodsVipBottomDlg(context, GoodsConstant.DLG_VIP_AV_CHAT);
//            }
//            return;
//        }

        if (diamandCount < serviceSetVideoPrice) {
            if (backCall != null) {
                if (!backCall.showDiamondDlg()) {
                    UIShow.showGoodsDiamondDialogAndTag(context, Constant.OPEN_FROM_CHAT_PLUGIN, dstUid, channel_uid);
                }
            } else {
                //showNeedDiamondsDlg(context, diamandCount, dstUid, channel_uid);
                UIShow.showGoodsDiamondDialogAndTag(context, Constant.OPEN_FROM_CHAT_PLUGIN, dstUid, channel_uid);
            }
            return;
        }
        if (backCall != null) {
            if (!backCall.oldProcess(context, serviceSetVideoPrice, isTip, dstUid, type, flag, singleType, channel_uid, isInvate, false, 0)) {
                inviteVAChatOld(context, dstUid, type, flag, singleType, channel_uid, isInvate, false, 0, source);
            }
        } else {
            inviteVAChatOld(context, dstUid, type, flag, singleType, channel_uid, isInvate, false, 0, source);
        }
    }

    /**
     * 邀请对方音频或视频聊天：兼容老接口
     */
    public void inviteVAChat(final Activity context, long dstUid, int type, String channel_uid, String source) {
        inviteVAChat(context, dstUid, type, channel_uid, false, 0, source);
    }

    public void inviteVAChat(final Activity context, long dstUid, int type, String channel_uid, boolean isUseVideoCard, int videochat_len, String source) {
        UserDetail userDetail = ModuleMgr.getCenterMgr().getMyInfo();
        if (userDetail.isMan() && !isUseVideoCard) {
            boolean isTip = false;
            switch (type) {
                case AgoraConstant.RTC_CHAT_VIDEO:
                    isTip = ModuleMgr.getCommonMgr().getCommonConfig().getVideo().isVideoCallNeedVip() && !userDetail.isVip();
                    break;
                case AgoraConstant.RTC_CHAT_VOICE:
                    isTip = ModuleMgr.getCommonMgr().getCommonConfig().getVideo().isAudioCallNeedVip() && !userDetail.isVip();
                    break;
                default:
                    break;
            }

//            if (isTip) {
//                UIShow.showGoodsVipBottomDlg(context, GoodsConstant.DLG_VIP_AV_CHAT);
//                return;
//            }
        }
        executeInviteChat(context, dstUid, type, channel_uid, isUseVideoCard, videochat_len, source);
    }

    /**
     * 发起音视频聊天，获取聊天渠道vcId, 消息版本号msgVer
     */
    public void executeInviteChat(final Context context, final long dstUid, final int type, final String channel_uid
            , final boolean isUseVideoCard, final int videochat_len, final String source) {
        if (ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
            // TODO: 2017/9/1 全量分支放开以下注释
//            RtcMessageMgr.getInstance().release(); // 清理同步对象
            ModuleMgr.getRtcEnginMgr().reqInviteChat(type, dstUid, isUseVideoCard ? 1 : 0, source, new RequestComplete() {
                @Override
                public void onRequestComplete(HttpResponse response) {
                    handleInviteChat(context, response, dstUid, type, channel_uid, isUseVideoCard, videochat_len,source);
                }
            });
            return;
        }
        girlSingleInvite((Activity) context, dstUid, type, source);
    }

    private void handleInviteChat(final Context context, HttpResponse response, final long dstUid, final int type, String channel_uid
            , final boolean isUseVideoCard, final int videochat_len,final String source) {
        //特殊错误码: 3001 用户正在视频聊天中 3002 该用户无法视频聊天 3003 钻石余额不足
        JSONObject jo = response.getResponseJson();
        int code = jo.optInt("code");

        if (response.isOk() || (isUseVideoCard && code == 3003)) {
            final Invited invited = (Invited) response.getBaseData();
            initEnginConfig(invited.getVcId(), dstUid, AgoraConstant.RTC_CHAT_INVITE, type, invited.getMsgVer(), isUseVideoCard, videochat_len);
            // TODO: 2017/9/1 全量分支放开以下注释
//            UIShow.showRtcInitAct((Activity) context);
            addvcID(invited.getVcId());
            return;
        }

        if(checkHeightPeerages(response,isUseVideoCard,type,dstUid,source) ){
            return;
        }

        if(code == AgoraConstant.RTC_USER_CHATING) {//上层已判断自身为男号，弹出女方忙线...
            UIShow.showBusyRandomDlg(context, dstUid, type, isUseVideoCard,source);
            return;
        }

        if (code == AgoraConstant.RTC_USER_NOT_PAY)
            UIShow.showGoodsDiamondDialogAndTag(context, Constant.OPEN_FROM_CHAT_PLUGIN, dstUid, channel_uid);
        PToast.showShort(response.getMsg());
    }

    // ====================== 女性版： 单邀，群邀音视频聊天 ===============================

    /**
     * 女性： 发起单独邀请
     */
    public void girlSingleInvite(final Activity activity, final long dstUid, final int type, String source) {
        // TODO: 2017/9/1 全量分支放开以下注释
//        RtcMessageMgr.getInstance().release(); // 清理同步对象
        ModuleMgr.getRtcEnginMgr().reqGirlSingleInvite(dstUid, type, source, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                handleSingleInvite(activity, response, dstUid, type);
            }
        });
    }

    private void handleSingleInvite(final Context context, HttpResponse response, final long dstUid, final int type) {
        int price = ModuleMgr.getCenterMgr().getMyInfo().getChatInfo().getAudioPrice();
        if (type == AgoraConstant.RTC_CHAT_VIDEO) {
            price = ModuleMgr.getCenterMgr().getMyInfo().getChatInfo().getVideoPrice();
        }

        if (response.isOk()) {
            Invited invited = (Invited) response.getBaseData();
            addvcID(invited.getVcId());

            initGirlEnginConfig(invited.getVcId(), dstUid, AgoraConstant.RTC_CHAT_INVITE, type, invited.getMsgVer(),
                    AgoraConstant.RTC_CHAT_SINGLE, price, 0);
            // TODO: 2017/9/1 全量分支放开以下注释
//            UIShow.showRtcGroupInitAct((Activity) context);
            return;
        }

        JSONObject jo = response.getResponseJson();
        int code = jo.optInt("code");
        if (code == AgoraConstant.RTC_USER_NOT_PAY)
            UIShow.showGoodsDiamondDialogAndTag(context, Constant.OPEN_FROM_CHAT_PLUGIN, dstUid, "");
        PToast.showShort(response.getMsg());
    }

    /**
     * 女性版： 发起群聊邀请
     */
    public void girlGroupInvite(final Activity activity, final int type) {
        // 处于群邀状态，不处理其他群邀请求
        if (getGroupInviteStatus()) {
            PToast.showShort(App.context.getString(R.string.invite_status));
            return;
        }
        if (ModuleMgr.getRtcEnginMgr().getEngineConfig().mHasJoin) {
            PToast.showShort(App.context.getString(R.string.call_status));
            return;
        }

        VideoVerifyBean verifyBean = ModuleMgr.getCommonMgr().getVideoVerify();
        // 如果没有开启音视频弹框，弹窗去开启
        if ((type == AgoraConstant.RTC_CHAT_VIDEO && !verifyBean.getBooleanVideochat())
                || (type == AgoraConstant.RTC_CHAT_VOICE && !verifyBean.getBooleanAudiochat())) {
            UIShow.showOpenVaDlg((FragmentActivity) activity, type);
            return;
        }
        setGroupInviteStatus(true);
        // TODO: 2017/9/1 全量分支放开以下注释
//        RtcMessageMgr.getInstance().release(); // 清理同步对象
        ModuleMgr.getRtcEnginMgr().reqGirlGroupInvite(type,new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                handleGroupInvite(response, activity, type);
            }
        });
    }

    private void handleGroupInvite(HttpResponse response, Activity activity, int type) {
        int price = ModuleMgr.getCenterMgr().getMyInfo().getChatInfo().getAudioPrice();
        if (type == AgoraConstant.RTC_CHAT_VIDEO) {
            price = ModuleMgr.getCenterMgr().getMyInfo().getChatInfo().getVideoPrice();
        }
        JSONObject jo = response.getResponseJson();
        if (response.isOk()) {
            JSONObject resJo = jo.optJSONObject("res");
            long inviteId = resJo.optLong("invite_id");
            initGirlEnginConfig(0, 0, AgoraConstant.RTC_CHAT_INVITE, type, AgoraConstant.RTC_MSG_VER,
                    AgoraConstant.RTC_CHAT_GROUP, price, inviteId);
            // TODO: 2017/9/1 全量分支放开以下注释
//            UIShow.showRtcGroupInitAct(activity);
            return;
        }
        setGroupInviteStatus(false);
        PToast.showShort(response.getMsg());
    }

    // ============================== 音视频受邀方 ===================================

    public void acceptInviteVAChat(final long inviteId, int selectVal, final String source) {
        acceptInviteVAChat(inviteId, selectVal, true, source, null);
    }

    /**
     * 男性：接受女性发来的群邀请求
     *
     * @param inviteId 邀请id,即为邀请流水号，接受邀请并发起视频的时候使用
     */
    public void acceptInviteVAChat(final long inviteId, int selectVal, final boolean showToast, final String source, final RequestComplete complete) {
        acceptInviteVAChat(inviteId, false, selectVal, showToast, source, complete);
    }

    public void acceptInviteVAChat(final long inviteId, boolean isUseVideoCard, int selectVal, final boolean showToast, final String source, final RequestComplete complete) {
        PSP.getInstance().put("ISINVITE", true);
        this.singleType = selectVal;
        LoadingDialog.show((FragmentActivity) App.activity, "加入中...");
        ModuleMgr.getRtcEnginMgr().reqGirlAcceptChat(inviteId, isUseVideoCard, source, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (complete != null) {
                    complete.onRequestComplete(response);
                }

                if (response.isOk()) {
                    MsgMgr.getInstance().delay(new Runnable() {
                        @Override
                        public void run() {
                            LoadingDialog.closeLoadingDialog();
                        }
                    }, 20000);
                } else {
                    LoadingDialog.closeLoadingDialog();
                    if (showToast)
                        PToast.showShort(TextUtils.isEmpty(response.getMsg()) ? App.getContext().getString(R.string.chat_join_fail_tips) : response.getMsg());
                }
            }
        });
    }

    /**
     * 普通： 打开被邀请时的页面
     *
     * @param vcId     通话频道ID
     * @param dstUid   对方UID
     * @param chatType 1视频，2音频
     */
    public void openInvitedActivity(Activity activity, long vcId, long dstUid, int chatType, long price) {
        isBeInvite = true;
        mInviteInfo = new InviteInfo(vcId, dstUid, chatType, price);
        singleType = 2;   // 男默认关闭摄像头
        EngineConfig config = initEnginConfig(vcId, dstUid, AgoraConstant.RTC_CHAT_BEINVITE, chatType,
                AgoraConstant.RTC_MSG_VER);
        config.mChatPrice = price;
        // TODO: 2017/9/1 全量分支放开以下注释
//        UIShow.showRtcInitAct(activity);
    }

    /**
     * 直接开启聊天页
     *
     * @param vcId     通话频道ID
     * @param dstUid   对方UID
     * @param chatType 1视频，2音频
     */
    public void openInvitedDirect(Activity activity, long vcId, long dstUid, int chatType, String vc_channel_key) {
        EngineConfig config = initEnginConfig(vcId, dstUid, AgoraConstant.RTC_CHAT_BEINVITE, chatType,
                AgoraConstant.RTC_MSG_VER);
        config.mChannelKey = vc_channel_key;

        // TODO: 2017/9/1 全量分支放开以下注释
//        RtcJointChannel.getInstance().joinChannelPrepare(false);
//
//        if (config.mChatType == AgoraConstant.RTC_CHAT_VIDEO) {
//            UIShow.showRtcVideoAct(activity);
//            return;
//        }
//        UIShow.showRtcVoiceAct(activity);
    }

    // ========================== 配置参数初始化 ===============================

    /**
     * 普通版：初始化引擎通信参数配置
     *
     * @param vcId       视频通道
     * @param dstUid     对方UID
     * @param inviteType 1邀请，2受邀
     * @param chatType   1视频，2音频
     * @param msgVer     程序版本号
     */
    private EngineConfig initEnginConfig(long vcId, long dstUid, int inviteType, int chatType, int msgVer) {
        return initEnginConfig(vcId, dstUid, inviteType, chatType, msgVer,
                chatType == AgoraConstant.RTC_CHAT_VIDEO ? ModuleMgr.getRtcEnginMgr().getEngineConfig().isUseVideoCard : false,
                chatType == AgoraConstant.RTC_CHAT_VIDEO ? ModuleMgr.getRtcEnginMgr().getEngineConfig().videoCardTime : 0);
    }

    private EngineConfig initEnginConfig(long vcId, long dstUid, int inviteType, int chatType, int msgVer, boolean isUseVideoCard, int videochat_len) {
        // TODO: 2017/9/1 全量分支放开以下注释
//        ModuleMgr.getRtcEnginMgr().releaseEngineConfig();
        EngineConfig config = ModuleMgr.getRtcEnginMgr().getEngineConfig();
        int foreverType = PSP.getInstance().getInt(ModuleMgr.getCommonMgr().getPrivateKey(Constant.APPEAR_FOREVER_TYPE), 0);
        config.mVcid = vcId;
        config.mChannel = String.valueOf(vcId);
        config.mOtherId = dstUid;
        config.mChatType = chatType;
        config.mInviteType = inviteType;
        config.msgVer = msgVer;
        config.mIntervalTime = ModuleMgr.getCommonMgr().getCommonConfig().getVideo().getScreenshot_interval() * 1000;
        config.mIntervalFirstTime = ModuleMgr.getCommonMgr().getCommonConfig().getVideo().getScreenshot_interval_first_minute() * 1000;
        config.mCamera = singleType == 0 ? (foreverType == 0 ? 2 : foreverType) : singleType;
        config.isUseVideoCard = isUseVideoCard;
        config.videoCardTime = videochat_len;
        return config;
    }

    /**
     * 女性版： 初始化引擎通信参数配置
     *
     * @param girlType 单邀，群邀类型
     * @param inviteId 邀请ID
     */
    private EngineConfig initGirlEnginConfig(long vcId, long dstUid, int inviteType, int chatType, int msgVer, int girlType, int chatPrice, long inviteId) {
        EngineConfig config = initEnginConfig(vcId, dstUid, inviteType, chatType, msgVer);
        config.mGirlType = girlType;
        config.mChatPrice = chatPrice;
        config.mInviteID = inviteId;
        return config;
    }

    // ---------------------------视频优化版---------------------------

    private static final int RTC_CHAT_VIDEO = 1;    // 视频通信
    private static final int RTC_CHAT_VOICE = 2;    // 音频通信
    private long inviteId;

    /**
     * 音视频邀请列表接受与回拨
     *
     * @param uid       对方uid
     * @param invite_id 女方邀请id
     * @param vtype     1 视频  2 语音
     * @param price     通信价格
     */
    public void handleGirlInvite(Activity activity, long uid, long invite_id, int vtype, int price, final String source) {
        // 失效或已回应过本次邀请
        if (inviteCancel(invite_id) || this.inviteId == invite_id) {
            if (vtype == RTC_CHAT_VIDEO) {
                inviteVAChat(activity, uid, AgoraConstant.RTC_CHAT_VIDEO,
                        true, Constant.APPEAR_TYPE_NO, "", false, source);
                return;
            }
            inviteVAChat(activity, uid, AgoraConstant.RTC_CHAT_VOICE, "", source);
            return;
        }

        // 钻石不足
        if (ModuleMgr.getCenterMgr().getMyInfo().getDiamand() < price) {
            //充值弹框
            UIShow.showGoodsDiamondBottomDlg(activity);
        } else {
            this.inviteId = invite_id;
            int show = PSP.getInstance().getInt(ModuleMgr.getCommonMgr().getPrivateKey(Constant.APPEAR_FOREVER_TYPE), 0);
            if (vtype == RTC_CHAT_VIDEO && show == 0) {
                UIShow.showLookAtHerDlg(App.activity, uid, "", invite_id, source);
            } else {
                acceptInviteVAChat(invite_id, show, true, source, null);
            }
        }
    }

    /**
     * 首页音视频邀请列表接受与回拨  20170904此处只走音频  大坑
     *
     * @param uid       对方uid
     * @param invite_id 女方邀请id
     * @param vtype     1 视频  2 语音
     * @param price     通信价格
     * @param complete  群邀结果回调
     */
    public void chatFragmentInvite(final Activity activity, final long uid, final long invite_id, final int vtype, final int price, final UserInfoLightweight userInfoLightweight, final String source, final RequestComplete complete) {
        // vip
        if (ModuleMgr.getCommonMgr().getCommonConfig().getVideo().isVideoCallNeedVip() // 需要vip
                && !ModuleMgr.getCenterMgr().getMyInfo().isVip()) {
            // UIShow.showGoodsVipBottomDlg(activity, GoodsConstant.DLG_VIP_AV_CHAT);
            UIShow.showGoodsVipBottomDlg(activity, GoodsConstant.DLG_VIP_INDEX_YULIAO,
                    userInfoLightweight.getAvatar(), userInfoLightweight.getNickname()
                    , userInfoLightweight.getAge(), userInfoLightweight.getDistance(),
                    ModuleMgr.getCenterMgr().isVip(userInfoLightweight.getGroup()),
                    userInfoLightweight.isMan(), userInfoLightweight.isOnline(), null,vtype,price
            );
            return;
        }

        // 钻石不足
        if (ModuleMgr.getCenterMgr().getMyInfo().getDiamand() < price) {
            //UIShow.showGoodsDiamondBottomDlg(activity);
            UIShow.showBottomChatDiamondDlg(activity, userInfoLightweight.getUid(),
                    userInfoLightweight.getVtype(), userInfoLightweight.getPrice(), false,
                    0, 2, GoodsConstant.DLG_DIAMOND_YULIAO, userInfoLightweight, true
            );
            return;
        }

        final SendVideoDlg dlg = new SendVideoDlg();
        dlg.setCallAndPrice(new SendVideoDlg.BackCall() {
            @Override
            public void call() {
                dlg.dismiss();
                // 失效
                if (inviteCancel(invite_id)) {
                    /*if (vtype == RTC_CHAT_VIDEO) {
                        inviteVAChat(activity, uid, AgoraConstant.RTC_CHAT_VIDEO,
                                true, Constant.APPEAR_TYPE_NO, "", false);
                        return;
                    }*/
                    userInfoLightweight.setInvite_id(0);
                    executeInviteChat(activity, uid, AgoraConstant.RTC_CHAT_VOICE, "", false, 0, source);
                    return;
                }

                // 回应邀请
                int show = PSP.getInstance().getInt(ModuleMgr.getCommonMgr().getPrivateKey(Constant.APPEAR_FOREVER_TYPE), 0);
                acceptInviteVAChat(invite_id, show, false, source, complete);

            }
        }, price, SendVideoDlg.VOICE, userInfoLightweight);
        dlg.showDialog((BaseActivity) activity);

    }

    /**
     * 邀请失效
     */
    public boolean inviteCancel(long status) {
        return status == 0;
    }
}
