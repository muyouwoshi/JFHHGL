package com.juxin.predestinate.module.logic.baseui.custom;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.baseui.picker.common.popup.ConfirmPopup;
import com.juxin.predestinate.module.util.UIUtil;


/**
 * 简单的提示dialog，默认点击确定或取消都会关闭dialog，如果需要实现点击之后的动作，需要setConfirmListener<br>
 * <li><b>使用示例：</b></li>
 * <pre>
 *     <code>
 * SimpleTipDialog tipDialog = new SimpleTipDialog(this);
 * tipDialog.setTitleText("对话框");
 * tipDialog.setContentString("这是一个简单的对话框");
 * tipDialog.setCancelVisible(false);//设置单按钮模式，只显示确定按钮
 * tipDialog.setConfirmListener(new SimpleTipDialog.ConfirmListener() {});//单按钮模式下使用的回调是onSubmit
 * tipDialog.show();
 *     </code>
 * </pre>
 * Created by ZRP on 2016/5/19.
 */
public class SimpleTipDialog extends ConfirmPopup {

    private CharSequence contentString = "";
    private int contentColor = R.color.theme_color_red;
    private int gravity = Gravity.CENTER;

    /**
     * Instantiates a new Confirm popup.
     *
     * @param activity the activity
     */
    public SimpleTipDialog(FragmentActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    protected View makeCenterView() {
        TextView textView = new TextView(activity);
        textView.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        textView.setGravity(gravity);
        int padding = UIUtil.dip2px(activity, 20);
        textView.setPadding(padding, padding, padding, padding);
        textView.setText(contentString);
        textView.setTextSize(14);
        textView.setTextColor(activity.getResources().getColor(contentColor));
        return textView;
    }

    /**
     * 设置Dialog内容的文字对齐方式
     */
    public void setTextGravity(int gravity) {
        this.gravity = gravity;
    }

    /**
     * 设置
     *
     * @param contentString 填充文字
     */
    public void setContentString(CharSequence contentString) {
        this.contentString = contentString;
    }

    /**
     * 设置填充文字颜色
     *
     * @param contentColor 填充文字颜色
     */
    public void setContentColor(int contentColor) {
        this.contentColor = contentColor;
    }

    private ConfirmListener confirmListener;

    /**
     * 设置按钮点击事件
     *
     * @param confirmListener 按钮点击监听
     */
    public void setConfirmListener(ConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
    }

    @Override
    protected void onSubmit() {
        dismiss();
        if (confirmListener != null) confirmListener.onSubmit();
    }

    @Override
    protected void onCancel() {
        if (confirmListener != null) confirmListener.onCancel();
    }

    public interface ConfirmListener {
        void onCancel();//取消回调，默认关闭dialog

        void onSubmit();//确定回调
    }
}
