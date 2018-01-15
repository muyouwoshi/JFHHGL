package com.juxin.predestinate.ui.mail.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.chat.utils.MessageConstant;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.mail.item.MailMsgID;

/**
 * 标题，例如私密消息，加载更多
 * Created by Kind on 17/8/9.
 */
public class CustomTitleItem extends CustomBaseMailItem {

    private BaseMessage msgData;
    private TextView mail_item_title_name, mail_item_title_desc;

    public CustomTitleItem(Context context) {
        super(context);
    }

    public CustomTitleItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTitleItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init() {
        super.init(R.layout.p1_mail_item_title);
        mail_item_title_name = (TextView) findViewById(R.id.mail_item_title_name);
        mail_item_title_desc = (TextView) findViewById(R.id.mail_item_title_desc);

        findViewById(R.id.mail_item_title_btn).setOnClickListener(this);
    }

    @Override
    public void showGap() {
        super.showGap();
    }

    @Override
    public void showData(BaseMessage msgData) {
        this.msgData = msgData;
        mail_item_title_name.setText(msgData.getName());
        mail_item_title_desc.setText(msgData.getAboutme());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mail_item_title_btn://更多
                if (msgData == null) return;
                if (msgData.getLWhisperID() == MailMsgID.Private_Msg.type) {//私有消息
                    MsgMgr.getInstance().sendMsg(MsgType.MT_Chat_Mail_More, MessageConstant.Chat_Mail_Private);
                } else if (msgData.getLWhisperID() == MailMsgID.Stranger_Msg.type) {//陌生人
                    MsgMgr.getInstance().sendMsg(MsgType.MT_Chat_Mail_More, MessageConstant.Chat_Mail_Stranger);
                } else if (msgData.getLWhisperID() == MailMsgID.Invite_Msg.type) {//邀请消息
                    MsgMgr.getInstance().sendMsg(MsgType.MT_Chat_Mail_More, MessageConstant.Chat_Mail_Invite);
                } else if (msgData.getLWhisperID() == MailMsgID.Gift_Msg.type) {//礼物消息
                    MsgMgr.getInstance().sendMsg(MsgType.MT_Chat_Mail_More, MessageConstant.Chat_Mail_Gift);
                } else {
                    UIShow.showPrivateChatAct(getContext(), msgData.getLWhisperID(), msgData.getName(), msgData.getKfID());
                }
                break;
            default:
                break;
        }
    }
}