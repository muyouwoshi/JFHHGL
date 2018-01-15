package com.juxin.library.request;

/**
 * 文件下载内容
 * Created by zrp on 2016/11/22.
 */
public class FileLoadingBean {

    private long total;      //文件总大小
    private long progress;   //已下载大小
    private String url;     //请求地址

    public FileLoadingBean(long total, long progress, String url) {
        this.total = total;
        this.progress = progress;
        this.url = url;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
