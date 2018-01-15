package com.juxin.predestinate.bean.my;


import com.juxin.predestinate.bean.net.BaseData;

/**
 * 礼物数量对应
 * Created by zm on 17/3/20.
 */
public class GiftsNumInfo extends BaseData {

    private int num;
    private String moral;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getMoral() {
        return moral;
    }

    public void setMoral(String moral) {
        this.moral = moral;
    }

    @Override
    public void parseJson(String jsonStr) {

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
