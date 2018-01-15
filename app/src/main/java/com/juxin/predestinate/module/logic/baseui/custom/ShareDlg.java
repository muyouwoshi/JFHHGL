package com.juxin.predestinate.module.logic.baseui.custom;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.statistics.StatisticsSpread;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.ui.share.ScreenShot;

/**
 * Created by zhang on 2017/9/19.
 */

public class ShareDlg extends BaseDialogFragment implements View.OnClickListener {

    private ImageView btn_close, btn_save;

    private ScreenShot.SaveCallBack saveCallBack = null;

    private Bitmap save_bitmap = null;

    public ShareDlg() {
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.BOTTOM);
        setDialogSizeRatio(0, 0);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.spread_share_save_dlg);
        View contentView = getContentView();
        initView(contentView);
        return contentView;
    }

    private void initView(View contentView) {
        btn_close = (ImageView) contentView.findViewById(R.id.share_dlg_close);
        btn_save = (ImageView) contentView.findViewById(R.id.share_dlg_btn);

        btn_close.setOnClickListener(this);
        btn_save.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_dlg_close:
                StatisticsSpread.onPage_share_share_close();
                dismiss();
                break;
            case R.id.share_dlg_btn:
                StatisticsSpread.onPage_share_share_save();
                saveImage();
                break;
        }
    }

    private void saveImage() {
        if (getSave_bitmap() != null && getSaveCallBack() != null) {
            ScreenShot.savePicToShare(getSave_bitmap(), getSaveCallBack());
            dismiss();
        } else {
            getSaveCallBack().shareFail();
            dismiss();
        }
    }


    public ScreenShot.SaveCallBack getSaveCallBack() {
        return saveCallBack;
    }

    public void setSaveCallBack(ScreenShot.SaveCallBack saveCallBack) {
        this.saveCallBack = saveCallBack;
    }


    public Bitmap getSave_bitmap() {
        return save_bitmap;
    }

    public void setSave_bitmap(Bitmap save_bitmap) {
        this.save_bitmap = save_bitmap;
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (getSave_bitmap() != null && !getSave_bitmap().isRecycled()) {
            getSave_bitmap().recycle();
            setSave_bitmap(null);
        }
    }
}
