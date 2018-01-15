package com.juxin.library.utils;

import android.text.TextUtils;
import com.juxin.library.log.PLogger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * @author JohnsonLi
 * @version 1.0
 * @qq 505214658
 * @date 2015-04-18
 */
public class TimeBaseUtil {

    public static final int SEC = 1000;
    public static final int MIN = 1000 * 60;

    /**
     * 将一个毫秒值，转成一个yyyy-MM-dd'T'HH:mm:ssZ格式的时间字符串。
     *
     * @param time 毫秒值。
     * @return 格式化后的字符串。
     */
    public static String millisecondToFormatString(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        String strTime = "";

        try {
            strTime = sdf.format(new Date(time));
        } catch (Exception e) {
            PLogger.printThrowable(e);
        }

        return strTime;
    }

    /**
     * 将一个毫秒值，转成一个yyyy-MM-dd格式的时间字符串。
     *
     * @param time 毫秒值。
     * @return 格式化后的字符串。
     */
    public static String millisecondToFormatStringSecond(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strTime = "";

        try {
            strTime = sdf.format(new Date(time));
        } catch (Exception e) {
            PLogger.printThrowable(e);
        }

        return strTime;
    }

    /**
     * 通过传入年月日时分秒，得到"yyyy-MM-dd'T'HH:mm:ssZZZZZ"格式时间
     *
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     * @param second
     * @return
     */
    public static String singleTimeToFormatString(int year, int month, int day, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute, second);
        Date d = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");
        String strTime = "";
        strTime = sdf.format(d);
        return strTime;
    }

    /**
     * 将一个yyyy-MM-dd'T'HH:mm:ssZ格式的时间字符串，转成对应的毫秒值。
     *
     * @param date 格式化字符串。
     * @return 毫秒值。
     */
    public static long stringTDateToMillisecond(String date) {
        // 兼容
        String tempDate = date;
        int index = tempDate.indexOf(".");

        if (index != -1 && tempDate.length() >= 25) {
            date = tempDate.substring(0, index);
            date += tempDate.substring(tempDate.length() - 6);
        }
        // 兼容 End

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        long rand = 0;

        try {
            Date temp = sdf.parse(date);
            rand = temp.getTime();
            return rand;
        } catch (Exception e) {
            PLogger.printThrowable(e);
        }

        return rand;
    }


    /**
     * 转换成时间对象
     *
     * @param date
     * @return
     */
    public static Calendar stringTDateToCalendar(String date) {
        long millisecond = stringTDateToMillisecond(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millisecond);
        return calendar;
    }

    /**
     * 获取一个时间拼成的字符串，做为文件名。
     *
     * @return 格式：20140316_183400_000的字符串。
     */
    public static String getFormatTimeFileName() {
        String fileName = "20140316_183400_000";

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
            fileName = sdf.format(new Date());
        } catch (Exception e) {
            PLogger.printThrowable(e);
        }

        return fileName;
    }

    /**
     * 获取一个时间拼成的字符串，做为文件名。
     *
     * @return 格式：20140316_1834的字符串。
     */
    public static String getSimpleFormatTimeFileName() {
        String fileName = "20140316_1834";

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmm");
            fileName = sdf.format(new Date());
        } catch (Exception e) {
            PLogger.printThrowable(e);
        }

        return fileName;
    }

    public static String getFormatTimeChatTip(long time) {
        String ret = "";

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            ret = sdf.format(new Date(time));
        } catch (Exception e) {
            PLogger.printThrowable(e);
        }

        return ret;
    }

    /**
     * @param mss 要转换的毫秒数
     * @return 该毫秒数转换为 hours * minutes * seconds 后的格式
     */
    public static String formatDuring(long mss) {
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        return hours + ":" + minutes + ":" + seconds;
    }

    /**
     * @param begin 时间段的开始
     * @param end   时间段的结束
     * @return 输入的两个Date类型数据之间的时间间格用* days * hours * minutes * seconds的格式展示
     * @author fy.zhang
     */
    public static String formatDuring(Date begin, Date end) {
        return formatDuring(end.getTime() - begin.getTime());
    }

    /**
     * 将"yyyy-MM-dd'T'HH:mm:ssZ"转化为
     * "yyyy-MM-dd HH:mm:ss"格式的字符串
     *
     * @param formalTime
     * @return
     */
    public static String getSimpleTimeFromFormalTime(String formalTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date d;
        try {
            d = sdf.parse(formalTime);
            SimpleDateFormat nsdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return nsdf.format(d);
        } catch (ParseException e) {
            return "";
        }
    }

    /**
     * 将"yyyy-MM-dd'T'HH:mm:ssZ"转化为
     * "yyyy-MM-dd"格式的字符串
     *
     * @param formalTime
     * @return
     */
    public static String getSimpleTimeFromFormalTime2(String formalTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date d;
        try {
            d = sdf.parse(formalTime);
            SimpleDateFormat nsdf = new SimpleDateFormat("yyyy-MM-dd");
            return nsdf.format(d);
        } catch (ParseException e) {
            return "";
        }
    }

    /**
     * 将"yyyy-MM-dd'T'HH:mm:ssZ"转化为
     * "MM-dd"格式的字符串
     *
     * @param formalTime
     * @return
     */
    public static String getSimpleTimeMonthAndDay(String formalTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date d;
        try {
            d = sdf.parse(formalTime);
            SimpleDateFormat nsdf = new SimpleDateFormat("M月dd日");
            return nsdf.format(d);
        } catch (ParseException e) {
            return "";
        }
    }

    /**
     * 将"yyyy-MM-dd'T'HH:mm:ssZ"转化为
     * "HH:mm:ss"格式的字符串
     *
     * @param formalTime
     * @return
     */
    public static String getSimpleHourAndMin(String formalTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date d;
        try {
            d = sdf.parse(formalTime);
            SimpleDateFormat nsdf = new SimpleDateFormat("HH点mm分");
            return nsdf.format(d);
        } catch (ParseException e) {
            return "";
        }
    }

    /**
     * 传入年月日，返回"yyyy-MM-dd"
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static String timeToSimpleFormatString(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        Date d = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strTime = "";
        strTime = sdf.format(d);
        return strTime;
    }

    /**
     * 格式化秒钟为天时分秒
     *
     * @param seconds
     */
    public static String formatSecondsToDate(int seconds) {
        int oneDayToSeconds = 86400;
        int oneHourToSeconds = 3600;
        int oneMinuteToSeconds = 60;
        int day = seconds / oneDayToSeconds;
        seconds = seconds - day * oneDayToSeconds;
        int hour = seconds / oneHourToSeconds;
        seconds = seconds - hour * oneHourToSeconds;
        int minute = seconds / oneMinuteToSeconds;
        seconds = seconds - minute * oneMinuteToSeconds;
        StringBuffer str = new StringBuffer();
        if (day != 0) {
            str.append(day + " 天 ");
        }
        if (hour != 0) {
            str.append(hour + " 小时 ");
        }
        if (minute != 0) {
            str.append(minute + " 分钟 ");
        }
        if (seconds != 0) {
            str.append(seconds + " 秒 ");
        }
        return str.toString();
    }

    /**
     * 格式化秒钟为天时分
     * 封禁弹框时间
     *
     * @param seconds
     */
    public static String formatSecondsToDate3(int seconds) {
        int tempS = seconds;
        int oneDayToSeconds = 86400;
        int oneHourToSeconds = 3600;
        int oneMinuteToSeconds = 60;
        int day = seconds / oneDayToSeconds;
        seconds = seconds - day * oneDayToSeconds;
        int hour = seconds / oneHourToSeconds;
        seconds = seconds - hour * oneHourToSeconds;
        int minute = seconds / oneMinuteToSeconds;
        StringBuffer str = new StringBuffer();
        if (day != 0) {
            str.append(day + "天");
        }
        if (hour != 0) {
            str.append(hour + "小时");
        }
        if (minute != 0) {
            str.append(minute + "分钟");
        }
        if (tempS < 60) {
            str.append("1分钟");
        }
        return str.toString();
    }

    /**
     * 格式化秒钟为天时分秒
     *
     * @param seconds
     */
    public static String formatSecondsToDate2(int seconds, int times[]) {
        int oneDayToSeconds = 86400;
        int oneHourToSeconds = 3600;
        int oneMinuteToSeconds = 60;
        int day = seconds / oneDayToSeconds;
        seconds = seconds - day * oneDayToSeconds;
        int hour = seconds / oneHourToSeconds;
        seconds = seconds - hour * oneHourToSeconds;
        int minute = seconds / oneMinuteToSeconds;
        seconds = seconds - minute * oneMinuteToSeconds;
        StringBuffer str = new StringBuffer();
        if (day != 0) {
            str.append(day + " : ");
        }
        if (hour != 0) {
            str.append(hour + " : ");
        }
        if (minute != 0) {
            str.append(minute + " 分 ");
        }
        if (seconds != 0) {
            str.append(seconds + "秒");
        }

        if (times != null) {
            times[0] = day;
            times[1] = hour;
            times[2] = minute;
            times[3] = seconds;
        }
        return str.toString();
    }


    /**
     * 格式化秒钟为天时分秒
     *
     * @param seconds
     */
    public static HashMap<String, Integer> formatSecondsToTimeMap(int seconds) {
        HashMap<String, Integer> tmMap = new HashMap<String, Integer>();
        int oneDayToSeconds = 86400;
        int oneHourToSeconds = 3600;
        int oneMinuteToSeconds = 60;
        int day = seconds / oneDayToSeconds;
        seconds = seconds - day * oneDayToSeconds;
        int hour = seconds / oneHourToSeconds;
        seconds = seconds - hour * oneHourToSeconds;
        int minute = seconds / oneMinuteToSeconds;
        seconds = seconds - minute * oneMinuteToSeconds;
        if (day != 0) {
            tmMap.put("dd", day);
        }
        if (hour != 0) {
            tmMap.put("HH", hour);
        }
        if (minute != 0) {
            tmMap.put("mm", minute);
        }
        if (seconds != 0) {
            tmMap.put("ss", seconds);
        }
        return tmMap;
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

    public static String getStatGameFormat(String time) {
        long ltime = stringTDateToMillisecond(time);
        String ret = "";

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH点mm分ss秒 开始游戏");
            ret = sdf.format(new Date(ltime));
        } catch (Exception e) {
            PLogger.printThrowable(e);
        }

        return ret;
    }

    /**
     * 获取时长
     *
     * @param duration 毫秒
     * @return 02:06
     */
    public static String getDuration(String duration) {
        if (TextUtils.isEmpty(duration)) return "0";
        long seconds = Long.parseLong(duration);
        StringBuffer sb = new StringBuffer();
        if (seconds > (MIN * 60)) {
            long hour = seconds / (MIN * 60);
            seconds = seconds - hour * MIN * 60;
            sb.append(hour);
            sb.append(":");
        }
        if (seconds > MIN) {
            long min = seconds / MIN;
            seconds = seconds - min * MIN;
            sb.append(min);
            sb.append("\':");
        }
        if (seconds > SEC) {
            sb.append(seconds / SEC + "\"");
        }
        return sb.toString();
    }

    /**
     * 倒计时
     *
     * @param seconds 秒
     */
    public static String countDownTime(long seconds) {
        if (seconds <= 0) return "0天";
        long tempS = seconds;
        int oneDayToSeconds = 86400;
        int oneHourToSeconds = 3600;
        int oneMinuteToSeconds = 60;
        long day = seconds / oneDayToSeconds;
        seconds = seconds - day * oneDayToSeconds;
        long hour = seconds / oneHourToSeconds;
        seconds = seconds - hour * oneHourToSeconds;
        long minute = seconds / oneMinuteToSeconds;
        StringBuffer str = new StringBuffer();
        if (day != 0) {
            str.append(day + "天");
            return str.toString();
        }
        if (hour != 0) {
            str.append(hour + "小时");
            return str.toString();
        }
        if (minute != 0) {
            str.append(minute + "分钟");
            return str.toString();
        }
        if (tempS < 60) {
            str.append("1分钟");
            return str.toString();
        }
        return str.toString();
    }
}
