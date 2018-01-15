package com.juxin.predestinate.ui.user.util;

/**
 * 用户中心常量维护类
 * Created by Su on 2017/5/2.
 */

public class CenterConstant {

    public static final String USER_CHECK_INFO_KEY = "check_info";        // 查看资料区分TAG
    public static final String USER_CHECK_OTHER_KEY = "check_other_info"; // 查看TA人资料
    public static final String USER_CHECK_VIDEO_KEY = "check_other_video";// 查看TA人视频
    public static final String USER_CHECK_VIDEO_TITLE_KEY = "check_other_video_title";//查看title

    public static final String USER_CHECK_UNLOCK_VIDEO_LIST_KEY = "check_other_video_id_list";// 解锁视频id列表

    public static final int USER_CHECK_INFO_OWN = 0x11;      // 查看自己资料
    public static final int USER_CHECK_INFO_OTHER = 0x12;    // 查看TA人资料
    public static final int USER_CHECK_CONNECT_OTHER = 0x13; // 查看TA人资料 联系方式
    public static final int USER_CHECK_PEERAGE_OTHER = 0x14; // 查看TA人资料 贵族

    // 资料设置页跳转来源
    public static final String USER_SET_KEY = "user_set_key";   // 资料设置
    public static final int USER_SET_FROM_CHAT = 0x13;          // 聊天页跳转
    public static final int USER_SET_FROM_CHECK = 0x14;         // 个人主页跳转
    public static final int USER_SET_REQUEST_CODE = 0x15;       // 请求码
    public static final int USER_SET_RESULT_CODE = 0x16;        // 返回码

    // 头像审核状态码
    public static final int USER_AVATAR_UNCHECKING = 0;  // 未审核
    public static final int USER_AVATAR_PASS = 1;        // 通过
    public static final int USER_AVATAR_NO_PASS = 2;     // 拒绝
    public static final int USER_AVATAR_NO_UPLOAD = 3;   // 未上传
    public static final int USER_AVATAR_NICE = 4;        // 好
    public static final int USER_AVATAR_VERY_NICE = 5;   // 很好

    //爵位等级 1-5是男平民; 51-55女平民
    public static final int TITLE_LEVEL_LOW = 5;

    //勋爵1星
    public static final int TITLE_LEVEL_HEIGHT = 11;

    //用户分享
    public static final String USER_SHARE_UID = "pop_view_shareinfo"; //解析用户分享缓存用户uid

    //解析用户分享缓存 jump_live?room_id=%d
    public static final String USER_SHARE_LIVE="jump_live";

    //礼物红包版
    /**
     * 礼物红包版 视频聊天红包
     */
    public static final int TYPE_VIDEO = 2;
    /**
     * 礼物红包版 礼物红包
     */
    public static final int TYPE_GIFT = 3;
    /**
     * 礼物红包版 充值红包
     */
    public static final int TYPE_RECHARGE = 4;
    /**
     * 礼物红包版 分享红包
     */
    public static final int TYPE_SHARE = 5;

    //打卡红包
    /**
     * 打卡红包初始页
     */
    public static final int TYPE_INIT = 1;
    /**
     * 打卡红包分享成功页(男)
     */
    public static final int TYPE_SUCCEED = 2;
    /**
     * 打卡红包分享失败页
     */
    public static final int TYPE_FAILURE = 3;
    /**
     * 打卡红包分享成功页(女)
     */
    public static final int TYPE_SUCCEED_WOMAN = 4;


    //榜单类型
    /**
     * 榜单多标签 key
     */
    public static final String RANK_TYPE_TITLES = "RANK_TYPE_TITLES";


    //分享类型
    /**
     * 1.直播间分享/主页分享/解锁分享
     */
    public static final int SHARE_TYPE_FIRST = 1;
    /**
     * 2.提现分享
     */
    public static final int SHARE_TYPE_SECOND = 2;
    /**
     * 3.主动推广分享
     */
    public static final int SHARE_TYPE_THREE = 3;

    //分享链接方式
    /**
     * 1分享用户
     */
    public static final int SHARE_LINK_FIRST = 1;
    /**
     * 2分享直播
     */
    public static final int SHARE_LINK_SECOND = 2;

    //格式化分享的模板内容 参数
    /**
     * 主页分享
     * usermain
     */
    public static final String SHARE_TYPE_USERMAIN = "usermain";
    /**
     * 解锁分享
     * unlock
     */
    public static final String SHARE_TYPE_UNLOCK = "unlock";
    /**
     * 主动推广分享
     * active
     */
    public static final String SHARE_TYPE_ACTIVE = "active";
    /**
     * 赚钱分享
     * earn
     */
    public static final String SHARE_TYPE_EARN = "earn";
    /**
     * 直播分享
     * live
     */
    public static final String SHARE_TYPE_LIVE = "live";
    /**
     * 打卡分享
     * daka
     */
    public static final String SHARE_TYPE_DAKA = "daka";
    /**
     * 女性分享赚钱
     * woman_share
     */
    public static final String SHARE_TYPE_WOMAN_SHARE = "woman_share";

}
