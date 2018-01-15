package com.juxin.predestinate.module.local.msgview.chatview.msgpanel;

import android.content.Context;
import android.widget.TextView;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.chat.msgtype.TextMessage;
import com.juxin.predestinate.module.local.msgview.ChatAdapter;
import com.juxin.predestinate.module.local.msgview.chatview.ChatPanel;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.ui.utils.MyURLSpan;

/**
 * 文字及html文字类型消息展示panel
 * Created by Kind on 2017/5/12.
 */
public class ChatPanelText extends ChatPanel {

    private TextView chat_item_text;

    public ChatPanelText(Context context, ChatAdapter.ChatInstance chatInstance, boolean sender) {
        super(context, chatInstance, R.layout.f1_chat_item_panel_text, sender);
    }

    @Override
    public void initView() {
        chat_item_text = (TextView) findViewById(R.id.chat_item_text);
    }

    @Override
    public boolean reset(BaseMessage msgData, UserInfoLightweight infoLightweight) {
        if (msgData == null || !(msgData instanceof TextMessage)) return false;
        TextMessage msg = (TextMessage) msgData;

        String channel_uid = "";
        if (infoLightweight != null) {
            channel_uid = String.valueOf(infoLightweight.getChannel_uid());
        }

        String str;
        if(msg.getType() == BaseMessage.BaseMessageType.htmlText.getMsgType()
                || msg.getType() == BaseMessage.BaseMessageType.autoUpdateHtml.getMsgType()){
            str = msg.getHtm();
        }else {
            str = msg.getMsgDesc();
        }

        MyURLSpan.addClickToTextViewLink(App.getActivity(), chat_item_text, str, msg.getLWhisperID(), channel_uid);
        return true;
    }
}
