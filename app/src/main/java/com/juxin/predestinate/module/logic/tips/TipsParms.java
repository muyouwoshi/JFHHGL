package com.juxin.predestinate.module.logic.tips;

/**
 * 提示条用到的参数
 * Created by zhang on 2016/11/24.
 */
public class TipsParms {

    private String tipsTex; //普通提示条 提示语

    private long uid; //加好友提示条 uid

    public TipsParms() {
    }

    public TipsParms(String tipsTex, long uid) {
        this.tipsTex = tipsTex;
        this.uid = uid;
    }


    public String getTipsTex() {
        return tipsTex;
    }

    public void setTipsTex(String tipsTex) {
        this.tipsTex = tipsTex;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }
}
