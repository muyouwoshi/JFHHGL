package com.juxin.library.observe;

/**
 * 抛出数据处理实体类
 * Created by ZRP on 2016/12/27.
 */
public class Msg {

    private String key;
    private Object data;

    public Msg() {
    }

    public Msg(String key, Object data) {
        this.key = key;
        this.data = data;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Msg{" +
                "key='" + key + '\'' +
                ", data=" + String.valueOf(data) +
                '}';
    }
}
