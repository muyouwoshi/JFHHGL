package com.juxin.predestinate.ui.user.my;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.juxin.library.log.PSP;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.my.ShareTypeData;
import com.juxin.predestinate.module.local.statistics.StatisticsShareUnlock;
import com.juxin.predestinate.module.local.statistics.StatisticsSpread;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.config.Hosts;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.user.util.CenterConstant;


/**
 * 提现成功
 * Created by LC on 2016/11/7.
 */

public class WithDrawSuccessAct extends BaseActivity implements View.OnClickListener {

    private Button btnSuccess;
    private TextView tvServiceQQ;
    private String money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f1_bind_card_success_act);
        initView();
    }

    private void initView() {
        setTitle(getString(R.string.withdraw_succeed));
        btnSuccess = (Button) findViewById(R.id.btn_success);
        tvServiceQQ = (TextView) findViewById(R.id.tv_service_qq);
        money = getIntent().getStringExtra("money");

//        UIShow.showQQService(this);
//        if (!TextUtils.isEmpty(QQ_Utils.getServiceQQ())) {
//            tvServiceQQ.setText(QQ_Utils.getServiceQQ());
//        }

        btnSuccess.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_success:
                ShareTypeData shareTypeData = new ShareTypeData(CenterConstant.SHARE_TYPE_SECOND, 0, "", money);
                PSP.getInstance().put(ModuleMgr.getCommonMgr().getPrivateKey(Constant.TEMP_SHARE_TYPE), CenterConstant.SHARE_TYPE_EARN);
                UIShow.showWebActivity(this, 3, Hosts.H5_SHARE_WANTMONEY + "?earnMoney=" +
                        (Double.valueOf(money) * 100), null, shareTypeData);
                StatisticsShareUnlock.onPage_Withdrawsuccess_share();
                this.finish();
                break;

            default:
                break;
        }
    }
}
