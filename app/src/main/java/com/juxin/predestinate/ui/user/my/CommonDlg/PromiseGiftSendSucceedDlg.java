package com.juxin.predestinate.ui.user.my.CommonDlg;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;


/**
 * 创建日期：2017/7/12
 * 描述:定情礼物发送成功后的弹框
 * 作者:zm
 */
public class PromiseGiftSendSucceedDlg extends BaseDialogFragment implements View.OnClickListener {

    private TextView tvSucceed;

    public PromiseGiftSendSucceedDlg() {
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.BOTTOM);
        setDialogSizeRatio(1, 0);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.f1_promise_gift_send_succeed_dlg);
        View view = getContentView();
        initView(view);
        return view;
    }

    private void initView(View view) {
        view.findViewById(R.id.promise_gift_send_succeed).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.promise_gift_send_succeed: //关闭弹框
                dismiss();
                break;
            default:
                break;
        }
    }
}
