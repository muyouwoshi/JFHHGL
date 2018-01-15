package com.juxin.library.request;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.sdk.android.httpdns.HttpDnsService;
import com.juxin.library.log.PLogger;
import com.juxin.library.utils.HttpDnsInstance;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import okhttp3.Dns;

/**
 * okhttp DNS防劫持
 * Created by duanzheng on 2017/8/4.
 */
public class OkHttpDns implements Dns {

    private HttpDnsService httpDNS;//httpDNS 解析服务
    private static OkHttpDns instance = null;

    private OkHttpDns(Context context) {
        this.httpDNS = HttpDnsInstance.getInstance(context).getHttpDNS();
    }

    public static OkHttpDns getInstance(Context context) {
        if (instance == null) {
            instance = new OkHttpDns(context);
        }
        return instance;
    }

    @Override
    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
        //通过异步解析接口获取ip
        String ip = httpDNS.getIpByHostAsync(hostname);
        PLogger.i("hostname:" + hostname + "--->" + ip);
        if (!TextUtils.isEmpty(ip)) {
            //如果ip不为null，直接使用该ip进行网络请求
            return Arrays.asList(InetAddress.getAllByName(ip));
        }
        //如果返回null，走系统DNS服务解析域名
        return Dns.SYSTEM.lookup(hostname);
    }
}
