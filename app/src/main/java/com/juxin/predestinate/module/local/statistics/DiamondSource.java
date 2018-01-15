package com.juxin.predestinate.module.local.statistics;

import com.juxin.predestinate.bean.StackNode;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：
 * 创建时间：2017/9/15 20:38
 * 修改时间：2017/9/15 20:38
 * Created by Administrator on 2017/9/15
 * 修改备注：
 */
public class DiamondSource extends CommonSource<DiamondSource.DiamondSourceContainer> {

    final static List<String> sources = new ArrayList<String>(61);

    static {
        sources.add("faxian_yuliao_listitem_video");
        sources.add("faxian_yuliao_listitem_voice");
        sources.add("faxian_tuijian_userinfo_lookta");
        sources.add("faxian_tuijian_userinfo_sendvoice");
        sources.add("xiaoxi_listitem_video_huibo");
        sources.add("xiaoxi_listitem_video_jieting");
        sources.add("xiaoxi_listitem_voice_huibo");
        sources.add("xiaoxi_listitem_voice_jieting");
        sources.add("xiaoxi_chatframe_toolplus_video");
        sources.add("xiaoxi_chatframe_toolplus_voice");
        sources.add("xiaoxi_chatframe_content_video_huibo");
        sources.add("xiaoxi_chatframe_content_video_accept");
        sources.add("xiaoxi_chatframe_content_voice_huibo");
        sources.add("xiaoxi_chatframe_content_voice_accept");
        sources.add("xiaoxi_chatframe_lookta");
        sources.add("xiaoxi_chatframe_more_video");
        sources.add("xiaoxi_chatframe_more_voice");
        sources.add("xiaoxi_chatframe_userinfo_lookta");
        sources.add("xiaoxi_chatframe_userinfo_sendvoice");
        sources.add("xiaoxi_chatframe_userinfo_picture");
        sources.add("faxian_tuijian_userinfo_gift");
        sources.add("faxian_tuijian_userinfo_faxin_chatframe_gift");
        sources.add("faxian_tuijian_userinfo_faxin_chatframe_content_sendgift");
        sources.add("xiaoxi_chatframe_gift");
        sources.add("xiaoxi_chatframe_content_sendgift");
        sources.add("xiaoxi_chatframe_userinfo_gift");
        sources.add("tip_chatframe_gift");
        sources.add("tip_chatframe_content_sendgift");
        sources.add("me_mygem_pay");
        sources.add("faxian_yuliao_listitem_video_gift_gempay");
        sources.add("faxian_tuijian_userinfo_lookta_gift_gempay");
        sources.add("faxian_tuijian_userinfo_sendvoice_gift_gempay");
        sources.add("xiaoxi_listitem_video_huibo_gift_gempay");
        sources.add("xiaoxi_listitem_video_jieting_gift_gempay");
        sources.add("xiaoxi_listitem_voice_huibo_gift_gempay");
        sources.add("xiaoxi_listitem_voice_jieting_gift_gempay");
        sources.add("xiaoxi_chatframe_toolplus_video_gift_gempay");
        sources.add("xiaoxi_chatframe_toolplus_voice_gift_gempay");
        sources.add("xiaoxi_chatframe_content_video_huibo_gift_gempay");
        sources.add("xiaoxi_chatframe_content_video_accept_gift_gempay");
        sources.add("xiaoxi_chatframe_content_voice_huibo_gift_gempay");
        sources.add("xiaoxi_chatframe_content_voice_accept_gift_gempay");
        sources.add("xiaoxi_chatframe_lookta_gift_gempay");
        sources.add("xiaoxi_chatframe_more_video_gift_gempay");
        sources.add("xiaoxi_chatframe_more_voice_gift_gempay");
        sources.add("xiaoxi_chatframe_userinfo_lookta_gift_gempay");
        sources.add("xiaoxi_chatframe_userinfo_sendvoice_gift_gempay");
        sources.add("tip_video_accept_gift_gempay");
        sources.add("me_money_batchvideo_gift_gempay");
        sources.add("me_money_batchvoice_gift_gempay");
        sources.add("faxian_yuliao_other");
        sources.add("faxian_tuijian_other");
        sources.add("xiaoxi_other");
        sources.add("tip_other");

        //直播版新增
        sources.add("faxian_grabgoddess");
        //*_userinfo_game_grabgoddess	导航路径->用户资料页->小游戏->抓女神
        // 导航路径->用户资料页在App.class中已注册
        sources.add("_userinfo_game_grabgoddess");
        sources.add("zhibo_userinfo_gift_gempay");
        sources.add("zhibo_liveroom_gift_gempay");//直播->主播直播间->送礼物->钻石充值
        sources.add("zhibo_liveroom_zhubogift_gempay");//直播->主播直播间->送礼物->钻石充值
        sources.add("zhibo_liveroom_faceplusfirend_sendgift_gempay");//直播->主播直播间->左上角头像加好友->赠送礼物->充值钻石
        sources.add("zhibo_liveroom_danmu_gempay");//直播->主播直播间->弹幕->充值钻石
        sources.add("zhibo_liveroom_gameover_plusfirend_sendgift_gempay");//直播->主播直播间->直播结束->加好友->赠送礼物->充值钻石

        //分享2.0
        sources.add("gem_unlock_gempay");//钻石解锁充值
    }

    @Override
    public String getSource(DiamondSourceContainer container) {
        container = new DiamondSourceContainer(container);
        if (!contain(container)) return findOther(container);
        return container.getSource();
    }

    @Override
    public boolean containSource(DiamondSourceContainer container) {
        //跳转至userinfo前面的路径可变动，在APP.Class中注册的路径
        //解锁弹窗前面路劲可变动
        if (container.getSource().contains("_userinfo_game_grabgoddess")
                || container.getSource().contains("_unlockchat_gempay")){
            return true;
        }
        return sources.contains(container.getSource());
    }

    public static class DiamondSourceContainer extends CommonSource.CommonSourceContainer {
        private String source;

        public DiamondSourceContainer(String source, StackNode node) {
            this(new CommonSourceContainer(source, node));
        }

        /**
         * 由其他的统计来源得到统计信息
         * {@linkplain SourcePoint#lockSource(android.app.Activity, String)
         * {@linkplain SourcePoint#lockSource(String)
         * 保存的是统一的source,同一事件源不同的source需要处理}
         */
        public DiamondSourceContainer(CommonSourceContainer parentContainer) {
            super(parentContainer);
            source = parentContainer.getSource();
            // 视屏source 中的"faxian_tuijian_userinfo_chatframe"，
            // 对应的钻石source是 "faxian_tuijian_userinfo_faxin_chatframe"
            source = source.replace("faxian_tuijian_userinfo_chatframe", "faxian_tuijian_userinfo_faxin_chatframe");
            if (!sources.contains(source) && GiftSendSource.contain(source)) {   //直播中送礼物充值来源
                source += "_gempay";
            }
        }

        @Override
        public String getSource() {
            return source;
        }
    }
}
