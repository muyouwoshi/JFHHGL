package com.juxin.predestinate.ui.mail.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.mail.item.MailMsgID;

/**
 * 加载更多
 * Created by Kind on 17/8/9.
 */
public class CustomBottomItem extends CustomBaseMailItem {

    private BaseMessage msgData = null;

    public CustomBottomItem(Context context) {
        super(context);
    }

    public CustomBottomItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomBottomItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init() {
        super.init(R.layout.p1_mail_item_bottom);
        findViewById(R.id.mail_item_title_more).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msgData == null) return;
                if (msgData.getLWhisperID() == MailMsgID.Invite_More_Msg.type) {//邀请消息
                    UIShow.showChatInviteAct(getContext());
                } else if (msgData.getLWhisperID() == MailMsgID.Private_More_Msg.type) {//私有消息
                    UIShow.showChatSecretAct(getContext());
                } else if (msgData.getLWhisperID() == MailMsgID.Stranger_More_Msg.type) {
                    UIShow.showChatStrangerAct(getContext());
                } else if (msgData.getLWhisperID() == MailMsgID.Gift_More_Msg.type) {
                    UIShow.showGiftFromAct(getContext());
                }
            }
        });
    }

    @Override
    public void showData(BaseMessage msgData) {
        this.msgData = msgData;
    }
}