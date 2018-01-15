package com.juxin.predestinate.module.local.download;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @author Mr.Huang
 * @date 2017/11/9
 */
public class DownloadInfo {

    public static final byte STATUS_START = 1;
    public static final byte STATUS_DOWNLOADING = 2;
    public static final byte STATUS_SUCCESS = 3;
    public static final byte STATUS_STOP = 4;
    public static final byte STATUS_FAIL = 5;

    public DownloadInfo(String url, String path) {
        this.url = url;
        this.path = path;
    }

    private String url;
    private long total;
    private long length;
    private String path;
    private int status;
    private boolean stop;

    public String getUrl() {
        return url;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getLength() {
        return length;
    }

    public void appendLength(long length) {
        this.length += length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getPath() {
        return path;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public float getProcess() {
        return (float) length / (float) total;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof DownloadInfo) {
            DownloadInfo info = (DownloadInfo) obj;
            if (info.url != null && info.path != null) {
                return info.url.equals(url) && info.path.equals(path);
            }
        }
        return super.equals(obj);
    }

    public String toJSONString() {
        JSONObject object = new JSONObject();
        object.put("path", path);
        object.put("status", status);
        return object.toJSONString();
    }

    public void parse(String json) {
        JSONObject object = JSON.parseObject(json);
        this.path = object.getString("path");
        this.status = object.getIntValue("status");
    }
}
