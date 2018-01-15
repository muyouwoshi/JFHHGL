package com.juxin.predestinate.module.local.msgview.chatview.notifyview;

import android.text.TextUtils;

import com.juxin.library.log.PLogger;
import com.juxin.predestinate.bean.net.BaseData;

/**
 * 一个简单消息基类。
 */
public abstract class SimpleMsg extends BaseData {

    /**
     * 消息类型。
     */
    private String type = null;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static SimpleMsg toNewInstance(String jsonStr, Class<? extends SimpleMsg> msgClass) {
        SimpleMsg msg = null;

        if (TextUtils.isEmpty(jsonStr) || msgClass == null) {
            return msg;
        }

        try {
            msg = msgClass.newInstance();
            msg.parseJson(jsonStr);
        } catch (Exception e) {
            PLogger.printThrowable(e);
        }
        return msg;
    }
}
