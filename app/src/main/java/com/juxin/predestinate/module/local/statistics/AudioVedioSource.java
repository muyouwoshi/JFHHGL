package com.juxin.predestinate.module.local.statistics;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：
 * 创建时间：2017/9/16 13:37
 * 修改时间：2017/9/16 13:37
 * Created by Administrator on 2017/9/16
 * 修改备注：
 */

public class AudioVedioSource extends CommonSource<CommonSource.CommonSourceContainer> {
    final static List<String> sources = new ArrayList<String>(50);
    static {
        sources.add("faxian_yuliao_listitem_video");
        sources.add("faxian_yuliao_listitem_voice");
        sources.add("faxian_tuijian_userinfo_lookta");
        sources.add("faxian_tuijian_userinfo_sendvoice");
        sources.add("faxian_tuijian_userinfo_chatframe_toolplus_video");
        sources.add("faxian_tuijian_userinfo_chatframe_toolplus_voice");
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
        sources.add("tip_video_accept");
        sources.add("faxian_yuliao_other");
        sources.add("faxian_tuijian_other");
        sources.add("xiaoxi_other");
        sources.add("tip_other");
        sources.add("haoyou_chatframe_videotask_go");
        //直播新增
        sources.add("zhibo_userinfo_lookta");
        sources.add("zhibo_userinfo_sendvoice");
        sources.add("zhibo_userinfo_chatframe_toolplus_video");
        sources.add("zhibo_userinfo_chatframe_toolplus_voice");

        sources.add("me_myy");   // 防止拦截
        sources.add("me_myvip");
        sources.add("me_mygem_pay");
    }

    @Override
    public String getSource(CommonSourceContainer container) {
        if(!contain(container)) return findOther(container);
        return container.getSource();
    }

    @Override
    public boolean containSource(CommonSourceContainer container) {
        return sources.contains(container.getSource());
    }

    public static boolean contain(String source){
        return sources.contains(source);
    }
}
