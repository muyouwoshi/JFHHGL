package com.juxin.predestinate.module.local.msgview.chatview.msgpanel;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.chat.msgtype.GiftMessage;
import com.juxin.predestinate.module.local.chat.msgtype.PrivateMessage;
import com.juxin.predestinate.module.local.msgview.ChatAdapter;
import com.juxin.predestinate.module.local.msgview.chatview.ChatPanel;

/**
 * 私密消息展示panel（包含私密消息，私密图片，私密语音，私密视频）
 * Created by zm on 2017/6/27.
 */
public class ChatPanelPrivate extends ChatPanel {

    private LinearLayout llContainer;
    private ImageView ivSay;

    private ChatPanelPrivateCommon mChatPanelPrivateCommon;
    private ChatPanelPrivateAudio mChatPanelPrivateAudio;
    private ChatPanelPrivateVideo mChatPanelPrivateVideo;
    private ChatPanelPrivatePhoto mChatPanelPrivatePhoto;

    public ChatPanelPrivate(Context context, ChatAdapter.ChatInstance chatInstance, boolean sender) {
        super(context, chatInstance, R.layout.f1_chat_item_panel_private, sender);
        setShowParentLayout(false);
    }

    @Override
    public void initView() {
        llContainer = (LinearLayout) findViewById(R.id.rl_container);
        ivSay=(ImageView)findViewById(R.id.iv_say);
    }

    @Override
    public boolean reset(BaseMessage msgData, UserInfoLightweight infoLightweight) {
        if (msgData == null || !(msgData instanceof PrivateMessage))
            return false;

        PrivateMessage msg = (PrivateMessage) msgData;

        llContainer.removeAllViews();
        if (msg.getUnlocked() != 1 && msg.getfStatus() == 1) {//通用布局
            if (mChatPanelPrivateCommon == null) {
                mChatPanelPrivateCommon = new ChatPanelPrivateCommon(getContext(), getChatInstance(), infoLightweight);
            }
            llContainer.addView(mChatPanelPrivateCommon.getContentView());
            mChatPanelPrivateCommon.reSetData(msg);
            llContainer.setBackgroundResource(R.drawable.p1_invite_video_bg);
            ivSay.setImageResource(R.drawable.p1_chat_invite_tran);
            return true;
        } else {
            if (msg.getType() == BaseMessage.BaseMessageType.privateVideo.getMsgType()) {//视频
                if (mChatPanelPrivateVideo == null) {
                    mChatPanelPrivateVideo = new ChatPanelPrivateVideo(getContext());
                }
                llContainer.addView(mChatPanelPrivateVideo.getContentView());
                mChatPanelPrivateVideo.reSetData(msg);
                llContainer.setBackgroundResource(R.drawable.p1_invite_video_2_bg);
                ivSay.setImageResource(R.drawable.p1_chat_invite_say_video);
            } else if (msg.getType() == BaseMessage.BaseMessageType.privatePhoto.getMsgType()) {//照片
                if (mChatPanelPrivatePhoto == null) {
                    mChatPanelPrivatePhoto = new ChatPanelPrivatePhoto(getContext());
                }
                llContainer.addView(mChatPanelPrivatePhoto.getContentView());
                mChatPanelPrivatePhoto.reSetData(msg);
                llContainer.setBackgroundResource(R.drawable.p1_invite_pic_bg);
                ivSay.setImageResource(R.drawable.p1_chat_invite_say_pic);
            } else if (msg.getType() == BaseMessage.BaseMessageType.privateVoice.getMsgType()) {//语音及消息
                if (mChatPanelPrivateAudio == null) {
                    mChatPanelPrivateAudio = new ChatPanelPrivateAudio(getContext());
                }
                llContainer.addView(mChatPanelPrivateAudio.getContentView());
                mChatPanelPrivateAudio.reSetData(msg);

                if (!TextUtils.isEmpty(msg.getVoiceUrl())) { //私密语音

                    llContainer.setBackgroundResource(R.drawable.p1_invite_audio_bg);
                    ivSay.setImageResource(R.drawable.p1_chat_invite_say_audio);
                }else
                {
                    llContainer.setBackgroundResource(R.drawable.p1_invite_text_bg);
                    ivSay.setImageResource(R.drawable.p1_chat_invite_say_text);
                }

            }
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
}