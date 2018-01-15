package com.juxin.predestinate.ui.user.my;

import android.os.Bundle;
import android.view.View;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.mail.MailSpecialID;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.util.UIShow;

/**
 * 创建日期：2017/4/20
 * 描述: 提现说明
 * 作者:zm
 */
public class WithDrawExplainAct extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f1_wode_with_draw_explain_act);
        initView();
    }

    private void initView() {
        setBackView(R.id.base_title_back);
        setTitle(getString(R.string.withdrawal_instructions),R.color.black);
        findViewById(R.id.ll_customerservice_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_customerservice_btn:
                UIShow.showPrivateChatAct(this, MailSpecialID.customerService.getSpecialID(), "");
                break;

            default:
                break;
        }
    }
}