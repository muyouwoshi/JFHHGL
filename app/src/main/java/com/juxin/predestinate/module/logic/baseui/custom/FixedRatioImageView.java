package com.juxin.predestinate.module.logic.baseui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.juxin.predestinate.R;

/**
 * Created by zm on 2017/6/15
 */
public class FixedRatioImageView extends ImageView {
    private int mWidth;
    private int mHeight;
    private int mWidthRatio = 1;
    private int mHeightRatio = 1;

    public FixedRatioImageView(Context context) {
        this(context, null);
    }

    public FixedRatioImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FixedRatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FixedRatioView);
        mWidthRatio = a.getInt(R.styleable.FixedRatioView_mWidthRatio, 1);
        mHeightRatio = a.getInt(R.styleable.FixedRatioView_mHeightRatio, 1);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        if (mWidth != 0 && mHeightRatio != 0 && mWidthRatio != 0) {
            mHeight = mWidth * mHeightRatio / mWidthRatio;
        }
        if (this.getLayoutParams() != null && mHeight != 0) {
            this.getLayoutParams().height = mHeight;
        }
    }
}
