package com.juxin.predestinate.module.local.statistics.log;

/**
 * LogType.DEBUG分析日志上传时，应用内模块分组
 * Created by ZRP on 2017/7/31.
 */
public enum LogModule {

    CRASH,  // 单独对应崩溃日志收集

    // ---------------------------

    INFO,   // 客户端用户数据，与服务端进行比对
    CHAT,   // 聊天
    VIDEO,  // 视频
    LIVE,   // 直播
}
