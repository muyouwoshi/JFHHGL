package com.juxin.library.request;

/**
 * 下载监听
 * Created by ZRP on 2016/9/21.
 */
public interface DownloadListener {

    /**
     * 开始下载
     *
     * @param url      下载URL
     * @param filePath 本地保存文件路径
     */
    void onStart(String url, String filePath);

    /**
     * 下载进度，0~100。
     *
     * @param url     下载URL
     * @param process 下载进度。
     * @param size    文件大小。
     */
    void onProcess(String url, int process, long size);

    /**
     * 下载成功
     *
     * @param url      下载URL
     * @param filePath 本地保存文件路径
     */
    void onSuccess(String url, String filePath);

    /**
     * 下载失败
     *
     * @param url       下载URL
     * @param throwable 失败详情
     */
    void onFail(String url, Throwable throwable);
}
