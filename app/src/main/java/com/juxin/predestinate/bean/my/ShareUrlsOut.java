package com.juxin.predestinate.bean.my;

import android.os.Parcel;
import android.os.Parcelable;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 创建日期：2017/10/30
 * 描述:分享口令urls
 * 作者:lc
 */
public class ShareUrlsOut extends BaseData implements Parcelable {

    private ArrayList<ShareUrl> shareUrls;

    @Override
    public void parseJson(String jsonStr) {
        JSONObject jsonObject = getJsonObject(jsonStr);
        shareUrls = (ArrayList<ShareUrl>) getBaseDataList(getJsonArray(jsonObject.optString("res")), ShareUrl.class);
    }

    public ArrayList<ShareUrl> getShareUrls() {
        return shareUrls;
    }

    public void setShareUrls(ArrayList<ShareUrl> shareUrls) {
        this.shareUrls = shareUrls;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.shareUrls);
    }

    public ShareUrlsOut() {
    }

    protected ShareUrlsOut(Parcel in) {
        this.shareUrls = in.createTypedArrayList(ShareUrl.CREATOR);
    }

    public static final Creator<ShareUrlsOut> CREATOR = new Creator<ShareUrlsOut>() {
        @Override
        public ShareUrlsOut createFromParcel(Parcel source) {
            return new ShareUrlsOut(source);
        }

        @Override
        public ShareUrlsOut[] newArray(int size) {
            return new ShareUrlsOut[size];
        }
    };
}
