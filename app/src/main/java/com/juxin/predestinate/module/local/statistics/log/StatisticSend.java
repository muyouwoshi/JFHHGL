package com.juxin.predestinate.module.local.statistics.log;

/**
 * 统计发送策略
 * Created by ZRP on 2017/8/3.
 */
public enum StatisticSend {

    NORMAL,         // 按照正常的策略进行发送，缓存10条之后进行发送
    IMMEDIATELY,    // 立即进行发送，如果发送失败，以单独的key对此项内容进行缓存
    CACHE,          // 以NORMAL的缓存key对该统计项进行缓存，在下次NORMAL发送的时候夹带进行发送
    ;
}
