package com.juxin.predestinate.ui.mail.unlock;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.juxin.library.view.CustomFrameLayout;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;

/**
 * Created by IQQ on 2017-10-31.
 */

public class UnlockMsgResultDlg extends BaseDialogFragment implements View.OnClickListener {
    private TextView tv_title, tv_title_sub, tv_btn;
    private int type;
    public UnlockMsgResultDlg(){
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.BOTTOM);
        setDialogSizeRatio(0.95,0);
        setCancelable(false);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.spread_unlock_msg_result_dlg);
        View contentView = getContentView();
        initView(contentView);
        initData();
        return contentView;
    }

    private void initView(View contentView) {
        tv_title = contentView.findViewById(R.id.spread_result_title);
        tv_title_sub = contentView.findViewById(R.id.spread_result_title_sub);
        tv_btn = contentView.findViewById(R.id.spread_result_btn);
        tv_btn.setOnClickListener(this);
    }


    private void initData() {
        switch (type){
            case 2:
                tv_title.setText("消息发送成功");
                tv_title_sub.setText("本次消费1钻石");
                tv_btn.setText("知道了");
                break;
            case 1:
                tv_title.setText("解锁成功");
                tv_title_sub.setText("恭喜你分享成功已获得当日免费畅聊的权限");
                tv_btn.setText("好的");
                break;
        }
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.spread_result_btn :
                dismiss();
            break;
        }
    }

    public void setType(int type) {
        this.type = type;
    }
}
