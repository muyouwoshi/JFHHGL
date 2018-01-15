package com.juxin.predestinate.ui.live.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.juxin.predestinate.ui.live.ui.LiveTopFragment;
import com.juxin.predestinate.ui.live.ui.UserLiveFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by terry on 2017/7/12.
 */

public class LiveUiAdapter extends FragmentPagerAdapter {

    List<Fragment> views = new ArrayList();
    private LiveTopFragment mLiveTopFragment;

    public LiveUiAdapter(FragmentManager fm){
        super(fm);

        mLiveTopFragment = new UserLiveFragment();

//        LiveEmptyFragment emptyFragment = new LiveEmptyFragment();
//        views.add(emptyFragment);
        views.add(mLiveTopFragment);
    }

    @Override
    public Fragment getItem(int position) {
        return views.get(position);
    }

    @Override
    public int getCount() {
        return views.size();
    }

    /**
     * 获取topFragment
     */
    public LiveTopFragment getTopFragment() {
        return mLiveTopFragment;
    }
}
