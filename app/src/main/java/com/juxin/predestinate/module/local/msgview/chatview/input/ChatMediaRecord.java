package com.juxin.predestinate.module.local.msgview.chatview.input;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.juxin.library.log.PLogger;
import com.juxin.library.log.PToast;
import com.juxin.library.utils.FileUtil;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.library.dir.DirType;
import com.juxin.predestinate.module.util.TimeUtil;

import java.io.File;

/**
 * 录语音
 * Created by Kind on 2017/3/31.
 */

public class ChatMediaRecord implements Handler.Callback{
    public static final int MEDIA_RECORDER_EVENT_Error = 0;
    public static final int MEDIA_RECORDER_EVENT_Start = 1;
    public static final int MEDIA_RECORDER_EVENT_Stop = 2;
    private static final long MEDIA_RECORDER_Countdown = 60 * 1000;
    private static final int MEDIA_RECORDER_Frequency = 200;

    private MediaRecorder mediaRecorder = null;
    private File soundFile = null;

    private boolean recording = false;
    private long startTime = 0;
    private long countdown = MEDIA_RECORDER_Countdown;
    private Handler handler = null;

    private OnRecordListener onRecordListener = null;

    private static ChatMediaRecord ourInstance = new ChatMediaRecord();

    public static ChatMediaRecord getInstance() {
        return ourInstance;
    }

    private ChatMediaRecord() {
        Looper looper;

        if ((looper = Looper.myLooper()) != null) {
            handler = new Handler(looper, this);
        } else if ((looper = Looper.getMainLooper()) != null) {
            handler = new Handler(looper, this);
        } else {
            handler = null;
        }
    }

    /**
     * 开始录制音频。
     *
     * @param onRecordListener 录制监听。
     * @return 是否开始录制。
     */
    public boolean startRecordVoice(OnRecordListener onRecordListener) {
        return startRecordVoice(MEDIA_RECORDER_Countdown, onRecordListener);
    }

    /**
     * 模拟录制一段100ms的音频，用于主动请求权限时调用，其他时机请勿进行调用。
     */
    public void requestPermission() {
        startRecordVoice(100, new OnRecordListener() {
            @Override
            public void onStatus(int status) {
                if (status == MEDIA_RECORDER_EVENT_Error || status == MEDIA_RECORDER_EVENT_Stop) {
                    PLogger.d("------>requestPermission：" + status);
                    FileUtil.deleteFile(getVoiceFileName());
                }
            }

            @Override
            public void onDB(int db) {
            }

            @Override
            public void onProgress(long time) {
            }

            @Override
            public void onProgressMax() {
                PLogger.d("------>requestPermission：onProgressMax");
                FileUtil.deleteFile(getVoiceFileName());
            }
        });
    }

    /**
     * 开始录制音频。
     *
     * @param countdown        录制时长。
     * @param onRecordListener 录制监听。
     * @return 是否开始录制。
     */
    public boolean startRecordVoice(long countdown, OnRecordListener onRecordListener) {
        stopRecordVoice();
        this.onRecordListener = onRecordListener;

        if (countdown < 0 || countdown > MEDIA_RECORDER_Countdown) {
            countdown = MEDIA_RECORDER_Countdown;
        }
        this.countdown = countdown;

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            PToast.showShort("SD卡不存在，请插入SD卡！");
            if (onRecordListener != null) onRecordListener.onStatus(MEDIA_RECORDER_EVENT_Error);
            return false;
        }

        try {
            // 创建保存录音的音频文件
            soundFile = new File(DirType.getVoiceDir() + TimeUtil.getFormatTimeFileName() + ".amr");

            mediaRecorder = new MediaRecorder();
            // 设置录音的声音来源
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 设置录制的声音的输出格式（必须在设置声音编码格式之前设置）
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            // 设置声音编码的格式
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(soundFile.getAbsolutePath());
            mediaRecorder.prepare();//得到设置的音频来源，编码器，文件格式等等内容，在start()之前调用

            // 开始录音
            mediaRecorder.start();
            recording = true;
            setStreamMute(true);

            startTime = System.currentTimeMillis();

            if (onRecordListener != null) onRecordListener.onStatus(MEDIA_RECORDER_EVENT_Start);
            if (handler != null) handler.sendEmptyMessage(MEDIA_RECORDER_EVENT_Start);

            PLogger.d("countdown: " + this.countdown);
        } catch (Exception e) {
            PLogger.printThrowable(e);

            if (onRecordListener != null) {
                onRecordListener.onStatus(MEDIA_RECORDER_EVENT_Error);
            }
            return false;
        }
        return true;
    }

    /**
     * 停止录制音频。
     */
    public void stopRecordVoice() {
        if (!recording) return;
        if (handler != null) handler.sendEmptyMessage(MEDIA_RECORDER_EVENT_Stop);
        PLogger.d("length(毫秒): " + getVoiceDuration() + "; size(字节): " + soundFile.length() + "; file: " + getVoiceFileName());

        try {
            recording = false;
            setStreamMute(false);

            // 停止录音
            mediaRecorder.stop();

            // 释放资源
            mediaRecorder.release();
            mediaRecorder = null;
        } catch (Exception e) {
            PLogger.printThrowable(e);
        }

        if (onRecordListener != null) onRecordListener.onStatus(MEDIA_RECORDER_EVENT_Stop);
    }

    /**
     * 是否正在录制音频。
     *
     * @return 录制状态。
     */
    public boolean isRecording() {
        return mediaRecorder == null ? false : recording;
    }

    private int BASE = 1;

    private void updateMicStatus() {
        if (!isRecording()) return;

        int ratio = mediaRecorder.getMaxAmplitude() / BASE;// 该方法返回的是0到32767范围的16位整型
        int db = 0;
        if (ratio > 1) db = (int) (20 * Math.log10(ratio));
        if (onRecordListener != null && isRecording()) onRecordListener.onDB(db);
    }

    private void updateProgress() {
        long time = startTime + countdown - System.currentTimeMillis();
        if (time < 0) {
            if (startTime > 0 && onRecordListener != null) {//超过录音最大值
                onRecordListener.onProgressMax();
            }
            stopRecordVoice();
        } else {
            if (onRecordListener != null) onRecordListener.onProgress(time);
        }
    }

    /**
     * 获取录制的音频文件的文件路径。
     *
     * @return 文件路径。
     */
    public String getVoiceFileName() {
        return soundFile == null ? "" : soundFile.getAbsolutePath();
    }

    /**
     * 获取录制的音频文件的音频时长。
     *
     * @return 时长（毫秒）。
     */
    public int getVoiceDuration() {
        int duration = -1;

        try {
            MediaPlayer mp = MediaPlayer.create(App.context, Uri.parse(getVoiceFileName()));
            if (mp != null) {
                duration = mp.getDuration();
                mp.release();
            }
        } catch (Exception e) {
            PLogger.printThrowable(e);
        }

        return duration;
    }

    private int volume = 0;

    public void setStreamMute(boolean mute) {
        try {
            AudioManager audioManager = (AudioManager) App.context.getSystemService(Context.AUDIO_SERVICE);

            if (mute) {
                volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            } else {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
            }
        } catch (Exception e) {
            PLogger.printThrowable(e);
        }
    }

    public OnRecordListener getRecordListener() {
        return onRecordListener;
    }

    /**
     * 监听录制时麦克风的强弱。
     */
    public interface OnRecordListener {
        /**
         * 0 失败；1 开始录制；2 录制结束。
         *
         * @param status 0 1 2。
         */
        void onStatus(int status);

        /**
         * 计算出的分贝值正常值域为0 dB 到90.3 dB。
         *
         * @param db
         */
        void onDB(int db);

        /**
         * 从设定的时间开始，将录制时间从大开始递减。<br>
         * 如果没有指定时间，默认是60秒。
         *
         * @param time 毫秒。
         */
        void onProgress(long time);

        /**
         * 录音时间超过最大时间
         */
        void onProgressMax();
    }

    /**
     * 将声音文件上传到服务器。
     */
    public interface OnUploadListener {
        /**
         * 上传到服务器后的url，和音频文件长度。
         *
         * @param url      服务器url。
         * @param duration 音频长。
         */
        void onUpload(String url, int duration);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MEDIA_RECORDER_EVENT_Start:
                if (recording) {
                    updateMicStatus();
                    updateProgress();
                    handler.sendEmptyMessageDelayed(MEDIA_RECORDER_EVENT_Start, MEDIA_RECORDER_Frequency);
                }
                break;

            case MEDIA_RECORDER_EVENT_Stop:
                handler.removeMessages(MEDIA_RECORDER_EVENT_Start);
                break;
        }
        return true;
    }

    /**
     * 将录制的文件从本地上传到服务器。
     *
     * @param uploadListener 监听上传情况。
     */
    public void upload(final OnUploadListener uploadListener) {
        if (isRecording()) {
            stopRecordVoice();
        }

        final int duration = getVoiceDuration();
        final String fileName = getVoiceFileName();

        if (duration < 1000 || TextUtils.isEmpty(fileName)) {
            if (uploadListener != null) {
                uploadListener.onUpload("", 0);
            }
            return;
        }

//        ModuleMgr.getCommonMgr().sendHttpFile(Constant.INT_CHAT_VOICE, fileName, null, new HttpMgr.IReqComplete() {
//            @Override
//            public void onReqComplete(HttpResult result) {
//                String httpURL = "";
//                if (result.isOk()) {
//                    UpLoadResult upLoadResult = (UpLoadResult) result.getBaseData();
//                    httpURL = upLoadResult.getAmr();
//                }
//
//                if (uploadListener != null) {
//                    uploadListener.onUpload(httpURL, duration);
//                }
//            }
//        });
    }
}
