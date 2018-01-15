package com.juxin.predestinate.module.logic.baseui.intercept;

import android.view.MotionEvent;

/**
 * 事件拦截监听
 */
public interface OnInterceptTouchEventLayout {

    /**
     * 拦截Touch事件，只是触发，不会真正拦截掉此事件
     *
     * @param onInterceptTouchEvent 点击事件
     */
    void setOnInterceptTouchEvent(OnInterceptTouchEvent onInterceptTouchEvent);

    interface OnInterceptTouchEvent {
        void interceptTouchEvent(MotionEvent ev);
    }
}
