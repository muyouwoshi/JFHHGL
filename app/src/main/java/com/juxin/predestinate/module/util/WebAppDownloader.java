package com.juxin.predestinate.module.util;

import android.text.TextUtils;

import com.juxin.library.dir.DirType;
import com.juxin.library.log.PLogger;
import com.juxin.library.log.PSP;
import com.juxin.library.request.DownloadListener;
import com.juxin.library.utils.FileUtil;
import com.juxin.predestinate.module.logic.config.Hosts;
import com.juxin.predestinate.module.logic.model.impl.HttpMgrImpl;
import com.juxin.predestinate.module.logic.model.mgr.HttpMgr;

import java.util.HashMap;

/**
 * 下载H5到本地
 *
 * @author guowz
 */
public class WebAppDownloader {

    private static WebAppDownloader instance;
    public static final int TYPE_APP = 0;
    public static final int TYPE_WEB_VIDEO = 1;
    public static final int TYPE_CACHE_GIRL = 2;
    public static final int TYPE_WEB_SHARE = 3;

    // 应用内H5文件
    public static final String KEY_WEB_APP_ROOT = "sp_key_webapp_root";
    private static final String KEY_WEB_APP_VER = "sp_key_webapp_ver";
    private HashMap<String, String> downMap = new HashMap<>();
    private volatile int counter = 0;

    // 音视频H5文件
    public static final String KEY_WEB_VIDEO_ROOT = "sp_key_webvideo_root";
    private static final String KEY_WEB_VIDEO_VER = "sp_key_webvideo_ver";

    // 游戏：捕女神H5文件
    public static final String KEY_WEB_GAME_LOLITA_ROOT = "sp_key_game_lolita_root";
    private static final String KEY_WEB_GAME_LOLITA_VER = "sp_key_game_lolita_ver";

    // 分享H5文件
    public static final String KEY_WEB_SHARE_ROOT = "sp_key_web_share_root";
    private static final String KEY_WEB_SHARE_VER = "sp_key_web_share_ver";

    private WebAppDownloader() {
    }

    public static WebAppDownloader getInstance() {
        if (instance == null) {
            synchronized (WebAppDownloader.class) {
                if (instance == null) {
                    instance = new WebAppDownloader();
                }
            }
        }
        return instance;
    }

    public void download(final String url, final long ver, final int type) {
        final String spPathKey = getSpPathKey(type);
        final String spVerKey = getSpVerKey(type);
        if (TextUtils.isEmpty(url) || downMap.containsKey(url)
                || (PSP.getInstance().getLong(spVerKey, 0) == ver
                && FileUtil.isExist(PSP.getInstance().getString(spPathKey, "")))) {
            return;
        }
        counter++;
        if (counter > 20) {
            return;
        }
        PLogger.d("download url:" + url + "\ndownload ver:"
                + ver + "\nsp path:" + PSP.getInstance().getString(spPathKey, "")
                + "\nsp ver:" + PSP.getInstance().getLong(spVerKey, 0));
        downMap.put(url, url);
        HttpMgr httpMgr = new HttpMgrImpl();
        final String zipFile = DirType.getCacheDir() + ver + FileUtil.getFileNameFromUrl(url);
        httpMgr.download(url + "?v=" + ver, zipFile, new DownloadListener() {
            @Override
            public void onStart(String url, String filePath) {
            }

            @Override
            public void onProcess(String url, int process, long size) {
            }

            @Override
            public void onSuccess(String url, String filePath) {
                downMap.clear();
                String outputPath = DirType.getWebDir();
                String path = FileUtil.unZipFile(filePath, outputPath);
                if (path == null) {
                    FileUtil.deleteFile(zipFile);
                    download(url, ver, type);
                    return;
                }
                if (counter > 0) {
                    counter--;
                }
                PLogger.d("this file \"" + url + "\" download,unzip ok");
                PLogger.d("counter :" + counter);
                PSP.getInstance().put(spPathKey, path);
                PSP.getInstance().put(spVerKey, ver);
                Hosts.initWebAppUrl();
            }

            @Override
            public void onFail(String url, Throwable throwable) {
                downMap.clear();
                PLogger.d("this file \"" + url + "\" download fail,retry....");
                FileUtil.deleteFile(zipFile);
                download(url, ver, type);
            }
        });
    }

    private String getSpVerKey(int type) {
        switch (type) {
            case TYPE_APP:
                return KEY_WEB_APP_VER;
            case TYPE_WEB_VIDEO:
                return KEY_WEB_VIDEO_VER;
            case TYPE_CACHE_GIRL:
                return KEY_WEB_GAME_LOLITA_VER;
            case TYPE_WEB_SHARE:
                return KEY_WEB_SHARE_VER;
            default:
                return "";
        }
    }

    private String getSpPathKey(int type) {
        switch (type) {
            case TYPE_APP:
                return KEY_WEB_APP_ROOT;
            case TYPE_WEB_VIDEO:
                return KEY_WEB_VIDEO_ROOT;
            case TYPE_CACHE_GIRL:
                return KEY_WEB_GAME_LOLITA_ROOT;
            case TYPE_WEB_SHARE:
                return KEY_WEB_SHARE_ROOT;
            default:
                return "";
        }
    }
}
