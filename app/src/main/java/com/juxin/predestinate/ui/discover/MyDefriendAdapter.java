package com.juxin.predestinate.ui.discover;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.module.local.statistics.StatisticsMessage;
import com.juxin.predestinate.module.logic.baseui.ExBaseAdapter;
import com.juxin.predestinate.module.util.UIShow;

import java.util.List;

/**
 * Created by zhang on 2017/5/4.
 */

public class MyDefriendAdapter extends ExBaseAdapter<UserInfoLightweight> {


    public MyDefriendAdapter(Context context, List<UserInfoLightweight> datas) {
        super(context, datas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder holder;
        if (convertView == null) {
            convertView = inflate(R.layout.f1_my_defriend_item);
            holder = new MyViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (MyViewHolder) convertView.getTag();
        }
        final UserInfoLightweight userInfo = getItem(position);
        ImageLoader.loadRoundAvatar(getContext(), userInfo.getAvatar(), holder.iv_avatar);
        holder.tv_name.setText(userInfo.getShowName());
        holder.iv_vip.setVisibility(userInfo.isVip() ? View.VISIBLE : View.GONE);

        if (userInfo.isToper()) {
            holder.iv_ranking.setVisibility(View.VISIBLE);
            if (userInfo.isMan()) {
                holder.iv_ranking.setImageResource(R.drawable.f1_top02);
            } else {
                holder.iv_ranking.setImageResource(R.drawable.f1_top01);
            }
        } else {
            holder.iv_ranking.setVisibility(View.GONE);
        }

        if (userInfo.getAge() == 0) {
            holder.tv_age.setVisibility(View.GONE);
        } else {
            holder.tv_age.setVisibility(View.VISIBLE);
            holder.tv_age.setText(userInfo.getAge() + "岁");
        }

        if (userInfo.getHeight() == 0) {
            holder.tv_height.setVisibility(View.GONE);
        } else {
            holder.tv_height.setVisibility(View.VISIBLE);
            holder.tv_height.setText(userInfo.getHeight() + "cm");
        }


        holder.rel_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIShow.showCheckOtherInfoAct(getContext(), userInfo.getUid());
            }
        });

        holder.iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIShow.showCheckOtherInfoAct(getContext(), userInfo.getUid());
                StatisticsMessage.blackUserInfo(userInfo.getUid());
            }
        });
        return convertView;
    }


    private class MyViewHolder {
        private ImageView iv_avatar, iv_vip, iv_ranking;
        private TextView tv_name, tv_age, tv_height;
        private RelativeLayout rel_item;

        public MyViewHolder(View convertView) {
            initView(convertView);
        }

        private void initView(View convertView) {
            iv_avatar = (ImageView) convertView.findViewById(R.id.mydefriend_item_avatar);
            iv_vip = (ImageView) convertView.findViewById(R.id.mydefriend_item_vip_state);


            tv_name = (TextView) convertView.findViewById(R.id.mydefriend_item_name);
            tv_age = (TextView) convertView.findViewById(R.id.mydefriend_item_age);
            tv_height = (TextView) convertView.findViewById(R.id.mydefriend_item_height);

            iv_ranking = (ImageView) convertView.findViewById(R.id.mydefriend_item_ranking_state);
            rel_item = (RelativeLayout) convertView.findViewById(R.id.mydefriend_item);
        }

    }
}
