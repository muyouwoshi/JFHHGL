package com.juxin.predestinate.module.local.statistics;

import java.util.HashMap;
import java.util.Map;

/**
 * 聊天框解锁与推广分享统计
 * Created by IQQ on 2017/11/03.
 */
public class StatisticsShareUnlock {
    /**
     * 解锁聊天弹窗->点击微信按钮或其他按钮(分享解锁)(会跳到二维码分享名片页面向下看)
     */
    public static void onAlert_unlockchat_share() {
        Statistics.userBehavior(SendPoint.alert_unlockchat_share);
    }

    /**
     * 解锁成功->好的
     */
    public static void onAlert_unlockchatsuccess_ok() {
        Statistics.userBehavior(SendPoint.alert_unlockchatsuccess_ok);
    }

    /**
     * * 解锁聊天弹窗->付费发送(钻石解锁)
     *`
     * @param price 扣钻石数
     */
    public static void onAlert_unlockchat_paysend(int price) {
        Map<String, Object> parms = new HashMap<>();
        parms.put("price", price);
        Statistics.userBehavior(SendPoint.alert_unlockchat_paysend, parms);
    }


    /**
     * 消息发送成功->知道了
     */
    public static void onAlert_sendsuccess_ok() {
        Statistics.userBehavior(SendPoint.alert_sendsuccess_ok);
    }

    /**
     * 解锁聊天弹窗->立即赠送(爵位解锁)
     *
     * @param gap        升级到富人需要的爵位值
     * @param product_id 礼物ID
     * @param price      礼物价格(钻石数量)
     */
    public static void onAlert_unlockchat_give(int gap, int product_id, int price) {
        Map<String, Object> parms = new HashMap<>();
        parms.put("price", price);
        parms.put("gap", gap);
        parms.put("product_id", product_id);
        Statistics.userBehavior(SendPoint.alert_unlockchat_give, parms);
    }

    /**
     * 右上角分享图标(选择了什么模板,在分享赚钱2.0中alert_sharecard_click/分享名片弹窗->点击任意类型分享按钮/传递)
     */
    public static void onPage_qrcodeshare_share() {
        Statistics.userBehavior(SendPoint.page_qrcodeshare_share);
    }


    /**
     * 去分享赚钱(普通点击)(右边是赚钱小秘书按钮)
     */
    public static void onPage_sharetomakemoney_gotoshare() {
        Statistics.userBehavior(SendPoint.page_sharetomakemoney_gotoshare);
    }

    /**
     * 赚钱小秘书(普通点击)(左边是去分享赚钱按钮)
     */
    public static void onPage_sharetomakemoney_xiaomishu() {
        Statistics.userBehavior(SendPoint.page_sharetomakemoney_xiaomishu);
    }

    /**
     * 右上角收益明细(普通点击)
     */
    public static void onPage_sharetomakemoney_profitdetail() {
        Statistics.userBehavior(SendPoint.page_sharetomakemoney_profitdetail);
    }

    /**
     * 分享名片弹窗->右上‘x’关闭
     */
    public static void onAlert_sharecard_close() {
        Statistics.userBehavior(SendPoint.alert_sharecard_close);
    }

    /**
     * 分享名片弹窗->点击任意分享按钮
     *
     * @param source          来源。比如从直播间点出来的，就传直播间分享按钮的埋点名称page_live_share
     * @param channel         分享渠道1微信好友|2微信朋友圈|3QQ好友|4QQ空间|5二维码|6链接
     * @param mould           模板的唯一ID
     * @param link            分享链接
     */
    public static void onAlert_sharecard_click(String source, int channel, int mould, String link) {
        Map<String, Object> parms = new HashMap<>();
        parms.put("source", source);
        parms.put("channel", channel);
        parms.put("mould", mould);
        parms.put("link", link);
        Statistics.userBehavior(SendPoint.alert_sharecard_click, parms);
    }

    /**
     * 只要弹出来就算(类似pageview,然后是用户选择关闭还是点击立即赚钱)
     */
    public static void onAlert_share() {
        Statistics.userBehavior(SendPoint.alert_share);
    }

    /**
     * 分享就能赚钱弹窗(一键打招呼关闭后那个弹窗)->右上‘x’关闭
     */
    public static void onAlert_share_closee() {
        Statistics.userBehavior(SendPoint.alert_share_close);
    }

    /**
     * 分享就能赚钱弹窗(一键打招呼关闭后那个弹窗)->立即赚钱
     */
    public static void onAlert_share_makemoney() {
        Statistics.userBehavior(SendPoint.alert_share_makemoney);
    }

    /**
     * 分享奖励弹窗->右上‘x’关闭
     */
    public static void onAlert_sharereward_close() {
        Statistics.userBehavior(SendPoint.alert_sharereward_close);
    }

    /**
     * /这是您要找的人吗？->右上‘x’关闭
     */
    public static void onAlert_gotouser_close() {
        Statistics.userBehavior(SendPoint.alert_gotouser_close);
    }

    /**
     * 这是您要找的人吗？->聊天
     *
     * @param touid 分享码上的uid
     */
    public static void onAlert_gotouser_go(long touid) {
        Map<String, Object> parms = new HashMap<>();
        parms.put("touid", touid);
        Statistics.userBehavior(SendPoint.alert_gotouser_go, parms);
    }

    /**
     * 我的->分享赚钱(普通点击)
     */
    public static void onMenu_me_sharetomakemoney() {
        Statistics.userBehavior(SendPoint.menu_me_sharetomakemoney);
    }

    /**
     * 我的->主页->分享(普通点击)(跟推广版里page_info_share/个人资料页->右上角分享按钮区分一下,从推广版里剥离出来)
     */
    public static void onMenu_me_homepage_share() {
        Statistics.userBehavior(SendPoint.menu_me_homepage_share);
    }

    /**
     * 我的->我的粉丝
     */
    public static void onMenu_me_myfans() {
        Statistics.userBehavior(SendPoint.menu_me_myfans);
    }

    /**
     * 我的->新手教程
     */
    public static void onMenu_me_novicetutorial() {
        Statistics.userBehavior(SendPoint.menu_me_novicetutorial);
    }

    /**
     * 我的排名
     */
    public static void onPage_sharetomakemoney_myrank() {
        Statistics.userBehavior(SendPoint.page_sharetomakemoney_myrank);
    }

    /**
     * 赚钱学堂
     */
    public static void onPage_sharetomakemoney_moneyschool() {
        Statistics.userBehavior(SendPoint.page_sharetomakemoney_moneyschool);
    }

    /**
     * 直接点击问题
     *
     * @param id 问题的id
     */
    public static void onPage_sharetomakemoney_questionclick(int id) {
        Map<String, Object> parms = new HashMap<>();
        parms.put("id", id);
        Statistics.userBehavior(SendPoint.page_sharetomakemoney_questionclick);
    }

    /**
     * 更多问题
     */
    public static void onPage_sharetomakemoney_morequestion() {
        Statistics.userBehavior(SendPoint.page_sharetomakemoney_morequestion);
    }

    /**
     * 点击问题
     *
     * @param id 问题的id
     */
    public static void onPage_moneyschool_questionclick(int id) {
        Map<String, Object> parms = new HashMap<>();
        parms.put("id", id);
        Statistics.userBehavior(SendPoint.page_moneyschool_questionclick);
    }

    /**
     * 分享给美女(普通点击)
     */
    public static void onPage_sharingtasks_sharewoman() {
        Statistics.userBehavior(SendPoint.page_sharingtasks_sharewoman);
    }


    /**
     * 分享给帅哥(普通点击)
     */
    public static void onPage_sharingtasks_shareman() {
        Statistics.userBehavior(SendPoint.page_sharingtasks_shareman);
    }


    /**
     * 分享按钮(普通点击)
     */
    public static void onPage_live_share() {
        Statistics.userBehavior(SendPoint.page_live_share);
    }


    /**
     * 红包卡悬浮按钮->关闭
     */
    public static void onFloat_button_redpacketcard_close() {
        Statistics.userBehavior(SendPoint.float_button_redpacketcard_close);
    }

    /**
     * /红包卡悬浮按钮->点击
     */
    public static void onFloat_button_redpacketcard_click() {
        Statistics.userBehavior(SendPoint.float_button_redpacketcard_click);
    }

    /**
     * 每日打卡红包弹窗->关闭
     */
    public static void onAlert_dayredpacketcard_close() {
        Statistics.userBehavior(SendPoint.alert_dayredpacketcard_close);
    }

    /**
     *每日打卡红包弹窗->去分享并领取(走分享流程)
     */
    public static void onAlert_dayredpacketcard_share() {
        Statistics.userBehavior(SendPoint.alert_dayredpacketcard_share);
    }

    /**
     * 分享结果弹窗->知道了(分享成功)
     */
    public static void onAlert_shareresult_ok() {
        Statistics.userBehavior(SendPoint.alert_shareresult_ok);
    }

    /**
     * 分享结果弹窗->重新分享(分享失败)
     */
    public static void onAlert_shareresult_reshare() {
        Statistics.userBehavior(SendPoint.alert_shareresult_reshare);
    }

    /**
     * 分享完成提现 提现成功页面
     */
    public static void onPage_Withdrawsuccess_share() {
        Statistics.userBehavior(SendPoint.page_Withdrawsuccess_share);
    }
}
