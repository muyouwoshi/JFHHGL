package com.juxin.predestinate.ui.utils;

import android.view.View;

import java.util.Calendar;

/**
 * 防止过快点击造成多次事件的点击监听
 *
 * 用法:
 * button.setOnClickListener(new NoDoubleClickListener() {
 *   @Override
 *   public void onNoDoubleClick(View v) {
 *     // 处理点击事件
 *   }
 * });
 */
public abstract class NoDoubleClickListener implements View.OnClickListener {

    // 连续两次点击之间的时间间隔
    public static final int MIN_CLICK_DELAY_TIME = 500;

    // 上一次的点击时间
    private long lastClickTime = 0;

    @Override
    public void onClick(View v) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onNoDoubleClick(v);
        }
    }

    public abstract void onNoDoubleClick(View v);
}
