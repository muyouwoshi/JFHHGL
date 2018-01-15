package com.juxin.predestinate.bean.my;

import android.text.TextUtils;
import com.juxin.predestinate.bean.live.LiveStatus;
import com.juxin.predestinate.bean.net.BaseData;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 创建日期：2017/6/25
 * 描述:
 * 作者:lc
 */
public class ChatInfo extends BaseData {
    private int diamond;                //我的钻石余额
    private int ycoin;                  //我的Y币余额
    private String ycoinBindUid;       //未解锁VIP时，Y币绑定的用户uid
    private int talk_unlocked;         //对方是否已聊天解锁 1为已解锁 否则为0
    private int know;

    private OtherInfo otherInfo;
    private ABInfo abInfo;

    @Override
    public void parseJson(String jsonStr) {
        JSONObject resJob = getJsonObjectRes(jsonStr);
        this.setDiamond(resJob.optInt("diamond"));
        this.setYcoin(resJob.optInt("ycoin"));
        this.setYcoinBindUid(resJob.optString("yb_uid"));
        this.setTalk_unlocked(resJob.optInt("talk_unlocked"));
        this.setKnow(resJob.optInt("know"));

        if (!resJob.isNull("otherInfo")) {
            otherInfo = new OtherInfo();
            otherInfo.parseJson(resJob.optJSONObject("otherInfo").toString());
        }

        if (!resJob.isNull("ab_info")) {
            abInfo = new ABInfo();
            this.abInfo.parseJson(resJob.optJSONObject("ab_info").toString());
        }
    }

    public int getDiamond() {
        return diamond;
    }

    public void setDiamond(int diamond) {
        this.diamond = diamond;
    }

    public int getYcoin() {
        return ycoin;
    }

    public void setYcoin(int ycoin) {
        this.ycoin = ycoin;
    }

    public String getYcoinBindUid() {
        return ycoinBindUid;
    }

    public void setYcoinBindUid(String ycoinBindUid) {
        this.ycoinBindUid = ycoinBindUid;
    }

    public int getKnow() {
        return know;
    }

    public void setKnow(int know) {
        this.know = know;
    }

    public OtherInfo getOtherInfo() {
        if (otherInfo == null) {
            return new OtherInfo();
        }
        return otherInfo;
    }

    public void setOtherInfo(OtherInfo otherInfo) {
        this.otherInfo = otherInfo;
    }

    public boolean isTalk_unlocked() {
        return talk_unlocked == 1;
    }

    public void setTalk_unlocked(int talk_unlocked) {
        this.talk_unlocked = talk_unlocked;
    }

    public static class OtherInfo extends BaseData {
        private int age;
        private String avatar;
        private int avatarstatus;
        private long channel_uid;
        private int city;
        private int gender;
        private int group;
        private int height;
        private boolean is_online;
        private long kf_id;
        private String nickname;
        private int photonum;
        private int province;
        private String remark;
        private int top;
        private String last_online;         //最近上线状态
        private long uid;
        private int net_tp;                 //用户上网方式（2017-06-20）Wifi 1 4G 2 3G/2G 3 其它 4
        private String phone_info;          //用户设备描述
        private boolean isLive;             //是否在直播
        private boolean isGiftFriend;       // 是否是礼物好友
        private int isIntimateFriend;       // 0 非好友 1 等待中 2 已成为好友
        private boolean beauty_switch;      //追女神开关，true:打开， false：关闭
        private LiveStatus liveStatus = new LiveStatus();

        private VideoConfig videoConfig = new VideoConfig();

        @Override
        public void parseJson(String jsonStr) {
            JSONObject jo = getJsonObject(jsonStr);
            this.setAge(jo.optInt("age"));
            this.setAvatar(jo.optString("avatar"));
            this.setAvatarstatus(jo.optInt("avatarstatus"));
            this.setChannel_uid(jo.optLong("channel_uid"));
            this.setCity(jo.optInt("city"));
            this.setGender(jo.optInt("gender"));
            this.setGroup(jo.optInt("group"));
            this.setHeight(jo.optInt("height"));
            this.setIs_online(jo.optBoolean("is_online"));
            this.setKf_id(jo.optLong("kf_id"));
            this.setNickname(jo.optString("nickname"));
            this.setPhotonum(jo.optInt("photonum"));
            this.setProvince(jo.optInt("province"));
            this.setRemark(jo.optString("remark"));
            this.setTop(jo.optInt("top"));
            this.setLast_online(jo.optString("last_online"));
            this.setUid(jo.optLong("uid"));
            this.setNet_tp(jo.optInt("net_tp"));
            this.setPhone_info(jo.optString("phone_info"));
            this.setGiftFriend(jo.optBoolean("isGiftFriend"));
            this.setIsIntimateFriend(jo.optInt("isIntimateFriend"));
            this.setBeauty_switch(jo.optBoolean("beauty_switch"));
            this.videoConfig.parseJson(jo.optString("videochatconfig"));

            if(!jo.isNull("live_status")){
                this.liveStatus.parseJson(jo.optString("live_status"));
            }
        }

        public String netTp2Str(int netType) {
            String tempType;
            switch (netType) {
                case 1:
                    tempType = "Wifi";
                    break;
                case 2:
                    tempType = "4G";
                    break;
                case 3:
                    tempType = "3G/2G";
                    break;
                case 4:
                    tempType = "其它";
                    break;
                default:
                    tempType = "其它";
                    break;
            }
            return tempType;
        }

        public boolean isGiftFriend() {
            return isGiftFriend;
        }

        public void setGiftFriend(boolean giftFriend) {
            isGiftFriend = giftFriend;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public int getAvatarstatus() {
            return avatarstatus;
        }

        public void setAvatarstatus(int avatarstatus) {
            this.avatarstatus = avatarstatus;
        }

        public long getChannel_uid() {
            return channel_uid;
        }

        public void setChannel_uid(long channel_uid) {
            this.channel_uid = channel_uid;
        }

        public int getCity() {
            return city;
        }

        public void setCity(int city) {
            this.city = city;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public int getGroup() {
            return group;
        }

        public void setGroup(int group) {
            this.group = group;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public boolean is_online() {
            return is_online;
        }

        public void setIs_online(boolean is_online) {
            this.is_online = is_online;
        }

        public long getKf_id() {
            return kf_id;
        }

        public void setKf_id(long kf_id) {
            this.kf_id = kf_id;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public int getPhotonum() {
            return photonum;
        }

        public void setPhotonum(int photonum) {
            this.photonum = photonum;
        }

        public int getProvince() {
            return province;
        }

        public void setProvince(int province) {
            this.province = province;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        /**
         * 获取展示名称
         */
        public String getShowName() {
            return TextUtils.isEmpty(remark) ? getNickname() : remark;
        }

        public int getTop() {
            return top;
        }

        public void setTop(int top) {
            this.top = top;
        }

        public String getLast_online() {
            return last_online;
        }

        public void setLast_online(String last_online) {
            this.last_online = last_online;
        }

        public long getUid() {
            return uid;
        }

        public void setUid(long uid) {
            this.uid = uid;
        }

        public int getNet_tp() {
            return net_tp;
        }

        public void setNet_tp(int net_tp) {
            this.net_tp = net_tp;
        }

        public String getPhone_info() {
            return phone_info;
        }

        public void setPhone_info(String phone_info) {
            this.phone_info = phone_info;
        }

        public VideoConfig getVideoConfig() {
            return videoConfig;
        }

        public void setVideoConfig(VideoConfig videoConfig) {
            this.videoConfig = videoConfig;
        }

        public int getIsIntimateFriend() {
            return isIntimateFriend;
        }

        public void setIsIntimateFriend(int isIntimateFriend) {
            this.isIntimateFriend = isIntimateFriend;
        }

        public void setIsIntimateFriend() {
            this.isIntimateFriend = 0;
        }

        public boolean isBeauty_switch() {
            return beauty_switch;
        }

        public void setBeauty_switch(boolean beauty_switch) {
            this.beauty_switch = beauty_switch;
        }

        public static class VideoConfig extends BaseData {
            private int videoChat;      // 视频开关 1：开启 0：关闭
            private int voiceChat;      // 音频开关 1：开启 0：关闭
            private int videoVertify;   // 视频认证 1：未通过 3：通过
            private int videoPrice;     // 视频价格
            private int audioPrice;     // 音频价格

            @Override
            public void parseJson(String str) {
                JSONObject jsonObject = getJsonObject(str);

                this.videoChat = jsonObject.optInt("videochat");
                this.voiceChat = jsonObject.optInt("audiochat");
                this.videoVertify = jsonObject.optInt("videoverify");
                this.videoPrice = jsonObject.optInt("videoprice");
                this.audioPrice = jsonObject.optInt("audioprice");
            }

            /**
             * 视频验证是否通过
             */
            public boolean isVerifyVideo() {
                return videoVertify == 3;
            }

            /**
             * 是否可视频
             */
            public boolean isVideoChat() {
                return videoChat == 1;
            }

            /**
             * 是否可音频
             */
            public boolean isVoiceChat() {
                return voiceChat == 1;
            }

            public int getVideoChat() {
                return videoChat;
            }

            public void setVideoChat(int videoChat) {
                this.videoChat = videoChat;
            }

            public int getVoiceChat() {
                return voiceChat;
            }

            public void setVoiceChat(int voiceChat) {
                this.voiceChat = voiceChat;
            }

            public int getVideoVertify() {
                return videoVertify;
            }

            public void setVideoVertify(int videoVertify) {
                this.videoVertify = videoVertify;
            }

            public int getVideoPrice() {
                return videoPrice;
            }

            public void setVideoPrice(int videoPrice) {
                this.videoPrice = videoPrice;
            }

            public int getAudioPrice() {
                return audioPrice;
            }

            public void setAudioPrice(int audioPrice) {
                this.audioPrice = audioPrice;
            }
        }
    }

    public ABInfo getAbInfo() {
        if (abInfo == null) {
            return new ABInfo();
        }
        return abInfo;
    }

    public static final class ABInfo extends BaseData {
        private int keyCount;//我账户余额中的钥匙数
        private int keyUnlocaked;//对方是否已经被我用钥匙解锁

        public int getKeyCount() {
            return this.keyCount;
        }

        public boolean isUnlock() {
            return this.keyUnlocaked == 1;
        }

        @Override
        public void parseJson(String jsonStr) {
            try {
                JSONObject object = getJsonObject(jsonStr);

                if (!object.isNull("key_count")) {
                    this.keyCount = object.getInt("key_count");
                }
                if (!object.isNull("key_unlocaked")) {
                    this.keyUnlocaked = object.getInt("key_unlocaked");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
