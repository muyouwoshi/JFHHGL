package com.juxin.predestinate.ui.setting;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.settting.ContactBean;
import com.juxin.predestinate.module.local.mail.MailSpecialID;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.util.UIShow;

/**
 * 关于页面
 */
public class AboutAct extends BaseActivity implements OnClickListener {

    private String qq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f1_activity_about);
        setBackView(getResources().getString(R.string.title_about_us));
        initView();
    }

    private void initView() {
        String email = getResources().getString(R.string.txt_email);
        TextView tv_email = (TextView) this.findViewById(R.id.txt_about_email);
        TextView tv_ver = (TextView) this.findViewById(R.id.txt_about_vers);

        View img_logo = this.findViewById(R.id.img_logo);
        img_logo.setOnClickListener(this);
        tv_ver.setText("V" + ModuleMgr.getAppMgr().getVerName());
        tv_email.setText(email);

        findViewById(R.id.ll_open_qq_btn).setOnClickListener(this);
        ContactBean contactBean = ModuleMgr.getCommonMgr().getContactBean();
        if (contactBean == null) {
            return;
        }
        ((TextView) findViewById(R.id.tv_customerservice_phone)).setText(contactBean.getTel());
        ((TextView) findViewById(R.id.tv_customerservice_worktime)).setText("(" + contactBean.getWork_time() + ")");
        findViewById(R.id.ll_customerservice_btn).setOnClickListener(this);
        qq = contactBean.getQq();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_open_qq_btn://在线客服qq交流
                UIShow.showQQService(AboutAct.this, qq);
                break;
            case R.id.ll_customerservice_btn:
                Statistics.userBehavior(SendPoint.menu_me_setting_about_kefuservice);
                UIShow.showPrivateChatAct(AboutAct.this, MailSpecialID.customerService.getSpecialID(), "");
                break;
            default:
                break;
        }
    }
}