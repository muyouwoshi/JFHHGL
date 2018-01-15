package com.juxin.predestinate.ui.live.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.juxin.predestinate.R;

import java.lang.reflect.Field;

/**
 * Created by terry on 2017/10/18.
 */

public class StrokeTextView extends TextView{

    TextPaint mPaint;
    int mInnerColor;
    int mOuterColor;

    boolean mBDrawSideLine = true; //默认描边

    public StrokeTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.mPaint = getPaint();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LiveCharmStroke);
        this.mInnerColor = a.getColor(R.styleable.LiveCharmStroke_innerColor,0xFFFFFF);
        this.mOuterColor = a.getColor(R.styleable.LiveCharmStroke_outerColor,0xFFFFFF);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBDrawSideLine){
            //描外层
            setTextColorUseReflection(mOuterColor);
            mPaint.setStrokeWidth(5);
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mPaint.setFakeBoldText(true);
            mPaint.setShadowLayer(1,0,0,0);
            super.onDraw(canvas);

            //描内层
            setTextColorUseReflection(mInnerColor);
            mPaint.setStrokeWidth(0);
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mPaint.setFakeBoldText(false);
            mPaint.setShadowLayer(0,0,0,0);
        }
        super.onDraw(canvas);
    }

    private void setTextColorUseReflection(int color){
        Field textColorField;
        try {
            textColorField = TextView.class.getDeclaredField("mCurTextColor");
            textColorField.setAccessible(true);
            textColorField.set(this,color);
            textColorField.setAccessible(false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        mPaint.setColor(color);
    }
}
