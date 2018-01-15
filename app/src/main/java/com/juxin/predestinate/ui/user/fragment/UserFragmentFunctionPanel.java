package com.juxin.predestinate.ui.user.fragment;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.juxin.library.log.PToast;
import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.utils.NoDoubleClickListener;

/**
 * 用户中心功能panel
 */
@Deprecated
public class UserFragmentFunctionPanel extends BasePanel {

    UserFragmentFunctionPanel(Context context) {
        super(context);
        setContentView(R.layout.p1_user_fragment_function_panel);
        initView();
    }

    private void initView() {
        RelativeLayout rl_vip_rechange = (RelativeLayout) findViewById(R.id.rl_vip_rechange);
        RelativeLayout rl_main_page = (RelativeLayout) findViewById(R.id.rl_main_page);
        RelativeLayout rl_rank = (RelativeLayout) findViewById(R.id.rl_rank);
        RelativeLayout rl_gift = (RelativeLayout) findViewById(R.id.rl_gift);

        rl_vip_rechange.setOnClickListener(clickListener);
        rl_main_page.setOnClickListener(clickListener);
        rl_rank.setOnClickListener(clickListener);
        rl_gift.setOnClickListener(clickListener);
    }

    private final NoDoubleClickListener clickListener = new NoDoubleClickListener() {
        @Override
        public void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.rl_vip_rechange:// VIP充值
                    if (!ModuleMgr.getCenterMgr().getMyInfo().isMan()) return;
                    UIShow.showOpenVipActivity(getContext());
                    Statistics.userBehavior(SendPoint.menu_me_vippay);
                    break;
                case R.id.rl_main_page:// 我的主页
                    UIShow.showCheckOwnInfoAct(getContext());
                    Statistics.userBehavior(SendPoint.menu_me_myhome);
                    break;
                case R.id.rl_rank:// 榜单
                    PToast.showShort("暂未开放");
                    break;
                case R.id.rl_gift:// 我的礼物
                    UIShow.showMyGiftActivity(getContext());
                    Statistics.userBehavior(SendPoint.menu_me_gift);
                    break;
                default:
                    break;
            }
        }
    };
}
