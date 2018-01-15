package com.juxin.predestinate.module.logic.baseui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 向右滑动退出的顶层控件
 *
 * @author ZRP
 */
public class RightSlidLinearLayout extends LinearLayout {

    public RightSlidLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RightSlidLinearLayout(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    private int mTouchSlop;
    private int startX = 0, startY = 0;// 开始点
    private int moveDistanceX = 0, moveDistanceY = 0;// 移动的距离
    private VelocityTracker mVelocityTracker;
    private static final int XSPEED_MIN = 1000;//手指向右滑动时的最小速度（px/s）
    private List<ViewPager> mViewPagers = new LinkedList<ViewPager>();
    private BaseActivity activity;

    /**
     * 绑定到activity
     *
     * @param activity
     */
    public void attachToActivity(BaseActivity activity) {
        this.activity = activity;
        TypedArray a = activity.getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.windowBackground});
        int background = a.getResourceId(0, 0);
        a.recycle();

        ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView();
        ViewGroup decorChild = (ViewGroup) decor.getChildAt(0);
        decorChild.setBackgroundResource(background);
        decor.removeView(decorChild);
        addView(decorChild);
        decor.addView(this);
    }

    private List<View> mIgnoredViews = new ArrayList<View>();//滑动忽略控件列表

    /**
     * @param v 添加右滑退出忽略控件
     */
    public void addIgnoredView(View v) {
        if (!mIgnoredViews.contains(v)) {
            v.setClickable(true);
            mIgnoredViews.add(v);
        }
    }

    /**
     * @param v 移除右滑退出忽略控件
     */
    public void removeIgnoredView(View v) {
        mIgnoredViews.remove(v);
    }

    /**
     * 清除所有忽略的右滑退出控件
     */
    public void clearIgnoredViews() {
        mIgnoredViews.clear();
    }

    /**
     * 是否是可忽略的滑动事件
     *
     * @param ev 手势
     * @return 可忽略的view：true/不可忽略的view：false
     */
    private boolean isInIgnoredView(MotionEvent ev) {
        Rect rect = new Rect();
        int[] location = new int[2];
        for (View v : mIgnoredViews) {
            v.getLocationInWindow(location);
            rect.set(location[0], location[1], location[0] + v.getMeasuredWidth(), location[1] + v.getMeasuredHeight());
            if (rect.contains((int) ev.getX(), (int) ev.getY())) return true;
        }
        return false;
    }

    /**
     * 事件拦截操作
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 处理ViewPager冲突问题
        ViewPager mViewPager = getTouchViewPager(mViewPagers, ev);
        if (mViewPager != null && mViewPager.getCurrentItem() != 0) {
            return super.onInterceptTouchEvent(ev);
        }
        // 如果是添加的忽略view
        if (isInIgnoredView(ev)) return super.onInterceptTouchEvent(ev);

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) ev.getRawX();
                startY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) ev.getRawX();
                // 若满足此条件，屏蔽子类的touch事件
                if (moveX - startX > mTouchSlop
                        && Math.abs((int) ev.getRawY() - startY) < mTouchSlop) {
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:// 手指按下
                startX = (int) ev.getRawX();
                startY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:// 手指移动
                moveDistanceX = (int) (ev.getRawX() - startX);
                moveDistanceY = (int) (ev.getRawY() - startY);
                break;
            case MotionEvent.ACTION_UP:// 手指离开
                startX = 0;
                startY = 0;
                if ((moveDistanceX > ModuleMgr.getAppMgr().getScreenWidth() / 20)
                        && (moveDistanceX > 2 * Math.abs(moveDistanceY))
                        && getScrollVelocity() > XSPEED_MIN) {
                    activity.back();
                    return true;
                }
                moveDistanceX = 0;
                moveDistanceY = 0;
                recycleVelocityTracker();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 释放速度计算
     */
    private void recycleVelocityTracker() {
        mVelocityTracker.recycle();
        mVelocityTracker = null;
    }

    /**
     * 计算在x方向的速度
     *
     * @return 当前在x方向速度的绝对值
     */
    private int getScrollVelocity() {
        mVelocityTracker.computeCurrentVelocity(1000);
        int velocity = (int) mVelocityTracker.getXVelocity();
        return Math.abs(velocity);
    }

    /**
     * 获取SwipeBackLayout里面的ViewPager的集合
     *
     * @param mViewPagers
     * @param parent
     */
    private void getAlLViewPager(List<ViewPager> mViewPagers, ViewGroup parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            if (child instanceof ViewPager) {
                mViewPagers.add((ViewPager) child);
            } else if (child instanceof ViewGroup) {
                getAlLViewPager(mViewPagers, (ViewGroup) child);
            }
        }
    }

    /**
     * 返回我们touch的ViewPager
     *
     * @param mViewPagers
     * @param ev
     * @return
     */
    private ViewPager getTouchViewPager(List<ViewPager> mViewPagers, MotionEvent ev) {
        if (mViewPagers == null || mViewPagers.size() == 0) {
            return null;
        }
        Rect mRect = new Rect();
        int[] location = new int[2];
        for (ViewPager v : mViewPagers) {
            v.getLocationInWindow(location);
            mRect.set(location[0], location[1], location[0] + v.getMeasuredWidth(), location[1] + v.getMeasuredHeight());
            if (mRect.contains((int) ev.getX(), (int) ev.getY())) {
                return v;
            }
        }
        return null;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            getAlLViewPager(mViewPagers, this);
        }
    }
}