package com.juxin.predestinate.ui.start;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.start.UP;
import com.juxin.predestinate.module.logic.baseui.ExBaseAdapter;

import java.util.List;

/**
 * 登录界面：账号密码列表 Adapter
 */
class UserPrivacyAdapter extends ExBaseAdapter<UP> {
    private OnDelItemListener mDelItemListener;

    UserPrivacyAdapter(Context context, List datas) {
        super(context, datas);
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder mHolder;
        if (convertView == null) {
            mHolder = new ViewHolder();
            convertView = inflate(R.layout.p1_user_login_usernameselect_items);
            mHolder.username = (TextView) convertView.findViewById(R.id.userlogin_username_item_text);
            mHolder.userdelete = (ImageView) convertView.findViewById(R.id.userLogin_username_item_delete);

            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        final UP hr = getItem(position);
        mHolder.username.setText(hr.getUid() + "");

        mHolder.userdelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDelItemListener != null) {
                    mDelItemListener.onDelClick(position);
                }
            }
        });

        return convertView;
    }

    private class ViewHolder {
        TextView username;
        ImageView userdelete;
    }

    // --------------------与Activity交互-------------------------------------
    interface OnDelItemListener {
        void onDelClick(int position);
    }

    void setDelListener(OnDelItemListener listener) {
        this.mDelItemListener = listener;
    }
}
