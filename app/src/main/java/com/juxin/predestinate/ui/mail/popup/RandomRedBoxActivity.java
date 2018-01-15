package com.juxin.predestinate.ui.mail.popup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.juxin.library.log.PToast;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.application.ModuleMgr;

/**
 * 聊天随机红包弹窗
 * Created by sks on 2016/12/28.
 */
public class RandomRedBoxActivity extends Activity implements View.OnClickListener {

    private ImageView mBackLightIv;

    private int red_log_id;
    private String mMsg;

    private Animation bgdRotationAnimation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.f1_dialog_random_red_box);
        Intent intent = getIntent();
        if (intent != null) {
            red_log_id = intent.getIntExtra("red_log_id", -1);
            mMsg = intent.getStringExtra("msg");
        }
        initView();
    }

    private void initView() {
        mBackLightIv = (ImageView) findViewById(R.id.iv_random_red_box_back_light);
        Button mGetBtn = (Button) findViewById(R.id.btn_random_red_box_get);
        TextView msgTv = (TextView) findViewById(R.id.tv_random_red_box_msg);

        if (!TextUtils.isEmpty(mMsg)) {
            msgTv.setText(mMsg);
        }

        mGetBtn.setOnClickListener(this);
        Window win = getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        win.getDecorView().setBackgroundResource(R.color.transparent);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        win.setAttributes(lp);

        bgdRotationAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotating_linear);
        mBackLightIv.setAnimation(bgdRotationAnimation);
        mBackLightIv.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bgdRotationAnimation.start();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_random_red_box_get:
                if (red_log_id != -1) {
                    ModuleMgr.getCommonMgr().receiveChatRedBag(red_log_id);
                } else {
                    PToast.showShort(getString(R.string.received_error));
                }
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
    }

    ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            if (bgdRotationAnimation != null) {
                bgdRotationAnimation.cancel();
                mBackLightIv.clearAnimation();
                mBackLightIv.setAnimation(bgdRotationAnimation);
                bgdRotationAnimation.start();
            }
        }
    };
}
