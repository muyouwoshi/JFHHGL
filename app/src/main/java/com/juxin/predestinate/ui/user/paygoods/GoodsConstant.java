package com.juxin.predestinate.ui.user.paygoods;

/**
 * 商品相关常亮
 * Created by Su on 2017/4/12.
 */
public class GoodsConstant {

    // ----- 支付渠道 ------
    public static final int PAY_TYPE_WECHAT = 6;  // 微信支付
    public static final int PAY_TYPE_ALIPAY = 1;  // 支付宝支付

    // ----- 支付类型 在线配置接口定义，不可随便更改------
    public static final String PAY_TYPE_WECHAT_NAME = "wechat";  // 微信支付
    public static final String PAY_TYPE_ALIPAY_NAME = "alipay";  // 支付宝支付

    public static final int PAY_TYPE_OLD = 0;     // 布局类型：老布局样式
    public static final int PAY_TYPE_NEW = 1;     // 布局类型：新布局样式

    // ----- 选择状态 ------
    public static final int PAY_STATUS_UNCHOOSE = 0; // 未选中支付
    public static final int PAY_STATUS_CHOOSE = 1;   // 选中支付

    // 钻石充值弹框
    public static final String DLG_TYPE_KEY = "typeKey";    // 弹框样式
    public static final String DLG_GIFT_NEED = "needKey";   // 钻石不足数目
    public static final int DLG_DIAMOND_NORMAL = 0;         // 默认弹框样式
    public static final int DLG_DIAMOND_GIFT_SHORT = 1;     // 送礼钻石不足弹框
    public static final int DLG_DIAMOND_VIDEO = 51;         // 视频版钻石不足弹框
    public static final int DLG_DIAMOND_YULIAO = 52;         // 语聊钻石不足弹框
    public static final String DLG_OPEN_FROM = "frome_tag"; //打开弹框的来源（统计用 可选）
    public static final String DLG_OPEN_TOUID = "frome_touid"; //是否因为某个用户充值 （统计用 可选）
    public static final String DLG_OPEN_CHANNEL_UID = "channel_uid"; //是否因为某个用户充值渠道uid （统计用 可选）

    // 充值vip弹框类型
    public static final int DLG_VIP_VIDEO_NEW = 50;    // 视频优化vip
    public static final int DLG_VIP_AV_CHAT = 1;       // 发起音视频
    public static final int DLG_VIP_SEARCH_B = 2;       // B环境搜索
    public static final int DLG_VIP_CHOOSE_B = 3;       // B环境vip筛选
    public static final int DLG_VIP_CHECK_ALBUM = 4;    // 查看用户相册
    public static final int DLG_VIP_INDEX_YULIAO = 5;    // 首页语聊充值类型
    public static final int DLG_VIP_INDEX_YULIAO_AVATER = 6;    // 首页语聊充值

    // Y币
    public static final int DLG_YCOIN_NEW = 6;                // 新购买Y币弹框
    public static final int DLG_YCOIN_PRIVEDEG = 7;           // 购买Y币

    // 送礼钻石弹框
    public static final int DLG_DIAMOND_GIFT = 8;              // 充值钻石
    public static final int DLG_DIAMOND_CHAT = 9;              // 聊天充值钻石
}
