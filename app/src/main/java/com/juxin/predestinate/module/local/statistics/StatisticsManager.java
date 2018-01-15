package com.juxin.predestinate.module.local.statistics;

import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.ui.discover.MainAVChatFragmentAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mr.Huang
 * @date 2017/9/5
 */
public class StatisticsManager {

    /**
     * 充值y币弹窗->确认支付(A版,B版无y币)
     *
     * @param product_id
     * @param price
     * @param num
     * @param pay_type
     */
    public static void alert_y_pay(int product_id, float price, int num, int pay_type) {
        Map<String, Object> params = new HashMap<>();
        params.put("product_id", product_id);
        params.put("price", price);
        params.put("num", num);
        params.put("pay_type", StatisticsMessage.getPayType(pay_type));
        Statistics.userBehavior(SendPoint.alert_y_pay, params);
    }

    /**
     * 充值钻石弹窗->确认支付
     *
     * @param product_id 钻石购买ID
     * @param price      本次充值金额(人民币)
     * @param num        本次购买钻石的数量
     * @param pay_type   支付类型/wechat/alipay
     */
    public static void alert_gem_pay(int product_id, float price, int num, int pay_type) {
        Map<String, Object> params = new HashMap<>();
        params.put("product_id", product_id);
        params.put("price", price);
        params.put("num", num);
        params.put("pay_type", StatisticsMessage.getPayType(pay_type));
        params.put("env", ModuleMgr.getCenterMgr().getMyInfo().isB() ? 1 : 0);
        Statistics.userBehavior(SendPoint.alert_gem_pay, params);
    }

    /**
     * ab版视频内推->拒绝接受按钮
     *
     * @param from_uid 内推的女性uid
     * @param isAccept 是否接受
     */
    public static void alert_push_refuse_and_accept(int from_uid, boolean isAccept) {
        Map<String, Object> params = new HashMap<>();
        params.put("from_uid", from_uid);
        Statistics.userBehavior(isAccept ? SendPoint.alert_push_accept : SendPoint.alert_push_refuse, params);
    }

    /**
     * 发现->搜索
     *
     * @param content 用户输入的搜索条件
     * @param number  /剩余的搜索次数(本次搜索后剩余的次数)
     */
    public static void menu_faxian_sousuo(String content, int number) {
        Map<String, Object> params = new HashMap<>();
        params.put("content", content);
        params.put("number", number);
        Statistics.userBehavior(SendPoint.menu_faxian_sousuo, params);
    }

    /**
     * 发现->语聊->页面第一次加载时和刷新
     *
     * @param list 此页面的所有数据
     */
    public static void menu_faxian_yuliao_first_and_flush(List<UserInfoLightweight> list, boolean isRefresh) {
        Map<String, Object> params = new HashMap<>();
        long[] uids = new long[list.size()];
        int i = 0;
        for (UserInfoLightweight userInfoLightweight : list) {
            uids[i] = userInfoLightweight.getUid();
            i++;
        }
        params.put("uids", uids);
        Statistics.userBehavior(isRefresh ? SendPoint.menu_faxian_yuliao_flush : SendPoint.menu_faxian_yuliao_first, params);
    }

    /**
     * 发现->推荐->页面第一次加载和刷新
     *
     * @param list 此页面的所有数据
     */
    public static void menu_faxian_tuijian_first_and_flush(List<UserInfoLightweight> list, boolean isRefresh) {
        Map<String, Object> params = new HashMap<>();
        long[] uids = new long[list.size()];
        int i = 0;
        for (UserInfoLightweight userInfoLightweight : list) {
            uids[i] = userInfoLightweight.getUid();
            i++;
        }
        params.put("uids", uids);
        Statistics.userBehavior(isRefresh ? SendPoint.menu_faxian_tuijian_first : SendPoint.menu_faxian_tuijian_flush, params);
    }

    /**
     * 发现->语聊->点击用户头像
     * @param touid 被点击的用户id
     * @param type 1视频,2语音
     * @param list 行为发生时,此页面的所有uid
     */
    public static void menu_faxian_yuliao_click_head(long touid, int type, List<UserInfoLightweight> list){
        Map<String, Object> params = new HashMap<>();
        params.put("touid", touid);
        params.put("type", type);
        long[] uids = new long[list.size()];
        int i = 0;
        for (UserInfoLightweight userInfoLightweight : list) {
            uids[i] = userInfoLightweight.getUid();
            i++;
        }
        params.put("uids", uids);
        Statistics.userBehavior(SendPoint.menu_faxian_yuliao_click_head, params);
    }

}
