package com.juxin.predestinate.module.local.statistics;

/**
 * 推广版本预埋点
 * Created by IQQ
 */
public class StatisticsSpread {
    /**
     * 个人资料页->右上角分享按钮(普通点击)
     */
    public static void onPage_info_share() {
        Statistics.userBehavior(SendPoint.page_info_share);
    }

    /**
     * 推广赚钱页面->分享->保存到手机(普通点击)(安卓)
     */
    public static void onPage_share_share_save() {
        Statistics.userBehavior(SendPoint.page_share_share_save);
    }

    /**
     * 推广赚钱页面->分享->关闭(普通点击)(安卓)
     */
    public static void onPage_share_share_close() {
        Statistics.userBehavior(SendPoint.page_share_share_close);
    }

    /**
     * 我的->推广赚钱
     */
    public static void onMenu_me_share(){
        Statistics.userBehavior(SendPoint.menu_me_share);
    }
}
