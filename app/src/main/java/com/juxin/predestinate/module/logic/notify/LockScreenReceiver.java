package com.juxin.predestinate.module.logic.notify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.juxin.predestinate.module.logic.application.App;

/**
 * 锁屏状态监听receiver
 */
public class LockScreenReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();

            if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                LockScreenMgr.getInstance().reenableKeyguard();
                LockScreenMgr.getInstance().popupActivity(false);
                App.isKeyguard = App.AppKeyguard.KG_SCREEN_OFF;
            }else if (action.equals(Intent.ACTION_USER_PRESENT)) {
                App.isKeyguard = App.AppKeyguard.KG_SCREEN_ON;
            }

        //    PLogger.d("---LockScreenReceiver--->" + action);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
