package com.juxin.predestinate.ui.live.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;

import com.juxin.predestinate.R;

/**
 * 加载动画的Imageview
 * Created by zhoujie on 2017/7/17.
 */

public class LiveLoadAnimView extends android.support.v7.widget.AppCompatImageView{
    public LiveLoadAnimView(Context context) {
        super(context);
        initAnim(context);

        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

            }
        });
    }

    public LiveLoadAnimView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAnim(context);
    }

    /**
     * 设置动画资源
     * @param context 帧动画资源ID
     */
    private void initAnim(Context context) {
        this.setImageResource(R.drawable.anim_live_loading_in);

        start();
    }

    /**
     * 开始播放动画
     */
    public void start(){
        AnimationDrawable animationDrawable = (AnimationDrawable) getDrawable();
        animationDrawable.start();
    }

    /**
     * 结束播放动画
     */
    public void stop(){
        AnimationDrawable animationDrawable = (AnimationDrawable) getDrawable();
        animationDrawable.stop();
    }
}
