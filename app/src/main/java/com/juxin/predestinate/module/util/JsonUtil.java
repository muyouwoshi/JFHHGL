package com.juxin.predestinate.module.util;

import android.text.TextUtils;

import com.juxin.library.log.PLogger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * json操作工具类
 * Created by ZRP on 2017/3/24.
 */
public final class JsonUtil {

    /**
     * 将字符串解析成JsonObject
     *
     * @param str json字符串。
     * @return JSONObject实例。
     */
    public static JSONObject getJsonObject(String str) {
        try {
            if (!TextUtils.isEmpty(str)) return new JSONObject(str);
        } catch (JSONException e) {
            PLogger.printThrowable(e);
        }
        return new JSONObject();
    }

    /**
     * 将字符串解析成JsonObject数组。
     *
     * @param str json数组字符串。
     * @return JSONArray实例。
     */
    public static JSONArray getJsonArray(String str) {
        try {
            return new JSONArray(str);
        } catch (JSONException e) {
            PLogger.printThrowable(e);
        }
        return new JSONArray();
    }

    public static String mapToJSONString(Map<String, Object> map) {
        JSONObject json = mapToJSON(map);
        if (json == null) return "";
        return json.toString();
    }

    public static JSONObject mapToJSON(Map<String, Object> map) {
        if (map == null) return null;

        JSONObject json = new JSONObject();
        try {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                if (key == null) continue;
                json.put(key, wrap(entry.getValue()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static JSONArray arrayToJSONArray(Collection array) {
        if (array == null) return null;

        JSONArray jsonArray = new JSONArray();
        for (Object anArray : array) jsonArray.put(wrap(anArray));
        return jsonArray;
    }

    public static JSONArray arrayToJSONArray(Object array) {
        if (array == null || !array.getClass().isArray()) return null;

        JSONArray jsonArray = new JSONArray();
        final int length = Array.getLength(array);
        for (int i = 0; i < length; ++i) {
            jsonArray.put(wrap(Array.get(array, i)));
        }
        return jsonArray;
    }

    public static Object wrap(Object o) {
        if (o == null) return null;

        if (o instanceof JSONArray || o instanceof JSONObject) {
            return o;
        }
        if (o.equals(null)) {
            return o;
        }
        try {
            if (o instanceof Collection) {
                return arrayToJSONArray((Collection) o);
            } else if (o.getClass().isArray()) {
                return arrayToJSONArray(o);
            }
            if (o instanceof Map) {
                return mapToJSON((Map) o);
            }
            if (o instanceof Boolean ||
                    o instanceof Byte ||
                    o instanceof Character ||
                    o instanceof Double ||
                    o instanceof Float ||
                    o instanceof Integer ||
                    o instanceof Long ||
                    o instanceof Short ||
                    o instanceof String) {
                return o;
            }

            if (o.getClass().getPackage().getName().startsWith("java.")) {
                return o.toString();
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
