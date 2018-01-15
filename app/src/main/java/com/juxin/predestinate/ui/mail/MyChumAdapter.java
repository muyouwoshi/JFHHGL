package com.juxin.predestinate.ui.mail;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.observe.Msg;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.library.observe.PObserver;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.config.base.NobilityList;
import com.juxin.predestinate.module.local.mail.MyChum;
import com.juxin.predestinate.module.local.mail.MyChumTaskList;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.ExBaseAdapter;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.user.util.CenterConstant;

import java.util.List;

/**
 * 密友
 * Created by Kind on 2017/7/14.
 */

public class MyChumAdapter extends ExBaseAdapter<MyChum> implements PObserver {

//    public MyChumAdapter(Context context, List<MyChum> datas) {
//        super(context, datas);
//    }
//
//
//    @Override
//    public int[] getItemLayouts() {
//        return new int[]{R.layout.f2_mychum_item};
//    }
//
//    @Override
//    public void onBindRecycleViewHolder(BaseRecyclerViewHolder viewHolder, int position) {
//        int itemType = this.getRecycleViewItemType(position);
//        final MyChum myChum = getItem(position);
//
//        LinearLayout mychum_item = viewHolder.findViewById(R.id.mychum_item);
//        LinearLayout mychum_item_buttom = viewHolder.findViewById(R.id.mychum_item_buttom);
//        ImageView mychum_item_img = viewHolder.findViewById(R.id.mychum_item_img);
//        TextView mychum_item_title = viewHolder.findViewById(R.id.mychum_item_title);
//        ImageView mychum_item_icon = viewHolder.findViewById(R.id.mychum_item_icon);
//        TextView mychum_item_desc = viewHolder.findViewById(R.id.mychum_item_desc);
//        Button mychum_item_task = viewHolder.findViewById(R.id.mychum_item_task);
//
//        ProgressBar mychum_item_progress = viewHolder.findViewById(R.id.mychum_item_progress);
//        TextView mychum_item_progress_txt = viewHolder.findViewById(R.id.mychum_item_progress_txt);
//        ImageView mychum_item_task_num = viewHolder.findViewById(R.id.mychum_item_task_num);
//
//
//        ImageLoader.loadRoundAvatar(getContext(), myChum.getAvatar(), mychum_item_img);
//        String nickname = myChum.getNickname();
//        if (!TextUtils.isEmpty(nickname)) {
//            mychum_item_title.setText(nickname);
//        } else {
//            mychum_item_title.setText(String.valueOf(myChum.getUid()));
//        }
//
//        if (myChum.isTodayTaskStatus()) {//已完成任务
//            mychum_item_task.setText("已完成");
//            mychum_item_task_num.setVisibility(View.GONE);
//            mychum_item_task.setEnabled(false);
//        } else {
//            mychum_item_task.setText("任务");
//            mychum_item_task_num.setVisibility(View.VISIBLE);
//        }
//
//        NobilityList nobilityList = ModuleMgr.getCommonMgr().getCommonConfig().getNobilityList();
//        NobilityList.CloseFriend closeFriend = nobilityList.queryCloseFriend(myChum.getLevel());
//
//        mychum_item_desc.setText(closeFriend.getTitle() + "Lv" + closeFriend.getLevel() + "/" + nobilityList.getCloseFriendList().size());
//
//        int experience = myChum.getExperience();
//        int levelMaxExp = myChum.getLevelMaxExp();
//
//        mychum_item_progress_txt.setText(experience + "/" + levelMaxExp);
//        int progress = experience / (levelMaxExp / 100);
//        if (progress < 100) {
//            progress += 1;
//        }
//
//        mychum_item_progress.setProgress(progress);
//        mychum_item_task.setOnClickListener(new onClickLin(myChum, mychum_item_buttom));
//
//        mychum_item.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                UIShow.showRemoveCloseFriendDlg(myChum, new RequestComplete() {
//                    @Override
//                    public void onRequestComplete(HttpResponse response) {
//                        if (!response.isOk()) {
//                            PToast.showShort("解除密友关系失败，请重试！");
//                            return;
//                        }
//
//                        getList().remove(myChum);
//                        notifyDataSetChanged();
//                        ModuleMgr.getChatListMgr().getIntimateTaskCnt();
//                    }
//                });
//                return false;
//            }
//        });
//
//
//    }
//
//    @Override
//    public int getRecycleViewItemType(int position) {
//        return 0;
//    }


    public MyChumAdapter(Context context, List<MyChum> datas) {
        super(context, datas);
    }


    public MyChum queryMyChum(long uid) {
        if (getList() == null || getList().size() < 0) {
            return new MyChum();
        }
        for (MyChum temp : getList()) {
            if (uid == temp.getUid()) {
                requestFriendTask(uid, temp);
                return temp;
            }
        }
        return new MyChum();
    }

    private void requestFriendTask(long uid, final MyChum temp) {
        ModuleMgr.getCommonMgr().reqGetIntimateFriendTask(uid, new RequestComplete() {
            @Override
            public void onRequestComplete(HttpResponse response) {
                MyChumTaskList myChumTaskList = (MyChumTaskList) response.getBaseData();
                List<MyChumTaskList.MyChumTask> myChumTasks = myChumTaskList.getMyChumTasks();
                if (myChumTasks.size() <= 0 || temp == null || MyChumAdapter.this == null) {
                    return;
                }
                temp.setLevel(myChumTaskList.getLevel());
                temp.setTodayTaskStatus(myChumTaskList.getTodayTaskStatus());
                temp.setExperience(myChumTaskList.getExperience());
                temp.setLevelMaxExp(myChumTaskList.getLevelMaxExp());
                temp.setMyChumTasks(myChumTasks);
                MyChumAdapter.this.notifyDataSetChanged();
            }
        });
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            convertView = inflate(R.layout.f2_mychum_item);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        final MyChum myChum = getItem(position);


        if (position == this.getCount() - 1) {
            vh.mychum_item_view_line.setVisibility(View.GONE);
        } else {
            vh.mychum_item_view_line.setVisibility(View.VISIBLE);
        }

        ImageLoader.loadRoundAvatar(getContext(), myChum.getAvatar(), vh.mychum_item_img);
        vh.mychum_item_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIShow.showUserOtherSetAct((FragmentActivity) App.getActivity(), myChum.getUid(), null, CenterConstant.USER_SET_FROM_CHAT);
            }
        });
        String nickname = myChum.getNickname();
        String reMark = myChum.getRemark();
        if (!TextUtils.isEmpty(reMark)) {
            vh.mychum_item_title.setText(reMark);
        } else if (!TextUtils.isEmpty(nickname)) {
            vh.mychum_item_title.setText(nickname);
        } else {
            vh.mychum_item_title.setText(String.valueOf(myChum.getUid()));
        }

        NobilityList nobilityList = ModuleMgr.getCommonMgr().getCommonConfig().getNobilityList();
        NobilityList.CloseFriend closeFriend = nobilityList.queryCloseFriend(myChum.getLevel());

        vh.mychum_item_desc.setText("Lv." + closeFriend.getLevel() + " " + closeFriend.getTitle());
        if (myChum.getIsOnline() == 1) {
            vh.mychum_item_tv_isonline.setTextColor(ContextCompat.getColor(getContext(), R.color.color_8fcda9));
            vh.mychum_item_tv_isonline.setText(getResources().getText(R.string.user_online));
        } else {
            vh.mychum_item_tv_isonline.setTextColor(ContextCompat.getColor(getContext(), R.color.color_e1e1e1));
            vh.mychum_item_tv_isonline.setText(getResources().getText(R.string.net_offline));
        }
        if (myChum.getNobility() > 0) {
            vh.mychum_item_knighthood.setVisibility(View.VISIBLE);
            ImageLoader.loadFitCenter(getContext(), nobilityList.queryNobility(myChum.getNobility(), myChum.getGender()).getTitle_icon(), vh.mychum_item_knighthood);
        } else {
            vh.mychum_item_knighthood.setVisibility(View.GONE);
        }
        if (myChum.getTop() > 0) {
            vh.mychum_item_icon.setVisibility(View.VISIBLE);
            if (myChum.getGender() == 2) {//女
                vh.mychum_item_icon.setImageResource(R.drawable.f2_icon_meili);
            } else {
                vh.mychum_item_icon.setImageResource(R.drawable.f2_icon_tuhao);
            }
        } else {
            vh.mychum_item_icon.setVisibility(View.GONE);
        }
        if (myChum.getGroup() >= 2) {
            vh.mychum_item_img_vip.setVisibility(View.VISIBLE);
        } else {
            vh.mychum_item_img_vip.setVisibility(View.GONE);
        }

        int experience = myChum.getExperience();
        int levelMaxExp = myChum.getLevelMaxExp();

        vh.mychum_item_progress_txt.setText(experience + "/" + levelMaxExp);
        int progress = 0;
        if (levelMaxExp >= 100) {
            progress = experience / (levelMaxExp / 100);
        }
        if (progress < 100) {
            progress += 1;
        }

        vh.mychum_item_progress.setProgress(progress);

        List<MyChumTaskList.MyChumTask> myChumTasks = myChum.getMyChumTasks();
        vh.mychum_item_buttom.removeAllViews();
        if (!myChum.isTodayTaskStatus() && myChum.isTaskShow() && myChumTasks != null && myChumTasks.size() > 0) {
            ChumTaskPanel chumTaskPanel;
            for (MyChumTaskList.MyChumTask temp : myChumTasks) {
                chumTaskPanel = new ChumTaskPanel(getContext(), temp, myChum);
                vh.mychum_item_buttom.addView(chumTaskPanel.getContentView());
            }
        }

        vh.mychum_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Statistics.userBehavior(SendPoint.menu_haoyou_click);
                UIShow.showFriendTaskDlg(myChum);
            }
        });
        return convertView;
    }

    @Override
    public void onMessage(String key, Object value) {
        if (key == MsgType.MT_REMARK_INFORM) {
            Msg msg = (Msg) value;
            if (msg.getData() instanceof String) {
                for (int i = 0; i < this.getList().size(); i++) {
                    if (this.getList().get(i).getUid() == Long.valueOf(msg.getKey())) {
                        this.getList().get(i).setRemark(String.valueOf(msg.getData()));
                        break;
                    }
                }
            }
            this.notifyDataSetChanged();
        }
    }

    class ViewHolder {
        LinearLayout mychum_item, mychum_item_buttom;
        ImageView mychum_item_img, mychum_item_icon, mychum_item_img_vip, mychum_item_knighthood;
        TextView mychum_item_title, mychum_item_progress_txt, mychum_item_desc, mychum_item_tv_isonline, mychum_item_tv_friend_level;
        ProgressBar mychum_item_progress;
        RelativeLayout mychum_item_friend_level;
        View mychum_item_view_line;

        public ViewHolder(View convertView) {
            initView(convertView);
        }

        private void initView(View convertView) {
            mychum_item = (LinearLayout) convertView.findViewById(R.id.mychum_item);
            mychum_item_buttom = (LinearLayout) convertView.findViewById(R.id.mychum_item_buttom);
            mychum_item_img = (ImageView) convertView.findViewById(R.id.mychum_item_img);
            mychum_item_title = (TextView) convertView.findViewById(R.id.mychum_item_title);
            mychum_item_icon = (ImageView) convertView.findViewById(R.id.mychum_item_icon);
            mychum_item_knighthood = (ImageView) convertView.findViewById(R.id.mychum_item_knighthood);
            mychum_item_desc = (TextView) convertView.findViewById(R.id.mychum_item_desc);
            mychum_item_img_vip = (ImageView) convertView.findViewById(R.id.mychum_item_img_vip);
            mychum_item_tv_isonline = (TextView) convertView.findViewById(R.id.mychum_item_tv_isonline);
            mychum_item_tv_friend_level = (TextView) convertView.findViewById(R.id.mychum_item_tv_friend_level);
            mychum_item_friend_level = (RelativeLayout) convertView.findViewById(R.id.mychum_item_friend_level);

            mychum_item_progress = (ProgressBar) convertView.findViewById(R.id.mychum_item_progress);
            mychum_item_progress_txt = (TextView) convertView.findViewById(R.id.mychum_item_progress_txt);
            mychum_item_view_line = convertView.findViewById(R.id.mychum_item_view_line);
        }
    }

    public void attach() {
        MsgMgr.getInstance().attach(this);
    }

    public void detach() {
        MsgMgr.getInstance().detach(this);
    }
}
