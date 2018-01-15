package com.juxin.library.utils;

import android.content.Context;

import com.alibaba.sdk.android.httpdns.HttpDns;
import com.alibaba.sdk.android.httpdns.HttpDnsService;

/**
 * 全局DNS处理实例
 * Created by duanzheng on 2017/8/3.
 */
public class HttpDnsInstance {

    private static HttpDnsInstance instance;
    private static HttpDnsService httpDNS;

    private HttpDnsInstance() {
    }

    public static synchronized HttpDnsInstance getInstance(Context context) {
        if (instance == null || httpDNS == null) {
            instance = new HttpDnsInstance();
            httpDNS = HttpDns.getService(context.getApplicationContext(), "104970");
            httpDNS.setExpiredIPEnabled(true);
            httpDNS.setLogEnabled(false);
            httpDNS.setPreResolveAfterNetworkChanged(true);
        }
        return instance;
    }

    public synchronized HttpDnsService getHttpDNS() {
        return httpDNS;
    }
}
