package com.juxin.predestinate.module.logic.config;

import com.juxin.predestinate.module.local.chat.msgtype.CommonMessage;

/**
 * Created by duanzheng on 2017/9/5.
 * http://test.game.xiaoyouapp.cn:20080/juxin-data/juxin-data-doc/src/master/flume/topic-dic.md#video_source
 */

public enum FlumeTopic {
    /**== 视频发起来源 =========================================================================================================================================================*/

    /**== 钻石充值来源 =========================================================================================================================================================*/
    FAXIAN_TUIJIAN_USERINFO_GIFT("faxian_tuijian_userinfo_gift"),// 发现->推荐->用户资料页->礼物->充值钻石
    FAXIAN_TUIJIAN_USERINFO_FAXIN_CHATFRAME_GIFT("faxian_tuijian_userinfo_faxin_chatframe_gift"),// 发现->推荐->用户资料页->发信->聊天框->礼物->充值钻石
    FAXIAN_TUIJIAN_USERINFO_FAXIN_CHATFRAME_CONTENT_SENDGIFT("faxian_tuijian_userinfo_faxin_chatframe_content_sendgift"),//发现->推荐->用户资料页->发信->聊天框->索要礼物消息->赠送礼物->充值钻石
    XIAOXI_CHATFRAME_GIFT("xiaoxi_chatframe_gift"),//消息->聊天框->礼物->充值钻石
    XIAOXI_CHATFRAME_CONTENT_SENDGIFT("xiaoxi_chatframe_content_sendgift"),//消息->聊天框->索要礼物消息->赠送礼物(暂不区分私密消息类型,有赠送礼物就转发数据)->充值钻石
    XIAOXI_CHATFRAME_USERINFO_GIFT("xiaoxi_chatframe_userinfo_gift"),//消息->聊天框->用户资料页->礼物->充值钻石
    TIP_CHATFRAME_GIFT("tip_chatframe_gift"),//通知消息->聊天框->礼物->充值钻石
    TIP_CHATFRAME_CONTENT_SENDGIFT("tip_chatframe_content_sendgift"),//通知消息->聊天框->索要礼物消息->赠送礼物(暂不区分私密消息类型,有赠送礼物就转发数据)->充值钻石
    ME_MYGEM_PAY("me_mygem_pay"),//我的->我的钻石->充值
    FAXIAN_YULIAO_LISTITEM_VIDEO_GIFT_GEMPAY("faxian_yuliao_listitem_video_gift_gempay"),//发现->语聊->用户列表项->视频->送礼物->充值钻石
    FAXIAN_TUIJIAN_USERINFO_LOOKTA_GIFT_GEMPAY("faxian_tuijian_userinfo_lookta_gift_gempay"),//发现->推荐->用户资料页->看看TA->送礼物->充值钻石
    FAXIAN_TUIJIAN_USERINFO_SENDVOICE_GIFT_GEMPAY("faxian_tuijian_userinfo_sendvoice_gift_gempay"),//发现->推荐->用户资料页->发语音->送礼物->充值钻石
    XIAOXI_LISTITEM_VIDEO_HUIBO_GIFT_GEMPAY("xiaoxi_listitem_video_huibo_gift_gempay"),//消息->用户列表->视频邀请->回拨->送礼物->充值钻石
    XIAOXI_LISTITEM_VIDEO_JIETING_GIFT_GEMPAY("xiaoxi_listitem_video_jieting_gift_gempay"),//消息->用户列表->视频邀请->接听->送礼物->充值钻石
    XIAOXI_LISTITEM_VOICE_HUIBO_GIFT_GEMPAY("xiaoxi_listitem_voice_huibo_gift_gempay"),//消息->用户列表->语音邀请->回拨->送礼物->充值钻石
    XIAOXI_LISTITEM_VOICE_JIETING_GIFT_GEMPAY("xiaoxi_listitem_voice_jieting_gift_gempay"),//消息->用户列表->语音邀请->接听->送礼物->充值钻石
    XIAOXI_CHATFRAME_TOOLPLUS_VIDEO_GIFT_GEMPAY("xiaoxi_chatframe_toolplus_video_gift_gempay"),//	消息->聊天框->加号按钮->邀请视频->送礼物->充值钻石
    XIAOXI_CHATFRAME_TOOLPLUS_VOICE_GIFT_GEMPAY("xiaoxi_chatframe_toolplus_voice_gift_gempay"),//	消息->聊天框->加号按钮->邀请语音->送礼物->充值钻石
    XIAOXI_CHATFRAME_CONTENT_VIDEO_HUIBO_GIFT_GEMPAY("xiaoxi_chatframe_content_video_huibo_gift_gempay"),// 消息->聊天框->消息内容->视频->立即回拨->送礼物->充值钻石
    XIAOXI_CHATFRAME_CONTENT_VIDEO_ACCEPT_GIFT_GEMPAY("xiaoxi_chatframe_content_video_accept_gift_gempay"),//	消息->聊天框->消息内容->视频->视频立即接通->送礼物->充值钻石
    XIAOXI_CHATFRAME_CONTENT_VOICE_HUIBO_GIFT_GEMPAY("xiaoxi_chatframe_content_voice_huibo_gift_gempay"),// 	消息->聊天框->消息内容->语音->立即回拨->送礼物->充值钻石
    XIAOXI_CHATFRAME_CONTENT_VOICE_ACCEPT_GIFT_GEMPAY("xiaoxi_chatframe_content_voice_accept_gift_gempay"),//	消息->聊天框->消息内容->语音->立即接通->送礼物->充值钻石
    XIAOXI_CHATFRAME_MORE_VIDEO_GIFT_GEMPAY("xiaoxi_chatframe_more_video_gift_gempay"),//	消息->聊天框->右上角更多->视频聊天->送礼物->充值钻石
    XIAOXI_CHATFRAME_MORE_VOICE_GIFT_GEMPAY("xiaoxi_chatframe_more_voice_gift_gempay"),//   消息->聊天框->右上角更多->语音聊天->送礼物->充值钻石
    XIAOXI_CHATFRAME_USERINFO_LOOKTA_GIFT_GEMPAY("xiaoxi_chatframe_userinfo_lookta_gift_gempay"),// 消息->聊天框->用户资料页->看看TA->送礼物->充值钻石
    XIAOXI_CHATFRAME_USERINFO_SENDVOICE_GIFT_GEMPAY("xiaoxi_chatframe_userinfo_sendvoice_gift_gempay"),//消息->聊天框->用户资料页->发语音->送礼物->充值钻石

    /**== VIP充值来源 ==========================================================================================================================================================*/
    ME_GOPAY("me_gopay"),// 我的->立即开通VIP
    ME_MYY_VIP_GOPAY("me_myy_vip_gopay"),//	我的->我的Y币->开通VIP->立即支付
    ME_MYVIP_VIP_GOPAY("me_myvip_vip_gopay"),//	我的->我的VIP->开通VIP->立即支付

    /**== 视频发起来源 + 钻石充值来源 ==========================================================================================================================================*/
    TIP_VIDEO_ACCEPT("tip_video_accept"),// 通知消息->视频->接受
    XIAOXI_CHATFRAME_LOOKTA("xiaoxi_chatframe_lookta"),//消息->聊天框->看看TA

    /**== 视频发起来源 + VIP充值来源 ============================================================================================================================================*/
    ME_SETTING_ENABLEVIDEO("me_setting_enablevideo"),//	我的->设置->开启视频

    /**== 钻石充值来源 + VIP充值来源 ============================================================================================================================================*/
    FAXIAN_YULIAO_USERINFO_PICTURE("faxian_yuliao_userinfo_picture"),//	发现->语聊->用户资料页->照片*
    FAXIAN_TUIJIAN_USERINFO_PICTURE("faxian_tuijian_userinfo_picture"),//	发现->推荐->用户资料页->照片*
    XIAOXI_CHATFRAME_USERINFO_PICTURE("xiaoxi_chatframe_userinfo_picture"),//	消息->聊天框->用户资料页->照片*
    ME_SETTING_ENABLEVOICE("me_setting_enablevoice"),//	我的->设置->开启语音
    XIAOXI_CHATFRAME_LOOKTA_GIFT_GEMPAY("xiaoxi_chatframe_lookta_gift_gempay"),//消息->聊天框->看看TA->送礼物->充值钻石

    /**== 视频发起来源  +  钻石充值来源  +  VIP充值来源 ========================================================================================================================*/
    FAXIAN_YULIAO_LISTITEM_VIDEO("faxian_yuliao_listitem_video"),//	发现->语聊->用户列表项->视频
    FAXIAN_TUIJIAN_USERINFO_LOOKTA("faxian_tuijian_userinfo_lookta"),//发现->推荐->用户资料页->看看TA
    FAXIAN_TUIJIAN_USERINFO_SENDVOICE("faxian_tuijian_userinfo_sendvoice"),//发现->推荐->用户资料页->发语音
    XIAOXI_LISTITEM_VIDEO_HUIBO("xiaoxi_listitem_video_huibo"),//消息->用户列表->视频邀请->回拨
    XIAOXI_LISTITEM_VIDEO_JIETING("xiaoxi_listitem_video_jieting"),//消息->用户列表->视频邀请->接听
    XIAOXI_LISTITEM_VOICE_HUIBO("xiaoxi_listitem_voice_huibo"),//消息->用户列表->语音邀请->回拨
    XIAOXI_LISTITEM_VOICE_JIETING("xiaoxi_listitem_voice_jieting"),//消息->用户列表->语音邀请->接听
    XIAOXI_CHATFRAME_TOOLPLUS_VIDEO("xiaoxi_chatframe_toolplus_video"),//消息->聊天框->加号按钮->邀请视频
    XIAOXI_CHATFRAME_TOOLPLUS_VOICE("xiaoxi_chatframe_toolplus_voice"),//消息->聊天框->加号按钮->邀请语音
    XIAOXI_CHATFRAME_CONTENT_VIDEO_HUIBO("xiaoxi_chatframe_content_video_huibo"),//消息->聊天框->消息内容->视频->立即回拨
    XIAOXI_CHATFRAME_CONTENT_VIDEO_ACCEPT("xiaoxi_chatframe_content_video_accept"),//消息->聊天框->消息内容->视频->视频立即接通
    XIAOXI_CHATFRAME_CONTENT_VOICE_HUIBO("xiaoxi_chatframe_content_voice_huibo"),//	消息->聊天框->消息内容->语音->立即回拨
    XIAOXI_CHATFRAME_CONTENT_VOICE_ACCEPT("xiaoxi_chatframe_content_voice_accept"),//消息->聊天框->消息内容->语音->立即接通
    XIAOXI_CHATFRAME_MORE_VIDEO("xiaoxi_chatframe_more_video"),//消息->聊天框->右上角更多->视频聊天
    XIAOXI_CHATFRAME_MORE_VOICE("xiaoxi_chatframe_more_voice"),//消息->聊天框->右上角更多->语音聊天
    XIAOXI_CHATFRAME_USERINFO_LOOKTA("xiaoxi_chatframe_userinfo_lookta"),//	消息->聊天框->用户资料页->看看TA
    XIAOXI_CHATFRAME_USERINFO_SENDVOICE("xiaoxi_chatframe_userinfo_sendvoice"),//消息->聊天框->用户资料页->发语音
    FAXIAN_YULIAO_OTHER("faxian_yuliao_other"),// 发现->语聊->其他未采集到的逻辑
    FAXIAN_TUIJIAN_OTHER("faxian_tuijian_other"),//	发现->推荐->其他未采集到的逻辑
    XIAOXI_OTHER("xiaoxi_other"),//	消息->其他未采集到的逻辑
    TIP_OTHER("tip_other");//通知消息->其他未采集到的逻辑

    public String name;

    private FlumeTopic(String name) {
        this.name = name;
    }

    public static boolean contains(String source) {
        for(FlumeTopic ft:FlumeTopic.values()){
            if(ft.name.equals(source)) return true;
        }
        return false;
    }
}
