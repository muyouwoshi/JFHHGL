package com.juxin.predestinate.ui.user.fragment.bean;

/**
 * 个人中心界面展示控制实体类
 */
public class UserAuth {

    private int id;         // 条目ID
    private int res;        // 资源ID
    private int level;      // 分组
    private String name;    // 条目名
    private boolean isShow; // 条目显隐标志

    public UserAuth(int id, int res, int level, String name, boolean isShow) {
        this.id = id;
        this.res = res;
        this.level = level;
        this.name = name;
        this.isShow = isShow;
    }

    public boolean isShow() {
        return isShow;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
