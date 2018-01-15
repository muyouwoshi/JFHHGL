package com.juxin.predestinate.ui.user.my.adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.config.base.Goods;
import com.juxin.predestinate.module.local.statistics.Source;
import com.juxin.predestinate.module.local.statistics.SourcePoint;
import com.juxin.predestinate.module.local.statistics.StatisticsUser;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.third.recyclerholder.BaseRecyclerViewAdapter;
import com.juxin.predestinate.third.recyclerholder.BaseRecyclerViewHolder;


/**
 * 钻石列表
 * Created by zm on 2017/4/13.
 */
public class MyDiamondAdapter extends BaseRecyclerViewAdapter<Goods> {

    private Context mContext;

    public MyDiamondAdapter(Context mContext) {
        super(mContext, null);
        this.mContext = mContext;
    }

    @Override
    public int[] getItemLayouts() {
        return new int[]{R.layout.f1_wode_my_diamond_item};//钻石item布局
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindRecycleViewHolder(BaseRecyclerViewHolder viewHolder, int position) {

        MyViewHolder vh = new MyViewHolder(viewHolder);
        final Goods info = getItem(position);
        vh.tv_num.setText(info.getNum() + mContext.getString(R.string.diamond));//产品
        vh.tv_price.setText("￥" + info.getCost() + "");//价格
        vh.tv_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//跳转支付列表
                StatisticsUser.centerGemPay(info.getNum());
                UIShow.showPayListAct((FragmentActivity) mContext, info.getPid());
            }
        });
    }

    @Override
    public int getRecycleViewItemType(int position) {
        return 0;
    }

    class MyViewHolder {

        ImageView img_diamond;
        TextView tv_num;
        TextView tv_price;

        public MyViewHolder(BaseRecyclerViewHolder convertView) {
            initView(convertView);
        }

        private void initView(BaseRecyclerViewHolder convertView) {
            img_diamond = convertView.findViewById(R.id.wode_my_diamond_item_img_diamond);
            tv_num = convertView.findViewById(R.id.wode_my_diamond_item_tv_num);
            tv_price = convertView.findViewById(R.id.wode_my_diamond_item_tv_price);
        }
    }
}