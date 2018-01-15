package com.juxin.predestinate.ui.live.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.library.log.PLogger;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.module.util.UIUtil;
import com.juxin.predestinate.ui.live.bean.LiveStartLiveBean;

/**
 * 开播提醒的View
 * Created by chengxiaobo on 2017/10/31.
 */

public class LiveStartLiveTipView {

    public static void showTip(final Activity activity, final LiveStartLiveBean bean) {

        final WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        // 更多type：https://developer.android.com/reference/android/view/WindowManager.LayoutParams.html#TYPE_PHONE
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.format = PixelFormat.TRANSLUCENT;
        // 更多falgs:https://developer.android.com/reference/android/view/WindowManager.LayoutParams.html#FLAG_NOT_FOCUSABLE
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.gravity = Gravity.TOP;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = UIUtil.dip2px(activity, 33);

        LayoutInflater inflater = activity.getLayoutInflater();
        final View layout = inflater.inflate(R.layout.live_start_live_tip, null, false);
        final TextView textView = (TextView) layout
                .findViewById(R.id.tv_start_live_tip);
        textView.setText(bean.getMct());
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (layout != null && windowManager != null) {
                    windowManager.removeView(layout);
                }
            }
        };
        textView.postDelayed(runnable, 3000);
        LinearLayout ll = (LinearLayout) layout.findViewById(R.id.ll_start_live_tip);
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PLogger.e("=========Toast.Click======");
//                UIShow.showLiveAct(activity, bean.getRoom_id(), bean.getRoom_id());
                if (layout != null && windowManager != null) {
                    windowManager.removeView(layout);
                    textView.removeCallbacks(runnable);
                }

            }
        });
        windowManager.addView(layout, params);
    }
}
