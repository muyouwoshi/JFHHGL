package com.juxin.predestinate.module.local.msgview.chatview.input;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.juxin.library.log.PLogger;
import com.juxin.library.log.PSP;
import com.juxin.library.log.PToast;
import com.juxin.library.observe.MsgMgr;
import com.juxin.library.request.DownloadListener;
import com.juxin.library.utils.FileUtil;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;
import com.juxin.predestinate.module.logic.baseui.custom.TextureVideoView;

import java.io.File;
import java.io.FileInputStream;

/**
 * 媒体文件播放工具
 * Created by Kind on 2017/5/9.
 */
public class ChatMediaPlayer implements Handler.Callback, SensorEventListener {

    private boolean playing = false;            //视频或音频是否正在播放

    private MediaPlayer mediaPlayer = null;     //媒体播放
    private String oriFilePath = null;          //播放文件地址：本地文件地址

    private static final int MEDIA_RECORDER_EVENT_Start = 0;
    private static final int MEDIA_RECORDER_EVENT_Stop = 1;
    private static final int frequency = 200;
    private Handler handler = null;

    public void setOnVoicePlayListener(OnPlayListener onVoicePlayListener) {
        this.onPlayListener = onVoicePlayListener;
    }

    private OnPlayListener onPlayListener = null;   //播放监听

    private static ChatMediaPlayer ourInstance = new ChatMediaPlayer();

    public static ChatMediaPlayer getInstance() {
        return ourInstance;
    }

    private ChatMediaPlayer() {
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
     * 切换播放状态，如果当前播放的和将要播放的是同一个文件，则停止播放。
     *
     * @param filePath       音频文件。
     * @param onPlayListener 播放状态监听。
     */
    public synchronized void togglePlayVoice(final String filePath, final OnPlayListener onPlayListener, DownloadListener downloadListener) {
        if (isPlayingVoice(filePath)){
            oriFilePath = null;
            stopPlayVoice();
            return;
        }
        playVoice(filePath, onPlayListener, downloadListener);
    }

    public synchronized void togglePlayVoice(final String filePath, final OnPlayListener onPlayListener) {
        togglePlayVoice(filePath, onPlayListener, null);
    }


    /**
     * 播放一个音频，外部控制传入的音频地址为本地或者网络
     *
     * @param filePath       音频文件。
     * @param onPlayListener 播放状态监听。
     */
    public synchronized void playVoice(final String filePath, OnPlayListener onPlayListener, final DownloadListener downloadListener) {
        stopPlayVoice();
        this.oriFilePath = filePath;
        this.onPlayListener = onPlayListener;

        PLogger.d("---voicePath--->" + filePath);
        if (TextUtils.isEmpty(filePath)) return;

        if (!FileUtil.isURL(filePath)) {
            playVoice(filePath);
            return;
        }

        ModuleMgr.getHttpMgr().downloadVoice(filePath, new DownloadListener() {
            @Override
            public void onStart(String url, String filePath) {
                if (downloadListener != null) {
                    downloadListener.onStart(url, filePath);
                }
            }

            @Override
            public void onProcess(String url, int process, long size) {
                if (downloadListener != null) {
                    downloadListener.onProcess(url, process, size);
                }
            }

            @Override
            public void onSuccess(String url, String filePath) {
                if (downloadListener != null) {
                    downloadListener.onSuccess(url, filePath);
                }
                ChatMediaPlayer.getInstance().playVoice(filePath);
            }

            @Override
            public void onFail(String url, Throwable throwable) {
                if (downloadListener != null) {
                    downloadListener.onFail(url, throwable);
                }
            }
        });
    }

    public synchronized void playVoice(final String filePath, OnPlayListener onPlayListener) {
        playVoice(filePath, onPlayListener, null);
    }


    /**
     * 只接受本地地址
     *
     * @param filePath 本地文件地址
     */
    private void playVoice(final String filePath) {
        PLogger.d("file: " + filePath);

        if (TextUtils.isEmpty(filePath)) return;
        if (isPlaying()) return;

        try {
            final AudioManager audioManager = (AudioManager) App.context.getSystemService(Context.AUDIO_SERVICE);
            mediaPlayer = new MediaPlayer();

            if (isSpeakerDisplay()) {//扬声器播放
                audioManager.setMode(AudioManager.MODE_NORMAL);
                audioManager.setSpeakerphoneOn(true);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            } else {//听筒播放
                audioManager.setSpeakerphoneOn(false);// 关闭扬声器
                // 把声音设定成Earpiece（听筒）出来，设定为正在通话中
                audioManager.setMode(AudioManager.MODE_IN_CALL);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }

            //生成音频焦点监听
            final AudioManager.OnAudioFocusChangeListener focusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {}
            };

            FileInputStream fis = new FileInputStream(new File(filePath));
            mediaPlayer.setDataSource(fis.getFD());
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (ChatMediaPlayer.this.onPlayListener != null) {
                        ChatMediaPlayer.this.onPlayListener.onStart(oriFilePath);
                    }
                    //申请音频焦点，优先于其他音频
                    audioManager.requestAudioFocus(focusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
                    mediaPlayer.start();
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    //释放音频焦点
                    audioManager.abandonAudioFocus(focusChangeListener);
                    stopPlayVoice();
                }
            });
            mediaPlayer.prepareAsync();

            readySensor();
            playing = true;
        } catch (Exception e) {
            PLogger.printThrowable(e);
            try {
                mediaPlayer.release();
            } catch (Exception e1) {
            }
            mediaPlayer = null;
        }
    }

    /**
     * 停止播放音频。
     */
    public void stopPlayVoice() {
        playing = false;
        stopSensor();

        if (onPlayListener != null) {
            MsgMgr.getInstance().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(onPlayListener != null) {
                        onPlayListener.onStop(oriFilePath);
                        onPlayListener = null;
                    }
                }
            });
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        oriFilePath = null;
    }

    /**
     * 是否正在播放音频。
     *
     * @return 播放状态。
     */
    public boolean isPlaying() {
        return playing;
    }

    public OnPlayListener getOnPlayListener() {
        return onPlayListener;
    }

    /**
     * 监听音频播放。
     */
    public interface OnPlayListener {
        /**
         * 开始播放。
         *
         * @param filePath 文件路径。
         */
        void onStart(String filePath);

        /**
         * 停止播放。
         *
         * @param filePath 文件路径。
         */
        void onStop(String filePath);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MEDIA_RECORDER_EVENT_Start:
                handler.sendEmptyMessageDelayed(MEDIA_RECORDER_EVENT_Start, frequency);
                break;

            case MEDIA_RECORDER_EVENT_Stop:
                handler.removeMessages(MEDIA_RECORDER_EVENT_Start);
                break;
        }

        return false;
    }

    private boolean speakerDisplay = true;//是否为扬声器播放

    /**
     * @return 是否为扬声器播放
     */
    public boolean isSpeakerDisplay() {
        return speakerDisplay;
    }

    /**
     * 设置是否为扬声器进行语音播放
     *
     * @param speakerDisplay 是否为外置播放器进行语音播放
     */
    public void setSpeakerDisplay(boolean speakerDisplay) {
        this.speakerDisplay = speakerDisplay;
    }

    private SensorManager _sensorManager = null; // 传感器管理器
    private Sensor mProximiny = null; // 传感器实例
    private float f_proximiny; // 当前传感器距离

    private void readySensor() {
        if (_sensorManager == null) {
            _sensorManager = (SensorManager) App.getActivity().getSystemService(Context.SENSOR_SERVICE);
            mProximiny = _sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }
        _sensorManager.registerListener(this, mProximiny, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void stopSensor() {
        if (_sensorManager != null) {
            _sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        try {
            f_proximiny = event.values[0];
            PLogger.d("" + f_proximiny + " | " + mProximiny.getMaximumRange());

            AudioManager audioManager = (AudioManager) App.context.getSystemService(Context.AUDIO_SERVICE);

            if (f_proximiny == mProximiny.getMaximumRange()) {
                audioManager.setMode(AudioManager.MODE_NORMAL);
                audioManager.setSpeakerphoneOn(true);
            } else {
                audioManager.setSpeakerphoneOn(false);
                audioManager.setMode(AudioManager.MODE_IN_CALL);
            }
        } catch (Exception e) {
            PLogger.printThrowable(e);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * 是否正在播放的语音
     * @param filePath
     * @return
     */
    public boolean isPlayingVoice(String filePath){
        if (TextUtils.isEmpty(filePath))
            return false;

        if (!isPlaying())
            return false;

        if (filePath.equals(oriFilePath))
            return true;

        if (FileUtil.isURL(oriFilePath))
            oriFilePath = PSP.getInstance().getString(oriFilePath.hashCode() + "", null);

        if (FileUtil.isURL(filePath))
            filePath = PSP.getInstance().getString(filePath.hashCode() + "", null);

        if (TextUtils.isEmpty(oriFilePath) || TextUtils.isEmpty(filePath))
            return false;

        return filePath.equals(oriFilePath);
    }

    /* -------------------------------视频播放---------------------------------- */

    /**
     * 播放一个音频。
     *
     * @param mute             是否为静音播放
     * @param textureVideoView 播放控件
     * @param filePath         视频文件
     */
    public synchronized void playVideo(final boolean mute, final TextureVideoView textureVideoView, final String filePath) {
        PLogger.d("filePath:" + filePath);
        if (TextUtils.isEmpty(filePath)) return;

        // 本地文件
        if (!FileUtil.isURL(filePath)) {
            playLocalVideo(mute, textureVideoView, filePath);
            return;
        }
        // 网络地址，下载完成之后进行播放
        ModuleMgr.getHttpMgr().downloadVideo(filePath, new DownloadListener() {
            @Override
            public void onStart(String url, String filePath) {
            }

            @Override
            public void onProcess(String url, int process, long size) {
            }

            @Override
            public void onSuccess(String url, String filePath) {
                playLocalVideo(mute, textureVideoView, filePath);
            }

            @Override
            public void onFail(String url, Throwable throwable) {
            }
        });
    }

    /**
     * 播放视频
     */
    public void playLocalVideo(boolean mute, final TextureVideoView textureVideoView, String filePath) {
        PLogger.d("filePath:" + filePath);
        if (TextUtils.isEmpty(filePath)) return;

        textureVideoView.setVideoPath(filePath);
        textureVideoView.setMute(mute);
        textureVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mp) {
                textureVideoView.start();
            }
        });
        textureVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                PToast.showShort("视频播放异常");
                return true;
            }
        });
    }
}
