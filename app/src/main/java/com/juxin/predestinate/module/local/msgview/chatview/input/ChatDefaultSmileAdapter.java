package com.juxin.predestinate.module.local.msgview.chatview.input;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.juxin.library.log.PLogger;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.baseui.ExBaseAdapter;

import java.io.IOException;
import java.util.List;

/**
 * 根据表情的文件名，将表情图片显示到表情面板。
 *
 */
public class ChatDefaultSmileAdapter extends ExBaseAdapter<EmojiPack.EmojiItem> {
    public ChatDefaultSmileAdapter(Context context, List<EmojiPack.EmojiItem> datas) {
        super(context, datas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh ;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = inflate(R.layout.p1_chat_smile_grid_item);

            vh.smile = (ImageView) convertView.findViewById(R.id.chat_smile);

            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        EmojiPack.EmojiItem smileItem = getItem(position);

        vh.smile.setTag(smileItem.key);

        try {
            vh.smile.setImageBitmap(BitmapFactory.decodeStream(getContext().getAssets().open(smileItem.name)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private class ViewHolder {
        public ImageView smile;
    }
}
