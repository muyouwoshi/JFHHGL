package com.juxin.predestinate.ui.discover;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.observe.MsgType;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.light.UserInfoLightweight;
import com.juxin.predestinate.bean.config.CommonConfig;
import com.juxin.predestinate.module.local.chat.MessageRet;
import com.juxin.predestinate.module.local.statistics.StatisticsDiscovery;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.ExBaseAdapter;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.socket.IMProxy;
import com.juxin.predestinate.module.logic.socket.NetData;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.user.util.CenterConstant;

import java.util.List;


/**
 * 发现Adapter
 * Created by zhang on 2017/4/21.
 */

public class DiscoverAdapter extends ExBaseAdapter<UserInfoLightweight> {

    private boolean isNear = false;
    private CommonConfig commonConfig;

    public boolean isNear() {
        return isNear;
    }

    public void setNear(boolean near) {
        isNear = near;
    }

    public DiscoverAdapter(Context context, List<UserInfoLightweight> datas) {
        super(context, datas);
        commonConfig = ModuleMgr.getCommonMgr().getCommonConfig();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        MyViewHolder holder;
        if (convertView == null) {
            convertView = inflate(R.layout.f1_discover_item);
            holder = new MyViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (MyViewHolder) convertView.getTag();
        }

        final UserInfoLightweight userInfo = getItem(position);
        ImageLoader.loadRoundAvatar(getContext(), userInfo.getAvatar(), holder.iv_avatar);
        holder.tv_name.setText(userInfo.getNickname());
        holder.iv_vip.setVisibility(ModuleMgr.getCenterMgr().isVip(userInfo.getGroup()) ? View.VISIBLE : View.GONE);

        if (userInfo.isToper()) {
            holder.iv_ranking.setVisibility(View.VISIBLE);
            if (userInfo.isMan()) {
                holder.iv_ranking.setImageResource(R.drawable.f1_top02);
            } else {
                holder.iv_ranking.setImageResource(R.drawable.f1_top01);
            }
        } else {
            holder.iv_ranking.setVisibility(View.GONE);
        }

        if (userInfo.getNobility_rank() > CenterConstant.TITLE_LEVEL_LOW) {//爵位和对应背景图
            holder.iv_title.setVisibility(View.VISIBLE);
            ImageLoader.loadFitCenter(getContext(), commonConfig.queryNobility(userInfo.getNobility_rank(), userInfo.getGender()).getTitle_icon(), holder.iv_title);
        } else {
            holder.iv_title.setVisibility(View.GONE);
        }
        if (userInfo.getIntimate_level() > 0) {//亲密度等级
            holder.tv_relation.setVisibility(View.VISIBLE);
            holder.tv_relation.setText("LV" + userInfo.getIntimate_level());
        } else {
            holder.tv_relation.setVisibility(View.GONE);
        }
        if (userInfo.getAge() == 0) {
            holder.tv_age.setVisibility(View.GONE);
        } else {
            holder.tv_age.setVisibility(View.VISIBLE);
            holder.tv_age.setText(userInfo.getAge() + "岁");
        }

        if (userInfo.getHeight() == 0) {
            holder.tv_height.setVisibility(View.GONE);
        } else {
            holder.tv_height.setVisibility(View.VISIBLE);
            holder.tv_height.setText(userInfo.getHeight() + "cm");
        }

        if (TextUtils.isEmpty(userInfo.getDistance())) {
            holder.tv_distance.setVisibility(View.GONE);
        } else {
            holder.tv_distance.setVisibility(View.VISIBLE);
            holder.tv_distance.setText(userInfo.getDistance());
        }


        if ((userInfo.getAge() != 0 || userInfo.getHeight() != 0) && !TextUtils.isEmpty(userInfo.getDistance())) {
            holder.point.setVisibility(View.VISIBLE);
        } else {
            holder.point.setVisibility(View.GONE);
        }

        if (userInfo.isVideo_busy()) {
            holder.iv_calling.setVisibility(View.VISIBLE);
            holder.lin_call_state.setVisibility(View.GONE);
            holder.lin_video_state.setVisibility(View.GONE);
        } else {
            if (userInfo.isVideo_available() && userInfo.isAudio_available()) {
                //可语音可视频显示可视频
                holder.iv_calling.setVisibility(View.GONE);
                holder.lin_call_state.setVisibility(View.GONE);
                holder.lin_video_state.setVisibility(View.VISIBLE);
                holder.iv_video.setEnabled(userInfo.isVideo_available());
            } else if (userInfo.isVideo_available() && !userInfo.isAudio_available()) {
                // 可视频不可语音显示可视频
                holder.iv_calling.setVisibility(View.GONE);
                holder.lin_call_state.setVisibility(View.GONE);
                holder.lin_video_state.setVisibility(View.VISIBLE);
                holder.iv_video.setEnabled(userInfo.isVideo_available());
            } else if (!userInfo.isVideo_available() && userInfo.isAudio_available()) {
                //可语音不可视频显示可语音
                holder.iv_calling.setVisibility(View.GONE);
                holder.lin_call_state.setVisibility(View.VISIBLE);
                holder.lin_video_state.setVisibility(View.GONE);
                holder.iv_call.setEnabled(userInfo.isAudio_available());
                holder.tv_call.setVisibility(View.VISIBLE);
            } else {
                //不可语音不可视频都不显示
                holder.iv_calling.setVisibility(View.GONE);
                holder.lin_call_state.setVisibility(View.GONE);
                holder.lin_video_state.setVisibility(View.GONE);
                holder.iv_call.setEnabled(userInfo.isVideo_available());
                holder.tv_call.setVisibility(View.GONE);
            }
        }

        if (ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
            if (!isNear()) {
                if (ModuleMgr.getCenterMgr().isRobot(userInfo.getKf_id()) && !ModuleMgr.getCenterMgr().getMyInfo().isVip()) {
                    showSayHello(holder, userInfo, position);
                } else {
                    showOnline(holder, userInfo);
                }
            } else {
                if (ModuleMgr.getCenterMgr().isRobot(getItem(0).getKf_id()) && !ModuleMgr.getCenterMgr().getMyInfo().isVip()) {
                    showSayHello(holder, userInfo, position);
                } else {
                    showOnline(holder, userInfo);
                }
            }
        } else {
            showSayHello(holder, userInfo, position);
        }


        if (ModuleMgr.getCenterMgr().getMyInfo().isMan()) {
            if (ModuleMgr.getCenterMgr().isRobot(userInfo.getKf_id()) && !ModuleMgr.getCenterMgr().getMyInfo().isVip()) {
                showSayHello(holder, userInfo, position);
            } else {
                showOnline(holder, userInfo);
            }
        } else {
            if (ModuleMgr.getCenterMgr().getMyInfo().isVip()) {
                showOnline(holder, userInfo);
            } else {
                showSayHello(holder, userInfo, position);
            }
        }

        holder.rel_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIShow.showCheckOtherInfoAct(getContext(), userInfo.getUid());
                //统计
                StatisticsDiscovery.onRecommendViewUser(userInfo.getUid(), position, isNear);
            }
        });

        holder.iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIShow.showCheckOtherInfoAct(getContext(), userInfo.getUid());
                //统计
                StatisticsDiscovery.onAvatarClick(userInfo, getList(), isNear);
            }
        });
        return convertView;
    }

    /**
     * 显示在线状态
     *
     * @param holder
     * @param userInfo
     */
    private void showOnline(MyViewHolder holder, UserInfoLightweight userInfo) {
        holder.btn_sayhi.setVisibility(View.GONE);
        holder.tv_online_state.setVisibility(View.VISIBLE);
        holder.tv_online_state.setText(userInfo.isOnline() ? "在线" : userInfo.getLast_onLine());
    }

    /**
     * 显示打招呼
     *
     * @param holder
     * @param userInfo
     * @param position
     */
    private void showSayHello(MyViewHolder holder, final UserInfoLightweight userInfo, final int position) {
        holder.tv_online_state.setVisibility(View.GONE);
        holder.btn_sayhi.setVisibility(View.VISIBLE);
        holder.btn_sayhi.setEnabled(!userInfo.isSayHello());
        if (!userInfo.isSayHello()) {
            holder.btn_sayhi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ModuleMgr.getCenterMgr().isCanSayHi(getContext())) {
                        ModuleMgr.getChatMgr().sendSayHelloMsg(String.valueOf(userInfo.getUid()),
                                getContext().getString(R.string.say_hello_txt),
                                userInfo.getKf_id(),
                                ModuleMgr.getCenterMgr().isRobot(userInfo.getKf_id()) ?
                                        Constant.SAY_HELLO_TYPE_ONLY : Constant.SAY_HELLO_TYPE_SIMPLE, new IMProxy.SendCallBack() {
                                    @Override
                                    public void onResult(long msgId, boolean group, String groupId, long sender, String contents) {
                                        //170824 UPDATE START 打招呼提示使用消息体返回的错误信息
                                        //PToast.showShort(getContext().getString(R.string.user_info_hi_suc));
                                        //getItem(position).setSayHello(true);
                                        //notifyDataSetChanged();

                                        MessageRet messageRet = new MessageRet();
                                        messageRet.parseJson(contents);

                                        if (messageRet.getS() == 0) { // 成功
                                            PToast.showShort(getContext().getString(R.string.user_info_hi_suc));
                                            UserInfoLightweight tempInfo = getItem(position);
                                            if (tempInfo == null) return;
                                            tempInfo.setSayHello(true);
                                            notifyDataSetChanged();
                                            MsgMgr.getInstance().sendMsg(MsgType.MT_Say_HI_Notice, tempInfo.getUid());
                                        } else {
                                            if (!TextUtils.isEmpty(messageRet.getFailMsg())) {
                                                PToast.showShort(messageRet.getFailMsg());
                                            } else {
                                                PToast.showShort(getContext().getString(R.string.user_info_hi_fail));
                                            }
                                        }
                                        //170824 UPDATE END 打招呼提示使用消息体返回的错误信息
                                    }

                                    @Override
                                    public void onSendFailed(NetData data) {
                                        PToast.showShort(getContext().getString(R.string.user_info_hi_fail));
                                    }
                                });
                    }

                    ///统计
                    StatisticsDiscovery.onSayHello(userInfo.getUid(), position, isNear);

                }
            });
        }
    }


    class MyViewHolder {
        private ImageView iv_avatar, iv_vip, iv_ranking, iv_title;
        private Button iv_video, iv_call;
        private TextView tv_name, tv_age, tv_height, tv_distance, tv_relation;
        private Button btn_sayhi;
        private LinearLayout iv_calling, lin_video_state, lin_call_state;
        private View rel_item;
        private View point;

        private TextView tv_online_state, tv_call;

        public MyViewHolder(View convertView) {
            initView(convertView);
        }

        private void initView(View convertView) {
            iv_avatar = (ImageView) convertView.findViewById(R.id.discover_item_avatar);

            iv_vip = (ImageView) convertView.findViewById(R.id.discover_item_vip_state);
            iv_title = (ImageView) convertView.findViewById(R.id.discover_title_state);
            tv_relation = (TextView) convertView.findViewById(R.id.discover_relation_state);
            iv_ranking = (ImageView) convertView.findViewById(R.id.discover_item_ranking_state);

            iv_calling = (LinearLayout) convertView.findViewById(R.id.discover_item_calling_state);
            iv_video = (Button) convertView.findViewById(R.id.discover_item_video);
            iv_call = (Button) convertView.findViewById(R.id.discover_item_call);

            tv_name = (TextView) convertView.findViewById(R.id.discover_item_name);
            tv_age = (TextView) convertView.findViewById(R.id.discover_item_age);
            tv_height = (TextView) convertView.findViewById(R.id.discover_item_height);
            tv_distance = (TextView) convertView.findViewById(R.id.discover_item_distance);

            btn_sayhi = (Button) convertView.findViewById(R.id.discover_item_sayhi);
            rel_item =  convertView.findViewById(R.id.discover_item);
            point = convertView.findViewById(R.id.discover_item_point);

            tv_online_state = (TextView) convertView.findViewById(R.id.discover_item_online_state);
            lin_video_state = (LinearLayout) convertView.findViewById(R.id.discover_item_video_state);
            lin_call_state = (LinearLayout) convertView.findViewById(R.id.discover_item_call_state);
            tv_call = (TextView) convertView.findViewById(R.id.discover_item_call_tv);
        }

    }
}
