package com.juxin.predestinate.ui.user.check.self.album;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserPhoto;

import java.util.List;

/**
 * 我的相册
 */
public class AlbumAdapter extends BaseAdapter {
    public static final int PHOTO_STATUS_CHECKING = 0;
    public static final int PHOTO_STATUS_NORMAL = 1;
    public static final int PHOTO_STATUS_NOTPASS = 2;
    private final Context context;

    private Resources resources;
    private LayoutInflater inflater;
    private List<UserPhoto> userPhotoList;
    private int itemWidth;

    public AlbumAdapter(Context context, List<UserPhoto> userPhotoList, int itemWidth) {
        this.context = context;
        this.resources = context.getResources();
        this.inflater = LayoutInflater.from(context);
        this.userPhotoList = userPhotoList;
        this.itemWidth = itemWidth;
    }

    @Override
    public int getCount() {
        return userPhotoList.size();
    }

    @Override
    public Object getItem(int position) {
        return userPhotoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;
        if (convertView == null) {
            mHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.f1_user_info_album_item, null);
            mHolder.img_info_grid_item_pic = (ImageView) convertView.findViewById(R.id.img_info_grid_item_pic);
            mHolder.txt_info_grid_item_status = (TextView) convertView.findViewById(R.id.txt_info_grid_item_status);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(itemWidth, itemWidth);
            mHolder.img_info_grid_item_pic.setLayoutParams(lp);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        UserPhoto userPhoto = userPhotoList.get(position);

        String path = userPhoto.getPic();
        if (path != null && !"".equals(path)) {
            String url = ImageLoader.checkOssImageUrl(path);
            ImageLoader.loadCenterCrop(context, url, mHolder.img_info_grid_item_pic);
        } else {
            mHolder.img_info_grid_item_pic.setImageResource(R.drawable.f1_upload_photo_btn);
        }
        int state = userPhoto.getStatus();
        switch (state) {
            case PHOTO_STATUS_CHECKING:
                mHolder.txt_info_grid_item_status.setVisibility(View.VISIBLE);
                mHolder.txt_info_grid_item_status.setText("审核中");
                mHolder.txt_info_grid_item_status.setBackgroundColor(resources.getColor(R.color.color_6ba2fd));
                break;
            case PHOTO_STATUS_NORMAL:
                mHolder.txt_info_grid_item_status.setVisibility(View.GONE);
                break;
            case PHOTO_STATUS_NOTPASS:
                mHolder.txt_info_grid_item_status.setVisibility(View.VISIBLE);
                mHolder.txt_info_grid_item_status.setText("未通过");
                mHolder.txt_info_grid_item_status.setBackgroundColor(resources.getColor(R.color.color_ee3434));
                break;
        }
        return convertView;
    }

    private class ViewHolder {
        public ImageView img_info_grid_item_pic;
        public TextView txt_info_grid_item_status;
    }

}
