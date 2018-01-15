// ICoreService.aidl
package com.juxin.predestinate.module.logic.socket;

// Declare any non-default types here with import statements
import com.juxin.predestinate.module.logic.socket.ICSCallback;
import com.juxin.predestinate.module.logic.socket.NetData;

interface ICoreService {

    /**
    * 设置回调。
    */
    void setCallback(ICSCallback iCSCallback);

    /**
    * 登录。
    */
    void login(long uid, String token);

    /**
    * 退出登录。
    */
    void logout();

    /**
    * 设置GPS坐标，经纬度坐标。
    */
    void setLocationGPS(double longitude, double latitude);

    /**
    * 设置系统类型。操作系统
    */
    void setSystemInfo(int systemInfo);
    /**
    * 发送消息
    */
    void sendMsg(in NetData data);

    /**
         * 获取socket状态
         */
    int getSocketStatus();

     /**
     * 重新设置socket 链接的地址和端口
     */
    void reSetHostPort(String hosts,int port);
}
