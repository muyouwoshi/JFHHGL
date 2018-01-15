package com.juxin.predestinate.ui.user.my;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.library.log.PSP;
import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.library.observe.PObserver;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.model.impl.UnreadMgrImpl;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.user.auth.IDCardAuthenticationSucceedAct;
import com.juxin.predestinate.ui.utils.NoDoubleClickListener;

/**
 * 我的钱包页面
 * Created by zm on 2017/4/20
 */
public class RedBoxRecordAct extends BaseActivity implements PObserver {

    public static String REDBOXMONEY = "REDBOXMONEY";//键
    private int authResult = 103, authForVodeo = 104, authIDCard = 105;

    private TextView tvMoney, tvExplain, tvTip;
    private TextView tvWithdraw;
    private LinearLayout llWithDraw;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f1_wode_red_bag_record_act);
        MsgMgr.getInstance().attach(this);
        if (!ModuleMgr.getCommonMgr().getIdCardVerifyStatusInfo().getIsVerifyIdCard()) {
            ModuleMgr.getCommonMgr().getVerifyStatus(null);
        }
        initView();
        ModuleMgr.getUnreadMgr().resetUnreadByKey(UnreadMgrImpl.MY_WALLET);
    }

    private void initView() {
        setBackView(R.id.base_title_back);
        tvMoney = (TextView) findViewById(R.id.wode_wallet_tv_money);
        tvWithdraw = (TextView) findViewById(R.id.wode_wallet_tv_draw);
        tvExplain = (TextView) findViewById(R.id.wode_wallet_tv_explain);
        llWithDraw = (LinearLayout) findViewById(R.id.wode_wallet_ll_withdraw);
        tvTip = (TextView) findViewById(R.id.wode_wallet_tv_tip);
        tvTip.setText(this.getString(R.string.balance_in_full, String.valueOf(ModuleMgr.getCommonMgr().getCommonConfig().getPay().getMin_withdraw_money() / 100)));
        llWithDraw.addView(new RedBagRecordPanel(this).getContentView());
        tvWithdraw.setOnClickListener(clickListener);
        tvExplain.setOnClickListener(clickListener);
        refreshView(ModuleMgr.getCenterMgr().getMyInfo().getRedbagsum() / 100f);
        setTitle(getString(R.string.my_red));
        setTitleRight(getString(R.string.withdrawal_record), R.color.color_9ea3b5, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到提现记录页
                UIShow.showWithDrawRecordAct(RedBoxRecordAct.this);
                Statistics.userBehavior(SendPoint.menu_me_money_explain);
            }
        });
    }

    public void refreshView(double money) {
        PSP.getInstance().put(REDBOXMONEY + ModuleMgr.getCenterMgr().getMyInfo().getUid(), (float) money);//存储可提现金额
        tvMoney.setText(money + "");
        ModuleMgr.getCenterMgr().getMyInfo().setRedbagsum(money * 100);
    }

    private NoDoubleClickListener clickListener = new NoDoubleClickListener() {
        @Override
        public void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.wode_wallet_tv_explain:
                    //跳转到提现说明页
                    UIShow.showWithDrawExplainAct(RedBoxRecordAct.this);
                    Statistics.userBehavior(SendPoint.menu_me_money_explain);
                    break;
                case R.id.wode_wallet_tv_draw:
                    Statistics.userBehavior(SendPoint.menu_me_money_withdraw);
                    String money = tvMoney.getText().toString().trim();

                    int status = ModuleMgr.getCommonMgr().getIdCardVerifyStatusInfo().getStatus();
                    if (!ModuleMgr.getCenterMgr().getMyInfo().isVerifyCellphone()) {//是否绑定了手机号
                        UIShow.showRedBoxPhoneVerifyAct(RedBoxRecordAct.this);
                        break;
                    }
                    if (status <= 0) {//是否进行了身份认证
                        UIShow.showIDCardAuthenticationAct(RedBoxRecordAct.this, authIDCard);
                        break;
                    }
                    if (status != 2) {//身份认证还未通过
                        PToast.showShort(R.string.the_identity_certification_audit);
                        break;
                    }

                    float minMoney = ModuleMgr.getCommonMgr().getCommonConfig().getPay().getMin_withdraw_money() / 100f;
                    if (Float.valueOf(money) <= minMoney) {
                        PToast.showShort(getString(R.string.withdraw_tips) + minMoney + getString(R.string.head_unit));
                        return;
                    }
                    UIShow.showWithDrawApplyAct(0, 0, false, 0, RedBoxRecordAct.this);
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == authResult) {//手机号认证

        } else if (requestCode == authIDCard) {//身份认证
            if (data != null) {
                int back = data.getIntExtra(IDCardAuthenticationSucceedAct.IDCARDBACK, 0);
                if (back == 2) {
                    this.finish();
                }
            }
        }
    }

    @Override
    public void onMessage(String key, Object value) {
        switch (key) {
            case MsgType.MT_GET_MONEY_Notice:
                refreshView(0.0); //提现成功更新可提现金额
                //主动刷新提现记录
//                if (panls.size() != 0) {
//                    if (panls.get(2) != null) {
//                        ((WithDrawRecordPanel) panls.get(2)).onRefresh();
//                    }
//                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MsgMgr.getInstance().detach(this);
    }
}