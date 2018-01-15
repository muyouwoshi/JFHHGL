package com.juxin.predestinate.ui.live.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.os.CountDownTimer;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.ui.live.bean.DoubleGiftBean;
import com.juxin.predestinate.ui.live.callback.OnSendGiftCallbackListener;
import com.juxin.predestinate.ui.live.util.FrameAnimation;

/**
 * Created by terry on 2017/7/27.
 * 直播间礼物连击
 */

public class LiveDoubleHitGiftView extends FrameLayout {

    TextView gift_double_count_tv;
    ImageView gift_click_iv;
    ImageView gift_loading_iv;

    int mClickCount = 0;
    int millisInFuture = 2900;
    FrameAnimation loadingAnimation;
    DoubleGiftBean doubleGiftBean = null;
    OnDoubleHitListener onDoubleHitListener;
    OnSendGiftCallbackListener giftComplete;

    public LiveDoubleHitGiftView(@NonNull Context context) {
        super(context);
    }

    public LiveDoubleHitGiftView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LiveDoubleHitGiftView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int[] getRes() {
        TypedArray typedArray = getResources().obtainTypedArray(R.array.live_double_gif);
        int len = typedArray.length();
        int[] resId = new int[len];
        for (int i = 0; i < len; i++) {
            resId[i] = typedArray.getResourceId(i, -1);
        }
        typedArray.recycle();
        return resId;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        gift_double_count_tv = (TextView) findViewById(R.id.gift_double_count_tv);
        gift_click_iv = (ImageView) findViewById(R.id.gift_click_iv);
        gift_loading_iv = (ImageView) findViewById(R.id.gift_loading_iv);

        loadingAnimation = new FrameAnimation(gift_loading_iv, getRes(), 100, true);

//        gift_loading_iv.setImageResource(R.drawable.l1_gift_double_hit_loading_anim);
        gift_click_iv.setImageResource(R.drawable.l1_gift_double_hit_click_anim);

        this.setOnClickListener(new OnClickListener() {//连击送礼物
            @Override
            public void onClick(View v) {

                if (doubleGiftBean != null) {
                    mClickCount++;
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }
                    millisInFuture = 3000;
                    countDownTimer.start();
                    if (giftTimer != null) {
                        giftTimer.onFinish();
                        giftTimer.cancel();
                        giftTimer.start();
                    }
                    gift_loading_iv.setVisibility(View.GONE);
                    gift_click_iv.setVisibility(View.VISIBLE);
                    ((AnimationDrawable) gift_click_iv.getDrawable()).stop();
                    ((AnimationDrawable) gift_click_iv.getDrawable()).start();

                    ModuleMgr.getChatMgr().sendLiveGiftMsg(doubleGiftBean.channel_id, doubleGiftBean.room_id, doubleGiftBean.gift_id, doubleGiftBean.gift_count, giftComplete, false);
                }
            }
        });
    }

    /**
     * 设置回调
     *
     * @param listener 连击结束回调
     * @param complete 发送礼物接口回调
     */
    public void setOnDoubleHitListener(OnDoubleHitListener listener, OnSendGiftCallbackListener complete) {
        this.onDoubleHitListener = listener;
        this.giftComplete = complete;
    }

    /**
     * 设置连击礼物的参数
     *
     * @param bean
     */
    public void setDoubleGiftBean(DoubleGiftBean bean) {
        this.doubleGiftBean = bean;
    }

    /**
     * 启动连击效果
     */
    public void startLoadingDoubleHint() {
        if (mClickCount > 0) return;
//        ((AnimationDrawable)gift_loading_iv.getDrawable()).start();

        if (loadingAnimation == null) {
            loadingAnimation = new FrameAnimation(gift_loading_iv, getRes(), 100, true);
        }
        loadingAnimation.restartAnimation();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            millisInFuture = 2900;
            countDownTimer.start();
        }
    }

    private CountDownTimer giftTimer = new CountDownTimer(850, 850) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            gift_click_iv.setVisibility(View.GONE);
            gift_loading_iv.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (giftTimer != null) {
            giftTimer.cancel();
        }
        mClickCount = 0;
    }

    private CountDownTimer countDownTimer = new CountDownTimer(millisInFuture, 100) {
        @Override
        public void onTick(long millisUntilFinished) {
            millisUntilFinished = millisUntilFinished / 100;
            gift_double_count_tv.setText(millisUntilFinished + "\n连击");
        }

        @Override
        public void onFinish() {
            onReset();
        }
    };

    public void onReset(){
        setVisibility(View.GONE);
        if (onDoubleHitListener != null) onDoubleHitListener.onHitEnd();
        gift_loading_iv.setVisibility(View.VISIBLE);
        gift_click_iv.setVisibility(View.GONE);
        loadingAnimation.release();
        ((AnimationDrawable) gift_click_iv.getDrawable()).stop();
        mClickCount = 0;
        millisInFuture = 2900;
    }

    public interface OnDoubleHitListener {
        void onHitEnd();
    }
}
