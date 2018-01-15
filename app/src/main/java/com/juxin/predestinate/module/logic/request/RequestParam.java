package com.juxin.predestinate.module.logic.request;

import com.juxin.predestinate.module.logic.config.UrlParam;

import java.io.File;
import java.util.Map;

/**
 * 请求时参数
 *
 * @author ZRP
 * @date 2017/3/9
 */
public class RequestParam {

    /**
     * 缓存类型枚举
     */
    public enum CacheType {
        /**
         * 不缓存
         */
        CT_Cache_No,
        /**
         * 用无参数的Url作为缓存的key
         */
        CT_Cache_Url,
        /**
         * 用Url和请求参数作为缓存的key
         */
        CT_Cache_Params;
    }

    /**
     * 请求类型枚举
     */
    public enum RequestType {
        POST, GET;
    }

    private RequestType requestType = RequestType.POST;     //请求方式
    private UrlParam urlParam;                              //请求枚举
    private Map<String, String> headParam;                  //请求头map
    private Map<String, Object> getParam;                   //get请求参数
    private Map<String, Object> postParam;                  //post请求参数
    private String jsonParam;                               //json请求参数
    private Map<String, File> fileParam;                    //文件上传参数
    private RequestComplete requestCallback;                //请求回调
    private CacheType cacheType = CacheType.CT_Cache_No;    //缓存类型：默认不缓存
    private boolean needEncrypt = false;                    //是否加密：默认不加密
    private boolean jsonRequest = true;                     //是否为application/json格式提交的post数据：默认为application/json

    public RequestType getRequestType() {
        return requestType;
    }

    /**
     * 设置请求类型[当前实现可根据请求参数类型确定请求方式，暂时无用]
     *
     * @param requestType 请求类型枚举
     * @return 当前请求参数实例
     */
    public RequestParam setRequestType(RequestType requestType) {
        this.requestType = requestType;
        return this;
    }

    public UrlParam getUrlParam() {
        return urlParam;
    }

    /**
     * 设置请求url信息
     *
     * @param urlParam Url参数信息
     * @return 当前请求参数实例
     */
    public RequestParam setUrlParam(UrlParam urlParam) {
        this.urlParam = urlParam;
        return this;
    }

    public Map<String, String> getHeadParam() {
        return headParam;
    }

    /**
     * 设置请求头信息
     *
     * @param headParam 请求头参数
     * @return 当前请求参数实例
     */
    public RequestParam setHeadParam(Map<String, String> headParam) {
        this.headParam = headParam;
        return this;
    }

    public Map<String, Object> getGetParam() {
        return getParam;
    }

    /**
     * 设置get请求参数
     *
     * @param getParam get请求参数
     * @return 当前请求参数实例
     */
    public RequestParam setGetParam(Map<String, Object> getParam) {
        this.getParam = getParam;
        return this;
    }

    public Map<String, Object> getPostParam() {
        return postParam;
    }

    /**
     * 设置post请求参数
     *
     * @param postParam post请求参数
     * @return 当前请求参数实例
     */
    public RequestParam setPostParam(Map<String, Object> postParam) {
        this.postParam = postParam;
        return this;
    }

    public String getJsonParam() {
        return jsonParam;
    }

    /**
     * 设置以application/json格式提交的字符串参数
     *
     * @param jsonParam application/json格式自交的字符串参数
     * @return 当前请求参数实例
     */
    public RequestParam setJsonParam(String jsonParam) {
        this.jsonParam = jsonParam;
        return this;
    }

    public Map<String, File> getFileParam() {
        return fileParam;
    }

    /**
     * 设置文件上传参数
     *
     * @param fileParam 文件上传参数
     * @return 当前请求参数实例
     */
    public RequestParam setFileParam(Map<String, File> fileParam) {
        this.fileParam = fileParam;
        return this;
    }

    public RequestComplete getRequestCallback() {
        return requestCallback;
    }

    /**
     * 设置请求回调
     *
     * @param requestCallback 请求回调
     * @return 当前请求参数实例
     */
    public RequestParam setRequestCallback(RequestComplete requestCallback) {
        this.requestCallback = requestCallback;
        return this;
    }

    public CacheType getCacheType() {
        return cacheType;
    }

    /**
     * 设置请求缓存类型[默认为不进行缓存]
     *
     * @param cacheType 缓存类型枚举
     * @return 当前请求参数实例
     */
    public RequestParam setCacheType(CacheType cacheType) {
        this.cacheType = cacheType;
        return this;
    }

    public boolean isNeedEncrypt() {
        return needEncrypt;
    }

    /**
     * 设置是否为加密请求[默认不加密]
     *
     * @param needEncrypt 是否为加密请求
     * @return 当前请求参数实例
     */
    public RequestParam setNeedEncrypt(boolean needEncrypt) {
        this.needEncrypt = needEncrypt;
        return this;
    }

    public boolean isJsonRequest() {
        return jsonRequest;
    }

    /**
     * 设置是否为application/json格式进行post参数提交[默认以application/json格式进行参数提交]，与setPostParam配合使用
     *
     * @param jsonRequest 是否为application/json格式进行post参数提交
     * @return 当前请求参数实例
     */
    public RequestParam setJsonRequest(boolean jsonRequest) {
        this.jsonRequest = jsonRequest;
        return this;
    }

    @Override
    public String toString() {
        return "RequestParam{" +
                "requestType=" + requestType +
                ", urlParam=" + urlParam +
                ", headParam=" + headParam +
                ", getParam=" + getParam +
                ", postParam=" + postParam +
                ", jsonParam='" + jsonParam + '\'' +
                ", fileParam=" + fileParam +
                ", requestCallback=" + requestCallback +
                ", cacheType=" + cacheType +
                ", needEncrypt=" + needEncrypt +
                ", jsonRequest=" + jsonRequest +
                '}';
    }
}