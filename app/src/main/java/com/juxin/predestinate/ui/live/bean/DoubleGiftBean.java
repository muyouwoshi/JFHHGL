package com.juxin.predestinate.ui.live.bean;

/**
 * Created by terry on 2017/7/21.
 */

public class DoubleGiftBean {
    public String channel_id;
    public String room_id;
    public int gift_id;
    public int gift_count;

    public DoubleGiftBean(String channel_id,String room_id,int gift_id,int gift_count){
        this.channel_id = channel_id;
        this.room_id = room_id;
        this.gift_id = gift_id;
        this.gift_count = gift_count;
    }
}
