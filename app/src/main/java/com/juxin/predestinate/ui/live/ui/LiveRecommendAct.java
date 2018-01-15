package com.juxin.predestinate.ui.live.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.LinearLayout;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.ui.live.view.LiveListPanel;

/**
 * 更多签约推荐
 *
 * @author
 */

public class LiveRecommendAct extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_live_recommend);
        setBackView(getString(R.string.label_recommend_live));
        ((LinearLayout)findViewById(R.id.root)).addView(LiveListPanel.newRecommendPanel(this).getContentView());
    }
}
