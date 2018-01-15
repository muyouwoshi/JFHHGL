package com.juxin.predestinate.module.logic.request;

import android.content.Context;
import android.text.TextUtils;

import com.juxin.library.log.PLogger;
import com.juxin.library.request.DownloadListener;
import com.juxin.library.request.FileObserver;
import com.juxin.library.request.Requester;
import com.juxin.library.utils.JniUtil;
import com.juxin.predestinate.BuildConfig;
import com.juxin.predestinate.module.logic.config.Hosts;
import com.juxin.predestinate.module.util.JsonUtil;
import com.juxin.predestinate.module.util.UrlEnc;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * 网络请求管理类，基于Request对retrofit的二次封装
 *
 * @author ZRP
 * @date 2016/9/19
 */
public class RequestHelper {

    private volatile static RequestHelper instance = null;

    private RequestHelper() {
    }

    public static RequestHelper getInstance() {
        if (instance == null) {
            synchronized (RequestHelper.class) {
                if (instance == null) {
                    instance = new RequestHelper();
                }
            }
        }
        return instance;
    }

    // =====================初始化=========================

    private RequestAPI requestAPI;
    private RequestAPI requestAPIDns;

    /**
     * 初始化网络请求
     */
    public void init(Context context) {
        Requester.initBuilder(context, BuildConfig.DEBUG, false);
        requestAPI = Requester.getRequestAPI(Hosts.HOST_URL, RequestAPI.class, false);

        Requester.initBuilder(context, BuildConfig.DEBUG, true);
        requestAPIDns = Requester.getRequestAPI(Hosts.HOST_URL, RequestAPI.class, true);
    }

    /**
     * 根据重新指定的url上传文件 zrp
     *
     * @param url     完整url
     * @param fileMap 文件map
     * @return Call
     */
    public Observable<Response<ResponseBody>> reqUploadCall(String url, Map<String, File> fileMap) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        if (fileMap != null) {
            for (Map.Entry<String, File> entry : fileMap.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue().getName(), RequestBody.create(MediaType.parse("image/jpeg"), entry.getValue()));
            }
        }
        builder.setType(MultipartBody.FORM);
        return requestAPI.executePostCallUploadCall(new HashMap<String, String>(), url, builder.build());
    }

    /**
     * 网络请求
     *
     * @param headerMap     请求headerMap
     * @param url           请求Url
     * @param getParam      get数据
     * @param postParam     post数据
     * @param jsonParam     json数据
     * @param fileParams    文件数据
     * @param isEncrypt     是否加密
     * @param isJsonRequest 是否为application/json格式提交的post数据
     * @return Call
     */
    public Observable<Response<ResponseBody>> reqHttpCallUrl(Map<String, String> headerMap, String url,
                                                             Map<String, Object> getParam, Map<String, Object> postParam, String jsonParam,
                                                             Map<String, File> fileParams, boolean isEncrypt, boolean isJsonRequest) {
        url = UrlEnc.appendUrl(url, getParam, postParam, isEncrypt);
        if (headerMap == null) {
            headerMap = new HashMap<>();
        }

        // post请求，上传文件
        if (fileParams != null && fileParams.size() > 0) {
            PLogger.d("---request--->文件上传post请求：" + url);
            MultipartBody.Builder builder = new MultipartBody.Builder();
            for (Map.Entry<String, File> entry : fileParams.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue().getName(),
                        RequestBody.create(MediaType.parse("image/jpeg"), entry.getValue()));
            }
            // 如果文件上传时有post参数，就将post参数添加到请求参数中, form表单形式上传参数
            if (postParam != null) {
                for (Map.Entry<String, Object> entry : postParam.entrySet()) {
                    builder.addFormDataPart(entry.getKey(), String.valueOf(entry.getValue()));
                }
            }
            builder.setType(MultipartBody.FORM);
            return requestAPI.executePostCallUploadCall(headerMap, url, builder.build());
        }
        // post请求
        else if (postParam != null) {
            PLogger.d("---request--->带参数的post请求：" + url);
            if (isJsonRequest) {
                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                        isEncrypt ? JniUtil.GetEncryptString(JsonUtil.mapToJSON(postParam).toString()) : JsonUtil.mapToJSON(postParam).toString());
                if (headerMap.containsKey("Accept") && !TextUtils.isEmpty(headerMap.get("Accept")) && headerMap.get("Accept").equals("application/jx-json")) {
                    return requestAPI.executeCusHeaderPostCall(headerMap, url, body);
                } else {
                    return requestAPI.executePostCall(headerMap, url, body);
                }
            } else {
                return requestAPI.executePostCall(headerMap, url, postParam);
            }
        }
        // json格式字符串请求参数post请求
        else if (!TextUtils.isEmpty(jsonParam)) {
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                    isEncrypt ? JniUtil.GetEncryptString(jsonParam) : jsonParam);
            return requestAPI.executePostCall(headerMap, url, body);
        }
        // 无请求参数的post/get请求[get请求参数已经在hash的时候拼接，故无需再次拼接]
        else {
            PLogger.d("---request--->带参数的get请求：" + url);
            return requestAPI.executeGetCall(headerMap, url);
        }
    }

    /**
     * 下载文件
     *
     * @param url      下载地址
     * @param filePath 文件存储路径
     * @param listener 下载监听
     */
    public HTCallBack downloadFile(String url, String filePath, DownloadListener listener) {
        return downloadFile(url, filePath, listener, false);
    }

    /**
     * 下载文件
     *
     * @param url      下载地址
     * @param filePath 文件存储路径
     * @param listener 下载监听
     * @param isDns    是否发起的是dns防护请求[段铮]
     * @return 可取消的请求控制参数
     */
    public HTCallBack downloadFile(final String url, String filePath, final DownloadListener listener, boolean isDns) {
        Observable<Response<ResponseBody>> downloadObservable = (isDns ? requestAPIDns : requestAPI).download(new HashMap<String, String>(), url);
        FileObserver fileObserver = new FileObserver(url, filePath, listener);
        downloadObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(fileObserver);
        return new HTCallBack(fileObserver.getRxDisposable());
    }
}
