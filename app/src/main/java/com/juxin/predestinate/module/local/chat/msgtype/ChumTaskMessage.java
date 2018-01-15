package com.juxin.predestinate.module.local.chat.msgtype;

import android.os.Bundle;
import android.text.TextUtils;

import com.juxin.library.log.PLogger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 密友任务
 * Created by Kind on 2017/7/17.
 */

public class ChumTaskMessage extends BaseMessage {

    private long task_id;//密友任务id
    private int task_tp;//密友任务消息类型1任务邀请2任务完成
    private int task_action_tp;// 密友任务动作类型， VIDEOVOICE_TRIGGER_TYPE = 1 //视频或语音 GIFT_TRIGGER_TYPE  = 2 //送礼物 MINIGAME_TRIGGER_TYPE = 3 //小游戏 ROLL_TRIGGER_TYPE = 4 //大转盘 CHAT_TRIGGER_TYPE= 5 //聊天
    private String task_info;// [opt]任务描述
    private String task_award_info;// [opt]任务奖励描述
    private long expire;//邀请超时时间

    public ChumTaskMessage(String whisperID, long taskID) {
        super(null, whisperID);
        this.setTask_id(taskID);
        this.setTask_tp(1);
        this.setType(BaseMessageType.chumTask.getMsgType());
    }

    @Override
    public BaseMessage parseJson(String jsonStr) {
        super.parseJson(jsonStr);
        parseNobilityJson(getJsonObject(jsonStr));
        return this;
    }

    @Override
    public String getJson(BaseMessage message) {
        JSONObject json = new JSONObject();
        try {
            json.put("tid", new JSONArray().put(message.getWhisperID()));
            json.put("mtp", message.getType());
            json.put("mt", message.getTime());
            json.put("d", message.getMsgID());
            json.put("msg_id", message.getMsgID());

            if (!TextUtils.isEmpty(message.getMsgDesc())) {
                json.put("mct", message.getMsgDesc());
            }

            json.put("task_tp", ((ChumTaskMessage) message).getTask_tp());
            json.put("task_id", ((ChumTaskMessage) message).getTask_id());
            json.put("expire", ((ChumTaskMessage) message).getExpire());
            json.put("task_action_tp", ((ChumTaskMessage) message).getTask_action_tp());
            json.put("task_info", ((ChumTaskMessage) message).getTask_info());
            json.put("task_award_info", ((ChumTaskMessage) message).getTask_award_info());

            return json.toString();
        } catch (JSONException e) {
            PLogger.printThrowable(e);
        }
        return null;
    }

    public long getTask_id() {
        return task_id;
    }

    public void setTask_id(long task_id) {
        this.setSpecialMsgID(task_id);
        this.task_id = task_id;
    }

    public int getTask_tp() {
        return task_tp;
    }

    /**
     * 是否任务完成  ==2任务完成
     * @return
     */
    public boolean isTaskTp() {
        return task_tp == 2;
    }

    public void setTask_tp(int task_tp) {
        this.task_tp = task_tp;
    }

    public int getTask_action_tp() {
        return task_action_tp;
    }

    public void setTask_action_tp(int task_action_tp) {
        this.task_action_tp = task_action_tp;
    }

    public String getTask_info() {
        return task_info;
    }

    public void setTask_info(String task_info) {
        this.task_info = task_info;
    }

    public String getTask_award_info() {
        return task_award_info;
    }

    public void setTask_award_info(String task_award_info) {
        this.task_award_info = task_award_info;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public ChumTaskMessage(){
        super();
    }

    public ChumTaskMessage(Bundle bundle) {
        super(bundle);
        convertJSON(getJsonStr());
    }

    //私聊列表
    public ChumTaskMessage(Bundle bundle, boolean fletter) {
        super(bundle, fletter);
        convertJSON(getJsonStr());
    }

    @Override
    public void convertJSON(String jsonStr) {
        super.convertJSON(jsonStr);
        JSONObject object = getJsonObject(jsonStr);
        this.setMsgDesc(object.optString("mct")); //消息内容
        parseNobilityJson(object);
    }

    private void parseNobilityJson(JSONObject object) {
        this.setTask_id(object.optLong("task_id"));
        this.setTask_tp(object.optInt("task_tp"));
        this.setTask_action_tp(object.optInt("task_action_tp"));
        this.setTask_info(object.optString("task_info"));
        this.setTask_award_info(object.optString("task_award_info"));
        this.setExpire(object.optLong("expire"));
        this.setSpecialMsgID(getTask_id());
    }
}
