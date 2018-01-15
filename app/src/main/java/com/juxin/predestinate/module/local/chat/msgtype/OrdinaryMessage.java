package com.juxin.predestinate.module.local.chat.msgtype;

import com.juxin.library.log.PLogger;
import com.juxin.predestinate.module.local.chat.utils.MsgIDUtils;
import com.juxin.predestinate.module.logic.application.ModuleMgr;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 普通发送消息
 * Created by Kind on 2017/5/15.
 */

public class OrdinaryMessage{

    private long userID;
    private int type;
    private long cMsgID;
    private String content;

    //关注
    private int gz;//关注状态1为关注2为取消关注
    private int kf;

    /**
     * 关注消息
     */
    public OrdinaryMessage(long userID, String content, int kf, int gz) {
        this.setUserID(userID);
        this.setType(BaseMessage.Follow_MsgType);
        this.setcMsgID(MsgIDUtils.getMsgIDUtils().getMsgID());
        this.setContent(content);
        this.setKf(kf);
        this.setGz(gz);
    }


    public String toFllowJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("tid", new JSONArray().put(String.valueOf(this.getUserID())));
            json.put("mtp", this.getType());
            json.put("mct", this.getContent());
            json.put("mt", ModuleMgr.getAppMgr().getTime());
            json.put("d", this.getcMsgID());

            int kf = this.getKf();
            int gz = this.getGz();

            if(kf != -1){
                json.put("kf", kf);
            }
            if(gz != -1){
                json.put("gz", gz);
            }

            return json.toString();
        } catch (JSONException e) {
            PLogger.printThrowable(e);
        }
        return null;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getcMsgID() {
        return cMsgID;
    }

    public void setcMsgID(long cMsgID) {
        this.cMsgID = cMsgID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getGz() {
        return gz;
    }

    public void setGz(int gz) {
        this.gz = gz;
    }

    public int getKf() {
        return kf;
    }

    public void setKf(int kf) {
        this.kf = kf;
    }
}
