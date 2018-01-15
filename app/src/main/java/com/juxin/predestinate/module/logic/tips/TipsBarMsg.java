package com.juxin.predestinate.module.logic.tips;

/**
 * 那个位置要显示标题
 * Created by Kind on 2016/11/16.
 */

public enum TipsBarMsg {

    //首页所需监听事件
    Home_Page(TipsBarType.Show_Update_Portrait, TipsBarType.Show_Network_Status_Change),

    //消息页面所需监听事件
    Mail_Page(TipsBarType.Show_Network_Status_Change),

    //广场页监听事件
    Square_Page(TipsBarType.Show_Network_Status_Change),

    //通讯录监听事件
    Address_Page(TipsBarType.Show_Network_Status_Change),

    //聊天界面所需监听事件
    Chat_Page(TipsBarType.Show_Float_Update_Portrait, TipsBarType.Show_Chart_Simple_tips),

    //普通悬浮文字提示条

    Simple_page(TipsBarType.Show_Simple_tips),

    // 用户中心页监听事件
    UserCenter_Page(TipsBarType.Show_Update_Portrait, TipsBarType.Show_Network_Status_Change);

    //领取红包界面监听事件
    //  Envelope_Page(MsgType.MT_APP_Suspension_Notice),
    //普通通知类型提示条
    //  Simple_Page(MsgType.MT_APP_Suspension_Notice),
    //
    //  AllListener(MsgType.MT_Network_Status_Change, MsgType.MT_APP_Suspension_Notice);

    TipsBarType[] msgType;

    TipsBarMsg(TipsBarType... msgType) {
        this.msgType = msgType;
    }

    public TipsBarType[] getTipsBarType() {
        return msgType;
    }
}
