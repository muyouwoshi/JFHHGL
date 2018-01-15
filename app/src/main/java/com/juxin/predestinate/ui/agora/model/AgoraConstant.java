package com.juxin.predestinate.ui.agora.model;

/**
 * 音视频内部常量维护类
 */
public class AgoraConstant {
    // Agora声网
    public static final String AGORA_APPID = "42188e897f004754a21fec161bc0d0a9";

    // 权限申请
    public static final int BASE_VALUE_PERMISSION = 0X0001;
    public static final int PERMISSION_REQ_ID_RECORD_AUDIO = BASE_VALUE_PERMISSION + 1;
    public static final int PERMISSION_REQ_ID_CAMERA = BASE_VALUE_PERMISSION + 2;
    public static final int PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE = BASE_VALUE_PERMISSION + 3;
    public static final int OVERLAY_PERMISSION_REQUEST_CODE = BASE_VALUE_PERMISSION + 4;

    // ======================  服务器常量 ======================
    public static final String PIC_TYPE_VIDEO_CHAT = "videochat";      // 图片类型： 鉴黄
    public static final int RTC_MSG_VER = 20;    //  消息版本

    // 通信特殊错误码
    public static final int RTC_USER_CHATING = 3001;    //  用户正在视频聊天中
    public static final int RTC_USER_NOT_CHAT = 3002;   //  该用户无法视频聊天
    public static final int RTC_USER_NOT_PAY = 3003;    //  钻石余额不足
    public static final int RTC_VIDEOCHAT_NEED_VIP = 3012;    //  需要充值vip才能和高爵位女用户视频 在来源为 "faxian_yuliao_listitem_video", "tip_video_accept" 会判断女用户爵位

    // ======================  产品常量 ======================
    public static final long CHAT_TIME_COUNT_DOWN = 60000;       // 钻石不足开始倒计时：1分钟
    public static final long CHAT_COST_PROMPT = 60;              // 钻石不足开始提示时间(单位：s)
    public static final long CHAT_TIME_TIPS = 30;                // 聊天未接听提示时间(单位：s)
    public static final long CHAT_TIME_AUTO_HUANGUP = 60;        // 聊天未接听自动挂断时间(单位：s)
    public static final long CHAT_UPDATE_VIDEO_FIRST = 60;       // 鉴黄图片短轮询上传时间(单位：s)
    public static final long CHAT_UPDATE_VIDEO_PIC_FIRST = 10000;// 鉴黄图片上传时间间隔(单位：ms)
    public static final long CHAT_UPDATE_VIDEO_PIC = 30000;      // 鉴黄图片上传时间间隔(单位：ms)
    public static final long CHAT_UPDATE_PAY_INFO = 30000;       // 更新付费信息轮询间隔(单位：ms)
    public static final long CHAT_UPDATE_PAY_FAIL = 5000;        // 更新付费信息失败后，缩减付费轮询间隔(单位：ms)
    public static final long CHAT_UPDATE_PAY_FAIL_TIME = 5;      // 更新付费信息失败后，缩减付费轮询次数
    public static final long CHAT_TIME_AUTO_HUANGUP_GROUP = 120;  // 女性任务版: 群发未接听自动挂断时间(单位：s)
    public static final long CHAT_TIME_AUTO_HUANGUP_SINGLE = 60;  // 女性任务版: 单聊未接听自动挂断时间(单位：s)
    public static final long INVITE_CHAT_INTERVAL = 3 * 1000;     // 发起音视频时间间隔
    public static final long CHAT_COMPLAIN_INTERVAL = 60 * 1000;   // 男性投诉时间间隔

    //  ======================  计时Type区分 ======================
    public static final int TIMER_INVITE = 1;      // 邀请计时
    public static final int TIMER_CHATING = 2;     // 通信计时
    public static final int TIMER_LAST_TIPS = 3;   // 余额计时

    //  ======================  Type区分 ======================
    public static final int RTC_CHAT_INVITE = 1;   // 邀请
    public static final int RTC_CHAT_BEINVITE = 2; // 被邀请

    public static final int RTC_CHAT_VIDEO = 1;    // 视频通信
    public static final int RTC_CHAT_VOICE = 2;    // 音频通信

    public static final int RTC_CHAT_SINGLE = 1;   // 女性任务版： 单聊
    public static final int RTC_CHAT_GROUP = 2;    // 女性任务版： 群聊

    // ====================== 通信网络环境监测 ==================
    public static final long CHECK_INTERVAL = 10 * 1000; // 检测间隔
    public static final int QUALITY_BAD = 4;             // 环境较差  好（<=2）, 较差（3），差（>=4）


    public static final String USER_LIGHT_INFO_KEY = "other_info_key";   // 他人资料
}
