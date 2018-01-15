package com.juxin.predestinate.module.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import com.juxin.library.log.PLogger;
import com.juxin.predestinate.module.logic.application.App;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.content.Context.AUDIO_SERVICE;
import static com.juxin.predestinate.module.logic.application.App.context;

/**
 * 创建日期：2017/8/28
 * 描述:追女神音乐播放
 * 作者:lc
 */
public class GamePlayer implements SoundPool.OnLoadCompleteListener {

    private Context mContext;
    private Hashtable<String, Integer> soundsLoopMap = new Hashtable<>();
    //要进行播放的声音ID-map：key-声音文件的路径，value-soundPool load之后的声音文件id
    private Hashtable<String, Integer> soundsMap = new Hashtable<>();
    //已经load的声音文件
    private CopyOnWriteArrayList<Integer> loadedSoundsMap = new CopyOnWriteArrayList<>();

    private boolean playFlag = true;   //是否停止
    private float volume;               //音量
    private SoundPool soundPool;        //维护的SoundPool对象，用于播放较短的音效
    private ExecutorService executors = Executors.newSingleThreadExecutor();

    public static GamePlayer instance;

    private GamePlayer() {
        mContext = App.context;
        if (mContext instanceof Activity)
            ((Activity) mContext).setVolumeControlStream(AudioManager.STREAM_MUSIC);

        AudioManager audioManager = (AudioManager) mContext.getSystemService(AUDIO_SERVICE);
        float actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = actVolume / maxVolume;

        buildSoundPool();
    }

    public static GamePlayer getInstance() {
        if (instance == null) {
            synchronized (GamePlayer.class) {
                if (instance == null) {
                    instance = new GamePlayer();
                }
            }
        }
        return instance;
    }

    public Integer loadSound(int resId) {
        PLogger.d("---GamePlayer--->loadSound：" + resId);
        Integer id = soundsMap.get(String.valueOf(resId));
        if (id == null) {
            if (soundPool != null && !loadedSoundsMap.contains(id)) {
                PLogger.d("---GamePlayer--->load：" + resId);
                id = soundPool.load(mContext, resId, 1);
                soundsMap.put(String.valueOf(resId), id);
            }
        }
        return id;
    }

    /**
     * 异步加载
     */
    public void initLoadSound() {
        executors.execute(new Runnable() {
            @Override
            public void run() {
                for (Integer resId : allSound()) {
                    loadSound(resId);
                }
            }
        });
    }

    private ArrayList<Integer> allSound() {
        ArrayList<Integer> list = new ArrayList<>();
        Resources resources = mContext.getResources();
        String packageName = context.getPackageName();
        list.add(resources.getIdentifier("background", "raw", packageName));
        list.add(resources.getIdentifier("bgm", "raw", packageName));
        list.add(resources.getIdentifier("button_click", "raw", packageName));
        list.add(resources.getIdentifier("clickspeed", "raw", packageName));
        list.add(resources.getIdentifier("count_down", "raw", packageName));
        list.add(resources.getIdentifier("lose", "raw", packageName));
        list.add(resources.getIdentifier("morespeed", "raw", packageName));
        list.add(resources.getIdentifier("normalspeed", "raw", packageName));
        list.add(resources.getIdentifier("over", "raw", packageName));
        list.add(resources.getIdentifier("so_game_success", "raw", packageName));
        return list;
    }

    /**
     * 停止循环播放的音乐
     */
    public void stop() {
        if (soundPool != null) {
            for (String str : soundsLoopMap.keySet()) {
                soundPool.stop(soundsLoopMap.get(str));
            }
        }
    }

    public void release() {
        if (soundPool != null) {
            soundPool.release();
        }
    }

    public void setPlayFlag(boolean playFlag) {
        this.playFlag = playFlag;
    }

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

    public void playRawSound(int resId, boolean isLoop) {
        if(!playFlag) return;
        Integer id = loadSound(resId);
        PLogger.d("---GamePlayer--->playRawSound：" + id);
        if (id != null && soundPool != null && loadedSoundsMap.contains(id)) {
            int playId = soundPool.play(id,
                    volume, // 左声道音量
                    volume, // 右声道音量
                    1, // 优先级
                    isLoop ? -1 : 0, // 循环播放次数 0 为不循环， -1 为循环
                    1f // 回放速度，该值在0.5-2.0之间 1为正常速度
            );
            if (isLoop) {
                soundsLoopMap.put(String.valueOf(resId), playId);
            }
        }
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
        loadedSoundsMap.add(sampleId);
        PLogger.d("---GamePlayer--->onLoadComplete：" + sampleId);
    }
}
