package com.juxin.predestinate.ui.discover;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.discover.DefriendType;
import com.juxin.predestinate.module.logic.baseui.ExBaseAdapter;

import java.util.List;

/**
 * 举报adapter
 * Created by zhang on 2017/4/14.
 */

public class DefriendAdapter extends ExBaseAdapter<DefriendType> {
    private OnSelect onSelect;

    public DefriendAdapter(Context context, List<DefriendType> datas, OnSelect onSelect) {
        super(context, datas);
        this.onSelect = onSelect;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflate(R.layout.p1_defriend_item);
            holder = new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.defriend_item_name);
            holder.is_check = (CheckBox) convertView.findViewById(R.id.defriend_item_check);
            holder.divider_line = convertView.findViewById(R.id.defriend_divider_line);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_name.setText(getItem(position).getStr_typeName());
        holder.is_check.setChecked(getItem(position).isCheck());

//        if (position < (getCount() - 1)) {
//            holder.divider_line.setVisibility(View.VISIBLE);
//        } else {
//            holder.divider_line.setVisibility(View.GONE);
//        }

        holder.is_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCheck(position);
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCheck(position);
            }
        });

        return convertView;
    }


    private void setCheck(int position) {
        if (getItem(position) != null) {
            getItem(position).setCheck(!getItem(position).isCheck());
            for (int i = 0; i < getList().size(); i++) {
                if (i != position) {
                    getItem(i).setCheck(false);
                }
            }
            notifyDataSetChanged();
            if (onSelect != null) {
                onSelect.onSelectChange(getItem(position));
            }
        }
    }

    private class ViewHolder {
        public TextView tv_name;
        public CheckBox is_check;
        public View divider_line;
    }

    public interface OnSelect {
        void onSelectChange(DefriendType defriendType);
    }
}
