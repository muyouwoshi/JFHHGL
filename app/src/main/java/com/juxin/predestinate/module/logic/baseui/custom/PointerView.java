package com.juxin.predestinate.module.logic.baseui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.juxin.predestinate.R;

public class PointerView extends View implements ViewPager.OnPageChangeListener{

    /**
     * 在图标之外将其向内挤压
     */
    public static final int FILL_CENTER = -1;
    /**
     * 在图标之间将其向外撑开
     */
    public static final int FILL_SPACING = -2;

    private Drawable mPointer;

    private static final int[] EMPTY_STATE_SET = {android.R.attr.state_empty};
    private static final int[] SELECTED_STATE_SET = {android.R.attr.state_selected};

    private int mDrawableWidth = -1;
    private int mDrawableHeight = -1;
    private int mSpacing = FILL_CENTER;

    private int mPointerCount;
    private int mCurrentPosition;
    private float mPositionOffset = 0f;

    public PointerView(Context context) {
        this(context, null);
    }

    public PointerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PointerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PointerView, defStyleAttr, 0);
        Drawable pointer = a.getDrawable(R.styleable.PointerView_pointerDrawable);
        updatePointerDrawable(pointer);
        int spacing = a.getLayoutDimension(R.styleable.PointerView_spacing, FILL_CENTER);
        if (spacing == FILL_CENTER || spacing == FILL_SPACING) {
            mSpacing = spacing;
        } else if (spacing >= 0) {
            mSpacing = spacing;
        } else {
            mSpacing = 0;
        }
        mPointerCount = a.getInt(R.styleable.PointerView_pointerCount, 0);
        if (mPointerCount < 0) {
            throw new IllegalArgumentException("The count must be greater than 0.");
        }
        mCurrentPosition = a.getInt(R.styleable.PointerView_currentPosition, 0);
        a.recycle();
    }

    public void setViewPager(ViewPager viewPager){
        this.setPointerCount(viewPager.getAdapter().getCount());
        this.setCurrentPosition(viewPager.getCurrentItem());
        viewPager.addOnPageChangeListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w;
        int h;

        int count = mPointerCount;

        if (mPointer == null) {
            mDrawableWidth = -1;
            mDrawableHeight = -1;
            w = h = 0;
        } else {
            w = mDrawableWidth;
            h = mDrawableHeight;
            if (w < 0) w = 0;
            if (h < 0) h = 0;

            w *= count;

            if (mSpacing > 0 && count > 1) {
                w += (count - 1) * mSpacing;
            }
        }

        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        w += paddingLeft + paddingRight;
        h += paddingTop + paddingBottom;

        w = Math.max(w, getSuggestedMinimumWidth());
        h = Math.max(h, getSuggestedMinimumHeight());

        int widthSize;
        if (count > 0 && (mSpacing == FILL_CENTER || (mSpacing == FILL_SPACING && count > 1))) {
            widthSize = getDefaultSize(w, widthMeasureSpec);
        } else {
            widthSize = resolveSizeAndState(w, widthMeasureSpec, 0);
        }
        int heightSize = resolveSizeAndState(h, heightMeasureSpec, 0);

        setMeasuredDimension(widthSize, heightSize);
    }

    /**
     * 设置图标的间距
     *
     * @param spacing 间距，单位为像素
     * @see #FILL_CENTER
     * @see #FILL_SPACING
     */
    public void setSpacing(int spacing) {
        if (spacing < FILL_SPACING)
            spacing = 0;

        if (spacing != mSpacing) {
            mSpacing = spacing;
            requestLayout();
            invalidate();
        }
    }

    /**
     * @return 当前图标的间距
     */
    public int getSpacing() {
        return mSpacing;
    }

    /**
     * 设置图标的数量
     *
     * @param count 图标的数量
     */
    public void setPointerCount(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("The count must be greater than 0.");
        }

        if (count != mPointerCount) {
            mPointerCount = count;
            requestLayout();
            invalidate();
        }
    }

    /**
     * @return 当前图标的数量
     */
    public int getPointerCount() {
        return mPointerCount;
    }

    /**
     * 设置当前指示的位置
     *
     * @param position 位置
     * @see #setCurrentPosition(int, float)
     */
    public void setCurrentPosition(int position) {
        setCurrentPosition(position, 0f);
    }

    /**
     * 设置当前指示的位置和偏移量
     *
     * @param position       位置
     * @param positionOffset 偏移量
     */
    public void setCurrentPosition(int position, float positionOffset) {
        positionOffset = Math.max(0, Math.min(positionOffset, 1f));
        if (position != mCurrentPosition || positionOffset != mPositionOffset) {
            mPositionOffset = positionOffset;
            mCurrentPosition = position;
            postInvalidate();
        }
    }

    /**
     * @return 当前指示的位置
     */
    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    /**
     * @return 图标Drawable
     */
    public Drawable getPointerDrawable() {
        return mPointer;
    }

    /**
     * 设置指示图标的资源ID
     *
     * @param resId 资源ID
     * @see #setPointerDrawable(Drawable)
     */
    public void setPointerResource(int resId) {
        final int oldWidth = mDrawableWidth;
        final int oldHeight = mDrawableHeight;

        updatePointerDrawable(null);

        Drawable point = null;
        if (resId != 0) {
            try {
                point = getResources().getDrawable(resId);
            } catch (Exception e) {
                // Don't try again.
            }
        }
        updatePointerDrawable(point);

        if (oldWidth != mDrawableWidth || oldHeight != mDrawableHeight) {
            requestLayout();
        }

        invalidate();
    }

    /**
     * 设置指示图标Drawable
     *
     * @param point 图标Drawable
     */
    public void setPointerDrawable(Drawable point) {
        if (mPointer != point) {
            final int oldWidth = mDrawableWidth;
            final int oldHeight = mDrawableHeight;

            updatePointerDrawable(point);

            if (oldWidth != mDrawableWidth || oldHeight != mDrawableHeight) {
                requestLayout();
            }
            invalidate();
        }
    }

    private void updatePointerDrawable(Drawable point) {
        if (mPointer != null) {
            mPointer.setCallback(null);
            unscheduleDrawable(mPointer);
        }

        mPointer = point;

        if (point != null) {
            point.setCallback(this);
            point.setVisible(getVisibility() == VISIBLE, true);
            mDrawableWidth = point.getIntrinsicWidth();
            mDrawableHeight = point.getIntrinsicHeight();

            point.setBounds(0, 0, mDrawableWidth, mDrawableHeight);
        } else {
            mDrawableWidth = mDrawableHeight = -1;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int count = mPointerCount;
        final int dw = mDrawableWidth;
        if (count <= 0 || dw <= 0 || mDrawableHeight <= 0)
            return;

        int width = getWidth();
        int height = getHeight();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        float spacing = mSpacing;
        boolean fillCenter = false;
        if (spacing < 0) {
            if (spacing == FILL_CENTER) {
                fillCenter = true;
                int w = (width - paddingLeft - paddingRight) - dw * count;
                spacing = w / (float) (count + 1);
            } else if (spacing == FILL_SPACING && count > 1) {
                int w = (width - paddingLeft - paddingRight) - dw * count;
                spacing = w / (float) (count - 1);
            } else {
                spacing = 0f;
            }
        }

        final int saveCount = canvas.save();
        int rightBound = width - paddingRight;

        canvas.clipRect(paddingLeft, paddingTop, rightBound, height - paddingBottom);
        canvas.translate(paddingLeft, paddingTop);

        int currentPosition = mCurrentPosition;
        boolean offset = mPositionOffset != 0f && count > 1 && currentPosition >= 0 && currentPosition < count - 1;
        final int saveCount2 = offset ? canvas.save() : -1;

        Drawable pointer = mPointer;
        float translateX = paddingLeft;
        boolean fill = mSpacing < 0;
        for (int i = 0; i < count && translateX < rightBound; i++) {
            if (!fill) {
                translateX += (float) dw + spacing;
            }
            if (fillCenter) {
                canvas.translate(spacing, 0);
            }

            pointer.setState((!offset && i == currentPosition) ? SELECTED_STATE_SET : EMPTY_STATE_SET);
            pointer.draw(canvas);
            canvas.translate(fillCenter ? dw : ((float) dw + spacing), 0);
        }

        if (offset) {
            canvas.restoreToCount(saveCount2);
            translateX = ((float) dw + spacing) * currentPosition;
            if (fillCenter) {
                translateX += spacing;
            }

            if (translateX + paddingLeft < rightBound) {
                canvas.translate(translateX, 0);
                pointer.setState(SELECTED_STATE_SET);
                onDrawOffset(canvas, pointer, spacing, mPositionOffset);
            }
        }

        canvas.restoreToCount(saveCount);
    }

    /**
     * 绘画偏移图标
     * @param canvas 画布
     * @param pointer 图标Drawable
     * @param spacing 间距
     * @param positionOffset 偏移量(0~1)
     */
    protected void onDrawOffset(Canvas canvas, Drawable pointer, float spacing, float positionOffset) {
        Rect bounds = pointer.getBounds();
        int right = bounds.right;
        int dw = pointer.getIntrinsicWidth();
        pointer.setBounds(0, 0, dw + (int) (spacing * Math.sin(positionOffset * Math.PI) / 1d), bounds.bottom);
        canvas.translate((dw + spacing) * positionOffset, 0);
        pointer.draw(canvas);
        pointer.setBounds(0, 0, right, bounds.bottom);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        this.setCurrentPosition(position, positionOffset);
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
