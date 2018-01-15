package com.juxin.predestinate.bean.config.base;

import android.text.TextUtils;

import com.juxin.predestinate.bean.net.BaseData;
import com.juxin.predestinate.module.logic.config.Hosts;
import com.juxin.predestinate.module.util.WebAppDownloader;

import org.json.JSONObject;

/**
 * config/GetSet#h5_config节点
 *
 * @author ZRP
 * @date 2017/7/19
 */
public class H5 extends BaseData {

    // 应用内h5文件
    private String webappRoot;      // 应用内h5默认host
    private String webappUrl;       // 应用内h5文件的下载地址
    private long webappVersion;     // 应用内h5文件的版本号

    // 音视频模块
    private String videoRoot;       // 音视频模块h5默认host
    private String videoUrl;        // 音视频模块h5文件的下载地址
    private long videoVersion;      // 音视频模块h5文件的版本号

    // 游戏模块
    private String gameLolitaRoot;  // 捕女神游戏h5默认host
    private String gameLolitaUrl;   // 捕女神游戏h5文件下载地址
    private long gameLolitaVersion; // 捕女神游戏h5文件的版本号

    // 分享模块
    private String webShareRoot;    // 分享模块h5默认host
    private String webShareUrl;     // 分享模块h5文件的下载地址
    private long webShareVersion;   // 分享模块h5文件的版本号

    @Override
    public void parseJson(String jsonStr) {
        JSONObject jsonObject = getJsonObject(jsonStr);

        // 应用内h5文件
        JSONObject webapp = jsonObject.optJSONObject("webapp");
        if (webapp != null) {
            webappRoot = webapp.optString("web_root");
            if (!TextUtils.isEmpty(webappRoot)) {
                Hosts.WEB_APP_ROOT = webappRoot;
            }
            webappUrl = webapp.optString("url");
            webappVersion = webapp.optLong("version");
            WebAppDownloader.getInstance().download(webappUrl, webappVersion, WebAppDownloader.TYPE_APP);
        }

        // 音视频模块
        JSONObject video = jsonObject.optJSONObject("video");
        if (video != null) {
            videoRoot = video.optString("web_root");
            if (!TextUtils.isEmpty(videoRoot)) {
                Hosts.H5_VIDEO_ROOT = videoRoot;
            }
            videoUrl = video.optString("url");
            videoVersion = video.optLong("version");
            WebAppDownloader.getInstance().download(videoUrl, videoVersion, WebAppDownloader.TYPE_WEB_VIDEO);
        }

        // 游戏模块
        JSONObject gameCatchLolita = jsonObject.optJSONObject("game_catch_lolita");
        if (gameCatchLolita != null) {
            gameLolitaRoot = gameCatchLolita.optString("web_root");
            if (!TextUtils.isEmpty(gameLolitaRoot)) {
                Hosts.H5_GAME_LOLITA_ROOT = gameLolitaRoot;
            }
            gameLolitaUrl = gameCatchLolita.optString("url");
            gameLolitaVersion = gameCatchLolita.optLong("version");
            WebAppDownloader.getInstance().download(gameLolitaUrl, gameLolitaVersion, WebAppDownloader.TYPE_CACHE_GIRL);
        }

        // 分享模块
        JSONObject share = jsonObject.optJSONObject("share");
        if (share != null) {
            webShareRoot = share.optString("web_root");
            if (!TextUtils.isEmpty(webShareRoot)) {
                Hosts.H5_SHARE_ROOT = webShareRoot;
            }
            webShareUrl = share.optString("url");
            webShareVersion = share.optLong("version");
            WebAppDownloader.getInstance().download(webShareUrl, webShareVersion, WebAppDownloader.TYPE_WEB_SHARE);
        }
    }

    public String getWebappRoot() {
        return webappRoot;
    }

    public String getWebappUrl() {
        return webappUrl;
    }

    public long getWebappVersion() {
        return webappVersion;
    }

    public String getVideoRoot() {
        return videoRoot;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public long getVideoVersion() {
        return videoVersion;
    }

    public String getGameLolitaRoot() {
        return gameLolitaRoot;
    }

    public String getGameLolitaUrl() {
        return gameLolitaUrl;
    }

    public long getGameLolitaVersion() {
        return gameLolitaVersion;
    }

    public String getWebShareRoot() {
        return webShareRoot;
    }

    public String getWebShareUrl() {
        return webShareUrl;
    }

    public long getWebShareVersion() {
        return webShareVersion;
    }

    @Override
    public String toString() {
        return "H5{" +
                "webappRoot='" + webappRoot + '\'' +
                ", webappUrl='" + webappUrl + '\'' +
                ", webappVersion=" + webappVersion +
                ", videoRoot='" + videoRoot + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", videoVersion=" + videoVersion +
                ", gameLolitaRoot='" + gameLolitaRoot + '\'' +
                ", gameLolitaUrl='" + gameLolitaUrl + '\'' +
                ", gameLolitaVersion=" + gameLolitaVersion +
                ", webShareRoot='" + webShareRoot + '\'' +
                ", webShareUrl='" + webShareUrl + '\'' +
                ", webShareVersion=" + webShareVersion +
                '}';
    }
}
