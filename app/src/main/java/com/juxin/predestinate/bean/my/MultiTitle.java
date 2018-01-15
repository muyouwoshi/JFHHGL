package com.juxin.predestinate.bean.my;

import android.os.Parcel;
import android.os.Parcelable;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 创建日期：2017/10/30
 * 描述: 多标签
 *
 * @author :lc
 */
public class MultiTitle extends BaseData implements Parcelable {

    /**
     * 标签标识
     */
    private String module;
    /**
     * 打开方式（1为侧滑页面，2为全屏页面，全屏时显示loading） 3带分享按钮
     */
    private int type;
    /**
     * 标题集合
     */
    private ArrayList<MTitle> multiTitle;

    @Override
    public void parseJson(String jsonStr) {
        JSONObject jsonObject = getJsonObjectRes(jsonStr);
        this.module = jsonObject.optString("module");
        this.type = jsonObject.optInt("type");
        this.multiTitle = (ArrayList<MTitle>) getBaseDataList(jsonObject.optJSONArray("multiTitle"), MTitle.class);
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ArrayList<MTitle> getMultiTitle() {
        return multiTitle;
    }

    public void setMultiTitle(ArrayList<MTitle> multiTitle) {
        this.multiTitle = multiTitle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.module);
        dest.writeInt(this.type);
        dest.writeTypedList(this.multiTitle);
    }

    public MultiTitle() {
    }

    protected MultiTitle(Parcel in) {
        this.module = in.readString();
        this.type = in.readInt();
        this.multiTitle = in.createTypedArrayList(MTitle.CREATOR);
    }

    public static final Creator<MultiTitle> CREATOR = new Creator<MultiTitle>() {
        @Override
        public MultiTitle createFromParcel(Parcel source) {
            return new MultiTitle(source);
        }

        @Override
        public MultiTitle[] newArray(int size) {
            return new MultiTitle[size];
        }
    };
}
