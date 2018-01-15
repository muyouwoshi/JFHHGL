package com.juxin.predestinate.bean.center.user.hot;

import android.text.TextUtils;

import com.juxin.library.log.PLogger;
import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 个人资料热门列表
 */
public class UserInfoHotList extends BaseData {

    private ArrayList<UserInfoHot> hotLists = new ArrayList<>();

    /**
     * ref 如果是 true 并且请求的如果非第一页
     * 那么返回来的就是第一页 应该把之前的数据都清掉
     * 把返回的数据作为第一页
     */
    private boolean isRef = false;

    /**
     * 正常解析用户热门信息
     *
     * @param jsonStr
     */
    @Override
    public void parseJson(String jsonStr) {
        if (!TextUtils.isEmpty(jsonStr)) {
            PLogger.d("UserInfoHotList parseJson ---- jsonStr " + jsonStr);
            String jsonData = getJsonObject(jsonStr).optString("res");
            JSONObject object = getJsonObject(jsonData);
            JSONArray jsonArray = object.optJSONArray("list");
            setRef(object.optInt("ref") == 1);
            this.hotLists = (ArrayList<UserInfoHot>) getBaseDataList(jsonArray, UserInfoHot.class);
        }
    }

    public boolean isRef() {
        return isRef;
    }

    public void setRef(boolean ref) {
        isRef = ref;
    }

    public ArrayList<UserInfoHot> getHotLists() {
        return hotLists;
    }

    public void setHotLists(ArrayList<UserInfoHot> hotLists) {
        this.hotLists = hotLists;
    }
}
