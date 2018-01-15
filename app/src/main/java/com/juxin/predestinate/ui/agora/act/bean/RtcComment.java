package com.juxin.predestinate.ui.agora.act.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 视频评价信息
 * Created by Su on 2017/7/19.
 */
public class RtcComment extends BaseData implements Parcelable{
    private long duration;  // 聊天时长(s)
    private String avatar;  // 对方头像
    private String nickName;// 对方昵称
    private long uid;       // 对方uid
    private int vType;      // 视频类型 1 视频 2 语音

    // 男性花费信息
    private int cost;       // 花费钻石数
    private int videoCost;  // 视频花费的钻石
    private int giftCost;   // 礼物花费的钻石

    // 女性结算信息
    private int totalbonus; // 总收入 目前只有视频语音收入
    private int videoBonus; // 视频/语音聊天收入
    private int giftBonus;  // 礼物收入，单位分
    private int gameBonus;  // 礼物收入，单位分
    private int exp;        // 增加的密友亲密值

    private List<String> reasons = new ArrayList<>(); // 举报列表（在线配置接口未拿到时使用）

    @Override
    public void parseJson(String jsonStr) {
        String jsonData = getJsonObject(jsonStr).optString("res");
        JSONObject jsonObject = getJsonObject(jsonData);

        this.duration = jsonObject.optLong("duration");
        this.vType = jsonObject.optInt("vtype");
        this.exp = jsonObject.optInt("exp");

        // 男性
        this.cost = jsonObject.optInt("cost");
        this.videoCost = jsonObject.optInt("videoCost");
        this.giftCost = jsonObject.optInt("giftCost");
        parseReportReason(getJsonArray(jsonObject.optString("reasons")));

        // 女性
        this.totalbonus = jsonObject.optInt("totalBonus");
        this.videoBonus = jsonObject.optInt("videoBonus");
        this.giftBonus = jsonObject.optInt("giftBonus");


        JSONObject object = null;
        if (!jsonObject.isNull("payee"))  // 男性：付费方包含此字段
            object = getJsonObject(jsonObject.optString("payee"));

        if (!jsonObject.isNull("payer"))  // 女性：收费方含此字段: 对方信息
            object = getJsonObject(jsonObject.optString("payer"));

        if (object == null) return;
        this.avatar = object.optString("avatar");
        this.nickName = object.optString("nickname");
        this.uid = object.optLong("uid");
    }

    private void parseReportReason(JSONArray jsonArray) {
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    reasons.add(jsonArray.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int getVideoCost() {
        return videoCost;
    }

    public int getGiftCost() {
        return giftCost;
    }

    public int getGiftBonus() {
        return giftBonus;
    }

    public int getExp() {
        return exp;
    }

    public int getCost() {
        return cost;
    }

    public long getDuration() {
        return duration;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getNickName() {
        return nickName;
    }

    public long getUid() {
        return uid;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public int getvType() {
        return vType;
    }

    public int getTotalbonus() {
        return totalbonus;
    }

    public int getVideoBonus() {
        return videoBonus;
    }

    public int getGameBonus() {
        return gameBonus;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.duration);
        dest.writeString(this.avatar);
        dest.writeString(this.nickName);
        dest.writeLong(this.uid);
        dest.writeInt(this.vType);
        dest.writeInt(this.cost);
        dest.writeInt(this.videoCost);
        dest.writeInt(this.giftCost);
        dest.writeInt(this.totalbonus);
        dest.writeInt(this.videoBonus);
        dest.writeInt(this.giftBonus);
        dest.writeInt(this.gameBonus);
        dest.writeInt(this.exp);
        dest.writeStringList(this.reasons);
    }

    public RtcComment() {
    }

    protected RtcComment(Parcel in) {
        this.duration = in.readLong();
        this.avatar = in.readString();
        this.nickName = in.readString();
        this.uid = in.readLong();
        this.vType = in.readInt();
        this.cost = in.readInt();
        this.videoCost = in.readInt();
        this.giftCost = in.readInt();
        this.totalbonus = in.readInt();
        this.videoBonus = in.readInt();
        this.giftBonus = in.readInt();
        this.gameBonus = in.readInt();
        this.exp = in.readInt();
        this.reasons = in.createStringArrayList();
    }

    public static final Creator<RtcComment> CREATOR = new Creator<RtcComment>() {
        @Override
        public RtcComment createFromParcel(Parcel source) {
            return new RtcComment(source);
        }

        @Override
        public RtcComment[] newArray(int size) {
            return new RtcComment[size];
        }
    };
}
