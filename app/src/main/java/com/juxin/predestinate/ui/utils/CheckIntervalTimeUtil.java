package com.juxin.predestinate.ui.utils;

import java.util.Calendar;

/**
 * 用于检测上次调用的时间
 */
public class CheckIntervalTimeUtil {

    private Calendar lastCallTime = null;

    /**
     * 调用本方法后会判断上次调用的时间是否与本次调用的时间相差tm间隔，如果是则返回true,否则返回false
     *
     * @param tm 毫秒数
     * @return
     */
    public boolean check(long tm) {
        boolean bool = false;
        Calendar currCalendar = Calendar.getInstance();
        if (lastCallTime != null) {
            if (currCalendar.getTime().getTime() - lastCallTime.getTime().getTime() > tm) {
                bool = true;
            }
        } else {
            bool = true;
        }
        if (bool) {
            lastCallTime = Calendar.getInstance();
        }
        return bool;
    }
}