package com.juxin.predestinate.ui.user.my.CommonDlg;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.library.log.PLogger;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.util.ChineseFilter;

/**
 * 礼物红包消息
 * Created by Kind on 2017/10/11.
 */

public class GiftRedPackageStatusDlg extends BaseDialogFragment implements View.OnClickListener {

    private double price = 0;
    private boolean isReceive;
    private boolean isOwn;
    private TextView gift_redpackage_pirce,tvTip,tvClose,tvOkTwo;
    private LinearLayout llBottomOne,llBottomTwo;

    public GiftRedPackageStatusDlg(double price, boolean isReceive, boolean isOwn) {
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.BOTTOM);
        setDialogSizeRatio(-2, 0);
        setCancelable(true);
        this.price = price;
        this.isReceive = isReceive;
        this.isOwn = isOwn;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.f42_bottom_gift_redpackage_receive);
        initView();
        initData();
        return getContentView();
    }

    private void initView() {
        gift_redpackage_pirce = (TextView) findViewById(R.id.gift_redpackage_pirce);
        llBottomOne = (LinearLayout) findViewById(R.id.gift_redpackage_bottom_one);
        llBottomTwo = (LinearLayout) findViewById(R.id.gift_redpackage_bottom_two);
        tvTip = (TextView) findViewById(R.id.gift_redpackage_tip);
        tvClose = (TextView) findViewById(R.id.gift_redpackage_close);
        tvOkTwo = (TextView) findViewById(R.id.gift_redpackage_ok_two);

        findViewById(R.id.gift_redpackage_close).setOnClickListener(this);
        findViewById(R.id.gift_redpackage_ok).setOnClickListener(this);
        tvOkTwo.setOnClickListener(this);

        if (isOwn){
            llBottomTwo.setVisibility(View.GONE);
            llBottomOne.setVisibility(View.VISIBLE);
            tvClose.setText(getContext().getString(R.string.success_receive));
        }else {
            llBottomTwo.setVisibility(View.VISIBLE);
            llBottomOne.setVisibility(View.GONE);
            tvClose.setText(getContext().getString(R.string.send_redbag));
            if (isReceive){
                tvOkTwo.setText(getContext().getString(R.string.success_receive));
                tvTip.setVisibility(View.GONE);
            }else {
                tvOkTwo.setText(getContext().getString(R.string.yet_receive));
                tvTip.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initData() {
        if(price <= 0){
            gift_redpackage_pirce.setVisibility(View.GONE);
        }else {
            double d = price/100;
            PLogger.d("dddddddd=" + d);
            gift_redpackage_pirce.setText("￥" + ChineseFilter.subZeroString(ChineseFilter.formatNum(d, 2)+""));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.gift_redpackage_close:
            case R.id.gift_redpackage_ok:
            case R.id.gift_redpackage_ok_two:
                this.dismiss();
                break;
        }
    }
}
