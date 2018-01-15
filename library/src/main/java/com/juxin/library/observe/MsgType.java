package com.juxin.library.observe;

/**
 * 主线订阅消息类型
 * Created by ZRP on 2016/12/27.
 */
public class MsgType {

    /**
     * 系统相关的消息类型。
     */
    public static final String MT_System = "MT_System";

    /**
     * 服务器配置更新通知
     */
    public static final String MT_Server_Config = "MT_Server_Config";

    /**
     * 应用相关的消息类型。<br>
     * 登录、退出登录消息。true登录；false退出。
     */
    public static final String MT_App_Login = "MT_App_Login";

    /**
     * 应用相关的消息类型。<br>
     * 退出应用程序。
     */
    public static final String MT_App_Exit = "MT_App_Exit";

    /**
     * 和CoreService建立通讯。<br>
     * 连接、断开消息。true连接；false断开。
     */
    public static final String MT_App_CoreService = "MT_App_CoreService";

    /**
     * 推送消息。发送小米或者信鸽发过来的消息。
     */
    public static final String MT_App_Push = "MT_App_Push";

    /**
     * 聊天的状态。
     */
    public static final String MT_App_IMStatus = "MT_App_IMStatus";

    /**
     * 应用相关的消息类型。<br>
     * 进入前台、后台的消息。true前台；false后台。
     * Boolean isForeground = msg.getData();
     */
    public static final String MT_App_Foreground = "MT_App_Foreground";

    /**
     * 地图定位功能，经纬度。<br>
     * bundle.getDouble("lng");<br>
     * bundle.getDouble("lat");
     */
    public static final String MT_Located = "MT_Located";

    /**
     * 个人信息变更消息类型
     * 消息变更注册这个
     */
    public static final String MT_MyInfo_Change = "MT_MyInfo_Change";

    /**
     * 请求更新myInfo消息
     * 通知更新消息用这个
     */
    public static final String MT_Update_MyInfo = "MT_Update_MyInfo";

    /**
     * 通知刷新Ycoin
     * true是请求http，false是不请求本地处理
     */
    public static final String MT_Update_Ycoin = "MT_Update_Ycoin";

    /**
     * 通知刷新钻石
     */
    public static final String MT_Update_Diamond = "MT_Update_Diamond";

    /**
     * 未读数变更消息
     */
    public static final String MT_Unread_change = "MT_Unread_change";

    /**
     * 有cmd指令
     */
    public static final String MT_CMD_Notify = "MT_CMD_Notify";

    /**
     * 跳转Tab
     */
    public static final String MT_Jump_Tab = "MT_Jump_Tab";

    /**
     * 充值成功
     */
    public static final String MT_Pay_Success = "MT_Pay_Success";

    /**
     * 网络状态变化
     */
    public static final String MT_Network_Status_Change = "MT_Network_Status_Change";

    /**
     * App内悬浮窗通知
     */
    public static final String MT_APP_Suspension_Notice = "MT_APP_Suspension_Notice";

    /**
     * 悬浮窗内部互调通知
     * 非TipsBarMgr相关不监听
     */
    public static final String MT_Inner_Suspension_Notice = "MT_Inner_Suspension_Notice";

    /**
     * 消息列表更新
     */
    public static final String MT_User_List_Msg_Change = "MT_User_List_Msg_Change";

    /**
     * 清除聊天记录
     */
    public static final String MT_Chat_Clear_History = "MT_Chat_Clear_History";

    /**
     * 聊天页面加载更多 1，邀请消息加载更多
     */
    public static final String MT_Chat_Mail_More = "MT_Chat_Mail_More";

    /**
     * 能否聊天
     */
    public static final String MT_Chat_Can = "MT_Chat_Can";

    /**
     * 陌生的人消息
     */
    public static final String MT_Stranger_New = "MT_Stranger_New";

    /**
     * 数据库初始化完成
     */
    public static final String MT_DB_Init_Ok = "MT_DB_Init_Ok";

    /**
     * 添加自定义表情
     */
    public static final String MT_ADD_CUSTOM_SMILE = "MT_ADD_CUSTOM_SMILE";

    /**
     * 一键打招呼完成后回调用
     */
    public static final String MT_Say_Hello_Notice = "MT_Say_Hello_Notice";

    /**
     * 打招呼完成后回调用
     */
    public static final String MT_Say_HI_Notice = "MT_Say_HI_Notice";


    /**
     * 好友条数通知
     */
    public static final String MT_Friend_Num_Notice = "MT_Friend_Num_Notice";

    /**
     * 音视频群邀邀请时间倒计时[专用]
     */
    public static final String MT_Time_Change = "MT_MyTime_Change";

    /**
     * 网络变更
     */
    public static final String MT_Net_Change = "MT_Net_Change";

    /**
     * 提现成功通知
     */
    public static final String MT_GET_MONEY_Notice = "MT_GET_MONEY_Notice";

    /**
     * 解锁成功通知
     */
    public static final String MT_Private_Msg_Ulock = "MT_PRIVATE_MSG_ULOCK";

    /**
     * 钻石消费差额通知
     */
    public static final String MT_DIAMAND_CONSUME = "MT_DIAMAND_CONSUME";

    /**
     * 钻石变更通知
     */
    public static final String MT_DIAMAND_CHANGE = "MT_DIAMAND_CHANGE";

    /**
     * 完成任务跳转
     */
    public static final String MT_TASK_TO_JUMP = "MT_TASK_TO_JUMP";

    /**
     * 更新密友任务数据
     */
    public static final String MT_Update_TASK = "MT_Update_TASK";

    /**
     * 密友任务完成通知
     */
    public static final String MT_CHUM_TASK_FINISH = "MT_CHUM_TASK_FINISH";

    /**
     * 加密友完成通知
     */
    public static final String MT_ADD_CHUM_INFORM = "MT_ADD_CHUM_INFORM";

    /**
     * 密友解除通知
     */
    public static final String MT_REMOVE_CHUM_INFORM = "MT_REMOVE_CHUM_INFORM";

    /**
     * 忙线中...是否刷新语聊数据
     */
    public static final String MT_REFRESH_CHAT = "MT_REFRESH_CHAT";

    /**
     * 昵称修改通知
     */
    public static final String MT_REMARK_INFORM = "MT_REMARK_INFORM";

    /**
     * 拉黑状态通知
     */
    public static final String MT_BLACK_STATUS = "MT_BLACK_STATUS";

    /**
     * 进入后台后收到信息点击回复
     */
    public static final String MT_App_BackGround_chatInfo = "MT_APP_BACKGROUND_CHATINFO";

    /**
     *  音视频等待中
     */
    public static final String MT_App_BackGround_chatInfo_for_inviteIng = "MT_APP_BACKGROUND_CHATINFO_INVEING";

    /**
     * 群邀等待中
     */
    public static final String MT_App_BackGround_chatInfo_for_group_inviteIng = "MT_APP_BACKGROUND_CHATINFO_GROUP_INVEING";

    /**
     * 私聊页礼物图标上方 获得红包提示
     */
    public static final String MT_GET_BAG_TIP = "MT_GET_BAG_TIP";

    /**
     * 解锁弹框消息
     */
    public static final String MESSAGE_UNLOCK = "MESSAGE_UNLOCK";

    /**
     * 一键打招呼页关闭消息
     */
    public static final String MT_CLOSE_SAY_HELLO = "MT_CLOSE_SAY_HELLO";

    /**
     * 是否要找的人
     */
    public static final String MT_SURE_USER = "MT_SURE_USER";

    /**
     * 打卡红包通知UI
     */
    public static final String MT_PUNCH_REDENVELOPE = "MT_PUNCH_REDENVELOPE";

    /**
     * 打卡红包分享成功通知UI
     */
    public static final String MT_SHARE_SUCCEED = "MT_SHARE_SUCCEED";

    /**
     * 打卡红包分享失败通知UI
     */
    public static final String MT_SHARE_FAILURE = "MT_SHARE_FAILURE";
}