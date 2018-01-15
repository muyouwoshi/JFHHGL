package com.juxin.predestinate.module.util.my;

import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.util.JsonUtil;

import org.json.JSONObject;

/**
 * json操作工具类
 * Created by ZRP on 2017/3/24.
 */
public final class ResultJsonUtil {

    /**
     * 将字符串解析成JsonObject
     *
     * @param str json字符串。
     * @return JSONObject实例。
     */
    public static void handleJson(HttpResponse response) {
        JSONObject jsonObject = JsonUtil.getJsonObject(response.getResponseString());
        if ("ok".equalsIgnoreCase(jsonObject.optString("status")))
            response.setOK();
        else
            response.setError();
    }
}
