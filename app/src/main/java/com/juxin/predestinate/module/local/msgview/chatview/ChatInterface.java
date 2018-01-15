package com.juxin.predestinate.module.local.msgview.chatview;

import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;

/**
 * Created by Kind on 2017/3/30.
 */

public class ChatInterface {
    /**
     * 点击聊天内容相关接口，界面层。
     */
    public interface OnClickChatItemListener {
        /**
         * 当头像被点击的时候调用。
         *
         * @param msgData 消息。
         * @return 返回true表示已经被处理过。
         */
        boolean onClickHead(BaseMessage msgData);

        /**
         * 当发送状态被点击的时候掉调用。
         *
         * @param msgData 消息。
         * @return 返回true表示已经被处理过。
         */
        boolean onClickStatus(BaseMessage msgData);

        /**
         * 当整个消息内容被点击时调用。
         *
         * @param msgData   消息。
         * @param longClick 长按。
         * @return 返回true表示已经被处理过。
         */
        boolean onClickContent(BaseMessage msgData, boolean longClick);

        /**
         * 当消息内容发送失败后，点击重发
         *
         * @param msgData
         * @return
         */
        boolean onClickErrorResend(BaseMessage msgData);
    }

    /**
     * 监听获取用户信息。
     */
    public interface OnUserInfoListener {
        void onComplete(UserInfoLightweight infoLightweight);
    }
}
