package com.juxin.predestinate.module.local.msgview.chatview.input;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.juxin.library.image.ImageLoader;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.msgview.smile.SmileItem;
import com.juxin.predestinate.module.logic.baseui.ExBaseAdapter;
import com.juxin.predestinate.module.util.UIUtil;

import java.util.List;

public class ChatCustomSmileAdapter extends ExBaseAdapter<SmileItem> {
    private boolean mOutDelClick;
    private int mCurPage;
    private int mDefWH;
    private DelCEmojiCallBack mCallBack;

    public interface DelCEmojiCallBack {
        void delCEmoji(String url, int curPage, int positon);
    }

    public ChatCustomSmileAdapter(Context context, List<SmileItem> datas, int curPage, boolean delFlag, DelCEmojiCallBack callBack) {
        super(context, datas);
        this.mOutDelClick = delFlag;
        this.mCurPage = curPage;
        this.mCallBack = callBack;
        this.mDefWH = UIUtil.dip2px(context, 60f);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = inflate(R.layout.p1_chat_custom_smile_grid_item);
            vh.chat_custom_smile = (ImageView) convertView.findViewById(R.id.chat_custom_smile);
            vh.iv_custom_emoji_del = (ImageView) convertView.findViewById(R.id.iv_custom_emoji_del);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        final SmileItem smileItem = getItem(position);
        final String picUrl = smileItem.getPic();
        if("custom".equals(smileItem.getPic())){
            vh.chat_custom_smile.setBackgroundResource(R.drawable.f1_bt_add_emoji);
        }else {
            ImageLoader.loadAsBmpFitCenter(getContext(), smileItem.getPic(), vh.chat_custom_smile);
            if(mOutDelClick) {
                vh.iv_custom_emoji_del.setVisibility(View.VISIBLE);
                vh.iv_custom_emoji_del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(picUrl)) {
                            mCallBack.delCEmoji(picUrl, mCurPage, position);
                        }
                    }
                });
            }else {
                vh.iv_custom_emoji_del.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    private class ViewHolder {
        ImageView chat_custom_smile;
        ImageView iv_custom_emoji_del;
    }
}