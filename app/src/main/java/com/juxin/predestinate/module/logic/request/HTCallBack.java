package com.juxin.predestinate.module.logic.request;

import io.reactivex.disposables.CompositeDisposable;

/**
 * 二次封装的请求实例，代替原始请求实例，以便替换
 *
 * @author ZRP
 * @date 2016/9/26
 */
public class HTCallBack {

    private CompositeDisposable compositeDisposable = null;

    public HTCallBack() {
        super();
    }

    public HTCallBack(CompositeDisposable compositeDisposable) {
        this.compositeDisposable = compositeDisposable;
    }

    /**
     * 取消请求
     */
    public void cancel() {
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }
}