package com.juxin.predestinate.module.local.mail;

import com.juxin.predestinate.bean.net.BaseData;
import org.json.JSONObject;
import java.util.List;

/**
 * Created by Kind on 2017/7/19.
 */

public class MyChumList extends BaseData {

    private List<MyChum> myChumList;

    @Override
    public void parseJson(String jsonStr) {
        myChumList = (List<MyChum>) getBaseDataList(getJsonObjectRes(jsonStr).optJSONArray("friends"), MyChum.class);
    }

    public List<MyChum> getMyChumList() {
        return myChumList;
    }

    public void setMyChumList(List<MyChum> myChumList) {
        this.myChumList = myChumList;
    }
}
