package com.juxin.predestinate.module.logic.baseui.custom;

import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.ViewGroup;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.baseui.picker.common.popup.BottomPopup;

/**
 * 底部弹框的实现
 *
 * @author Mr.Huang
 * @date 2017-07-11
 */
public abstract class BottomPopupSupport extends BottomPopup {

    public BottomPopupSupport(FragmentActivity activity) {
        super(activity);
        setAnimationStyle(R.style.AnimDownInDownOutOverShoot);
        getWindow().setGravity(Gravity.BOTTOM);
        setWidth(screenWidthPixels);//padding 布局文件中控制
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
