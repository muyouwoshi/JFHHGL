package com.juxin.predestinate.module.local.msgview.chatview.msgpanel.video;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.juxin.predestinate.module.local.msgview.chatview.input.ChatMediaPlayer;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.baseui.custom.TextureVideoView;

/**
 * 视频播放dialog
 * Created by ZRP on 2016/7/4.
 */
public class VideoPlayDialog extends DialogFragment {

    private Dialog dialog;
    private FragmentActivity activity;
    private TextureVideoView textureView;
    private String url;//视频的本地地址

    public VideoPlayDialog() {
        this((FragmentActivity) App.activity, null);
    }

    /**
     * Instantiates a new Popup.
     *
     * @param activity the context
     */
    @SuppressLint("ValidFragment")
    public VideoPlayDialog(FragmentActivity activity, String url) {
        this.activity = activity;
        this.url = url;
        init(activity);
    }

    private void init(Context context) {
        RelativeLayout contentLayout = new RelativeLayout(context);
        contentLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        contentLayout.setBackgroundColor(Color.BLACK);

        textureView = new TextureVideoView(context);
        RelativeLayout.LayoutParams videoParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        videoParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        textureView.setLayoutParams(videoParams);
        textureView.setPlayingScreenOn(true);

        TextView textView = new TextView(context);
        RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        textParams.bottomMargin = 100;
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(textParams);
        textView.setText("轻触退出");

        contentLayout.addView(textureView);
        contentLayout.addView(textView);
        contentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });

        dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setCanceledOnTouchOutside(true);//触摸屏幕取消窗体
        dialog.setCancelable(true);//按返回键取消窗体

        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);//位于屏幕中间
        window.setContentView(contentLayout);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return dialog;
    }

    /**
     * Show.
     */
    public void show() {
        synchronized (this) {
            if (isAdded()) {
                Log.e("Popup", "show: ------>the popup dialog has added to window,it's returned without showing.");
                return;
            }
            try {
                FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                transaction.add(VideoPlayDialog.this, activity.toString());
                transaction.commitAllowingStateLoss();
                ChatMediaPlayer.getInstance().playVideo(false, textureView, url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        textureView.stopPlayback();
    }
}
