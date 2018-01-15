package com.juxin.predestinate.module.local.msgview.chatview.msgpanel;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.juxin.library.image.ImageLoader;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.chat.msgtype.SysNoticeMessage;
import com.juxin.predestinate.module.local.msgview.ChatAdapter;
import com.juxin.predestinate.module.local.msgview.chatview.ChatPanel;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.ui.utils.CMDJumpUtil;

/**
 * 系统消息通知
 * Created by Kind on 2017/6/14.
 */
public class ChatPanelSysNotice extends ChatPanel {

    private TextView tvTitle, tvContent, tvJump;
    private ImageView imgPic;

    public ChatPanelSysNotice(Context context, ChatAdapter.ChatInstance chatInstance, boolean sender) {
        super(context, chatInstance, R.layout.f1_chat_item_panel_sysnotice, sender);
    }

    @Override
    public void initView() {
        tvTitle = (TextView) findViewById(R.id.sys_mess_item_tv_title);
        tvContent = (TextView) findViewById(R.id.sys_mess_item_tv_content);
        tvJump = (TextView) findViewById(R.id.sys_mess_item_tv_jump);
        imgPic = (ImageView) findViewById(R.id.sys_mess_item_img_pic);
    }

    @Override
    public boolean reset(final BaseMessage msgData, UserInfoLightweight infoLightweight) {
        if (msgData == null || !(msgData instanceof SysNoticeMessage)) {
            return false;
        }

        final SysNoticeMessage noticeMessage = (SysNoticeMessage) msgData;

        tvTitle.setText(noticeMessage.getMsgDesc());
        tvContent.setText(noticeMessage.getInfo());
        if (!TextUtils.isEmpty(noticeMessage.getPic())) {
            imgPic.setVisibility(View.VISIBLE);
            imgPic.setOnClickListener(new CustomOnClick(noticeMessage));
            ImageLoader.loadCenterCrop(getContext(), noticeMessage.getPic(), imgPic, 0, 0);
            tvJump.setTextColor(ContextCompat.getColor(getContext(), R.color.color_zhuyao));
        } else {
            imgPic.setVisibility(View.GONE);
            tvJump.setTextColor(ContextCompat.getColor(getContext(), R.color.color_45A3EC));
        }

        tvJump.setText(noticeMessage.getBtn_text());
        tvJump.setOnClickListener(new CustomOnClick(noticeMessage));
        tvContent.setOnClickListener(new CustomOnClick(noticeMessage));
        getContentView().setOnClickListener(new CustomOnClick(noticeMessage));
        return true;
    }

    private class CustomOnClick implements View.OnClickListener {
        private SysNoticeMessage noticeMessage;

        CustomOnClick(SysNoticeMessage noticeMessage) {
            this.noticeMessage = noticeMessage;
        }

        @Override
        public void onClick(View v) {
            new CMDJumpUtil(App.getActivity(), noticeMessage.getBtn_action(), noticeMessage.getLWhisperID(), null).onCMD();
        }
    }
}