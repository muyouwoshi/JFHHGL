package com.juxin.predestinate.module.logic.webserver;

import android.text.TextUtils;
import android.util.Log;

import com.juxin.library.utils.NetworkUtils;
import com.juxin.predestinate.module.logic.config.Hosts;

/**
 * 本地服务地址配置管理
 * Created by Su on 2017/1/6.
 */

public enum WebServiceConfig {

    // ================================ 地址配置 ==============================================
    reqVideoWebRotary("/VideoWeb/index.html"),
    liveRoomWeb("/VideoWeb/live.html"),
    liteLiveRoom("/VideoWeb/alive_combine.html"),
    catchGirl("/catch_girl/index.html"),

    // 占位
    LastUrl("");

    public static final int port = 9088;  // WebService服务端口
    private String url = null;            // 根据文件存储位置配置

    WebServiceConfig(final String url) {
        this.url = url;
    }

    /**
     * 获取完整Url。
     * <p>
     * TODO 未获取到本地地址处理;  端口号统一管理
     */
    public String getUrl() {
        String host = getLogicServer();
        if (TextUtils.isEmpty(host)) {
            return "处理地址获取错误";
        } else if (host.equals(Hosts.NO_HOST)) {
            return url;
        }
        return host + url;
    }

    /**
     * 获取本地WebServer服务host
     *
     * @return
     */
    private String getLogicServer() {  // 本地服务host
        String localIp = NetworkUtils.getIpAddressString();
        Log.d("netIPAddr: ", localIp);
        if (TextUtils.isEmpty(localIp)) {
            return "";
        }
//        return "http://" + localIp + ":" + port;
        return "http://localhost:" + port;
    }
}
