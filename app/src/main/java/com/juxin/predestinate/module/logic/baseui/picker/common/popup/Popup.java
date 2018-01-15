package com.juxin.predestinate.module.logic.baseui.picker.common.popup;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import com.juxin.predestinate.R;
import com.juxin.predestinate.module.logic.application.App;

/**
 * 弹窗
 */
public class Popup extends DialogFragment {

    private Dialog dialog;
    private FrameLayout contentLayout;
    private FragmentActivity activity;
    /**
     * 声明一个监听
     */
    private DialogInterface.OnDismissListener dismissListener;

    public Popup() {
        this((FragmentActivity) App.getActivity());
    }

    /**
     * Instantiates a new Popup.
     *
     * @param activity the context
     */
    @SuppressLint("ValidFragment")
    public Popup(FragmentActivity activity) {
        this.activity = activity;
        init(activity);
    }

    private void init(Context context) {
        contentLayout = new FrameLayout(context);
        contentLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        contentLayout.setFocusable(true);
        contentLayout.setFocusableInTouchMode(true);

        dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(false);//触摸屏幕取消窗体
        dialog.setCancelable(true);//按返回键取消窗体

        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);//位于屏幕中间
        window.setWindowAnimations(R.style.AnimScaleInScaleOutOverShoot);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //android.util.AndroidRuntimeException: requestFeature() must be called before adding content
        window.requestFeature(Window.FEATURE_NO_TITLE);
        window.setContentView(contentLayout);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return dialog;
    }

    /**
     * @return instance of dialog
     */
    @Override
    public Dialog getDialog() {
        return dialog;
    }

    /**
     * Gets context.
     *
     * @return the context
     */
    public Context getContext() {
        return contentLayout.getContext();
    }

    /**
     * Sets animation.
     *
     * @param animRes the anim res
     */
    public void setAnimationStyle(@StyleRes int animRes) {
        Window window = dialog.getWindow();
        window.setWindowAnimations(animRes);
    }

    /**
     * Is showing boolean.
     *
     * @return the boolean
     */
    public boolean isShowing() {
        return dialog.isShowing();
    }

    /**
     * Show.
     */
    @CallSuper
    public void show() {
        synchronized (this) {
            if (activity == null || activity.isFinishing() || isAdded()) {
                Log.e("Popup", "show: ------>activity is finish or the popup dialog has added to window, it's returned without showing.");
                return;
            }
            try {
                FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                transaction.add(Popup.this, activity.toString());
                transaction.commitAllowingStateLoss();
                activity.getSupportFragmentManager().executePendingTransactions();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sets content view.
     *
     * @param view the view
     */
    public void setContentView(View view) {
        contentLayout.removeAllViews();
        contentLayout.addView(view);
    }

    /**
     * Gets content view.
     *
     * @return the content view
     */
    public View getContentView() {
        return contentLayout.getChildAt(0);
    }

    /**
     * Sets size.
     *
     * @param width  the width
     * @param height the height
     */
    public void setSize(int width, int height) {
        ViewGroup.LayoutParams params = contentLayout.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(width, height);
        } else {
            params.width = width;
            params.height = height;
        }
        contentLayout.setLayoutParams(params);
    }

    /**
     * @updateAuther Mr.Huang
     * @updateDate 2017-07-11
     * 给dialog设置OnDismissListener会在DialogFragment中失效，原因是DialogFragment内部会替换掉自己设置的监听
     */
    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
//        dialog.setOnDismissListener(onDismissListener); //注释掉  因为这个设置不起作用
        this.dismissListener = onDismissListener;
    }

    /**
     * @updateAuther Mr.Huang
     * @updateDate 2017-07-11
     * 重写DialogFragment的onDismiss添加dismiss回调
     * @param dialog
     */
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(dismissListener != null){
            dismissListener.onDismiss(dialog);
        }
    }

    /**
     * Sets on key listener.
     *
     * @param onKeyListener the on key listener
     */
    public void setOnKeyListener(DialogInterface.OnKeyListener onKeyListener) {
        dialog.setOnKeyListener(onKeyListener);
    }

    /**
     * Gets window.
     *
     * @return the window
     */
    public Window getWindow() {
        return dialog.getWindow();
    }

    /**
     * Gets root view.
     *
     * @return the root view
     */
    public ViewGroup getRootView() {
        return contentLayout;
    }
}
