package com.juxin.predestinate.module.util;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by siow on 2016/11/18.
 */
public abstract class BaseHandler extends Handler {

    private final WeakReference<Object> actRef;

    public BaseHandler(Object object) {
        actRef = new WeakReference<>(object);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        Object obj = actRef.get();
        if (obj != null) {
            handleEvent(msg, obj);
        }
    }

    public abstract void handleEvent(Message msg, Object object);
}
