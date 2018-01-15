package com.juxin.predestinate.module.util.my;

/**
 * 倒计时
 * Created by zm on 2017/4/28
 */
public class CountdownUtil extends Thread{

    private boolean isStop = false;
    private int duration = 60;//默认为60秒
    private TimeListener mTimeListener;

    public void setStop(boolean isStop) {
        this.isStop = isStop;
    }

    public CountdownUtil setDuration(int duration){
        this.duration = duration;
        return this;
    }

    public void run() {
        int count = duration;
        while (!isStop && count >= 0) {
            try {
                Thread.sleep(1000);
                if (mTimeListener != null)
                    mTimeListener.onTimeListener(count);
            } catch (Exception e) {
                break;
            }
            count--;
        }
    }
    public CountdownUtil setOnTimeListener(TimeListener mTimeListener){
        this.mTimeListener = mTimeListener;
        return this;
    }

    public interface TimeListener{
        void onTimeListener(int time);
    }
}