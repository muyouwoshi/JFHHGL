package com.juxin.predestinate.module.local.statistics.log;

import android.os.Process;

import com.juxin.predestinate.module.local.statistics.Statistics;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;

/**
 * 全局崩溃拦截处理
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static CrashHandler instance;
    //系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler defaultHandler;

    public static synchronized CrashHandler getInstance() {
        if (instance == null) instance = new CrashHandler();
        return instance;
    }

    //这里主要完成初始化工作
    public void init() {
        //获取系统默认的异常处理器
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //将当前实例设为系统默认的异常处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        if (!handleException(throwable) && defaultHandler != null) {
            defaultHandler.uncaughtException(thread, throwable);
        }
        // 退出应用
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Process.killProcess(Process.myPid());
        System.exit(1);
        System.gc();
    }

    /**
     * 收集上报崩溃信息
     *
     * @param throwable 崩溃信息
     * @return 是否自己对崩溃进行处理
     */
    private boolean handleException(Throwable throwable) {
        if (throwable == null) return false;

        try {
            // 收集上报崩溃信息
            Statistics.CRASH(throwable.toString(), getStackTraceString(throwable));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Get a loggable stack trace from a Throwable
     *
     * @param tr An exception to log
     */
    private String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }

        // This is to reduce the amount of log spew that apps do in the non-error
        // condition of the network being unavailable.
        Throwable t = tr;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return "";
            }
            t = t.getCause();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }
}
