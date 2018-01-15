package com.juxin.predestinate.ui.utils;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.juxin.predestinate.R;


/**
 * 插件下载弹窗
 */
public class DownloadPluginFragment extends DialogFragment implements View.OnClickListener {

    private ProgressBar progressBar;
    private boolean isLiveStyle = false;

    public DownloadPluginFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);

        View rootView = inflater.inflate(R.layout.fragment_download_plugin, container);
        View btnClose = rootView.findViewById(R.id.btnClose);
        View iv_plugin = rootView.findViewById(R.id.iv_plugin);
        TextView tv_title = (TextView) rootView.findViewById(R.id.tv_title);
        TextView tvContent = (TextView) rootView.findViewById(R.id.tvContent);
        progressBar = (ProgressBar) rootView.findViewById(R.id.pbDownload);

        if (isLiveStyle) {
            btnClose.setVisibility(View.GONE);
            iv_plugin.setVisibility(View.GONE);
            tv_title.setText("正在下载美女的直播间");
            tvContent.setText("请稍候...");
        }

        btnClose.setOnClickListener(this);
        return rootView;
    }

    /**
     * 设置是否为直播下载风格
     *
     * @param isLiveStyle 是否为直播下载风格
     */
    public void setLiveStyle(boolean isLiveStyle) {
        this.isLiveStyle = isLiveStyle;
    }

    public void updateProgress(int progress) {
        if (progressBar != null) {
            progressBar.setProgress(progress);
        }
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    public void show(FragmentActivity activity){
        synchronized (this) {
            if (activity == null || activity.isFinishing() || isAdded()) {
                return;
            }
            try {
                FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                transaction.add(DownloadPluginFragment.this, activity.toString());
                transaction.commitAllowingStateLoss();
                activity.getSupportFragmentManager().executePendingTransactions();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
