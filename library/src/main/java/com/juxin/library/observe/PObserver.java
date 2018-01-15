package com.juxin.library.observe;

/**
 * RxBus事件监听回调
 * Created by ZRP on 2017/3/7.
 */
public interface PObserver {

    /**
     * 事件回调
     *
     * @param key   事件key
     * @param value 事件传递值
     */
    void onMessage(String key, Object value);
}
