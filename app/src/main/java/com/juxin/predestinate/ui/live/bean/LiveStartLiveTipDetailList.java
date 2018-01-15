package com.juxin.predestinate.ui.live.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * 开播提醒
 * <p>
 * Created by chengxiaobo on 2017/10/26.
 */

public class LiveStartLiveTipDetailList implements Serializable {

    private List<LiveStartLiveTipDetail> list;


    /**
     * 解析成对象
     *
     * @param jsonStr
     */
    public void parseJson(JSONObject jsonStr) throws JSONException {

        JSONArray jsonArray = jsonStr.optJSONArray("list");
        if (jsonArray != null) {
            list = new ArrayList<LiveStartLiveTipDetail>();
            for (int i = 0; i < jsonArray.length(); ++i) {
                LiveStartLiveTipDetail bean = new LiveStartLiveTipDetail();
                bean.parseJson(jsonArray.getJSONObject(i));
                list.add(bean);
            }
        }
    }

    public List<LiveStartLiveTipDetail> getList() {
        return list;
    }

    public void setList(List<LiveStartLiveTipDetail> list) {
        this.list = list;
    }
}
