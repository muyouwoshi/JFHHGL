package com.juxin.predestinate.module.local.statistics;

import com.juxin.predestinate.bean.StackNode;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：
 * 创建时间：2017/9/16 13:41
 * 修改时间：2017/9/16 13:41
 * Created by Administrator on 2017/9/16
 * 修改备注：
 */

public class GiftSendSource extends CommonSource<GiftSendSource.GiftSendSourceContainer> {
    final static List<String> sources = new ArrayList<String>(50);

    static {
        sources.add("faxian_tuijian_userinfo_gift");
        sources.add("faxian_tuijian_userinfo_faxin_chatframe_gift");
        sources.add("faxian_tuijian_userinfo_faxin_chatframe_content_sendgift");
        sources.add("xiaoxi_chatframe_gift");
        sources.add("xiaoxi_chatframe_content_sendgift");
        sources.add("xiaoxi_chatframe_userinfo_gift");
        sources.add("tip_chatframe_gift");
        sources.add("tip_chatframe_content_sendgift");
        sources.add("faxian_yuliao_listitem_video_gift");
        sources.add("faxian_tuijian_userinfo_lookta_gift");
        sources.add("xiaoxi_listitem_video_huibo_gift");
        sources.add("xiaoxi_listitem_video_jieting_gift");
        sources.add("xiaoxi_chatframe_toolplus_video_gift");
        sources.add("xiaoxi_chatframe_toolplus_voice_gift");
        sources.add("xiaoxi_chatframe_content_video_huibo_gift");
        sources.add("xiaoxi_chatframe_content_video_accept_gift");
        sources.add("xiaoxi_chatframe_content_ljsl");
        sources.add("xiaoxi_chatframe_lookta_gift");
        sources.add("xiaoxi_chatframe_more_video_gift");
        sources.add("xiaoxi_chatframe_userinfo_lookta_gift");
        sources.add("xiaoxi_chatframe_userinfo_sendvoice_gift");
        sources.add("tip_video_accept_gift");
        sources.add("me_money_batchvideo_gift");
        sources.add("me_money_batchvoice_gift");
        sources.add("faxian_yuliao_other");
        sources.add("faxian_tuijian_other");
        sources.add("xiaoxi_other");
        sources.add("tip_other");

        sources.add("haoyou_chatframe_gifttask_go");
        //直播版新增
        sources.add("zhibo_userinfo_gift");
        sources.add("zhibo_liveroom_gift");//直播->主播直播间->送礼物
        sources.add("zhibo_liveroom_zhubogift");//直播->主播直播间->主播送礼物
        sources.add("zhibo_liveroom_faceplusfirend_sendgift");//直播->主播直播间->左上角头像加好友->赠送礼物
        sources.add("zhibo_liveroom_gameover_plusfirend_sendgift");//直播->主播直播间->直播结束->加好友->赠送礼物

    }

    @Override
    public String getSource(GiftSendSourceContainer container) {
        if (!contain(container)) return findOther(container);
        return container.getSource();
    }

    @Override
    public boolean containSource(GiftSendSourceContainer container) {
        //解锁弹窗前面路径可变动
        if(container.getSource().contains("_unlockchat")){
            return true;
        }
        return sources.contains(container.getSource());
    }

    public static boolean contain(String source) {
        return sources.contains(source);
    }

    public static class GiftSendSourceContainer extends CommonSource.CommonSourceContainer {
        private String source;

        public GiftSendSourceContainer(String source, StackNode node) {
            this(new CommonSourceContainer(source, node));
        }

        public GiftSendSourceContainer(CommonSourceContainer parentContainer) {
            super(parentContainer);
            source = parentContainer.getSource();
            // 视屏source 中的"faxian_tuijian_userinfo_chatframe"，
            // 对应的钻石source是 "faxian_tuijian_userinfo_faxin_chatframe"
            source = source.replace("faxian_tuijian_userinfo_chatframe", "faxian_tuijian_userinfo_faxin_chatframe");
            if (!sources.contains(source) && AudioVedioSource.contain(source)) {   //视屏中送礼物来源
                source += "_gift";
            }
        }

        @Override
        public String getSource() {
            return source;
        }
    }
}
