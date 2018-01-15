package com.juxin.predestinate.ui.live.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;

/**
 * <使用/> <br/>
 * new LiveBaseDialog().setInformation.setConfirmText().setCancelText()
 *                      .setOnItemClickListener()
 *                      .showDialog();
 *
 * Created by 周杰 on 2017/7/20.
 */

public class LiveBaseDialog extends BaseDialogFragment implements View.OnClickListener{

    private String confirmStr,cancelStr,infoStr;
    private ImageView titleView;
    private TextView infoTv;
    private TextView confirmBtn,cancelBtn;
    private OnItemClickListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.live_base_dialog);
        initView();
        initViewState();
        return getContentView();
    }

    private void initView() {
        titleView = (ImageView) findViewById(R.id.title);
        infoTv = (TextView) findViewById(R.id.message);
        confirmBtn = (TextView) findViewById(R.id.confirm_bt);
        cancelBtn = (TextView) findViewById(R.id.cancel_bt);

        confirmBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    private void initViewState() {
        if(infoStr!=null )infoTv.setText(infoStr);
        if(confirmStr!=null)confirmBtn.setText(confirmStr);
        if(cancelStr!=null)cancelBtn.setText(cancelStr);
    }


    public LiveBaseDialog setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
        return this;
    }

    /**
     * 设置确定按钮文字
     * @param string 提示信息
     */
    public LiveBaseDialog setConfirmText(String string){
        confirmStr = string;
        if(confirmBtn!=null)confirmBtn.setText(confirmStr);
        return this;
    }

    /**
     * 设置取消按钮文字
     * @param string 提示信息
     */
    public LiveBaseDialog setCancelText(String string){
        cancelStr = string;
        if(cancelBtn!=null)cancelBtn.setText(cancelStr);
        return this;
    }

    /**
     * 设置提示信息
     * @param string 提示信息
     */
    public LiveBaseDialog setInformation(String string){
        infoStr = string;
        if(infoTv!=null)infoTv.setText(infoStr);
        return this;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.confirm_bt:
                if(mListener !=null) mListener.onConfirm();
                break;
            case R.id.cancel_bt:
                if(mListener !=null) mListener.onCancel();
                break;
        }
        dismiss();
    }

    public interface OnItemClickListener{
        /** 点击确定时触发*/
        void onConfirm();
        /** 点击取消时触发*/
        void onCancel();
    }
}
