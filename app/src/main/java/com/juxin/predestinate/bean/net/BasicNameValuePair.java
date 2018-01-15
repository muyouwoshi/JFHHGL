package com.juxin.predestinate.bean.net;

/**
 * 重写{@link org.apache.http.message.BasicNameValuePair}使其支持java的基本类型。
 */
public class BasicNameValuePair implements NameValuePair {

    private String name = null;
    private Object Value = null;

    @Override
    public String getName() {
        return name == null ? "" : name;
    }

    @Override
    public Object getValue() {
        return Value;
    }

    public BasicNameValuePair(String name, Object value) {
        this.name = name;
        Value = value;
    }
}
