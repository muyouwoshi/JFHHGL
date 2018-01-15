package com.juxin.predestinate.ui.user.my.CommonDlg;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.juxin.library.log.PSP;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.util.UIShow;

/**
 * 创建日期：2017/7/11
 * 描述:当用户首次产生钻石消费后，弹窗提示用户。
 * 点击查看详情，跳转到爵位特权页。
 * 如用户首次消费钻石的方式是语音或视频通话，提示用户的弹窗，将在结束通话后弹出。
 * 如用户首次一次性消费以满足某一个爵位的条件，则直接提示用户荣升为XX爵位。
 * 作者:zm
 */
public class AccumulatedSystemDlg extends BaseDialogFragment implements View.OnClickListener {

    private TextView tvCheckPrivilege; //跳转按钮

    public AccumulatedSystemDlg() {
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.BOTTOM);
        setDialogSizeRatio(1, 0);
        setCancelable(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.p1_the_title_accumulated_system_dlg);
        View view = getContentView();
        initView(view);
        return view;
    }

    private void initView(View view) {
        view.findViewById(R.id.accumlated_dlg_check_privilege).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.accumlated_dlg_check_privilege: //跳转到爵位特权页。
                Statistics.userBehavior(SendPoint.alert_peerage_first_viewprivileges);
                UIShow.showTitlePrivilegeAct(getContext());
                dismiss();
                break;
            default:
                break;
        }
    }
}
