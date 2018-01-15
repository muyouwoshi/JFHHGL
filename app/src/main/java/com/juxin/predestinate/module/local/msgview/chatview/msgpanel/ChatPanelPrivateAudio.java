package com.juxin.predestinate.module.local.msgview.chatview.msgpanel;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.juxin.library.utils.FileUtil;
import com.juxin.library.utils.StringUtils;
import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.chat.msgtype.CommonMessage;
import com.juxin.predestinate.module.local.chat.msgtype.PrivateMessage;
import com.juxin.predestinate.module.local.msgview.chatview.input.ChatMediaPlayer;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.util.MediaNotifyUtils;
import com.juxin.predestinate.module.util.UIUtil;

import org.w3c.dom.Text;

/**
 * 私密语音及文字布局
 */
public class ChatPanelPrivateAudio extends BasePanel implements View.OnClickListener, ChatMediaPlayer.OnPlayListener {

    private PrivateMessage msg;
    private AnimationDrawable voiceAnimation = null;

    private ImageView imgAudio;
    private TextView tvContent;

    public ChatPanelPrivateAudio(Context context) {
        super(context);
        setContentView(R.layout.f1_chat_item_panel_secre_voice);

        initView();
    }

    private void initView() {
        imgAudio = (ImageView) findViewById(R.id.chat_panel_secre_audio_img_audio);
        tvContent = (TextView) findViewById(R.id.chat_panel_secre_audio_tv_content);

        findViewById(R.id.rl_content).setOnClickListener(this);
    }

    @Override
    public Context getContext() {
        return super.getContext();
    }

    public void reSetData(PrivateMessage msg) {
        if (msg == null)
            return;

        this.msg = msg;
        if (!TextUtils.isEmpty(msg.getVoiceUrl())){ //私密语音
            imgAudio.setImageResource(R.drawable.f1_voice_from_icon);
            tvContent.setText(context.getString(R.string.private_voice));
            onVoiceDisplayContent();
        }else {  //私密消息
            imgAudio.setImageResource(R.drawable.f1_lttext);
            tvContent.setText(msg.getInfo()+"");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_content:
                String url = msg.getVoiceUrl();
                if (TextUtils.isEmpty(url)) {
                    break;
                }
                ChatMediaPlayer.getInstance().togglePlayVoice(StringUtils.spliceStringAmr(url), this);
                break;
            default:
                break;
        }
    }

    private void showAnimation() {

        imgAudio.setImageResource(R.drawable.f1_voice_from_icon);

        voiceAnimation = (AnimationDrawable) imgAudio.getDrawable();
        voiceAnimation.start();
    }

    private void stopAnimation() {
        if (voiceAnimation != null) {
            voiceAnimation.stop();
        }
        imgAudio.setImageResource(R.drawable.f1_voiceico);
    }

    @Override
    public void onStart(String filePath) {
        showAnimation();
    }

    @Override
    public void onStop(String filePath) {
        stopAnimation();
        if (!TextUtils.isEmpty(filePath)) {
            MediaNotifyUtils.playSound(getContext(), R.raw.play_voice_after);
        }
    }

    /**
     * 加载展示语音消息
     */
    private void onVoiceDisplayContent() {
        // stopAnimation();

//        time.setText("" + msg.getVoiceLen() + "''");
//        time.setWidth(UIUtil.dp2px((float) (20.f + Math.sqrt(msg.getVoiceLen() - 1) * 20)));

        if (ChatMediaPlayer.getInstance().isPlayingVoice(msg.getVoiceUrl())) {
            ChatMediaPlayer.getInstance().setOnVoicePlayListener(this);
            showAnimation();
        } else {
            stopAnimation();
        }
    }
}