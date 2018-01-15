package com.juxin.predestinate.module.util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by wizard on 2016/12/6.
 */
public class PerformanceHelper {

    static final int DEVICEINFO_UNKNOWN = -1;
    static final int HIGH_RAM = 512;
    static final int HIGH_CPU_FREQ = 1500 * 1000;

    private static long getAvailMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return mi.availMem;
    }

    private static final FileFilter CPU_FILTER = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            String path = pathname.getName();
            //regex is slow, so checking char by char.
            if (path.startsWith("cpu")) {
                for (int i = 3; i < path.length(); i++) {
                    if (path.charAt(i) < '0' || path.charAt(i) > '9') {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    };

    public static int getNumberOfCPUCores() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            // Gingerbread doesn't support giving a single application access to both cores, but a
            // handful of devices (Atrix 4G and Droid X2 for example) were released with a dual-core
            // chipset and Gingerbread; that can let an app in the background run without impacting
            // the foreground application. But for our purposes, it makes them single core.
            return 1;
        }
        int cores;
        try {
            cores = new File("/sys/devices/system/cpu/").listFiles(CPU_FILTER).length;
        } catch (SecurityException e) {
            cores = DEVICEINFO_UNKNOWN;
        } catch (NullPointerException e) {
            cores = DEVICEINFO_UNKNOWN;
        }
        return cores;
    }

    private static int getCPUMaxFreqKHz() {
        int maxFreq = DEVICEINFO_UNKNOWN;
        try {
            int cpuCnt = getNumberOfCPUCores();
            for (int i = 0; i < cpuCnt; i++) {
                String filename =
                        "/sys/devices/system/cpu/cpu" + i + "/cpufreq/cpuinfo_max_freq";
                File cpuInfoMaxFreqFile = new File(filename);
                if (cpuInfoMaxFreqFile.exists()) {
                    byte[] buffer = new byte[128];
                    FileInputStream stream = new FileInputStream(cpuInfoMaxFreqFile);
                    try {
                        stream.read(buffer);
                        int endIndex = 0;
                        //Trim the first number out of the byte buffer.
                        while (buffer[endIndex] >= '0' && buffer[endIndex] <= '9'
                                && endIndex < buffer.length) endIndex++;
                        String str = new String(buffer, 0, endIndex);
                        Integer freqBound = Integer.parseInt(str);
                        if (freqBound > maxFreq) maxFreq = freqBound;
                    } catch (NumberFormatException e) {
                        //Fall through and use /proc/cpuinfo.
                    } finally {
                        stream.close();
                    }
                }
            }
            if (maxFreq == DEVICEINFO_UNKNOWN) {
//                FileInputStream stream = new FileInputStream("/proc/cpuinfo");
//                try {
//                    int freqBound = parseFileForValue("cpu MHz", stream);
//                    freqBound *= 1000; //MHz -> kHz
//                    if (freqBound > maxFreq) maxFreq = freqBound;
//                } finally {
//                    stream.close();
//                }
            }
        } catch (IOException e) {
            maxFreq = DEVICEINFO_UNKNOWN; //Fall through and return unknown.
        }
        return maxFreq;
    }

    public static boolean isHighPerformance(Context context) {
        if (getAvailMemory(context) < HIGH_RAM) {
            return false;
        }

        if (getNumberOfCPUCores() < 4) {
            return false;
        }

        if (getCPUMaxFreqKHz() < HIGH_CPU_FREQ) {
            return false;
        }

        return true;
    }
}
