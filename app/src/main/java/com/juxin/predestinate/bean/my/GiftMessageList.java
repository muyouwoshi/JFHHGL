package com.juxin.predestinate.bean.my;


import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

import java.util.List;

/**
 * 标签列表
 * Created by zm on 17/3/20.
 */
public class GiftMessageList extends BaseData {

    private List giftMessageList;

    public List getGiftMessageList() {
        return giftMessageList;
    }

    @Override
    public void parseJson(String s) {
        JSONObject jsonObject = getJsonObject(s);
        giftMessageList = getBaseDataList(jsonObject.optJSONArray("list"), GiftMessageInfo.class);
    }

    public static class GiftMessageInfo extends BaseData {

        private long from_uid;
        private long to_uid;
        private String from_name;
        private String to_name;
        private int giftid;
        private String gname;
        private int num;

        @Override
        public void parseJson(String s) {
            JSONObject jsonObject = getJsonObject(s);
            this.setFrom_uid(jsonObject.optLong("from_uid"));
            this.setTo_uid(jsonObject.optLong("to_uid"));
            this.setFrom_name(jsonObject.optString("from_name"));
            this.setTo_name(jsonObject.optString("to_name"));
            this.setGiftid(jsonObject.optInt("giftid"));
            this.setGname(jsonObject.optString("gname"));
            this.setNum(jsonObject.optInt("num"));
        }

        public long getFrom_uid() {
            return from_uid;
        }

        public void setFrom_uid(long from_uid) {
            this.from_uid = from_uid;
        }

        public long getTo_uid() {
            return to_uid;
        }

        public void setTo_uid(long to_uid) {
            this.to_uid = to_uid;
        }

        public String getFrom_name() {
            return from_name;
        }

        public void setFrom_name(String from_name) {
            this.from_name = from_name;
        }

        public String getTo_name() {
            return to_name;
        }

        public void setTo_name(String to_name) {
            this.to_name = to_name;
        }

        public int getGiftid() {
            return giftid;
        }

        public void setGiftid(int giftid) {
            this.giftid = giftid;
        }

        public String getGname() {
            return gname;
        }

        public void setGname(String gname) {
            this.gname = gname;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
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
