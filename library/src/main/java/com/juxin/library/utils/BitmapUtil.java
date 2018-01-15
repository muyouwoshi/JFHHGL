package com.juxin.library.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;

import com.juxin.library.log.PLogger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * bitmap操作工具类
 */
public final class BitmapUtil {

    private static final int DEFAULT_BLUR_RADIUS = 12;

    /**
     * 得到压缩后的Bitmap，主要用于Activity中设置大图背景
     */
    public static Bitmap getDecodeBitmap(Context context, int resID, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), resID, options);

        options.inSampleSize = calculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resID, options);
        return bitmap;
    }

    /**
     * 计算InSampleSize
     * 宽的压缩比和高的压缩比的较小值  取接近的2的次幂的值
     * 比如宽的压缩比是3 高的压缩比是5 取较小值3  而InSampleSize必须是2的次幂，取接近的2的次幂4
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            int ratio = heightRatio < widthRatio ? heightRatio : widthRatio;
            // inSampleSize只能是2的次幂  将ratio就近取2的次幂的值
            if (ratio < 3) inSampleSize = ratio;
            else if (ratio < 6.5) inSampleSize = 4;
            else if (ratio < 8) inSampleSize = 8;
            else inSampleSize = ratio;
        }
        return inSampleSize;
    }

    /**
     * 旋转图片
     *
     * @param bmp   原始图片
     * @param angle 旋转的角度
     * @return Bitmap
     */
    public static Bitmap rotate(Bitmap bmp, float angle) {
        Matrix matrixRotateLeft = new Matrix();
        matrixRotateLeft.setRotate(angle);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrixRotateLeft, true);
    }

    /**
     * 旋转图片
     *
     * @param original 原始图片
     * @param angle    旋转的角度
     * @return Bitmap
     */
    public static Bitmap rotate(Bitmap original, final int angle) {
        if ((angle % 360) == 0) {
            return original;
        }

        final boolean dimensionsChanged = angle == 90 || angle == 270;
        final int oldWidth = original.getWidth();
        final int oldHeight = original.getHeight();
        final int newWidth = dimensionsChanged ? oldHeight : oldWidth;
        final int newHeight = dimensionsChanged ? oldWidth : oldHeight;

        Bitmap bitmap = Bitmap.createBitmap(newWidth, newHeight, original.getConfig());
        Canvas canvas = new Canvas(bitmap);

        Matrix matrix = new Matrix();
        matrix.preTranslate((newWidth - oldWidth) / 2f, (newHeight - oldHeight) / 2f);
        matrix.postRotate(angle, bitmap.getWidth() / 2f, bitmap.getHeight() / 2);
        canvas.drawBitmap(original, matrix, null);

        original.recycle();

        return bitmap;
    }

    /**
     * 按原比例缩放图片
     *
     * @param contentResolver ContentResolver
     * @param uri             图片的URI地址
     * @param maxWidth        缩放后的宽度
     * @param maxHeight       缩放后的高度
     * @return Bitmap
     */
    public static Bitmap scale(ContentResolver contentResolver, Uri uri, int maxWidth, int maxHeight) {
        PLogger.v("uri=" + uri.toString());
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            InputStream input = contentResolver.openInputStream(uri);
            BitmapFactory.decodeStream(input, null, options);

            int sourceWidth = options.outWidth;
            int sourceHeight = options.outHeight;

            PLogger.v("sourceWidth=" + sourceWidth + ", sourceHeight=" + sourceHeight);
            PLogger.v("maxWidth=" + maxWidth + ", maxHeight=" + maxHeight);

            input.close();

            float rate = Math.max(sourceWidth / (float) maxWidth, sourceHeight / (float) maxHeight);
            options.inJustDecodeBounds = false;
            options.inSampleSize = (int) rate;
            PLogger.v("rate=" + rate + ", inSampleSize=" + options.inSampleSize);

            input = contentResolver.openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);

            int w0 = bitmap.getWidth();
            int h0 = bitmap.getHeight();

            PLogger.v("w0=" + w0 + ", h0=" + h0);


            float scaleWidth = maxWidth / (float) w0;
            float scaleHeight = maxHeight / (float) h0;
            float maxScale = Math.min(scaleWidth, scaleHeight);
            PLogger.v("scaleWidth=" + scaleWidth + ", scaleHeight=" + scaleHeight);

            Matrix matrix = new Matrix();
            matrix.reset();
            if (maxScale < 1) matrix.postScale(maxScale, maxScale);

            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, w0, h0, matrix, true);

            input.close();
            // bitmap.recycle();

            return resizedBitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Drawable转换为Bitmap
     *
     * @param drawable Drawable
     * @return Bitmap
     */
    public static Bitmap getBitmap(Drawable drawable) {
        if (drawable == null)
            return null;

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888 : Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        }
    }

    /**
     * 为指定图片增加阴影
     *
     * @param map    　图片
     * @param radius 　阴影的半径
     * @return Bitmap
     */
    public static Bitmap drawShadow(Bitmap map, int radius) {
        if (map == null)
            return null;

        BlurMaskFilter blurFilter = new BlurMaskFilter(radius, BlurMaskFilter.Blur.OUTER);
        Paint shadowPaint = new Paint();
        shadowPaint.setMaskFilter(blurFilter);

        int[] offsetXY = new int[2];
        Bitmap shadowImage = map.extractAlpha(shadowPaint, offsetXY);
        shadowImage = shadowImage.copy(Config.ARGB_8888, true);
        Canvas c = new Canvas(shadowImage);
        c.drawBitmap(map, -offsetXY[0], -offsetXY[1], null);
        return shadowImage;
    }

    /**
     * 获得圆角的bitmap
     *
     * @param bitmap  Bitmap
     * @param roundPx 圆角值px
     * @return Bitmap
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static int getExifOrientation(ContentResolver cr, Uri contentUri) {
        int returnValue = 0;
        String uriString = contentUri.toString();

        if (ContentResolver.SCHEME_CONTENT.equals(contentUri.getScheme())) {
            // can post image
            String[] proj = {MediaStore.Images.Media.ORIENTATION};
            Cursor cursor = cr.query(contentUri, proj, null, null, null);

            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    returnValue = cursor.getInt(cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media.ORIENTATION));
                }
                cursor.close();
            }
        } else if (ContentResolver.SCHEME_FILE.equals(contentUri.getScheme())) {
            returnValue = getExifOrientation(contentUri.getPath());
        } else if (uriString.startsWith("/")) {
            returnValue = getExifOrientation(contentUri.getPath());
        }
        return returnValue;
    }

    public static int getExifOrientation(String fileName) {
        if (TextUtils.isEmpty(fileName)) return 0;

        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(fileName);
        } catch (IOException ex) {
            PLogger.printThrowable(ex);
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                // We only recognize a subset of orientation tag values.
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }

            }
        }
        return degree;
    }

    public static byte[] generateByteArray(Bitmap src, Bitmap.CompressFormat format, int quality) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        src.compress(format, quality, os);
        return os.toByteArray();
    }

    public static Bitmap getReflectBitmap(Bitmap originalImage, float rate) {
        if (originalImage == null || originalImage.isRecycled())
            return null;
        //The gap we want between the reflection and the original image
        final int reflectionGap = 4;

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int reflectHeight = Math.round(rate * height);


        //This will not scale but will flip on the Y axis
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        //Create a Bitmap with the flip matrix applied to it.
        //We only want the bottom half of the image
        Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height - reflectHeight, width, reflectHeight, matrix, false);

        //Create a new bitmap with same width but taller to fit reflection
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width, height + reflectHeight, Config.ARGB_8888);

        //Create a new Canvas with the bitmap that's big enough for
        //the image plus gap plus reflection
        Canvas canvas = new Canvas(bitmapWithReflection);
        //Draw in the original image
        canvas.drawBitmap(originalImage, 0, 0, null);
        //Draw in the gap
        Paint defaultPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);
        //Draw in the reflection
        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

        //Create a shader that is a linear gradient that covers the reflection
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0,
                bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff,
                Shader.TileMode.CLAMP);
        //Set the paint to use this shader (linear gradient)
        paint.setShader(shader);
        //Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        //Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, height, width,
                bitmapWithReflection.getHeight() + reflectionGap, paint);

        if (!originalImage.isRecycled()) {
            originalImage.recycle();
        }

        return bitmapWithReflection;
    }

    public static Bitmap getSquareBitmap(Bitmap src) {
        return getSquareBitmap(src, 0.1f);
    }

    public static Bitmap getSquareBitmap(Bitmap src, float rate) {
        if (src == null || src.isRecycled()) return null;

        Bitmap ret = src;
        int w = src.getWidth();
        int h = src.getHeight();
        int min = Math.min(w, h);
        int max = Math.max(w, h);
        float r = (float) (max - min) / (float) min;
        if (w != h && r > rate) {
            max = Math.round((1.0f + rate) * min);
            if (w > h) {
                ret = Bitmap.createBitmap(src, (w - max) / 2, 0, max, min);
            } else {
                ret = Bitmap.createBitmap(src, 0, (h - max) / 2, min, max);
            }
        }
        return ret;
    }

    public static Bitmap clipCircleBitmap(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) return null;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top = 0, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        width = width - 2;
        height = height - 2;
        if (width <= height) {
            roundPx = width / 2;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width,
                height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);

        paint.setAntiAlias(true);


        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);

        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }

        return output;
    }

    /**
     * 获取高斯模糊的bitmap，默认模糊度为12
     *
     * @param sentBitmap Bitmap
     * @return Bitmap
     */
    public static Bitmap getBlurredBitmap(Bitmap sentBitmap) {
        return getBlurredBitmap(sentBitmap, DEFAULT_BLUR_RADIUS);
    }

    /**
     * 获取高斯模糊的bitmap
     *
     * @param sentBitmap Bitmap
     * @param radius     radius
     * @return Bitmap
     */
    public static Bitmap getBlurredBitmap(Bitmap sentBitmap, int radius) {

        // Stack Blur v1.0 from
        // http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
        //
        // Java Author: Mario Klingemann <mario at quasimondo.com>
        // http://incubator.quasimondo.com
        // created Feburary 29, 2004
        // Android port : Yahel Bouaziz <yahel at kayenko.com>
        // http://www.kayenko.com
        // ported april 5th, 2012

        // This is a compromise between Gaussian Blur and Box blur
        // It creates much better looking blurs than Box Blur, but is
        // 7x faster than my Gaussian Blur implementation.
        //
        // I called it Stack Blur because this describes best how this
        // filter works internally: it creates a kind of moving stack
        // of colors whilst scanning through the image. Thereby it
        // just has to addPart one new block of color to the right side
        // of the stack and remove the leftmost color. The remaining
        // colors on the topmost layer of the stack are either added on
        // or reduced by one, depending on if they are on the right or
        // on the left side of the stack.
        //
        // If you are using this algorithm in your code please addPart
        // the following line:
        //
        // Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>

        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return (bitmap);
    }

    public static File compress(String src, String dest, int maxWidth, int quality) throws IOException {
        Bitmap bitmap = getCompressedBitmap(src, maxWidth);
        if (src == null || dest == null || bitmap == null) {
            return null;
        }
        PLogger.v("compress() maxWidth=" + maxWidth + " quality=" + quality);
        PLogger.v("compress() src=" + src);
        PLogger.v("compress() dest=" + dest);
        PLogger.v("compress() bitmap=(" + bitmap.getWidth() + "," + bitmap.getHeight() + ")");

        FileOutputStream fos = null;
        try {
            Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
            if (src.toLowerCase().lastIndexOf("png") != -1) {
                format = Bitmap.CompressFormat.PNG;
            }
            fos = new FileOutputStream(dest);
            bitmap.compress(format, quality, fos);
            bitmap.recycle();
            return new File(dest);
        } finally {
            if (fos != null) fos.close();
        }
    }

    private static Bitmap getCompressedBitmap(String path, int maxWidth) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int inSampleSize = 1;
        for (int w = options.outWidth; w > maxWidth * 2 - 10; w /= 2) {
            inSampleSize++;
        }
        PLogger.v("getCompressedBitmap() original bitmap=(" + options.outWidth + "," + options.outHeight + ")");
        PLogger.v("getCompressedBitmap() inSampleSize=" + inSampleSize);

        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        if (bitmap != null) {
            int bw = bitmap.getWidth();
            int bh = bitmap.getHeight();
            int length = bitmap.getRowBytes() * bitmap.getHeight();
            PLogger.v("getCompressedBitmap() decode bitmap size=(" + bw + "," + bh + ")");
            PLogger.v("getCompressedBitmap() decode bitmap length=(" + length / 1000 + "k)");

            Matrix m = new Matrix();
            if (bw > maxWidth) {
                float scale = (float) maxWidth / (float) bw;
                m.postScale(scale, scale);
                PLogger.v("getCompressedBitmap() matrix scale=" + scale);
            }
            int rotation = getExifOrientation(path);
            if (getExifOrientation(path) != 0) {
                m.postRotate(rotation);
            }
            PLogger.v("getCompressedBitmap() matrix rotation=" + rotation);
            Bitmap resultBitmap = Bitmap.createBitmap(bitmap, 0, 0, bw, bh, m, true);
            if (resultBitmap != bitmap) {
                bitmap.recycle();
            }
            int sw = resultBitmap.getWidth();
            int sh = resultBitmap.getHeight();
            length = resultBitmap.getRowBytes() * resultBitmap.getHeight();
            PLogger.v("getCompressedBitmap() final bitmap size=(" + sw + "," + sh + ")");
            PLogger.v("getCompressedBitmap() final bitmap length=(" + length / 1000 + "k)");
            return resultBitmap;
        }
        return null;
    }

    /**
     * 根据uri解析得到Bitmap
     *
     * @param resolver
     * @param uri
     * @param maxDim
     * @return
     */
    public static Bitmap decodeImage(final ContentResolver resolver, final Uri uri, final int maxDim) {
        // Get original dimensions
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream is = null;
        try {
            is = resolver.openInputStream(uri);
            BitmapFactory.decodeStream(is, null, options);
        } catch (Exception e) {
            PLogger.printThrowable(e);
            return null;
        } finally {
            if (is != null) try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        final int origWidth = options.outWidth;
        final int origHeight = options.outHeight;
        options.inJustDecodeBounds = false;
        options.inScaled = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inDither = true;
        options.inPreferredConfig = Config.RGB_565;

        if (origWidth > maxDim || origHeight > maxDim) {
            int k = 1;
            int tmpHeight = origHeight, tmpWidth = origWidth;
            while ((tmpWidth / 2) >= maxDim || (tmpHeight / 2) >= maxDim) {
                tmpWidth /= 2;
                tmpHeight /= 2;
                k *= 2;
            }
            options.inSampleSize = k;
        }

        Bitmap bitmap = null;
        try {
            is = resolver.openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(is, null, options);
        } catch (Exception e) {
            PLogger.printThrowable(e);
        } finally {
            if (is != null) try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (null != bitmap) {
            PLogger.v("decodeImage() " + bitmap.getWidth() + "x" + bitmap.getHeight());
        }

        return bitmap;
    }

    public static Bitmap getImage(Context context, final Uri uri, int maxDimen) {
        Bitmap bitmap = decodeImage(context.getContentResolver(), uri, maxDimen);
        Bitmap rotatedBitmap = rotate(bitmap, getExifOrientation(context.getContentResolver(), uri));
        if (bitmap != rotatedBitmap && bitmap != null) bitmap.recycle();
        return rotatedBitmap;
    }

    // =========================================================================

    /**
     * 通过byte流创建Bitmap
     */
    public static Bitmap bytesToBitmap(byte[] bytes) {
        if (bytes != null && bytes.length != 0) {
            try {
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            } catch (Error e) {
                PLogger.printThrowable(e);
            }
        }
        return null;
    }

    /**
     * 从Uri中获取图片
     *
     * @param cr  ContentResolver对象
     * @param uri 图片的Uri
     * @return Bitmap
     */
    public static Bitmap getBitmapFromUri(ContentResolver cr, Uri uri) {
        try {
            return BitmapFactory.decodeStream(cr.openInputStream(uri));
        } catch (FileNotFoundException e) {
            PLogger.printThrowable(e);
        }
        return null;
    }

    /**
     * 根据宽度和长度进行缩放图片
     *
     * @param path 图片的路径
     * @param w    宽度
     * @param h    长度
     * @return Bitmap
     */
    public static Bitmap createBitmap(String path, int w, int h) {
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            // 这里是整个方法的关键，inJustDecodeBounds设为true时将不为图片分配内存。
            BitmapFactory.decodeFile(path, opts);
            int srcWidth = opts.outWidth;// 获取图片的原始宽度
            int srcHeight = opts.outHeight;// 获取图片原始高度
            int destWidth = 0;
            int destHeight = 0;
            // 缩放的比例
            double ratio = 0.0;
            if (srcWidth < w || srcHeight < h) {
                ratio = 0.0;
                destWidth = srcWidth;
                destHeight = srcHeight;
            } else if (srcWidth > srcHeight) {// 按比例计算缩放后的图片大小，maxLength是长或宽允许的最大长度
                ratio = (double) srcWidth / w;
                destWidth = w;
                destHeight = (int) (srcHeight / ratio);
            } else {
                ratio = (double) srcHeight / h;
                destHeight = h;
                destWidth = (int) (srcWidth / ratio);
            }
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            // 缩放的比例，缩放是很难按准备的比例进行缩放的，目前我只发现只能通过inSampleSize来进行缩放，其值表明缩放的倍数，SDK中建议其值是2的指数值
            newOpts.inSampleSize = (int) ratio + 1;
            // inJustDecodeBounds设为false表示把图片读进内存中
            newOpts.inJustDecodeBounds = false;
            // 设置大小，这个一般是不准确的，是以inSampleSize的为准，但是如果不设置却不能缩放
            newOpts.outHeight = destHeight;
            newOpts.outWidth = destWidth;
            // 获取缩放后图片
            return BitmapFactory.decodeFile(path, newOpts);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取圆角图片
     */
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, pixels, pixels, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * 获取正方形的圆角图片
     */
    public static Bitmap toSquareRoundCorner(Bitmap bitmap, int pixels) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int grp = Math.abs(width - height);
        if (width > height) {
            width = width - grp;
        } else if (height > width) {
            height = height - grp;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, pixels, pixels, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 压缩图片
     */
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        Bitmap bm = BitmapFactory.decodeFile(filePath, options);
        if (bm == null) return null;

        int degree = readPictureDegree(filePath);
        bm = rotateBitmap(bm, degree);
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        } finally {
            try {
                if (baos != null) baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bm;
    }

    /**
     * 读取图片并压缩图片，获取小图的存储路径
     *
     * @param filePath 原图片地址
     * @return 压缩之后的小图地址
     */
    public static String getSmallBitmapAndSave(String filePath, String storeFolder) {
        Bitmap smallBitmap = getSmallBitmap(filePath);
        String savePath = saveBitmap(smallBitmap, storeFolder + UUID.randomUUID().toString() + ".jpg");
        if (smallBitmap != null && !smallBitmap.isRecycled()) {
            smallBitmap.recycle();
            smallBitmap = null;
        }
        return savePath;
    }

    /**
     * 获取图片大小
     */
    @SuppressLint("NewApi")
    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            return bitmap.getByteCount();
        }
        return bitmap.getRowBytes() * bitmap.getHeight();                //earlier version
    }

    /**
     * 旋转bitmap
     */
    private static Bitmap rotateBitmap(Bitmap bitmap, int rotate) {
        if (bitmap == null) return null;

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        // Setting post rotate to 90
        Matrix mtx = new Matrix();
        mtx.postRotate(rotate);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    /**
     * 顺时针旋转保存图片。
     */
    public static void rotateBitmap(String filePath, String saveFile, int rotate) {
        Bitmap bm = BitmapFactory.decodeFile(filePath);

        if (bm == null) {
            return;
        }

        bm = rotateBitmap(bm, rotate);
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(saveFile);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, fos);
        } catch (FileNotFoundException e) {
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 图片转换byte[]
     */
    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 原图压缩方法
     */
    public static byte[] compressBitmap(Bitmap bitmap, float size) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        int quality = 75;
        while (baos.toByteArray().length / 1024f > size) {
            quality = quality - 4;
            baos.reset();
            if (quality <= 0) {
                break;
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        }
        return baos.toByteArray();
    }

    /**
     * 压缩图片
     */
    public static byte[] compressImageToBytes(Bitmap image, int iScaleSize, int iMaxW, int iMaxH) {
        int w = image.getWidth();
        int h = image.getHeight();

        Bitmap thumbBmp = image;
        if ((w > iMaxW) || (h > iMaxH)) {
            float iRw = iMaxW / w;
            float iRh = iMaxH / h;
            float iR = Math.min(iRw, iRh);
            w = Math.round(w * iR);
            h = Math.round(h * iR);
            thumbBmp = Bitmap.createScaledBitmap(thumbBmp, w, h, true);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        thumbBmp.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        int options = 80;
        while (baos.toByteArray().length / 1024 > iScaleSize) {
            baos.reset();
            options -= 10;// 每次都减少10
            thumbBmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        return baos.toByteArray();
    }

    /**
     * 将Drawable转化为Bitmap
     *
     * @param drawable Drawable
     * @return Bitmap
     */
    public static Bitmap getBitmapFromDrawable(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
                .getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 从sd卡路径读取bitmap
     *
     * @param path 文件本地路径
     * @return bitmap
     */
    public static String imagePathToBase64(String path) {
        return encodeToBase64(getSmallBitmap(path), Bitmap.CompressFormat.JPEG, 100);
    }

    /**
     * bitmap转为base64
     */
    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        String base64 = "";
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        try {
            image.compress(compressFormat, quality, byteArrayOS);
            base64 = Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
            byteArrayOS.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return base64.replaceAll("\n", "");
    }

    /**
     * base64转为bitmap
     */
    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    /**
     * 将bitmap保存到sd卡
     *
     * @param savePath 保存文件路径
     * @return 文件保存路径
     */
    public static String saveBitmap(Bitmap bitmap, String savePath) {
        if (bitmap == null || TextUtils.isEmpty(savePath)) return null;
        File file = new File(savePath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return savePath;
    }

    /**
     * 图片等比例压缩
     *
     * @param filePath
     * @param reqWidth  期望的宽
     * @param reqHeight 期望的高
     * @return
     */
    public static Bitmap decodeSampledBitmap(String filePath, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = BitmapUtil.calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 图片缩放到指定宽高
     * <p/>
     * 非等比例压缩，图片会被拉伸
     *
     * @param bitmap 源位图对象
     * @param w      要缩放的宽度
     * @param h      要缩放的高度
     * @return 新Bitmap对象
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        return newBmp;
    }

    /**
     * Try to return the absolute file path from the given Uri  兼容了file:///开头的 和 content://开头的情况
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String getRealFilePathFromUri(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 判断图片高度和宽度是否过小
     */
    public static boolean bitmapIsSmall(String sPath, int size) {
        BitmapFactory.Options options = getBitmapOptions(sPath);
        return options.outWidth < size || options.outHeight < size;
    }

    private static BitmapFactory.Options getBitmapOptions(String sPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(sPath, options);

        if (options.outHeight == -1 || options.outWidth == -1) {
            try {
                ExifInterface exifInterface = new ExifInterface(sPath);
                options.outHeight = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, ExifInterface.ORIENTATION_NORMAL);//获取图片的高度
                options.outWidth = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, ExifInterface.ORIENTATION_NORMAL);//获取图片的宽度
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return options;
    }
}