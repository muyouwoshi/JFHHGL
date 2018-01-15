package com.juxin.predestinate.ui.live.bean;

/**
 * Created by terry on 2017/9/13.
 */

public class LiveUserRankBean {
    public String avatar;
    public int level;
    public int gender;
    public long uid;
    public int diamond;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public int getDiamond() {
        return diamond;
    }

    public void setDiamond(int diamond) {
        this.diamond = diamond;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + String.valueOf(uid).hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof LiveUserRankBean)) {
            return false;
        }

        LiveUserRankBean rankBean = (LiveUserRankBean) obj;
        boolean b = this.getUid() == rankBean.uid;
        return b;
    }
}
