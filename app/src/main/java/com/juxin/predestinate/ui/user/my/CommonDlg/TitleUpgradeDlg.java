package com.juxin.predestinate.ui.user.my.CommonDlg;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.config.base.NobilityList;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.util.my.DlgMessageHelper;

/**
 * 创建日期：2017/7/11
 * 描述:爵位升级弹框
 * 作者:zm
 */
public class TitleUpgradeDlg extends BaseDialogFragment {

    private ImageView imgCrown, imgBottomCrown, imgStar;
    private TextView tvName;
    private NobilityList.Nobility mNobility;
    private int[] imgs = new int[]{R.drawable.f2_word_rongsheng_1x, R.drawable.f2_word_rongsheng_2x,
            R.drawable.f2_word_rongsheng_3x, R.drawable.f2_word_rongsheng_4x, R.drawable.f2_word_rongsheng_5x};

    public TitleUpgradeDlg() {
        settWindowAnimations(R.style.dialog);
        setGravity(Gravity.CENTER);
        setDialogSizeRatio(1, 0);
        setDimAmount(0);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        window.setAttributes(windowParams);
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.f1_the_title_to_upgrade_dlg);
        View view = getContentView();
        initView(view);
        return view;
    }

    private void initView(View view) {
        imgCrown = (ImageView) view.findViewById(R.id.title_upgrade_img_crown);
        imgStar = (ImageView) view.findViewById(R.id.title_upgrade_img_star);
        imgBottomCrown = (ImageView) view.findViewById(R.id.title_upgrade_img_bottom_crown);
        tvName = (TextView) view.findViewById(R.id.title_upgrade_tv_name);

        mNobility = ModuleMgr.getCommonMgr().getCommonConfig().queryNobility(ModuleMgr.getCenterMgr().getMyInfo().getNobilityInfo().getRank(), ModuleMgr.getCenterMgr().getMyInfo().getGender());
        tvName.setText(ModuleMgr.getCenterMgr().getMyInfo().getNickname());
        ImageLoader.loadAsBmpFitCenter(getContext(), mNobility.getTitle_badge(), imgCrown);
        ImageLoader.loadAsBmpFitCenter(getContext(), mNobility.getTitle_name_pic(), imgBottomCrown);
        imgStar.setImageResource(imgs[(ModuleMgr.getCenterMgr().getMyInfo().getNobilityInfo().getRank() - 1) % 5]);

        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissAllowingStateLoss();
                DlgMessageHelper.getInstance().HandleDlgMsgs();
            }
        }, 3000);
    }
}
