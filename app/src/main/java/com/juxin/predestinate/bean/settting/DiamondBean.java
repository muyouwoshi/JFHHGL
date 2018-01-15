package com.juxin.predestinate.bean.settting;

import java.io.Serializable;

/**
 * 钻石bean
 * Created by lc on 2016/12/20.
 */

public class DiamondBean implements Serializable {

    private int num;    // 钻石数
    private int cost;   // 价格
    private int pid;   // 产品ID

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }
}
