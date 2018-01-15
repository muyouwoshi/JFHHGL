package com.juxin.predestinate.ui.discover;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.unread.BadgeView;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.config.base.NobilityList;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.chat.msgtype.InviteVideoMessage;
import com.juxin.predestinate.module.local.chat.utils.SortList;
import com.juxin.predestinate.module.local.statistics.SourcePoint;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.ExBaseAdapter;
import com.juxin.predestinate.module.logic.baseui.custom.EmojiTextView;
import com.juxin.predestinate.module.logic.config.FlumeTopic;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.agora.util.VideoAudioChatHelper;
import com.juxin.predestinate.ui.mail.item.MailMsgID;

import java.util.List;

import javax.xml.transform.Source;

/**
 * 视频语音邀请
 * Created by Su on 2017/8/12.
 */

public class ChatInviteAdapter extends ExBaseAdapter<BaseMessage> {
    private static final int RTC_CHAT_VIDEO = 1;    // 视频通信
    private static final int RTC_CHAT_VOICE = 2;    // 音频通信

    public ChatInviteAdapter(Context context, List<BaseMessage> datas) {
        super(context, datas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final MyViewHolder holder;
        if (convertView == null) {
            convertView = inflate(R.layout.f1_chat_invite_adapter);
            holder = new MyViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (MyViewHolder) convertView.getTag();
        }


        if (getItemHeight() == 0) {
            int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            holder.rel_item.measure(width, height);
            setItemHeight(holder.rel_item.getMeasuredHeight());
        }

        final BaseMessage userInfo = getItem(position);
        final InviteVideoMessage inviteMessage;

        setUnreadnum(holder.item_unreadnum, userInfo);
        ImageLoader.loadRoundAvatar(getContext(), userInfo.getAvatar(), holder.iv_avatar);
        holder.iv_vip.setVisibility(ModuleMgr.getCenterMgr().isVip(userInfo.getIsVip()) ? View.VISIBLE : View.GONE);

        String nickname = userInfo.getName();
        if (!TextUtils.isEmpty(nickname)) {
            holder.tv_name.setText(nickname.length() <= 10 ? nickname : nickname.substring(0, 9) + "...");
        } else {
            holder.tv_name.setText(String.valueOf(userInfo.getLWhisperID()));
        }

        if (userInfo.isTop()) {
            holder.iv_ranking.setVisibility(View.VISIBLE);
            if (userInfo.getGender() == 1) {
                holder.iv_ranking.setImageResource(R.drawable.f1_top02);
            } else {
                holder.iv_ranking.setImageResource(R.drawable.f1_top01);
            }
        } else {
            holder.iv_ranking.setVisibility(View.GONE);
        }

        NobilityList.Nobility nobility = ModuleMgr.getCommonMgr().getCommonConfig().queryNobility(userInfo.getNobility_rank(), userInfo.getGender());
        String titleIcon = nobility.getTitle_icon();
        holder.item_nobility.setVisibility(View.GONE);
        if(!TextUtils.isEmpty(titleIcon)){
            ImageLoader.loadCenterCrop(getContext(), titleIcon, holder.item_nobility);
            holder.item_nobility.setVisibility(View.VISIBLE);
        }

        if(userInfo.getIntimateLevel() > 0) {
            holder.item_relation_state.setVisibility(View.VISIBLE);
            holder.item_relation_state.setText("LV" + userInfo.getIntimateLevel());
        }else {
            holder.item_relation_state.setVisibility(View.GONE);
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

        if (userInfo.getDistance() == 0) {
            holder.tv_distance.setVisibility(View.GONE);
        } else {
            holder.tv_distance.setVisibility(View.VISIBLE);
            holder.tv_distance.setText(String.valueOf(userInfo.getDistance()));
        }

        if ((userInfo.getAge() != 0 || userInfo.getHeight() != 0) && userInfo.getDistance() != 0) {
            holder.point.setVisibility(View.VISIBLE);
        } else {
            holder.point.setVisibility(View.GONE);
        }

        holder.tv_price.setVisibility(View.GONE);
        if (userInfo instanceof InviteVideoMessage) {
            inviteMessage = (InviteVideoMessage) userInfo;
            if (inviteMessage.getPrice() > 0) {
                holder.tv_price.setVisibility(View.VISIBLE);
                holder.tv_price.setText(getContext().getString(R.string.user_other_set_chat_price, inviteMessage.getPrice()));
            }

            final boolean isCancel = inviteMessage.getTimeout_tm() > ModuleMgr.getAppMgr().getSecondTime();
            if (inviteMessage.getMedia_tp() == RTC_CHAT_VIDEO) {
                holder.call_back.setBackgroundResource(!isCancel ?
                        R.drawable.f1_chat_video_callback : R.drawable.f1_chat_video_answer);
                holder.call_state.setText(getContext().getString(R.string.chat_invite, getContext().getString(R.string.video)));
            } else {
                holder.call_back.setBackgroundResource(!isCancel ?
                        R.drawable.f1_chat_voice_callback : R.drawable.f1_chat_voice_answer);
                holder.call_state.setText(getContext().getString(R.string.chat_invite, getContext().getString(R.string.audio)));
            }

            holder.call_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SourcePoint.getInstance().lockSource(FlumeTopic.XIAOXI_OTHER.name).lockPPCSource(inviteMessage.getFid(),inviteMessage.getChannel_uid());//ppc统计
                    VideoAudioChatHelper.getInstance().handleGirlInvite((Activity) getContext(), userInfo.getLWhisperID(),
                            !isCancel ? 0 : inviteMessage.getInvite_id(), inviteMessage.getMedia_tp(), inviteMessage.getPrice(),SourcePoint.getInstance().getSource() );
                }
            });
        }

        holder.content_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userInfo != null) {
                    MailMsgID mailMsgID = MailMsgID.getMailMsgID(userInfo.getLWhisperID());
                    if (mailMsgID == null) {
                        UIShow.showPrivateChatAct(getContext(), userInfo.getLWhisperID(), userInfo.getName(), userInfo.getKfID());
                    }
                }
            }
        });

        holder.iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIShow.showCheckOtherInfoAct(getContext(), userInfo.getLWhisperID());
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
        private TextView tv_name, tv_age, tv_height, tv_distance, tv_price, item_relation_state;
        private Button call_back;
        private TextView call_state;
        private RelativeLayout content_item;
        private RelativeLayout rel_item;
        private View point;
        private BadgeView item_unreadnum;

        public MyViewHolder(View convertView) {
            initView(convertView);
        }

        private void initView(View convertView) {
            iv_avatar = (ImageView) convertView.findViewById(R.id.discover_item_avatar);
            iv_vip = (ImageView) convertView.findViewById(R.id.discover_item_vip_state);

            call_state = (TextView) convertView.findViewById(R.id.calling_state);

            tv_name = (TextView) convertView.findViewById(R.id.discover_item_name);
            tv_age = (TextView) convertView.findViewById(R.id.discover_item_age);
            tv_height = (TextView) convertView.findViewById(R.id.discover_item_height);
            tv_distance = (TextView) convertView.findViewById(R.id.discover_item_distance);

            call_back = (Button) convertView.findViewById(R.id.chat_call_back);

            item_nobility = (ImageView) convertView.findViewById(R.id.discover_item_nobility);
            item_relation_state = (TextView) convertView.findViewById(R.id.discover_relation_state);

            iv_ranking = (ImageView) convertView.findViewById(R.id.discover_item_ranking_state);
            content_item = (RelativeLayout) convertView.findViewById(R.id.rl_content);
            rel_item = (RelativeLayout) convertView.findViewById(R.id.discover_item);

            point = convertView.findViewById(R.id.discover_item_point);
            tv_price = (TextView) convertView.findViewById(R.id.chat_price);

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