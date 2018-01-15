package com.juxin.predestinate.ui.mail.base;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.chat.msgtype.VideoMessage;
import com.juxin.predestinate.module.local.statistics.SourcePoint;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.config.FlumeTopic;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.agora.model.AgoraConstant;
import com.juxin.predestinate.ui.agora.util.VideoAudioChatHelper;

/**
 * 私聊类型
 * Created by Kind on 16/2/3.
 */
public class CustomLetterMailItem extends CustomBaseMailItem {

    private BaseMessage msgData = null;
    private LinearLayout mail_item_right;
    private TextView mail_item_right_icon;

    public CustomLetterMailItem(Context context) {
        super(context);
    }

    public CustomLetterMailItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomLetterMailItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init() {
        super.init(-1);
        mail_item_right_icon = (TextView) findViewById(R.id.mail_item_right_icon);
        mail_item_right = (LinearLayout) findViewById(R.id.mail_item_right);
        mail_item_right.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (msgData == null) return;
        switch (v.getId()) {
            case R.id.mail_item_headpic:
                UIShow.showPrivateChatAct(getContext(), msgData.getLWhisperID(), msgData.getName(), msgData.getKfID());
                break;
            case R.id.mail_item_right:
                if (!(msgData instanceof VideoMessage)) {
                    break;
                }
                VideoMessage videoMessage = (VideoMessage) msgData;
                SourcePoint.getInstance().lockSource(FlumeTopic.XIAOXI_OTHER.name);
                if (videoMessage.isVideoMediaTp()) {
                    VideoAudioChatHelper.getInstance().inviteVAChat((Activity) getContext(), msgData.getLWhisperID(),
                            AgoraConstant.RTC_CHAT_VIDEO, true, Constant.APPEAR_TYPE_NO, "", false, SourcePoint.getInstance().getSource());
                } else {
                    VideoAudioChatHelper.getInstance().inviteVAChat((Activity) getContext(), msgData.getLWhisperID(),
                            AgoraConstant.RTC_CHAT_VOICE, "", SourcePoint.getInstance().getSource());
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void showGap() {
        super.showGap();
    }

    @Override
    public void hideGap() {
        super.hideGap();
    }

    @Override
    public void showData(BaseMessage msgData) {
        super.showData(msgData);
        this.msgData = msgData;

        if (item_last_time != null && BaseMessage.video_MsgType == msgData.getType()) {
            item_last_time.setVisibility(GONE);
        }
        if (mail_item_right == null || mail_item_right_icon == null) return;

        if (BaseMessage.video_MsgType != msgData.getType() || !(msgData instanceof VideoMessage)) {// 视频语音消息
            mail_item_right.setVisibility(GONE);
            return;
        }

        mail_item_right.setVisibility(VISIBLE);
        item_last_time.setText("");
        VideoMessage videoMessage = (VideoMessage) msgData;
        mail_item_right_icon.setBackgroundResource(videoMessage.isVideoMediaTp() ? R.drawable.f1_video_state_ico : R.drawable.f1_call_state_ico);
        mail_item_right_icon.setEnabled(true);
    }
}