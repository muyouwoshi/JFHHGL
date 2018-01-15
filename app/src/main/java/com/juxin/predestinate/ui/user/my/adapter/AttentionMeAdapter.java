package com.juxin.predestinate.ui.user.my.adapter;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.log.PLogger;
import com.juxin.library.log.PToast;
import com.juxin.library.utils.NetworkUtils;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.bean.my.AttentionUserDetail;
import com.juxin.predestinate.module.local.chat.MessageRet;
import com.juxin.predestinate.module.local.statistics.StatisticsMessage;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.ExBaseAdapter;
import com.juxin.predestinate.module.logic.baseui.LoadingDialog;
import com.juxin.predestinate.module.logic.config.AreaConfig;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.logic.socket.IMProxy;
import com.juxin.predestinate.module.logic.socket.NetData;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.module.util.my.AttentionUtil;

import java.util.List;


/**
 * 关注我的适配器
 * Created by zm on 2017/4/13.
 */
public class AttentionMeAdapter extends ExBaseAdapter<AttentionUserDetail> implements RequestComplete {

    private int postion;
    private MyViewHolder vh;
    private int followType = 1;   // 关注、取消关注

    public AttentionMeAdapter(Context context, List<AttentionUserDetail> datas) {
        super(context, datas);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final MyViewHolder mHolder;
        if (convertView == null) {
            convertView = inflate(R.layout.f1_attention_panel_list_item);
            mHolder = new MyViewHolder(convertView);

            convertView.setTag(mHolder);
        } else {
            mHolder = (MyViewHolder) convertView.getTag();
        }
        final AttentionUserDetail info = getItem(position);

        ImageLoader.loadAvatar(getContext(), info.getAvatar(), mHolder.imgHead);
        mHolder.tvNickname.setText(info.getShowName());//昵称
        mHolder.imVipState.setVisibility(info.is_vip() ? View.VISIBLE : View.GONE);

        mHolder.tvAge.setVisibility(info.getAge() <= 0 ? View.GONE : View.VISIBLE);
        mHolder.tvAge.setText(info.getAge() + getContext().getString(R.string.age));//年龄

        mHolder.tvDiqu.setText(AreaConfig.getInstance().getCityNameByID(info.getCity()));
        mHolder.tvpiccount.setText(info.getPhotoNum() + getContext().getString(R.string.check_info_album));
        mHolder.tvconcern.setText(info.getType() == 0 ? R.string.attention_ta : R.string.cancel_the_attention);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick(view, position);
            }
        });

        mHolder.tvconcern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserDetail userDetail = ModuleMgr.getCenterMgr().getMyInfo();
                AttentionMeAdapter.this.postion = position;
                AttentionMeAdapter.this.vh = mHolder;
                if (userDetail != null) {
                    if (userDetail.getGender() != 2) {//不是女生需要开通vip
                        if (!userDetail.isVip()) {
                            createOpenVipDialog(getContext().getString(R.string.open_the_vip_can_be_operation));//提示开通vip
                            StatisticsMessage.followMeToVip(userDetail.getUid());
                            return;
                        } else {
                            if (info.getType() != 0) {
                                StatisticsMessage.followMeToCancel(userDetail.getUid());
                            } else {
                                StatisticsMessage.followMeToFollow(userDetail.getUid());
                            }
                        }
                    }
                }

                if (!NetworkUtils.isConnected(getContext())) {//未联网返回
                    PToast.showShort(getContext().getString(R.string.net_error_check_your_net));
                    return;
                }
                // 执行关注Class
                followType = info.getType() + 1;
                String content;
                if (!TextUtils.isEmpty(info.getNickname()) && !"null".equals(info.getNickname())) {
                    content = "[" + info.getNickname() + "]刚刚关注了你";
                } else {
                    content = "刚刚关注了你";
                }
                ModuleMgr.getChatMgr().sendAttentionMsg(info.getUid(), content, info.getKf_id(), followType, new IMProxy.SendCallBack() {
                    @Override
                    public void onResult(long msgId, boolean group, String groupId, long sender, String contents) {
                        MessageRet messageRet = new MessageRet();
                        messageRet.parseJson(contents);
                        if (messageRet.getS() == 0) {
                            int mPosition = getPosition(info);
                            if (getList().get(mPosition).getType() == 1) {
                                vh.tvconcern.setText(getContext().getString(R.string.attention_ta));
                                getList().get(mPosition).setType(0);
                                AttentionUtil.deleteUserId(info);
                            } else {
                                vh.tvconcern.setText(getContext().getString(R.string.privatechat_title_unsubscribe));
                                getList().get(mPosition).setType(1);
                                AttentionUtil.saveUserIds(info);
                            }
                            notifyDataSetChanged();
                        } else {
                            handleFollowFail();
                        }

                    }

                    @Override
                    public void onSendFailed(NetData data) {
                        handleFollowFail();
                    }
                });
            }
        });
        return convertView;
    }

    /**
     * 获取信息的准确位置（加锁）
     *
     * @param info 某一条具体的信息
     * @return 返回 info 在list中的position
     */
    private synchronized int getPosition(AttentionUserDetail info) {
        int size = getList().size();
        for (int i = 0; i < size; i++) {
            if (info.getUid() == getList().get(i).getUid()) {
                return i;
            }
        }
        return -1;
    }

    private void handleFollowFail() {
        String msg = "";
        switch (followType) {
            case 1:
                msg = getContext().getResources().getString(R.string.user_info_follow_fail);
                break;

            case 2:
                msg = getContext().getResources().getString(R.string.user_info_unfollow_fail);
                break;
        }
        PToast.showShort(msg);
    }

    private void createOpenVipDialog(String str) {//vip弹框
        final Dialog dialog = new Dialog(getContext(), R.style.dialog);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.f1_app_tips_dialog, null);
        TextView txt_update_info = (TextView) view.findViewById(R.id.txt_update_info);
        txt_update_info.setText(str);
        TextView txt_update_submit = (TextView) view.findViewById(R.id.txt_update_submit);
        TextView txt_update_cancel = (TextView) view.findViewById(R.id.txt_update_cancel);
        txt_update_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        txt_update_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(ModuleMgr.getCenterMgr().getMyInfo().isB()){
                    UIShow.showBuyKeyActivity(getContext());
                }else{
                    UIShow.showBuyCoinActivity(getContext());
                }
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }

    @Override
    public void onRequestComplete(HttpResponse response) {
        LoadingDialog.closeLoadingDialog();
        try {
            if (response.isOk()) {//返回成功
                if (getList().get(postion).getType() == 0) {
                    vh.tvconcern.setText(getContext().getString(R.string.privatechat_title_unsubscribe));
                    getList().get(postion).setType(1);
                    notifyDataSetChanged();
                    return;
                }
                vh.tvconcern.setText(getContext().getString(R.string.attention_ta));
                getList().get(postion).setType(0);
                notifyDataSetChanged();
            } else {
                PToast.showShort(getContext().getString(R.string.toast_commit_suggest_error));
            }
        } catch (Exception e) {
            PLogger.e("AttentionMeAdapter_________" + e.toString());
        }
    }

    public void onItemClick(View convertView, int position) {
        //跳转他人资料页
        UIShow.showCheckOtherInfoAct(getContext(), getItem(position).getUid());
        StatisticsMessage.followMeToSeeUserInfo(getItem(position).getUid());
    }

    class MyViewHolder {

        // 头像
        public ImageView imgHead;
        // 年龄
        public TextView tvAge;
        // 身高
        public TextView tvDiqu;
        // 昵称
        public TextView tvNickname;
        // 照片数量
        public TextView tvpiccount;
        // 关注按钮
        public TextView tvconcern;
        // VIP角标
        public ImageView imVipState;

        public MyViewHolder(View convertView) {
            initView(convertView);
        }

        private void initView(View convertView) {
            imgHead = (ImageView) convertView.findViewById(R.id.concern_list_item_head);
            tvAge = (TextView) convertView.findViewById(R.id.concern_list_item_age);
            tvDiqu = (TextView) convertView.findViewById(R.id.concern_list_item_diqu);
            tvNickname = (TextView) convertView.findViewById(R.id.concern_list_item_nickname);
            tvpiccount = (TextView) convertView.findViewById(R.id.concern_list_item_piccount);
            tvconcern = (TextView) convertView.findViewById(R.id.concern_list_item_btnconcern);
            imVipState = (ImageView) convertView.findViewById(R.id.concern_list_item_img_vipState);
        }
    }
}