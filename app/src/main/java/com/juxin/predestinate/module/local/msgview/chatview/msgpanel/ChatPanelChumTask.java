package com.juxin.predestinate.module.local.msgview.chatview.msgpanel;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.library.log.PSP;
import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.chat.msgtype.ChumTaskMessage;
import com.juxin.predestinate.module.local.msgview.ChatAdapter;
import com.juxin.predestinate.module.local.msgview.chatview.ChatPanel;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.util.TimeUtil;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.module.util.UIUtil;
import com.juxin.predestinate.ui.utils.NoDoubleClickListener;

/**
 * 密友任务
 * Created by Kind on 2017/7/12.
 */

public class ChatPanelChumTask extends ChatPanel {

    private TextView tvTask, tvAward, tvTaskName, tvAwardLeft;
    private ImageView imgTaskIcon, imageLeft, imgRight;
    private LinearLayout llAction, llTask;
    private ChumTaskMessage taskMessage;

    public ChatPanelChumTask(Context context, ChatAdapter.ChatInstance chatInstance, boolean sender) {
        super(context, chatInstance, R.layout.f2_chat_item_panel_chum_task, sender);
    }

    @Override
    public void initView() {
        setShowParentLayout(false);
        tvTask = (TextView) findViewById(R.id.chat_item_chum_task_lovers_task);
        tvAward = (TextView) findViewById(R.id.chat_item_chum_task_lovers_award);
        imgTaskIcon = (ImageView) findViewById(R.id.chat_item_chum_task_task_icon);
        tvTaskName = (TextView) findViewById(R.id.chat_item_chum_task_task_name);
        imageLeft = (ImageView) findViewById(R.id.chat_item_chum_task_img_left);
        imgRight = (ImageView) findViewById(R.id.chat_item_chum_task_img_right);
        tvAwardLeft = (TextView) findViewById(R.id.chat_item_chum_task_lovers_award_left);
        llTask = (LinearLayout) findViewById(R.id.chat_item_chum_task_ll_task);

        if (isSender()) {
            imageLeft.setVisibility(View.INVISIBLE);
            imgRight.setVisibility(View.VISIBLE);
        } else {
            imageLeft.setVisibility(View.VISIBLE);
            imgRight.setVisibility(View.INVISIBLE);
        }

        llAction = (LinearLayout) findViewById(R.id.chat_item_chum_task_ll_action);
        if (ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
            llAction.setOnClickListener(clickListener);
        } else {
            llAction.setBackgroundResource(R.drawable.f2_close_task_btn_unclick_bg);
            llAction.setOnClickListener(null);
        }
        getContentView().setVisibility(View.INVISIBLE);

        float scale = PSP.getInstance().getFloat("SCALE", -1);//获取缩放比例
        if (scale > 0) {
            setScale(scale);
        }
        llTask.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
    }

    private ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() { //监听布局宽度
        @Override
        public void onGlobalLayout() {
            int contentViewWidth = getContentView().getMeasuredWidth();
            int llTaskWidth = llTask.getMeasuredWidth();
            if (contentViewWidth != 0 && llTaskWidth != 0 && llTaskWidth > contentViewWidth) {
                float scale = (float) contentViewWidth / (float) (llTaskWidth + UIUtil.dip2px(getContext(), 16));
                PSP.getInstance().put("SCALE", scale); //保存缩放比例
                setScale(scale);
            }else if (contentViewWidth != 0 && llTaskWidth != 0 && llTaskWidth <= contentViewWidth){
                PSP.getInstance().put("SCALE", 1); //保存缩放比例
                PSP.getInstance().put("TRANX", contentViewWidth - llTaskWidth - UIUtil.dip2px(getContext(), 12));
                setScale(1);
            }
            if (contentViewWidth != 0 && llTaskWidth != 0){
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    llTask.getViewTreeObserver().removeOnGlobalLayoutListener(layoutListener);
                } else {
                    llTask.getViewTreeObserver().removeGlobalOnLayoutListener(layoutListener);
                }
            }
        }
    };

    private void setScale(float scale){
        llTask.setTranslationX(0);
        if (isSender()){
            float tranX = PSP.getInstance().getFloat("TRANX",0);
            llTask.setTranslationX(tranX > 0 ? tranX:0);
        }
        llTask.setPivotX(0);
        llTask.setPivotY(0);
        llTask.setScaleX(scale);
        llTask.setScaleY(scale);
        getContentView().setVisibility(View.VISIBLE);
    }

    @Override
    public boolean reset(BaseMessage msgData, UserInfoLightweight infoLightweight) {
        if (msgData == null || !(msgData instanceof ChumTaskMessage)) return false;

        ChumTaskMessage task = (ChumTaskMessage) msgData;
        taskMessage = task;
        tvTask.setText(task.getTask_info() + "");
        tvAward.setText(task.getTask_award_info() + "");

        if (task.getTask_tp() == 1) {
            llAction.setBackgroundResource(R.drawable.f2_close_task_btn_bg);
            tvAwardLeft.setVisibility(View.GONE);
        } else {
            llAction.setBackgroundResource(R.drawable.f2_close_task_btn_unclick_bg);
            tvAwardLeft.setVisibility(View.VISIBLE);
            tvAward.setText(getContext().getString(R.string.task_award_two, task.getTask_award_info()));
        }
        if (task.getTask_action_tp() == 1) {
            imgTaskIcon.setBackgroundResource(R.drawable.f2_icon_video_task);//去音视频
        } else if (task.getTask_action_tp() == 2) {
            imgTaskIcon.setBackgroundResource(R.drawable.f2_icon_gift_task);//去送礼
        } else if (task.getTask_action_tp() == 4) {
            imgTaskIcon.setBackgroundResource(R.drawable.f2_icon_roulette_task);//去转盘
        }

        if (!ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
            llAction.setBackgroundResource(R.drawable.f2_close_task_btn_unclick_bg);
        }
        if (task.getfStatus() == 0){
            llAction.setBackgroundResource(R.drawable.f2_close_task_btn_unclick_bg);
        }

        if (taskMessage.getTask_tp() != 1) {
            tvTaskName.setText(R.string.txt_authstatus_authok);
        }else {
            tvTaskName.setText("去完成");
        }

        return false;
    }

    private NoDoubleClickListener clickListener = new NoDoubleClickListener() {

        @Override
        public void onNoDoubleClick(View v) {
            if (taskMessage == null) return;
            if (taskMessage.getfStatus() == 0){
                return;
            }
            if (taskMessage.getTask_tp() != 1) {
                PToast.showShort(R.string.txt_authstatus_authok);
                return;
            }

            long expire = TimeUtil.onPad(taskMessage.getExpire()) - ModuleMgr.getAppMgr().getTime();
            if (expire <= 0) {
                PToast.showShort(R.string.out_of_date);
                return;
            }

            switch (taskMessage.getTask_action_tp()) {
                //发起视频聊天
                case 1:
                case 4:
                    MsgMgr.getInstance().sendMsg(MsgType.MT_TASK_TO_JUMP, null);
                    break;
                //送礼提示
                case 2:
                    UIShow.showBottomGiftDlg(App.getActivity(), taskMessage.getLWhisperID(), Constant.OPEN_FROM_CHAT_FRAME, taskMessage.getChannelID());
                    break;
                default:
                    break;
            }
        }
    };
}
