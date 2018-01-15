package com.juxin.predestinate.bean.live;

/**
 * 类描述：
 * 创建时间：2017/10/12 16:54
 * 修改时间：2017/10/12 16:54
 * Created by Administrator on 2017/10/12
 * 修改备注：
 */

public class RoomPayInfo {
    public long room_uid;       //主播UID
    public long room_id;        //直播间ID
    public String room_live_id; //本次直播唯一ID
    public long room_live_time; //本次主播开播时长秒数
    public String pay_type;     //支付方式|wechat|alipay|other
    public int product_id;      //钻石购买Id
    public int gem_num;         //购买钻石数量
    public int price;           //金额人民币单位元
    public String source;       //充值来源,定义参考《直播充值来源》
}
