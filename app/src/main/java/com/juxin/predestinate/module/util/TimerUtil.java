package com.juxin.predestinate.module.util;

import android.os.Handler;

/**
 * 延时执行
 * Created by zijunna on 15/1/26.
 */
public class TimerUtil {

    public interface CallBack {
        void call();
    }

    /**
     * 延时执行
     *
     * @param callBack 延时执行回调
     * @param tm       延时时长
     */
    public static void beginTime(final CallBack callBack, int tm) {
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                callBack.call();
            }
        };
        handler.postDelayed(runnable, tm);
    }

    private static int increaseTimeCount = 0;

    /**
     * 获取在上下区间以最小值进行递增的数值，达到最大值时重置
     *
     * @param minTime 最小值
     * @param maxTime 最大值
     * @param reset   是否立即重置
     * @return 递增的数值
     */
    public static int increaseTime(int minTime, int maxTime, boolean reset) {
        if (reset) increaseTimeCount = 0;
        increaseTimeCount += minTime;
        if (increaseTimeCount > maxTime) increaseTimeCount = minTime;
        return increaseTimeCount;
    }

    /**
     * 重置递增数值
     */
    public static void resetIncreaseTime(){
        increaseTimeCount = 0;
    }

}
