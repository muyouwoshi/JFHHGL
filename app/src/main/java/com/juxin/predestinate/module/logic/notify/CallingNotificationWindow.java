package com.juxin.predestinate.module.logic.notify;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.log.PSP;
import com.juxin.library.utils.ViewUtils;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.module.local.chat.msgtype.InviteVideoMessage;
import com.juxin.predestinate.module.local.statistics.SourcePoint;
import com.juxin.predestinate.module.local.statistics.StatisticsManager;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.config.FlumeTopic;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.agora.model.AgoraConstant;
import com.juxin.predestinate.ui.agora.util.VideoAudioChatHelper;
import com.juxin.predestinate.ui.user.paygoods.GoodsConstant;
import com.juxin.predestinate.ui.user.paygoods.diamond.BottomChatDiamondDlg;
import com.juxin.predestinate.ui.utils.NoDoubleClickListener;

/**
 * @author Mr.Huang
 * @date 2017/8/31
 * 来视频或语音的时候首页弹框
 */
public class CallingNotificationWindow extends NoDoubleClickListener {

    private PopupWindow window;
    private ImageView ivHead, ivFriend;
    private TextView tvName, tvMessage;

    private UserDetail detail;
    private InviteVideoMessage message;
    private SoundPool mSoundPool;
    private int streamId, height;

    public CallingNotificationWindow(Context context) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.video_primary_calling_notification, null);
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        height = contentView.getMeasuredHeight();
        View bg = contentView.findViewById(R.id.view_center);
        View btnAccept = contentView.findViewById(R.id.btn_accept);
        btnAccept.setOnClickListener(this);
        View btnRefuse = contentView.findViewById(R.id.btn_refuse);
        btnRefuse.setOnClickListener(this);
        ivHead = ViewUtils.findById(contentView, R.id.iv_head);
        tvName = ViewUtils.findById(contentView, R.id.tv_name);
        ivFriend = ViewUtils.findById(contentView, R.id.iv_friend);
        tvMessage = ViewUtils.findById(contentView, R.id.tv_message);
        window = new PopupWindow(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        window.setContentView(contentView);
        window.setClippingEnabled(true);
        window.setTouchable(true);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                stopPlaySound();
            }
        });

        int ar = ViewUtils.dpToPx(context, 4);//外边框圆角
        int r = ViewUtils.dpToPx(context, 15);//按钮圆角

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(0xff908185);
        gd.setCornerRadius(ar);
        gd.setAlpha(240);
        bg.setBackgroundDrawable(gd);

        gd = new GradientDrawable();
        gd.setColor(0xfffe7596);
        gd.setCornerRadius(r);
        btnAccept.setBackgroundDrawable(gd);

        gd = new GradientDrawable();
        gd.setColor(0xff7d6d71);
        gd.setCornerRadius(r);
        btnRefuse.setBackgroundDrawable(gd);

        mSoundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
        streamId = mSoundPool.load(context, R.raw.ring, 0);
    }

    @Override
    public void onNoDoubleClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_accept) {
            SourcePoint.getInstance().lockSource(FlumeTopic.TIP_VIDEO_ACCEPT.name).lockPPCSource(detail.getUid(), detail.getChannel_uid());
            StatisticsManager.alert_push_refuse_and_accept(message.getFid(), true);
            // 对自己的vip状态、钻石余额进行判断，弹框中展示对方的个人资料
            if (!ModuleMgr.getCenterMgr().getMyInfo().isVip()) {
                UIShow.showCallingVipDialog(detail);
            } else if (ModuleMgr.getCenterMgr().getMyInfo().getDiamand() < message.getPrice()) {
                BottomChatDiamondDlg dlg = new BottomChatDiamondDlg();
                dlg.setGravity(Gravity.CENTER);
                dlg.setDialogSizeRatio(-2, -1);
                UserInfoLightweight u = new UserInfoLightweight();
                u.setAvatar(detail.getAvatar());
                u.setNickname(detail.getNickname());
                u.setAge(detail.getAge());
                u.setVip(detail.isVip());
                u.setGender(detail.getGender());
                u.setIsonline(detail.isOnline());
                dlg.UserInfoLightweight(u);
                dlg.setInfo(
                        message.getFid(),
                        message.getMedia_tp(),
                        message.getPrice(),
                        false,
                        message.getInvite_id(),
                        GoodsConstant.DLG_VIP_INDEX_YULIAO,
                        message.getType(), false);
                dlg.showDialog((FragmentActivity) App.activity);
            } else {
                connect();
                stopPlaySound();
                window.dismiss();
                this.detail = null;
                this.message = null;
            }
        } else if (id == R.id.btn_refuse) {
            stopPlaySound();
            window.dismiss();
            StatisticsManager.alert_push_refuse_and_accept(message.getFid(), false);
            this.detail = null;
            this.message = null;
        }
    }

    /**
     * 接通,实现区分全量和减量
     */
    public void connect() {
//        ModuleMgr.getCommonMgr().completeUrlHttp(1, UpgradeSource.Other);
        // 回应邀请
        int show = PSP.getInstance().getInt(ModuleMgr.getCommonMgr().getPrivateKey(Constant.APPEAR_FOREVER_TYPE), 0);
        SourcePoint.getInstance().lockSource(FlumeTopic.TIP_VIDEO_ACCEPT.name).lockPPCSource(message.getFid(), message.getChannel_uid());
        final String source = SourcePoint.getInstance().getSource();
        //解决下面 onNoDoubleClick中点击之后dismiss时变量已重置问题
        final InviteVideoMessage message = this.message;
        VideoAudioChatHelper.getInstance().acceptInviteVAChat(message.getInvite_id(), show, false, source, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (response.isOk()) return;//接受成功的话不再走回拨逻辑
                //onNoDoubleClick中点击之后dismiss时变量已重置，暂时进行非空判断
                if (message == null) return;
                VideoAudioChatHelper.getInstance().inviteVAChat((Activity) App.getActivity(), message.getFid(), AgoraConstant.RTC_CHAT_VIDEO,
                        true, Constant.APPEAR_TYPE_NO, "", false,
                        source);

            }
        });
    }

    public void setData(final UserDetail detail, final InviteVideoMessage message) {
        if (checkPreviousIsFriend()) {
            return;
        }
        this.detail = detail;
        this.message = message;
        ImageLoader.loadCircleAvatar(ivHead.getContext(), detail.getAvatar(), ivHead);
        tvName.setText(detail.getNickname());
        String string = "发来一个" + (message.getMedia_tp() == 1 ? "视频" : "语音") + "邀请";

        if (detail.getIsIntimateFriend() == 2) {
            ivFriend.setVisibility(View.VISIBLE);
            tvMessage.setText("您的好友");
            tvMessage.append(string);
        } else {
            tvMessage.setText(string);
            ivFriend.setVisibility(View.GONE);
        }
    }

    public void showAsDropDown(final View anchor) {
        if (checkPreviousIsFriend()) {
            //如果window已经显示，并且上一个显示的是好友的信息直接return
            return;
        }
        if (!window.isShowing()) {
            window.showAsDropDown(anchor, 0, -height - anchor.getMeasuredHeight());
        }
        stopPlaySound();
        // 如果在设置界面打开了声音提示，才进行消息播放
        if (PSP.getInstance().getBoolean(Constant.SETTING_VOICE, Constant.SETTING_VOICE_DEFAULT)) {
            streamId = mSoundPool.play(1, 1, 1, 1, -1, 1.0f);
        }
    }

    public void onResume() {
        if (window.isShowing() && PSP.getInstance().getBoolean(Constant.SETTING_VOICE, Constant.SETTING_VOICE_DEFAULT)) {
            streamId = mSoundPool.play(1, 1, 1, 1, -1, 1.0f);
        }
    }

    private boolean checkPreviousIsFriend() {
        if (window.isShowing() && detail != null && detail.getIsIntimateFriend() == 2) {
            return true;
        }
        return false;
    }

    public void stopPlaySound() {
        mSoundPool.stop(streamId);
    }

}
