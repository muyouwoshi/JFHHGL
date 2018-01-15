package com.juxin.predestinate.ui.mail.unlock;

import android.support.v4.app.FragmentActivity;

import com.juxin.library.log.PLogger;
import com.juxin.library.log.PSP;
import com.juxin.library.observe.Msg;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.util.TimeUtil;
import com.juxin.predestinate.module.util.UIShow;

/**
 * Created by IQQ on 2017-11-01.
 */

public class UnlockComm {
    /**
     * 分享常量，每日更新
     */
    private static String getShareNumStr(){
        return getUserConst(Constant.MESSAGE_SHARE_NUM) + TimeUtil.getCurrentData();
    }

    /**
     * 获取分享次数
     *
     * @return
     */
    public static int getShareNum() {
        return PSP.getInstance().getInt(getShareNumStr(), 0);
    }

    /**
     * 设置分享次数
     *
     * @param num
     */
    public static void setShareNum(int num) {
        PSP.getInstance().put(getShareNumStr(), num);
    }


    /**
     * 获取爵位是否符合发送消息
     */
    public static boolean getJueweiOk() {
        return ModuleMgr.getCenterMgr().getMyInfo().getNobilityInfo().getRank() >= ModuleMgr.getCommonMgr().getCommonConfig().getCommon().getFree_speak_level();
    }

    /**
     * 获取下次是否提示钻石扣费 反向取值
     */
    public static boolean isShowDiamondMsg() {
        return PSP.getInstance().getBoolean(getUserConst(Constant.MESSAGE_DIAMOND_SHOW), false);
    }

    /**
     * 设置下次是否提示钻石扣费 反向取值
     */
    public static void setShowDiamondMsg(boolean showDiamondMsg) {
        PSP.getInstance().put(getUserConst(Constant.MESSAGE_DIAMOND_SHOW), showDiamondMsg);
    }

    /**
     * 获取是否显示弹框
     */
    public static Boolean getCanMsg() {
        UserDetail myDetail = ModuleMgr.getCenterMgr().getMyInfo();
        PLogger.d("-----canMsg---- diamand:" + myDetail.getDiamand() + "  isShowDiamondMsg:" + isShowDiamondMsg() +
                "  juewei:" + getJueweiOk() + " shareNum:" + getShareNum());
        return (myDetail.getDiamand() > 0 && isShowDiamondMsg()) || getJueweiOk() || getShareNum() > 0;
    }

    /**
     * 获取是否显示解锁弹框并显示
     * <p>
     * 有一个符合条件为true 意思是可以发消息
     * false会弹出解锁弹框
     *
     * @param activity
     * @param touid    需要put参数：touid
     * @return
     */
    public static Boolean getCanMsgAndShow(FragmentActivity activity, Long touid) {
        Boolean can = getCanMsg();
        PLogger.d("-----canMsg----  can:" + can);
        if (!can) UIShow.showMessageUnlockDlg(activity, touid);
        return can;
    }

    /**
     * * 发送给主窗体解锁消息
     *
     * @param type  解锁类型 1 分享解锁 2.钻石消费解锁 3.爵位解锁
     * @param value 对应解锁类型 1.0，2.钻石消费数默认-1 3.赠送礼物id
     */
    public static void sendUnlockMessage(String type, int value) {
        MsgMgr.getInstance().sendMsg(MsgType.MESSAGE_UNLOCK, new Msg(type, value));
    }


    /**
     * 显示钻石扣费弹框
     *
     * @param activity
     */
    public static void showDiamondMessageOKDlg(FragmentActivity activity) {
        if (!isShowDiamondMsg()) UIShow.showMessageUnlockOKDlg(activity, 2);
    }


    /**
     * 获取带用户的PSP
     *
     * @param str
     * @return
     */
    public static String getUserConst(String str) {
        return str + "_" + String.valueOf(ModuleMgr.getCenterMgr().getMyInfo().getUid());
    }
}
