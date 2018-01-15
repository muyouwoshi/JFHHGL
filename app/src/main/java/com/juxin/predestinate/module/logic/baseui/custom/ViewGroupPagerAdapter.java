package com.juxin.predestinate.module.logic.baseui.custom;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import com.juxin.library.controls.smarttablayout.PagerItem;
import java.util.List;

/**
 * viewGroupPagerAdapter
 * Created by Kind on 16/5/19.
 */
public class ViewGroupPagerAdapter extends PagerAdapter {

    public List<PagerItem> pagerItems;

    @SuppressWarnings("unchecked")
    public <T> ViewGroupPagerAdapter(List<T> views) {
        this.pagerItems = (List<PagerItem>) views;
    }

    @Override
    public int getCount() {
        return pagerItems == null ? 0 : pagerItems.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ViewGroup) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = getPagerItem(position).getView();
        container.addView(view, 0);
        return view;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return getPagerItem(position).getTitle();
    }

    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }


    protected PagerItem getPagerItem(int position) {
        return pagerItems.get(position);
    }

}
