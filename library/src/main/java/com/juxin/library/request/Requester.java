package com.juxin.library.request;

import android.content.Context;

import com.alibaba.sdk.android.httpdns.HttpDns;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Dns;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * 全局统一的网络请求工具类：retrofit
 * Created by ZRP on 2016/9/8.
 */
public class Requester {

    private static final int CONNECT_TIMEOUT = 5;  //请求超时时间，单位：min
    private static final int RW_TIMEOUT = 5;       //读写超时时间，单位：min

    private static OkHttpClient.Builder builder;
    private static OkHttpClient.Builder dnsBuilder;

    /**
     * 初始化OkHttpClient.Builder()
     */
    public static void initBuilder(final Context context, boolean isDebug, boolean isDns) {
        OkHttpClient.Builder localBuilder = new OkHttpClient.Builder();
        if (isDebug) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
            localBuilder.addInterceptor(loggingInterceptor);
        }

        //配置缓存选项
        File cacheFile = new File(context.getCacheDir(), "responses");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 50);// 50 MiB
        localBuilder.cache(cache);
        //设置文件下载监听
        localBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse
                        .newBuilder()
                        .body(new FileResponseBody(originalResponse, chain.request().url().toString()))
                        .build();
            }
        });
        //设置超时
        localBuilder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.MINUTES);
        localBuilder.readTimeout(RW_TIMEOUT, TimeUnit.MINUTES);
        localBuilder.writeTimeout(RW_TIMEOUT, TimeUnit.MINUTES);
        //错误重连
        localBuilder.retryOnConnectionFailure(true);
        if (isDns) {
            localBuilder.dns(OkHttpDns.getInstance(context));
            dnsBuilder = localBuilder;
        } else {
            builder = localBuilder;
        }

    }

    /**
     * 构造retrofit请求api
     *
     * @param host    host地址
     * @param service retrofit请求api
     * @param <T>     retrofit请求api
     * @return retrofit请求api
     */
    public static <T> T getRequestAPI(String host, Class<T> service, boolean isDns) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(host)
                .client(isDns ? dnsBuilder.build() : builder.build())
                // 此处使用createAsync方式进行请求创建，此时subscribeOn方法失效
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                .build();
        return retrofit.create(service);
    }
}
