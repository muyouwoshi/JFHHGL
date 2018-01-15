package com.juxin.predestinate.module.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;

import com.juxin.library.log.PSP;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.config.Constant;

/**
 * 通知栏提示工具类。所有本地实现的通知栏提示全部调用此工具类
 */
public class NotificationsUtils {

    /**
     * 显示通知，通知布局为系统默认。
     *
     * @param context  上下文
     * @param intent
     * @param notifyId 通知id
     * @param icon     通知大图
     * @param title    通知标题
     * @param content  通知内容
     * @param tickText 顶部提示文字
     * @param time     通知时间显示，为unix时间戳格式
     */
    public static void showSimpleNotify(Context context, Intent intent, int notifyId, Bitmap icon,
                                        String title, String content, String tickText, long time) {
        if (!PSP.getInstance().getBoolean(Constant.SETTING_MESSAGE, Constant.SETTING_MESSAGE_DEFAULT)) {
            return;
        } else {
            // 不在前台 并且关闭后台消息提醒
            if (!PSP.getInstance().getBoolean(Constant.SETTING_QUIT_MESSAGE, true)
                    && !ModuleMgr.getAppMgr().isForeground()) return;
        }
        // 创建 Intent 实例
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // 在准备好使用之前，持有intent
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new Builder(context);
        mBuilder.setContentIntent(pi).setSmallIcon(R.drawable.ic_launcher).setContentText(content).setAutoCancel(true)
                .setContentTitle(title).setWhen(time);
        if (icon != null) {
            mBuilder.setLargeIcon(icon);
        } else {
            mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), (R.drawable.ic_launcher)));
        }
        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        Notification noti = mBuilder.build();
        noti.tickerText = tickText;
        NotificationManager noteManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // 发布到系统栏，此为真正的通知。
        noteManager.notify(notifyId, noti);
        // 再次发送
        noteManager.notify(notifyId, mBuilder.build());
    }

    /**
     * 根据id删除某个通知
     *
     * @param notifyid
     */
    public static void cancelById(int notifyid) {
        // 获取通知 manager 的实例
        NotificationManager noteManager = (NotificationManager) App.context.getSystemService(Context.NOTIFICATION_SERVICE);
        noteManager.cancel(notifyid);
    }

    /**
     * 清除所有通知
     */
    public static void cancelAll() {
        // 获取通知 manager 的实例
        NotificationManager noteManager = (NotificationManager) App.context.getSystemService(Context.NOTIFICATION_SERVICE);
        noteManager.cancelAll();
    }

    /**
     * 获取默认的pendingIntent,为了防止2.3及以下版本报错
     *
     * @param flags flags属性: 在顶部常驻:Notification.FLAG_ONGOING_EVENT 点击去除：Notification.FLAG_AUTO_CANCEL
     */
    public PendingIntent getDefalutIntent(int flags) {
        PendingIntent pendingIntent = PendingIntent.getActivity(App.context, 1, new Intent(), flags);
        return pendingIntent;
    }
}
