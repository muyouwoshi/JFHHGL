package com.juxin.predestinate.ui.live.bean;

/**
 * Created by terry on 2017/7/26.
 * 发送完礼物回调给ui
 */

public class SendGiftCallbackBean {

    public String channel_id;  //渠道id
    public String room_id;     //房间id
    public int gift_id;        //礼物id
    public int gift_count;     //礼物数量
    public String charm;       //魅力值

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public void setGift_id(int gift_id) {
        this.gift_id = gift_id;
    }

    public void setGift_count(int gift_count) {
        this.gift_count = gift_count;
    }

    public void setCharm(String charm) {
        this.charm = charm;
    }
}
