package com.juxin.predestinate.ui.discover;

import android.graphics.drawable.AnimationDrawable;
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
import com.juxin.predestinate.bean.config.CommonConfig;
import com.juxin.predestinate.module.local.statistics.SendPoint;
import com.juxin.predestinate.module.local.statistics.Statistics;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseDialogFragment;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.ChineseFilter;

import java.util.HashMap;

/**
 * 创建日期：2017/11/1
 * 描述:这是您要找的直播间吗
 *
 * @author :lc
 */
public class SureLiveRoomDlg extends BaseDialogFragment implements View.OnClickListener, RequestComplete {

    private LinearLayout llLiveSure;
    private ImageView ivClose, ivAvatar, ivLiving, ivPeerage, ivMei;
    private TextView tvTitle, tvNick, tvAgeDis, tvLiveTheme;

    private long uid;
    private String liveTheme;
    private UserDetail userDetail;
    private CommonConfig commonConfig;
    private AnimationDrawable animDrawable;

    public SureLiveRoomDlg() {
        settWindowAnimations(R.style.AnimDownInDownOutOverShoot);
        setGravity(Gravity.BOTTOM);
        setDialogSizeRatio(1, 0);
        setCancelable(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setContentView(R.layout.spread_sure_live_room_dlg);
        commonConfig = ModuleMgr.getCommonMgr().getCommonConfig();
        View contentView = getContentView();
        initView(contentView);
        initData();
        ModuleMgr.getCenterMgr().reqOtherInfo(uid, this);
        return contentView;
    }

    public void setData(long uid, String liveTheme) {
        this.uid = uid;
        this.liveTheme = liveTheme;
    }

    private void initView(View contentView) {
        ivClose = contentView.findViewById(R.id.iv_close);
        tvTitle = contentView.findViewById(R.id.tv_title);
        ivAvatar = contentView.findViewById(R.id.iv_avatar);
        ivLiving = contentView.findViewById(R.id.iv_living);
        tvNick = contentView.findViewById(R.id.tv_nick);
        ivPeerage = contentView.findViewById(R.id.iv_peerage);
        ivMei = contentView.findViewById(R.id.iv_mei);
        tvAgeDis = contentView.findViewById(R.id.tv_age_dis);
        tvLiveTheme = contentView.findViewById(R.id.tv_live_theme);
        llLiveSure = contentView.findViewById(R.id.ll_live_sure);

        animDrawable = (AnimationDrawable) ivLiving.getBackground();
        animDrawable.start();
        ivClose.setOnClickListener(this);
        llLiveSure.setOnClickListener(this);
    }

    private void initData() {
        if (userDetail == null) {
            return;
        }
        tvNick.setText(userDetail.getNickname());

        if (userDetail.getTop() != 0) {
            ivMei.setVisibility(View.VISIBLE);
            if (userDetail.isMan()) {
                ivMei.setImageResource(R.drawable.f1_top02);
            } else {
                ivMei.setImageResource(R.drawable.f1_top01);
            }
        } else {
            ivMei.setVisibility(View.GONE);
        }
        String dis = userDetail.getDistance() > 5 ? getString(R.string.user_info_distance_far) : getString(R.string.user_info_distance_near);
        if (userDetail.getAge() != 0 && userDetail.getDistance() != 0) {
            tvAgeDis.setVisibility(View.VISIBLE);
            tvAgeDis.setText(userDetail.getAge() + "岁" + "    -    " + dis);
        } else if (userDetail.getAge() == 0 && userDetail.getDistance() != 0) {
            tvAgeDis.setVisibility(View.VISIBLE);
            tvAgeDis.setText(dis);
        } else if (userDetail.getAge() != 0 && userDetail.getDistance() == 0) {
            tvAgeDis.setVisibility(View.VISIBLE);
            tvAgeDis.setText(userDetail.getAge() + "岁");
        } else {
            tvAgeDis.setVisibility(View.GONE);
        }
        ImageLoader.loadFitCenter(getContext(), commonConfig.queryNobility(userDetail.getNobilityInfo().getRank(),
                userDetail.getGender()).getTitle_icon(), ivPeerage);
        ImageLoader.loadCircleAvatar(getContext(), userDetail.getAvatar(), ivAvatar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                dismiss();
                Statistics.userBehavior(SendPoint.alert_gotoliveroom_close);
                break;
            case R.id.ll_live_sure:
                // TODO: 2017/11/1  跳转 直播页
                dismiss();
                //统计
                HashMap<String, Object> params = new HashMap<>();
                params.put("room_uid",userDetail.getUid());
                params.put("room_id",userDetail.getUid());
                Statistics.userBehavior(SendPoint.alert_gotoliveroom_go,params);
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestComplete(HttpResponse response) {
        if (response.isOk()) {
            userDetail = new UserDetail();
            userDetail.parseJson(response.getResponseString());
            initData();
        }
    }

    @Override
    public void onDestroy() {
        if (animDrawable != null) {
            animDrawable.stop();
        }
        super.onDestroy();
    }
}
