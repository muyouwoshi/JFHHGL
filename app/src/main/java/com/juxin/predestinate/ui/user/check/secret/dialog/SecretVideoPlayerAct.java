package com.juxin.predestinate.ui.user.check.secret.dialog;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;
import com.juxin.library.image.ImageLoader;
import com.juxin.library.log.PToast;
import com.juxin.library.request.DownloadListener;
import com.juxin.library.view.DownloadProgressView;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserVideo;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.BaseActivity;
import com.juxin.predestinate.module.logic.baseui.custom.CountDownTextView;
import com.juxin.predestinate.module.logic.baseui.custom.TextureVideoView;
import com.juxin.predestinate.module.util.TimeUtil;
import com.juxin.predestinate.module.util.UIShow;
import com.juxin.predestinate.ui.user.util.CenterConstant;

/**
 * 私密视频播放页
 * Created by Su on 2017/5/17.
 */
public class SecretVideoPlayerAct extends BaseActivity implements View.OnClickListener, DownloadListener {

    private TextureVideoView tvv_player;
    private ImageView iv_pic, iv_start;
    private CountDownTextView tv_video_time;
    private DownloadProgressView progress_bar;
    private UserVideo userVideo;

    private String videoLocalUrl;  // 视频文件本地地址

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.f1_secret_video_player);
        initView();

        if (getIntent().getBooleanExtra(CenterConstant.USER_CHECK_VIDEO_TITLE_KEY, false)) {
            initVideoTop();
        }
    }

    private void initVideoTop() {
        findViewById(R.id.base_title).setVisibility(View.VISIBLE);
        setBackView(getResources().getString(R.string.check_info_secret_video));
        setTitleRightImgText(R.drawable.f1_private_photo_evaluate, getResources().getString(R.string.photo_feel_pingjia), RightClickListener);
    }

    private View.OnClickListener RightClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            UIShow.showPhotoFeelDlg(SecretVideoPlayerAct.this,1,1);
        }
    };

    private void initView() {
        userVideo = getIntent().getParcelableExtra(CenterConstant.USER_CHECK_VIDEO_KEY);
        if (userVideo == null) {
            PToast.showShort(getString(R.string.user_info_check_video_fail));
            return;
        }

        iv_pic = (ImageView) findViewById(R.id.iv_cover);
        iv_start = (ImageView) findViewById(R.id.btn_start);
        tvv_player = (TextureVideoView) findViewById(R.id.tvv_player);
        progress_bar = (DownloadProgressView) findViewById(R.id.progress_bar);
        tv_video_time = (CountDownTextView) findViewById(R.id.tv_video_time);

        iv_start.setOnClickListener(this);
        findViewById(R.id.btn_back).setOnClickListener(this);

        ImageLoader.loadFitCenter(this, userVideo.getPic(), iv_pic);
        tv_video_time.setText(TimeUtil.getLongToMinuteTime(userVideo.getDuration() * 1000l));
        tv_video_time.setCountDownTimes(userVideo.getDuration() * 1000);
        tv_video_time.setCountDownListener(tipsListener);
    }

    /**
     * 播放视频
     */
    private void playLocalVideo() {
        tvv_player.setVideoPath(videoLocalUrl);
        tvv_player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mp) {
                tvv_player.start();
                tv_video_time.start();
                iv_start.setVisibility(View.GONE);
                ViewAnimator.animate(iv_pic).alpha(1, 0).duration(1000)
                        .start().onStop(new AnimationListener.Stop() {
                    @Override
                    public void onStop() {
                        iv_pic.setVisibility(View.GONE);
                    }
                });
            }
        });

        tvv_player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                resetPlay();
                return true;
            }
        });
    }

    private void resetPlay() {
        resetCountTime();
        tvv_player.stopPlayback();
        iv_pic.setVisibility(View.VISIBLE);
        iv_start.setVisibility(View.VISIBLE);
    }

    // 重置倒计时
    private void resetCountTime() {
        tv_video_time.cancel();
        tv_video_time.setText(TimeUtil.getLongToMinuteTime(userVideo.getDuration() * 1000l));
        tv_video_time.setCountDownTimes(userVideo.getDuration() * 1000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                onViewStart();
                break;
            case R.id.btn_back:
                finish();
                break;
        }
    }

    private void onViewStart() {
        iv_start.setVisibility(View.GONE);
        progress_bar.setVisibility(View.VISIBLE);
        ModuleMgr.getHttpMgr().downloadVideo(userVideo.getVideo(), this);//下载小视频
    }

    private CountDownTextView.OnCountDownListener tipsListener = new CountDownTextView.OnCountDownListener() {
        @Override
        public void onTick(long min, long sec) {
        }

        @Override
        public void onFinish() {
            resetPlay();
        }
    };

    // --------------------下载回调--------------------

    @Override
    public void onStart(String url, String filePath) {
        progress_bar.setProgress(0);
    }

    @Override
    public void onProcess(String url, int process, long size) {
        progress_bar.setVisibility(View.VISIBLE);
        progress_bar.setProgress(process);
    }

    @Override
    public void onSuccess(String url, String filePath) {
        progress_bar.setVisibility(View.GONE);
        videoLocalUrl = filePath;
        playLocalVideo();
    }

    @Override
    public void onFail(String url, Throwable throwable) {
        progress_bar.setVisibility(View.GONE);
        resetPlay();
        PToast.showShort(getString(R.string.user_info_check_video_fail));
    }

}
