package com.juxin.predestinate.module.local.chat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;

import com.juxin.library.dir.DirType;
import com.juxin.library.log.PLogger;
import com.juxin.library.log.PSP;
import com.juxin.library.log.PToast;
import com.juxin.library.observe.ModuleBase;
import com.juxin.library.observe.Msg;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.library.utils.BitmapUtil;
import com.juxin.library.utils.FileUtil;
import com.juxin.library.utils.InputUtils;
import com.juxin.library.utils.TypeConvertUtil;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweightList;
import com.juxin.predestinate.bean.db.DBCallback;
import com.juxin.predestinate.bean.db.DBCenter;
import com.juxin.predestinate.bean.file.UpLoadResult;
import com.juxin.predestinate.bean.my.GiftsList;
import com.juxin.predestinate.bean.my.RedbagInfo;
import com.juxin.predestinate.bean.my.SendChumInvite;
import com.juxin.predestinate.bean.my.SendChumTask;
import com.juxin.predestinate.bean.my.SendGiftResultInfo;
import com.juxin.predestinate.module.local.chat.inter.ChatMsgInterface;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.chat.msgtype.ChumInviteMessage;
import com.juxin.predestinate.module.local.chat.msgtype.ChumTaskMessage;
import com.juxin.predestinate.module.local.chat.msgtype.CommonMessage;
import com.juxin.predestinate.module.local.chat.msgtype.GiftMessage;
import com.juxin.predestinate.module.local.chat.msgtype.GiftRedPackageMesaage;
import com.juxin.predestinate.module.local.chat.msgtype.MailReadedMessage;
import com.juxin.predestinate.module.local.chat.msgtype.OrdinaryMessage;
import com.juxin.predestinate.module.local.chat.msgtype.RedPackageMessage;
import com.juxin.predestinate.module.local.chat.msgtype.TextMessage;
import com.juxin.predestinate.module.local.chat.msgtype.VideoMessage;
import com.juxin.predestinate.module.local.chat.utils.MessageConstant;
import com.juxin.predestinate.module.local.mail.MailSpecialID;
import com.juxin.predestinate.module.local.statistics.SourcePoint;
import com.juxin.predestinate.module.local.unread.UnreadReceiveMsgType;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.logic.socket.IMProxy;
import com.juxin.predestinate.module.logic.socket.NetData;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.agora.util.VideoAudioChatHelper;
import com.juxin.predestinate.ui.live.bean.SendGiftCallbackBean;
import com.juxin.predestinate.ui.live.callback.OnSendGiftCallbackListener;
import com.juxin.predestinate.ui.mail.unlock.UnlockComm;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 消息处理管理类
 * Created by Kind on 2017/3/28.
 */
public class ChatMgr implements ModuleBase {

    private Bundle bundle = null;
    private IMProxy.SendCallBack sendCallBack = null;
    private RecMessageMgr messageMgr = new RecMessageMgr();
    private ChatSpecialMgr specialMgr = ChatSpecialMgr.getChatSpecialMgr();

    @Inject
    DBCenter dbCenter;

    @Override
    public void init() {
        messageMgr.init();
        specialMgr.init();
    }

    @Override
    public void release() {
        messageMgr.release();
        specialMgr.release();
        updateStatusFail();
    }

    /**
     * 检测是否可以聊天，是否上锁  以后统一到这个方法里，目前没有时间弄
     * @return
     */
    public boolean checkIsCanchat(long whisperID){
      return false;
    }


    /**
     * 检测免费聊一条是否开着
     * @return
     */
    public boolean isChatShow(){
        if(ModuleMgr.getCommonMgr().getCommonConfig().getTalkConfig().isFree_talk_one()){//true可以免费聊一条
            return ModuleMgr.getChatListMgr().getTodayChatShow();
        }else {
            return false;
        }
    }

    /**
     * 退出程序的时候把发送中都更改为发送失败
     */
    public void updateStatusFail() {
        dbCenter.getCenterFLetter().updateStatusFail(null);
        dbCenter.getCenterFMessage().updateStatusFail(null);
    }

    public void inject() {
        ModuleMgr.getChatListMgr().getAppComponent().inject(this);
    }

    /**
     * 更新某个用户的本地状态
     * 如果在聊天框的时候。发送过来消息立即更改为已读
     *
     * @param channelID
     * @param whisperID
     */
    public void updateLocalReadStatus(final String channelID, final String whisperID, final long msgID) {
        dbCenter.getCenterFMessage().updateToRead(channelID, whisperID, new DBCallback() {
            @Override
            public void OnDBExecuted(long result) {
                if (result == MessageConstant.OK) {
                    ModuleMgr.getChatListMgr().getWhisperListUnSubscribe();
                }
            }
        });
    }

    /**
     * 对方已读
     *
     * @param channelID
     * @param whisperID
     * @param sendID
     */
    public void updateOtherSideRead(String channelID, String whisperID, String sendID) {
        updateOtherSideRead(channelID, whisperID, sendID, -1);
    }

    public void updateOtherSideRead(String channelID, String whisperID, String sendID, long msgID) {
        dbCenter.getCenterFMessage().updateOtherSideRead(channelID, whisperID, sendID, msgID, null);
    }

    /**
     * 对方已读
     *
     * @param channelID
     * @param whisperID
     * @param sendID
     */
    public void updateOtherRead(String channelID, String whisperID, long sendID, BaseMessage message) {
        ModuleMgr.getChatListMgr().updateToReadPrivate(Long.valueOf(whisperID));
        ModuleMgr.getChatMgr().updateOtherSideRead(null, whisperID, Long.toString(sendID));
    }

    /**
     * 本地模拟语音视频消息
     *
     * @param otherID
     */
    public void sendVideoMsgLocalSimulation(String otherID, int type, long videoID) {
        final VideoMessage videoMessage = new VideoMessage(null, otherID, type, videoID, 3, 3);
        videoMessage.setStatus(MessageConstant.OK_STATUS);
        videoMessage.setDataSource(MessageConstant.FOUR);
        videoMessage.setJsonStr(videoMessage.getJson(videoMessage));

        dbCenter.insertMsgLocalVideo(videoMessage, new DBCallback() {
            @Override
            public void OnDBExecuted(long result) {
                if (result == MessageConstant.ERROR) {
                    return;
                }
                pushMsg(videoMessage);
            }
        });
    }

    /**
     * 批量打招呼[ab测试版添加/2017.8.18]
     *
     * @param uids            打招呼的uid列表
     * @param requestComplete 请求回调
     */
    public void sendSayHelloMsg(final long[] uids, final RequestComplete requestComplete) {
        LoadingDialog.show((FragmentActivity) App.getActivity());

        Map<String, Object> params = new HashMap<>();
        params.put("tuids", uids);
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.SAY_HELLO_PEERAGE, params, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (requestComplete != null) {
                    requestComplete.onRequestComplete(response);
                }
                if (response.isOk()) {
                    for (long uid : uids) {
                        insertDBSayHelloMsg(String.valueOf(uid));
                    }
                    PToast.showLong(App.getContext().getString(R.string.say_hello_batch_success));
                } else {
                    PToast.showLong(TextUtils.isEmpty(response.getMsg()) ? "打招呼失败！" : response.getMsg());
                }
                LoadingDialog.closeLoadingDialog(500);
            }
        });
    }

    /**
     * 数据库插入批量打招呼文字
     *
     * @param uid 打招呼用户
     */
    private void insertDBSayHelloMsg(String uid) {
        final CommonMessage commonMessage = new CommonMessage(uid,
                App.getContext().getResources().getString(R.string.say_hello_txt),
                -1, Constant.SAY_HELLO_TYPE_SIMPLE);
        commonMessage.setStatus(MessageConstant.OK_STATUS);
        commonMessage.setJsonStr(commonMessage.getJson(commonMessage));
        commonMessage.setKnow(MessageConstant.Know_stranger);

        saveSayHelloMsg(commonMessage);
    }

    /**
     * 打招呼
     *
     * @param whisperID
     * @param content
     * @param kf           当前发信用户为机器人  客服id
     * @param sayHelloType 当前发信用户为机器人 机器人打招呼类型(0为普通,1为向机器人一键打招呼, 3附近的人群打招呼,4为向机器人单点打招呼(包括首页和详细资料页等))
     */
    public void sendSayHelloMsg(final String whisperID, String content, int kf, int sayHelloType, final IMProxy.SendCallBack sendCallBack) {
        final CommonMessage commonMessage = new CommonMessage(whisperID, content, kf, sayHelloType);
        commonMessage.setStatus(MessageConstant.OK_STATUS);
        commonMessage.setJsonStr(commonMessage.getJson(commonMessage));
        commonMessage.setKnow(MessageConstant.Know_stranger);

        IMProxy.getInstance().send(new NetData(App.uid, commonMessage.getType(), commonMessage.getJsonStr()), new IMProxy.SendCallBack() {
            @Override
            public void onResult(long msgId, boolean group, String groupId, long sender, String contents) {
                if (sendCallBack != null) {
                    sendCallBack.onResult(msgId, group, groupId, sender, contents);
                }
                MessageRet messageRet = new MessageRet();
                messageRet.parseJson(contents);
                if (!messageRet.isS()) return;

                saveSayHelloMsg(commonMessage);
            }

            @Override
            public void onSendFailed(NetData data) {
                if (sendCallBack != null) {
                    sendCallBack.onSendFailed(data);
                }
            }
        });
    }

    private void saveSayHelloMsg(final CommonMessage commonMessage) {
        Observable<BaseMessage> observable = dbCenter.getCenterFLetter().isHaveMsg(commonMessage.getWhisperID());
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseMessage>() {
                    private Disposable queryDisposable;

                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        queryDisposable = d;
                    }

                    @Override
                    public void onNext(@NonNull BaseMessage tmpMsg) {
                        queryDisposable.dispose();
                        if (tmpMsg.getLWhisperID() > 0) {
                            dbCenter.getCenterFLetter().updateLetter(commonMessage, tmpMsg, null);
                        }
                        dbCenter.getCenterFMessage().insertMsg(commonMessage, tmpMsg == null ? null : new DBCallback() {
                            @Override
                            public void OnDBExecuted(long result) {
                                if (result == MessageConstant.ERROR)
                                    return;
                                onChatMsgUpdate(commonMessage.getChannelID(), commonMessage.getWhisperID(), commonMessage);
                            }
                        });
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /**
     * 重发消息
     *
     * @param message 消息体
     */
    public void resendMsg(BaseMessage message) {
        BaseMessage.BaseMessageType messageType = BaseMessage.BaseMessageType.valueOf(message.getType());
        if (messageType != null) {
            message.setStatus(MessageConstant.SENDING_STATUS);
            onChatMsgUpdate(message.getChannelID(), message.getWhisperID(), message);

            switch (messageType) {
                case common: {
                    final CommonMessage commonMessage = (CommonMessage) message;

                    String voiceUrl = commonMessage.getVoiceUrl();
                    String localVoiceUrl = commonMessage.getLocalVoiceUrl();
                    String img = commonMessage.getImg();
                    String localImg = commonMessage.getLocalImg();

                    if (commonMessage.getMsgID() <= 0) {
                        commonMessage.setMsgID(commonMessage.getcMsgID());
                    }
                    commonMessage.setJsonStr(commonMessage.getJson(commonMessage));

                    if (!TextUtils.isEmpty(voiceUrl) || !TextUtils.isEmpty(localVoiceUrl)) {//语音
                        if (!TextUtils.isEmpty(voiceUrl) && FileUtil.isURL(voiceUrl)) {
                            sendMessage(commonMessage, null);
                        } else {
                            if (TextUtils.isEmpty(voiceUrl)) {
                                voiceUrl = localVoiceUrl;
                            }

                            sendHttpFile(Constant.UPLOAD_TYPE_VOICE, commonMessage, voiceUrl, new RequestComplete() {
                                @Override
                                public void onRequestComplete(final HttpResponse response) {
                                    if (response.isOk()) {
                                        UpLoadResult upLoadResult = (UpLoadResult) response.getBaseData();
                                        commonMessage.setVoiceUrl(upLoadResult.getFile_http_path());
                                        commonMessage.setJsonStr(commonMessage.getJson(commonMessage));
                                        dbCenter.updateFmessage(commonMessage, new DBCallback() {
                                            @Override
                                            public void OnDBExecuted(long result) {
                                                if (result == MessageConstant.ERROR) {
                                                    onChatMsgUpdate(commonMessage.getChannelID(), commonMessage.getWhisperID(), commonMessage);
                                                    return;
                                                }
                                                sendMessage(commonMessage, null);
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    } else if (!TextUtils.isEmpty(img) || !TextUtils.isEmpty(localImg)) {//图片
                        if (!TextUtils.isEmpty(img) && FileUtil.isURL(img)) {
                            sendMessage(commonMessage, null);
                        } else {
                            if (TextUtils.isEmpty(img)) {
                                img = localImg;
                            }
                            sendHttpFile(Constant.UPLOAD_TYPE_PHOTO, commonMessage, img, new RequestComplete() {
                                @Override
                                public void onRequestComplete(final HttpResponse response) {
                                    if (response.isOk()) {
                                        UpLoadResult upLoadResult = (UpLoadResult) response.getBaseData();
                                        commonMessage.setImg(upLoadResult.getFile_http_path());
                                        commonMessage.setJsonStr(commonMessage.getJson(commonMessage));
                                        dbCenter.updateFmessage(commonMessage, new DBCallback() {
                                            @Override
                                            public void OnDBExecuted(long result) {
                                                if (result != MessageConstant.OK) {
                                                    return;
                                                }

                                                onChatMsgUpdate(commonMessage.getChannelID(), commonMessage.getWhisperID(), commonMessage);
                                                sendMessage(commonMessage, null);
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    } else {//文字
                        sendMessage(commonMessage, null);
                    }
                    break;
                }
                case gift: {
                    String source = SourcePoint.getInstance().getGiftSource();
                    final GiftMessage giftMessage = (GiftMessage) message;
                    ModuleMgr.getCommonMgr().sendGift(giftMessage.getWhisperID(), String.valueOf(giftMessage.getGiftID()),
                            giftMessage.getGiftCount(), giftMessage.getGiftFtype(), source, giftMessage.getGiftfrom(), new RequestComplete() {
                                @Override
                                public void onRequestComplete(HttpResponse response) {
                                    SendGiftResultInfo info = new SendGiftResultInfo();
                                    info.parseJson(response.getResponseString());
                                    if (response.isOk()) {
                                        MsgMgr.getInstance().sendMsg(MsgType.MT_Update_TASK, null);//通知更新密友任务数据
                                        MessageRet messageRet = new MessageRet();
                                        messageRet.setMsgId(info.getMsgID());
                                        updateOk(giftMessage, messageRet);
                                    } else {
                                        updateFail(giftMessage, null);
                                        showToast(response.getMsg());
                                    }
                                }
                            });
                    break;
                }
                case chumInvite: {
                    final ChumInviteMessage inviteMessage = (ChumInviteMessage) message;

                    ModuleMgr.getCommonMgr().startIntimateRequestM(inviteMessage.getLWhisperID(), inviteMessage.getGift_id(), new RequestComplete() {
                        @Override
                        public void onRequestComplete(HttpResponse response) {
                            SendChumInvite sendChumInvite = new SendChumInvite();
                            sendChumInvite.parseJson(response.getResponseString());
                            if (response.isOk()) {
                                inviteMessage.setInvite_id(sendChumInvite.getInvite_id());
                                inviteMessage.setExpire(sendChumInvite.getExpire());
                                inviteMessage.setJsonStr(inviteMessage.getJson(inviteMessage));
                                MessageRet messageRet = new MessageRet();
                                messageRet.setMsgId(sendChumInvite.getMsgID());
                                updateOk(inviteMessage, messageRet);
                            } else {
                                updateFail(inviteMessage, null);
                                showToast(response.getMsg());
                            }
                        }
                    });
                    break;
                }
                default:
                    break;
            }
        }
    }

    /**
     * 检测是否弹窗
     *
     * @param whisperID
     * @return
     */
    private boolean checkIsPopUps(String whisperID) {
        long uid = TypeConvertUtil.toLong(whisperID);
        return !ModuleMgr.getCenterMgr().getMyInfo().isMan() || isChatShow() || MailSpecialID.shareMsg.getSpecialID() == uid ||
                MailSpecialID.customerService.getSpecialID() == uid || (ModuleMgr.getCenterMgr().getMyInfo().isB()
                && ModuleMgr.getCenterMgr().getMyInfo().isVip()) || (!ModuleMgr.getCenterMgr().getMyInfo().isB()
                && (ModuleMgr.getCenterMgr().getMyInfo().getYcoin() > 79)) ||
                UnlockComm.getCanMsgAndShow((FragmentActivity) App.activity, uid);
    }

    private final int msgType_text = 1;//临时文本消息
    private final int msgType_IMG = 2;//临时图片消息
    private final int msgType_Voice = 3;//临时语音消息

    /**
     * 临时存储文本消息
     *
     * @param whisperID
     * @param content
     * @param know
     */
    public void temporaryStorageTextMsg(String whisperID, String content, int know) {
        bundle = new Bundle();
        bundle.putInt("msgType", msgType_text);
        bundle.putString("whisperID", whisperID);
        bundle.putString("content", content);
        bundle.putInt("know", know);
        this.sendCallBack = null;
    }

    public void temporaryStorageTextMsg(String whisperID, String content, int know, IMProxy.SendCallBack sendCallBack) {
        temporaryStorageTextMsg(whisperID, content, know);
        this.sendCallBack = sendCallBack;
    }

    public void temporaryStorageImgMsg(String whisperID, String img_url, int know) {
        bundle = new Bundle();
        bundle.putInt("msgType", msgType_text);
        bundle.putString("whisperID", whisperID);
        bundle.putString("img_url", img_url);
        bundle.putInt("know", know);
    }

    public void temporaryStorageVoiceMsg(String whisperID, String url, int length, int know) {
        bundle = new Bundle();
        bundle.putInt("msgType", msgType_Voice);
        bundle.putString("whisperID", whisperID);
        bundle.putString("url", url);
        bundle.putInt("length", length);
        bundle.putInt("know", know);
    }

    public void sendMsg() {
        if (bundle == null) {
            return;
        }

        String whisperID = bundle.getString("whisperID");
        int know = bundle.getInt("know");

        switch (bundle.getInt("msgType")) {
            case msgType_text:
                String content = bundle.getString("content");
                sendTextMessage(null, whisperID, content, know, sendCallBack);
                break;
            case msgType_IMG:
                String img_url = bundle.getString("img_url");
                sendImgMsg(null, whisperID, img_url, know);
                break;
            case msgType_Voice:
                String url = bundle.getString("url");
                int length = bundle.getInt("length");
                sendVoiceMsg(null, whisperID, url, length, know);
                break;
            default:
                break;
        }
        bundle.clear();
        bundle = null;
    }

    /**
     * 文字消息
     */
    public void sendTextMsg(String channelID, String whisperID, @Nullable String content) {
        sendTextMsg(channelID, whisperID, content, MessageConstant.Know_stranger, null);
    }

    public void sendTextMsg(String channelID, String whisperID, @Nullable String content, int know) {
        sendTextMsg(channelID, whisperID, content, know, null);
    }

    public void sendTextMsg(String channelID, String whisperID, @Nullable String content, IMProxy.SendCallBack sendCallBack) {
        sendTextMsg(channelID, whisperID, content, MessageConstant.Know_stranger, sendCallBack);
    }

    /**
     * 文字消息
     *
     * @param whisperID
     * @param content
     */
    public void sendTextMsg(String channelID, String whisperID, @Nullable String content, int know, IMProxy.SendCallBack sendCallBack) {
        if (checkIsPopUps(whisperID)) {
            sendTextMessage(channelID, whisperID, content, know, sendCallBack);
        } else {
            temporaryStorageTextMsg(whisperID, content, know, sendCallBack);
        }
    }

    public void sendTextMessage(String channelID, String whisperID, @Nullable String content, int know, final IMProxy.SendCallBack sendCallBack) {
        final CommonMessage commonMessage = new CommonMessage(channelID, whisperID, content);
        commonMessage.setStatus(MessageConstant.SENDING_STATUS);
        commonMessage.setJsonStr(commonMessage.getJson(commonMessage));
        commonMessage.setKnow(know);

        dbCenter.insertMsg(commonMessage, new DBCallback() {
            @Override
            public void OnDBExecuted(long result) {
                onChatMsgUpdate(commonMessage.getChannelID(), commonMessage.getWhisperID(), commonMessage);
                if (result == MessageConstant.ERROR)
                    return;
                sendMessage(commonMessage, sendCallBack);
            }
        });
    }

    /**
     * 赠送红包 只把数据插入数据库
     */
    public void sendRedPackage(final String whisperID, int money) {
        final RedPackageMessage p = new RedPackageMessage();
        p.setType(34);
        p.setStatus(MessageConstant.OK_STATUS);
        p.setChannelID("");
        p.setSendID(App.uid);
        p.setWhisperID(whisperID);
        com.alibaba.fastjson.JSONObject o = new com.alibaba.fastjson.JSONObject();
        o.put("red_money", money);
        p.setJsonStr(o.toJSONString());
        dbCenter.insertMsg(p, new DBCallback() {
            @Override
            public void OnDBExecuted(long result) {
                onChatMsgUpdate("", whisperID, p);
            }
        });
    }

    /**
     * 关注
     *
     * @param userID
     * @param content
     * @param kf
     * @param gz      关注状态1为关注2为取消关注
     */
    public void sendAttentionMsg(long userID, String content, int kf, int gz, IMProxy.SendCallBack sendCallBack) {
        OrdinaryMessage message = new OrdinaryMessage(userID, content, kf, gz);
        IMProxy.getInstance().send(new NetData(App.uid, message.getType(), message.toFllowJson()), sendCallBack);
    }

    /**
     * 发送已读
     *
     * @param userID
     */
    public void sendMailReadedMsg(String channelID, long userID, IMProxy.SendCallBack sendCallBack) {
        MailReadedMessage message = new MailReadedMessage(channelID, userID);
        IMProxy.getInstance().send(new NetData(App.uid, message.getType(), message.toMailReadedJson()), sendCallBack);
    }

    public void sendImgMsg(String channelID, String whisperID, @Nullable final String img_url) {
        sendImgMsg(channelID, whisperID, img_url, MessageConstant.Know_stranger);
    }

    public void sendImgMsg(String channelID, String whisperID, @Nullable final String img_url, int know) {
        if (checkIsPopUps(whisperID)) {
            sendImgMessage(channelID, whisperID, img_url, know);
        } else {
            temporaryStorageImgMsg(whisperID, img_url, know);
        }
    }

    private void sendImgMessage(String channelID, String whisperID, final String img_url, int know) {
        final CommonMessage commonMessage = new CommonMessage(channelID, whisperID, img_url, null);
        commonMessage.setLocalImg(BitmapUtil.getSmallBitmapAndSave(img_url, DirType.getImageDir()));
        commonMessage.setStatus(MessageConstant.SENDING_STATUS);
        commonMessage.setJsonStr(commonMessage.getJson(commonMessage));
        commonMessage.setKnow(know);

        dbCenter.insertMsg(commonMessage, new DBCallback() {
            @Override
            public void OnDBExecuted(long result) {
                onChatMsgUpdate(commonMessage.getChannelID(), commonMessage.getWhisperID(), commonMessage);
                if (result != MessageConstant.OK)
                    return;

                MsgMgr.getInstance().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (FileUtil.isURL(img_url)) {
                            sendMessage(commonMessage, null);
                            return;
                        }

                        sendHttpFile(Constant.UPLOAD_TYPE_PHOTO, commonMessage, img_url, new RequestComplete() {
                            @Override
                            public void onRequestComplete(HttpResponse response) {
                                if (!response.isOk()) {
                                    return;
                                }

                                UpLoadResult upLoadResult = (UpLoadResult) response.getBaseData();
                                commonMessage.setImg(upLoadResult.getFile_http_path());
                                commonMessage.setJsonStr(commonMessage.getJson(commonMessage));
                                dbCenter.updateFmessage(commonMessage, new DBCallback() {
                                    @Override
                                    public void OnDBExecuted(long result) {
                                        if (result != MessageConstant.OK) {
                                            return;
                                        }
                                        onChatMsgUpdate(commonMessage.getChannelID(), commonMessage.getWhisperID(), commonMessage);
                                        sendMessage(commonMessage, null);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    public void sendVoiceMsg(String channelID, String whisperID, @Nullable final String url, @Nullable int length) {
        sendVoiceMsg(channelID, whisperID, url, length, MessageConstant.Know_stranger);
    }


    //语音消息
    public void sendVoiceMsg(String channelID, String whisperID, @Nullable final String url, @Nullable int length, int know) {
        if (checkIsPopUps(whisperID)) {
            sendVoiceMessage(channelID, whisperID, url, length, know);
        } else {
            temporaryStorageVoiceMsg(whisperID, url, length, know);
        }
    }

    private void sendVoiceMessage(String channelID, String whisperID, final String url, int length, int know) {
        final CommonMessage commonMessage = new CommonMessage(channelID, whisperID, url, length);
        commonMessage.setLocalVoiceUrl(url);
        commonMessage.setStatus(MessageConstant.SENDING_STATUS);
        commonMessage.setJsonStr(commonMessage.getJson(commonMessage));
        commonMessage.setKnow(know);

        dbCenter.insertMsg(commonMessage, new DBCallback() {
            @Override
            public void OnDBExecuted(long result) {
                onChatMsgUpdate(commonMessage.getChannelID(), commonMessage.getWhisperID(), commonMessage);
                if (result != MessageConstant.OK) return;

                MsgMgr.getInstance().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sendHttpFile(Constant.UPLOAD_TYPE_VOICE, commonMessage, url, new RequestComplete() {
                            @Override
                            public void onRequestComplete(final HttpResponse response) {
                                if (response.isOk()) {
                                    UpLoadResult upLoadResult = (UpLoadResult) response.getBaseData();
                                    commonMessage.setVoiceUrl(upLoadResult.getFile_http_path());
                                    commonMessage.setJsonStr(commonMessage.getJson(commonMessage));
                                    dbCenter.updateFmessage(commonMessage, new DBCallback() {
                                        @Override
                                        public void OnDBExecuted(long result) {
                                            if (result != MessageConstant.OK) {
                                                return;
                                            }

                                            onChatMsgUpdate(commonMessage.getChannelID(), commonMessage.getWhisperID(), commonMessage);
                                            sendMessage(commonMessage, null);
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private void sendHttpFile(String uploadType, final BaseMessage message, String url, final RequestComplete complete) {
        ModuleMgr.getMediaMgr().sendHttpFile(uploadType, url, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (!response.isOk()) {
                    updateFail(message, null);
                    return;
                }
                complete.onRequestComplete(response);
            }
        });
    }

    public void sendGiftMsg(String channelID, String whisperID, @Nullable int giftID,
                            @Nullable int giftCount, @Nullable int gType, int giftfrom, RequestComplete complete) {
        sendGiftMsg(channelID, whisperID, giftID, giftCount, gType, complete, MessageConstant.Know_stranger, giftfrom);
    }

    public void sendGiftMsg(String channelID, String whisperID, @Nullable int giftID,
                            @Nullable int giftCount, @Nullable int gType, RequestComplete complete) {
        sendGiftMsg(channelID, whisperID, giftID, giftCount, gType, complete, MessageConstant.Know_stranger, 2);
    }

    public void sendGiftMsg(final String channelID, final String whisperID, @Nullable final int giftID,
                            @Nullable final int giftCount, @Nullable final int gType, final RequestComplete complete, int know, final int giftfrom) {
        final GiftMessage giftMessage = new GiftMessage(channelID, whisperID, giftID, giftCount, gType, giftfrom);
        giftMessage.setStatus(MessageConstant.SENDING_STATUS);
        giftMessage.setJsonStr(giftMessage.getJson(giftMessage));
        giftMessage.setKnow(know);

        dbCenter.insertMsg(giftMessage, new DBCallback() {
            @Override
            public void OnDBExecuted(long result) {
                onChatMsgUpdate(giftMessage.getChannelID(), giftMessage.getWhisperID(), giftMessage);
                if (result != MessageConstant.OK)
                    return;

                String source = SourcePoint.getInstance().getGiftSource();
                ModuleMgr.getCommonMgr().sendGift(whisperID, String.valueOf(giftID), giftCount, gType, source, giftfrom, new RequestComplete() {
                    @Override
                    public void onRequestComplete(HttpResponse response) {
                        SendGiftResultInfo info = new SendGiftResultInfo();
                        info.parseJson(response.getResponseString());
                        if (complete != null)
                            complete.onRequestComplete(response);
                        if (response.isOk()) {
                            MsgMgr.getInstance().sendMsg(MsgType.MT_Update_TASK, null);//通知更新密友任务数据
                            MessageRet messageRet = new MessageRet();
                            messageRet.setMsgId(info.getMsgID());
                            updateOk(giftMessage, messageRet);
                            GiftsList.GiftInfo giftInfo = ModuleMgr.getCommonMgr().getGiftLists().getGiftInfo(giftID);
                            if (giftInfo == null)
                                return;
                            int stone = ModuleMgr.getCenterMgr().getMyInfo().getDiamand() - giftInfo.getMoney() * giftCount;
                            if (stone >= 0 && giftfrom != 1)
                                ModuleMgr.getCenterMgr().getMyInfo().setDiamand(stone);
                        } else {
                            updateFail(giftMessage, null);
                            showToast(response.getMsg());
                        }
                    }
                });
            }
        });
    }

    public void sendChumInviteMsg(@Nullable final String whisperID, @Nullable final int giftID, final RequestComplete complete) {
        sendChumInviteMsg(whisperID, giftID, complete, MessageConstant.Know_stranger);
    }

    /**
     * 发送密友消息
     *
     * @param whisperID
     * @param giftID
     * @param complete
     */
    public void sendChumInviteMsg(@Nullable final String whisperID, @Nullable final int giftID, final RequestComplete complete, int know) {
        final ChumInviteMessage chumInviteMessage = new ChumInviteMessage(whisperID, giftID);
        chumInviteMessage.setStatus(MessageConstant.SENDING_STATUS);
        chumInviteMessage.setJsonStr(chumInviteMessage.getJson(chumInviteMessage));
        chumInviteMessage.setKnow(know);

        dbCenter.insertMsg(chumInviteMessage, new DBCallback() {
            @Override
            public void OnDBExecuted(long result) {
                onChatMsgUpdate(null, chumInviteMessage.getWhisperID(), chumInviteMessage);
                if (result != MessageConstant.OK) return;

                ModuleMgr.getCommonMgr().startIntimateRequestM(TypeConvertUtil.toLong(whisperID), giftID, new RequestComplete() {
                    @Override
                    public void onRequestComplete(HttpResponse response) {
                        if (complete != null)
                            complete.onRequestComplete(response);
                        if (response.isOk()) {
                            SendChumInvite sendChumInvite = (SendChumInvite) response.getBaseData();
                            chumInviteMessage.setInvite_id(sendChumInvite.getInvite_id());
                            chumInviteMessage.setExpire(sendChumInvite.getExpire());
                            chumInviteMessage.setJsonStr(chumInviteMessage.getJson(chumInviteMessage));
                            MessageRet messageRet = new MessageRet();
                            messageRet.setMsgId(sendChumInvite.getMsgID());
                            updateOk(chumInviteMessage, messageRet);
                        } else {
                            updateFail(chumInviteMessage, null);
                            showToast(response.getMsg());
                        }
                    }
                });
            }
        });
    }

    public void sendFriendChumTaskMsg(@Nullable final String whisperID, @Nullable final long taskID, final RequestComplete complete) {
        sendChumTaskMsg(whisperID, taskID, complete, MessageConstant.Know_Friends);
    }

    /**
     * 发送密友任务消息
     *
     * @param whisperID
     * @param taskID
     * @param complete
     */
    public void sendChumTaskMsg(@Nullable final String whisperID, @Nullable final long taskID, final RequestComplete complete, int know) {
        final ChumTaskMessage chumTaskMessage = new ChumTaskMessage(whisperID, taskID);
        chumTaskMessage.setStatus(MessageConstant.OK_STATUS);
        chumTaskMessage.setKnow(know);

        ModuleMgr.getCommonMgr().sendIntimateTask(TypeConvertUtil.toLong(whisperID), taskID, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (complete != null)
                    complete.onRequestComplete(response);
                if (response.isOk()) {
                    SendChumTask task = new SendChumTask();
                    task.parseJson(response.getResponseString());
                    chumTaskMessage.setExpire(task.getExpire());
                    chumTaskMessage.setTask_award_info(task.getTask_award_info());
                    chumTaskMessage.setTask_info(task.getTask_info());
                    chumTaskMessage.setTask_action_tp(task.getTask_action_tp());
                    chumTaskMessage.setMsgID(task.getMsg_id());
                    chumTaskMessage.setJsonStr(chumTaskMessage.getJson(chumTaskMessage));
                    dbCenter.insertMsg(chumTaskMessage, new DBCallback() {
                        @Override
                        public void OnDBExecuted(long result) {
                            onChatMsgUpdate(null, chumTaskMessage.getWhisperID(), chumTaskMessage);
                        }
                    });
                } else {
                    showToast(response.getMsg());
                }
            }
        });
    }

    public void sendRedbagMsg(@Nullable final String whisperID, final RequestComplete complete) {
        sendRedbagMsg(whisperID, complete, MessageConstant.Know_Friends);
    }

    /**
     * 赠送红包消息
     *
     * @param whisperID
     * @param complete
     */
    public void sendRedbagMsg(@Nullable final String whisperID, final RequestComplete complete, final int know) {
        ModuleMgr.getCommonMgr().bestowRewardHongbao(TypeConvertUtil.toLong(whisperID), new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (complete != null)
                    complete.onRequestComplete(response);
                if (response.isOk()) {
                    RedbagInfo info = new RedbagInfo();
                    info.parseJson(response.getResponseString());

                    final GiftRedPackageMesaage giftRedPackageMesaage = new GiftRedPackageMesaage(whisperID, info.getRed_id(), info.getRed_amount());
                    giftRedPackageMesaage.setStatus(MessageConstant.OK_STATUS);
                    giftRedPackageMesaage.setKnow(know);
                    giftRedPackageMesaage.setMsgID(info.getMsg_id());
                    giftRedPackageMesaage.setJsonStr(giftRedPackageMesaage.getJson(giftRedPackageMesaage));

//                    SendChumTask task = new SendChumTask();
//                    task.parseJson(response.getResponseString());
//                    chumTaskMessage.setExpire(task.getExpire());
//                    chumTaskMessage.setTask_award_info(task.getTask_award_info());
//                    chumTaskMessage.setTask_info(task.getTask_info());
//                    chumTaskMessage.setTask_action_tp(task.getTask_action_tp());
//                    chumTaskMessage.setMsgID(task.getMsg_id());
//                    chumTaskMessage.setJsonStr(chumTaskMessage.getJson(chumTaskMessage));
                    dbCenter.insertMsg(giftRedPackageMesaage, new DBCallback() {
                        @Override
                        public void OnDBExecuted(long result) {
                            onChatMsgUpdate(null, giftRedPackageMesaage.getWhisperID(), giftRedPackageMesaage);
                        }
                    });
                } else {
                    showToast(response.getMsg());
                }
            }
        });
    }

    /**
     * 发送礼物
     *
     * @param channelID 目前没有用
     * @param whisperID 对方ID
     * @param giftID    礼物ID
     * @param giftCount 礼物个数
     * @param gType     来源
     */
    public void sendGiftMsg(final String channelID, final String whisperID, @Nullable final int giftID,
                            @Nullable final int giftCount, @Nullable final int gType, int giftfrom) {
        sendGiftMsg(channelID, whisperID, giftID, giftCount, gType, giftfrom, null);
    }

    /**
     * 发送礼物
     *
     * @param channelID 目前没有用
     * @param whisperID 对方ID
     * @param giftID    礼物ID
     * @param giftCount 礼物个数
     * @param gType     来源
     */
    public void sendGiftMsg(final String channelID, final String whisperID, @Nullable final int giftID,
                            @Nullable final int giftCount, @Nullable final int gType) {
        sendGiftMsg(channelID, whisperID, giftID, giftCount, gType, 2, null);
    }

    //tost提示
    private void showToast(final String tip) {
        MsgMgr.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PToast.showShort(tip + "");
            }
        });
    }

    /**
     * 更新小红点
     *
     * @param msgID
     */
    public void updateMsgFStatus(long msgID, final DBCallback callback) {
        updateMsgFStatus(msgID, false, callback);
    }

    /**
     * 更新小红点
     *
     * @param msgID
     * @param isUpdate 是否更新界面
     * @param callback
     */
    public void updateMsgFStatus(long msgID, final boolean isUpdate, final DBCallback callback) {
        dbCenter.getCenterFMessage().updateMsgFStatus(msgID, new DBCallback() {
            @Override
            public void OnDBExecuted(long result) {
                if (result == MessageConstant.OK && isUpdate) {
                    ModuleMgr.getChatListMgr().getWhisperListUnSubscribe();
                }
                if (callback != null) {
                    callback.OnDBExecuted(result);
                }
            }
        });
    }

    public void updateMsgSpecialMsgID(int type, long specialMsgID, DBCallback callback) {
        updateMsgSpecialMsgID(type, specialMsgID, false, callback);
    }

    /**
     * 以特殊消息ID修改状态
     *
     * @param specialMsgID
     * @param isUpdate     是否要更新界面
     * @param callback
     */
    public void updateMsgSpecialMsgID(int type, long specialMsgID, final boolean isUpdate, final DBCallback callback) {
        dbCenter.getCenterFMessage().updateMsgSpecialMsgID(type, specialMsgID, new DBCallback() {
            @Override
            public void OnDBExecuted(long result) {
                if (result == MessageConstant.OK && isUpdate) {
                    ModuleMgr.getChatListMgr().getWhisperListUnSubscribe();
                }
                if (callback != null) {
                    callback.OnDBExecuted(result);
                }
            }
        });
    }

    /**
     * 更新某个类型的所有消息
     *
     * @param uid
     * @param type
     * @param callback
     */
    public void updateTypeFStatus(long uid, int type, DBCallback callback) {
        dbCenter.getCenterFMessage().updateTypeFStatus(uid, type, callback);
    }

    /**
     * 更新送达状态
     *
     * @param msgID
     * @param callback
     */
    public void updateDeliveryStatus(long msgID, DBCallback callback) {
        dbCenter.getCenterFMessage().updateDeliveryStatus(msgID, null);
        dbCenter.getCenterFLetter().updateDeliveryStatus(msgID, callback);
    }

    public void updateFmessage(final BaseMessage message, DBCallback callback) {
        dbCenter.getCenterFMessage().updateMsgIDMsg(message, callback);
    }

    private void sendMessage(final BaseMessage message, final IMProxy.SendCallBack sendCallBack) {
        IMProxy.getInstance().send(new NetData(App.uid, message.getType(), message.getJsonStr()), new IMProxy.SendCallBack() {
            @Override
            public void onResult(long msgId, boolean group, String groupId, long sender, String contents) {
                if (sendCallBack != null) {
                    sendCallBack.onResult(msgId, group, groupId, sender, contents);
                }
                MessageRet messageRet = new MessageRet();
                messageRet.parseJson(contents);
                PLogger.d("isMsgOK=" + message.getType() + "=" + contents);

                if (messageRet.isS()) {
                    updateOk(message, messageRet);
// 暂时没有用
// if (!ModuleMgr.getCenterMgr().getMyInfo().isB()) {
//                        sendMessageRefreshYcoin();
//                    }
                    sendMessageRefreshDiamond(messageRet);
                    checkPermissions(message, true);
                } else {
                    checkPermissions(message, false);
                    if (MessageRet.MSG_CODE_PULL_BLACK == messageRet.getS()) {
                        updateFailBlacklist(message, messageRet);
                    } else {
                        updateFail(message, messageRet);
                    }
                    onInternalPro(messageRet);
                }
            }

            @Override
            public void onSendFailed(NetData data) {
                if (sendCallBack != null) {
                    sendCallBack.onSendFailed(data);
                }
                updateFail(message, null);
                PLogger.d("isMsgError=" + message.getJsonStr());
            }
        });
    }

    //更新钻石
    private void sendMessageRefreshDiamond(MessageRet messageRet) {
        UserDetail userDetail = ModuleMgr.getCenterMgr().getMyInfo();
        int diamondCost = messageRet.getDiamond_cost();
        if (diamondCost <= 0) {
            return;
        }

        if (!userDetail.isB() && userDetail.getYcoin() > 0) {
            int yCoin = userDetail.getYcoin() - diamondCost;
            userDetail.setYcoin(yCoin);

            MsgMgr.getInstance().sendMsg(MsgType.MT_Update_Diamond, diamondCost);
            return;
        }

        if (diamondCost <= 0) {
            return;
        }
        int diamand = userDetail.getDiamand() - diamondCost;
        userDetail.setDiamand(diamand);

        MsgMgr.getInstance().sendMsg(MsgType.MT_Update_Diamond, diamondCost);
    }

    private void sendMessageRefreshYcoin() {
        UserDetail userDetail = ModuleMgr.getCenterMgr().getMyInfo();
        if (userDetail.isUnlock_ycoin()) {//不需要Y币
            return;
        }

        //如何今天可以免费发一条消息不扣除Y币，并刷新Y币以防与服务器不同步
        if (ModuleMgr.getChatListMgr().getTodayChatShow()) {
            MsgMgr.getInstance().sendMsg(MsgType.MT_Update_Ycoin, true);
            return;
        }

        if (userDetail.isUnlock_vip() && userDetail.getYcoin() > 0) {//不需要VIP
            ModuleMgr.getCenterMgr().getMyInfo().setYcoin(userDetail.getYcoin() - 1);
            return;
        }

        if ((userDetail.isVip() && userDetail.getYcoin() > 0) || (!userDetail.isVip() && userDetail.getYcoin() > 79)) {
            if (userDetail.isVip())
                ModuleMgr.getCenterMgr().getMyInfo().setYcoin(userDetail.getYcoin() - 1);
            else
                ModuleMgr.getCenterMgr().getMyInfo().setYcoin(userDetail.getYcoin() - 80);
            MsgMgr.getInstance().sendMsg(MsgType.MT_Update_Ycoin, false);
        } else {
            MsgMgr.getInstance().sendMsg(MsgType.MT_Update_Ycoin, true);
        }
    }

    /**
     * 消息内部处理,如果失败之类等等
     */
    private void onInternalPro(MessageRet messageRet) {
        if (messageRet.isS()) {
            PLogger.printObject("s=" + messageRet.getS());
            switch (messageRet.getS()) {
                case MessageRet.MSG_CODE_BALANCE_INSUFFICIENT: {//-1 余额不足或者不是VIP
                    sendChatCanError();
                    break;
                }
                case MessageRet.MSG_CODE_PULL_BLACK: {//已拉黑
                    toSendMsgToUI("已拉黑，消息无法发送！");
                    break;
                }
                default:
                    sendChatCanError();
                    break;
            }
        }
    }

    /**
     * 聊天权限处理
     *
     * @param message
     * @updateAuthor Mr.Huang
     * @updateDate 2017-07-24
     * 处理免费一次聊天
     */
    private void checkPermissions(BaseMessage message, boolean success) {
        if (ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
            if (isChatShow() &&
                    MailSpecialID.customerService.getSpecialID() != message.getLWhisperID() &&
                    MailSpecialID.shareMsg.getSpecialID() != message.getLWhisperID()) {
                //更新时间
                ModuleMgr.getChatListMgr().setTodayChatShow();
            }
            MsgMgr.getInstance().sendMsg(MsgType.MT_Chat_Can, success);
        }
    }

    /**
     * 是否已经发完当天发的一条了
     */
    private void sendChatCanError() {
        UserDetail userDetail = ModuleMgr.getCenterMgr().getMyInfo();

        if (!userDetail.isMan() || isChatShow()
                || (userDetail.getDiamand() > 0) || UnlockComm.getJueweiOk()
                || (UnlockComm.getShareNum() > 0) || (userDetail.isB() && userDetail.isVip())
                || (!userDetail.isB() && (userDetail.getYcoin() > 79))) {
        } else {
            ModuleMgr.getChatListMgr().setTodayChatShow();
            MsgMgr.getInstance().sendMsg(MsgType.MT_Chat_Can, false);
        }
    }

    /**
     * 提示消息
     *
     * @param strMsg
     */
    private void toSendMsgToUI(final String strMsg) {
        MsgMgr.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(strMsg)) {
                    PToast.showShort(strMsg);
                }
            }
        });
    }

    // 成功后更新数据库
    private void updateOk(final BaseMessage message, MessageRet messageRet) {
        if (messageRet != null && messageRet.getMsgId() > 0) {
            message.setMsgID(messageRet.getMsgId());
            message.setTime(messageRet.getTm() <= 0 ? getTime() : messageRet.getTm());
        } else {
            message.setMsgID(MessageConstant.NumNo);
            message.setTime(getTime());
        }

        message.setStatus(MessageConstant.OK_STATUS);
        dbCenter.updateMsg(message, new DBCallback() {
            @Override
            public void OnDBExecuted(long result) {
                onChatMsgUpdate(message.getChannelID(), message.getWhisperID(), message);
            }
        });

    }

    private void updateFail(BaseMessage message, MessageRet messageRet) {
        updateFail(message, messageRet, MessageConstant.FAIL_STATUS);
    }

    private void updateFailBlacklist(BaseMessage message, MessageRet messageRet) {
        updateFail(message, messageRet, MessageConstant.BLACKLIST_STATUS);
    }

    /**
     * 发送失败
     *
     * @param message
     * @param messageRet
     * @param status
     */
    private void updateFail(final BaseMessage message, MessageRet messageRet, int status) {
        if (messageRet != null && messageRet.getMsgId() > 0) {
            message.setMsgID(messageRet.getMsgId());
            message.setTime(messageRet.getTm());
        } else {
            message.setMsgID(MessageConstant.NumNo);
            message.setTime(getTime());
        }
        message.setStatus(status);
        dbCenter.updateMsg(message, new DBCallback() {
            @Override
            public void OnDBExecuted(long result) {
                onChatMsgUpdate(message.getChannelID(), message.getWhisperID(), message);
            }
        });

    }

    /**
     * 删除多少个小时以前的消息
     *
     * @param hour
     * @return
     */
    public void deleteMessageHour(int hour) {
        dbCenter.deleteMessageHour(hour);
    }

    /**
     * 删除机器人多少小时以前的消息
     *
     * @param hour
     */
    public void deleteMessageKFIDHour(int hour) {
        dbCenter.deleteMessageKFIDHour(hour);
    }


    /**
     * 接收消息
     *
     * @param message
     */
    public void onReceiving(final BaseMessage message) {
        if (BaseMessage.BaseMessageType.video.getMsgType() == message.getType()) {//视频消息
            onReceivingVideoMsg(message);
        } else if (BaseMessage.BaseMessageType.chumInvite.getMsgType() == message.getType()) {//密友消息
            onReceivingChumInviteMsg(message);
        } else if (BaseMessage.BaseMessageType.chumTask.getMsgType() == message.getType()) {//密友任务
            onReceivingChumTaskMsg(message);
        } else if (BaseMessage.BaseMessageType.giftRedPackage.getMsgType() == message.getType()) {//礼物红包版本
            onReceivingGiftRedPackageMsg(message);
        } else {
            message.setStatus(MessageConstant.UNREAD_STATUS);
            PLogger.printObject(message);
            dbCenter.insertMsg(message, new DBCallback() {
                @Override
                public void OnDBExecuted(long result) {
                    if (result != MessageConstant.OK) {
                        return;
                    }
                    pushMsg(message);
                }
            });
        }
    }


    private void onReceivingChumInviteMsg(BaseMessage message) {
        final ChumInviteMessage chumInviteMessage = (ChumInviteMessage) message;
        if (chumInviteMessage.getClose_tp() == 2 || chumInviteMessage.getClose_tp() == 3) {
            MsgMgr.getInstance().sendMsg(MsgType.MT_Update_TASK, null);//通知更新密友任务数据
            if (!PSP.getInstance().getBoolean("ChumReject", false) || chumInviteMessage.getClose_tp() == 2) {
                message.setSendID(App.uid);
            }
            PSP.getInstance().put("ChumReject", false);
        }

        if (chumInviteMessage.isSender()) {
            chumInviteMessage.setStatus(MessageConstant.OK_STATUS);
        } else {
            message.setStatus(MessageConstant.UNREAD_STATUS);
        }

        dbCenter.insertMsgVideo(message, new DBCallback() {
            @Override
            public void OnDBExecuted(long result) {
                if (result != MessageConstant.OK) {
                    return;
                }
                pushMsg(chumInviteMessage);
            }
        });
    }

    private void onReceivingGiftRedPackageMsg(final BaseMessage message) {
        GiftRedPackageMesaage giftRedPackageMsg = (GiftRedPackageMesaage) message;
        if (giftRedPackageMsg.getFrom_type() == 2 || giftRedPackageMsg.getFrom_type() == 3) {//2通知红包已收取
            updateMsgSpecialMsgID(giftRedPackageMsg.getType(), giftRedPackageMsg.getSpecialMsgID(), null);
            MsgMgr.getInstance().sendMsg(MsgType.MT_Update_TASK, null);//通知更新密友任务数据
            pushMsg(message);
        } else {
            message.setStatus(MessageConstant.UNREAD_STATUS);
            dbCenter.insertMsg(message, new DBCallback() {
                @Override
                public void OnDBExecuted(long result) {
                    if (result != MessageConstant.OK) {
                        return;
                    }
                    pushMsg(message);
                }
            });
        }
    }

    private void onReceivingChumTaskMsg(final BaseMessage message) {
        final ChumTaskMessage chumTaskMessage = (ChumTaskMessage) message;

        if (chumTaskMessage.isTaskTp()) {
            MsgMgr.getInstance().sendMsg(MsgType.MT_Update_TASK, null);     //通知更新密友任务数据
            Msg msg = new Msg();
            msg.setData(chumTaskMessage);
            MsgMgr.getInstance().sendMsg(MsgType.MT_CHUM_TASK_FINISH, msg);//密友任务完成通知
            ModuleMgr.getChatListMgr().getSpecialMsgMgr().getIntimateTaskCnt();
            if (!ModuleMgr.getCenterMgr().getMyInfo().isMan()) { //为女时
                message.setSendID(App.uid);
            }
        }

        if (chumTaskMessage.isSender()) {
            chumTaskMessage.setStatus(MessageConstant.OK_STATUS);
        } else {
            message.setStatus(MessageConstant.UNREAD_STATUS);
        }

        dbCenter.insertMsgVideo(message, !chumTaskMessage.isTaskTp(), new DBCallback() {
            @Override
            public void OnDBExecuted(long result) {
                if (result != MessageConstant.OK) {
                    return;
                }
                pushMsg(chumTaskMessage);
            }
        });
        if (chumTaskMessage.getTask_tp() == 2) { //完成任务时插入完成任务提示消息
            insertHit(message);
        }
    }

    /**
     * 往数据库插一条14提示消息，标识任务状态
     *
     * @param message
     */
    private void insertHit(BaseMessage message) {
        // 往数据库插一条14提示消息，标识任务状态
        TextMessage textMessage = new TextMessage();
        textMessage.setWhisperID(message.getWhisperID());
        textMessage.setSendID(App.uid);
        textMessage.setMsgDesc(message.getMsgDesc() + "");
        textMessage.setcMsgID(BaseMessage.getCMsgID());
        textMessage.setType(BaseMessage.BaseMessageType.hint.getMsgType());
        textMessage.setJsonStr(textMessage.getJson(textMessage));
        ModuleMgr.getChatMgr().onLocalReceiving(textMessage);
    }

    /**
     * 视频语音消息
     *
     * @param message
     */
    private void onReceivingVideoMsg(BaseMessage message) {
        final VideoMessage videoMessage = (VideoMessage) message;
        //   reMsg-jsonStr={"d":167211,"fid":110872900,"mct":"","media_tp":1,"mt":1496199691,"mtp":24,
        // "ru":1,"tid":110872803,"vc_esc_code":3,"vc_id":100000496,"vc_tp":3}
        if (VideoAudioChatHelper.getInstance().isContain(videoMessage.getVideoID())) {//发送方
            message.setSendID(App.uid);
        }

        //3拒绝或取消 4挂断（挂断可能会收到不止一次）
        if (videoMessage.getVideoTp() == 3 || videoMessage.getVideoTp() == 4) {
            VideoAudioChatHelper.getInstance().remove(videoMessage.getVideoID());

            MsgMgr.getInstance().sendMsg(MsgType.MT_Update_TASK, null);//通知更新密友任务数据

            if (videoMessage.getVideoTp() == 4 && videoMessage.getVideoVcTalkTime() > 0) {
                UIShow.showRtcCommentAct(App.getActivity());
            }
            VideoAudioChatHelper.getInstance().setIsInACall(false);
            List<BaseMessage> messages = VideoAudioChatHelper.getInstance().getDlgMessage();
            if (messages.size() > 0) {  //视频之后处理弹框消息
                for (BaseMessage msg : messages) {
                    ModuleMgr.getChatListMgr().setSpecialMsg(msg);
                }
            }
            VideoAudioChatHelper.getInstance().getDlgMessage().clear();
        }

        if (videoMessage.isSender()) {
            videoMessage.setStatus(MessageConstant.OK_STATUS);
        } else {
            if (videoMessage.getEmLastStatus() == VideoMessage.EmLastStatus.cancel ||
                    videoMessage.getEmLastStatus() == VideoMessage.EmLastStatus.timeout) {
                videoMessage.setStatus(MessageConstant.UNREAD_STATUS);
            } else {
                videoMessage.setStatus(MessageConstant.READ_STATUS);
            }
        }

        if (TextUtils.isEmpty(videoMessage.getWhisperID())) return;

        dbCenter.insertMsgVideo(videoMessage, new DBCallback() {
            @Override
            public void OnDBExecuted(long result) {
                if (result != MessageConstant.OK) {
                    return;
                }

                pushMsg(videoMessage);
            }
        });

        //通知刷新个人资料
        long vcId = PSP.getInstance().getLong("VIDEOID" + App.uid, 0);
        if (videoMessage.getVideoTp() == 4 && videoMessage.getVideoID() != vcId) {
            MsgMgr.getInstance().sendMsg(MsgType.MT_Update_MyInfo, null);
            PSP.getInstance().put("VIDEOID" + App.uid, videoMessage.getVideoID() + "");
        }
    }

    /**
     * 批量接收消息
     *
     * @param baseMessageList
     */
    public void onReceivingList(List<BaseMessage> baseMessageList) {
        dbCenter.getCenterFMessage().insertMsgList(baseMessageList, null);
    }

    /**
     * 本地模拟消息
     *
     * @param message
     */
    public void onLocalReceiving(final BaseMessage message) {
        message.setDataSource(MessageConstant.FOUR);
        message.setStatus(MessageConstant.READ_STATUS);

        dbCenter.insertMsg(message, new DBCallback() {
            @Override
            public void OnDBExecuted(long result) {
                if (result != MessageConstant.OK) {
                    return;
                }

                pushMsg(message);
            }
        });
    }

    /**
     * 获取系统推送消息
     *
     * @param page
     * @return
     */
    public Observable<List<BaseMessage>> getSystemNotice(int page) {
        String systemMsg = String.valueOf(MailSpecialID.customerService.getSpecialID());
        Observable<List<BaseMessage>> observable = dbCenter.getCenterFMessage().queryMsgList(null,
                systemMsg, BaseMessage.BaseMessageType.sysNotice.getMsgType(), page, 20);
//        if (page == 0) {
//            dbCenter.getCenterFMessage().updateToRead(null, systemMsg, new DBCallback() {
//                @Override
//                public void OnDBExecuted(long result) {
//                    if (result != MessageConstant.OK) {
//                        return;
//                    }
//                    ModuleMgr.getChatListMgr().getWhisperListUnSubscribe();
//                }
//            });
//        }
        return observable;
    }


    /**
     * 获取历史聊天记录
     *
     * @param channelID 频道ID
     * @param whisperID 私聊ID
     * @param page      页码
     */
    public Observable<List<BaseMessage>> getHistoryChat(final String channelID, final String whisperID, int page) {
        return dbCenter.getCenterFMessage().queryMsgList(channelID, whisperID, page, 20);
    }

    /**
     * 获取某个用户最近20条聊天记录C
     *
     * @param channelID  频道ID
     * @param whisperID  私聊ID
     * @param last_msgid 群最后一条消息ID
     */
    public Observable<List<BaseMessage>> getRecentlyChat(final String channelID, final String whisperID, long last_msgid) {
        //把当前用户未读信息都更新成已读
        dbCenter.getCenterFMessage().updateToRead(channelID, whisperID, new DBCallback() {
            @Override
            public void OnDBExecuted(long result) {
                if (result == MessageConstant.OK) {
                    ModuleMgr.getChatListMgr().getWhisperListUnSubscribe();
                    if (!TextUtils.isEmpty(whisperID)) {
                        sendMailReadedMsg(channelID, Long.valueOf(whisperID));
                    }
                }
            }
        });

        return dbCenter.getCenterFMessage().queryMsgList(channelID, whisperID, 0, 20);
    }

    /**
     * 发送已读消息
     */
    public void sendMailReadedMsg(String channelID, final long userID) {
        sendMailReadedMsg(channelID, userID, new IMProxy.SendCallBack() {
            @Override
            public void onResult(long msgId, boolean group, String groupId, long sender, String contents) {
                MessageRet messageRet = new MessageRet();
                messageRet.parseJson(contents);
                if (messageRet.getS() == 0) {

                }
            }

            @Override
            public void onSendFailed(NetData data) {
            }
        });
    }

    private void pushMsg(BaseMessage message) {
        if (message == null)
            return;
        onChatMsgUpdate(message.getChannelID(), message.getWhisperID(), message);
    }

    /******************************************/
    private Set<ChatMsgInterface.ChatMsgListener> chatMsgListener = new LinkedHashSet<>();
    private Map<String, Set<ChatMsgInterface.ChatMsgListener>> chatMapMsgListener = new HashMap<>();

    /**
     * 注册一个私聊监听者，将监听者和所有消息类型绑定。
     * <p/>
     * 监听者实例。
     */
    public void attachChatListener(ChatMsgInterface.ChatMsgListener listener) {
        synchronized (chatMsgListener) {
            if (chatMsgListener == null) {
                return;
            }
            boolean listenerExist = false;
            for (ChatMsgInterface.ChatMsgListener item : chatMsgListener) {
                if (item != null && item == listener) {
                    listenerExist = true;
                    break;
                }
            }
            if (!listenerExist) {
                chatMsgListener.add(listener);
            }
        }
    }

    /**
     * 取消注册私聊的监听者，解除监听者的所有绑定。
     *
     * @param listener 监听者实例。
     */
    public void detachChatListener(ChatMsgInterface.ChatMsgListener listener) {
        if (chatMsgListener != null) {
            chatMsgListener.remove(listener);
        }

        for (Map.Entry<String, Set<ChatMsgInterface.ChatMsgListener>> entry : chatMapMsgListener.entrySet()) {
            entry.getValue().remove(listener);
        }
    }

    /**
     * 注册一个私聊监听者，将监听者和消息ID
     *
     * @param msgID        单个消息ID
     * @param chatListener
     */
    public void attachChatListener(final String msgID, final ChatMsgInterface.ChatMsgListener chatListener) {
        Set<ChatMsgInterface.ChatMsgListener> observers = chatMapMsgListener.get(msgID);
        if (observers == null) {
            observers = new LinkedHashSet<>();
            chatMapMsgListener.put(msgID, observers);
        }
        observers.add(chatListener);
    }

    /**
     * 取消注册私聊的监听者，解除监听者的所有绑定。
     *
     * @param chatListener
     */
    public void detachChatListener(final String msgID, final ChatMsgInterface.ChatMsgListener chatListener) {
        Set<ChatMsgInterface.ChatMsgListener> observers = chatMapMsgListener.get(msgID);
        if (observers != null) {
            observers.remove(chatListener);
        }

        if (chatMsgListener != null) {
            chatMsgListener.remove(chatListener);
        }
    }

    /**
     * 更新聊天信息
     *
     * @param msgID0  群ID
     * @param msgID1  私聊ID
     * @param message
     */
    public void onChatMsgUpdate(String msgID0, String msgID1, final BaseMessage message) {
        PLogger.printObject(message);
        ModuleMgr.getChatListMgr().getSpecialMsgMgr().handleGiftMsg(message);
        final Set<ChatMsgInterface.ChatMsgListener> listeners = chatMapMsgListener.get(msgID0);
        final Set<ChatMsgInterface.ChatMsgListener> listeners2 = chatMapMsgListener.get(msgID1);
        MsgMgr.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (listeners != null) {
                    for (ChatMsgInterface.ChatMsgListener imListener : listeners) {
                        imListener.onChatUpdate(message);
                    }
                }

                if (listeners2 != null) {
                    for (ChatMsgInterface.ChatMsgListener imListener : listeners2) {
                        imListener.onChatUpdate(message);
                    }
                }

                for (ChatMsgInterface.ChatMsgListener imListener : chatMsgListener) {
                    imListener.onChatUpdate(message);
                }

                //纯私聊消息
                if (!TextUtils.isEmpty(message.getWhisperID())) {
                    // 私聊消息
                    specialMgr.onWhisperMsgUpdate(message);
                }

                //角标消息更改
                if (App.uid != message.getSendID() && UnreadReceiveMsgType.getUnreadReceiveMsgID(message.getType()) != null) {
                    specialMgr.updateUnreadMsg(message);
                }
            }
        });
    }

    /**
     * 个人资料存储
     */
    private Map<Long, ChatMsgInterface.InfoComplete> infoMap = new HashMap<>();

    public void getUserInfoLightweight(final long uid, final ChatMsgInterface.InfoComplete infoComplete) {
        synchronized (infoMap) {
            infoMap.put(uid, infoComplete);
            Observable<UserInfoLightweight> observable = dbCenter.getCacheCenter().queryProfile(uid);
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            observable.subscribe(new Observer<UserInfoLightweight>() {
                private Disposable queryDisposable;

                @Override
                public void onSubscribe(@NonNull Disposable d) {
                    queryDisposable = d;
                }

                @Override
                public void onNext(@NonNull UserInfoLightweight lightweight) {
                    queryDisposable.dispose();

                    PLogger.d("getUserInfoLightweight=" + lightweight.toString());
                    long infoTime = lightweight.getTime();
                    //如果有数据且是一小时内请求的就不用请求了
                    if (lightweight.getUid() > 0 && (infoTime > 0 && (infoTime + (10 * 60 * 1000)) > getTime())) {
                        removeInfoComplete(true, true, uid, lightweight);
                        return;
                    }
                    removeInfoComplete(false, false, uid, lightweight);
                    getNetSingleProfile(uid, new ChatMsgInterface.InfoComplete() {
                        @Override
                        public void onReqComplete(boolean ret, UserInfoLightweight infoLightweight) {
                            // 再次请求一遍之后不管成功失败都移除
                            removeInfoComplete(true, ret, uid, infoLightweight);
                        }
                    });
                }

                @Override
                public void onError(@NonNull Throwable e) {
                }

                @Override
                public void onComplete() {
                }
            });
        }
    }

    /**
     * 从数据库中查询多个用户信息
     *
     * @param uids 用户uid集合
     * @return 查询结果Observable
     */
    public Observable<List<UserInfoLightweight>> getUserInfoList(List<Long> uids) {
        return dbCenter.getCacheCenter().queryProfile(uids);
    }

    /**
     * 批量请求简略个人资料
     *
     * @param userIds 用户uid集合
     */
    public void getProFile(List<Long> userIds) {
        ModuleMgr.getCommonMgr().reqUserInfoSummary(userIds, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (!response.isOk()) {
                    return;
                }
                UserInfoLightweightList infoLightweightList = new UserInfoLightweightList();
                infoLightweightList.parseJsonSummary(response.getResponseJson());

                if (infoLightweightList.getUserInfos() != null && infoLightweightList.getUserInfos().size() > 0) {//数据大于1条
                    ArrayList<UserInfoLightweight> infoLightweights = infoLightweightList.getUserInfos();

                    updateUserInfoList(infoLightweights);
                    dbCenter.getCacheCenter().storageProfileData(infoLightweights, null);
                }
            }
        });
    }

    /**
     * 带回调地请求单个用户的简略个人资料
     *
     * @param userID       查询的用户uid
     * @param infoComplete 查询完成回调
     */
    public void getNetSingleProfile(final long userID, final ChatMsgInterface.InfoComplete infoComplete) {
        List<Long> longs = new ArrayList<>();
        longs.add(userID);
        ModuleMgr.getCommonMgr().reqUserInfoSummary(longs, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                PLogger.d("---getNetSingleProfile--->" + response.getResponseString());
                UserInfoLightweight temp = new UserInfoLightweight();
                if (!response.isOk()) {
                    infoComplete.onReqComplete(false, temp);
                    return;
                }

                UserInfoLightweightList infoLightweightList = new UserInfoLightweightList();
                infoLightweightList.parseJsonSummary(response.getResponseJson());

                if (infoLightweightList.getUserInfos() == null || infoLightweightList.getUserInfos().isEmpty()) {
                    infoComplete.onReqComplete(false, temp);
                    return;
                }

                temp = infoLightweightList.getUserInfos().get(0);
                temp.setTime(getTime());
                temp.setUid(userID);
                infoComplete.onReqComplete(true, temp);

                dbCenter.getCacheCenter().storageProfileData(temp, null);
                dbCenter.getCenterFLetter().updateUserInfoLight(temp, null);
            }
        });
    }

    /**
     * 更新消息列表个人资料
     *
     * @param lightweight
     * @param callback
     */
    public void updateUserInfoLight(UserInfoLightweight lightweight, DBCallback callback) {
        dbCenter.getCenterFLetter().updateUserInfoLight(lightweight, callback);
    }

    /**
     * 批量更新数据库中存储的简略个人资料
     *
     * @param infoLightweights 需要批量更新的简略个人资料
     */
    public void updateUserInfoList(List<UserInfoLightweight> infoLightweights) {
        dbCenter.getCenterFLetter().updateUserInfoLightList(infoLightweights);
    }

    /**
     * 更新个人资料
     *
     * @param isRemove        是否要从回调map中移除：true是移除（回调成功之后移除本次回调）
     * @param isOK            是否请求成功：true是成功（包括数据库查询成功及请求返回数据成功）
     * @param infoLightweight 个人资料数据
     */
    private void removeInfoComplete(boolean isRemove, boolean isOK, long userID, UserInfoLightweight infoLightweight) {
        PLogger.d("---removeInfoComplete--->" + infoLightweight);
        synchronized (infoMap) {
            if (infoMap.size() <= 0)
                return;
            ChatMsgInterface.InfoComplete infoComplete = infoMap.get(userID);
            if (infoComplete != null)
                infoComplete.onReqComplete(isOK, infoLightweight);
            if (isRemove && infoComplete != null)
                infoMap.remove(userID);
        }
    }

    private long getTime() {
        return ModuleMgr.getAppMgr().getTime();
    }

    /**
     * 发送礼物
     *
     * @param channelID 目前没有用
     * @param giftID    礼物ID
     * @param giftCount 礼物个数
     * @param complete  接口回调
     */
    public void sendLiveGiftMsg(final String channelID, final String roomId, @Nullable final int giftID,
                                @Nullable final int giftCount, final OnSendGiftCallbackListener complete, final boolean isSuccessCallback) {

        String source = SourcePoint.getInstance().getGiftSource();
        ModuleMgr.getCommonMgr().sendLiveGift(roomId, String.valueOf(giftID), giftCount, source, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (response.isOk()) {
                    try {
                        JSONObject resJo = response.getResponseJson().getJSONObject("res");
                        int stone;
                        if (resJo.has("gem_total") && !resJo.isNull("gem_total")) {
                            stone = resJo.optInt("gem_total");

                        } else {
                            GiftsList.GiftInfo giftInfo = ModuleMgr.getCommonMgr().getGiftLists().getGiftInfo(giftID);
                            if (giftInfo == null)
                                return;
                            stone = ModuleMgr.getCenterMgr().getMyInfo().getDiamand() - giftInfo.getMoney() * giftCount;
                        }
                        ModuleMgr.getCenterMgr().getMyInfo().setDiamand(stone);
                        if (isSuccessCallback) {
                            SendGiftCallbackBean bean = new SendGiftCallbackBean();
                            bean.setRoom_id(roomId);
                            bean.setGift_count(giftCount);
                            bean.setGift_id(giftID);
                            complete.onSendGiftCallback(true, bean);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        complete.onSendGiftCallback(false, null);
                    }
                } else {
                    showToast(response.getMsg());
                    complete.onSendGiftCallback(false, null);
                }
            }
        });
    }
}