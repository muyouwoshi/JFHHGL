package com.juxin.library.utils;

/**
 * 类型转换。
 *
 * @author JohnsonLi
 * @version 1.0
 * @qq 505214658
 * @date 2015-06-25
 */
public class TypeConvertUtil {

    /**
     * {@link #toString(int, String)}
     */
    public static String toString(int value) {
        return toString(value, "");
    }

    /**
     * 将int转成String。
     *
     * @param value    传入值。
     * @param defValue 默认值。
     * @return 如果转换失败，返回defValue。
     */
    public static String toString(int value, String defValue) {
        try {
            return String.valueOf(value);
        } catch (Exception e) {
        }
        return defValue;
    }

    /**
     * {@link #toInt(String, int)}
     *
     * @param value
     */
    public static int toInt(String value) {
        return toInt(value, 0);
    }

    /**
     * 把String转成int。
     *
     * @param value    字符串值。
     * @param defValue 默认值。
     * @return 如果转换失败，返回defValue。
     */
    public static int toInt(String value, int defValue) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
        }
        return defValue;
    }

    /**
     * {@link #toLong(String, long)}
     */
    public static long toLong(String value) {
        return toLong(value, 0);
    }

    /**
     * 把String转成long。
     *
     * @param value    字符串值。
     * @param defValue 默认值。
     * @return 如果转换失败，返回defValue。
     */
    public static long toLong(String value, long defValue) {
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
        }
        return defValue;
    }

    /**
     * 爵位等级转一至五
     * @param id 爵位等级 1-50
     */
    public static String titleLv2Str(int id) {
        String str = "一星";
        if(id <= 0) return str;
        switch (id % 5) {
            case 0:
                str = "五星";
                break;
            case 1:
                str = "一星";
                break;
            case 2:
                str = "二星";
                break;
            case 3:
                str = "三星";
                break;
            case 4:
                str = "四星";
                break;
        }
        return str;
    }
}
