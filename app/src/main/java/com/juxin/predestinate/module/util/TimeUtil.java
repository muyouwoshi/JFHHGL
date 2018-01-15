package com.juxin.predestinate.module.util;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.format.Time;
import com.juxin.library.log.PLogger;
import com.juxin.library.utils.StringUtils;
import com.juxin.library.utils.TimeBaseUtil;
import com.juxin.library.utils.TypeConvertUtil;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.config.Constant;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author JohnsonLi
 * @version 1.0
 * @qq 505214658
 * @date 2015-04-18
 */
public class TimeUtil extends TimeBaseUtil {

    /**
     * 根据指定的unix时间戳获取显示时间，默认格式化样式为"yyyy-MM-dd"
     *
     * @param timeMillion unix时间戳
     * @return 格式化之后的显示时间
     */
    public static String formatServerData(long timeMillion) {
        return formatServerData("yyyy-MM-dd", timeMillion);
    }

    /**
     * 根据指定的unix时间戳获取显示时间
     *
     * @param dataFormat  格式化样式
     * @param timeMillion unix时间戳
     * @return 格式化之后的显示时间
     */
    public static String formatServerData(String dataFormat, long timeMillion) {
        if (timeMillion == 0) return "";

        SimpleDateFormat format = new SimpleDateFormat(dataFormat, Locale.getDefault());
        return format.format(new Date(timeMillion));
    }

    /**
     * 根据指定的unix时间戳获取显示时间，默认格式化样式为"yyyy-MM-dd"
     *
     * @param timeMillion unix时间戳
     * @return 格式化之后的显示时间
     */
    public static String formatData(long timeMillion) {
        return formatData("yyyy-MM-dd", timeMillion);
    }

    /**
     * 将一个毫秒值，格式化为3月6日 晚上08:31格式
     *
     * @param time 毫秒值。
     * @return 格式化后的字符串。
     */
    public static String millisecondToFormatString(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 -HH-:mm");
        String strTime = "";
        String str[] ;
        try {
            strTime = sdf.format(new Date(time));
            str = strTime.split("-");
            String strTempTime = "";
            if (str.length != 3)
                return strTime;
            strTempTime += str[0];
            int hh = Integer.valueOf(str[1]);
            if (hh >= 0 && hh < 6){
                strTempTime += ("凌晨"+hh);
            }else if (hh >= 6 && hh < 12){
                strTempTime += ("上午"+hh);
            }else if (hh >= 12 && hh < 18){
                strTempTime += ("下午"+(hh-12));
            }else if (hh >= 18 && hh <= 24){
                strTempTime += ("晚上"+(hh-12));
            }
            strTempTime += str[2];
            strTime = strTempTime;
        } catch (Exception e) {
            PLogger.printThrowable(e);
        }

        return strTime;
    }

    /**
     * 根据指定的unix时间戳获取显示时间
     *
     * @param dataFormat  格式化样式
     * @param timeMillion unix时间戳
     * @return 格式化之后的显示时间
     */
    public static String formatData(String dataFormat, long timeMillion) {
        if (timeMillion == 0) return "";

        timeMillion = timeMillion * 1000;
        SimpleDateFormat format = new SimpleDateFormat(dataFormat, Locale.getDefault());
        return format.format(new Date(timeMillion));
    }

    /**
     * 将指定时间与当前时间对比：<br>
     * 小于1分钟，显示刚刚<br>
     * 小于1小时，显示多少分钟前<br>
     * 小于24小时，显示多少小时前<br>
     * 大于24小时，显示几天前
     *
     * @param str 指定时间，格式化字符串。
     * @return 返回格式化后的字符串，如果时间无效，则返回""。
     */
    public static String formatBeforeTime(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        long time = 0L;
        String format = "";
        if (str.length() == 10) {
            format = "yyyy-MM-dd";
        } else if (str.length() == 19) {
            format = "yyyy-MM-dd HH:mm:ss";
        } else if (str.length() == 25) {
            format = "yyyy-MM-dd\'T\'HH:mm:ssZZZZZ";
        } else {
            return "";
        }
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            Date date = simpleDateFormat.parse(str);
            time = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (time == 0)
            return "";
        else
            return formatBeforeTime(time);
    }

    /**
     * 将指定时间与当前时间对比：<br>
     * 小于1分钟，显示刚刚<br>
     * 小于1小时，显示多少分钟前<br>
     * 小于24小时，显示多少小时前<br>
     * 大于24小时，显示几天前
     *
     * @param time 指定时间，毫秒值。
     * @return 返回格式化后的字符串，如果时间无效，则返回""。
     */
    public static String formatBeforeTime(long time) {
        String ret = "";
        long currentTime = ModuleMgr.getAppMgr().getTime();
        long distance = (currentTime - time) / 1000;

        if (distance < 0)
            distance = 0;

        if (distance < 60) {
            ret = "刚刚";
        } else if (distance < 60 * 60) {
            distance = distance / 60;
            ret = distance + "分钟前";
        } else if (distance < 60 * 60 * 24) {
            distance = distance / (60 * 60);
            ret = distance + "小时前";
        } else {
            distance = distance / (60 * 60 * 24);
            if (distance >= 7) {
                ret = "一周前";
            } else
                ret = distance + "天前";
        }
        return ret;
    }

    /**
     * 处理已过去毫秒数（服务器返回的是时间差） <br>
     * 小于1分钟，显示刚刚<br>
     * 小于1小时，显示多少分钟前<br>
     * 小于24小时，显示多少小时前<br>
     * 大于24小时，显示几天前
     *
     * @param time 时间的差值，毫秒值。
     * @return 返回格式化后的字符串，如果时间无效，则返回""。
     */
    public static String formatSpaceTime(long time) {
        String ret = "";
        long distance = time / 1000;

        if (distance < 0)
            distance = 0;

        if (distance < 60) {
            ret = "刚刚";
        } else if (distance < 60 * 60) {
            distance = distance / 60;
            ret = distance + "分钟前";
        } else if (distance < 60 * 60 * 24) {
            distance = distance / (60 * 60);
            ret = distance + "小时前";
        } else {
            distance = distance / (60 * 60 * 24);
            if (distance >= 7) {
                ret = "一周前";
            } else
                ret = distance + "天前";
        }
        return ret;
    }

    /**
     * 获取时间
     *
     * @param str 2015-02-10 22:00:00
     * @return
     */
    public static String getDateTime(String str) {
        return millisecondToFormatStringSecond(stringTDateToMillisecond(str));
    }


    /**
     * 获取时间
     *
     * @param str 2015-02-10 22:00:00
     * @return
     */
    public static Calendar getCalendar(String str) {
        if (str == null || str.length() == 0 || str.equals("null")) {
            return null;
        }
        String format = "";
        if (str.length() == 10) {
            format = "yyyy-MM-dd";
        } else if (str.length() == 19) {
            format = "yyyy-MM-dd HH:mm:ss";
        } else if (str.length() == 25) {
            format = "yyyy-MM-dd\'T\'HH:mm:ssZZZZZ";
        } else {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            Date date = simpleDateFormat.parse(str);
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    /**
     * 补齐 毫秒
     *
     * @param number
     * @return
     */
    public static long onPad(long number) {
        try {
            int poslength = 13;
            int length = String.valueOf(number).length();
            int remaining = poslength - length;
            if (remaining > 0) {
                StringBuilder sb = new StringBuilder();
                sb.append(number);
                for (int i = 0; i < remaining; i++) {
                    sb.append(0);
                }
                return TypeConvertUtil.toLong(sb.toString(), System.currentTimeMillis());
            } else {
                return TypeConvertUtil.toLong(String.valueOf(number).substring(0, 13), System.currentTimeMillis());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return System.currentTimeMillis();
        }
    }

    /* 将指定时间与当前时间对比：<br>
     * 小于1分钟，显示刚刚<br>
     * 小于1小时，显示多少分钟前<br>
     * 小于24小时，显示多少小时前<br>
     * 大于24小时，显示几天前
     *
     * @param time
     *            指定时间，毫秒值。
     *
     * @return 返回格式化后的字符串，如果时间无效，则返回""。
     */
    public static String formatBeforeTimeWeek(long time) {
        String ret = "";
        long currentTime = onPad(ModuleMgr.getAppMgr().getTime());
        currentTime = onPad(currentTime);
        time = onPad(time);

        long distance = (currentTime - time) / 1000;
        // long distance = (currentTime - time);
        if (distance < 0)
            distance = 0;

        if (distance < 60) {
            ret = "刚刚";
        } else if (distance < 60 * 60) {
            distance = distance / 60;
            ret = distance + "分钟前";
        } else if (distance < 60 * 60 * 24) {
            distance = distance / (60 * 60);
            ret = distance + "小时前";
        } else if (distance < (60 * 60 * 24 * 7)) {
            Calendar c_time = Calendar.getInstance();
            c_time.setTimeInMillis(time);
            //int c_week = c_time.get(Calendar.DAY_OF_WEEK_IN_MONTH);
            int c_week = c_time.get(Calendar.WEEK_OF_MONTH);

            int day_week = c_time.get(Calendar.DAY_OF_WEEK);
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(currentTime);
            int week = c.get(Calendar.WEEK_OF_MONTH);
            if (c_week == week) {//同一周
                switch (day_week) {
                    case 1:
                        ret = "星期日";
                        break;
                    case 2:
                        ret = "星期一";
                        break;
                    case 3:
                        ret = "星期二";
                        break;
                    case 4:
                        ret = "星期三";
                        break;
                    case 5:
                        ret = "星期四";
                        break;
                    case 6:
                        ret = "星期五";
                        break;
                    case 7:
                        ret = "星期六";
                        break;
                    default:
                        break;
                }
            } else {// 不是同一周
                switch (day_week) {
                    case 1:
                        ret = "上星期日";
                        break;
                    case 2:
                        ret = "上星期一";
                        break;
                    case 3:
                        ret = "上星期二";
                        break;
                    case 4:
                        ret = "上星期三";
                        break;
                    case 5:
                        ret = "上星期四";
                        break;
                    case 6:
                        ret = "上星期五";
                        break;
                    case 7:
                        ret = "上星期六";
                        break;
                    default:
                        break;
                }
            }
        } else {
            distance = distance / (60 * 60 * 24);
            ret = distance + "天前";
        }
        return ret;
    }

    public static String getChineseMonth(int month) {
        switch (month) {
            case 1:
                return "一月";
            case 2:
                return "二月";
            case 3:
                return "三月";
            case 4:
                return "四月";
            case 5:
                return "五月";
            case 6:
                return "六月";
            case 7:
                return "七月";
            case 8:
                return "八月";
            case 9:
                return "九月";
            case 10:
                return "十月";
            case 11:
                return "十一月";
            case 12:
                return "十二月";
        }
        return "";
    }

    /**
     * 将指定时间与当前时间对比：<br>
     * 获取小时间隔
     *
     * @param time 指定时间，毫秒值。
     * @return 返回格式化后的字符串，如果时间无效，则返回""。
     */
    public static long formatBeforeTimeHour(long time) {
        long ret = 0;
        long currentTime = ModuleMgr.getAppMgr().getTime();
        long distance = (currentTime - time) / 1000;
        distance = distance / (60 * 60);
        ret = distance;
        return ret;
    }

    public static String millisecondToFormatStringThree(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ssZZZZZ");
        String strTime = "";

        try {
            strTime = sdf.format(new Date(time));
            if (strTime.charAt(strTime.length() - 3) != ':') {
                StringBuffer stringBuffer = new StringBuffer(strTime);
                stringBuffer.insert(stringBuffer.length() - 2, ":");
                return stringBuffer.toString();
            }
        } catch (Exception e) {
            PLogger.printThrowable(e);
        }
        return strTime;
    }

    /**
     * 获取当前年份
     */
    public static int getCurrentYear() {
        Time time = new Time();
        time.setToNow();
        return time.year;
    }

    /**
     * 获取服务器时间校验后的当前年份
     */
    public static int getInspectYear() {
        String data = formatServerData(ModuleMgr.getAppMgr().getTime());
        if (TextUtils.isEmpty(data)) {
            return getCurrentYear();
        }
        String[] datas = StringUtils.stringToArray(data, "-");
        return TypeConvertUtil.toInt(datas[0]);
    }

    /**
     * @return 获取当前年月日
     */
    public static String getCurrentData() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        long time = ModuleMgr.getAppMgr().getTime();
        if (time <= 0) {
            time = System.currentTimeMillis();
        }
        Date curDate = new Date(time);// 获取当前日期
        return formatter.format(curDate);
    }

    public static long dateDiff(String timeInterval, Date date1, Date date2) {
        try {
            if (timeInterval.equalsIgnoreCase("year")) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date1);
                int time = calendar.get(Calendar.YEAR);
                calendar.setTime(date2);
                return time - calendar.get(Calendar.YEAR);
            }

            if (timeInterval.equalsIgnoreCase("quarter")) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date1);
                int time = calendar.get(Calendar.YEAR) * 4;
                calendar.setTime(date2);
                time -= calendar.get(Calendar.YEAR) * 4;
                calendar.setTime(date1);
                time += calendar.get(Calendar.MONTH) / 4;
                calendar.setTime(date2);
                return time - calendar.get(Calendar.MONTH) / 4;
            }

            if (timeInterval.equalsIgnoreCase("month")) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date1);
                int time = calendar.get(Calendar.YEAR) * 12;
                calendar.setTime(date2);
                time -= calendar.get(Calendar.YEAR) * 12;
                calendar.setTime(date1);
                time += calendar.get(Calendar.MONTH);
                calendar.setTime(date2);
                return time - calendar.get(Calendar.MONTH);
            }

            if (timeInterval.equalsIgnoreCase("week")) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date1);
                int time = calendar.get(Calendar.YEAR) * 52;
                calendar.setTime(date2);
                time -= calendar.get(Calendar.YEAR) * 52;
                calendar.setTime(date1);
                time += calendar.get(Calendar.WEEK_OF_YEAR);
                calendar.setTime(date2);
                return time - calendar.get(Calendar.WEEK_OF_YEAR);
            }

            if (timeInterval.equalsIgnoreCase("day")) {
                long time = date1.getTime() / 1000 / 60 / 60 / 24;
                return time - date2.getTime() / 1000 / 60 / 60 / 24;
            }

            if (timeInterval.equalsIgnoreCase("hour")) {
                long time = date1.getTime() / 1000 / 60 / 60;
                return time - date2.getTime() / 1000 / 60 / 60;
            }

            if (timeInterval.equalsIgnoreCase("minute")) {
                long time = date1.getTime() / 1000 / 60;
                return time - date2.getTime() / 1000 / 60;
            }

            if (timeInterval.equalsIgnoreCase("second")) {
                long time = date1.getTime() / 1000;
                return time - date2.getTime() / 1000;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            return date1.getTime() - date2.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取当前本地时间秒
     *
     * @return
     */
    public static long getSecondTimeMil() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 获取当前年月日往后推几天的时间  2016年01月26日
     *
     * @param dayAddNum 推后时间
     * @return
     */
    public static String getRetardDateStr(int dayAddNum) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
        long time = ModuleMgr.getAppMgr().getTime();
        if (time <= 0) {
            time = System.currentTimeMillis();
        }
        long tmp = (dayAddNum * 24 * 60 * 60 * 1000) + time;
        Date curDate = new Date(tmp);// 获取当前日期往后推几天的日期
        return formatter.format(curDate);
    }

    /**
     * The enum Difference mode.
     */
    public enum DifferenceMode {
        /**
         * Second difference mode.
         */
        Second, /**
         * Minute difference mode.
         */
        Minute, /**
         * Hour difference mode.
         */
        Hour, /**
         * Day difference mode.
         */
        Day
    }

    /**
     * Calculate different second long.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return the long
     */
    public static long calculateDifferentSecond(Date startDate, Date endDate) {
        return calculateDifference(startDate, endDate, DifferenceMode.Second);
    }

    /**
     * Calculate different minute long.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return the long
     */
    public static long calculateDifferentMinute(Date startDate, Date endDate) {
        return calculateDifference(startDate, endDate, DifferenceMode.Minute);
    }

    /**
     * Calculate different hour long.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return the long
     */
    public static long calculateDifferentHour(Date startDate, Date endDate) {
        return calculateDifference(startDate, endDate, DifferenceMode.Hour);
    }

    /**
     * Calculate different day long.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return the long
     */
    public static long calculateDifferentDay(Date startDate, Date endDate) {
        return calculateDifference(startDate, endDate, DifferenceMode.Day);
    }

    /**
     * Calculate different second long.
     *
     * @param startTimeMillis the start time millis
     * @param endTimeMillis   the end time millis
     * @return the long
     */
    public static long calculateDifferentSecond(long startTimeMillis, long endTimeMillis) {
        return calculateDifference(startTimeMillis, endTimeMillis, DifferenceMode.Second);
    }

    /**
     * Calculate different minute long.
     *
     * @param startTimeMillis the start time millis
     * @param endTimeMillis   the end time millis
     * @return the long
     */
    public static long calculateDifferentMinute(long startTimeMillis, long endTimeMillis) {
        return calculateDifference(startTimeMillis, endTimeMillis, DifferenceMode.Minute);
    }

    /**
     * Calculate different hour long.
     *
     * @param startTimeMillis the start time millis
     * @param endTimeMillis   the end time millis
     * @return the long
     */
    public static long calculateDifferentHour(long startTimeMillis, long endTimeMillis) {
        return calculateDifference(startTimeMillis, endTimeMillis, DifferenceMode.Hour);
    }

    /**
     * Calculate different day long.
     *
     * @param startTimeMillis the start time millis
     * @param endTimeMillis   the end time millis
     * @return the long
     */
    public static long calculateDifferentDay(long startTimeMillis, long endTimeMillis) {
        return calculateDifference(startTimeMillis, endTimeMillis, DifferenceMode.Day);
    }

    /**
     * Calculate difference long.
     *
     * @param startTimeMillis the start time millis
     * @param endTimeMillis   the end time millis
     * @param mode            the mode
     * @return the long
     */
    public static long calculateDifference(long startTimeMillis, long endTimeMillis, DifferenceMode mode) {
        return calculateDifference(new Date(startTimeMillis), new Date(endTimeMillis), mode);
    }

    /**
     * Calculate difference long.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @param mode      the mode
     * @return the long
     */
    public static long calculateDifference(Date startDate, Date endDate, DifferenceMode mode) {
        long[] different = calculateDifference(startDate, endDate);
        if (mode.equals(DifferenceMode.Minute)) {
            return different[2];
        } else if (mode.equals(DifferenceMode.Hour)) {
            return different[1];
        } else if (mode.equals(DifferenceMode.Day)) {
            return different[0];
        } else {
            return different[3];
        }
    }

    /**
     * Calculate difference long [ ].
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return the long [ ]
     */
    public static long[] calculateDifference(Date startDate, Date endDate) {
        return calculateDifference(endDate.getTime() - startDate.getTime());
    }

    /**
     * Calculate difference long [ ].
     *
     * @param differentMilliSeconds the different milli seconds
     * @return the long [ ]
     */
    public static long[] calculateDifference(long differentMilliSeconds) {
        long secondsInMilli = 1000;//1s==1000ms
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long elapsedDays = differentMilliSeconds / daysInMilli;
        differentMilliSeconds = differentMilliSeconds % daysInMilli;
        long elapsedHours = differentMilliSeconds / hoursInMilli;
        differentMilliSeconds = differentMilliSeconds % hoursInMilli;
        long elapsedMinutes = differentMilliSeconds / minutesInMilli;
        differentMilliSeconds = differentMilliSeconds % minutesInMilli;
        long elapsedSeconds = differentMilliSeconds / secondsInMilli;
        return new long[]{elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds};
    }

    /**
     * Calculate days in month int.
     *
     * @param month the month
     * @return the int
     */
    public static int calculateDaysInMonth(int month) {
        return calculateDaysInMonth(0, month);
    }

    /**
     * Calculate days in month int.
     *
     * @param year  the year
     * @param month the month
     * @return the int
     */
    public static int calculateDaysInMonth(int year, int month) {
        // 添加大小月月份并将其转换为list,方便之后的判断
        String[] bigMonths = {"1", "3", "5", "7", "8", "10", "12"};
        String[] littleMonths = {"4", "6", "9", "11"};
        List<String> bigList = Arrays.asList(bigMonths);
        List<String> littleList = Arrays.asList(littleMonths);
        // 判断大小月及是否闰年,用来确定"日"的数据
        if (bigList.contains(String.valueOf(month))) {
            return 31;
        } else if (littleList.contains(String.valueOf(month))) {
            return 30;
        } else {
            if (year <= 0) {
                return 29;
            }
            // 是否闰年
            if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
                return 29;
            } else {
                return 28;
            }
        }
    }

    /**
     * 月日时分秒，0-9前补0
     *
     * @param number the number
     * @return the string
     */
    @NonNull
    public static String fillZero(int number) {
        return number < 10 ? "0" + number : "" + number;
    }

    /**
     * 功能：判断日期是否和当前date对象在同一天。
     * 参见：http://www.cnblogs.com/myzhijie/p/3330970.html
     *
     * @param date 比较的日期
     * @return boolean 如果在返回true，否则返回false。
     * @author 沙琪玛 QQ：862990787 Aug 21, 2013 7:15:53 AM
     */
    public static boolean isSameDay(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("date is null");
        }
        Calendar nowCalendar = Calendar.getInstance();
        Calendar newCalendar = Calendar.getInstance();
        newCalendar.setTime(date);
        return (nowCalendar.get(Calendar.ERA) == newCalendar.get(Calendar.ERA) &&
                nowCalendar.get(Calendar.YEAR) == newCalendar.get(Calendar.YEAR) &&
                nowCalendar.get(Calendar.DAY_OF_YEAR) == newCalendar.get(Calendar.DAY_OF_YEAR));
    }

    /**
     * 将yyyy-MM-dd HH:mm:ss字符串转换成日期<br/>
     *
     * @param dateStr    时间字符串
     * @param dataFormat 当前时间字符串的格式。
     * @return Date 日期 ,转换异常时返回null。
     */
    public static Date parseDate(String dateStr, String dataFormat) {
        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat dateFormat = new SimpleDateFormat(dataFormat);
            Date date = dateFormat.parse(dateStr);
            return new Date(date.getTime());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将yyyy-MM-dd HH:mm:ss字符串转换成日期<br/>
     *
     * @param dateStr yyyy-MM-dd HH:mm:ss字符串
     * @return Date 日期 ,转换异常时返回null。
     */
    public static Date parseDate(String dateStr) {
        return parseDate(dateStr, "yyyy-MM-dd HH:mm:ss");
    }

    // 判断当前时间是否在某个时间段之内
    public static boolean judgeTimeRange(int startHour, int startMinute, int endHour, int endMinute) {
        if (startHour < 0 || startHour > 24) {
            return false;
        }
        if (startMinute < 0 || startMinute >= 60) {
            return false;
        }
        if (endHour < 0 || endHour > 24) {
            return false;
        }
        if (endMinute < 0 || endMinute >= 60) {
            return false;
        }

        // 时间范围
        final int start = startHour * 60 + startMinute;// 起始时间
        final int end = endHour * 60 + endMinute;// 结束时间
        if (start >= end) {
            return false;
        }

        Calendar cal = Calendar.getInstance();// 当前日期
        int hour = cal.get(Calendar.HOUR_OF_DAY);// 获取小时
        int minute = cal.get(Calendar.MINUTE);// 获取分钟
        int minuteOfDay = hour * 60 + minute;// 当前时间
        if (minuteOfDay >= start && minuteOfDay <= end) {
            // System.out.println("在外围内");
            return true;
        } else {
            // System.out.println("在外围外");
            return false;
        }
    }

    /**
     * 获取当前时间戳，以秒为单位
     */
    public static String getCurrentTimeMil() {
        return System.currentTimeMillis() / 1000 + "";
    }

    // 返回毫秒
    public static long getTimeInMillis() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis();
    }

    // long转时间
    public static String getLongToTimeEx(Long l) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(l * 1000);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(calendar.getTime());
    }

    /**
     * 返回 分钟:秒格 这种格式的文本
     */
    public static String getLongToMinuteTime(Long l) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(l);
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        return format.format(calendar.getTime());
    }

    public static String getData() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前日期
        return formatter.format(curDate);
    }

    public static String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        return formatter.format(curDate);
    }

    public static String getFormatTimeChatTip(long time) {
        String ret = "";

        try {
            SimpleDateFormat e = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            ret = e.format(new Date(time));
        } catch (Exception e) {
            PLogger.printObject(e);
        }

        return ret;
    }

    /**
     * 获取年月
     * @return
     */
    public static String getCurrentDataYYYYMM(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月");
        return formatter.format(date);
    }


    /**
     * 获取当前小时 只有小时
     * @return
     */
    public static String getCurrentDataHH() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH");
        long time = ModuleMgr.getAppMgr().getTime();
        if (time <= 0) {
            time = System.currentTimeMillis();
        }
        Date curDate = new Date(time);// 获取当前日期
        return formatter.format(curDate);
    }

    /**
     * 获取上一个几点
     * @return
     */
    public static long getCurrentDataPoints(int hh) {
        int tmpHH = TypeConvertUtil.toInt(TimeUtil.getCurrentDataHH());
        long currentTime;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ModuleMgr.getAppMgr().getTime());
        calendar.set(Calendar.HOUR_OF_DAY, hh);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        currentTime = calendar.getTimeInMillis();
        if (tmpHH < hh) {
            currentTime -= (24 * Constant.HOUR_TIME);
        }
        currentTime = currentTime / 1000;
        return currentTime;
    }

    public static String formatTimeLong(Long l) {
        DecimalFormat f = new DecimalFormat();
        f.applyPattern("00");
        String result = f.format(l / 60 % 60) + ':' + f.format(l % 60);
        if (l >= 3600)
            result = f.format(l / 3600) + ':' + result;
        return result;
    }
}