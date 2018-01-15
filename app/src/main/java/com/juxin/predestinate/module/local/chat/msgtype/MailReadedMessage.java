package com.juxin.predestinate.module.local.chat.msgtype;

import com.juxin.library.log.PLogger;
import com.juxin.predestinate.module.local.chat.utils.MsgIDUtils;
import com.juxin.predestinate.module.logic.application.ModuleMgr;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 普通发送消息
 * Created by zm on 2017/5/26.
 */

public class MailReadedMessage {

    private long userID;
    private int type;
    private long cMsgID;
    private String content;

    /**
     * 关注消息
     */
    public MailReadedMessage(String channelID,long userID) {
        this.setUserID(userID);
        this.setType(BaseMessage.System_MsgType);
        this.setcMsgID(MsgIDUtils.getMsgIDUtils().getMsgID());
//        this.setContent(channelID);
    }


    public String toMailReadedJson() {
        String loginId = ModuleMgr.getCenterMgr().getMyInfo().getUid()+"";
        JSONObject jsonObject = new JSONObject();
        // 拼接消息
        try {
            jsonObject.put("fid", loginId);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(getUserID());
            jsonObject.put("mtp", this.getType());
            jsonObject.put("tid", jsonArray);
            jsonObject.put("mt", ModuleMgr.getAppMgr().getTime());
            jsonObject.put("mct", "");
            jsonObject.put("d", this.getcMsgID());
            jsonObject.put("xt", 3);
            jsonObject.put("mv", 4);
            return jsonObject.toString();
        } catch (JSONException e) {
            PLogger.printThrowable(e);
        } catch (Exception e) {
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

}
