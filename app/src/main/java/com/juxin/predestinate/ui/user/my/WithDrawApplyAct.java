package com.juxin.predestinate.ui.user.my;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.juxin.library.log.PSP;
import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.library.utils.NetworkUtils;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.bean.my.WithdrawAddressInfo;
import com.juxin.predestinate.module.util.UIUtil;
import com.juxin.predestinate.ui.utils.NoDoubleClickListener;

/**
 * 红包提现--银行卡界面
 * Created by zm on 2017/4/20
 */
public class WithDrawApplyAct extends BaseActivity implements RequestComplete, View.OnClickListener {

    private String mEidtMoney = "0";
    private boolean mIsFromEdit;

    private LinearLayout llOpenBank, llBankBranch, llName;
    private EditText etMoney;
    private EditText etCardName;
    private EditText etCardLocal;
    private EditText etCardLocalBranch;
    private EditText etCardNum;
    private View viewBank, viewBankBranch;
    private TextView tvOpenBank;
    private Button btnNext;
    private WithdrawAddressInfo info;
    private RadioButton rbZhi;
    private RadioButton rbYin;
    private String cardName = "", cardLocal = "", cardLocalBranch = "", cardNum = "", carZhi = "";
    private int status; //提现状态
    private int id;     //提现id

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f1_wode_bank_card_act);
        mIsFromEdit = getIntent().getBooleanExtra("fromEdit", false);
        info = getIntent().getParcelableExtra("info");
        status = getIntent().getIntExtra("status", 0);
        id = getIntent().getIntExtra("id", 0);
        mEidtMoney = String.valueOf(getIntent().getDoubleExtra("money", 0));
        initView();
    }

    private void initView() {
        setBackView(R.id.base_title_back);
        setTitle(getString(R.string.withdrawal_page));

        llOpenBank = (LinearLayout) findViewById(R.id.bank_card_ll_open_bank);
        llBankBranch = (LinearLayout) findViewById(R.id.bank_card_ll_open_bank_branch);
        etMoney = (EditText) findViewById(R.id.bank_card_et_money);
        etCardName = (EditText) findViewById(R.id.bank_card_et_card_name);
        etCardLocal = (EditText) findViewById(R.id.bank_card_et_card_local);
        etCardLocalBranch = (EditText) findViewById(R.id.bank_card_et_card_local_branch);
        etCardNum = (EditText) findViewById(R.id.bank_card_et_card_num);
        viewBank = findViewById(R.id.bank_card_view_bank);
        viewBankBranch = findViewById(R.id.bank_card_view_bank_branch);
        btnNext = (Button) findViewById(R.id.bank_card_btn_next);
        tvOpenBank = (TextView) findViewById(R.id.bank_card_tv_card);
        rbZhi = (RadioButton) findViewById(R.id.id_card_rb_zhi);
        rbYin = (RadioButton) findViewById(R.id.id_card_rb_yin);
        llName = (LinearLayout) findViewById(R.id.bank_card_ll_name);
        rbZhi.setOnClickListener(this);
        rbYin.setOnClickListener(this);
        setBg();
        cardName = info.getAccountname();
        cardLocal = info.getBank();
        cardLocalBranch = info.getSubbank();
        if (info.getPaytype() == 2) {
            carZhi = info.getAccountnum();
        } else {
            cardNum = info.getAccountnum();
        }
        if (status != 3) {
            mEidtMoney = PSP.getInstance().getFloat(RedBoxRecordAct.REDBOXMONEY + ModuleMgr.getCenterMgr().getMyInfo().getUid(), 0) + "";
        }
        etMoney.setText(mEidtMoney + getString(R.string.head_unit));
        btnNext.setOnClickListener(clickListener);
        initDefaultAddress();

    }

    private void initDefaultAddress() {
        if (!TextUtils.isEmpty(info.getAccountnum()) && status != 3) {
            rbYin.setClickable(false);
            rbZhi.setClickable(false);
            etCardLocal.setClickable(false);
            etCardLocalBranch.setClickable(false);
            etCardNum.setClickable(false);
            etCardName.setClickable(false);

            etCardLocal.setFocusable(false);
            etCardLocalBranch.setFocusable(false);
            etCardNum.setFocusable(false);
            etCardName.setFocusable(false);
            rbZhi.setChecked(true);
            rbYin.setChecked(false);
        }
        if (info.getPaytype() == 2) {
            cardName = etCardName.getText().toString();
            cardLocal = etCardLocal.getText().toString();
            cardLocalBranch = etCardLocalBranch.getText().toString();
            llOpenBank.setVisibility(View.GONE);
            llBankBranch.setVisibility(View.GONE);
            viewBank.setVisibility(View.GONE);
            viewBankBranch.setVisibility(View.GONE);
            llName.setVisibility(View.GONE);
            etCardNum.setInputType(InputType.TYPE_CLASS_TEXT);
            etCardNum.setFilters(new InputFilter[]{new InputFilter.LengthFilter(26)});
            etCardNum.setHint(R.string.input_your_zhifubao_id);
            tvOpenBank.setText(getString(R.string.zhi_fu_id));
            etCardNum.setText(carZhi);
            etCardNum.setSelection(etCardNum.getText() != null ? etCardNum.getText().length() : 0);
            rbZhi.setChecked(true);
        } else if (info.getPaytype() == 1) {
            llOpenBank.setVisibility(View.VISIBLE);
            llBankBranch.setVisibility(View.VISIBLE);
            viewBank.setVisibility(View.VISIBLE);
            viewBankBranch.setVisibility(View.VISIBLE);
            llName.setVisibility(View.VISIBLE);
            etCardNum.setInputType(InputType.TYPE_CLASS_NUMBER);
            etCardNum.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
            etCardNum.setFilters(new InputFilter[]{new InputFilter.LengthFilter(21)});
            etCardNum.setHint(R.string.input_your_bank_card_id);
            etCardName.setText(cardName);
            etCardName.setSelection(etCardName.getText() != null ? etCardName.getText().length() : 0);
            etCardLocal.setText(cardLocal);
            etCardLocal.setSelection(etCardLocal.getText() != null ? etCardLocal.getText().length() : 0);
            etCardNum.setText(cardNum);
            etCardNum.setSelection(etCardNum.getText() != null ? etCardNum.getText().length() : 0);
            etCardLocalBranch.setText(cardLocalBranch);
            etCardLocalBranch.setSelection(etCardLocalBranch.getText() != null ? etCardLocalBranch.getText().length() : 0);
            tvOpenBank.setText(getString(R.string.bank_id));
            etCardLocal.requestFocus();
            if (!TextUtils.isEmpty(info.getAccountnum())) {
                rbYin.setChecked(true);
                rbZhi.setChecked(false);
            }
        }
    }

    //银行卡号校验
    public boolean bankCardForm(String num) {
        String reg = "^\\d{16}$|^\\d{18,21}$";
        if (!num.matches(reg)) {
            System.out.println("Format Error!");
            return false;
        }
        return true;
    }

//    @Override
//    public void onClick(View v) {
//        if (!(NetworkUtils.isConnected(this))) {
//            PToast.showShort(getResources().getString(R.string.tip_net_error));
//            return;
//        }
//        switch (v.getId()) {
//            case R.id.bank_card_btn_next:
////                String cardName = etCardName.getText().toString().trim();
////                String cardLocal = etCardLocal.getText().toString().trim();
////                String cardLocalBranch = etCardLocalBranch.getText().toString().trim();
////                String cardNum = etCardNum.getText().toString().trim();
////                if(mIsFromEdit) {
////                    if(TextUtils.isEmpty(cardName)) {
////                        PToast.showShort(getString(R.string.name_cannot_be_empty));
////                        return;
////                    }
////                    if(TextUtils.isEmpty(cardLocal)) {
////                        PToast.showShort(getString(R.string.bank_cannot_be_empty));
////                        return;
////                    }
////
////                    if(TextUtils.isEmpty(cardNum)) {
////                        PToast.showShort(getString(R.string.bank_card_cannot_be_empty));
////                        return;
////                    }
////                    // 修改地址
////                    ModuleMgr.getCommonMgr().reqWithdrawModify(mEidtMoney,info.getPaytype(),cardName,cardNum,cardLocal,info.getSubbank(),this);
////                    break;
////                }
//                //获取自己可提现金额
////                String myMoney = PSP.getInstance().getLong(RedBoxRecordAct.REDBOXMONEY+ModuleMgr.getCenterMgr().getMyInfo().getUid(),0)+"";
////                int minMoney = ModuleMgr.getCommonMgr().getCommonConfig().getMinmoney();
//
//                // 提现请求
//                if (Float.valueOf(mEidtMoney) <= 0) {
//                    PToast.showShort(R.string.money_cout_be_0);
//                    break;
//                }
//                ModuleMgr.getCommonMgr().reqWithdraw(mEidtMoney, info.getPaytype(), info.getAccountname(), info.getAccountnum(), info.getBank(), info.getSubbank(), this);
//                break;
//
//            default:
//                break;
//        }
//    }

    private void setBg() {
        Drawable drawableFirst = getResources().getDrawable(R.drawable.f1_id_card_selector);
        drawableFirst.setBounds(0, 0, UIUtil.dp2px(17), UIUtil.dp2px(17));//第一0是距左右边距离，第二0是距上下边距离，第三69长度,第四宽度
        Drawable drawable = getResources().getDrawable(R.drawable.f1_id_card_selector);
        drawable.setBounds(0, 0, UIUtil.dp2px(17), UIUtil.dp2px(17));//第一0是距左右边距离，第二0是距上下边距离，第三69长度,第四宽度
        rbZhi.setCompoundDrawables(drawableFirst, null, null, null);
        rbYin.setCompoundDrawables(drawable, null, null, null);
    }

    private NoDoubleClickListener clickListener = new NoDoubleClickListener() {
        @Override
        public void onNoDoubleClick(View v) {
            if (!(NetworkUtils.isConnected(WithDrawApplyAct.this))) {
                PToast.showShort(getResources().getString(R.string.tip_net_error));
                return;
            }
            switch (v.getId()) {
                case R.id.bank_card_btn_next:
                    if (Float.valueOf(mEidtMoney) <= 0) {
                        PToast.showShort(R.string.money_cout_be_0);
                        break;
                    }
                    cardName = etCardName.getText().toString();
                    cardLocal = etCardLocal.getText().toString();
                    cardLocalBranch = etCardLocalBranch.getText().toString();
                    if (info.getPaytype() == 2) {
                        carZhi = etCardNum.getText().toString();
                    } else {
                        cardNum = etCardNum.getText().toString();
                    }


                    if (info.getPaytype() == 1) {
                        if (TextUtils.isEmpty(cardLocal)) {
                            PToast.showShort(getString(R.string.bank_cannot_be_empty));
                            return;
                        }
                        if (TextUtils.isEmpty(cardLocalBranch)) {
                            PToast.showShort(getString(R.string.branch_cannot_be_empty));
                            return;
                        }
                    }
                    if (info.getPaytype() == 1 && TextUtils.isEmpty(cardNum)) {
                        PToast.showShort(getString(R.string.bank_card_cannot_be_empty));
                        return;
                    }
                    if (info.getPaytype() == 2 && TextUtils.isEmpty(carZhi)) {
                        PToast.showShort(getString(R.string.zhi_card_cannot_be_empty));
                        return;
                    }
                    if (info.getPaytype() == 1 && TextUtils.isEmpty(cardName)) {
                        PToast.showShort(getString(R.string.name_cannot_be_empty));
                        return;
                    }
                    if (info.getPaytype() == 1 && !bankCardForm(cardNum)) {
                        PToast.showShort(getString(R.string.bank_card_number_format_is_wrong));
                        return;
                    }

                    btnNext.setEnabled(false);
                    if (status != 3) {
                        ModuleMgr.getCommonMgr().reqWithdraw(mEidtMoney, info.getPaytype(), cardName, info.getPaytype() == 2 ? carZhi : cardNum, cardLocal, cardLocalBranch, WithDrawApplyAct.this);
                    } else {
                        ModuleMgr.getCommonMgr().reqWithdrawmodifyNew(id, info.getPaytype(), cardName, info.getPaytype() == 2 ? carZhi : cardNum, cardLocal, cardLocalBranch, WithDrawApplyAct.this);
                    }
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    public void onRequestComplete(HttpResponse response) {
        LoadingDialog.closeLoadingDialog();
//        if (response.getUrlParam() == UrlParam.reqWithdrawAddress){//请求默认地址结果返回
//            if (response.isOk()){
//                info = new WithdrawAddressInfo();
//                info.parseJson(response.getResponseString());
//                etCardName.setText(info.getAccountname());
//                etCardLocal.setText(info.getBank());
//                etCardNum.setText(info.getAccountnum());
//            }
//            return;
//        }
        btnNext.setEnabled(true);
        if (response.getUrlParam() == UrlParam.reqWithdraw || response.getUrlParam() == UrlParam.reqWithdrawModify
                || response.getUrlParam() == UrlParam.reqWithdrawmodifyNew) {//申请提现结果返回，修改地址结果返回
            if (response.isOk()) {
                PToast.showShort(TextUtils.isEmpty(response.getMsg()) ? getString(R.string.submit_succeed) : response.getMsg());
                //通知提现成功 更新可提现余额
                MsgMgr.getInstance().sendMsg(MsgType.MT_GET_MONEY_Notice, null);
                UIShow.showWithDrawSuccessAct(WithDrawApplyAct.this, mEidtMoney);
                this.finish();
                return;
            }
            PToast.showShort(TextUtils.isEmpty(response.getMsg()) ? getString(R.string.submit_failure) : response.getMsg());
            return;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_card_rb_zhi:
                rbYin.setChecked(false);
                if (info.getPaytype() == 1) {
                    cardNum = etCardNum.getText().toString();
                }
                info.setPaytype(2);
                initDefaultAddress();
                break;
            case R.id.id_card_rb_yin:
                rbZhi.setChecked(false);
                if (info.getPaytype() == 2) {
                    carZhi = etCardNum.getText().toString();
                }
                info.setPaytype(1);
                initDefaultAddress();
                break;
            default:
                break;
        }
    }
}