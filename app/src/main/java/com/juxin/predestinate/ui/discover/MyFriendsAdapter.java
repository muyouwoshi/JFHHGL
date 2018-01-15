package com.juxin.predestinate.ui.discover;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.log.PLogger;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.module.local.statistics.StatisticsMessage;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.ExBaseAdapter;
import com.juxin.predestinate.module.util.UIShow;

import java.util.List;


/**
 * Created by zhang on 2017/5/4.
 */

public class MyFriendsAdapter extends ExBaseAdapter<UserInfoLightweight> {

    private boolean mIsMan;

    public MyFriendsAdapter(Context context, List<UserInfoLightweight> datas) {
        super(context, datas);
        this.mIsMan = ModuleMgr.getCenterMgr().getMyInfo().isMan();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder holder;
        if (convertView == null) {
            convertView = inflate(R.layout.f1_my_friend_item);
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
            if (!mIsMan) {
                holder.iv_ranking.setImageResource(R.drawable.f1_top02);
            } else {
                holder.iv_ranking.setImageResource(R.drawable.f1_top01);
            }
        } else {
            holder.iv_ranking.setVisibility(View.GONE);
        }
        if(userInfo.getIntimate_level() > 0) {
            holder.tv_relation.setVisibility(View.VISIBLE);
            holder.tv_relation.setText("LV" + userInfo.getIntimate_level());
        }else {
            holder.tv_relation.setVisibility(View.GONE);
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

        /*if (mIsMan) { // 女号好友列表扔显示爱心值
            if (userInfo.isVideo_available() || userInfo.isVideo_available()) {
                //holder.ll_right_relation.setVisibility(View.GONE);
                holder.iv_va_open.setVisibility(View.VISIBLE);
                holder.iv_va_open.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new SelectCallTypeDialog(getContext(), userInfo.uid, String.valueOf(userInfo.getChannel_uid()));
                    }
                });
            } else {
                holder.iv_va_open.setVisibility(View.GONE);
                //holder.ll_right_relation.setVisibility(View.VISIBLE);
            }
        }*/

        //holder.tv_heartNum.setText(userInfo.getHeartNum() + "");

        holder.rel_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIShow.showPrivateChatAct(getContext(), userInfo.getUid(), null);
                StatisticsMessage.openFriendChat(userInfo.getUid(), ModuleMgr.getChatListMgr().getNoReadNum(userInfo.getUid()));
            }
        });

        holder.iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIShow.showPrivateChatAct(getContext(), userInfo.getUid(), null);
                StatisticsMessage.openFriendChat(userInfo.getUid(), ModuleMgr.getChatListMgr().getNoReadNum(userInfo.getUid()));
            }
        });


        if(userInfo.isonline()){
            holder.iv_online_status.setImageResource(R.drawable.myfriends_online);
        }else{
            holder.iv_online_status.setImageResource(R.drawable.myfriends_offline);
        }

        return convertView;
    }


    private class MyViewHolder {
        private ImageView iv_avatar, iv_vip, iv_ranking,iv_online_status;
        private TextView tv_name, tv_age, tv_height, tv_heartNum, tv_relation;
        private RelativeLayout rel_item;

        //private LinearLayout ll_right_relation;
        //private ImageView iv_va_open;

        public MyViewHolder(View convertView) {
            initView(convertView);
        }

        private void initView(View convertView) {
            iv_avatar = (ImageView) convertView.findViewById(R.id.myfriend_item_avatar);
            iv_vip = (ImageView) convertView.findViewById(R.id.myfriend_item_vip_state);


            tv_name = (TextView) convertView.findViewById(R.id.myfriend_item_name);
            tv_age = (TextView) convertView.findViewById(R.id.myfriend_item_age);
            tv_height = (TextView) convertView.findViewById(R.id.myfriend_item_height);

           // tv_heartNum = (TextView) convertView.findViewById(R.id.myfriend_heart_num);

            iv_ranking = (ImageView) convertView.findViewById(R.id.myfriend_item_ranking_state);
            rel_item = (RelativeLayout) convertView.findViewById(R.id.myfriend_item);

            //ll_right_relation = (LinearLayout) convertView.findViewById(R.id.ll_right_relation);
            //iv_va_open = (ImageView) convertView.findViewById(R.id.iv_va_open);

            iv_online_status = (ImageView) convertView.findViewById(R.id.iv_online_status);
            tv_relation = (TextView) convertView.findViewById(R.id.myfriend_relation_state);
        }

    }
}
