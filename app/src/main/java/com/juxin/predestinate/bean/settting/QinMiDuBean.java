package com.juxin.predestinate.bean.settting;

import java.io.Serializable;

/**
 * 创建日期：2017/1/6
 * 描述:
 * 作者:lc
 */
public class QinMiDuBean implements Serializable {

    private int q;  //亲密度
    private String red;     // 红包收益率

    public int getQ() {
        return q;
    }

    public void setQ(int q) {
        this.q = q;
    }

    public String getRed() {
        return red;
    }

    public void setRed(String red) {
        this.red = red;
    }
}
