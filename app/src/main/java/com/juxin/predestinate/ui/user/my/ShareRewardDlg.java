package com.juxin.predestinate.ui.user.my;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.juxin.library.log.PSP;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.statistics.StatisticsShareUnlock;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.invoke.Invoker;

/**
 * 创建日期：2017/10/29
 * 描述:分享奖励弹框
 * @author :lc
 */
public class ShareRewardDlg extends BaseDialogFragment implements View.OnClickListener {

    public ShareRewardDlg() {
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.BOTTOM);
        setDialogSizeRatio(1, 0);
        setCancelable(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.spread_share_reward_dlg);
        View view = getContentView();
        initView(view);
        PSP.getInstance().put(ModuleMgr.getCommonMgr().getPrivateKey(Constant.SHARE_REWARD), true);
        return view;
    }

    private void initView(View view) {
        ImageView ivShareRewardClose = view.findViewById(R.id.iv_share_reward_close);
        ivShareRewardClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_share_reward_close:
                StatisticsShareUnlock.onAlert_sharereward_close();
                dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Invoker.getInstance().doInJS(Invoker.JSCMD_open_share_guide_page, null);
    }
}
