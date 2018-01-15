package com.juxin.predestinate.ui.user.auth;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.settting.ContactBean;
import com.juxin.predestinate.module.local.mail.MailSpecialID;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.util.UIShow;

/**
 * 手机认证完成页面
 * Created by xy on 2017/5/17.
 */

public class PhoneVerifyCompleteAct extends BaseActivity implements View.OnClickListener {
    private String qq;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f1_phoneverify_complete_act);
        setBackView(getResources().getString(R.string.title_phone_certification));
        initView();

    }

    private void initView() {
        findViewById(R.id.ll_customerservice_btn).setOnClickListener(this);
        findViewById(R.id.bt_return_authact).setOnClickListener(this);
        findViewById(R.id.ll_open_qq_btn).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_customerservice_desc)).setText(getResources().getString(R.string.txt_customerservice_complete_desc));

       ContactBean contactBean = ModuleMgr.getCommonMgr().getContactBean();
        if (contactBean == null) {
            return;
        }
        ((TextView) findViewById(R.id.tv_customerservice_phone)).setText(contactBean.getTel());
        ((TextView) findViewById(R.id.tv_customerservice_worktime)).setText("("+contactBean.getWork_time()+")");
        qq = contactBean.getQq();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_open_qq_btn://在线客服qq交流
                UIShow.showQQService(PhoneVerifyCompleteAct.this,qq);
                break;
            case R.id.bt_return_authact:
                setResult(203);
                finish();
                break;
            case R.id.ll_customerservice_btn:
                UIShow.showPrivateChatAct(PhoneVerifyCompleteAct.this, MailSpecialID.customerService.getSpecialID(),"");
                break;
            default:
                break;
        }
    }
}
