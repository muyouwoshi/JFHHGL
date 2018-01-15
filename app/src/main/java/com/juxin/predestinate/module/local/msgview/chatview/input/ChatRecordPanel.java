package com.juxin.predestinate.module.local.msgview.chatview.input;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juxin.library.log.PLogger;
import com.juxin.library.log.PToast;
import com.juxin.library.utils.FileUtil;
import com.juxin.library.view.BasePanel;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.msgview.ChatAdapter;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.util.BaseHandler;

/**
 * 音频录制panel
 * Created by Kind on 2017/3/30.
 */
public class ChatRecordPanel extends BasePanel implements ChatMediaRecord.OnRecordListener {

    private boolean isPosY = true;//是否手指跑到屏幕上
    private boolean isSend = false;//是否可以发送,这个字段主要是给录音超过60秒,自动发送后重复发送用的

    private ChatAdapter.ChatInstance chatInstance = null;
    private ImageView ani_record_image;
    private TextView ani_record_hint;

    // 录音动画
    private Drawable[] micImages;
    public Handler mHandler = new MyHandler(this);

    private static class MyHandler extends BaseHandler {

        public MyHandler(Object object) {
            super(object);
        }

        @Override
        public void handleEvent(Message msg, Object object) {
            switch (msg.what) {
                case 1:
                    this.removeMessages(1);
                    if (ChatMediaRecord.getInstance().startRecordVoice((ChatRecordPanel) object)) {
                        ((ChatRecordPanel) object).setVisibility(View.VISIBLE);
                        ((ChatRecordPanel) object).ani_record_hint.setText(
                                ((ChatRecordPanel) object).getContext().getString(R.string.chat_move_up_to_cancel));
                        ((ChatRecordPanel) object).isPosY = true;
                        ((ChatRecordPanel) object).isSend = true;
                    }
                    break;

                default:
                    break;
            }
        }
    }

    public ChatRecordPanel(Context context, ChatAdapter.ChatInstance chatInstance) {
        super(context);
        this.chatInstance = chatInstance;
        setContentView(R.layout.p1_chat_record);
        initView();
    }

    public void initView() {
        // 居中显示
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        getContentView().setLayoutParams(lp);

        ani_record_image = (ImageView) findViewById(R.id.chat_mic_image);
        ani_record_hint = (TextView) findViewById(R.id.chat_recording_hint);

        // 动画资源文件,用于录制语音时
        micImages = new Drawable[]{
                getDrawable(R.drawable.y1_talk_sound_ly_1),
                getDrawable(R.drawable.y1_talk_sound_ly_2),
                getDrawable(R.drawable.y1_talk_sound_ly_3),
                getDrawable(R.drawable.y1_talk_sound_ly_4)
        };
        setVisibility(View.INVISIBLE);
    }

    /**
     * 根据Touch动作，开始录音、停止、取消等动作。
     *
     * @param action Touch动作。
     * @param posY   手指位置。
     */
    public void onTouch(int action, float posY) {
        onTouch(action, posY, null, null, 0);
    }

    /**
     * 根据Touch动作，开始录音、停止、取消等动作。
     *
     * @param action Touch动作。
     * @param posY   手指位置。
     */
    public void onTouch(int action, float posY, String channelId, String whisperId, int know) {
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mHandler.removeMessages(1);
                mHandler.sendEmptyMessageDelayed(1, 300);
                break;

            case MotionEvent.ACTION_MOVE:
                if (posY < 0) {
                    mHandler.removeMessages(1);
                    ani_record_image.setImageResource(R.drawable.v3_voice_up_cancel);
                    ani_record_hint.setText(getContext().getString(R.string.chat_release_to_cancel));
                    isPosY = false;
                } else {
                    ani_record_hint.setText(getContext().getString(R.string.chat_move_up_to_cancel));
                    isPosY = true;
                }
                break;

            case MotionEvent.ACTION_UP:
                mHandler.removeMessages(1);
                if (!isSend) return;
                isSend = false;

                setVisibility(View.INVISIBLE);
                ChatMediaRecord.getInstance().stopRecordVoice();

                if (posY < 0) {//取消发送
                    FileUtil.deleteFile(ChatMediaRecord.getInstance().getVoiceFileName());
                } else {
                    int voice_length = (ChatMediaRecord.getInstance().getVoiceDuration() + 500) / 1000;
                    String voice_file = ChatMediaRecord.getInstance().getVoiceFileName();
                    if (voice_length >= 1) {
                        ModuleMgr.getChatMgr().sendVoiceMsg(channelId, whisperId, voice_file, voice_length, know);
                    } else {
                        PToast.showShort("录音时间太短!");
                        FileUtil.deleteFile(voice_file);//清除无效文件
                    }
                }
                break;
            default:
                mHandler.removeMessages(1);
                setVisibility(View.INVISIBLE);
                ChatMediaRecord.getInstance().stopRecordVoice();
                FileUtil.deleteFile(ChatMediaRecord.getInstance().getVoiceFileName());
                break;
        }
    }

    /**
     * 根据资源id获取一个Drawable。
     *
     * @param id 资源id。
     * @return Drawable实例。
     */
    private Drawable getDrawable(int id) {
        try {
            return getContext().getResources().getDrawable(id);
        } catch (Exception e) {
            PLogger.printThrowable(e);
        }
        return null;
    }

    @Override
    public void onDB(int db) {
        int index = db / 25;
        if (index >= 4) index = 3;
        try {
            if (isPosY) ani_record_image.setImageDrawable(micImages[index]);
        } catch (Exception e) {
            PLogger.printThrowable(e);
        }
    }

    @Override
    public void onProgress(long time) {
    }

    @Override
    public void onProgressMax() {
        onTouch(MotionEvent.ACTION_UP, 0, chatInstance.chatAdapter.getChannelId(),
                chatInstance.chatAdapter.getWhisperId(), chatInstance.chatAdapter.getChatInfo().getKnow());
    }

    @Override
    public void onStatus(int status) {
    }
}