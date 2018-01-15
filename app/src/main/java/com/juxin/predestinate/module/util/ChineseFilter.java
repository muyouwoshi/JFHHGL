package com.juxin.predestinate.module.util;

import android.content.Context;
import android.text.ClipboardManager;
import android.text.TextUtils;

import com.juxin.library.log.PToast;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ChineseFilter {

    /**
     * 将中文字符串转换为Unicode编码的字符串</p>
     * 注意：此方法比较耗时，大字符串的转换耗时在6s以上，需要在workThread进行处理
     *
     * @param string 中文字符串
     * @return 将中文字符串转换为Unicode编码的字符串
     */
    public static String toUnicode(final String string) {
        char[] utfBytes = string.toCharArray();
        String unicodeBytes = "";
        for (char utfByte : utfBytes) {
            String hexB = Integer.toHexString(utfByte);
            String prefix = "";
            for (int i = 0; i < 4 - hexB.length(); i++) {
                prefix += "0";
            }
            unicodeBytes += "\\u" + (prefix + hexB);
        }
        return unicodeBytes;
    }

    /**
     * 判断是否为中文字符
     *
     * @param c 传入的char
     * @return 是否为中文字符
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);

        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }

        return false;
    }

    public static String filter(String str) {
        String reg = "[\u4e00-\u9fa5]";
        Pattern pat = Pattern.compile(reg);
        Matcher mat = pat.matcher(str);
        String repickStr = mat.replaceAll("");
        return repickStr;
    }

    /**
     * 判断是否为手机号码
     */
    public static boolean isPhoneNumber(String mobiles) {
        // 所有的客户端手机号码判断都只判断1开头11位数字，其他格式由服务器判断，以防止运营商增加新的号段
        Pattern p = Pattern.compile("^1\\d{10}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 检查Email的格式
     */
    public static boolean checkEmailFormat(String email) {
        String check = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(email);
        return matcher.matches();
    }

    /**
     * 判断是否为QQ号码
     *
     * @auther Kyle.DY
     * @date 2016-1-26 11:53
     */
    public static boolean isQQNumber(String qq) {
        Pattern p = Pattern.compile("^[1-9][0-9]{5,12}$");
        Matcher m = p.matcher(qq);
        return m.matches();
    }

    /**
     * 判断是否为合法微信号码
     *
     * @param wechat
     * @return
     * @auther Kyle.DY
     * @date 2016-1-28 14:29
     */
    public static boolean isWeChatNumber(String wechat) {
        Pattern p = Pattern.compile("^[a-zA-Z0-9_]{5,25}$");
        Matcher m = p.matcher(wechat);
        return m.matches();
    }

    /**
     * 过滤输入的中文字符
     *
     * @param str
     * @return
     * @throws PatternSyntaxException
     */
    public static String chineseFilter(String str) throws PatternSyntaxException {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        return m.replaceAll("");
    }

    /**
     * 过滤输入的特殊字符
     *
     * @param str
     * @return
     * @throws PatternSyntaxException
     */
    public static String stringFilter(String str) throws PatternSyntaxException {
        String regEx = "[/\\:*?<>|\"\n\t]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("");
    }

    /**
     * 使用正则表达式去掉字符串末尾多余的.与0
     */
    public static String subZeroString(String s) {
        if (!TextUtils.isEmpty(s) && s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }

    /**
     * 对double类型的数值保留指定位数的小数: 就进舍入
     *
     * @param value ： 需格式化的数字
     * @param digit : 小数点后保留的位数
     */
    public static Double formatNum(double value, int digit) {
        BigDecimal bg = new BigDecimal(value);
        return bg.setScale(digit, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 将JSONObject转换成Map<String, Object>
     *
     * @param jsonObject JSONObject对象
     * @return Map对象
     */
    public static Map<String, Object> JSONObjectToMap(JSONObject jsonObject) {
        Map<String, Object> result = new HashMap<>();
        if (jsonObject == null) return result;

        Iterator<String> iterator = jsonObject.keys();
        String key = null;
        Object value = null;
        while (iterator.hasNext()) {
            key = iterator.next();
            value = jsonObject.opt(key);
            result.put(key, value);
        }
        return result;
    }

    /**
     * 复制字符串到剪贴板，并提示：已复制到剪贴板
     *
     * @param copy_string 需要赋值的文字
     */
    public static void copyString(Context context, String copy_string) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setText(copy_string);
        PToast.showShort("已复制到剪贴板");
    }
}
