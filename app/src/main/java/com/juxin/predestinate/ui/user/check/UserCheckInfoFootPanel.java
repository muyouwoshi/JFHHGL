package com.juxin.predestinate.ui.user.check;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.library.log.PToast;
import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserDetail;
import com.juxin.predestinate.module.local.statistics.SourcePoint;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.request.HttpResponse;
import com.juxin.predestinate.module.logic.request.RequestComplete;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.user.check.bean.BeautyCar;
import com.juxin.predestinate.ui.user.check.bean.VideoConfig;
import com.juxin.predestinate.ui.user.check.self.info.UserInfoPanel;
import com.juxin.predestinate.ui.user.util.AlbumHorizontalPanel;
import com.juxin.predestinate.ui.user.util.CenterConstant;
import com.juxin.predestinate.ui.utils.NoDoubleClickListener;

/**
 * 查看他人资料底部panel
 */
public class UserCheckInfoFootPanel extends BasePanel implements RequestComplete {

    private boolean isMatchGame = false;
    private final int channel;
    private UserDetail userDetail;   // 用户资料
    private UserInfoPanel infoPanel; // 资料列表

    private LinearLayout albumLayout, videoLayout, chatLayout, ll_her_car;
    private TextView tv_album, tv_video, tv_video_price, tv_audio_price, tv_suc_num, tv_fail_num;
    private AlbumHorizontalPanel albumPanel, videoPanel;
    private ImageView iv_auth_photo, iv_auth_phone, iv_auth_video, iv_match_game; // 认证

    public UserCheckInfoFootPanel(Context context, int channel, UserDetail userProfile) {
        super(context);
        setContentView(R.layout.p1_user_checkinfo_footer);
        this.channel = channel;
        this.userDetail = userProfile;

        initView();
    }

    private void initView() {
        if (channel == CenterConstant.USER_CHECK_INFO_OWN) {
            userDetail = ModuleMgr.getCenterMgr().getMyInfo();
        }
        if (userDetail == null) {
            PToast.showShort(getContext().getString(R.string.user_other_info_req_fail));
            return;
        }

        LinearLayout info_container = (LinearLayout) findViewById(R.id.ll_info_container);
        LinearLayout ll_secret_album = (LinearLayout) findViewById(R.id.ll_secret_album);
        LinearLayout ll_secret_video = (LinearLayout) findViewById(R.id.ll_secret_video);
        tv_album = (TextView) findViewById(R.id.album_num);
        tv_video = (TextView) findViewById(R.id.video_num);
        chatLayout = (LinearLayout) findViewById(R.id.item_chat);
        tv_video_price = (TextView) findViewById(R.id.tv_video_price);
        tv_audio_price = (TextView) findViewById(R.id.tv_audio_price);
        ll_her_car = (LinearLayout) findViewById(R.id.ll_her_car);
        iv_match_game = (ImageView) findViewById(R.id.iv_match_game);
        tv_suc_num = (TextView) findViewById(R.id.tv_suc_num);
        tv_fail_num = (TextView) findViewById(R.id.tv_fail_num);
        albumLayout = (LinearLayout) findViewById(R.id.album_item);
        videoLayout = (LinearLayout) findViewById(R.id.video_item);
        iv_auth_photo = (ImageView) findViewById(R.id.iv_auth_photo);
        iv_auth_phone = (ImageView) findViewById(R.id.iv_auth_phone);
        iv_auth_video = (ImageView) findViewById(R.id.iv_auth_video);
        refreshAuth();  // 认证状态

        // 照片列表
        if (userDetail.getUserPhotos().size() > 0) {
            ll_secret_album.setVisibility(View.VISIBLE);
            albumPanel = new AlbumHorizontalPanel(getContext(), channel, AlbumHorizontalPanel.EX_HORIZONTAL_ALBUM, userDetail);
            albumLayout.addView(albumPanel.getContentView());
            tv_album.setText(String.valueOf(userDetail.getUserPhotos().size()));
        }

        // 视频列表
        if (userDetail.getUserVideos().size() > 0) {
            ll_secret_video.setVisibility(View.VISIBLE);
            videoPanel = new AlbumHorizontalPanel(getContext(), channel, AlbumHorizontalPanel.EX_HORIZONTAL_VIDEO, userDetail);
            videoLayout.addView(videoPanel.getContentView());
            tv_video.setText(String.valueOf(userDetail.getUserVideos().size()));
        }

        // 男用户点击首页贵族进入TA人资料，相册、视频隐藏
        if (ModuleMgr.getCenterMgr().getMyInfo().isMan() && channel == CenterConstant.USER_CHECK_PEERAGE_OTHER) {
            ll_secret_album.setVisibility(View.GONE);
            ll_secret_video.setVisibility(View.GONE);
        }

        // TA人详细信息列表
        info_container.setVisibility(View.VISIBLE);
        infoPanel = new UserInfoPanel(getContext(), userDetail, channel);
        info_container.addView(infoPanel.getContentView());
    }

    // 添加右滑退出忽略view
    public void setSlideIgnoreView(BaseActivity activity) {
        activity.addIgnoredView(albumLayout);
        activity.addIgnoredView(videoLayout);
    }

    /**
     * 认证状态
     */
    public void refreshAuth() {
        if (channel == CenterConstant.USER_CHECK_INFO_OWN) {
            boolean isAuthVideo = ModuleMgr.getCommonMgr().getVideoVerify().isVerifyVideo();
            iv_auth_video.setVisibility(isAuthVideo ? View.VISIBLE : View.GONE);
        }

        iv_auth_photo.setVisibility(userDetail.isVerifyIdcard() ? View.VISIBLE : View.GONE);
        iv_auth_phone.setVisibility(userDetail.isVerifyCellphone() ? View.VISIBLE : View.GONE);
    }

    /**
     * 她的座驾
     */
    public void refreshCar() {
        //是否显示她的座驾 对方女号显示
        if (channel != CenterConstant.USER_CHECK_INFO_OWN && !userDetail.isMan() && userDetail.isBeauty_switch()) {
            ll_her_car.setVisibility(View.VISIBLE);
            iv_match_game.setOnClickListener(listener);
            ModuleMgr.getCenterMgr().reqBeautyCar(userDetail.uid, this);
        }
    }

    public void refreshView(UserDetail userDetail) {
        if (userDetail == null) {
            return;
        }
        if (channel == CenterConstant.USER_CHECK_INFO_OWN) {
            tv_album.setText(String.valueOf(userDetail.getUserPhotos().size()));
            albumPanel.refresh(userDetail);
            refreshAuth();
            return;
        }
        infoPanel.refreshView(userDetail);
    }

    /**
     * 刷新聊天价格
     */
    public void refreshChatPrice(VideoConfig config) {
        if (config == null) {
            return;
        }
        chatLayout.setVisibility((config.isVideoChat() || config.isVoiceChat()) ? View.VISIBLE : View.GONE);
        LinearLayout videoLayout = (LinearLayout) findViewById(R.id.layout_video);
        videoLayout.setVisibility(config.isVideoChat() ? View.VISIBLE : View.GONE);
        LinearLayout voiceLayout = (LinearLayout) findViewById(R.id.layout_voice);
        voiceLayout.setVisibility(config.isVoiceChat() ? View.VISIBLE : View.GONE);
        findViewById(R.id.layout_spacer).setVisibility((config.isVideoChat() && config.isVoiceChat()) ? View.VISIBLE : View.GONE);

        if (!ModuleMgr.getCenterMgr().getMyInfo().isMan()) { //男的资料不再显示价格
            tv_video_price.setText("");
            tv_audio_price.setText("");
        } else {
            tv_video_price.setText(getContext().getString(R.string.user_info_chat_video, config.getVideoPrice()));
            tv_audio_price.setText(getContext().getString(R.string.user_info_chat_voice, config.getAudioPrice()));
        }
        iv_auth_video.setVisibility(config.isVerifyVideo() ? View.VISIBLE : View.GONE);
    }

    /**
     * 刷新私密视频状态
     */
    public void freshSecretVideo() {
        videoPanel.refresh(userDetail);
    }

    private NoDoubleClickListener listener = new NoDoubleClickListener() {
        @Override
        public void onNoDoubleClick(View v) {
            switch (v.getId()) {
                case R.id.iv_match_game:
                    isMatchGame = true;
                    SourcePoint.getInstance().lockSource((Activity) getContext(), "game_grabgoddess");//添加游戏页面的统计来源
                    UIShow.showCatchGoddessAct(getContext(), userDetail.getUid());
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onRequestComplete(HttpResponse response) {
        if (response.isOk()) {
            BeautyCar beautyCar = new BeautyCar();
            beautyCar.parseJson(response.getResponseString());
            if (tv_suc_num == null || tv_fail_num == null) {
                return;
            }
            tv_suc_num.setText(String.format(context.getString(R.string.catch_game_num), beautyCar.getWin_count()));
            tv_fail_num.setText(String.format("%d 局", beautyCar.getLose_count()));
        }
    }
}
