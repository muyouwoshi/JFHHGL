package com.juxin.predestinate.module.local.msgview.chatview.input;

import android.content.Context;
import android.support.v4.view.ViewPager;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.msgview.ChatAdapter;
import com.juxin.predestinate.module.local.msgview.chatview.base.ChatViewPanel;
import com.juxin.predestinate.module.logic.baseui.custom.PointsView;

/**
 * Created by Kind on 2017/3/30.
 */

public class ChatBaseSmilePanel extends ChatViewPanel {

    private PointsView pointsView = null;

    public ChatBaseSmilePanel(Context context, ChatAdapter.ChatInstance chatInstance) {
        super(context, chatInstance);
    }

    public void initPointsView(ViewPager vp, int totalNum, boolean firstShow) {
        pointsView = (PointsView) findViewById(R.id.chat_points);
        pointsView.setTotalPoints(totalNum, firstShow);
        vp.setCurrentItem(pointsView.selectIndex);
        vp.setOnPageChangeListener(pointsView);
    }
}
