package com.juxin.predestinate.bean.my;


import com.juxin.predestinate.bean.net.BaseData;

import java.io.Serializable;

/**
 * 送礼列表信息
 * Created by zm on 17/8/7.
 */
public class SendGiftInfo extends BaseData implements Serializable {

    private int gift_id;  //礼物ID
    private int gem_num;  //钻石数量

    public SendGiftInfo(int gift_id, int gem_num) {
        this.gift_id = gift_id;
        this.gem_num = gem_num;
    }

    public int getGift_id() {
        return gift_id;
    }

    public void setGift_id(int gift_id) {
        this.gift_id = gift_id;
    }

    public int getGem_num() {
        return gem_num;
    }

    public void setGem_num(int gem_num) {
        this.gem_num = gem_num;
    }

    @Override
    public void parseJson(String jsonStr) {

    }

    @Override
    public String toString() {
        return "{" +
                "gift_id=" + gift_id +
                ", gem_num=" + gem_num +
                '}';
    }
}
