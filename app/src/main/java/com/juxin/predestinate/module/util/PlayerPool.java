package com.juxin.predestinate.module.util;

import android.app.Activity;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.text.TextUtils;

import com.juxin.library.log.PLogger;
import com.juxin.library.request.DownloadListener;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.application.ModuleMgr;

import java.util.Hashtable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.content.Context.AUDIO_SERVICE;

/**
 * 声音播放工具
 * Created by siow on 2016/11/7.
 */
public class PlayerPool implements SoundPool.OnLoadCompleteListener {

    private Context mContext;

    //要进行播放的声音ID-map：key-声音文件的路径，value-soundPool load之后的声音文件id
    private Hashtable<String, Integer> soundsMap = new Hashtable<>();
    //已经load的声音文件
    private CopyOnWriteArrayList<Integer> loadedSoundsMap = new CopyOnWriteArrayList<>();

    private SoundPool soundPool;        //维护的SoundPool对象，用于播放较短的音效
    private float volume;               //音量
    private MediaPlayer mediaPlayer;    //维护的MediaPlayer对象，用于播放大段背景音乐

    private ExecutorService executors = Executors.newSingleThreadExecutor();

    private PlayerPool(Context context) {
        mContext = context.getApplicationContext();
        if (context instanceof Activity)
            ((Activity) context).setVolumeControlStream(AudioManager.STREAM_MUSIC);

        AudioManager audioManager = (AudioManager) mContext.getSystemService(AUDIO_SERVICE);
        float actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = actVolume / maxVolume;

        buildSoundPool();
    }

    private static class SingletonHolder {
        public static PlayerPool instance = new PlayerPool(App.context);
    }

    public static PlayerPool getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * 加载SD卡音效文件
     *
     * @param filename 文件地址
     * @return soundPool读取之后生成的音效id，可以用来播放或者停止音效播放
     */
    public Integer loadSound(String filename) {
        Integer id = soundsMap.get(filename);
        if (id == null) {
            if (soundPool != null) {
                id = soundPool.load(filename, 1);
                soundsMap.put(filename, id);
            }
        }
        PLogger.d("---PlayerPool--->filename：" + filename + "，soundId：" + id + "，soundPool：" + soundPool);
        return id;
    }

    /**
     * 异步加载SD卡音效文件
     *
     * @param filename 文件地址
     */
    public void asyncLoadSound(final String filename) {
        if (TextUtils.isEmpty(filename))
            return;

        executors.execute(new Runnable() {
            @Override
            public void run() {
                loadSound(filename);
            }
        });
    }

    /**
     * 释放mediaPlayer资源
     */
    public void releaseMediaPlayer() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (Exception e) {
            try {
                mediaPlayer.release();
            } catch (Exception err) {
                err.printStackTrace();
            }
            mediaPlayer = null;
        }
        PLogger.d("---PlayerPool--->releaseMediaPlayer");
    }

    /**
     * 播放声音文件[较短音效使用SoundPool进行播放。如果文件未下载或未缓存，就下载并播放，如果本地存在，就直接播放]
     *
     * @param url 声音文件网络地址
     */
    public void playSound(String url) {
        //TODO 重新设置下载路径
        ModuleMgr.getHttpMgr().download(url, "", new DownloadListener() {
            @Override
            public void onStart(String url, String filePath) {
            }

            @Override
            public void onProcess(String url, int process, long size) {
            }

            @Override
            public void onSuccess(String url, String filePath) {
                playLocalSound(filePath);
            }

            @Override
            public void onFail(String url, Throwable throwable) {
            }
        });
    }

    /**
     * 播放背景音乐[如果文件未下载或未缓存，就下载并播放，如果本地存在，就直接播放]<br>
     * 注意：调用该方法播放声音的页面，onDestroy的时候要调用releaseMediaPlayer方法
     *
     * @param url 音效网络地址
     */
    public void playBg(String url) {
        //TODO 重新设置下载路径
        ModuleMgr.getHttpMgr().download(url, "", new DownloadListener() {
            @Override
            public void onStart(String url, String filePath) {
            }

            @Override
            public void onProcess(String url, int process, long size) {
            }

            @Override
            public void onSuccess(String url, String filePath) {
                playBgLocal(filePath);
            }

            @Override
            public void onFail(String url, Throwable throwable) {
            }
        });
    }

    // ==========================私有方法============================

    /**
     * 初始化声音池
     */
    private void buildSoundPool() {
        if (soundPool != null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(24)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(24, AudioManager.STREAM_MUSIC, 0);
        }
        soundPool.setOnLoadCompleteListener(this);
    }

    private void playLocalSound(String filename) {
        PLogger.d("---PlayerPool--->filename：" + filename);

        Integer id = loadSound(filename);
        if (id != null && soundPool != null && loadedSoundsMap.contains(id)) {
            PLogger.d("---PlayerPool--->playing，filename：" + filename + "，soundId：" + id);
            soundPool.play(id,
                    volume, // 左声道音量
                    volume, // 右声道音量
                    1, // 优先级
                    0, // 循环播放次数 0 为不循环， -1 为循环
                    1f // 回放速度，该值在0.5-2.0之间 1为正常速度
            );
        } else {
            PLogger.d("---PlayerPool--->not play，filename：" + filename + "，soundId：" + id
                    + "，soundPool：" + soundPool + "，loadedSoundsMap.contains(id)：" + loadedSoundsMap.contains(id));
        }
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
        loadedSoundsMap.add(sampleId);
        PLogger.d("---PlayerPool--->soundId：" + sampleId);
    }

    /**
     * 播放本地背景音乐文件[大段背景音乐使用MediaPlayer进行播放]
     *
     * @param filename 文件本地路径
     */
    private void playBgLocal(String filename) {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
            } else {
                mediaPlayer.reset();
            }
            mediaPlayer.setDataSource(filename);
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setVolume(volume, volume);
            mediaPlayer.setLooping(true);
        } catch (Exception e) {
            try {
                if (mediaPlayer != null) mediaPlayer.release();
            } catch (Exception err) {
                err.printStackTrace();
            }
            mediaPlayer = null;
        }
    }
}
