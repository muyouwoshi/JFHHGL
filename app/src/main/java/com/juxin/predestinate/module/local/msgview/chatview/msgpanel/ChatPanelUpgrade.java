package com.juxin.predestinate.module.local.msgview.chatview.msgpanel;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.update.UpgradeSource;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.chat.msgtype.MaxVersionMessage;
import com.juxin.predestinate.module.local.msgview.ChatAdapter;
import com.juxin.predestinate.module.local.msgview.chatview.ChatPanel;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;

/**
 * 升级展示
 * Created by Kind on 2017/5/12.
 */
public class ChatPanelUpgrade extends ChatPanel {

    public ChatPanelUpgrade(Context context, ChatAdapter.ChatInstance chatInstance, boolean sender) {
        super(context, chatInstance, R.layout.f1_chat_item_panel_upgrade, sender);
    }

    @Override
    public void initView() {
        findViewById(R.id.chat_item_text_upgrade).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ModuleMgr.getCommonMgr().checkUpdate((FragmentActivity) App.getActivity(), UpgradeSource.Chat, true);
            }
        });
    }

    @Override
    public boolean reset(BaseMessage msgData, UserInfoLightweight infoLightweight) {
        if (msgData == null || !(msgData instanceof MaxVersionMessage)) return false;
        return true;
    }
}
