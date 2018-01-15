package com.juxin.predestinate.ui.discover;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.log.PSP;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.bean.config.CommonConfig;
import com.juxin.predestinate.module.local.statistics.StatisticsShareUnlock;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.logic.config.Constant;
import com.juxin.predestinate.module.logic.config.UrlParam;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.user.check.bean.VideoConfig;
import com.juxin.predestinate.ui.user.util.CenterConstant;

/**
 * 要找的人弹窗
 * Created by zhang on 2017/9/25.
 */

public class SureUserDlg extends BaseDialogFragment implements View.OnClickListener, RequestComplete {

    private ImageView btn_close, iv_avatar, iv_vip, iv_ranking, iv_title;
    private TextView tv_nick, tv_age_dis, tv_price, tv_relation;
    private ImageView iv_online;
    private LinearLayout btn_sure;


    private UserDetail userDetail;
    private CommonConfig commonConfig;

    private long uid;
    private boolean showOther;
    private boolean jumpToChat;

    public SureUserDlg() {
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.BOTTOM);
        setDialogSizeRatio(0, 0);
        setCancelable(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.spread_sure_user_dlg);
        commonConfig = ModuleMgr.getCommonMgr().getCommonConfig();
        View contentView = getContentView();
        initView(contentView);
        setViewData();
        ModuleMgr.getCommonMgr().reSetPromotionuid();
        PSP.getInstance().put(ModuleMgr.getCommonMgr().getPrivateKey(Constant.SHARE_SHOW_MAKE_MONEY), false);
        return contentView;
    }

    private void initView(View contentView) {
        btn_close = (ImageView) contentView.findViewById(R.id.sure_user_dlg_close);
        iv_avatar = (ImageView) contentView.findViewById(R.id.sure_user_dlg_avatar);
        iv_vip = (ImageView) contentView.findViewById(R.id.sure_user_dlg_vip_state);
        iv_ranking = (ImageView) contentView.findViewById(R.id.sure_user_dlg_ranking_state);
        iv_title = (ImageView) contentView.findViewById(R.id.sure_user_dlg_title_state);
        iv_online = (ImageView) contentView.findViewById(R.id.iv_online_status);

        tv_nick = (TextView) contentView.findViewById(R.id.sure_user_dlg_name);
        tv_age_dis = (TextView) contentView.findViewById(R.id.sure_user_dlg_age_dis);
        tv_price = (TextView) contentView.findViewById(R.id.sure_user_dlg_tv_price);
        tv_relation = (TextView) contentView.findViewById(R.id.sure_user_dlg_relation_state);
        btn_sure = (LinearLayout) contentView.findViewById(R.id.sure_user_dlg_sure);

        btn_close.setOnClickListener(this);
        btn_sure.setOnClickListener(this);
    }

    private void setViewData() {
        if (getUserDetail() != null) {
            ModuleMgr.getCenterMgr().reqVideoChatConfig(getUserDetail().getUid(), this); // 请求音视频开关配置
            ImageLoader.loadCircleAvatar(getContext(), getUserDetail().getAvatar(), iv_avatar, 8, getResources().getColor(R.color.pink));
            tv_nick.setText(getUserDetail().getNickname());
            iv_vip.setVisibility(ModuleMgr.getCenterMgr().isVip(getUserDetail().getGroup()) ? View.VISIBLE : View.GONE);

            if (getUserDetail().getTop() != 0) {
                iv_ranking.setVisibility(View.VISIBLE);
                if (getUserDetail().isMan()) {
                    iv_ranking.setImageResource(R.drawable.f1_top02);
                } else {
                    iv_ranking.setImageResource(R.drawable.f1_top01);
                }
            } else {
                iv_ranking.setVisibility(View.GONE);
            }

            if (getUserDetail().getNobilityInfo().getRank() > CenterConstant.TITLE_LEVEL_LOW) {//爵位和对应背景图
                iv_title.setVisibility(View.VISIBLE);
                ImageLoader.loadFitCenter(getContext(), commonConfig.queryNobility(getUserDetail().getNobilityInfo().getRank(),
                        getUserDetail().getGender()).getTitle_icon(), iv_title);
            } else {
                iv_title.setVisibility(View.GONE);
            }

            iv_online.setImageResource(getUserDetail().isOnline() ? R.drawable.myfriends_online : R.drawable.myfriends_offline);
            String dis = getUserDetail().getDistance() > 5 ? getString(R.string.user_info_distance_far) : getString(R.string.user_info_distance_near);

            if (getUserDetail().getAge() != 0 && getUserDetail().getDistance() != 0) {
                tv_age_dis.setVisibility(View.VISIBLE);
                tv_age_dis.setText(getUserDetail().getAge() + "岁" + "    -    " + dis);
            } else if (getUserDetail().getAge() == 0 && getUserDetail().getDistance() != 0) {
                tv_age_dis.setVisibility(View.VISIBLE);
                tv_age_dis.setText(dis);
            } else if (getUserDetail().getAge() != 0 && getUserDetail().getDistance() == 0) {
                tv_age_dis.setVisibility(View.VISIBLE);
                tv_age_dis.setText(getUserDetail().getAge() + "岁");
            } else {
                tv_age_dis.setVisibility(View.GONE);
            }
        } else {
            ModuleMgr.getCenterMgr().reqOtherInfo(getUid(), this);
        }
    }


    private void reSetTVPrice(VideoConfig config) {
        if (config.isVideoChat() && !getUserDetail().isMan()) {
            tv_price.setVisibility(View.VISIBLE);
            SpannableString spannableString = new SpannableString("视频通话：" + config.getVideoPrice() + "钻/分钟");
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#fd6c8e")), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv_price.setText(spannableString);
        } else {
            tv_price.setVisibility(View.GONE);
        }

    }


    public UserDetail getUserDetail() {
        return userDetail;
    }

    public void setUserDetail(UserDetail userDetail) {
        this.userDetail = userDetail;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setShowOther(boolean showOther) {
        this.showOther = showOther;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sure_user_dlg_close:
                StatisticsShareUnlock.onAlert_gotouser_close();
                dismiss();
                break;
            case R.id.sure_user_dlg_sure:
                jumpToChat = true;
                if (showOther) {
                    PSP.getInstance().put(ModuleMgr.getCommonMgr().getPrivateKey(Constant.SHARE_SHOW_MAKE_MONEY), true);
                }
                UIShow.showPrivateChatAct(getContext(), getUserDetail().getUid(), null);
                StatisticsShareUnlock.onAlert_gotouser_go(getUserDetail().getUid());
                dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestComplete(HttpResponse response) {
        if (response.getUrlParam() == UrlParam.reqVideoChatConfig) {
            if (response.isOk()) {
                VideoConfig config = (VideoConfig) response.getBaseData();
                reSetTVPrice(config);
            }
        } else {
            if (response.isOk()) {
                UserDetail detail = new UserDetail();
                detail.parseJson(response.getResponseString());
                setUserDetail(userDetail);
                setViewData();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
