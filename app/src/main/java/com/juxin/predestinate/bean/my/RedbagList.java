package com.juxin.predestinate.bean.my;


import android.text.TextUtils;
import android.util.Log;

import com.juxin.predestinate.bean.net.BaseData;
import com.juxin.predestinate.module.util.TimeUtil;

import org.json.JSONObject;

import java.util.List;

/**
 * 标签列表
 * Created by zm on 17/3/20.
 */
public class RedbagList extends BaseData {

    private List redbagLists;
    private String status;
    private double total;
    private int onekeynum;
    public static String oldDate;
    public static RedbagInfo info;

    public boolean isOk() {
        if ("ok".equalsIgnoreCase(status))
            return true;
        else
            return false;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total/100f;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List getRedbagLists() {
        return redbagLists;
    }

    public int getOnekeynum() {
        return onekeynum;
    }

    public void setOnekeynum(int onekeynum) {
        this.onekeynum = onekeynum;
    }

    @Override
    public void parseJson(String s) {
        info = null;
        JSONObject jsonObject = getJsonObject(s);
        if (jsonObject.has("total"))
            this.setTotal(jsonObject.optDouble("total"));
        if (jsonObject.has("onekeynum"))
            this.setOnekeynum(jsonObject.optInt("onekeynum"));
        redbagLists = getBaseDataList(jsonObject.optJSONArray("result"), RedbagInfo.class);
    }

    public static class RedbagInfo extends BaseData {

        private long id;
        private double money;
        private String create_time;
        private int type;
        private int rank;
        private int frozen;
        private String type_text;       //类型 文字
        private boolean isFrist;
        private boolean isLast;
        private String date;//2017年9月

        @Override
        public void parseJson(String s) {
            JSONObject jsonObject = getJsonObject(s);
            this.setId(jsonObject.optLong("id"));
            this.setMoney(jsonObject.optDouble("money"));
            this.setCreate_time(jsonObject.optString("create_time"));
            this.setType(jsonObject.optInt("type"));
            this.setRank(jsonObject.optInt("rank"));
            this.setFrozen(jsonObject.optInt("frozen"));
            this.setType_text(jsonObject.optString("type_text"));
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public double getMoney() {
            return money;
        }

        public void setMoney(double money) {
            this.money = money/100f;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
            if (!TextUtils.isEmpty(create_time)) {
                this.date = TimeUtil.getCurrentDataYYYYMM(TimeUtil.parseDate(create_time, "yyyy-MM-dd HH:mm:ss"));
                if (this.date == null) {
                    this.date = "";
                }
                if (!this.date.equalsIgnoreCase(oldDate)) {
                    isFrist = true;
                    if (info != null){
                        info.isLast = true;
                    }
                    oldDate = date;
                } else {
                    isFrist = false;
                }
            }
            info = this;
        }

        public boolean isFrist() {
            return isFrist;
        }

        public boolean isLast() {
            return isLast;
        }

        public String getDate() {
            return date;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }

        public int getFrozen() {
            return frozen;
        }

        public void setFrozen(int frozen) {
            this.frozen = frozen;
        }

        public String getType_text() {
            return type_text;
        }

        public void setType_text(String type_text) {
            this.type_text = type_text;
        }

        @Override
        public String toString() {
            return "RankList{" +
                    //                    "uid=" + uid +
                    //                    ", avatar=" + avatar +
                    //                    ", nickname=" + nickname +
                    //                    ", gender=" + gender +
                    //                    ", score=" + score +
                    //                    ", exp=" + exp +
                    '}';
        }
    }
}
