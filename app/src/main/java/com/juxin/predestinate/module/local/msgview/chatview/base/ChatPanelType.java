package com.juxin.predestinate.module.local.msgview.chatview.base;


/**
 * 显示消息的面板类型。
 */
public enum ChatPanelType {
    /**
     * 普通面板
     */
    CPT_Normal,

    /**
     * 标示聊天消息中出现的面板是否用来显示自定义格式，默认没有头像和状态。
     */
    CPT_Custom,

    /**
     * 右上角位置固定提示。
     */
    CPT_Fixed
}
