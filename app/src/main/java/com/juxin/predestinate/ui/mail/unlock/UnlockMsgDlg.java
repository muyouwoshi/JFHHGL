package com.juxin.predestinate.ui.mail.unlock;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.library.view.CustomFrameLayout;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.mail.MailSpecialID;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIShow;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by IQQ on 2017-10-31.
 */

public class UnlockMsgDlg extends BaseDialogFragment implements View.OnClickListener {
    private ImageView iv_close;
    private TextView tv_share, tv_diamond, tv_juewei;
    private CustomFrameLayout current;
    private LinearLayout llBottom;

    private UnlockDiamondPanel ll_diamond;
    private UnlockSharePanel ll_share;
    private UnlockJueweiPanel ll_juewei;
    private Long toUid;

    public UnlockMsgDlg() {
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.BOTTOM);
        setDialogSizeRatio(0.95, 0);
        setCancelable(false);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.spread_unlock_msg_dlg);
        View contentView = getContentView();
        initView(contentView);
        initData();
        return contentView;
    }

    private void initView(View contentView) {
        iv_close = contentView.findViewById(R.id.spread_unlock_iv_close);
        tv_share = contentView.findViewById(R.id.spread_unlock_tv_share);
        tv_diamond = contentView.findViewById(R.id.spread_unlock_tv_diamond);
        tv_juewei = contentView.findViewById(R.id.spread_unlock_tv_juewei);
        current = contentView.findViewById(R.id.spread_unlock_fl_content);
        llBottom = contentView.findViewById(R.id.spread_unlock_ll_bottom);
        ll_diamond = new UnlockDiamondPanel(getContext(), onUnlockDlgMsg);
        ll_share = new UnlockSharePanel(getContext(), onUnlockDlgMsg);
        ll_juewei = new UnlockJueweiPanel(getContext(), onUnlockDlgMsg);
        ll_juewei.setData(toUid);
        iv_close.setOnClickListener(this);
        tv_share.setOnClickListener(this);
        tv_diamond.setOnClickListener(this);
        tv_juewei.setOnClickListener(this);

        ll_share.setToUid(toUid);

        findViewById(R.id.spread_unlock_ll_bottom).setOnClickListener(this);
    }

    private void initData() {
        current.addView(ll_share.getContentView());
        current.addView(ll_diamond.getContentView());
        current.addView(ll_juewei.getContentView());

        switchBtn(1);//新需求，改成1

    }

    private OnUnlockDlgMsg onUnlockDlgMsg = new OnUnlockDlgMsg() {
        @Override
        public void onDismiss() {
            dismiss();
        }
    };

    /**
     * 切换button
     *
     * @param id 1:分享 2钻石 3;爵位
     */
    private void switchBtn(int id) {
        tv_share.setBackgroundResource(0);
        tv_diamond.setBackgroundResource(0);
        tv_juewei.setBackgroundResource(0);
        tv_share.setTextColor(getResources().getColor(R.color.spread_unlock_title_btn));
        tv_diamond.setTextColor(getResources().getColor(R.color.spread_unlock_title_btn));
        tv_juewei.setTextColor(getResources().getColor(R.color.spread_unlock_title_btn));

        switch (id) {
            case 1:
                tv_share.setBackgroundResource(R.drawable.spread_unlock_button);
                tv_share.setTextColor(Color.WHITE);
                current.show(R.id.spread_unlock_share_layout);
                llBottom.setVisibility(View.GONE);
                break;
            case 2:
                tv_diamond.setBackgroundResource(R.drawable.spread_unlock_button);
                tv_diamond.setTextColor(Color.WHITE);
                current.show(R.id.spread_unlock_diamond_layout);
                llBottom.setVisibility(View.VISIBLE);
                break;
            case 3:
                tv_juewei.setBackgroundResource(R.drawable.spread_unlock_button);
                tv_juewei.setTextColor(Color.WHITE);
                current.show(R.id.root);
                llBottom.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.spread_unlock_iv_close:
                dismiss();
                break;
            case R.id.spread_unlock_tv_share:
                switchBtn(1);
                break;
            case R.id.spread_unlock_tv_diamond:
                switchBtn(2);
                break;
            case R.id.spread_unlock_tv_juewei:
                switchBtn(3);
                break;
            case R.id.spread_unlock_ll_bottom:
                UIShow.showPrivateChatAct(App.getActivity(), MailSpecialID.customerService.getSpecialID(), null);
                break;
            default:
                break;
        }
    }

    public void setToUid(Long toUid) {
        this.toUid = toUid;
    }


    public interface OnUnlockDlgMsg {
        void onDismiss();
    }
}
