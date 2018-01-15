package com.juxin.predestinate.ui.pay;

import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.pay.goods.PayGood;
import com.juxin.predestinate.module.util.UIShow;

/**
 * Created by Kind on 2017/4/19.
 */

public class PayAlipayWebPannel extends BasePayPannel {


    public PayAlipayWebPannel(FragmentActivity activity, PayGood payGood) {
        super(activity, payGood);
        setImageIcon(R.drawable.y2_paytype_credit_card);
        setTextTitle("信用卡支付");
        setTextRemark("支持全国60多家信用卡机构，免网银");
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        UIShow.showPayWebAct(getActivity(), getPayGood());
    }
}
