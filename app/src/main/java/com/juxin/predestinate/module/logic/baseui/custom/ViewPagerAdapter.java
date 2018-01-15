package com.juxin.predestinate.module.logic.baseui.custom;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.List;

/**
 * @author JohnsonLi
 * @version 1.0
 * @qq 505214658
 * @date 2015-04-28
 */
public class ViewPagerAdapter extends PagerAdapter {

    public List<View> views;

    @SuppressWarnings("unchecked")
    public <T> ViewPagerAdapter(List<T> views) {
        this.views = (List<View>) views;
    }

    @Override
    public int getCount() {
        return views == null ? 0 : views.size();
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public Object instantiateItem(View container, int position) {
        ((ViewPager) container).addView(views.get(position), 0);

        return views.get(position);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }
}
