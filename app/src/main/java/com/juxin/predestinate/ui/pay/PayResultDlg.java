package com.juxin.predestinate.ui.pay;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.juxin.library.log.PSP;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.settting.ContactBean;
import com.juxin.predestinate.module.local.mail.MailSpecialID;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.user.util.CenterConstant;

/**
 * 订单确认
 * Created by siow on 2017/7/3.
 */

public class PayResultDlg extends BaseActivity implements View.OnClickListener {

    private View btn_ok;
    private int stat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stat = getIntent().getIntExtra("stat", 2);
        setContentView(stat == 1 ? R.layout.f1_pay_result_success : R.layout.f1_pay_result_fail);
        initView();
    }

    private void initView() {
        Window win = getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        win.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setAttributes(lp);

        setFinishOnTouchOutside(true);

        btn_ok = findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);

//        TextView tv1 = (TextView) findViewById(R.id.tv_text1_value);
//        if (stat == 1) {
//            if (PayConst.payGood != null) {
//                tv1.setText(PayConst.payGood.getPay_name());
//                tv2.setText(getString(R.string.pay_result_success_text2, PayConst.payGood.getPay_money()));
//            }
//        } else if (stat == 2) {
//            ContactBean contactBean = ModuleMgr.getCommonMgr().getContactBean();
//            if (contactBean != null) tv1.setText(contactBean.getTel());
//            tv2.setOnClickListener(this);
//        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ok:
                finish();
                break;
            case R.id.tv_text2_value:
                UIShow.showPrivateChatAct(App.getActivity(), MailSpecialID.customerService.getSpecialID(), null);
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //是否获得充值红包
        boolean isGetRechargeBag = PSP.getInstance().getBoolean(ModuleMgr.getCommonMgr().getPrivateKey(Constant.GET_RECHARGE_RED_BAG), false);
        if (isGetRechargeBag && (stat == 1)) {
            UIShow.showGetRedBagDlg(App.getActivity(), CenterConstant.TYPE_RECHARGE);
            PSP.getInstance().put(ModuleMgr.getCommonMgr().getPrivateKey(Constant.GET_RECHARGE_RED_BAG), false);
        }
    }

    @Override
    public boolean isFullScreen() {
        return true;
    }
}
