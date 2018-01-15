package com.juxin.predestinate.ui.user.fragment;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.bean.config.base.NobilityList;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.SourcePoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.util.UIShow;

/**
 * 创建日期：2017/7/11
 * 描述:爵位相关
 * 作者:lc
 */
public class UserFragmentPeeragePanel extends BasePanel implements View.OnClickListener {

    private boolean isFromMy;
    private UserDetail userDetail;
    private NobilityList.Nobility nobility, nobility2;

    private ImageView iv_title, iv_guard, iv_car;
    private TextView tv_identity_top, tv_identity, tv_title, tv_guard, tv_car;

    public UserFragmentPeeragePanel(Context context, UserDetail userDetail, boolean isFromMy, ViewGroup parent_view) {
        super(context);
        setContentView(R.layout.p1_user_fragment_funct, parent_view,false);
        this.userDetail = userDetail;
        this.isFromMy = isFromMy;
        initNobility();
        initView();
    }

    private void initNobility() {
        nobility = ModuleMgr.getCommonMgr().getCommonConfig().queryNobility(userDetail.getNobilityInfo().getRank(), userDetail.getGender());
        nobility2 = ModuleMgr.getCommonMgr().getCommonConfig().queryLv2Nobility(userDetail.getGender());
    }

    private void initView() {
        LinearLayout ll_title = (LinearLayout) findViewById(R.id.ll_title);
        RelativeLayout rl_identity = (RelativeLayout) findViewById(R.id.rl_identity);
        RelativeLayout rl_title = (RelativeLayout) findViewById(R.id.rl_title);
        RelativeLayout rl_guard = (RelativeLayout) findViewById(R.id.rl_guard);
        RelativeLayout rl_car = (RelativeLayout) findViewById(R.id.rl_car);
        tv_identity_top = (TextView) findViewById(R.id.tv_identity_top);
        tv_identity = (TextView) findViewById(R.id.tv_identity);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_guard = (TextView) findViewById(R.id.tv_guard);
        tv_car = (TextView) findViewById(R.id.tv_car);
        iv_title = (ImageView) findViewById(R.id.iv_title);
        iv_guard = (ImageView) findViewById(R.id.iv_guard);
        iv_car = (ImageView) findViewById(R.id.iv_car);

        rl_identity.setOnClickListener(this);
        rl_title.setOnClickListener(this);
        rl_guard.setOnClickListener(this);
        rl_car.setOnClickListener(this);

        titleLevel();
        if (isFromMy || (userDetail != null && TextUtils.isEmpty(userDetail.getAvatar()))) {//来自我的(来自我的是必须有)； 头像为空时设置背景色
            ll_title.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_5D6376));
        }
    }

    void refreshView() {
        userDetail = ModuleMgr.getCenterMgr().getMyInfo();//自己
        titleLevel();
    }

    private void titleLevel() {
        try {
            initNobility();
            boolean isSame = userDetail.getUid() == ModuleMgr.getCenterMgr().getMyInfo().getUid();
            tv_identity_top.setText(isSame ? "VIP" : "身份");
            tv_identity.setText(userDetail.isVip() ? (isSame ? (userDetail.getMemdatenum() > 3650 ? "永久" : String.format(getContext().getString(R.string.user_info_vip_left), userDetail.getMemdatenum())) : "VIP") : "无");
            tv_title.setText(String.format("%s", nobility.getStar_name()));
            tv_guard.setText(TextUtils.isEmpty(nobility.getGuard_name()) ? "无" : nobility.getGuard_name());
            tv_car.setText(TextUtils.isEmpty(nobility.getCar_name()) ? "无" : nobility.getCar_name());
            ImageLoader.loadFitCenter(context, nobility.getTitle_badge(), iv_title);//贫民爵位显示贵族爵位(内容为无)
            ImageLoader.loadFitCenter(context, nobility.getId() < nobility2.getId() ? nobility2.getGuard_pic() : nobility.getGuard_pic(), iv_guard);//贫民守护显示贵族守护(内容为无)
            ImageLoader.loadFitCenter(context, nobility.getId() < nobility2.getId() ? nobility2.getCar_pic() : nobility.getCar_pic(), iv_car);//贫民座驾显示贵族座驾(内容为无)
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        boolean isSame = false;
        if (userDetail != null) {
            isSame = userDetail.getUid() != ModuleMgr.getCenterMgr().getMyInfo().getUid();
        }
        switch (v.getId()) {
            case R.id.rl_title:
                if (isSame) Statistics.userBehavior(SendPoint.page_info_juewei);
                UIShow.showTitlePrivilegeAct(getContext());
                break;
            case R.id.rl_guard:
                if (isSame) Statistics.userBehavior(SendPoint.page_info_shouhu);
                UIShow.showTitlePrivilegeAct(getContext());
                break;
            case R.id.rl_car:
                if (isSame) Statistics.userBehavior(SendPoint.page_info_zuojia);
                //if(!isFromMy) return;//只有“我的”页面可以查看爵位特权
                UIShow.showTitlePrivilegeAct(getContext());
                break;
            case R.id.rl_identity:
                if (!isSame && !ModuleMgr.getCenterMgr().getMyInfo().isVip() && ModuleMgr.getCenterMgr().getMyInfo().isMan()) {//!isSame 确定是主页个人中心页
                    Statistics.userBehavior(SendPoint.menu_me_vipmark);
                    SourcePoint.getInstance().lockSource("menu_me_vipmark");
                    UIShow.showOpenVipActivity(getContext());
                }
                break;
            default:
                break;
        }
    }
}
