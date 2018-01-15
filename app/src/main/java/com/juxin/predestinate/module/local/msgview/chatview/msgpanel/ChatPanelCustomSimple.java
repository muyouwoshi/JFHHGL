package com.juxin.predestinate.module.local.msgview.chatview.msgpanel;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.msgview.ChatAdapter;
import com.juxin.predestinate.module.local.msgview.chatview.ChatPanel;

/**
 * 只处理客户端自定义消息类型。
 */
public class ChatPanelCustomSimple extends ChatPanel {
    private TextView text = null;
    private ViewGroup viewGroup = null;

    public ChatPanelCustomSimple(Context context, ChatAdapter.ChatInstance chatInstance, boolean sender) {
        super(context, chatInstance, R.layout.p1_chat_item_panel_custom_simple, sender);
    }

    @Override
    public void initView() {
        text = (TextView) findViewById(R.id.chat_item_text);
        viewGroup = (ViewGroup) findViewById(R.id.chat_item_layout);
    }

    @Override
    public boolean reset(BaseMessage msgData, UserInfoLightweight infoLightweight) {
//        if (msgData == null || !(msgData instanceof CustomMessage)) {
//            return false;
//        }
//
//        CustomMessage msg = (CustomMessage) msgData;
//        text.setText(msg.getContent());
//        viewGroup.removeAllViews();
//
//        if (msg.eggMsg) {
//            viewGroup.addView(EggUtil.getInstance().getEggView());
//            viewGroup.setVisibility(View.VISIBLE);
//        }

        return true;
    }
}
