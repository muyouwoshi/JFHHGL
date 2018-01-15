package com.juxin.predestinate.module.logic.request;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * retrofit请求接口封装
 * Created by ZRP on 2016/9/19.
 */
interface RequestAPI {

    // =============get请求============

    @Headers({"User-Agent: "})
    @GET
    Observable<Response<ResponseBody>> executeGetCall(@HeaderMap Map<String, String> headerMap, @Url String url, @QueryMap Map<String, Object> maps);

    @Headers({"User-Agent: "})
    @GET
    Observable<Response<ResponseBody>> executeGetCall(@HeaderMap Map<String, String> headerMap, @Url String url);

    // 注意：添加Streaming注解，以支持大文件下载。意味着立刻传递字节码，而不需要把整个文件读进内存，防止OOM
    // 必须在子线程对流进行处理，否则会报NetworkOnMainThreadException错误，此处对下载单独处理
    @Streaming
    @GET
    Observable<Response<ResponseBody>> download(@HeaderMap Map<String, String> headerMap, @Url String url);

    // =============post请求============

    @Headers({"Content-Type: application/json", "Accept: application/json", "User-Agent: "})
    @POST
    Observable<Response<ResponseBody>> executePostCall(@HeaderMap Map<String, String> headerMap, @Url String url, @Body RequestBody str);

    @Headers({"User-Agent: "})
    @FormUrlEncoded
    @POST
    Observable<Response<ResponseBody>> executePostCall(@HeaderMap Map<String, String> headerMap, @Url String url, @FieldMap Map<String, Object> maps);

    @Headers({"User-Agent: "})
    @POST
    Observable<Response<ResponseBody>> executePostCall(@HeaderMap Map<String, String> headerMap, @Url String url);

    @Headers({"User-Agent: "})
    @POST
    Observable<Response<ResponseBody>> executeCusHeaderPostCall(@HeaderMap Map<String, String> headerMap, @Url String url, @Body RequestBody str);

    // =============文件请求============

    @Headers({"User-Agent: "})
    @POST
    Observable<Response<ResponseBody>> executePostCallUploadCall(@HeaderMap Map<String, String> headerMap, @Url String url, @Body MultipartBody multipartBody);
}