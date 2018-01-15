package com.juxin.predestinate.ui.share;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.statistics.StatisticsShareUnlock;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;


/**
 * 类描述：
 * 创建时间：2017/11/3 14:54
 * 修改时间：2017/11/3 14:54
 * Created by zhoujie on 2017/11/3
 * 修改备注：
 */

public class ShareSuccessDialog extends BaseDialogFragment implements View.OnClickListener {
    private TextView tv_btn;

    public ShareSuccessDialog() {
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.BOTTOM);
        setDialogSizeRatio(0.95, 0);
        setCancelable(false);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.spread_share_success_dialog);
        View contentView = getContentView();
        initView(contentView);
        return contentView;
    }

    private void initView(View contentView) {
        tv_btn = contentView.findViewById(R.id.spread_result_btn);
        tv_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.spread_result_btn:
                StatisticsShareUnlock.onAlert_unlockchatsuccess_ok();
                dismiss();
                break;
            default:
                break;
        }
    }
}
