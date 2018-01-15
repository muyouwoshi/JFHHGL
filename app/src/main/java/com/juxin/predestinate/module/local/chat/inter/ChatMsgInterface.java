package com.juxin.predestinate.module.local.chat.inter;

import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import java.util.List;

/**
 * 聊天消息的接口
 * Created by Kind on 2016/11/24.
 */

public class ChatMsgInterface {

    /**
     * 监听事件的回调接口。
     */
    public interface ChatMsgListener {

        /**
         * 更新数据
         * @param message
         */
        void onChatUpdate(BaseMessage message);
    }

    /**
     * 查询单个用户个人资料
     */
    public interface InfoComplete {
        void onReqComplete(boolean ret, UserInfoLightweight infoLightweight);
    }

    /**
     * 批量查询单个用户个人资料
     */
    public interface InfoListComplete {
        void onReqInfosComplete(List<UserInfoLightweight> infoLightweights);
    }

    public interface DBMsgListener {
        void onDBMsgListener(long ret);
    }

    public interface MsgListener {
        /**
         * 单个消息回调
         * @param messages 返回的数据 如果是空就是没有数据
         */
        void onListener(BaseMessage messages);
    }

    /**
     * 系统消息推送
     */
    public interface SystemMsgListener {
        void onSystemMsg(BaseMessage message);
    }

    /**
     * 未读角标消息
     */
    public interface UnreadReceiveMsgListener {
        void onUpdateUnread(BaseMessage message);
    }

    /**
     * 私聊消息
     */
    public interface WhisperMsgListener {
        void onUpdateWhisper(BaseMessage message);
    }
}
