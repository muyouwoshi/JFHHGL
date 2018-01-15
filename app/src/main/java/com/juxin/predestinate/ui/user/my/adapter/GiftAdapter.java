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
 * 礼物列表
 * Created by zm on 2017/4/13.
 */
public class GiftAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context mContext;
    private List<GiftsList.GiftInfo> mLists;//礼物列表
    private int pSize = 6;//每页的item个数

    public GiftAdapter(Context fContext, List<GiftsList.GiftInfo> lGift, int page, int pSize) {
        this.pSize = pSize;
        this.mContext = fContext;
        this.inflater = LayoutInflater.from(fContext);
        mLists = new ArrayList<>();
        //计算本页礼物列表
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
        MyViewHolder vh;
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.p1_bottom_gif_item, null);
            vh = new MyViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (MyViewHolder) convertView.getTag();
        }

        if (mLists != null && mLists.size() > 0) {
            final GiftsList.GiftInfo info = mLists.get(position);
            vh.txvGifName.setText(info.getName());
            //        vh.txvIntimate.setText("亲密度+"+info.getIntimacy());
            if (info.getGiftfrom() == 1) {
                vh.txvNeedStone.setText(mContext.getString(R.string.have, info.getCount()));
                if (info.getType() == 1){
                    vh.img.setImageResource(R.drawable.redbog_gift_hb);
                }else {
                    ImageLoader.loadFitCenter(mContext, info.getPic(), vh.img);
                }

            } else {
                vh.txvNeedStone.setText(info.getMoney() + mContext.getString(R.string.diamond));
                ImageLoader.loadFitCenter(mContext, info.getPic(), vh.img);
            }
            if (!info.isSelect()) {
                vh.llTop.setBackgroundResource(R.color.white);//设置为父控件的背景色（未选中）
            } else {
                vh.llTop.setBackgroundResource(R.drawable.gift_item_bg);//设置选中背景色
            }
        }
        return convertView;
    }

    class MyViewHolder {

        ImageView img;//礼物图像
        TextView txvGifName, txvNeedStone;//礼物名称，需要的钻石
        LinearLayout llTop;//用于控制选中或未选中的背景


        public MyViewHolder(View convertView) {
            initView(convertView);
        }

        private void initView(View convertView) {
            img = (ImageView) convertView.findViewById(R.id.bottom_gif_item_img);
            txvGifName = (TextView) convertView.findViewById(R.id.bottom_gif_item_txvgifname);
            txvNeedStone = (TextView) convertView.findViewById(R.id.bottom_gif_item_txvneedstone);
            llTop = (LinearLayout) convertView.findViewById(R.id.bottom_gif_item_ll_top);
        }
    }

}