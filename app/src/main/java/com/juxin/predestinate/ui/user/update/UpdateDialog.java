package com.juxin.predestinate.ui.user.update;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.juxin.library.dir.DirType;
import com.juxin.library.image.ImageLoader;
import com.juxin.library.log.PToast;
import com.juxin.library.request.DownloadListener;
import com.juxin.library.utils.APKUtil;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.update.AppUpdate;
import com.juxin.predestinate.bean.center.update.UpgradeSource;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.StatisticsUser;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.util.UIShow;

/**
 * 软件升级dialog
 *
 * @author ZRP
 */
public class UpdateDialog extends BaseDialogFragment implements DownloadListener, View.OnClickListener {

    private FragmentActivity activity;
    private DownloadingDialog downloadingDialog;

    private ImageView updateBg, updateIcon;
    private TextView updateInfo, updateHint;
    private View updateCancel, updateSubmit;

    private AppUpdate appUpdate;            //软件更新信息
    private UpgradeSource upgradeSource;    //升级弹窗来源

    public UpdateDialog() {
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.BOTTOM);
        setDialogSizeRatio(0.9, 0);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.common_update_dialog);

        View contentView = getContentView();
        initView(contentView);
        return contentView;
    }

    private void initView(View view) {
        downloadingDialog = new DownloadingDialog();

        updateBg = view.findViewById(R.id.update_bg);
        updateInfo = view.findViewById(R.id.update_info);
        updateIcon = view.findViewById(R.id.update_icon);
        updateHint = view.findViewById(R.id.update_hint);

        updateCancel = view.findViewById(R.id.update_cancel);
        updateSubmit = view.findViewById(R.id.update_submit);

        updateCancel.setOnClickListener(this);
        updateSubmit.setOnClickListener(this);

        initData();
    }

    private void initData() {
        if (appUpdate == null) {
            return;
        }
        updateInfo.setText(appUpdate.getSummary());
        ImageLoader.loadFitCenter(getContext(), appUpdate.getTop_img_url(),
                updateBg, R.drawable.spread_update_bg, R.drawable.spread_update_bg);
        if (TextUtils.isEmpty(appUpdate.getMid_img_url())) {
            updateIcon.setVisibility(View.GONE);
        } else {
            updateIcon.setVisibility(View.VISIBLE);
            ImageLoader.loadFitCenter(getContext(), appUpdate.getMid_img_url(), updateIcon);
        }
        if (TextUtils.isEmpty(appUpdate.getFoot_text())) {
            updateHint.setVisibility(View.GONE);
        } else {
            updateHint.setVisibility(View.VISIBLE);
            updateHint.setText(appUpdate.getFoot_text());
        }
    }

    /**
     * 初始化数据
     *
     * @param appUpdate     软件升级信息
     * @param upgradeSource 升级弹窗来源
     */
    public void setData(AppUpdate appUpdate, UpgradeSource upgradeSource) {
        this.appUpdate = appUpdate;
        this.upgradeSource = upgradeSource;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.update_cancel:
                StatisticsUser.upgradeDialog(SendPoint.page_upgrade_box_cancel, appUpdate, upgradeSource);
                dismiss();
                if (appUpdate.getForce() == 1) {// 强杀进程
                    UIShow.simulateExit(getContext());
                    return;
                }

                //author Mr.Huang
                //当取消更新了继续检查其他消息
                if (UpgradeSource.Main == upgradeSource) {
                    UIShow.showSayHelloDialog(activity);//判断男性展示一键打招呼弹窗
                    UIShow.showMakeMoneyDlg(activity);//判断女性展示我要赚钱弹窗
                }
                break;
            case R.id.update_submit:
                StatisticsUser.upgradeDialog(SendPoint.page_upgrade_box_confirm, appUpdate, upgradeSource);
                updateCancel.setEnabled(false);
                updateSubmit.setEnabled(false);

                dismiss();
                downloadingDialog.showDialog(activity);

                String fileName = DirType.getApkDir() + appUpdate.getPackage_name()
                        + "_" + appUpdate.getVersion() + ".apk";
                ModuleMgr.getHttpMgr().download(appUpdate.getUrl(), fileName, this);
                break;
            default:
                break;
        }
    }

    @Override
    public void onStart(String url, String fileName) {
    }

    @Override
    public void onProcess(String url, int process, long size) {
        downloadingDialog.updateProgress(process);
    }

    @Override
    public void onSuccess(String url, String filePath) {
        downloadingDialog.dismissAllowingStateLoss();
        if (appUpdate.getForce() == 1) {
            showDialog(activity);// 重新调起升级弹窗
        }
        APKUtil.installAPK(activity, filePath);
    }

    @Override
    public void onFail(String url, Throwable throwable) {
        PToast.showShort(App.getContext().getString(R.string.update_download_error));
        updateCancel.setEnabled(true);
        updateSubmit.setEnabled(true);

        // 重新调起升级弹窗
        downloadingDialog.dismissAllowingStateLoss();
        showDialog(activity);
    }

    @Override
    public void showDialog(FragmentActivity context) {
        super.showDialog(context);
        StatisticsUser.upgradeDialog(SendPoint.page_upgrade_box_pageview, appUpdate, upgradeSource);
        this.activity = context;
    }
}
