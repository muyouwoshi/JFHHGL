package com.juxin.predestinate.module.logic.model.impl;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.juxin.library.dir.DirType;
import com.juxin.library.log.PLogger;
import com.juxin.library.log.PSP;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.request.DownloadListener;
import com.juxin.library.utils.FileUtil;
import com.juxin.library.utils.JniUtil;
import com.juxin.library.utils.NetworkUtils;
import com.juxin.library.utils.StringUtils;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.model.mgr.HttpMgr;
import com.juxin.predestinate.module.logic.request.HTCallBack;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.logic.request.RequestHelper;
import com.juxin.predestinate.module.logic.request.RequestParam;
import com.juxin.predestinate.module.util.CheckDomainName;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * 网络请求实现类
 *
 * @author ZRP
 * @date 2016/12/29
 */
public class HttpMgrImpl implements HttpMgr {

    @Override
    public void init() {
    }

    @Override
    public void release() {
    }

    @Override
    public HTCallBack reqPost(UrlParam urlParam, Map<String, String> headerMap,
                              Map<String, Object> getParam, Map<String, Object> postParam,
                              RequestParam.CacheType cacheType, boolean isEncrypt,
                              boolean isJsonRequest, RequestComplete requestCallback) {
        return request(new RequestParam().setUrlParam(urlParam)
                .setHeadParam(headerMap).setGetParam(getParam).setPostParam(postParam)
                .setCacheType(cacheType).setNeedEncrypt(isEncrypt).setJsonRequest(isJsonRequest)
                .setRequestCallback(requestCallback));
    }

    @Override
    public HTCallBack reqPostNoCacheHttp(UrlParam urlParam, Map<String, Object> postParam, RequestComplete requestCallback) {
        return request(new RequestParam().setUrlParam(urlParam)
                .setPostParam(postParam).setJsonRequest(true)
                .setNeedEncrypt(true).setRequestCallback(requestCallback));
    }

    @Override
    public HTCallBack reqPostJsonNoCacheHttp(UrlParam urlParam, String jsonParam, RequestComplete requestCallback) {
        return request(new RequestParam().setUrlParam(urlParam)
                .setJsonParam(jsonParam).setJsonRequest(true)
                .setNeedEncrypt(true).setRequestCallback(requestCallback));
    }

    @Override
    public HTCallBack reqPostNoCacheNoEncHttp(UrlParam urlParam, Map<String, Object> postParam, RequestComplete requestCallback) {
        return request(new RequestParam().setUrlParam(urlParam)
                .setPostParam(postParam).setJsonRequest(false).
                        setRequestCallback(requestCallback));
    }

    @Override
    public HTCallBack reqPostAndCacheHttp(UrlParam urlParam, Map<String, Object> postParam, RequestComplete requestCallback) {
        return request(new RequestParam().setUrlParam(urlParam)
                .setPostParam(postParam).setJsonRequest(true)
                .setCacheType(RequestParam.CacheType.CT_Cache_Params)
                .setNeedEncrypt(true).setRequestCallback(requestCallback));
    }

    @Override
    public HTCallBack reqPostNoCacheHttp(UrlParam urlParam, Map<String, Object> getParam, Map<String, Object> postParam, RequestComplete requestCallback) {
        return request(new RequestParam().setUrlParam(urlParam).setGetParam(getParam)
                .setPostParam(postParam).setJsonRequest(true)
                .setNeedEncrypt(true).setRequestCallback(requestCallback));
    }

    @Override
    public HTCallBack reqGetAndCacheHttp(UrlParam urlParam, Map<String, Object> getParam, RequestComplete requestCallback) {
        return request(new RequestParam().setUrlParam(urlParam)
                .setGetParam(getParam).setNeedEncrypt(true)
                .setCacheType(RequestParam.CacheType.CT_Cache_Params)
                .setRequestCallback(requestCallback));
    }

    @Override
    public HTCallBack reqGetNoCacheHttp(UrlParam urlParam, Map<String, Object> getParam, RequestComplete requestCallback) {
        return request(new RequestParam().setUrlParam(urlParam).setNeedEncrypt(true)
                .setGetParam(getParam).setRequestCallback(requestCallback));
    }

    @Override
    public HTCallBack uploadFile(UrlParam urlParam, Map<String, Object> postParam, Map<String, File> fileParam, RequestComplete requestCallback) {
        return request(new RequestParam().setUrlParam(urlParam)
                .setPostParam(postParam).setFileParam(fileParam)
                .setRequestCallback(requestCallback));
    }

    @Override
    public HTCallBack downloadVideo(String url, DownloadListener downloadListener) {
        String filePath = DirType.getVideoDir() + FileUtil.getFileNameFromUrl(url) + ".mp4";
        return download(url, filePath, downloadListener);
    }

    @Override
    public HTCallBack downloadPic(String url, DownloadListener downloadListener) {
        String filePath = DirType.getImageDir() + FileUtil.getFileNameFromUrl(url) + ".jpg";
        return download(url, filePath, downloadListener);
    }

    @Override
    public HTCallBack downloadVoice(String url, DownloadListener downloadListener) {
        String filePath = DirType.getVoiceDir() + FileUtil.getFileNameFromUrl(url) + ".amr";
        return download(url, filePath, downloadListener);
    }

    @Override
    public HTCallBack downloadApk(String url, DownloadListener downloadListener) {
        String filePath = DirType.getApkDir() + FileUtil.getFileNameFromUrl(url) + ".apk";
        return download(url, filePath, downloadListener);
    }

    @Override
    public HTCallBack download(final String url, final String filePath, final DownloadListener downloadListener) {
        if (TextUtils.isEmpty(url)) {
            if (downloadListener != null) {
                MsgMgr.getInstance().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        downloadListener.onFail(url, new NullPointerException("The download url is empty."));
                    }
                });
            }
            return new HTCallBack();
        }
        if (FileUtil.isExist(filePath)) {
            MsgMgr.getInstance().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (downloadListener != null) {
                        downloadListener.onSuccess(url, filePath);
                    }
                }
            });
            return new HTCallBack();
        }
        PSP.getInstance().put(String.valueOf(url.hashCode()), filePath);
        return RequestHelper.getInstance().downloadFile(url, filePath, downloadListener, true);
    }

    @Override
    public HTCallBack request(RequestParam requestParam) {
        final CompositeDisposable compositeDisposable = new CompositeDisposable();

        final UrlParam urlParam = requestParam.getUrlParam();
        final Map<String, String> headerMap = requestParam.getHeadParam();
        final Map<String, Object> getParam = requestParam.getGetParam();
        final Map<String, Object> postParam = requestParam.getPostParam();
        final String jsonParam = requestParam.getJsonParam();
        final Map<String, File> fileParam = requestParam.getFileParam();
        final RequestComplete requestCallback = requestParam.getRequestCallback();
        final RequestParam.CacheType cacheType = requestParam.getCacheType();
        final boolean isEncrypt = requestParam.isNeedEncrypt();
        final boolean isJsonRequest = requestParam.isJsonRequest();

        // 获取url
        String url = urlParam.getFinalUrl();
        // 由域名灾备重置请求地址
        try {
            URL hostUrl = new URL(url);
            String host = CheckDomainName.addSeparator(hostUrl.getHost());
            String newUrl = CheckDomainName.getNewDomainName(host);
            if (!TextUtils.isEmpty(newUrl)) {
                url = url.replace(host, newUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 用Url和Post请求参数作为缓存的key
        String cacheUrl = url;
        if (RequestParam.CacheType.CT_Cache_Params == cacheType) {
            if (postParam != null) {
                cacheUrl += JSON.toJSONString(postParam);
            } else if (getParam != null) {
                cacheUrl += JSON.toJSONString(getParam);
            }
        }

        final String finalUrl = url;
        final String finalCacheUrl = cacheUrl;

        // 判断登录，如果用户没有登录，并且该请求又需要登录的话，则不发出请求，且清除该url的缓存数据
        PLogger.d(urlParam + "_url: " + url);
        if (TextUtils.isEmpty(finalUrl) || (!App.isLogin && urlParam.isNeedLogin())) {
            if (RequestParam.CacheType.CT_Cache_No != cacheType) {
                // 清除该url的缓存
                PSP.getInstance().remove(finalCacheUrl);
            }
            HttpResponse preResult = new HttpResponse(urlParam);
            preResult.setError();
            preResult.setCache(false);
            if (requestCallback != null) {
                requestCallback.onRequestComplete(preResult);
            }
            return new HTCallBack(null);
        }

        // 请求预处理：缓存回调
        Disposable cacheDisposable = Flowable.create(new FlowableOnSubscribe<HttpResponse>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<HttpResponse> e) throws Exception {
                // 先从缓存中拿数据，如果有缓存，先行抛出缓存结果
                HttpResponse cacheResult = new HttpResponse(urlParam);
                cacheResult.setError();
                // 标识数据源为缓存
                cacheResult.setCache(true);
                if (RequestParam.CacheType.CT_Cache_No != cacheType) {
                    // 获取缓存中的数据,如果不为null,则开始解析数据,并返回数据
                    String cacheStr = PSP.getInstance().getString(finalCacheUrl, "");
                    if (!TextUtils.isEmpty(cacheStr)) {
                        // 拿到缓存数据之后进行判断解密
                        if (!TextUtils.isEmpty(cacheStr) && isEncrypt && (!cacheStr.startsWith("{") || !cacheStr.endsWith("}"))) {
                            cacheStr = new String(JniUtil.GetDecryptString(cacheStr));
                        }
                        cacheResult.setOK();
                        cacheResult.parseJson(cacheStr);
                    }
                    PLogger.d("response cache，request url：" + finalUrl + "\ncache String：" + cacheStr);
                }
                e.onNext(cacheResult);
                e.onComplete();
            }
        }, BackpressureStrategy.BUFFER).subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<HttpResponse>() {
            @Override
            public void accept(@NonNull HttpResponse cacheResult) throws Exception {
                if (RequestParam.CacheType.CT_Cache_No != cacheType) {
                    // 缓存回调
                    if (requestCallback != null) {
                        requestCallback.onRequestComplete(cacheResult);
                    }
                }
            }
        });
        compositeDisposable.add(cacheDisposable);

        // 拼接请求头
        Map<String, String> requestHeaderMap = new HashMap<>();
        requestHeaderMap.put("Cookie", ModuleMgr.getLoginMgr().getCookieVerCode());
        if (headerMap != null) {
            requestHeaderMap.putAll(headerMap);
        }
        // 实际请求处理，正式发出请求，请求完成之后异步处理结果并抛出到主线程
        final String requestParamsString = url + ", GetParams: " + getRequestParams(getParam) + ", PostParams: " + getRequestParams(postParam);
        Disposable requestDisposable = RequestHelper.getInstance().reqHttpCallUrl(requestHeaderMap, url,
                getParam, postParam, jsonParam, fileParam,
                isEncrypt, isJsonRequest).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io()).flatMap(new Function<Response<ResponseBody>, ObservableSource<HttpResponse>>() {
                    @Override
                    public ObservableSource<HttpResponse> apply(Response<ResponseBody> response) throws Exception {
                        HttpResponse requestResult = new HttpResponse(urlParam);
                        // Headers与Raw双重保证Cookie的成功获取
                        if (urlParam == UrlParam.reqLogin || urlParam == UrlParam.reqRegister) {
                            String cookie = response.headers().get("Set-Cookie");
                            if (TextUtils.isEmpty(cookie)) {
                                cookie = response.raw().header("Set-Cookie");
                            }
                            ModuleMgr.getLoginMgr().setCookie(StringUtils.getBeforeNoFlag(cookie, ";"));
                        }
                        StringBuilder sb = new StringBuilder();

                        // 处理服务器返回异常，抛出error
                        if (!response.isSuccessful()) {
                            PLogger.d("response fail, response code " + response.code() + ", request url: " + requestParamsString);
                            requestResult.setServerResponse();
                            requestResult.setError();//设置失败
                            requestResult.setCache(false);

                            //切换域名服务器
                            if (App.getActivity() != null && NetworkUtils.isConnected(App.getActivity()) && (response.code() == 400 ||
                                    response.code() == 404 || response.code() == 503 || response.code() == 504) &&
                                    (urlParam == UrlParam.reqRegister || urlParam == UrlParam.reqLogin || urlParam == UrlParam.reqNewLogin)) {
                                CheckDomainName.checkDomainArr(CheckDomainName.domainNameArrIndex);
                            }
                            return Observable.just(requestResult);
                        }

                        // 处理返回body，如果body为null，不做读取
                        if (response.body() != null) {
                            try {
                                BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream()));
                                String line;
                                try {
                                    while ((line = reader.readLine()) != null) {
                                        sb.append(line);
                                    }
                                } catch (IOException e) {
                                    PLogger.printThrowable(e);
                                }
                            } catch (Exception e) {
                                PLogger.d("response fail, message: " + e.getMessage() + " , request url: " + requestParamsString);
                                requestResult.setServerResponse();
                                requestResult.setError();
                                requestResult.setCache(false);
                                return Observable.just(requestResult);
                            }
                        }
                        String resultString = sb.toString();
                        // 对请求成功的内容进行缓存
                        if (RequestParam.CacheType.CT_Cache_No != cacheType && !TextUtils.isEmpty(resultString)) {
                            PSP.getInstance().put(finalCacheUrl, resultString);
                        }

                        // 如果是加密数据，对其进行解密并抛出
                        if (!TextUtils.isEmpty(resultString) && isEncrypt && (!resultString.startsWith("{") || !resultString.endsWith("}"))) {
                            resultString = new String(JniUtil.GetDecryptString(resultString));
                        }
                        PLogger.d("response OK, request url: " + requestParamsString + "\nresponse: " + resultString);
                        requestResult.setServerResponse();
                        requestResult.setOK();
                        requestResult.setCache(false);
                        requestResult.parseJson(resultString);

                        return Observable.just(requestResult);
                    }
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<HttpResponse>() {
                    @Override
                    public void accept(HttpResponse requestResult) throws Exception {
                        if (!compositeDisposable.isDisposed()) {
                            compositeDisposable.dispose();
                        }
                        if (requestCallback != null) {
                            requestCallback.onRequestComplete(requestResult);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (!compositeDisposable.isDisposed()) {
                            compositeDisposable.dispose();
                        }
                        PLogger.d("response fail, message: " + throwable.getMessage() + " , request url: " + requestParamsString);
                        HttpResponse requestResult = new HttpResponse(urlParam);
                        requestResult.setServerResponse();
                        requestResult.setError();
                        requestResult.setCache(false);

                        // 切换域名服务器
                        if (App.getActivity() != null && NetworkUtils.isConnected(App.getActivity())
                                && (throwable instanceof SocketTimeoutException || throwable instanceof ConnectException || throwable instanceof UnknownHostException)
                                && (urlParam == UrlParam.reqRegister || urlParam == UrlParam.reqLogin || urlParam == UrlParam.reqNewLogin)) {
                            CheckDomainName.checkDomainArr(CheckDomainName.domainNameArrIndex);
                        }
                        if (requestCallback != null) {
                            requestCallback.onRequestComplete(requestResult);
                        }
                    }
                });
        compositeDisposable.add(requestDisposable);
        return new HTCallBack(compositeDisposable);
    }

    /**
     * 将请求参数map转换成json字符串
     *
     * @param params 请求参数map
     * @return 转换字符串
     */
    private String getRequestParams(Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return "empty";
        }
        return JSON.toJSONString(params);
    }
}
