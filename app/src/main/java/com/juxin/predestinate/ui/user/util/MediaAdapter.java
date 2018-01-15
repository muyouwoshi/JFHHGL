package com.juxin.predestinate.ui.user.util;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserPhoto;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.third.recyclerholder.BaseRecyclerViewAdapter;
import com.juxin.predestinate.third.recyclerholder.BaseRecyclerViewHolder;

/**
 * RecyclerView媒体文件查看Adapter: 相册、视频
 * <p>
 * 有问题暂时弃用{水平列表item莫名自动match_parent}
 * Created by Su on 2017/5/10.
 */

public class MediaAdapter extends BaseRecyclerViewAdapter {
    private int showType = AlbumHorizontalPanel.EX_HORIZONTAL_ALBUM;
    private int params;

    // data
    private String url;

    public MediaAdapter(Context context, int showType, int params) {
        super(context, null);
        this.showType = showType;
        this.params = params;
    }

    @Override
    public int[] getItemLayouts() {
        return new int[]{R.layout.p1_user_checkinfo_secret_adapter};
    }

    @Override
    public void onBindRecycleViewHolder(BaseRecyclerViewHolder viewHolder, int position) {
        ImageView img_media = viewHolder.findViewById(R.id.img_media);
        ImageView img_shade = viewHolder.findViewById(R.id.img_shade);
        TextView tv_num = viewHolder.findViewById(R.id.tv_num);
        img_media.setLayoutParams(new RelativeLayout.LayoutParams(params, params));
        img_shade.setLayoutParams(new RelativeLayout.LayoutParams(params, params));


        // 展示相册
        if (showType == AlbumHorizontalPanel.EX_HORIZONTAL_ALBUM) {
            UserPhoto userPhoto = (UserPhoto) getItem(position);
            url = userPhoto.getPic();
        }

        ImageLoader.loadCenterCrop(img_media.getContext(), url, img_media);
        if (showType == AlbumHorizontalPanel.EX_HORIZONTAL_VIDEO)
            img_shade.setImageResource(R.drawable.p1_user_check_video_lock);
    }

    @Override
    public int getRecycleViewItemType(int position) {
        return 0;
    }
}
