package com.juxin.predestinate.bean.discover;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 举报类型
 * Created by zhang on 2017/4/14.
 */

public class DefriendType implements Parcelable {
    private String str_typeName = ""; //举报显示条目
    private int int_typeNum; //举报的对应用类型
    private boolean isCheck = false; //是否选中该类型作为举报


    public String getStr_typeName() {
        return str_typeName;
    }

    public void setStr_typeName(String str_typeName) {
        this.str_typeName = str_typeName;
    }

    public int getInt_typeNum() {
        return int_typeNum;
    }

    public void setInt_typeNum(int int_typeNum) {
        this.int_typeNum = int_typeNum;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    @Override
    public String toString() {
        return "DefriendType{" +
                "str_typeName='" + str_typeName + '\'' +
                ", int_typeNum=" + int_typeNum +
                ", isCheck=" + isCheck +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.str_typeName);
        dest.writeInt(this.int_typeNum);
        dest.writeByte(this.isCheck ? (byte) 1 : (byte) 0);
    }

    public DefriendType() {
    }

    protected DefriendType(Parcel in) {
        this.str_typeName = in.readString();
        this.int_typeNum = in.readInt();
        this.isCheck = in.readByte() != 0;
    }

    public static final Creator<DefriendType> CREATOR = new Creator<DefriendType>() {
        @Override
        public DefriendType createFromParcel(Parcel source) {
            return new DefriendType(source);
        }

        @Override
        public DefriendType[] newArray(int size) {
            return new DefriendType[size];
        }
    };
}
