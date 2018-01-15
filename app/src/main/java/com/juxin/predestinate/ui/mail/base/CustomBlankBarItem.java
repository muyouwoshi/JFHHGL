package com.juxin.predestinate.ui.mail.base;

import android.content.Context;
import android.util.AttributeSet;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.chat.msgtype.BaseMessage;

/**
 * 空白条
 * Created by Kind on 17/8/9.
 */
public class CustomBlankBarItem extends CustomBaseMailItem {

    public CustomBlankBarItem(Context context) {
        super(context);
    }

    public CustomBlankBarItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomBlankBarItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init() {
        super.init(R.layout.common_gap_item_item);
    }

    @Override
    public void showData(BaseMessage msgData) {
    }
}