package com.juxin.library.utils;

import android.text.TextUtils;

/**
 * 字符串工具类
 * <p/>
 * 目前包括：
 * 1:  由标志位截取字符串，
 * 2:  字符串按分隔符转换为数组
 * 3:  去除字符串中空格
 * <p/>
 * 如有特殊需求请自行添加
 * <p/>
 * Created by Su on 2016/7/4.
 */
public final class StringUtils {

    public static final String EMPTY = "";      // 默认空值字符串

    private static char sHexDigits[] = {'0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String hexString(byte[] source) {
        if (source == null || source.length <= 0) {
            return "";
        }

        final int size = source.length;
        final char str[] = new char[size * 2];
        int index = 0;
        byte b;
        for (int i = 0; i < size; i++) {
            b = source[i];
            str[index++] = sHexDigits[b >>> 4 & 0xf];
            str[index++] = sHexDigits[b & 0xf];
        }
        return new String(str);
    }

    /**
     * 截取并保留标志位之前的字符串, 不包含标志位
     *
     * @param str  需截取字符串
     * @param expr 标志位
     * @return
     */
    public static String getBeforeNoFlag(String str, String expr) {
        return substringBefore(str, expr, false);
    }

    /**
     * 截取并保留标志位之前的字符串, 包含标志位
     *
     * @param str  需截取字符串
     * @param expr 标志位
     * @return
     */
    public static String getBeforeWithFlag(String str, String expr) {
        return substringBefore(str, expr, true);
    }

    /**
     * 截取并保留标志位之前的字符串
     *
     * @param isReserve 截取字符串中是否包括标志位： true 包括 ， false 不包括
     */
    private static String substringBefore(String str, String expr, boolean isReserve) {
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(expr)) {
            return str;
        }

        int pos = str.indexOf(expr);
        if (pos == -1) {
            return str;
        }

        if (isReserve) {
            return str.substring(0, pos + expr.length());
        }

        return str.substring(0, pos);
    }

    /**
     * 截取标志位之后的字符串, 不保留标志位
     *
     * @param str  字符串
     * @param expr 标志位
     * @return
     */
    public static String getAfterNoFlag(String str, String expr) {
        return substringAfter(str, expr, false);
    }


    /**
     * 截取标志位之后的字符串,保留标志位
     *
     * @param str  字符串
     * @param expr 标志位
     * @return
     */
    public static String getAfterWithFlag(String str, String expr) {
        return substringAfter(str, expr, true);
    }

    /**
     * 截取并保留标志位之后的字符串
     *
     * @param isReserve 截取字符串中是否包括标志位： true 包括 ， false 不包括
     */
    private static String substringAfter(String str, String expr, boolean isReserve) {
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(expr)) {
            return str;
        }

        int pos = str.indexOf(expr);
        if (pos == -1) {
            return EMPTY;
        }
        if (isReserve) {
            return str.substring(pos);
        }
        return str.substring(pos + expr.length());
    }

    /**
     * 截取并保留最后一个标志位之前的字符串： 不包括标志位
     *
     * @param str  字符串
     * @param expr 标志位
     * @return
     */
    public static String getBeforeLastNoFlag(String str, String expr) {
        return substringBeforeLast(str, expr, false);
    }

    /**
     * 截取并保留最后一个标志位之前的字符串： 包括标志位
     *
     * @param str  字符串
     * @param expr 标志位
     * @return
     */
    public static String getBeforeLastWithFlag(String str, String expr) {
        return substringBeforeLast(str, expr, true);
    }

    /**
     * 截取并保留最后一个标志位之前的字符串
     *
     * @param isReserve 截取字符串中是否包括标志位： true 包括 ， false 不包括
     * @return
     */
    private static String substringBeforeLast(String str, String expr, boolean isReserve) {
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(expr)) {
            return str;
        }
        int pos = str.lastIndexOf(expr);
        if (pos == -1) {
            return str;
        }
        if (isReserve) {
            return str.substring(0, pos + expr.length());
        }

        return str.substring(0, pos);
    }

    /**
     * 截取并保留最后一个标志位之后的字符串： 不包括标志位
     *
     * @param str
     * @param expr 标志位
     * @return
     */
    public static String getAfterLastNoFlag(String str, String expr) {
        return substringAfterLast(str, expr, false);
    }

    /**
     * 截取并保留最后一个标志位之后的字符串： 包括标志位
     *
     * @param str
     * @param expr 标志位
     * @return
     */
    public static String getAfterLastWithFlag(String str, String expr) {
        return substringAfterLast(str, expr, true);
    }

    /**
     * 截取并保留最后一个标志位之后的字符串
     *
     * @param isReserve 截取字符串中是否包括标志位： true 包括 ， false 不包括
     * @return
     */
    private static String substringAfterLast(String str, String expr, boolean isReserve) {
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(expr)) {
            return str;
        }

        int pos = str.lastIndexOf(expr);
        if (pos == -1 || pos == (str.length() - expr.length())) {
            return EMPTY;
        }
        if (isReserve) {
            return str.substring(pos);
        }
        return str.substring(pos + expr.length());
    }

    /**
     * 把字符串按分隔符转换为数组
     *
     * @param string 字符串
     * @param expr   分隔符
     * @return 切割完的数组
     */
    public static String[] stringToArray(String string, String expr) {
        return string.split(expr);
    }

    /**
     * 去除字符串中的空格
     */
    public static String noSpace(String str) {
        str = str.trim();
        str = str.replace(" ", "");
        return str;
    }

    /**
     * 截取语音
     *
     * @param url
     * @return
     */
    public static String subStringAmr(String url) {
        return url.contains(".amr") ? (url.replace(".amr", "")) : url;
    }

    /**
     * 拼接
     *
     * @param url
     * @return
     */
    public static String spliceStringAmr(String url) {
        return url.contains(".amr") ? url : (url + ".amr");
    }

    /**
     * 添加 http 前缀
     * @param url 无 http 前缀的 url
     * @return 添加http 前缀
     */
    public static String addHttpPrefix(String url) {
        return "http://" + url;
    }
}
