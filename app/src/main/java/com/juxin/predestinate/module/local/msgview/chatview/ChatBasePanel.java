package com.juxin.predestinate.module.local.msgview.chatview;

import android.content.Context;

import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.module.local.msgview.ChatAdapter;

/**
 * Created by Kind on 2017/3/30.
 */
public class ChatBasePanel extends BasePanel {

    private ChatAdapter.ChatInstance chatInstance = null;

    public ChatBasePanel(Context context, ChatAdapter.ChatInstance chatInstance) {
        super(context);
        this.chatInstance = chatInstance;
    }

    public ChatAdapter.ChatInstance getChatInstance() {
        return chatInstance;
    }

    public void setChatInstance(ChatAdapter.ChatInstance chatInstance) {
        this.chatInstance = chatInstance;
    }
}
