package com.juxin.predestinate.ui.live.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.juxin.library.image.ImageLoader;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.config.base.NobilityList;
import com.juxin.predestinate.module.local.statistics.SourcePoint;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.ui.live.bean.EnterLiveBean;
import com.juxin.predestinate.ui.live.bean.LiveUserRankBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 直播间 观众列表adapter
 *
 * @author terry
 * @date 2017/9/13
 */
public class LiveUserRankAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private EnterLiveBean liveBean;
    private boolean isGirl;

    private int[] numberResource = {R.drawable.icon_guanjun, R.drawable.icon_yajun, R.drawable.icon_jijun};
    public List<LiveUserRankBean> dataList = new ArrayList<>();

    public LiveUserRankAdapter(Context context, EnterLiveBean liveBean, boolean isGirl) {
        this.mContext = context;
        this.liveBean = liveBean;
        this.isGirl = isGirl;
    }

    /**
     * 加载观众列表
     *
     * @param dataList
     */
    public void addListData(ArrayList<LiveUserRankBean> dataList) {
        if (this.dataList ==null){
            dataList = new ArrayList<>();
        }
        this.dataList.addAll(dataList);
        HashSet<LiveUserRankBean> set = new HashSet(this.dataList); //手动去重
        this.dataList.clear();
        this.dataList.addAll(set);

    }

    /**
     * 清除观众列表
     */
    public void clear() {
        this.dataList.clear();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        ImageView numberIv;
        ImageView headerIv;
        ImageView rankIv;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.l1_room_rank, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.numberIv = (ImageView) view.findViewById(R.id.item_recycler_numb_iv);
        viewHolder.headerIv = (ImageView) view.findViewById(R.id.item_recycler_img);
        viewHolder.rankIv = (ImageView) view.findViewById(R.id.item_recycler_rank_iv);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag(R.string.live_gift_position);
                SourcePoint.getInstance().lockSource((Activity) mContext, "gift");
//                ((H5LivePlayAct) App.getActivity()).getUserDetail(String.valueOf(getItemId(position)),
//                        isGirl || (liveBean != null && liveBean.is_admin));
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        holder.itemView.setTag(R.string.live_gift_position, position);
        switch (position) {
            case 0:
            case 1:
            case 2:
                if (dataList.get(position).diamond > 0) {
                    ((ViewHolder) holder).numberIv.setImageResource(numberResource[position]);
                    ((ViewHolder) holder).numberIv.setVisibility(View.VISIBLE);
                } else {
                    ((ViewHolder) holder).numberIv.setVisibility(View.GONE);
                }
                break;
            default:
                ((ViewHolder) holder).numberIv.setVisibility(View.GONE);
                break;
        }
        ImageLoader.loadCircleAvatar(mContext, dataList.get(position).avatar, ((ViewHolder) holder).headerIv);

        NobilityList.Nobility tmpNobility = ModuleMgr.getCommonMgr().getCommonConfig().queryNobility(dataList.get(position).level, dataList.get(position).getGender());

        ImageLoader.loadAvatar(mContext, tmpNobility.getTitle_icon(), ((ViewHolder) holder).rankIv);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public long getItemId(int position) {
        return dataList.get(position).getUid();
    }
}
