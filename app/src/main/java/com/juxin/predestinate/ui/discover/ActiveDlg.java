package com.juxin.predestinate.ui.discover;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.discover.Active;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.logic.config.FinalKey;
import com.juxin.predestinate.module.util.UIShow;

/**
 * 创建日期：2017/9/4
 * 描述:惊喜回馈：充值，购买成功弹框
 * 作者:lc
 */
public class ActiveDlg extends BaseDialogFragment implements View.OnClickListener {

    private int packId;
    private Context context;

    public ActiveDlg() {
        settWindowAnimations(R.style.AnimScaleInScaleOutOverShoot);
        setGravity(Gravity.CENTER);
        setDialogSizeRatio(1, 0.5);
        setCancelable(true);
    }

    public void setData(Context context, int packId) {
        this.context = context;
        this.packId = packId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.f2_active_dlg);
        View view = getContentView();
        initView(view);
        return view;
    }

    private void initView(View view) {
        TextView tv_vip_days = (TextView) view.findViewById(R.id.tv_vip_days);
        TextView tv_ykeys_num = (TextView) view.findViewById(R.id.tv_ykeys_num);
        TextView tv_ykeys_num_below = (TextView) view.findViewById(R.id.tv_ykeys_num_below);
        TextView tv_diamond_num = (TextView) view.findViewById(R.id.tv_diamond_num);
        TextView tv_exp_num = (TextView) view.findViewById(R.id.tv_exp_num);
        TextView tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        TextView tv_ok = (TextView) view.findViewById(R.id.tv_ok);

        tv_cancel.setOnClickListener(this);
        tv_ok.setOnClickListener(this);

        try {
            Active active = ModuleMgr.getCommonMgr().getCommonConfig().queryActiveConfig(packId);
            if (active.isB()) {//钥匙版
                tv_ykeys_num.setText(String.valueOf(active.getKeys()));
                tv_ykeys_num_below.setText("钥匙");
            } else {//YB版
                tv_ykeys_num.setText(String.valueOf(active.getYcoin()));
            }
            tv_vip_days.setText(String.valueOf(active.getVip_days()));
            tv_diamond_num.setText(String.valueOf(active.getDiamonds()));
            tv_exp_num.setText(String.format("+ %s", active.getExp()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                this.dismiss();
                break;

            case R.id.tv_ok:
                UIShow.showMainWithTabData(context, FinalKey.MAIN_TAB_5, null);
                this.dismiss();
                break;

            default:
                break;
        }
    }
}
