package com.juxin.predestinate.module.local.statistics;

import com.juxin.predestinate.bean.StackNode;
import com.juxin.predestinate.ui.discover.ChatFragment;
import com.juxin.predestinate.ui.discover.DiscoverFragment;
import com.juxin.predestinate.ui.live.ui.LiveListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：音视频，支付来源共有的变量
 * 创建时间：2017/9/22 13:29
 * 修改时间：2017/10/13 13:29
 * Created by zhoujie on 2017/9/22
 * 修改备注：添加容器类父类{@link CommonSourceContainer}
 */

public abstract class CommonSource<T extends CommonSource.CommonSourceContainer> implements Source<T> {
    private static List<String> sourceList = new ArrayList<String>();
    static {
        sourceList.add("live_other");
        sourceList.add("faxian_yuliao_other");
        sourceList.add("faxian_tuijian_other");
        sourceList.add("xiaoxi_other");
        sourceList.add("tip_other");
        sourceList.add("zhibo_liveroom");
    }

    @Override
    public boolean contain(T container){
        if(containSource(container)) return true;
        return sourceList.contains(container.getSource());
    }

    /**  子类如添加自己的source字段需要在此方法中返回传入source是否存在
     *   @param container source容器
     *   @return 包含该字段返回true 不包含返回false
     */
    public abstract boolean containSource(T container);

    @Override
    public String findOther(T container) {
        if (container.node.withNode(Source.TIP.class)) return "tip_other";
        else if (container.node.withNode(ChatFragment.class)) return "faxian_yuliao_other";
        else if (container.node.withNode(DiscoverFragment.class)) return "faxian_tuijian_other";
        else if (container.node.withNode(LiveListFragment.class)) return "live_other";
        else return "xiaoxi_other";
    }

    /**
     * 类描述：用于储存source统计来源和其他信息的容器<p/>
     * 子类可添加其他信息
     */
    public static class CommonSourceContainer implements Source.SourceContainer {
        final String source;    //source 统计信息
        final StackNode node;   //Activity 及 Fragment 的栈信息

        public CommonSourceContainer(String source, StackNode node) {
            this.source = source;
            this.node = node;
        }

        /** 由其他的统计来源得到统计信息*/
        public CommonSourceContainer(CommonSourceContainer container) {
            this.source = container.source;
            this.node = container.node;
        }

        @Override
        public String getSource() {
            return source;
        }
    }
}
