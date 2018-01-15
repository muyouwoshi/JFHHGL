package com.juxin.predestinate.ui.live.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by terry on 2017/7/24.
 * 进入直播间
 */

public class EnterLiveBean implements Parcelable{

    public static final int  ROOM_STATUS_0 = 0; //未直播
    public static final int  ROOM_STATUS_1 = 1; //被禁播

    public int status;      // 0 正常,1 禁言,2 拉黑,3 踢出 对应 LiveRoomUserStatus
    public int charm;       // 魅力值
    public String avatar;   // 头像
    public String nickname; // 昵称
    public String rtmpurl;  // 直播视频地址
    public String hlsurl;   // 直播视频地址
    public int room_status;  //房间状态 0正常-未直播 1被禁播 2表示正在直播
    public String user_total;//观看直播总人数
    public String room_id;  //房间号
    public String pic;      //封面
    public String title;    //标题
    public long tm;     //直播时长
    public boolean is_friend;    //是否是好友 true - 好友 false-非好友
    public boolean is_admin = false;                   //是否场控
    public long current_time;
    public long enter_time;   //进入直播间的时间
    public int channel_uid;   //渠道id
    public String live_id;    //本次直播唯一ID
    public String ws_url;     //websocket h5地址
    public int single_card;   //0-没有免费弹幕卡片
    public int service_card;  //0-没有免费全服广播卡片


    public void setEnterTime(long enter_time) {
        this.enter_time = enter_time;
    }

    public EnterLiveBean(){}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(status);
        dest.writeInt(charm);
        dest.writeString(avatar);
        dest.writeString(nickname);
        dest.writeString(rtmpurl);
        dest.writeInt(room_status);
        dest.writeString(user_total);
        dest.writeString(room_id);
        dest.writeString(pic);
        dest.writeString(title);
        dest.writeLong(tm);
        dest.writeByte((byte) (is_friend?1:0));
        dest.writeByte((byte) (is_admin?1:0));
        dest.writeLong(enter_time);
        dest.writeInt(channel_uid);
        dest.writeString(live_id);
        dest.writeLong(current_time);
        dest.writeString(ws_url);
        dest.writeInt(single_card);
        dest.writeInt(service_card);
    }

    public EnterLiveBean(Parcel in){
        status = in.readInt();
        charm = in.readInt();
        avatar = in.readString();
        nickname = in.readString();
        rtmpurl = in.readString();
        room_status = in.readInt();
        user_total = in.readString();
        room_id = in.readString();
        pic = in.readString();
        title = in.readString();
        tm = in.readLong();
        is_friend = in.readByte() == 1?true:false;
        is_admin = in.readByte() == 1?true:false;
        enter_time = in.readLong();
        channel_uid = in.readInt();
        live_id = in.readString();
        current_time = in.readLong();
        ws_url = in.readString();
        single_card = in.readInt();
        service_card = in.readInt();
    }

    public static final Creator<EnterLiveBean> CREATOR = new Creator<EnterLiveBean>() {
        @Override
        public EnterLiveBean createFromParcel(Parcel in) {
            return new EnterLiveBean(in);
        }

        @Override
        public EnterLiveBean[] newArray(int size) {
            return new EnterLiveBean[size];
        }
    };

}
