package com.juxin.predestinate.ui.discover;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.config.base.NobilityList;
import com.juxin.predestinate.module.local.msgview.chatview.input.CommonGridBtnPanel;
import com.juxin.predestinate.module.local.statistics.SourcePoint;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.ui.agora.model.AgoraConstant;
import com.juxin.predestinate.ui.agora.util.VideoAudioChatHelper;
import com.juxin.predestinate.ui.mail.chat.PrivateChatAct;
import com.juxin.predestinate.ui.user.check.bean.VideoConfig;

/**
 * 创建日期：2017/5/23
 * 描述:选择通话类型弹框
 * 作者:lc
 */
public class SelectCallTypeDialog extends Dialog implements RequestComplete, View.OnClickListener {

    private long mOtherUserId;
    private String channel_uid;
    private Context mContext;
    private int level = 0;//密友等级
    private TextView tv_video_price, tv_video_price1, tv_audio_price, tv_cancel;
    private LinearLayout ll_dlg, ll_video_chat, ll_video_chat1, ll_audio_chat;

    public SelectCallTypeDialog(Context context, long otherUserId, String channel_uid) {
        super(context);
        this.mContext = context;
        this.mOtherUserId = otherUserId;
        this.channel_uid = channel_uid;

        initView(context);
        LoadingDialog.show((FragmentActivity) context);
        ModuleMgr.getCenterMgr().reqVideoChatConfig(mOtherUserId, this);//获取对方是否开启视频或语音
    }

    public SelectCallTypeDialog(Context context) {
        super(context);
    }

    public SelectCallTypeDialog(Context context, int theme) {
        super(context, theme);
    }

    protected SelectCallTypeDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private void initView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_select_call_type, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ll_dlg = (LinearLayout) view.findViewById(R.id.ll_dlg);
        ll_video_chat = (LinearLayout) view.findViewById(R.id.ll_video_chat);
        ll_video_chat1 = (LinearLayout) view.findViewById(R.id.ll_video_chat1);
        ll_audio_chat = (LinearLayout) view.findViewById(R.id.ll_audio_chat);
        tv_video_price = (TextView) view.findViewById(R.id.tv_video_price);
        tv_video_price1 = (TextView) view.findViewById(R.id.tv_video_price1);
        tv_audio_price = (TextView) view.findViewById(R.id.tv_audio_price);
        tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);

        ll_video_chat.setOnClickListener(this);
        ll_video_chat1.setOnClickListener(this);
        ll_audio_chat.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);

        //将布局设置给Dialog
        setContentView(view);
        try {
            //获取当前Activity所在的窗体
            Window dialogWindow = getWindow();
            if (dialogWindow != null) {
                dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
                //设置Dialog从窗体底部弹出
                dialogWindow.setGravity(Gravity.BOTTOM);
                //获得窗体的属性
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialogWindow.setAttributes(lp);
                setCanceledOnTouchOutside(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_video_chat:
                //邀请视频聊天
                startVa(AgoraConstant.RTC_CHAT_VIDEO, Constant.APPEAR_TYPE_OWN);
                break;

            case R.id.ll_video_chat1:
                //邀请视频聊天
                startVa(AgoraConstant.RTC_CHAT_VIDEO, Constant.APPEAR_TYPE_NO_OWN);
                break;

            case R.id.ll_audio_chat:
                //邀请音频聊天
                startVa(AgoraConstant.RTC_CHAT_VOICE, Constant.APPEAR_TYPE_NO);
                break;

            case R.id.tv_cancel:
                this.dismiss();
                break;

            default:
                break;
        }
    }

    private void startVa(int type, int singleType) {
        try {
            this.dismiss();
            if (mContext != null) {
                Activity activity = null;
                if (mContext instanceof MyFriendsAct) {
                    activity = (MyFriendsAct) mContext;
                } else if (mContext instanceof PrivateChatAct) {
                    activity = (PrivateChatAct) mContext;
                }
                if (activity != null) {
                    String typeStr = AgoraConstant.RTC_CHAT_VIDEO == type? "more_video" : "more_voice";
                    SourcePoint.getInstance().lockSource(App.activity,typeStr);
                    VideoAudioChatHelper.getInstance().inviteVAChat(activity, mOtherUserId, type, false, singleType, channel_uid, false, SourcePoint.getInstance().getSource());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestComplete(HttpResponse response) {
        try {
            LoadingDialog.closeLoadingDialog();
            if (response.getUrlParam() == UrlParam.reqVideoChatConfig) {
                if (response.isOk()) {
                    VideoConfig config = (VideoConfig) response.getBaseData();
                    if (config.getVideoChat() != 1 && config.getVoiceChat() != 1) {
                        Toast.makeText(mContext, "对方没有开启音视频", Toast.LENGTH_SHORT).show();
                        this.dismiss();
                    } else {
                        // 设置打折后的音视频价格
                        NobilityList.CloseFriend chumInfo = ModuleMgr.getCommonMgr().getCommonConfig().getNobilityList().queryCloseFriend(level);
                        ll_dlg.setVisibility(View.VISIBLE);
                        if (config.getVideoChat() == 1) {
                            ll_video_chat.setVisibility(View.VISIBLE);
                            ll_video_chat1.setVisibility(View.VISIBLE);
                            if (chumInfo.getDiscount() != 10 && level > 0) {  //设置语音打折后的价格
                                tv_video_price.setText((config.getVideoPrice() * chumInfo.getDiscount() / 10) + "钻石/分");
                                tv_video_price1.setText((config.getVideoPrice() * chumInfo.getDiscount() / 10) + "钻石/分");
                            }else {
                                tv_video_price.setText(config.getVideoPrice() + "钻石/分");
                                tv_video_price1.setText(config.getVideoPrice() + "钻石/分");
                            }
                        } else {
                            ll_video_chat.setVisibility(View.GONE);
                            ll_video_chat1.setVisibility(View.GONE);
                        }

                        if (config.getVoiceChat() == 1) {
                            ll_audio_chat.setVisibility(View.VISIBLE);
                            if (chumInfo.getDiscount() != 10 && level > 0) {  //设置语音打折后的价格
                                tv_audio_price.setText((config.getAudioPrice() * chumInfo.getDiscount() / 10) + "钻石/分");
                            }else {
                                tv_audio_price.setText(config.getAudioPrice() + "钻石/分");
                            }
                        } else {
                            ll_audio_chat.setVisibility(View.GONE);
                        }
                        this.show();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
