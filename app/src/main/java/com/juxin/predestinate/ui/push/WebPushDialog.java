package com.juxin.predestinate.ui.push;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.juxin.library.log.PLogger;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.logic.baseui.WebPanel;

@SuppressLint("ValidFragment")
public class WebPushDialog extends BaseDialogFragment {

    private FragmentActivity activity;
    private String webUrl;

    public WebPushDialog() {
    }

    public WebPushDialog(FragmentActivity activity, String webUrl, double rate) {
        PLogger.d("---WebPushDialog--->url：" + webUrl + "，rate：" + rate);
        this.activity = activity;
        this.webUrl = webUrl;
        settWindowAnimations(R.style.AnimScaleInScaleOutOverShoot);
        setGravity(Gravity.CENTER);
        setCanceledOnTouchOutside(true);

        WindowManager windowManager = activity.getWindow().getWindowManager();
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        int width = (int) (dm.widthPixels * 0.8);
        setSize(width, (int) (width / rate));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.f1_web_push_dialog);

        View contentView = getContentView();
        initView(contentView);
        return contentView;
    }

    /**
     * 初始化界面
     */
    private void initView(View view) {
        LinearLayout web_container = (LinearLayout) view.findViewById(R.id.web_container);
        WebPanel webPanel = new WebPanel(activity);
        webPanel.loadUrl(webUrl);
        web_container.removeAllViews();
        web_container.addView(webPanel.getContentView());
    }
}
