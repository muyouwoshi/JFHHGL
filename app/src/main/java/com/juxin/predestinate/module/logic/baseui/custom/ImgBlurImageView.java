package com.juxin.predestinate.module.logic.baseui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.juxin.library.image.transform.blur.FastBlur;
import com.juxin.predestinate.R;


public class ImgBlurImageView extends ImageView {
    private int iBlur;

    public ImgBlurImageView(Context context) {
        this(context, null);
    }

    public ImgBlurImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImgBlurImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray ivTypeArr = context.obtainStyledAttributes(attrs, R.styleable.ImgBlurImageView, defStyle, 0);
        iBlur = ivTypeArr.getInt(R.styleable.ImgBlurImageView_blur, 1);
        ivTypeArr.recycle();
    }

    private Bitmap doBlur(Bitmap bkg) {
        long startMs = System.currentTimeMillis();
        float scaleFactor = 8;
        int radius = (int) (iBlur / scaleFactor);

        Bitmap overlay = Bitmap.createBitmap(
                (int) (this.getMeasuredWidth() / scaleFactor),
                (int) (this.getMeasuredHeight() / scaleFactor),
                Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-this.getLeft() / scaleFactor, -this.getTop()
                / scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);

        overlay = FastBlur.blur(overlay, radius, true);

        Log.i("aaa", "doBlur:" + (System.currentTimeMillis() - startMs) + "ms");
        return overlay;
    }

    private Bitmap drawableToBitamp(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
//            BitmapDrawable bd = (BitmapDrawable) drawable;
//            return bd.getBitmap();
            return null;
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            super.onDraw(canvas);
            return;
        }
        long startMs = System.currentTimeMillis();
        Bitmap bmp = drawableToBitamp(drawable);
        if (iBlur != 0) {
            try {
                if (bmp != null) {
//					bmp = FastBlur.doBlur(bmp, iBlur, true);
                    bmp = doBlur(bmp);
                    canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                            | Paint.FILTER_BITMAP_FLAG));
                    canvas.drawBitmap(bmp, new Rect(0, 0, bmp.getWidth(), bmp.getHeight()),
                            new Rect(0, 0, getWidth(), getHeight()),
                            new Paint());
                    invalidate();
                }
            } catch (Exception e) {
            }
        }
        Log.i("aaa", "onDraw doBlur:" + (System.currentTimeMillis() - startMs) + "ms");
    }
}
