package com.juxin.predestinate.ui.user.my.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.msgview.chatview.input.ChatMediaPlayer;


/**
 * Created by zm on 2016/12/8
 */
public class VoiceView extends LinearLayout implements View.OnClickListener,ChatMediaPlayer.OnPlayListener{

    private ImageView img_voice;
    private TextView txt_voice;
    private AnimationDrawable voiceAnimation = null;

    private String url;//语音地址
    private String length;//语音长度

    public VoiceView(Context context) {
        this(context, null);
    }

    public VoiceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VoiceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }
    //初始化
    private void initView(Context context){
        LayoutInflater.from(context).inflate(R.layout.f1_voice_view,this);
        this.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        txt_voice = (TextView)findViewById(R.id.voice_txt_voice);
        img_voice = (ImageView)findViewById(R.id.voice_img_voice);
        findViewById(R.id.voice_rl_voice).setOnClickListener(this);
    }

    /**
     * 设置数据
     * @param length  语音长度
     */
    public void setData(String url,String length){
        this.url = url;
        this.length = length;
        txt_voice.setText(length);
    }

    /**
     * 开始动画
     */
    private void showAnimation() {
        img_voice.setImageResource(R.drawable.voice_from_icon);
        voiceAnimation = (AnimationDrawable) img_voice.getDrawable();
        voiceAnimation.start();
    }

    /**
     * 停止动画
     */
    private void stopAnimation() {
        if (voiceAnimation != null) {
            voiceAnimation.stop();
        }
        img_voice.setImageResource(R.drawable.y1_talk_sound_l3);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.voice_rl_voice://播放语音
                ChatMediaPlayer.getInstance().togglePlayVoice(url, this);
                break;
        }
    }

    @Override
    public void onStart(String filePath) {
        showAnimation();
    }

    @Override
    public void onStop(String filePath) {
        stopAnimation();
    }

    //退出后停止播放语音并结束动画
    public void close(){
        if(ChatMediaPlayer.getInstance().isPlayingVoice(url)){
            stopAnimation();
            ChatMediaPlayer.getInstance().stopPlayVoice();
        }
    }
}
