package com.juxin.predestinate.module.logic.config;

/**
 * 客户端与服务器时间校正类
 * Created by zijunna on 15/3/2.
 */
public class ServerTime {

    // 服务器与本地时间的时差
    private volatile static long SERVER_SC;

    /**
     * 设置服务器时间
     *
     * @param sys_tm 服务器时间戳
     */
    public synchronized static void setServerTime(long sys_tm) {
        if (sys_tm <= 0) return;

        if (String.valueOf(sys_tm).length() == 10) {//正常格式的时间戳
            SERVER_SC = sys_tm - (System.currentTimeMillis() / 1000);
        } else {//如果服务器返回是异常格式的时间戳，抹平差值，直接使用客户端时间戳
            SERVER_SC = 0;
        }
    }

    /**
     * 获取服务器时间
     *
     * @return 根据服务器返回时间矫正后的Calendar对象
     */
    public synchronized static long getServerTime() {
        return System.currentTimeMillis() + SERVER_SC * 1000;
    }
}
