package com.juxin.predestinate.bean.start;

import android.os.Parcel;
import android.os.Parcelable;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 离线消息解析类
 * Created by Su on 2017/5/25.
 */
public class OfflineMsg extends BaseData implements Parcelable {
    private List<OfflineBean> msgList = new ArrayList<>();

    @Override
    public void parseJson(String jsonStr) {
        String jsonData = getJsonObject(jsonStr).optString("res");
        JSONObject jsonObject = getJsonObject(jsonData);

        // 离线消息列表
        if (!jsonObject.isNull("msglist")) {
            this.msgList = (List<OfflineBean>) getBaseDataList(jsonObject.optJSONArray("msglist"), OfflineBean.class);
        }
    }

    public List<OfflineBean> getMsgList() {
        return msgList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.msgList);
    }

    public OfflineMsg() {
    }

    protected OfflineMsg(Parcel in) {
        this.msgList = new ArrayList<>();
        in.readList(this.msgList, OfflineBean.class.getClassLoader());
    }

    public static final Creator<OfflineMsg> CREATOR = new Creator<OfflineMsg>() {
        @Override
        public OfflineMsg createFromParcel(Parcel source) {
            return new OfflineMsg(source);
        }

        @Override
        public OfflineMsg[] newArray(int size) {
            return new OfflineMsg[size];
        }
    };
}
