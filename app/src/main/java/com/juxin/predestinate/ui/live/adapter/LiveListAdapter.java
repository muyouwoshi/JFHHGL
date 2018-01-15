package com.juxin.predestinate.ui.live.adapter;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.log.PToast;
import com.juxin.predestinate.R;
import com.juxin.predestinate.ui.live.bean.LiveRoomBean;
import com.juxin.predestinate.ui.live.view.LiveListPanel;

import java.util.ArrayList;

/**
 * @author gwz
 */

public class LiveListAdapter extends BaseAdapter {
    private static final int TYPE_SMALL = 0;
    private static final int TYPE_BIG = 1;
    private ArrayList<LiveRoomBean> liveList = new ArrayList<>();
    private int tagId;
    private boolean isSmall = true;

    public void addData(ArrayList<LiveRoomBean> data) {
        liveList.addAll(data);
    }

    public void setData(ArrayList<LiveRoomBean> data) {
        liveList.clear();
        addData(data);
    }

    public void switchBigSmall(boolean isSmall) {
        this.isSmall = isSmall;
        notifyDataSetChanged();
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    @Override
    public int getCount() {
        return isSmall ? (liveList.size() / 2 + liveList.size() % 2) : liveList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup viewGroup) {
        ViewHolder vh;
        int viewType = getItemViewType(pos);
        if (convertView == null) {
            vh = new ViewHolder();
            if (viewType == TYPE_SMALL) {
                convertView = View.inflate(viewGroup.getContext(), R.layout.live_home_page_small_listview_item, null);
                vh.initSmallView(convertView);
            } else {
                convertView = View.inflate(viewGroup.getContext(), R.layout.live_home_page_big_listview_item, null);
                vh.initBigView(convertView);
            }
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        if (isSmall) {
            vh.updateData(liveList.get(pos * 2), (pos * 2 + 1) == liveList.size() ? null : liveList.get(pos * 2 + 1));
        } else {
            vh.updateBigData(liveList.get(pos));
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        if (isSmall) {
            return TYPE_SMALL;
        } else {
            return TYPE_BIG;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public final class ViewHolder {
        ViewItem item1;
        ViewItem item2;
        ViewItem itemBig;


        public void initSmallView(View convertView) {
            item1 = new ViewItem(convertView.findViewById(R.id.item_1));
            item2 = new ViewItem(convertView.findViewById(R.id.item_2));
            if (tagId == LiveListPanel.TAG_HOT) {
                convertView.setBackgroundColor(Color.WHITE);
            }
        }

        public void initBigView(View convertView) {
            itemBig = new ViewItem(convertView);
        }

        public void updateData(LiveRoomBean bean1, LiveRoomBean bean2) {
            item1.updateData(bean1);
            item2.updateData(bean2);
        }

        public void updateBigData(LiveRoomBean liveRoomBean) {
            itemBig.updateData(liveRoomBean);
        }

    }

    public final class ViewItem {
        View itemView;
        ImageView ivPic;
        TextView tvLiveTitle;
        TextView tvCount;
        TextView tvLoc;
        TextView tvTitle;
        ImageView ivAvatar;
        ImageView ivWeekStar;

        public ViewItem(View itemView) {
            this.itemView = itemView;
            ivPic = itemView.findViewById(R.id.iv_pic);
            tvLiveTitle = itemView.findViewById(R.id.tv_live_title);
            tvCount = itemView.findViewById(R.id.tv_count);
            tvLoc = itemView.findViewById(R.id.tv_position);
            tvTitle = itemView.findViewById(R.id.tv_title);
            if (!isSmall) {
                ivAvatar = itemView.findViewById(R.id.iv_avatar);
            }
            ivWeekStar = itemView.findViewById(R.id.iv_week_star);
        }

        public void updateData(LiveRoomBean liveRoomBean) {
            if (liveRoomBean == null && itemView != null) {
                itemView.setVisibility(View.INVISIBLE);
                return;
            }
            itemView.setVisibility(View.VISIBLE);
            if (isSmall) {
                ImageLoader.loadRoundAvatar(ivPic.getContext(), ImageLoader.checkOssImageUrl(liveRoomBean.pic, 320), ivPic);
                tvCount.setText(liveRoomBean.total + "");
            } else {
                ImageLoader.loadCenterCrop(ivPic.getContext(), liveRoomBean.pic, ivPic);
                ImageLoader.loadCircleAvatar(ivAvatar.getContext(), liveRoomBean.avatar, ivAvatar);
                tvCount.setText(liveRoomBean.total + "观看");
            }
            tvLiveTitle.setText(liveRoomBean.title);
            tvLoc.setText(liveRoomBean.location);
            if (tagId == LiveListPanel.TAG_DISCOVERTY) {
                tvLoc.setText(liveRoomBean.distance);
            }
            tvTitle.setText(liveRoomBean.nickname);
            ivWeekStar.setVisibility(liveRoomBean.star > 0 ? View.VISIBLE : View.GONE);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }
}
