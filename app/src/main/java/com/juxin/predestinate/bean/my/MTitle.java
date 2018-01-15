package com.juxin.predestinate.bean.my;

import android.os.Parcel;
import android.os.Parcelable;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

/**
 * 创建日期：2017/10/30
 * 描述:多标签
 *
 * @author lc
 */
public class MTitle extends BaseData implements Parcelable {

    private String url;                 //页面路径 如：pages/windRanking/windRanking.html
    private String title;               //标题
    private MTitleParams params;         //参数
    private String defaultUrl;          //默认路径 如：https://page.xiaoyaoai.cn/app/pages/windRanking/windRanking.html

    @Override
    public void parseJson(String jsonStr) {
        JSONObject jsonObject = getJsonObjectRes(jsonStr);
        this.url = jsonObject.optString("url");
        this.title = jsonObject.optString("title");
        this.defaultUrl = jsonObject.optString("defaultUrl");

        String paramsStr = jsonObject.optString("params");
        MTitleParams titleParams = new MTitleParams();
        titleParams.parseJson(paramsStr);
        params = titleParams;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MTitleParams getParams() {
        return params;
    }

    public void setParams(MTitleParams params) {
        this.params = params;
    }

    public String getDefaultUrl() {
        return defaultUrl;
    }

    public void setDefaultUrl(String defaultUrl) {
        this.defaultUrl = defaultUrl;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.title);
        dest.writeParcelable(this.params, flags);
        dest.writeString(this.defaultUrl);
    }

    public MTitle() {
    }

    protected MTitle(Parcel in) {
        this.url = in.readString();
        this.title = in.readString();
        this.params = in.readParcelable(MTitleParams.class.getClassLoader());
        this.defaultUrl = in.readString();
    }

    public static final Creator<MTitle> CREATOR = new Creator<MTitle>() {
        @Override
        public MTitle createFromParcel(Parcel source) {
            return new MTitle(source);
        }

        @Override
        public MTitle[] newArray(int size) {
            return new MTitle[size];
        }
    };
}
