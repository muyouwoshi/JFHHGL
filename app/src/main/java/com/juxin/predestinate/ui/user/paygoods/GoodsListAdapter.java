package com.juxin.predestinate.ui.user.paygoods;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juxin.library.view.CustomFrameLayout;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.config.base.Goods;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.third.recyclerholder.BaseRecyclerViewAdapter;
import com.juxin.predestinate.third.recyclerholder.BaseRecyclerViewHolder;

/**
 * 通用商品列表adapter
 * Created by Su on 2017/3/31.
 */
public class GoodsListAdapter extends BaseRecyclerViewAdapter {

    private int selectPosition = 0;
    private int itemType = 0;       // 展示布局
    private int chargeType = 0;     // 充值种类
    private boolean isB;

    public GoodsListAdapter(Context context) {
        super(context, null);
    }

    public GoodsListAdapter(Context context, int itemType) {
        super(context, null);
        isB = ModuleMgr.getCenterMgr().getMyInfo().isB();
        this.chargeType = itemType;

        if (itemType == GoodsConstant.DLG_VIP_VIDEO_NEW) {
            this.itemType = 2;
        } else if (itemType == GoodsConstant.DLG_DIAMOND_VIDEO) {
            this.itemType = 3;
        } else if (itemType == GoodsConstant.DLG_DIAMOND_YULIAO) {
            this.itemType = 4;
        } else {
            if (itemType > 0) this.itemType = 1;// 非0状态下都引用第二种布局
            if (itemType == GoodsConstant.DLG_DIAMOND_CHAT) {  // 聊天充值
                this.itemType = 0;
            }
        }
    }

    @Override
    public int[] getItemLayouts() {
        return new int[]{R.layout.p1_goods_list_adapter, R.layout.f1_goods_list_item,
                R.layout.f1_goods_video_item, R.layout.f1_goods_item_new, R.layout.f1_goods_item_yuliao};
    }

    @Override
    public void onBindRecycleViewHolder(BaseRecyclerViewHolder viewHolder, final int position) {
        final Goods data = (Goods) getItem(position);

        RelativeLayout payItem = viewHolder.findViewById(R.id.pay_item);
        CustomFrameLayout payBg = viewHolder.findViewById(R.id.pay_bg);
        ImageView img_choose = viewHolder.findViewById(R.id.iv_choose);
        ImageView goods_ico = viewHolder.findViewById(R.id.goods_ico);
        TextView tv_name = viewHolder.findViewById(R.id.tv_name);       // 商品名称
        TextView tv_money = viewHolder.findViewById(R.id.tv_money);     // 商品价格
        TextView tv_desc = viewHolder.findViewById(R.id.tv_desc);       // 商品活动

        // 选中状态
        payItem.setSelected(selectPosition == position);
        payBg.showOfIndex(selectPosition == position ? 1 : 0);

        // 设置数据
        // itemType等于1或2的时候为y币和vip样式，其他为钻石样式
        tv_name.setVisibility(View.VISIBLE);
        if(itemType == 4){
            tv_name.setText(data.getNum()+"钻石");
        }else{
            tv_name.setText(itemType == 1 || itemType == 2 ? data.getName() : String.valueOf(data.getNum()));
        }

        if (itemType == 1 || itemType == 2 || itemType == 4) {
            tv_money.setText(Html.fromHtml("&#165;").toString() + data.getCost());
        } else {
            tv_money.setText(data.getCost() + "元");
        }
        if (isB) {
            tv_desc.setText(data.getDesc());
        } else {
            tv_desc.setText("");
        }

        // 充值类型区分
        switch (chargeType) {
            case GoodsConstant.DLG_YCOIN_NEW:       // 新Y币充值弹框
                if (ModuleMgr.getCenterMgr().getMyInfo().isVip())
                    tv_desc.setVisibility(View.GONE);
                break;

            case GoodsConstant.DLG_DIAMOND_CHAT:    // 聊天钻石充值
                goods_ico.setVisibility(View.GONE);
                break;

            default:
                break;
        }
    }

    public void updateData(int positon) {
        this.selectPosition = positon;
        notifyDataSetChanged();
    }

    @Override
    public int getRecycleViewItemType(int position) {
        return itemType;
    }
}
