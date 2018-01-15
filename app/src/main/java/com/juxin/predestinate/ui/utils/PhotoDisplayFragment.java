package com.juxin.predestinate.ui.utils;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.juxin.library.image.ImageLoader;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.baseui.BaseFragment;
import com.juxin.predestinate.module.logic.baseui.custom.TouchImageView;
import com.juxin.predestinate.module.util.TimerUtil;

/**
 * PhotoDisplayAct：查看大图Fragment页面
 */
public class PhotoDisplayFragment extends BaseFragment {

    private ProgressBar progress;
    private TouchImageView image;

    private String pic;
    private TouchImageView.OnClickEvent onClickEvent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.p1_photo_display_item, container, false);
        pic = getArguments().getString("pic");
        initView(view);
        return view;
    }

    private void initView(View v) {
        progress = (ProgressBar) v.findViewById(R.id.progress);
        image = (TouchImageView) v.findViewById(R.id.image_view);
        if (onClickEvent != null) {
            image.setOnClickEvent(onClickEvent);
        }
        loadPic(pic);
    }

    public void setOnClickEvent(TouchImageView.OnClickEvent onClickEvent) {
        this.onClickEvent = onClickEvent;
    }

    /**
     * 加载图片
     */
    public void loadPic(String pic) {
        ImageLoader.loadFitCenter(getContext(), pic, image);
        TimerUtil.beginTime(new TimerUtil.CallBack() {
            @Override
            public void call() {
                progress.setVisibility(View.GONE);
            }
        }, 100);
    }
}
