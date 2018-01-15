package com.juxin.predestinate.ui.discover;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.juxin.library.log.PLogger;

/**
 * 创建日期：2017/9/29
 * 描述:发现首页viewPager
 * 作者:lc
 */
public class DiscoverMViewPager extends ViewPager {

    private final int TITLE_NUM = 3;            //直播版 发现顶部三个按钮（最后一个是追女神）
    private final int CATCH_GIRL_ITEM = 2;      //追女神

    public DiscoverMViewPager(Context context) {
        super(context);
    }

    public DiscoverMViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return !isCatchGirl() && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return !isCatchGirl() && super.onTouchEvent(ev);
    }

    private boolean isCatchGirl() {
        return getAdapter().getCount() == TITLE_NUM && getCurrentItem() == CATCH_GIRL_ITEM; // 是否是 追女神 item
    }
}
