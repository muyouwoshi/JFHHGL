package com.juxin.predestinate.ui.mail;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.bean.config.base.NobilityList;
import com.juxin.predestinate.module.local.mail.MyChum;
import com.juxin.predestinate.module.local.mail.MyChumTaskList;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.util.UIShow;

import java.util.List;

/**
 * 好友任务
 * Created by Kind on 2017/9/4.
 */

public class FriendTaskDlg extends BaseDialogFragment implements View.OnClickListener {

    private ImageView friendtaskdlg_other_avater, friendtaskdlg_other_self;
    private LinearLayout friend_task_lin_container;
    private TextView friend_task_rank, friend_task_talk_length, friend_task_gift_num;

    private ChumTaskPanel chumTaskPanel, chumTaskPanelT;

    private MyChum myChum;
    private MyChumTaskList myChumTaskList;

    public FriendTaskDlg() {
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.BOTTOM);
        setDialogSizeRatio(-2, 0);
        setCancelable(true);
    }

    public void setData(MyChum myChum, MyChumTaskList myChumTaskList) {
        this.myChum = myChum;
        this.myChumTaskList = myChumTaskList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.f2_bottom_friend_task);
        initView();
        initData();
        return getContentView();
    }

    private void initView() {
        friendtaskdlg_other_avater = (ImageView) findViewById(R.id.friendtaskdlg_other_avater);
        friendtaskdlg_other_self = (ImageView) findViewById(R.id.friendtaskdlg_other_self);
        friend_task_lin_container = (LinearLayout) findViewById(R.id.friend_task_lin_container);
        friend_task_rank = (TextView) findViewById(R.id.friend_task_rank);
        friend_task_talk_length = (TextView) findViewById(R.id.friend_task_talk_length);
        friend_task_gift_num = (TextView) findViewById(R.id.friend_task_gift_num);

        friendtaskdlg_other_avater.setOnClickListener(this);
        friendtaskdlg_other_self.setOnClickListener(this);
        findViewById(R.id.friend_task_lin_chat).setOnClickListener(this);
        findViewById(R.id.friendtaskdlg_privilege).setOnClickListener(this);
    }

    //    private void initData() {
//        UserDetail userDetail = ModuleMgr.getCenterMgr().getMyInfo();
//        ImageLoader.loadCircleAvatar(getContext(), myChum.getAvatar(), friendtaskdlg_other_avater);
//        ImageLoader.loadCircleAvatar(getContext(), userDetail.getAvatar(), friendtaskdlg_other_self);
//
//        if (myChumTaskList != null) {
//            List<MyChumTaskList.MyChumTask> myChumTasks = myChumTaskList.getMyChumTasks();
//
//            if (myChumTasks.size() > 0) {
//                chumTaskPanel = new ChumTaskPanel(getContext(), myChumTasks.get(0), myChum);
//            }
//
//            if (myChumTasks.size() > 1) {
//                chumTaskPanelT = new ChumTaskPanel(getContext(), myChumTasks.get(1), myChum);
//            }
//
//            friend_task_lin_container.addView(chumTaskPanel.getContentView());
//            friend_task_lin_container.addView(chumTaskPanelT.getContentView());
//
//            NobilityList.CloseFriend closeFriend = ModuleMgr.getCommonMgr().getCommonConfig().getNobilityList().queryCloseFriend(myChum.getLevel());
//
//            friend_task_rank.setText("Lv." + closeFriend.getLevel() + " " + closeFriend.getTitle());
//            friend_task_talk_length.setText(((int) Math.floor(myChumTaskList.getVideoVoiceSec() / 60)) + "");
//            friend_task_gift_num.setText(myChumTaskList.getGiftCnt() + "");
//        }
//
//    }
    private void initData() {
        UserDetail userDetail = ModuleMgr.getCenterMgr().getMyInfo();
        if (myChum != null && !isEmpty(myChum.getAvatar())) {
            ImageLoader.loadCircleAvatar(getContext(), myChum.getAvatar(), friendtaskdlg_other_avater);
        }
        if (userDetail != null && !isEmpty(userDetail.getAvatar())) {
            ImageLoader.loadCircleAvatar(getContext(), userDetail.getAvatar(), friendtaskdlg_other_self);
        }

        if (myChumTaskList != null) {
            List<MyChumTaskList.MyChumTask> myChumTasks = myChumTaskList.getMyChumTasks();

            if (myChumTasks.size() > 0) {
                chumTaskPanel = new ChumTaskPanel(getContext(), myChumTasks.get(0), myChum);
                friend_task_lin_container.addView(chumTaskPanel.getContentView());
            }

            if (myChumTasks.size() > 1) {
                chumTaskPanelT = new ChumTaskPanel(getContext(), myChumTasks.get(1), myChum);
                friend_task_lin_container.addView(chumTaskPanelT.getContentView());
            }


            NobilityList.CloseFriend closeFriend = ModuleMgr.getCommonMgr().getCommonConfig().getNobilityList().queryCloseFriend(myChum.getLevel());

            String str = "Lv.0";
            if (closeFriend != null) {
                str = "Lv." + closeFriend.getLevel();
                if (!isEmpty(closeFriend.getTitle())) {
                    str += " " + closeFriend.getTitle();
                }
            }
            friend_task_rank.setText(str);
            friend_task_talk_length.setText(((int) Math.floor(myChumTaskList.getVideoVoiceSec() / 60)) + "");
            friend_task_gift_num.setText(myChumTaskList.getGiftCnt() + "");
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.friend_task_lin_chat:
                if (myChum != null) {
                    Statistics.userBehavior(SendPoint.alert_haoyou_click_chat);
                    UIShow.showPrivateChatAct(getContext(), myChum.getUid(), myChum.getNickname());
                    this.dismiss();
                }
                break;
            case R.id.friendtaskdlg_privilege:
                UIShow.showIntimateFriendExplain(getActivity());
                this.dismiss();
                break;
            case R.id.friendtaskdlg_other_avater:
                if (myChum != null) {
                    UIShow.showCheckOtherInfoAct(getActivity(), myChum.getUid());
                    this.dismiss();
                }
                break;
            case R.id.friendtaskdlg_other_self:
                UIShow.showCheckOwnInfoAct(getActivity());
                this.dismiss();
                break;
            default:
                break;
        }
    }
}
