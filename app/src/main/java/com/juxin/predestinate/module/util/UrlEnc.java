package com.juxin.predestinate.module.util;

import android.text.TextUtils;

import com.juxin.library.utils.EncryptUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 加密
 *
 * @author Kind
 */
public class UrlEnc {

    /**
     * 对请求参数进行hash拼接
     *
     * @param getParams  get参数
     * @param postParams post参数
     * @return 加密之后的数值
     */
    private static String getHashMethod(Map<String, Object> getParams, Map<String, Object> postParams) {
        StringBuilder builder = new StringBuilder("yuafenba(&^%_=>yuafenba|");
        String[] getKeys = null;
        String[] postKeys = null;
        if (getParams != null) {
            getKeys = new String[getParams.size()];
            int index = 0;
            for (Entry<String, Object> entry : getParams.entrySet()) {
                getKeys[index] = entry.getKey();
                index++;
            }
            Arrays.sort(getKeys, String.CASE_INSENSITIVE_ORDER);
            for (int i = 0; i < getKeys.length; i++) {
                String value = String.valueOf(getParams.get(getKeys[i]));
                builder.append(value);
                builder.append("|");
            }
        }

        if (postParams != null) {
            postKeys = new String[postParams.size()];
            int index = 0;
            for (Entry<String, Object> entry : postParams.entrySet()) {
                postKeys[index] = entry.getKey();
                index++;
            }
            Arrays.sort(postKeys, String.CASE_INSENSITIVE_ORDER);
            for (int i = 0; i < postKeys.length; i++) {
                String value = String.valueOf(postParams.get(postKeys[i]));
                builder.append(value);
                if (i != postParams.size() - 1) {
                    builder.append("|");
                }
            }
        }
        return EncryptUtil.md5(builder.toString());
    }

    /**
     * 拼接请求url
     *
     * @param url        请求url
     * @param getParams  get参数
     * @param postParams post参数
     * @return 加密之后的请求url
     */
    public static String appendUrl(String url, Map<String, Object> getParams, Map<String, Object> postParams, boolean isDes) {

        getParams = getSplitMap(url, getParams);
        getParams.put("ts", TimeUtil.getCurrentTimeMil());
        // 服务器要求用C++代码加密的参数，此处的hash值只对ts进行hash算法  2016/12/19
        getParams.put("hash", isDes ? UrlEnc.getHashMethod(getParams, null) : UrlEnc.getHashMethod(getParams, postParams));

        url = url.split("\\?")[0];
        StringBuilder builder = new StringBuilder(url);
        builder.append(url.contains("?") ? "&" : "?");
        boolean flag = false;
        for (Entry<String, Object> entry : getParams.entrySet()) {
            if (flag) builder.append("&");
            builder.append(entry.getKey());
            builder.append("=");
            builder.append(entry.getValue());
            flag = true;
        }
        return builder.toString();
    }

    /**
     * 切割出请求网址中包含的get参数
     *
     * @param url       需要切割的url
     * @param getParams 其他需要拼接的get参数
     * @return 切割完成的参数map
     */
    private static Map<String, Object> getSplitMap(String url, Map<String, Object> getParams) {
        Map<String, Object> splitMap = new HashMap<>();
        if (getParams != null && !getParams.isEmpty()) splitMap.putAll(getParams);
        if (TextUtils.isEmpty(url)) return splitMap;

        try {
            String[] paramArray = url.split("\\?");
            if (paramArray.length > 1) {
                String[] kvArray = paramArray[1].split("&");
                String[] param = null;
                for (String kv : kvArray) {
                    param = kv.split("=");
                    splitMap.put(param[0], param.length > 1 ? param[1] : "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return splitMap;
    }
}