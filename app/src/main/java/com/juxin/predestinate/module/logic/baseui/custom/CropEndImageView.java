package com.juxin.predestinate.module.logic.baseui.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 图片加载控件，让图片宽度填满，高度从头部开始自适应
 * Created by ZRP on 2015/11/13.
 */
public class CropEndImageView extends android.support.v7.widget.AppCompatImageView {

    public CropEndImageView(Context context) {
        super(context);
    }

    public CropEndImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CropEndImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final Drawable d = this.getDrawable();
        if (d != null) {
            // ceil not round - avoid thin vertical gaps along the left/right edges
            final int width = MeasureSpec.getSize(widthMeasureSpec);
            final int height = (int) Math.ceil(width * (float) d.getIntrinsicHeight() / d.getIntrinsicWidth());
            this.setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
