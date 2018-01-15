package com.juxin.predestinate.module.util;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;

import com.juxin.library.log.PLogger;
import com.juxin.predestinate.module.logic.application.App;

/**
 * 软件内通知：声音及震动工具
 */
public class MediaNotifyUtils {

    private static int maxStreams = 2;//声音池传入音频数量
    private static SoundPool soundPool = null;

    /**
     * @return 获得SoundPool实例
     */
    public static SoundPool getSoundPool() {
        if (soundPool == null) {
            // 判断系统sdk版本，如果版本超过21，调用第一种
            if (Build.VERSION.SDK_INT >= 21) {
                SoundPool.Builder builder = new SoundPool.Builder();
                builder.setMaxStreams(maxStreams);//传入音频数量

                // AudioAttributes是一个封装音频各种属性的方法
                AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
                attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);

                builder.setAudioAttributes(attrBuilder.build());
                soundPool = builder.build();
            } else {
                soundPool = new SoundPool(maxStreams, AudioManager.STREAM_MUSIC, 0);
            }

            // 有些手机加载资源偏慢，造成播放失败。监听加载事件，成功后再开始播放。
            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    if (status == 0) {
                        playSound(sampleId, false);
                    }
                }
            });
        }
        return soundPool;
    }

    /**
     * 播放一个简短的声音。
     *
     * @param resId
     */
    public static void playSound(Context context, int resId) {
        int soundID = getSoundPool().load(context, resId, 1);
    }

    /**
     * 播放一个简短的声音。
     *
     * @param afd
     */
    public static void playSound(AssetFileDescriptor afd) {
        int soundID = getSoundPool().load(afd, 1);
    }

    /**
     * 播放一个简短的声音。
     *
     * @param soundID 声音Id。{@link SoundPool#load(String, int)}
     * @param loop    是否循环。
     */
    public static void playSound(int soundID, boolean loop) {
        // 播放音频
        // 第二个参数为左声道音量
        // 第三个参数为右声道音量
        // 第四个参数为优先级
        // 第五个参数为循环次数，0不循环，-1循环
        // 第六个参数为速率，速率最低为0.5最高为2，1代表正常速度
        getSoundPool().play(soundID, 1.0f, 1.0f, 0, loop ? -1 : 0, 1.0f);
    }

    // ---------------------------------------------------------------------

    /**
     * 震动
     */
    public static void vibrator() {
        Vibrator vibrator = (Vibrator) App.context.getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {100, 400};
        vibrator.vibrate(pattern, -1);
    }

    /**
     * 播放系统默认消息提示音
     */

    private static MediaPlayer mMediaPlayer = null;

    public static void playSound() {
        if (mMediaPlayer == null) {
            /*
            华为机器在调用RingtoneManager#getActualDefaultRingtoneUri时同时在内部框架里会同时调用
            RingtoneManager#getActualDefaultRingtoneUri方法，导致出现权限不足异常
            （android.permission.WRITE_SETTINGS），
            此权限声明会引发其他安全性问题所以此处改为如果有该权限异常则不进行播放音效处理
             */
            Uri uri = null;
            try {
                uri = RingtoneManager.getActualDefaultRingtoneUri(App.context, RingtoneManager.TYPE_NOTIFICATION);
            } catch (Exception e) {
                PLogger.e(e.toString());
            }
            if (uri == null) return;
            mMediaPlayer = MediaPlayer.create(App.context, uri);
        }
        //MediaPlayer#create 可能返回null
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.setLooping(false);
                if (!mMediaPlayer.isPlaying())
                    mMediaPlayer.start();
            } catch (Exception e) {
                PLogger.e(e.toString());
                mMediaPlayer = null;
            }
        }
    }
}
