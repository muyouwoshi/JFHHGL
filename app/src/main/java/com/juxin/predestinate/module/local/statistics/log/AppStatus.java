package com.juxin.predestinate.module.local.statistics.log;

/**
 * APP运行状态(进入前台/进入后台/启动/关闭)
 * Created by ZRP on 2017/8/2.
 */
public enum AppStatus {

    FOREGROUND(0),  // 进入前台
    BACKGROUND(1),  // 进入后台
    START(2),       // 启动APP
    CLOSE(3),       // 关闭APP(全部关闭操作事件)
    ;

    // -------------inner structure---------------

    private int status;

    AppStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
