package com.juxin.predestinate.ui.start;

import android.os.Bundle;
import android.view.View;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.local.statistics.log.AppStatus;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.utils.NoDoubleClickListener;

/**
 * 用户导航页面
 * <p/>
 * Created by XY on 2017/3/21.
 */
public class NavUserAct extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isCanBack(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p1_nav_act);
        initView();
    }

    private void initView() {
        findViewById(R.id.btn_user_nav_reg).setOnClickListener(clickListener);
        findViewById(R.id.btn_user_nav_login).setOnClickListener(clickListener);
    }

    @Override
    public boolean isFitsSystemWindows() {
        return false;
    }

    private NoDoubleClickListener clickListener = new NoDoubleClickListener() {
        @Override
        public void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.btn_user_nav_login:
                    UIShow.showUserLoginExtAct(NavUserAct.this);
                    Statistics.userBehavior(SendPoint.welcome_login);
                    break;

                case R.id.btn_user_nav_reg:
                    UIShow.showUserRegInfoAct(NavUserAct.this);
                    Statistics.userBehavior(SendPoint.welcome_regist);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        ModuleMgr.getLoginMgr().clearCookie();
        Statistics.appStatusChange(AppStatus.CLOSE);
        super.onBackPressed();
    }
}
