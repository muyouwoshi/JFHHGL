package com.juxin.predestinate.ui.user.my.CommonDlg;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.config.base.NobilityList;
import com.juxin.predestinate.module.local.mail.MyChum;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.logic.request.RequestComplete;


/**
 * 创建日期：2017/7/17
 * 描述:解除密友弹框
 * 作者:zm
 */
public class RemoveCloseFriendDlg extends BaseDialogFragment implements View.OnClickListener {

    private TextView tvName, tvReject, tvThink;
    private MyChum myChum;    //对方uid
    private RequestComplete requestComplete;

    public RemoveCloseFriendDlg() {
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.CENTER);
        setDialogSizeRatio(0.7, 0);
        setCancelable(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.f1_to_lift_private_dlg);
        View view = getContentView();
        initView(view);
        return view;
    }

    /**
     * @param myChum          对方uid
     * @param requestComplete 请求回调
     */
    public void setData(MyChum myChum, RequestComplete requestComplete) {
        this.myChum = myChum;
        this.requestComplete = requestComplete;
    }

    private void initView(View view) {
        tvName = (TextView) view.findViewById(R.id.to_lift_private_tv_name);

        view.findViewById(R.id.to_lift_private_tv_reject).setOnClickListener(this);
        view.findViewById(R.id.to_lift_private_tv_think).setOnClickListener(this);

        NobilityList nobilityList = ModuleMgr.getCommonMgr().getCommonConfig().getNobilityList();
        NobilityList.CloseFriend closeFriend = nobilityList.queryCloseFriend(myChum.getLevel());

        tvName.setText("是否与" + myChum.getNickname() + "(" + "Lv" + myChum.getLevel() + closeFriend.getTitle() + ")");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.to_lift_private_tv_reject: //确定拒绝逻辑，关闭弹框
                Statistics.userBehavior(SendPoint.menu_xiaoxi_miyou_alert_yes);
                ModuleMgr.getCommonMgr().cancelIntimateFriend(myChum.getUid(), requestComplete);
                dismiss();
                break;
            case R.id.to_lift_private_tv_think: //考虑一下（直接关闭弹框）
                Statistics.userBehavior(SendPoint.menu_xiaoxi_miyou_alert_no);
                dismiss();
                break;
            default:
                break;
        }
    }
}
