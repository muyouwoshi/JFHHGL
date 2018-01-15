package com.juxin.predestinate.module.local.chat;


import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;

import com.alibaba.fastjson.JSON;
import com.juxin.library.log.PSP;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.bean.my.BigGiftInfo;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.chat.msgtype.ChumInviteMessage;
import com.juxin.predestinate.module.local.chat.msgtype.DialogMessage;
import com.juxin.predestinate.module.local.chat.msgtype.InviteVideoMessage;
import com.juxin.predestinate.module.local.chat.msgtype.SystemMessage;
import com.juxin.predestinate.module.local.chat.msgtype.VideoMessage;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.CountDownTimerUtil;
import com.juxin.predestinate.module.util.PreferenceUtils;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.module.util.my.DlgMessageHelper;
import com.juxin.predestinate.module.util.my.GiftHelper;
import com.juxin.predestinate.ui.agora.util.VideoAudioChatHelper;
import com.juxin.predestinate.ui.live.bean.LiveStartLiveBean;
import com.juxin.predestinate.ui.main.MainActivity;
import com.juxin.predestinate.ui.user.my.CommonDlg.UpgradeGiftSendDlg;
import com.juxin.predestinate.ui.user.util.CenterConstant;

import org.json.JSONObject;

/**
 * 特殊消息处理类，减轻重度依赖
 * Created by Kind on 2017/8/22.
 */
public class SpecialMsgMgr {

    private int taskNum;
    private Handler handDelayed = new Handler(); //用于延时操作

    public void init() {}

    public void release() {}

    public void setSpecialMsg(final BaseMessage message) {
        switch (message.getType()) {
            case BaseMessage.TalkRed_MsgType://红包消息
                setTalkMsg(message);
                break;
            case BaseMessage.video_MsgType://视频消息
                setVideoMsg(message);
                break;
            case BaseMessage.System_MsgType://系统消息
                setSystemMsg(message);
                break;
            case BaseMessage.Recved_MsgType://送达消息
                setMsgRecvedType(message);
                break;
            case BaseMessage.inviteVideoDelivery_MsgType://语音(视频)邀请送达男用户
                setInviteVideoDelivery(message);
                break;
            case BaseMessage.Alone_Invite_Video_MsgType://女用户单独视频邀请
                setAloneInviteVideoMsg(message);
                break;
            case BaseMessage.VideoInviteTomen_MsgType://女性对男性的语音(视频)邀请送达男用户 此消息为群发视频/语音，送达人数对女用户的通知
                setVideoInviteTomen(message);
                break;
            case BaseMessage.chumInvite_MsgType://加密友消息
                setChumInvite(message);
                break;
            case BaseMessage.Gift_MsgType://礼物消息
                MsgMgr.getInstance().sendMsg(MsgType.MT_Update_TASK, null);//通知更新密友任务数据
                break;
            case BaseMessage.DIALOG_TYPE:
                if(showDialog(message)){
                    return;
                }
                handDelayed.post(new Runnable() {
                    @Override
                    public void run() {
                        DlgMessageHelper.getInstance().addDlgMsgs(message);
                    }
                });
                break;
            case BaseMessage.Intimate_Rem_MsgType://密友解除通知消息
                setIntimateRemMsg(message);
                break;
            case BaseMessage.Effect_Pop_MsgType://大礼物弹框
                handDelayed.post(new Runnable() {
                    @Override
                    public void run() {
                        DlgMessageHelper.getInstance().addDlgMsgs(message);
                    }
                });
                break;
            case BaseMessage.MSG_TYPE_VIDEOCHAT_POP:
                peerageInviteVideoMessage(message);
                break;
            case BaseMessage.LiveStartLive_MsgType: //开播提醒
                handleStartLiveMsg(message);
                break;
            default:
                break;

        }
        /**
         * 0或者不存在此键：不处理
         * 1：需要刷新自己的个人信息
         */
        JSONObject jsonObject = message.getJsonObj();
        if (!jsonObject.isNull("ref") && jsonObject.optInt("ref") == 1) {
            ModuleMgr.getCenterMgr().reqMyInfo();
        }
    }

    /**
     * 音视频消息
     *
     * @param message
     */
    private void setVideoMsg(BaseMessage message) {
        if (message == null) return;
        LoadingDialog.closeLoadingDialog();
        final VideoMessage videoMessage = (VideoMessage) message;
        // TODO: 2017/9/15 全量分支打开以下注释
//        RtcMessageMgr.getInstance().addVideoMsg(videoMessage);  // 保存当前消息

        if (videoMessage.getVideoTp() == 1) {
            // 女性用户且处于群发状态, 直接打开聊天界面
            if (!ModuleMgr.getCenterMgr().getMyInfo().isMan() && VideoAudioChatHelper.getInstance().getGroupInviteStatus()) {
                VideoAudioChatHelper.getInstance().setGroupInviteStatus(false);
                // TODO: 2017/9/15 全量分支打开以下注释
//                RtcMessageMgr.getInstance().sendGroupAcceptMsg(videoMessage);
                return;
            }
            VideoAudioChatHelper.getInstance().openInvitedActivity((Activity) App.getActivity(),
                    videoMessage.getVideoID(), videoMessage.getLWhisperID(), videoMessage.getVideoMediaTp(), 0);
        } else {
            boolean isInvite = PSP.getInstance().getBoolean("ISINVITE", false);  // 男号私聊页立即接听时为true， 其他全部为false
            if (isInvite && videoMessage.getVideoTp() == 2) { // 男号点立即接听时收到女性同意消息后，直接开启聊天页面
                CountDownTimerUtil.getInstance().addHandledIds(VideoAudioChatHelper.getInstance().getInviteTime());
                VideoAudioChatHelper.getInstance().openInvitedDirect((Activity) App.getActivity(),
                        videoMessage.getVideoID(), videoMessage.getLWhisperID(), videoMessage.getVideoMediaTp(), videoMessage.getVc_channel_key());
                return;
            }
            // TODO: 2017/9/15 全量分支打开以下注释
//            RtcMessageMgr.getInstance().sendVideoMsg(videoMessage.getVideoTp(), videoMessage.getVc_channel_key());
        }
    }

    /**
     * 男用户： 女性单独音视频邀请消息
     *
     * @param message
     */
    private void setAloneInviteVideoMsg(BaseMessage message) {
        if (message == null) return;
        InviteVideoMessage videoMessage = new InviteVideoMessage();
        videoMessage.parseJs(message.getJsonStr());
        // TODO: 2017/9/15 全量分支打开以下注释
//        RtcMessageMgr.getInstance().addVideoMsg(videoMessage);  // 保存当前消息
        if (ModuleMgr.getCenterMgr().getMyInfo().getDiamand() < videoMessage.getPrice()) {

            //充值弹框
            UIShow.showBottomChatDiamondDlg(App.getActivity(), message.getLWhisperID(), videoMessage.getMedia_tp(),
                    (int) videoMessage.getPrice(), true, videoMessage.getInvite_id());
            return;
        }

        //跳转视频
        VideoAudioChatHelper.getInstance().openInvitedActivity((Activity) App.getActivity(),
                videoMessage.getInvite_id(), message.getLWhisperID(), videoMessage.getMedia_tp(), videoMessage.getPrice());
    }

    /**
     * 女性对男性的语音(视频)邀请送达男用户 此消息为群发视频/语音，送达人数对女用户的通知
     *
     * @param message
     */
    private void setVideoInviteTomen(BaseMessage message) {
        if (message == null) return;
        InviteVideoMessage inviteMessage = new InviteVideoMessage();
        inviteMessage.parseJsonTotal(message.getJsonStr());
        //更新已经邀请人数
        // TODO: 2017/9/15 全量分支打开以下注释
//        ModuleMgr.getRtcEnginMgr().getEngineConfig().mGroupInvNum = inviteMessage.getSend_count();
//        MsgMgr.getInstance().sendMsg(MsgType.MT_Rtc_Group_Chat_invite, inviteMessage.getSend_count());
    }

    /**
     * 显示获得钥匙弹框
     *
     * @param message 弹框消息体
     * @author Mr.Huang
     * @updateDate 2017-07-20
     */
    private boolean showDialog(final BaseMessage message) {
        final DialogMessage dm = new DialogMessage();
        dm.parseJson(message.getJsonStr());
        if (DialogMessage.TYPE_FREEKEY_RECVED.equals(dm.getDialogType())) {
//            final DialogMessage.FreeKeyDialogData data = (DialogMessage.FreeKeyDialogData) dm.getDialogData();
//            MsgMgr.getInstance().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if (App.activity != null && App.activity instanceof MainActivity) {
//                        UIShow.showGetKeyOpenerDialog((FragmentActivity) App.activity, data.keyCount);
//                    } else {
//                        PSP.getInstance().put("show_get_key_opener_dialog_" + App.uid, data.keyCount);
//                    }
//                }
//            });
            return true;
        } else if (DialogMessage.FREEVIDEOCARD_RECVED.equals(dm.getDialogType())) {
            final DialogMessage.FreeVideoCard data = (DialogMessage.FreeVideoCard) dm.getDialogData();
            MsgMgr.getInstance().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (App.activity != null && App.activity instanceof MainActivity) {
                        UIShow.showGetVideoCardDialog((FragmentActivity) App.activity, data.count, data.videochat_len);
                    } else {
                        PreferenceUtils.putFreeVideoInfo(data.count, data.videochat_len);
                    }
                }
            });
            return true;
        }
        return false;
    }

    /**
     * 红包消息
     *
     * @param message
     */
    private void setTalkMsg(BaseMessage message) {
        if (message == null) return;

        JSONObject jsonObject = message.getJsonObj();
        int red_log_id = jsonObject.optInt("red_log_id");
        String content = jsonObject.optString("mct");
        UIShow.showChatRedBoxDialog((Activity) App.getActivity(), red_log_id, content);
    }

    /**
     * 系统消息
     *
     * @param message
     */
    private void setSystemMsg(BaseMessage message) {
        if (message == null) return;
        SystemMessage mess = new SystemMessage();
        mess.parseJson(message.getJsonStr());
        switch (mess.getXtType()) {
            case 3:
                ModuleMgr.getChatMgr().updateOtherRead(null, mess.getFid() + "", mess.getTid(), message);
                break;
        }
    }

    /**
     * 送达消息Msg_RecvedType
     *
     * @param message
     */
    private void setMsgRecvedType(BaseMessage message) {
        if (message == null) return;
        SystemMessage mess = new SystemMessage();
        mess.parseJson(message.getJsonStr());
        ModuleMgr.getChatMgr().updateDeliveryStatus(mess.getMsgID(), null);
    }

    private void peerageInviteVideoMessage(final BaseMessage message) {
        String content = message.getJsonStr();
        final com.alibaba.fastjson.JSONObject object = JSON.parseObject(content);
        if (object == null) {
            return;
        }
        final int fid = object.getIntValue("fid");

        ModuleMgr.getCenterMgr().reqOtherInfo(fid, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (!response.isOk()) {
                    return;
                }
                UserDetail detail = new UserDetail();
                detail.parseJson(response.getResponseString());
                Activity activity = App.activity;
                if (activity != null && activity instanceof MainActivity) {
                    long invite_id = object.getLongValue("invite_id");
                    int media_tp = object.getIntValue("media_tp");
                    int price = object.getIntValue("price");
                    InviteVideoMessage ivm = new InviteVideoMessage();
                    ivm.setFid(fid);
                    ivm.setInvite_id(invite_id);
                    ivm.setMedia_tp(media_tp);
                    ivm.setPrice(price);
                    ivm.setType(message.getType());
                    ((MainActivity) activity).showWindow(detail, ivm);
                }
            }
        });
    }

    /**
     * 男用户： 语音(视频)群邀消息
     *
     * @param message
     */
    private void setInviteVideoDelivery(BaseMessage message) {
        if (message == null || !(message instanceof InviteVideoMessage))
            return;

        final InviteVideoMessage mInviteVideoMessage = (InviteVideoMessage) message;
        CountDownTimerUtil util = CountDownTimerUtil.getInstance();
        long timeCount = mInviteVideoMessage.getTimeout_tm() - ModuleMgr.getAppMgr().getSecondTime();//计时时间
        if (timeCount > 120) {
            timeCount = 120;
        }
        if (timeCount > 0 && !util.isTimingTask(mInviteVideoMessage.getInvite_id()) && !util.isTimingTask(mInviteVideoMessage.getInvite_id())) {
            util.addTimerTask(mInviteVideoMessage.getTime(), timeCount);
        }
        if (mInviteVideoMessage.isShowPop()) {
            ModuleMgr.getCenterMgr().reqOtherInfo(mInviteVideoMessage.getFid(), new RequestComplete() {
                @Override
                public void onRequestComplete(HttpResponse response) {
                    if (!response.isOk()) {
                        return;
                    }
                    Activity activity = App.activity;
                    if (activity != null && activity instanceof MainActivity) {
                        UserDetail detail = new UserDetail();
                        detail.parseJson(response.getResponseString());
                        detail.getChatInfo().parseJsonOtherInfo();
                        ((MainActivity) activity).showWindow(detail, mInviteVideoMessage);
                    }
                }
            });
        }
    }

    /**
     * 加密友
     *
     * @param message
     */
    private void setChumInvite(BaseMessage message) {
        if (!(message instanceof ChumInviteMessage)) return;

        ChumInviteMessage inviteMessage = (ChumInviteMessage) message;
        if (inviteMessage.getClose_tp() == 2) {//加密友成功
            getIntimateTaskCnt();//获取任务角标
        }
    }

    /**
     * 获取今天未完成密友任务的好友数
     */
    public void getIntimateTaskCnt() {
        ModuleMgr.getCommonMgr().getIntimateTaskCnt(new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (!response.isOk()) return;

                JSONObject jsonObject = response.getResponseJsonRes();
                if (!jsonObject.isNull("taskCnt")) {
                    taskNum = jsonObject.optInt("taskCnt");
                    MsgMgr.getInstance().sendMsg(MsgType.MT_User_List_Msg_Change, null);
                }
            }
        });
    }

    public int getTaskNum() {
        return taskNum;
    }

    /**
     * 弹窗消息
     *
     * @param message
     */
    public void setPopups(BaseMessage message) {
        JSONObject jsonObject = message.getJsonObj();
        String dialog_tp = jsonObject.optString("dialog_tp");//弹窗类型nobility_hello欢迎进入爵位系统nobility_left爵位一步之遥nobility_up爵位升级
        if (VideoAudioChatHelper.getInstance().isInACall() && !"nobility_up".equalsIgnoreCase(dialog_tp)) {
            if (!"nobility_left".equalsIgnoreCase(dialog_tp) && !"intimate_left".equalsIgnoreCase(dialog_tp)) {
                VideoAudioChatHelper.getInstance().setDlgMessages(message);
            }
            return;
        }

        if ("nobility_hello".equalsIgnoreCase(dialog_tp)) { //欢迎进入爵位系统
            UIShow.showAccumulatedSystemDlg();
        }

        if (jsonObject.isNull("dialog_data")) { //无消息结构体则返回
            return;
        }

        if ("intimate_lvup".equalsIgnoreCase(dialog_tp)) { //intimate_lvup 密友等级提升
            MsgMgr.getInstance().sendMsg(MsgType.MT_Update_TASK, null);//通知更新密友任务数据
            jsonObject = jsonObject.optJSONObject("dialog_data");//// [opt]弹窗数据，暂时为空
            UIShow.showCloseFriendUpgradeDlg(jsonObject.optString("t_nick"), jsonObject.optInt("intimate_level"));
        } else if ("intimate_left".equalsIgnoreCase(dialog_tp)) { //密友还差100弹窗
            UIShow.showUpgradeGiftSendDlg(UpgradeGiftSendDlg.CLOSEFRIENDUPGRADE, message, null);
        } else if ("nobility_left".equalsIgnoreCase(dialog_tp)) { //爵位一步之遥
            long whisperID = PSP.getInstance().getLong("whisperID", 0);
            if (whisperID > 0) {
                message.setWhisperID(String.valueOf(whisperID));
            }
            UIShow.showUpgradeGiftSendDlg(UpgradeGiftSendDlg.BANNERETUPGRADEB, message, null);
        } else if ("nobility_up".equalsIgnoreCase(dialog_tp)) { //爵位升级
            UIShow.showTitleUpgradeDlg();
        } else if ("pack_recved".equalsIgnoreCase(dialog_tp)) { //礼包购买成功
            jsonObject = jsonObject.optJSONObject("dialog_data");
            int pack_id = jsonObject.optInt("pack_id");
            UIShow.showActiveDlg(App.getActivity(), pack_id);
        } else if ("red_recved".equalsIgnoreCase(dialog_tp)) { //收到红包弹窗
            jsonObject = jsonObject.optJSONObject("dialog_data");
            int redType = jsonObject.optInt("red_type");
            if (redType == CenterConstant.TYPE_SHARE){ //2017.11.7 屏蔽首次分享成功红包弹框
                return;
            }
            if (redType == CenterConstant.TYPE_RECHARGE) {
                PSP.getInstance().put(ModuleMgr.getCommonMgr().getPrivateKey(Constant.GET_RECHARGE_RED_BAG), true);
            } else {
                UIShow.showGetRedBagDlg(App.getActivity(), redType);
            }
        }
    }

    /**
     * 解除密友操作
     * @param message
     */
    private void setIntimateRemMsg(BaseMessage message) {
        //更新数据库中的密友任务中的fStatus状态
        ModuleMgr.getChatMgr().updateTypeFStatus(message.getLWhisperID(), 36, null);
        MsgMgr.getInstance().sendMsg(MsgType.MT_REMOVE_CHUM_INFORM, null);
    }

    /**
     * 大礼物弹窗消息
     *
     * @param message
     */
    public void setEffectPopMsg(BaseMessage message) {
        if (GiftHelper.isBigDialogIsCanShow()) { //显示大礼物弹框
            BigGiftInfo info = new BigGiftInfo();
            info.parseJson(message.getJsonStr());
            UIShow.showBigGiftDlg(info);
        }
    }

    /**
     * 处理自动接收礼物消息
     *
     * @param message
     */
    public void handleGiftMsg(BaseMessage message) {
        if (message != null && message.getType() == BaseMessage.Gift_MsgType) {
            JSONObject object = message.getJsonObj();
            JSONObject giftJo = object.optJSONObject("gift");
            if (giftJo.optInt("recved") == 1) {
                ModuleMgr.getChatMgr().updateMsgFStatus(message.getMsgID(), true, null);
            }
        }
    }

    /**
     * 开播提醒
     * @param message
     */
    public void handleStartLiveMsg(BaseMessage message){
        /**
         * {
         "mct":"你的好友张三正在直播，要不要去看看",	//消息内容
         "fid":100221,					//发送者uid
         "tid":100221,					//接收者uid
         "mtp":1004,						//消息类型 和包头一致
         "mt":442112121,					//消息时间 int64
         "d":43432423423,				//消息id int64
         "cover":"http://",					//封面图
         "room_id":"123211",				//直播间ID
         }

         */
        if (message != null && message.getType() == BaseMessage.LiveStartLive_MsgType) {
            JSONObject object = message.getJsonObj();

            LiveStartLiveBean bean = new LiveStartLiveBean();
            bean.parseJson(object);

        }
    }
}