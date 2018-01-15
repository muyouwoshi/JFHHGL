package com.juxin.predestinate.ui.discover;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.discover.DiscoverSelect;
import com.juxin.predestinate.module.logic.baseui.ExBaseAdapter;

import java.util.List;

/**
 * Created by zhang on 2017/6/1.
 */

public class DisCoverSelectAdapter extends ExBaseAdapter<DiscoverSelect> {

    private OnSelect onSelect;

    public DisCoverSelectAdapter(Context context, List<DiscoverSelect> datas, OnSelect onSelect) {
        super(context, datas);
        this.onSelect = onSelect;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = inflate(R.layout.f1_discover_select_item);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.tv_selectType.setText(getItem(position).getSelectType());
        vh.ck_selectTag.setChecked(getItem(position).isSelect());


        vh.ck_selectTag.setOnClickListener(new View.OnClickListener() {
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
            getItem(position).setSelect(!getItem(position).isSelect());
            for (int i = 0; i < getList().size(); i++) {
                if (i != position) {
                    getItem(i).setSelect(false);
                }
            }
            notifyDataSetChanged();
            if (onSelect != null) {
                onSelect.onSelectChange(position, getItem(position));
            }
        }
    }


    class ViewHolder {
        TextView tv_selectType;
        CheckBox ck_selectTag;

        public ViewHolder(View view) {
            initView(view);
        }

        private void initView(View view) {
            tv_selectType = (TextView) view.findViewById(R.id.discover_select_item_text);
            ck_selectTag = (CheckBox) view.findViewById(R.id.discover_select_item_check);
        }
    }


    public interface OnSelect {
        void onSelectChange(int position, DiscoverSelect select);
    }

}
