package com.juxin.predestinate.module.local.chat;

import android.text.TextUtils;
import com.juxin.library.log.PLogger;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.chat.msgtype.NoSaveMsgType;
import com.juxin.predestinate.module.local.chat.utils.MessageConstant;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.socket.IMProxy;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 接收消息处理类
 * Created by Kind on 2017/4/1.
 */

public class RecMessageMgr implements IMProxy.IMListener {

    public void init() {
        IMProxy.getInstance().attach(this);
    }

    public void release() {
        IMProxy.getInstance().detach(this);
    }

    @Override
    public void onMessage(long msgID, boolean group, String groupId, long senderID, String jsonStr) {
        try {
            PLogger.d("reMsg-jsonStr=" + jsonStr);
            JSONObject object = new JSONObject(jsonStr);
            if (senderID <= 0) {//如果小于或等于0
                senderID = object.optLong("fid");
            }
            int type = object.optInt("mtp");
            BaseMessage.BaseMessageType messageType = BaseMessage.BaseMessageType.valueOf(type);
            BaseMessage message;
            //基本消息
            if (messageType != null) {
                message = messageType.msgClass.newInstance();
                onSaveSendUI(true, message, group, msgID, groupId, senderID, jsonStr, type);
            } else {
                message = new BaseMessage();
                onSaveSendUI(false, message, group, msgID, groupId, senderID, jsonStr, type);
            }
        } catch (Exception e) {
            PLogger.printThrowable(e);
        }
    }

    private void onSaveSendUI(boolean isSave, BaseMessage message, boolean group, long msgID,
                              String groupId, long senderID, String jsonStr, int type) throws JSONException {
        PLogger.d(message.getWhisperID());
        message.setSendID(senderID);
        message.setMsgID(msgID);
        message.setType(type);
        message.parseJson(jsonStr);
        message.setDataSource(MessageConstant.TWO);

        if (group) {// 群聊
            message.setChannelID(groupId);
            ModuleMgr.getChatMgr().onChatMsgUpdate(message.getChannelID(), null, message);
        } else {//私聊或群私聊
            if (!TextUtils.isEmpty(groupId)) {//群里面的私聊
                message.setChannelID(groupId);
            }

            JSONObject object = new JSONObject(jsonStr);
            if (!object.isNull("af") && 1 == object.optInt("af") && App.uid == senderID) {
                message.setWhisperID(String.valueOf(object.optLong("tid")));
            } else {
                message.setWhisperID(String.valueOf(senderID));
            }

            //接收特殊消息
            ModuleMgr.getChatListMgr().setSpecialMsg(message);

            //红包消息不保存，也不通知上层,女性对男性的语音(视频)邀请送达男用户 此消息为群发视频/语音，送达人数对女用户的通知也不保存，不通知ui
            //@updateAuthor Mr.Huang
            //@updateDate 2017-07-24
            // 弹框消息不通知UI
            if (BaseMessage.TalkRed_MsgType == message.getType()
                    || BaseMessage.VideoInviteTomen_MsgType == message.getType()
                    || BaseMessage.DIALOG_TYPE == message.getType()) {
                return;
            }

            if (NoSaveMsgType.getNoSaveMsgType(message.getType()) != null) {
                isSave = false;
            }

            if (isSave) {//是否保存
                ModuleMgr.getChatMgr().onReceiving(message);
            } else {
                ModuleMgr.getChatMgr().onChatMsgUpdate(message.getChannelID(), message.getWhisperID(), message);
            }
        }
    }
}