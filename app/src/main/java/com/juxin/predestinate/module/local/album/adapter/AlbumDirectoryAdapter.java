package com.juxin.predestinate.module.local.album.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.album.bean.AlbumDirectories;
import com.juxin.predestinate.module.local.album.help.AlbumBitmapCacheHelper;
import com.juxin.predestinate.module.logic.baseui.ExBaseAdapter;
import com.juxin.predestinate.module.util.UIUtil;

import java.io.File;
import java.util.List;

/**
 * 相册目录列表Adapter
 * <p/>
 * Created by Su on 2016/7/11.
 */
public class AlbumDirectoryAdapter extends ExBaseAdapter<AlbumDirectories> implements AlbumBitmapCacheHelper.ILoadImageCallback {
    private ListView listView;
    private int currentShowPosition;   // 当前显示的文件夹

    public AlbumDirectoryAdapter(Context context, ListView listView, List<AlbumDirectories> datas) {
        super(context, datas);
        this.listView = listView;
    }

    public void setData(int currentShowPosition) {
        this.currentShowPosition = currentShowPosition;
    }

    @Override
    public int getCount() {
        return super.getCount() + 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (isEmpty()) return null;

        ViewHolder holder;
        if (convertView == null) {
            convertView = inflate(R.layout.p1_album_item_directory);
            holder = new ViewHolder();
            holder.iv_directory_pic = (ImageView) convertView.findViewById(R.id.iv_directory_pic);
            holder.tv_directory_name = (TextView) convertView.findViewById(R.id.tv_directory_name);
            holder.iv_directory_check = (ImageView) convertView.findViewById(R.id.iv_directory_check);
            holder.tv_directory_nums = (TextView) convertView.findViewById(R.id.tv_directory_nums);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String path = getItem(position - 1 < 0 ? 0 : position - 1).images.getImagePath(0) + "";  //获取当前目录第一张图片路径
        if (holder.iv_directory_pic.getTag() != null) {
            AlbumBitmapCacheHelper.getInstance((Activity) getContext()).removePathFromShowlist((String) (holder.iv_directory_pic.getTag()));
        }

        //全部图片
        if (isAllDir(position)) {
            int size = 0; // 全部图片个数
            for (AlbumDirectories directories : getList())
                size += directories.images.getImageCounts();
            holder.iv_directory_pic.setTag(path + "all");
            holder.tv_directory_nums.setText(size + "张");
            holder.tv_directory_name.setText(getContext().getString(R.string.album_all));
        } else { // 单一目录图片
            holder.iv_directory_pic.setTag(path);
            holder.tv_directory_nums.setText(getItem(position - 1).images.getImageCounts() + "张");
            holder.tv_directory_name.setText(new File(getItem(position - 1).directoryPath).getName());
        }
        showArrowStates(currentShowPosition == position - 1, holder);
        holder.tv_directory_name.setTag(position);

        AlbumBitmapCacheHelper.getInstance((Activity) getContext()).addPathToShowlist(path);
        Bitmap bitmap = AlbumBitmapCacheHelper.getInstance((Activity) getContext()).getBitmap(path, 225, 225, this, isAllDir(position));
        if (bitmap != null) {
            BitmapDrawable bd = new BitmapDrawable(getResources(), bitmap);
            UIUtil.setBackground(holder.iv_directory_pic, bd);
        }
        return convertView;
    }

    // 设置 item 展示图片
    @Override
    public void onLoadImageCallBack(Bitmap bitmap, String path, Object... objects) {
        if (bitmap == null) return;
        BitmapDrawable bd = new BitmapDrawable(getResources(), bitmap);
        View v = null;
        if ((boolean) objects[0]) {
            v = listView.findViewWithTag(path + "all");
        } else {
            v = listView.findViewWithTag(path);
        }
        if (v != null) UIUtil.setBackground(v, bd);
    }

    private class ViewHolder {
        public ImageView iv_directory_pic;
        public TextView tv_directory_name;
        public ImageView iv_directory_check;
        public TextView tv_directory_nums;
    }

    // -------------------------------内部调用方法----------------------------------------

    /**
     * 当前选中文件夹状态
     */
    private void showArrowStates(boolean judge, ViewHolder holder) {
        if (judge) {
            holder.iv_directory_check.setTag("picked");
            holder.iv_directory_check.setVisibility(View.VISIBLE);
        } else {
            holder.iv_directory_check.setTag(null);
            holder.iv_directory_check.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 当前选中item是否为： 全部图片
     *
     * @return true： 全部图片
     */
    public boolean isAllDir(int position) {
        return position == 0 ? true : false;
    }


}
