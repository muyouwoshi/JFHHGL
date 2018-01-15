package com.juxin.predestinate.module.local.mail;

import com.juxin.predestinate.bean.net.BaseData;
import org.json.JSONObject;

import java.util.List;

/**
 * 我的密友
 * Created by Kind on 2017/7/14.
 */

public class MyChum extends BaseData{

    private int age;
    private String avatar;
    private int avatarstatus;
    private long ctime;//好友通过实践
    private int experience;//密友值
    private int group;//group>=2 就是vip
    private int height;
    private int level;//密友级别
    private int levelMaxExp;//当前级别的最大密友值
    private String nickname;
    private String remark;
    private int todayTaskStatus;//是否完成今日任务 1 完成 0 未完成
    private long uid;
    private int isOnline;//是否在线 1 在线 0 离线
    private int nobility;//爵位等级
    private int gender;//性别 男 1 女 2
    private int top;//排行榜排名(只有前20才有数据), 没有上榜
    private int topType;//排行榜类型 0 没上榜 1土豪榜 2魅力榜
    private boolean taskShow = false;//任务是否显示中  true是显示


    private List<MyChumTaskList.MyChumTask> myChumTasks = null;

    public MyChumTaskList.MyChumTask queryMyChumTask(long taskID) {
        if (myChumTasks == null || myChumTasks.size() < 0) {
            return new MyChumTaskList.MyChumTask();
        }
        for (MyChumTaskList.MyChumTask temp : myChumTasks) {
            if (taskID == temp.getTaskId()) return temp;
        }
        return new MyChumTaskList.MyChumTask();
    }

    public boolean isUndoneTask() {
        if (myChumTasks == null || myChumTasks.size() < 0) {
            return false;
        }
        for (MyChumTaskList.MyChumTask temp : myChumTasks) {
            if(temp.getTaskDone() != 1){
                return false;
            }
        }
        return true;
    }


    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getAvatarstatus() {
        return avatarstatus;
    }

    public void setAvatarstatus(int avatarstatus) {
        this.avatarstatus = avatarstatus;
    }

    public long getCtime() {
        return ctime;
    }

    public void setCtime(long ctime) {
        this.ctime = ctime;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevelMaxExp() {
        return levelMaxExp;
    }

    public void setLevelMaxExp(int levelMaxExp) {
        this.levelMaxExp = levelMaxExp;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    //是否完成今日任务 1 完成 0 未完成
    public boolean isTodayTaskStatus() {
        return todayTaskStatus == 1;
    }

    public int getTodayTaskStatus() {
        return todayTaskStatus;
    }

    public void setTodayTaskStatus(int todayTaskStatus) {
        this.todayTaskStatus = todayTaskStatus;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public int getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(int isOnline) {
        this.isOnline = isOnline;
    }

    public int getNobility() {
        return nobility;
    }

    public void setNobility(int nobility) {
        this.nobility = nobility;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public boolean isTaskShow() {
        return taskShow;
    }

    public int getTopType() {
        return topType;
    }

    public void setTopType(int topType) {
        this.topType = topType;
    }

    public void setTaskShow(boolean taskShow) {
        this.taskShow = taskShow;
    }

    public List<MyChumTaskList.MyChumTask> getMyChumTasks() {
        return myChumTasks;
    }

    public void setMyChumTasks(List<MyChumTaskList.MyChumTask> myChumTasks) {
        this.myChumTasks = myChumTasks;
    }

    @Override
    public void parseJson(String jsonStr) {
        JSONObject jsonObject = getJsonObject(jsonStr);
        this.setAge(jsonObject.optInt("age"));
        this.setAvatar(jsonObject.optString("avatar"));
        this.setAvatarstatus(jsonObject.optInt("avatarstatus"));
        this.setCtime(jsonObject.optLong("ctime"));
        this.setExperience(jsonObject.optInt("experience"));
        this.setGroup(jsonObject.optInt("group"));
        this.setHeight(jsonObject.optInt("height"));
        this.setLevel(jsonObject.optInt("level"));
        this.setLevelMaxExp(jsonObject.optInt("levelMaxExp"));
        this.setNickname(jsonObject.optString("nickname"));
        this.setRemark(jsonObject.optString("remark"));
        this.setTodayTaskStatus(jsonObject.optInt("todayTaskStatus"));
        this.setUid(jsonObject.optLong("uid"));
        this.setIsOnline(jsonObject.optInt("isOnline"));
        this.setNobility(jsonObject.optInt("nobility"));
        this.setGender(jsonObject.optInt("gender"));
        this.setTop(jsonObject.optInt("top"));
        this.setTopType(jsonObject.optInt("topType"));
    }
}
