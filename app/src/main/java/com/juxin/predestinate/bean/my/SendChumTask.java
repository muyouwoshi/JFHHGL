package com.juxin.predestinate.bean.my;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONObject;

/**
 * 发送密友
 * Created by Kind on 2017/7/20.
 */

public class SendChumTask extends BaseData {

    private long expire;           //超时时间
    private long msg_id;           //消息ID
    private int task_action_tp;    //任务类型
    private String task_award_info;//任务奖励
    private String task_info;      //任务描述

    @Override
    public void parseJson(String jsonStr) {
        JSONObject jsonObject = getJsonObjectRes(jsonStr);
        this.setExpire(jsonObject.optLong("expire"));
        this.setMsg_id(jsonObject.optLong("msg_id"));
        this.setTask_action_tp(jsonObject.optInt("task_action_tp"));
        this.setTask_award_info(jsonObject.optString("task_award_info"));
        this.setTask_info(jsonObject.optString("task_info"));
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public long getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(long msg_id) {
        this.msg_id = msg_id;
    }

    public int getTask_action_tp() {
        return task_action_tp;
    }

    public void setTask_action_tp(int task_action_tp) {
        this.task_action_tp = task_action_tp;
    }

    public String getTask_award_info() {
        return task_award_info;
    }

    public void setTask_award_info(String task_award_info) {
        this.task_award_info = task_award_info;
    }

    public String getTask_info() {
        return task_info;
    }

    public void setTask_info(String task_info) {
        this.task_info = task_info;
    }

    @Override
    public String toString() {
        return "SendChumTask{" +
                "expire=" + expire +
                ", msg_id=" + msg_id +
                ", task_action_tp=" + task_action_tp +
                ", task_award_info='" + task_award_info + '\'' +
                ", task_info='" + task_info + '\'' +
                '}';
    }
}
