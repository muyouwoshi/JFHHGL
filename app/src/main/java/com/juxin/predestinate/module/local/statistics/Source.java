package com.juxin.predestinate.module.local.statistics;

/**
 * 类描述：
 * 创建时间：2017/9/15 20:25
 * 修改时间：2017/9/15 20:25
 * Created by Administrator on 2017/9/15
 * 修改备注：
 */

public interface Source<T extends Source.SourceContainer> {

    String getSource(T container);

    String findOther(T container);

    boolean contain(T container);

    interface SourceContainer {
        String getSource();
    }
    interface TIP{}
}
