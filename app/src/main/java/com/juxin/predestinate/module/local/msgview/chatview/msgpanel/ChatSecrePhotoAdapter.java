package com.juxin.predestinate.module.local.msgview.chatview.msgpanel;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.juxin.library.image.ImageLoader;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.baseui.ExBaseAdapter;

import java.util.List;

import static com.juxin.predestinate.module.logic.application.App.context;

/**
 * Created by zm on 2017/6/29.
 */
public class ChatSecrePhotoAdapter extends ExBaseAdapter<String> {

    public ChatSecrePhotoAdapter(Context fContext, List<String> photoList) {
        super(fContext, photoList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder vh;
        if (null == convertView) {
            convertView = inflate(R.layout.f1_chat_item_panel_secre_photo_item);
            vh = new MyViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (MyViewHolder) convertView.getTag();
        }

        String strPhoto = getItem(position);
        if (TextUtils.isEmpty(strPhoto)) {
            vh.img.setVisibility(View.INVISIBLE);
            return convertView;
        }
        vh.img.setVisibility(View.VISIBLE);

        ImageLoader.loadRoundFitCenter(context, strPhoto, vh.img);
        return convertView;
    }

    class MyViewHolder {

        ImageView img;  //相册

        public MyViewHolder(View convertView) {
            initView(convertView);
        }

        private void initView(View convertView) {
            img = (ImageView) convertView.findViewById(R.id.chat_panel_secre_photo_item_img);
        }
    }
}
