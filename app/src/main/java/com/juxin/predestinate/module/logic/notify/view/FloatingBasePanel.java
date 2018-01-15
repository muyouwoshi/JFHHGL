package com.juxin.predestinate.module.logic.notify.view;

import android.content.Context;

import com.juxin.library.view.BasePanel;

/**
 * 悬浮窗口view基类
 */
public abstract class FloatingBasePanel extends BasePanel {

    private boolean fixed = false;  //是否固定显示，不自动消失

    public FloatingBasePanel(Context context) {
        super(context);
    }

    public boolean isFixed() {
        return fixed;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }

    /**
     * 初始化所有需要显示的View
     */
    public abstract void initView();

    /**
     * 用消息数据重新初始化显示View
     */
    public abstract void reset();
}
