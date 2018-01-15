package com.juxin.predestinate.module.local.chat.msgtype;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.util.UIUtil;

/**
 * 显示宽度常量类
 * 各种消息在聊天界面显示时的边距默认是80
 * Created by Kind on 15/10/24.
 */
public class DisplayWidth {

    private static int WIDTH_DEFAULT = -1;//默认
    private static int WIDTH_ZERO = 0;

    public static int getDisplayWidth() {
        if (WIDTH_DEFAULT == -1) {
            WIDTH_DEFAULT = UIUtil.px2dp(App.getActivity().getResources().getDimension(R.dimen.px158_dp));
        }
        return WIDTH_DEFAULT;
    }

    public static int getDisplayWidthZero() {
        return WIDTH_ZERO;
    }
}
