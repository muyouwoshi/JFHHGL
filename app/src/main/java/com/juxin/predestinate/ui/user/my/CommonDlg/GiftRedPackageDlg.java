package com.juxin.predestinate.ui.user.my.CommonDlg;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.util.ChineseFilter;

/**
 * 礼物红包消息
 * Created by Kind on 2017/10/11.
 */

public class GiftRedPackageDlg extends BaseDialogFragment implements View.OnClickListener {

    private double price = 0;
    private TextView gift_redpackage_title, gift_redpackage_pirce;
    private boolean isReceive;
    private boolean isOwn;

    public GiftRedPackageDlg(double price, boolean isReceive, boolean isOwn) {
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
        setContentView(R.layout.f42_bottom_gift_redpackage);
        initView();
        initData();
        return getContentView();
    }

    private void initView() {
        gift_redpackage_title = (TextView) findViewById(R.id.gift_redpackage_title);
        gift_redpackage_pirce = (TextView) findViewById(R.id.gift_redpackage_pirce);

        findViewById(R.id.gift_redpackage_close).setOnClickListener(this);
        findViewById(R.id.gift_redpackage_ok).setOnClickListener(this);
        if (isOwn){
            gift_redpackage_title.setText("成功领取了红包");
        }else {
            if (!isReceive) {
                gift_redpackage_title.setText("哇哦，" + (ModuleMgr.getCenterMgr().getMyInfo().isMan() ? "她" : "他") + "送你红包啦");
            } else {
                gift_redpackage_title.setText("已领取该红包");
            }
        }
    }

    private void initData() {
        if (price <= 0) {
            gift_redpackage_pirce.setVisibility(View.GONE);
        } else {
            double d = price / 100;
            gift_redpackage_pirce.setText("￥" + ChineseFilter.subZeroString(ChineseFilter.formatNum(d, 2) + ""));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gift_redpackage_close:
            case R.id.gift_redpackage_ok:
                this.dismiss();
                break;
        }
    }
}
