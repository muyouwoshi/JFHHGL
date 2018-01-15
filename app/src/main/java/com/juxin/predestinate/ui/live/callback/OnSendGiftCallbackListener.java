package com.juxin.predestinate.ui.live.callback;

import com.juxin.predestinate.ui.live.bean.SendGiftCallbackBean;

/**
 * Created by terry on 2017/7/24.
 * 发送礼物回调接口
 */

public interface OnSendGiftCallbackListener{

    /**
     *
     * @param success 是否发送成功
     * @param bean 礼物回调对象
     */
    void onSendGiftCallback(boolean success, SendGiftCallbackBean bean);
}