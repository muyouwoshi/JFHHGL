package com.juxin.predestinate.ui.live.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.logic.config.Hosts;
import com.juxin.predestinate.module.util.CommonUtil;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.utils.NoDoubleClickListener;

/**
 * 开播认证
 * Created by chengxiaobo on 2017/11/1.
 */

public class LiveAuthDialog extends BaseDialogFragment {

    private Click mClick;

    private TextView mTvAuth;
    private ImageView mIvOk;
    private TextView mTvCancel;
    private TextView mTvConfirm;

    private boolean mIsOk = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        setContentView(R.layout.live_auth_dialog);
        initView();
        initViewState();

        return getContentView();
    }

    private void initView() {

        mTvAuth = (TextView) findViewById(R.id.tv_auth);
        mIvOk = (ImageView) findViewById(R.id.iv_is_ok);
        mTvCancel = (TextView) findViewById(R.id.tv_cancel);
        mTvConfirm = (TextView) findViewById(R.id.tv_confirm);

        //非纯净包隐藏直播协议同意选择框和直播协议查看链接
        if (!CommonUtil.isPurePackage()) {
            findViewById(R.id.ly_agree).setVisibility(View.GONE);
            mIsOk = true;
            updateConfirm();
        }
    }

    private void initViewState() {

        mTvAuth.setOnClickListener(myClickListener);
        mIvOk.setOnClickListener(myClickListener);
        mTvCancel.setOnClickListener(myClickListener);
        mTvConfirm.setOnClickListener(myClickListener);

        updateConfirm();

    }

    private void updateConfirm() {
        if (mIsOk) {
            mIvOk.setImageResource(R.drawable.live_auth_true);
            mTvConfirm.setClickable(true);
            mTvConfirm.setTextColor(Color.parseColor("#333333"));
        } else {

            mIvOk.setImageResource(R.drawable.live_auth_false);
            mTvConfirm.setClickable(false);
            mTvConfirm.setTextColor(Color.parseColor("#999999"));
        }

    }

    public void setClick(Click mClick) {
        this.mClick = mClick;
    }

    NoDoubleClickListener myClickListener = new NoDoubleClickListener() {

        @Override
        public void onNoDoubleClick(View v) {

            switch (v.getId()) {
                case R.id.tv_auth:
                    UIShow.showWebActivity(getActivity(), Hosts.H5_ANCHOR_AGREEMENT);
                    break;
                case R.id.iv_is_ok:
                    mIsOk = !mIsOk;
                    updateConfirm();
                    break;
                case R.id.tv_cancel:
                    if (mClick != null) {
                        mClick.cancel();
                    }
                    LiveAuthDialog.this.dismiss();
                    break;
                case R.id.tv_confirm:
                    if (mClick != null) {
                        mClick.confirm();
                    }
                    LiveAuthDialog.this.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

    public interface Click {

        void cancel();

        void confirm();
    }
}
