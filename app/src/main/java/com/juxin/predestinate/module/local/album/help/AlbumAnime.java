package com.juxin.predestinate.module.local.album.help;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ListView;

import com.juxin.predestinate.module.util.UIUtil;

/**
 * 相册展示动画管理
 * <p>
 * Created by Su on 2016/12/22.
 */

public class AlbumAnime {

    private ObjectAnimator turnOn;    // 相册文件夹弹出动画
    private ObjectAnimator turnOff;   // 相册文件夹关闭动画
    private Animation alphaAnimation; // 头部时间展示透明动画

    /**
     * 初始化动画
     */
    public void initAnim(Context context, ListView listView, OnAlbumAnimeListener listener) {
        if (Build.VERSION.SDK_INT >= 11) {  // no animation cause low SDK version
            initTurnOn(context, listView, listener);
            initTurnOff(context, listView, listener);
        }
        initHeaderAlpha(listener);
    }

    public void startTurnOn() {
        turnOn.start();
    }

    public void startTurnOff() {
        turnOff.start();
    }

    public void startAlpha(View view) {
        view.setAnimation(alphaAnimation);
        alphaAnimation.startNow();
    }

    public void cancelAlpha() {
        alphaAnimation.cancel();
    }

    public interface OnAlbumAnimeListener {
        void onDirectoryShow(ValueAnimator valueAnimator);    // 相册文件夹弹出动画

        void onDirectoryClose(ValueAnimator valueAnimator);   // 相册文件夹关闭动画

        void onHeaderDateShow(Animation animation);   // 相册头部时间展示动画
    }

    public void release() {
        turnOn = null;
        turnOff = null;
        alphaAnimation = null;
    }

    // ********************************* 内部初始化 *************************************

    /**
     * 初始化弹出动画
     */
    private void initTurnOn(final Context context, final ListView listView, final OnAlbumAnimeListener listener) {
        turnOn = ObjectAnimator.ofInt(listView, "bottomMargin", -UIUtil.dip2px(context, 400), 0);
        turnOn.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                listener.onDirectoryShow(valueAnimator);
            }
        });
        turnOn.setDuration(500);
    }

    /**
     * 初始化关闭动画
     */
    private void initTurnOff(final Context context, final ListView listView, final OnAlbumAnimeListener listener) {
        turnOff = ObjectAnimator.ofInt(listView, "bottomMargin", 0, -UIUtil.dip2px(context, 400));
        turnOff.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                listener.onDirectoryClose(valueAnimator);
            }
        });
        turnOff.setDuration(500);
    }

    /**
     * 初始化头部透明度动画
     */
    private void initHeaderAlpha(final OnAlbumAnimeListener listener) {
        alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                listener.onHeaderDateShow(animation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }
}
