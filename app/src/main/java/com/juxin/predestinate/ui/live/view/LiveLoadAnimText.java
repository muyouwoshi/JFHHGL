package com.juxin.predestinate.ui.live.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;

/**
 * Created by 周杰 on 2017/7/17.
 */

/**
 * 播放文字动画的TextView
 */
public class LiveLoadAnimText extends android.support.v7.widget.AppCompatTextView {
    private volatile int count = 0;
    private volatile boolean isAnimRun;
    private String[] loadStrs = new String[]{
            "加载中",
            "加载中.",
            "加载中..",
            "加载中...",
    };

    public LiveLoadAnimText(Context context, AttributeSet attrs) {
        super(context, attrs);
        start();
    }
    public LiveLoadAnimText(Context context) {
        super(context);
        start();
    }

    /**
     * 设置动画文字
     * @param strs
     */
    public void setTextArray(String[] strs){
        loadStrs = strs;
        this.invalidate();
    }

    /**
     * 开始播放
     */
    public void start(){
        count = 0;
        isAnimRun = true;
        this.setText(loadStrs[count]);
        this.setTextColor(Color.parseColor("#ffffff"));
        this.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
    }

    /**
     * 结束播放
     */
    public void stop(){
        isAnimRun = false;
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(isAnimRun){
            if(count>=loadStrs.length) count = 0;
            postDelayed(new Runnable(){
                @Override
                public void run() {
                    LiveLoadAnimText.this.setText(loadStrs[(++count)%loadStrs.length]);
                }
            },200);
        }
    }
}
