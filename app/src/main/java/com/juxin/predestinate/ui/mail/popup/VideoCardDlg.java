package com.juxin.predestinate.ui.mail.popup;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.local.statistics.StatisticsMessage;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;

/**
 * 视频卡弹窗
 * Created by duanzheng on 2017/8/3.
 */
public class VideoCardDlg extends BaseDialogFragment implements View.OnClickListener {

    private TextView des2;
    private TextView use;
    private TextView no_use;
    private TextView count_tv;
    private TextView des;

    private long to_uid;
    private int count = 0;
    private int length = 0;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.use:
                StatisticsMessage.abTestUseVideoCard(to_uid, count);
                if (mBackCall != null) {
                    mBackCall.call(true);
                }
                dismiss();
                break;
            case R.id.no_use:
                Statistics.userBehavior(SendPoint.alert_videocard_donotuse, to_uid);
                if (mBackCall != null) {
                    mBackCall.call(false);
                }
                dismiss();
                break;
        }
    }

    public interface BackCall {
        void call(boolean isUseVideoCard);
    }

    private BackCall mBackCall;

    public void setCall(BackCall call) {
        mBackCall = call;
    }

    public VideoCardDlg() {
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.BOTTOM);
        setDialogSizeRatio(-2, -1);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.videocard_dialog);
        initView();
        initData();
        return getContentView();
    }

    private void initData() {
        use.setOnClickListener(this);
        no_use.setOnClickListener(this);
        count_tv.setText("X" + count + "张");
        des2.setText("使用此卡,可选择任意一个你喜欢的女孩免费通话" + length + "''");
        des.setText("您有" + count + "张视频卡,可免费视频通话");
    }

    /**
     * 设置视频卡弹框界面显示参数
     *
     * @param count  拥有视频卡的数目
     * @param length 视频卡可用通话时长
     */
    public void setCountTimeLength(long to_uid, int count, int length) {
        this.to_uid = to_uid;
        this.count = count;
        this.length = length;
    }

    private void initView() {
        des = (TextView) findViewById(R.id.des);
        des2 = (TextView) findViewById(R.id.des2);
        use = (TextView) findViewById(R.id.use);
        no_use = (TextView) findViewById(R.id.no_use);
        count_tv = (TextView) findViewById(R.id.count);
    }
}
