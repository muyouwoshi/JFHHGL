package com.juxin.predestinate.ui.pay.wepayother.h5;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.sdk.android.httpdns.HttpDnsService;
import com.juxin.library.log.PLogger;
import com.juxin.library.log.PToast;
import com.juxin.library.utils.NetworkUtils;
import com.juxin.library.view.BasePanel;
import com.juxin.library.view.CustomFrameLayout;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.invoke.Invoker;
import com.juxin.predestinate.module.logic.invoke.WebAppInterface;
import com.juxin.predestinate.module.util.PerformanceHelper;
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
import java.net.MalformedURLException;
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
 * 专用的支付panel
 *
 * @author IQQ
 * @date 2017/05/27
 */
public class WebPanelPayH5 extends BasePanel {
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
     * 是否由内部控制loading的展示逻辑
     */
    private boolean isLoadingInnerControl = true;
    /**
     * 是否加载出错
     */
    private boolean isLoadError = false;

    private CustomFrameLayout customFrameLayout;
    private WebView webView;
    private Context context;
    private H5PayStart h5PayStart;

    public WebPanelPayH5(Context context, String url, boolean isLoadingInnerControl, H5PayStart h5PayStart) {
        super(context);
        this.context = context;
        this.url = url;
        this.isLoadingInnerControl = isLoadingInnerControl;
        setContentView(R.layout.common_web_panel);
        this.h5PayStart = h5PayStart;
        initView();
    }

    private void toWeixin(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            ((Activity) context).startActivityForResult(intent, PayWebActivity.payResutl);
        } else {
            PToast.showShort("微信未安装");
        }
        if (null != h5PayStart) {
            h5PayStart.OnPayStart();
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        webView = (WebView) findViewById(R.id.webView);
        customFrameLayout = (CustomFrameLayout) findViewById(R.id.customFrameLayout);
        customFrameLayout.setList(new int[]{R.id.webView, R.id.common_net_error, R.id.common_loading});
        customFrameLayout.showOfIndex(FRAME_LOADING);

        findViewById(R.id.error_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUrl();
            }
        });

        WebSettings webSettings = webView.getSettings();
        webSettings.setDefaultTextEncodingName("utf-8");
        //设置允许访问文件数据
        webSettings.setAllowFileAccess(true);
        webSettings.setJavaScriptEnabled(true);
        //不从缓存加载，确保每次进入的时候都是从服务器请求的最新页面
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        if (PerformanceHelper.isHighPerformance(getContext())) {
            /*为高内存手机 增加清晰度*/
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadWithOverviewMode(true);
        }

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
                if (url.startsWith("weixin://")) {
                    toWeixin(url);
                } else {
                    String ref = NetworkUtils.getDomainName(webView.getUrl());
                    Map extraHeaders = new HashMap();
                    extraHeaders.put("Referer", ref);
                    webView.loadUrl(url, extraHeaders);
                }
                return true;
            }

            @Override
            public void onReceivedError(WebView webView, int errorCode, String description, String failingUrl) {
                super.onReceivedError(webView, errorCode, description, failingUrl);
                PLogger.d("-----onReceivedError---->errorCode: " + errorCode +
                        ", description: " + description + ", failingUrl: " + failingUrl);
                if (!TextUtils.isEmpty(failingUrl) && failingUrl.toLowerCase().startsWith("file://") &&
                        (failingUrl.toLowerCase().endsWith(".png") || failingUrl.toLowerCase().endsWith(".jpeg") || failingUrl.toLowerCase().endsWith(".jpg") || failingUrl.toLowerCase().endsWith(".gif"))) {
                    if (!new File(failingUrl).exists()) {
                        return;
                    }
                }

                try {
                    URL url = new URL(failingUrl);
                    String path = url.getPath();
                    if (!TextUtils.isEmpty(path) && (!path.toLowerCase().endsWith(".js") && !path.toLowerCase().endsWith(".htm") && !path.toLowerCase().endsWith(".html"))) {
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                isLoadError = true;
                customFrameLayout.showOfIndex(FRAME_ERROR);
                LoadingDialog.closeLoadingDialog(500);
                if (webListener != null) {
                    webListener.onLoadFinish(WebLoadStatus.ERROR);
                }
            }

            @Override
            public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
                super.onReceivedError(webView, webResourceRequest, webResourceError);
                PLogger.d("-----onReceivedError---->webResourceRequest: " + webResourceRequest + ", webResourceError: " + webResourceError);
                if (webResourceRequest != null && webResourceRequest.getUrl() != null && !TextUtils.isEmpty(webResourceRequest.getUrl().getPath()) && "file".equals(webResourceRequest.getUrl().getScheme()) && (webResourceRequest.getUrl().getPath().toLowerCase().endsWith(".png") || webResourceRequest.getUrl().getPath().toLowerCase().endsWith(".jpeg") || webResourceRequest.getUrl().getPath().toLowerCase().endsWith(".jpg") || webResourceRequest.getUrl().getPath().toLowerCase().endsWith(".gif"))) {
                    String url = webResourceRequest.getUrl().getScheme() + "://" + webResourceRequest.getUrl().getPath();
                    if (!new File(url).exists()) {
                        return;
                    }
                }
                if (webResourceRequest != null && webResourceRequest.getUrl() != null && !TextUtils.isEmpty(webResourceRequest.getUrl().getPath())
                        && !webResourceRequest.getUrl().getPath().toLowerCase().endsWith(".js") && !webResourceRequest.getUrl().getPath().toLowerCase().endsWith(".htm") && !webResourceRequest.getUrl().getPath().toLowerCase().endsWith(".html")) {

                    return;
                }

                isLoadError = true;
                customFrameLayout.showOfIndex(FRAME_ERROR);
                LoadingDialog.closeLoadingDialog(500);
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
                // customFrameLayout.showOfIndex(FRAME_LOADING);
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
        loadUrl();
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

                        if (contentTypeArr != null && contentTypeArr.length > 1) {
                            String codeType = "UTF-8";
                            if (!TextUtils.isEmpty(contentTypeArr[1]) && contentTypeArr[1].trim().contains("charset=") && (("text/html".equals(contentTypeArr[0].trim()) || ("application/javascript".equals(contentTypeArr[0].trim()))))) {
                                String[] codeTypeArr = contentTypeArr[1].split("=");
                                if (codeTypeArr != null && codeTypeArr.length > 1 && !TextUtils.isEmpty(codeTypeArr[1]) &&
                                        ("UTF-8".equals(codeTypeArr[1].toUpperCase()) || "GBK".equals(codeTypeArr[1].toUpperCase()) || "GB2312".equals(codeTypeArr[1].toUpperCase()))) {
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
                //return new com.tencent.smtt.export.external.interfaces.WebResourceResponse("text/html", "UTF-8", connection.getInputStream());
                return null;
            } catch (MalformedURLException e) {
                e.printStackTrace();
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
    public void loadUrl() {
        if (customFrameLayout == null || webView == null) {
            return;
        }

        // 判断有无网络
        if (!NetUtil.getInstance().isNetConnect(getContext())) {
            customFrameLayout.showOfIndex(FRAME_ERROR);
            LoadingDialog.closeLoadingDialog(500);
        } else {
            customFrameLayout.showOfIndex(FRAME_WEB);
            LoadingDialog.show((FragmentActivity) context);
            webView.loadUrl(url);
        }
    }

    /**
     * @return 获取展示的WebView实例
     */
    public WebView getWebView() {
        return webView;
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
            loadUrl();
        }
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
        LoadingDialog.closeLoadingDialog(500);
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
