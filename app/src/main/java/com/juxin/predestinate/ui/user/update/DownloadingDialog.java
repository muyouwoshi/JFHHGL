package com.juxin.predestinate.ui.user.update;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juxin.library.view.DownloadProgressView;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;

/**
 * 下载进度dialog
 * Created by ZRP on 2017/4/1.
 */
public class DownloadingDialog extends BaseDialogFragment {

    private DownloadProgressView progress_bar;
    private TextView loading_txt;

    public DownloadingDialog() {
        settWindowAnimations(R.style.AnimScaleInScaleOutOverShoot);
        setGravity(Gravity.CENTER);
        setDialogSizeRatio(-1, -1);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.p1_app_download_dialog);

        View contentView = getContentView();
        initView(contentView);
        return contentView;
    }

    private void initView(View view) {
        progress_bar = (DownloadProgressView) view.findViewById(R.id.progress_bar);
        loading_txt = (TextView) view.findViewById(R.id.loading_txt);
    }

    /**
     * 更新下载进度
     */
    public void updateProgress(int percent) {
        progress_bar.setProgress(percent);
    }
}
