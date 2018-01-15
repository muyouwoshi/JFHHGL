package com.juxin.predestinate.module.logic.application;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.juxin.library.log.PSP;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.local.statistics.log.AppStatus;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.notify.view.LockScreenActivity;
import com.juxin.predestinate.module.logic.notify.view.UserMailNotifyAct;
import com.juxin.predestinate.module.logic.socket.IMProxy;

import java.util.LinkedList;

/**
 * Application中activity生命周期监测回调，>=API14
 * Created by @author ZRP on 2016/9/8.
 */
public class PActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    final int LISTSIZE = 10;
    /**
     * 本地模拟的activity栈链表，记录activity跳转行为
     */
    private LinkedList<String> activities = new LinkedList<>();
    /**
     * 最后的Activity是否属于前台显示
     */
    private volatile boolean isForeground = false;

    /**
     * @return 获取本地模拟的activity栈链表
     */
    public LinkedList<String> getActivities() {
        return activities;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        //直接关闭 LockScreenActivity 与 UserMailNotifyAct 时，不响应之前Activity的onResume， App.activity 静态引用无法释放导致内存泄露
        if (!(activity instanceof LockScreenActivity) && !(activity instanceof UserMailNotifyAct)) {
            App.activity = activity;
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        //直接关闭 LockScreenActivity 与 UserMailNotifyAct 时，不响应之前Activity的onResume， App.activity 静态无法释放引用导致内存泄露
        if (!(activity instanceof LockScreenActivity) && !(activity instanceof UserMailNotifyAct)) {
            App.activity = activity;
        }

        // 本地模拟一个只存储10条记录的activity栈
        activities.add(activity.getClass().getSimpleName());
        if (activities.size() > LISTSIZE) {
            activities.remove(0);
        }

        if (!isForeground
                //锁屏弹窗和应用外弹窗不算切换为前台
                && !(activity instanceof LockScreenActivity)
                && !(activity instanceof UserMailNotifyAct))

        {
            IMProxy.getInstance().heartBeat3(true);
            Statistics.appStatusChange(AppStatus.FOREGROUND);

            isForeground = true;
            PSP.getInstance().put(Constant.APP_IS_FOREGROUND, isForeground);
        }

    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (App.activity == activity) {
            isForeground = false;
            PSP.getInstance().put(Constant.APP_IS_FOREGROUND, isForeground);
            IMProxy.getInstance().heartBeat3(false);
            Statistics.appStatusChange(AppStatus.BACKGROUND);
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    /**
     * @return 最后的Activity是否属于前台显示
     */
    public boolean isForeground() {
        return isForeground;
    }
}
