package com.juxin.predestinate.ui.user.my;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.baseui.WebPanel;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.util.UIShow;

/**
 * 我要赚钱界面
 *
 * @author gwz
 */
public class EarnMoneyAct extends BaseActivity {

    private WebPanel webPanel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        isCanBack(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_web_activity);
        String url = getIntent().getStringExtra("url");

        setBackView();
        setRightImgTextBtn(getString(R.string.earn_money_income_detail), R.drawable.f2_ic_income_list, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIShow.showIncomeDetail(EarnMoneyAct.this);
            }
        });

        LinearLayout web_container = (LinearLayout) findViewById(R.id.web_container);
        webPanel = new WebPanel(this);
        webPanel.loadUrl(url);
        webPanel.setWebListener(new WebPanel.WebListener() {
            @Override
            public void onTitle(String title) {
                setTitle(title);
            }

            @Override
            public void onLoadFinish(WebPanel.WebLoadStatus loadStatus) {
            }
        });
        web_container.addView(webPanel.getContentView());
        //validation();
    }

    //验证用户认证状态、公会状态
    private void validation() {
        UserDetail userDetail = ModuleMgr.getCenterMgr().getMyInfo();
        //如果未认证通过，弹框提示
        if (!userDetail.isVerifyAll()) {
            UIShow.showUserAuthDlg(this, Constant.OPEN_FROM_CMD);
            return;
        }
        //如果没有加入公会，弹加入公会
        if (!userDetail.isJoinGuild()) {
            UIShow.showJoinGuildDlg(this);
        }
    }

    @Override
    public void onBackPressed() {
        if (webPanel != null && webPanel.getWebView() != null && webPanel.getWebView().canGoBack()) {
            webPanel.getWebView().goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (webPanel != null) {
            webPanel.resetWebView(false);
            webPanel.getWebView().onResume();
            webPanel.getWebView().resumeTimers();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webPanel != null) {
            webPanel.getWebView().onPause();
            webPanel.getWebView().pauseTimers();
        }
    }

    @Override
    protected void onDestroy() {
        if (webPanel != null) {
            webPanel.onDestroy();
        }
        UIShow.showSureUserDlg(this, true);
        super.onDestroy();
    }

    /**
     * 关闭loading，显示webView
     */
    public void hideLoading() {
        if (webPanel != null) {
            webPanel.hideLoading();
        }
    }
}
