package com.juxin.predestinate.ui.user.my.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.List;

/**
 * Created by Think on 2016/12/22.
 */

public class GiftViewPagerAdapter extends PagerAdapter {
    private List<GridView> mLists;

    public GiftViewPagerAdapter(Context mContext, List<GridView> ls) {
        mLists = ls;
    }

    @Override
    public int getCount() {
        return mLists.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mLists.get(position));
        return mLists.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
