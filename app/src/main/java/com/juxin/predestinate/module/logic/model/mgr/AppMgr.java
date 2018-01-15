package com.juxin.predestinate.module.logic.model.mgr;

import com.juxin.library.observe.ModuleBase;

/**
 * 应用信息管理
 */
public interface AppMgr extends ModuleBase {

    // ================渠道与包信息================

    /**
     * 获取包名
     *
     * @return 安装包名
     */
    String getPackageName();

    /**
     * 获取当前软件签名
     *
     * @return 当前软件签名，如果未获取到，返回""
     */
    String getSignature();

    /**
     * 获取应用的版本号
     *
     * @return 版本号，如果没有获取到返回0
     */
    int getVerCode();

    /**
     * 获取应用的版本名
     *
     * @return 版本名，如果没有获取到返回""
     */
    String getVerName();

    /**
     * 获取软件轻量包升级配置id
     *
     * @return 轻量包升级配置id，全量包默认为空字符串
     */
    String getLiteAppId();

    /**
     * @return 获取渠道标识
     */
    String getUMChannel();

    /**
     * @return 获取主渠道号
     */
    String getMainChannelID();

    /**
     * @return 子渠道号
     */
    String getSubChannelID();

    // ================手机硬件信息================

    /**
     * @return 获取手机Android系统版本号
     */
    String getAndroidOSVer();

    /**
     * @return 获取手机型号
     */
    String getPhoneMode();

    /**
     * @return 获取手机生产商
     */
    String getPhoneManufacturer();

    /**
     * @return 获取移动设备国际识别码
     */
    String getIMEI();

    /**
     * 判断当前移动设备国际识别码是否一致。
     * 如果imei为空，也返回false
     *
     * @return 只有全部不为kong，且相等才返回true
     */
    boolean isEqualIMEI(String imei);

    /**
     * @return 获取移动用户识别码
     */
    String getIMSI();

    /**
     * @return SIM卡供应商
     */
    String getSimOperator();

    /**
     * @return 获取mac地址
     */
    String getMAC();

    /**
     * @return 获取手机唯一标识，根据IMEI与MAC进行hash之后生成
     */
    String getDeviceID();

    // ================软件状态信息================

    /**
     * 获取当前进程名称
     *
     * @param pid 进程id
     * @return 进程名称
     */
    String getProcessName(int pid);

    /**
     * 是否Debug模式
     */
    boolean isDebug();

    /**
     * 判定应用是否在前台运行。
     *
     * @return 前台运行返回true，否则返回false。
     */
    boolean isForeground();

    /**
     * 是否WiFi网络
     *
     * @return WiFi网络返回true，否则返回false
     */
    boolean isWiFi();

    /**
     * 是否有网络可用
     *
     * @return 有可用网络返回true，否则返回false
     */
    boolean isNetAvailable();

    /**
     * 获取系统时间，经过了服务器时间修正。毫秒级
     */
    long getTime();

    /**
     * 注意是秒级别用错了 获取系统时间，经过了服务器时间修正。秒级
     */
    long getSecondTime();

    /**
     * 获取屏幕宽度
     */
    int getScreenWidth();

    /**
     * 获取屏幕高度
     */
    int getScreenHeight();

    /**
     * @return 获取软件状态栏高度
     */
    int getStatusBarHeight();

    /**
     * @return 软件打开次数统计
     */
    int appRunCount();
}