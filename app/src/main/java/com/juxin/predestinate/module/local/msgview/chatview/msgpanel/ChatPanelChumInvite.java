package com.juxin.predestinate.module.local.msgview.chatview.msgpanel;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.log.PLogger;
import com.juxin.library.log.PSP;
import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.library.utils.TimeBaseUtil;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.bean.db.DBCallback;
import com.juxin.predestinate.bean.my.GiftsList;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.chat.msgtype.ChumInviteMessage;
import com.juxin.predestinate.module.local.msgview.ChatAdapter;
import com.juxin.predestinate.module.local.msgview.chatview.ChatPanel;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.TimeUtil;
import com.juxin.predestinate.module.util.UIShow;

/**
 * 密友消息
 * Created by Kind on 2017/7/12.
 */

public class ChatPanelChumInvite extends ChatPanel implements View.OnClickListener {

    private ImageView chum_invite_img, chum_invite_receive_gift_icon;
    private TextView chum_invite_title, chum_invite_desc, chum_invite_time, chum_invite_receive_gift_name, chum_invite_reject_gift_name;
    private LinearLayout chum_invite_lay, chum_invite_lay_bottom, chum_invite_cruel_refused, chum_invite_receive_gift;
    private ChumInviteMessage inviteMessage;
    private long oneDayMs = 1000 * 60 * 60 * 24;

    public ChatPanelChumInvite(Context context, ChatAdapter.ChatInstance chatInstance, boolean sender) {
        super(context, chatInstance, R.layout.f2_chat_item_panel_chum_invite, sender);
        setShowParentLayout(false);
    }

    @Override
    public void initView() {
        chum_invite_img = (ImageView) findViewById(R.id.chum_invite_img);
        chum_invite_title = (TextView) findViewById(R.id.chum_invite_title);
        chum_invite_desc = (TextView) findViewById(R.id.chum_invite_desc);
        chum_invite_time = (TextView) findViewById(R.id.chum_invite_time);

        chum_invite_lay = (LinearLayout) findViewById(R.id.chum_invite_lay);
        chum_invite_lay_bottom = (LinearLayout) findViewById(R.id.chum_invite_lay_bottom);
        chum_invite_cruel_refused = (LinearLayout) findViewById(R.id.chum_invite_cruel_refused);
        chum_invite_receive_gift = (LinearLayout) findViewById(R.id.chum_invite_receive_gift);

        chum_invite_receive_gift_icon = (ImageView) findViewById(R.id.chum_invite_receive_gift_icon);
        chum_invite_receive_gift_name = (TextView) findViewById(R.id.chum_invite_receive_gift_name);
        chum_invite_reject_gift_name = (TextView) findViewById(R.id.chum_invite_reject_gift_name);

        chum_invite_cruel_refused.setOnClickListener(this);
        chum_invite_receive_gift.setOnClickListener(this);

        if (isSender()) {
            chum_invite_lay.setBackgroundResource(R.drawable.f2_img_chat_friend_bg_01_you);
            chum_invite_lay_bottom.setBackgroundResource(R.drawable.f2_img_chat_friend_bg_02_you);
            chum_invite_lay.setPadding(17, 12, 17, 12);
        } else {
            chum_invite_lay.setBackgroundResource(R.drawable.f2_img_chat_friend_bg_01);
            chum_invite_lay_bottom.setBackgroundResource(R.drawable.f2_img_chat_friend_bg_02);
            chum_invite_lay.setPadding(24, 12, 24, 12);
        }

        chum_invite_lay.setGravity(Gravity.CENTER_VERTICAL);
    }

    @Override
    public boolean reset(BaseMessage msgData, UserInfoLightweight infoLightweight) {
        if (msgData == null || !(msgData instanceof ChumInviteMessage)) return false;

        inviteMessage = (ChumInviteMessage) msgData;
        onData(inviteMessage);

        return true;
    }

    private void onData(ChumInviteMessage inviteMessage) {
        GiftsList.GiftInfo giftInfo = ModuleMgr.getCommonMgr().getGiftLists().getGiftInfo(inviteMessage.getGift_id());
        if (giftInfo == null) {
            setVisibility(View.GONE);
            return;
        }

        ImageLoader.loadFitCenter(context, giftInfo.getPic(), chum_invite_img);
        if (isSender()) {
            chum_invite_cruel_refused.setOnClickListener(null);
            chum_invite_receive_gift.setOnClickListener(null);
            chum_invite_title.setText("我想和你成为密友");
            chum_invite_desc.setText(ModuleMgr.getCenterMgr().getMyInfo().isMan() ? "送你定情信物：" + giftInfo.getName() : "送我定情信物吧");

            switch (inviteMessage.getClose_tp()) {//密友消息类型1邀请密友2接受邀请3拒绝邀请
                case 1:
                    chum_invite_cruel_refused.setVisibility(View.GONE);
                    long expire = TimeUtil.onPad(inviteMessage.getExpire()) - ModuleMgr.getAppMgr().getTime();
                    if (expire >= oneDayMs) {
                        expire = oneDayMs - 1;
                    }
                    if (expire > 0) {
                        PLogger.printObject("expire=0=" + expire);
                        chum_invite_time.setText("剩余时间：" + TimeBaseUtil.formatDuring(expire));
                        chum_invite_time.setVisibility(View.VISIBLE);
                    } else {
                        chum_invite_time.setVisibility(View.GONE);
                    }
                    chum_invite_receive_gift_name.setText("已发送");
                    chum_invite_receive_gift_icon.setVisibility(View.GONE);
                    break;
                case 2:
                    chum_invite_cruel_refused.setVisibility(View.GONE);
                    chum_invite_time.setVisibility(View.GONE);
                    chum_invite_receive_gift_name.setText("已接受");
                    chum_invite_receive_gift_icon.setVisibility(View.VISIBLE);
                    chum_invite_receive_gift_icon.setBackgroundResource(R.drawable.f2_icon_accept);
                    break;
                case 3:
                    chum_invite_cruel_refused.setVisibility(View.GONE);
                    chum_invite_time.setVisibility(View.GONE);
                    chum_invite_receive_gift_name.setText("已拒绝");
                    chum_invite_receive_gift_icon.setVisibility(View.VISIBLE);
                    chum_invite_receive_gift_icon.setBackgroundResource(R.drawable.f2_icon_refuse);
                    break;
                default:
                    setVisibility(View.GONE);
                    break;
            }
        } else {
            chum_invite_cruel_refused.setOnClickListener(this);
            chum_invite_receive_gift.setOnClickListener(this);
            chum_invite_title.setText(inviteMessage.getMsgDesc());
            chum_invite_desc.setText(ModuleMgr.getCenterMgr().getMyInfo().isMan() ? "送我定情信物吧" : "送你定情信物：" + giftInfo.getName());
            chum_invite_receive_gift_icon.setVisibility(View.VISIBLE);
            switch (inviteMessage.getClose_tp()) {//密友消息类型1邀请密友2接受邀请3拒绝邀请
                case 1:
                    chum_invite_cruel_refused.setVisibility(View.VISIBLE);
                    chum_invite_receive_gift_name.setText(ModuleMgr.getCenterMgr().getMyInfo().isMan() ? getContext().getString(R.string.receive_gift) : "接受他");
                    chum_invite_reject_gift_name.setText(ModuleMgr.getCenterMgr().getMyInfo().isMan() ? getContext().getString(R.string.cruel_refused) : "拒绝他");
                    long expire = TimeUtil.onPad(inviteMessage.getExpire()) - ModuleMgr.getAppMgr().getTime();
                    if (expire >= oneDayMs) {
                        expire = oneDayMs - 1;
                    }
                    if (expire > 0) {
                        PLogger.printObject("expire=1=" + expire);
                        chum_invite_time.setVisibility(View.VISIBLE);
                        chum_invite_time.setText("剩余时间：" + TimeBaseUtil.formatDuring(expire));
                    } else {
                        chum_invite_time.setVisibility(View.GONE);
                    }
                    chum_invite_receive_gift_icon.setBackgroundResource(R.drawable.f2_icon_accept);
                    break;
                case 2:
                    chum_invite_cruel_refused.setVisibility(View.GONE);
                    chum_invite_time.setVisibility(View.GONE);
                    chum_invite_receive_gift_name.setText(ModuleMgr.getCenterMgr().getMyInfo().isMan() ? "已接受并赠送" : "已接受");
                    chum_invite_receive_gift_icon.setBackgroundResource(R.drawable.f2_icon_accept);
                    break;
                case 3:
                    chum_invite_cruel_refused.setVisibility(View.GONE);
                    chum_invite_time.setVisibility(View.GONE);
                    chum_invite_receive_gift_name.setText("已拒绝");
                    chum_invite_receive_gift_icon.setBackgroundResource(R.drawable.f2_icon_refuse);
                    break;
                default:
                    setVisibility(View.GONE);
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        final ChumInviteMessage tmpMsg = getInviteMessage();
        if (tmpMsg == null || tmpMsg.getClose_tp() == 2 || tmpMsg.getClose_tp() == 3) {
            return;
        }
        switch (v.getId()) {
            case R.id.chum_invite_cruel_refused:
                Statistics.userBehavior(SendPoint.page_chat_msg_miyou_refuseher);
                saveChumInviteReject(true);
                ModuleMgr.getCommonMgr().rejectIntimateRequestW(tmpMsg.getInvite_id(), new RequestComplete() {
                    @Override
                    public void onRequestComplete(HttpResponse response) {
                        if (response.isOk()) {
                            tmpMsg.setClose_tp(3);//密友消息类型1邀请密友2接受邀请3拒绝邀请
                            tmpMsg.setJsonStr(tmpMsg.getJson(tmpMsg));
                            ModuleMgr.getChatMgr().updateFmessage(tmpMsg, new DBCallback() {
                                @Override
                                public void OnDBExecuted(long result) {

                                }
                            });
                            onData(tmpMsg);
                        } else {
                            saveChumInviteReject(false);
                            PToast.showShort("请求失败！");
                        }
                    }
                });
                break;
            case R.id.chum_invite_receive_gift:
                Statistics.userBehavior(SendPoint.page_chat_msg_miyou_accepther);
                boolean isMan = ModuleMgr.getCenterMgr().getMyInfo().isMan();
                GiftsList.GiftInfo giftInfo = ModuleMgr.getCommonMgr().getGiftLists().getGiftInfo(tmpMsg.getGift_id());
                if (isMan && giftInfo != null && giftInfo.getMoney() > ModuleMgr.getCenterMgr().getMyInfo().getDiamand()) {
                    UIShow.showGoodsDiamondDialog(App.getActivity(), giftInfo.getMoney() - ModuleMgr.getCenterMgr().getMyInfo().getDiamand(),
                            0, tmpMsg.getLWhisperID(), tmpMsg.getChannelID());
                    return;
                }
                saveChumInviteReject(true);
                ModuleMgr.getCommonMgr().acceptIntimateRequstW(tmpMsg.getInvite_id(), new RequestComplete() {
                    @Override
                    public void onRequestComplete(HttpResponse response) {
                        if (response.isOk()) {
                            MsgMgr.getInstance().sendMsg(MsgType.MT_Update_TASK, null);//通知更新密友任务数据
                            tmpMsg.setClose_tp(2);//密友消息类型1邀请密友2接受邀请3拒绝邀请
                            tmpMsg.setJsonStr(tmpMsg.getJson(tmpMsg));
                            ModuleMgr.getChatMgr().updateFmessage(tmpMsg, new DBCallback() {
                                @Override
                                public void OnDBExecuted(long result) {

                                }
                            });
                            onData(tmpMsg);
                        } else {
                            saveChumInviteReject(false);
                            PToast.showShort("请求失败！");
                        }
                    }
                });
                break;
            default:
                break;
        }
    }

    //保存是否为主动拒绝方
    private void saveChumInviteReject(boolean isReject) {
        PSP.getInstance().put("ChumReject", isReject);
    }

    @Override
    public boolean onClickErrorResend(BaseMessage msgData) {
        if (msgData == null || !(msgData instanceof ChumInviteMessage)) {
            return false;
        }
        setDialog(msgData, null);
        return true;
    }

    public ChumInviteMessage getInviteMessage() {
        return inviteMessage;
    }
}
