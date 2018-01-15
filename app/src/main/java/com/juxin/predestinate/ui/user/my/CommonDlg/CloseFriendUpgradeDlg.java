package com.juxin.predestinate.ui.user.my.CommonDlg;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.config.base.NobilityList;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.util.UIUtil;
import com.juxin.predestinate.module.util.my.DlgMessageHelper;


/**
 * 创建日期：2017/7/17
 * 描述:密友升级弹框
 * 作者:zm
 */
public class CloseFriendUpgradeDlg extends BaseDialogFragment {

    private TextView tvTitle, tvLevel, tvBottomTitle, tvBottomDiscount, tvTopLevel;
    private LinearLayout llcontainer;

    private String name; //昵称
    private int level;   //等级

    public CloseFriendUpgradeDlg() {
        settWindowAnimations(R.style.dialog);
        setGravity(Gravity.CENTER);
        setDialogSizeRatio(1, 0);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        window.setAttributes(windowParams);

        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.f2_close_friend_upgrade_dlg);
        View view = getContentView();
        initView(view);
        int width = getContext().getResources().getDisplayMetrics().widthPixels - UIUtil.dip2px(getContext(), 17);
        if (width > UIUtil.dip2px(getContext(), 380) && llcontainer.getLayoutParams() != null) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) llcontainer.getLayoutParams();
            params.width = UIUtil.dip2px(getContext(), 380);
            params.leftMargin = (getContext().getResources().getDisplayMetrics().widthPixels - UIUtil.dip2px(getContext(), 380)) / 2;
            params.rightMargin = params.leftMargin;
            llcontainer.setLayoutParams(params);
        }
        return view;
    }

    public void setData(String name, int level) {
        this.name = name;
        this.level = level;
    }

    private void initView(View view) {
        tvTitle = (TextView) view.findViewById(R.id.close_friend_upgrade_tv_title);
        tvLevel = (TextView) view.findViewById(R.id.close_friend_upgrade_tv_level);
        tvBottomTitle = (TextView) view.findViewById(R.id.close_friend_upgrade_tv_bottom_title);
        tvBottomDiscount = (TextView) view.findViewById(R.id.close_friend_upgrade_tv_bottom_discount);
        tvTopLevel = (TextView) view.findViewById(R.id.close_friend_upgrade_tv_top_level);
        llcontainer = (LinearLayout) view.findViewById(R.id.close_friend_upgrade_ll_container);

        NobilityList.CloseFriend closeFriend = ModuleMgr.getCommonMgr().getCommonConfig().getNobilityList().queryCloseFriend(level);
        tvTitle.setText(Html.fromHtml(getContext().getString(R.string.closeness_to_upgrade, name)));
        tvTopLevel.setText(closeFriend.getTitle() + "(Lv" + level + ")");
        tvLevel.setText(closeFriend.getTitle() + "(Lv" + level + ")");
        tvBottomTitle.setText(getString(R.string.special_mark, level));
//        tvBottomDiscount.setText(getString(R.string.discount, closeFriend.getDiscount()));

        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissAllowingStateLoss();
                DlgMessageHelper.getInstance().HandleDlgMsgs();
            }
        }, 3000);
    }

    @Override
    public void showDialog(FragmentActivity context) {
        super.showDialog(context);
    }
}
