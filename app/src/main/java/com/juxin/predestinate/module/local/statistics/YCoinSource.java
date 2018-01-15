package com.juxin.predestinate.module.local.statistics;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：
 * 创建时间：2017/9/15 20:30
 * 修改时间：2017/9/15 20:30
 * Created by Administrator on 2017/9/15
 * 修改备注：
 */

public class YCoinSource extends CommonSource<CommonSource.CommonSourceContainer> {
    final static List<String> sources = new ArrayList<String>(50);
    static{
        sources.add("faxian_tuijian_userinfo_sendchat");
        sources.add("xiaoxi_chatframe_unlockandcontact");
        sources.add("xiaoxi_chatframe_userinfo_sendchat");
        sources.add("me_myy_y_gopay");
        sources.add("me_myvip_y_gopay");
        sources.add("faxian_yuliao_other");
        sources.add("faxian_tuijian_other");
        sources.add("xiaoxi_other");
        sources.add("tip_other");

        sources.add("me_myy");   // 防止拦截
        sources.add("me_myvip");
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
}
