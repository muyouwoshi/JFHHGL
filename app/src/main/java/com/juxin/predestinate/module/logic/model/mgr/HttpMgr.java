package com.juxin.predestinate.module.logic.model.mgr;

import com.juxin.library.observe.ModuleBase;
import com.juxin.library.request.DownloadListener;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HTCallBack;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.logic.request.RequestParam;

import java.io.File;
import java.util.Map;

/**
 * 网络请求接口类
 *
 * @author ZRP
 * @date 2016/12/29
 */
public interface HttpMgr extends ModuleBase {

    /**
     * post请求
     *
     * @param urlParam        UrlParam请求接口实例
     * @param headerMap       请求头map
     * @param get_param       get参数map，url拼接
     * @param post_param      post参数map
     * @param cacheType       缓存类型
     * @param isEncrypt       是否为加密请求
     * @param isJsonRequest   是否为application/json格式提交的post数据：默认为application/json
     * @param requestCallback 请求回调
     * @return HTCallBack请求实例
     */
    HTCallBack reqPost(UrlParam urlParam, Map<String, String> headerMap, Map<String, Object> get_param,
                       Map<String, Object> post_param, RequestParam.CacheType cacheType,
                       boolean isEncrypt, boolean isJsonRequest, RequestComplete requestCallback);

    /**
     * Post: 加密，不缓存
     */
    HTCallBack reqPostNoCacheHttp(UrlParam urlParam, Map<String, Object> post_param, RequestComplete requestCallback);

    /**
     * Post: 加密，不缓存
     */
    HTCallBack reqPostJsonNoCacheHttp(UrlParam urlParam, String jsonParam, RequestComplete requestCallback);

    /**
     * Post: 不加密，不缓存
     */
    HTCallBack reqPostNoCacheNoEncHttp(UrlParam urlParam, Map<String, Object> post_param, RequestComplete requestCallback);

    /**
     * Post: 加密，缓存
     */
    HTCallBack reqPostAndCacheHttp(UrlParam urlParam, Map<String, Object> post_param, RequestComplete requestCallback);

    /**
     * Post: 加密，不缓存
     */
    HTCallBack reqPostNoCacheHttp(UrlParam urlParam, Map<String, Object> get_param, Map<String, Object> post_param, RequestComplete requestCallback);

    /**
     * Get: 加密，不缓存
     */
    HTCallBack reqGetNoCacheHttp(UrlParam urlParam, Map<String, Object> get_param, RequestComplete requestCallback);

    /**
     * Get: 加密，缓存
     */
    HTCallBack reqGetAndCacheHttp(UrlParam urlParam, Map<String, Object> get_param, RequestComplete requestCallback);

    /**
     * 发起普通请求
     */
    HTCallBack request(RequestParam requestParam);

    /**
     * 文件上传
     */
    HTCallBack uploadFile(UrlParam urlParam, Map<String, Object> post_param, Map<String, File> file_param, RequestComplete requestCallback);

    /**
     * 文件下载
     *
     * @param url              下载地址
     * @param filePath         本地存放路径
     * @param downloadListener 下载监听
     * @return HTCallBack
     */
    HTCallBack download(String url, String filePath, DownloadListener downloadListener);


    /**
     * 下载视频文件
     */
    HTCallBack downloadVideo(String url, DownloadListener downloadListener);

    /**
     * 下载图片文件
     */
    HTCallBack downloadPic(String url, DownloadListener downloadListener);

    /**
     * 下载音频文件
     */
    HTCallBack downloadVoice(String url, DownloadListener downloadListener);

    /**
     * 下载Apk文件
     */
    HTCallBack downloadApk(String url, DownloadListener downloadListener);
}