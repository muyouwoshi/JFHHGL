package com.juxin.predestinate.module.local.msgview.chatview.msgpanel;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.library.log.PSP;
import com.juxin.library.observe.MsgType;
import com.juxin.library.observe.PObserver;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.chat.msgtype.GiftMessage;
import com.juxin.predestinate.module.local.chat.msgtype.InviteVideoMessage;
import com.juxin.predestinate.module.local.msgview.ChatAdapter;
import com.juxin.predestinate.module.local.msgview.chatview.ChatPanel;
import com.juxin.predestinate.module.local.statistics.SourcePoint;
import com.juxin.predestinate.module.local.statistics.StatisticsMessage;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.util.CountDownTimerUtil;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.agora.util.VideoAudioChatHelper;
import com.juxin.predestinate.ui.user.my.TimeMgr;
import com.juxin.predestinate.ui.utils.NoDoubleClickListener;

import static com.juxin.predestinate.R.id.ll_invite_connect;
import static com.juxin.predestinate.R.id.ll_invite_reject;
import static com.juxin.predestinate.module.logic.application.App.activity;

/**
 * 邀请音视频消息，只能出现在发送左侧
 *
 * @author zm
 * @date 2017/6/20
 */
public class ChatPanelInvite extends ChatPanel implements PObserver {

    private ImageView imgPic;
    private TextView tvTitle, tvContent, tvTime, tvConnect;
    private LinearLayout llReject, llConnect;
    private InviteVideoMessage mInviteVideoMessage;
    private CountDownTimerUtil util;
    private long id = -1;
    private long whisperID;
    private String channelUid;
    private long inviteId;
    private int type;
    private boolean isHandled = false;

    public ChatPanelInvite(Context context, ChatAdapter.ChatInstance chatInstance, boolean sender) {
        super(context, chatInstance, R.layout.f1_chat_item_panel_invite, sender);
        setShowParentLayout(false);
    }

    @Override
    public void initView() {
        tvTitle = (TextView) findViewById(R.id.tv_invite_tv_title);
        tvContent = (TextView) findViewById(R.id.tv_invite_tv_content);
        tvTime = (TextView) findViewById(R.id.tv_invite_tv_time);
        tvConnect = (TextView) findViewById(R.id.tv_invite_tv_connect);
        llReject = (LinearLayout) findViewById(ll_invite_reject);
        llConnect = (LinearLayout) findViewById(ll_invite_connect);
        imgPic = (ImageView) findViewById(R.id.iv_invite_img);

        llReject.setOnClickListener(clickListener);
        llConnect.setOnClickListener(clickListener);

        util = CountDownTimerUtil.getInstance();
    }

    @Override
    public boolean reset(BaseMessage msgData, UserInfoLightweight infoLightweight) {
        if (msgData == null || !(msgData instanceof InviteVideoMessage)) {
            return false;
        }
        whisperID = msgData.getLWhisperID();
        channelUid = infoLightweight == null ? "0" : String.valueOf(infoLightweight.getChannel_uid());
        mInviteVideoMessage = (InviteVideoMessage) msgData;
        inviteId = mInviteVideoMessage.getInvite_id();

        this.id = mInviteVideoMessage.getTime();
        this.type = mInviteVideoMessage.getMedia_tp();
        tvContent.setText(getContext().getString(R.string.video_cost, mInviteVideoMessage.getPrice()));
        if (mInviteVideoMessage.getMedia_tp() == 1) {
            tvTitle.setText(R.string.invite_video);
            imgPic.setBackgroundResource(R.drawable.p1_ltlx01);
        } else if (mInviteVideoMessage.getMedia_tp() == 2) {
            tvTitle.setText(R.string.invite_voice);
            imgPic.setBackgroundResource(R.drawable.p1_ltlx02);
        }
        if (util.isTimingTask(id) && !util.isHandled(id)) {
            TimeMgr.getInstance().attach(this, id);
            isHandled = false;
        }
        if (util.isTimingTask(id) && !util.isHandled(id)) {
            Long time = util.getTask(id);
            if (time != null && time > 0) {
                tvTime.setText(getContext().getString(R.string.time, time));
                llReject.setVisibility(View.VISIBLE);
                llReject.setOnClickListener(clickListener);
                tvConnect.setText(R.string.connect);
            } else {
                tvTime.setText(getContext().getString(R.string.lost_efficacy));
                llReject.setVisibility(View.GONE);
                tvConnect.setText(R.string.call_back);
            }
        } else {
            llReject.setVisibility(View.GONE);
            tvConnect.setText(R.string.call_back);
            tvTime.setText(getContext().getString(R.string.lost_efficacy));
        }
        return true;
    }

    @Override
    public boolean onClickContent(BaseMessage msgData, boolean longClick) {
        if (msgData == null || !(msgData instanceof GiftMessage)) {
            return false;
        }
        return true;
    }

    @Override
    public void onMessage(String key, Object value) {
        if (MsgType.MT_Time_Change.equalsIgnoreCase(key) && value instanceof Long && !util.isHandled(id)) {
            Long messageId = (long) value;
            if (messageId == id && util.getTask(id) != null) {
                tvTime.setText(getContext().getString(R.string.time, util.getTask(id) >= 0 ? util.getTask(id) : 0));
                if (util.getTask(id) <= 0) {
                    tvTime.setText(getContext().getString(R.string.lost_efficacy));
                    llReject.setVisibility(View.GONE);
                    tvConnect.setText(R.string.call_back);
                }
            }
        } else if (MsgType.MT_Time_Change.equalsIgnoreCase(key) && util.isHandled(id) && !isHandled) {
            isHandled = true;
            tvTime.setText(getContext().getString(R.string.lost_efficacy));
            llReject.setVisibility(View.GONE);
            tvConnect.setText(R.string.call_back);
        }
    }

    private NoDoubleClickListener clickListener = new NoDoubleClickListener() {
        @Override
        public void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case ll_invite_reject:
                    StatisticsMessage.chatInviteReject(whisperID, mInviteVideoMessage.getPrice(), type);

                    if (util.isTimingTask(id) && !util.isHandled(id)) {
                        util.addHandledIds(id);
                        tvTime.setText(getContext().getString(R.string.lost_efficacy));
                        llReject.setVisibility(View.GONE);
                        tvConnect.setText(R.string.call_back);
                    }
                    break;
                case ll_invite_connect:
                    StatisticsMessage.chatInviteAccept(whisperID, mInviteVideoMessage.getPrice(), type);

                    if (util.isTimingTask(id) && !util.isHandled(id)) {
                        String typeStr = (type == 1) ? "content_video_accept" : "content_voice_accept";
                        SourcePoint.getInstance().lockSource(App.activity, typeStr);
                        //接通逻辑
                        if (isHasDiamond()) {
                            int show = PSP.getInstance().getInt(ModuleMgr.getCommonMgr().getPrivateKey(Constant.APPEAR_FOREVER_TYPE), 0);
                            VideoAudioChatHelper.getInstance().setInviteTime(mInviteVideoMessage.getTime());
                            if (type == 1 && show == 0) {
                                UIShow.showLookAtHerDlg(App.activity, whisperID, channelUid, inviteId, SourcePoint.getInstance().getSource());
                            } else {
                                VideoAudioChatHelper.getInstance().acceptInviteVAChat(inviteId, show, SourcePoint.getInstance().getSource());
                            }
                        }
                        break;
                    }
                    //点击回拨
                    if (type == 1) {//视频
                        SourcePoint.getInstance().lockSource(App.activity, "content_video_huibo");
                        String source = SourcePoint.getInstance().getSource();
                        if (ModuleMgr.getCenterMgr().getMyInfo().isMan() && ModuleMgr.getCenterMgr().getMyInfo().isB()) {
                            UIShow.showInvitaExpiredDlg(activity, whisperID, channelUid, mInviteVideoMessage.getMedia_tp(), mInviteVideoMessage.getPrice(), source, new HasDiamondBackCall() {
                                @Override
                                public void Call() {
                                    isHasDiamond();//显示钻石充值老界面
                                }
                            });
                        } else {
                            if (isHasDiamond()) {
                                UIShow.showInvitaExpiredDlg(activity, whisperID, channelUid, mInviteVideoMessage.getMedia_tp(), mInviteVideoMessage.getPrice(), source, null);
                            }
                        }
                    } else {
                        SourcePoint.getInstance().lockSource(App.activity, "content_voice_huibo");
                        if (isHasDiamond()) {
                            UIShow.showInvitaExpiredDlg(activity, whisperID, channelUid, mInviteVideoMessage.getMedia_tp(), mInviteVideoMessage.getPrice(),
                                    SourcePoint.getInstance().getSource(), null);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public interface HasDiamondBackCall {
        public void Call();
    }

    private boolean isHasDiamond() {
        if (ModuleMgr.getCenterMgr().getMyInfo().getDiamand() < mInviteVideoMessage.getPrice()) {
            //充值弹框
            UIShow.showBottomChatDiamondDlg(App.activity, whisperID, mInviteVideoMessage.getMedia_tp(), mInviteVideoMessage.getPrice(),
                    false, 0);
            return false;
        }
        return true;
    }
}
