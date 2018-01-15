package com.juxin.library.image.transform;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.juxin.library.image.transform.blur.FastBlur;

/**
 * 图片高斯模糊处理
 */
public class BlurImage implements Transformation<Bitmap> {

    private static int MAX_RADIUS = 25;
    private static int DEFAULT_DOWN_SAMPLING = 4;

//    private Context mContext;
    private BitmapPool mBitmapPool;

    public void setRadius(int mRadius) {
        this.mRadius = mRadius;
    }

    private int mRadius;
    private int mSampling;

    public BlurImage(Context context) {
        this(context, Glide.get(context).getBitmapPool(), MAX_RADIUS, DEFAULT_DOWN_SAMPLING);
    }

    public BlurImage(Context context, BitmapPool pool) {
        this(context, pool, MAX_RADIUS, DEFAULT_DOWN_SAMPLING);
    }

    public BlurImage(Context context, BitmapPool pool, int radius) {
        this(context, pool, radius, DEFAULT_DOWN_SAMPLING);
    }

    public BlurImage(Context context, int radius) {
        this(context, Glide.get(context).getBitmapPool(), radius, DEFAULT_DOWN_SAMPLING);
    }

    public BlurImage(Context context, int radius, int sampling) {
        this(context, Glide.get(context).getBitmapPool(), radius, sampling);
    }

    public BlurImage(Context context, BitmapPool pool, int radius, int sampling) {
//        mContext = context.getApplicationContext();
        mBitmapPool = pool;
        mRadius = radius;
        mSampling = sampling;
    }

    @Override
    public Resource<Bitmap> transform(Resource<Bitmap> resource, int outWidth, int outHeight) {
        Bitmap source = resource.get();

        int width = source.getWidth();
        int height = source.getHeight();
        int scaledWidth = width / mSampling;
        int scaledHeight = height / mSampling;

        Bitmap bitmap = mBitmapPool.get(scaledWidth, scaledHeight, Bitmap.Config.RGB_565);
        if (bitmap == null)
            bitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);
        canvas.scale(1 / (float) mSampling, 1 / (float) mSampling);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(source, 0, 0, paint);

        return BitmapResource.obtain(FastBlur.blur(bitmap, mRadius, true), mBitmapPool);
    }

    @Override
    public String getId() {
        return "BlurTransformation(radius=" + mRadius + ", sampling=" + mSampling + ")";
    }
}
