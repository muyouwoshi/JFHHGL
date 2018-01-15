package com.juxin.predestinate.ui.user.my;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.statistics.StatisticsUser;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.agora.model.AgoraConstant;

/**
 * 开启音、视频弹框
 *
 * @author gwz
 */
public class OpenVaDlg extends BaseDialogFragment implements View.OnClickListener {

    private TextView tvTitle;
    private TextView tvContent;
    private int vType;

    public OpenVaDlg() {
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.BOTTOM);
        setDialogSizeRatio(1, 0);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.f1_open_video_audio_dlg);
        initView();
        initData();
        return getContentView();
    }

    private void initView() {
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_sure).setOnClickListener(this);
        tvTitle = (TextView) findViewById(R.id.tv_open_va_title);
        tvContent = (TextView) findViewById(R.id.tv_open_va_content);
    }

    private void initData() {
        if (vType == AgoraConstant.RTC_CHAT_VIDEO) {
            tvTitle.setText(R.string.open_video_chat_title);
            tvContent.setText(R.string.open_video_chat_content);
        } else if (vType == AgoraConstant.RTC_CHAT_VOICE) {
            tvTitle.setText(R.string.open_audio_chat_title);
            tvContent.setText(R.string.open_audio_chat_content);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sure:
                StatisticsUser.userOpenAVDialog(vType, false);
                UIShow.showUserSetAct(getActivity(), 0);
                dismiss();
                break;
            case R.id.btn_cancel:
                StatisticsUser.userOpenAVDialog(vType, true);
                dismiss();
                break;
            default:
                break;
        }
    }

    public void setvType(int vType) {
        this.vType = vType;
    }
}
