package com.juxin.predestinate.module.local.statistics;


/**
 * 支付统计
 */


//切水果游戏冲vip: -100;
//红包灵气值:冲vip: -101;
//红包体力冲vip: -102;
//斗牛犬冲vip: -103;
//直播充值: -104;
//一键打招呼：-105;
//插队：-106;
//主页钥匙: -107;
//CMD钥匙: -108；  //目前只有这一个有用
public class StatisticsPay {
    private static String CCMDKEY = "-108";
    public static void TJ_CMDKey(){
        Statistics.payStatistic(CCMDKEY,CCMDKEY);
    }
}
