package com.juxin.predestinate.ui.user.fragment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.NobilityInfo;
import com.juxin.predestinate.bean.config.CommonConfig;
import com.juxin.predestinate.bean.config.base.NobilityList;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.util.UIUtil;

/**
 * 创建日期：2017/7/20
 * 描述:头部右侧进度panel
 * 作者:lc
 */
class UserFragmentProgressPanel extends BasePanel {

    private LinearLayout ll_star;
    private ProgressBar progressBar;
    private ImageView iv_pre_progreee, iv_aft_progress;
    private TextView tv_progress_value, tv_level, tv_level_c, tv_level_a, tv_pre_level;

    private NobilityInfo nobilityInfo;
    private NobilityList.Nobility nobility, nextNobility;

    UserFragmentProgressPanel(Context context, ViewGroup parent_view) {
        super(context);
        setContentView(R.layout.p1_user_fragment_funco, parent_view, false);
        initView();
        refreshLevel();
    }

    private void initView() {
        //头像右侧
        tv_pre_level = (TextView) findViewById(R.id.tv_pre_level);
        tv_level = (TextView) findViewById(R.id.tv_level);
        ll_star = (LinearLayout) findViewById(R.id.ll_star);
        iv_pre_progreee = (ImageView) findViewById(R.id.iv_pre_progreee);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tv_progress_value = (TextView) findViewById(R.id.tv_progress_value);
        iv_aft_progress = (ImageView) findViewById(R.id.iv_aft_progress);
        tv_level_c = (TextView) findViewById(R.id.tv_level_c);
        tv_level_a = (TextView) findViewById(R.id.tv_level_a);

        showOrGone();
    }

    private void showOrGone() {
        ll_star.setVisibility(View.VISIBLE);
        tv_pre_level.setText(getContext().getString(R.string.user_title_up));
        tv_level.setText("");
        levelStar();
    }

    /**
     * 星星等级
     */
    private void levelStar() {
        ll_star.removeAllViews();
        getNobility();
        int starLv = ((nobility.getId() > 0) && (nobility.getId() % 5 == 0)) ? 5 : nobility.getId() % 5;
        for (int i = 0; i < 5; i++) {
            ImageView imageView = new ImageView(context);
            LinearLayout.LayoutParams layoutParms = new LinearLayout.LayoutParams(UIUtil.dip2px(context, 13), UIUtil.dip2px(context, 13));
            layoutParms.setMargins(UIUtil.dip2px(context, 2), 0, 0, 0);
            imageView.setLayoutParams(layoutParms);
            if (i < starLv) {
                imageView.setImageResource(R.drawable.f2_star_icon);
            } else {
                imageView.setImageResource(R.drawable.f2_star_def_icon);
            }
            ll_star.addView(imageView);
        }
    }

    void refreshView() {
        levelStar();
        refreshLevel();
    }

    private CommonConfig getCommonConfig() {
        return ModuleMgr.getCommonMgr().getCommonConfig();
    }

    private void getNobility() {
        nobilityInfo = ModuleMgr.getCenterMgr().getMyInfo().getNobilityInfo();
        nobility = getCommonConfig().queryNobility(nobilityInfo.getRank(), ModuleMgr.getCenterMgr().getMyInfo().getGender());
        nextNobility = getCommonConfig().queryNobility(nobilityInfo.getRank() + 1, ModuleMgr.getCenterMgr().getMyInfo().getGender());
    }

    /**
     * 进度条
     */
    private void refreshLevel() {
        try {
            getNobility();
            tv_level.setText(nobility.getTitle_name());
            progressBar.setMax(nextNobility.getUpgrade_condition());
            progressBar.setProgress(nobilityInfo.getCmpprocess());
            if (nobilityInfo.getRank() < getCommonConfig().getNobilitys().size() / 2) {
                tv_progress_value.setText(nobilityInfo.getCmpprocess() + "/" + nextNobility.getUpgrade_condition());
            } else { //达到最高级
                iv_aft_progress.setVisibility(View.INVISIBLE);
                tv_level_a.setVisibility(View.INVISIBLE);
                if (nobilityInfo.getCmpprocess() > nobility.getUpgrade_condition()) { //超过部分
                    int temp = nobilityInfo.getCmpprocess() - nobility.getUpgrade_condition();
                    tv_progress_value.setText(String.valueOf(temp));
                } else {
                    tv_progress_value.setText(nobilityInfo.getCmpprocess() + "/" + nobility.getUpgrade_condition());
                }
            }
            tv_level_c.setText(nobility.getStar_name());
            tv_level_a.setText(nextNobility.getStar_name());
            ImageLoader.loadFitCenter(context, nobility.getTitle_badge(), iv_pre_progreee);
            ImageLoader.loadFitCenter(context, nextNobility.getTitle_badge(), iv_aft_progress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
