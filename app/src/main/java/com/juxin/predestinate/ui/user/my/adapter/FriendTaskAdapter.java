package com.juxin.predestinate.ui.user.my.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.config.base.NobilityList;
import com.juxin.predestinate.module.local.chat.utils.MessageConstant;
import com.juxin.predestinate.module.local.mail.MyChumTaskList;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.ExBaseAdapter;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.user.my.CommonDlg.CloseFriendTaskView;

import java.util.List;


/**
 * 任务列表
 * Created by zm on 2017/7/21.
 */
public class FriendTaskAdapter extends ExBaseAdapter<MyChumTaskList.MyChumTask> {

    private LayoutInflater inflater;
    private Context mContext;
    private long tuid;
    private String channel_uid;
    private CloseFriendTaskView mCloseFriendTaskView;
    private int level = -1;
    private MyChumTaskList myChumTaskList;

    public FriendTaskAdapter(Context fContext, CloseFriendTaskView mCloseFriendTaskView, List<MyChumTaskList.MyChumTask> infos,
                             long tuid, String channel_uid, MyChumTaskList info) {
        super(fContext, infos);
        this.mContext = fContext;
        this.inflater = LayoutInflater.from(fContext);
        this.tuid = tuid;
        this.channel_uid = channel_uid;
        this.mCloseFriendTaskView = mCloseFriendTaskView;
        this.myChumTaskList = info;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final MyViewHolder vh;
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.f2_friend_task_item, null);
            vh = new MyViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (MyViewHolder) convertView.getTag();
        }

        final MyChumTaskList.MyChumTask info = getItem(position);
        if (info == null) {
            return convertView;
        }
        vh.tvTask.setText(info.getTaskName() + "");
        vh.tvAward.setText(mContext.getString(R.string.task_award, info.getTaskAward()));
        NobilityList.CloseFriend friendInfo = ModuleMgr.getCommonMgr().getCommonConfig().getNobilityList().queryCloseFriend(level);
        switch (info.getTriggerType()) {
            case 1://视频&语音
//                if (level > 1 && friendInfo != null) {
//                    vh.tvTask.setText(info.getTaskName() + " (Lv." + friendInfo.getLevel() + "好友等级，" + friendInfo.getDiscount() + "折)");
//                }
                break;
            case 4://大转盘
                break;
            case 2://送礼物
                break;
            case 3://小游戏(删掉)
                break;
            case 5://聊天
                break;
            default:
                break;
        }
//        0未完成，1已完成，2已推送
        if (info.getTaskDone() == 1) {
            vh.tvState.setTextColor(ContextCompat.getColor(mContext, R.color.color_b7b9c2));
            vh.tvState.setBackgroundResource(R.drawable.f2_friend_task_item_state_unbg);
            vh.tvState.setText(R.string.done);
            convertView.setOnClickListener(null);
        } else if (info.getTaskDone() == 2 && !ModuleMgr.getCenterMgr().getMyInfo().isMan()) {//已推送且是发送任务端
            convertView.setOnClickListener(null);
            vh.tvState.setBackgroundResource(R.drawable.f2_friend_task_item_state_unbg);
            vh.tvState.setText(R.string.sended_task);
            vh.tvState.setTextColor(ContextCompat.getColor(mContext, R.color.color_b7b9c2));
        } else {
            vh.tvState.setBackgroundResource(R.drawable.f2_friend_task_item_state_bg);
            vh.tvState.setTextColor(ContextCompat.getColor(mContext, R.color.color_6F7485));
            if (ModuleMgr.getCenterMgr().getMyInfo().isMan()) { //男方
                vh.tvState.setText(R.string.had_done);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { //去完成
                        Statistics.userBehavior(SendPoint.page_chat_expansion_task);
                        if (mCloseFriendTaskView != null && mCloseFriendTaskView.getState() == 2) {
                            UIShow.showPrivateChatAct(getContext(), tuid, true, info.getTriggerType());
                            mCloseFriendTaskView.hideTask();
                        }
                    }
                });
            } else {  //女方
                vh.tvState.setText(R.string.send_task);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { //发送任务
                        Statistics.userBehavior(SendPoint.page_chat_expansion_task);
                        if (mCloseFriendTaskView != null && mCloseFriendTaskView.getState() == 2) {
                            ModuleMgr.getChatMgr().sendChumTaskMsg(String.valueOf(tuid), info.getTaskId(), new RequestComplete() {
                                @Override
                                public void onRequestComplete(HttpResponse response) {
                                    if (response.isOk()) {
                                        info.setTaskDone(2);
                                        notifyDataSetChanged();
                                    }
                                }
                            }, myChumTaskList != null ? myChumTaskList.getKnow() : MessageConstant.Know_Friends);
                        }
                    }
                });
            }
        }
        vh.tvLine.setVisibility(View.VISIBLE);
        if (position == getCount() - 1) {
            vh.tvLine.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    class MyViewHolder {

        TextView tvTask, tvAward, tvState, tvLine;

        public MyViewHolder(View convertView) {
            initView(convertView);
        }

        private void initView(View convertView) {
            tvTask = (TextView) convertView.findViewById(R.id.friend_task_item_tv_task);
            tvAward = (TextView) convertView.findViewById(R.id.friend_task_item_tv_award);
            tvState = (TextView) convertView.findViewById(R.id.friend_task_item_tv_state);
            tvLine = (TextView) convertView.findViewById(R.id.friend_task_item_ll_line);
        }
    }

    public void setLevel(int level) {
        this.level = level;
    }

}