package com.juxin.library.dir;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * SD卡工具类
 *
 * @author Kind
 */
public class SDCardUtil {

    // -----------------------伪·SD状态获取----------------------------

    /**
     * 创建文件夹目录
     *
     * @param path 目录路径
     * @return 是否创建成功
     */
    public static boolean createDirFile(String path) {
        File dir = new File(path);
        return dir.exists() || dir.mkdirs();
    }

    /**
     * 创建文件，如果文件已存在，返回存在的文件；如果文件不存在就进行创建
     *
     * @param path 文件路径
     * @return 创建的文件, 如果文件创建失败，返回null
     */
    public static File createNewFile(String path) {
        File file = new File(path);
        if (file.exists()) return file;

        try {
            return file.createNewFile() ? file : null;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 删除文件夹
     *
     * @param folderPath 文件夹的路径
     * @return 是否删除成功
     */
    public static boolean delFolder(String folderPath) {
        delAllFile(folderPath);
        File myFilePath = new File(folderPath);
        return myFilePath.delete();
    }

    /**
     * 删除文件或目录
     *
     * @param path 路径
     */
    public static void delAllFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);
                delFolder(path + "/" + tempList[i]);
            }
        }
    }

    /**
     * 获取目录文件大小
     *
     * @param dir 目录
     * @return 文件目录大小
     */
    public static long getDirSize(File dir) {
        if (dir == null || !dir.isDirectory()) {
            return 0;
        }
        long dirSize = 0;
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                dirSize += file.length();
            } else if (file.isDirectory()) {
                dirSize += file.length();
                dirSize += getDirSize(file); //递归调用继续统计
            }
        }
        return dirSize;
    }

    /**
     * 清除缓存目录
     *
     * @param dir     目录
     * @param curTime 当前系统时间
     * @return 清除的文件数
     */
    public static int clearCacheFolder(File dir, long curTime) {
        int deletedFiles = 0;
        if (dir != null && dir.isDirectory()) {
            try {
                for (File child : dir.listFiles()) {
                    if (child.isDirectory()) {
                        deletedFiles += clearCacheFolder(child, curTime);
                    }
                    if (child.lastModified() < curTime) {
                        if (child.delete()) {
                            deletedFiles++;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return deletedFiles;
    }

    /**
     * 换算文件大小
     *
     * @param size
     * @return
     */
    public static String formatFileSize(long size) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "unknown size";
        if (size < 1024) {
            fileSizeString = df.format((double) size) + "B";
        } else if (size < 1048576) {
            fileSizeString = df.format((double) size / 1024) + "K";
        } else if (size < 1073741824) {
            fileSizeString = df.format((double) size / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) size / 1073741824) + "G";
        }
        return fileSizeString;
    }

    // -----------------------真·SD状态获取----------------------------

    /**
     * 获取所有sd卡信息
     *
     * @param context 上下文
     * @return 所有sd卡信息List<StatFs>
     */
    public static List<StatFs> getSdcardPathAll(Context context) {
        List<StatFs> statFses = new LinkedList<>();
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Class<?>[] paramClasses = {};
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", paramClasses);
            getVolumePathsMethod.setAccessible(true);
            Object[] params = {};
            Object invoke = getVolumePathsMethod.invoke(storageManager, params);
            for (int i = 0; i < ((String[]) invoke).length; i++) {
                StatFs stat = new StatFs(((String[]) invoke)[i]);
                if (stat.getBlockCount() > 0) statFses.add(stat);
            }
        } catch (NoSuchMethodException | IllegalArgumentException | IllegalAccessException
                | InvocationTargetException | ClassCastException e) {
            e.printStackTrace();
        }
        return statFses;
    }

    /**
     * 获取每个sd卡可用的内存大小(单位MB) todo 待优化
     *
     * @param context 上下文
     * @return 每个sd卡可用的内存大小(单位MB)
     */
    public static List<Integer> getSdcardAvailableAll(Context context) {
        List<Integer> availableAll = new LinkedList<>();
        List<StatFs> sdcardPathAll = getSdcardPathAll(context);
        for (StatFs statFs : sdcardPathAll) {
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2){
                availableAll.add(statFs.getBlockSize() * statFs.getAvailableBlocks() / 1024 / 1024);
            }else{
                availableAll.add((int) ( statFs.getAvailableBytes() / 1024 / 1024 ));
            }
        }
        return availableAll;
    }

    /**
     * 获取每个sd卡已用的内存大小(单位MB) todo 待优化
     *
     * @param context 上下文
     * @return 每个sd卡已用的内存大小(单位MB)
     */
    public static List<Integer> getSdcardUsageAll(Context context) {
        List<Integer> usageAll = new LinkedList<>();
        List<StatFs> sdcardPathAll = getSdcardPathAll(context);
        for (StatFs statFs : sdcardPathAll) {
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                usageAll.add(statFs.getBlockSize() * (statFs.getBlockCount() - statFs.getAvailableBlocks()) / 1024 / 1024);
            }else{
                usageAll.add((int) ((statFs.getTotalBytes()-statFs.getAvailableBytes()) / 1024 / 1024 ));
            }
        }
        return usageAll;
    }

    /**
     * 检测sdcard是否可用
     *
     * @return true为可用，否则为不可用
     */
    public static boolean isSdcardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * Checks if there is enough Space on SDCard
     *
     * @param updateSize Size to Check
     * @return True if the Update will fit on SDCard, false if not enough space
     * on SDCard Will also return false, if the SDCard is not mounted as
     * read/write
     */
    public static boolean enoughSpaceOnSdCard(long updateSize) {
        return isSdcardExist() && (updateSize < getRealSizeOnSdcard());
    }

    /**
     * get the space is left over on sdcard
     */
    public static long getRealSizeOnSdcard() {
        File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * Checks if there is enough Space on phone self
     */
    public static boolean enoughSpaceOnPhone(long updateSize) {
        return getRealSizeOnPhone() > updateSize;
    }

    /**
     * get the space is left over on phone self
     */
    public static long getRealSizeOnPhone() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return blockSize * availableBlocks;
    }
}