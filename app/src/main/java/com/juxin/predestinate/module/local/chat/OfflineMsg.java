package com.juxin.predestinate.module.local.chat;

import android.support.v4.app.FragmentActivity;

import com.juxin.library.log.PLogger;
import com.juxin.library.log.PSP;
import com.juxin.predestinate.bean.my.OffMsgInfo;
import com.juxin.predestinate.bean.start.OfflineBean;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.chat.msgtype.DialogMessage;
import com.juxin.predestinate.module.local.chat.utils.MessageConstant;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.BaseUtil;
import com.juxin.predestinate.module.util.PreferenceUtils;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.main.MainActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 离线消息，减轻重度依赖
 * Created by Kind on 2017/8/22.
 */

public class OfflineMsg {

    public void init() {
    }

    public void release() {
    }

    // ------------------------------------- 离线消息处理 ------------------------------------
    private static Map<Long, OfflineBean> lastOfflineAVMap = new HashMap<>(); // 维护离线音视频消息

    /**
     * 获取离线消息并处理
     */
    public void getOfflineMsg() {
        ModuleMgr.getCommonMgr().reqOfflineMsg(new RequestComplete() {
            @Override
            public void onRequestComplete(final HttpResponse response) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List < OffMsgInfo > mOffMsgInfos = new ArrayList<>();
                        PLogger.d("offlineMsg:  " + response.getResponseString());
                        if (!response.isOk())
                            return;

                        com.juxin.predestinate.bean.start.OfflineMsg offlineMsg = (com.juxin.predestinate.bean.start.OfflineMsg) response.getBaseData();
                        if (offlineMsg == null || offlineMsg.getMsgList().size() <= 0)
                            return;
                        OfflineBean bea = null;
                        if (offlineMsg.getMsgList().size() > 0)
                            bea = offlineMsg.getMsgList().get(offlineMsg.getMsgList().size() - 1);
                        // 逐条处理离线消息
                        for (OfflineBean bean : offlineMsg.getMsgList()) {
                            if (bean == null)
                                continue;
                            dispatchOfflineMsg(bean);
                            if (bean.getMtp() != BaseMessage.Recved_MsgType && bean.getMtp() != BaseMessage.System_MsgType) {//普通消息才加入送达列表中
                                mOffMsgInfos.add(new OffMsgInfo(bean.getFid(), bean.getD(), bean.getMtp()));
                            }
                            if (bean == bea) {
                                ModuleMgr.getCommonMgr().reqOfflineRecvedMsg(mOffMsgInfos, null);
                            }
                        }

                        // 服务器每次最多取1000条，若超过则再次请求
                        if (offlineMsg.getMsgList().size() >= 1000) {
                            getOfflineMsg();
                            return;
                        }
                        dispatchLastOfflineAVMap();
                    }
                }).start();
            }
        });
    }

    /**
     * 离线消息派发
     */
    private void dispatchOfflineMsg(OfflineBean bean) {
        if (bean.getD() == 0)
            return;
        if (lastOfflineAVMap == null)
            lastOfflineAVMap = new HashMap<>();
        // 音视频消息
        if (bean.getMtp() == BaseMessage.BaseMessageType.video.getMsgType()) {
            long vc_id = bean.getVc_id();
            if (lastOfflineAVMap.get(vc_id) == null) {
                lastOfflineAVMap.put(vc_id, bean);
            } else {
                lastOfflineAVMap.remove(vc_id);
            }
            return;
        }
        //已送达消息
        if (bean.getMtp() == BaseMessage.Recved_MsgType) {
            ModuleMgr.getChatMgr().updateDeliveryStatus(bean.getMsg_id(), null);
            return;
        }

        //已读消息
        if (bean.getMtp() == BaseMessage.System_MsgType) {
            ModuleMgr.getChatListMgr().updateToReadPrivate(bean.getFid());
            ModuleMgr.getChatMgr().updateOtherSideRead(null, bean.getFid() + "", bean.getTid() + "");
            return;
        }

        offlineMessage(bean.getJsonStr());
    }

    /**
     * 处理最新的音视频离线消息
     */
    private void dispatchLastOfflineAVMap() {
        if (lastOfflineAVMap == null || lastOfflineAVMap.size() == 0)
            return;
        if (BaseUtil.isScreenLock(App.context))
            return;

        OfflineBean bean = null;
        long mt = 0;

        for (Map.Entry<Long, OfflineBean> entry : lastOfflineAVMap.entrySet()) {
            OfflineBean msgBean = entry.getValue();
            if (msgBean == null)
                return;

            // 邀请加入聊天, 过滤最新一条
            if (msgBean.getVc_tp() == 1) {
                long t = msgBean.getMt();   // 最新时间戳
                if (t > mt) {
                    mt = t;
                    bean = msgBean;
                }
            }
        }
        lastOfflineAVMap.clear();
        if (bean != null) {
            offlineMessage(bean.getJsonStr());
        }
    }

    //离线消息
    private void offlineMessage(String str) {
        try {
            JSONObject tmp = new JSONObject(str);
            long from_id = tmp.optLong("fid");//发送者ID
            int type = tmp.optInt("mtp");
            long msgID = tmp.optLong("d");
            BaseMessage.BaseMessageType messageType = BaseMessage.BaseMessageType.valueOf(type);
            BaseMessage message;
            if (messageType != null) {
                message = messageType.msgClass.newInstance();
                message.setSave(true);
            } else {
                message = new BaseMessage();
                message.setSave(false);
            }
            message.parseJson(tmp.toString());
            message.setSendID(from_id);
            if (!tmp.isNull("af") && 1 == tmp.optInt("af")) {
                message.setWhisperID(String.valueOf(tmp.optLong("tid")));
            } else {
                message.setWhisperID(String.valueOf(from_id));
            }
            message.setMsgID(msgID);
            message.setDataSource(MessageConstant.THREE);
            message.setType(type);
            message.setChannelID(null);

            PLogger.printObject("offlineMessage=" + message.getType());
            if (message.getType() == BaseMessage.DIALOG_TYPE) {
                DialogMessage msg = (DialogMessage) message;
                if(DialogMessage.TYPE_FREEKEY_RECVED.equals(msg.getDialogType())){
//                    DialogMessage.FreeKeyDialogData data = (DialogMessage.FreeKeyDialogData) msg.getDialogData();
//                    if (App.activity != null && App.activity instanceof MainActivity) {
//                        UIShow.showGetKeyOpenerDialog((FragmentActivity) App.activity, data.keyCount);
//                    } else {
//                        PSP.getInstance().put("show_get_key_opener_dialog_" + App.uid, data.keyCount);
//                    }
                }else if(DialogMessage.FREEVIDEOCARD_RECVED.equals(msg.getDialogType())){
                    DialogMessage.FreeVideoCard data = (DialogMessage.FreeVideoCard) msg.getDialogData();
                    if (App.activity != null) {
                        UIShow.showGetVideoCardDialog((FragmentActivity) App.activity, data.count, data.videochat_len);
                    } else {
                        PreferenceUtils.putFreeVideoInfo(data.count, data.videochat_len);
                    }
                }
                return;
            }
            if (message.isSave()) {
                ModuleMgr.getChatMgr().onReceiving(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
