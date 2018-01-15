package com.juxin.predestinate.ui.user.my.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.juxin.library.image.ImageLoader;
import com.juxin.predestinate.R;

import java.util.List;

/**
 *
 * Created by Think on 2016/12/22.
 */

public class ViewPagerViewAdapter extends PagerAdapter {

    private Context context;
    private List<String> lists;

    public ViewPagerViewAdapter(Context context, List<String> lists) {
        this.context = context;
        this.lists = lists;
    }

    @Override
    public int getCount() {
        return lists == null ? 0 : lists.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.p1_private_photo_display_item, null);
        ImageView ivPic = (ImageView) view.findViewById(R.id.iv_private_photo_img);
        ImageLoader.loadFitCenter(context, lists.get(position), ivPic);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
