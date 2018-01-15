package com.juxin.predestinate.ui.tips;

import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.juxin.library.utils.ViewUtils;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.baseui.custom.BottomPopupSupport;
import com.juxin.predestinate.module.util.PreferenceUtils;

/**
 * @author Mr.Huang
 * @date 2017/9/12
 */
public class PeerageGetVideoCardDialog extends BottomPopupSupport {

    private TextView tv_tip, tv_time;
    private int time;
    private String title = "恭喜您，获得了%d秒的视频聊天卡";

    public PeerageGetVideoCardDialog(FragmentActivity activity) {
        super(activity);
        getWindow().setGravity(Gravity.CENTER);
    }

    @Override
    protected View makeContentView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_peerage_get_video_card, null);
        tv_tip = ViewUtils.findById(view, R.id.tv_tip);
        tv_tip.setText(String.format(title, time));
        tv_time = ViewUtils.findById(view, R.id.tv_time);
        tv_time.setText(String.valueOf(time));
        tv_time.append("''");
        view.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceUtils.remoteFreeVideInfo();
                dismiss();
            }
        });
        return view;
    }

    public void show(int time) {
        this.time = time;
        super.show();
    }
}
