// ICSCallback.aidl
package com.juxin.predestinate.module.logic.socket;

import com.juxin.predestinate.module.logic.socket.NetData;
// Declare any non-default types here with import statements

interface ICSCallback {

    // 处理消息
    void onMessage(in NetData data);

    /**
    * 发送失败的消息
    */
    void onSendMsgError(in NetData data);

    // 即时通讯状态
    // type: 0 登录成功；1 登录失败；2 连接成功；3 断开连接；
    // msg: 提示消息
    void onStatusChange(int type, String msg);

    //帐号无效
    void accountInvalid(int reason, String content);

    //心跳状态回送，判断socket连接状态
    void heartbeatStatus(boolean isBeating);

      //请求切换域名
    void changeDomainName();
}
