package com.juxin.predestinate.ui.user.my.CommonDlg;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;

/**
 * 礼物红包消息
 * Created by Kind on 2017/10/11.
 */

public class GiftRedPackageExplainDlg extends BaseDialogFragment implements View.OnClickListener {

    public GiftRedPackageExplainDlg() {
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.BOTTOM);
        setDialogSizeRatio(-2, 0);
        setCancelable(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.f42_bottom_gift_redpackage_explain);
        initView();
        return getContentView();
    }

    private void initView() {
        findViewById(R.id.redbag_explain_img_close).setOnClickListener(this);
        findViewById(R.id.redbag_explain_tv_know).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.redbag_explain_img_close:
            case R.id.redbag_explain_tv_know:
                this.dismiss();
                break;
        }
    }
}
