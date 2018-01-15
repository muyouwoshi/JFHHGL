package com.juxin.predestinate.module.logic.socket;

import com.juxin.predestinate.module.logic.config.Hosts;

/**
 * socket长连接中一些常用的常量
 */
public class TCPConstant {

    // socket连接地址
    public static String HOST = Hosts.FATE_IT_TCP;    //socket连接IP地址
    public static int PORT = Hosts.FATE_IT_TCP_PORT;                    //socket连接端口

    // --------------socket常量----------------
    public static final int MSG_ID_Login = 1;               //长连接登录消息id
    public static final int MSG_ID_Account_Closed = 65530;    //账号被封消息
    public static final int MSG_ID_KICK_Offline = 65533;    //异地登陆踢下线消息id
    //    public static final int MSG_ID_Heartbeat_Reply = 65532; //回送逻辑的心跳消息，客户端发送心跳，服务器回复该类型的消息，包体为0
    public static final int MSG_ID_Heartbeat = 65535;       //普通心跳消息id，登录使用
    public static final int MSG_ID_PUSH_MESSAGE = 102;      //服务器推送的消息
    public static final int MSG_ID_SERVER_PUSH_START_INDEX = 100;//服务器推送消息，开始id
    public static final int MSG_ID_SERVER_PUSH_END_INDEX = 200; //服务器推送消息，结束id（暂定）
    public static final long Heartbeat_Time = 60 * 1000;    //心跳时间间隔，60s
    public static final long SEND_RESPOND_TIME_OUT = 60 * 1000; //发送消息等待响应的超时时间
    public static final int MSG_ID_HEART_BEAT3 = 65531;     // 表示用户的前端状态 1为前端在线 2为非前端在线 用户切换前端状态时需要发一次心跳消息

    /**
     * 即时通许中，CoreService连接服务器时，返回的几种状态。
     */
    public static final int SOCKET_STATUS_Login_Success = 0;    //登录成功
    public static final int SOCKET_STATUS_Login_Fail = 1;       //登录失败
    public static final int SOCKET_STATUS_Connected = 2;        //连接成功
    public static final int SOCKET_STATUS_Disconnect = 3;       //断开连接
    public static final int SOCKET_STATUS_ConnectIng = 4;       //正在连接

    /**
     * [消息体长度（4Bytes）][客户ID（4Byte）][消息类型 (2Bytes)][内容]
     */
    public static final int TCP_DATA_Header_Size = 4 + 4 + 2;

    public static final int SOCKET_CONNECT_TIMEOUT = 5000;             //Socket连接时超时时间
    public static final int SOCKET_AUTO_CONNECT_Increment_Time = 1000;  //Socket自动重连时间间隔
    public static final int SOCKET_CONNECT_Heartbeat_Time = 30 * 1000;  //连接服务器的心跳间隔
}
