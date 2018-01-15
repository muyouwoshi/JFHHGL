package com.juxin.predestinate.module.local.chat.utils;

import com.juxin.predestinate.module.logic.config.Constant;

/**
 * 消息常量类
 * Created by Kind on 2017/5/24.
 */

public class MessageConstant {

    public static int KF_ID = 1;//机器人ID
    /**
     * 关系 >=2 的时候放消息列表
     * 关系 >= 1的时候放陌生人列表
     * 剩下的放打招呼
     */
    public static int Know_stranger = 0;//0：陌生人
    public static int Know_Chat_Person = 1;//1：有过聊天的的人
    public static int Know_Friends = 2;//2：有过礼物来往的好友


    //数据来源 1.本地  2.网络  3.离线(默认是本地) 4.模拟
    public static int ONE = 1;
    public static int TWO = 2;
    public static int THREE = 3;
    public static int FOUR = 4;

    public final static int NumDefault = 0;//数字默认值
    public final static String StrDefault = "";//字符默认值

    //显示权重
    public static int Max_Weight = 10000;//最大权重
    public static int Invite_Ordinary_Weight = 900;//邀请普通
    public static int Invite_Max_Weight = 999;//邀请最大
    public static int Private_Ordinary_Weight = 800;//私有消息普通
    public static int Private_Max_Weight = 888;//私有最大

    public static int Gift_Ordinary_Weight = 700;//礼物消息普通
    public static int Gift_Max_Weight = 777;//礼物消息普通
    public static int Stranger_Ordinary_Weight = 600;//陌生人消息普通
    public static int Stranger_Max_Weight = 666;//陌生人消息普通

    public static int Great_Weight = 100;//大权重
    public static int In_Weight = 50;//中等权重
    public static int Small_Weight = 1;//小权重

    //聊天消息加载更多
    public static int Chat_Mail_Invite = 1;//邀请消息
    public static int Chat_Mail_Private = 2;//私有消息
    public static int Chat_Mail_Stranger = 3;//陌生人消息
    public static int Chat_Mail_Gift = 4;//礼物消息

    public static final int ERROR = -1; //失败
    public static final int OK = 1;//成功

    public static final int NumNo = -1;// 数据库数字不修改

    public static final int OK_STATUS = 1;//发送成功
    public static final int FAIL_STATUS = 2;//发送失败
    public static final int SENDING_STATUS = 3;//发送中
    public static final int BLACKLIST_STATUS = 4;//黑名单中
    public static final int DELIVERY_STATUS = 5;//送达
    public static final int UNREAD_STATUS = 10;//未读
    public static final int READ_STATUS = 11;//对方已读

    /**
     * 是否超过最大版本消息
     * @return true就是超过版本了
     */
    public static boolean isMaxVersionMsg(int type){
        return (type > Constant.SHOW_MAX_VERSION && type <= 100);
    }
}
