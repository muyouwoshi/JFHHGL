package com.juxin.predestinate.ui.live.util;

import android.support.v4.app.FragmentActivity;

import com.juxin.predestinate.ui.live.ui.LiveAuthDialog;

/**
 * DialogUtil (不从UIShow里面写了，从这个类写)
 * <p>
 * Created by chengxiaobo on 2017/11/1.
 */

public class DialogUtil {

    public static void ShowAuthDialog(FragmentActivity activity, LiveAuthDialog.Click click) {

        LiveAuthDialog dialog = new LiveAuthDialog();
        dialog.setClick(click);
        dialog.showDialog(activity);
    }
}
