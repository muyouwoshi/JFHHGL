package com.juxin.library.image;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.SparseArray;
import android.widget.ImageView;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.juxin.library.R;
import com.juxin.library.image.transform.BlurImage;
import com.juxin.library.image.transform.CircleTransform;
import com.juxin.library.image.transform.RoundedCorners;
import com.juxin.library.utils.FileUtil;
import com.juxin.library.utils.NetworkUtils;

/**
 * 基于Glide图片请求，处理类
 */
public class ImageLoader {
    private static CenterCrop bitmapCenterCrop;
    private static FitCenter bitmapFitCenter;
    private static CircleTransform circleTransform;

    private static SparseArray<Object> cache = new SparseArray<>();//替换图缓存
    private static boolean usedCache = true;
    private static Headers headers = new LazyHeaders.Builder().addHeader("User-Agent", NetworkUtils.getUserAgent()).build();

    public static void init(Context context) {
        Context mContext = context.getApplicationContext();
        //Glide相关全局变量
        bitmapCenterCrop = new CenterCrop(mContext);
        bitmapFitCenter = new FitCenter(mContext);
        circleTransform = new CircleTransform(mContext);
    }

    /**
     * 加载头像
     */
    public static <T> void loadAvatar(Context context, T model, ImageView view) {
        loadPic(context, checkOssAvatar(model), view, R.drawable.default_head, R.drawable.default_head, bitmapCenterCrop);
    }

    public static <T> void loadCircleAvatar(Context context, T model, ImageView view) {
        loadCircleAvatar(context, model, view, 0);
    }

    public static <T> void loadCircleAvatar(Context context, T model, ImageView view, int borderWidth) {
        loadCircle(context, checkOssAvatar(model), view, R.drawable.default_head, R.drawable.default_head, borderWidth, Color.WHITE);
    }

    public static <T> void loadCircleAvatar(Context context, T model, ImageView view, int borderWidth, int color) {
        loadCircle(context, checkOssAvatar(model), view, R.drawable.default_head, R.drawable.default_head, borderWidth, color);
    }

    public static <T> void loadRoundAvatar(Context context, T model, ImageView view) {
        loadRoundAvatar(context, model, view, 8);
    }

    public static <T> void loadRoundAvatar(Context context, T model, ImageView view, int roundPx) {
        loadRoundCenterCrop(context, checkOssAvatar(model), view, roundPx, R.drawable.default_head, R.drawable.default_head);
    }

    /**
     * CenterCrop加载图片
     */
    public static <T> void loadCenterCrop(Context context, T model, ImageView view) {
        loadCenterCrop(context, model, view, R.drawable.default_pic, R.drawable.default_pic);
    }

    public static <T> void loadCenterCrop(Context context, T model, ImageView view, int defResImg, int errResImg) {
        loadPic(context, model, view, defResImg, errResImg, bitmapCenterCrop);
    }

    /**
     * FitCenter加载图片
     */
    public static <T> void loadFitCenter(Context context, T model, ImageView view) {
        loadFitCenter(context, model, view, R.drawable.default_pic, R.drawable.default_pic);
    }

    public static <T> void loadFitCenter(Context context, T model, ImageView view, int defResImg, int errResImg) {
        loadPic(context, model, view, defResImg, errResImg, bitmapFitCenter);
    }

    /**
     * 图片圆角处理: 默认全角处理，其他需求自行重载方法
     */
    public static <T> void loadRoundCenterCrop(Context context, T model, ImageView view) {
        loadRoundCenterCrop(context, model, view, 8, R.drawable.default_pic, R.drawable.default_pic);
    }

    public static <T> void loadRoundCenterCrop(Context context, T model, ImageView view,
                                               int roundPx, int defResImg, int errResImg) {
        loadStylePic(context, model, view, defResImg, errResImg, bitmapCenterCrop, getCacheRoundCorners(context, roundPx));
    }

    public static <T> void loadRoundFitCenter(Context context, T model, ImageView view) {
        loadRoundFitCenter(context, model, view, 8, R.drawable.default_pic, R.drawable.default_pic);
    }

    public static <T> void loadRoundFitCenter(Context context, T model, ImageView view,
                                              int roundPx, int defResImg, int errResImg) {
        loadStylePic(context, model, view, defResImg, errResImg, bitmapFitCenter, getCacheRoundCorners(context, roundPx));
    }

    /**
     * 图片圆角处理: 上面两个角
     */
    public static <T> void loadRoundTop(Context context, T model, ImageView view) {
        loadRoundTop(context, model, view, 15, R.drawable.default_pic, R.drawable.default_pic);
    }

    public static <T> void loadRoundTop(Context context, T model, ImageView view,
                                        int roundPx, int defResImg, int errResImg) {
        loadStylePic(context, model, view, defResImg, errResImg, bitmapCenterCrop, getCacheRoundCornersTop(context, roundPx));
    }

    /**
     * 加载为圆形图像
     */
    public static <T> void loadCircle(Context context, T model, ImageView view,
                                      int defResImg, int errResImg, int borderWidth, int borderColor) {
        circleTransform.setBorderWidth(borderWidth);
        circleTransform.setBorderColor(borderColor);
        loadStylePic(context, model, view, defResImg, errResImg, bitmapCenterCrop, circleTransform);
    }

    /**
     * 网络图片高斯模糊处理
     */
    public static <T> void loadBlur(Context context, T model, ImageView view) {
        loadBlur(context, model, view, 8);
    }

    public static <T> void loadBlur(Context context, T model, ImageView view, int level) {
        loadPic(context, model, view, R.drawable.default_pic, R.drawable.default_pic, bitmapCenterCrop, getCacheBlurImage(context, level));
    }

    public static <T> void loadBlur(Context context, T model, ImageView view, int level, @DrawableRes int defaultPic) {
        loadPic(context, model, view, defaultPic, defaultPic, bitmapCenterCrop, getCacheBlurImage(context, level));
    }
    /**
     * 以静态图片展示Gif
     */
    public static <T> void loadAsBmpFitCenter(Context context, T model, ImageView view) {
        loadAsBmp(context, model, view, bitmapFitCenter);
    }

    public static <T> void loadAsBmpCenterCrop(Context context, T model, ImageView view) {
        loadAsBmp(context, model, view, bitmapCenterCrop);
    }

    public static <T> void loadAsBmp(Context context, T model, ImageView view, Transformation<Bitmap>... transformation) {
        loadAsBmp(context, model, view, R.drawable.default_pic, R.drawable.default_pic, transformation);
    }

    /**
     * 带回调的加载
     */
    public static <T> void loadPicWithCallback(Context context, T model, GlideCallback callback) {
        loadPicWithCallback(context, model, callback, (Transformation<Bitmap>[]) null);
    }

    /**
     * 清除Glide内存缓存
     *
     * @return
     */
    public boolean clearCacheMemory(Context context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Glide.get(context).clearMemory();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void setHeaders(Headers headers) {
        ImageLoader.headers = headers;
    }

    public static GlideUrl getGlideUrl(String url) {
        return new GlideUrl(url, headers);
    }

    // ==================================== 内部私有调用 =============================================

    /**
     * 带占位图预处理样式的加载函数
     *
     * @param context
     * @param model
     * @param view
     * @param defResImg
     * @param errResImg
     * @param transformation
     * @param <T>
     */
    private static <T> void loadStylePic(final Context context, final T model, final ImageView view,
                                         int defResImg, final int errResImg,
                                         final Transformation<Bitmap>... transformation) {
        if (!isInvalidTag(view, model, transformation))
            return;

        setImgTag(view, model, transformation);

        loadPicWithCallback(context, defResImg, new GlideCallback() {
                    @Override
                    public void onResourceReady(final GlideDrawable defRes) {
                        loadPicWithCallback(context, errResImg, new GlideCallback() {
                                    @Override
                                    public void onResourceReady(GlideDrawable errRes) {
                                        loadPic(context, model, view, defRes, errRes, transformation);
                                    }
                                },
                                transformation);
                    }
                },
                transformation);
    }

    private static <T> void loadPic(Context context, T model, ImageView view,
                                    int defResImg, int errResImg,
                                    Transformation<Bitmap>... transformation) {
        try {
            if (!isInvalidTag(view, model, transformation))
                return;

            setImgTag(view, model, transformation);

            loadPic(context, model, view, defResImg > 0 ? ContextCompat.getDrawable(context, defResImg) : null,
                    errResImg > 0 ? ContextCompat.getDrawable(context, errResImg) : null, transformation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static <T> void loadPic(final Context context, final T model, final ImageView view,
                                    final Drawable defResImg, final Drawable errResImg,
                                    final Transformation<Bitmap>... transformation) {
        try {
            //先加载默认图
            if (isInvalidTag(view, model, transformation))
                return;

            view.setImageDrawable(defResImg);

            if (model == null || "".equals(model))
                return;

            //再去网络请求
            loadPicWithCallback(context, model, new GlideCallback() {
                @Override
                public void onResourceReady(GlideDrawable resource) {
                    if (isInvalidTag(view, model, transformation))
                        return;

                    getDrawableBuilder(context, model)
                            .diskCacheStrategy(resource.isAnimated() ? DiskCacheStrategy.SOURCE : DiskCacheStrategy.ALL)
                            .bitmapTransform(transformation)
                            .placeholder(defResImg)
                            .error(errResImg)
                            .into(view);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 带回调的加载
     */
    private static <T> void loadPicWithCallback(final Context context, final T model, final GlideCallback callback,
                                                final Transformation<Bitmap>... transformation) {
        try {
            if (isActDestroyed(context))
                return;

            if (usedCache && model instanceof Integer && (Integer) model > 0) {
                GlideDrawable drawable = getCacheDrawable((Integer) model, transformation);

                if (drawable != null && callback != null) {
                    callback.onResourceReady(drawable);
                    return;
                }
            }

            DrawableRequestBuilder<T> builder = getDrawableBuilder(context, model);

            if (transformation != null && transformation.length > 0)
                builder.bitmapTransform(transformation);

            builder.into(new SimpleTarget<GlideDrawable>() {
                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                    if (usedCache && model instanceof Integer && (Integer) model > 0) {
                        GlideDrawable drawable = getCacheDrawable((Integer) model, transformation);

                        if (drawable == null) {
                            putCacheDrawable((Integer) model, transformation, resource);
                        }
                    }

                    if (isActDestroyed(context))
                        return;

                    if (callback != null)
                        callback.onResourceReady(resource);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static <T> void loadAsBmp(Context context, T model, ImageView view, int defResImg, int errResImg,
                                      Transformation<Bitmap>... transformation) {
        try {
            if (isActDestroyed(context))
                return;

            getBitmapBuilder(context, model)
                    .transform(transformation)
                    .placeholder(defResImg)
                    .error(errResImg)
                    .into(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static <T> DrawableRequestBuilder<T> getDrawableBuilder(Context context, T model) {
        return getRequest(context, model)
//                .crossFade()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE);
    }

    private static <T> BitmapRequestBuilder<T, Bitmap> getBitmapBuilder(Context context, T model) {
        return getRequest(context, model)
                .asBitmap()
//                .crossFade()//加载gif会显示不正常
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL);
    }

    private static <T> DrawableTypeRequest<T> getRequest(Context context, T model) {
        return Glide.with(context).load(model);
    }

    private static boolean isActDestroyed(Context context) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 &&
                context instanceof Activity && ((Activity) context).isDestroyed();
    }

    /**
     * 是否无效标记
     *
     * @param view
     * @param model
     * @param <T>
     * @return
     */
    private static <T> boolean isInvalidTag(ImageView view, T model, Object[] trans) {
        Object url_obj = view.getTag(R.string.view_url_tag_id);
        int url_tag = url_obj == null ? 0 : (int) url_obj;
        if (model == null ? 0 != url_tag : model.hashCode() != url_tag)
            return true;

        Object trans_obj = view.getTag(R.string.view_trans_tag_id);
        int trans_tag = trans_obj == null ? 0 : (int) trans_obj;
        if (trans == null ? 0 != trans_tag : getArrayHash(trans) != trans_tag)
            return true;

        return false;
    }

    /**
     * 设置标记
     *
     * @param view
     * @param model
     * @param trans
     */
    private static <T> void setImgTag(ImageView view, T model, Object[] trans) {
        view.setTag(R.string.view_url_tag_id, model == null ? 0 : model.hashCode());
        view.setTag(R.string.view_trans_tag_id, trans == null ? 0 : getArrayHash(trans));
    }

    /**
     * 按存储对象的Hash值计算Array的Hash值
     *
     * @param trans
     * @return
     */
    private static int getArrayHash(Object[] trans) {
        int objHash = 0;
        if (trans != null) {
            for (Object tran : trans)
                objHash = objHash ^ tran.hashCode();
        }
        return objHash;
    }

    /**
     * 根据resId和Transformation生成CacheKey
     *
     * @param resId
     * @param trans
     * @return
     */
    private static int getCacheKey(int resId, Object[] trans) {
        return getArrayHash(trans) ^ resId;
    }

    /**
     * 根据一个Object数组型参数和ClassName的hashCode生成CacheKey
     *
     * @param className
     * @param params
     * @return
     */
    private static int getCacheKey(String className, Object[] params) {
        return getArrayHash(params) ^ className.hashCode();
    }

    private static RoundedCorners getCacheRoundCorners(Context context, int iRadius, RoundedCorners.CornerType cornerType) {
        int key = getCacheKey(RoundedCorners.class.getSimpleName(), new Integer[]{iRadius, cornerType.ordinal()});
        Object obj = cache.get(key);
        RoundedCorners roundedCorners = obj instanceof RoundedCorners ? (RoundedCorners) obj : null;
        if (roundedCorners == null) {
            roundedCorners = new RoundedCorners(context.getApplicationContext(), iRadius, 0, cornerType);
            cache.put(key, roundedCorners);
        }
        return roundedCorners;
    }

    private static RoundedCorners getCacheRoundCorners(Context context, int iRadius) {
        return getCacheRoundCorners(context, iRadius, RoundedCorners.CornerType.ALL);
    }

    private static RoundedCorners getCacheRoundCornersTop(Context context, int iRadius) {
        return getCacheRoundCorners(context, iRadius, RoundedCorners.CornerType.TOP);
    }

    private static BlurImage getCacheBlurImage(Context context, int iRadius) {
        int key = getCacheKey(BlurImage.class.getSimpleName(), new Integer[]{iRadius});
        Object obj = cache.get(key);
        BlurImage blurImage = obj instanceof BlurImage ? (BlurImage) obj : null;
        if (blurImage == null) {
            blurImage = new BlurImage(context.getApplicationContext(), iRadius);
            cache.put(key, blurImage);
        }
        return blurImage;
    }

    private static GlideDrawable getCacheDrawable(int model, Object[] trans) {
        int key = getCacheKey(model, trans);
        Object obj = cache.get(key);
        return obj instanceof GlideDrawable ? (GlideDrawable) obj : null;
    }

    private static void putCacheDrawable(int model, Object[] trans, GlideDrawable resource) {
        if (!(resource instanceof GlideBitmapDrawable)) return;

        Bitmap bmp = ((GlideBitmapDrawable) resource).getBitmap();
        if (bmp != null && !bmp.isRecycled()) {
            int key = getCacheKey(model, trans);
            cache.put(key, new GlideBitmapDrawable(null, bmp.copy(bmp.getConfig(), true)));
        }
    }

    /**
     * 检测并拼接头像的带裁切参数的图片url
     *
     * @param model
     * @return
     */
    public static <T> T checkOssAvatar(T model) {
        return checkOssImageUrl(model, 128);
    }

    public static <T> T checkOssImageUrl(T model) {
        return checkOssImageUrl(model, 128);
    }

    /**
     * 获取拼接裁切参数的图片url[适用于阿里云存储的图片]
     *
     * @param model
     * @param wh    最大图片宽高，按最短边优先缩放
     * @param <T>
     * @return
     */
    public static <T> T checkOssImageUrl(T model, int wh) {
        return checkOssImageUrl(model, wh, null);
    }

    /**
     * 获取拼接裁切参数的图片url[适用于阿里云存储的图片]
     *
     * @param model  图片原url
     * @param wh     裁切宽高
     * @param suffix 扩展名
     * @return 拼接之后的请求url
     */
    public static <T> T checkOssImageUrl(T model, int wh, String suffix) {
        if (!(model instanceof String))
            return model;
        String url = (String) model;
        if (TextUtils.isEmpty(url) || !FileUtil.isURL(url))
            return model;
        if (TextUtils.isEmpty(suffix)) {
            suffix = "jpg";
            try {
                String tmpUrl = url;
                int idx = tmpUrl.indexOf("?");
                if (idx >= 0) tmpUrl = tmpUrl.substring(0, idx);
                idx = tmpUrl.lastIndexOf(".");
                if (idx >= 0) suffix = tmpUrl.substring(idx + 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //e 	缩放优先边， 默认值：0：长边（默认值） 由于图片缩放过程中，原图尺寸与缩放尺寸不一定是相同比例，
        //      需要指定以长边还是短边优先进行缩放，如原图200 * 400（比例1:2），需要缩放为100 * 100（比例1:1）
        //      长边优先时，缩放为50 100；短边优先时(e=1)，缩放为`100 200`，若不特别指定，则代表长边优先
        //      0 表示按长边优先，默认值
        //      1 表示按短边优先
        //      2 强制缩略
        //l 	目标缩略图大于原图是否处理。值是1, 即不处理，是0，表示处理 	0/1, 默认是0
        //w 	指定目标缩略图的宽度  1-4096
        //h 	指定目标缩略图的高度  1-4096
        //o 	进行自动旋转
        //      0：表示按原图默认方向，不进行自动旋转
        //      1：表示根据图片的旋转参数，对图片进行自动旋转，如果存在缩略参数，是先进行缩略，再进行旋转。
        //      2: 表示根据图片的旋转参数，对图片进行自动旋转，如果存在缩略参数，先进行旋转，再进行缩略 	[0, 2]
        //Q 	- 决定图片的绝对质量，把原图质量压到Q%，如果原图质量小于指定数字，则不压缩。如果原图质量是100%，
        //        使用”90Q”会得到质量90％的图片；如果原图质量是95%，使用“90Q”还会得到质量90%的图片；如果原图质量是80%，
        //        使用“90Q”不会压缩，返回质量80%的原图。
        //      - 只能在保存格式为jpg/webp效果上使用，其他格式无效果。 如果一个转换url里，同时指定了q和Q，按Q来处理
        //      - 当取值为lossless时，webp格式图片会按照无损格式保存 	1-100,lossless
        url = url.contains("/oss/") && !url.contains("@1e_") ? (url + "@1e_1l_" + wh + "w_" + wh + "h_2o_75Q." + suffix) : url;
        return (T) url;
    }

    /**
     * 请求回调
     */
    public interface GlideCallback {
        void onResourceReady(GlideDrawable resource);
    }
}
