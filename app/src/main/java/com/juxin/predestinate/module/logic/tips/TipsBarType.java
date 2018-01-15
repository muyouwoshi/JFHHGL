package com.juxin.predestinate.module.logic.tips;


import com.juxin.library.log.PLogger;
import com.juxin.predestinate.ui.tips.ChatSimpleTips;
import com.juxin.predestinate.ui.tips.NetErrorTips;
import com.juxin.predestinate.ui.tips.PortraitTips;
import com.juxin.predestinate.ui.tips.SimpleTips;
import com.juxin.predestinate.ui.tips.TipsBarBasePanel;

/**
 * type类型
 * Created by Kind on 2016/11/16.
 */

public enum TipsBarType {
    none(1, TipsBarBasePanel.class, 0, 0),
    //聊天界面使用的提示条
    Show_Chart_Simple_tips(2, ChatSimpleTips.class, 700, 3),
    //普通提示条
    Show_Simple_tips(3, SimpleTips.class, 700, 3),

    //更新头像提示
    Show_Update_Portrait(4, PortraitTips.class, 800, -1),

    //更新头像提示
    Show_Float_Update_Portrait(5, PortraitTips.class, 800, 3),


    //网络变更提示
    Show_Network_Status_Change(6, NetErrorTips.class, 1000, -1);

    private int barType;
    private Class<? extends TipsBarBasePanel> viewPanel;
    private int weight;
    private int time;

    TipsBarType(int barType, Class<? extends TipsBarBasePanel> viewPanel, int weight, int time) {
        this.barType = barType;
        this.viewPanel = viewPanel;
        this.weight = weight;
        this.time = time;
    }

    public Class<? extends TipsBarBasePanel> getBaseViewPanel() {
        return viewPanel;
    }

    public static String getTipsBarPanelClassName(int type) {
        try {
            TipsBarType barType = TipsBarType.valueOf("BAR_" + type);
            return barType.viewPanel.getSimpleName();
        } catch (Exception e) {
            PLogger.printThrowable(e);
        }

        return "";
    }

    public int getBarType() {
        return barType;
    }

    public int getWeight() {
        return weight;
    }

    public int getTime() {
        return time;
    }
}
