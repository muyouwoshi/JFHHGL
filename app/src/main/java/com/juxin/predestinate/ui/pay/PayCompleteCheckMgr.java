package com.juxin.predestinate.ui.pay;

import android.support.v4.app.FragmentActivity;

import com.juxin.library.observe.MsgMgr;
import com.juxin.predestinate.module.util.BaseUtil;
import com.juxin.predestinate.module.util.UIUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by siow on 2017/7/12.
 */


public class PayCompleteCheckMgr {
    private final FragmentActivity activity;
    private final boolean closeAct;
    private Timer timer;

    public PayCompleteCheckMgr(FragmentActivity activity, boolean closeAct){
        this.activity = activity;
        this.closeAct = closeAct;
    }

    public void startTimer(){
        cancelTimer();

        timer = new Timer();
        timer.schedule(new PayCompleteCheckTask(), 500, 500);
    }

    public void cancelTimer(){
        try {
            if (timer != null) timer.cancel();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private class PayCompleteCheckTask extends TimerTask {
        @Override
        public void run() {
            MsgMgr.getInstance().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (UIUtil.isActDestroyed(activity)) return;
                    if (!BaseUtil.isTopActivity(activity, activity.getClass().getName())) return;

                    cancelTimer();
                    PayConst.checkPayComplete(activity);

                    if (closeAct) activity.finish();
                }
            });
        }
    }
}