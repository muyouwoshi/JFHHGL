package com.juxin.predestinate.module.util;

import android.text.TextUtils;

import com.juxin.library.utils.FileUtil;
import com.juxin.predestinate.module.logic.application.App;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * html及url拼接工具类
 * Created by ZRP on 2017/5/3.
 */
public class WebUtil {

    /* url正则匹配字符串 */
    private static final String URL_PATTERN = "^((https|http)?:\\/\\/)[^\\s]+";

    /**
     * 判断字符串是否为url
     *
     * @param input 需要判断的字符串
     * @return 是否是一个正确格式的url字符串
     */
    public static boolean isUrl(String input) {
        return !TextUtils.isEmpty(input) && Pattern.matches(URL_PATTERN, input);
    }

    /**
     * 拼接url参数，控制刷新时间及清晰度
     *
     * @param url 需要拼接的url
     * @return 拼接完成的url
     */
    public static String jointUrl(String url) {
        url = url.contains("?") ?  url + "&" : url + "?";
        return url + "resolution=" + (PerformanceHelper.isHighPerformance(App.context) ? "2" : "1")
                + "&v=" + System.currentTimeMillis();
    }

    /**
     * 拼接带参数的url
     *
     * @param url    需要拼接的url
     * @param params 请求参数
     * @return 拼接完成的url
     */
    public static String jointUrl(String url, HashMap<String, Object> params) {
        String joint = jointUrl(url);
        if (params == null || params.isEmpty()) {
            return joint;
        }
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            joint += "&" + entry.getKey() + "=" + String.valueOf(entry.getValue());
        }
        return joint;
    }

    /**
     * 获取本地路径纠错之后的最终路径
     *
     * @param webRoot   在线h5根目录，备份使用
     * @param localRoot 本地的h5文件存放根路径
     * @param path      h5文件的加载路径
     * @return 本地路径纠错之后的最终路径
     */
    public static String getHtmlPath(String webRoot, String localRoot, String path) {
        if (localRoot == null || path == null) {
            return "";
        }
        String localPath = localRoot + path;
        return FileUtil.isExist(localPath.replace("file://", "")) ? localPath : (webRoot + path);
    }
}
