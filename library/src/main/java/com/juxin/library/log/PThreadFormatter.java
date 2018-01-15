package com.juxin.library.log;

import android.os.Process;

import com.elvishew.xlog.formatter.thread.ThreadFormatter;

/**
 * 线程进程信息打印
 * Created by ZRP on 2017/6/17.
 */
public class PThreadFormatter implements ThreadFormatter {

    @Override
    public String format(Thread data) {
        return "Thread: " + data.getName() + " , Pid: " + Process.myPid();
    }
}
