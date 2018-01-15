package com.juxin.predestinate.module.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.juxin.library.log.PLogger;

import java.util.Calendar;

/**
 * 增强型定时器，慎用。
 *
 * @author JohnsonLi
 * @version 1.0
 * @qq 505214658
 * @date 2015-06-02
 */
public class ExTimerUtil {
    /**
     * 启动一个循环定时器，默认是针对Service的。
     *
     * @param context
     * @param intent
     * @param time    时间间隔。
     */
    public static void startRepeatTimer(Context context, Intent intent, long time) {
        if (intent == null) {
            return;
        }

        try {
            Calendar calendar = Calendar.getInstance();
            long firstTime = calendar.getTimeInMillis() + time;

            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            am.setRepeating(AlarmManager.RTC_WAKEUP, firstTime, time, pendingIntent);
        } catch (Exception e) {
            PLogger.printThrowable(e);
        }
    }

    /**
     * 启动一个定时器，默认是针对Service的。
     *
     * @param context
     * @param intent
     * @param time    时间间隔。
     */
    public static void startTimer(Context context, Intent intent, long time) {
        if (intent == null) {
            return;
        }

        try {
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, time, pendingIntent);
        } catch (Exception e) {
            PLogger.printThrowable(e);
        }
    }

    /**
     * 取消一个闹钟，默认是针对Service的。
     *
     * @param context 上下文。
     * @param intent
     */
    public static void cancelTimer(Context context, Intent intent) {
        if (intent == null) {
            return;
        }

        try {
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            am.cancel(pendingIntent);
        } catch (Exception e) {
            PLogger.printThrowable(e);
        }
    }
}
