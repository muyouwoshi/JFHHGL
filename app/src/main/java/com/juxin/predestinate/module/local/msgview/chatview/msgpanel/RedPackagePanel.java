package com.juxin.predestinate.module.local.msgview.chatview.msgpanel;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;
import com.juxin.predestinate.module.local.msgview.ChatAdapter;
import com.juxin.predestinate.module.local.msgview.chatview.ChatPanel;
import com.juxin.predestinate.module.local.statistics.StatisticsMessage;
import com.juxin.predestinate.module.logic.application.App;

public class RedPackagePanel extends ChatPanel {

    private View ivLeft, ivRight, layoutLeft, layoutRight, layoutContent;

    public RedPackagePanel(Context context, ChatAdapter.ChatInstance chatInstance, boolean sender) {
        super(context, chatInstance, R.layout.abtest_chat_message_red_package, sender);
    }

    @Override
    public void initView() {
        ivLeft = contentView.findViewById(R.id.iv_left);
        ivRight = contentView.findViewById(R.id.iv_right);
        layoutContent = contentView.findViewById(R.id.layout_content);
        layoutLeft = contentView.findViewById(R.id.layout_left);
        layoutRight = contentView.findViewById(R.id.layout_right);
    }

    @Override
    public boolean reset(BaseMessage msgData, UserInfoLightweight infoLightweight) {
        if (isSender()) {
            ivLeft.setVisibility(View.INVISIBLE);
            layoutLeft.setVisibility(View.GONE);
            ivRight.setVisibility(View.VISIBLE);
            layoutRight.setVisibility(View.VISIBLE);
        } else {
            ivRight.setVisibility(View.INVISIBLE);
            layoutRight.setVisibility(View.GONE);
            ivLeft.setVisibility(View.VISIBLE);
            layoutLeft.setVisibility(View.VISIBLE);
        }
        return true;
    }

    @Override
    public boolean isShowParentLayout() {
        return false;
    }

    @Override
    public boolean onClickContent(BaseMessage msgData, boolean longClick) {
        if (longClick) {
            return false;
        }
        StatisticsMessage.abTestRedBagMsg(msgData.getLWhisperID());
        if (App.activity != null && App.activity instanceof FragmentActivity) {
//            ABTestBottomDialog dialog = new ABTestBottomDialog((FragmentActivity) App.activity);
//            dialog.showGetRedPackage(isSender(), msgData);
        }
        return true;
    }
}
