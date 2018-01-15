package com.juxin.predestinate.ui.user.fragment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.library.log.PSP;
import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.MyEarnInfo;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.local.statistics.StatisticsUser;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.user.util.CenterConstant;

/**
 * 创建日期：2017/8/31
 * 描述:女号收益相关和去赚钱
 * 作者:lc
 */
class IncomePanel extends BasePanel implements View.OnClickListener {

    private UserDetail userDetail;
    private TextView tv_income, tv_base_income, tv_active_bonus, tv_task_subsidy;

    IncomePanel(Context context, UserDetail userDetail, ViewGroup parent_view) {
        super(context);
        setContentView(R.layout.p2_income_panel,parent_view,false);
        this.userDetail = userDetail;
        initView();
    }

    private void initView() {
        View rl_income = findViewById(R.id.rl_income);
        View rl_base_income = findViewById(R.id.rl_base_income);
        View rl_active_bonus = findViewById(R.id.rl_active_bonus);
        View rl_task_subsidy = findViewById(R.id.rl_task_subsidy);
        LinearLayout ll_to_make_money = (LinearLayout) findViewById(R.id.ll_to_make_money);
        tv_income = (TextView) findViewById(R.id.tv_income);
        tv_base_income = (TextView) findViewById(R.id.tv_base_income);
        tv_active_bonus = (TextView) findViewById(R.id.tv_active_bonus);
        tv_task_subsidy = (TextView) findViewById(R.id.tv_task_subsidy);

        rl_income.setOnClickListener(this);
        rl_base_income.setOnClickListener(this);
        rl_active_bonus.setOnClickListener(this);
        rl_task_subsidy.setOnClickListener(this);
        ll_to_make_money.setOnClickListener(this);
        womenIncome();
    }

    void refreshView() {
        userDetail = ModuleMgr.getCenterMgr().getMyInfo();
        womenIncome();
    }

    private void womenIncome() {
        MyEarnInfo myEarnInfo = ModuleMgr.getCommonMgr().getMyEarnInfo();
        tv_income.setText(String.format("%s", String.valueOf(myEarnInfo.getFEstimatedincome())));
        tv_base_income.setText(String.format("%s", String.valueOf(myEarnInfo.getFBasicincome())));
        tv_active_bonus.setText(String.format("%s", String.valueOf(myEarnInfo.getFActivebonus())));
        tv_task_subsidy.setText(String.format("%s", String.valueOf(myEarnInfo.getFTasksubsidy())));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_income:
                StatisticsUser.userTodayMoney(ModuleMgr.getCommonMgr().getMyEarnInfo().getFEstimatedincome());
                UIShow.showEarnMoney(getContext());
                break;
            case R.id.rl_base_income:
            case R.id.rl_active_bonus:
            case R.id.rl_task_subsidy:
                UIShow.showEarnMoney(getContext());
                Statistics.userBehavior(SendPoint.menu_me_redpackage);
                break;
            case R.id.ll_to_make_money:
                PSP.getInstance().put(ModuleMgr.getCommonMgr().getPrivateKey(Constant.TEMP_SHARE_TYPE), CenterConstant.SHARE_TYPE_WOMAN_SHARE);
                UIShow.showEarnMoney(getContext());
                Statistics.userBehavior(SendPoint.menu_me_redpackage);
                break;
            default:
                break;
        }
    }
}
