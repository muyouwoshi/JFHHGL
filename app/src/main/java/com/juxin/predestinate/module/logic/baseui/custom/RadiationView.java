package com.juxin.predestinate.module.logic.baseui.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

/**
 * 带辐射效果的view
 * <p>
 * Created by Su on 2017/6/14.
 */
public class RadiationView extends View {
    private static final int MESSAGE_DRAW = 0;  // 重绘消息
    private Handler mHandler = null;

    // 默认值
    private float density;
    private static final int DEFAULT_ALPHA = 70;      //  默认透明度
    private static final int DEFAULT_MIN_RADIUS = 16;  //  默认最小半径

    private Paint mPaint = new Paint();         // 画笔
    private int mColor = 0xe6ffffff;            // 颜色
    private int mAlpha;                         // 透明度  0~255
    private int mAlphaInterval = 3;             // 透明度增大区间
    private int speed = 110;                    // 扩散速度
    private int maxRadius = 100;                // 最大辐射半径
    private int minRadius;                      // 最小半径
    private int radius = minRadius;             // 当前半径

    private int width;
    private int height;
    private int centerX;
    private int centerY;
    private boolean isStarted = false;   // 是否已开始动画

    /**
     * 设置颜色
     */
    public void setColor(int color) {
        this.mColor = color;
    }

    /**
     * 设置扩展速度
     */
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    /**
     * 设置最小半径
     */
    public void setMinRadius(int radius) {
        this.minRadius = radius;
    }

    /**
     * 设置屏幕转换参数
     */
    public void setDpMultiple(float density) {
        this.density = density;
        radius = minRadius = (int) (density * DEFAULT_MIN_RADIUS);
        mAlpha = (int) (density * DEFAULT_ALPHA);
    }

    /**
     * 开始动画
     */
    public void startRadiate() {
        isStarted = true;
        mHandler.sendEmptyMessage(MESSAGE_DRAW);
    }

    /**
     * 暂停动画
     */
    public void stopRadiate() {
        mHandler.removeMessages(MESSAGE_DRAW);
    }

    public RadiationView(Context context) {
        super(context);
        init();
    }

    public RadiationView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStrokeWidth(1);
        mPaint.setAntiAlias(true);
        // 必须先设置color，再设置alpha
        mPaint.setColor(mColor);
        mPaint.setAlpha(mAlpha);

        mHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == MESSAGE_DRAW) {
                    invalidate();
                    if (isStarted) {
                        sendEmptyMessageDelayed(MESSAGE_DRAW, speed);
                        if (mAlpha == 0) {
                            return;
                        }
                        mAlpha -= mAlphaInterval;
                    }
                }
            }
        };
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = this.getWidth();
        height = this.getHeight();
        if (width <= 0 || height <= 0) {
            throw new RuntimeException("size illegal");
        }
        // 中心点
        centerX = width / 2;
        centerY = height / 2;
        // 最大辐射半径
        maxRadius = 10 + ((width > height) ? height / 2 : width / 2);
        if (maxRadius < (int) (density * DEFAULT_MIN_RADIUS)) {
            //throw new RuntimeException("size too small");
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(mColor);
        mPaint.setAlpha(mAlpha);

        if (radius <= 0) {
            return;
        }

        // 位置监听回调
        if (radius == (maxRadius - 15)) {
            if (listener != null) {
                listener.onArrival();
            }
        }

        // 重置
        if (radius > maxRadius) {
            stopRadiate();
            mAlpha = (int) (density * DEFAULT_ALPHA);
            radius = minRadius;
        }

        canvas.save();
        canvas.drawCircle(centerX, centerY, radius, mPaint);
        canvas.restore();
        radius += 1;
    }

    // ------------------ 辐射监听 --------------------------------
    private RadiationListener listener;

    public void setRadiationListener(RadiationListener listener) {
        this.listener = listener;
    }

    public interface RadiationListener {
        void onArrival();    // 到达指定位置
    }

    // 当view 被添加到window中，被绘制之前的回调。
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }
}
