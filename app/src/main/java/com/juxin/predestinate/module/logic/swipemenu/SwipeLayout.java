package com.juxin.predestinate.module.logic.swipemenu;

import android.content.Context;
import android.support.v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.juxin.library.log.PLogger;

public class SwipeLayout extends FrameLayout {
	private static final int STATE_CLOSE = 0;
	private static final int STATE_OPEN_MENU = 1;
	private static final int STATE_OPEN_CHOOSE = 2;

	private View mContentView;
	private View mMenuView;
	private View mChooseView;

	private ScrollerCompat mOpenScroller;
	private ScrollerCompat mCloseScroller;
	private ScrollerCompat mCloseChooseScroller;

	private int state = STATE_CLOSE;
	private float mDownX;
	private int mBaseX;

	private boolean mIsShowChooseView = true;

	public SwipeLayout(View contentView, View menuView, View chooseView, boolean isOpenChooseView) {
		this(contentView.getContext());
		mContentView = contentView;
		mMenuView = menuView;
		mChooseView = chooseView;
		if (isOpenChooseView) {
			state = STATE_OPEN_CHOOSE;
		}
		init();
	}

	private SwipeLayout(Context context) {
		this(context, null);
	}

	private SwipeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private void init() {
		mChooseView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mContentView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		mMenuView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		addView(mChooseView);
		addView(mContentView);
		addView(mMenuView);

		// 初始化ScrollerCompat对象
		mOpenScroller = ScrollerCompat.create(getContext());
		mCloseScroller = ScrollerCompat.create(getContext());
		mCloseChooseScroller = ScrollerCompat.create(getContext());
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mChooseView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(mContentView.getMeasuredHeight(), MeasureSpec.EXACTLY));
		mMenuView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(mContentView.getMeasuredHeight(), MeasureSpec.EXACTLY));
	}
    //170825 ADD START 修复当改变item大小后，整个item大小异常的问题
	public void changeContentView(View contentView){
		removeView(mContentView);
		mContentView = contentView;
		mContentView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		addView(mContentView);
	}
    //170825 ADD END 修复当改变item大小后，整个item大小异常的问题

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (state == STATE_OPEN_CHOOSE) {
			mContentView.layout(mChooseView.getWidth(), 0, getMeasuredWidth() + mChooseView.getWidth(), mContentView.getMeasuredHeight());
			mChooseView.layout(0, 0, mChooseView.getWidth(), mContentView.getMeasuredHeight());
		} else {
			mChooseView.layout(-mChooseView.getMeasuredWidth(), 0, 0, mContentView.getMeasuredHeight());
			mContentView.layout(0, 0, getMeasuredWidth(), mContentView.getMeasuredHeight());
		}
		mMenuView.layout(getMeasuredWidth(), 0, getMeasuredWidth() + mMenuView.getMeasuredWidth(), mContentView.getMeasuredHeight());
	}

	public boolean onSwipeTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mDownX = event.getX();
				break;
			case MotionEvent.ACTION_MOVE:
				int dis = (int) (mDownX - event.getX());
				if (state == STATE_OPEN_MENU) {
					dis += mMenuView.getWidth();
				}
				swipeMenu(dis);
				break;
			case MotionEvent.ACTION_UP:
				if ((mDownX - event.getX()) >= (mMenuView.getWidth() / 2)) {
					smoothOpenMenu();
				} else {
					smoothCloseMenu();
					return false;
				}
				break;
		}
		return true;
	}

	private void swipeChoose(int dis) {
		if (dis > 0) {
			dis = 0;
		}
		if (dis < -mChooseView.getWidth()) {
			dis = -mChooseView.getWidth();
		}
		mContentView.layout(-dis, 0, getMeasuredWidth() - dis, mContentView.getMeasuredHeight());
		mChooseView.layout(-mChooseView.getMeasuredWidth() - dis, 0, -dis, mContentView.getMeasuredHeight());
	}

	public void smoothOpenChoose() {
		if (mIsShowChooseView) {
			state = STATE_OPEN_CHOOSE;
			mOpenScroller.startScroll(0, 0, -mChooseView.getWidth(), 0, 350);
			postInvalidate();
		}
	}

	public void smoothCloseChoose() {
		if (mIsShowChooseView) {
			state = STATE_CLOSE;
			mCloseChooseScroller.startScroll(-mChooseView.getWidth(), 0, mChooseView.getWidth(), 0, 350);
			postInvalidate();
		}
	}

	public void openChoose() {
		if (state == STATE_CLOSE && mIsShowChooseView) {
			state = STATE_OPEN_CHOOSE;
			swipeChoose(-mChooseView.getWidth());
		}
	}

	public void closeChoose() {
		if (mCloseChooseScroller.computeScrollOffset()) {
			mCloseChooseScroller.abortAnimation();
		}
		if (state == STATE_OPEN_CHOOSE && mIsShowChooseView) {
			state = STATE_CLOSE;
			swipeChoose(0);
		}
	}

	public boolean isChooseOpen() {
		return state == STATE_OPEN_CHOOSE;
	}

	private void swipeMenu(int dis) {
		if (dis > mMenuView.getWidth()) {
			dis = mMenuView.getWidth();
		}
		if (dis < 0) {
			dis = 0;
		}
		mContentView.layout(-dis, 0, getMeasuredWidth() - dis, mContentView.getMeasuredHeight());
		mMenuView.layout(getMeasuredWidth() - dis, 0, getMeasuredWidth() + mMenuView.getMeasuredWidth() - dis, mContentView.getMeasuredHeight());
	}

	public void smoothOpenMenu() {
		state = STATE_OPEN_MENU;
		mOpenScroller.startScroll(-mContentView.getLeft(), 0, mMenuView.getWidth(), 0, 350);
		postInvalidate();
	}

	public void smoothCloseMenu() {
		state = STATE_CLOSE;
		mBaseX = -mContentView.getLeft();
		mCloseScroller.startScroll(0, 0, mBaseX, 0, 350);
		postInvalidate();
	}

	public void openMenu() {
		if (state == STATE_CLOSE) {
			state = STATE_OPEN_MENU;
			swipeMenu(mMenuView.getWidth());
		}
	}

	public void closeMenu() {
		if (mCloseScroller.computeScrollOffset()) {
			mCloseScroller.abortAnimation();
		}
		if (state == STATE_OPEN_MENU) {
			state = STATE_CLOSE;
			swipeMenu(0);
		}
	}

	public boolean isMenuOpen() {
		return state == STATE_OPEN_MENU;
	}

	@Override
	public void computeScroll() {
		if (state == STATE_OPEN_MENU) {
			if (mOpenScroller.computeScrollOffset()) {
				swipeMenu(mOpenScroller.getCurrX());
				postInvalidate();
			}
		} else if (state == STATE_OPEN_CHOOSE) {
			if (mOpenScroller.computeScrollOffset()) {
				swipeChoose(mOpenScroller.getCurrX());
				postInvalidate();
			}
		} else {
			if (mCloseScroller.computeScrollOffset()) {
				swipeMenu(mBaseX - mCloseScroller.getCurrX());
				postInvalidate();
			}
			if (mCloseChooseScroller.computeScrollOffset()) {
				swipeChoose(mCloseChooseScroller.getCurrX());
				postInvalidate();
			}
		}
	}

	public View getContentView() {
		return mContentView;
	}

	public View getChooseView() {
		return mChooseView;
	}

	public View getMenuView() {
		return mMenuView;
	}

	public void setIsShowChooseView(boolean isShowChooseView) {
		this.mIsShowChooseView = isShowChooseView;
	}
}
