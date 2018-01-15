package com.juxin.predestinate.ui.mail.chat;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;

/**断网
 * Created by Administrator on 2017/7/25.
 */

public class OffNetDescribeAct extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p1_offnetdescribe);
        initView();
    }

    private void initView() {
        setBackView(R.id.base_title_back);
        setTitle("未能连接到互联网");
    }
}
