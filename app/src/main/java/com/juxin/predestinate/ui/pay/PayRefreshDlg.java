package com.juxin.predestinate.ui.pay;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.BaseUtil;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.module.util.UIUtil;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 订单确认
 * Created by siow on 2017/7/3.
 */

public class PayRefreshDlg extends BaseActivity implements View.OnClickListener {

    private View btn_close;
    private Timer timer;
    private long time;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f1_pay_refresh_dlg);
        initView();
        initData();
    }

    private void initView() {
        Window win = getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        win.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setAttributes(lp);

        setFinishOnTouchOutside(true);

        btn_close = findViewById(R.id.btn_close);
        btn_close.setOnClickListener(this);

        final TextView tvTimer = (TextView) findViewById(R.id.tv_timer);

        time = System.currentTimeMillis();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                MsgMgr.getInstance().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (time == 0) return;
                        if (UIUtil.isActDestroyed(PayRefreshDlg.this)) return;
                        long t = Math.max(0, 60 - (System.currentTimeMillis() - time) / 1000);
                        if (t <= 1) {
                            cancelTimer();
                            UIShow.showResultDlg(PayRefreshDlg.this, 2);
                            PayRefreshDlg.this.finish();
                        }
                        tvTimer.setText(t + "秒");
                    }
                });
            }
        }, 0, 1000);
    }

    private void initData() {
        if (TextUtils.isEmpty(PayConst.out_trade_no)) {
            finish();
            return;
        }
        reqPayComplete(PayConst.out_trade_no);
    }

    private void reqPayComplete(final String out_trade_no) {
        long t = Math.max(0, 60 - (System.currentTimeMillis() - time) / 1000);
        if (t <= 1) return;

        ModuleMgr.getCommonMgr().reqPayComplete(out_trade_no, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                if (UIUtil.isActDestroyed(PayRefreshDlg.this)) return;

                long t = Math.max(0, 60 - (System.currentTimeMillis() - time) / 1000);
                if (t <= 1) return;
                int stat = -1;//支付结果 1支付成功 2支付失败 3订单初始化 -1暂无此订单
                if (response.isOk()) {
                    JSONObject jo = response.getResponseJson();
                    JSONObject resJo = jo.optJSONObject("res");
                    if (resJo != null)
                        stat = resJo.optInt("stat", -1);
                }

                switch (stat) {
                    case 1:
                    case 2:
                        cancelTimer();
                        if (BaseUtil.isTopActivity(PayRefreshDlg.this, PayRefreshDlg.this.getClass().getName()))
                            UIShow.showResultDlg(PayRefreshDlg.this, stat);
                        PayRefreshDlg.this.finish();
                        //刷新个人资料
                        MsgMgr.getInstance().sendMsg(MsgType.MT_Update_MyInfo, null);
                        break;
                    default:
                        MsgMgr.getInstance().delay(new Runnable() {
                            @Override
                            public void run() {
                                reqPayComplete(out_trade_no);
                            }
                        }, 5000);
                        break;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimer();
    }

    public void cancelTimer(){
        if (timer != null) timer.cancel();
        time = 0;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_close:
                cancelTimer();
                finish();
                break;
        }
    }

    @Override
    public boolean isFullScreen() {
        return true;
    }
}
