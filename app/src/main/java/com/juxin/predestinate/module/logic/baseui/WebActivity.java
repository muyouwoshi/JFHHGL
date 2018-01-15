package com.juxin.predestinate.module.logic.baseui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.juxin.library.log.PSP;
import com.juxin.library.log.PToast;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.my.ShareTypeData;
import com.juxin.predestinate.module.local.statistics.SourcePoint;
import com.juxin.predestinate.module.local.statistics.StatisticsShareUnlock;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.config.Hosts;
import com.juxin.predestinate.module.util.ShareUtil;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.share.ScreenShot;
import com.juxin.predestinate.ui.user.util.CenterConstant;
import com.juxin.predestinate.ui.utils.NoDoubleClickListener;

/**
 * 通用网页
 *
 * @author ZRP
 * @date 2016/12/9
 */
public class WebActivity extends BaseActivity implements ScreenShot.SaveCallBack {

    private WebPanel webPanel;
    private View layoutLoading;
    private ShareTypeData shareTypeData;

    private String url;                 // 页面链接
    private String pageTitle;           // 页面标题
    private boolean hasShowShareReward;

    private static final int NORMAL_WEB = 1;        // 普通网页（普通导航条）
    private static final int FULLSCREEN_WEB = 2;    // 全屏网页
    private static final int SHARE_WEB = 3;         // 分享网页（带分享按钮的导航条）
    public static final int NORMAL_RIGHT = 4;       // 右侧收益明细

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        isCanBack(false);
        int type = getIntent().getIntExtra("type", NORMAL_WEB);
        url = getIntent().getStringExtra("url");
        if (getIntent().hasExtra("shareTypeData")) {
            shareTypeData = getIntent().getParcelableExtra("shareTypeData");
        }

        // 根据type确定是否为全屏页面
        if (type == FULLSCREEN_WEB) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_web_activity);

        initTitle(type);
        initView(type, url);
    }

    /**
     * 初始化标题栏
     *
     * @param type 页面打开类型
     */
    private void initTitle(int type) {
        // 根据type处理导航条展示
        View baseTitle = findViewById(R.id.base_title);
        baseTitle.setVisibility(type == FULLSCREEN_WEB ? View.GONE : View.VISIBLE);
        if (type == SHARE_WEB) {
            //View.OnClickListener 替换成  NoDoubleClickListener 防止多次点击，生成多次图片
            setTitleRightImg(R.drawable.spread_share_btn_black, new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    saveShareImg();
                }
            });
        }
        if (type == NORMAL_RIGHT) {
            setRightImgTextBtn("收益明细", 0, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UIShow.showShareForPromotion(WebActivity.this);
                    StatisticsShareUnlock.onPage_sharetomakemoney_profitdetail();
                }
            });
        }
        setBackView();
    }

    private void initView(int type, String url) {
        LinearLayout webContainer = findViewById(R.id.web_container);
        webPanel = new WebPanel(this, false, true);
        webPanel.loadUrl(url);
        webPanel.setWebListener(new WebPanel.WebListener() {
            @Override
            public void onTitle(String title) {
                pageTitle = title;
                setTitle(title);
            }

            @Override
            public void onLoadFinish(WebPanel.WebLoadStatus loadStatus) {
            }
        });
        webContainer.addView(webPanel.getContentView());

        layoutLoading = findViewById(R.id.layout_loading);
        // 非全屏加载页面隐藏loading
        layoutLoading.setVisibility(type == FULLSCREEN_WEB ? View.VISIBLE : View.GONE);
    }

    /**
     * 首次进入分享赚钱页面弹出分享奖励
     */
    private void isShowShareReward() {
        if (hasShowShareReward) {
            return;
        }
        hasShowShareReward = true;
        if (!TextUtils.isEmpty(url) && url.equals(Hosts.H5_SHARE_EXTENSION)) {
            //是否弹出过分享奖励
            boolean isShowAgin = PSP.getInstance().getBoolean(ModuleMgr.getCommonMgr().getPrivateKey(Constant.SHARE_REWARD), false);
            if (!isShowAgin) {
                UIShow.showShareRewardDlg(this);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (webPanel != null && webPanel.canGoBack()) {
//            webPanel.getWebView().goBack();
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
        super.onDestroy();
    }

    /**
     * 关闭loading，显示webView
     */
    public void hideLoading() {
        layoutLoading.setVisibility(View.GONE);
        if (webPanel != null) {
            webPanel.hideLoading();
        }
    }

    /**
     * 保存分享图片
     */
    private void saveShareImg() {
        if (webPanel != null) {
            if (shareTypeData == null) {
                if (url.contains("pages/share/share2.html")) {  //我要赚钱分享
                    shareTypeData = new ShareTypeData();
                    shareTypeData.setShareType(CenterConstant.SHARE_TYPE_SECOND);
                    shareTypeData.setEarnMoney(ModuleMgr.getCommonMgr().getMyEarnInfo().getFEstimatedincome() + "");
                    SourcePoint.getInstance().lockShareSource(pageTitle);
                } else {
                    shareTypeData = new ShareTypeData(CenterConstant.SHARE_TYPE_SECOND);
                }
            }
            ShareUtil.shareFunction = ShareUtil.FUNCTION_NORMAL_SHARE;
            ShareUtil.scontent = ShareUtil.SCONTENT_USER_SHARE;
            ShareUtil.opt = ShareUtil.OPT_DEFAULT;
            ScreenShot.saveShareImg(WebActivity.this, webPanel.getContentView(), shareTypeData, null);
        } else {
            PToast.showShort(getString(R.string.share_act_nodata));
        }
    }

    @Override
    public void shareSuccess() {
        PToast.showShort(getString(R.string.share_act_success));
        //启动微信
        UIShow.startWexin();
    }

    @Override
    public void shareFail() {
        PToast.showShort(getString(R.string.share_act_fail));
    }

}
