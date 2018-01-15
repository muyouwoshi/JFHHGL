package com.juxin.predestinate.bean.net;

import android.text.TextUtils;

import com.juxin.library.log.PLogger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 基本数据类型接口，所有需要网络请求的数据类型均需要实现此接口。<br>
 * 网络请求实现会在请求完成时，调用parseJson。
 */
public abstract class BaseData {

    public abstract void parseJson(final String jsonStr);

    /**
     * 将字符串解析成JsonObject
     *
     * @param str json字符串。
     * @return JSONObject实例。
     */
    public JSONObject getJsonObject(String str) {
        try {
            if (!TextUtils.isEmpty(str)) {
                return new JSONObject(str);
            }
        } catch (JSONException e) {
            PLogger.printThrowable(e);
        }
        return new JSONObject();
    }

    /**
     * 将字符串解析成JsonObject  重点：解res目录下的json
     *
     * @param str json字符串
     * @return res节点json对象
     */
    public JSONObject getJsonObjectRes(String str) {
        JSONObject jsonObject = getJsonObject(str);
        if (!jsonObject.isNull("res")) {
            return jsonObject.optJSONObject("res");
        } else {
            return jsonObject;
        }
    }

    /**
     * 将字符串解析成JsonObject数组。
     *
     * @param str json数组字符串。
     * @return JSONArray实例。
     */
    public JSONArray getJsonArray(String str) {
        try {
            return new JSONArray(str);
        } catch (JSONException e) {
            PLogger.printThrowable(e);
        }
        return new JSONArray();
    }

    /**
     * 将一个JsonArray转换成List&lt;BaseData&gt;。
     *
     * @param jsonArray     JSONArray实例。
     * @param BaseDataClass BaseData Class
     * @return 通过指定的BaseData类解析JsonArray，返回指定BaseData的list
     */
    public List<? extends BaseData> getBaseDataList(JSONArray jsonArray, Class BaseDataClass) {
        ArrayList<BaseData> list = new ArrayList<BaseData>();
        if (jsonArray == null || BaseDataClass == null) {
            return list;
        }

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                BaseData baseData = (BaseData) BaseDataClass.newInstance();
                baseData.parseJson(jsonArray.getString(i));
                list.add(baseData);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 返回不保证有序,返回String对象
     *
     * @param json
     * @return
     */
    public Map<String, String> getMap(final JSONObject json) {
        Map<String, String> map = new HashMap<String, String>();
        if (json != null) {
            for (Iterator iterator = json.keys(); iterator.hasNext(); ) {
                String type = (String) iterator.next();
                map.put(type, json.optString(type));
            }
        }
        return map;
    }

    /**
     * 返回不保证有序,返回String对象
     *
     * @param json
     * @param dataObjClass
     * @return
     */
    public Map<String, ? extends BaseData> getMap(final JSONObject json, final Class<? extends BaseData> dataObjClass) {
        Map<String, BaseData> map = new HashMap<String, BaseData>();

        if (json != null) {
            for (Iterator iterator = json.keys(); iterator.hasNext(); ) {
                String type = (String) iterator.next();

                try {
                    BaseData baseData = (BaseData) dataObjClass.newInstance();
                    baseData.parseJson(json.optString(type));
                    map.put(type, baseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }
}
