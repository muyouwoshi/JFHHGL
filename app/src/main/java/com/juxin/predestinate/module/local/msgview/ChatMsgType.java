package com.juxin.predestinate.module.local.msgview;

import com.juxin.library.log.PLogger;
import com.juxin.library.utils.TypeConvertUtil;
import com.juxin.predestinate.module.local.chat.utils.MessageConstant;
import com.juxin.predestinate.module.local.msgview.chatview.ChatPanel;
import com.juxin.predestinate.module.local.msgview.chatview.base.ChatPanelType;
import com.juxin.predestinate.module.local.msgview.chatview.msgpanel.ChatPanelChumInvite;
import com.juxin.predestinate.module.local.msgview.chatview.msgpanel.ChatPanelChumTask;
import com.juxin.predestinate.module.local.msgview.chatview.msgpanel.ChatPanelCommon;
import com.juxin.predestinate.module.local.msgview.chatview.msgpanel.ChatPanelCustomHint;
import com.juxin.predestinate.module.local.msgview.chatview.msgpanel.ChatPanelCustomSimple;
import com.juxin.predestinate.module.local.msgview.chatview.msgpanel.ChatPanelGift;
import com.juxin.predestinate.module.local.msgview.chatview.msgpanel.ChatPanelGiveMeGift;
import com.juxin.predestinate.module.local.msgview.chatview.msgpanel.ChatPanelInvite;
import com.juxin.predestinate.module.local.msgview.chatview.msgpanel.ChatPanelPrivate;
import com.juxin.predestinate.module.local.msgview.chatview.msgpanel.ChatPanelRedbag;
import com.juxin.predestinate.module.local.msgview.chatview.msgpanel.ChatPanelSmallIcon;
import com.juxin.predestinate.module.local.msgview.chatview.msgpanel.ChatPanelSysNotice;
import com.juxin.predestinate.module.local.msgview.chatview.msgpanel.ChatPanelText;
import com.juxin.predestinate.module.local.msgview.chatview.msgpanel.ChatPanelUpgrade;
import com.juxin.predestinate.module.local.msgview.chatview.msgpanel.ChatPanelVideo;
import com.juxin.predestinate.module.local.msgview.chatview.msgpanel.RedPackagePanel;
import com.juxin.predestinate.module.local.msgview.chatview.notifyview.NotifyBasePanel;

/**
 * Created by Kind on 2017/3/30.
 */
public enum ChatMsgType {

    // 基本消息类型
    CMT_2(ChatPanelCommon.class, "普通消息"),

    CMT_3(ChatPanelCommon.class, "打招呼消息"),

    CMT_7("已读消息"),

    CMT_10(ChatPanelGift.class, "礼物消息"),

    CMT_14(ChatPanelCustomHint.class, ChatPanelType.CPT_Custom, "小提示消息"),

    CMT_19(ChatPanelCustomHint.class, ChatPanelType.CPT_Custom, "HTML小提示消息"),

    CMT_20(ChatPanelGiveMeGift.class, "索要礼物消息"),

    CMT_24(ChatPanelVideo.class, "音视频消息"),

    CMT_25(ChatPanelText.class, "HTML文本消息"),

    CMT_28(ChatPanelText.class, "自动升级提示消息"),

    CMT_29(ChatPanelSysNotice.class,  ChatPanelType.CPT_Custom, "系统消息"),

    CMT_30(ChatPanelInvite.class, "邀请消息"),

    CMT_31(ChatPanelPrivate.class, "私密照片"),

    CMT_32(ChatPanelPrivate.class, "私密视频"),

    CMT_33(ChatPanelPrivate.class, "私密语音"),

    CMT_34(RedPackagePanel.class, "红包"),

    CMT_35(ChatPanelChumInvite.class, "好友邀请"),

    CMT_36(ChatPanelChumTask.class, "好友任务"),

    CMT_37(ChatPanelSmallIcon.class, "小图标消息"),

    CMT_38(ChatPanelRedbag.class, "红包消息"),

    CMT_1001("送达消息"),

    CMT_1006("弹框消息"),

    CMT_1008("解除密友消息"),

    CMT_Max(ChatPanelUpgrade.class, "该版本不能显示的消息"),

    // 非有效类型
    CMT_Invalid(null, "非有效类型");

    /**
     * 聊天消息中出现的面板。
     */
    private Class<? extends ChatPanel> panelClass = null;

    /**
     * 滚动提示消息中出现的面板。
     */
    private Class<? extends NotifyBasePanel> notifyPanelClass = null;

    /**
     * {@link ChatPanelType}
     */
    private ChatPanelType msgPanelType = ChatPanelType.CPT_Normal;

    /**
     * 当前类型的描述。
     */
    private String desc = null;

    ChatMsgType(String desc) {
        this.desc = desc;
    }

    ChatMsgType(Class<? extends ChatPanel> panelClass, String desc) {
        this.panelClass = panelClass;
        this.desc = desc;
    }

    ChatMsgType(Class<? extends ChatPanel> panelClass, ChatPanelType msgPanelType, String desc) {
        this.panelClass = panelClass;
        this.msgPanelType = msgPanelType;
        this.desc = desc;
    }

    ChatMsgType(Class<? extends ChatPanel> panelClass, Class<? extends NotifyBasePanel> notifyPanelClass, String desc) {
        this.panelClass = panelClass;
        this.notifyPanelClass = notifyPanelClass;
        this.desc = desc;
    }

    public String toStringType() {
        return toString().substring(4);
    }

    public int toIntegerType() {
        return TypeConvertUtil.toInt(toString().substring(4));
    }

    public Class<? extends ChatPanel> getPanelClass() {
        return panelClass;
    }

    public boolean isCustomMsgPanel() {
        return ChatPanelType.CPT_Custom == msgPanelType;
    }

    public ChatPanelType getMsgPanelType() {
        return msgPanelType;
    }

    public Class<? extends NotifyBasePanel> getNotifyPanelClass() {
        return notifyPanelClass;
    }

    /**
     * 获取类型对应的消息面板类。
     *
     * @param type 消息类型。
     * @return 对应的Panel类。
     */
    public static Class<? extends ChatPanel> getPanelClass(int type) {
        try {
            if(MessageConstant.isMaxVersionMsg(type)){
                return ChatMsgType.valueOf("CMT_Max").panelClass;
            }

            return ChatMsgType.valueOf("CMT_" + type).panelClass;
        } catch (Exception e) {
            PLogger.d("--->" + type);
        }
        return null;
    }

    /**
     * 获取类型对应的消息面板类名。
     *
     * @param type 消息类型。
     * @return 对应的Panel类名。
     */
    public static String getPanelClassName(int type) {
        try {
            if(MessageConstant.isMaxVersionMsg(type)){
                return ChatMsgType.valueOf("CMT_Max").panelClass.getSimpleName();
            }
            return ChatMsgType.valueOf("CMT_" + type).panelClass.getSimpleName();
        } catch (Exception e) {
            PLogger.d("--->" + type);
        }
        return "";
    }

    /**
     * 判断类型和面板是否匹配。
     *
     * @param panel 面板实例
     * @param type  类型。
     * @return 是否匹配。
     */
    public static boolean isMatchPanel(ChatPanel panel, int type) {
        try {
            return getPanelClassName(type).equals(panel.getClass().getSimpleName());
        } catch (Exception e) {
            PLogger.d("--->" + type);
        }
        return false;
    }


    /**
     * 获取类型对应的通知消息面板类。
     *
     * @param type 消息类型。
     * @return 对应的Panel类。
     */
    public static Class<? extends NotifyBasePanel> getNotifyPanelClass(int type) {
        try {
            if(MessageConstant.isMaxVersionMsg(type)){
                return ChatMsgType.valueOf("CMT_Max").notifyPanelClass;
            }

            return ChatMsgType.valueOf("CMT_" + type).notifyPanelClass;
        } catch (Exception e) {
            PLogger.d("--->" + type);
        }
        return null;
    }

    /**
     * 获取类型对应的通知消息面板类名。
     *
     * @param type 消息类型。
     * @return 对应的Panel类名。
     */
    public static String getNotifyPanelClassName(int type) {
        try {
            ChatMsgType chatMsgType = ChatMsgType.valueOf("CMT_" + type);
            return chatMsgType.notifyPanelClass.getSimpleName();
        } catch (Exception e) {
            PLogger.d("--->" + type);
        }
        return "";
    }

    /**
     * 判断类型和面板是否匹配。
     *
     * @param panel 面板实例
     * @param type  类型。
     * @return 是否匹配。
     */
    public static boolean isMatchNotify(ChatPanelCustomSimple panel, int type) {
        try {
            return getNotifyPanelClassName(type).equals(panel.getClass().getSimpleName());
        } catch (Exception e) {
            PLogger.d("--->" + type);
        }
        return false;
    }

    /**
     * 获取对应的消息类型。
     *
     * @param type 消息类型。
     * @return 对应的消息类型。
     */
    public static ChatMsgType getMsgType(int type) {
        try {
            if(MessageConstant.isMaxVersionMsg(type)){
                return ChatMsgType.valueOf("CMT_Max");
            }
            return ChatMsgType.valueOf("CMT_" + type);
        } catch (Exception e) {
            PLogger.d("--->" + type);
        }
        return CMT_Invalid;
    }
}
