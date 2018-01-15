package com.juxin.predestinate.ui.user.my.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.my.GiftsList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Think on 2016/12/22.
 */

public class GiftGridviewSmallAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context mContext;
    private List<GiftsList.GiftInfo> mLists;//礼物列表
    private static final int pSize = 8;//一页的礼物数量

    public GiftGridviewSmallAdapter(Context fContext, List<GiftsList.GiftInfo> lGift, int page) {
        this.mContext = fContext;
        this.inflater = LayoutInflater.from(fContext);
        mLists = new ArrayList<>();
        int start = page * pSize;
        int end = start + pSize;
        while ((start < lGift.size()) && (start < end)) {
            mLists.add(lGift.get(start));
            start++;
        }
    }

    @Override
    public int getCount() {
        return mLists.size();
    }

    @Override
    public Object getItem(int position) {
        return mLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (null == convertView) {
            vh = new ViewHolder();
            convertView = inflater.inflate(R.layout.f1_act_message_gift_small_items, null);
            vh.llItem = (LinearLayout) convertView.findViewById(R.id.ll_gift_item);
            vh.tvName = (TextView) convertView.findViewById(R.id.tv_gift_name);
            vh.tvMoney = (TextView) convertView.findViewById(R.id.tv_gift_money);
            vh.ivPic = (ImageView) convertView.findViewById(R.id.iv_gift_pic);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        if (null != vh) {
            GiftsList.GiftInfo item = mLists.get(position);
            ImageLoader.loadCenterCrop(mContext, item.getPic(), vh.ivPic);//设置礼物图像
            vh.tvName.setText(item.getName());//设置礼物名称
            vh.tvMoney.setText(item.getMoney() + "");//设置礼物所需的钻石
            if(item.isSelect()){//判断是否选中来设置item的背景
                vh.llItem.setBackgroundResource(R.drawable.f1_view_select_small);
            }else{
                vh.llItem.setBackgroundResource(R.drawable.f1_view_unselect_small);
            }
        }
        return convertView;
    }

    private static class ViewHolder {
        LinearLayout llItem;
        TextView tvName, tvMoney;
        ImageView ivPic;
    }
}
