package com.juxin.predestinate.bean.my;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * 创建日期：2017/11/2
 * 描述: 分享类型数据
 *
 * @author :lc
 */
public class ShareTypeData implements Parcelable {

    private int shareType;               //1.直播间分享/主页分享/解锁分享 2.提现分享 3.主动推广分享
    private long otherId;                //对方uid
    private String otherName;            //对方名称
    private String otherAvatar;          //对方头像链接
    private String earnMoney;            //赚了xxx元
    private String shareCode;               //分享码
    private int mould;                   //模版id

    public ShareTypeData(int shareType) {
        this.shareType = shareType;
    }

    public ShareTypeData(int shareType, long otherId, String otherName, String earnMoney) {
        this.shareType = shareType;
        this.otherId = otherId;
        this.otherName = otherName;
        this.earnMoney = earnMoney;
    }

    public ShareTypeData(int shareType, long otherId, String otherName, String otherAvatar, String earnMoney) {
        this.shareType = shareType;
        this.otherId = otherId;
        this.otherName = otherName;
        this.otherAvatar = otherAvatar;
        this.earnMoney = earnMoney;
    }

    public int getShareType() {
        return shareType;
    }

    public void setShareType(int shareType) {
        this.shareType = shareType;
    }

    public long getOtherId() {
        return otherId;
    }

    public void setOtherId(long otherId) {
        this.otherId = otherId;
    }

    public String getOtherName() {
        return TextUtils.isEmpty(otherName) ? "" : otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public String getOtherAvatar() {
        return otherAvatar;
    }

    public void setOtherAvatar(String otherAvatar) {
        this.otherAvatar = otherAvatar;
    }

    public String getEarnMoney() {
        return TextUtils.isEmpty(earnMoney) ? "" : earnMoney;
    }

    public void setEarnMoney(String earnMoney) {
        this.earnMoney = earnMoney;
    }


    public String getShareCode() {
        return TextUtils.isEmpty(shareCode) ? "" : shareCode;
    }

    public void setShareCode(String shareCode) {
        this.shareCode = shareCode;
    }


    public int getMould() {
        return mould;
    }

    public void setMould(int mould) {
        this.mould = mould;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.shareType);
        dest.writeLong(this.otherId);
        dest.writeString(this.otherName);
        dest.writeString(this.otherAvatar);
        dest.writeString(this.earnMoney);
        dest.writeString(this.shareCode);
        dest.writeInt(this.mould);
    }

    public ShareTypeData() {
    }

    protected ShareTypeData(Parcel in) {
        this.shareType = in.readInt();
        this.otherId = in.readLong();
        this.otherName = in.readString();
        this.otherAvatar = in.readString();
        this.earnMoney = in.readString();
        this.shareCode = in.readString();
        this.mould = in.readInt();
    }

    public static final Creator<ShareTypeData> CREATOR = new Creator<ShareTypeData>() {
        @Override
        public ShareTypeData createFromParcel(Parcel source) {
            return new ShareTypeData(source);
        }

        @Override
        public ShareTypeData[] newArray(int size) {
            return new ShareTypeData[size];
        }
    };
}
