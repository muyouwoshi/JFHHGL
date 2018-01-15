package com.juxin.predestinate.ui.user.my.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * 环境：默认的viewpager是铺满整个父布局的，无法自适应item的高度
 * 自定义viewpager自动适应子布局的height
 * Created by zm on 2017/5/10
 */
public class CustomViewPager extends ViewPager {

    private int row = 1;//多少列默认1类（一般用于子布局是gridview时）

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setRow(int row){
        this.row = row;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int height = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            if (h > height)
                height = h*row;//不为0时设置自定义控件的高度
        }

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
