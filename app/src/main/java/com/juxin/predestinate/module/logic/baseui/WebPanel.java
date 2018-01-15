package com.juxin.predestinate.module.logic.baseui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import com.alibaba.sdk.android.httpdns.HttpDnsService;
import com.juxin.library.log.PLogger;
import com.juxin.library.utils.NetworkUtils;
import com.juxin.library.view.BasePanel;
import com.juxin.library.view.CustomFrameLayout;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.invoke.Invoker;
import com.juxin.predestinate.module.logic.invoke.WebAppInterface;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebBackForwardList;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.third.wa5.sdk.common.utils.NetUtil;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * 通用的网络请求处理panel
 * Created by ZRP on 2016/12/8.
 */
public class WebPanel extends BasePanel {

    /**
     * web页
     */
    private static final int FRAME_WEB = 0;
    /**
     * 加载失败页
     */
    private static final int FRAME_ERROR = 1;
    /**
     * loading页
     */
    private static final int FRAME_LOADING = 2;

    private String url;
    /**
     * 是否展示panel自身的loading效果，默认展示
     */
    private boolean isShowSelfLoading = true;
    /**
     * 是否由内部控制loading的展示逻辑
     */
    private boolean isLoadingInnerControl = true;
    /**
     * 是否加载出错
     */
    private boolean isLoadError = false;
    private CustomFrameLayout customFrameLayout;
    private WebView webView;

    public WebPanel(Context context) {
        this(context, true);
    }

    /**
     * 构造方法
     *
     * @param context           上下文
     * @param isShowSelfLoading 是否展示panel自身的loading效果
     */
    public WebPanel(Context context, boolean isShowSelfLoading) {
        this(context, true, isShowSelfLoading);
    }

    public WebPanel(Context context, boolean isLoadingInnerControl, boolean isShowSelfLoading) {
        super(context != null ? context : App.getActivity());
        this.isShowSelfLoading = isShowSelfLoading;
        this.isLoadingInnerControl = isLoadingInnerControl;
        setContentView(R.layout.common_web_panel);

        initView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        // android 6.0替换clip的加载动画
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.h5_progress);
            progressBar.setIndeterminateDrawable(getContext().getResources().getDrawable(R.drawable.loading_progressbar_higher));
        }

        webView = (WebView) findViewById(R.id.webView);
        customFrameLayout = (CustomFrameLayout) findViewById(R.id.customFrameLayout);
        customFrameLayout.setList(new int[]{R.id.webView, R.id.common_net_error, R.id.common_loading});
        customFrameLayout.showOfIndex(FRAME_LOADING);
        findViewById(R.id.error_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUrl(url);
            }
        });

        WebSettings webSettings = webView.getSettings();
        webSettings.setDefaultTextEncodingName("utf-8");
        //设置允许访问文件数据
        webSettings.setAllowFileAccess(true);
        webSettings.setJavaScriptEnabled(true);
        //不从缓存加载，确保每次进入的时候都是从服务器请求的最新页面
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        //网页自动播放媒体
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        // ---增加网页清晰度 start---
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        // ---增加网页清晰度 end---

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView webView, String s) {
                super.onReceivedTitle(webView, s);
                if (webListener != null) {
                    webListener.onTitle(s);
                }
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                PLogger.d("-----shouldOverrideUrlLoading---->" + url);
                String ref = NetworkUtils.getDomainName(webView.getUrl());
                Map<String, String> extraHeaders = new HashMap<>();
                extraHeaders.put("Referer", ref);
                //此处必须使用shouldOverrideUrlLoading回调里的两个参数
                webView.loadUrl(url, extraHeaders);
                return true;
            }

            @Override
            public void onReceivedError(WebView webView, int errorCode, String description, String failingUrl) {
                super.onReceivedError(webView, errorCode, description, failingUrl);
                PLogger.d("-----onReceivedError---->errorCode: " + errorCode +
                        ", description: " + description + ", failingUrl: " + failingUrl);
                if (!TextUtils.isEmpty(failingUrl) && failingUrl.toLowerCase().startsWith("file://") &&
                        (failingUrl.toLowerCase().endsWith(".png")
                                || failingUrl.toLowerCase().endsWith(".jpeg")
                                || failingUrl.toLowerCase().endsWith(".jpg")
                                || failingUrl.toLowerCase().endsWith(".gif"))) {
                    if (!new File(failingUrl).exists()) {
                        return;
                    }
                }

                try {
                    URL url = new URL(failingUrl);
                    String path = url.getPath();
                    if (!TextUtils.isEmpty(path) && (!path.toLowerCase().endsWith(".js")
                            && !path.toLowerCase().endsWith(".htm") && !path.toLowerCase().endsWith(".html"))) {
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                isLoadError = true;
                customFrameLayout.showOfIndex(FRAME_ERROR);
                if (webListener != null) {
                    webListener.onLoadFinish(WebLoadStatus.ERROR);
                }
            }

            @Override
            public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
                super.onReceivedError(webView, webResourceRequest, webResourceError);
                PLogger.d("-----onReceivedError---->webResourceRequest: " + webResourceRequest + ", webResourceError: " + webResourceError);
                if (webResourceRequest != null && webResourceRequest.getUrl() != null
                        && !TextUtils.isEmpty(webResourceRequest.getUrl().getPath())
                        && "file".equals(webResourceRequest.getUrl().getScheme())
                        && (webResourceRequest.getUrl().getPath().toLowerCase().endsWith(".png")
                        || webResourceRequest.getUrl().getPath().toLowerCase().endsWith(".jpeg")
                        || webResourceRequest.getUrl().getPath().toLowerCase().endsWith(".jpg")
                        || webResourceRequest.getUrl().getPath().toLowerCase().endsWith(".gif"))) {
                    String url = webResourceRequest.getUrl().getScheme() + "://" + webResourceRequest.getUrl().getPath();
                    if (!new File(url).exists()) {
                        return;
                    }
                }
                if (webResourceRequest != null && webResourceRequest.getUrl() != null
                        && !TextUtils.isEmpty(webResourceRequest.getUrl().getPath())
                        && !webResourceRequest.getUrl().getPath().toLowerCase().endsWith(".js")
                        && !webResourceRequest.getUrl().getPath().toLowerCase().endsWith(".htm")
                        && !webResourceRequest.getUrl().getPath().toLowerCase().endsWith(".html")) {
                    return;
                }

                isLoadError = true;
                customFrameLayout.showOfIndex(FRAME_ERROR);
                if (webListener != null) {
                    webListener.onLoadFinish(WebLoadStatus.ERROR);
                }
            }

            @Override
            public void onPageFinished(WebView webView, String url) {
                PLogger.d("-----onPageFinished---->" + url);
                // [单独处理url为空的情况](https://stackoverflow.com/questions/30426017/webview-clicking-on-link-results-in-aboutblank?rq=1)
                if ("about:blank".equals(url) && webView.getTag() != null) {
                    webView.loadUrl(webView.getTag().toString());
                } else {
                    webView.setTag(url);
                }
                if (isLoadingInnerControl && !isLoadError) {
                    hideLoading();
                }
                if (webListener != null) {
                    webListener.onLoadFinish(WebLoadStatus.FINISH);
                }
            }

            @Override
            public void onPageStarted(WebView webView, String url, Bitmap bitmap) {
                super.onPageStarted(webView, url, bitmap);
                PLogger.d("-----onPageStarted---->" + url);
                isLoadError = false;
            }

            @Override
            public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
                sslErrorHandler.proceed();
            }

            // 该方法从API 21开始引入
            @SuppressLint("NewApi")
            @Override
            public com.tencent.smtt.export.external.interfaces.WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                if (request != null && request.getUrl() != null && request.getMethod().equalsIgnoreCase("get")) {
                    String scheme = request.getUrl().getScheme().trim();
                    String url = request.getUrl().toString();
                    //Log.d(TAG, "request.getUrl().toString(): " + url+"-->"+scheme);
                    // 假设我们对所有css文件的网络请求进行httpdns解析
                    com.tencent.smtt.export.external.interfaces.WebResourceResponse webResourceResponse = httpDnsDispose(App.httpdns, scheme, url);
                    return webResourceResponse != null ? webResourceResponse : super.shouldInterceptRequest(view, request);
                }
                return super.shouldInterceptRequest(view, request);
            }

            // 从API 11开始
            @Override
            public com.tencent.smtt.export.external.interfaces.WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (!TextUtils.isEmpty(url) && Uri.parse(url).getScheme() != null) {
                    String scheme = Uri.parse(url).getScheme().trim();
                    //Log.d(TAG, "url: " + url);
                    // 假设我们对所有css文件的网络请求进行httpdns解析
                    com.tencent.smtt.export.external.interfaces.WebResourceResponse webResourceResponse = httpDnsDispose(App.httpdns, scheme, url);
                    return webResourceResponse != null ? webResourceResponse : super.shouldInterceptRequest(view, url);
                }
                return super.shouldInterceptRequest(view, url);
            }
        });
        webView.addJavascriptInterface(new WebAppInterface(getContext(), webView), "Android");
        webView.requestFocus();
    }

    private com.tencent.smtt.export.external.interfaces.WebResourceResponse httpDnsDispose(HttpDnsService httpdns, String scheme, String url) {
        if ((scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https")) /*&& url.contains(".css")*/) {
            try {
                URL oldUrl = new URL(url);
                oldUrl.openConnection();
                //HttpsURLConnection connection;
                // 异步获取域名解析结果
                String ip = httpdns.getIpByHostAsync(oldUrl.getHost());
                if (!TextUtils.isEmpty(ip)) {
                    // 通过HTTPDNS获取IP成功，进行URL替换和HOST头设置
                    //Log.d(TAG, "Get IP: " + ip + " for host: " + oldUrl.getHost() + " from HTTPDNS successfully!");
                    String newUrl = url.replaceFirst(oldUrl.getHost(), ip);

                    String contentType = "";
                    InputStream inputStream;

                    if (!TextUtils.isEmpty(url) && url.toLowerCase().startsWith("https")) {
                        HttpsURLConnection httpsConnection = (HttpsURLConnection) new URL(newUrl).openConnection();
                        // 设置HTTP请求头Host域
                        httpsConnection.setRequestProperty("Host", oldUrl.getHost());
                        SSLContext sc = SSLContext.getInstance("SSL");
                        sc.init(null, trustManager, new SecureRandom());
                        httpsConnection.setSSLSocketFactory(sc.getSocketFactory());
                        httpsConnection.setHostnameVerifier(hostnameVerifier);
                        contentType = httpsConnection.getContentType();
                        inputStream = httpsConnection.getInputStream();
                    } else {
                        URLConnection connection = new URL(newUrl).openConnection();
                        connection.setRequestProperty("Host", oldUrl.getHost());
                        contentType = connection.getContentType();
                        inputStream = connection.getInputStream();
                    }

                    // Log.d(TAG, "ContentType: " + contentType+"-->"+url);
                    if (!TextUtils.isEmpty(contentType) && inputStream != null) {
                        String[] contentTypeArr = contentType.split(";");
                        if (contentTypeArr.length > 1) {
                            String codeType = "UTF-8";
                            if (!TextUtils.isEmpty(contentTypeArr[1])
                                    && contentTypeArr[1].trim().contains("charset=")
                                    && ("text/html".equals(contentTypeArr[0].trim())
                                    || "application/javascript".equals(contentTypeArr[0].trim()))) {
                                String[] codeTypeArr = contentTypeArr[1].split("=");
                                if (codeTypeArr.length > 1
                                        && !TextUtils.isEmpty(codeTypeArr[1])
                                        && ("UTF-8".equals(codeTypeArr[1].toUpperCase())
                                        || "GBK".equals(codeTypeArr[1].toUpperCase())
                                        || "GB2312".equals(codeTypeArr[1].toUpperCase()))) {
                                    codeType = codeTypeArr[1].toUpperCase();
                                    //Log.d(TAG,"codeType:"+codeType);
                                }
                                return new com.tencent.smtt.export.external.interfaces.WebResourceResponse(contentTypeArr[0], codeType, inputStream);
                            }
                            return null;
                        } else if (!TextUtils.isEmpty(contentType) && "application/x-javascript".equals(contentType.trim())) {
                            return new com.tencent.smtt.export.external.interfaces.WebResourceResponse(contentType, "UTF-8", inputStream);
                        } else {
                            return new com.tencent.smtt.export.external.interfaces.WebResourceResponse(contentType, "UTF-8", inputStream);
                        }
                    } else {
                        return new com.tencent.smtt.export.external.interfaces.WebResourceResponse(contentType, "UTF-8", inputStream);
                    }
                }
                return null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    HostnameVerifier hostnameVerifier = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    TrustManager[] trustManager = new TrustManager[]{new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }};

    /**
     * 加载url
     */
    public void loadUrl(String url) {
        if (TextUtils.isEmpty(url) || customFrameLayout == null || webView == null) {
            return;
        }
        this.url = url;

        // 判断有无网络
        if (!NetUtil.getInstance().isNetConnect(getContext())) {
            customFrameLayout.showOfIndex(FRAME_ERROR);
        } else {
            customFrameLayout.showOfIndex(isShowSelfLoading ? FRAME_LOADING : FRAME_WEB);
            webView.loadUrl(url);
        }
    }

    /**
     * @return 获取展示的WebView实例
     */
    public WebView getWebView() {
        return webView;
    }

    public CustomFrameLayout getCustomFrameLayout() {
        return customFrameLayout;
    }

    public boolean isLoadError() {
        return isLoadError;
    }

    /**
     * 设置webView背景透明
     */
    public void setWebViewTransparent() {
        if (webView != null) {
            webView.setBackgroundColor(0);
        }
    }

    /**
     * 隐藏滚动条
     */
    public void hideScrollBar() {
        if (webView != null) {
            webView.setVerticalScrollBarEnabled(false);
            webView.setHorizontalScrollBarEnabled(false);
            if (webView.getX5WebViewExtension() != null) {
                webView.getX5WebViewExtension().setScrollBarFadingEnabled(false);
            }
        }
    }

    /**
     * webPanel重设webView状态
     *
     * @param isHiddenChange 是否为fragment的hide状态变更，与onResume进行区分
     */
    public void resetWebView(boolean isHiddenChange) {
        Invoker.getInstance().setWebView(getWebView());
        if (isHiddenChange) {
            Invoker.getInstance().doInJS(Invoker.JSCMD_refresh_web, null);
        } else {
            loadUrl(url);
        }
    }

    public void onResume() {
        if (webView != null) {
            resetWebView(false);
            webView.onResume();
            webView.resumeTimers();
        }
    }

    public void onPause() {
        if (webView != null) {
            webView.onPause();
            webView.pauseTimers();
        }
        Invoker.getInstance().setWebView(null);
    }

    /**
     * 清除webView引用并置空，最好在引用webPanel页面的super.onDestroy()之前进行调用
     */
    public void onDestroy() {
        if (webView != null) {
            webView.removeJavascriptInterface("Android");
            webView.removeAllViews();
            webView.destroy();
            webView = null;
        }
    }

    /**
     * @return 判断webView是否可以回退，并维护内部回退逻辑
     */
    public boolean canGoBack() {
        WebBackForwardList history = webView.copyBackForwardList();
        int index = -1;
        String url = null;

        while (webView.canGoBackOrForward(index)) {
            if (!history.getItemAtIndex(history.getCurrentIndex() + index).getUrl().equals("about:blank")) {
                webView.goBackOrForward(index);
                url = history.getItemAtIndex(-index).getUrl();
                //Log.e("tag", "first non empty" + url);
                break;
            }
            index--;
        }
        return url != null;
    }

    /**
     * 隐藏loading并展示WebView
     */
    public void hideLoading() {
        customFrameLayout.showOfIndex(FRAME_WEB);
    }

    private WebListener webListener;

    /**
     * 设置网页标题监听
     */
    public void setWebListener(WebListener webListener) {
        this.webListener = webListener;
    }

    /**
     * 网页标题监听
     */
    public interface WebListener {
        /**
         * 解析网页得到title
         *
         * @param title html title
         */
        void onTitle(String title);

        /**
         * 页面加载完成，由多方控制，如onPageFinished，cmd-hide_loading
         */
        void onLoadFinish(WebLoadStatus loadStatus);
    }

    /**
     * 网络加载状态
     */
    public enum WebLoadStatus {
        ERROR, FINISH
    }
}
