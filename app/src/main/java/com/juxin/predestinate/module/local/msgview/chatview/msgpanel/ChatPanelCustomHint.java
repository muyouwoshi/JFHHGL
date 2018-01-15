package com.juxin.predestinate.module.local.msgview.chatview.msgpanel;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
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
 * 小提示消息
 * Created by Kind on 2017/5/23.
 */

public class ChatPanelCustomHint extends ChatPanel {

    private TextView text = null;
    private ViewGroup viewGroup = null;

    public ChatPanelCustomHint(Context context, ChatAdapter.ChatInstance chatInstance, boolean sender) {
        super(context, chatInstance, R.layout.f1_chat_item_panel_custom_simple_tip, sender);
    }

    @Override
    public void initView() {
        text = (TextView) findViewById(R.id.chat_item_text);
        viewGroup = (ViewGroup) findViewById(R.id.chat_item_layout);
    }

    @Override
    public boolean reset(BaseMessage msgData, UserInfoLightweight infoLightweight) {
        if (msgData == null || !(msgData instanceof TextMessage)) return false;

        TextMessage msg = (TextMessage) msgData;
        String channel_uid = "";
        if (infoLightweight != null) {
            channel_uid = String.valueOf(infoLightweight.getChannel_uid());
        }

        String hintContent = TextUtils.isEmpty(msg.getHtm()) ? msg.getMsgDesc() : msg.getHtm();
        if (TextUtils.isEmpty(hintContent)) {
            getContentView().setVisibility(View.GONE);
        }
        MyURLSpan.addClickToTextViewLink(App.getActivity(), text, hintContent, msg.getLWhisperID(), channel_uid);
        return true;
    }
}
