package com.juxin.predestinate.ui.mail.base;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.unread.BadgeView;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.chat.msgtype.InviteVideoMessage;
import com.juxin.predestinate.module.local.statistics.SourcePoint;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.custom.EmojiTextView;
import com.juxin.predestinate.module.logic.config.FlumeTopic;
import com.juxin.predestinate.module.util.TimeUtil;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.agora.util.VideoAudioChatHelper;
import com.juxin.predestinate.ui.user.paygoods.GoodsConstant;

import static com.juxin.predestinate.module.logic.application.ModuleMgr.getCommonMgr;

/**
 * 邀请，私密消息，礼物，陌生人
 * Created by Kind on 17/8/9.
 */
public class CustomSpecialMailItem extends CustomBaseMailItem {

    private LinearLayout item_dial;
    private ImageView item_dial_icon;
    private TextView item_dial_price;
    private BaseMessage msgData = null;

    public CustomSpecialMailItem(Context context) {
        super(context);
    }

    public CustomSpecialMailItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSpecialMailItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init() {
        super.init(R.layout.p1_mail_item_special);

        item_unreadnum = (BadgeView) findViewById(R.id.mail_item_unreadnum);
        item_headpic = (ImageView) getContentView().findViewById(R.id.mail_item_headpic);
        item_nickname = (TextView) getContentView().findViewById(R.id.mail_item_nickname);
        item_last_msg = (EmojiTextView) getContentView().findViewById(R.id.mail_item_last_msg);
        item_last_time = (TextView) getContentView().findViewById(R.id.mail_item_last_time);

        mail_item_nobility = (ImageView) getContentView().findViewById(R.id.mail_item_nobility);
        mail_relation_state = (TextView) getContentView().findViewById(R.id.mail_relation_state);
        item_ranking_state = (ImageView) getContentView().findViewById(R.id.mail_item_ranking_state);
        item_vip = (ImageView) getContentView().findViewById(R.id.mail_item_vip);
        item_certification = (TextView) getContentView().findViewById(R.id.mail_item_certification);

        item_dial = (LinearLayout) getContentView().findViewById(R.id.mail_item_invite_dial);
        item_dial_icon = (ImageView) getContentView().findViewById(R.id.mail_item_invite_dial_icon);
        item_dial_price = (TextView) getContentView().findViewById(R.id.mail_item_invite_dial_price);
        item_headpic.setOnClickListener(this);
        item_dial.setOnClickListener(this);
    }

    @Override
    public void showData(BaseMessage msgData) {
        this.msgData = msgData;
        ImageLoader.loadRoundAvatar(getContext(), msgData.getAvatar(), item_headpic);

        setNickName(msgData);
        setNobility(msgData);
        setRelation(msgData);
        setRanking(msgData);

        item_dial.setVisibility(GONE);
        if (ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
            if (msgData.getType() == 30) {//视频邀请
                item_dial.setVisibility(VISIBLE);
                item_last_time.setText("");
                if (msgData instanceof InviteVideoMessage) {
                    InviteVideoMessage inviteMessage = (InviteVideoMessage) msgData;

                    item_dial_price.setText(inviteMessage.getPrice() > 0 ? inviteMessage.getPrice() + "钻/分" : "");
                    setInviteDialIcon(inviteMessage);

                    setContent(msgData);
                }
            } else {
                item_last_msg.setTextContent(msgData.getfPrivateNum() + "条私密消息");

                long time = msgData.getTime();
                item_last_time.setText(time > 0 ? TimeUtil.formatBeforeTimeWeek(time) : "");
            }
            setUnreadnum(msgData);
        } else {
            if (ModuleMgr.getChatListMgr().isContainGiftList(msgData.getLWhisperID())) {
                item_last_msg.setText(msgData.getfGiftNum() + "条礼物未领取消息");

            } else {
                setContent(msgData);
                setUnreadnum(msgData);
            }
            long time = msgData.getTime();
            item_last_time.setText(time > 0 ? TimeUtil.formatBeforeTimeWeek(time) : "");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mail_item_headpic:
                UIShow.showPrivateChatAct(getContext(), msgData.getLWhisperID(), msgData.getName(), msgData.getKfID());
                break;
            case R.id.mail_item_invite_dial:

                if (msgData == null || !(msgData instanceof InviteVideoMessage)) return;
                InviteVideoMessage inviteMessage = (InviteVideoMessage) msgData;

                String source = getInviteAVType(isInviteValid,inviteMessage.getMedia_tp());
                SourcePoint.getInstance().lockSource(source).lockPPCSource(inviteMessage.getFid(),inviteMessage.getChannel_uid());

                setInviteDialIcon(inviteMessage);
                boolean isVip = (getCommonMgr().getCommonConfig().getVideo().isVideoCallNeedVip() && !ModuleMgr.getCenterMgr().getMyInfo().isVip());

//                if (isVip && !isInviteValid) {//不是vip
//                    UIShow.showGoodsVipBottomDlg(getContext(), GoodsConstant.DLG_VIP_AV_CHAT);
//                } else
                if (ModuleMgr.getCenterMgr().getMyInfo().getDiamand() < inviteMessage.getPrice()) {
                    //充值弹框
                    UIShow.showBottomChatDiamondDlg(App.activity, inviteMessage.getLWhisperID(), inviteMessage.getMedia_tp(), inviteMessage.getPrice(), false, 0);
                } else {
                    msgData.setfStatus(0);
                    VideoAudioChatHelper.getInstance().handleGirlInvite((Activity) App.getActivity(),
                            msgData.getLWhisperID(), isInviteValid ? inviteMessage.getInvite_id() : 0, inviteMessage.getMedia_tp(), inviteMessage.getPrice(),source );

                }
                break;
            default:
                break;
        }
    }

    private boolean isInviteValid = false;   // 邀请是否有效

    private void setInviteDialIcon(InviteVideoMessage inviteMessage) {
        if (inviteMessage.getMedia_tp() == 1) {//视频
            if (inviteMessage.getTimeout_tm() > ModuleMgr.getAppMgr().getSecondTime()) {
                isInviteValid = true;
                item_dial_icon.setImageResource(R.drawable.f1_chat_video_answer);
            } else {
                isInviteValid = false;
                item_dial_icon.setImageResource(R.drawable.f1_chat_video_callback);
            }
        } else {//语音
            if (inviteMessage.getTimeout_tm() > ModuleMgr.getAppMgr().getSecondTime()) {
                isInviteValid = true;
                item_dial_icon.setImageResource(R.drawable.f1_chat_voice_answer);
            } else {
                isInviteValid = false;
                item_dial_icon.setImageResource(R.drawable.f1_chat_voice_callback);
            }
        }
    }

    /**
     * 获取统计的类型
     * @param isInviteValid 邀请是否有效
     * @param media_tm 消息类型
     */
    private String getInviteAVType(boolean isInviteValid,long media_tm ) {
        if (media_tm == 1) {//视频
            if(isInviteValid) return FlumeTopic.XIAOXI_LISTITEM_VIDEO_JIETING.name;
            else return FlumeTopic.XIAOXI_LISTITEM_VIDEO_HUIBO.name;
        }else{//语音
            if(isInviteValid) return FlumeTopic.XIAOXI_LISTITEM_VOICE_JIETING.name;
            else return FlumeTopic.XIAOXI_LISTITEM_VOICE_HUIBO.name;
        }
    }
}