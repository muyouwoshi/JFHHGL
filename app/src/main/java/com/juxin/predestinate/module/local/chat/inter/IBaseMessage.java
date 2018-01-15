package com.juxin.predestinate.module.local.chat.inter;

import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;

/**
 * Created by Kind on 2017/3/17.
 */

public interface IBaseMessage {

    /**
     * 解析json
     *
     * @param jsonStr
     */
    BaseMessage parseJson(String jsonStr);

    /**
     * 拼接json
     *
     * @param message
     */
    String getJson(BaseMessage message);
}
