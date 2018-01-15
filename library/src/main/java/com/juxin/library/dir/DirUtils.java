package com.juxin.library.dir;

import android.os.Environment;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 媒体文件夹操作工具类
 * Created by ZRP on 2016/9/8.
 */
public class DirUtils {

    private static final String PREFIX_IMAGE = "IMG_";
    private static final String EXTENSION_JPEG = ".jpg";
    private static final String EXTENSION_PNG = ".png";

    private static final DateFormat IMG_FILE_NAME_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);

    /**
     * 保存在 /sdcard/Pictures/xxx，用于普通的保存图片
     */
    public static File createPictureFile(String dirName) {
        String timeStamp = IMG_FILE_NAME_FORMAT.format(new Date());
        String imageFileName = PREFIX_IMAGE + timeStamp + EXTENSION_JPEG;
        return new File(getPicturesDir(dirName), imageFileName);
    }

    /**
     * 保存在 /sdcard/DCIM/xxx，用于拍照图片保存
     */
    public static File createMediaFile(String dirName) {
        String timeStamp = IMG_FILE_NAME_FORMAT.format(new Date());
        String imageFileName = PREFIX_IMAGE + timeStamp + EXTENSION_JPEG;
        return new File(getMediaDir(dirName), imageFileName);
    }

    private static File getPicturesDir(String dirName) {
        File picturesDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), dirName);
        if (!picturesDir.exists()) picturesDir.mkdirs();
        return picturesDir;
    }

    private static File getMediaDir(String dirName) {
        File dcim = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DCIM);
        File mediaDir = new File(dcim, dirName);
        if (!mediaDir.exists()) mediaDir.mkdirs();
        return mediaDir;
    }

    // ----------------------------------------------------------------------------

    /**
     * 获取目录占用空间大小，必须是目录
     *
     * @param dir 目录
     * @return 单位字节
     */
    public static long getDirSize(File dir) {
        if (dir == null) return 0;
        if (!dir.isDirectory()) return 0;

        long dirSize = 0;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    dirSize += file.length();
                } else if (file.isDirectory()) {
                    dirSize += file.length();
                    dirSize += getDirSize(file);
                }
            }
        }
        return dirSize;
    }

    /**
     * 删除目录下的所有文件
     *
     * @param path 目录
     */
    public static void delAllFile(String path, boolean delEmptyFolder) {
        File file = new File(path);

        if (!file.exists()) return;
        if (!file.isDirectory()) return;

        String[] tempList = file.list();
        File temp = null;

        if (tempList == null) return;
        for (String aTempList : tempList) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + aTempList);
            } else {
                temp = new File(path + File.separator + aTempList);
            }
            if (temp.isFile()) temp.delete();
            if (temp.isDirectory()) {
                delAllFile(path + "/" + aTempList, delEmptyFolder);
                delFolder(path + "/" + aTempList, delEmptyFolder);
            }
        }
    }

    /**
     * 删除空目录
     *
     * @param folderPath 目录
     */
    public static void delFolder(String folderPath, boolean delEmptyFolder) {
        delAllFile(folderPath, delEmptyFolder);

        if (delEmptyFolder) {
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            myFilePath.delete();
        }
    }
}
