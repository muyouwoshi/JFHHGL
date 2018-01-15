package com.juxin.predestinate.bean.net;

/**
 * 重写{@link org.apache.http.NameValuePair}使其支持java的基本类型
 */
public interface NameValuePair {

    String getName();

    Object getValue();
}