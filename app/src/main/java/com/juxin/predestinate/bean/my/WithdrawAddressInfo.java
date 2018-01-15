package com.juxin.predestinate.bean.my;


import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

/**
 * 标签列表
 * Created by zm on 17/3/20.
 */
public class WithdrawAddressInfo extends BaseData implements Parcelable {

//    "status": "ok",
//            "list": {
//        "accountname": "你好",
//                "accountnum": "2147483647345353453",    //支付宝账号/银行账号
//                "bank": "中国银行",                    //为"支付宝"时 类型为支付宝
//                "subbank": "长沙支行"
//    }

    private String accountname;
    private String accountnum;
    private String bank;
    private String subbank;
    private String status;
    private int paytype = 2;

    public  WithdrawAddressInfo(){

    }

    protected WithdrawAddressInfo(Parcel in) {
        accountname = in.readString();
        accountnum = in.readString();
        bank = in.readString();
        subbank = in.readString();
        status = in.readString();
        paytype = in.readInt();
    }

    public static final Creator<WithdrawAddressInfo> CREATOR = new Creator<WithdrawAddressInfo>() {
        @Override
        public WithdrawAddressInfo createFromParcel(Parcel in) {
            return new WithdrawAddressInfo(in);
        }

        @Override
        public WithdrawAddressInfo[] newArray(int size) {
            return new WithdrawAddressInfo[size];
        }
    };

    public boolean isOk() {
        if ("ok".equalsIgnoreCase(status))
            return true;
        else
            return false;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public void parseJson(String s) {
        JSONObject jsonObject = getJsonObject(s).optJSONObject("list");
        if (jsonObject != null){
            this.setAccountname(jsonObject.optString("accountname"));
            this.setAccountnum(jsonObject.optString("accountnum"));
            this.setBank(jsonObject.optString("bank"));
            this.setSubbank(jsonObject.optString("subbank"));
            if ("支付宝".equalsIgnoreCase(bank))
                paytype = 2;
            else
                paytype =1;
            if (TextUtils.isEmpty(this.getAccountnum())){
                paytype = 2;
            }
        }
    }

    public String getAccountname() {
        return accountname;
    }

    public void setAccountname(String accountname) {
        this.accountname = accountname != null?accountname:"";
    }

    public String getAccountnum() {
        return accountnum;
    }

    public void setAccountnum(String accountnum) {
        this.accountnum = accountnum != null?accountnum:"";
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank != null?bank:"";
    }

    public String getSubbank() {
        return subbank;
    }

    public void setSubbank(String subbank) {
        this.subbank = subbank != null?subbank:"";
    }

    public int getPaytype() {
        return paytype;
    }

    public void setPaytype(int paytype) {
        this.paytype = paytype;
    }

    @Override
    public String toString() {
        return "WithdrawAddressInfo{" +
                "accountname=" + accountname +
                ", accountnum=" + accountnum +
                ", bank=" + bank +
                ", subbank=" + subbank +
                ", status=" + status +
                ", paytype=" + paytype +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(accountname);
        parcel.writeString(accountnum);
        parcel.writeString(bank);
        parcel.writeString(subbank);
        parcel.writeString(status);
        parcel.writeInt(paytype);
    }
}
