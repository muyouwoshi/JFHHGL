package com.juxin.predestinate.module.logic.baseui.custom;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class PhotoDisplayViewPager extends ViewPager {

	public PhotoDisplayViewPager(Context context) {
		super(context);
	}

	// 这个构造方法必须有要不然xml中无法加载 会报错
	public PhotoDisplayViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	// 这个方法是为了避免手势滑动的时候产生异常
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		try {
			return super.onInterceptTouchEvent(ev);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return false;
		}
	}
}
