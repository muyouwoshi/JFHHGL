package com.juxin.predestinate.module.local.chat;

import android.os.Bundle;

import com.juxin.predestinate.bean.db.FMark;
import com.juxin.predestinate.module.logic.application.ModuleMgr;

/**
 * 专门做一些标记用的
 * Created by Kind on 2017/9/14.
 */

public class ChatMark {

    public static final int CHATMARK_TYPE_XXX = 100;//聊天友情提示
    public static final int CHATMARK_TYPE_TASK = 101;//聊天好友任务提示

    private long userID; //用户
    private int type; //类型
    private int num; //数量，目前是1
    private String content; //内容
    private long time;//最后时间

    public ChatMark() {
    }

    public ChatMark(long userID, int type) {
        this.userID = userID;
        this.type = type;
        this.time = ModuleMgr.getAppMgr().getTime();
    }

    public ChatMark(long userID, int type, String content) {
        this.userID = userID;
        this.type = type;
        this.content = content;
        this.time = ModuleMgr.getAppMgr().getTime();
    }

    public ChatMark(Bundle bundle) {
        this.setUserID(bundle.getLong(FMark.COLUMN_USERID));
        this.setType(bundle.getInt(FMark.COLUMN_TYPE));
        this.setNum(bundle.getInt(FMark.COLUMN_NUM));
        this.setContent(bundle.getString(FMark.COLUMN_CONTENT));
        this.setTime(bundle.getLong(FMark.COLUMN_TIME));
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

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
