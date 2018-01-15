package com.juxin.predestinate.ui.user.my.CommonDlg;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.library.observe.PObserver;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.config.base.NobilityList;
import com.juxin.predestinate.module.local.mail.MyChumTaskList;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.util.UIUtil;
import com.juxin.predestinate.ui.user.my.adapter.FriendTaskAdapter;
import com.juxin.predestinate.ui.utils.NoDoubleClickListener;


/**
 * 私聊页密友任务下拉panel
 * Created by zm on 2017/7/17
 */
public class MarqueeView extends LinearLayout {

    private Context mContext;
    private ImageView imgHead, imgGiftIcon;
    private TextView tvFromName, tvToName, tvGiftName;

    public MarqueeView(Context context) {
        this(context, null);
    }

    public MarqueeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarqueeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.f2_marquee_panel_two, this);
        initView();
    }

    private void initView() {
        imgHead = (ImageView) findViewById(R.id.marquee_panel_two_head);
        imgGiftIcon = (ImageView) findViewById(R.id.marquee_panel_img_gift_icon);
        tvFromName = (TextView) findViewById(R.id.marquee_panel_tv_from_name);
        tvToName = (TextView) findViewById(R.id.marquee_panel_tv_to_name);
        tvGiftName = (TextView) findViewById(R.id.marquee_panel_tv_gift_name);
    }

    public void setData(MyChumTaskList info, long tuid, String channel_uid) {

    }

}