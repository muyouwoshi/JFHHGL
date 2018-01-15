package com.juxin.predestinate.ui.live.event;

/**
 * Created by terry on 2017/7/20.
 */
public class LiveCmdBusEvent {

    public static final int USER_ENTER = 1;         //进入房间
    public static final int USER_LEAVE = 2;         //离开房间
    public static final int REFRESH_CHARM = 3;      //刷新魅力值
    public static final int GIRL_END_LIVE = 4;      //主播结束直播
    public static final int WEBVIEW_TOUCH_ACTIVITY = 5;//WEBVIEW 事件传递
    public static final int ROOM_CONTROL = 6;       //房间控制
    public static final int RANKING = 7;            //刷新排行榜排名信息
    public static final int OPEN_USER_CARD = 8;     //打开个人资料
    public static final int SYNC_INCOME_VALUE = 9;  //刷新主播收益
    public static final int SYNC_USER_CONSUME = 10; //用户本场贡献消费
    public static final int ACTIVITY_ONE_HOT = 11;  //抢热一活动


    public long uid;         //用户uid
    public String avatar;    //用户头像
    public int type;         //进入房间类型 USER_ENTER USER_LEAVE
    public String charm;     //魅力值
    public long liveTime;    //直播时长
    public String nickName;  //昵称
    public boolean activityTouch;// true h5页面相应touch事件 false h5页面不相应事件
    public long target_uid;         //被操作者id
    public int room_control_type;   //0-禁言，1-拉黑，3-踢出
    public int ranking;             //排名
    public String need_charm;       //距前一名还少多少魅力值
    public long income;             //主播收益
    public int nobilityLevel;       //用户等级
    public int gender;              //性别
    public int consume;             //本场消费贡献
    public int activity_state;      //1 开始 2 结束
    public String message; //消息内容


    public LiveCmdBusEvent(Builder builder) {
        this.uid = builder.uid;
        this.avatar = builder.avatar;
        this.type = builder.type;
        this.charm = builder.charm;
        this.liveTime = builder.liveTime;
        this.nickName = builder.nickName;
        this.activityTouch = builder.activityTouch;
        this.target_uid = builder.target_uid;
        this.room_control_type = builder.room_control_type;
        this.ranking = builder.ranking;
        this.need_charm = builder.need_charm;
        this.income = builder.income;
        this.nobilityLevel = builder.nobilityLevel;
        this.gender = builder.gender;
        this.consume = builder.consume;
        this.activity_state = builder.activity_state;
        this.message = builder.message;
    }


    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    public static class Builder {
        private long uid;         //用户uid
        private String avatar;   //用户头像
        private int type;        //进入房间类型 USER_ENTER USER_LEAVE
        private String charm;   //魅力值
        private long liveTime;    //直播时长
        private String nickName;  //昵称
        private boolean activityTouch;// true h5页面相应touch事件 false h5页面不相应事件
        public long target_uid;         //被操作者id
        public int room_control_type;   //0-禁言，1-拉黑，3-踢出
        public int ranking;             //排名
        public String need_charm;       //距前一名还少多少魅力值
        public long income;             //主播收益
        public int nobilityLevel;       //用户等级
        public int gender;              //性别
        public int consume;             //本场消费贡献
        public int activity_state;      //1 开始 2 结束
        public String message; //消息内容

        public LiveCmdBusEvent build() {
            return new LiveCmdBusEvent(this);
        }

        public Builder uid(long uid) {
            this.uid = uid;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder avtar(String avatar) {
            this.avatar = avatar;
            return this;
        }

        public Builder type(int type) {
            this.type = type;
            return this;
        }

        public Builder charm(String charm) {
            this.charm = charm;
            return this;
        }

        public Builder liveTime(long liveTime) {
            this.liveTime = liveTime;
            return this;
        }

        public Builder nickName(String nickName) {
            this.nickName = nickName;
            return this;
        }

        public Builder activity(boolean activityTouch) {
            this.activityTouch = activityTouch;
            return this;
        }

        public Builder target_uid(long uid) {
            this.target_uid = uid;
            return this;
        }

        public Builder room_control_type(int type) {
            this.room_control_type = type;
            return this;
        }

        public Builder ranking(int ranking) {
            this.ranking = ranking;
            return this;
        }

        public Builder need_charm(String charm) {
            this.need_charm = charm;
            return this;
        }

        public Builder income(long income) {
            this.income = income;
            return this;
        }

        public Builder nobilityLevel(int nobilityLevel) {
            this.nobilityLevel = nobilityLevel;
            return this;
        }

        public Builder gender(int gender) {
            this.gender = gender;
            return this;
        }

        public Builder consume(int consume) {
            this.consume = consume;
            return this;
        }

        public Builder activity_state(int state) {
            this.activity_state = state;
            return this;
        }
    }

}
