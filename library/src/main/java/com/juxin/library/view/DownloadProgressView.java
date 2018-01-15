package com.juxin.library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;

import com.juxin.library.R;

/**
 * 下载进度条
 * Created by zrp on 16-7-11.
 */
public class DownloadProgressView extends View {

    private static final int TYPE_CIRCLE = 0;       //圆形下载进度
    private static final int TYPE_HORIZONTAL = 1;   //水平progressBar的下载进度

    private Paint paint;        //画笔

    private int progressColor;  // view绘制的颜色：TYPE_CIRCLE时为绘制扇形view的颜色，最好带透明度。TYPE_HORIZONTAL时为水平进度条的颜色
    private int max;            // 最大进度：同progressBar
    private int progress;       // 当前进度：同progressBar
    private int type;           // progressBar的显示类型

    public DownloadProgressView(Context context) {
        this(context, null);
    }

    public DownloadProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DownloadProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        paint = new Paint();

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DownloadProgress);
        progressColor = typedArray.getColor(R.styleable.DownloadProgress_progressColor, 0xffffffff);
        max = typedArray.getInteger(R.styleable.DownloadProgress_max, 100);
        progress = typedArray.getInteger(R.styleable.DownloadProgress_progress, -1);
        type = typedArray.getInt(R.styleable.DownloadProgress_shapeType, TYPE_CIRCLE);
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getVisibility() == VISIBLE) {
            paint.setAntiAlias(true);
            paint.setColor(progressColor);

            if (type == TYPE_CIRCLE) {
                if (max > 0 && progress >= 0) {
                    //画圆环
                    paint.setStyle(Paint.Style.STROKE);//绘制空心圆
                    paint.setStrokeWidth(getWidth() / 16);
                    canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2 - getWidth() / 16, paint);

                    //画扇形loading区域
                    paint.setStyle(Paint.Style.FILL);
                    RectF sector = new RectF(getWidth() / 7, getHeight() / 7, getWidth() - getWidth() / 7, getHeight() - getHeight() / 7);//绘制扇形的区域
                    canvas.drawArc(sector, 270, (float) progress / (float) max * 360f, true, paint);
                }
            } else if (type == TYPE_HORIZONTAL) {
                paint.setStyle(Paint.Style.FILL);//绘制进度
                paint.setStrokeWidth(getHeight());

                float viewProgress = 0;
                if (max > 0 && progress >= 0) {
                    viewProgress = ((float) progress / (float) max) * getWidth();
                }
                canvas.drawLine(0, getHeight(), viewProgress, getHeight(), paint);
            }
        }
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            invalidate();
        }
    }

    /**
     * 设置绘制view/进度的颜色
     *
     * @param progressColor 绘制view/进度的颜色
     */
    public void setProgressColor(@ColorInt int progressColor) {
        this.progressColor = progressColor;
        invalidate();
    }

    /**
     * set max onProgress value
     *
     * @param max max value of download
     */
    public void setMax(int max) {
        this.max = max;
    }

    /**
     * set onProgress value
     *
     * @param progress onProgress of downloading
     */
    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }
}