package com.juxin.predestinate.module.local.statistics;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：
 * 创建时间：2017/9/15 20:29
 * 修改时间：2017/9/15 20:29
 * Created by Administrator on 2017/9/15
 * 修改备注：
 */

public class VipSource extends CommonSource<CommonSource.CommonSourceContainer> {
    final static List<String> sources = new ArrayList<String>(50);

    static {
        sources.add("faxian_yuliao_listitem_video");
        sources.add("faxian_yuliao_listitem_voice");
        sources.add("faxian_tuijian_userinfo_lookta");
        sources.add("faxian_tuijian_userinfo_sendvoice");
        sources.add("faxian_tuijian_userinfo_picture");
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
        sources.add("tip_video_accept");
        sources.add("me_setting_enablevideo");
        sources.add("me_setting_enablevoice");
        sources.add("me_gopay");
        sources.add("me_myy_vip_gopay");
        sources.add("me_mylock_vip_gopay");
        sources.add("me_myvip_vip_gopay");
        sources.add("faxian_yuliao_other");
        sources.add("faxian_tuijian_other");
        sources.add("xiaoxi_other");
        sources.add("tip_other");

        sources.add("me_myy");   // 防止拦截
        sources.add("me_myvip");

        //缘分吧优化
        sources.add("menu_me_vipmark"); //我的->vip(爵位-守护-座驾这一行)->充值
    }

    @Override
    public String getSource(CommonSourceContainer container) {
        if (!contain(container)) return findOther(container);
        return container.getSource();
    }

    @Override
    public boolean containSource(CommonSourceContainer container) {
        return sources.contains(container.getSource());
    }
}
