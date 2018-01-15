package com.juxin.predestinate.ui.user.check.self.info;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.predestinate.R;
import com.juxin.predestinate.third.recyclerholder.BaseRecyclerViewAdapter;
import com.juxin.predestinate.third.recyclerholder.BaseRecyclerViewHolder;

import java.util.List;

/**
 * 用户详细信息
 * Created by Su on 2017/5/24.
 */
public class UserInfoAdapter extends BaseRecyclerViewAdapter {
    private LinearLayout ll_title, middle_line;
    private TextView item_title, item_contact;
    private View bottom_line;

    public UserInfoAdapter(Context context) {
        super(context, null);
    }

    @Override
    public int[] getItemLayouts() {
        return new int[]{R.layout.f1_user_detail_list_item};
    }

    @Override
    public void onBindRecycleViewHolder(BaseRecyclerViewHolder viewHolder, int position) {
        UserPersonInfo info = (UserPersonInfo) getItem(position);

        ll_title = viewHolder.findViewById(R.id.ll_title);
        item_title = viewHolder.findViewById(R.id.item_title);
        TextView item_name = viewHolder.findViewById(R.id.item_name);
        TextView item_value = viewHolder.findViewById(R.id.item_value);
        item_contact = viewHolder.findViewById(R.id.item_contact);
        middle_line = viewHolder.findViewById(R.id.middle_line);
        bottom_line = viewHolder.findViewById(R.id.bottom_line);

        showOrHideTopTitle(info, position);
        showMiddleOrBottomLine(info, position);
        showOrHideContactText(info);
        item_name.setText(info.getName());
        item_value.setText(info.getValue());
    }

    @Override
    public int getRecycleViewItemType(int position) {
        return 0;
    }

    private void showOrHideTopTitle(UserPersonInfo info, int position) {
        if (position > 0) {
            if (info.getTitle().equals(((UserPersonInfo) getList().get(position - 1)).getTitle())) {
                ll_title.setVisibility(View.GONE);
            } else {
                ll_title.setVisibility(View.VISIBLE);
                item_title.setText(info.getTitle());
            }
        } else {
            ll_title.setVisibility(View.VISIBLE);
            item_title.setText(info.getTitle());
        }
    }

    private void showMiddleOrBottomLine(UserPersonInfo info, int position) {
        if (position + 1 < getList().size()) {
            if (info.getTitle().equals(((UserPersonInfo) getList().get(position + 1)).getTitle())) {
                middle_line.setVisibility(View.VISIBLE);
                bottom_line.setVisibility(View.GONE);
            } else {
                bottom_line.setVisibility(View.VISIBLE);
                middle_line.setVisibility(View.GONE);
            }
        } else {
            bottom_line.setVisibility(View.GONE);
            middle_line.setVisibility(View.GONE);
        }
    }

    private void showOrHideContactText(UserPersonInfo info) {
        if (info.getContact() != null) {
            item_contact.setVisibility(View.VISIBLE);
            item_contact.setText(info.getContact());
        } else {
            item_contact.setVisibility(View.GONE);
        }
    }
}
