package com.juxin.predestinate.ui.discover;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.unread.BadgeView;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.config.base.NobilityList;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.chat.utils.SortList;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.ExBaseAdapter;
import com.juxin.predestinate.module.util.TimeUtil;
import com.juxin.predestinate.module.util.UIShow;

import java.util.List;

/**
 * 私密消息，陌生人消息，送礼物的人
 * Created by Su on 2017/8/12.
 */

public class ChatPageAdapter extends ExBaseAdapter<BaseMessage> {
    public static final int CHAT_STRANGER_PAGE = 1;  // 陌生人消息
    private int pageType;

    public ChatPageAdapter(Context context, List<BaseMessage> datas) {
        super(context, datas);
    }

    public ChatPageAdapter(Context context, List<BaseMessage> datas, int pageType) {
        super(context, datas);
        this.pageType = pageType;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder holder;
        if (convertView == null) {
            convertView = inflate(R.layout.f1_chat_secret_item);
            holder = new MyViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (MyViewHolder) convertView.getTag();
        }

        final BaseMessage msgData = getItem(position);
        ImageLoader.loadRoundAvatar(getContext(), msgData.getAvatar(), holder.iv_avatar);

        setUnreadnum(holder.item_unreadnum, msgData);
        String nickname = msgData.getName();
        if (!TextUtils.isEmpty(nickname)) {
            holder.tv_name.setText(nickname.length() <= 10 ? nickname : nickname.substring(0, 9) + "...");
        } else {
            holder.tv_name.setText(String.valueOf(msgData.getLWhisperID()));
        }

        holder.iv_vip.setVisibility(ModuleMgr.getCenterMgr().isVip(msgData.getIsVip()) ? View.VISIBLE : View.GONE);

        if (getItemHeight() == 0) {
            int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            holder.rel_item.measure(width, height);
            setItemHeight(holder.rel_item.getMeasuredHeight());
        }

        if (msgData.isTop()) {
            holder.iv_ranking.setVisibility(View.VISIBLE);
            if (msgData.getGender() == 1) {
                holder.iv_ranking.setImageResource(R.drawable.f1_top02);
            } else {
                holder.iv_ranking.setImageResource(R.drawable.f1_top01);
            }
        } else {
            holder.iv_ranking.setVisibility(View.GONE);
        }


        NobilityList.Nobility nobility = ModuleMgr.getCommonMgr().getCommonConfig().queryNobility(msgData.getNobility_rank(), msgData.getGender());
        String titleIcon = nobility.getTitle_icon();
        holder.item_nobility.setVisibility(View.GONE);
        if(!TextUtils.isEmpty(titleIcon)){
            ImageLoader.loadCenterCrop(getContext(), titleIcon, holder.item_nobility);
            holder.item_nobility.setVisibility(View.VISIBLE);
        }

        if(msgData.getIntimateLevel() > 0) {
            holder.item_relation_state.setVisibility(View.VISIBLE);
            holder.item_relation_state.setText("LV" + msgData.getIntimateLevel());
        }else {
            holder.item_relation_state.setVisibility(View.GONE);
        }

        String result = BaseMessage.getContent(msgData);
        if (!TextUtils.isEmpty(result)) {
            if (msgData.getType() == BaseMessage.BaseMessageType.common.getMsgType()) {
                holder.call_state.setText(result);
            } else {
                holder.call_state.setText(Html.fromHtml(result));
            }
        } else {
            holder.call_state.setText("");
        }

        holder.tv_time.setText(TimeUtil.formatBeforeTimeWeek(msgData.getTime()));
        switch (pageType) {
            case CHAT_STRANGER_PAGE:
                break;
        }

        holder.iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIShow.showCheckOtherInfoAct(getContext(), msgData.getLWhisperID());
            }
        });
        return convertView;
    }

    /**
     * 角标
     */
    protected void setUnreadnum(BadgeView item_unreadnum, BaseMessage msgData) {
        item_unreadnum.setVisibility(View.GONE);
        if (msgData.getNum() > 0) {
            item_unreadnum.setVisibility(View.VISIBLE);
            item_unreadnum.setText(ModuleMgr.getChatListMgr().getUnreadNum(msgData.getNum()));
        }
    }

    class MyViewHolder {
        private ImageView iv_avatar, iv_vip, iv_ranking, item_nobility;
        private TextView tv_name, call_state, tv_time, item_relation_state;
        private RelativeLayout rel_item;
        public BadgeView item_unreadnum;

        public MyViewHolder(View convertView) {
            initView(convertView);
        }

        private void initView(View convertView) {
            iv_avatar = (ImageView) convertView.findViewById(R.id.discover_item_avatar);
            iv_vip = (ImageView) convertView.findViewById(R.id.discover_item_vip_state);

            call_state = (TextView) convertView.findViewById(R.id.calling_state);

            tv_name = (TextView) convertView.findViewById(R.id.discover_item_name);


            item_nobility = (ImageView) convertView.findViewById(R.id.discover_item_nobility);
            item_relation_state = (TextView) convertView.findViewById(R.id.discover_relation_state);

            iv_ranking = (ImageView) convertView.findViewById(R.id.discover_item_ranking_state);
            rel_item = (RelativeLayout) convertView.findViewById(R.id.discover_item);
            tv_time = (TextView) convertView.findViewById(R.id.chat_item_time);
            item_unreadnum = (BadgeView) convertView.findViewById(R.id.mail_item_unreadnum);
        }
    }

    private int itemHeight;

    public void setItemHeight(int itemHeight) {
        this.itemHeight = itemHeight;
    }

    public int getItemHeight() {
        return itemHeight;
    }

    @Override
    public void notifyDataSetChanged() {
        SortList.sortWeightTimeListView(getList());
        super.notifyDataSetChanged();
    }
}

