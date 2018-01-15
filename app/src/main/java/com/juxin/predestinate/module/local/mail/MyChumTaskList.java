package com.juxin.predestinate.module.local.mail;

import com.juxin.predestinate.bean.net.BaseData;
import org.json.JSONObject;
import java.util.List;

/**
 * 密友任务
 * Created by Kind on 2017/7/25.
 */

public class MyChumTaskList extends BaseData {

    private int level;//密友级别
    private int experience;//密友值
    private int levelMaxExp;//当前级别最大的密友值
    private long ctime;//好友关系建立时间
    private int todayTaskStatus;//是否完成今日任务
    private int videoVoiceSec;//累计视频&语音时长 单位秒
    private int giftCnt;//累计赠送礼物数量
    private int know;

    private String otherAvatar;  //对方头像地址（需自己手动存入，接口中没有）

    private List<MyChumTask> myChumTasks;

    @Override
    public void parseJson(String jsonStr) {
        JSONObject jsonObject = getJsonObjectRes(jsonStr);
        JSONObject intimacyJSON = jsonObject.optJSONObject("intimacy");
        if(intimacyJSON != null){
            this.setLevel(intimacyJSON.optInt("level"));
            this.setExperience(intimacyJSON.optInt("experience"));
            this.setLevelMaxExp(intimacyJSON.optInt("levelMaxExp"));
            this.setCtime(intimacyJSON.optLong("ctime"));
            this.setTodayTaskStatus(intimacyJSON.optInt("todayTaskStatus"));
            this.setVideoVoiceSec(intimacyJSON.optInt("videoVoiceSec"));
            this.setGiftCnt(intimacyJSON.optInt("giftCnt"));
        }
        myChumTasks = (List<MyChumTask>) getBaseDataList(getJsonObjectRes(jsonStr).optJSONArray("tasks"), MyChumTask.class);
    }

    public int getVideoVoiceSec() {
        return videoVoiceSec;
    }

    public void setVideoVoiceSec(int videoVoiceSec) {
        this.videoVoiceSec = videoVoiceSec;
    }

    public int getGiftCnt() {
        return giftCnt;
    }

    public void setGiftCnt(int giftCnt) {
        this.giftCnt = giftCnt;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getLevelMaxExp() {
        return levelMaxExp;
    }

    public void setLevelMaxExp(int levelMaxExp) {
        this.levelMaxExp = levelMaxExp;
    }

    public long getCtime() {
        return ctime;
    }

    public void setCtime(long ctime) {
        this.ctime = ctime;
    }

    public int getTodayTaskStatus() {
        return todayTaskStatus;
    }

    public void setTodayTaskStatus(int todayTaskStatus) {
        this.todayTaskStatus = todayTaskStatus;
    }

    public String getOtherAvatar() {
        return otherAvatar;
    }

    public void setOtherAvatar(String otherAvatar) {
        this.otherAvatar = otherAvatar;
    }

    public List<MyChumTask> getMyChumTasks() {
        return myChumTasks;
    }

    public int getKnow() {
        return know;
    }

    public void setKnow(int know) {
        this.know = know;
    }

    public static class MyChumTask extends BaseData {

        private long taskId;       //任务id
        private int taskType;      //0 免费 1 付费
        private int triggerType;   ////触发类型 1 视频&语音 2 送礼物 3 小游戏 4大转盘 5 聊天
        private String taskName;   //任务名称
        private int taskAward;     //任务奖励
        private int taskDone;      //是否已完成 0未完成，1已完成，2已推送
        private String taskIcon;   //任务icon

        @Override
        public void parseJson(String s) {
            JSONObject jsonObject = getJsonObject(s);
            this.setTaskId(jsonObject.optLong("taskId"));
            this.setTaskType(jsonObject.optInt("taskType"));
            this.setTriggerType(jsonObject.optInt("triggerType"));
            this.setTaskName(jsonObject.optString("taskName"));
            this.setTaskAward(jsonObject.optInt("taskAward"));
            this.setTaskDone(jsonObject.optInt("taskDone"));
            this.setTaskIcon(jsonObject.optString("create_time"));
        }

        public int getTaskAward() {
            return taskAward;
        }

        public void setTaskAward(int taskAward) {
            this.taskAward = taskAward;
        }

        public int getTaskDone() {
            return taskDone;
        }

        public void setTaskDone(int taskDone) {
            this.taskDone = taskDone;
        }

        public String getTaskIcon() {
            return taskIcon;
        }

        public void setTaskIcon(String taskIcon) {
            this.taskIcon = taskIcon;
        }

        public String getTaskName() {
            return taskName;
        }

        public void setTaskName(String taskName) {
            this.taskName = taskName;
        }

        public int getTaskType() {
            return taskType;
        }

        public void setTaskType(int taskType) {
            this.taskType = taskType;
        }

        public long getTaskId() {
            return taskId;
        }

        public void setTaskId(long taskId) {
            this.taskId = taskId;
        }

        public int getTriggerType() {
            return triggerType;
        }

        public void setTriggerType(int triggerType) {
            this.triggerType = triggerType;
        }
    }
}
