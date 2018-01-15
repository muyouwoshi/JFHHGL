package com.juxin.predestinate.ui.user.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserVideo;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.baseui.ExBaseAdapter;

import java.util.List;

/**
 * 视频列表适配器
 * Created by Su on 2017/5/26.
 */
public class VideoHoriAdapter extends ExBaseAdapter<UserVideo> {

    private int params;

    public VideoHoriAdapter(Context context, int params, List<UserVideo> datas) {
        super(context, datas);
        this.params = params;
    }

    public void setParams(int params) {
        this.params = params;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;
        if (convertView == null) {
            mHolder = new ViewHolder();
            convertView = inflate(R.layout.f1_user_check_video_adapter);
            mHolder.img_media = (ImageView) convertView.findViewById(R.id.img_media);
            mHolder.img_shade = (ImageView) convertView.findViewById(R.id.img_shade);
            mHolder.tv_num = (TextView) convertView.findViewById(R.id.tv_num);
            mHolder.img_media.setLayoutParams(new RelativeLayout.LayoutParams(params, params));
            mHolder.img_shade.setLayoutParams(new RelativeLayout.LayoutParams(params, params));
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        UserVideo userVideo = getItem(position);
        String url = ImageLoader.checkOssImageUrl(userVideo.getPic());
        if (userVideo.isCanView()) {
            ImageLoader.loadCenterCrop(getContext(), url, mHolder.img_media);
        } else {
            ImageLoader.loadBlur(getContext(), url, mHolder.img_media);
        }
        mHolder.img_shade.setVisibility(View.VISIBLE);
        return convertView;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    private class ViewHolder {
        ImageView img_media, img_shade;
        TextView tv_num;
    }
}