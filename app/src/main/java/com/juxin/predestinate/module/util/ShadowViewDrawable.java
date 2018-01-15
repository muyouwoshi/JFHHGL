package com.juxin.predestinate.module.util;

import android.graphics.*;
import android.graphics.drawable.Drawable;

/**
 * 添加阴影实现类
 * Created by zm on 2016/8/6.
 */
public class ShadowViewDrawable extends Drawable {
    private Paint paint;

    private RectF bounds = new RectF();

    private int width;
    private int height;

    private ShadowProperty shadowProperty;
    private int shadowOffset;

    private RectF drawRect;

    private float rx;
    private float ry;

    public ShadowViewDrawable(ShadowProperty shadowProperty, int color, float rx, float ry) {
        this.shadowProperty = shadowProperty;
        shadowOffset = this.shadowProperty.getShadowOffset();

        this.rx = rx;
        this.ry = ry;

        paint = new Paint();
        paint.setAntiAlias(true);
        /**
         * 解决旋转时的锯齿问题
         */
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        /**
         * 设置阴影
         */
        paint.setShadowLayer(shadowProperty.getShadowRadius(), shadowProperty.getShadowDx(), shadowProperty.getShadowDy(), shadowProperty.getShadowColor());

        drawRect = new RectF();
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        if (bounds.right - bounds.left > 0 && bounds.bottom - bounds.top > 0) {
            this.bounds.left = bounds.left;
            this.bounds.right = bounds.right;
            this.bounds.top = bounds.top;
            this.bounds.bottom = bounds.bottom;
            width = (int) (this.bounds.right - this.bounds.left);
            height = (int) (this.bounds.bottom - this.bounds.top);

            drawRect = new RectF(shadowOffset, shadowOffset, width - shadowOffset, height - shadowOffset);

            invalidateSelf();

        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRoundRect(
                drawRect,
                rx, ry,
                paint
        );

    }

    public ShadowViewDrawable setColor(int color) {
        paint.setColor(color);
        return this;
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }
}
