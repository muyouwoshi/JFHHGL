package com.juxin.predestinate.ui.live.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseFragment;
import com.juxin.predestinate.module.logic.baseui.WebPanel;
import com.juxin.predestinate.module.logic.config.Hosts;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.GamePlayer;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 直播列表
 *
 * @author gwz
 */
public class LiveListFragment extends BaseFragment {

    private WebPanel webPanel;
    private LinearLayout web_container;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.f1_main_web_fragment);
        setTitle(getString(R.string.main_btn_live));
        initView();
        initWebView(Hosts.H5_LIVE_LIST);
        initData();
        return getContentView();
    }

    private void initData() {
        if (ModuleMgr.getCenterMgr().getMyInfo().isMan())
            reqBatchAuth();
    }

    private void initView() {
        web_container = (LinearLayout) findViewById(R.id.web_container);
    }

    private void initWebView(String urlParm) {
        webPanel = new WebPanel(getActivity());
        webPanel.loadUrl(urlParm);
        web_container.removeAllViews();
        web_container.addView(webPanel.getContentView());
    }

    private void reqBatchAuth() {
        ModuleMgr.getHttpMgr().reqPostNoCacheHttp(UrlParam.liveBatchAuth, null, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (response.isOk()) {
                    try {
                        JSONObject resJo = response.getResponseJson().getJSONObject("res");
                        boolean isFull = resJo.optBoolean("ok");
                        if (!isFull)
                            initWebView(ModuleMgr.getCommonMgr().getCommonConfig().getCommon().getSquare_url());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    initWebView(ModuleMgr.getCommonMgr().getCommonConfig().getCommon().getSquare_url());
                }
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (webPanel != null) webPanel.resetWebView(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (webPanel != null) {
            webPanel.getWebView().onResume();
            webPanel.getWebView().resumeTimers();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (webPanel != null) {
            webPanel.getWebView().onPause();
            webPanel.getWebView().pauseTimers();
        }
        GamePlayer.getInstance().stop();
        GamePlayer.getInstance().setPlayFlag(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webPanel != null) webPanel.onDestroy();
    }
}
