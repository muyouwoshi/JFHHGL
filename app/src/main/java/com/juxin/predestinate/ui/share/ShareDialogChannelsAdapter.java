package com.juxin.predestinate.ui.share;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.juxin.predestinate.R;

import java.util.List;

/**
 * 类描述：
 * 创建时间：2017/11/2 20:15
 * 修改时间：2017/11/2 20:15
 * Created by zhoujie on 2017/11/2
 * 修改备注：
 */
public class ShareDialogChannelsAdapter extends BaseAdapter {

    private Context context;
    private List<Integer> channelTypes;
    private int[] icon = {R.drawable.spread_share_dialog_item_qq, R.drawable.spread_share_dialog_item_qqkongjian,
            R.drawable.spread_share_dialog_item_weixinhaoy, R.drawable.spread_share_dialog_item_pengyouquan,
            R.drawable.spread_share_dialog_item_erweima, R.drawable.spread_share_dialog_item_fuzhi};
    private String[] iconName;
    private LayoutInflater inflater;

    public ShareDialogChannelsAdapter(Context context, List<Integer> channelsType) {
        inflater = LayoutInflater.from(context);
        channelTypes = channelsType;
        this.context = context;
        iconName = new String[]{context.getString(R.string.spread_share_dialog_item_qq), context.getString(R.string.spread_share_dialog_item_qqkongjian),
                context.getString(R.string.spread_share_dialog_item_weixinhaoy), context.getString(R.string.spread_share_dialog_item_pengyouquan),
                context.getString(R.string.spread_share_dialog_item_erweima), context.getString(R.string.spread_share_dialog_item_fuzhi)};
    }

    @Override
    public int getCount() {
        return channelTypes.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.spread_share_dialog_channel_item, null);
            viewHolder = new ViewHolder();
            viewHolder.channelName = convertView.findViewById(R.id.channel_name);
            viewHolder.channleIcon = convertView.findViewById(R.id.channel_icon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.channelName.setText(iconName[channelTypes.get(position) - 1]);
        viewHolder.channleIcon.setImageResource(icon[channelTypes.get(position) - 1]);
        return convertView;
    }

    class ViewHolder {
        public TextView channelName;
        public ImageView channleIcon;
    }
}
