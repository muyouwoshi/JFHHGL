package com.juxin.library.dir;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.juxin.library.R;

import java.io.File;

/**
 * 软件名目录下文件夹集，通过get方法获取目录的同时自动进行创建[目录末尾已拼接separator]
 * Created by ZRP on 2017/4/11.
 */
public final class DirType {

    /**
     * 文件夹名称
     */
    private enum Dir {
        root, cache, download, apk, video, voice, image, upload, web, glide
    }

    private static final String SD_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static final String SPARE_ROOT = "xiaou";//备用文件夹名称，防止初始化时传空

    private static String DIR_ROOT = SD_ROOT + File.separator + SPARE_ROOT + File.separator;
    private static String CACHE = DIR_ROOT + Dir.cache + File.separator;
    private static String DOWNLOAD = DIR_ROOT + Dir.download + File.separator;
    private static String APK = DIR_ROOT + Dir.apk + File.separator;
    private static String VIDEO = DIR_ROOT + Dir.video + File.separator;
    private static String VOICE = DIR_ROOT + Dir.voice + File.separator;
    private static String IMAGE = DIR_ROOT + Dir.image + File.separator;
    private static String UPLOAD = DIR_ROOT + Dir.upload + File.separator;
    /**
     * 隐藏的web资源文件夹，双重路径确保出错时可以通过删除外侧文件夹进行重置
     */
    private static String WEB = DIR_ROOT + Dir.web + File.separator + "." + Dir.web + File.separator;
    private static String GLIDE_CACHE = DIR_ROOT + Dir.glide + File.separator;

    /**
     * 初始化
     *
     * @param context 上下文
     * @param appName 在sd卡根目录创建的文件目录名
     */
    public static void init(Context context, String appName) {
        appName = TextUtils.isEmpty(appName) ? SPARE_ROOT : appName;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            DIR_ROOT = SD_ROOT + File.separator + appName + File.separator;
        } else {
            DIR_ROOT = context.getFilesDir().getAbsolutePath() + File.separator + appName + File.separator;
        }
        CACHE = DIR_ROOT + Dir.cache + File.separator;
        DOWNLOAD = DIR_ROOT + Dir.download + File.separator;
        APK = DIR_ROOT + Dir.apk + File.separator;
        VIDEO = DIR_ROOT + Dir.video + File.separator;
        VOICE = DIR_ROOT + Dir.voice + File.separator;
        IMAGE = DIR_ROOT + Dir.image + File.separator;
        UPLOAD = DIR_ROOT + Dir.upload + File.separator;
        WEB = DIR_ROOT + Dir.web + File.separator + "." + Dir.web + File.separator;
    }

    /**
     * 文件夹是否存在/是否创建成功（不存在则创建）
     *
     * @param folder 文件夹路径
     * @return 文件夹是否存在/是否创建成功
     */
    public static boolean isFolderExists(String folder) {
        File file = new File(folder);
        return file.exists() || file.mkdirs();
    }

    /**
     * @return 获取软件名根目录
     */
    public static String getRootDir() {
        return isFolderExists(DIR_ROOT) ? DIR_ROOT : "";
    }

    /**
     * @return 获取缓存文件夹目录
     */
    public static String getCacheDir() {
        return isFolderExists(CACHE) ? CACHE : "";
    }

    /**
     * @return 获取下载文件夹目录
     */
    public static String getDownloadDir() {
        return isFolderExists(DOWNLOAD) ? DOWNLOAD : "";
    }

    /**
     * @return 获取apk文件夹目录
     */
    public static String getApkDir() {
        return isFolderExists(APK) ? APK : "";
    }

    /**
     * @return 获取视频文件夹目录
     */
    public static String getVideoDir() {
        return isFolderExists(VIDEO) ? VIDEO : "";
    }

    /**
     * @return 获取音频文件夹目录
     */
    public static String getVoiceDir() {
        return isFolderExists(VOICE) ? VOICE : "";
    }

    /**
     * @return 获取图片文件夹目录
     */
    public static String getImageDir() {
        return isFolderExists(IMAGE) ? IMAGE : "";
    }

    /**
     * @return 获取上传临时存储文件夹目录
     */
    public static String getUploadDir() {
        return isFolderExists(UPLOAD) ? UPLOAD : "";
    }

    /**
     * @return Glide缓存目录
     */
    public static String getGliceCacheDir() {
        return isFolderExists(GLIDE_CACHE) ? GLIDE_CACHE : "";
    }

    /**
     * @return 获取本地解压的网页文件夹目录
     */
    public static String getWebDir() {
        return isFolderExists(WEB) ? WEB : "";
    }

    // --------------------------------------------------------------------------------

    /**
     * @return 获取格式化的缓存大小
     */
    public static String getFormatCacheSize(Context context) {
        long dirSize = DirUtils.getDirSize(new File(getCacheDir()));
        if (dirSize < 1024 * 1024) {
            return context.getString(R.string.cache_state_good);
        }
        if (dirSize < 1024 * 1024 * 1024 && ((double) dirSize / (1024 * 1024)) < 50) {
            return context.getString(R.string.cache_state_little);
        }
        return context.getString(R.string.cache_state_huge);
    }

    /**
     * @return 获取总缓存大小
     */
    public static String getCacheSize(Context context) {
        // 计算缓存大小
        long fileSize = 0;
        String cacheSize = "0KB";
        File filesDir = context.getFilesDir();
        File cacheDir = context.getCacheDir();

        fileSize += SDCardUtil.getDirSize(filesDir);
        fileSize += SDCardUtil.getDirSize(cacheDir);
        // 2.2版本才有将应用缓存转移到sd卡的功能
        if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            File externalCacheDir = getExternalCacheDir(context);
            fileSize += SDCardUtil.getDirSize(externalCacheDir);
        }
        long dirSize = DirUtils.getDirSize(new File(getCacheDir()));
        fileSize += dirSize;
        long glideSize = DirUtils.getDirSize(new File(getGliceCacheDir()));
        fileSize += glideSize;
        if (fileSize > 0) {
            cacheSize = SDCardUtil.formatFileSize(fileSize);
        }
        return cacheSize;
    }

    /**
     * 清除缓存
     */
    public static void clearCache(Context context) {
        DirUtils.delAllFile(getCacheDir(), false);
        DirUtils.delAllFile(getGliceCacheDir(), false);
        SDCardUtil.clearCacheFolder(context.getFilesDir(), System.currentTimeMillis());
        SDCardUtil.clearCacheFolder(context.getCacheDir(), System.currentTimeMillis());
        // 2.2版本才有将应用缓存转移到sd卡的功能
        if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            SDCardUtil.clearCacheFolder(getExternalCacheDir(context), System.currentTimeMillis());
        }
    }

    /**
     * 判断当前版本是否兼容目标版本的方法
     */
    private static boolean isMethodsCompat(int VersionCode) {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        return currentVersion >= VersionCode;
    }

    private static File getExternalCacheDir(Context context) {
        return context.getExternalCacheDir();
    }
}
