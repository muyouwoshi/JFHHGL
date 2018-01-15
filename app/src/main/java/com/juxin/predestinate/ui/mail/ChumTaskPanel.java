package com.juxin.predestinate.ui.mail;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import com.juxin.library.log.PToast;
import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.config.base.NobilityList;
import com.juxin.predestinate.module.local.mail.MyChum;
import com.juxin.predestinate.module.local.mail.MyChumTaskList;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIShow;

/**
 * 密友任务
 * Created by Kind on 2017/7/27.
 */

public class ChumTaskPanel extends BasePanel {

    private TextView chum_task_title, chum_task_desc, chum_task_btn;

    private MyChumTaskList.MyChumTask myChumTask;
    private MyChum myChum;

    public ChumTaskPanel(Context context, MyChumTaskList.MyChumTask myChumTask, MyChum myChum) {
        super(context);
        setContentView(R.layout.f2_mychum_item_task);
        this.myChumTask = myChumTask;
        this.myChum = myChum;
        if (myChumTask == null || myChum == null) {
            PToast.showShort("任务出错！");
            return;
        }

        initView();
        initData();
    }

    private void initView() {
        chum_task_title = (TextView) findViewById(R.id.chum_task_title);
        chum_task_desc = (TextView) findViewById(R.id.chum_task_desc);
        chum_task_btn = (TextView) findViewById(R.id.chum_task_btn);
    }

    private void initData() {
        if (myChumTask.getTriggerType() == 1) {//语音视频单独处理显示
//            NobilityList.CloseFriend closeFriend = ModuleMgr.getCommonMgr().getCommonConfig().getNobilityList().queryCloseFriend(myChum.getLevel());

//            if(closeFriend.getLevel() > 1){//等级大于显示
//                Spanned spanned = Html.fromHtml(myChumTask.getTaskName() + "<font color=\"#FD698C\">(Lv."
//                        + closeFriend.getLevel() + " " + closeFriend.getTitle() + "," + closeFriend.getDiscount() + "折)</font>");
//
//                chum_task_title.setText(spanned);
//            }else {
                chum_task_title.setText(myChumTask.getTaskName());
//            }
        } else {
            chum_task_title.setText(myChumTask.getTaskName());
        }
        chum_task_desc.setText(context.getString(R.string.task_award, myChumTask.getTaskAward()));

        if (myChumTask.getTaskDone() == 1) {
            chum_task_btn.setTextColor(ContextCompat.getColor(context, R.color.color_999999));
            chum_task_btn.setText(R.string.done);
            chum_task_btn.setOnClickListener(null);
        } else if (myChumTask.getTaskDone() == 2 && !ModuleMgr.getCenterMgr().getMyInfo().isMan()) { //已推送且是发送端
            chum_task_btn.setOnClickListener(null);
            chum_task_btn.setText(R.string.sended_task);
            chum_task_btn.setTextColor(ContextCompat.getColor(context, R.color.color_999999));
        } else {
            chum_task_btn.setTextColor(ContextCompat.getColor(context, R.color.color_61D6C8));
            if (ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
                chum_task_btn.setText(R.string.had_done);
                Statistics.userBehavior(SendPoint.alert_haoyou_click_task);
            } else {
                chum_task_btn.setText(R.string.send_task);
                Statistics.userBehavior(SendPoint.menu_xiaoxi_miyou_task_sendtask);
            }
            chum_task_btn.setOnClickListener(onClickListener);
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
                UIShow.showPrivateChatAct(getContext(), myChum.getUid(), false, myChumTask.getTriggerType());
            } else {
                myChumTask.setTaskDone(2);
                initData();
                ModuleMgr.getChatMgr().sendFriendChumTaskMsg(String.valueOf(myChum.getUid()), myChumTask.getTaskId(), new RequestComplete() {
                    @Override
                    public void onRequestComplete(HttpResponse response) {
                        PToast.showShort(!response.isOk() ? response.getMsg() : "发送任务成功！");
                        if (!response.isOk()) {
                            myChumTask.setTaskDone(0);
                            initData();
                        }
                    }
                });
            }
        }
    };
}
