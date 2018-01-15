package com.juxin.predestinate.ui.pay;

import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.juxin.predestinate.module.local.pay.goods.PayGood;
import com.juxin.predestinate.module.util.UIShow;

/**
 * Created by siow on 2017/7/4.
 */

public class PayConst {
    public static boolean payFlag;
    public static PayGood payGood;
    public static String out_trade_no;

    public static void checkPayComplete(FragmentActivity activity) {
        if (PayConst.payFlag && payGood != null && !TextUtils.isEmpty(out_trade_no)) {
            PayConst.payFlag = false;
            UIShow.showRefreshDlg(activity);
        }
    }
}
