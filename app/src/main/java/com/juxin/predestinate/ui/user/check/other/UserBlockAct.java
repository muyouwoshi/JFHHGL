package com.juxin.predestinate.ui.user.check.other;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;

/**
 * 用户封禁提示
 * Created by Su on 2017/5/18.
 */
public class UserBlockAct extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f1_user_block_act);
        setBackView(getString(R.string.user_tip_blocked_title));
    }
}
