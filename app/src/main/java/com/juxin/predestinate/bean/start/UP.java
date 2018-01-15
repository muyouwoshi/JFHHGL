package com.juxin.predestinate.bean.start;

/**
 * 用户名，密码
 * Created by zhang on 2016/4/1.
 */
public class UP implements Comparable<UP> {
    private long uid;   // 用戶id
    private String pw;  // 用戶密碼
    private long tm;    // 存储的时间，由该值确定用户List展示顺序

    public UP() {
        super();
    }

    public UP(long uid, String pw) {
        this.uid = uid;
        this.pw = pw;
    }

    public UP(long uid, String pw, long tm) {
        this.uid = uid;
        this.pw = pw;
        this.tm = tm;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public long getTm() {
        return tm;
    }

    public void setTm(long tm) {
        this.tm = tm;
    }

    @Override
    public int compareTo(UP up) { // 倒序排列
        if (this.tm < up.getTm())
            return 1;

        if (this.tm > up.getTm())
            return -1;

        return 0;
    }

    @Override
    public String toString() {
        return "UP{" +
                "uid=" + uid +
                ", pw='" + pw + '\'' +
                ", tm=" + tm +
                '}';
    }
}
