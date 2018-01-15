package com.juxin.predestinate.module.local.chat.msgtype;

import android.os.Bundle;
import android.text.TextUtils;

import com.juxin.library.log.PLogger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 礼物消息
 * Created by Kind on 2017/4/19.
 */

public class BigGiftMessage extends BaseMessage {

    private int giftID;
    private int giftCount;
    private long giftLogID;//礼物流水id，用于进行礼物接收
    private int giftReceived;//标识礼物是否已经由服务器自动接收
    private int giftFtype;//礼物来源类型 1聊天列表 2旧版索要 3 新版索要 4私密视频 5音视频插件 6 解锁礼物（2.3）
    private int effect_type;//特效类型 1 特大礼物特效
    private int effect_id;//特效动画资源ID
    private String effect_data;//特效数据
    private long from_uid;
    private String from_name;
    private String from_avatar;
    private long to_uid;
    private String to_name;
    private String to_avatar;
//    {    //特大礼物特效结构
//        "count": 1,
//            "from_uid": 1231232131,
//            "from_name": "注意！临时小号",
//            "from_avatar":"http://image1.yuanfenba.net/uploads/oss/video/20170315/148954291723358.jpg"
//        "to_uid":1232131231,
//            "to_name": "注意！临时小号",
//            "to_avatar":"http://image1.yuanfenba.net/uploads/oss/video/20170315/148954291723358.jpg"
//        "gift_id": 10,
//    }
    //    "mct":"",						//
//            "fid":100221,					//发送者uid
//            "tid":100221,					//接收者uid
//            "mtp":1004,						//消息类型 和包头一致
//            "mt":442112121,					//消息时间 int64
//            "d":43432423423,				//消息id int64
//            "effect_type":1,				//特效类型 1 特大礼物特效
//            "effect_data":{...	},			//特效数据
//            "effect_id":"aef3344"			//特效动画资源ID

    public BigGiftMessage() {
        super();
    }

    public BigGiftMessage(String channelID, String whisperID, int giftID, int giftCount, int fType) {
        super(channelID, whisperID);
        this.setGiftID(giftID);
        this.setGiftCount(giftCount);
        this.setGiftFtype(fType);
        this.setType(BaseMessageType.gift.getMsgType());
    }

    @Override
    public BaseMessage parseJson(String jsonStr) {
        super.parseJson(jsonStr);
        JSONObject object = getJsonObject(jsonStr);
        this.setEffect_data(object.optString("effect_data"));

        parseCommonJson(object);
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

            if (!TextUtils.isEmpty(message.getMsgDesc())) {
                json.put("mct", message.getMsgDesc());
            }

            JSONObject tmpGift = new JSONObject();
            tmpGift.put("count", ((BigGiftMessage) message).getGiftCount());
            tmpGift.put("gift_id", ((BigGiftMessage) message).getGiftID());
            tmpGift.put("gift_log_id", ((BigGiftMessage) message).getGiftLogID());
            tmpGift.put("recved", ((BigGiftMessage) message).getGiftReceived());
            tmpGift.put("ftype", ((BigGiftMessage) message).getGiftFtype());
            json.put("gift", tmpGift);

            return json.toString();
        } catch (JSONException e) {
            PLogger.printThrowable(e);
        }
        return null;
    }

    public BigGiftMessage(Bundle bundle, boolean fletter) {
        super(bundle, fletter);
        convertJSON(getJsonStr());
    }

    /**
     * 转换类 fmessage
     */
    public BigGiftMessage(Bundle bundle) {
        super(bundle);
        convertJSON(getJsonStr());
    }

    public int getEffect_type() {
        return effect_type;
    }

    public void setEffect_type(int effect_type) {
        this.effect_type = effect_type;
    }

    public int getEffect_id() {
        return effect_id;
    }

    public void setEffect_id(int effect_id) {
        this.effect_id = effect_id;
    }

    public String getEffect_data() {
        return effect_data;
    }

    public void setEffect_data(String effect_data) {
        this.effect_data = effect_data;
    }

    public long getFrom_uid() {
        return from_uid;
    }

    public void setFrom_uid(long from_uid) {
        this.from_uid = from_uid;
    }

    public String getFrom_name() {
        return from_name;
    }

    public void setFrom_name(String from_name) {
        this.from_name = from_name;
    }

    public String getFrom_avatar() {
        return from_avatar;
    }

    public void setFrom_avatar(String from_avatar) {
        this.from_avatar = from_avatar;
    }

    public long getTo_uid() {
        return to_uid;
    }

    public void setTo_uid(long to_uid) {
        this.to_uid = to_uid;
    }

    public String getTo_name() {
        return to_name;
    }

    public void setTo_name(String to_name) {
        this.to_name = to_name;
    }

    public String getTo_avatar() {
        return to_avatar;
    }

    public void setTo_avatar(String to_avatar) {
        this.to_avatar = to_avatar;
    }

    public int getGiftCount() {
        return giftCount;
    }

    public void setGiftCount(int giftCount) {
        this.giftCount = giftCount;
    }

    public int getGiftID() {
        return giftID;
    }

    public void setGiftID(int giftID) {
        this.giftID = giftID;
    }

    public long getGiftLogID() {
        return giftLogID;
    }

    public void setGiftLogID(long giftLogID) {
        this.giftLogID = giftLogID;
    }

    public int getGiftReceived() {
        return giftReceived;
    }

    public void setGiftReceived(int giftReceived) {
        this.giftReceived = giftReceived;
    }

    public int getGiftFtype() {
        return giftFtype;
    }

    public void setGiftFtype(int giftFtype) {
        this.giftFtype = giftFtype;
    }

    @Override
    public void convertJSON(String jsonStr) {
        super.convertJSON(jsonStr);
        JSONObject object = getJsonObject(jsonStr);
        this.setMsgDesc(object.optString("mct")); //消息内容

        parseCommonJson(object);
    }

    /**
     * 礼物消息数据解析
     */
    private void parseCommonJson(JSONObject object) {
        //索要礼物
        if (object.has("gift_id")) this.setGiftID(object.optInt("gift_id"));
        //收到礼物
        if (object.has("gift")) {
            JSONObject giftJSON = object.optJSONObject("gift");
            this.setGiftCount(giftJSON.optInt("count"));
            this.setGiftID(giftJSON.optInt("gift_id"));
            this.setGiftLogID(giftJSON.optLong("gift_log_id"));
            this.setGiftReceived(giftJSON.optInt("recved"));
            this.setGiftFtype(giftJSON.optInt("ftype"));
        }
    }

    /**
     * @return 该条礼物是否由服务器自动接收[视频通话的礼物会由服务器自动接收]
     */
    public boolean isGiftAutoReceived() {
        return giftReceived == 1;
    }
}