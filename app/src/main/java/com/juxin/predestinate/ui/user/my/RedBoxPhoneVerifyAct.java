package com.juxin.predestinate.ui.user.my;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.juxin.library.log.PToast;
import com.juxin.library.utils.NetworkUtils;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.my.PhoneVerifyInfo;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.BaseUtil;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.module.util.my.CountdownUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 手机验证
 * Created by zm on 2017/4/27
 */
public class RedBoxPhoneVerifyAct extends BaseActivity implements View.OnClickListener, RequestComplete, CountdownUtil.TimeListener {

    private EditText edtPhoneNum;
    private EditText edtPhoneCode;
    private Button btTakePhoneCode;
    private Button btnPhoneverifyNext;
    private String phoneNum;
    private CountdownUtil mCountdownUtil;


    private int authIDCard = 105;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f1_red_box_phone_verify_act);
        initView();
    }

    private void initView() {
        setBackView(R.id.base_title_back);
        setTitle(getString(R.string.phone_verify));

        edtPhoneNum = (EditText) findViewById(R.id.phone_verify_edt_phone_num);
        edtPhoneCode = (EditText) findViewById(R.id.phone_verify_edt_phone_code);
        btTakePhoneCode = (Button) findViewById(R.id.phone_verify_bt_take_phone_code);
        btnPhoneverifyNext = (Button) findViewById(R.id.phone_verify_btn_phoneverify_next);
        btTakePhoneCode.setOnClickListener(this);
        btnPhoneverifyNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.phone_verify_bt_take_phone_code://获取验证码
                reqPhoneCode();
                break;
            case R.id.phone_verify_btn_phoneverify_next://下一步
                checkCode();
                break;
            default:
                break;
        }
    }

    //判断是否是手机号
    public static boolean isPhoneNum(String phoneNum) {
        Pattern pattern = Pattern.compile("^((13[0-9])|(14[5,7,9])|(15[^4,\\D])|(17[0,1,3,5-8])|(18[0-9]))\\d{8}$");
        Matcher matcher = pattern.matcher(phoneNum);
        return matcher.matches();
    }

    /**
     * 获取手机验证码p
     */
    private void reqPhoneCode() {
        if (!NetworkUtils.isConnected(RedBoxPhoneVerifyAct.this)) {//无网络
            return;
        }
        phoneNum = edtPhoneNum.getText().toString().trim();
        if (phoneOrCodeIsEmpty(phoneNum)) return;
        LoadingDialog.show(this, getString(R.string.tip_loading_submit));
        ModuleMgr.getCommonMgr().sendSMS(phoneNum, this);
        btnPhoneverifyNext.setEnabled(false);
    }

    private boolean phoneOrCodeIsEmpty(String phoneNum) {
        if (TextUtils.isEmpty(phoneNum)) {
            PToast.showShort(getString(R.string.toast_phone_isnull));
            return true;
        }
        if (!BaseUtil.isNumeric(phoneNum) || phoneNum.length() != 11) {
            PToast.showShort(getString(R.string.toast_phone_iserror));
            return true;
        }
        return false;
    }

    /**
     * 点击下一步，验证
     */
    private void checkCode() {
        phoneNum = edtPhoneNum.getText().toString().trim();
        String code = edtPhoneCode.getText().toString().trim();

        if (phoneOrCodeIsEmpty(phoneNum)) return;

        if (TextUtils.isEmpty(code)) {
            PToast.showShort(getString(R.string.toast_code_isnull));
            return;
        }
        LoadingDialog.show(this, getString(R.string.tip_loading_submit));
        ModuleMgr.getCommonMgr().bindCellPhone(code, phoneNum, this);
    }

    @Override
    public void onRequestComplete(HttpResponse response) {
//        Log.e("TTTTTTTTMMM", response.getResponseString() + "||");
        LoadingDialog.closeLoadingDialog();
        btTakePhoneCode.setEnabled(true);
        PhoneVerifyInfo info = new PhoneVerifyInfo();
        info.parseJson(response.getResponseString());

        if (response.getUrlParam() == UrlParam.sendSMS) {
            switch (info.getResCode()) {
                case PhoneVerifyInfo.VERIFICATIONCODE_ERROR:
                    PToast.showLong(response.getMsg() + "");
                    break;
                case PhoneVerifyInfo.VERIFICATIONCODE_SUCCEED:
                    btnPhoneverifyNext.setEnabled(true);
                    //计时
                    if (mCountdownUtil == null) {
                        mCountdownUtil = new CountdownUtil();
                        mCountdownUtil.setOnTimeListener(RedBoxPhoneVerifyAct.this).start();
                        break;
                    }
                    if (mCountdownUtil != null && !mCountdownUtil.isAlive()) {
                        mCountdownUtil = new CountdownUtil();
                        mCountdownUtil.setOnTimeListener(RedBoxPhoneVerifyAct.this).start();
                        break;
                    }
                    break;
                case PhoneVerifyInfo.PHONE_INKED:
                    PToast.showLong("您的帐号已与手机绑定，不能再绑定");
                    this.finish();
                    break;
                case PhoneVerifyInfo.OTHER_INKED:
                    PToast.showLong("该手机已经被验证过了，请输入其他的手机号");
                    break;
            }
            return;
        }
        if (response.isOk()) {
            btTakePhoneCode.setEnabled(true);
            switch (info.getResCode()) {
                case PhoneVerifyInfo.VERIFICATIONCODE_ERROR:
                    PToast.showLong(getString(R.string.toast_code_error));
                    break;
                case PhoneVerifyInfo.VERIFICATIONCODE_SUCCEED:
                    PToast.showLong(getString(R.string.toast_code_succeed));
                    ModuleMgr.getCenterMgr().getMyInfo().setVerifyCellphone(true);
                    ModuleMgr.getCenterMgr().getMyInfo().setMobile(phoneNum);
                    int status = ModuleMgr.getCommonMgr().getIdCardVerifyStatusInfo().getStatus();
                    if (status <= 0) {//是否进行了身份认证
                        UIShow.showIDCardAuthenticationAct(this, authIDCard);
                        finish();
                        break;
                    }
                    if (status != 2) {//身份认证还未通过
                        PToast.showShort(R.string.the_identity_certification_audit);
                        finish();
                        break;
                    }
                    UIShow.showWithDrawApplyAct(0, 0, false, 0, this);
                    finish();
                    break;
                case PhoneVerifyInfo.PHONE_INKED:
                    PToast.showLong(getString(R.string.please_send_verification_code));
                    break;
                case PhoneVerifyInfo.OTHER_INKED:
                    PToast.showLong(getString(R.string.toast_code_error));
                    break;
            }
            return;
        }
        PToast.showLong(getString(R.string.net_error_retry));
    }

    @Override
    public void onTimeListener(final int time) {
        if (btTakePhoneCode != null) {
            RedBoxPhoneVerifyAct.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (time > 0) {
                        btTakePhoneCode.setText(getString(R.string.resend) + time + getString(R.string.seconds));
                        btTakePhoneCode.setEnabled(false);
                    } else {
                        btTakePhoneCode.setText(getString(R.string.bt_send_code_normal));
                        btTakePhoneCode.setEnabled(true);
                        btnPhoneverifyNext.setEnabled(true);
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountdownUtil != null && mCountdownUtil.isAlive()) {//退出时停止计时
            mCountdownUtil.setOnTimeListener(null);
            mCountdownUtil.setStop(true);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}