package com.juxin.predestinate.module.local.chat;

import com.juxin.predestinate.bean.net.BaseData;
import org.json.JSONObject;

/**
 * 消息返回
 * Created by Kind on 2017/5/11.
 */
public class MessageRet extends BaseData {


    public static final int MSG_CODE_OK = 0;//成功
    public static final int MSG_CODE_BALANCE_INSUFFICIENT = -1;//余额不足或者不是VIP
    public static final int MSG_CODE_PULL_BLACK = -2;//已拉黑
    public static final int MSG_CODE_BALANCE_Y = -4;//Y币不够了
    public static final int MSG_CODE_CERTIFICATION_LEVEL = -1001;// 用户认证等级不足（未视频 认证，手机认证，身份认证等）
    public static final int MSG_CODE_BANNED = -1002;// 用户相关功能被封禁（禁言，禁图，禁语音等）


    //{"d":3302,"msg_id":1166518,"s":0,"status":"ok","tm":1498109958}
    private long d;//本地消息ID
    private long msgId;//服务器消息ID
    private long tm;
    private int s;
    private int diamond_cost;//此消息消耗的钻石数
    //170824 ADD START 打招呼提示使用消息体返回的错误信息
    private String fail_msg;//发送失败时的错误提示,只在s!=0时有效
    //170824 ADD END 打招呼提示使用消息体返回的错误信息

    @Override
    public void parseJson(String jsonStr) {
        JSONObject jsonObject = getJsonObject(jsonStr);
        this.setD(jsonObject.optLong("d"));
        this.setMsgId(jsonObject.optLong("msg_id"));
        this.setTm(jsonObject.optLong("tm"));
        if (!jsonObject.isNull("s")) {//状态
            this.setS(jsonObject.optInt("s"));
        }
        //170824 ADD START 打招呼提示使用消息体返回的错误信息
        setFailMsg(jsonObject.optString("fail_msg"));
        //170824 ADD END 打招呼提示使用消息体返回的错误信息
        this.setDiamondCost(jsonObject.optInt("diamond_cost"));
    }

    public long getD() {
        return d;
    }

    public void setD(long d) {
        this.d = d;
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public long getTm() {
        return tm;
    }

    public void setTm(long tm) {
        this.tm = tm;
    }

    public int getS() {
        return s;
    }

    public boolean isS() {
        return s == MSG_CODE_OK;
    }

    public void setS(int s) {
        this.s = s;
    }

    //170824 ADD START 打招呼提示使用消息体返回的错误信息
    public String getFailMsg() {
        return fail_msg;
    }

    public void setFailMsg(String fail_msg) {
        this.fail_msg = fail_msg;
    }
    //170824 ADD END 打招呼提示使用消息体返回的错误信息


    public int getDiamond_cost() {
        return diamond_cost;
    }

    /**
     * 减钻石
     * @param diamond_cost
     */
    private void setDiamondCost(int diamond_cost) {
        this.diamond_cost = diamond_cost;
    }
}