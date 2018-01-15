package com.juxin.predestinate.ui.user.my;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.juxin.library.log.PSP;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.my.ShareTypeData;
import com.juxin.predestinate.module.local.statistics.SourcePoint;
import com.juxin.predestinate.module.local.statistics.StatisticsShareUnlock;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.baseui.WebPanel;
import com.juxin.predestinate.module.logic.baseui.custom.HorizontalListView;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.util.ShareUtil;
import com.juxin.predestinate.module.util.UIUtil;
import com.juxin.predestinate.ui.live.bean.ShareMatrial;
import com.juxin.predestinate.ui.share.EarnMoneyQQShareCallBack;
import com.juxin.predestinate.ui.share.ScreenShot;
import com.juxin.predestinate.ui.user.my.adapter.ShareCodeAdapter;
import com.juxin.predestinate.ui.user.my.view.InterceptLinearLayout;
import com.juxin.predestinate.ui.user.util.CenterConstant;
import com.juxin.predestinate.ui.utils.NoDoubleClickListener;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.Tencent;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建日期：2017/10/30
 * 描述: 分享二维码页
 *
 * @author :lc
 */
public class ShareCodeAct extends BaseActivity implements AdapterView.OnItemClickListener {

    private int type;
    private int gender;
    private int initIndex;
    private List<ShareMatrial> shareMatrials = new ArrayList<>();

    private ShareTypeData shareTypeData;

    private WebPanel webPanel;
    private ShareMatrial shareMatrial;
    private HorizontalListView horiListView;
    private ShareCodeAdapter shareCodeAdapter;
    private InterceptLinearLayout webContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        isCanBack(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spread_share_code_act);
        gender = getIntent().getIntExtra("gender", 0);
        type = getIntent().getIntExtra("type", 0);
        if (getIntent().hasExtra("shareTypeData")) {
            shareTypeData = getIntent().getParcelableExtra("shareTypeData");
        }
        initIndex = PSP.getInstance().getInt(ModuleMgr.getCommonMgr().getPrivateKey(Constant.SHARE_CODE_MODE), 0);
        initTitle();
        initView();
        initData();
    }

    private void initTitle() {
        setBackView(getString(R.string.share_code_card));
        setTitleRight(getString(R.string.share_title), new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if (webPanel != null && shareMatrial != null) {
                    if (shareTypeData == null) {
                        shareTypeData = new ShareTypeData();
                        shareTypeData.setShareType(CenterConstant.SHARE_TYPE_THREE);
                    }
                    ShareUtil.shareFunction = ShareUtil.FUNCTION_NORMAL_SHARE;
                    ShareUtil.scontent = ShareUtil.SCONTENT_USER_SHARE;
                    ShareUtil.opt = ShareUtil.OPT_DEFAULT;
                    SourcePoint.getInstance().lockShareSource(getString(R.string.share_code_card));
                    StatisticsShareUnlock.onPage_qrcodeshare_share();
                    ScreenShot.saveShareImg(ShareCodeAct.this, webPanel.getWebView(), shareTypeData, shareMatrial);
                }
            }
        });
    }

    private void initView() {
        horiListView = findViewById(R.id.list_horizontal);
        webContainer = findViewById(R.id.web_container);
        webPanel = new WebPanel(this);
        shareCodeAdapter = new ShareCodeAdapter(this, shareMatrials);

        horiListView.setItemMargin(UIUtil.dp2px(8));
        horiListView.setAdapter(shareCodeAdapter);
        horiListView.setOnItemClickListener(this);
    }

    private void loadUrl() {
        if (initIndex >= shareMatrials.size()) {
            return;
        }
        shareMatrial = shareMatrials.get(initIndex);
        String url = shareMatrials.get(initIndex).getPosterPageUrl();
        if (!TextUtils.isEmpty(url)) {
            webPanel.loadUrl(url);
            webContainer.removeAllViews();
            webContainer.addView(webPanel.getContentView());
        }
    }

    private void initData() {
        ShareUtil.getShareMatriData(new ShareUtil.GetShareMatrialCallBack() {
            @Override
            public void shareMatrialCallBack(List<ShareMatrial> list, int flag, String msg) {
                if (list != null && !list.isEmpty()) {
                    shareMatrials = list;
                    if (shareCodeAdapter != null) {
                        shareCodeAdapter.setList(shareMatrials);
                        loadUrl();
                    }
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        PSP.getInstance().put(ModuleMgr.getCommonMgr().getPrivateKey(Constant.SHARE_CODE_MODE), position);
        shareCodeAdapter.notifyDataSetChanged();
        initIndex = position;
        loadUrl();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (webPanel != null) {
            webPanel.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webPanel != null) {
            webPanel.onPause();
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
     * 调用ShareDialog分享，需要在此接收QQ分享的回调
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_QQ_SHARE || requestCode == Constants.REQUEST_QZONE_SHARE) {
            EarnMoneyQQShareCallBack callBack = new EarnMoneyQQShareCallBack();
            callBack.setChannel(requestCode == Constants.REQUEST_QQ_SHARE ? ShareUtil.CHANNEL_QQ_FRIEND : ShareUtil.CHANNEL_QQ_ZONE);
            Tencent.onActivityResultData(requestCode, resultCode, data, callBack);
        }
    }
}
