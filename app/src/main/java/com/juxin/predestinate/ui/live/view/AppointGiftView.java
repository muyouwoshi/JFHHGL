package com.juxin.predestinate.ui.live.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.my.GiftsList;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.util.UIUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by terry on 2017/7/20.
 * 指定礼物id 的礼物面板
 */

public class AppointGiftView extends RecyclerView{

    private Context mContext;
    private int spanCount = 3;
    private List<GiftsList.GiftInfo> giftInfos = new ArrayList<>();

    private OnGiftItemClickListener onGiftItemClickListener;

    public AppointGiftView(Context context) {
        super(context);
        init(context);
    }

    public AppointGiftView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AppointGiftView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        this.mContext = context;
        GridLayoutManager layoutManager = new GridLayoutManager(context,spanCount);
        addItemDecoration(new SpaceItemDecoration(UIUtil.dp2px(12)));
        setLayoutManager(layoutManager);

        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    /**
     * 指定礼物id
     * @param ids
     */
    public void initGiftId(OnGiftItemClickListener listener,int...ids){

        this.onGiftItemClickListener = listener;

//        for(int id:ids){
//            GiftsList.GiftInfo info = ModuleMgr.getCommonMgr().getGiftLists().getGiftInfo(id);
//            if (info != null){
//                giftInfos.add(info);
//            }
//        }

        giftInfos = ModuleMgr.getCommonMgr().getCommonConfig().getGift().getNormalFriendsAddGifts();
        setAdapter(new AppointViewAdapter());
    }


    private class AppointViewAdapter extends Adapter<AppointViewAdapter.GiftHolder>{


        class GiftHolder extends ViewHolder {

            ImageView giftIv;
            TextView diamondsTv;
            TextView giftNameTv;
            LinearLayout giftContainerLl;
            public GiftHolder(View itemView) {
                super(itemView);

                giftIv = (ImageView) itemView.findViewById(R.id.appoint_gif_item_img);
                diamondsTv = (TextView) itemView.findViewById(R.id.appoint_gif_item_txvneedstone);
                giftNameTv = (TextView) itemView.findViewById(R.id.appoint_gif_item_txvgifname);
                giftContainerLl = (LinearLayout) itemView.findViewById(R.id.appoint_gif_item_ll_top);
            }

        }

        int lastSelectPosition =0;
        OnClickListener onClickListener = new OnClickListener(){
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag(R.string.live_gift_position);

                giftInfos.get(lastSelectPosition).setIsSelect(false);
                giftInfos.get(position).setIsSelect(!giftInfos.get(position).isSelect());
                notifyDataSetChanged();
                lastSelectPosition = position;
                if (onGiftItemClickListener!=null){
                    onGiftItemClickListener.onItemClick(giftInfos.get(position));
                }
            }
        };

        @Override
        public GiftHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.live_appoint_gift_item,parent,false);
            GiftHolder holder = new GiftHolder(view);

            view.setOnClickListener(onClickListener);
            return holder;
        }

        @Override
        public void onBindViewHolder(GiftHolder holder, int position) {

            holder.itemView.setTag(R.string.live_gift_position,position);

            ImageLoader.loadFitCenter(mContext,giftInfos.get(position).getPic(),holder.giftIv);
            holder.giftNameTv.setText(giftInfos.get(position).getName());
            holder.diamondsTv.setText(giftInfos.get(position).getMoney() + mContext.getString(R.string.diamond));

            if (!giftInfos.get(position).isSelect()){
                holder.giftContainerLl.setBackgroundResource(R.drawable.live_appoint_gift_item_normal);//设置为父控件的背景色（未选中）
            }else {
                holder.giftContainerLl.setBackgroundResource(R.drawable.live_appoint_gift_item_bg);//设置选中背景色
            }
        }

        @Override
        public int getItemCount() {
            return giftInfos.size();
        }
    }

    class SpaceItemDecoration extends ItemDecoration{
        private int space;
        public SpaceItemDecoration(int space){
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            super.getItemOffsets(outRect,view,parent,state);

            outRect.left = space/2;
            outRect.right = space/2;
        }
    }

    public interface OnGiftItemClickListener{
        void onItemClick(GiftsList.GiftInfo info);
    }
}
