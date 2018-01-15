package com.juxin.predestinate.ui.discover.task;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.statistics.StatisticsUser;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.util.UIShow;

/**
 * 资料认证提示弹框
 * Created by zhang on 2017/6/19.
 */
public class UserAuthDialog extends BaseDialogFragment implements View.OnClickListener {

    private TextView dlgTitle;
    private Button sure_btn;
    private Button cancel_btn;

    private int fromType = -1;

    public UserAuthDialog() {
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.BOTTOM);
        setDialogSizeRatio(0, 0);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.f1_user_auth_dialog);
        initData();
        initView();
        return getContentView();
    }

    private void initData() {

    }

    private void initView() {
        dlgTitle = (TextView) findViewById(R.id.user_auth_title);
        sure_btn = (Button) findViewById(R.id.user_auth_sure_btn);
        cancel_btn = (Button) findViewById(R.id.user_auth_cancel_btn);
        setFromType(fromType);
        sure_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);
    }

    public int getFromType() {
        return fromType;
    }

    public void setFromType(int fromType) {
        this.fromType = fromType;
        switch (getFromType()) {
            case Constant.OPEN_FROM_HOME:
                setHomeDlgTitle();
                break;
            case Constant.OPEN_FROM_CMD:
                setCMDTitle();
                break;
            default:
                break;
        }
    }

    private void setHomeDlgTitle() {
        if (dlgTitle != null) {
            dlgTitle.setText(getString(R.string.user_auth_dialog_home_title));
        }
    }

    private void setCMDTitle() {
        if (dlgTitle != null) {
            dlgTitle.setText(getString(R.string.user_auth_dialog_cmd_title));
        }
    }

    @Override
    public void onClick(View view) {
        //DELETE START 170725 取消弹框时暂不关闭当前所在页面
        //if (fromType == Constant.OPEN_FROM_CMD) getActivity().finish();
        //DELETE END 170725 取消弹框时暂不关闭当前所在页面
        switch (view.getId()) {
            case R.id.user_auth_sure_btn:
                StatisticsUser.userAuthDialog(fromType, false);
                UIShow.showMyAuthenticationAct((FragmentActivity) getContext(), 103);
                dismiss();
                break;
            case R.id.user_auth_cancel_btn:
                StatisticsUser.userAuthDialog(fromType, true);
                dismiss();
                break;
            default:
                break;
        }
    }
}
