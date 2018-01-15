package com.juxin.predestinate.module.logic.config;

import com.juxin.predestinate.module.util.CommonUtil;

/**
 * 应用常量
 * Created by ZRP on 2016/9/19.
 */
public class Constant {

    // -------------------------版本信息常量---------------------------

    // [以下3个参数对应文档](http://test.game.xiaoyouapp.cn:20080/juxin/api_doc/src/master/version/versions.md)
    public static final int MS_TYPE = 56;           //消息版本号
    public static final int SUB_VERSION = CommonUtil.isPurePackage() ? 1056 : 56;//客户端版本号 纯净版客户端标记号+1000
    public static final int SHOW_MAX_VERSION = 39;  //客户端版显示最大版本号 用于提示软件升级
    public static final int PLATFORM_TYPE = 2;      //平台号：2为android，3为iphone
    public static final int REG_FLAG = 0;           //0缘分吧，1爱爱，2同城快约，3附近秘约，标记

    // [参数对应文档](http://test.game.xiaoyouapp.cn:20080/juxin-data/juxin-data-doc/src/master/flume#Startup)
    public static final int PACKAGE_TYPE = 1;       //[opt]包类型 1为轻量包 2为全量包

    // 友盟
    public static final String UMENG_APPKEY = "549fccadfd98c51fae00019a";

    //微信
    public static String WEIXIN_APP_ID = "wxab302910f007a8ed";//聚鑫 //"wxc56bbddc3c2e0c18";//APPID
    public final static String WEIXIN_App_Key = "xCNyzKceB8szwWmUqT4laGqK5SapQn5L";

    public static final int APPEAR_TYPE_NO = 0;//默认没有选择
    public static final int APPEAR_TYPE_OWN = 1;//自己露脸
    public static final int APPEAR_TYPE_NO_OWN = 2;//自己不露脸

    public static final String APP_IS_FOREGROUND = "app_is_foreground";//是否属于前台显示

    // -----------------------私有K-V-----------------------
    public static final String PRIVATE_CHAT_TOP_H = "private_chat_top_h"; //私聊页顶部三个高度
    public static final String APPEAR_FOREVER_TYPE = "appear_forever_type"; //默认出场方式
    public static final String FLIP_ALBUM_UID = "flip_album_uid";//被查看的相册用户id
    public static final String CLOSE_Y_TIPS_VALUE = "close_y_tips_value";//是否关闭了Y币提示
    public static final String CLOSE_Y_TMP_TIPS_VALUE = "close_y_tmp_tips_value";//是否临时关闭了Y币提示
    public static final String ADD_CLOSE_FRIEND_VALUE = "add_close_friend_value";//是否已发送一次征服她
    public static final String GET_RED_BAG_TIP = "get_red_bag_tip";              //获得红包提示
    public static final String GET_RECHARGE_RED_BAG = "get_recharge_red_bag";    //获得充值红包
    public static final String SHARE_MAKE_MONEY = "share_make_money";            //分享立即赚钱
    public static final String SHARE_REWARD = "share_reward";                    //分享奖励
    public static final String SHARE_CODE_MODE = "share_code_mode";              //分享二维码模板
    public static final String SHARE_SHOW_MAKE_MONEY = "SHARE_SHOW_MAKE_MONEY";  //是否显示分享赚钱(要找的人弹窗SureUserDlg之后)
    public static final String TEMP_CHAT_OTHER_INFO = "TEMP_CHAT_OTHER_INFO";      //聊天对方名称-头像-性别
    public static final String TEMP_SHARE_TYPE = "TEMP_SHARE_TYPE";                //分享类型

    // -------------------------K-V---------------------------
    public static final String IS_SHOW_MESSAGE = "is_show_message";             // 是否显示过通知栏
    public static final String SETTING_QUIT_MESSAGE = "setting_quit_message";   //是否进行锁屏弹窗，存储key及默认值
    public static final boolean SETTING_QUIT_MESSAGE_DEFAULT = true;
    public static final String SETTING_MESSAGE = "setting_message";             //是否进行消息提示，存储key及默认值
    public static final boolean SETTING_MESSAGE_DEFAULT = true;
    public static final String SETTING_VIBRATION = "setting_vibration";         //是否进行新消息震动提示，存储key及默认值
    public static final boolean SETTING_VIBRATION_DEFAULT = true;
    public static final String SETTING_VOICE = "setting_voice";                 //是否进行新消息声音提示，存储key及默认值
    public static final boolean SETTING_VOICE_DEFAULT = true;
    public static final String SETTING_SLEEP_MESSAGE = "setting_sleep_message"; //是否睡眠免打扰，存储key及默认值
    public static final boolean SETTING_SLEEP_MESSAGE_DEFAULT = true;
    /**
     * 解锁聊天框
     */
    public static final String MESSAGE_SHARE_NUM = "message_share_num";         //用户分享的次数
    public static final String MESSAGE_JUEWEI_OK = "message_juewei_ok";         //用户爵位是否达到
    public static final String MESSAGE_DIAMOND_SHOW = "message_diamond_show";   //用户爵位是否达到

    public static final String MESSAGE_ISSHOWREDCARD = "isShowRedCard";   //是否显示红包卡


    public static final long CHAT_RESEND_TIME = 5 * 60 * 1000;              //5分钟内重发消息
    public static final long CHAT_SHOW_TIP_TIME_Interval = 20 * 60 * 1000;  //Chat相关
    public static final int CHAT_TEXT_LIMIT = 3478;//Chat相关
    public static final long TWO_HOUR_TIME = 2 * 60 * 60 * 1000;              //2小时
    public static final long HOUR_TIME = 60 * 60 * 1000;              //1小时
    public static final long FOUR_HOUR_TIME = 4 * 60 * 60 * 1000;             //4小时
    public static final long CHAT_FRIEND_LIST_REFRESH = 60000;        // 好友列表页自动刷新时间限制(单位：s)
    public static final long DLG_TIME = 15000;                        // 送礼弹框相关
    public static final long SHARE_GAP_TIME = 5000;                   // 分享间隔

    // ------ 文件长存储/短存储 start --------
    public static final String STR_SHORT_TAG = "oss";    // 短存储图片截取标志
    public static final String STR_LONG_TAG = "jxfile";  // 长存储图片截取标志

    public static final String SINCE_GROWTHID = "since_growthid"; //消息自增长ID
    public static final long SINCE_GROWTHID_DEFAULT = -1;         // 默认值

    // 老缘分吧上传文件类型
    public static final String UPLOAD_TYPE_FACE = "face";           // 自定义表情
    public static final String UPLOAD_TYPE_VOICE = "voice";         // 语音
    public static final String UPLOAD_TYPE_PHOTO = "photo";         // 图片
    public static final String UPLOAD_TYPE_VIDEO_CHAT = "videochat";// 头像认证
    public static final String UPLOAD_TYPE_VIDEO = "video";         // 视频

    // 支付类型
    public static final int GOOD_DIAMOND = 1;    //钻石
    public static final int GOOD_VIP = 2;        //VIP服务
    public static final int REQ_PAYLISTACT = 0x15;       //支付请求码
    public static final int BACK_PAYLISTACT = 0x16;// 支付返回
    public static final int PAY_WEIXIN = 0x17;// 微信支付
    public static final int PAYMENTACT = 0x18;
    public static final int PAY_VOICE_OK = 0x19; // 银联语音支付
    public static final int PAY_VOICE_DETAIL = 0x20; // 银联语音支付
    public static final int PAY_VOICEACT = 0x21;// 银联语音支付
    public static final int PAYMENTACT_TO = 0x22;// 跳转支付宝网页
    public static final int PAY_ALIPAY_WEB_ACT = 0x23;// 支付宝网页支付

    //============= 打招呼类型 start =============
    /**
     * 0为普通
     */
    public static final int SAY_HELLO_TYPE_SIMPLE = 0;
    /**
     * 1为向机器人一键打招呼
     */
    public static final int SAY_HELLO_TYPE_ROBOT = 1;
    /**
     * 3附近的人群打招呼
     */
    public static final int SAY_HELLO_TYPE_NEAR = 3;
    /**
     * 4为向机器人单点打招呼(包括首页和详细资料页等)
     */
    public static final int SAY_HELLO_TYPE_ONLY = 4;
    //============= 打招呼类型 end =============

    //=================== 打开送礼物来源 =============
    public static final int OPEN_FROM_HOT = 1;         //热门打开送礼物
    public static final int OPEN_FROM_CHAT_FRAME = 2;  //私聊页打开送礼物
    public static final int OPEN_FROM_INFO = 3;        //个人资料页打开送礼物
    public static final int OPEN_FROM_CHAT_PLUGIN = 4; //音视频时钻石不足弹窗
    public static final int OPEN_FROM_LIVE = 5;        //直播间礼物

    //================== 打开认证提示弹窗来源 =============

    public static final int OPEN_FROM_HOME = 5;       //首页打开资料认证弹框
    public static final int OPEN_FROM_CMD = 6;        //CMD打开资料认证弹框

    /**
     * 退出登录resultcode
     */
    public static final int EXITLOGIN_RESULTCODE = 200;

}
