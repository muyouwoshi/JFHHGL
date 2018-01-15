package com.juxin.predestinate.ui.utils;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

/**
 * 创建日期：2017/7/13
 * 描述:滚动标题颜色渐变
 * 作者:lc
 */
@Deprecated
public class PeerageTitleScrollView extends ScrollView {

    private int rangeViewHeight;
    private boolean isMan = true;

    private View alphaView;
    private View rangeView;

    public PeerageTitleScrollView(Context context) {
        super(context);
    }

    public PeerageTitleScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PeerageTitleScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 设置参数
     * @param alphaView         发生渐变的view
     * @param rangeView         在该View的高度范围内完成变化，滚动时计算 alphaView 的透明度
     * @param isMan             决定alphaView的最终颜色，男：蓝色，女：红色；false默认是红色
     */
    public void setData(View alphaView, View rangeView, boolean isMan) {
        this.alphaView = alphaView;
        this.rangeView = rangeView;
        this.isMan = isMan;
        initListeners();
    }

    /**
     * 获取View高度，设置滚动监听
     */
    private void initListeners() {
        if(alphaView == null || rangeView == null) return;
        ViewTreeObserver vto = alphaView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                alphaView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                rangeViewHeight = rangeView.getHeight();
            }
        });
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        try {
            if (y <= 0) {
                alphaView.setBackgroundColor(getColor(0));
            } else if (y > 0 && y <= rangeViewHeight) {
                float scale = (float) y / rangeViewHeight;
                float alpha = (255 * scale);
                alphaView.setBackgroundColor(getColor(alpha));
            } else {
                alphaView.setBackgroundColor(getColor(255));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getColor(float alpha) {
        int color;
        if (isMan) {
            color = Color.argb((int) alpha, 130, 160, 237); //picker_blue_color #82A0ED--> 对应的十进制
        } else {
            color = Color.argb((int) alpha, 253, 108, 142);  //picker_pink_color #fd6c8e --> 对应的十进制
        }
        return color;
    }
}
