package com.juxin.predestinate.module.logic.config;

/**
 * 一些key值String
 * Created by ZRP on 2016/12/8.
 */
public class FinalKey {

    public static final String APP_RUN_COUNT = "APP_RUN_COUNT"; //软件运行次数统计
    public static final String HOME_TAB_TYPE = "HOME_TAB_TYPE"; //跳转到首页时，切换的tab标记

    public static final String UP_DES_KEY = "yfb+-73+";         //DES加解密key
    public static final String UP_DES_KEY_PCK = "yuasa%kk";     //DES加解密辅助key

    public static final String LOGIN_USER_KEY = "LOGIN_USER_KEY";  //保存当前登录过的账号信息
    public static final String SERVER_STATIC_CONFIG = "SERVER_STATIC_CONFIG";//服务器在线配置
    public static final String TESTING_WEB_STORE = "TESTING_WEB_STORE";//测试彩蛋url存储地址

    // 是否展示个人中心女性自动回复“NEW”提示角标的判断key
    public static final String ISSHOWAUTOREPLYPOINT_MYHOME = "isShowAutoRePlyPoint_myHome";
    // 是否展示打招呼的人界面右上角自动回复小红点提示角标的判断key
    public static final String ISSHOWAUTOREPLYPOINT_SAYHELLO = "isShowAutoRePlyPoint_sayHello";

    // 模拟退出应用的intent参数key，只在MainActivity进行使用
    public static final String SIMULATE_EXIT_KEY = "SIMULATE_EXIT_KEY";
    // 软件是否进行日志打印的控制参数key
    public static final String GM_PRINT_DEBUG_LOG = "GM_PRINT_DEBUG_LOG";

    // 首页tab标记，从左到右依次为1-5
    public static final int MAIN_TAB_1 = 0x01;
    public static final int MAIN_TAB_2 = 0x02;
    public static final int MAIN_TAB_3 = 0x03;
    public static final int MAIN_TAB_4 = 0x04;
    public static final int MAIN_TAB_5 = 0x05;
}
