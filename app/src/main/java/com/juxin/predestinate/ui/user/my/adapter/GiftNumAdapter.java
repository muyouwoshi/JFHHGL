package com.juxin.predestinate.ui.user.my.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.my.GiftsNumInfo;
import com.juxin.predestinate.third.recyclerholder.BaseRecyclerViewAdapter;
import com.juxin.predestinate.third.recyclerholder.BaseRecyclerViewHolder;
import com.juxin.predestinate.ui.user.my.view.GiftPopView;

import java.util.List;


/**
 * 礼物列表
 * Created by zm on 2017/4/13.
 */
public class GiftNumAdapter extends BaseRecyclerViewAdapter<GiftsNumInfo> {

    private int width;
    private int height;
    private GiftPopView.OnNumSelectedChangedListener mListener;

    public GiftNumAdapter(Context context, List<GiftsNumInfo> giftsNumInfos, int width, int height){
        super(context, giftsNumInfos);
        this.width = width;
        this.height = height;
    }
    @Override
    public int[] getItemLayouts() {
        return new int[]{R.layout.f1_bottom_gif_num_pop_item};
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindRecycleViewHolder(BaseRecyclerViewHolder viewHolder, int position) {
        MyViewHolder vh = new MyViewHolder(viewHolder);
        final GiftsNumInfo info = getItem(position);
        vh.mLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height));
        if (position%2==1){
            vh.mLinearLayout.setBackgroundResource(R.color.color_F8F8F8);
        }else {
            vh.mLinearLayout.setBackgroundResource(R.color.transparent);
        }
        vh.txvNum.setText(info.getNum()+"");
        vh.txvMoral.setText(info.getMoral() + "");
        this.setOnItemClickListener(new BaseRecyclerViewHolder.OnItemClickListener() {
            @Override
            public void onItemClick(View convertView, int position) {
                if (mListener != null)
                    mListener.onNumSelectedChanged(getItem(position).getNum());
//                Log.e("TTTTTTTTTGG",mOnSelectNumChangedListener+"|||");
            }
        });
    }

    @Override
    public int getRecycleViewItemType(int position) {
        return 0;
    }

    public void setOnNumSelectedChanged(GiftPopView.OnNumSelectedChangedListener mListener){
        this.mListener = mListener;
    }

    class MyViewHolder {

        TextView txvNum, txvMoral;
        LinearLayout mLinearLayout;

        public MyViewHolder(BaseRecyclerViewHolder convertView) {
            initView(convertView);
        }

        private void initView(BaseRecyclerViewHolder convertView) {
            txvNum =  convertView.findViewById(R.id.bottom_gif_num_pop_item_num);
            txvMoral =  convertView.findViewById(R.id.bottom_gif_num_pop_item_moral);
            mLinearLayout = convertView.findViewById(R.id.bottom_gif_num_pop_item_ll);
        }
    }

}