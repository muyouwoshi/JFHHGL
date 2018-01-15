package com.juxin.predestinate.module.logic.notify;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.view.View;

import com.juxin.library.log.PLogger;
import com.juxin.library.log.PSP;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.notify.view.LockChatPanel;
import com.juxin.predestinate.module.util.UIShow;

/**
 * 锁屏弹窗管理类
 *
 * @author ?
 */
public class LockScreenMgr {

    private static BroadcastReceiver receiver = null;
    private static IntentFilter filter = null;
    private static boolean reg = false;

    private static class SingletonHolder {
        public static LockScreenMgr instance = new LockScreenMgr();
    }

    public static LockScreenMgr getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * 动态注册锁屏监听
     */
    public void registerReceiver() {
        if (reg) {
            return;
        }

        try {
            filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            receiver = new LockScreenReceiver();

            App.context.registerReceiver(receiver, filter);
            reg = true;
        } catch (Exception e) {
            PLogger.printThrowable(e);
        }
    }

    /**
     * 解除receiver绑定
     */
    public void unregisterReceiver() {
        if (!reg) {
            return;
        }

        try {
            App.context.unregisterReceiver(receiver);
            reg = false;
        } catch (Exception e) {
            PLogger.printThrowable(e);
        }
    }

    // ================================================

    /**
     * 锁屏弹框相关
     */

    private LockChatPanel lockChatPanel;

    /**
     * 锁屏聊天框
     */
    private UserInfoLightweight simpleData;
    private BaseMessage baseMessage;
    private String lockChatContent;

    /**
     * 设置聊天内容
     *
     * @param simpleData  用户的简单个人资料
     * @param baseMessage 预先传递消息内容，以处理特殊的情况
     * @param content     聊天信息
     */
    public void setChatData(UserInfoLightweight simpleData, BaseMessage baseMessage, String content) {
        this.simpleData = simpleData;
        this.baseMessage = baseMessage;
        this.lockChatContent = content;
    }

    private OnLockScreenCallback onLockScreenCallback;

    /**
     * 获取展示的锁屏弹窗view内容
     *
     * @param onLockScreenCallback 锁屏弹窗回调
     * @return 锁屏弹窗view
     */
    public View getLockView(OnLockScreenCallback onLockScreenCallback) {
        this.onLockScreenCallback = onLockScreenCallback;
        if (lockChatContent == null || baseMessage == null) {
            return null;
        }

        if (lockChatPanel == null) {
            lockChatPanel = new LockChatPanel(App.getActivity(), simpleData, baseMessage, lockChatContent);
        } else {
            lockChatPanel.refresh(simpleData, baseMessage, lockChatContent);
        }
        return lockChatPanel.getContentView();
    }

    public void clearOnLockScreenCallback() {
        if (this.onLockScreenCallback != null) {
            this.onLockScreenCallback = null;
        }
    }


    /**
     * 关闭当前锁屏弹窗
     */
    public void closeLockNotify() {
        this.simpleData = null;
        this.baseMessage = null;
        if (onLockScreenCallback != null) {
            onLockScreenCallback.closePopupActivity();
            onLockScreenCallback.disableKeyguard();
        }
    }

    /**
     * 显示锁屏弹窗
     *
     * @param wakeLock 锁屏弹窗是否点亮屏幕
     */
    public boolean popupActivity(boolean wakeLock) {
        if (App.isForeground() || getLockView(null) == null || !isTip() || !isLockScreen()) {
            // 前台/显示面板为空/(应用为退出状态且消息提示状态为退出不提示)/不是锁屏状态：不进行锁屏弹窗
            /* PLogger.d("=== 锁屏弹窗popupActivity returned ===");*/
            return false;
        }

         /*PLogger.d("=== 锁屏弹窗popupActivity===");*/
        UIShow.showLockScreenActivity();

        if (wakeLock) {
            //点亮屏幕
            wakeLock();
        }
        return true;
    }

    /**
     * 点亮屏幕
     */
    public void wakeLock() {
        PowerManager pm = (PowerManager) App.context.getSystemService(Context.POWER_SERVICE);
        if (!pm.isScreenOn()) {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
            wl.acquire();
            wl.release();
        }
    }

    /**
     * 是否显示锁屏弹窗
     *
     * @return true显示
     */
    public boolean isTip() {
        return PSP.getInstance().getBoolean(Constant.SETTING_QUIT_MESSAGE, Constant.SETTING_QUIT_MESSAGE_DEFAULT);
    }

    /**
     * 解除锁屏
     */
    private KeyguardManager.KeyguardLock keyguardLock = null;

    /**
     * 打开系统锁屏
     */
    public void disableKeyguard() {
        if (keyguardLock == null) {
            KeyguardManager keyguardManager = (KeyguardManager) App.context.getSystemService(Context.KEYGUARD_SERVICE);
            keyguardLock = keyguardManager.newKeyguardLock("IN");
        }
        keyguardLock.disableKeyguard();
    }

    /**
     * 恢复disableKeyguard调用隐藏的锁屏
     */
    public void reenableKeyguard() {
        if (keyguardLock == null) {
            KeyguardManager keyguardManager = (KeyguardManager) App.context.getSystemService(Context.KEYGUARD_SERVICE);
            keyguardLock = keyguardManager.newKeyguardLock("IN");
        }
        keyguardLock.reenableKeyguard();
    }

    /**
     * @return 是否为锁屏状态
     */
    public boolean isLockScreen() {
        KeyguardManager mKeyguardManager = (KeyguardManager) App.context.getSystemService(Context.KEYGUARD_SERVICE);
        return mKeyguardManager.inKeyguardRestrictedInputMode();
    }

    /**
     * 锁屏弹窗内部事件回调
     */
    public interface OnLockScreenCallback {

        void closePopupActivity();

        void disableKeyguard();
    }
}
