package com.juxin.predestinate.module.logic.request;

/**
 * 请求完成后的回调接口
 * Created by ZRP on 2017/3/9.
 */
public interface RequestComplete {

    /**
     * 请求完成后的回调接口
     *
     * @param response 请求返回的数据实体
     */
    void onRequestComplete(HttpResponse response);
}
