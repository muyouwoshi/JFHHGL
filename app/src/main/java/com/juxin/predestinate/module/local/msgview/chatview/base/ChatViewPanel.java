package com.juxin.predestinate.module.local.msgview.chatview.base;

import android.content.Context;
import android.view.View;
import com.juxin.predestinate.module.local.msgview.ChatAdapter;
import com.juxin.predestinate.module.local.msgview.chatview.ChatBasePanel;

/**
 * Created by Kind on 2017/3/30.
 */

public class ChatViewPanel extends ChatBasePanel {

    public ChatViewPanel(Context context, ChatAdapter.ChatInstance chatInstance) {
        super(context, chatInstance);
    }

    /**
     * 控制当前控件组的显示状态。如果显示中则隐藏，否则显示。
     */
    public void showToggle() {
        if (contentView == null) {
            return;
        }

        show(!(contentView.isShown()));
    }

    /**
     * 控制当前控件组的显示状态。
     *
     * @param show 显示状态。
     */
    public void show(boolean show) {
        if (contentView == null) {
            return;
        }

        if (show) {
            contentView.setVisibility(View.VISIBLE);
        } else {
            contentView.setVisibility(View.GONE);
        }
    }

    /**
     * 控制当前控件组的显示状态。
     *
     * @param visibility 显示状态。
     */
    public void show(int visibility) {
        if (contentView == null) {
            return;
        }

        contentView.setVisibility(visibility);
    }
}
