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

public class GiftMessage extends BaseMessage {

    private int giftID;
    private int giftCount;
    private long giftLogID;//礼物流水id，用于进行礼物接收
    private int giftReceived;//标识礼物是否已经由服务器自动接收
    private int giftfrom;    //[opt] 礼物出处 1 =从礼物背包获取 2=充钻石购买,不传参数时默认为2
    private int giftFtype;//礼物来源类型 1聊天列表 2旧版索要 3 新版索要 4私密视频 5音视频插件 6 解锁礼物（2.3）

    public GiftMessage() {
        super();
    }

    public GiftMessage(String channelID, String whisperID, int giftID, int giftCount, int fType, int giftfrom) {
        super(channelID, whisperID);
        this.setGiftID(giftID);
        this.setGiftCount(giftCount);
        this.setGiftFtype(fType);
        this.setGiftfrom(giftfrom);
        this.setType(BaseMessageType.gift.getMsgType());
    }

    @Override
    public BaseMessage parseJson(String jsonStr) {
        super.parseJson(jsonStr);
        parseCommonJson(getJsonObject(jsonStr));
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
            tmpGift.put("count", ((GiftMessage) message).getGiftCount());
            tmpGift.put("gift_id", ((GiftMessage) message).getGiftID());
            tmpGift.put("gift_log_id", ((GiftMessage) message).getGiftLogID());
            tmpGift.put("recved", ((GiftMessage) message).getGiftReceived());
            tmpGift.put("ftype", ((GiftMessage) message).getGiftFtype());
            tmpGift.put("giftfrom", ((GiftMessage) message).getGiftfrom());
            json.put("gift", tmpGift);

            return json.toString();
        } catch (JSONException e) {
            PLogger.printThrowable(e);
        }
        return null;
    }

    public GiftMessage(Bundle bundle, boolean fletter) {
        super(bundle, fletter);
        convertJSON(getJsonStr());
    }

    /**
     * 转换类 fmessage
     */
    public GiftMessage(Bundle bundle) {
        super(bundle);
        convertJSON(getJsonStr());
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

    public int getGiftfrom() {
        return giftfrom;
    }

    public void setGiftfrom(int giftfrom) {
        this.giftfrom = giftfrom;
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
            this.setGiftfrom(giftJSON.optInt("giftfrom"));
        }
    }

    /**
     * @return 该条礼物是否由服务器自动接收[视频通话的礼物会由服务器自动接收]
     */
    public boolean isGiftAutoReceived() {
        return giftReceived == 1;
    }
}