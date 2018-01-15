package com.juxin.library.observe;

import io.reactivex.Flowable;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subscribers.SerializedSubscriber;

/**
 * rxJava事件订阅者
 */
public class RxBus {

    private static FlowableProcessor<Object> mBus;
    private static volatile RxBus mInstance = null;

    private RxBus() {
        mBus = PublishProcessor.create().toSerialized();//调用toSerialized()方法，保证线程安全
    }

    public static synchronized RxBus getInstance() {
        if (mInstance == null) {
            synchronized (RxBus.class) {
                if (mInstance == null) {
                    mInstance = new RxBus();
                }
            }
        }
        return mInstance;
    }

    /**
     * 发送消息
     */
    public void post(Object o) {
        new SerializedSubscriber<>(mBus).onNext(o);
    }

    /**
     * 确定接收消息的类型
     */
    public <T> Flowable<T> toFlowable(Class<T> eventType) {
        return mBus.ofType(eventType);
    }

    /**
     * 判断是否有订阅者
     */
    public boolean hasSubscribers() {
        return mBus.hasSubscribers();
    }
}
