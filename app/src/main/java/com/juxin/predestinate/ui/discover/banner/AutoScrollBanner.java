package com.juxin.predestinate.ui.discover.banner;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import java.lang.ref.WeakReference;

public class AutoScrollBanner extends ViewPager implements AutoScrollBannerInterface {

    private AntoBannerViewPagerHandler mListenersHandler;

    private boolean isRunning = false;

    private boolean stopScrollWhenTouch = true;

    public final static int METHOD_LEFT = 0;

    public final static int METHOD_RIGHT = 1;

    public final static int START_WHAT = 0;

    private final static int STOP_WHAT = 1;

    private int DEFAULT_ROLL_SPEED = 1500;

    private int method = METHOD_LEFT;

    private boolean smoothScroll = true;

    private AutoScrollBannerInterface mAutoBannerListener;

    public AutoScrollBanner(Context context) {
        super(context);
        init();
    }

    public AutoScrollBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mListenersHandler = new AntoBannerViewPagerHandler(this);
    }

    public void setmAutoBannerListener(AutoScrollBannerInterface mAutoBannerListener) {
        this.mAutoBannerListener = mAutoBannerListener;
    }

    public void startScroll() {
        this.isRunning = true;
        mListenersHandler.removeCallbacksAndMessages(null);
        Message message = mListenersHandler.obtainMessage(START_WHAT, mAutoBannerListener);
        mListenersHandler.sendMessageDelayed(message, DEFAULT_ROLL_SPEED);
    }

    public void stopScroll() {
        this.isRunning = false;
        mListenersHandler.removeCallbacksAndMessages(null);
        Message message = mListenersHandler.obtainMessage(STOP_WHAT, mAutoBannerListener);
        mListenersHandler.sendMessageDelayed(message, DEFAULT_ROLL_SPEED);
    }

    public void setSpeed(int speed) {
        DEFAULT_ROLL_SPEED = speed;
    }

    /**
     * 设置滚动的方向
     *
     * @param method 从左往右 从右往左
     */
    public void setMethod(int method) {
        this.method = method;
    }

    /**
     * 设置平滑滚动，从第一个滚动到其他位置 平滑滚动
     *
     * @param smoothScroll true为平滑滚动
     */
    public void setSmoothScroll(boolean smoothScroll) {
        this.smoothScroll = smoothScroll;
    }

    /**
     * 当触摸到AutoBanner的时候停止自动轮播
     */
    public void setStopScrollWhenTouch() {
        this.stopScrollWhenTouch = false;
    }

    public void scroll() {
        if (mAutoBannerListener != null) {
            mAutoBannerListener.scroll();
        }

        PagerAdapter adapter = getAdapter();
        int currentItem = getCurrentItem();
        int totalItem;
        if (adapter == null || (totalItem = adapter.getCount()) <= 1) {
            return;
        }

        int nextItemPostion = (method == METHOD_LEFT) ? --currentItem : ++currentItem;
        if (nextItemPostion < 0) {
            setCurrentItem(totalItem - 1, smoothScroll);
        } else if (currentItem == totalItem) {
            setCurrentItem(0, smoothScroll);
        } else {
            setCurrentItem(nextItemPostion);
        }

        isRunning = true;

        Message message = mListenersHandler.obtainMessage(START_WHAT, mAutoBannerListener);
        mListenersHandler.sendMessageDelayed(message, DEFAULT_ROLL_SPEED);
    }

    private static final class AntoBannerViewPagerHandler extends Handler {

        private WeakReference<AutoScrollBannerInterface> mAutoBanner;

        public AntoBannerViewPagerHandler(AutoScrollBannerInterface mAutoBannerViewPager) {
            this.mAutoBanner = new WeakReference<>(mAutoBannerViewPager);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AutoScrollBannerInterface autoBannerInterface = mAutoBanner.get();
            if (autoBannerInterface == null) {
                return;
            }

            switch (msg.what) {
                case AutoScrollBanner.START_WHAT:
                    autoBannerInterface.scroll();
                    break;
                case AutoScrollBanner.STOP_WHAT:
                    autoBannerInterface.stopScroll();
                    break;
                default:
                    break;
            }
        }

    }

    float startX = 0, startY = 0;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        int action = ev.getAction();
        float currX = ev.getX();
        float currY = ev.getY();
        if (stopScrollWhenTouch) {
            if (isRunning && MotionEvent.ACTION_DOWN == action) {
                stopScroll();
            } else if (!isRunning && MotionEvent.ACTION_UP == action) {
                startScroll();
            }
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startX = ev.getX();
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:

                if (currY - startY > 110) {
                    Log.e("","currY:"+currY+",startY:"+startY);
                    getParent().requestDisallowInterceptTouchEvent(false);
                    return false;
                }

                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

}