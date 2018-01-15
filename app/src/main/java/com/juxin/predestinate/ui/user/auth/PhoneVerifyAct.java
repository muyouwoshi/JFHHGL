package com.juxin.predestinate.ui.user.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.settting.ContactBean;
import com.juxin.predestinate.module.local.mail.MailSpecialID;
import com.juxin.predestinate.module.local.statistics.StatisticsUser;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.ChineseFilter;
import com.juxin.predestinate.module.util.UIShow;

import java.lang.Thread.State;
import java.lang.ref.WeakReference;

/**
 * 手机验证页面
 *
 * @author XY
 */
public class PhoneVerifyAct extends BaseActivity implements OnClickListener, RequestComplete {

    private EditText edtPhone, et_code;
    private Button bt_send_code, btnok;

    // byIQQ phone fare
    private String phone, code;
    private final MyHandler m_Handler = new MyHandler(this);
    private SendEnableThread sendthread = null;
    private String qq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f1_activity_phoneverify);
        setBackView(getResources().getString(R.string.title_phone_certification));
        initView();
    }

    private static class MyHandler extends Handler {
        private final WeakReference<PhoneVerifyAct> mActivity;

        MyHandler(PhoneVerifyAct act) {
            this.mActivity = new WeakReference<>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            PhoneVerifyAct act = mActivity.get();
            if (act != null) {
                if (msg.what > 0) {
                    act.bt_send_code.setText(String.format(act.getString(R.string.bt_send_code_press), msg.what));
                } else {
                    act.bt_send_code.setText(act.getResources().getString(R.string.bt_send_code_normal));
                    act.bt_send_code.setEnabled(true);
                }
            }
        }
    }

    private class SendEnableThread extends Thread {
        public void run() {
            int count = 60;
            while (count >= 0) {
                try {
                    Thread.sleep(1000);
                    m_Handler.sendEmptyMessage(count);
                } catch (Exception e) {
                    break;
                }
                count--;
            }
        }
    }

    private void initView() {
        TextView tv_customerservice_desc = (TextView) findViewById(R.id.tv_customerservice_desc);
        edtPhone = (EditText) this.findViewById(R.id.edt_phoneverify_phone);
        et_code = (EditText) this.findViewById(R.id.edt_phoneverify_note);
        bt_send_code = (Button) this.findViewById(R.id.btn_phoneverify_begin);
        btnok = (Button) this.findViewById(R.id.btn_phoneverify_ok);
        bt_send_code.setOnClickListener(this);
        btnok.setOnClickListener(this);
        findViewById(R.id.ll_customerservice_btn).setOnClickListener(this);
        findViewById(R.id.ll_open_qq_btn).setOnClickListener(this);
        ContactBean contactBean = ModuleMgr.getCommonMgr().getContactBean();
        if (contactBean == null) {
            return;
        }
        tv_customerservice_desc.setText(getResources().getString(R.string.txt_customerservice_bind_desc));
        ((TextView) findViewById(R.id.tv_customerservice_phone)).setText(contactBean.getTel());
        ((TextView) findViewById(R.id.tv_customerservice_worktime)).setText("(" + contactBean.getWork_time() + ")");
        qq = contactBean.getQq();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_open_qq_btn://在线客服qq交流
                UIShow.showQQService(PhoneVerifyAct.this, qq);
                break;
            case R.id.btn_phoneverify_begin:
                StatisticsUser.userPhoneVerify(edtPhone.getText().toString(), et_code.getText().toString(), false);

                if (validPhone()) {
                    LoadingDialog.show(this, getResources().getString(R.string.tip_loading_submit));
                    ModuleMgr.getCenterMgr().reqVerifyCodeEx(phone, this);
                }
                break;
            case R.id.btn_phoneverify_ok:
                StatisticsUser.userPhoneVerify(edtPhone.getText().toString(), et_code.getText().toString(), true);

                if (validInput()) {
                    LoadingDialog.show(this, getResources().getString(R.string.tip_loading_submit));
                    ModuleMgr.getCenterMgr().mobileAuthEx(phone, code, this);
                }
                break;
            case R.id.ll_customerservice_btn://客服小秘书
                UIShow.showPrivateChatAct(PhoneVerifyAct.this, MailSpecialID.customerService.getSpecialID(), "");
                break;
            default:
                break;
        }
    }

    private boolean validPhone() {
        phone = edtPhone.getText().toString();
        if ("".equals(phone)) {
            PToast.showShort(getResources().getString(R.string.toast_phone_isnull));
            return false;
        }
        if (!ChineseFilter.isPhoneNumber(phone)) {
            PToast.showShort(getResources().getString(R.string.toast_phone_iserror));
            return false;
        }
        return true;
    }

    private boolean validInput() {
        code = et_code.getText().toString();
        if (TextUtils.isEmpty(code)) {
            PToast.showShort(getResources().getString(R.string.toast_code_isnull));
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoadingDialog.closeLoadingDialog();
    }

    @Override
    public void onRequestComplete(HttpResponse response) {
        LoadingDialog.closeLoadingDialog();
        if (response.getUrlParam() == UrlParam.reqReqVerifyCode) {
            if (!response.isOk()) {
                PToast.showLong(response.getMsg());
                bt_send_code.setEnabled(true);
                return;
            }
            et_code.requestFocus();
            bt_send_code.setEnabled(false);
            btnok.setEnabled(true);
            if (sendthread == null || sendthread.getState() == State.TERMINATED) {
                sendthread = new SendEnableThread();
                sendthread.start();
            }
        } else if (response.getUrlParam() == UrlParam.mobileAuth) {
            PToast.showLong(response.getMsg());
            if (!response.isOk()) return;

            MsgMgr.getInstance().sendMsg(MsgType.MT_Update_MyInfo, null);
            ModuleMgr.getCenterMgr().getMyInfo().setVerifyCellphone(true);
            ModuleMgr.getCenterMgr().getMyInfo().setMobile(phone);
            UIShow.showPhoneVerifyCompleteAct(PhoneVerifyAct.this, MyAuthenticationAct.AUTHENTICSTION_REQUESTCODE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MyAuthenticationAct.AUTHENTICSTION_REQUESTCODE) {
            setResult(resultCode);
            finish();
        }
    }

}
