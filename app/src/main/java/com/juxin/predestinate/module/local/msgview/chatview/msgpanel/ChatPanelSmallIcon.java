package com.juxin.predestinate.module.local.msgview.chatview.msgpanel;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.juxin.library.image.ImageLoader;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.chat.msgtype.SmallIconMessage;
import com.juxin.predestinate.module.local.msgview.ChatAdapter;
import com.juxin.predestinate.module.local.msgview.chatview.ChatPanel;
import com.juxin.predestinate.module.logic.invoke.Invoker;

/**
 * 密友任务
 * Created by Kind on 2017/7/12.
 */

public class ChatPanelSmallIcon extends ChatPanel implements View.OnClickListener {

    private ImageView small_icon_icon;
    private TextView small_icon_title, small_icon_desc, small_icon_btn;
    private SmallIconMessage smallIconMessage;

    public ChatPanelSmallIcon(Context context, ChatAdapter.ChatInstance chatInstance, boolean sender) {
        super(context, chatInstance, R.layout.f2_chat_item_panel_small_icon, sender);
    }

    @Override
    public void initView() {
        small_icon_icon = (ImageView) findViewById(R.id.small_icon_icon);
        small_icon_title = (TextView) findViewById(R.id.small_icon_title);
        small_icon_desc = (TextView) findViewById(R.id.small_icon_desc);
        small_icon_btn = (TextView) findViewById(R.id.small_icon_btn);
        small_icon_btn.setOnClickListener(this);
    }

    @Override
    public boolean reset(BaseMessage msgData, UserInfoLightweight infoLightweight) {
        if (msgData == null || !(msgData instanceof SmallIconMessage)) return false;

        smallIconMessage = (SmallIconMessage) msgData;
        ImageLoader.loadFitCenter(context, smallIconMessage.getIcon(), small_icon_icon);

        small_icon_title.setText(smallIconMessage.getMsgDesc());
        small_icon_desc.setText(smallIconMessage.getInfo());
        small_icon_btn.setText(smallIconMessage.getBtn_text());

        return false;
    }

    @Override
    public void onClick(View v) {
        if(R.id.small_icon_btn == v.getId() && getSmallIconMessage() != null){
            Invoker.getInstance().doInApp(null, getSmallIconMessage().getBtn_action(), null);
        }
    }

    public SmallIconMessage getSmallIconMessage() {
        return smallIconMessage;
    }
}
