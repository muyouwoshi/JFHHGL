package com.juxin.predestinate.ui.discover.task;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.mail.MailSpecialID;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.util.UIShow;

/**
 * Created by Administrator on 2017/7/24.
 */

public class MakeMoneyNewTipDialog extends BaseDialogFragment implements View.OnClickListener {

    private Button make_money;
    private ImageView cancal;
    private TextView xiaomishu;


    public MakeMoneyNewTipDialog() {
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.BOTTOM);
        setDialogSizeRatio(0, 0);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.f1_make_money_new_dialog);
        initView();
        initData();
        ModuleMgr.getCommonMgr().setMMoneyDlgShow(true);
        return getContentView();
    }

    private void initView() {
        make_money = (Button) findViewById(R.id.make_money_sure_btn);
        cancal = (ImageView) findViewById(R.id.make_money_cancel_btn);
        xiaomishu = (TextView) findViewById(R.id.xiaomishu);
    }

    private void initData() {
        make_money.setOnClickListener(this);
        cancal.setOnClickListener(this);
        xiaomishu.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.make_money_sure_btn:
                dismiss();
                UIShow.showEarnMoney(getActivity());
                break;

            case R.id.make_money_cancel_btn:
                dismiss();
                UIShow.showSureUserDlg(getActivity());
                MsgMgr.getInstance().sendMsg(MsgType.MT_CLOSE_SAY_HELLO, true);
                break;
            case R.id.xiaomishu:
                dismiss();
                UIShow.showPrivateChatAct(getActivity(), MailSpecialID.customerService.getSpecialID(), "");
                break;
            default:
                break;
        }
    }

    /**
     * @author Mr.Huang
     */
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        UIShow.showWebPushDialog(getActivity());//内部根据在线配置判断是否展示活动推送弹窗
    }
}
