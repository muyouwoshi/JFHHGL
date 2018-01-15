package com.juxin.predestinate.bean.my;


import com.juxin.predestinate.bean.net.BaseData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * 跑马灯消息列表
 * Created by zm on 17/7/21.
 */
public class MarqueeMessageList extends BaseData {

    private List giftMessageList;

    public List getGiftMessageList() {
        return giftMessageList;
    }

    @Override
    public void parseJson(final String s) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                giftMessageList = getBaseDataList(getJsonObjectRes(s).optJSONArray("banners"), MarqueeMessageInfo.class);
            }
        }).start();
    }

    public static class MarqueeMessageInfo extends BaseData {

        private int type;          //1:大礼物滚动条2:爵位升级
        private int count;         //礼物数量
        private long from_uid;     //送礼物人的uid
        private String from_name;  //送礼人昵称
        private int gift_id;       //礼物id
        private String to_name;    //接收礼物人的昵称
        private String from_avatar;//头像

        private long uid;         //升级的uid
        private String nickname;  //升级的昵称
        private int level;        //升级的等级
        private String avatar;    //头像
        private int gender;       //性别 1=男 2= 女

        @Override
        public void parseJson(String s) {
            JSONObject jsonObject = getJsonObject(s);
            this.setType(jsonObject.optInt("type"));
            if (jsonObject.isNull("data")) {
                return;
            }
            jsonObject = jsonObject.optJSONObject("data");
            if (type == 1) {
                this.setCount(jsonObject.optInt("count"));
                this.setFrom_uid(jsonObject.optLong("from_uid"));
                this.setFrom_name(jsonObject.optString("from_name"));
                this.setGift_id(jsonObject.optInt("gift_id"));
                this.setTo_name(jsonObject.optString("to_name"));
                this.setFrom_avatar(jsonObject.optString("from_avatar"));
            } else if (type == 2) {
                this.setUid(jsonObject.optLong("uid"));
                this.setNickname(jsonObject.optString("nickname"));
                this.setLevel(jsonObject.optInt("level"));
                this.setAvatar(jsonObject.optString("avatar"));
                this.setGender(jsonObject.optInt("gender"));
            }
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
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

        public int getGift_id() {
            return gift_id;
        }

        public void setGift_id(int gift_id) {
            this.gift_id = gift_id;
        }

        public String getTo_name() {
            return to_name;
        }

        public void setTo_name(String to_name) {
            this.to_name = to_name;
        }

        public long getUid() {
            return uid;
        }

        public void setUid(long uid) {
            this.uid = uid;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public String getFrom_avatar() {
            return from_avatar;
        }

        public void setFrom_avatar(String from_avatar) {
            this.from_avatar = from_avatar;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }
    }
}
