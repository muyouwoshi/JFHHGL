package com.juxin.predestinate.module.local.album.adapter;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.juxin.library.log.PToast;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.album.activity.PickOrTakeImageActivity;
import com.juxin.predestinate.module.local.album.bean.SingleImageModel;
import com.juxin.predestinate.module.local.album.help.AlbumBitmapCacheHelper;
import com.juxin.predestinate.module.local.album.help.AlbumHelper;
import com.juxin.predestinate.module.local.album.ImgSelectUtil;
import com.juxin.predestinate.module.logic.baseui.ExBaseAdapter;
import com.juxin.predestinate.module.util.UIUtil;
import com.juxin.predestinate.ui.utils.NoDoubleClickListener;

import java.util.List;

/**
 * 相册展示Adapter
 * <p>
 * Created by Su on 2016/7/14.
 */
public class AlbumExhibitionAdapter extends ExBaseAdapter<SingleImageModel> implements AlbumBitmapCacheHelper.ILoadImageCallback {
    private PickOrTakeImageActivity activity;
    private GridView gridView;
    private int count; // 图片总数量
    private int currentShowPosition;
    private int perWidth;   // 每张图片需要显示的高度和宽度
    private int picNums = 4; // 选择图片的数量总数，默认为4

    public AlbumExhibitionAdapter(Context context, List datas) {
        super(context, datas);
        activity = (PickOrTakeImageActivity) context;

        //计算每张图片应该显示的宽度
        perWidth = (((WindowManager) (context.getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay().getWidth() - UIUtil.dip2px(context, 4)) / 3;
    }

    /**
     * 设置变量
     */
    public void setData(int count, int currentShowPosition) {
        this.count = count;
        this.currentShowPosition = currentShowPosition;
    }

    /**
     * 设置常量
     */
    public void setConstant(int picNums, GridView gridView) {
        this.gridView = gridView;
        this.picNums = picNums;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //第一个要显示拍摄照片图片
        if (currentShowPosition == -1 && position == 0) {
            convertView = inflate(R.layout.p1_album_takephoto);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (PermissionsUtil.hasPermission(activity, Manifest.permission.CAMERA)) {
                        ImgSelectUtil.getInstance().takePhoto(activity, false);
                    } else {
                        PermissionsUtil.requestPermission(activity, new PermissionListener() {
                            @Override
                            public void permissionGranted(@NonNull String[] permissions) {
                                ImgSelectUtil.getInstance().takePhoto(activity, false);
                            }

                            @Override
                            public void permissionDenied(@NonNull String[] permissions) {
                                PToast.showShort("请打开存储权限");
                            }
                        }, new String[]{Manifest.permission.CAMERA});
                    }
                }
            });
            convertView.setLayoutParams(new GridView.LayoutParams(perWidth, perWidth));
            return convertView;
        }
        //在此处直接进行处理最好，能够省去其他部分的处理，其他部分直接可以使用原来的数据结构
        if (currentShowPosition == -1)
            position--;
        //其他部分的处理
        final String path = AlbumHelper.getInstance().getImgPath(position, currentShowPosition);
        if (convertView == null || convertView.getTag() == null) {
            convertView = inflate(R.layout.p1_album_item);
            GridViewHolder holder = new GridViewHolder();
            holder.iv_content = (ImageView) convertView.findViewById(R.id.iv_content);
            holder.v_gray_masking = convertView.findViewById(R.id.v_gray_masking);
            holder.iv_pick_or_not = (ImageView) convertView.findViewById(R.id.iv_pick_or_not);
            if (picNums == 1) {
                holder.iv_pick_or_not.setVisibility(View.GONE);
            }

            OnclickListenerWithHolder listener = new OnclickListenerWithHolder(holder);
            holder.iv_content.setOnClickListener(listener);
            holder.iv_pick_or_not.setOnClickListener(listener);
            convertView.setTag(holder);
            //要在这进行设置，在外面设置会导致第一个项点击效果异常
            convertView.setLayoutParams(new GridView.LayoutParams(perWidth, perWidth));
        }
        final GridViewHolder holder = (GridViewHolder) convertView.getTag();
        //一定不要忘记更新position
        holder.position = position;
        //如果该图片被选中，则讲状态变为选中状态
        boolean isPicked = AlbumHelper.getInstance().getSelectState(position, currentShowPosition);
        if (isPicked) {
            holder.v_gray_masking.setVisibility(View.VISIBLE);
            holder.iv_pick_or_not.setImageResource(R.drawable.p1_album_chosed);
        } else {
            holder.v_gray_masking.setVisibility(View.GONE);
            holder.iv_pick_or_not.setImageResource(R.drawable.p1_album_chose);
        }

        //优化显示效果
        if (holder.iv_content.getTag() != null) {
            String remove = (String) holder.iv_content.getTag();
            AlbumBitmapCacheHelper.getInstance(activity).removePathFromShowlist(remove);
        }
        AlbumBitmapCacheHelper.getInstance(activity).addPathToShowlist(path);
        holder.iv_content.setTag(path);
        Bitmap bitmap = AlbumBitmapCacheHelper.getInstance(activity).getBitmap(path, perWidth, perWidth, this, position);
        if (bitmap != null) {
            BitmapDrawable bd = new BitmapDrawable(getResources(), bitmap);
            UIUtil.setBackground(holder.iv_content, bd);
        }
        return convertView;
    }

    @Override
    public void onLoadImageCallBack(Bitmap bitmap, String path, Object... objects) {
        if (bitmap == null) return;
        BitmapDrawable bd = new BitmapDrawable(getResources(), bitmap);
        View v = gridView.findViewWithTag(path);
        if (v != null) UIUtil.setBackground(v, bd);
    }

    public class GridViewHolder {
        public ImageView iv_content;
        public View v_gray_masking;
        public ImageView iv_pick_or_not;
        public int position;
    }

    /**
     * 带holder的监听器
     */
    private class OnclickListenerWithHolder extends NoDoubleClickListener {
        GridViewHolder holder;

        public OnclickListenerWithHolder(GridViewHolder holder) {
            this.holder = holder;
        }

        @Override
        public void onNoDoubleClick(View v) {
            activity.refresh(v, holder);
        }
    }
}
