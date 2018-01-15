package com.juxin.predestinate.ui.live.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juxin.library.log.PLogger;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 直播间 消费榜
 * Created by chengxiaobo on 2017/10/23.
 */

public class LiveConsumeListDialog extends BaseDialogFragment implements View.OnClickListener {

    private TextView mTv1;
    private TextView mTv2;

    private View mView1;
    private View mView2;

    private List<Fragment> mFragmentList;
    private String mRoomid;

    private ViewPager viewpager;
    private int[] typeArr = new int[]{1, 2};//1- 今日榜，  2 - 七日榜， 3 - 总榜


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRoomid = (String) getArguments().get("roomid");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        setContentView(R.layout.live_consume_list_dialog);
        initView();
        initViewState();

        return getContentView();
    }

    private void initView() {

        mTv1 = (TextView) findViewById(R.id.tv_1);
        mTv2 = (TextView) findViewById(R.id.tv_2);

        mView1 = findViewById(R.id.view_1);
        mView2 = findViewById(R.id.view_2);

        viewpager = (ViewPager) findViewById(R.id.viewpager);
        viewpager.setVisibility(View.VISIBLE);
    }

    private void initViewState() {

        mFragmentList = new ArrayList<Fragment>();

        for (int i = 0; i < 2; ++i) {
            LiveConsumeListFragment fragment = new LiveConsumeListFragment();
            Bundle bundle = new Bundle();
            bundle.putString("type", typeArr[i] + "");
            bundle.putString("roomid", mRoomid);
            fragment.setArguments(bundle);

            mFragmentList.add(fragment);
        }

        viewpager.setAdapter(new MyPagerFragmentAdapter(getChildFragmentManager(), mFragmentList));
        viewpager.setOffscreenPageLimit(3);

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                updateDataAndView(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewpager.post(new Runnable() {
            @Override
            public void run() {
                viewpager.setCurrentItem(1);
            }
        });

        mTv1.setOnClickListener(this);
        mTv2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_1:
                clickConsumeType(0);
                break;
            case R.id.tv_2:
                clickConsumeType(1);
                break;
            default:
                break;
        }
    }

    /**
     * 点击textView 场榜 七日榜 总榜
     *
     * @param position
     */
    private void clickConsumeType(int position) {

        if (viewpager != null) {
            viewpager.setCurrentItem(position);
        }
    }

    /**
     * 更新数据
     *
     * @param position
     */
    private void updateDataAndView(int position) {

        mView1.setVisibility(View.INVISIBLE);
        mView2.setVisibility(View.INVISIBLE);

        switch (position) {
            case 0:
                mView1.setVisibility(View.VISIBLE);
                break;
            case 1:
                mView2.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }

        //更新数据
        ((LiveConsumeListFragment) mFragmentList.get(position)).updateData();

        PLogger.e("========updateDataAndView=============");

    }

    class MyPagerFragmentAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragmentList;

        public MyPagerFragmentAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }
    }

}
