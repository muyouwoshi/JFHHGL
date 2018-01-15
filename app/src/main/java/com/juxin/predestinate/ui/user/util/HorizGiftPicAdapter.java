package com.juxin.predestinate.ui.user.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.juxin.library.image.ImageLoader;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.baseui.ExBaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建日期：2017/6/27
 * 描述:索要图片礼物弹框adapter
 * 作者:lc
 */
public class HorizGiftPicAdapter extends ExBaseAdapter {

    private List<String> list;
    private final String ADD_BTN_FLAG = "addBtn";

    private OptCallBack optCallBack;

    public interface OptCallBack {
        void delCallBack(int position);
    }

    public HorizGiftPicAdapter(Context context, ArrayList<String> list, OptCallBack optCallBack) {
        super(context, list);
        this.list = list;
        this.optCallBack = optCallBack;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflate(R.layout.p1_horiz_gift_pic_adapter);
            viewHolder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);
            viewHolder.iv_pic_del = (ImageView) convertView.findViewById(R.id.iv_pic_del);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (position != (getCount()-1)) {
            viewHolder.iv_pic_del.setVisibility(View.VISIBLE);
            ImageLoader.loadRoundAvatar(getContext(), getItem(position), viewHolder.iv_pic);
            viewHolder.iv_pic_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    optCallBack.delCallBack(position);
                }
            });
        } else {
            if (getList().contains(ADD_BTN_FLAG)) {// 未满 6 张
                viewHolder.iv_pic_del.setVisibility(View.GONE);
                ImageLoader.loadRoundAvatar(getContext(), R.drawable.f1_bt_add_gift, viewHolder.iv_pic);
            } else {
                viewHolder.iv_pic_del.setVisibility(View.VISIBLE);
                ImageLoader.loadRoundAvatar(getContext(), getItem(position), viewHolder.iv_pic);
                viewHolder.iv_pic_del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        optCallBack.delCallBack(position);
                    }
                });
            }
        }
        return convertView;
    }

    private class ViewHolder {
        ImageView iv_pic;
        ImageView iv_pic_del;
    }
}
