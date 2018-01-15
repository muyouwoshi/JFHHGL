package com.juxin.predestinate.bean.discover;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 选择首页类型
 * Created by zhang on 2017/6/1.
 */

public class DiscoverSelect implements Parcelable {
    private String selectType = "";
    private boolean isSelect = false;

    public DiscoverSelect() {

    }

    public DiscoverSelect(String selectType, boolean isSelect) {
        this.selectType = selectType;
        this.isSelect = isSelect;
    }

    public String getSelectType() {
        return selectType;
    }

    public void setSelectType(String selectType) {
        this.selectType = selectType;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.selectType);
        dest.writeByte(this.isSelect ? (byte) 1 : (byte) 0);
    }

    protected DiscoverSelect(Parcel in) {
        this.selectType = in.readString();
        this.isSelect = in.readByte() != 0;
    }

    public static final Creator<DiscoverSelect> CREATOR = new Creator<DiscoverSelect>() {
        @Override
        public DiscoverSelect createFromParcel(Parcel source) {
            return new DiscoverSelect(source);
        }

        @Override
        public DiscoverSelect[] newArray(int size) {
            return new DiscoverSelect[size];
        }
    };
}
