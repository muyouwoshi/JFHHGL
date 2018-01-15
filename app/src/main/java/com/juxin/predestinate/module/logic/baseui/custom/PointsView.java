package com.juxin.predestinate.module.logic.baseui.custom;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.juxin.library.view.CustomFrameLayout;
import com.juxin.predestinate.R;

/**
 * Created by Kind on 2017/3/30.
 */

public class PointsView extends LinearLayout implements ViewPager.OnPageChangeListener{

    private int totalNum = 0;
    public int selectIndex = 0;
    private boolean firstShow = true;
    private int layoutId = R.layout.common_point;

    public PointsView(Context context) {
        super(context);
    }

    public PointsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PointsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 重置所有的状态。
     */
    private void reset() {
        removeAllViews();

        CustomFrameLayout view = null;

        clearFocus();
//        setVisibility(totalNum > 1 ? View.VISIBLE : View.INVISIBLE);
        setVisibility(firstShow ? View.VISIBLE : View.INVISIBLE);
        for (int i = 0; i < totalNum; ++i) {
            view = (CustomFrameLayout) inflate(getContext(), layoutId, null);
            view.showOfIndex(0);
            addView(view);
        }

        setPointState(selectIndex, true);
    }

    private void setPointState(int index, boolean select) {
        CustomFrameLayout view = (CustomFrameLayout) getChildAt(index);

        if (view != null) {
            view.showOfIndex(select ? 1 : 0);
        }
    }

    /**
     * 设置总点数。
     *
     * @param totalNum
     */
    public void setTotalPoints(int totalNum, boolean firstShow) {
        if (this.totalNum == totalNum) {
            return;
        }

        this.totalNum = totalNum;
        this.firstShow = firstShow;
        reset();
    }

    /**
     * 选中的页码，从0开始。
     *
     * @param index
     */
    public void setSelect(int index) {
        if (index == selectIndex) {
            return;
        }

        setPointState(selectIndex, false);
        selectIndex = index;
        setPointState(selectIndex, true);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setSelect(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean isFocused() {
        return false;
    }
}
