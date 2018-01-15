package com.juxin.predestinate.ui.user.my;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserVideo;
import com.juxin.predestinate.bean.my.GiftsList;
import com.juxin.predestinate.module.local.chat.msgtype.PrivateMessage;
import com.juxin.predestinate.module.local.statistics.StatisticsMessage;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.module.util.my.GiftHelper;
import com.third.wa5.sdk.common.utils.NetUtil;

import java.util.ArrayList;

/**
 * 创建日期：2017/6/26
 * 描述:查看私密弹框
 * <p>
 * 之前存在的
 * 本次感受弹框 PhotoFeelDlg
 * 解锁私密视频、语音、消息弹框 DiamondOpenVideoDlg
 * <p>
 * 作者:lc
 */
public class LookPrivateDlg extends BaseDialogFragment implements View.OnClickListener, GiftHelper.OnRequestGiftListCallback {

    private int giftCount = 1;
    private int gType = 6;
    private int type;        //1消息、2语音、3视频、4图片
    private int TYPE_ONE = 1;
    private int TYPE_TWO = 2;
    private int TYPE_THREE = 3;
    private int TYPE_FOUR = 4;
    private String price;

    private Context context;
    private ImageView iv_audio_msg_close, iv_video_gift, iv_audio_msg_gift;
    private LinearLayout ll_all, ll_video, ll_audio_msg;
    private TextView tv_video_title, tv_video_gift_price, tv_video_gift_name, tv_audio_msg_price, tv_audio_msg_name, tv_video_ok, tv_audio_msg_ok;

    private PrivateMessage msg;
    private GiftsList.GiftInfo info;

    private boolean isSend = false;

    public LookPrivateDlg() {
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.BOTTOM);
        setDialogSizeRatio(1, 0);
        setCancelable(true);
    }

    public void setData(Context context, PrivateMessage msg, int type) {
        this.context = context;
        this.msg = msg;
        this.type = type;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.f1_look_private_dlg);
        View view = getContentView();
        initView(view);
        initGifts();
        return view;
    }

    private void initView(View view) {
        ll_all = (LinearLayout) view.findViewById(R.id.ll_all);
        //video img 部分
        ll_video = (LinearLayout) view.findViewById(R.id.ll_video);
        tv_video_title = (TextView) view.findViewById(R.id.tv_video_title);
        iv_video_gift = (ImageView) view.findViewById(R.id.iv_video_gift);
        tv_video_gift_price = (TextView) view.findViewById(R.id.tv_video_gift_price);
        tv_video_gift_name = (TextView) view.findViewById(R.id.tv_video_gift_name);
        //audio msg 部分
        ll_audio_msg = (LinearLayout) view.findViewById(R.id.ll_audio_msg);
        iv_audio_msg_close = (ImageView) view.findViewById(R.id.iv_audio_msg_close);
        iv_audio_msg_gift = (ImageView) view.findViewById(R.id.iv_audio_msg_gift);
        tv_audio_msg_price = (TextView) view.findViewById(R.id.tv_audio_msg_price);
        tv_audio_msg_name = (TextView) view.findViewById(R.id.tv_audio_msg_name);
        //ok
        tv_video_ok = (TextView) view.findViewById(R.id.tv_video_ok);
        tv_audio_msg_ok = (TextView) view.findViewById(R.id.tv_audio_msg_ok);

        showType();
        iv_audio_msg_close.setOnClickListener(this);
        tv_video_ok.setOnClickListener(this);
        tv_audio_msg_ok.setOnClickListener(this);
    }

    private void showType() {
        try {
            if (type == TYPE_ONE) {
                ll_audio_msg.setVisibility(View.VISIBLE);
                iv_audio_msg_close.setVisibility(View.VISIBLE);
            } else if (type == TYPE_TWO) {
                ll_audio_msg.setVisibility(View.VISIBLE);
                iv_audio_msg_close.setVisibility(View.VISIBLE);
            } else if (type == TYPE_THREE || type == TYPE_FOUR) {
                ll_video.setVisibility(View.VISIBLE);
                tv_video_title.setText(getString(R.string.private_type, getType(type)));
            } else {
                ll_all.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getType(int type) {
        String typeStr = "";
        if (type == TYPE_ONE) {
            typeStr = "消息";
        } else if (type == TYPE_TWO) {
            typeStr = "语音";
        } else if (type == TYPE_THREE) {
            typeStr = "视频";
        } else if (type == TYPE_FOUR) {
            typeStr = "图片";
        }
        return typeStr;
    }

    /**
     * 初始化礼物列表
     */
    private void initGifts() {
        info = ModuleMgr.getCommonMgr().getGiftLists().getGiftInfo((int) msg.getGift_id());
        if (info == null) {
            ModuleMgr.getCommonMgr().requestGiftList(this);
        } else {
            setGiftView(info);
        }
    }

    private void setGiftView(GiftsList.GiftInfo info) {
        if (type == TYPE_ONE || type == TYPE_TWO) {
            ImageLoader.loadFitCenter(context, info.getPic(), iv_audio_msg_gift);
            tv_audio_msg_price.setText(getString(R.string.private_num, info.getMoney()));
            tv_audio_msg_name.setText(info.getName());
            price = tv_audio_msg_price.getText().toString().trim();
        } else if (type == TYPE_THREE || type == TYPE_FOUR) {
            ImageLoader.loadFitCenter(context, info.getPic(), iv_video_gift);
            tv_video_gift_price.setText(getString(R.string.private_num, info.getMoney()));
            tv_video_gift_name.setText(info.getName());
            price = tv_video_gift_price.getText().toString().trim();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_audio_msg_close:
                dismissAllowingStateLoss();
                break;
            case R.id.tv_video_ok:
                okClick();
                break;
            case R.id.tv_audio_msg_ok:
                okClick();
                break;
            default:
                break;
        }
    }

    private void okClick() {
        if (TextUtils.isEmpty(price) || info == null) {
            return;
        }
        if (msg != null) {
            StatisticsMessage.chatPrivateBounty(msg.getLWhisperID(), type, info.getMoney(), info.getId(), msg.getMsgID());
        } else {
            dismissAllowingStateLoss();
        }

        if (!NetUtil.getInstance().isNetConnect(context)) {
            PToast.showShort(context.getString(R.string.tip_net_error));
            return;
        }

        int needStone = info.getMoney();
        if (needStone > ModuleMgr.getCenterMgr().getMyInfo().getDiamand()) {
            dismissAllowingStateLoss();
            UIShow.showGoodsDiamondDialog(getContext(), needStone - ModuleMgr.getCenterMgr().getMyInfo().getDiamand(),
                    Constant.OPEN_FROM_CHAT_FRAME, Long.parseLong(msg.getWhisperID()), msg.getChannelID());
            return;
        }
        if (!isSend) {
            sendGift();
        } else {
            PToast.showShort(getString(R.string.send_lock_tip));
        }
    }

    @Override
    public void onRequestGiftListCallback(boolean isOk) {
        if (isOk && msg != null) {
            info = ModuleMgr.getCommonMgr().getGiftLists().getGiftInfo((int) msg.getGift_id());
            if (info == null) return;
            setGiftView(info);
        }
    }

    /**
     * 发送礼物
     */
    private void sendGift() {
        isSend = true;
        ModuleMgr.getChatMgr().sendGiftMsg(msg.getChannelID(), msg.getWhisperID(), (int) msg.getGift_id(), giftCount, gType, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                isSend = false;
                if (response.isOk()) {
                    if (info == null)
                        return;
                    ModuleMgr.getChatMgr().updateMsgFStatus(msg.getMsgID(), null);
                    msg.setUnlocked(1);
                    MsgMgr.getInstance().sendMsg(MsgType.MT_Private_Msg_Ulock, 0);
                    int stone = ModuleMgr.getCenterMgr().getMyInfo().getDiamand() - info.getMoney() * giftCount;
                    if (stone >= 0)
                        ModuleMgr.getCenterMgr().getMyInfo().setDiamand(stone);
                    reqSerectUnlock();
                    if (msg == null)
                        return;
                    if (type == TYPE_FOUR) {//跳转浏览私密照片页
                        UIShow.showPrivatePhotoDisplayAct(App.activity, msg.getQun_id(), msg.getLWhisperID(), msg.getInfo(), (ArrayList<String>) msg.getPhotos());
                    } else if (type == TYPE_THREE) {//跳转到视频播放画面
                        UserVideo userVideo = new UserVideo();
                        userVideo.setOpen(2);
                        userVideo.setPic(msg.getVideoThumb());
                        userVideo.setVideo(msg.getVideoUrl());
                        userVideo.setContent(msg.getInfo());
                        userVideo.setId(msg.getMsgID());
                        userVideo.setDuration(msg.getVideoLen());
                        UIShow.showSecretVideoPlayerDlg((FragmentActivity) App.activity, userVideo, true);
                    }
                } else {
                    dismissAllowingStateLoss();
                    PToast.showShort(response.getMsg());
                }
            }
        });
    }

    /**
     * 私密视频/语音/图片/聊天的解锁通知
     */
    private void reqSerectUnlock() {
        ModuleMgr.getCommonMgr().reqSerectUnlock(msg.getQun_id(), msg.getLWhisperID(), msg.getcMsgID(), new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                dismissAllowingStateLoss();
            }
        });
    }
}
