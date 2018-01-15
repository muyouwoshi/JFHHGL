package com.juxin.predestinate.bean.my;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

/**
 * 创建日期：2017/10/30
 * 描述:分享口令urls
 * 作者:lc
 */
public class ShareUrl extends BaseData implements Parcelable {
    /**
     * id : 4
     * selfGender : 1
     * shareGender : 2
     * shareUrl : w.mjgglcj.com/mahi/index1?token=
     * shareHeadImageUrl : http://image1.yuanfenba.net/uploads/oss/promoter/matrial/201710/25/1123428695.png
     * state : 1
     * createTime : Oct 25, 2017 11:23:42 AM
     * lastUpdateTime : Oct 26, 2017 3:25:57 PM
     * shareImageUrl : http://w.mjgglcj.com/mahi/index01?qrcode_content=
     */

    private int id;
    private String contentUserId;
    private String contentNickName;
    private int selfGender;
    private int shareGender;
    private String shareUrl;
    private String shareHeadImageUrl;
    private int state;
    private String createTime;
    private String lastUpdateTime;
    private String shareImageUrl;

    private boolean isSelected;

    @Override
    public void parseJson(String jsonStr) {
        JSONObject jsonObject = getJsonObjectRes(jsonStr);
        this.id = jsonObject.optInt("id");
        this.contentUserId = jsonObject.optString("contentUserId");
        this.contentNickName = jsonObject.optString("contentNickName");
        this.selfGender = jsonObject.optInt("selfGender");
        this.shareGender = jsonObject.optInt("shareGender");
        this.shareUrl = jsonObject.optString("shareUrl");
        this.shareHeadImageUrl = jsonObject.optString("shareHeadImageUrl");
        this.state = jsonObject.optInt("state");
        this.createTime = jsonObject.optString("createTime");
        this.lastUpdateTime = jsonObject.optString("lastUpdateTime");
        this.shareImageUrl = jsonObject.optString("shareImageUrl");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContentUserId() {
        return contentUserId;
    }

    public long getUserIdToLong() {
        return TextUtils.isEmpty(contentUserId) ? 0 : Long.parseLong(contentUserId);
    }

    public void setContentUserId(String contentUserId) {
        this.contentUserId = contentUserId;
    }

    public String getContentNickName() {
        return contentNickName;
    }

    public void setContentNickName(String contentNickName) {
        this.contentNickName = contentNickName;
    }

    public int getSelfGender() {
        return selfGender;
    }

    public void setSelfGender(int selfGender) {
        this.selfGender = selfGender;
    }

    public int getShareGender() {
        return shareGender;
    }

    public void setShareGender(int shareGender) {
        this.shareGender = shareGender;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getShareHeadImageUrl() {
        return shareHeadImageUrl;
    }

    public void setShareHeadImageUrl(String shareHeadImageUrl) {
        this.shareHeadImageUrl = shareHeadImageUrl;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getShareImageUrl() {
        return shareImageUrl;
    }

    public void setShareImageUrl(String shareImageUrl) {
        this.shareImageUrl = shareImageUrl;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.contentUserId);
        dest.writeString(this.contentNickName);
        dest.writeInt(this.selfGender);
        dest.writeInt(this.shareGender);
        dest.writeString(this.shareUrl);
        dest.writeString(this.shareHeadImageUrl);
        dest.writeInt(this.state);
        dest.writeString(this.createTime);
        dest.writeString(this.lastUpdateTime);
        dest.writeString(this.shareImageUrl);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
    }

    public ShareUrl() {
    }

    protected ShareUrl(Parcel in) {
        this.id = in.readInt();
        this.contentUserId = in.readString();
        this.contentNickName = in.readString();
        this.selfGender = in.readInt();
        this.shareGender = in.readInt();
        this.shareUrl = in.readString();
        this.shareHeadImageUrl = in.readString();
        this.state = in.readInt();
        this.createTime = in.readString();
        this.lastUpdateTime = in.readString();
        this.shareImageUrl = in.readString();
        this.isSelected = in.readByte() != 0;
    }

    public static final Creator<ShareUrl> CREATOR = new Creator<ShareUrl>() {
        @Override
        public ShareUrl createFromParcel(Parcel source) {
            return new ShareUrl(source);
        }

        @Override
        public ShareUrl[] newArray(int size) {
            return new ShareUrl[size];
        }
    };
}
