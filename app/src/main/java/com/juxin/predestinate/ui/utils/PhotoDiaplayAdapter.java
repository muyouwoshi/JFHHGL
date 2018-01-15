package com.juxin.predestinate.ui.utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.juxin.predestinate.bean.center.user.detail.UserPhoto;
import com.juxin.predestinate.module.logic.baseui.custom.TouchImageView;

import java.util.List;

/**
 * PhotoDisplayAct: 大图展示页面
 */
public class PhotoDiaplayAdapter extends FragmentStatePagerAdapter {
    private List<UserPhoto> photoList;
    private List<String> imgList;
    private int mCount;

    private PhotoDisplayFragment[] fragments;

    private TouchImageView.OnClickEvent onClickEvent;

    public void setOnClickEvent(TouchImageView.OnClickEvent onClickEvent) {
        this.onClickEvent = onClickEvent;
    }

    public PhotoDiaplayAdapter(FragmentManager fm, List<UserPhoto> photoList, List<String> imgList) {
        super(fm);

        setIsPhoto(photoList, imgList);

        if (isPhoto()) {
            this.photoList = photoList;
            this.imgList = imgList;
            this.mCount = photoList.size();
        } else {
            this.photoList = photoList;
            this.imgList = imgList;
            this.mCount = imgList.size();
        }

        fragments = new PhotoDisplayFragment[mCount];
    }

    @Override
    public Fragment getItem(int position) {
        if (fragments[position] == null) {
            fragments[position] = new PhotoDisplayFragment();
            Bundle bundle = new Bundle();
            bundle.putString("pic", isPhoto() ? photoList.get(position).getPic() : imgList.get(position));
            fragments[position].setArguments(bundle);
            if (onClickEvent != null) {
                fragments[position].setOnClickEvent(onClickEvent);
            }
        }
        return fragments[position];
    }

    @Override
    public int getCount() {
        return mCount;
    }


    private boolean isPhoto = false;


    public boolean isPhoto() {
        return isPhoto;
    }

    private void setIsPhoto(List<UserPhoto> photoList, List<String> imgList) {
        if (photoList != null && imgList == null) {
            isPhoto = true;
        } else if (photoList == null && imgList != null) {
            isPhoto = false;
        } else {
            isPhoto = false;
        }
    }

}
