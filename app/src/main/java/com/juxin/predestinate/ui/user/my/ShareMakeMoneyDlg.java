package com.juxin.predestinate.ui.user.my;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.juxin.library.log.PSP;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.statistics.StatisticsShareUnlock;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.config.Hosts;
import com.juxin.predestinate.module.util.UIShow;

/**
 * 创建日期：2017/10/29
 * 描述:分享赚钱弹框
 *
 * @author :lc
 */
public class ShareMakeMoneyDlg extends BaseDialogFragment implements View.OnClickListener {

    public ShareMakeMoneyDlg() {
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.BOTTOM);
        setDialogSizeRatio(1, 0);
        setCancelable(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.spread_share_make_money_dlg);
        View view = getContentView();
        initView(view);
        PSP.getInstance().put(ModuleMgr.getCommonMgr().getPrivateKey(Constant.SHARE_MAKE_MONEY), true);
        PSP.getInstance().put(ModuleMgr.getCommonMgr().getPrivateKey(Constant.SHARE_SHOW_MAKE_MONEY), false);
        return view;
    }

    private void initView(View view) {
        ImageView ivShareClose = view.findViewById(R.id.iv_share_close);
        TextView tvMakeMoney = view.findViewById(R.id.tv_make_money);
        ivShareClose.setOnClickListener(this);
        tvMakeMoney.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_share_close:
                StatisticsShareUnlock.onAlert_share_closee();
                dismiss();
                break;
            case R.id.tv_make_money:
                UIShow.showWebActivity(getContext(), Hosts.H5_SHARE_EXTENSION);
                StatisticsShareUnlock.onAlert_share_makemoney();
                dismiss();
                break;
            default:
                break;
        }
    }
}
