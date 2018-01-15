package com.juxin.predestinate.module.logic.tips;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 提示条数据结构
 * Created by Kind on 2016/11/22.
 */

public class TipsBarData {

    public static final int Show_No = -1;//未显示
    public static final int Show_Already = -2;//已显示

    //提示条类型对应的 显示与不显示的状态设置 value 对应Show_No、Show_Already
    private Map<TipsBarType, Integer> integerMap = new HashMap<>();
    private JSONObject object;
    //提示条需不需要显示 需要与不需要对应 true、false
    private Map<TipsBarType, Boolean> typeNeedShow = new HashMap<>();

    public Map<TipsBarType, Integer> getIntegerMap() {
        return integerMap;
    }

    public void setIntegerMap(Map<TipsBarType, Integer> integerMap) {
        this.integerMap = integerMap;
    }

    public JSONObject getObject() {
        return object;
    }

    public void setObject(JSONObject object) {
        this.object = object;
    }


    public Map<TipsBarType, Boolean> getTypeNeedShow() {
        return typeNeedShow;
    }

    public void setTypeNeedShow(Map<TipsBarType, Boolean> typeNeedShow) {
        this.typeNeedShow = typeNeedShow;
    }

    public void clear() {
        if (integerMap != null) {
            integerMap.clear();
        }
        if (object != null) {
            object = null;
        }

    }
}
