package com.juxin.predestinate.module.util;

import com.juxin.library.observe.MsgType;
import com.juxin.predestinate.ui.user.my.TimeMgr;

import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 倒计时工具类
 * Created by zm on 17/6/20.
 */
public class CountDownTimerUtil {

    private static CountDownTimerUtil countDownTimerUtil = new CountDownTimerUtil();
    private ConcurrentHashMap<Long, Long> timeList = new ConcurrentHashMap<>();
    private Timer mTimer = new Timer();                    //用于计时操作
    private boolean isTiming = false;                      //用于判断计时是否开始
    private String ids = "";
    private String handledIds = "";


    private CountDownTimerUtil() {
    }

    public static CountDownTimerUtil getInstance() {
        return countDownTimerUtil;
    }

    public Long getTask(long id){
        return timeList.get(id);
    }

    public boolean isTimingTask(Long id) {
        return ids.contains(id + "LL");
    }

    public  boolean isHandled(long id){
        return handledIds.contains(id + "LL");
    }

    public void deleteTask(long id) {
        synchronized (this) {
            timeList.remove(id);
            ids = ids.replace(id + "LL", "");
            timerEnd();
        }
    }

    public void addHandledIds(long id) {
        handledIds = handledIds + id + "LL";
    }

    private void timerEnd(){
        if (timeList.size() <= 0) {
            isTiming = false;
            mTimer.cancel();
            mTimer = new Timer();
            ids = "";
            handledIds = "";
            TimeMgr.getInstance().clear();
        }
    }

    /**
     * 添加任务
     *
     * @param id        任务id
     * @param timeCount 倒计时时间
     */
    public void addTimerTask(final Long id, long timeCount) {
        synchronized (this) {
            timeList.put(id, timeCount);
            if (!isTimingTask(id)){
                ids = ids + id + "LL";
            }
            if (!isTiming && timeList.size() > 0) {
                isTiming = true;
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        synchronized (this) {
                            Iterator<Map.Entry<Long, Long>> it = timeList.entrySet().iterator();
                            while (it.hasNext()) {
                                Map.Entry<Long, Long> itEntry = it.next();
                                Long timeRemaining = itEntry.getValue() - 1;
                                itEntry.setValue(timeRemaining);
                                if (timeRemaining < 0 || isHandled(itEntry.getKey())) {
                                    timeRemaining = -1L;
                                    itEntry.setValue(timeRemaining);
                                    ids = ids.replace(itEntry.getKey() + "LL", "");
                                    it.remove();
                                    timerEnd();
                                }
                                TimeMgr.getInstance().sendMsg(MsgType.MT_Time_Change, itEntry.getKey());
                            }
                        }
                    }
                }, 0, 1000);
            }
        }
    }
}
