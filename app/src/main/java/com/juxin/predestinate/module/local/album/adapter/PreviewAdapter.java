package com.juxin.predestinate.module.local.album.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.album.activity.PickBigImagesActivity;
import com.juxin.predestinate.module.local.album.activity.ZoomImageView;
import com.juxin.predestinate.module.local.album.bean.SingleImageModel;
import com.juxin.predestinate.module.local.album.help.AlbumBitmapCacheHelper;

import java.util.List;

/**
 * 预览界面 adapter
 * <p>
 * Created by Su on 2016/12/6.
 */

public class PreviewAdapter extends PagerAdapter {

    private PickBigImagesActivity activity;
    private List<SingleImageModel> lstSelect;  // 选中的预览图片List

    private ViewPager viewPager;

    public PreviewAdapter(Context context,  ViewPager viewPager, List<SingleImageModel> lstSelect) {
        this.lstSelect = lstSelect;
        this.viewPager = viewPager;
        activity = (PickBigImagesActivity) context;
    }

    @Override
    public int getCount() {
        return lstSelect == null ? 0 : lstSelect.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(activity).inflate(R.layout.p1_album_preview_act, null);
        final ZoomImageView zoomImageView = (ZoomImageView) view.findViewById(R.id.zoom_image_view);

        if (lstSelect == null) {
            container.addView(view);
            Toast.makeText(activity, "预览出错，请返回重新选择", Toast.LENGTH_SHORT).show();
            return view;
        }
        AlbumBitmapCacheHelper.getInstance(activity).addPathToShowlist(lstSelect.get(position).path);
        zoomImageView.setTag(lstSelect.get(position).path);
        Bitmap bitmap = AlbumBitmapCacheHelper.getInstance(activity).getBitmap(lstSelect.get(position).path, 0, 0, new AlbumBitmapCacheHelper.ILoadImageCallback() {
            @Override
            public void onLoadImageCallBack(Bitmap bitmap, String path, Object... objects) {
                ZoomImageView view = ((ZoomImageView) viewPager.findViewWithTag(path));
                if (view != null && bitmap != null)
                    ((ZoomImageView) viewPager.findViewWithTag(path)).setSourceImageBitmap(bitmap, activity);
            }
        }, position);
        if (bitmap != null) {
            zoomImageView.setSourceImageBitmap(bitmap, activity);
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (lstSelect == null) return;
        View view = (View) object;
        container.removeView(view);
        AlbumBitmapCacheHelper.getInstance(activity).removePathFromShowlist(lstSelect.get(position).path);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
