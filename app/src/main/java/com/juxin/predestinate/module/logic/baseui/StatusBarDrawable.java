package com.juxin.predestinate.module.logic.baseui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

/**
 * 跟布局背景状态栏部分颜色
 */
public class StatusBarDrawable extends Drawable {

    private Paint paint = new Paint();
    private int statusBarColor;
    private Drawable background;
    private int statusBarHeight;
    private boolean fitsSystemWindows = true;

    public StatusBarDrawable(int statusBarColor, Drawable background, int statusBarHeight) {
        this.statusBarColor = statusBarColor;
        this.background = background;
        paint.setAntiAlias(true);
        this.statusBarHeight = statusBarHeight;
    }

    @Override
    public void draw(Canvas canvas) {
        if (fitsSystemWindows && statusBarColor != Color.TRANSPARENT) {
            paint.setColor(statusBarColor);
            canvas.drawRect(0, 0, canvas.getWidth(), statusBarHeight, paint);
        }
        if (background != null) {
            if (statusBarColor != Color.TRANSPARENT) {
                background.setBounds(0, fitsSystemWindows ? statusBarHeight : 0, canvas.getWidth(), canvas.getHeight());
            }
            background.draw(canvas);
        }
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }

    public void setColor(int color) {
        this.statusBarColor = color;
        if (fitsSystemWindows) {
            invalidateSelf();
        }
    }

    public void setFitsSystemWindows(boolean fitsSystemWindows) {
        this.fitsSystemWindows = fitsSystemWindows;
        invalidateSelf();
    }
}
