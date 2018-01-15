package com.juxin.predestinate.bean.settting;

import java.io.Serializable;

/**
 * 创建日期：2017/1/6
 * 描述: 体力购买配置
 * 作者:lc
 */
public class StrengthBean implements Serializable {

    private int strength;  // 体力
    private int y;          // Y币
    private String img;

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
