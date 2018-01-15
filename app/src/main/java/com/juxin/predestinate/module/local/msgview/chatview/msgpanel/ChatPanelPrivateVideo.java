package com.juxin.predestinate.module.local.msgview.chatview.msgpanel;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.juxin.library.image.ImageLoader;
import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.bean.center.user.detail.UserVideo;
import com.juxin.predestinate.module.local.chat.msgtype.PrivateMessage;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.util.TimeUtil;
import com.juxin.predestinate.module.util.UIShow;

/**
 * 私密视频布局
 */
public class ChatPanelPrivateVideo extends BasePanel implements View.OnClickListener {

    private PrivateMessage msg;

    private ImageView imgVideo;
    private TextView tvTime;

    public ChatPanelPrivateVideo(Context context) {
        super(context);
        setContentView(R.layout.f1_chat_item_panel_secre_video);

        initView();
    }

    private void initView() {
        imgVideo = (ImageView) findViewById(R.id.chat_panel_secre_video_img_video);
        tvTime = (TextView) findViewById(R.id.chat_panel_secre_video_tv_time);

        imgVideo.setOnClickListener(this);
    }

    @Override
    public Context getContext() {
        return super.getContext();
    }

    public void reSetData(PrivateMessage msg) {
        if (msg == null)
            return;

        this.msg = msg;
        tvTime.setText(TimeUtil.getLongToMinuteTime((long) msg.getVideoLen() * 1000));
        ImageLoader.loadRoundFitCenter(context, msg.getVideoThumb(), imgVideo, 12, com.juxin.library.R.drawable.default_pic, com.juxin.library.R.drawable.default_pic);
    }

    @Override
    public void onClick(View view) {
        if (msg == null)
            return;
        //跳转到视频播放画面
        UserVideo userVideo = new UserVideo();
        userVideo.setOpen(2);
        userVideo.setPic(msg.getVideoThumb());
        userVideo.setVideo(msg.getVideoUrl());
        userVideo.setContent(msg.getInfo());
        userVideo.setId(msg.getMsgID());
        userVideo.setDuration(msg.getVideoLen());
        UIShow.showSecretVideoPlayerDlg((FragmentActivity) App.activity, userVideo, true);
    }
}