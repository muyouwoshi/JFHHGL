package com.juxin.predestinate.module.local.statistics;

import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.ui.user.paygoods.GoodsConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息模块统计项添加
 * Created by IQQ on 2017/6/9.
 */
public class StatisticsMessage {

    //我的好友(普通点击)
    public static void friendClick() {
        Statistics.userBehavior(SendPoint.menu_xiaoxi_myfriend);
    }

    //黑名单(普通点击)
    public static void blackUser() {
        Statistics.userBehavior(SendPoint.menu_xiaoxi_myfriend_lahei);
    }

    //消息->我的好友->黑名单->查看用户资料
    public static void blackUserInfo(long touid) {
        Statistics.userBehavior(SendPoint.menu_xiaoxi_myfriend_lahei_viewuserinfo, touid);
    }

    //消息->我的好友->黑名单->查看用户资料
    public static void blackRemove(long touid) {
        Statistics.userBehavior(SendPoint.menu_xiaoxi_myfriend_lahei_remove, touid);
    }

    //消息->我的好友->打开聊天框(需要传递to_uid)
    public static void openFriendChat(long touid, int unReadNum) {
//        Map<String, Object> parms = new HashMap<>();
//        parms.put("unread_num", unReadNum);
        Statistics.userBehavior(SendPoint.menu_xiaoxi_myfriend_chatframe, touid);
    }

    //消息->我的好友->打开聊天框(需要传递to_uid)
    public static void openChat(long touid, int unReadNum) {
        Map<String, Object> parms = new HashMap<>();
        parms.put("unread_num", unReadNum);
        Statistics.userBehavior(SendPoint.menu_xiaoxi_chatframe, touid, parms);
    }

    //消息->删除用户
    public static void deleteUser(List<BaseMessage> lst) {
        if (lst == null || lst.isEmpty()) return;

        List<Long> uids = new ArrayList<>();
        for (BaseMessage message : lst) {
            uids.add(message.getId());
        }
        Map<String, Object> params = new HashMap<>();
        params.put("to_uid", uids);
        Statistics.userBehavior(SendPoint.menu_xiaoxi_deluser, params);
    }

    //消息->谁关注我(普通点击)
    public static void whoFollowMe() {
        Statistics.userBehavior(SendPoint.menu_xiaoxi_sgzw);
    }

    //消息->谁关注我->我关注的(普通点击)
    public static void meFollow() {
        Statistics.userBehavior(SendPoint.menu_xiaoxi_sgzw_wgz);
    }

    ///消息->谁关注我->我关注的->取消关注
    public static void cancelFollow(long touid) {
        Statistics.userBehavior(SendPoint.menu_xiaoxi_sgzw_wgz_cancelfollow, touid);
    }

    //谁关注我->我关注的->查看用户资料
    public static void seeFollowUserInfo(long touid) {
        Statistics.userBehavior(SendPoint.menu_xiaoxi_sgzw_wgz_viewuserinfo, touid);
    }

    //消息->谁关注我->关注我的(普通点击)
    public static void followMe() {
        Statistics.userBehavior(SendPoint.menu_xiaoxi_sgzw_gzw);
    }

    ///消息->谁关注我->关注我的->升级VIP会员,查看关注用户资料(男用户)
    public static void followMeToVip(long touid) {
        Statistics.userBehavior(SendPoint.menu_xiaoxi_sgzw_gzw_vippay, touid);
    }

    //消息->谁关注我->关注我的->查看用户资料
    public static void followMeToSeeUserInfo(long touid) {
        Statistics.userBehavior(SendPoint.menu_xiaoxi_sgzw_gzw_viewuserinfo, touid);
    }

    //消息->谁关注我->关注我的->取消关注
    public static void followMeToCancel(long touid) {
        Statistics.userBehavior(SendPoint.menu_xiaoxi_sgzw_gzw_cancelfollow, touid);
    }

    //消息->谁关注我->关注我的->关注TA
    public static void followMeToFollow(long touid) {
        Statistics.userBehavior(SendPoint.menu_xiaoxi_sgzw_gzw_followit, touid);
    }

    // -----------------------------聊天框------------------------------

    //消息->谁关注我->关注我的->关注TA
    public static void chatSendBtn(String msg, long to_uid) {
        Map<String, Object> params = new HashMap<>();
        params.put("msg", msg);
        Statistics.userBehavior(SendPoint.chatframe_tool_btnsend, to_uid, params);
    }

    /**
     * 聊天框->工具栏->礼物按钮->赠送按钮(礼物的ID,位置)
     *
     * @param to_uid  产生交互的uid
     * @param gift_id 礼物ID
     * @param gemNum  礼物钻石数
     */
    public static void chatGiveGift(long to_uid, int gift_id, int gemNum) {
        Map<String, Object> map = new HashMap<>();
        map.put("gift_id", gift_id);
        map.put("price", gemNum / 10);
        Statistics.userBehavior(SendPoint.chatframe_tool_gift_give, to_uid, map);
    }

    /**
     * 聊天框->工具栏->礼物按钮->赠送按钮->立即充值
     *
     * @param to_uid  产生交互的uid
     * @param payType 支付方式微信/支付宝/其他支付
     * @param gem_num 选中的钻石数量
     * @param price   人民币金额
     */
    public static void chatGiveGiftPay(long to_uid, int payType, int gem_num, double price) {
        Map<String, Object> map = new HashMap<>();
        map.put("payType", getPayType(payType));
        map.put("gem_num", gem_num);
        map.put("price", (int) (price / 100));//元
        Statistics.userBehavior(SendPoint.chatframe_tool_gift_give_btnljcz, to_uid, map);
    }

    /**
     * 聊天框->导航栏->充值弹窗确认支付按钮
     *
     * @param to_uid  产生交互的uid
     * @param payType 支付方式微信/支付宝/其他支付
     * @param price   人民币金额
     */
    public static void chatNavConfirmPay(SendPoint sendPoint, long to_uid, int payType, double price) {
        Map<String, Object> map = new HashMap<>();
        map.put("payType", getPayType(payType));
        map.put("price", (int) (price / 100));//元
        Statistics.userBehavior(sendPoint, to_uid, map);
    }

    /**
     * 根据整数支付类型获取对应等等字符串展示
     *
     * @param payType 支付类型
     * @return 支付类型
     */
    public static String getPayType(int payType) {
        String type;
        switch (payType) {
            case GoodsConstant.PAY_TYPE_WECHAT:
                type = GoodsConstant.PAY_TYPE_WECHAT_NAME;
                break;
            case GoodsConstant.PAY_TYPE_ALIPAY:
                type = GoodsConstant.PAY_TYPE_ALIPAY_NAME;
                break;
            default:
                type = GoodsConstant.PAY_TYPE_WECHAT_NAME;
                break;
        }
        return type;
    }

    // --------------------------缘分吧2.2新增统计--------------------------------

    /**
     * 聊天框->立即购买Y币(传递当前Y币余额)
     *
     * @param to_uid 产生交互的uid
     */
    public static void chatFramePayY(long to_uid) {
        Map<String, Object> map = new HashMap<>();
        map.put("money_num", ModuleMgr.getCenterMgr().getMyInfo().getYcoin());//当前Y币余额
        Statistics.userBehavior(SendPoint.page_chatframe_gopayy, to_uid, map);
    }

    /**
     * 聊天框->消息->音视频邀请->拒绝接收
     *
     * @param to_uid  产生交互的uid
     * @param gem_num 多少钻石1分钟
     * @param type    邀请类型：InviteVideoMessage#media_tp，1视频，2语音
     */
    public static void chatInviteReject(long to_uid, int gem_num, int type) {
        Map<String, Object> map = new HashMap<>();
        map.put("gem_num", gem_num);
        map.put("type", type == 1 ? 1 : 0);
        Statistics.userBehavior(SendPoint.page_chatframe_msg_invite_rejected, to_uid, map);
    }

    /**
     * 聊天框->消息->音视频邀请->立即接通
     *
     * @param to_uid  产生交互的uid
     * @param gem_num 多少钻石1分钟
     * @param type    邀请类型：InviteVideoMessage#media_tp，1视频，2语音
     */
    public static void chatInviteAccept(long to_uid, int gem_num, int type) {
        Map<String, Object> map = new HashMap<>();
        map.put("gem_num", gem_num);
        map.put("type", type == 1 ? 1 : 0);
        Statistics.userBehavior(SendPoint.page_chatframe_msg_invite_accept, to_uid, map);
    }

    /**
     * 聊天框->消息->邀请已过有效时间->立即回拨
     *
     * @param to_uid  产生交互的uid
     * @param gem_num 多少钻石1分钟
     * @param type    0语音1视频
     */
    public static void chatInviteTimeoutCall(long to_uid, int gem_num, int type) {
        Map<String, Object> map = new HashMap<>();
        map.put("gem_num", gem_num);
        map.put("type", type == 1 ? 1 : 0);
        Statistics.userBehavior(SendPoint.page_chatframe_msg_invitetimeout_gocall, to_uid, map);
    }

    /**
     * 聊天框->消息->邀请已过有效时间->立即回拨
     *
     * @param to_uid      产生交互的uid
     * @param gem_num     钻石余额
     * @param pay_gem_num 购买钻石数量
     * @param pay_type    0语音1视频
     */
    public static void chatInviteGemPay(long to_uid, int gem_num, int pay_gem_num, int pay_type) {
        Map<String, Object> map = new HashMap<>();
        map.put("gem_num", gem_num);
        map.put("pay_gem_num", pay_gem_num);
        map.put("pay_type", getPayType(pay_type));
        Statistics.userBehavior(SendPoint.page_invitewait_gempay_gopay, to_uid, map);
    }

    // --------------------------缘分吧2.3女性任务版新增统计--------------------------------

    /**
     * 聊天框->消息->加入公会(女用户,普通点击,touid传男用户UID)
     *
     * @param to_uid 产生交互的uid
     */
    public static void chatAddUnion(long to_uid) {
        Statistics.userBehavior(SendPoint.page_chatframe_msg_addlabourunion, to_uid);
    }

    // ------------------索要礼物-聊天框页(男用户收到)-------------------

    /**
     * 聊天框->消息->私密消息查看(普通点击,touid等于女用户UID)
     *
     * @param to_uid 产生交互的uid
     * @param type   消息类型，以确定是那种类型的查看：1消息、2语音、3视频、4图片，对应LookPrivateDlg中类型
     */
    public static void chatViewPrivate(long to_uid, int type) {
        SendPoint sendPoint = SendPoint.page_chatframe_msg_privatemsg_goview;
        switch (type) {
            case 0://私密消息
                sendPoint = SendPoint.page_chatframe_msg_privatemsg_goview;
                break;
            case 1://私密语言
                sendPoint = SendPoint.page_chatframe_msg_privatevoice_golisten;
                break;
            case 3://私密视频
                sendPoint = SendPoint.page_chatframe_msg_privatevideo_goview;
                break;
            case 4://私密照片
                sendPoint = SendPoint.page_chatframe_msg_privateimg_goview;
                break;
            default:
                break;
        }
        Statistics.userBehavior(sendPoint, to_uid);
    }

    /**
     * 聊天框->消息->私密视频->本次感受->确定
     *
     * @param to_uid   产生交互的uid
     * @param video_id 视频ID
     * @param like     是否喜欢该视频
     */
    public static void chatPrivateMood(long to_uid, long video_id, boolean like) {
        Map<String, Object> map = new HashMap<>();
        map.put("video_id", video_id);
        map.put("mood", like ? 0 : 1);//0非常喜欢/1心碎了一地
        Statistics.userBehavior(SendPoint.page_chatframe_msg_privatevideo_mood_submit, to_uid, map);
    }

    /**
     * 聊天框->消息->私密消息：打赏查看[确认打赏]
     *
     * @param to_uid  产生交互的uid
     * @param msgType 消息类型，以确定是那种类型的打赏：1消息、2语音、3视频、4图片，对应LookPrivateDlg中类型
     * @param gem_num 礼物的钻石数量
     * @param girl_id 礼物ID
     * @param msgid   消息ID
     */
    public static void chatPrivateBounty(long to_uid, int msgType, int gem_num, int girl_id, long msgid) {
        SendPoint sendPoint = SendPoint.page_chatframe_msg_privatemsg_dashang_pay;
        switch (msgType) {
            case 0://私密消息
                sendPoint = SendPoint.page_chatframe_msg_privatemsg_dashang_pay;
                break;
            case 1://私密语言
                sendPoint = SendPoint.page_chatframe_msg_privatevoice_dashang_pay;
                break;
            case 3://私密视频
                sendPoint = SendPoint.page_chatframe_msg_privatevideo_dashang_payview;
                break;
            default:
                break;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("gem_num", gem_num);
        map.put("girl_id", girl_id);
        map.put("msgid", msgid);
        Statistics.userBehavior(sendPoint, to_uid, map);
    }

    // --------------------------缘分吧AB测试版新增统计--------------------------------

    /**
     * 聊天页->解锁聊天->确认解锁
     *
     * @param to_uid  产生交互的对方uid
     * @param key_num 解锁之前钥匙数量(我的钥匙)
     */
    public static void abTestKeyUnlock(long to_uid, int key_num) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("key_num", key_num);
        Statistics.userBehavior(SendPoint.alert_unlock_confirm, to_uid, params);
    }

    /**
     * 购买钥匙弹窗->确定
     *
     * @param to_uid   产生交互的对方uid
     * @param price    人民币/分
     * @param pay_type 选择支付类型
     */
    public static void abTestBuyKeyConfirm(long to_uid, int price, int pay_type) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("price", price * 100);//此处服务器返回单位/元，统一处理成/分
        params.put("pay_type", getPayType(pay_type));
        Statistics.userBehavior(SendPoint.alert_buykeys_confirm, to_uid, params);
    }

    /**
     * 视频卡弹窗->立即使用
     *
     * @param to_uid 产生交互的对方uid
     * @param cards  当前视频卡数量
     */
    public static void abTestUseVideoCard(long to_uid, int cards) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("cards", cards);
        Statistics.userBehavior(SendPoint.alert_videocard_use, to_uid, params);
    }

    /**
     * 聊天页->红包->开
     *
     * @param to_uid         产生交互的对方uid
     * @param uid            开谁的红包
     * @param redpackage_num 红包个数
     */
    public static void abTestOpenRedBag(long to_uid, long uid, int redpackage_num) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("uid", uid);
        params.put("redpackage_num", redpackage_num);
        Statistics.userBehavior(SendPoint.page_chat_redpackage_open, to_uid, params);
    }

    /**
     * 聊天页->红包->开->领取
     *
     * @param to_uid 产生交互的对方uid
     * @param fuid   fromUID的简写,领取了谁的红包
     * @param price  人民币/分
     */
    public static void abTestRedBagOpenReceive(long to_uid, long fuid, double price) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("fuid", fuid);
        params.put("price", price);
        Statistics.userBehavior(SendPoint.page_chat_redpackage_open_receive, to_uid, params);
    }

    /**
     * 聊天页->红包->开->赠送
     *
     * @param to_uid 产生交互的对方uid
     * @param touid  赠送给谁(你们的逻辑应该是开谁的红包，就赠送给谁)
     * @param price  人民币/分
     */
    public static void abTestRedBagOpenGive(long to_uid, long touid, double price) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("touid", touid);
        params.put("price", price);
        Statistics.userBehavior(SendPoint.page_chat_redpackage_open_receive, to_uid, params);
    }

    /**
     * 聊天页->查看红包(普通点击)
     *
     * @param to_uid 产生交互的对方uid
     */
    public static void abTestRedBagMsg(long to_uid) {
        Statistics.userBehavior(SendPoint.page_chat_msg_redpackage, to_uid);
    }

    /**
     * 聊天页->恭喜获得红包弹窗->领取
     *
     * @param to_uid 产生交互的对方uid
     * @param price  人民币/单位/分
     */
    public static void abTestRedBagAlertReceive(long to_uid, int price) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("price", price);
        Statistics.userBehavior(SendPoint.page_chat_alert_redpackage_receive, to_uid, params);
    }

    /**
     * 聊天页->恭喜获得红包弹窗->关闭
     *
     * @param to_uid 产生交互的对方uid
     */
    public static void abTestRedBagAlertClose(long to_uid) {
        Statistics.userBehavior(SendPoint.page_chat_alert_redpackage_close, to_uid);
    }
}
