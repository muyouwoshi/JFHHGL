package com.juxin.predestinate.ui.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 用于textview跑马灯获取焦点
 */

public class MarqueeTextView extends TextView {
    public MarqueeTextView(Context context) {
        super(context);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    //始终返回true，即一直获得焦点
    @Override
    public boolean isFocused() {
        return true;
    }
}
