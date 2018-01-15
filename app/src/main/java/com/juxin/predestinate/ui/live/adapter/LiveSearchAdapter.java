package com.juxin.predestinate.ui.live.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.predestinate.R;
import com.juxin.predestinate.ui.live.bean.LiveSearchBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 直播模块用户搜索视频器
 *
 * @author terry
 * @date 2017/10/27
 */
public class LiveSearchAdapter extends BaseAdapter {

    private Context mContext;
    private List<LiveSearchBean> dataList = new ArrayList<>();

    public LiveSearchAdapter(Context ctx) {
        this.mContext = ctx;
    }

    public void setDataList(List<LiveSearchBean> list) {
        if (dataList == null) {
            dataList = new ArrayList<>();
        }
        dataList.clear();
        dataList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(dataList.get(position).uid);
    }

    ViewHolder holder;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.live_v2_search_item, null);
            holder.iv_avatar = convertView.findViewById(R.id.live_search_item_avatar);
            holder.tv_name = convertView.findViewById(R.id.live_search_item_name);
            holder.tv_id = convertView.findViewById(R.id.live_search_item_id);
            holder.tv_live_state = convertView.findViewById(R.id.live_search_item_state_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        LiveSearchBean data = dataList.get(position);
        holder.tv_name.setText(data.nickname);
        holder.tv_id.setText("ID:" + data.uid);

        if (data.living_status == LiveSearchBean.LIVE_ING) {
            holder.tv_live_state.setVisibility(View.VISIBLE);
        } else {
            holder.tv_live_state.setVisibility(View.GONE);
        }

        ImageLoader.loadRoundAvatar(mContext, data.avatar, holder.iv_avatar);
        return convertView;
    }

    class ViewHolder {
        private ImageView iv_avatar;
        private TextView tv_name, tv_id, tv_live_state;
    }

}
