package com.juxin.library.log;

import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.Printer;

/**
 * 全局统一的日志打印工具类：comes from <pre>https://github.com/elvishew/XLog</pre>
 * Created by ZRP on 2016/9/8.
 */
public class PLogger {

    public static void init(boolean isDebug) {
        LogConfiguration config = new LogConfiguration.Builder()
                .logLevel(isDebug ? LogLevel.ALL : LogLevel.NONE)       // 指定日志级别，低于该级别的日志将不会被打印，默认为 LogLevel.ALL
                .tag("Predestinate")                                    // 指定 TAG，默认为 "X-LOG"
                .t()                                                    // 允许打印线程信息，默认禁止
                .threadFormatter(new PThreadFormatter())                // 指定线程信息格式化器，默认为 DefaultThreadFormatter
                .st(3)                                                  // 允许打印深度为2的调用栈信息，默认禁止
                .build();
        Printer androidPrinter = new AndroidPrinter();                  // 通过 android.util.Log 打印日志的打印器
        XLog.init(                                                      // 初始化 XLog
                config,                                                 // 指定日志配置，如果不指定，会默认使用 new LogConfiguration.Builder().build()
                androidPrinter);                                        // 添加任意多的打印器。如果没有添加任何打印器，会默认使用 AndroidPrinter(Android)/ConsolePrinter(java)
    }

    public static void v(String msg) {
        XLog.v(getLogEnvironment(), msg);
    }

    public static void d(String msg) {
        XLog.d(getLogEnvironment(), msg);
    }

    public static void i(String msg) {
        XLog.i(getLogEnvironment(), msg);
    }

    public static void w(String msg) {
        XLog.w(getLogEnvironment(), msg);
    }

    public static void e(String msg) {
        XLog.e(getLogEnvironment(), msg);
    }

    public static void printObject(Object object) {
        XLog.d(getLogEnvironment(), object);
    }

    public static void printJson(String jsonString) {
        XLog.json(jsonString);
    }

    public static void printXML(String xmlString) {
        XLog.xml(xmlString);
    }

    public static void printThrowable(Throwable throwable) {
        XLog.w("", throwable);
    }

    /**
     * 拼接打印当前log的环境信息，方便定位
     *
     * @return 当前调用日志打印的环境信息
     */
    private static String getLogEnvironment() {
        return "%s";
    }
}
