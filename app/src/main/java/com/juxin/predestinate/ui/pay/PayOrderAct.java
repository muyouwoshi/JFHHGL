package com.juxin.predestinate.ui.pay;

import android.content.Intent;
import android.os.Bundle;

import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.util.UIShow;

/**
 * 支付前等待的透明窗体
 * Created by siow
 */

public class PayOrderAct extends BaseActivity {
    private boolean pauseFlag;
    PayCompleteCheckMgr payCompleteCheckMgr = new PayCompleteCheckMgr(this, true);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        UIShow.showPay(this, bundle.getInt("commodity_Id"), bundle.getInt("payType"),
                bundle.getLong("uid"), bundle.getString("channel_uid"));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        super.onNewIntent(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseFlag = true;
        payCompleteCheckMgr.cancelTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        payCompleteCheckMgr.cancelTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pauseFlag) {
            pauseFlag = false;
            payCompleteCheckMgr.startTimer();
        }
    }
}
