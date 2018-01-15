package com.juxin.predestinate.bean.my;


import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

import java.util.List;

/**
 * 礼物列表
 * Created by zm on 17/3/20.
 */
public class AttentionList extends BaseData {

    private List arr_lists;
    private int num;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public List getArr_lists() {
        return arr_lists;
    }

    @Override
    public void parseJson(String s) {
        JSONObject jsonObject = getJsonObject(s);
        if (jsonObject.has("num")){
            this.setNum(jsonObject.optInt("num"));
        }
        arr_lists = getBaseDataList(jsonObject.optJSONArray("item"), AttentionInfo.class);
    }

    public static class AttentionInfo extends BaseData {

        private long uid;
        private long time;

        @Override
        public void parseJson(String s) {
            JSONObject jsonObject = getJsonObject(s);
            //json串解析
            this.setUid(jsonObject.optLong("uid"));
            this.setTime(jsonObject.optLong("time"));
        }

        public long getUid() {
            return uid;
        }

        public void setUid(long uid) {
            this.uid = uid;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
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
